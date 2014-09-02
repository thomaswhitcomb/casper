(ns casper.core
  (:gen-class)
  (:use compojure.core)
  (:use ring.adapter.jetty)
  (:require
    [ casper.crypto :refer [encrypt decrypt encrypt-base64 decrypt-base64]] 
    [ casper.db :refer [insert-secret select-secret delete-secret]] 
    [charset-bytes.core :refer [utf8-bytes]]
    [compojure.handler :as handler]
    [compojure.route :as route]
    [digest :as digest]
  )
)

(defn unique-key [] (digest/md5 (str (java.util.UUID/randomUUID))))

(defroutes casper-routes           
  (GET "/" [] "<h1>Create a Secret</h1><a href='./create'>Here</a>")

  (GET "/create" [] "<form method='post' action='/create'> <textarea type='text' name='secret' rows='4' cols='50'>What's your secret</textarea ><p><input type='submit' /></p></form>")

  (GET "/secret/:secret" [secret] 
    (let [ record  (first (select-secret secret)) ]
      (delete-secret (get record :key)) 
      (if (= 0 (count record))
        { :status 404
          :headers {"Content-Type" "text/plain; charset=utf-8"}
          :body "Secret already viewed"
        } 
        { :status 200
          :headers {"Content-Type" "text/plain; charset=utf-8"}
          :body (str (decrypt-base64 (get record :secret) "THE KEY"))
        } 
      )
    )
  )     

  (GET "/test" []  
    { :status 404
      :headers {"Content-Type" "text/html; charset=utf-8"}
      :body "<h1>Just returned a 404</h1>"
    }
  )

  (GET "/params" {params :params} (str "query params are: " (pr-str params)))

  (GET "/request" request (str "request is: " (pr-str request)))

  (POST "/create" {params :params,port :server-port,server :server-name} 
    (if (not= nil (get params :secret))    
      (let [ 
          encrypted-secret (encrypt-base64 (get params :secret) "THE KEY")
          my-key (unique-key) 
         ]
         (insert-secret my-key encrypted-secret)
         { :status 201
           :header {"Content-Type" "text/plain; charset=utf-8"}
           :body (str "http://" server ":" port "/secret/" my-key )
         }
      )  
      { :status 400
        :header {"Content-Type" "text/plain; charset=utf-8"}
        :body "Missing secret form field"
      } 
    )  
  )
  (route/not-found "Page not found")
) 

(def app (handler/site casper-routes))

(defn -main [& m]
  (run-jetty app {:port 8080 })
)

;(run-jetty app {:port 8080})
