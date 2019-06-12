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
   String[] input = in .readLine().split("#");
   int myId = Integer.parseInt(input[0]);
   int n = Integer.parseInt(input[1]);
   System.out.println(message("I am Process #" + myId));
   Flag flag = new Flag();
   new Thread(new Runnable() {
    public void run() {
     java.util.Random random = new java.util.Random();
     while (true) {
      try { //CPU Burst long time = System.currentTimeMillis(); System.out.println(message("Busy Processing.")); int randomN1 = random.nextInt(10000); Thread.sleep(randomN1); 
       //I/O Burst flag.needResource = true; System.out.println(message("Need Resource.")); flag.reqTimestamp = new Timestamp(System.currentTimeMillis()); 
       out.println("*#" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(flag.reqTimestamp)));
       while (!flag.usingResource) Thread.sleep(500); //Wait for OKs randomN1 = random.nextInt(10000); System.out.println(message("All OKs Received. Resource Locked for "+randomN1)); 
       Thread.sleep(randomN1);
       System.out.println(message("Resource Released."));
       flag.usingResource = false;
       while (!flag.queue.isEmpty()) out.println(flag.queue.remove() + "#" + "OK");
      } catch (Exception e) {
       e.printStackTrace();
      }
     }
    }
   }).start();
   new Thread(new Runnable() {
    public void run() {
     DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     int count = 0;
     while (true) {
      try {
       String inputLine = null;
       while ((inputLine = in .readLine()) == null);
       String[] input = inputLine.split("#");
       int sender = Integer.parseInt(input[0]);
       if (input[1].equals("OK")) {
        System.out.println(message("Received OK from " + sender + ". Waiting for OKs from " + (n - ++count) + " processes."));
        if (n == count) {
         flag.usingResource = true;
         count = 0;
         flag.needResource = false;
        }
       } else {
        if (!flag.needResource && !flag.usingResource) {
         out.println(sender + "#" + "OK");
        } else if (flag.usingResource) {
         flag.queue.add(sender);
        } else if (flag.needResource) {
         Timestamp timestamp = new Timestamp(formatter.parse(input[1]).getTime());
         if (flag.reqTimestamp.before(timestamp)) flag.queue.add(sender);
         else out.println(sender + "#" + "OK");
        }
       }
      } catch (Exception e) {
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
 Timestamp reqTimestamp;
 boolean needResource, usingResource;
 java.util.Queue < Integer > queue = new java.util.LinkedList < Integer > ();
}
