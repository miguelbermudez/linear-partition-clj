(ns linear-partition.render)

(def viewport-width (.-clientWidth (.-body js/document)))
(def ideal-height (/ (.-clientHeight (.-body js/document)) 2))

(defn preload-imgs [coll]
  (doseq [x (take 5 coll)]
    (.log js/console "preload: " x)
    (.init x)))

