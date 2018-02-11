(ns receipts-native.core
  (:require [cljs-time.core :as time]
            [cljs-time.coerce :refer [from-date to-date]]
            [oops.core :as oops]
            [reagent.core :as r]
            [reagent.impl.component :as ru]  ;; [TODO] ??
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [receipts-native.handlers]
            [receipts-native.subs]))

(defonce react-native (js/require "react-native"))
(defonce native-base (js/require "native-base"))
(defonce native-calendar-strip (js/require "react-native-calendar-strip"))


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

(def CalendarStrip (get-class native-calendar-strip "default"))

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

(def for-whom ["David" "Agatha" "Betsy" "Carol"])

(defn dropdown-input [{:keys [items prompt] :as params}]
  (let [picker (into [Picker (assoc params :style {:width "30%"
                                                   :padding 0})]
                     (map (fn [li]
                            [Item {:label li :value li}])
                          items))]
    (if prompt
      [View   ;; [TODO] Item doesn't work, use View with styling for now.    Item {:inlineLabel true}
       {:style {:flexDirection "row"
                :borderWidth 0
                :borderBottomWidth 0.666
                :padding 0
                :borderColor "#d9d5dc"
                :marginLeft 2}}
       [Label {:style {:width "20%" :paddingTop 12 :color "#575757"}} prompt]
       picker]
      picker)))


(defonce receipt (r/atom {:date (time/today)
                          :source "mc5678"
                          :price "42"
                          :currency "NIS"}))

(defn next-day [cljs-date]
  (time/plus cljs-date (time/days 1)))

(defn week-start [cljs-date]
  (time/minus cljs-date
              (time/days (-> cljs-date time/day-of-week (mod 7)))))

(defn next-week
  ([cljs-date]
   (next-week cljs-date 1))
  ([cljs-date weeks]
   (time/plus (week-start cljs-date) (time/days (* weeks 7)))))

(defn prev-week
  ([cljs-date]
   (prev-week cljs-date 1))
  ([cljs-date weeks]
   (time/minus (week-start cljs-date) (time/days (* weeks 7)))))

(defn receipt-dropdown [prompt key choices]
  (dropdown-input {:prompt prompt
                   :items choices
                   :mode "dialog"
                   :selected-value (key @receipt)
                   :onValueChange #(swap! receipt assoc key %1)}))

(defn log1 [prompt x]
  (js/console.log prompt x)
  x)

(defn small-calendar [receipt]
  (let [date (:date @receipt)
        today (time/today)]
    [CalendarStrip {:style {:borderWidth 1
                            :borderBottomWidth 0.666
                            :padding 0
                            :borderColor "#d9d5dc"}
                    :startingDate (-> today week-start to-date)
                    :selectedDate (to-date date)
                    :useIsoWeekday false
                    :minDate (-> today prev-week (time/earliest date) to-date)
                    :maxDate (to-date today)
                    :datesBlacklist [{:start (-> today next-day to-date)
                                      :end   (-> today next-week to-date)}]
                    :dateNumberStyle {:color "black"}
                    :highlightDateNumberStyle {:color "red"}
                    :disabledDateNumberStyle {:color "grey"}
                    :styleWeekend false
                    :onDateSelected #(swap! receipt assoc :date (from-date (oops/ocall % "toDate")))}]))

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
          [small-calendar receipt]
          (receipt-dropdown "Source" :source sources)
          [labelled-item {} "Price"
           [View {:style {:flexDirection "row"}}
            [Input {:keyboardType "numeric"
                    :maxLength 7
                    :onChangeText #(swap! receipt assoc :price %1)
                    :value (:price @receipt)}]
            [receipt-dropdown nil :currency currencies]]]
          [receipt-dropdown "Category" :category categories]
          [receipt-dropdown "Vendor" :vendor vendors]
          [receipt-dropdown "For Whom" :for-whom for-whom]
          [labelled-item {} "Comment"
           [Input {:multiline true
                   :numberOfLines 2}]]
          [Button {:transparent true
                   :primary true
                   :onPress #(js/alert (str "done! " @receipt))}
           [Text "Submit Receipt"]]]]
        [Tab {:heading "Edit"}
         [Text "Fake content 2"]
         [Button {:danger true
                  :onPress #(js/alert "done!")}
          [Text "Submit"]]
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
