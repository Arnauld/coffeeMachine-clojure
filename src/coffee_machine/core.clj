(ns coffee-machine.core)
(import java.math.BigDecimal)

(defrecord Drink [kw label protocol-part price sugar-adapter hot-adapter])
(defrecord Order [drink nb-sugar money very-hot])

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

(defn drink-or-fail [selector detail]
  (let [found (some selector all-drinks)]
  (if (nil? found) 
      (throw (IllegalArgumentException. (str "Drink unknown: '" detail "'")))
      ; else
      found)))

(defn drink-by-label [label]
  (drink-or-fail #(if (.equalsIgnoreCase (:label %) label) %) label))

(defn drink-by-keword [kw]
  (drink-or-fail #(if (= (:kw %) kw) %) kw))


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

(defn process-message [message]
     (str "M:" message))


(defn process-order [order 
                     beverage-quantity-checker 
                     missing-drink-notifier]
     (let [missing (missing-money order)
           label   (:label (:drink order))]
        (if (< 0 (.compareTo missing ZERO))
            (do 
              ;(println "missing money (" (:money order) " vs " (:price (:drink order)) ")")
              (process-message (str "Not enough money " missing " missing")))
            ; else
            (if (beverage-quantity-checker label)
              (let [drink-part (drink-protocol-part order)
                    sugar-part (sugar-protocol-part order)]
                  (update-stats order)
                  (str drink-part ":" sugar-part))
              ;else
              (missing-drink-notifier label)))))



