(ns my-website.views.common
  (:require [noir.cookies :as cookies])
  (:use [noir.request])
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]
        [hiccup.core]
        [hiccup.page]
        [hiccup.form]))

(defn get-username []
    (cookies/get :username)
)

(defn show-user []
  (let [username (get-username)]
  (if username
    username
    "anonymous"
  )))


(defn get-current-uri []
  (:uri (ring-request))
)

(defpartial header []
  (let [username (show-user)]
	[:p username " " 
  (if (= "anonymous" username)
  [:a {:href (str "/login?page=" (get-current-uri))} "login"]
  [:a {:href "/logout"} "logout"]
  )])
)

(defpartial trace []
	(str (ring-request))
)

(defpartial layout [& content]
	(html5
	  [:head
	   [:title "my-website"]
	   (include-css "/css/reset.css")]
	  [:body
  	  (header)
      [:div#wrapper
        content]
      ])
)
