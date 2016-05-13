import java.io.*;
import java.net.*;
import javax.net.ssl.*;

class ConnexionSocket{

    public ConnexionSocket(){  
	try{
		// Connexion avec le serveur ssl
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socketToServer = (SSLSocket) socketFactory.createSocket("agenda.univ-ubs.fr",443);
        
        // Connexion avec un serveur non ssl
        // Socket socketToServer = new Socket ("www.google.fr", 80);
			
		// Récupération des flux de communication
		DataOutputStream toServer   = new DataOutputStream(socketToServer.getOutputStream());
		DataInputStream  fromServer = new DataInputStream (socketToServer.getInputStream());
	    
	    // envoi vers le serveur
	    // byte [] msg = new byte[1024];
	    // toServer.write(msg,0,lg);
	    
	    //reception du serveur
	    // int lg = in.read(msg);	    
	    
	    // fermeture des connexions
		toServer.close();
		fromServer.close();
			
	    }
	}
	catch (Exception e){
	    e.printStackTrace();
	}
    }
}
