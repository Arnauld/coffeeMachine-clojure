(ns coffee-machine.core)
(import java.math.BigDecimal)

(defrecord Drink [kw label protocol-part price sugar-adapter hot-adapter])
(defrecord Order [drink nb-sugar money very-hot])
(defprotocol Processor (process [processable]))

;;
;; Drinks: ALL + Helpers
;;

(defn accept-sugar [sugar] sugar)
(defn no-sugar     [sugar] 0)
(defn not-hot      [hot] false)
(defn accept-hot   [hot] hot)

(def all-drinks [(->Drink :tea       "Tea" "T" (BigDecimal. "0.4") accept-sugar accept-hot)
                 (->Drink :coffee    "Coffee" "C" (BigDecimal. "0.5") accept-sugar accept-hot)
                 (->Drink :chocolate "Chocolate" "H" (BigDecimal. "0.6") accept-sugar accept-hot)
                 (->Drink :orange    "Orange Juice" "O" (BigDecimal. "0.6") no-sugar not-hot)])

;; TODO refactor drink-or-fail + filter by-label & by-keyword
(defn drink-by-label [label]
  (let [found (some #(if (.equalsIgnoreCase (:label %) label) %) all-drinks)]
    (if (nil? found) 
        (throw (IllegalArgumentException. (str "Drink unknown: '" label "'")))
        ; else
        found)))

(defn drink-by-keword [kw]
  (let [found (some #(if (= (:kw %) kw) %) all-drinks)]
    (if (nil? found) 
        (throw (IllegalArgumentException. (str "Drink unknown: '" kw "'")))
        ; else
        found)))

;;
;; "Dependency Injection" : EmailNotifier and BeverageQuantityChecker
;;

(declare ^:dynamic *email-notifier*)
(defmacro with-email-notifier [notifier & body]
  `(binding [*email-notifier* (atom ~notifier)]
      (do ~@body)))


(declare ^:dynamic *beverage-quantity-checker*)
(defmacro with-beverage-quantity-checker [checker & body]
  `(binding [*beverage-quantity-checker* (atom ~checker)]
      (do ~@body)))


;;
;; Protocol Helpers
;;

(defn- adapted-sugar [order]
  (let [sugar (:nb-sugar order)
        drink (:drink order)
        adapter (:sugar-adapter drink)]
    (adapter sugar)))

(defn sugar-protocol-part [order]
  (let [nb-sugar   (adapted-sugar order)]
    (if (< 0 nb-sugar) (str nb-sugar ":0") ":")))

(defn- adapted-hot [order]
  (let [very-hot (:very-hot order)
        drink (:drink order)
        adapter (:hot-adapter drink)]
    (adapter very-hot)))

(defn drink-protocol-part [order]
  (let [hot   (adapted-hot order)
        drink (:drink order)]
    (str (:protocol-part drink) (if hot "h" ""))))

;;
;; Create Order
;;

(defn create-order 
  ([drink-label nb-sugar money] 
    (create-order drink-label nb-sugar money false))
  ([drink-label nb-sugar money very-hot] 
    (let [drink (drink-by-label drink-label)]
      (->Order drink nb-sugar money very-hot))))

(def ZERO BigDecimal/ZERO)

(defn- missing-money [order]
  (let [money (:money order)
        drink (:drink order)
        price (:price drink)]
      (.subtract price money)))

;;
;; STATS
;;

(def stats (atom {:total ZERO}))

(defn reset-stats []
  (reset! stats {:total ZERO}))

;(defn on-change [the-key the-ref old-value new-value] 
;  (println "Hey, seeing change from" old-value "to" new-value))
;(add-watch stats :stats-watcher on-change)

(defn increment-key 
  ([m key]
    (increment-key m key 1))
  ([m key amount]
    (assoc m 
           key 
           (if-let [n (m key)] (+ n amount) amount))))

(def money-formatter 
  (let [nf (java.text.NumberFormat/getInstance java.util.Locale/US)]
    (.setMaximumFractionDigits nf 2)
    (.setMinimumFractionDigits nf 2)
    nf))

(defn format-money [amount]
  (.format money-formatter amount))

(defn update-stats [order]
  (let [drink (:drink order)]
    (swap! stats increment-key :total (:price drink))
    (swap! stats increment-key (:kw drink))))

(defn- stats-reducer [stats] 
  (fn [prev key]
    (let [value (stats key)
          drink (drink-by-keword key)
          label (:label drink)]
        (str prev (.toLowerCase label) ": " value "\n"))))

(defn stats-snapshot [] 
  (let [stats @stats
        all-keys (keys stats)
        filtered (filter #(not= % :total) all-keys)
        sorted-keys (sort filtered)
        formatted (reduce (stats-reducer stats) "" sorted-keys)]
      (str formatted "---\nTotal: " (format-money (:total stats)) "€")))

(extend-protocol Processor
  Order
  (process [order]
     (let [missing (missing-money order)]
        (if (< 0 (.compareTo missing ZERO))
            (do 
              ;(println "missing money (" (:money order) " vs " (:price (:drink order)) ")")
              (process (str "Not enough money " missing " missing")))
            ; else
            (let [drink-part (drink-protocol-part order)
                  sugar-part (sugar-protocol-part order)]
                (update-stats order)
                (str drink-part ":" sugar-part)))))
  String
  (process [message]
     (str "M:" message)))


