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
public class Kitchen {
  private final static String QUEUE_NAME = "zamowienia";
  private final static String EXCHANGE_NAME = "odbior";
    
    public static void main(String[] args) throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://umxkhrem:p5NKwAZnIE1RKgVQXXhb2FEetT6sd1qA@puma.rmq.cloudamqp.com/umxkhrem");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        final Channel channel2 = connection.createChannel();
        channel2.exchangeDeclare(EXCHANGE_NAME, "fanout");
        
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [K] Odebrano zamowienie na '" + message + "'");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                channel2.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
