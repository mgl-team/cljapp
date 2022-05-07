(ns app.ui.home.dragable
  (:require
    ["react-native-gesture-handler" :as gesture]
    ["react-native-reanimated" :as re-animated]
    ["react-native" :as rn]
    [reagent.core :as r]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]))


(defn draggable-view-gesture []
  (let [x (new rn/Animated.Value 0)
        y (new rn/Animated.Value 0)
        last-offset (new rn/Animated.ValueXY #js {:x 0
                                                  :y 0})
        gesture-handler (rn/Animated.event (bean/->js [{:nativeEvent {:translationX x
                                                                      :translationY y}}])
                                           (bean/->js {:useNativeDriver true}))
        state-handler (fn [event]
                        (if (= (j/get-in event [:nativeEvent :oldState])
                               gesture/State.ACTIVE)
                          (do
                            (.setValue ^js last-offset #js {:x (+ (j/get-in last-offset [:x :_value]) (j/get-in event [:nativeEvent :translationX]))
                                                            :y (+ (j/get-in last-offset [:y :_value]) (j/get-in event [:nativeEvent :translationY]))})

                            (.setOffset ^js x (j/get-in last-offset [:x :_value]))
                            (.setOffset ^js y (j/get-in last-offset [:y :_value]))

                            (.setValue ^js x 0)
                            (.setValue ^js y 0))))]
    (fn []
      [:> gesture/PanGestureHandler {:maxPointers 1
                                     :onGestureEvent gesture-handler
                                     :onHandlerStateChange state-handler}
       [:> rn/Animated.View {:style {:background-color "blue"
                                     :height 100
                                     :width 100
                                     :transform [{:translateX x}
                                                 {:translateY y}]}}]])))
