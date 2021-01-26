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
	     console.log("WebpageServer | GET socketCount="+socketCount)
             if( ! alreadyConnected ){
		alreadyConnected = true;
		res.sendFile('indexOk.html', { root: './../../WebGLScene' }) 
             }else if( socketCount == 0 ) {	//the alreadyConnected is still on
		res.sendFile('indexNoControl.html', { root: './../../WebGLScene' }) 
             }else{
		res.sendFile('indexOk.html', { root: './../../WebGLScene' }) 
	     }
    })

//EXPERIMENT Jan 2021
    app.get("/r", (req, res ) => { 	 
	   Object.keys(sockets).forEach( key => sockets[key].emit('turnRight', 800) );  //non può mantenere la pagina
           //res.sendFile('index.html', { root: './../../WebGLScene' }) 
	});

    app.get("/l", (req, res ) => { Object.keys(sockets).forEach( key => sockets[key].emit('turnLeft', 800) );
           //res.sendFile('index.html', { root: './../../WebGLScene' }) 
	});
//END EXPERIMENT
    http.listen(8090)
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
        console.log("WebpageServer | webpage ready")

        socket.on( 'sonarActivated', callbacks.onSonarActivated )
        socket.on( 'collision',      callbacks.onCollision )
        socket.on( 'disconnect',     () => { 
        		delete sockets[key]; 
        		webpageReady = false; 
          		socketCount--
        		console.log("WebpageServer disconnect | socketCount="+socketCount)
        		})
    })
    

}

function isWebpageRead() {
    return webpageReady;
}

module.exports = { WebpageServer, isWebpageRead }