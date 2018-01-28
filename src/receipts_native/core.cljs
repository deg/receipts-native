(ns receipts-native.core
  (:require [oops.core :as oops]
            [reagent.core :as r]
            [reagent.impl.component :as ru]  ;; [TODO] ??
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [receipts-native.handlers]
            [receipts-native.subs]))

(def react-native (js/require "react-native"))
(def native-base (js/require "native-base"))


(defn adapt-class [class]
  (when class
    (r/adapt-react-class class)))

(defn get-class [module name]
  (adapt-class (oops/oget+ module name)))


(def Button (get-class native-base "Button"))
(def Body (get-class native-base "Body"))
(def Title (get-class native-base "Title"))
(def Container (get-class native-base "Container"))
(def Content (get-class native-base "Content"))
(def Header (get-class native-base "Header"))
(def Tabs (get-class native-base "Tabs"))
(def Tab (get-class native-base "Tab"))
(def Text (get-class native-base "Text"))
(def Left (get-class native-base "Left"))
(def Right (get-class native-base "Right"))
(def Icon (get-class native-base "Icon"))
(def Footer (get-class native-base "Footer"))
(def FooterTab (get-class native-base "FooterTab"))


(def app-registry (.-AppRegistry react-native))
(def text (get-class react-native "Text"))
(def view (get-class react-native "View"))
(def image (get-class react-native "Image"))
(def touchable-highlight (get-class react-native "TouchableHighlight"))
(def Alert (.-Alert react-native))

(def picker (get-class react-native "Picker"))
(def picker-item (adapt-class (.-Item (oops/oget+ react-native "Picker"))))

(def navigator (get-class react-native "NavigatorIOS"))
(def scroll (get-class react-native "ScrollView"))
(def input (get-class react-native "TextInput"))
(def list-view (get-class react-native "ListView"))


(def text-input-class (get-class react-native "TextInput"))

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
    (js->clj (.create (.-StyleSheet react-native) (clj->js s1)))))

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

(defn alert [content]
  (.alert Alert (str "Alert: ")
          content
          (clj->js [{:text "dismiss" :onPress identity}])))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [Container
       [Header {:hasTabs true}
        [Left
         [Button {:transparent true :onPress #(alert "menu")}
          [Icon {:name "menu"}]]]
        [Body
         [Title "Receipts v4.0"]]
        [Right]]
       [Tabs
        [Tab {:heading "Receipts"}
         [text "Fake content 1"]]
        [Tab {:heading "Edit"}
         [text "Fake content 2"]]
        [Tab {:heading "History"}
         [text "Fake content 3"]]
        [Tab {:heading "About"}
         [text "Fake content 4"]]
        [Tab {:heading "Setup"}
         [text "Fake content 5"]]]
       [Footer
        [FooterTab
         [Button
          [Icon {:name "paper"}]]
         [Button
          [Icon {:name "map"}]]
         [Button
          [Icon {:name "keypad"}]]]]]
      #_
      [scroll {:always-bounce-vertical true
               :bounces true
               :style (styles "fullscreen")}
       [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
        [image {:source (js/require "./assets/images/cljs.png")
                :style {:width 350
                        :height 350}}]
        [picker {:style {:width 350 :height 50 :margin 10}
                 :selected-value :b
                 :on-value-change #(alert (str "Picked: " %1 ", pos=" %2))}
         [picker-item {:value :a :label "Alabama"}]
         [picker-item {:value :b :label "Baltimore"}]
         [picker-item {:value :c :label "Canada"}]]
        [text-input-class]
        [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
        [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                              :on-press #(alert "Hello world")}
         [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
