package ua.com.alevel.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppMain {

    public static void main(String[] args) {

        Race race = new Race(1000);

        Race.Horse[] horses = new Race.Horse[10];
        
        for (int i = 0; i < 10; i++) {
            horses[i] = new Race.Horse("Horse " + (i + 1));
        }

        for (Race.Horse horse : horses) {
            horse.addToRace(race);
        }

        System.out.println("Choose the number of horse to bet on(1-10)");

        int horseNumber;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                if (!((horseNumber = Integer.parseInt(reader.readLine())) > horses.length || horseNumber <= 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("No horse with such number. Choose the number of horse to bet on(1-10)");
        }

        Race.Horse chosen = horses[horseNumber - 1];

        race.start();

        int place = race.getPlace(chosen);
        if (place == 1) {
            System.out.println("Congratulations! You won.");
        } else System.out.println("You lost(");
        System.out.println("Chosen horse finished " + place + " place");
    }
}
