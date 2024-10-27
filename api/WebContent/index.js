/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Global variables to manage pagination state
 */
let currentPage = 1;
let recordsPerPage = 10; // Default records per page

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleMovieListResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Find the empty table body by id "movie_list_table_body"
    let movieTableBodyElement = jQuery("#movie_list_table_body");
    movieTableBodyElement.empty();

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";

        // Create links for each star
        let starNames = resultData[i]["movie_stars"].split(", ");  //stars are comma-separated
        let starIds = resultData[i]["movie_star_ids"].split(", "); //star_ids are comma-separated
        let starHTML = "";
        let starLimit = Math.min(3, starNames.length);
        for (let k = 0; k < starLimit; k++) {
            starHTML += '<a href="single-star.html?id=' + starIds[k] + '">' + starNames[k] + '</a>';
            // Add comma between stars
            if (k < starLimit - 1) {
                starHTML += ", ";
            }
        }
        rowHTML += "<th>" + starHTML + "</th>"; // Add stars column with links

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Changes the page number based on user interaction with Prev/Next buttons
 * @param step -1 for previous, 1 for next
 */
function changePage(step) {
    currentPage += step;
    fetchMovies();
}

/**
 * Changes the number of records per page when the user selects a new option
 */
function changeNumRecords() {
    recordsPerPage = parseInt(jQuery("#numRecordsSelect").val());
    currentPage = 1; // Reset to first page whenever N changes
    fetchMovies();
}

/**
 * Initialize the script
 */
jQuery(document).ready(function() {
    // Event listeners for pagination controls
    jQuery("#prevButton").click(function() { changePage(-1); });
    jQuery("#nextButton").click(function() { changePage(1); });
    jQuery("#numRecordsSelect").change(changeNumRecords);

    // Initial fetch of movies
    fetchMovies();
});

// /**
//  * Once this .js is loaded, following scripts will be executed by the browser
//  */
//
// // Makes the HTTP GET request and registers on success callback function handleStarResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "api/movie-list", // Setting request url, which is mapped by MovieListServlet
//     success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
// });
/**
 * Fetches movies from the server based on the current page and number of records per page
 */
function fetchMovies() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: `api/movie-list?page=${currentPage}&size=${recordsPerPage}`,
        success: handleMovieListResult,
        error: function(error) {
            console.error('Error fetching data: ', error);
            alert('Error fetching movie data. Please try again.');
        }
    });
}