package clients;


import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import sender.Bolid;


@MessageDriven(mappedName = "java:/jms/queue/routerQueue", activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/routerQueue"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
				})

public class MessageRouter implements MessageListener{

	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
	
	@Resource(lookup =  "java:/jms/queue/driverQueue")
	private Queue driverQueue;
	
	@Resource(lookup =  "java:/jms/queue/mechanicQueue")
	private Queue mechanicQueue;
	
	public MessageRouter(){}
	
	@Override
	public void onMessage(Message message) {
		Connection connectionToDiver = null;
		Connection connectionToPit = null;
		try {
			if(message instanceof ObjectMessage){
				
				
				System.out.println("Router odebral wiadomosc.");
			
				ObjectMessage objectMessage = (ObjectMessage)message;
				Bolid bolid = (Bolid) objectMessage.getObject();
				
				if(ifToDriver(bolid)){
					connectionToDiver = connectionFactory.createConnection();
					Session session = connectionToDiver.createSession(false, Session.AUTO_ACKNOWLEDGE);
					MessageProducer messageProducerQueueDriver = session.createProducer(driverQueue);
					messageProducerQueueDriver.send(message);
					System.out.println("Wyslano do kierowcy wiadomosc.");
				}	
			
				if(ifToMechanic(bolid)){
					connectionToPit = connectionFactory.createConnection();
					Session session = connectionToPit.createSession(false, Session.AUTO_ACKNOWLEDGE);
					MessageProducer messageProducerQueueMechanik = session.createProducer(mechanicQueue);
					messageProducerQueueMechanik.send(message);
					System.out.println("Wyslano do mechanika wiadomosc.");
				}
			} 
		}catch (JMSException e) {
				e.printStackTrace();
		}finally {
			if(connectionToDiver!=null){
				try {
					connectionToDiver.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			
			if(connectionToPit!=null){
				try {
					connectionToPit.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
		}
		
	
	private Boolean ifToDriver(Bolid state){

		if(state.getEngineTemperature() > 70.00 || state.getTiresPressure() < 50.00 || state.getOilPressure() < 50.00){
			return true;
		}
		
		return false;
	}

	private Boolean ifToMechanic(Bolid state){
		if(state.getEngineTemperature() > 90.00 || state.getTiresPressure() < 20.00 || state.getOilPressure() < 30.00){
			return true;
		}
		return false;
	}
}
