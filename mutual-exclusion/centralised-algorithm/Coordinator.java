import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
class Coordinator implements Runnable {
 ServerSocket serverSocket = null;
 iList queue = new iList();
 boolean resource = false;
 int clients = 0;
 java.util.List < Thread > list = new java.util.LinkedList < Thread > ();
 public static void main(String...args) {
  if (args.length != 1) {
   System.err.println("Usage: java Coordinator <port number>");
   System.exit(1);
  }
  int portNumber = Integer.parseInt(args[0]);
  //Establish Server Coordinator c = new Coordinator(); try { 
  c.serverSocket = new ServerSocket(Integer.parseInt(args[0]));
  Thread t = new Thread(c);
  t.start();
 } catch (IOException e) {
  System.out.println(message("Exception caught when trying to listen on port " +
   portNumber + " or listening for a connection"));
  System.out.println(e.getMessage());
  System.exit(1);
 }
 //accept new Processes as Clients while(true) { 
 try {
  ClientHandler client = new ClientHandler();
  client.clientSocket = c.serverSocket.accept();
  client.coordinator = c;
  client.out = new PrintWriter(client.clientSocket.getOutputStream(), true);
  client.in = new BufferedReader(new InputStreamReader(client.clientSocket.getInputStream()));
  client.id = ++c.clients;
  Thread t = new Thread(client);
  t.start();
  c.list.add(t);
  client.out.println(client.id);
  System.out.println(message("Connection to client " + client.id + " established."));
 } catch (Exception e) {
  e.printStackTrace();
 }
}
}
static String message(String msg) {
 return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] " + msg;
}
public void run() {
 try {
  while (true) {
   if (!queue.isEmpty() && !resource) {
    resource = true;
    ClientHandler client = queue.remove().client;
    System.out.println("Pending Requests in Queue: " + queue);
    System.out.println(message("Resource allocated to client " + client.id));
    client.out.println("OK");
   } else {
    Thread.sleep(50);
   }
  }
 } catch (Exception e) {
  e.printStackTrace();
 }
}
}
class ClientHandler implements Runnable {
 int id;
 Socket clientSocket;
 public PrintWriter out;
 public BufferedReader in ;
 Coordinator coordinator;
 public void run() {
  String inputLine = null;
  java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  while (true) {
   try {
    inputLine = in .readLine();
    System.out.println(message(inputLine));
    String[] input = inputLine.split(" ");
    if (input[0].equals("request"))
     coordinator.queue.add(new iNode(this, new Timestamp(formatter.parse(input[1] + " " + input[2]).getTime())));
    else coordinator.resource = false;
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
 }
 String message(String msg) {
  return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] Client " + id + ": " + msg;
 }
}
class iList {
 iNode head, tail;
 void add(iNode node) {
  if (head == null) head = tail = node;
  else {
   iNode ptr = tail;
   while (ptr != null && node.timestamp.before(ptr.timestamp)) ptr = ptr.prev;
   if (ptr == null) {
    head.prev = node;
    node.next = head;
    head = node;
   } else {
    node.next = ptr.next;
    node.prev = ptr;
    ptr.next = node;
    if (node.next != null) node.next.prev = node;
    else tail = node;
   }
  }
 }
 iNode remove() {
  iNode current = head;
  head = head.next;
  if (head != null) head.prev = null;
  return current;
 }
 boolean isEmpty() {
  return head == null;
 }
 public String toString() {
  String list = "";
  for (iNode ptr = head; ptr != null; ptr = ptr.next) {
   list += ("[" + ptr.client.id + ": " + new SimpleDateFormat("HH:mm:ss").format(ptr.timestamp) + "]\t");
  }
  return list;
 }
}
class iNode {
 public ClientHandler client;
 public Timestamp timestamp;
 public iNode prev, next;
 iNode(ClientHandler c, Timestamp t) {
  client = c;
  timestamp = t;
  prev = next = null;
 }
}
