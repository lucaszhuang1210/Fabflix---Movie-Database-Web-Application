/**
 * This example demonstrates frontend and backend separation for adding a new movie.
 *
 * This JavaScript performs three steps:
 *      1. Listens for the form submission to capture data.
 *      2. Sends the form data to the backend API via an AJAX POST request.
 *      3. Handles the response from the backend and provides feedback to the user.
 */

let add_movie_form = $("#add_movie_form");  // Select the form by ID

/**
 * Handle the data returned by the API, reads the response JSON, and provides feedback.
 * @param resultData jsonObject
 */
function handleAddMovieResult(resultData) {
    console.log("handleAddMovieResult: handling result from add movie API");
    console.log("Result data received:", resultData);

    if (resultData["status"] === "success") {
        // Display the success message with the generated movie ID
        $("#add_movie_message").html(`<p style="color: green;">${resultData["message"]}</p>`);
        add_movie_form[0].reset();  // Clear the form fields
    } else {
        // Display error message in red
        $("#add_movie_message").html(`<p style="color: red;">Error: ${resultData["message"]}</p>`);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add movie form");
    formSubmitEvent.preventDefault();  // Prevent the default form submission

    // Make an AJAX POST request to the API to add a new movie
    $.ajax({
        url: "../api/add-movie",
        method: "POST",
        data: add_movie_form.serialize(),  // Serialize form data
        success: handleAddMovieResult,
        error: function (xhr, status, error) {
            console.error("AJAX request failed:", status, error);
            $("#add_movie_message").html(`<p style="color: red;">An error occurred while adding the movie. Please try again.</p>`);
        }
    });

    console.log("AJAX request sent to backend.");
}

// Bind the submit action of the form to a handler function
add_movie_form.submit(submitAddMovieForm);

