# Team **MicroHard**

## Developed by:
- Lucas (Kaixiang) Zhuang, Student ID: 73969468
- Betty (Jiatong) Liu, Student ID: 51549174

---

## Video Explanation
[Watch the video on YouTube](coming soon)  

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

## Key Technologies Used:
- **Java**: Core backend logic.
- **JavaScript**: Frontend interactions and dynamic content generation.
- **Apache Tomcat**: Web server for deploying servlets.
- **Maven**: Project management and dependency handling.
- **MySQL**: Database management.
- **AWS**: Cloud hosting for scalable and accessible deployment.

---

This project demonstrates a full-stack approach with a focus on seamless integration between the frontend and backend, powered by efficient cloud deployment.
