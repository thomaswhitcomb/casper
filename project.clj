(defproject casper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojars.amit/commons-codec "1.8.0"] 
                 [charset-bytes "1.0.0"] 
                 [ring/ring-core "1.3.0"] 
                 [ring/ring-jetty-adapter "1.3.0"]
                 [compojure "1.1.6"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [digest "1.4.4"]
                 ]
  :main ^:skip-aot casper.core
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler casper.core/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
