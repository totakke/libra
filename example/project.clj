(defproject example "0.1.0-SNAPSHOT"
  :description "Example of Libra"
  :url "https://github.com/totakke/libra"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :plugins [[net.totakke/lein-libra "0.1.0-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[net.totakke/libra "0.1.0-SNAPSHOT"]]}}
  :libra {:bench-paths ["bench"] ; default "bench"
          })
