package ua.com.alevel.app;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class Race {

    private final int distance;

    private final Map<Horse, Integer> finished;

    private final Set<Horse> horses;

    private final AtomicInteger place;

    private final Phaser phaser;

    private final Random random;

    Race(int distance) {
        this.distance = distance;
        finished = new ConcurrentHashMap<>();
        horses = ConcurrentHashMap.newKeySet();
        place = new AtomicInteger();
        phaser = new Phaser(1);
        random = new Random();
    }

    int getPlace(Horse horse) {
        return finished.get(horse);
    }

    synchronized void start() {
        place.set(1);
        finished.clear();
        int numberOfHorses = horses.size();
        phaser.bulkRegister(numberOfHorses);
        for (Horse horse : horses) {
            new Thread(horse).start();
        }
        phaser.arriveAndAwaitAdvance();
        System.out.println("All horses Started the race!");
        phaser.arriveAndAwaitAdvance();
        System.out.println("All horses finished the race!");
    }

    public static class Horse implements Runnable {

        private final String name;

        private Race race;

        private int position;

        Horse(String name) {
            this.name = Objects.requireNonNull(name);
        }

        void addToRace(Race race) {
            removeFromRace();
            this.race = race;
            race.horses.add(this);
        }

        void removeFromRace() {
            if (race == null) return;
            race.horses.remove(this);
        }

        @Override
        public void run() {
            if (race == null) return;
            position = 0;

            System.out.println(name + " is ready!");
            race.phaser.arriveAndAwaitAdvance();

            int distance = race.distance;
            do {
                move();
                delay();
            } while (position < distance);

            int place = race.place.getAndIncrement();
            System.out.println(name + " has finished!");
            race.finished.put(this, place);
            race.phaser.arriveAndDeregister();
        }

        private void delay() {
            int sleep = randomize(500, 400);
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void move() {
            int move = Math.min(
                    randomize(200, 100),
                    race.distance - position
            );
            position += move;
            System.out.println(name + " moved " + move + " meters. Total: " + position);
        }

        private int randomize(int max, int min) {
            return race.random.nextInt(max - min + 1) + min;
        }
    }
}
