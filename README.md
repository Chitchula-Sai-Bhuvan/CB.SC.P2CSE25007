# Notification System Project

This is my java spring boot project for notification management. It handles sending notifications and tracking them.

### Features
* java 17 and spring boot used
* h2 database so no need to setup mysql
* spring data jpa

### How to start
* open folder in IDE
* run the main class NotificationApplication.java
* server will start at 8080

### Endpoints
* POST `/notifications` - to send notification
* GET `/notifications?userId=1` - get users unread notifications
* PATCH `/notifications/{id}/read?userId=1` - mark as read
* DELETE `/notifications/{id}?userId=1` - delete notification

### DB Info
h2 console is at http://localhost:8080/h2-console
url is `jdbc:h2:mem:notificationdb`
user: sa
pass: password
