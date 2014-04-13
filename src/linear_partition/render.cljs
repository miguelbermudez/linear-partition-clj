(ns linear-partition.render)

(defn log [& args]
  (js/console.log (apply pr-str args)))

(def viewport-width (.-clientWidth (.-body js/document)))
(def ideal-height (/ (.-clientHeight (.-body js/document)) 2))


(defn preload-imgs [coll]
  (dorun (map #(.init %) (take 5 coll))))


;; This would also work to run through the lazyseq
;(defn preload-imgs [coll]
  ;(doseq [x (take 5 coll)]
    ;(.init x)
    ;(log (summed-width (take 5 coll)))
    ;)
  ;)


