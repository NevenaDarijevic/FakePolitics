(defproject alati "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [com.novemberain/monger "3.5.0"]
                 [hiccup "1.0.5"]
                [ring/ring-jetty-adapter "1.9.4"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler alati.handler/app}
  :main alati.web                                           ;start
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
