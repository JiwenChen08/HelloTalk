package org.lucky.multi;

import org.lucky.multi.impl.RemoteServer;

public class StartServer {

    public static void main(String[] args) {


        RemoteServer remoteServer = new RemoteServer(8888);

        System.out.println("Server starts.");
        remoteServer.start();


        remoteServer.stop();
        System.out.println("Server stopped.");
    }
}
