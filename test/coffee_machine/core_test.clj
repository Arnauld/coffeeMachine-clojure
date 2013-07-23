(ns coffee-machine.core-test
  (:use clojure.test
        coffee-machine.core))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest drink-by-label-test
  (testing "FIXME, I fail."
    (is (thrown? IllegalArgumentException (drink-by-label "chocolator")))
    (is (= :chocolate (:kw (drink-by-label "chocolate"))))
    (is (= :chocolate (:kw (drink-by-label "chOcOlAtE"))))))