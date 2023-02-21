package service;

import com.rabbitmq.client.*;
import model.SwipeDetails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static final String HOST = "54.245.155.254";
    private static final String QUEUE_NAME = "swipes";

    private final Connection connection;
    private final BlockingQueue<Channel> channels;

    public RabbitMQ(int poolSize) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername("admin");
        factory.setPassword("123");
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        channels = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Channel channel = null;
            try {
                channel = connection.createChannel();
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            channels.add(channel);
        }
    }

    public void postMessage(SwipeDetails details) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream objects = new ObjectOutputStream(bytes)) {
            objects.writeObject(details);

            Channel channel = channels.take();
            channel.basicPublish("", QUEUE_NAME, null, bytes.toByteArray());
            channels.add(channel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void handleMessage(DeliverCallback callback) {
        Channel channel = null;
        try {
            channel = channels.take();
            channel.basicConsume(QUEUE_NAME, true, callback, s -> {});
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
