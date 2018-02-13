(ns receipts-native.core
  (:require-macros [receipts-native.macros :refer [def-native-components]])
  (:require [cljs-time.core :as time]
            [cljs-time.coerce :refer [from-date to-date]]
            [oops.core :as oops]
            [reagent.core :as r]
            [reagent.impl.component :as ru]  ;; [TODO] ??
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-frame.loggers :refer [console]]
            [receipts-native.macros :as radon :refer [get-class]]
            [receipts-native.handlers]
            [receipts-native.subs]))

(defonce react-native (js/require "react-native"))



(def app-registry (.-AppRegistry react-native))
(def Alert (.-Alert react-native))


(defonce native-base (js/require "native-base"))  ;; [TODO] Figwheel seems to need this. Test/report/fix!
(def-native-components
  "native-base"
  [Body
   Button
   Container
   Header
   Icon
   Left
   Right
   Tab
   Tabs
   Title])


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
         ]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
