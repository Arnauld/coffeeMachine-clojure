(ns coffee-machine.core)
(import java.math.BigDecimal)

(defrecord Drink [label protocol-part price])
(defrecord Order [drink nb-sugar money])
(defprotocol Processor (process [processable]))

(def all-drinks [(->Drink "Tea" "T" (BigDecimal. "0.4"))
                 (->Drink "Coffee" "C" (BigDecimal. "0.5"))
                 (->Drink "Chocolate" "H" (BigDecimal. "0.6"))])

(defn drink-by-label [label]
  (let [found (some #(if (.equalsIgnoreCase (:label %) label) %) all-drinks)]
    (if (nil? found) 
        (throw (IllegalArgumentException. (str "Drink unknown: '" label "'")))
        ; else
        found)))

(defn create-order [drink-label nb-sugar money] 
  (let [drink (drink-by-label drink-label)]
    (->Order drink nb-sugar money)))

(def ZERO BigDecimal/ZERO)

(defn- missing-money [order drink]
  (let [money (:money order)
        drink (:drink order)
        price (:price drink)]
      (.subtract price money)))

(extend-protocol Processor
  Order
  (process [order]
     (let [drink (:drink order)
           missing (missing-money order drink)]
        (if (< 0 (.compareTo missing ZERO))
            (process (str "Not enough money " missing " missing"))
            ; else
            (let [drink-part (:protocol-part drink)
                  nb-sugar   (:nb-sugar order)
                  sugar-part (if (< 0 nb-sugar) (str nb-sugar ":0") ":")]
                (str drink-part ":" sugar-part)))))
  String
  (process [message]
     (str "M:" message)))


