import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.Timestamp;
class Process {
 public static void main(String...args) {
  if (args.length != 2) {
   System.err.println("Usage: java Process <host name> <port number>");
   System.exit(1);
  }
  String hostName = args[0];
  int portNumber = Integer.parseInt(args[1]);
  try {
   Socket echoSocket = new Socket(hostName, portNumber);
   PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
   BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
   String[] inputs = in .readLine().split("#");
   int id = Integer.parseInt(inputs[0]);
   int n = Integer.parseInt(inputs[1]);
   System.out.println(message("I am Process #" + id));
   java.util.Random random = new java.util.Random();
   Flag flag = new Flag();
   flag.election = random.nextInt(50) % 2 == 1;
   if (flag.election) {
    out.println("*#ELECTION");
    System.out.println(message("Contesting Elections."));
   } else System.out.println(message("Not Contesting Elections."));
   new Thread(new Runnable() {
    public void run() {
     while (true) {
      try {
       flag.queue.add( in .readLine());
      } catch (IOException e) {
       e.printStackTrace();
      }
     }
    }
   }).start();
   new Thread(new Runnable() {
    public void run() {
     int i = 0;
     while (true) {
      try {
       while (!flag.queue.isEmpty()) {
        String msg = flag.queue.remove();
        String msgParts[] = msg.split("#");
        if (msgParts[1].equals("COORDINATOR")) {
         System.out.println(message(msgParts[0] + " is Coordinator."));
         System.exit(0);
        } else if (msgParts[1].equals("ELECTION") && flag.election)
         out.println(msgParts[0] + "#STOP");
        else if (msgParts[1].equals("STOP")) {
         System.out.println(message("Stop Received. Lost Elections."));
         flag.stopReceieved = true;
        }
       }
       Thread.sleep(500);
       if (++i == 5 && flag.election && !flag.stopReceieved) {
        System.out.println(message("No Stop Received. Won Elections."));
        out.println("*#COORDINATOR");
        System.exit(0);
       }
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
