(ns receipts-native.core
    (:require [reagent.core :as r :refer [atom]]
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

#_(def flat-list-class (get-class "FlatList"))
#_(def section-list-class (get-class "SectionList"))


(defn alert [title]
  (.alert Alert title))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [image {:source (js/require "./assets/images/cljs.png")
               :style {:width 50
                       :height 50}}]
       [picker {:style {:width 350 :height 50 :margin 10}
                :selected-value :b
                :on-value-change #(js/alert (str "Got: " %1 ", pos=" %2))}
        [picker-item {:value :a :label "Alabama"}]
        [picker-item {:value :b :label "Baltimore"}]
        [picker-item {:value :c :label "Canada"}]]
       #_[flat-list-class {:data [{:key :a}
                                {:key :b}
                                {:key :c}]
                         :render-item (fn [x] (:key x))}]
       [text-input-class]
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting "yyy"]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
