# Team **MicroHard**

## Developed by:
- Lucas (Kaixiang) Zhuang, Student ID: 73969468
- Betty (Jiatong) Liu, Student ID: 51549174

---

# Team Contributions

We collaborated on setting up the environment (Tasks 1-5) and worked together on the Movie List Page (`MovieListServlet`, `index.html`, and `index.js`).

## Specific Contributions:

- **Lucas:** Debugged and updated the Single Movie page, wrote the `README.md`, created the `moviedb` database, set up AWS, and recorded video demonstrations.
- **Betty:** Developed Single Star and Single Movie features, created the `style.css`, fixed Maven packages setup, and assisted with AWS instance setup.

---

## Video Explanation
[Watch the video on YouTube](https://www.youtube.com/watch?v=9H6ypb_tpBI)  

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

### Frontend:
- **index.html**: The main HTML file that imports **jQuery**, **Bootstrap**, and the `index.js` script. It also provides the skeleton structure for the movie table.

- **index.js**: The primary JavaScript file that sends an HTTP GET request to `MovieListServlet` and populates the movie table using the returned data.

- **single-star.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-star.js`. It contains the initial structure for displaying movie information related to a star.

- **single-star.js**: JavaScript file that sends an HTTP GET request to `SingleStarServlet` and fills the table with movie details related to the star.

- **single-movie.html**: HTML file that imports **jQuery**, **Bootstrap**, and `single-movie.js`. It structures the data for stars and genres of a specific movie.

- **single-movie.js**: JavaScript file that sends an HTTP GET request to `SingleMovieServlet` and populates the table with star and genre information of a movie.

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
