CREATE TABLE messages
(id SERIAL PRIMARY KEY,
message_text TEXT,
author VARCHAR(30) NOT NULL REFERENCES users(login),
message_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
