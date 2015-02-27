var express = require('express');
var app = express();

var python_shell = require('python-shell');

app.get('/', function(req, res)
{
	python_shell.run('script.py', function(err, results){
		console.log('ran python');
		console.log( results);
	});

});
app.get('/test', function(req, res){
	res.send('hello world');	
});
console.log('server running');
app.listen(8080);
