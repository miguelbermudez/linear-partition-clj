(ns linear-partition.photos
  (:require [goog.events :as events]))

(defn log [& args]
  (js/console.log (apply pr-str args)))

(defrecord Photo [^:mutable img url]
  Object
  (init [this atm] 
    "Preload Photo with url"
    ;; set img src
    (set! (.-src img) url)
    ;; attach onload event to img
    (events/listen
      img events/EventType.LOAD
      (fn [evt]
        ;(.dir js/console evt)
        ;(log (str "[LOAD] " (.-src (.-target evt))))
        ;(log (.aspect this))
        (swap! atm inc)
        ))
    ;; return img
    img)

  (aspect [this]
    "Get the apect ratio of the photo"
    (/ (.-width img)(.-height img)))
  
  )

(defn make-photo [url]
  "Factory for creating Photos.
   Returns false if the url is invalid"
  (let [img (js/Image.)]
    (if-not (= url nil)
      (Photo. img url) 
      false)))

