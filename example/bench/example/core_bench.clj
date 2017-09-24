(ns example.core-bench
  (:require [libra.bench :refer :all]
            [libra.criterium :as c]
            [example.core :refer :all]))

(defbench primes-with-trial-div-bench
  (is (dur 10 (doall (primes-with-trial-div 100000)))))

(defbench primes-with-eratosthenes-bench
  (is (dur 10 (doall (primes-with-eratosthenes 100000)))))

(defbench primes-with-trial-div-bench-precise
  (is (c/bench (doall (primes-with-trial-div 100000)))))

(defbench primes-with-eratosthenes-bench-precise
  (is (c/bench (doall (primes-with-eratosthenes 100000)))))
