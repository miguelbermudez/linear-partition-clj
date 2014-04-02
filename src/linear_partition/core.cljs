(ns linear-partition.core
  (:require [clojure.browser.net :as gnet]
            [clojure.browser.event :as gevent]))

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
(def s3-obj-xpath "//*[name() = 'Contents']")

(defn print-key-name [node]
  (-> node
      (.getElementsByTagName "Key")
      nodelist-to-seq
      first
      .-textContent))

(defn handle-s3-result [evt]
  (let [response (.getResponseXml (.-target evt))
        results (evaluateXPath response s3-obj-xpath)]
    (set! (.-s3r js/window) response)
    (set! (.-resp js/window)  (println (map #(print-key-name %) results)))

    (.log js/console response results (count results))

    ))

(defn get-images []
  (let [xhr (gnet/xhr-connection.)]
    (gevent/listen xhr :error #(.error js/console Error))
    (gevent/listen xhr :success handle-s3-result)

    ;; make request
    (gnet/transmit xhr s3-bucket-url "GET")))

(get-images)
