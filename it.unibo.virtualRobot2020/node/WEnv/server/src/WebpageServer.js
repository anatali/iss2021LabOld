/*
WebpageServer.js
*/

const app      = require('express')()
const express  = require('express')
const http     = require('http').Server(app)
const socketIO = require('socket.io')(http)

const sockets   = {}
let socketCount = -1

var alreadyConnected = false

let webpageReady = false
const moveTime   = 800
const serverPort = 8090
var target = "notarget"

function WebpageServer(callbacks) {
    startServer(callbacks)
	//console.log(this)	//WebpageServer {}
    this.moveForward   = duration => Object.keys(sockets).forEach( key => sockets[key].emit('moveForward', duration) )
    this.moveBackward  = duration => Object.keys(sockets).forEach( key => sockets[key].emit('moveBackward', duration) )
    this.turnRight     = duration => Object.keys(sockets).forEach( key => sockets[key].emit('turnRight', duration) )
    this.turnLeft      = duration => Object.keys(sockets).forEach( key => sockets[key].emit('turnLeft', duration) )
    this.alarm = () => Object.keys(sockets).forEach( key => sockets[key].emit('alarm') )
//DEC 2019
    this.remove        = name => Object.keys(sockets).forEach( key => sockets[key].emit('remove', name) )
}

function startServer(callbacks) {
    startHttpServer()
    initSocketIOServer(callbacks)
}

function startHttpServer() {
    app.use(express.static('./../../WebGLScene'))
    app.get('/', (req, res) => { 
	     console.log("WebpageServer | GET socketCount="+socketCount + " alreadyConnected =" + alreadyConnected )
             if( ! alreadyConnected ){
		alreadyConnected = true;
		res.sendFile('indexOk.html', { root: './../../WebGLScene' }) 
	     }else{
		res.sendFile('indexNoControl.html', { root: './../../WebGLScene' }) 
                //See socket.on( 'disconnect' ...
	     }
    })

	//USING POST (with axios) : by AN Jan 2021

    	app.post("/api/move", function(req, res,next)  {  
	    var data = ""	    
	    req.on('data', function (chunk) { data += chunk; }); //accumulate data sent by POST
            req.on('end', function () {	//elaborate data received JSon: { robotmove: turnLeft | turnRight | ... }
     		var moveTodo = JSON.parse(data).robotmove
    		console.log('POST moveTodo  ' + moveTodo  );
	   	    Object.keys(sockets).forEach( key => sockets[key].emit(moveTodo, moveTime) );	//execute the command on the scene
		//Configure the answer
    		res.writeHead(200, {
      			'Content-Type': 'text/json'
    		});
    		res.statusCode=200
			 
    		//res.write(JSON.stringify(data));	
			//WE must wait, since we could have a collision			
			setTimeout(function() { 
				const collision = target != 'notarget'
				console.log('POST collision  ' + collision  );
				const answer =  JSON.stringify( "{ \"collision\" : " +  collision + ", \"move\": \"" + moveTodo + "\"}" )
				
				res.write(  answer  ); 
				target = "notarget"; 	//reset
				res.end();
				}, 
				moveTime);	 	
    		
  	   });
	});


    http.listen(serverPort)
}



function initSocketIOServer(callbacks) {
	console.log("WebpageServer | initSocketIOServer socketCount="+socketCount)
    socketIO.on('connection', socket => {
        socketCount++
        console.log("WebpageServer connection | socketCount="+socketCount)
        const key    = socketCount
        sockets[key] = socket
        
        callbacks.onWebpageReady()
        webpageReady = true
        if( socketCount == 0) console.log("WebpageServer | MASTER webpage ready")

        socket.on( 'sonarActivated', callbacks.onSonarActivated )
        socket.on( 'collision',     (obj) => { 
		    console.log( "collision detected " + obj ); 
		    target = obj;
			//callbacks.onCollision
			} )
        socket.on( 'disconnect',     () => { 
        		delete sockets[key]; 
        		webpageReady = false; 
          		socketCount--;
			alreadyConnected = ( socketCount == 0 )
        		console.log("WebpageServer disconnect | socketCount="+socketCount)
        	})
    })
    

}

function isWebpageRead() {
    return webpageReady;
}

module.exports = { WebpageServer, isWebpageRead }