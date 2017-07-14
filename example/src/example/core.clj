(ns example.core)

;; Trial division

(defn prime-with-trial-div?
  [n]
  (cond
    (< n 2) false
    (= n 2) true
    (zero? (mod n 2)) false
    :else (->> (range 3 n 2)
               (take-while #(<= % (/ n %)))
               (every? #(pos? (mod n %))))))

(defn primes-with-trial-div
  [n]
  (filter prime-with-trial-div? (range (inc n))))

;; Sieve of Eratosthenes

(defn primes-with-eratosthenes
  [n]
  (let [m (Math/sqrt n)]
    (loop [[f & r :as xs] (range 2 n)
           primes []]
      (if (<= f m)
        (recur (remove #(zero? (mod % f)) r) (conj primes f))
        (concat primes xs)))))
