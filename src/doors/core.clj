(ns doors.core
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :only [conde fresh all run* run == != s# u#]]))

(defn claim [honest liar is-honest]
  (conde [(== is-honest true)  honest]
         [(== is-honest false) liar]))

(defn signo [honest? sign-num door-num out]
  (== out [honest? sign-num door-num]))

(defn dooro [sign-num door-num color trap out]
  (== out [sign-num door-num color trap]))

(defn door-idxo [door idx]
  (fresh [s c t]
    (dooro s idx c t door)))

(defn connecto [door sign]
  (fresh [sign-num door-num]
    (fresh [honest?]
      (signo honest? sign-num door-num sign))
    (fresh [color trap]
      (dooro sign-num door-num color trap door))))

(defn see-door [door-num color door]
  (fresh [sign-num trap]
    (dooro sign-num door-num color trap door)))

(defn see-sign [sign sign-num true-claim false-claim]
  (fresh [honest? door-num]
    (signo honest? sign-num door-num sign)
    (claim true-claim false-claim honest?)))

(defn sign-honesto [sign honest?]
  (fresh [s d]
    (signo honest? s d sign)))

(defn door-honesto [door honest?]
  (fresh [sign]
    (connecto door sign)
    (sign-honesto sign honest?)))

(defn trapo [door trap?]
  (fresh [s d c]
    (dooro s d c trap? door)))

(defn persono [honest? num race fat incognito out]
  (== out [honest? num race fat incognito]))

(defn person-honesto [honest? person]
  (fresh [n r f i]
    (persono honest? n r f i person)))

(defn raceo [race person]
  (fresh [h n f i]
    (persono h race f i person)))

(defn vampireo [person]
  (person-honesto false person)
  (raceo 'vampire person))

(defn wizardo [person]
  (person-honesto true person)
  (raceo 'wizard person))

(defn humano [person]
  (raceo 'human person))

(defn see-person [person ])

;; $ ./Build/doors 345 0
;; #1;yellow;1;1;1
;; The door number 2 is a trap.
;; Inverted: The door number 2 is safe to enter.
;; #2;green;1;0;1
;; Either all the doors tell truth, or they all are lying.
;; Inverted: Some of the doors tell truth, some are lying.
(run 1 [x]
  (fresh [door1 door2
          sign1 sign2]
    (fresh [door]
      (trapo door false)
      (conde [(== door door1)]
             [(== door door2)])
      (door-idxo door x))

    (conde [(trapo door1 false)]
           [(trapo door2 false)])

    (see-door 1 'yellow door1)
    (see-door 2 'green door2)
    (connecto door1 sign1)
    (connecto door2 sign2)
    (see-sign sign1 1
              (trapo door2 true)
              (trapo door2 false))
    (see-sign sign2 2
              (fresh [all-true?]
                (sign-honesto sign1 all-true?)
                (sign-honesto sign2 all-true?))
              (all (conde [(sign-honesto sign1 true)]
                          [(sign-honesto sign2 true)])
                   (conde [(sign-honesto sign1 false)]
                          [(sign-honesto sign2 false)])))))