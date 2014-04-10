(ns linear-partition.photos)

(defrecord Photo [^:mutable img url]
  Object
  (init [this] 
    (set! (.-src img) url)
    img)
  (aspect [this] (/ (.-width img)(.-height img))))

(defn make-photo [url]
  (let [img (js/Image.)]
    (if-not (= url nil)
      (Photo. img url) 
      false)))

