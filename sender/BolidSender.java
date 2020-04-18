package sender;

import java.util.Date;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

@Startup
@Singleton
public class BolidSender {
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory conn;
	
	@Resource(lookup =  "java:/jms/topic/bolidTopic")
	private Topic bolidTopic;

	@Resource(lookup =  "java:/jms/queue/routerQueue")
	private Queue routerQueue;
	
	public BolidSender(){}
	
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Schedule(second="*/15",minute="*",hour="*")
	@PostConstruct
	public void sendMessage(){
		
		Connection connection = null;
		
		try{
			connection = conn.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducerQueueRouter = session.createProducer(routerQueue);
			MessageProducer messageProducerTopicLogger = session.createProducer(bolidTopic);
			
			
			Bolid state = new Bolid();
			state = createState(state);
			
			ObjectMessage message = session.createObjectMessage(state);
			
			messageProducerTopicLogger.send(message);
			messageProducerQueueRouter.send(message);
			
			System.out.println("Wyslano wiadomosc: " + state.toString());
		}catch(JMSException e){
			e.printStackTrace();
		}finally{
			if(connection != null){
				try{
					connection.close();
				}catch(JMSException e){
					e.printStackTrace();
				}
			}
		}
	}
    
    private Bolid createState(Bolid state){
    	Random r = new Random();
    	double rangeMin = 10.0;
    	double rangeMax = 100.0;
    	
    	Date date = new Date();
		state.setEngineTemperature(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		state.setOilPressure(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		state.setTiresPressure(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
		state.setDate(date);
		
		return state;
    }
}
