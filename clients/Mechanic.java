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
import javax.jms.Session;
import javax.jms.TextMessage;

import sender.Bolid;

@MessageDriven(mappedName = "java:/jms/queue/mechanicQueue",activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/mechanicQueue"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
				})

public class Mechanic implements MessageListener {

	private enum PitStopAnswer {NO, YES}
	
	public Mechanic(){}
	
	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory conn;
	
	@Override
	public void onMessage(Message message) {
		Connection connection = null;
		
		try{
			/**
			*  Jesli wiadomosc w typie obiektowym - wiadomosc o stanie bolidu,
			*  Jesli wiadomosc w typie tekstowym - prosba o pitstop
			**/
			if(message instanceof ObjectMessage){
				ObjectMessage objectMessage = (ObjectMessage)message;
				Bolid bolid = (Bolid) objectMessage.getObject();
				
				System.out.println("Mechanik odebral: " + bolid.toString());
			}
			
			if(message instanceof TextMessage){
				System.out.println("Mechanic dostal prosbe o PIT ");
				
				connection = conn.createConnection();
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer messageProducerPit = session.createProducer(message.getJMSReplyTo());
				TextMessage reply = session.createTextMessage((PitStopAnswer.YES).toString());
				messageProducerPit.send(reply);
				
				System.out.println("Mechanik odpowiada na pit: "+ reply.getText());
			}
			
		}catch (JMSException e) {
			e.printStackTrace();
		}finally{
			if(connection!=null)
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
		}
		
	}
}
