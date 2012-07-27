(ns my-website.views.welcome
  (:require [my-website.views.common :as common])
  (:require [noir.validation :as vali])
  (:require [noir.response :as resp])
  (:require [noir.cookies :as cookies])
  (:use [noir.request])
  (:use [noir.core]
        [hiccup.core]
        [hiccup.page]
        [hiccup.form]
))

(defn add-auth-cookie [username]
	(cookies/put! :username username)
)

(defn authenticate [{:keys [username password]}]
  (vali/rule (= password "1")
             [:password "Incorrect."])
  (let [valid? (not (vali/errors? :password))]
    (when valid? (add-auth-cookie username))
    valid?))

(defn authenticated? [] 
  (common/get-username))

(defn page-query-param [query] 
  (get query "page")
  )

(defn get-page []
  (let [page (str (page-query-param (:query-params (ring-request))))]
    (if page
      page
      "/"))
)

(defn is-admin? []
  (= "admin" (cookies/get :username)))

(pre-route "/privileged" {}
           (if (not (authenticated?))
             (resp/redirect "/login?page=/privileged")
             (when-not (is-admin?)
               (resp/redirect "/forbidden"))))

(pre-route "/private" {}
           (when-not (authenticated?)
             (resp/redirect "/login?page=/private")))

(defpage "/welcome" []
         (common/layout
           [:div [:p "Welcome: " (common/show-user)]]
           [:div [:a {:href "/privileged"} "Privileged"] " (you need to be super)"]
           [:div [:a {:href "/private"} "Private"]]))

(pre-route "/" []
  (resp/redirect "/welcome"))

(defpage "/privileged" []
		 (common/layout
		   [:p "Welcome back super user. You have to be one to see this!"]))

(defpage "/private" []
		 (common/layout
		   [:p "Welcome to my private page"]))

(defpage "/login" {:as params}
	(common/layout
	  [:p "Login"]
	  (form-to [:post (str "/login?page=" (get-page))]
	    (label "username" "Username: ")
	    (text-field "username")
	    (label "password" "Password: ")
	    (text-field "password")
	    (submit-button "Login"))))

(defpage [:post "/login"] {:as params}
  (if (authenticate params)
    (resp/redirect (get-page))
    (common/layout
   			[:p "Oops!"])))

(defpage "/logout" {}
	(common/layout
	  [:p "Logout"]
	  (form-to [:post "/logout"]
	    (submit-button "Logout"))))

(defpage [:post "/logout"] {}
  	(cookies/put! :username "")
    (resp/redirect "/"))

(defpage "/users" []
{:body "users..."}  
  )

(defpage "/users/:user" {username :user}
	(common/layout
	  [:p "User: " username]))

(defpage "/error" []
    {:status 500
     :body "Oh no! An error has occurred"})

(defpage "/forbidden" []
    {:status 403
     :body "Oh, oh... You are not allowed to access this resource"})

(defpage "/hn-catalog" {:as params}
	(common/layout
	  [:p "Machine(s) Provisioning"]
	  (form-to [:post (str "/provision")]
	    [:div (label "cpu" "CPU: ")
	    (text-field "cpu")]
      [:div (label "memory" "RAM: ")
       (text-field "memory") "GB"]
	    [:div (label "base-image" "Image reference: ")
	    (text-field "base-image")(submit-button "Select...")]
	    [:div (label "disk-type" "Disk type (e.g. HDD, SSD): ")
	    (drop-down "disk-type" ["HDD" "SSD"])]
	    [:div (label "extra-disk-size" "Extra disk size: ")
	    (text-field "extra-disk-size")]
	    [:div (label "count" "How many: ")
	    (text-field "count")]
	    [:div (label "green" "Green: ")
	    (check-box "green")]
	    [:div (label "other" "Other: ")
	    (text-field "other")]
	    [:div (label "support" "Support: ")
	    (drop-down "support" ["Best effort" "5/8" "24/7"])]
      [:div "Here you go..."]
      [:a {:href "/privileged"} "Logica"](submit-button "Provision")
      [:div ][:a {:href "/privileged"} "Atos"](submit-button "Provision")
      [:div ][:a {:href "/privileged"} "T-Systems"](submit-button "Provision")
      [:div ][:a {:href "/privileged"} "CloudSigma"](submit-button "Provision")
)))
