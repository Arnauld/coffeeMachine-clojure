(use 'clojure.test)
(require '[coffee-machine.core :as core])
(import java.math.BigDecimal)


(def TWO (BigDecimal. "2"))

(def actual-output (atom ""))
(def actual-money (atom TWO))

(Before []
  (reset! actual-output "")
  (reset! actual-money TWO))

(Given #"^I've inserted (\d+(?:\.\d+)?)€ in the machine$" [amount]
      (reset! actual-money (BigDecimal. amount)))

(defn- process-order [drink nb-sugar]
      (let [order (core/create-order drink nb-sugar @actual-money)
            output (core/process order)]
          (reset! actual-output output)))

(When #"^I order an? '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (process-order drink (Integer/parseInt nb-sugar-cubes)))

(When #"^I order an extra hot '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (process-order drink (Integer/parseInt nb-sugar-cubes)))

(When #"^I order an? '([^\']*)'$" [drink]
      (process-order drink 0))

(When #"^the message '([^']*)' is sent$" [message]
      (let [output (core/process message)]
          (reset! actual-output output)))

(Then #"^the instruction generated should be '([^']*)'$" [expected-output]
  (is (= expected-output @actual-output)))

