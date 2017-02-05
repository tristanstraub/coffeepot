(ns coffeepot.core-test
  (:require [coffeepot.core :refer [emit-form emit-main parse-forms]])
  (:use midje.sweet))

(fact "function apply"
  (emit-form (first (parse-forms "(a 1 2)")))
  => "a(1,2)")

(fact "main"
  (emit-main)
  => "int main() {
}")

(fact "map"
  (-> "(map + [1 2 3] [4 5 6]) (+ 2 3)"
      parse-forms)

  => '((map + [1 2 3] [4 5 6]) (+ 2 3)))

(fact "parse"
  (-> "(a b [c] [d] [e]) (b)"
      parse-forms)

  => '((a b [c] [d] [e]) (b)))

(fact "main with body"
  (emit-main '(map plus [1 2 3] [4 5 6]))
  => "int main() {
}")
