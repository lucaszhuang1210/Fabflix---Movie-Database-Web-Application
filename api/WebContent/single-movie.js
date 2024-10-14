/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Years: " + resultData[0]["movie_year"] + "<p>Director: " +
        resultData[0]["movie_director"] +"</p>");

    console.log("handleResult: populating movie table from resultData");

    // Find the empty table body by id "movie_genres_and_stars_table_body"
    let movieTableBodyElement = jQuery("#movie_genres_and_stars_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let i = 0;
    while (i < resultData.length) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";

        // Create links for each star
        let starNames = resultData[i]["movie_stars"].split(", ");  //stars are comma-separated
        let starIds = resultData[i]["movie_star_ids"].split(", "); //star_ids are comma-separated

        let starHTML = "";
        for (let k = 0; k < starNames.length; k++) {
            starHTML += '<a href="single-star.html?id=' + starIds[k] + '">' + starNames[k] + '</a>';
            // Add comma between stars
            if (k < starNames.length - 1) {
                starHTML += ", ";
            }
        }
        rowHTML += "<th>" + starHTML + "</th>"; // Add stars column with links

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        i++;
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});