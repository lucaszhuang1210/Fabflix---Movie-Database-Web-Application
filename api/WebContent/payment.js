$(document).ready(function() {
    const shoppingCart = JSON.parse(localStorage.getItem('shoppingCart')) || {};
    console.log(shoppingCart);

    // Calculate total price
    let totalPrice = 0;
    Object.values(shoppingCart).forEach(movie => {
        totalPrice += parseFloat(movie.price) * movie.quantity;
    });
    $("#total_price").text(`Total: $${totalPrice.toFixed(2)}`);

    // Handle the form submission
    $("#payment_form").submit(function(event) {
        event.preventDefault();

        const paymentData = {
            first_name: $("#first_name").val(),
            last_name: $("#last_name").val(),
            card_number: $("#card_number").val(),
            expiration_date: $("#expiration_date").val(),
            total_price: totalPrice.toFixed(2),
            cart: shoppingCart,
        };
        console.log("paymentData:", paymentData);

        // Send payment data to the backend for validation
        $.ajax({
            dataType: "json",
            method: "POST",
            url: "api/payment",
            data: JSON.stringify(paymentData),
            contentType: "application/json",
            success: function(response) {
                if (response.status === "success") {
                    console.log(shoppingCart);
                    alert("Transaction Successful!");
                    console.log(shoppingCart);
                    window.location.href = "confirmation.html";
                    //localStorage.removeItem('shoppingCart');
                } else {
                    alert("Payment failed: " + response.message);
                }
            },
            error: function() {
                alert("Payment processing failed. Please try again.");
            }
        });
    });
});
