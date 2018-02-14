;;; Author: David Goldfarb (deg@degel.com)
;;; Copyright (c) 2018, David Goldfarb

(ns receipts-native.macros
  #?(:cljs (:require-macros [radon.core :refer [def-native-components]]))
  (:require
   [oops.core :as oops]
   [reagent.core :as r]))


#?(:cljs
   (do
     (defn adapt-class
       "Adapt React class for use as a Reagent component."
       [class]
       (r/adapt-react-class class))

     (defn get-class
       "Extract React class from JavaScript module and adapt as Reagent component."
       [module class-name]
       (adapt-class (oops/oget+ module class-name)))))

#?(:clj
   (defmacro def-native-components
     "Extract one or more React classes from a JavaScript module. Adapt them as Reagent components and name them.

      [TODO] Weird bug that the js/require here seems to crash figwheel, unless the same module was previously
      require'd from .cljs. If this bug is real, I'll probably have to remove the require from this macro."
     [module-name names]
     (let [module (gensym "module-")]
       `(let [~module (js/require ~module-name)]
          ~@(map (fn [name]
                   `(def ~name (get-class ~module ~(str name))))
                   names)))))
