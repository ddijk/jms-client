package com.airhacks;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalTime;
import java.util.Properties;

public class MainConsumer implements Runnable {

    private InitialContext initialContext;
    private ConnectionFactory connectionFactory;
    private String id;
    private String filter;

    public MainConsumer(InitialContext initialContext, ConnectionFactory connectionFactory, String id, String filter) {
        this.initialContext = initialContext;
        this.connectionFactory = connectionFactory;
        this.id = id;
        this.filter = filter;
    }



    @Override
    public void run() {
        final JMSContext jmsContext = connectionFactory.createContext();

        // JNDI Lookup for the Queue in remote JMS Provider
        Topic topic = null;
        try {
            topic = (Topic) initialContext.lookup("jms/newsTopic");
            final Message receive;
            try (JMSConsumer consumer = jmsContext.createConsumer(topic, String.format("NewsType='%s'", filter))) {

                System.out.println("Waiting for message -"+ id +" - " + LocalTime.now());
                receive = consumer.receive();
            }
            System.out.println("Received message -"+ id +" - " + LocalTime.now());
            try {
                System.out.println("Message Received: " + receive.getBody(String.class));
            } catch (JMSException e) {
                System.err.println("FAiled to read message body");
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            // Make sure all the resources are released
            System.out.println("Cleaning up");
            jmsContext.close();
        }


    }

    public static void main(String[] args) throws NamingException, JMSException {
        // Provide the details of remote JMS Client
        Properties props = new Properties();
        props.put(Context.PROVIDER_URL, "mq://localhost:7676");

        // Create the initial context for remote JMS server
        InitialContext initialContext = new InitialContext(props);
        System.out.println("Context Created");

        // JNDI Lookup for QueueConnectionFactory in remote JMS Provider
        ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");

        MainConsumer consumer1 = new MainConsumer(initialContext, connectionFactory, "Jaap", "Sports");
        MainConsumer consumer2 = new MainConsumer(initialContext, connectionFactory, "Bram", "Opinion");

        Thread t1 = new Thread(consumer1);
        t1.start();
        Thread t2 = new Thread(consumer2);
        t2.start();
        System.out.println("Threads are started");
    }
}
