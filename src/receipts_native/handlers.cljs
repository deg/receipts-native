(ns receipts-native.handlers
  (:require
   [re-frame.core :refer [reg-event-db]]
   [receipts-native.db :as db :refer [app-db]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    app-db))

(reg-event-db
  :set-greeting
  (fn [db [_ value]]
    (assoc db :greeting value)))
