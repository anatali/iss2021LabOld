/**
 * WEnvConnSupport
 * @author AN - DISI - Unibo
===============================================================
cril  = concrete-robot interaction language
aril  = highlevel robot interaction language
Using the javax.websocket library:
 sets a connection via websocket with the WENv working at the given hostAddr
 provides a method (sendMessage) to send commands written in aril to a robot able to understand the the cril language
 *** redirects the messages sent on the websocket by the WENv to a Kotlin channel
 provides methods (activateReceiver, startReceiver) that calls a given callback
 for each message received on the Kotlin channel

===============================================================
 */
package it.unibo.interactionKotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.glassfish.tyrus.client.ClientManager
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.websocket.*

@ClientEndpoint
public class WEnvConnSupport(
        val scope : CoroutineScope,    //scope required for channel
        val hostAddr  : String,
        val moveDuration : String = "600"
) {
    var userSession: Session?                     = null
    private var messageHandler: MessageHandler?   = null
    val socketEventChannel: Channel<String> = Channel(10) //our channel buffer is 10 events
    //TODO define socketEventChannel related to application messages and not to simple String


    init{
        initConn( hostAddr )
    }

    public fun initConn(addr: String) {
        try {
            //val container = ContainerProvider.getWebSocketContainer()
            //container.connectToServer(this, URI("ws://$addr"))
            val endpointURI = URI( "ws://$addr/" )
            println("WEnvConnSupport | initClientConn $endpointURI")
            val client = ClientManager.createClient()
            client.connectToServer(this, endpointURI)
        } catch (ex: URISyntaxException) {
            println("WEnvConnSupport | URISyntaxException exception: " + ex.message)
        } catch (e1: DeploymentException) {
            println("WEnvConnSupport | DeploymentException : " + e1.message)
        } catch (e2: IOException) {
            println("WEnvConnSupport | IOException : " + e2.message)
        }
    }


    @OnOpen
    fun onOpen(userSession: Session?) {
        println("WEnvConnSupport | opening websocket")
             this.userSession = userSession
    }

    @OnClose
    fun onClose(userSession: Session?, reason: CloseReason?) {
        println("WEnvConnSupport | closing websocket")
        this.userSession = null
    }

    @OnMessage
    fun onMessage(message: String) {
        //println("WEnvConnSupport | websocket receives: $message ")
        sendToChannel( message )
    }

    fun sendMessage(message: String) : Boolean{
        //println("WEnvConnSupport | sendMessage $message")
        //userSession!!.getAsyncRemote().sendText(translate(message));
        if( userSession != null) {
            userSession!!.basicRemote.sendText(translate(message)) //synch: blocks until the message has been transmitted
            return true
        }else{
            println("WEnvConnSupport | sorry, no userSession")
            return false
        }
    }


    fun translate(cmd: String) : String{ //cmd is written in application-language
        var jsonMsg = "{\"robotmove\":\"MOVE\" , \"time\": DURATION}"  //"{ 'type': 'alarm', 'arg': -1 }"
        when( cmd ){
            "msg(w)", "w" -> jsonMsg = jsonMsg.replace("MOVE","moveForward").replace("DURATION", moveDuration)
            "msg(s)", "s" -> jsonMsg = jsonMsg.replace("MOVE","moveBackward").replace("DURATION", moveDuration)
                    //"{ 'type': 'moveBackward', 'arg': -1 }"
            "msg(a)", "a", "l" -> jsonMsg = jsonMsg.replace("MOVE","turnLeft").replace("DURATION", "300")
                    //"{ 'type': 'turnLeft',  'arg': -1  }"
            "msg(d)", "d", "r" -> jsonMsg = jsonMsg.replace("MOVE","turnRight").replace("DURATION", "300")
            //"{ 'type': 'turnRight', 'arg': -1  }"
            //"msg(l)", "l" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': 300 }"
            //"msg(r)", "r" -> jsonMsg = "{ 'type': 'turnRight', 'arg': 300 }"
            //"msg(z)", "z" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
            //"msg(x)", "x" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
            "msg(h)", "h" -> jsonMsg = jsonMsg.replace("MOVE","alarm").replace("DURATION", "100")
            //"{ 'type': 'alarm',     'arg': 100 }"
            else -> println("WEnvConnSupport command $cmd unknown")
        }
        val jsonObject = JSONObject( jsonMsg )
        val msg        =  jsonObject.toString()
        //println("WEnvConnSupport | translate output= $msg ")
        return msg
    }

/*
From socket to channel
 */
    fun sendToChannel(  msg : String ){
        scope.launch {
            socketEventChannel.send( msg )
        }
    }

    /*
    A receiver with a 'conventional' callback
     */
    suspend fun activateReceiver( cb:( String ) -> Unit ) {
        println("WEnvConnSupport | activateReceiver  ")
        val receiver = scope.launch {
            while ( true ) {
                val v = socketEventChannel.receive(  )
                //println("RECEIVER | activateReceiver receives $v ")  //in ${curThread()}
                if( v != "terminate")  cb( v )  else break
            }
        }
    }//activateReceiver


    /*
    A receiver with a 'suspendable' callback
     */
    suspend fun startReceiver(  cb: suspend ( String ) -> Unit ) { //callback
        println("WEnvConnSupport | startReceiver  ")
        val receiver = scope.launch {
                 while ( true ) {
                     try {
                            val v = socketEventChannel.receive(  )
                            //println("WEnvConnSupport | RECEIVER receives $v ")  //in ${curThread()}
                            if( v != "terminate")  cb( v )   else break
                     }catch( e : java.lang.Exception){
                         println("WEnvConnSupport | startReceiver ERROR $e")
                     }               }
                println("WEnvConnSupport | receiver ends ")
        }

    }

    suspend fun stopReceiver(   ) {
        println("WEnvConnSupport | stopReceiver ")
        socketEventChannel.send("terminate")
    }
}

//Just to test ...
fun main( ) = runBlocking {
    println("==============================================")
    println("WEnvConnSupport | main start n_Threads=" + Thread.activeCount());
    println("==============================================")
    val hh = WEnvConnSupport(this, "localhost:8091")
    //hh.initConn("localhost:8091")       //blocking
    // hh.activateReceiver( showWEnvEvents )
    // hh.activateReceiver( ::handleWEnvEvent )
    //doSomeMove( hh )

    //walker = Walker( hh )  //boundary

    //hh.activateReceiver( ::handlerToWalk  )
    hh.sendMessage("w")

    println("WEnvConnSupport | main end n_Threads=" + Thread.activeCount());
    delay(5000)    //to show data sent by WEnv
    println("BYE")
}