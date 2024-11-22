-- Drop the database if it already exists
DROP DATABASE IF EXISTS moviedb;

-- Create the new database
CREATE DATABASE moviedb;

-- Use the new database
USE moviedb;

-- Create the 'movies' table
CREATE TABLE movies (
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL,
    FULLTEXT idx (title)
);

-- use below line to add full-text indexing without re-creating the table
-- ALTER TABLE movies ADD FULLTEXT(title); 

-- Create the 'stars' table
CREATE TABLE stars (
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER
);

-- Create the 'stars_in_movies' table
CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    PRIMARY KEY (starId, movieId),
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create the 'genres' table
CREATE TABLE genres (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(32) NOT NULL
);

-- Create the 'genres_in_movies' table
CREATE TABLE genres_in_movies (
    genreId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    PRIMARY KEY (genreId, movieId),
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create the 'creditcards' table
CREATE TABLE creditcards (
    id VARCHAR(20) PRIMARY KEY NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    expiration DATE NOT NULL
);

-- Create the 'customers' table
CREATE TABLE customers (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccId VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL,
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

-- Create the 'sales' table
CREATE TABLE sales (
    id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    customerId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    saleDate Date NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create the 'ratings' table
CREATE TABLE ratings (
    movieId VARCHAR(10) PRIMARY KEY NOT NULL,
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create the 'employees' table
CREATE TABLE employees (
    email VARCHAR(50) PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100)
);


