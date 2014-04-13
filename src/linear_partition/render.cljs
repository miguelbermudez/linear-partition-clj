(ns linear-partition.render)

(defn log [& args]
  (js/console.log (apply pr-str args)))

(def viewport-width (.-clientWidth (.-body js/document)))
(def ideal-height (/ (.-clientHeight (.-body js/document)) 2))


(defn preload-imgs [coll]
  (dorun (map #(.init %) (take 5 coll))))


