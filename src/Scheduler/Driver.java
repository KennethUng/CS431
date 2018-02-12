package Scheduler;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Driver {

    public static void main (String [] args) throws Exception {



        boolean input = false;
        while (!input) { //As long as valid, will keep running
            LinkedList<Integer> processID = new LinkedList<Integer>();
            LinkedList<Integer> burstTime = new LinkedList<Integer>();
            LinkedList<Integer> priority = new LinkedList<Integer>();
            Scanner sc = new Scanner(System.in);
            System.out.println("File Name: ");
            String fileName = sc.nextLine();
            try {
                sc = new Scanner(new File(fileName));
                while (sc.hasNext()) {
                    processID.add(sc.nextInt()); //Holds all of the process IDs
                    burstTime.add(sc.nextInt()); //Holds all of the burst Times
                    priority.add(sc.nextInt());  // Holds all of the priorities

                }
                int[] pID = new int[processID.size()];
                int[] burst_time = new int[burstTime.size()];
                int[] priority_level = new int[priority.size()];
                for (int i = 0; i < processID.size(); i++) {
                    pID[i] = processID.get(i);
                    burst_time[i] = burstTime.get(i);
                    priority_level[i] = priority.get(i);
                }
                Scheduler scheduler = new Scheduler(pID, burst_time, priority_level, fileName);
                scheduler.firstComeFirstServe();
                System.out.println("----FIFO SUCCESS----");
                scheduler.shortestJobFirst();
                System.out.println("----SJF SUCCESS----");
                scheduler.roundRobin(25);
                System.out.println("----RR25 SUCCESS----");
                scheduler.roundRobin(50);
                System.out.println("----RR50 SUCCESS----");
                scheduler.lottery(50);
                System.out.println("----Lottery50 SUCCESS----");
            } catch (FileNotFoundException notFound) {
                input = true;
                System.out.println("Unable to open file " + fileName);
            }
        }
    }
}
