(ns receipts-native.core
  (:require [oops.core :as oops]
            [reagent.core :as r]
            [reagent.impl.component :as ru]  ;; [TODO] ??
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [receipts-native.handlers]
            [receipts-native.subs]))

(defonce react-native (js/require "react-native"))
(defonce native-base (js/require "native-base"))
(defonce native-calendar (js/require "react-native-calendars"))


(defn adapt-class [class]
  (when class
    (r/adapt-react-class class)))

(defn get-class [module name]
  (adapt-class (oops/oget+ module name)))


(def app-registry (.-AppRegistry react-native))
(def Alert (.-Alert react-native))

(def ScrollView (get-class react-native "ScrollView"))
(def View (get-class react-native "View"))

(def Body       (get-class native-base "Body"))
(def Button     (get-class native-base "Button"))
(def Container  (get-class native-base "Container"))
(def Content    (get-class native-base "Content"))
(def Footer     (get-class native-base "Footer"))
(def FooterTab  (get-class native-base "FooterTab"))
(def Form       (get-class native-base "Form"))
(def Header     (get-class native-base "Header"))
(def Icon       (get-class native-base "Icon"))
(def Input      (get-class native-base "Input"))
(def InputGroup (get-class native-base "InputGroup"))
(def Item       (get-class native-base "Item"))
(def Label      (get-class native-base "Label"))
(def ListNB     (get-class native-base "List"))
(def ListItem   (get-class native-base "ListItem"))
(def Left       (get-class native-base "Left"))
(def Picker     (get-class native-base "Picker"))
(def Right      (get-class native-base "Right"))
(def Tab        (get-class native-base "Tab"))
(def Tabs       (get-class native-base "Tabs"))
(def Text       (get-class native-base "Text"))
(def Title      (get-class native-base "Title"))

(def Calendar   (get-class native-calendar "Calendar"))


(defn alert [content]
  (.alert Alert (str "Alert: ")
          content
          (clj->js [{:text "dismiss" :onPress identity}])))

(defn labelled-item [params label component]
  [Item (assoc params
               :inlineLabel true)
   [Label label]
   component])


(def sources ["v1234" "mc5678" "cash"])

(def currencies ["EUR" "GBP" "NIS" "USD"])

(def categories ["Car" "Entertainment" "Food" "Home"])

(def vendors ["Ace" "Bally" "Crazy Eddie" "Deals-r-us"])

(defn dropdown-input [{:keys [items prompt] :as params}]
  [View   ;; [TODO] This doesn't work, use View for now.    Item {:inlineLabel true}
   [Label prompt]
   (into [Picker params]
         (map (fn [li]
                [Item {:label li :value li}])
              items))])


(defonce receipt (r/atom {:date "2018-02-07";(js/Date.)
                          :source "mc5678"
                          :price "42"
                          :currency "NIS"}))

(defn receipt-dropdown [prompt key choices]
  (dropdown-input {:prompt prompt
                   :items choices
                   :mode "dialog"
                   :selected-value (key @receipt)
                   :onValueChange #(swap! receipt assoc key %1)}))

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
         [ScrollView {}
          [Calendar {:monthFormat "MMMM yyyy"
                     :minDate "2018-01-01"
                     :maxDate (js/Date.)
                     :onDayPress #(swap! receipt assoc :date (js->clj % :keywordize-keys true))
                     :markedDates {(get-in @receipt [:date :dateString]) {:selected true}}}]
          (receipt-dropdown "Source" :source sources)
          [labelled-item {} "Price"
           [Input {:keyboardType "numeric"
                   :maxLength 7
                   :onChangeText #(swap! receipt assoc :price %1)
                   :value (:price @receipt)}]]
          (receipt-dropdown "Currency" :currency currencies)
          (receipt-dropdown "Category" :category categories)
          (receipt-dropdown "Vendor" :vendor vendors)
          [labelled-item {:last false} "Comment"
           [Input {:multiline true
                   :numberOfLines 2}]]]]
        [Tab {:heading "Edit"}
         [Text "Fake content 2"]
         [ListNB
          [ListItem [Text "a"]]
          [ListItem [Text "b"]]]
         [Text "And more"]]
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
