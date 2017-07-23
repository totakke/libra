(defproject net.totakke/libra "0.1.0-SNAPSHOT"
  :description "Simple benchmarking framework for Clojure"
  :url "https://github.com/totakke/libra"
  :scm {:dir ".."}
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})
