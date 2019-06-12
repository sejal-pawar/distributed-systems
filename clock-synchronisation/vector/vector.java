import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.Timestamp;
class Process {
 static String tableToString(int[] table) {
  String sTable = "\n";
  for (int i = 0; i < table.length; i++) sTable += "P" + i + "\t";
  sTable += "\n=====================\n";
  for (int i = 0; i < table.length; i++) sTable += (table[i] + "\t");
  sTable += "\n";
  return sTable;
 }
 public static void main(String...args) {
  if (args.length < 2) {
   System.err.println("Usage: java Process <host name> <port number> [Optional Election Started]");
   System.exit(1);
  }
  String hostName = args[0];
  int portNumber = Integer.parseInt(args[1]);
  try {
   Socket echoSocket = new Socket(hostName, portNumber);
   PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
   BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
   String[] input = in .readLine().split("#");
   int id = Integer.parseInt(input[0]), n = Integer.parseInt(input[1]);
   int[] table = new int[n];
   new Thread(new Runnable() {
    public void run() {
     while (true) {
      try {
       String msg = in .readLine();
       String[] iTable = msg.split("#");
       System.out.println(message("Incoming Message: " + msg));
       for (int i = 0; i < n; i++)
        table[i] = Math.max(Integer.parseInt(iTable[i]), table[i]);
       table[id]++;
       System.out.println(message("Updated Table: " + tableToString(table)));
       Thread.sleep(1000);
      } catch (IOException e) {
       e.printStackTrace();
      }
     }
    }
   }).start();
   new Thread(new Runnable() {
    public void run() {
     java.util.Random random = new java.util.Random();
     while (true) {
      try {
       int typeOfEvent = random.nextInt(50) % 2, sender = -1;
       table[id]++;
       if (typeOfEvent == 1) {
        sender = random.nextInt(n);
        String sTable = "";
        for (int i: table) sTable += (i + "#");
        out.println(sTable);
       }
       System.out.println(message(((typeOfEvent == 1) ? "External Event" : "Internal Event") + " occured.\nUpdated Table:\n" + tableToString(table) + (typeOfEvent == 1 ? ("Message sent to \n" + sender) : "\n")));
       Thread.sleep(5000);
      } catch (InterruptedException e) {
       e.printStackTrace();
      }
     }
    }
   }).start();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
 static String message(String msg) {
  return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] " + msg;
 }
}
class Flag {
 boolean election, stopReceieved;
 java.util.Queue < String > queue = new java.util.LinkedList < String > ();
}
