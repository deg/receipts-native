(ns receipts-native.core
  (:require [reagent.core :as r]
            [reagent.impl.component :as ru]  ;; [TODO] ??
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [receipts-native.handlers]
            [receipts-native.subs]))

(def ReactNative (js/require "react-native"))

(defn get-react-property [name]
  (aget ReactNative name))

(defn adapt-class [class]
  (when class
    (r/adapt-react-class class)))

(defn get-class [name]
  (adapt-class (get-react-property name)))

(def app-registry (.-AppRegistry ReactNative))
(def text (get-class "Text"))
(def view (get-class "View"))
(def image (get-class "Image"))
(def touchable-highlight (get-class "TouchableHighlight"))
(def Alert (.-Alert ReactNative))

(def picker (get-class "Picker"))
(def picker-item (adapt-class (.-Item (get-react-property "Picker"))))

(def navigator (get-class "NavigatorIOS"))
(def scroll (get-class "ScrollView"))
(def input (get-class "TextInput"))
(def switch (get-class "SwitchIOS"))
(def list-view (get-class "ListView"))
(def slider (get-class "SliderIOS"))


(def text-input-class (get-class "TextInput"))

(defn text-input [{:keys [font style] :as opts
                   :or   {font :default}} text]
  [text-input-class (merge
                     {:underline-color-android :transparent
                      :placeholder-text-color  "red"
                      :placeholder             "type a message"
                      :value                   text}
                     (-> opts
                         (dissoc :font)
                         (assoc :style (merge style font))))])


(defn create-style[s]
  (let [s1 (reduce #(assoc %1 (%2 0) (ru/camelify-map-keys (%2 1))) {} s)]
    (js->clj (.create (.-StyleSheet ReactNative) (clj->js s1)))))

(enable-console-print!)

(def styles (create-style
             {:fullscreen {:position "absolute"
                           :top 0
                           :left 0
                           :bottom 0
                           :right 0}
              :partial {:position "absolute"
                        :top 0
                        :left 20
                        :bottom 300
                        :right 20}
              :green {:color "#00ff00"}
              :viewbg {:padding 10
                       :background-color "#ffffff"}
              :input {:height 35
                      :border-color "gray"
                      :border-width 1
                      :padding-left 10
                      :border-radius 5
                      :margin 10}}))

(defn alert [title content]
  (.alert Alert title content))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [scroll {:always-bounce-vertical true
               :bounces true
               :style (styles "fullscreen")}
       [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
        [image {:source (js/require "./assets/images/cljs.png")
                :style {:width 350
                        :height 350}}]
        [picker {:style {:width 350 :height 50 :margin 10}
                 :selected-value :b
                 :on-value-change #(alert "Picked" (str "Got: " %1 ", pos=" %2))}
         [picker-item {:value :a :label "Alabama"}]
         [picker-item {:value :b :label "Baltimore"}]
         [picker-item {:value :c :label "Canada"}]]
        [text-input-class]
        [image {:source (js/require "./assets/images/cljs.png")
                :style {:width 350
                        :height 350}}]
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                              :on-press #(alert "HELLO!" "Hello world")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
