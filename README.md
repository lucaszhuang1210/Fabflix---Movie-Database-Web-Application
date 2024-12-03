# Team **MicroHard Data-Files**

## Developed by:
- Lucas (Kaixiang) Zhuang, Student ID: 73969468
- Betty (Jiatong) Liu, Student ID: 51549174

---



# Data File Branches

## Developed by:
- Lucas (Kaixiang) Zhuang, Student ID: 73969468
- Betty (Jiatong) Liu, Student ID: 51549174

---

## Video Explanation
[Watch the video on YouTube](https://www.youtube.com/@lucaszhuang1478)  

---

This directory contains all necessary data files, including SQL files for building the `moviedb` database and inserting data into it. Additionally, it includes `stanford-movies` data in XML format, which should be used with the files in `api/src/XMLParser`.

---

## Instructions to Build the Database

### Log into MySQL:
- **Mac**:  
  `/usr/local/mysql/bin/mysql -u mytestuser -p`
- **Linux/Unix**:  
  `mysql -u mytestuser -p`

### Steps:
1. **Create Schema in Database:**  
   Run: `SOURCE createtable.sql`

2. **Insert Data into Database:**  
   Option 1:  
   Run: `SOURCE movie-data.sql`  
   Option 2 (via terminal):
   Run: `mysql -u mytestuser -p –database=moviedb –force < movie-data.sql`

3. **Compile Stored Procedure:**  
Run: `SOURCE stored-procedure.sql`

4. **Insert Employee Data:**  
Run: `SOURCE employee-data.sql`

5. **Encrypt Customer Password:**  
Navigate to: `PasswordsEncryption/cs122b-project3-encryption-example-main`  
Execute:  `mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass=“UpdateSecurePassword”

            mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass=“VerifyPassword”`

6. **Encrypt Employee Password:**  
Navigate to: `PasswordsEncryption/employeeEcryPassword`  
Execute:  `mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass=“UpdateSecurePassword”`

7. **Verify Data Insertion:**  
Use the following command to check the number of records in the database:
`mysql -u mytestuser -p -e “use moviedb;select count() from stars; select count() from movies;”`

---

## Deploy Web Application on AWS Instance

1. **Remove Current `.war` File:**
   `sudo rm /var/lib/tomcat10/webapps/api.war`

2. **Build the `.war` File:**  
Navigate to the directory where `pom.xml` is located and execute:
    `mvn package`

3. **Copy the `.war` File to Tomcat:**
   `ls -lah /var/lib/tomcat10/webapps/
sudo cp ./target/*.war /var/lib/tomcat10/webapps/`

---

## Run XMLParser

1. Navigate to the `api` directory where `pom.xml` is present. 
2. Execute the following commands:
   `mvn compile
mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass=“XMLParser.ParseXMLFileAndInsertToDatabase”`

---

## MySQL Credentials

- **Username:** `mytestuser`  
- **Password:** `My6$Password`

---

## Keystone Restore
`CN=L, OU=UCI, O=UCI, L=Irvine, ST=California, C=US`

---

This project demonstrates a full-stack approach with a focus on seamless integration between the frontend and backend, powered by efficient cloud deployment.
