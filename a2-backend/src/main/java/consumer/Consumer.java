package consumer;

import model.SwipeDetails;
import service.RabbitMQ;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer {
    private static final RabbitMQ QUEUE = new RabbitMQ(1);
    private static final ConcurrentMap<String, Integer> lefts = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Integer> rights = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, List<String>> likes = new ConcurrentHashMap<>();
    private static final AtomicInteger handled = new AtomicInteger(0);

    public static void main(String[] args) {
        QUEUE.handleMessage((s, delivery) -> {
            ByteArrayInputStream bytes = new ByteArrayInputStream(delivery.getBody());
            ObjectInputStream objects = new ObjectInputStream(bytes);
            SwipeDetails details = null;
            try {
                details = (SwipeDetails) objects.readObject();
                System.out.println(details);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            String swiper = details.swiper;
            String swipee = details.swipee;

            if (details.left) {
                lefts.put(swiper, lefts.getOrDefault(swiper, 0) + 1);
            } else {
                rights.put(swiper, rights.getOrDefault(swiper, 0) + 1);
                if (!likes.containsKey(swiper)) {
                    likes.put(swiper, new ArrayList<>());
                }
                likes.get(swiper).add(swipee);
            }

            System.out.println("Handled messages: " + handled.incrementAndGet());
        });
    }
}
