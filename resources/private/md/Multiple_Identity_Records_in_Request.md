---
author: david
title: Multiple Identity Records in Request
date: 2018-09-01T17:18:55.104-04:00
modified: 2018-09-02T10:25:12.062-04:00
---

At the beginning of the login process, the request looks like this:

```
post-login: req:
{:cookies
 {"ring-session" {:value "cf9914b5-c3d6-4bf0-9aa3-d80a2ee8f81f"},
  "Idea-87b9c033" {:value "b46c40c4-863f-4ebc-92d1-d7e66a6a600c"},
  "Idea-87b9c032" {:value "d8045bca-e39a-45d1-b65e-0cb2e2c519ae"},
  "Idea-87b9bc73" {:value "4a2e4995-3bf1-4c5e-b913-f9811eb05828"}},
 :remote-addr "0:0:0:0:0:0:0:1",
 :params {:user-name "david", :password "david"},
 :flash nil,
 :route-params {},
 :headers
 {"origin" "http://localhost:1350",
  "host" "localhost:1350",
  "user-agent"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15",
  "content-type"
  "multipart/form-data; boundary=----WebKitFormBoundaryC5pQnBjI7unf6eZB",
  "cookie"
  "ring-session=cf9914b5-c3d6-4bf0-9aa3-d80a2ee8f81f; Idea-87b9c033=b46c40c4-863f-4ebc-92d1-d7e66a6a600c; Idea-87b9c032=d8045bca-e39a-45d1-b65e-0cb2e2c519ae; Idea-87b9bc73=4a2e4995-3bf1-4c5e-b913-f9811eb05828",
  "content-length" "245",
  "referer" "http://localhost:1350/login",
  "connection" "keep-alive",
  "upgrade-insecure-requests" "1",
  "accept"
  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
  "accept-language" "en-us",
  "accept-encoding" "gzip, deflate"},
 :async-channel
 #object[org.httpkit.server.AsyncChannel 0x2ae5ba01 "/0:0:0:0:0:0:0:1:1350<->/0:0:0:0:0:0:0:1:53660"],
 :server-port 1350,
 :content-length 245,
 :form-params {},
 :compojure/route [:post "/login"],
 :websocket? false,
 :session/key nil,
 :query-params {},
 :content-type
 "multipart/form-data; boundary=----WebKitFormBoundaryC5pQnBjI7unf6eZB",
 :character-encoding "utf8",
 :uri "/login",
 :server-name "localhost",
 :query-string nil,
 :body
 #object[org.httpkit.BytesInputStream 0x3808e6a3 "BytesInputStream[len=245]"],
 :multipart-params {"user-name" "david", "password" "david"},
 :scheme :http,
 :request-method :post,
 :session {}}
```

The new session at the end of the login is:

```
post-login: new-session:
{:status 302,
 :headers {"Location" "/"},
 :body "",
 :session
 {:identity
  {:user_new_password_time nil,
   :user_role "admin",
   :user_registration #inst "2018-08-15T18:35:31.339000000-00:00",
   :user_new_password nil,
   :user_touched #inst "2018-08-15T18:35:31.568000000-00:00",
   :user_email_expires nil,
   :user_email "",
   :user_id 4,
   :user_name "david",
   :user_email_token 0}}}
```
By the time that the handler for the `home` route is activated, the request looks like this:

```
home: req:
{:identity
 {:user_new_password_time nil,
  :user_role "admin",
  :user_registration #inst "2018-08-15T18:35:31.339000000-00:00",
  :user_new_password nil,
  :user_touched #inst "2018-08-15T18:35:31.568000000-00:00",
  :user_email_expires nil,
  :user_email "",
  :user_id 4,
  :user_name "david",
  :user_email_token 0},
 :cookies
 {"ring-session" {:value "3fe964fd-cc9b-4409-99a9-25c85a0103f8"},
  "Idea-87b9c033" {:value "b46c40c4-863f-4ebc-92d1-d7e66a6a600c"},
  "Idea-87b9c032" {:value "d8045bca-e39a-45d1-b65e-0cb2e2c519ae"},
  "Idea-87b9bc73" {:value "4a2e4995-3bf1-4c5e-b913-f9811eb05828"}},
 :remote-addr "0:0:0:0:0:0:0:1",
 :params {},
 :flash nil,
 :route-params {},
 :headers
 {"origin" "http://localhost:1350",
  "host" "localhost:1350",
  "user-agent"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15",
  "cookie"
  "ring-session=3fe964fd-cc9b-4409-99a9-25c85a0103f8; Idea-87b9c033=b46c40c4-863f-4ebc-92d1-d7e66a6a600c; Idea-87b9c032=d8045bca-e39a-45d1-b65e-0cb2e2c519ae; Idea-87b9bc73=4a2e4995-3bf1-4c5e-b913-f9811eb05828",
  "referer" "http://localhost:1350/login",
  "connection" "keep-alive",
  "upgrade-insecure-requests" "1",
  "accept"
  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
  "accept-language" "en-us",
  "accept-encoding" "gzip, deflate"},
 :async-channel
 #object[org.httpkit.server.AsyncChannel 0x7a57ef5 "/0:0:0:0:0:0:0:1:1350<->/0:0:0:0:0:0:0:1:53835"],
 :server-port 1350,
 :content-length 0,
 :form-params {},
 :compojure/route [:get "/"],
 :websocket? false,
 :session/key "3fe964fd-cc9b-4409-99a9-25c85a0103f8",
 :query-params {},
 :content-type nil,
 :character-encoding "utf8",
 :uri "/",
 :server-name "localhost",
 :query-string nil,
 :body nil,
 :multipart-params {},
 :scheme :http,
 :request-method :get,
 :session
 {:identity
  {:user_new_password_time nil,
   :user_role "admin",
   :user_registration #inst "2018-08-15T18:35:31.339000000-00:00",
   :user_new_password nil,
   :user_touched #inst "2018-08-15T18:35:31.568000000-00:00",
   :user_email_expires nil,
   :user_email "",
   :user_id 4,
   :user_name "david",
   :user_email_token 0}}}
```
Now it contains two `:identity` maps -- one at the beginning and one at the end.