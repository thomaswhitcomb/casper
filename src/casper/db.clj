(ns casper.db
  (:require [clojure.java.jdbc :as sql])
  (:gen-class)
)

(def database 
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "database.db"
  }
)
;(def database 
;  {:classname   "org.h2.Driver"
;   :subprotocol "h2"
;   ;:subname     "db/database.db"
;   :subname     "jdbc:h2:mem:test"
;  }
;)
(def testdata 
  {
   :key "aa-bb-cc-dd"
   :secret "===adsf=sdf==sd=f=sd=f===sd=sdf23rwebdfg"
  }
) 

(defn drop-db []
  (try ( sql/db-do-commands database 
     (sql/drop-table-ddl :secrets
     )
  )                    
  (catch Exception e (println e))
  )
)

(defn create-db []

  (try (sql/db-do-commands database
    (sql/create-table-ddl :secrets
       [:id :serial "PRIMARY KEY"]
       [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
       [:key :varchar]
       [:secret :varchar]
    )
  )
  (catch Exception e (println e))
  )
)

(defn insert-secret [key secret] 
  (sql/insert! database :secrets { :key key :secret secret })
) 
(defn select-secret [key ] 
  (sql/query database [(str "SELECT id,key,secret,created_at FROM secrets WHERE key = '" key "';")])
) 
(defn delete-secret [key ] 
  (sql/delete! database  :secrets [(str "key = '" key "'" )])
) 
(drop-db)
(create-db)
(sql/insert! database :secrets testdata)
