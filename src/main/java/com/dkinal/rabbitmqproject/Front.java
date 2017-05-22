/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dkinal.rabbitmqproject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author student
 */
public class Front {
  private final static String QUEUE_NAME = "zamowienia";
  private static final String EXCHANGE_NAME = "odbior";
    
    public static void main(String[] args) throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://umxkhrem:p5NKwAZnIE1RKgVQXXhb2FEetT6sd1qA@puma.rmq.cloudamqp.com/umxkhrem");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        Channel channel2 = connection.createChannel();
        channel2.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel2.queueDeclare().getQueue();
        channel2.queueBind(queueName, EXCHANGE_NAME, "");
        
        Consumer consumer = new DefaultConsumer(channel2) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" [F] Odebrano '" + message + "'");
            }
        };
        channel2.basicConsume(queueName, true, consumer);
        
        try {
            while(true) {
                String message = "hamburger";
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                Thread.sleep(2000);
            }
        } catch (InterruptedException ex) {
            System.out.println("interrupted");
        }
    }
}
