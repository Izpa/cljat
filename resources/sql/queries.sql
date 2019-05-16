-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(login, pass)
VALUES (:login, :password)

-- :name get-user :? :1
-- :doc retrieves a user record given the login
SELECT * FROM users
WHERE login = :login

-- :name create-message! :! :n
-- :doc creates a new message
INSERT INTO messages
(message_text, user_id)
VALUES (:text, :user_id)

-- :name get-old-messages :*
-- :doc retrieves 10 messages records with id <
SELECT * FROM messages
JOIN users ON messages.user_id = users.id
WHERE messages.id < :last_id
ORDER BY message.id
LIMIT 10
