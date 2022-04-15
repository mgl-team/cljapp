(ns app.ui.keyboard.candidates
  (:require
   [reagent.core :as reagent]
   [re-frame.core :refer [dispatch subscribe]]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [app.ui.text :as text]
   [app.ui.keyboard.bridge :as bridge]
   [app.ui.keyboard.state :as state]
   [app.ui.editor :refer [cursor]]
   [app.persist.sqlite :as sqlite]
   [clojure.string :as str]

   [steroid.rn.core :as rn]
   [steroid.rn.components.list :as rn-list]))


(def candidates-list (reagent/atom []))
(def candidates-index (reagent/atom ""))


(defn candidate-select [m]
  (reset! candidates-index "")
  (sqlite/next-words m
    #(reset! candidates-list %))
  (js/console.log "xxxx" (bean/->js @cursor))
  (let [text (:char_word m)
        new-text (str (cond
                        (empty? (:char @cursor))
                        ""

                        (= 8239 (int (.codePointAt text 0)))
                        ""

                        (= " " (:char @cursor))
                        ""

                        (= "\n" (:char @cursor))
                        ""

                        :else
                        " ")
                      text)]
    (bridge/editor-insert new-text)))


(defn candidates-query [i]
  (let [ii (str @candidates-index i)]
    (reset! candidates-index ii)
    (sqlite/candidates
      ii
      #(reset! candidates-list %))))

(defn candidates-delete []
  (let [old-index @candidates-index
        new-index (str/join "" (drop-last old-index))]
    (cond
      (or (empty? old-index) (= 1 (count old-index)))
      (if (empty? @candidates-list)
        (do
          (reset! candidates-index "")
          (reset! candidates-list [])
          (bridge/editor-delete))

        (do
          (reset! candidates-index "")
          (reset! candidates-list [])))

      :else
      (do
        (reset! candidates-index new-index)
        (sqlite/candidates
          new-index
          #(reset! candidates-list %))))))

(defn views []
  (fn []
    (let [candidates @candidates-list]
      (cond
        ; (empty? candidates)
        (empty? @candidates-index)
        nil

        :else
        [rn/view {:style {:position "absolute"
                          :left 0
                          :right 0
                          ;:top 0
                          :bottom 300
                          :elevation 1998
                          :alignItems "center"
                          :justifyContent "center"
                          :z-index 999}}
         [rn/text @candidates-index]
         [rn/view
          {:style {;:opacity 0.6
                   :backgroundColor "ghostwhite"
                   :borderRadius 5
                   ; :padding 10
                   ; :height "auto"
                   ; :maxheight 100
                   :min-height 60
                   :alignItems "flex-start"
                   :justifyContent "center"
                   :maxWidth "50%"
                   :minWidth 10
                   :borderWidth 1
                   :borderColor "lightgray"
                   :flex-direction "row"}}
          [rn-list/flat-list
           {:keyExtractor    (fn [_ index] (str "text-" index))
            :data      (cond
                         (not-empty candidates)
                         candidates

                         :else
                         [])
            :renderItem (fn [x]
                          (let [{:keys [item index separators]} (j/lookup x)]
                            (reagent/as-element
                              [rn/touchable-opacity {:on-press #(candidate-select item)
                                                     :px 1}
                               [rn/view {:style {:height "100%"}}; :width 28}}
                                [text/measured-text {:fontFamily "MongolianBaiZheng" :fontSize 18}
                                  (:char_word item)]]])))
            :p 3
            :initialNumToRender 7
            :showsHorizontalScrollIndicator false
            :horizontal true}]]]))))
