(ns leiningen.libra
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as tns]
            [clojure.tools.reader.edn :as edn]
            [leiningen.core.eval :as eval]
            [leiningen.core.project :as project]))

(defn- parse-args
  [args]
  (let [args (map edn/read-string args)]
    (if (= (first args) :only)
      {:only (rest args)})))

(defn- select-namespaces
  [namespaces selectors]
  (let [selector-nses (set (:only selectors))]
    (if (seq selector-nses)
      (filter selector-nses namespaces)
      namespaces)))

(defn benchmarking-form
  [namespaces selectors]
  (let [namespaces (select-namespaces namespaces selectors)]
    (if (seq namespaces)
      (let [ns-sym (gensym "namespaces")]
        `(let [~ns-sym '~namespaces]
           (when (seq ~ns-sym)
             (apply require :reload ~ns-sym))
           (apply libra.bench/run-benches ~ns-sym))))))

(defn libra
  "Measure the project's benchmarks.

A default :only bench-selector is available to run select benchmarks. For
example, `lein libra :only example.foo-bench` only runs benchmarks in the
specified namespace."
  [project & args]
  (let [libra-profile (merge {:bench-paths ["bench"]} (:libra project))
        project (project/merge-profiles project [{:source-paths (:bench-paths libra-profile)}
                                                 {:libra libra-profile}])
        selectors (parse-args args)
        namespaces (->> (:bench-paths libra-profile)
                        (mapcat (comp tns/find-namespaces-in-dir io/file))
                        distinct)
        form (benchmarking-form namespaces selectors)]
    (eval/eval-in-project project form '(require 'libra.bench))))
