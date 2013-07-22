(use 'clojure.test)
(require '[coffee-machine.core :as core])
(import java.math.BigDecimal)
(import coffeemachine.EmailNotifier)
(require '[conjure.core])

(def TWO (BigDecimal. "2"))
(defn IDENTITY [x] x)
(defn in? 
  "true if seq contains elm"
  [seq elm]  
  (some #(= elm %) seq))

(def actual-output (atom ""))
(def actual-stats (atom ""))
(def actual-money (atom TWO))
(def emails-sent (atom []))
(def missing-beverages (atom []))

;;

(defn beverage-quantity-checker [drink]
  (not (in? @missing-beverages (.toLowerCase drink))))

(defn missing-drink-notifier [drink]
  (swap! emails-sent conj drink))

;;
;; Reset all "global" states before each scenario
;;

(Before []
  (reset! actual-output "")
  (reset! actual-stats "")
  (reset! actual-money TWO)
  (reset! emails-sent [])
  (reset! missing-beverages [])
  (core/reset-stats))

;;
;;
;;

(Given #"^I've inserted (\d+(?:\.\d+)?)€ in the machine$" [amount]
      (reset! actual-money (BigDecimal. amount)))

(defn- process-order 
  ([drink nb-sugar very-hot]
      (process-order drink nb-sugar very-hot @actual-money))
  ([drink nb-sugar very-hot money]
      (let [order (core/create-order drink nb-sugar money very-hot)
            output (core/process-order order 
                                       beverage-quantity-checker 
                                       missing-drink-notifier)]
          (reset! actual-output output))))

(When #"^I order an? '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (process-order drink (Integer/parseInt nb-sugar-cubes) false))

(When #"^I order an extra hot '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (process-order drink (Integer/parseInt nb-sugar-cubes) true))

(When #"^I order an? '([^\']*)'$" [drink]
      (process-order drink 0 false))

(When #"^the message '([^']*)' is sent$" [message]
      (let [output (core/process-message message)]
          (reset! actual-output output)))

(Then #"^the instruction generated should be '([^']*)'$" [expected-output]
  (is (= expected-output @actual-output)))

;;
;;
;;

(Given #"^the following orders:$" [orders]
  (comment  Express the Regexp above with the code you wish you had  )
  (def df (java.text.SimpleDateFormat. "HH:mm:ss"))
  (doseq [order (.asMaps orders)]
    (let [raw-ts (.get order "time")
          ts (.parse df raw-ts) ; unused
          raw-drink (.get order "drink")
          drink (core/drink-by-label raw-drink)
          money (:price drink)]
        (process-order raw-drink 0 false money))))

(When #"^I query for a report$" []
      (reset! actual-stats (core/stats-snapshot)))

(Then #"^the report output should be$" [expected-stats]
      (is (= expected-stats @actual-stats)))

;;
;;
;;

(Given #"^no more '([^']*)' remaining in the machine$" [drink]
  (swap! missing-beverages conj (.toLowerCase drink)))

(Then #"^a mail should have been sent indicating '([^']*)' is running out$" [drink]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

