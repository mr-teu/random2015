(ns algonum-uno.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:text "Hell world!"}))

(def canvas-size {:width  900 :height 300 })

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

(defn pol-line [{:keys [x0 y0 width height xs ys]}]
  (let [
        x-scale (/ width (- (apply max xs) (apply min xs)))
        y-scale (/ height (- (apply max ys) (apply min ys)))
        x1 (+ 0 (* x-scale (first xs)))
        xi (+ x0 (* x-scale (first xs)))
        y1 (+ 0 (* y-scale (first ys)))
        yi  (+ y0 (- height (* y-scale (first ys))))
       inicio (str "M" xi " " yi " ")
        x-pxfn (fn [x] (+ (* x x-scale)  x0 ))
        y-pxfn (fn [y] (+ (- height (* y y-scale)) y0 ))
        linea (reduce #(str %1  %2) 
                      "L" (map #(str (x-pxfn %1) " " (y-pxfn %2) " ") xs ys)) ]
   
    (dom/path #js{:d (str inicio linea)
                  :fill "none"
                  :style #js{:stroke "#BBCCCC" :stroke-width "2pt" :stroke-opacity .9}
                  :stroke "red"})))

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
           
          (into [(pol-line {:x0 1 :y0 100 
                            :width 800 :height 90 
                            :xs [0 1 2 3 4]
                            :ys [(rand-int 50)(rand-int 5)(rand-int 50)(rand-int 5)(rand-int 50)]})] 
                [])))
 app-state
 {:target (. js/document (getElementById "app"))})




