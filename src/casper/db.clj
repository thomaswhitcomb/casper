(ns casper.db
  (:require [clojure.java.jdbc :as sql])
  (:gen-class)
)

(def database 
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
  }
)
(def testdata 
  {
   :uuid "aa-bb-cc-dd"
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
       [:uuid :varchar]
       [:secret :varchar]
    )
  )
  (catch Exception e (println e))
  )
)

(defn insert-secret [uuid secret] 
  (sql/insert! database :secrets { :uuid uuid :secret secret })
) 
(defn select-secret [uuid ] 
  (sql/query database [(str "SELECT id,uuid,secret,created_at FROM secrets WHERE uuid = '" uuid "';")])
) 
(defn delete-secret [uuid ] 
  (sql/delete! database  :secrets [(str "uuid = '" uuid "'" )])
) 
(drop-db)
(create-db)
(sql/insert! database :secrets testdata)


