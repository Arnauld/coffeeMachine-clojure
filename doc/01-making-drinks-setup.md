
```bash
  $ lein new coffee-machine
``

In `project.clj`:

```clojure
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[lein-cucumber "1.0.2"]]
  :cucumber-feature-paths ["features/"]
```

Copy the first feature file from `coffeeMachine-java` into `features/01-making-drinks.feature`:

```gherkin
Feature: Making Drinks
  In order to send commands to the drink maker
  As a developer
  I want to implement the logic that translates orders
  from customers of the coffee machine to the drink maker

Scenario: 1 tea with 1 sugar and a stick

  When I order a "Tea" with 1 sugar
  Then the instruction generated should be "T:1:0"

Scenario: 1 chocolate with no sugar - and therefore no stick

  When I order a "Chocolate" with 0 sugar
  Then the instruction generated should be "H::"

Scenario: 1 tea with 1 sugar and a stick

  When I order a "Coffee" with 2 sugar
  Then the instruction generated should be "C:2:0"

Scenario Outline: any message received is forwarded for the customer to see
  When the message "<message>" is sent
  Then the instruction generated should be "<expected>"

  Examples:
   | message          |  expected           |
   | Hello            |  M:Hello            |
   | Not enough money |  M:Not enough money |
```

```bash
  $ lein cucumber
  Exception in thread "main" java.lang.IllegalArgumentException: Not a file or directory: 
  /Users/arnauld/Projects/cucumber-jvm-series/coffeeMachine-clojure/features/step_definitions
  at cucumber.runtime.io.FileResourceIterator$FileIterator.<init>(FileResourceIterator.java:54)
  at cucumber.runtime.io.FileResourceIterator.<init>(FileResourceIterator.java:20)
  at cucumber.runtime.io.FileResourceIterable.iterator(FileResourceIterable.java:19)
  at clojure.lang.RT.seqFrom(RT.java:495)
  ...
```

Ok let's create the missing folder: `features/step_definitions`

```bash
  $ lein cucumber
  Running cucumber...
Looking for features in:  [/Users/arnauld/Projects/cucumber-jvm-series/coffeeMachine-clojure/features]
Looking for glue in:  [/Users/arnauld/Projects/cucumber-jvm-series/coffeeMachine-clojure/features/step_definitions]
UUUUUUUUUU


You can implement missing steps with the snippets below:

(When #"^I order a \"([^\"]*)\" with (\d+) sugar$" [arg1 arg2]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

(Then #"^the instruction generated should be \"([^\"]*)\"$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))

(When #"^the message \"([^\"]*)\" is sent$" [arg1]
  (comment  Express the Regexp above with the code you wish you had  )
  (throw (cucumber.runtime.PendingException.)))
```

Hu hu! Let's start implementing those specifications!

Weird it is required the step definitions be defined directly at the root of `step_definitions/`

