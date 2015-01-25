(ns algonum-uno.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:text "Hell world!"}))

(def canvas-size {:width  300 :height 300 })

(defn circle [x y r fill]
  (dom/circle
   #js{:cx (str x)
       :cy (str y)
       :r (str r)
       :fill fill}))

(defn bar [x height]
  (dom/line
   #js{:x1 x
       :x2 x
       :y1 (+ 1 (- (:height canvas-size) height )) ;(canvas-size :height)
       :y2  (- (:height canvas-size) height )
       :stroke "red"
       :strokeWidth "1"}))
(defn line [points]
  (dom/polyline
   #js{ :stroke "red" :strokeWidth "1" :fill "none" :points (apply str(map #(let [[x y] %] (str x ","y " " ))  points))}))

(defn plot [x1 x2 y1 y2 fx steps]
  (let [x-scale (/(:width canvas-size) (- x2 x1))
        y-scale (/ (:height canvas-size) (- y2 y1) )]
    (map #(bar (* x-scale (- % x1)) (* y-scale (+ 10 (fx %)))) (range x1
                               x2
                               steps))))

(defn punch[x] (cond  (< x 2) 3
                      (> x 5) (* (- .05) x x)
                      :else 2))

(om/root
 (fn [app owner]
   (apply dom/svg #js{:width (canvas-size :width) :height (canvas-size :height)}
          
          (into [(line (mapv #(vector % (+ 80 (* 50 (Math/sin %)))  ) (range 1 200 10)))] 
                (map #(circle (* % 10) (+ 200 (* 30 (Math/sin %))) .5 "blue") (range 1 200 .005))
                  )
          ))
 app-state
 {:target (. js/document (getElementById "app"))})




