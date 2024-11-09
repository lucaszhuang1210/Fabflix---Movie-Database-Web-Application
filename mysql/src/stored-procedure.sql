-- stored-procedure.sql

-- Drop the procedure if it already exists to avoid conflicts
DROP PROCEDURE IF EXISTS add_movie;

DELIMITER //

-- Create the stored procedure for adding a movie with a single star and genre
CREATE PROCEDURE add_movie(
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN star_birth_year INT,
    IN genre_name VARCHAR(32)
)
BEGIN
    DECLARE new_movie_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);
    DECLARE genre_id INT;

    -- Generate a new movie ID
    SELECT CONCAT('mv', LPAD(COALESCE(MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)), 0) + 1, 8, '0'))
    INTO new_movie_id
    FROM movies;

    -- Insert the new movie
    INSERT INTO movies (id, title, year, director)
    VALUES (new_movie_id, movie_title, movie_year, movie_director);

    -- Check if the star exists, if not, add the star
    SELECT id INTO new_star_id
    FROM stars
    WHERE name = star_name AND (birthYear = star_birth_year OR star_birth_year IS NULL);

    IF new_star_id IS NULL THEN
        -- Generate a new star ID
        SELECT CONCAT('nm', LPAD(COALESCE(MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)), 0) + 1, 8, '0'))
        INTO new_star_id
        FROM stars;

        -- Insert the new star
        INSERT INTO stars (id, name, birthYear)
        VALUES (new_star_id, star_name, star_birth_year);
    END IF;

    -- Link the star to the movie in the stars_in_movies table
    INSERT INTO stars_in_movies (starId, movieId)
    VALUES (new_star_id, new_movie_id);

    -- Check if the genre exists, if not, add the genre
    SELECT id INTO genre_id
    FROM genres
    WHERE name = genre_name;

    IF genre_id IS NULL THEN
        -- Insert the new genre
        INSERT INTO genres (name) VALUES (genre_name);
        SET genre_id = LAST_INSERT_ID();
    END IF;

    -- Link the genre to the movie in the genres_in_movies table
    INSERT INTO genres_in_movies (genreId, movieId)
    VALUES (genre_id, new_movie_id);

    -- Confirmation message
    SELECT CONCAT('Movie added successfully with ID: ', new_movie_id) AS confirmation_message;
END //

DELIMITER ;
