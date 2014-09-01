(ns casper.crypto
    (:gen-class)
    (:require
          [charset-bytes.core :refer [utf8-bytes]]
    )
    (:import 
          [javax.crypto Cipher KeyGenerator SecretKey]
          [javax.crypto.spec SecretKeySpec]
          [java.security SecureRandom]
          [org.apache.commons.codec.binary Base64]
    )
)  
(defn bytes_ [s]
  (.getBytes s "UTF-8"))

(defn base64 [b]
  (Base64/encodeBase64String b))

(defn debase64 [s]
  ;(Base64/decodeBase64 (bytes_ s)))
  (Base64/decodeBase64 s))

(defn- get-raw-key [seed]
    (let [keygen (KeyGenerator/getInstance "AES")
                  sr (SecureRandom/getInstance "SHA1PRNG")]
          (.setSeed sr (utf8-bytes seed))
          (.init keygen 128 sr)
          (.. keygen generateKey getEncoded)))

(defn- get-cipher [mode seed]
    (let [key-spec (SecretKeySpec. (get-raw-key seed) "AES")
                  cipher (Cipher/getInstance "AES")]
          (.init cipher mode key-spec)
          cipher))
(defn encrypt 
    "value should be a byte[] and returns a byte[]"
    [value key]
    (let [ cipher (get-cipher Cipher/ENCRYPT_MODE key)]
          (.doFinal cipher value)))

(defn encrypt-base64 
    [value key]
    (base64 (encrypt (utf8-bytes value) key)))

(defn decrypt 
    "value should be a byte[] and returns a byte[]"
    [value key]
    (let [cipher (get-cipher Cipher/DECRYPT_MODE key)]
          (.doFinal cipher value)))

(defn decrypt-base64
    [value key]
    "value should be a byte[]"
    (String. (decrypt (debase64 value) key)))
