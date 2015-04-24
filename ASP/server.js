/*
NODE SETUP
1. make sure server.js and package.json are in the same directory
2. from the same directory run the command 'sudo npm install'. This downloads all the dependencies
for the project that are listed in package.json
3. Make sure you have a MongoDb server up and running by runnning the command 'mongod'
4. Finally run the command 'node server.js' this will spin up a node server on port 8080
where the url is qqroute.com:8x
*/


// Retrieve the packages we need
var python_shell = require('python-shell');
var express    = require('express');
var app        = express();
var async 	   = require('async');
var url 	   = require('url');
var request	   = require('request');
var bodyParser = require('body-parser');
var fs = require('fs');

//Data base connection
var MongoClient = require('mongodb').MongoClient
  , assert = require('assert');

var dbUrl = 'mongodb://localhost/heartware';

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


/*
=========================================================================
Put all API's being used here
*/
var retrieveUser = "https://jawbone.com//nudge/api/v.1.1/users/@me"
var movement = "https://jawbone.com/nudge/api/v.1.1/users/@me/moves";

/*
=======================================================================
*/

// function called before every request api it structures all api requests
	var optionsConstruct = function(Url, Token){
		var Options = {url: Url, 
					   headers: {
					   	'Accept': 'application/json',
					   	'Authorization': 'Bearer ' + Token
					   }

					}
		return Options			
	}


/* 
All our our API's which we are developing
*/
app.get('/', function(req, res){
 			        python_shell.run('script.py', function(err, results){
		               		 console.log('ran python');
        		       		 console.log( results);
					res.json(results);
  				});
});

/* 
Called from android app when initially connecting with Jawbone UP
Auth Token is recieved and stored in MongoDB associated with the name that is returned
from retrieveuser API.
*/
app.post('/sendToken', function(req, res){
        console.log("token recieved");

        request(optionsConstruct(retrieveUser, req.body.token),
         function(error, response, body){
         	body = JSON.parse(body); 
         	console.log(body.data.last);
         	var insertDocuments = function(db, callback){
         		var collection = db.collection('users');
         		collection.update(
         			{user : body.data.last}, 
         			{$set:{token : req.body.token}}, 
         			{upsert : true}, function(err, result) {
         				callback(err);
         			}
         		);
         		
         	}

         	MongoClient.connect(dbUrl, function(err, db){
					assert.equal(null, err);

					insertDocuments(db, function(){
						db.close();
					});
				});
         	
        })

        res.send(200);

})

/*
	orale
	So this is the new Getdata API. Its a post now so I recommend you get a REST client called postman(its a chrome extension) so you can\
	test it out. Right now the token is being stored in mongodb in the users collection associated with the USERS last name.
	To test this API go to the postman client and use the URL http://qqroute.com:8080/getData MAKE SURE its a POST request. Next find a tab that says
	x-www-form-urlencoded, click on it and where it says key enter user and where it says value enter in Mazzola. These values will be sent from the Android
	App when the actual application is working
	Ok so this API works as follows
	1. Searchs mongodb for the token associated with lastName(in this case Mazzola)
	2. Deletes old data.
	3. Uses the token from step 1 calls jawbone movement API and inserts into the DataBase in the movement collection.
	4. Then calls and does whatever your python stuff does
	I did an initial run of this on qqroute so mongodb will have the necessary info to work.
	IF it doesn't work that is probably because somebody ran the helloUp app and it reissued a new token so the 
	JAwbone api's won't work correctly(This could happen often). So just post something on Facebook so I can get the new token in the db
	Once the above /sendToken API is integrated with our Android app this won't be a problem
*/
app.post('/getData', function(req, res){

	var token;

	var findDocuments = function(db, callback){
		var collection = db.collection('users');
	    db.createCollection('movement', function(err, collection) {});

		collection.findOne({user: req.body.user}, function(err, doc){
			console.log("userFound");
			if(err)
				throw err;
			token = doc.token;
			callback(doc);

		})
	}


	var deleteDocuments = function(db, callback){
		var movementCollection = db.collection('movement');
		movementCollection.remove({}, function(err, result){
			console.log("documents deleted");
			callback(result);
		});

	}

	var insertDocuments = function(db, callback) {
	  // Get the documents collection
	  var collection = db.collection('movement');
	  console.log("inserstion area");
	  //console.log(token);
	  request(optionsConstruct(movement, token),
				function(error, response, body){
	  if(response.statusCode != 401){	
	  collection.insert(JSON.parse(body), function(err, result) {
	    assert.equal(err, null);
	    console.log("documents inserted");
	    callback(result);

	    //IMPORTANT -- make sure you run your python code in the insertDocuments method to ensure that it runs after the new data has been inserted
	    // into the db.
	    /*
	    python_shell.run('script.py', function(err, results){
		               		 console.log('ran python');
        		       		 console.log( results);
					res.json(results);
  				});
		*/
	  });
	}
	 });
	};

	MongoClient.connect(dbUrl, function(err, db){
		  assert.equal(null, err);

		findDocuments(db, function(){
			deleteDocuments(db, function(){
				insertDocuments(db, function(){
					db.close();
				});
			});
		});
	});

	res.send(200);
});

app.get('/getWorkout', function(req, res){
       MongoClient.connect(dbUrl, function(err,db) {

                if (err) {
                        throw err;
                 } else {
                        var collection = db.collection('movement');

                        collection.find().toArray( function(err, results) {
                                if(err || !results) {
                                        console.log('odd data set');
                                }  else {
                                        var items = results[0].data.items;
                                        var last_item = items.pop();
					
					var data = "steps(";
					data += last_item.details.steps;
					data += ").";
					console.log(data);
					fs.writeFile("/root/HeartWare/ASP/asp_steps.db", data, function(err){
						if(err){
							console.log(err);
						}
						else{
							console.log("The file was saved!");
						}
				});
                                }

                                db.close();

                        });
                }

        });





                                python_shell.run('script.py', function(err, results){
                                	if(err){
						console.log(err)
					}
					  
				        console.log('ran python');
                                        var array_results = [];
					array_results = results.map(function (item){
						return item;});
					var result = "";
					console.log("here");
					var index = Math.floor(Math.random()*(5-1) + 1);
					console.log(index);
					if(index < array_results.length)
					{
						if(index == 1)	result += array_results[1];
						else if(index == 2)	result += array_results[3];
						else if(index == 3)	result += array_results[5];
						else if(index == 4) result += array_results[7];
						else result += array_results[9];
					}
					else
						result += array_results[1];
					var workout = result.substring(7);
					var arr = workout.split(",");
					workout = arr[0];
					var time = arr[1];
					arr = workout.split("_");
					workout = "";
					for(var i = 0; i<arr.length; i++){
						workout += arr[i];
						workout += " ";
					}
					workout += time;
					workout += "!";
					var final_workout = workout.charAt(0).toUpperCase()+workout.slice(1);
					console.log(final_workout); 
                                        res.send(final_workout);
                                });
});
/*
//post method for calendar information
app.post('/sendCal', function(req, res){
	console.log("calendar info recieved");

        request(optionsConstruct(retrieveUser, req.body.cal),
	function(error, response, body){

	}

});
*/

app.post('/profileData', function(req, res){
         	var insertDocuments = function(db, callback){
         		var collection = db.collection('profile');
         		collection.update(
         			{disability : req.body.disability,
         			difficulty: req.body.difficulty}, 
         			{$set:{user : req.body.user}}, 
         			{upsert : true}, function(err, result) {
         				callback(result);
         			}
         		);
         		console.log(req.body.user + " " + req.body.disability);
         	}

         	MongoClient.connect(dbUrl, function(err, db){
					assert.equal(null, err);

					insertDocuments(db, function(){
						db.close();
					});
				});

         	res.send(200);
})

app.post('/sendCal', function(req, res){
	console.log(req.body.cal);
	fs.writeFile("asp_time.db", "time(" + req.body.cal +").", function(err){
		if(err){
			return console.log(err);
		}
	})
})
//port app is running on http://qqroute:8x
app.listen(8080);
console.log('server up');
