(ns linear-partition.render)

(def viewport-width (.-clientWidth (.-body js/document)))
(def ideal-height (/ (.-clientHeight (.-body js/document)) 2))


(defn preload-imgs [coll]
  (dorun (map #(.init %) (take 5 coll))))


