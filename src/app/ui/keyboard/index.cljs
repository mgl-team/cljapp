(ns app.ui.keyboard.index
  (:require
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.common :as keycommon]
   [app.ui.keyboard.layout :as layout]
   [app.ui.keyboard.state :as state]))

(defn keyboard []
  (let [params {:alter state/alter
                :alter-num state/alter-num
                :shift state/shift
                :shift-num state/shift-num}]
    (fn []
      [nbase/box {:style {:flex-direction "column"
                          :flex 1
                          :height "100%"}}
       ;; keyboard
       (if (true? @state/alter)
         [layout/en-layout params]
         [layout/mn-layout params])])))
