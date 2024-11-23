CREATE TABLE IF NOT EXISTS users (
  user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR NOT NULL,
  login VARCHAR NOT NULL,
  name VARCHAR,
  birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friendship (
  user_id INTEGER REFERENCES users(user_id),
  friend_id INTEGER REFERENCES users(user_id),
  accept boolean DEFAULT false,
  constraint pk_viewing primary key (user_id, friend_id)
);


CREATE TABLE IF NOT EXISTS mpa_type (
  mpa_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  mpa_name VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
  film_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  film_name VARCHAR NOT NULL,
  description VARCHAR,
  release_date TIMESTAMP,
  duration INTEGER,
  mpa INTEGER REFERENCES mpa_type(mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
  like_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  film_id INTEGER REFERENCES films(film_id),
  user_id INTEGER REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS genre_type (
  genre_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  genre_name VARCHAR
);

CREATE TABLE IF NOT EXISTS genres_film (
  genres_film_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  film_id INTEGER REFERENCES films(film_id),
  genre_id INTEGER REFERENCES genre_type(genre_id)
);

CREATE TABLE IF NOT EXISTS directors (
  dir_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  dir_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS directors_films (
  film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
  dir_id INTEGER REFERENCES directors(dir_id) ON DELETE CASCADE,
constraint pk_dir_film primary key (dir_id, film_id)

);

