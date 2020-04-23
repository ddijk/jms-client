package com.airhacks;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MainProducer {
    public static void main(String[] args) throws NamingException, JMSException {



//        final JMSConsumer consumer = jmsContext.createConsumer(topic, "messageSelector=(NewsType = 'Sports') OR  (NewsType = 'Opinion')");

//        consumer.setMessageListener(msg-> {
//            try {
//                logger.info("*** "+msg.getBody(String.class));
//            } catch (JMSException e) {
//                e.printStackTrace();
//            }
//        });

        // Provide the details of remote JMS Client
        Properties props = new Properties();
        props.put(Context.PROVIDER_URL, "mq://localhost:7676");

        // Create the initial context for remote JMS server
        InitialContext cntxt = new InitialContext(props);
        System.out.println("Context Created");

        // JNDI Lookup for QueueConnectionFactory in remote JMS Provider
        ConnectionFactory connectionFactory = (ConnectionFactory)cntxt.lookup("jms/__defaultConnectionFactory");

        // Create a Connection from QueueConnectionFactory
        Connection connection = connectionFactory.createConnection();


        System.out.println("Connection established with JMS Provide ");

        // Initialise the communication session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        String msg = "Hello from remote JMS Client";

        final JMSContext jmsContext= connectionFactory.createContext();

        // JNDI Lookup for the Queue in remote JMS Provider
        Topic topic = (Topic)cntxt.lookup("jms/newsTopic");

        // Create the message
//        jmsContext.createConsumer(topic)
        TextMessage message = session.createTextMessage();
        message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        message.setText(msg);
        message.setStringProperty("NewsType" , "Opinion");


        // Create the MessageProducer for this communication
        // Session on the Queue we have
        MessageProducer mp = session.createProducer(topic);

        // Send the message to Queue
        mp.send(message);
        System.out.println("Message Sent: " + msg);

        // Make sure all the resources are released
        mp.close();
        session.close();
        cntxt.close();

    }
}
