```
You can implement missing steps with the snippets below:

(When #"^I order an extra hot 'Tea' with (\d+) sugar$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

(When #"^I order an 'Orange Juice'$" []
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

(When #"^I order an 'Orange Juice' with (\d+) sugar$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

(When #"^I order an extra hot 'Orange Juice' with (\d+) sugar$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))
```



Forgot to reset state between tests

```clojure
(Before []
  (reset! actual-output "")
  (reset! actual-money TWO))
```