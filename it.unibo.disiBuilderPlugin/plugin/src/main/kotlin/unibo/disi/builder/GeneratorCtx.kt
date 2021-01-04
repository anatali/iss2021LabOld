package unibo.disi.builder

import alice.tuprolog.Prolog

object GeneratorCtx {

    fun genTheContextCode( systemName: String, ctxName : String, modelFileName : String, sysKb : Prolog, msgdriven:Boolean) {
        genCtxMain(ctxName, modelFileName)
        //GENERATE THE SKELETON CODE OF ALL THE ACTORS IN THE CONTEXT
        //GeneratorMsgDrivenSystem.gen( ctxName  )
        GeneratorActorsInContext.gen(systemName, ctxName, sysKb, msgdriven)
    }

    fun genCtxMain( ctxName : String, modelFileName : String ){
        val content = genCtxMainContent( GenUtils.genFilePathName(ctxName), modelFileName)
        genMainCtxFile(ctxName, content)
    }

    /*
     Generate the code for the contexts
    */
    fun genCtxMainContent(dirName : String, modelFileName: String ) : String{
        val packageName = dirName.replace("${Generator.outSrcDir}/","").replace("/",".")
        return """
/* Generated by AN DISI Unibo */
package $packageName
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts( "localhost", this, "$modelFileName.pl", "sysRules.pl" )
}
"""
    }

    fun genMainCtxFile( ctxName: String, content: String ) {
        try {
            //generate a new directory
            val dirName = GenUtils.genFilePathName(ctxName)
            GenUtils.genDirectory(dirName)
            val mainfName = "$dirName/Main_${ctxName}.kt"
            println( "generator | genMainCtxFile mainfName=$mainfName"  )
            val mainf     = java.io.File( mainfName )
            if( mainf.exists() ) return
            mainf.writeText( content )		//writeText creates the file
            println("generator | done $mainfName")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


}//generatorCtx