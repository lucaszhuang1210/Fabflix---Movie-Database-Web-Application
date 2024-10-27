$(document).ready(function() {
    // Initialize or load the shopping cart from localStorage
    let shoppingCart = JSON.parse(localStorage.getItem('shoppingCart')) || {};
    // localStorage 是一种在浏览器中存储数据的方式，它可以跨页面保存数据，即使用户关闭浏览器后数据也会保存。
    // || {}意思：
    // 如果 JSON.parse(localStorage.getItem('shoppingCart')) 返回 null（即 localStorage
    // 中没有保存 shoppingCart），它会返回一个空对象 {} 作为默认值。

    // Function to handle "Add to Cart" button click
    function addToCart(movieId, movieTitle) {
        // Check if the movie is already in the cart
        if (shoppingCart[movieId]) {
            shoppingCart[movieId].quantity += 1;
        } else {
            // Generate a random price for the movie between $5 and $25
            const price = (Math.random() * 20 + 5).toFixed(2);
            shoppingCart[movieId] = {
                title: movieTitle,
                quantity: 1,
                price: parseFloat(price)
            };
        }

        // Save the updated cart to localStorage
        localStorage.setItem('shoppingCart', JSON.stringify(shoppingCart));

        // Display success message
        alert(`${movieTitle} has been added to the cart!`);
    }

    // Event listener for the "Add to Cart" button
    $(document).on('click', '.add-to-cart-btn', function() {
        const movieId = $(this).data('movie-id');
        const movieTitle = $(this).data('movie-title');

        // Call the addToCart function
        addToCart(movieId, movieTitle);
    });
});
