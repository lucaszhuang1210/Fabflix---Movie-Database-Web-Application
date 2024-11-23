## CS 122B Project 5 Tomcat Pooling example

This example shows how to use Connection Pooling with JDBC.

### Before running the example

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

- login to mysql as a root user
   ```
   local> mysql -u root -p
   ```

- create a test user and grant privileges:
   ```
   mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'My6$Password';
   mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
   mysql> quit;
   ```

#### prepare the database `moviedbexample`


```
local> mysql -u mytestuser -p
mysql> CREATE DATABASE IF NOT EXISTS moviedbexample;
mysql> USE moviedbexample;
mysql> CREATE TABLE IF NOT EXISTS stars(
               id varchar(10) primary key,
               name varchar(100) not null,
               birthYear integer
           );

mysql> INSERT IGNORE INTO stars VALUES('755011', 'Arnold Schwarzeneggar', 1947);
mysql> INSERT IGNORE INTO stars VALUES('755017', 'Eddie Murphy', 1961);

mysql> quit;
```

### To run this example:

1. Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) ->
   Choose Import project from external model -> Click on Finish -> The IntelliJ will load automatically
2. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `My6$Password`.
3. Also make sure you have the `moviedbexample` database.
4. Add Tomcat local server Run/Debug Configuration.
5. Change the application context to `/cs122b-project5-TomcatPooling-example` at Edit Run/Debug Configurations
   for Tomcat -> Deployment.
6. Navigate to http://localhost:8080/cs122b-project5-TomcatPooling-example to see a list of stars.

### Brief Explanation

- `TomcatPoolingServlet.java` is a Java servlet that showcases connection pooling. The username and password of the
  MySQL is given to JDBC through a context.xml file. JDBC uses the credentials to create a connection pool. The servlet
  leases connections from this pool when needed and returns when the task is done.

- `META-INF/context.xml` includes important information about the database, username, password, and pooling
  configuration. Note that there are two lines added for the connection pooling configuration.
  ```              
  factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
  maxTotal="100" maxIdle="30" maxWaitMillis="10000"
  ```
  You would have to redeploy after changing this file. Otherwise, the Tomcat server may still use the
  previous context.xml. Check [this](http://tomcat.apache.org/tomcat-10.0-doc/jndi-datasource-examples-howto.html) for more information about how to configure Connection Pooling.

- Look at the following files for the main changes in this war file compared to the previous war file without Connection
  Pooling:
    - `/META-INF/context.xml`.
    - `/src/TomcatPoolingServlet.java`
    - `/WEB-INF/web.xml` (see the `resource-ref` tag)

