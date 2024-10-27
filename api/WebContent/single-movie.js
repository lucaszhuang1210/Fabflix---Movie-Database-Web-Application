/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to URL encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, reads the jsonObject and populates data into HTML elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // Check if resultData is not empty
    if (resultData.length > 0) {
        // Populate the movie info
        let movieInfoElement = jQuery("#movie_info");

        // Append movie details including rating
        movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
            "<p>Release Year: " + resultData[0]["movie_year"] + "</p>" +
            "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
            "<p>Rating: " + (resultData[0]["movie_rating"] || "N/A") + "</p>"+
            // Add the "Add to Cart" button with a unique ID for the movie
            `<button class='btn btn-success add-to-cart-btn' data-movie-id='${resultData[0]["movie_id"]}' 
            data-movie-title='${resultData[0]["movie_title"]}'>Add to Cart</button>`);

        // Handle Genres
        console.log("handleResult: populating genres from resultData");
        let genres = resultData[0]["movie_genres"].split(", ");
        let genreTableBody = jQuery("#movie_genres_table_body");
        genreTableBody.empty(); // Clear previous data if any
        genres.forEach(genre => {
            //TODO：记得TASK3 要求： each genre each hyperlinked, as equivalent to browsing by this genre.
            //let rowHTML = "<tr><td><a href='movie-list.html?genre=" + genre + "'>" + genre + "</a></td></tr>";
            let rowHTML = "<tr><th>" + genre + "</th></tr>";
            genreTableBody.append(rowHTML);
        });

        // Handle Stars
        console.log("handleResult: populating stars from resultData");
        let starNames = resultData[0]["movie_stars"].split(", ");
        let starIds = resultData[0]["movie_star_ids"].split(", ");
        let starsTableBody = jQuery("#movie_stars_table_body");
        starsTableBody.empty(); // Clear previous data if any
        for (let i = 0; i < starNames.length; i++) {
            let rowHTML = "<tr><td><a href='single-star.html?id=" + starIds[i] + "'>" + starNames[i] + "</a></td></tr>";
            starsTableBody.append(rowHTML);
        }
    } else {
        console.log("No movie data available");
    }
}

/**
 * Once this .js is loaded, the following scripts will be executed by the browser
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",     // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request URL, which is mapped by SingleMovieServlet
    success: (resultData) => handleResult(resultData), // Setting callback function to handle data returned successfully by the SingleMovieServlet
    error: (error) => console.error('Error fetching movie data:', error) // Log error if AJAX fails
});
