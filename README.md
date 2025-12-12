# Getting Started

### Assumptions
* Java 17 is installed and JAVA_HOME environment variable is set
* all commands are run under unix shell (Git Bash, Mac terminal, etc)
* Database integration not built as per requirement, in-memory data structures are used to store parking data
* RuntimeException is thrown for error scenarios, can be improved with custom exceptions and global exception handler

### Building a Spring Boot Application with Gradle
```
./gradlew clean test bootJar
```

### Running the Application
```
java -jar build/libs/parking-0.0.1-SNAPSHOT.jar
```

### Swagger UI

http://localhost:8080/swagger-ui/index.html

