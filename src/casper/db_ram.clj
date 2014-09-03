(ns casper.db-ram
  (:gen-class)
)

(defn now-millis [] (quot (System/currentTimeMillis) 1000))

(def ram-db (ref {} ))

(defn insert-secret [key secret] 
  (dosync
    (ref-set ram-db (assoc @ram-db key {:secret secret :created_at (now-millis)}))
  )
) 
(defn select-secret [key ] 
  (dosync
    (filter (comp not nil?) (list (get @ram-db key)))
  )
) 
(defn delete-secret [key ] 
  (dosync
    (ref-set ram-db (dissoc @ram-db key))
  )
) 
