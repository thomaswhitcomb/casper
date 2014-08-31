(ns casper.crypto-test
    (:require
          [casper.crypto :refer :all]
          [clojure.test :refer :all])
    (:refer-clojure :exclude [key]))

(def key "test key")
(def value "test value")

(deftest test-encrypt
  (is (not (= value 
           (encrypt value key)))))

(deftest test-decrypt
  (is (= value 
           (decrypt (encrypt value key) key))))
