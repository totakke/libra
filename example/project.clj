(defproject example "0.1.0-SNAPSHOT"
  :description "Example of Libra"
  :url "https://github.com/totakke/libra"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :plugins [[net.totakke/lein-libra "0.1.2"]]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [net.totakke/libra "0.1.1"]]}}
  :libra {:bench-paths ["bench"] ; default "bench"
          :bench-selectors {:default (complement :slow)
                            :slow :slow}})
