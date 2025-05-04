# Users Management Project

### Usability
This project is built for managing users of a system. There are some functionalities: 
- Authentication system;
- Role-Based Access Control (RBAC) to insure the users have the right permissions to access some endpoints of the API;
- A Backend for Frontend (BFF) which is developed using Angular.

### Technologies
- Java 21;
- Spring Boot 3;
- Spring Security;
- Postgresql.

### Installation
- Install JVM (Runtime);
- Clone the project;
- Run in your IDE;
- In a linux based terminal, run the following commands to generate RSA Keys:
  - ``cd user_manager/src/main/resources``
  - ``openssl genrsa``
  - ``openssl rsa -in app.key -pubout -out app.pub``
- Then, run the project using your IDE or the following command: ``./mvnw spring-boot:run``

### Author
Raí Rafael Santos Silva
Email: rairafaelss@gmail.com