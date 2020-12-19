# Quiz Master Back-end

This API was created as a University Project for Software technologies subject. The project is an MVP (Minimal Viable Product) which was created to be flexible in order to make it easy to implement further improvements. (Like questions, where answers could be Images)
QuizMaster was a fun coding project with the goal of getting to know as many technologies as possible.

The project was created by four Students:
```
Gábor Vitrai 
Pascal Tippe
Victor Schmit
Márk Dobó
```

### The code was uploaded to github as a reference.
Some of the interesting technologies and solutions we used during the project:
- MongoDB
- STOMP Endpoints (Sockets)
- REST Endpoints (HTTP)
- Google Authentication (JWT Authentication Filter)
- Gitlab.com CI
- MockMCV (Testing)
- Custom JsonDeserializer
- Spring Boot / Angular CLI

## Setting up:
To use it, a MongoDB connection has to be set up in the application.properties file.
For testing a TestQuiz is recommended to be created, with the example test JSON files from the test/resources folder.

## Running:
Back-end: Gradle Spring Project (e.g.: CMD: gradle build)
Front-end: Angular project (e.g.: CMD: ng serve / npm start)
