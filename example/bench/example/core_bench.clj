(ns example.core-bench
  (:require [libra.bench :refer :all]
            [example.core :refer :all]))

(defbench primes-with-trial-div-bench
  (is (dur 10 (doall (primes-with-trial-div 100000)))))

(defbench primes-with-eratosthenes-bench
  (is (dur 10 (doall (primes-with-eratosthenes 100000)))))
