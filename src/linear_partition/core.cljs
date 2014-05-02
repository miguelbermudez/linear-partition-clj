(ns linear-partition.core
  (:require [clojure.browser.net :as gnet]
            [clojure.browser.event :as gevent]
            [linear-partition.photos :as photo]
            [linear-partition.render :as render]
            ))

(enable-console-print!)

;; https://developer.mozilla.org/en-US/docs/Using_XPath#Node-specific_evaluator_function
;; Evaluate an XPath expression aExpression against a given DOM node
;; or Document object (aNode), returning the results as an array
;; thanks wanderingstan at morethanwarm dot mail dot com for the
;; initial work.

(defn evaluateXPath [aNode aExpr]
  (let [xpe (or (.-ownerDocument aNode)
                aNode)
        nsResolver (.createNSResolver xpe (.-documentElement xpe))
        result (.evaluate xpe aExpr aNode nsResolver 0 nil)]
    (loop [res (.iterateNext result) found []]
      (if res
        (recur (.iterateNext result) (conj found res))
        found))))


(defn nodelist-to-seq
  "Converts nodelist to (not lazy) seq."
  [nl]
  (let [result-seq (map #(.item nl %) (range (.-length nl)))]
    (doall result-seq)))


(def s3-bucket-url "http://caravaggio-src.s3.amazonaws.com")
(def s3-obj-uri-prefix "https://s3.amazonaws.com/caravaggio-src")
(def s3-obj-xpath "//*[name() = 'Contents']")

(def images-loaded (atom 0))


(defn get-seq-from-node [node tag]
  (-> node
      (.getElementsByTagName tag)
      nodelist-to-seq
      first
      .-textContent))


(defn url-for-s3obj
  "Create a url for the s3 object.
   Url needs to look like this:
   https://s3.amazonaws.com/caravaggio-src/images/Amor_Victorious_WGA.jpg"
  [node]
  (let [size (js/parseInt (get-seq-from-node node "Size"))
        name (get-seq-from-node node "Key")]
    (if-not (zero? size)
      (do
        ;(.log js/console (str s3-obj-uri-prefix "/" name))
        (str s3-obj-uri-prefix "/" name)))))


(defn loadAllImages [coll cb]
  (add-watch images-loaded :preload
             (fn [_key _ref ov nv]
              (if (= 3 nv)
                (cb))))
  (render/preload-imgs images-loaded))

(defn handle-s3-result [evt]
  (let [response (.getResponseXml (.-target evt))
        results (evaluateXPath response s3-obj-xpath)
        img-urls (map #(url-for-s3obj %) results)
        photo-coll (filter identity (map #(photo/make-photo %) img-urls))]
    (render/set-photos photo-coll)
    (loadAllImages photo-coll render/render)))

(defn get-images []
  (let [xhr (gnet/xhr-connection.)]
    (gevent/listen xhr :error #(.error js/console Error))
    (gevent/listen xhr :success handle-s3-result)

    ;; make request
    (gnet/transmit xhr s3-bucket-url "GET")))

(get-images)
