(ns receipts-native.core
  (:require-macros [receipts-native.macros :refer [def-native-components]])
  (:require
   [re-frame.core :refer [reg-event-db dispatch-sync]]
   [reagent.core :as r]
   [receipts-native.macros :as radon :refer [get-class]]))

(defonce react-native (js/require "react-native"))
(def app-registry (.-AppRegistry react-native))


;;;  **************** {READ THIS} ****************
;;; Restoring this next line removes the crash.
;;; It seems that Figwheel (or something) is unhappy when module is first required in the
;;; expansion of a macro???
;;- (defonce native-base (js/require "native-base"))

(def-native-components
  "native-base"
  [Body
   Container
   Header
   Title])


(defn app-root []
  (fn []
      [Container
       [Header
        [Body
         [Title "Receipts v4.0"]]]]))


;; initial state of app-db
(def app-db {})
(reg-event-db :initialize-db (fn [_ _] app-db))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
