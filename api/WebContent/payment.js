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
                console.log("Full response received:", response);
                console.log("Sale IDs received:", response.saleIds);
                if (response.status === "success") {
                    // alert("Transaction Successful!");
                    // console.log(shoppingCart);
                    // window.location.href = "confirmation.html";
                    alert("Transaction Successful!");
                    localStorage.setItem('saleIds', JSON.stringify(response.saleIds));
                    console.log("Sale IDs received:", response.saleIds);
                    console.log("Shoping chart", shoppingCart);
                    console.log("Shoping chart", localStorage);
                    window.location.href = "confirmation.html";

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
