CREATE TABLE users
(id SERIAL PRIMARY KEY,
 login VARCHAR(30) NOT NULL UNIQUE,
 pass VARCHAR(300) NOT NULL);
