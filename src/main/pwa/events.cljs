(ns pwa.events
  (:require
   [clojure.string :as str]
   [pwa.db :as db]
   [re-frame.core :as rf]
   [superstructor.re-frame.fetch-fx]))

(defn keywordize [x]
  (js->clj x :keywordize-keys true))

(defn remove-item
  [key]
  (.removeItem (.-localStorage js/window) (name key)))

(defn set-item
  [key val]
  (->> val clj->js (.stringify js/JSON) js/encodeURIComponent (.setItem (.-localStorage js/window) (name key))))



(defn get-item
  [key]
  (try (->> key
            name
            (.getItem (.-localStorage js/window))
            js/decodeURIComponent
            (.parse js/JSON)
            (keywordize))
       (catch js/Object e (do (remove-item key) nil))))

(rf/reg-cofx
 :storage/get
 (fn [coeffects keys]
   (reduce (fn [coef k]
             (assoc-in coef [:storage k] (get-item k)))
           coeffects (or keys (js->clj (js/Object.keys js/window.localStorage))))))

(rf/reg-fx
 :storage/set
 (fn [items]
   (doseq [[k v] items]
     (set-item k v))))

(rf/reg-fx
 :storage/remove
 (fn [keys]
   (doseq [k keys]
     (remove-item (name k)))))

(rf/reg-fx
  :navigate-to
  (fn [url]
    (set! (.-location js/window) url)))

(defn build-auth-url [authorization-endpoint state]
  (str authorization-endpoint "?response_type=code&client_id="
       (get-item :client-id) "&redirect_uri=http://localhost:8020"
       "&scope=launch/patient patient/Observation.rs patient/Patient.rs offline_acces"
       "&aud=" (get-item :ehr-url)
       "&state=" state))

(rf/reg-event-fx
  :smart-configuration-success
  (fn [{:keys [db]} [_ {{:keys [userinfo_endpoint authorization_endpoint token_endpoint]} :body}]]
    (let [state (random-uuid)]
      (prn "good-db" db)
      (prn "!!!" authorization_endpoint)
      {:navigate-to (build-auth-url authorization_endpoint state)
       :storage/set {:userinfo-endpoint userinfo_endpoint
                     :authorization-endpoint authorization_endpoint
                     :token-endpoint token_endpoint
                     :state state}})
    ))

(rf/reg-event-fx
  :bad-fetch-result
  (fn [{:keys [db]} opts]
    (prn "bad-request" opts)
    (js/alert "Something went wrong! Soooooorrrrrryyyyyyyy :(")
    {:db db}))

(rf/reg-event-fx
  :ehr-form-submit
  (fn [{:keys [db]} [_ {:keys [url client-id client-secret]}]]
    {:fetch {:method                 :get
             :url                    (str url (if (str/ends-with? url "/") "" "/")".well-known/smart-configuration")
             :mode                   :cors
             :timeout                5000
             :response-content-types {#"application/.*json" :json}
             :on-success             [:smart-configuration-success]
             :on-failure             [:bad-fetch-result]}
     :storage/set {:ehr-url url
                   :client-id client-id
                   :client-secret client-secret}}))

(rf/reg-event-fx
  :obtain-token-success
  (fn [{:keys [db]} [_ {body :body}]]
    (let [state (random-uuid)]
      (prn "obtain-token0sucess" body)
      {:storage/set {}})
    ))

(rf/reg-event-fx
  :obtain-token
  (fn [{:keys [db]} [_ code]]
    {:fetch {:method                 :post
             :url                    (get-item :token-endpoint)
             :body (js/JSON.stringify (clj->js {:client_id (get-item :client-id),
                                                :client_secret (get-item :client-secret),
                                                :code code,
                                                :grant_type "authorization_code"}))
             :request-content-type   :json
             :mode                   :cors
             :timeout                5000
             :on-success             [:obtain-token-success]
             :on-failure             [:bad-fetch-result]}}))

(defn login
  []
  (swap! db/state assoc :auth? true))

(defn logout
  []
  (swap! db/state assoc :auth? false))

(defn toggle-user-dropdown
  []
  (let [dropdown (:user-dropdown? @db/state)]
    (swap! db/state assoc :user-dropdown? (not dropdown))))
