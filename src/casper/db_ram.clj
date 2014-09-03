(ns casper.db-ram
  (:gen-class)
)

(defn now-seconds [] (quot (System/currentTimeMillis) 1000))

(def ram-db (ref {} ))

(defn insert-secret [uid secret ttl] 
  (dosync
    (ref-set ram-db (assoc @ram-db uid {:secret secret :ttl ttl :created_at (now-seconds)}))
  )
) 
(defn select-secret [uid ] 
  (dosync
    (filter (comp not nil?) (list (get @ram-db uid)))
  )
) 
(defn delete-secret [uid ] 
  (dosync
    (ref-set ram-db (dissoc @ram-db uid))
  )
) 
