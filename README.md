# Team **MicroHard** Dockerized-Branch

This branch is a **Dockerized branch**, designed to run the application in a Docker container. It requires a **database with no password encryption** for compatibility in the Dockerized environment.

The main differences between the **Dockerized-branch** and **local-branch** are:
1. **No reCAPTCHA**: The reCAPTCHA feature has been removed.
2. **No Password Encryption Filter**: Password encryption is not used in this branch.
3. **No HTTPS Requirement**: The application does not enforce HTTPS in this branch.

The database connection configuration in the file `/api/WebContent/META-INF/context.xml` is as follows:

```
url="jdbc:mysql://host.docker.internal:3306/moviedb?autoReconnect=true&amp;allowPublicKeyRetrieval=true&amp;useSSL=false"
```

---

## Docker Commands for the Dockerized Branch

### Build the Docker Image
```
sudo docker build . --platform linux/amd64 -t lucaszhuang1210/fabflix:v3
```

### Push the Docker Image to a Repository
```
sudo docker push lucaszhuang1210/fabflix:v3
```

### Pull the Docker Image from the Repository
```
sudo docker pull lucaszhuang1210/fabflix:v3
```

### Run the Docker Container
```
sudo docker run --add-host host.docker.internal:host-gateway -p 8080:8080 lucaszhuang1210/fabflix:v3
```

---

By following these steps, the Dockerized branch can be built, pushed, pulled, and run in a Dockerized environment seamlessly.

---

## Video Explanation
[Watch the video on YouTube](https://www.youtube.com/@lucaszhuang1478)  

---

## Project Overview

This project is a **scalable web-based application** deployed on **AWS**, designed for secure, high-performance data processing and seamless user engagement. It is developed in **Java** for backend logic and **JavaScript** for dynamic frontend functionality, hosted on an **Apache Tomcat** server. **Maven** manages dependencies, while a **MySQL database** provides reliable structured data storage.

To ensure scalability and high availability, the application leverages **AWS Cloud Services**, including an **Elastic Load Balancer (ELB)** for traffic distribution and **Auto-Scaling Groups** for dynamic scaling. **MySQL Master-Slave Replication** improves read performance and fault tolerance, while **MySQL and Tomcat connection pooling** optimizes resource utilization and supports high concurrency. Advanced **full-text search** and **autocomplete functionality** enhance user experience with fast, accurate results.

Optimized **XML parsers** (ActorParser, MovieParser, CastParser) efficiently process large datasets using in-memory caching and auto-ID generation, minimizing database queries and accelerating ingestion. Security is ensured through **reCAPTCHA**, **encrypted password storage**, and **HTTPS** for secure data transmission.

An **Employee Dashboard** powered by stored procedures provides tools for metadata display, star and movie insertion, and restricted-access filtering for authorized users. This full-stack application integrates frontend, backend, and database components into a secure, scalable, and interactive platform.

---
