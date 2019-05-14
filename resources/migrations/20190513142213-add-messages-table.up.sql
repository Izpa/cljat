CREATE TABLE messages
(id SERIAL PRIMARY KEY,
message_text TEXT,
user_id INTEGER REFERENCES users(id),
message_timestamp TIMESTAMP);
