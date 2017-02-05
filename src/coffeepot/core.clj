(ns coffeepot.core
  (:require [clojure.string :as str]
            [clojure.core.strint :refer [<<]]))

(defn parse-forms [input]
  (loop [[c & cs]                input
         stack                   []
         current-form-list       []
         current-form-list-type  nil
         current-form-chars      []
         current-form-chars-type nil]

    (let [stop-atom #(let [s (apply str current-form-chars)]
                       (cond (re-find #"^\d+$" s)
                             (Integer/parseInt s)

                             :else
                             (symbol s)


                             ;; :else
                             ;; current-form-chars
                             ))

          stop-atom-and-append #(if (empty? current-form-chars)
                                  current-form-list
                                  (conj current-form-list (stop-atom)))

          stop-list #(cond (= :vector current-form-list-type)
                           (stop-atom-and-append)
                           :else
                           (apply list (stop-atom-and-append)))]

      (cond (#{\space \tab} c)
            (recur cs stack
                   (stop-atom-and-append) current-form-list-type
                   [] nil)

            (#{\( \[} c)
            (recur cs (conj stack [(stop-atom-and-append) current-form-list-type])
                   [] (if (= c \[) :vector)
                   [] nil)

            (#{\) \]} c)
            (let [[prev-list form-list-type] (last stack)]
              (recur cs (into [] (butlast stack))
                     (conj prev-list (stop-list)) form-list-type
                     [] nil))

            (and (re-find #"[^() \t]" (str c))
                 (or (= :char-list current-form-chars-type)
                     (nil? current-form-chars-type)))
            (recur cs stack
                   current-form-list current-form-list-type
                   (conj current-form-chars c) :char-list)

            (not c)
            (let [[prev-list form-list-type] (last stack)]
              (concat prev-list (stop-list)))
            :else
            :error))))

(declare emit-form emit-forms)

(defn- args->list [& args]
  (str/join "," (map emit-form args)))

(defn emit-function-def [name & body]
  (<< "int ~{name}() {
~{(apply emit-forms body)}}"))

(defn- vector->value [form]
  (<< "Value[]{~{(apply args->list form)}}"))

(defn emit-form [form]
  (cond (list? form)
        (let [[f & args] form]
          (str (emit-form f) "(" (apply args->list args) ")"))
        (vector? form)
        (vector->value form)
        (symbol? form)
        (name form)
        (number? form)
        form
        :else
        ""))

(defn emit-forms [& forms]
  (str/join ";"
   (for [form forms]
     (emit-form form))))

(defn emit-main [& body]
  (apply emit-function-def "main" body))
