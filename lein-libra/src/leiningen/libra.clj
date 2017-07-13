(ns leiningen.libra
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as tns]
            [leiningen.core.eval :as eval]
            [leiningen.core.project :as project]))

(defn benchmarking-form
  [namespaces]
  (if (seq namespaces)
    (let [ns-sym (gensym "namespaces")]
      `(let [~ns-sym '~namespaces]
         (when (seq ~ns-sym)
           (apply require :reload ~ns-sym))
         (apply libra.bench/run-benches ~ns-sym)))))

(defn libra
  "Measure the project's benchmarks."
  [project]
  (let [libra-profile (merge {:bench-paths ["bench"]} (:libra project))
        project (project/merge-profiles project [{:source-paths (:bench-paths libra-profile)}
                                                 {:libra libra-profile}])
        namespaces (->> (:bench-paths libra-profile)
                        (mapcat (comp tns/find-namespaces-in-dir io/file))
                        distinct)
        form (benchmarking-form namespaces)]
    (eval/eval-in-project project form '(require 'libra.bench))))
