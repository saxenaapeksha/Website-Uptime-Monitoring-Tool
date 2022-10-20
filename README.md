# wup
## _Website Uptime Monitoring Tool v1.1_

WUP a monitoring tool that can track uptime of any web site.
Built in Java 11, a spring boot dockerised application.

- MVP Version
- v1.1
## Features

- Register a new check.
- View all existing checks.
- Filter a check either by frequency or name.
- Activate a check.
- Deactivate a check.
- View website status, uptime/downtime and response time.
- User gets notified via a message if application down.

## Tech

Wup uses below tech stack to work properly:

- [Java 11] - Java to write application backend services.
- [Springboot 2.7.3] - Springboot framework for Web application development.
- [Rest API] - Uses Rest API architectural style
- [Postgres DB] - Postgres DB to provide schema/storage support
- [JPA] - JPA ORM to develop JPL query.
- [Postman] - Postman Collection of Rest API's which define the wup application.
- [Gradle 7.5] - Build Automation tool.

## How to run :

1. Unzip **wup.zip**
2. On your terminal, go to application folder using `$cd wup/docker`
3. To start the application run on terminal `$docker-compose up`

## Postman Collection Link
1. https://www.getpostman.com/collections/8b688f1ea1dedcbc3177

## API Schema
1. Register New Check : http://localhost:8080/check/register
Request : Method.POST
`{
   "name": "hackerrank",
   "websiteURL": "https://www.hackerrank.com/",
   "frequency": 1,
   "frequencyUnit": "minute"
   }`

2. View All Checks : http://localhost:8080/check/all
   (Method.GET Request)

3. Filter Check By Frequency : http://localhost:8080/check/filter?frequency=1minute&name=mail
or http://localhost:8080/check/filter?frequency=1minute&name=null
   (Method.GET Request)
   Format of frequency : <Number><Minute/Hour> 
   Sample example for frequency : 1minute or 1 hour.


4. Filter Check By Name : http://localhost:8080/check/filter?frequency=null&name=mail
   (Method.GET Request)

5. Activate a check : http://localhost:8080/check/activate?id=1
   (Method.GET Request)

6. Deactivate a check : http://localhost:8080/check/deactivate?id=1
   (Method.GET Request)

7. To view status of a website, UpTime/DownTime, Response Time:http://localhost:8080/website/status?websiteURL=https://www.facebook.com/
   (Method.GET Request)

8. Email and maxFailedAttempt checks are maintained at DB level per check , it can be configured exposed to user when required.