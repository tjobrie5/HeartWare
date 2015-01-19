/*
NODE SETUP
1. make sure server.js and package.json are in the same directory
2. from the same directory run the command 'sudo npm install'. This downloads all the dependencies
for the project that are listed in package.json
3. Make sure you have a MongoDb server up and running by runnning the command 'mongod'
4. Finally run the command 'node server.js' this will spin up a node server on port 8080
where the url is localhost:8080
*/


// call the packages we need
var express    = require('express');
var app        = express();
var async 	   = require('async');
var url 	   = require('url');
var request	   = require('request');

//Data base connection
var MongoClient = require('mongodb').MongoClient
  , assert = require('assert');

var dbUrl = 'mongodb://localhost:27017/heartware';


/*
=========================================================================
Hardcoded values for api calls
*/
var movement = "https://jawbone.com/nudge/api/v.1.1/users/@me/moves";
var token = 'Bearer CHSvNoQb5olFNO4lSqs72yqWgdnkcicIGWilKcrLMVZ2ahETv37EE4DTJMD5Pw2a3__E7PglkcVrijZtkonp9FECdgRlo_GULMgGZS0EumxrKbZFiOmnmAPChBPDZ5JP'

var options = {
	url: movement,
	headers : {
	'Accept':'application/json',
	'Authorization': token
	}
}

//============================================================================
/*
RESTful API
==============
1. Authenticate user
2. Pull data from devices
3. write data to mongoDB
=============================
*/
//this is the api you will use in the Android app. The url for it will look the following way.
//http://localhost:8080/getData?username=mazzolaamy@cox.net&password=heartware
//This is a POST request
app.post('/getData', function(req, res){
	
	//parse request url and grab the query parameters
	var URL = url.parse(req.url, true);
	var query = URL.query;

	/*
	hard coded in for now We'll have to figure out proper auth later on.
	Possibly use Passport, an authentication framework in node that has support
	for OAuth 2.0
	*/
	
	if(query.username == 'mazzolaamy@cox.net' && query.password == 'heartware')
	{
		//RESTful api call for movement
		request(options, function(error, response, body)
		{
			//check to make sure jawbone api call is successful
			if(!error && response.statusCode == 200)
			{
				//function used for inserting data into mongo
				var insertDocuments = function(db, callback) {
				  // Get the documents collection
				  var collection = db.collection('document2');
				  // Insert some documents. JSON.parse(body) contains the jawbone data
				  collection.insert(JSON.parse(body), function(err, result) {
				    assert.equal(err, null);
				    callback(result);
				  });

				}
				//actually connects to mongodb and calls the insertDocuments function
				MongoClient.connect(dbUrl, function(err, db){
					assert.equal(null, err);
					console.log("connected to Db");

					insertDocuments(db, function(){
						db.close();
					});
				});
				//send back a success response to the client calling 
				res.send(201);
			}
			else
				//jawbone api error. Send error back to client
				res.send(400);
		});
	}

	else
	{
		//Failed Authorization response
		res.send(401, "Incorrect Username or Password");
	}

	});

//port app is running on http://localhost:8080
app.listen(8080);
console.log('server up');
