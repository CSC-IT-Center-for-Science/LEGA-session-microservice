# session-microservice

to build a package

mvn -DargLine="-Dspring.profiles.active=dev" clean compile package install

to run it

java -jar session-service.jar --spring.profiles.active=dev 

the profile will make the application to select the application-dev.properties file
