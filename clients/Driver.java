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
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import sender.Bolid;

@MessageDriven (mappedName = "java:/jms/queue/driverQueue", activationConfig = {
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/driverQueue"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
				})

public class Driver implements MessageListener{
	
	public Driver(){}

	@Resource(lookup =  "java:/jms/queue/mechanicQueue")
	private Queue mechanicQueue;
	
	@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
	
	@Override
	public void onMessage(Message message) {
		Connection connection = null;
		
		try{
			
			connection = connectionFactory.createConnection();
			
			if(message instanceof ObjectMessage){
				ObjectMessage objectMessage = (ObjectMessage)message;
				Bolid bolid = (Bolid) objectMessage.getObject();
				
				System.out.println("Kierowca odebral: " + bolid.toString());
			}
			
			goToPit();
			
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

	private void goToPit(){
		QueueConnection connection = null;
		try {
			connection = (QueueConnection) connectionFactory.createConnection();
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducerQueueDriver = session.createProducer(mechanicQueue);
			TemporaryQueue temporaryQueue = session.createTemporaryQueue();
			TextMessage message = session.createTextMessage("Pit!");
			message.setJMSReplyTo(temporaryQueue);
			messageProducerQueueDriver.send(message);
			System.out.println("Kierowca wyslal PIT");
			
			QueueReceiver receiver = session.createReceiver(temporaryQueue);
			connection.start();
			TextMessage answer = (TextMessage)receiver.receive();
			System.out.println("Kierowca, decyzja PIT: " + answer.getText());
			
		} catch (JMSException e) {
			e.printStackTrace();
		}finally{
			if(connection!=null){
				try{
					connection.close();
				}catch(JMSException e){
					e.printStackTrace();
				}
			}
		}
	}
	
}
