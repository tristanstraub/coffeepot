(set-env! :dependencies (expand-keywords! [:clojure
                                           :midje
                                           :core-logic
                                           :boot-test
                                           :incubator])
          :source-paths #{"src"}
          :test-paths #{"src"})

(apply require (tasks :boot-test))
