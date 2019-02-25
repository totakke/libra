(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/tools.namespace "0.2.11"]])

(def +version+ "0.1.1-SNAPSHOT")

(task-options!
 pom {:project 'net.totakke/boot-libra
      :version +version+
      :description "Libra Boot task"
      :url "https://github.com/totakke/libra"
      :scm {:url "https://github.com/totakke/libra"}
      :license {"The MIT License" "https://opensource.org/licenses/MIT"}})
