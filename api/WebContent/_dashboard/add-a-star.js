/**
 * This example demonstrates frontend and backend separation for adding a new star.
 *
 * This JavaScript performs three steps:
 *      1. Listens for the form submission to capture data.
 *      2. Sends the form data to the backend API via an AJAX POST request.
 *      3. Handles the response from the backend and provides feedback to the user.
 */

let add_star_form = $("#add_star_form");  // Select the form by ID

/**
 * Handle the data returned by the API, reads the response JSON, and provides feedback.
 * @param resultData jsonObject
 */
function handleAddStarResult(resultData) {
    console.log("handleAddStarResult: handling result from add star API");
    console.log("Result data received:", resultData);

    if (resultData["status"] === "success") {
        // Display the success message with the generated star ID
        alert(resultData["message"]);
        add_star_form[0].reset();  // Clear the form fields
    } else {
        alert("Error: " + resultData["message"]);  // Display error message
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    formSubmitEvent.preventDefault();  // Prevent the default form submission

    // Make an AJAX POST request to the API to add a new star
    $.ajax({
        url: "../api/add-a-star",
        method: "POST",
        data: add_star_form.serialize(),  // Serialize form data
        success: handleAddStarResult,
        error: function (xhr, status, error) {
            console.error("AJAX request failed:", status, error);
            alert("An error occurred while adding the star. Please try again.");
        }
    });

    console.log("AJAX request sent to backend.");
}

// Bind the submit action of the form to a handler function
add_star_form.submit(submitAddStarForm);




