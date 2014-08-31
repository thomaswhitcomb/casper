(ns casper.core
  (:gen-class)
  (:use compojure.core)
  (:use ring.adapter.jetty)
  (:require
    [ casper.crypto :refer [encrypt decrypt]] 
    [charset-bytes.core :refer [utf8-bytes]]
    [compojure.handler :as handler]
    [compojure.route :as route]
  )
)

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defroutes casper-routes           
  (GET "/" [] "<h1>Create a Secret</h1><a href='./create'>Here</a>")
  (GET "/create" [] "<form method='post' action='/create'> What's your secret? <input type='text' name='secret' /><input type='submit' /></form>")
  (GET "/secret/:secret" [secret] (str "<p>Secret is " (decrypt secret "THE KEY") "</p>"))
  (GET "/test" []  
    { :status 404
      :headers {"Content-Type" "text/html; charset=utf-8"}
      :body "<h1>Just returned a 404</h1>"
    }
  )
  (GET "/params" {params :params} (str "query params are: " (pr-str params)))
  (GET "/request" request (str "request is: " (pr-str request)))
  (POST "/create" {params :params} (str (String. (encrypt (get params :secret) "THE KEY") ) "UUID:" (uuid)))
  (route/not-found "Page not found")
) 

(def app (handler/site casper-routes))

(defn -main [& m]
  (run-jetty app {:port 8080 })
)

;(run-jetty app {:port 8080})
