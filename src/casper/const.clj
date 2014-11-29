(ns casper.const
  (:gen-class)
)

; Default TTL
(def default-ttl 600)

; HTTP response codes
(def http-status-ok 200)
(def http-status-created 201)
(def http-status-bad-request 400)
(def http-status-not-found 404)
(def http-status-gone 410)
