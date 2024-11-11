# Team **MicroHard**

## Developed by:
- Lucas (Kaixiang) Zhuang, Student ID: 73969468
- Betty (Jiatong) Liu, Student ID: 51549174

---

# Team Contributions
## project 1:
We collaborated on setting up the environment (Tasks 1-5).
### Specific Contributions:

- **Lucas:** Debugged and updated the Single Movie page, wrote the `README.md`, created the `moviedb` database, set up AWS, and recorded video demonstrations.
- **Betty:** Implement Movie List Page (`MovieListServlet`, `index.html`, and `index.js`), Single Stars Page (`SingleStarServlet`, `single-star.html`, and `single-star.js`), Single Movie (`SingleMovieServlet`, `single-movie.html`, and `single-movie.js`) Page, created the `style.css`, fixed Maven packages setup, and assisted with AWS instance setup.

## project 2:
We collaborated on designing the software architecture.
### Specific Contributions:

- **Lucas:** Implement the Search and Browse on the Main Page.
- **Betty:** Implement the Shopping Cart, Login Page.

## project 3:
We collaborated on debugging and use PreparedStatement.
### Specific Contributions:

- **Lucas:** Adding HTTPS, Importing large XML data files into the Fabflix database.
- **Betty:** Adding reCAPTCHA, Use Encrypted Password, Implementing an Employee Dashboard using Stored Procedure with function： 1. metadata display 2. insert a star 3. inserting a movie.
  
List filenames with Prepared Statements:
ActorParser.java
CastParser.java
MovieParser.java
AddMovieServlet.java
AddStarServlet.java
EmployeeLoginServlet.java
GenreServlet.java
LoginServlet.java
MetadataServlet.java
MovieListServlet.java
PaymentServlet.java
SingleMovieServlet.java
SingleStarServlet.java

---

## Video Explanation
[Watch the video on YouTube](https://www.youtube.com/@lucaszhuang1478)  

---

## Project Overview

This project is a **web-based application** deployed on **AWS**, utilizing a range of modern technologies. The application is developed using **Java** and **JavaScript**, with the backend hosted on an **Apache Tomcat** server. It employs **Maven** for dependency management and builds, while the **MySQL database** handles data storage. 

Leveraging AWS cloud services, the application is designed to be both scalable and accessible. It provides a robust platform for efficient data handling and dynamic user interactions, showcasing a full-stack architecture that integrates frontend, backend, and database layers.

---

## Application Components

### Backend:
- **MovieListServlet.java**: A Java servlet that interacts with the database to retrieve a list of movies. It responds with movie data in **JSON format**. Movie names are generated as links to the **Single Movie** page, and star names link to the **Single Star** page.
  
- **SingleStarServlet.java**: Retrieves information about a specific star and all movies the star has acted in, returning the results in **JSON format**.

- **SingleMovieServlet.java**: Retrieves information about a specific movie, including its stars and genres, and returns this data in **JSON format**.
  
- LoginFilter.java: A Java servlet filter that intercepts all incoming requests, allowing access to certain URLs without login while redirecting unauthorized users to the login page if they are not logged in.

- LoginServlet.java: A Java servlet that handles user login by verifying credentials against the database. It responds with a JSON object indicating success or failure and manages session creation upon successful login.

- PaymentServlet.java: A Java servlet that processes payment transactions by validating credit card details against the database. If valid, it proceeds to handle sales transactions, responding with JSON indicating the success or failure of the payment.

- SessionServlet.java: A Java servlet for managing user session state by storing and retrieving search filters and pagination settings. It saves session data via POST requests and loads it via GET requests, responding with JSON format.

- User.java: A simple Java class representing a user with a username field. It can be extended to include additional attributes like shopping cart items.


### Frontend:
- **index.html**: The main HTML file that imports **jQuery**, **Bootstrap**, and the `index.js` script. It also provides the skeleton structure for the movie table.

- **index.js**: The primary JavaScript file that sends an HTTP GET request to `MovieListServlet` and populates the movie table using the returned data.

- **single-star.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-star.js`. It contains the initial structure for displaying movie information related to a star.

- **single-star.js**: JavaScript file that sends an HTTP GET request to `SingleStarServlet` and fills the table with movie details related to the star.

- **single-movie.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-movie.js`. It structures the data for stars and genres of a specific movie.

- **single-movie.js**: JavaScript file that sends an HTTP GET request to `SingleMovieServlet` and populates the table with star and genre information of a movie.

- Add-to-cart.js: A JavaScript file that manages the shopping cart functionality. Persist the cart data across sessions, handles adding movies to the cart with a random price, and updates the cart when the "Add to Cart" button is clicked.

- confirmation.html: A purchase confirmation page that displays order details using data from localStorage. It includes movie information, total price, and navigation buttons to the homepage and login page.

- login.html: A login page with a centered form, featuring a gradient background, where users enter credentials validated through login.js.

- login.js: A JavaScript file that handles user login by submitting credentials via a POST request to LoginServlet, validating the response, and redirecting to the homepage on success or displaying error messages on failure.

- payment.html: A payment page featuring a form for entering credit card details, displaying the total cost, with navigation links for home, shopping cart, and logout actions, styled using Bootstrap and FontAwesome.

- payment.js: A JavaScript file that calculates the total cart price, collects payment details from a form, and sends the data to the backend for validation, redirecting to a confirmation page on success or displaying error messages on failure.

- shopping-cart.html: A shopping cart page displaying a list of selected movies with quantities, prices, and a total cost, allowing users to update the cart and proceed to payment, enhanced with Bootstrap and FontAwesome for styling and navigation.

- shopping-cart.js: A JavaScript file that manages and displays the shopping cart, allowing users to adjust quantities, delete items, and view the total price, with actions saved in localStorage and an option to proceed to the payment page.

---

## Substring Matching Using LIKE Predicate
We have implemented substring matching in our application to allow customers to search for movies using partial matches for the title, director’s name, and star’s name. This functionality is achieved using the `LIKE` predicate in our SQL queries.

### How and Where We Use LIKE:
- **Title Search**: We use the `LIKE` operator to enable substring matching on the movie title. This allows users to search for movies that contain the keyword anywhere in the title. For example, searching for “Term” will match movies like “Terminator” and “The Terminal”.
- **Director Search**: For director searches, we use `LIKE` to match directors whose names contain the provided keyword. This makes it easier for users to find movies directed by a specific director even if they only know part of the name.
- **Star’s Name Search**: When searching by a star’s name, we use `LIKE` to enable substring matching on the star’s name. Users can find movies featuring actors or actresses based on partial name information.

### Usage Details:
- **Wildcard Characters**: We utilize the `%` wildcard character with the `LIKE` operator to represent zero or more characters in the search pattern. This allows for flexible matching of search keywords within the text fields.
- **Case Sensitivity**: Our database is configured such that the `LIKE` operator is case-insensitive for string comparisons. This means that searches are not sensitive to the case of the input, improving user experience.

---

## Key Technologies Used:
- **Java**: Core backend logic.
- **JavaScript**: Frontend interactions and dynamic content generation.
- **Apache Tomcat**: Web server for deploying servlets.
- **Maven**: Project management and dependency handling.
- **MySQL**: Database management.
- **AWS**: Cloud hosting for scalable and accessible deployment.

---

This project demonstrates a full-stack approach with a focus on seamless integration between the frontend and backend, powered by efficient cloud deployment.
