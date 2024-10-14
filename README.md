Team MicroHard

members: 

Lucas(Kaixiang) Zhuang, Student ID: 73969468

Betty(Jiatong) Liu, Student ID: 51549174

Video Explaination

Youtube video URL: xxx

Brief Explanation

MovieListServlet.java is a Java servlet that talks to the database and get the movie. It returns a list of movie in the JSON format. The name of star is generated as a link to Single Star page.  The name of movie is generated as a link to Single movie page.

index.js is the main Javascript file that initiates an HTTP GET request to the MovieListServlet. After the response is returned, index.js populates the table using the data it gets.

index.html is the main HTML file that imports jQuery, Bootstrap, and index.js. It also contains the initial skeleton for the table.

SingleStarServlet.java is a Java servlet that talks to the database and get information about one Star and all the movie this Star performed. It returns a list of Movies in the JSON format.

single-star.js is the Javascript file that initiates an HTTP GET request to the SingleStarServlet. After the response is returned, single-star.js populates the table using the data it gets.

single-star.html is the HTML file that imports jQuery, Bootstrap, and single-star.js. It also contains the initial skeleton for the movies table.

SingleMovieServlet.java is a Java servlet that talks to the database and get information about one Movie and all the stars and genres this movie performed. It returns a list of star and genres in the JSON format.

single-movie.js is the Javascript file that initiates an HTTP GET request to the SingleMovieServlet. After the response is returned, single-movie.js populates the table using the data it gets.

single-movie.html is the HTML file that imports jQuery, Bootstrap, and single-movie.js. It also contains the initial skeleton for the movies table.
