(ns linear-partition.render)

(defn log [& args]
  (js/console.log (apply pr-str args)))

(def viewport-width (.-clientWidth (.-body js/document)))
(def ideal-height (/ (.-clientHeight (.-body js/document)) 2))
(def take-imgs 3)
(def photos (atom nil))

(defn set-photos [coll]
  (reset! photos coll))

(defn summed-width [coll]
   (reduce (fn [sum p]
            ;(log (str "[SUM] " (.-src (:img p)) ":  sum: " sum " p: " (.aspect p)))
            ;(log (str "   " (.aspect p) " " ideal-height " " (* (.aspect p) ideal-height)))
            (+ sum (* (.aspect p) ideal-height))) 
          0 
          (take take-imgs coll)))

;; This would also work to run through the lazyseq
(defn preload-imgs [atm]
  (doseq [x (take take-imgs @photos)]
    (.init x atm)))

;; Get num rows
(defn num-rows [sum]
  (log (str "[ROWS] sum: " sum " viewport: " viewport-width))
  (.round js/Math (/ sum viewport-width)))

;; Get Weights based on aspect ratio
(defn calc-weights [coll]
  (map #(js/parseInt (* (.aspect %) 100)) coll))

(defn resize-photos [coll]
  
  )

(defn render []
  (do
    (let [sum (summed-width @photos)
          rows (num-rows sum)
          ]
      (log "SUM result: " sum)
      (log "NUM rows: " rows)
      (cond
        (< 1 rows)  (resize-photos @photos)
        )
      )))

