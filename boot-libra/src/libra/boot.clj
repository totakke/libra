(ns libra.boot
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as tns]
            [boot.core :as core :refer [deftask]]
            [boot.pod :as pod]))

(def ^:private pod-deps '[])

(defn- init [fresh-pod]
  (pod/require-in fresh-pod '[libra.bench]))

(deftask libra
  "Measure the project's benchmarks."
  []
  (let [updated-env (update-in (core/get-env) [:dependencies] into pod-deps)
        worker-pods (pod/pod-pool updated-env :init init)]
    (core/cleanup (worker-pods :shutdown))
    (core/with-pre-wrap fileset
      (let [worker-pod (worker-pods :refresh)
            namespaces (->> (:source-paths (core/get-env))
                            (map io/file)
                            (mapcat tns/find-namespaces-in-dir)
                            distinct)
            ns-sym (gensym "namespaces")]
        (if (seq namespaces)
          (pod/with-eval-in worker-pod
            (let [~ns-sym '~namespaces]
              (when (seq ~ns-sym)
                (apply require :reload ~ns-sym))
              (apply libra.bench/run-benches ~ns-sym)))))
      (core/commit! fileset))))
