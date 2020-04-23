package com.airhacks;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalTime;
import java.util.Properties;

public class MainConsumer {
    public static void main(String[] args) throws NamingException, JMSException {
        // Provide the details of remote JMS Client
        Properties props = new Properties();
        props.put(Context.PROVIDER_URL, "mq://localhost:7676");

        // Create the initial context for remote JMS server
        InitialContext cntxt = new InitialContext(props);
        System.out.println("Context Created");

        // JNDI Lookup for QueueConnectionFactory in remote JMS Provider
        ConnectionFactory connectionFactory = (ConnectionFactory) cntxt.lookup("jms/__defaultConnectionFactory");

        String msg = "Hello from remote JMS Client";

        final JMSContext jmsContext = connectionFactory.createContext();

        // JNDI Lookup for the Queue in remote JMS Provider
        Topic topic = (Topic) cntxt.lookup("jms/newsTopic");
        final JMSConsumer consumer = jmsContext.createConsumer(topic, "NewsType='Sports'");

        System.out.println("Waiting for message " + LocalTime.now());
        final Message receive = consumer.receive();
        System.out.println("Received message " + LocalTime.now());
        System.out.println("Message Received: " + receive.getBody(String.class));


        // Make sure all the resources are released
        jmsContext.close();

    }
}
