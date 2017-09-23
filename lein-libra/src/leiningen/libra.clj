(ns leiningen.libra
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.namespace :as tns]
            [clojure.tools.reader.edn :as edn]
            [leiningen.core.eval :as eval]
            [leiningen.core.project :as project]))

(defn- split-args
  [args]
  (let [[nses selectors] (split-with (complement keyword?) args)]
    (loop [acc {} [selector & selectors] selectors]
      (if (seq selectors)
        (let [[args next] (split-with (complement keyword?) selectors)]
          (recur (assoc acc selector args) next))
        (if selector
          (assoc acc selector ())
          acc)))))

(defn- parse-only
  [xs]
  (->> (map str xs)
       (map (fn [s]
              (let [[ns* v] (string/split s #"/")]
                [ns* v])))
       (group-by first)
       (map (fn [[k v]]
              [(edn/read-string k)
               (->> (keep second v)
                    distinct
                    (map #(str k "/" %))
                    (map edn/read-string)
                    seq)]))
       (into {})))

(defn- parse-args
  [args project]
  (let [given-selectors (split-args args)
        project-selectors (merge {:all '(constantly true)
                                  :only '(constantly true)}
                                 (-> project :libra :bench-selectors))
        selectors (->> given-selectors
                       (keep (fn [[k v]]
                               (if-let [selector (k project-selectors)]
                                 [k (if (= k :only)
                                      (parse-only v)
                                      selector)])))
                       (into {}))]
    (if (seq selectors)
      selectors
      (select-keys project-selectors [:default]))))

(defn- select-namespaces
  [namespaces selectors]
  (let [selector-nses (-> (:only selectors) keys set)]
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
           (doseq [ns# ~ns-sym]
             (if-let [only# (:only '~selectors)]
               (if-let [vs# (get only# ns#)]
                 (doseq [v# vs#] (libra.bench/bench-var (resolve v#)))
                 (libra.bench/bench-ns ns#))
               (binding [libra.bench/*selector* (->> (vals '~selectors)
                                                     (map eval)
                                                     (apply every-pred))]
                 (libra.bench/bench-ns ns#)))))))))

(defn libra
  "Measure the project's benchmarks.

A default :only bench-selector is available to run select benchmarks. For
example, `lein libra :only example.foo-bench` only runs benchmarks in the
specified namespace."
  [project & args]
  (let [libra-profile (merge {:bench-paths ["bench"]} (:libra project))
        project (project/merge-profiles project [{:source-paths (:bench-paths libra-profile)}])
        selectors (parse-args (map edn/read-string args) project)
        _ (eval/prep project)
        namespaces (->> (:bench-paths libra-profile)
                        (mapcat (comp tns/find-namespaces-in-dir io/file))
                        distinct)
        form (benchmarking-form namespaces selectors)]
    (eval/eval-in-project project form '(require 'libra.bench))))
