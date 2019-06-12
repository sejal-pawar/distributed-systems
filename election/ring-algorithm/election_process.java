import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.Timestamp;
class Process {
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
   String id = in .readLine();
   if (args.length > 2) {
    System.out.println(message("Initiating Election. Message Sent: " + id));
    out.println(id);
   }
   while (true) {
    String inputLine = null;
    while ((inputLine = in .readLine()) == null);
    System.out.println(message("Message Received: " + inputLine));
    if (inputLine.contains("COORDINATOR")) {
     System.out.println("New Co-ordinator: " + inputLine.split("#")[0]);
     if (args.length == 2) {
      System.out.println(message("Message Sent: " + inputLine));
      out.println(inputLine);
     }
     break;
    } else {
     if (args.length > 2) {
      System.out.println(message("Determining Co-ordinator..."));
      String[] ids = inputLine.split("#");
      int greatest = -1;
      for (String i: ids) {
       int current = Integer.parseInt(i);
       if (current > greatest) greatest = current;
      }
      System.out.println(message("New Co-ordinator: " + greatest));
      System.out.println(message("Message Sent: " + (greatest + "#" + "COORDINATOR")));
      out.println(greatest + "#" + "COORDINATOR");
     } else {
      System.out.println(message("Message Sent: " + (inputLine + "#" + id)));
      out.println(inputLine + "#" + id);
     }
    }
   }
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
