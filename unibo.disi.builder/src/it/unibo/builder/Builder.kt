/* Generated by AN DISI Unibo */ 
package it.unibo.builder

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Builder ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("builder | STARTS")
						solve("consult('demo0.pl')","") //set resVar	
						solve("consult('sysRules.pl')","") //set resVar	
						solve("ctx(NAME,HOST,PROTOCOL,PORT)","") //set resVar	
						 val ctxName = getCurSol("NAME") 
								   val ctxHost = getCurSol("HOST") 
								   val ctxPort = getCurSol("PORT") 
						println("ctxName=${ctxName} ctxHost=${ctxHost}")
					}
				}	 
			}
		}
}