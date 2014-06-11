(ns finagle-clojure.futures-test
  (:refer-clojure :exclude [await for map])
  (:require [finagle-clojure.futures :refer :all]
            [finagle-clojure.scala :as scala]
            [midje.sweet :refer :all]))

(set! *warn-on-reflection* true)

(fact "flatmap"
  (await (flatmap (value "hi") [s] (value (.toUpperCase ^String s)))) => "HI")

(fact "nested flatmap"
  (await (flatmap (value "hi") [s] (flatmap (value "bob") [t] (value (str s " " t)))))  => "hi bob")

(fact "map"
  (await (map (value "hi") [s] (.toUpperCase ^String s))) => "HI")

(fact "for"
  (await (for [a (value 1)
             b (value 2)]
    (value (+ a b)))) => 3)

(fact "for chain"
  (await (for [a (value 1)
             b (value (inc a))]
    (value (+ a b)))) => 3)

(fact "exception"
  (await (exception (Exception.))) => (throws Exception))

(facts "rescue"
  (await (rescue (exception (Exception.)) [^Throwable t] (value 1))) => 1
  (await (rescue (exception (Exception.)) [t] (value 1))) => 1
  ;; this throws because the domain of the Function passed to rescue doesn't include Throwable
  (await (rescue (exception (Exception.)) [^String s] (value 1))) => (throws Exception))

(facts "handle"
  (await (handle (exception (Exception.)) [^Throwable t] 1)) => 1
  (await (handle (exception (Exception.)) [t] 1)) => 1
  ;; this throws because the domain of the Function passed to handle doesn't include Throwable
  (await (handle (exception (Exception.)) [^String s] 1)) => (throws Exception))

(fact "collect"
  (await (collect [(value 1) (value 2)])) => [1 2])
