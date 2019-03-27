# README #

### What is this repository for? ###

* Demo Spring Boot app for API authentication using basic auth protocol. -> See https://tools.ietf.org/html/rfc2617


### How do I get set up? ###

* Generate a token, encode it with SHA1 and put into properties file
* Call the API using Base 64 encoding of that token.
* Example: for token **test-token**: in properties file, we set value: **28e2bca89d8c60c5115a5b9e6663519ec2c9903c**, whereas the api is called with **basic dGVzdC10b2tlbg==**


* Build with maven
mvn package

* Run the jar in **dev** mode
java  -Dspring.profiles.active=dev  -jar target/cirrus-up-cloud-demo-auth-filter-1.0-SNAPSHOT.jar


### Curl Requests ###

* curl -X GET --header 'Authorization: basic dGVzdC10b2tlbg==' 'http://localhost:8080/hello' -> Hello world API
