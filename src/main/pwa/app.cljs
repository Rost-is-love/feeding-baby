(ns pwa.app
  (:require [reagent.dom :as dom]
            [pwa.views :as views]
            [pwa.db :as db]
            [re-frame.core :as rf]
            [goog.Uri :as uri]
            [goog.Uri.QueryData :as query]))

(defn get-url-param [param-name]
  (let [url (uri/parse js/window.location.href)
        query-data (.getQueryData url)]
    (.get query-data param-name)))

(defn get-current-url []
  (.-href (.-location js/window)))

(defn app
  []
  (let [code (get-url-param "code")]
    (prn "codeee" code)
    (when code (rf/dispatch [:obtain-token code]))
    (if false #_(:auth? @db/state)
        [views/patient-info]
        [views/ehr])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (dom/render [app]
              (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
