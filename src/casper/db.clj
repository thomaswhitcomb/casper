(ns casper.db
  (:require [taoensso.faraday :as dynamodb] )
  (:gen-class)
)

(def table :casper)
(def local-db "none")

(defn get-property [field] (. System getProperty field))
(defn now-seconds [] (quot (System/currentTimeMillis) 1000))
(defn get-aws-key [k] (if-let [id (get-property k) ] id local-db))

(def client-opts 
  (let [
        access-key (get-aws-key "AWS_ACCESS_KEY_ID")
        secret-key (get-aws-key "AWS_SECRET_KEY") 
        opts {:access-key access-key, :secret-key secret-key}
       ]
      (if (= local-db access-key)
        (assoc opts :endpoint"http://localhost:8000")
        opts
      )  
  )
)  


(defn drop-db []
  (let [tables (dynamodb/list-tables client-opts)]
    (if (= tables [table])
      (dynamodb/delete-table client-opts table)
    )  
  )  
)

(defn create-db []
  (let [tables (dynamodb/list-tables client-opts)]
    (if (= 0 (count tables)) 
      (dynamodb/create-table client-opts table [:key :s] {:throughput {:read 1 :write 1} :block? true})
    )  
  )  
)

(defn insert-secret [key secret ttl] 
  (dynamodb/put-item client-opts table {:key key :secret secret :ttl ttl :created_at (now-seconds) })
) 
(defn select-secret [key ] 
  (dynamodb/get-item client-opts table {:key key})
) 
(defn delete-secret [key ] 
  (dynamodb/delete-item client-opts table {:key key})
)
(println client-opts)
(create-db)
(insert-secret "blah" "blahblah" 115)
(delete-secret "blah")
