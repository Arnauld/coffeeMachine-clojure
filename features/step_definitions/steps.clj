(use 'clojure.test)
(require '[coffee-machine.core :as core])
(import java.math.BigDecimal)


(def TWO (BigDecimal. "2"))

(def *output* (atom ""))
(def *money* (atom TWO))

(Given #"^I've inserted (\d+(?:\.\d+)?)€ in the machine$" [amount]
      (reset! *money* (BigDecimal. amount)))

(defn- process-order [drink nb-sugar]
      (let [order (core/create-order drink nb-sugar @*money*)
            output (core/process order)]
          (reset! *output* output)))

(When #"^I order a '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (process-order drink (Integer/parseInt nb-sugar-cubes)))

(When #"^I order an? '([^\']*)'$" [drink]
      (process-order drink 0))

(When #"^the message '([^']*)' is sent$" [message]
      (let [output (core/process message)]
          (reset! *output* output)))

(Then #"^the instruction generated should be '([^']*)'$" [expected-output]
  (is (= expected-output @*output*)))

