(ns libra.criterium
  (:require [criterium.core :as c]))

(defmacro bench
  [expr]
  `(let [result# (c/benchmark ~expr {})]
     {:time (* (first (:mean result#)) 1e9)
      :sd (* (first (:sample-variance result#)) 1e9)}))

(defmacro quick-bench
  [expr]
  `(let [result# (c/quick-benchmark ~expr {})]
     {:time (* (first (:mean result#)) 1e9)
      :sd (* (first (:sample-variance result#)) 1e9)}))
