(ns libra.runner
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [clojure.tools.namespace :as tns]
            [libra.bench :as libra]))

(defn- bench
  [options]
  (let [dirs (or (:dir options)
                 #{"bench"})
        namespaces (or (seq (:namespace options))
                       (->> dirs
                            (map io/file)
                            (mapcat tns/find-namespaces-in-dir)
                            distinct))
        inclusion (if (:include options)
                    (apply some-fn (:include options))
                    (constantly true))
        exclusion (if (:exclude options)
                    (complement (apply some-fn (:exclude options)))
                    (constantly true))]
    (when (seq namespaces)
      (apply require :reload namespaces))
    (binding [libra/*selector* (every-pred inclusion exclusion)]
      (apply libra/run-benches namespaces))))

(defn- parse-kw
  [s]
  (if (.startsWith s ":")
    (read-string s)
    (keyword s)))

(defn- acc-opts
  [m k v]
  (update-in m [k] (fnil conj #{}) v))

(def cli-options
  [["-d" "--dir DIR" "Name of the directory containing benchmarks, default \"bench\"."
    :assoc-fn acc-opts]
   ["-n" "--namespace SYMBOL" "Symbol indicating a specific namespace to run benchmarks."
    :parse-fn symbol
    :assoc-fn acc-opts]
   ["-i" "--include KEYWORD" "Run only benchmarks that have this metadata keyword."
    :parse-fn parse-kw
    :assoc-fn acc-opts]
   ["-e" "--exclude KEYWORD" "Exclude benchmarks with this metadata keyword."
    :parse-fn parse-kw
    :assoc-fn acc-opts]
   ["-h" "--help"]])

(defn- help
  [summary]
  (println "Usage: clj -m" (namespace `usage) "[<options>]\n")
  (println summary))

(defn- exit
  [status message]
  (println message)
  (System/exit status))

(defn -main
  "Entry point for the libra runner"
  [& args]
  (let [{:keys [options errors summary]} (cli/parse-opts args cli-options)]
    (cond
      errors (exit 1 (string/join \newline errors))
      (:help options) (help summary)
      :else (bench options))))
