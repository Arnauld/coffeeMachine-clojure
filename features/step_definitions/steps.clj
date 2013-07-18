(use 'clojure.test)
(require '[coffee-machine.core :as core])

(def *output* (atom ""))

(When #"^I order a '([^\']*)' with (\d+) sugar$" [drink nb-sugar-cubes]
      (let [nb-sugar (Integer/parseInt nb-sugar-cubes)
            order (core/create-order drink nb-sugar)
            output (core/process order)]
          (reset! *output* output)))

(When #"^the message '([^']*)' is sent$" [message]
      (let [output (core/process message)]
          (reset! *output* output)))

(Then #"^the instruction generated should be '([^']*)'$" [expected-output]
  (is (= expected-output @*output*)))

(Given #"^I've inserted (\d+(?:\.\d+)?)€ in the machine$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))