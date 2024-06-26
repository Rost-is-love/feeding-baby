(ns pwa.views
  (:require [pwa.events :as events]
            [pwa.db :as db]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defonce form-data (r/atom {:url (or (events/get-item :ehr-url) "")
                            :client-id (or (events/get-item :client-id) "")
                            :client-secret (or (events/get-item :client-secret) "")}))

(defn handle-change [key e]
  (swap! form-data assoc key (-> e .-target .-value)))

(defn ehr
  []
  (let [{:keys [url client-id client-secret]} @form-data]
    [:div
     {:class "flex min-h-full flex-col justify-center px-6 py-12 lg:px-8"}
     [:div
      {:class "sm:mx-auto sm:w-full sm:max-w-sm"}
      [:h2
       {:class
        "mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900"}
       "Connect to your EHR"]]
     [:div
      {:class "mt-10 sm:mx-auto sm:w-full sm:max-w-sm"}
      [:form
       {:class "space-y-6"
        :on-submit #(do (.preventDefault %)
                        (rf/dispatch [:ehr-form-submit @form-data]))}
       [:div
        [:label
         {:for "ehr-url",
          :class "block text-sm font-medium leading-6 text-gray-900"}
         "EHR URL"]
        [:div
         {:class "mt-2"}
         [:input
          {:id "ehr-url",
           :name "ehr-url",
           :type "url",
           :value url,
           :on-change #(handle-change :url %)
           :required true,
           :class
           "block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"}]]]
       [:div
        [:div
         {:class "flex items-center justify-between"}
         [:label
          {:for "client-id",
           :class "block text-sm font-medium leading-6 text-gray-900"}
          "Client ID"]]
        [:div
         {:class "mt-2"}
         [:input
          {:id "client-id",
           :name "client-id",
           :type "text",
           :value client-id,
           :on-change #(handle-change :client-id %)
           :required true,
           :class
           "block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"}]]]
       [:div
        [:div
         {:class "flex items-center justify-between"}
         [:label
          {:for "password",
           :class "block text-sm font-medium leading-6 text-gray-900"}
          "Client secret"]]
        [:div
         {:class "mt-2"}
         [:input
          {:id "password",
           :name "password",
           :type "password",
           :value client-secret,
           :on-change #(handle-change :client-secret %)
           :required true,
           :class
           "block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"}]]]
       [:div
        [:button
         {:type "submit",
          :class
          "flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"}
         "Launch app"]]]]]))

(defn patient-info
  []
  [:div {:class "h-screen flex overflow-hidden bg-white"}
   [:div {:class "hidden lg:flex lg:flex-shrink-0"}
    [:div {:class "flex flex-col w-64 border-r border-gray-200 pt-5 pb-4 bg-gray-100"}
     [:div {:class "flex items-center flex-shrink-0 px-6"}
      [:div {:class "text-center text-3xl font-bold text-gray-900"} "App"]]
     [:div {:class "h-0 flex-1 flex flex-col overflow-y-auto"}
      [:div {:class "px-3 mt-6 relative inline-block text-left"}
       [:div
        [:button {:type "button"
                  :on-click #(events/toggle-user-dropdown)
                  :class "group w-full bg-gray-100 rounded-md px-3.5 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-pink-500"
                  :id "options-menu"
                  :aria-expanded "false"
                  :aria-haspopup "true"}
         [:span {:class "flex w-full justify-between items-center"}
          [:span {:class "flex min-w-0 items-center justify-between space-x-3"}
           [:img {:class "w-10 h-10 bg-gray-300 rounded-full flex-shrink-0" :src "https://images.unsplash.com/photo-1502685104226-ee32379fefbe?ixlib=rb-1.2.1&ixqx=4cZVjZZC0A&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=3&w=256&h=256&q=80" :alt ""}]
           [:span {:class "flex-1 min-w-0"}
            [:span {:class "text-gray-900 text-sm font-medium truncate"} "Jessy Schwarz"]
            [:br]
            [:span {:class "text-gray-500 text-sm truncate"} "@jessyschwarz"]]]
          [:svg {:class "flex-shrink-0 h-5 w-5 text-gray-400 group-hover:text-gray-500" :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor" :aria-hidden "true"}
           [:path {:fill-rule "evenodd" :d "M10 3a1 1 0 01.707.293l3 3a1 1 0 01-1.414 1.414L10 5.414 7.707 7.707a1 1 0 01-1.414-1.414l3-3A1 1 0 0110 3zm-3.707 9.293a1 1 0 011.414 0L10 14.586l2.293-2.293a1 1 0 011.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" :clip-rule "evenodd"}]]]]]]
      [:nav {:class "px-3 mt-6"}
       [:div {:class "space-y-1"}
        [:a {:href "#" :class "bg-gray-200 text-gray-900 group flex items-center px-2 py-2 text-sm font-medium rounded-md"}
         [:svg {:class "text-gray-500 mr-3 h-6 w-6" :xmlns "http://www.w3.org/2000/svg" :fill "none" :viewBox "0 0 24 24" :stroke "currentColor" :aria-hidden "true"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"}]]
         "Add data"]
        [:a {:href "#" :class "text-gray-700 hover:text-gray-900 hover:bg-gray-50 group flex items-center px-2 py-2 text-sm font-medium rounded-md"}
         [:svg {:class "text-gray-400 group-hover:text-gray-500 mr-3 h-6 w-6" :xmlns "http://www.w3.org/2000/svg" :fill "none" :viewBox "0 0 24 24" :stroke "currentColor" :aria-hidden "true"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M4 6h16M4 10h16M4 14h16M4 18h16"}]] "Register EHR"]]]]]]
   [:div {:class "flex flex-col w-0 flex-1 overflow-hidden"}
    [:main {:class "flex-1 relative z-0 overflow-y-auto focus:outline-none" :tabIndex "0"}
     [:div {:class "px-4 mt-6 sm:px-6 lg:px-8"}
      [:h2 {:class "text-gray-500 text-xs font-medium uppercase tracking-wide"} "SMART App"]]]]])
