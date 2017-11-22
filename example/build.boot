(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [criterium "0.4.4" :scope "test"]
                 [net.totakke/libra "0.1.1" :scope "test"]
                 [net.totakke/boot-libra "0.1.0" :scope "test"]])

(require '[libra.boot :refer [libra]])

(deftask benchmarking
  "Profile setup for running benchmarks."
  []
  (set-env! :source-paths #(conj % "bench"))
  identity)
