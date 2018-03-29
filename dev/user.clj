(ns user
  (:require
    [predis.core :as redis]
    [predis.carmine :as carmine]))

(def carmine-client (carmine/->redis {:pool {} :spec {:host "127.0.0.1" :port 6379}}))
