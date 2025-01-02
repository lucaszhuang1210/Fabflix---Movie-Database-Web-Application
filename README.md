# Team **MicroHard**

## Developed by:
- Lucas (Kaixiang) Zhuang
- Betty (Jiatong) Liu

---

## Video Explanation
[Watch my videos on YouTube](https://www.youtube.com/@lucaszhuang1478)  

---

# Fabflix Web Application Overview

This project is a **scalable web-based application** deployed on **AWS**, designed for secure, high-performance data processing and seamless user engagement. It is developed in **Java** for backend logic and **JavaScript** for dynamic frontend functionality, hosted on an **Apache Tomcat** server. **Maven** manages dependencies, while a **MySQL database** provides reliable structured data storage.

To ensure scalability and high availability, the application leverages **AWS Cloud Services**, including an **Elastic Load Balancer (ELB)** for traffic distribution and **Auto-Scaling Groups** for dynamic scaling. **MySQL Master-Slave Replication** improves read performance and fault tolerance, while **MySQL and Tomcat connection pooling** optimizes resource utilization and supports high concurrency. Advanced **full-text search** and **autocomplete functionality** enhance user experience with fast, accurate results.

The application is containerized with **Docker**, enabling portability and consistent deployment. It is further deployed to a **Kubernetes cluster(K8s)**, managed with **moviedb.yaml**, **ingress.yaml**, and **context.xml**, ensuring seamless orchestration of resources and high availability. Performance testing was conducted using **JMeter** to evaluate the scalability of the search feature under various load configurations, with throughput peaking at **11,137.428 requests per minute**.

Optimized **XML parsers** (ActorParser, MovieParser, CastParser) efficiently process large datasets using in-memory caching and auto-ID generation, minimizing database queries and accelerating ingestion. Security is ensured through **reCAPTCHA**, **encrypted password storage**, and **HTTPS** for secure data transmission.

An **Employee Dashboard** powered by stored procedures provides tools for metadata display, star and movie insertion, and restricted-access filtering for authorized users. This full-stack application integrates frontend, backend, and database components into a secure, scalable, and interactive platform.

---

# Project Milestones

## Project 5:

- **Lucas:** Built and ran the Fabflix application in a Docker container, wrote and managed a `Dockerfile`, configured and executed performance testing for the Fabflix search feature using JMeter with a custom `JMX` file, and analyzed results using the Graph Results Listener.
- **Betty:**  Set up a Kubernetes (K8s) cluster on AWS, Deploy Fabflix to a Kubernetes (K8s) cluster on AWS with `moviedb.yaml` and `ingress.yaml`, `context.xml`.

### To Run JMeter on Fabflix:
```
./bin/jmeter -n -t fabflix_test.jmx
```

## **Performance Testing Results**

### **Cluster Configurations**
Both configurations share:

**1 Control Plane Node** + **1 Master MySQL Pod** + **1 Slave MySQL Pod**
 
### **Throughput Numbers**
| Configuration             | Throughput (req/min) |
|---------------------------|----------------------|
| 3 Worker Nodes, 2 Fabflix Pods | 10,500.853 req/min     |
| 4 Worker Nodes, 3 Fabflix Pods | 11,137.428 req/min     |

<img width="1171" alt="Screenshot 2024-12-08 at 18 01 11" src="https://github.com/user-attachments/assets/87f1877b-af93-48e7-ac11-67fda81016d8">

---

## Project 4:

- **Lucas:** Scaling Fabflix with a cluster of MySQL/Tomcat and Elastic Load Balancer (ELB), MySQL/Tomcat connection pooling, MySQL full-text search index, autocomplete searching
- **Betty:** JDBC Connection Pooling, setting up MySQL Master-Slave Replication

### Connection Pooling

#### Servlets
All servlets using JDBC Connection Pooling are located in the `/api/src/` directory:

- **Admin Servlets**:
  - `EmployeeLoginServlet.java`, `AddMovieServlet.java`, `AddStarServlet.java`

- **Core Feature Servlets**:
  - `MovieListServlet.java`, `SingleMovieServlet.java`, `SingleStarServlet.java`
  - `LoginServlet.java`, `PaymentServlet.java`, `MetadataServlet.java`
  - `GenreServlet.java`, `TitleAutoComplete.java`
  
- **Parser Servlets**:
  - `ActorParser.java`, `CastParser.java`, `MovieParser.java`, `ParseXMLFileAndInsertToDatabase.java`
  
### How is Connection Pooling utilized in Fabflix:
In Fabflix, connection pooling is leveraged to optimize database access efficiency and scalability. When a servlet or any Java class responsible for database operations is invoked, it does not create a new database connection outright. Instead, it requests a connection from the pool, which is an instance of `javax.sql.DataSource` managed by Tomcat's connection pool. This pool is defined in the context.xml file with parameters like `maxTotal`, `maxIdle`, `maxWaitMillis`, ensuring that the system can handle multiple simultaneous database requests without latency or overhead of establishing new connections. The servlet uses this connection to execute SQL commands and then returns it to the pool when the operations are complete, thus making it available for subsequent requests.

- **Master Server:** Handles all write operations, including `INSERT`, `UPDATE`, and `DELETE` with a dedicated connection pool to centralize data modifications, ensuring consistency and integrity. Changes made on the master are replicated to the slave server.
- **Slave Server:** Optimized for handling read operations such as `SELECT` queries, handling high-volume queries and reducing load on the master.

This setup improves scalability, availability, and resource management, ensuring the system performs well under heavy usage. Connection pooling reduces the overhead of opening and closing database connections, ensuring quicker response times. It also balances the workload, as the slave server offloads most read tasks, while the master focuses on writes. 
 
## Project 3:

- **Lucas:** Developed and optimized XML parsers (`ActorParser`, `MovieParser`, `CastParser`, `ParseXMLFileAndInsertToDatabase`) with in-memory caching and ID generation, documented an optimization report, and deployed the application on AWS with HTTPS.
- **Betty:** Adding reCAPTCHA, Use Encrypted Password, an Employee Dashboard using Stored Procedure.

### Inconsistency Data Report
The [Inconsistency Data Report](https://github.com/lucaszhuang1210/Fabflix---Movie-Database-Web-Application/blob/main/inconsistency_entries.txt) captures data entries from the Stanford Movies dataset that did not meet the required criteria for inclusion in the database. These inconsistencies include missing critical fields or invalid entries that could compromise data integrity. Examples include Incomplete movie entries (e.g., missing title or ID), duplicate actor records, etc. View the dataset in `data-files` branch: [stanford-movies](https://github.com/lucaszhuang1210/Fabflix---Movie-Database-Web-Application/tree/data-files/stanford-movies).

## Files with Prepared Statements Updated
- ActorParser.java
- CastParser.java
- MovieParser.java
- AddMovieServlet.java
- AddStarServlet.java
- EmployeeLoginServlet.java
- GenreServlet.java
- LoginServlet.java
- MetadataServlet.java
- MovieListServlet.java
- PaymentServlet.java
- SingleMovieServlet.java
- SingleStarServlet.java

## Project 2:

- **Lucas:** Implement the **Search and Browse** feature on the Main Page, allowing users to Navigate seamlessly between Genres and Characters, Filter results using both search and browse options, with **Dynamic content updates** as filters are applied, giving users immediate feedback.
- **Betty:** Implement Dynamic Shopping Cart, Checkout function, Order confirmation, and Login Page.

## project 1:

- **Lucas:** Debugged and updated the Single Movie page, wrote the `README.md`, created the `moviedb` database, set up AWS, and recorded video demonstrations.
- **Betty:** Implement Movie List Page (`MovieListServlet`, `index.html`, and `index.js`), Single Stars Page (`SingleStarServlet`, `single-star.html`, and `single-star.js`), Single Movie (`SingleMovieServlet`, `single-movie.html`, and `single-movie.js`) Page, created the `style.css`, fixed Maven packages setup, and assisted with AWS instance setup.

---

## Application Components

### Backend:
- **MovieListServlet.java**: A Java servlet that interacts with the database to retrieve a list of movies. It responds with movie data in **JSON format**. Movie names are generated as links to the **Single Movie** page, and star names link to the **Single Star** page.
- **SingleStarServlet.java**: Retrieves information about a specific star and all movies the star has acted in, returning the results in **JSON format**.

- **SingleMovieServlet.java**: Retrieves information about a specific movie, including its stars and genres, and returns this data in **JSON format**.
  
- **LoginFilter.java**: A Java servlet filter that intercepts all incoming requests, allowing access to certain URLs without login while redirecting unauthorized users to the login page if they are not logged in.

- **LoginServlet.java**: A Java servlet that handles user login by verifying credentials against the database. It responds with a JSON object indicating success or failure and manages session creation upon successful login.

- **PaymentServlet.java**: A Java servlet that processes payment transactions by validating credit card details against the database. If valid, it proceeds to handle sales transactions, responding with JSON indicating the success or failure of the payment.

- **SessionServlet.java**: A Java servlet for managing user session state by storing and retrieving search filters and pagination settings. It saves session data via POST requests and loads it via GET requests, responding with JSON format.

- **User.java**: A simple Java class representing a user with a username field. It can be extended to include additional attributes like shopping cart items.

- **AddMovieServlet.java**: A servlet that handles the addition of a new movie to the database by checking for duplicates, calling a stored procedure to insert data, and returning success or error messages in JSON format.
  
- **AddStarServlet.java**: A servlet that adds a new star to the database by generating a unique ID, inserting the star’s name and optional birth year, and returning a JSON response indicating success or error.
  
- **EmployeeLoginServlet.java**: A servlet handling employee login by validating credentials against encrypted passwords in the database, verifying reCAPTCHA, and returning a JSON response with success or failure status.

- **GenreServlet.java**: A servlet that retrieves all genres from the database, returning the list as a JSON array sorted by name for use in the application.
  
- **RecaptchaConstants.java**: A class that stores the secret key for Google reCAPTCHA verification, used to validate user interactions on protected pages.
  
- **RecaptchaVerifyUtils.java**: A utility class that verifies Google reCAPTCHA responses by sending a POST request to the reCAPTCHA API and parsing the JSON response to confirm successful validation.

**Under XMLParser package**:

- **ActorParser.java**: An XML parser for reading actor data, validating uniqueness, handling errors, and inserting actors into the database with a unique ID, using SAX parsing and batch commits for efficiency.

- **CastParser.java**: An XML parser that processes cast data by linking actors to movies, checking for duplicates, handling missing entries, and managing batch inserts to the database for optimized performance.

- **MovieParser.java**: An XML parser that processes movie data, including title, director, year, and genres, and inserts movies and genre associations into the database, handling errors and avoiding duplicate entries with cache checks.

- **ParseXMLFileAndInsertToDatabase.java**: A program that initializes and runs XML parsers for actors, movies, and cast data, inserting the parsed data into a database and logging any inconsistencies to a specified error file.

- **XMLParser.java**: An interface that defines a `parse` method for parsing XML files, to be implemented by classes that handle specific XML data structures.


### Frontend:
- **index.html**: The main HTML file that imports **jQuery**, **Bootstrap**, and the `index.js` script. It also provides the skeleton structure for the movie table.

- **index.js**: The primary JavaScript file that sends an HTTP GET request to `MovieListServlet` and populates the movie table using the returned data.

- **single-star.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-star.js`. It contains the initial structure for displaying movie information related to a star.

- **single-star.js**: JavaScript file that sends an HTTP GET request to `SingleStarServlet` and fills the table with movie details related to the star.

- **single-movie.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-movie.js`. It structures the data for stars and genres of a specific movie.

- **single-movie.js**: JavaScript file that sends an HTTP GET request to `SingleMovieServlet` and populates the table with star and genre information of a movie.

- **Add-to-cart.js**: A JavaScript file that manages the shopping cart functionality. It persists the cart data across sessions, handles adding movies to the cart with a random price, and updates the cart when the "Add to Cart" button is clicked.

- **confirmation.html**: A purchase confirmation page that displays order details using data from localStorage. It includes movie information, total price, and navigation buttons to the homepage and login page.

- **login.html**: A login page with a centered form, featuring a gradient background, where users enter credentials validated through `login.js`.

- **login.js**: A JavaScript file that handles user login by submitting credentials via a POST request to `LoginServlet`, validating the response, and redirecting to the homepage on success or displaying error messages on failure.

- **payment.html**: A payment page featuring a form for entering credit card details, displaying the total cost, with navigation links for home, shopping cart, and logout actions, styled using Bootstrap and FontAwesome.

- **payment.js**: A JavaScript file that calculates the total cart price, collects payment details from a form, and sends the data to the backend for validation, redirecting to a confirmation page on success or displaying error messages on failure.

- **shopping-cart.html**: A shopping cart page displaying a list of selected movies with quantities, prices, and a total cost, allowing users to update the cart and proceed to payment, enhanced with Bootstrap and FontAwesome for styling and navigation.

- **shopping-cart.js**: A JavaScript file that manages and displays the shopping cart, allowing users to adjust quantities, delete items, and view the total price, with actions saved in localStorage and an option to proceed to the payment page.

Under _dashboard folder:

- **add-a-star.html**: A page with a form to add a new star's details, featuring a Bootstrap navbar, input fields for star name and birth year, and links to external CSS, Bootstrap, and FontAwesome for styling.

- **add-a-star.js**: JavaScript file that handles form submission for adding a new star, sending data to the backend via an AJAX POST request, and providing feedback based on the response.
  
- **add-movie.html**: A page with a form to add a new movie, including fields for title, year, director, star, and genre, styled with Bootstrap and featuring a navbar for navigation.
  
- **add-movie.js**: JavaScript file that handles form submission for adding a new movie, sends data to the backend via an AJAX POST request, and displays success or error messages based on the response.
  
- **login.html**: A login page for employees with a soft green gradient background, a centered login form, Google reCAPTCHA for security, and JavaScript for form handling and error display.
  
- **login.js**: JavaScript file that handles employee login by submitting credentials and reCAPTCHA via AJAX to `LoginServlet`, displaying error messages on failure or redirecting to the metadata dashboard on success.

- **metadata.html**: A dashboard displaying database metadata in table format with a Bootstrap-styled navbar, featuring sections for table attributes and types, populated dynamically via AJAX.

- **metadata.js**: JavaScript file that fetches and displays database metadata in tables by making an AJAX GET request, updating the dashboard with attributes and data types or showing an error message on failure.

- **styles.css**: CSS file providing a clean, professional style with a light background, centered headers, styled tables and forms, and a responsive layout for the database metadata dashboard and other pages.

---

# Optimization Report

Our XML parsing program processes large files with multiple entries, requiring numerous database operations that can lead to performance bottlenecks. To optimize the program’s runtime, we implemented the following enhancements:

## Caching Existing Database Entries

**Description**: At the start of each parser (e.g., `ActorParser`, `MovieParser`, `CastParser`), we load relevant database entries into in-memory data structures like HashMaps and HashSets. For example, we cache existing movie titles, actor names, and genres, enabling fast lookups to prevent redundant database queries.

**Implementation**: Each parser queries the database for existing entries (e.g., all movie titles in `MovieParser`) and stores them in a HashMap or HashSet. This allows the program to quickly check for duplicates in memory rather than querying the database each time.

**Expected Improvement**: This approach reduces database access for duplicate checking, improving runtime by up to **40%** for large datasets by eliminating redundant queries.

## Maintaining Last Generated IDs in Memory

**Description**: To add new records (e.g., stars, genres) with unique IDs, we avoid querying the database repeatedly for the current maximum ID. Instead, we retrieve the current maximum ID once at startup and maintain this last generated ID in memory, incrementing it for each new entry.

**Implementation**: During initialization, the program fetches the maximum ID for each entity (e.g., stars, genres) and stores it in memory. New IDs are generated by incrementing this in-memory ID, removing the need for frequent database lookups.

**Expected Improvement**: This reduces runtime by an estimated **20-30%** by avoiding frequent maximum ID lookups in the database, particularly useful for large datasets with many new records.

## Summary of Optimizations and Impact

By caching existing entries and maintaining last generated IDs in memory, we have minimized the need for repeated database queries. These optimizations reduce program runtime by approximately **50%** or more, especially beneficial for processing large XML files. This streamlines database interactions, significantly enhancing the program’s performance.

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
- **Java**: Core backend logic for processing and database interactions.
- **JavaScript**: Enables frontend interactions and dynamic content generation.
- **HTML**: Structuring the web content and interfaces.
- **Apache Tomcat**: Web server for deploying servlets and handling HTTP requests.
- **Maven**: Manages project dependencies and builds.
- **MySQL**: Manages the database for storing and retrieving data.
- **AWS**: Provides cloud hosting for scalable and accessible deployment.
- **reCAPTCHA**: Enhances security by verifying users to prevent bot access.
- **XML File Parser**: Processes XML files for data extraction and transformation.

---

This project demonstrates a full-stack approach with a focus on seamless integration between the frontend and backend, powered by efficient cloud deployment.
