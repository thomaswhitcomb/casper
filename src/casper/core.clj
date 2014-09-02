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
    [ring.util.response :refer [redirect]]
  )
)

; Content-Type for plain text
(def plain-text {"Content-Type" "text/plain; charset=utf-8"})

; HTML for the create form
(def create-html (str "<form method='post' action='/create'>"
                    "<textarea type='text' name='secret' rows='4' cols='50'>What's your secret</textarea >"
                    "<p><input type='submit' /></p>"
                    "</form>"))
; HTTP response codes
(def http-status-ok 200)
(def http-status-created 201)
(def http-status-bad-request 400)
(def http-status-not-found 404)

; Encryption key - TBD needs improvement
(def encryption-key "29dlsdn wp93hfsl;kns\\]opapihjfw")

; Create a unique URL consistent id
(defn unique-key [] (digest/md5 (str (java.util.UUID/randomUUID))))

(defroutes casper-routes           
  (GET "/" [] (redirect "/create"))

  (GET "/create" [] create-html)

  (GET "/secret/:secret" [secret] 
    (let [ record  (first (select-secret secret)) ]
      (delete-secret (get record :key)) 
      (if (= 0 (count record))
        { :status http-status-not-found
          :headers plain-text
          :body "Secret already viewed"
        } 
        { :status http-status-ok
          :headers plain-text
          :body (str (decrypt-base64 (get record :secret) encryption-key))
        } 
      )
    )
  )     

  (GET "/params" {params :params} (str "query params are: " (pr-str params)))

  (GET "/request" request (str "request is: " (pr-str request)))

  (POST "/create" {params :params,port :server-port,server :server-name} 
    (if (not= nil (get params :secret))    
      (let [ 
          encrypted-secret (encrypt-base64 (get params :secret) encryption-key)
          my-key (unique-key) 
         ]
         (insert-secret my-key encrypted-secret)
         { :status http-status-created
           :header plain-text
           :body (str "http://" server ":" port "/secret/" my-key )
         }
      )  
      { :status http-status-bad-request
        :header plain-text
        :body "Missing secret form field"
      } 
    )  
  )

  (route/not-found "<h1>Page not found</h1>")
) 

(def app (handler/site casper-routes))

(defn -main [& port]
  (run-jetty app {:port (Integer/parseInt (first port)) })
)

;(run-jetty app {:port 8080})
