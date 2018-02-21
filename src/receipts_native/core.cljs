(ns receipts-native.core
  (:require
   [re-frame.core :refer [reg-event-db dispatch-sync]]
   [reagent.core :as r]))

(defonce react-native (js/require "react-native"))
(def app-registry (.-AppRegistry react-native))


;;;  **************** {READ THIS} ****************
;;; Restoring this next line removes the crash.
;;; It seems that Figwheel (or something) is unhappy when module is first required in the
;;; expansion of a macro???

;; (defonce native-base (js/require "native-base"))

(defn get-class
       "Extract React class from JavaScript module and adapt as Reagent component."
       [module class-name]
     (r/adapt-react-class (aget module class-name)))

(let [module (js/require "native-base")]
  (def Body      (get-class module "Body"))
  (def Container (get-class module "Container"))
  (def Header    (get-class module "Header"))
  (def Title     (get-class module "Title")))


(defn app-root []
  (fn []
      [Container
       [Header
        [Body
         [Title "Receipts v4.0"]]]]))


(def app-db {})
(reg-event-db :initialize-db (fn [_ _] app-db))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
