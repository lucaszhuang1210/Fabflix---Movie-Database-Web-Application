$(document).ready(function() {
    // Load the cart from localStorage
    let shoppingCart = JSON.parse(localStorage.getItem('shoppingCart')) || {};

    // Display cart contents
    function displayCart() {
        const cartTableBody = $("#cart_table_body");
        
        cartTableBody.empty();
        let totalPrice = 0;

        // Populate the cart table
        Object.keys(shoppingCart).forEach(movieId => {
            const movie = shoppingCart[movieId];
            const totalMoviePrice = (movie.price * movie.quantity).toFixed(2);
            totalPrice += parseFloat(totalMoviePrice);

            // Generate table rows
            let rowHTML = `
                <tr>
                    <td>${movie.title}</td>
                    <td>${movie.quantity}</td>
                    <td>$${movie.price.toFixed(2)}</td>
                    <td>$${totalMoviePrice}</td>
                    <td>
                        <button class="btn btn-sm btn-success increase-btn" data-movie-id="${movieId}">+</button>
                        <button class="btn btn-sm btn-warning decrease-btn" data-movie-id="${movieId}">-</button>
                        <button class="btn btn-sm btn-danger delete-btn" data-movie-id="${movieId}">Delete</button>
                    </td>
                </tr>
            `;
            cartTableBody.append(rowHTML);
        });

        // Update total price display
        $("#total_price").text(`Total: $${totalPrice.toFixed(2)}`);
    }

    // Modify the cart quantity
    function modifyCart(movieId, action) {
        if (shoppingCart[movieId]) {
            if (action === 'increase') {
                shoppingCart[movieId].quantity += 1;
            } else if (action === 'decrease' && shoppingCart[movieId].quantity > 1) {
                shoppingCart[movieId].quantity -= 1;
            } else if (action === 'delete') {
                delete shoppingCart[movieId];
            }

            // Save the updated cart to localStorage
            localStorage.setItem('shoppingCart', JSON.stringify(shoppingCart));
            displayCart();
        }
    }

    // Event listeners for cart actions
    $(document).on('click', '.increase-btn', function() {
        const movieId = $(this).data('movie-id');
        modifyCart(movieId, 'increase');
    });

    $(document).on('click', '.decrease-btn', function() {
        const movieId = $(this).data('movie-id');
        modifyCart(movieId, 'decrease');
    });

    $(document).on('click', '.delete-btn', function() {
        const movieId = $(this).data('movie-id');
        modifyCart(movieId, 'delete');
    });

    // Event listener for the "Proceed to Payment" button
    $("#proceed_to_payment").click(function() {
        // Redirect to the payment page
        window.location.href = 'payment.html';
    });

    // Load the cart on page load
    displayCart();
});

