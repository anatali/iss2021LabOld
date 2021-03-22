/*
===============================================================
ClientBoundaryWebsockArilAsynch.java
Use the aril language and the support specified in the
configuration file IssProtocolConfig.txt

The business logic is defined in RobotControllerArilBoundary
that is 'message-driven'
===============================================================
*/
package it.unibo.resumableWalker;
import it.unibo.consolegui.ConsoleGui;
import it.unibo.oksupports.WebSocketJavaSupport;
import it.unibo.useokhttp.ActorObserverNaive;

public class MainResumableRobotBoundary {
    private RobotApplInputController controller;

    //Constructor
    public MainResumableRobotBoundary( ){
        WebSocketJavaSupport support = WebSocketJavaSupport.createForWs("localhost:8091" );
        controller = new RobotApplInputController(support, false, true );
        support.registerObserver( controller );

        ActorObserverNaive actorObs1 = new ActorObserverNaive(3);
        support.registerObserver(actorObs1);

        System.out.println("MainResumableRobotBoundary | CREATED  n_Threads=" + Thread.activeCount());
        new ConsoleGui(  controller );
    }


    public static void main(String args[]){
        try {
            System.out.println("MainResumableRobotBoundary | main start n_Threads=" + Thread.activeCount());
            new MainResumableRobotBoundary();
            //System.out.println("MainResumableRobotBoundary  | appl n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
