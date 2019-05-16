CREATE TABLE messages
(id SERIAL PRIMARY KEY,
message_text TEXT,
user_id INTEGER NOT NULL REFERENCES users(id),
message_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
