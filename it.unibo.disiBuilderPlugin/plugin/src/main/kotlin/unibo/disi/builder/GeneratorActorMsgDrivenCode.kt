package unibo.disi.builder

import alice.tuprolog.Prolog

object GeneratorActorMsgDrivenCode {
    val dollar = "$"

    fun gen( systemName: String, ctxName: String, sysKb: Prolog ) : String{
        val actorNamesList = GenUtils.getActorNames(ctxName,sysKb)
        return genSystemCode(systemName,actorNamesList)
        //
        //actorNamesList.forEach{ v-> genCodeActorFile(GenUtils.genFilePathName(v), v ) }
    }
/*
    fun genCodeActorFile( filePathName: String, actorName : String ){
        println( "GeneratorActorsInContext | genCodeActorFile actorName=$actorName filePathName=$filePathName")
        //generate a new directory
        val dirName = GenUtils.genFilePathName(actorName)
        GenUtils.genDirectory(dirName)
        val actorfName = "$dirName/${actorName}.kt"
        val actorf     = java.io.File( actorfName )
        if( actorf.exists() ) return
        println( "GeneratorActorsInContext | genCodeActorFile actorfName=$actorfName"  )
        var content = genActorsCode() //generatorActorMsgDrivenCode.gen(actorName,sysKb)
        actorf.writeText( content )
        //println("generator | done msgdriven=$msgdriven genCodeActorFile for $actorName")
    }
*/

    fun genSystemCode(sysName : String, actorNamesList : List<String>): String{
        val packageName =  "${Generator.packagelogo}/$sysName".replace("/",".")
        return """
/* System msg-driven Generated by AN DISI Unibo */
package $packageName
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel

val cpus = Runtime.getRuntime().availableProcessors(); 

fun curThread() : String { 
	return "thread=$dollar{Thread.currentThread().name} / nthreads=$dollar{Thread.activeCount()}" 
}
    ${genChannelNames(actorNamesList)}
    ${genActorCode(actorNamesList)}
    ${genMain(sysName,actorNamesList)}
"""
    }


    fun genChannelNames(actorNamesList : List<String>): String{
        var outs = ""
        actorNamesList.forEach{ v-> outs += """
var ${v}Actor : SendChannel<String>?  = null
""" }
        return outs
    }

    fun genActorCode(actorNamesList : List<String>): String{
        var outs = ""
        actorNamesList.forEach{ v-> outs += """
fun start$v( scope : CoroutineScope ){
	${v}Actor = scope.actor<String> {   
		println("${v}Actor STARTS")
        //OtherActor!!.send("hello from $v")        //TODO
        //OtherActor!!.send("end")                  //TODO
		var msg : String
		do{ 	//message-driven
            msg = channel.receive()
			println("${v}Actor receives: "+ msg)
		}while( msg != "end" )
        //OtherActor!!.send("end")       //TODO
		println("${v}Actor ENDS")
	}
}                
""" }
        return outs
    }

    fun genMain(sysName : String,actorNamesList : List<String>) : String{
return """
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() = runBlocking{
    println("${sysName} BEGINS CPU=$dollar{cpus}"   )
 	${genCreateActors(actorNamesList)}
    println("ENDS $dollar{curThread()}"   )
}   
""".trimIndent()
    }

    fun genCreateActors(actorNamesList : List<String>) : String{
        var outs = ""
        actorNamesList.forEach { v -> outs += """
        start$v(this)
""" }
        return outs
    }





 /*
 OLD PART
  */

    fun genOld( actorName: String) : String{
        val dollar = "$"
        val packageName =  "${Generator.packagelogo}/$actorName".replace("/",".")
        return """
/* Actor msg-driven Generated by AN DISI Unibo */
package $packageName
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel

var senderActor   : SendChannel<String>?  = null
var receiverActor : SendChannel<String>?  = null
val cpus = Runtime.getRuntime().availableProcessors(); 

fun curThread() : String { 
	return "thread=$dollar{Thread.currentThread().name} / nthreads=$dollar{Thread.activeCount()}" 
}
               
fun startReceiver( scope : CoroutineScope ){
	receiverActor = scope.actor<String> {   
		println("receiverActor STARTS")
		var msg = channel.receive()
		while( msg != "end" ){ 	//message-driven
			println("receiverActor receives "+ msg)
			msg = channel.receive()
		}
		println("receiverActor ENDS")
	}
}
 
fun startSender( scope : CoroutineScope){
	senderActor = scope.actor {  
		//actor is a coroutine builder (dual of produce)
		println("senderActor   STARTS")
 		receiverActor!!.send("Hello1")
		delay(250)
 		receiverActor!!.send("Hello2")
		delay(250)
		receiverActor!!.send("end")
		println("senderActor   ENDS")
 	}
} 

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi

fun main() = runBlocking{
    println("BEGINS CPU=$dollar{cpus}"   )
 	startReceiver( this )
	startSender( this )
    println("ENDS $dollar{curThread()}"   )
}
"""
    }

}//generatorActorMsgDrivenCode