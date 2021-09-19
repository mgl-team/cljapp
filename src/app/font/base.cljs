(ns app.font.base
  (:require
   ["@pdf-lib/fontkit" :as fontkit]
   ["react-native-fs" :as fs]
   [goog.object :as gobj]
   [goog.crypt.base64 :as b64]
   [applied-science.js-interop :as j]
   [promesa.core :as p]
   [promesa.async-cljs :refer-macros [async]]
   [reagent.core :as reagent]))

;; (defonce ^:private
(def
  fonts
  (reagent/atom {}))

(defn init []
  (p/alet [result (p/await (.readFileAssets fs "monbaiti.ttf" "base64"))]
          (swap! fonts assoc :white (fontkit/create (b64/decodeStringToUint8Array result))))
  ;; (async (p/await (p/delay 100)))
  (js/console.log "init function")
  )

(defn load [name url]
  (-> (fs/readFileAssets url)
      (.then (fn [res] (swap! fonts assoc name (fontkit/create res))))
      ;; (.then (fn [res] (swap! fonts assoc name res)))
      (.catch (fn [err] (js/console.log err)))))

;; (.then (.readFileAssets fs "monbaiti.ttf") (fn [res] (swap! fonts assoc :white (fontkit/create res))))
;; (.then (.readFileAssets fs "monbaiti.ttf" "base64") (fn [res] (swap! fonts assoc :white (fontkit/create (b64/decodeStringToUint8Array res)))))

(defn get-font [name]
  (get @fonts name))

(defn character-set [name]
  (gobj/get (get-font name) "characterSet"))

(defn units-per-em [name]
  (.-unitsPerEm (get-font name)))

(defn font-scale [name]
  (/ 1000 (units-per-em name)))

(defn layout [font-name value]
  (let [font (get-font font-name)]
    (if font
      (j/call (get-font font-name) :layout value)
      nil)))

(defn render [ctx glyph font-size]
  (j/call glyph :render ctx font-size))

(defn get-glyphs [font-name value]
  (if value
    (gobj/get (layout font-name value) "glyphs")
    nil))

(defn glyph-width [glyph]
  (if glyph
    (gobj/get glyph "advanceWidth")
    0))

(defn width [font-name font-size glyph]
  (* (/ font-size 1000)
     (glyph-width glyph)
     (font-scale font-name)))

(defn glyph [font id]
  (.getGlyph (get-font font) id))

(defn glyph-ids [font value]
  (map #(gobj/get % "id") (get-glyphs font value)))

(defn glyph-scale [name size]
  (->> (units-per-em name)
       (/ 1)
       (* size)))

(defn svg [glyph]
  (-> glyph
      (j/call :scale -1 1)
      (j/call :rotate (str (* 1 (.-PI js/Math))))
      (j/call :scale (* 0.1 (font-scale :white)))
      (j/call :toSVG)))

(def mstr "ᠡᠷᠬᠡ")

(defn msvg []
  ;; (if (empty? @fonts) (js/console.log "11xxxxx") (js/console.log "11aaaaa"))
  ;; "M-33.63 53.26L-33.63 42.6L-22.38 42.6Q-22.21 38.2 -21.59 33.63Q-20.98 29.06 -20.21 24.43L-18.11 23.85Q-16.93 26.37 -16.11 29.41Q-15.29 32.46 -14.71 35.39Q-14.12 38.32 -13.86 40.75Q-13.59 43.18 -13.59 44.47L-13.59 53.26Q-13.59 55.61 -13.04 57.04Q-12.48 58.48 -11.13 58.48Q-10.2 58.48 -9.46 58.33Q-8.73 58.18 -8.06 57.89Q-7.38 57.6 -6.68 57.19Q-5.98 56.78 -5.1 56.19Q-3.63 55.2 -2.84 54.46Q-2.05 53.73 -1.64 53.73Q-1 54.32 -0.5 55.05Q0 55.78 0 56.31Q-0.64 56.95 -1.46 57.95Q-2.29 58.95 -3.11 59.88Q-7.03 64.39 -9.79 66.39Q-12.54 68.38 -14.65 68.38Q-17.29 68.38 -18.75 65.77Q-20.21 63.16 -20.21 58.95L-20.21 53.26Z"
  ;; (js/console.log @fonts)
  ;; (js/console.log (.getTime (js/Date.)))
  (if (empty? @fonts)
    ;; "M-33.63 53.26L-33.63 42.6L-22.38 42.6Q-22.21 38.2 -21.59 33.63Q-20.98 29.06 -20.21 24.43L-18.11 23.85Q-16.93 26.37 -16.11 29.41Q-15.29 32.46 -14.71 35.39Q-14.12 38.32 -13.86 40.75Q-13.59 43.18 -13.59 44.47L-13.59 53.26Q-13.59 55.61 -13.04 57.04Q-12.48 58.48 -11.13 58.48Q-10.2 58.48 -9.46 58.33Q-8.73 58.18 -8.06 57.89Q-7.38 57.6 -6.68 57.19Q-5.98 56.78 -5.1 56.19Q-3.63 55.2 -2.84 54.46Q-2.05 53.73 -1.64 53.73Q-1 54.32 -0.5 55.05Q0 55.78 0 56.31Q-0.64 56.95 -1.46 57.95Q-2.29 58.95 -3.11 59.88Q-7.03 64.39 -9.79 66.39Q-12.54 68.38 -14.65 68.38Q-17.29 68.38 -18.75 65.77Q-20.21 63.16 -20.21 58.95L-20.21 53.26Z"
    ""
    (-> (get-glyphs :white mstr)
        last
        (.-path)
        (j/call :scale -1 1)
        ;; (j/call :transfrom )
        ;; (j/call :scale -1 1)
        (j/call :rotate (str (* 1 (.-PI js/Math))))
        (j/call :scale (* 0.1 (font-scale :white)))
        (j/call :toSVG))))
      

(comment
  (init)
  fonts
  (time (get-font :whtie))
  (time (get-glyphs :white mstr))
  (time (* 100 12 13))
  (time 
  (-> (get-glyphs :white mstr)
      first
      (j/get :path)
      (j/call :scale 1 -1)
      (j/call :rotate (str (* 1 (.-PI js/Math))))
      (j/call :scale (* 0.12 (font-scale :white)))
      (j/call :toSVG))
)

  (def mstr "ᠡᠷᠬᠡ")

  (msvg)
  (load :white "./assets/fonts/monbaiti.ttf")
  load

  fs/readFile
  (get-font :white)
  (:white @fonts)

  (units-per-em :white)
  (font-scale :white)
  ;; (js/console.log (layout :white "ᠡᠷᠬᠡ"))
  ;; (def glyphs (.-glyphs (layout :white "ᠡᠷᠬᠡ ")))
  ;; (def glyphs (.-glyphs (layout :white "aa")))
  glyphs

  (width :white 48 (first glyphs))

  (.-head (get-font :white))




  (require '[clj-bean.core :refer [bean ->clj ->js]])
  (def glyph-run (layout :white mstr))
  (-> (j/get glyph-run :glyphs)
      first)
  (def (-> (get-glyphs :white mstr)
           first)
    aa)
  aa
  (def ab (.getScaledPath aa -2400))
  (def ab (j/get aa :path))
  ab
  (.toSVG (.scale (.scale ab -1 1) (glyph-scale :white 12)))
  (.toSVG ab)
  (.toSVG (.rotate (.scale (.scale ab -1 1) (glyph-scale :white 12)) 90))
  (.toSVG (.scale (.scale ab -1 1) (glyph-scale :white 120)))

  (.toSVG (.scale ab (- (glyph-scale :white 24)) (glyph-scale :white 24)))
  ;; (.toSVG)    )
  (.getScaledPath ab 12)
  (glyph-scale :white 24)
  )


