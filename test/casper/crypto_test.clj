(ns casper.crypto-test
    (:require
          [casper.crypto :refer :all]
          [clojure.test :refer :all]
          [charset-bytes.core :refer [utf8-bytes]]
    )
    (:refer-clojure :exclude [key]))

(def key "test key")
(def value "test value")

(deftest test-encrypt
  (is (not (= value 
           (encrypt (utf8-bytes value) key)))))

(deftest test-encrypt-base64
  (is (not (= value 
           (encrypt-base64 value key)))))

(deftest test-decrypt
  (is (= value 
           (String. (decrypt (encrypt (utf8-bytes value) key) key)))))

(deftest test-decrypt-base64
  (is (= value 
           (decrypt-base64 (encrypt-base64 value key) key))))
