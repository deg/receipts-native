(ns receipts-native.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [receipts-native.handlers]
              [receipts-native.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))

(defn get-react-property [name]
  (aget ReactNative name))

(defn adapt-class [class]
  (when class
    (r/adapt-react-class class)))

(defn get-class [name]
  (adapt-class (get-react-property name)))

(def picker (r/adapt-react-class (.-Picker ReactNative)))
(def picker-item (r/adapt-react-class (.-Item (.-Picker ReactNative))))

#_(def picker-class (get-class "Picker"))
#_(def picker-item-class
  (when-let [picker (get-react-property "Picker")]
    (adapt-class (.-Item picker))))

#_(defn picker
  ([{:keys [style item-style selected on-change]} items]
   [picker-class {:selectedValue selected :style style :itemStyle item-style :onValueChange on-change
                  :prompt "hello" :type "dropdown"}
    (for [{:keys [label value]} items]
      ^{:key (str value)}
      [picker-item-class
       {:label (or label value) :value value}])]))


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

#_(deftype Item [value]
  IEncodeJS
  (-clj->js [x] (.-value x))
  (-key->js [x] (.-value x))
  IEncodeClojure
  (-js->clj [x _] (.-value x)))



#_(defn- to-js-array
  "Converts a collection to a JS array (but leave content as is)"
  [coll]
  (let [arr (array)]
    (doseq [x coll]
      (.push arr x))
    arr))

#_(defn- wrap-data [o]
  (js/console.log "GOT")
  (js/console.log o)
  (js/console.log (Item. (to-js-array o)))
  (Item. (to-js-array o)))

#_(defn- wrap-render-fn [f]
  (fn [data]
    (r/as-element (f (.-item data) (.-index data) (.-separators data)))))

#_(def base-separator
  {:height           1
   :opacity          0.5})

#_(def separator
  (merge
    base-separator
    {:margin-left   70}))



#_(def default-separator [view separator])





#_(defn- base-list-props
  [{:keys [render-fn empty-component header separator default-separator?]}]
  (merge {:keyExtractor (fn [_ i] i)}
           (when render-fn               {:renderItem (wrap-render-fn render-fn)})
           (when separator               {:ItemSeparatorComponent (fn [] (r/as-element separator))})
           (when empty-component         {:ListEmptyComponent (fn [] (r/as-element empty-component))})
           (when header                  {:ListHeaderComponent (fn [] (r/as-element header))})))

#_(defn flat-list
  "A wrapper for FlatList.
   See https://facebook.github.io/react-native/docs/flatlist.html"
  [{:keys [data] :as props}]
  {:pre [(or (nil? data)
             (sequential? data))]}
  [flat-list-class props
   (merge (base-list-props props)
          props
          {:data (wrap-data data)})])


#_(def xpicker (partial r/create-element (.-Picker ReactNative)))
#_(def xpicker-item (partial r/create-element (.. ReactNative -Picker -Item)))

#_(xpicker (clj->js {})
         (xpicker-item (clj->js {:label "hej"}))
         (xpicker-item (clj->js {:label "hopp"})))

(defn alert [title]
  (.alert Alert title))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [image {:source (js/require "./assets/images/cljs.png")
               :style {:width 50
                       :height 50}}]
       #_[xpicker]
       [picker {:style {:width 350 :height 50 :margin 10}
                :selected-value "Hi!"
                :key "heh wat"
                :on-value-change #(alert "???")}
        [picker-item {:key "ok"
                      :value "Lemons!"
                      :label "Hahah wat"}]
        [picker-item {:key "not"
                      :value "PEars!"
                      :label "blebble"}]
        [picker-item {:key "whatever"
                      :value "apples!"
                      :label "hubba hubba"}]]
       #_[picker {:selected :a :on-change identity}
          [{:value :a} {:value :b} {:value :c}]]
       [picker {:style {:width 350 :height 50 :margin 10}
                :selected-value :b
                :on-value-change #(js/alert "Kilroy")}
        [picker-item {:value :a :label "Alabama"}]
        [picker-item {:value :b :label "Baltimore"}]
        [picker-item {:value :c :label "Canada"}]]
       #_[flat-list-class {:data [{:key :a}
                                {:key :b}
                                {:key :c}]
                         :render-item (fn [x] (:key x))}]
       [text-input-class]
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting "xxx"]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
