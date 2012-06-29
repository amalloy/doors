;; http://rooms.jmpup.com/room.php?seed=345&level=0&rcnt=0&rank=0
;; $ ./Build/doors 345 0
;; #1;yellow;1;1;1
;; The door number 2 is a trap.
;; Inverted: The door number 2 is safe to enter.
;; #2;green;1;0;1
;; Either all the doors tell truth, or they all are lying.
;; Inverted: Some of the doors tell truth, some are lying.

(run 1 [x]
  ;; create door and sign objects
  (fresh [door1 door2
          sign1 sign2]
    ;; describe a safe door
    (fresh [safe-door]
      (trapo door false)       ;; it's not a trap
      (conde [(== door door1)] ;; it's one of the doors in this room
             [(== door door2)])
      (door-idxo door x)) ;; x (our eventual return value) should be its index

    ;; reassure the solver that at least one of the doors is safe
    (conde [(trapo door1 false)]
           [(trapo door2 false)])

    (see-door 1 'yellow door1) ;; describe colors, not that it matters this time
    (see-door 2 'green door2)

    (connecto door1 sign1) ;; no free-floating signs - we know where each is attached
    (connecto door2 sign2)

    ;; sign 1 is indexed 1, and says door 2 is a trap
    (see-sign sign1 1
              (trapo door2 true)
              (trapo door2 false))

    ;; sign 2 is indexed 2
    (see-sign sign2 2
              (fresh [all-true?] ; if it's telling the truth, all doors have the same
                                        ; "honesty" setting
                (sign-honesto sign1 all-true?)
                (sign-honesto sign2 all-true?))
              (all (conde [(sign-honesto sign1 true)] ; otherwise, at least one lies
                          [(sign-honesto sign2 true)]) ; and at least one is honest
                (conde [(sign-honesto sign1 false)] ; (ie they are not all the same)
                       [(sign-honesto sign2 false)])))))

;; (1)