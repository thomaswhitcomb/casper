(ns casper.core
  (:gen-class)
  (:use compojure.core)
  (:use ring.adapter.jetty)
  (:require
    [casper.crypto :refer [encrypt decrypt encrypt-base64 decrypt-base64]] 
    [casper.db :as db ]
    [casper.view :as view ]
    [casper.const :as const ]
    [charset-bytes.core :refer [utf8-bytes]]
    [compojure.handler :as handler]
    [compojure.route :as route]
    [digest :as digest]
    [clojure.data.json :as json]
    [ring.util.response :refer [redirect]]
    [net.cgrand.enlive-html :as html]
  )
)
(html/deftemplate generate-form-page "templates/view-form.html"
  [context]
  [:form#set-form] (html/set-attr :action context)
  [:input#set-value] (html/set-attr :value const/default-ttl)
)
(html/deftemplate generate-message-page "templates/view-message.html"
  [message]
  [:div.response-body] (html/content message)
)
(html/deftemplate generate-secret-page "templates/view-secret.html"
  [secret]
  [:textarea.response-body] (html/content secret)
)
(html/deftemplate generate-url-page "templates/view-url.html"
  [url server context port]
  [:script#swf-path] (html/content (str "ZeroClipboard.config( { swfPath: 'http://" server ":" port context "/html/ZeroClipboard.swf' });" ))
  [:div#clipboard-input] (html/content url)
)

; A regular expression for a list of digits and only digits
(def string-of-digits #"^[0123456789]+$")

; Encryption key - TBD needs improvement
;(def encryption-key (slurp "resources/encrypt-key.txt"))
(def encryption-key (db/get-aws-key "AWS_SECRET_KEY"))

; Create a unique URL consistent id
(defn unique-key [] (digest/md5 (str (java.util.UUID/randomUUID))))

(defn get-size-ttl [j] 
  (let [ m (json/read-str j) 
         size (get m "size" 16)
         ttl (get m "ttl" const/default-ttl)
       ]
    [size ttl]
  )
) 

(defn get-ttl-secret [j] 
  (let [ m (json/read-str j) 
         secret (get m "secret" "")
         ttl (get m "ttl" const/default-ttl)
       ]
    [ttl secret]
  )
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
    (view/build-response const/http-status-bad-request (generate-message-page "Missing Secret"))
    (if (integer? ttl)
      (let [ 
         encrypted-secret (encrypt-base64 (if (nil? secret) "none" secret) encryption-key)
         my-key (unique-key) 
        ]

        (db/insert-secret my-key encrypted-secret ttl)
        (view/build-response const/http-status-created (generate-url-page (create-url server context port my-key) server context port ) )
      )
      (view/build-response const/http-status-bad-request (generate-message-page "Bad or missing TTL"))
    )  
  )  
)  

(defroutes casper-routes           

  (GET "/" {context :context} (view/build-response const/http-status-ok (generate-form-page context)))

  ; good example of a redirect
  ; (GET "/" {context :context} (redirect (str context "/create")))

  (GET "/secret/:id" [id] 
    (let [ record  (db/select-secret id) ]
      (db/delete-secret id) 
      (if (nil? record)
        (view/build-response const/http-status-not-found, (generate-message-page "Secret already viewed"))
        (if (>= (+ (get record :ttl) (get record :created_at)) (db/now-seconds))
          (view/build-response const/http-status-ok (generate-secret-page (decrypt-base64 (get record :secret) encryption-key) ))
          (view/build-response const/http-status-gone (generate-message-page "TTL expired"))
        )  
      )
    )
  )     
  (GET "/params" {params :params} (str "query params are: " (pr-str params)))

  (GET "/request" request (str "request is: " (pr-str request)))

  (GET "/health" [] 
    (view/build-response const/http-status-ok (generate-message-page "i am healthy" ))
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
        (view/build-response const/http-status-bad-request (generate-message-page "TTL missing or bad"))
      )  
    )  
  )

  (POST "/create" {context :context, params :params,port :server-port,server :server-name} 
    (let [ ttl-secret (get-ttl-secret (get params :json)) ] 
      (encrypt-and-save-secret (ttl-secret 0) (ttl-secret 1) server context port)
    )  
  )

  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>")
) 

(def app (handler/site casper-routes))

(defn -main [& port]
  (run-jetty app {:port (Integer/parseInt (first port)) })
)

;(run-jetty app {:port 8080})
