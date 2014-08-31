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
  (Base64/decodeBase64 (bytes_ s)))

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
    "Symmetrically encrypts value with key, such that it can be
       decrypted later with (decrypt). The value and key parameters are
       expected to be a String. Returns byte[]"
    [value key]
    (let [b (utf8-bytes value)
                  cipher (get-cipher Cipher/ENCRYPT_MODE key)]
          (base64 (.doFinal cipher b))))

(defn decrypt 
    "Decrypts a value which has been encrypted via a call to
       (encrypt) using key. The value parameter is expected to be a 
       byte[] and the key parameter is expected to be a String. 
       Returns byte[]"
    [value key]
    (let [cipher (get-cipher Cipher/DECRYPT_MODE key)]
          (String. (.doFinal cipher (debase64 value)))))
