(ns libra.bench
  (:require [clojure.template :as temp]))

(defn- scaled-time
  [nanos]
  (condp > nanos
    1e3 [nanos "ns"]
    1e6 [(double (/ nanos 1e3)) "µs"]
    1e9 [(double (/ nanos 1e6)) "ms"]
    [(double (/ nanos 1e9)) "sec"]))

(defn- format-time
  [nanos]
  (if (nil? nanos)
    "n/a"
    (let [[scaled unit] (scaled-time nanos)]
      (format "%f %s" scaled unit))))

(defn- filename
  [s]
  (second (re-find #"([\w\.]+)$" s)))

(defn report
  [m]
  (newline)
  (when (:message m)
    (println (str "  " (:message m))))
  (println (format "  time: %s, sd: %s" (format-time (:time m)) (format-time (:sd m)))))

(defn bench-var
  [v]
  (let [m (meta v)]
    (when-let [b (:bench m)]
      (newline)
      (println (str (:name m) " (" (filename (:file m)) ":" (:line m) ")"))
      (b))))

(def ^:dynamic *selector* (constantly true))

(defn bench-ns
  [ns]
  (let [ns-obj (the-ns ns)
        vs (->> (ns-interns ns-obj)
                vals
                (sort-by (comp (juxt :file :line) meta))
                (filter (fn [v]
                          (let [m (meta v)]
                            (and (:bench m) (*selector* m))))))]
    (when (seq vs)
      (newline)
      (println "Measuring" (str ns-obj))
      (doseq [v vs]
        (bench-var v)))))

(defmacro is
  ([expr] `(is ~expr nil))
  ([expr msg] `(report (assoc ~expr :message ~msg))))

(defmacro are
  [argv expr & args]
  (letfn [(message [x]
            (let [s (str x)]
              (if (> (count s) 80) (subs s 0 80) s)))]
    (if (or (and (empty? argv) (empty? args))
            (and (pos? (count argv))
                 (pos? (count args))
                 (zero? (mod (count args) (count argv)))))
      `(temp/do-template ~argv (is ~expr (~message ~argv)) ~@args)
      (throw (IllegalArgumentException. "#args does not match argv")))))

(defmacro ^:deprecated measure
  ([expr] `(is ~expr))
  ([expr msg] `(is ~expr ~msg)))

(defmacro defbench
  [name & body]
  `(def ~(vary-meta name assoc :bench `(fn [] ~@body))
        (fn [] (bench-var (var ~name)))))

(defn run-benches
  ([] (run-benches *ns*))
  ([& namespaces]
   (doseq [ns namespaces]
     (bench-ns ns))))

(defn- mean
  [xs]
  (/ (reduce + xs) (count xs)))

(defn- variance
  [xs]
  (/ (->> (map #(- % (mean xs)) xs)
          (map #(Math/pow % 2))
          (reduce +))
     (dec (count xs))))

(defn- sd
  [xs]
  (Math/sqrt (variance xs)))

(defn dur*
  ([f]
   (let [start (System/nanoTime)]
     (f)
     {:time (double (- (System/nanoTime) start)), :sd nil}))
  ([n f]
   {:pre [(pos? n)]}
   (if (= n 1)
     (dur* f)
     (let [ts (map :time (repeatedly n #(dur* f)))]
       {:time (mean ts), :sd (sd ts)}))))

(defmacro dur
  ([expr] `(dur 1 ~expr))
  ([n expr] `(dur* ~n (fn [] ~expr))))
