
https://github.com/technomancy/leiningen/blob/master/doc/MIXED_PROJECTS.md

:java-source-path

new Folder: `java`

```clojure
(defproject coffee-machine "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[lein-cucumber "1.0.2"]]
  :cucumber-feature-paths ["features/"]
  :java-source-paths ["java"]
  :javac-options     ["-target" "1.6" "-source" "1.6"])
```