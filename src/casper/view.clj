(ns casper.view
  (:gen-class)
  (:require 
    [casper.const :as const ]
  )
)

; Content-Type 
(def text-plain {"Content-Type" "text/plain; charset=utf-8"})
(def text-html {"Content-Type" "text/html; charset=ISO-8859-4"})

(defn build-response [http-status content]
  { :status http-status :headers text-html
    :body content
  }
)  
