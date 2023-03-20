package service;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.GsonSingleton;
import model.SwipeDetails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static final String HOST = "52.42.201.74";
    private static final String QUEUE_NAME = "swipes";

    private final BlockingQueue<Channel> channels;
    private static final Gson GSON = GsonSingleton.getInstance();
    private static final AMQP.BasicProperties PROPERTIES = new AMQP.BasicProperties.Builder().deliveryMode(2).build();

    public RabbitMQ(int poolSize) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection;
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
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            channels.add(channel);
        }
    }

    public void postMessage(SwipeDetails details) throws InterruptedException, IOException {
        Channel channel = channels.take();
        channel.basicPublish("", QUEUE_NAME, PROPERTIES, GSON.toJson(details).getBytes(StandardCharsets.UTF_8));
        channels.add(channel);
    }

    public void handleMessage(DeliverCallback callback) {
        Channel channel = null;
        try {
            channel = channels.take();
            channel.basicConsume(QUEUE_NAME, true, callback, s -> {
            });
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
