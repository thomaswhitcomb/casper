(ns casper.core
  (:gen-class)
  (:use compojure.core)
  (:use ring.adapter.jetty)
  (:require
    [ casper.crypto :refer [encrypt decrypt encrypt-base64 decrypt-base64]] 
    ;[ casper.db-ram :refer [insert-secret select-secret delete-secret now-seconds]] 
    [ casper.db :refer [insert-secret select-secret delete-secret now-seconds get-aws-key]] 
    [charset-bytes.core :refer [utf8-bytes]]
    [compojure.handler :as handler]
    [compojure.route :as route]
    [digest :as digest]
    [clojure.data.json :as json]
    [ring.util.response :refer [redirect]]
  )
)

; Content-Type 
(def text-plain {"Content-Type" "text/plain; charset=utf-8"})
(def text-html {"Content-Type" "text/html; charset=ISO-8859-4"})

; Default TTL
(def default-ttl 15)

; A regular expression for a list of digits and only digits
(def string-of-digits #"^[0123456789]+$")

; HTTP response codes
(def http-status-ok 200)
(def http-status-created 201)
(def http-status-bad-request 400)
(def http-status-not-found 404)
(def http-status-gone 410)

; Encryption key - TBD needs improvement
;(def encryption-key (slurp "resources/encrypt-key.txt"))
(def encryption-key (get-aws-key "AWS_SECRET_KEY"))

; Create a unique URL consistent id
(defn unique-key [] (digest/md5 (str (java.util.UUID/randomUUID))))

(defn get-size-ttl [j] 
  (let [ m (json/read-str j) 
         size (get m "size" 16)
         ttl (get m "ttl" default-ttl)
       ]
    [size ttl]
  )
) 

(defn get-ttl-secret [j] 
  (let [ m (json/read-str j) 
         secret (get m "secret" "")
         ttl (get m "ttl" default-ttl)
       ]
    [ttl secret]
  )
) 

; HTML for the create form
(defn create-html [context] 
    {
      :status http-status-ok   
      :body (str "<form method='post' action='" context "/'>"
                    "<textarea type='text' name='secret' rows='4' cols='50' placeholder='What is your secret'></textarea >"
                    "<p><input type='text' name='ttl' placeholder='TTL (seconds)' value='" default-ttl "'/></p>"
                    "<p><input type='submit' /></p>"
                    "</form>")
      :headers text-html
    }
)                             

(defn build-response [http-status content]
  { :status http-status :headers text-plain :body content }
)  

(defn random-char [_] (char (+ 33 (rand-int 93))))

(defn create-secret [size] 
  (apply str (map random-char (range size)))
)

(defn create-url [server context port my-key]
   (str (if (even? port) "http" "https") "://" server ":" port context "/secret/" my-key )
)  

; ttl - integer
; secret - string of anything
(defn encrypt-and-save-secret [ttl secret server context port]

  (if (nil? secret)
    (build-response http-status-bad-request "Missing Secret")
    (if (integer? ttl)
      (let [ 
         encrypted-secret (encrypt-base64 (if (nil? secret) "none" secret) encryption-key)
         my-key (unique-key) 
        ]

        (insert-secret my-key encrypted-secret ttl)
        (build-response http-status-created (create-url server context port my-key))
      )
      (build-response http-status-bad-request "Missing or bad TTL")
    )  
  )  
)  

(defroutes casper-routes           

  (GET "/" {context :context} (create-html context))

  ; good example of a redirect
  ; (GET "/" {context :context} (redirect (str context "/create")))

  (GET "/secret/:id" [id] 
    (let [ record  (select-secret id) ]
      (delete-secret id) 
      (if (nil? record)
        (build-response http-status-not-found, "Secret already viewed")
        (if (>= (+ (get record :ttl) (get record :created_at)) (now-seconds))
          (build-response http-status-ok (str (decrypt-base64 (get record :secret) encryption-key)))
          (build-response http-status-gone "TTL expired")
        )  
      )
    )
  )     
  (GET "/params" {params :params} (str "query params are: " (pr-str params)))

  (GET "/request" request (str "request is: " (pr-str request)))

  (GET "/health" [] 
    (build-response http-status-ok "i am healthy" )
  )     

  (POST "/auto" {context :context, params :params, port :server-port,server :server-name}
    (let [ size-ttl (get-size-ttl (get params :json)) ] 
      (encrypt-and-save-secret (size-ttl 1) (create-secret (size-ttl 0)) server context port)
    )
  )     

  (POST "/" {context :context, params :params,port :server-port,server :server-name} 
    (let [ ttl (get params :ttl "")] 
      (if (re-find  string-of-digits ttl) 
        (encrypt-and-save-secret (Integer/parseInt ttl) (get params :secret) server context port)
        (build-response http-status-bad-request "TTL missing or bad")
      )  
    )  
  )

  (POST "/create" {context :context, params :params,port :server-port,server :server-name} 
    (let [ ttl-secret (get-ttl-secret (get params :json)) ] 
      (encrypt-and-save-secret (ttl-secret 0) (ttl-secret 1) server context port)
    )  
  )

  (route/not-found "<h1>Page not found</h1>")
) 

(def app (handler/site casper-routes))

(defn -main [& port]
  (run-jetty app {:port (Integer/parseInt (first port)) })
)

;(run-jetty app {:port 8080})
