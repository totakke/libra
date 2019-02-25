(defproject net.totakke/libra "0.1.2-SNAPSHOT"
  :description "Simple benchmarking framework for Clojure"
  :url "https://github.com/totakke/libra"
  :scm {:dir ".."}
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                                  [criterium "0.4.4"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.0-alpha6"]]}})
