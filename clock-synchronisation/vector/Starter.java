import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.Timestamp;
class PseudoServer {
 static ArrayList < ClientHandler > list = new ArrayList < ClientHandler > ();
 public static void main(String...args) throws IOException {
  if (args.length != 1) {
   System.err.println("Usage: java PseudoServer <port number>");
   System.exit(1);
  }
  ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
  //accept new Processes as Clients int noOfClients = 3; for(int i=0; i<noOfClients; i++) { 
  try {
   ClientHandler client = new ClientHandler();
   Socket clientSocket = serverSocket.accept();
   client.out = new PrintWriter(clientSocket.getOutputStream(), true);
   client.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   client.id = list.size();
   list.add(client);
   System.out.println(message("Connection to client " + client.id + " established."));
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
 for (int i = 0; i < noOfClients; i++) {
  list.get(i).out.println(i + "#" + noOfClients);
  Thread t = new Thread(list.get(i));
  t.start();
 }
}
static String message(String msg) {
 return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] " + msg;
}
}
class ClientHandler implements Runnable {
 int id;
 public PrintWriter out;
 public BufferedReader in ;
 public void run() {
  String inputLine = null;
  int size = PseudoServer.list.size();
  while (true) {
   try {
    while ((inputLine = in .readLine()) == null);
    System.out.println(message(id + ": " + inputLine));
    for (int i = 0; i < size; i++)
     if (i != id) PseudoServer.list.get(i).out.println(inputLine);
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
 }
 String message(String msg) {
  return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] " + msg;
 }
}
