(ns adventure.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str])
  (:gen-class))

(def items
  {:raw-egg {:title "Raw Egg"}
   :rope {:title "Rope"}
   :candlestick {:title "Candlestick"}
   :knife {:title "Knife"}
   :lead-pipe {:title "Lead pipe"}
   :revolver {:title "Revolver"}
   :wrench {:title "Wrench"}
   :penny {:title "Penny"}
   :hammer {:title "Hammer"}
   :lamp {:title "Lamp"}
   })


(def people
  {:butler {:title "Butler"
            :dialogue "Hi. I would begin by talking to Colonel Mustard in the drawing room located right east of the front door. He seemed like he saw something."
          }
   :professor-pum {:title "Professor Plum"
            :dialogue "I'm not talking to you before I talk to my lawyer."
          }
   :colonel-mustard {:title "Colonel Mustard"
            :dialogue "I saw a man dressed in purple run towards the back (north) side of the house."
          }
   :mrs-white {:title "Mrs. White"
            :dialogue "I didn't really see anything, sorry. I heard some gun shots though"
          }
   :mr-green {:title "Mr. Green"
            :dialogue "So you're here to solve the murder? Good luck."
          }
   :mrs-peacock {:title "Mrs. Peacock"
              :dialogue "There was a murder here? Oh no, I better leave."
          }
   :dave {:title "Dave"
            :dialogue "Messed up the game didn't ya? Ya I did that once."
          }
    })

(def the-map
  {:front-gate {:desc "The front gate of the mansion is old and rusty as if it doesn't see a lot of use. The gate is unlocked however so you can go inside."
           :title "at the front gate"
           :dir {:north :courtyard}
           :contents :raw-egg
           :person :mr-green}
   :courtyard {:desc "The courtyard is littered with leaves that have probably been here since last fall. To the east is a gazebo and to the west is a fountain. The butler greets you. To talk to him type the command 'talk'."
              :title "in the courtyard"
              :dir {:north :front-door
                    :south :front-gate
                    :east :gazebo
                    :west :fountain}
              :contents nil
              :person :butler}
   :front-door {:desc "The front door has an overly large set of double doors. This door is also unlocked so you can go right in."
              :title "at the front door"
              :dir {:north :cloak-room
                    :south :courtyard
                    :east :drawing-room
                    :west :library}
              :contents :rope
              :person nil}
   :gazebo {:desc "The gazebo doesn't have much to show. Maybe go look in the house for clues to the murder."
              :title "at the gazebo"
              :dir {:west :courtyard}
              :contents nil
              :person nil}
   :fountain {:desc "The fountain has a few pennies at the bottom that you can make out through the dirty water."
              :title "at the fountain"
              :dir {:east :courtyard}
              :contents :penny
              :person nil}
   :cloak-room {:desc "There's coats for all the people that are currently at the mansion. One belongs to the murderer and one belongs to the victim."
              :title "in the cloak room"
              :dir {:north :trophy-room
                    :south :front-door
                    :east :dining-room
                    :west :billard-room}
              :contents nil
              :person nil}
   :trophy-room {:desc "There's a couple horse racing trophies belonging to the original owner of the mansion. This room is the only one leading to the carriage house."
              :title "in the trophy room"
              :dir {:north :carriage-house
                    :south :cloak-room
                    :east :kitchen
                    :west :studio}
              :contents :wrench
              :person nil}
   :dining-room {:desc "In the dining room there are plates and food still on the table as dinner was being prepared when the dead body was found in the carriage house."
              :title "in the dining room"
              :dir {:north :kitchen
                    :south :drawing-room
                    :west :cloak-room}
              :contents :candlestick
              :person :mrs-white}
   :billard-room {:desc "The billard room has a big pool table taking up most of the room."
              :title "in the billard room"
              :dir {:north :studio
                    :south :library
                    :east :cloak-room}
              :contents :revolver
              :person nil}
   :carriage-house {:desc "A couple horses are in their stalls. That's not the reason this place smells so bad. The dead body of Dr. Black laysin the middle of the room with a couple of bullet casings on the floor and a bloody hole in the victim's chest where he as shot. This is where the murder took place."
              :title "in the carriage house"
              :dir {:south :trophy-room}
              :contents :lead-pipe
              :person :professor-pum}
   :kitchen {:desc "The kitchen is a mess. Looks as if they stopped in the middle of making a meal."
              :title "in the kitchen"
              :dir {:south :dining-room
                    :west :trophy-room}
              :contents :knife
              :person nil}
   :studio {:desc "The studio has a canvas on an easel in the middle of the room, that's about it."
              :title "in the studio"
              :dir {:north :conservatory
                    :south :billard-room
                    :east :trophy-room}
              :contents :lamp
              :person nil}
   :drawing-room {:desc "The drawing room has a couple large sofas. On one of them sits and old man who looks like he has something to say."
              :title "in the drawing room"
              :dir {:north :dining-room
                    :west :front-door}
              :contents nil
              :person :colonel-mustard}
   :library {:desc "The library is litered with books everywhere. There's spots on shelves where you can tell they used to go, but nobody put them back."
              :title "in the libary"
              :dir {:north :billard-room
                    :east :front-door}
              :contents :hammer
              :person :mrs-peacock}
   :conservatory {:desc "A couple large telescopes take up most of the conservatory. Professor plum stands in the corner looking very nervous."
              :title "in the conservatory"
              :dir {:south :studio}
              :contents nil
              :person :professor-pum}
   :grue-pen {:desc "There is no getting out of the grue pen."
              :title "in the grue pen"
              :dir {}
              :contents nil
              :person :dave}
   })

(def adventurer
  {:location :front-gate
   :inventory nil
   :tick 0
   :seen #{}
   :murderer nil
   :murder-location nil
   :murder-weapon nil})

(defn longStatus [location player]
  (println (-> the-map location :desc))
  (let [item (->> the-map location :contents)]
      (if(nil? item)
          (do (println "There is no item in this room.")
              player)
          (do (println (str "The item '" (->> items item :title) "' is in the room.")))))
  (let [dude (->> the-map location :person)]
      (if(nil? dude)
          (do (println "There is no person.")
              player)
          (do (println (str "The person '" (->> people dude :title) "' is in the room."))))))

(defn status [player]
  (let [location (player :location)]
    (print (str "You are " (-> the-map location :title) ". "))
    (when-not ((player :seen) location)
      ;;(println (-> the-map location :desc)))
      (longStatus location player))
    (update-in player [:seen] #(conj % location))))

(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

(defn go [dir player]
  (let [location (player :location)
        dest (->> the-map location :dir dir)]
    (if (nil? dest)
      (do (println "You can't go that way.")
          player)
      (assoc-in player [:location] dest))))

(defn talk [player]
  (let [location (player :location)
        dude (->> the-map location :person)]
    (if (nil? dude)
        (do (println "There's nobody to talk to.")
            player)
        (do (println (str "The " (->> people dude :title) " says: " (->> people dude :dialogue)))
            player))))

(defn grab [player]
  (let [location (player :location)
        item (->> the-map location :contents)]
    (if (nil? item)
        (do (println "There's nothing to grab in this room.")
            player)
        (let [player-with-item (assoc-in player [:inventory] (items item))]
            (println (str "You picked up a '" (->> player-with-item :inventory :title) "'."))
            player-with-item))))

(defn write-weapon [player]
  (let [item (->> player :inventory)]
    (if (nil? item)
        (do (println "There's no item in your inventory to write down.")
            player)
        (let [player-with-weapon (assoc-in player [:murder-weapon] item)]
            (println (str "You wrote down a '" (->> player-with-weapon :murder-weapon :title) "' as your murder weapon."))
            player-with-weapon))))

(defn write-murderer [player]
  (let [location (player :location)
        dude (->> the-map location :person)]
    (if (nil? dude)
        (do (println "There's nobody in this room.")
            player)
        (let [player-with-murderer (assoc-in player [:murderer] (people dude))]
            (println (str "You wrote down '" (->> player-with-murderer :murderer :title) "' as the murderer."))
            player-with-murderer))))

(defn write-location [player]
  (let [location (player :location)]
  (let [player-with-location (assoc-in player [:murder-location] (the-map location))]
      (println (str "You wrote down that the murderer happened '" (->> player-with-location :murder-location :title) "'."))
        player-with-location)))

(defn read-weapon [player]
  (let [w (player :murder-weapon)]
    (if(nil? w)
      (do (println "You haven't written anything down yet.")
          player)
      (do (println (str "You wrote down '" (w :title) "' as your murder weapon.")) player))))

(defn read-murderer [player]
   (let [m (player :murderer)]
    (if(nil? m)
      (do (println "You haven't written anything down yet.")
          player)
      (do (println (str "You wrote down '" (m :title) "' as your murderer.")) player))))

(defn read-location [player]
   (let [m (player :murder-location)]
    (if(nil? m)
      (do (println "You haven't written anything down yet.")
          player)
      (do (println (str "You wrote down that the murder happened '" (m :title) "'.")) player))))

(defn read-notes [player]
  (if (or (nil? (player :murder-weapon)) (nil? (player :murderer)) (nil? (player :murder-location)))
    (do (println "You need to write down something for each category first.") player)
    (do (println (str "You wrote down that the murder was committed by '" (->> player :murderer :title) "' with the '" (->> player :murder-weapon :title) "' '" (->> player :murder-location :title) "'."))
      player)))


(defn check [player]
  (if (or (nil? (player :murder-weapon)) (nil? (player :murderer)) (nil? (player :murder-location)))
    (do (println "You need to write down something for each category first.") player)
    (let [s (str "You wrote down that the murder was committed by '" (->> player :murderer :title) "' with the '" (->> player :murder-weapon :title) "' '" (->> player :murder-location :title) "'.")]
      (println s)
      (if (= s "You wrote down that the murder was committed by 'Professor Plum' with the 'Revolver' 'in the carriage house'.")
          (do (println "Congrats you Won!") player)
          (do (println "Oh no you lost :/")
            (assoc-in player [:location] :grue-pen))))))

(defn look-inv [player]
  (let [item (->> player :inventory)]
    (if (nil? item)
        (do (println "There's no item in your inventory.")
            player)
        (do (println (str "You have a '" (->> player :inventory :title) "' in your inventory.")) player))))


(defn tock [player]
  (update-in player [:tick] inc))

(defn respond [player command]
  (match command
         [:look] (update-in player [:seen] #(disj % (-> player :location)))
         [(:or :n :north )] (go :north player)
         [(:or :s :south )] (go :south player)
         [(:or :e :east )] (go :east player)
         [(:or :w :west )] (go :west player)
         [:talk] (talk player)
         [:grab] (grab player)
         [:write-weapon] (write-weapon player)
         [:write-murderer] (write-murderer player)
         [:write-location] (write-location player)
         [:check] (check player)
         [:read-weapon] (read-weapon player)
         [:read-murderer] (read-murderer player)
         [:read-location] (read-location player)
         [:read-notes] (read-notes player)
         [:look-inventory] (look-inv player)

         _ (do (println "I don't understand you.")
               player)

         ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Welcome to a text-based adventure version of the game Clue.")
  (println "In order to win, you must write down the location or the murder, the murderer, and the weapon used.")
  (println "You also have to have the weapon in your inventory to bring back to the police station.")
  (println "Your pockets are only big enough to hold one item at a time, to add an item type the command 'grab'.")
  (println "To write down the murderer, weapon and the location use the commands 'write-murderer', 'write-weapon' and 'write-location'.")
  (println "'Write-weapon' will write down the item in your inventory")
  (println "These commands write down the current room the player is in or the person in the current room.")
  (println "Talk to the people in the game to discover clues on what happened. Use command 'talk'.")
  (println "To check if you won, use the command 'check'.")
  (println "If you go to check if you won and you didn't than an agry grue will eat you. So use 'read-murderer', 'read-weapon' and 'read-location' to see what you wrote down first.")
  (println "To read them all at once type 'read-notes'. This will only work if you have something written for each category like in the real game.")
  (println "Good luck!")
  (println "-----------------------------------------")
  (println "")
  (loop [local-map the-map
         local-player adventurer]
    (let [pl (status local-player)
          _  (println "What do you want to do?")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
