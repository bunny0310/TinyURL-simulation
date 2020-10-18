# TinyURL-simulation
This is a microservice written in Java and Dropwizard that simulates the TinyURL service and allows users to generate base62 encoded shortened links for the URLs they provide.
The data is stored both in an LRU software cache as well as MongoDB. 

# Steps
1) Clone this repo and open it in an IDE of your choice
2) Spin up the MongoDB serverr on your local machine.
3) Update the database settings in the config.yml file
4) Create 2 collections in your MongoDB database - "urls" and "usersCollection"
5) Insert a user in the usersCollection with the format: {"username" : <USERNAME>, "password" : <PASSWORD>}
6) Open Postman and make a post request to localhost:8080/write with the following JSON body:
  {
    "longURL" : <YOUR_URL>
    "userToken" : <YOUR_USERNAME>@<YOUR_PASSWORD>
  }
7) Take note of the short URL generated.
8) Open a new browser window and key in localhost:8080/read/<YOUR_SHORT_URL>. You should be redirected to your original link.
  
# Algorithm and logistics
This service converts an integer to its base62 encoded form. This integer is derived from the record's id which increments by 1 every time a new record is pushed into the collection. Since id for every record is going to be unique, we can be rest assured that this service generates unique keys for every url. The keys expire after 24 hours.
