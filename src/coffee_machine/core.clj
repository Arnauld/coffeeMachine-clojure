(ns coffee-machine.core)

(defrecord Drink [label protocol-part])
(defrecord Order [drink nb-sugar])
(defprotocol Processor (process [processable]))

(def all-drinks [(->Drink "Tea" "T")
                 (->Drink "Coffee" "C")
                 (->Drink "Chocolate" "H")])

(defn drink-by-label [label]
  (some #(if (.equalsIgnoreCase (:label %) label) %) all-drinks))

(defn create-order [drink-label nb-sugar] 
  (let [drink (drink-by-label drink-label)]
    (->Order drink nb-sugar)))

(extend-protocol Processor
  Order
  (process [order]
     (let [drink-part (:protocol-part (:drink order))
           nb-sugar   (:nb-sugar order)
           sugar-part (if (< 0 nb-sugar) (str nb-sugar ":0") ":")]
         (str drink-part ":" sugar-part)))
  String
  (process [message]
     (str "M:" message)))


