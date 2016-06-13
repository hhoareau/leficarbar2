package com.ficar.server;

import com.ficar.shared.Tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

//Classe utiliser pour effectuer les appels REST en mode GET ou POST aux API SFR 
public class RestCall  {
		
	String id=null;
	private static final Logger log = Logger.getLogger(RestCall.class.getName());
	
	
	//Constructeur
	public RestCall(String urlServer,String params,String id) {				
			
		this.id=id;
	
		try {			
			URL url = new URL(urlServer.trim());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
			log.warning("Appel de "+url+" avec param="+params);
            
			if(params!=null){
	            connection.setRequestProperty("Content-Length", ""+params.getBytes().length);
	            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            connection.setRequestProperty("Content-Language", "en-US");
	            
	            connection.setUseCaches(true);
	            connection.setDoOutput(true);
	            connection.setDoInput(true);
	            connection.setConnectTimeout(30000);
	            
	            connection.setRequestMethod("POST");
	            
	            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
	            
	            writer.writeBytes(params);
	            writer.flush();
	            writer.close();
	            
            } else {
            	connection.setRequestMethod("GET");	
            }
            	
            //Delai de r�ponse fix� � 30 secondes
            connection.setReadTimeout(60*1000);
            connection.connect();

            //R�cuperation de la r�ponse
            InputStream is=connection.getInputStream();
            InputStreamReader isr=new InputStreamReader(is);
	        BufferedReader buffer=new BufferedReader(isr);
	        String line="";
			StringWriter writer=new StringWriter();
			while ( null!=(line=buffer.readLine()))writer.write(line);
	        
			int reponseCode=connection.getResponseCode();
	        if (reponseCode == HttpURLConnection.HTTP_OK) {     	
				if(reponseCode==200){
					onSuccess(writer.toString());
				}
				else 
					onFailure(reponseCode);
	        }
	        else onFailure(reponseCode);        
    } 
		catch (MalformedURLException e) {
			e.printStackTrace();
			Tools.log.info(e.getMessage());
			onFailure(2);
			} 
		catch (IOException e) {
			e.printStackTrace();
			Tools.log.info(e.getMessage());
			onFailure(1);
			}
	}
	
	//Est appell�e quand le serveur retourn 200, m�thode doit �tre surcharg�e
	public void onSuccess(String rep) {}; 
	
	//Idem que onSuccess pour les cas d'�chec
	public void onFailure(int reponseCode) {}	
}
