/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

// Global variables to store pagination, sorting, and search-related information
let currentPage = 1;
let numRecordsPerPage = 10; // Default number of records per page
let autocompleteCache = {};

// Global variables to store the user's search and sorting choices
let currentSortOption = 'rating_desc'; // Default sorting option
window.currentSearchCriteria = {
        year: '',
        director: '',
        star: '',
        genre: '',
        initial: '',
};

/**
 * Handles the data returned by the API and populates it into the page.
 * @param resultData jsonObject
 */
function handleMovieListResult(resultData) {
    // console.log("handleMovieListResult: populating movie table from resultData");

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

            // Genres with Links
            let genres = movies[i]["movie_genres"] ? movies[i]["movie_genres"].split(", ") : [];
            let genreIds = movies[i]["movie_genre_ids"] ? movies[i]["movie_genre_ids"].split(", ") : [];
            let genreHTML = "";
            for (let j = 0; j < genres.length; j++) {
                genreHTML += `<a href="#" class="genre-link" data-genre="${genreIds[j]}">${genres[j]}</a>`;
                if (j < genres.length - 1) {
                    genreHTML += ", "; // Separate genres with a comma
                }
            }
            rowHTML += "<th>" + genreHTML + "</th>";

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

            // Add the "Add to Cart" button with a unique data attribute for each movie
            rowHTML += `<th>
                            <button class='btn btn-success add-to-cart-btn' 
                                    data-movie-id='${movies[i]['movie_id']}' 
                                    data-movie-title='${movies[i]['movie_title']}'>Add to Cart</button>
                        </th>`;

            rowHTML += "</tr>";

            movieTableBodyElement.append(rowHTML);
        }
    }

    // Bind click event to genre links
    jQuery(".genre-link").off("click").on("click", function(event) {
        event.preventDefault();
        let genreId = jQuery(this).data("genre");

        // Update the genre in the search criteria and reset to the first page
        currentSearchCriteria.genre = genreId;
        currentPage = 1;

        saveSessionState(); // Save the new state in the session
        fetchMovies(); // Fetch updated movie list based on the selected genre
    });

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
            star: currentSearchCriteria.star,
            genre: currentSearchCriteria.genre,
            initial: currentSearchCriteria.initial
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
            star: currentSearchCriteria.star,
            genre: currentSearchCriteria.genre,
            initial: currentSearchCriteria.initial
        }
    });
}

function fetchGenres() {
    // Send an AJAX request to the backend to get the list of genres
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/genres",
        success: (resultData) => {
            populateGenreList(resultData);
        }
    });
}

function populateGenreList(genreData) {
    let container = jQuery("#genre_list");
    container.empty(); // Clear any existing content

    // Populate genres in the container
    genreData.forEach((genre) => {
        container.append(`<a href="#" class="browse-genre" data-genre="${genre.id}">${genre.name}</a> `);
    });

    // Event binding for genre click
    container.find(".browse-genre").off("click").on("click", function(event) {
        event.preventDefault();
        let genreId = jQuery(this).data("genre");

        // Update the search criteria for genre browsing
        currentSearchCriteria.genre = genreId;
        currentPage = 1; // Reset to the first page

        saveSessionState();
        fetchMovies(); // Fetch movies based on the selected genre
    });
}


function fetchTitleInitials() {
    populateTitleList("#title_initial_list");
}

function populateTitleList(containerId) {
    let container = $(containerId);
    container.empty(); // Clear existing content

    // Append numbers 0-9
    for (let i = 0; i <= 9; i++) {
        container.append(`<a href="#" class="browse-title" data-letter="${i}">${i}</a> `);
    }
    // Append letters A-Z
    for (let i = 65; i <= 90; i++) {
        let letter = String.fromCharCode(i);
        container.append(`<a href="#" class="browse-title" data-letter="${letter}">${letter}</a> `);
    }
    // Append '*'
    container.append('<a href="#" class="browse-title" data-letter="*">*</a>');

    // Event binding
    container.find(".browse-title").off("click").on("click", function(event) {
        event.preventDefault();
        let letter = $(this).data("letter");

        currentSearchCriteria.initial = letter;
        currentPage = 1; // Reset to the first page

        saveSessionState();
        fetchMovies(); // Fetch based on title initial
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

                fetchTitleInitials();
                fetchGenres();
                fetchMovies();
            } else {
                fetchTitleInitials();
                fetchGenres();
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
// Reset button event handler
jQuery("#reset_button").click(function () {
    // Clear search fields
    currentSearchCriteria.title = '';
    currentSearchCriteria.year = '';
    currentSearchCriteria.director = '';
    currentSearchCriteria.star = '';
    currentSearchCriteria.genre = '';
    currentSearchCriteria.initial = "%%";

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

// Auto-Complete Auto-Complete Auto-Complete Auto-Complete Auto-Complete Auto-Complete
$('#title').autocomplete({
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback);
    },
    onSelect: function (suggestion) {
        handleSelectSuggestion(suggestion);
    },
    deferRequestBy: 300, // Delay in milliseconds
    minChars: 3, // Minimum characters before triggering the request
});

// Handle lookup request
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    if (autocompleteCache[query]) {
        console.log("Using cached results for query:", query);
        console.log(autocompleteCache[query]);
        // Use cached results
        doneCallback({ suggestions: autocompleteCache[query] });
        return;
    }

    console.log("sending AJAX request to backend Java Servlet")

    $.ajax({
        method: "GET",
        url: "autocomplete",
        data: { title: query }, // Pass the query as "title"
        success: function (data) {
            autocompleteCache[query] = data;
            handleLookupAjaxSuccess(data, query, doneCallback);
        },
        error: function (errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    });
}

// Handle successful lookup response
function handleLookupAjaxSuccess(data, query, doneCallback) {
    // console.log("lookup ajax successful");
    console.log(data); // Log the response array
    doneCallback({ suggestions: data }); // Pass the response directly to the autocomplete library
}

// Handle suggestion selection
function handleSelectSuggestion(suggestion) {
    console.log("You selected " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"]);
    // Redirect to movie details page
    window.location.href = "/api_war/single-movie.html?id=" + suggestion["data"]["movieID"];
}