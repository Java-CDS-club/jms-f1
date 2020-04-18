package clients;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import sender.Bolid;

@MessageDriven(mappedName = "jms/topic/bolidTopic", activationConfig = {
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/bolidTopic"), 
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
				})

public class Logs implements MessageListener{
	public Logs(){}
	
	public void onMessage(Message message){
		try {
			if(message instanceof ObjectMessage){
			
				ObjectMessage objectMessage = (ObjectMessage)message;
				Bolid bolid = (Bolid) objectMessage.getObject();
				saveToFile(bolid);
				
				System.out.print("Dodane do logow");
			} 
		}catch (JMSException e) {
				e.printStackTrace();
			}
		}
	
	private void saveToFile(Bolid state) {
	    String textToAppend = state.toString() + "\n"; 
	     
	    Path path = Paths.get("/home/osboxes/Desktop/logs.txt");
	  
	    try {
	    	if(Files.exists(path)){
	    		Files.write(path, textToAppend.getBytes(), StandardOpenOption.APPEND);
	    	}else{
	    		Files.write(path, textToAppend.getBytes(), StandardOpenOption.CREATE_NEW);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}


