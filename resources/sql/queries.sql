-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(name, pass)
VALUES (:name, :pass)

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id
