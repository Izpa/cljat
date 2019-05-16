-- :name create-user! :<! :1
-- :doc creates a new user record
INSERT INTO users
(login, pass)
VALUES (:login, :password)
RETURNING *

-- :name get-user :? :1
-- :doc retrieves a user record given the login
SELECT * FROM users
WHERE login = :login

-- :name create-message! :<! :1
-- :doc creates a new message
INSERT INTO messages
(message_text, author)
VALUES (:text, :author)
RETURNING *

-- :name get-old-messages :*
-- :doc retrieves 10 messages records with id <
SELECT * FROM messages
WHERE messages.id < :last_id
ORDER BY message.id
LIMIT 10
