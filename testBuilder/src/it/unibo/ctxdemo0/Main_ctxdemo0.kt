
/* Generated by AN DISI Unibo */
package it.unibo.ctxdemo0
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts( "localhost", this, "demo1.pl", "sysRules.pl" )
}
