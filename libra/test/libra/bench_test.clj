(ns libra.bench-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [libra.bench :as b]))

(deftest format-time-test
  (are [x s] (= (#'b/format-time x) s)
    12.0 "12.000000 ns"
    12312.0 "12.312000 µs"
    12312312.0 "12.312312 ms"
    12312312312.0 "12.312312 sec"
    nil "n/a"))

(defspec format-time-ns-test
  100
  (prop/for-all [x (gen/double* {:min 0 :max (- 1e3 0.1) :infinite? false :NaN? false})]
    (re-matches #"-?\d{1,3}\.\d{6} ns" (#'b/format-time x))))

(defspec format-time-micros-test
  100
  (prop/for-all [x (gen/double* {:min 1e3 :max (- 1e6 0.1) :infinite? false :NaN? false})]
    (re-matches #"\d{1,3}\.\d{6} µs" (#'b/format-time x))))

(defspec format-time-ms-test
  100
  (prop/for-all [x (gen/double* {:min 1e6 :max (- 1e9 0.1) :infinite? false :NaN? false})]
    (re-matches #"\d{1,3}\.\d{6} ms" (#'b/format-time x))))

(defspec format-time-sec-test
  100
  (prop/for-all [x (gen/double* {:min 1e9 :infinite? false :NaN? false})]
    (re-matches #"\d+\.\d{6} sec" (#'b/format-time x))))

(deftest mean-test
  (are [xs x] (= (#'b/mean xs) x)
    [1 1 1 1 1] 1
    [1 2 3 4 5] 3))

(defspec mean-range-test
  100
  (prop/for-all [v (-> (gen/double* {:min -1e30 :max 1e30 :infinite? false :NaN? false})
                       gen/vector
                       gen/not-empty)]
    (<= (apply min v) (#'b/mean v) (apply max v))))

(deftest variance-test
  (are [xs x] (= (#'b/variance xs) x)
    [1 1 1 1 1] 0.0
    [1 2 3 4 5] 2.5))

(defspec variance-range-test
  100
  (prop/for-all [v (gen/vector (gen/double* {:min -1e30 :max 1e30 :infinite? false :NaN? false})
                               2 100)]
    (<= 0.0 (#'b/variance v))))
