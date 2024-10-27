/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */
//
//
// /**
//  * Handles the data returned by the API and populates data into HTML elements.
//  * @param resultData jsonObject
//  */
// function handleMovieListResult(resultData) {
//     console.log("handleMovieListResult: populating movie table from resultData");
//
//     // Extract pagination info
//     let currentPage = resultData.currentPage;
//     let totalPages = resultData.totalPages;
//     let movies = resultData.movies;
//
//     let movieTableBodyElement = jQuery("#movie_list_table_body");
//
//     // Clear existing content
//     movieTableBodyElement.empty();
//
//     // Check if any results were returned
//     if (movies.length === 0) {
//         movieTableBodyElement.append("<tr><td colspan='6'>No movies found matching your search criteria.</td></tr>");
//     } else {
//         // Populate the table
//         for (let i = 0; i < movies.length; i++) {
//             let rowHTML = "";
//             rowHTML += "<tr>";
//             rowHTML +=
//                 "<th>" +
//                 '<a href=" ' + movies[i]['movie_id'] + '">'
//                 + movies[i]["movie_title"] +
//                 '</a >' +
//                 "</th>";
//
//             rowHTML += "<th>" + movies[i]["movie_year"] + "</th>";
//             rowHTML += "<th>" + movies[i]["movie_director"] + "</th>";
//             rowHTML += "<th>" + movies[i]["movie_genres"] + "</th>";
//
//             // Create links for each star
//             let starNames = movies[i]["movie_stars"] ? movies[i]["movie_stars"].split(", ") : [];
//             let starIds = movies[i]["movie_star_ids"] ? movies[i]["movie_star_ids"].split(", ") : [];
//             let starHTML = "";
//             let starLimit = Math.min(3, starNames.length);
//             for (let k = 0; k < starLimit; k++) {
//                 starHTML += '<a href="single-star.html?id=' + starIds[k] + '">' + starNames[k] + '</a >';
//                 if (k < starLimit - 1) {
//                     starHTML += ", ";
//                 }
//             }
//             rowHTML += "<th>" + starHTML + "</th>";
//             rowHTML += "<th>" + movies[i]["movie_rating"] + "</th>";
//             rowHTML += "</tr>";
//
//             movieTableBodyElement.append(rowHTML);
//         }
//     }
//
//     // Create pagination controls
//     createPaginationControls(currentPage, totalPages);
// }
//
// /**
//  * Creates pagination controls based on current page and total pages.
//  * @param currentPage
//  * @param totalPages
//  */
// function createPaginationControls(currentPage, totalPages) {
//     let paginationControls = jQuery("#pagination_controls");
//     paginationControls.empty();
//
//     if (currentPage > 1) {
//         paginationControls.append('<button id="prev_page" class="btn btn-primary">Previous</button>');
//         jQuery("#prev_page").click(function () {
//             changePage(currentPage - 1);
//         });
//     }
//
//     paginationControls.append(' Page ' + currentPage + ' of ' + totalPages + ' ');
//
//     if (currentPage < totalPages) {
//         paginationControls.append('<button id="next_page" class="btn btn-primary">Next</button>');
//         jQuery("#next_page").click(function () {
//             changePage(currentPage + 1);
//         });
//     }
// }
//
// /**
//  * Sends an AJAX request to change the page.
//  * @param pageNumber
//  */
// function changePage(pageNumber) {
//     // Collect current sort option
//     let sortOption = jQuery("#sort").val();
//
//     // Send AJAX request with new page number
//     jQuery.ajax({
//         dataType: "json",
//         method: "GET",
//         url: "api/movie-list",
//         data: { page: pageNumber, sort: sortOption },
//         success: (resultData) => handleMovieListResult(resultData)
//     });
// }
//
// // Initial load: fetch movies based on session data
// jQuery.ajax({
//     dataType: "json",
//     method: "GET",
//     url: "api/movie-list",
//     success: (resultData) => handleMovieListResult(resultData)
// });
//
// // Attach a submit event handler to the search form
// jQuery("#search_form").submit(function (event) {
//     // Prevent the default form submission
//     event.preventDefault();
//
//     // Collect input values
//     let title = jQuery("#title").val();
//     let year = jQuery("#year").val();
//     let director = jQuery("#director").val();
//     let star = jQuery("#star").val();
//     let sortOption = jQuery("#sort").val();
//
//     // Send AJAX request to the servlet
//     jQuery.ajax({
//         dataType: "json",
//         method: "GET",
//         url: "api/movie-list",
//         data: {
//             title: title,
//             year: year,
//             director: director,
//             star: star,
//             sort: sortOption,
//             page: 1 // Start from first page when new search is made
//         },
//         success: (resultData) => handleMovieListResult(resultData)
//     });
// });
//
// // Handle sorting change
// jQuery("#sort").change(function () {
//     // Collect current sort option
//     let sortOption = jQuery("#sort").val();
//
//     // Send AJAX request with sort parameter
//     jQuery.ajax({
//         dataType: "json",
//         method: "GET",
//         url: "api/movie-list",
//         data: { sort: sortOption },
//         success: (resultData) => handleMovieListResult(resultData)
//     });
// });
//
// // Reset button handler
// jQuery("#reset_button").click(function () {
//     // Clear search fields
//     jQuery("#title").val('');
//     jQuery("#year").val('');
//     jQuery("#director").val('');
//     jQuery("#star").val('');
//
//     // Reset sort option
//     jQuery("#sort").val('rating_desc');
//
//     // Send AJAX request to reset search
//     jQuery.ajax({
//         dataType: "json",
//         method: "GET",
//         url: "api/movie-list",
//         data: { reset: true },
//         success: (resultData) => handleMovieListResult(resultData)
//     });
// });


// Global variables to store pagination, sorting, and search-related information
let currentPage = 1;
let numRecordsPerPage = 10; // Default number of records per page

// Global variables to store the user's search and sorting choices
let currentSortOption = 'rating_desc'; // Default sorting option
let currentSearchCriteria = {
    title: '',
    year: '',
    director: '',
    star: ''
};

/**
 * Handles the data returned by the API and populates it into the page.
 * @param resultData jsonObject
 */
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie table from resultData");

    // Extract pagination information
    currentPage = resultData.currentPage;
    let totalPages = resultData.totalPages;
    let movies = resultData.movies;

    let movieTableBodyElement = jQuery("#movie_list_table_body");

    // Clear existing content
    movieTableBodyElement.empty();

    // Check if any results were returned
    if (movies.length === 0) {
        movieTableBodyElement.append("<tr><td colspan='6'>No movies found matching your search criteria.</td></tr>");
    } else {
        // Populate the table
        for (let i = 0; i < movies.length; i++) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML +=
                "<th>" +
                // Add a link to single-movie.html with the movie ID passed as a GET URL parameter
                '<a class="movie-link" href="single-movie.html?id=' + movies[i]['movie_id'] + '">'
                + movies[i]["movie_title"] + // Display movie title as the link text
                '</a>' +
                "</th>";

            rowHTML += "<th>" + movies[i]["movie_year"] + "</th>";
            rowHTML += "<th>" + movies[i]["movie_director"] + "</th>";
            rowHTML += "<th>" + movies[i]["movie_genres"] + "</th>";

            // Create links for each star
            let starNames = movies[i]["movie_stars"] ? movies[i]["movie_stars"].split(", ") : [];
            let starIds = movies[i]["movie_star_ids"] ? movies[i]["movie_star_ids"].split(", ") : [];
            let starHTML = "";
            let starLimit = Math.min(3, starNames.length);
            for (let k = 0; k < starLimit; k++) {
                starHTML += '<a class="star-link" href="single-star.html?id=' + starIds[k] + '">' + starNames[k] + '</a>';
                if (k < starLimit - 1) {
                    starHTML += ", ";
                }
            }
            rowHTML += "<th>" + starHTML + "</th>";
            rowHTML += "<th>" + movies[i]["movie_rating"] + "</th>";
            rowHTML += "</tr>";

            movieTableBodyElement.append(rowHTML);
        }
    }

    // Create pagination controls
    createPaginationControls(currentPage, totalPages);
}

/**
 * Creates pagination controls that support dynamic selection of the number of records per page.
 * @param currentPage
 * @param totalPages
 */
function createPaginationControls(currentPage, totalPages) {
    let paginationControls = jQuery("#pagination_controls");
    paginationControls.empty();

    // Create the "Previous" button
    if (currentPage > 1) {
        paginationControls.append('<button id="prev_page" class="btn btn-primary">Previous</button>');
        jQuery("#prev_page").click(function () {
            changePage(currentPage - 1);
        });
    }

    // Display the current page and total pages
    paginationControls.append(' Page ' + currentPage + ' of ' + totalPages + ' ');

    // Create the "Next" button
    if (currentPage < totalPages) {
        paginationControls.append('<button id="next_page" class="btn btn-primary">Next</button>');
        jQuery("#next_page").click(function () {
            changePage(currentPage + 1);
        });
    }
}

/**
 * Changes the current page and fetches data based on the new page number.
 * @param pageNumber
 */
function changePage(pageNumber) {
    currentPage = pageNumber;
    saveSessionState();
    fetchMovies(); // Fetch data for the new page
}

/**
 * Fetches movie data from the database, supporting pagination, search, sorting, and dynamic records per page.
 */
function fetchMovies() {
    // Send an AJAX request to the backend to get movie data
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movie-list",
        data: {
            page: currentPage,
            limit: numRecordsPerPage, // Number of records per page
            sort: currentSortOption,
            title: currentSearchCriteria.title,
            year: currentSearchCriteria.year,
            director: currentSearchCriteria.director,
            star: currentSearchCriteria.star
        },
        success: (resultData) => handleMovieListResult(resultData)
    });
}

/**
 * Save the current state in the session before navigation
 */
function saveSessionState() {
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/session/save",
        data: {
            page: currentPage,
            limit: numRecordsPerPage,
            sort: currentSortOption,
            title: currentSearchCriteria.title,
            year: currentSearchCriteria.year,
            director: currentSearchCriteria.director,
            star: currentSearchCriteria.star
        }
    });
}

// Load initial data when the page loads
jQuery(document).ready(function() {
    // Load the session state on page load
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/session/load",
        success: (savedState) => {
            if (savedState) {
                currentPage = savedState.page || 1;
                numRecordsPerPage = savedState.limit || 10;
                currentSortOption = savedState.sort || 'rating_desc';
                currentSearchCriteria.title = savedState.title || '';
                currentSearchCriteria.year = savedState.year || '';
                currentSearchCriteria.director = savedState.director || '';
                currentSearchCriteria.star = savedState.star || '';

                // Repopulate search fields
                jQuery("#title").val(currentSearchCriteria.title);
                jQuery("#year").val(currentSearchCriteria.year);
                jQuery("#director").val(currentSearchCriteria.director);
                jQuery("#star").val(currentSearchCriteria.star);
                jQuery("#sort").val(currentSortOption);

                fetchMovies();
            } else {
                fetchMovies();
            }
        }
    });
});

// Search form submission event handler
jQuery("#search_form").submit(function (event) {
    // Prevent the default form submission
    event.preventDefault();

    // Update the search criteria
    currentSearchCriteria.title = jQuery("#title").val();
    currentSearchCriteria.year = jQuery("#year").val();
    currentSearchCriteria.director = jQuery("#director").val();
    currentSearchCriteria.star = jQuery("#star").val();
    currentPage = 1; // Reset to the first page when searching

    saveSessionState();
    fetchMovies(); // Fetch data based on the new search criteria
});

// Handle sorting change event
jQuery("#sort").change(function () {
    // Update the current sorting option
    currentSortOption = jQuery("#sort").val();
    currentPage = 1; // Reset to the first page when sorting changes

    saveSessionState();
    fetchMovies(); // Fetch data based on the new sorting option
});

// Reset button event handler
jQuery("#reset_button").click(function () {
    // Clear search fields
    currentSearchCriteria = {
        title: '',
        year: '',
        director: '',
        star: ''
    };
    currentSortOption = 'rating_desc'; // Reset sorting to the default
    currentPage = 1; // Reset to the first page

    // Update the form fields to be empty
    jQuery("#title").val('');
    jQuery("#year").val('');
    jQuery("#director").val('');
    jQuery("#star").val('');
    jQuery("#sort").val('rating_desc');

    saveSessionState();
    fetchMovies(); // Fetch data based on the reset criteria
});

// Handle change in the number of records per page
jQuery("#numRecordsSelect").change(function () {
    numRecordsPerPage = parseInt(jQuery("#numRecordsSelect").val());
    currentPage = 1; // Reset to the first page when the number of records changes

    saveSessionState();
    fetchMovies(); // Fetch data based on the new records per page setting
});
