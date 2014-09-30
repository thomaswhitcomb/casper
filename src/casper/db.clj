(ns casper.db
  (:require [taoensso.faraday :as far] )
  (:gen-class)
)
(defn now-seconds [] (quot (System/currentTimeMillis) 1000))

;(def client-opts {:access-key "some key" :secret-key "secret-key" :endpoint "http://localhost:8000"})
(def client-opts 
  (let [
        access-key (if-let [k (. System getProperty "AWS_ACCESS_KEY_ID") ] k "none")
        secret-key (if-let [k (. System getProperty "AWS_ACCESS_KEY_ID") ] k "none")
       ]
   {:access-key access-key, :secret-key secret-key :endpoint "http://localhost:8000"} 
  )
)  


(def table :casper)

(defn drop-db []
  (try 
    (far/delete-table client-opts table)
  (catch Exception e (str "caught exception: " (.getMessage e))))  
)

(defn create-db []
  (far/create-table client-opts table [:key :s] {:throughput {:read 1 :write 1} :block? true})
)

(defn insert-secret [key secret ttl] 
  (far/put-item client-opts table {:key key :secret secret :ttl ttl :created_at (now-seconds) })
) 
(defn select-secret [key ] 
  (far/get-item client-opts table {:key key})
) 
(defn delete-secret [key ] 
  (far/delete-item client-opts table {:key key})
)
(println client-opts)
(drop-db)
(create-db)
(insert-secret "blah" "blahblah" 115)
(delete-secret "blah")
