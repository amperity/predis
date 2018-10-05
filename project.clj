(defproject amperity/predis "0.2.3"
  :description "An in-memory Redis mock for Clojure"
  :url "https://github.com/amperity/predis"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/carmine "2.19.0"]
                 [org.clojure/test.check "0.7.0"]]
  :profiles {:repl
             {:source-paths ["dev"]}
             :uberjar {:aot :all}}
  :plugins [[codox "0.8.12"]]
  :codox {:src-dir-uri "http://github.com/amperity/predis/tree/master"})
