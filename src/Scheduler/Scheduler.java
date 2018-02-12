package Scheduler;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Scheduler {


    private String csvFile;
    private int[] pID;
    private int[] burst_time;
    private int[] priority;
    DecimalFormat df = new DecimalFormat("#.##");

    public Scheduler(int[] pID, int[] burst_time, int[] priority, String fileName) {
        this.pID = pID;
        this.burst_time = burst_time;
        this.priority = priority;
        this.csvFile = fileName.replace(".txt",".csv");
    }

    public void firstComeFirstServe() throws Exception {
        String fullCSV = "FIFO-";
        fullCSV += csvFile;
        FileWriter writer = new FileWriter(fullCSV);
        CSVFormatter.writeLine(writer, Arrays.asList("CpuTime","PID","StartingBurstTime","EndingBurstTime","CompletionTime"));
        int CPUtime = 0;
        for(int i = 0; i < pID.length; i++) {
            List<String> fifoWriter = new ArrayList<>();
            fifoWriter.add(String.valueOf(CPUtime));
            fifoWriter.add(String.valueOf(pID[i]));
            fifoWriter.add(String.valueOf(burst_time[i]));
            fifoWriter.add("0");
            CPUtime += burst_time[i] + 3;
            fifoWriter.add(String.valueOf(CPUtime));
            CSVFormatter.writeLine(writer,fifoWriter);
        }
        double averageRunTime = (double) CPUtime/pID.length;
        String formatted = df.format(averageRunTime);
        CSVFormatter.writeLine(writer,Arrays.asList(formatted));
        writer.flush();
        writer.close();
    }
    public void shortestJobFirst() throws Exception {
        int[] sPID = Arrays.copyOf(pID,pID.length);
        int[] sBurst = Arrays.copyOf(burst_time,burst_time.length);

        for(int i = (sBurst.length -1); i >= 0; i--) {
            for(int j = 1; j <= i; j++) {
                if(sBurst[j-1] > sBurst[j]) {
                    int temp = sBurst[j - 1];
                    sBurst[j - 1] = sBurst[j];
                    sBurst[j] = temp;
                    temp = sPID[j - 1];
                    sPID[j - 1] = sPID[j];
                    sPID[j] = temp;
                }
            }
        }

        String fullCSV = "SJF-";
        fullCSV += csvFile;
        FileWriter writer = new FileWriter(fullCSV);
        CSVFormatter.writeLine(writer, Arrays.asList("CpuTime","PID","StartingBurstTime","EndingBurstTime","CompletionTime"));
        int CPUtime = 0;
        for(int i = 0; i < sPID.length; i++) {
            List<String> fifoWriter = new ArrayList<>();
            fifoWriter.add(String.valueOf(CPUtime));
            fifoWriter.add(String.valueOf(sPID[i]));
            fifoWriter.add(String.valueOf(sBurst[i]));
            fifoWriter.add("0");
            CPUtime += sBurst[i] + 3;
            fifoWriter.add(String.valueOf(CPUtime));
            CSVFormatter.writeLine(writer,fifoWriter);
        }
        double averageRunTime = (double) CPUtime/pID.length;
        String formatted = df.format(averageRunTime);
        CSVFormatter.writeLine(writer,Arrays.asList(formatted));
        writer.flush();
        writer.close();
    }
    public void roundRobin(int quantum) throws Exception{
        int lastProcess = -1;
        int check = burst_time.length;
        int[] changeableBurst = Arrays.copyOf(burst_time,burst_time.length);
        String fullCSV = "RR";
        fullCSV += quantum;
        fullCSV += "-";
        fullCSV += csvFile;
        FileWriter writer = new FileWriter(fullCSV);
        CSVFormatter.writeLine(writer, Arrays.asList("CpuTime","PID","StartingBurstTime","EndingBurstTime","CompletionTime"));
        int CPUtime = 0;
        boolean RRComplete = false;
        while(!RRComplete) {
            for(int i = 0; i < burst_time.length; i++) {
                if(changeableBurst[i] == 0) {
                    continue;
                }
                List<String> fifoWriter = new ArrayList<>();
                fifoWriter.add(String.valueOf(CPUtime));
                fifoWriter.add(String.valueOf(pID[i]));
                fifoWriter.add(String.valueOf(changeableBurst[i]));
                if(changeableBurst[i] - quantum <= 0) {
                    CPUtime = CPUtime + changeableBurst[i] + 3;
                    changeableBurst[i] = 0;
                    fifoWriter.add(String.valueOf(changeableBurst[i]));
                    check--;
                    fifoWriter.add(String.valueOf(CPUtime));
                }
                else {
                    changeableBurst[i] = changeableBurst[i] - quantum;
                    if (lastProcess == pID[i]) {
                        CPUtime += quantum;

                    } else {
                        CPUtime += (quantum + 3);
                    }
                    fifoWriter.add(String.valueOf(changeableBurst[i]));
                }
                lastProcess = pID[i];
                CSVFormatter.writeLine(writer,fifoWriter);
                if(check == 0) {
                    RRComplete = true;
                }
            }
        }
        double averageRunTime = (double) CPUtime/pID.length;
        String formatted = df.format(averageRunTime);
        CSVFormatter.writeLine(writer,Arrays.asList(formatted));
        writer.flush();
        writer.close();
    }
    public void lottery(int quantum) throws Exception{
        int priorityLimit = 0;
        for(int i = 0; i < priority.length; i++) {
            priorityLimit += priority[i];
        }
        int lastProcess = -1;
        int check = burst_time.length;
        int[] changeableBurst = Arrays.copyOf(burst_time,burst_time.length);
        String fullCSV = "Lottery";
        fullCSV += quantum;
        fullCSV += "-";
        fullCSV += csvFile;
        FileWriter writer = new FileWriter(fullCSV);
        CSVFormatter.writeLine(writer, Arrays.asList("CpuTime","PID","StartingBurstTime","EndingBurstTime","CompletionTime"));
        int CPUtime = 0;
        boolean lotteryComplete = false;
        Random rd = new Random();
        int ticket;
        while(!lotteryComplete) { /* Current problem, how do you remove a process from being chosen */
            int chosen = 0;
            int pick = 0;
            ticket = rd.nextInt(priorityLimit) + 1; //From 1 to priorityLimit(Summation of priorities)
            for(int i = 0; i < priority.length; i++) {
                if(changeableBurst[i] == 0) { //Checks to see if this process is completed, if completed move on to the next process
                    continue;
                }
                pick += priority[i]; //Add the value in priority
                if(pick >= ticket) { //Checks if this value is greater than the ticket
                    chosen = i; //If it is, we are choosing this process
                    break; //Break out of for loop
                }
            }
            List<String> fifoWriter = new ArrayList<>();
            fifoWriter.add(String.valueOf(CPUtime));
            fifoWriter.add(String.valueOf(pID[chosen]));
            fifoWriter.add(String.valueOf(changeableBurst[chosen]));
            if(changeableBurst[chosen] - quantum <= 0) {
                CPUtime += changeableBurst[chosen];
                changeableBurst[chosen] = 0;
                fifoWriter.add(String.valueOf(changeableBurst[chosen]));
                check--;
                fifoWriter.add(String.valueOf(CPUtime));
                priorityLimit -= priority[chosen];
            }
            else {
                changeableBurst[chosen] = changeableBurst[chosen] - quantum;
                if (lastProcess == pID[chosen]) {
                    CPUtime += quantum;

                } else {
                    CPUtime += (quantum + 3);
                }
                fifoWriter.add(String.valueOf(changeableBurst[chosen]));
            }
            lastProcess = pID[chosen];
            CSVFormatter.writeLine(writer,fifoWriter);
            if(check == 0) {
                lotteryComplete = true;
            }
        }
        double averageRunTime = (double) CPUtime/pID.length;
        String formatted = df.format(averageRunTime);
        CSVFormatter.writeLine(writer,Arrays.asList(formatted));
        writer.flush();
        writer.close();
    }
}

