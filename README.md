# Mancala Game

This is a two-player Mancala game implemented in Java using the Spring Boot framework, MongoDB for data storage, and
REST APIs for game interactions.

## Overview

Mancala is a traditional two-player strategy game, which is fun to play and can be enjoyed by people of all ages. In
this implementation, we provide a digital version of the game with a user-friendly interface, powered by Spring Boot and
MongoDB.

## Features

- Play the Mancala game against another player through REST APIs.
- Customizable game settings, such as the number of stones per pit and more.
- Detailed game statistics, including the number of wins, losses, and draws.
- Responsive web-based interface for easy access from different devices.
- Persistent game data stored in MongoDB for later retrieval and analysis.
- Provides a fun and challenging gaming experience for two players.

## Technologies Used

- Java
- Spring Boot
- Spring MVC
- Spring Data MongoDB
- MongoDB (for data storage)
- REST API for game interactions

## Installation

1. Clone this repository to your local machine.

```
git clone https://github.com/abbasjafari1991/Mancala.git
```

2. Navigate to the project directory.

```
cd mancala
```

3. Configure the application(*).yaml file to connect to your MongoDB database. Update the database URL and credentials.

- application.properties and application.yaml -> for general config
- application-dev.yaml for dev environment
- application-prod.yaml for production environment

4. Build and run the application using Maven.

```
mvn spring-boot:run
```

## API Documentation

You can interact with the Mancala game through REST APIs. Full API documentation is available
at `http://localhost:8080/swagger-ui.html` in development environment.

odify this README to suit your specific project and provide more details as needed. Additionally, include any special
instructions or dependencies required to run your two-player Mancala game with Spring Boot, MongoDB, and REST API.

## Things to Do

This section is a task list for future project improvements and enhancements. It can serve as a roadmap for the
project's development.

- config Production profile and add Service Discovery
- Add cash management features.
- Implement custom exception handling.
- Expand the test coverage, especially for player-related functionality.
- Consider creating interfaces for services to allow for other implementations.
- Integrate Spring Security for enhanced security features.
