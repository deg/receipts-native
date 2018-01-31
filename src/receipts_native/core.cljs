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


(def app-registry (.-AppRegistry react-native))
(def Alert (.-Alert react-native))

(def Body      (get-class native-base "Body"))
(def Button    (get-class native-base "Button"))
(def Container (get-class native-base "Container"))
(def Content   (get-class native-base "Content"))
(def Footer    (get-class native-base "Footer"))
(def FooterTab (get-class native-base "FooterTab"))
(def Form      (get-class native-base "Form"))
(def Header    (get-class native-base "Header"))
(def Icon      (get-class native-base "Icon"))
(def Input     (get-class native-base "Input"))
(def Item      (get-class native-base "Item"))
(def Label     (get-class native-base "Label"))
(def ListNB    (get-class native-base "List"))
(def ListItem  (get-class native-base "ListItem"))
(def Left      (get-class native-base "Left"))
(def Right     (get-class native-base "Right"))
(def Tab       (get-class native-base "Tab"))
(def Tabs      (get-class native-base "Tabs"))
(def Text      (get-class native-base "Text"))
(def Title     (get-class native-base "Title"))


(defn alert [content]
  (.alert Alert (str "Alert: ")
          content
          (clj->js [{:text "dismiss" :onPress identity}])))

(defn labelled-item [params label component]
  [Item (assoc params
               :inlineLabel true)
   [Label label]
   component])

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
         [Form {}
          [labelled-item {} "Source"
           [Input {}]]
          [labelled-item {} "Date"
           [Input {}]]
          [labelled-item {} "Price"
           [Input {:keyboardType "numeric" :maxLength 7}]]
          [labelled-item {}
           "Currency"
           [Input {}]]
          [labelled-item {}
           "Category"
           [Input {}]]
          [labelled-item {:last false}
           "Comment"
           [Input {:multiline true
                   :numberOfLines 2}]]]]
        [Tab {:heading "Edit"}
         [Text "Fake content 2"]]
        [Tab {:heading "History"}
         [Text "Fake content 3"]]
        [Tab {:heading "About"}
         [Text "Fake content 4"]]
        [Tab {:heading "Setup"}
         [Text "Fake content 5"]]]
       [Footer
        [FooterTab
         [Button
          [Icon {:name "paper"}]]
         [Button
          [Icon {:name "map"}]]
         [Button
          [Icon {:name "keypad"}]]]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
