(ns coffee-machine.core)
(import java.math.BigDecimal)

(defrecord Drink [label protocol-part price sugar-adapter hot-adapter])
(defrecord Order [drink nb-sugar money very-hot])
(defprotocol Processor (process [processable]))

(defn accept-sugar [sugar] sugar)
(defn no-sugar     [sugar] 0)
(defn not-hot      [hot] false)
(defn accept-hot   [hot] hot)

(def all-drinks [(->Drink "Tea" "T" (BigDecimal. "0.4") accept-sugar accept-hot)
                 (->Drink "Coffee" "C" (BigDecimal. "0.5") accept-sugar accept-hot)
                 (->Drink "Chocolate" "H" (BigDecimal. "0.6") accept-sugar accept-hot)
                 (->Drink "Orange Juice" "O" (BigDecimal. "0.6") no-sugar not-hot)])

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


(defn drink-by-label [label]
  (let [found (some #(if (.equalsIgnoreCase (:label %) label) %) all-drinks)]
    (if (nil? found) 
        (throw (IllegalArgumentException. (str "Drink unknown: '" label "'")))
        ; else
        found)))

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
                (str drink-part ":" sugar-part)))))
  String
  (process [message]
     (str "M:" message)))


