import java.io.*;
import java.net.*;
import java.text.*;
import java.sql.Timestamp;
class RingProcess {
 public static void main(String...args) throws IOException {
  if (args.length < 2) {
   System.err.println("Usage: java RingProcess <Previous Host> <Number> [Optional Token Ring]");
   System.exit(1);
  }
  PrintWriter nextOut;
  BufferedReader prevIn;
  if (args[1].equals("1")) {
   prevIn = connectToPrevious(args[0], 8090);
   nextOut = connectToNext(8091);
  } else {
   nextOut = connectToNext(8090);
   prevIn = connectToPrevious(args[0], 8091);
  }
  new Process(nextOut, prevIn, args.length > 2);
 }
 static BufferedReader connectToPrevious(String hostname, int portNumber) throws IOException {
  Socket echoSocket = null;
  System.out.println(message("Waiting for connection to previous."));
  while (echoSocket == null) {
   try {
    echoSocket = new Socket(hostname, portNumber);
   } catch (Exception e) {}
  }
  System.out.println(message("Previous socket: ") + echoSocket);
  return new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
 }
 static PrintWriter connectToNext(int portNumber) throws IOException {
  ServerSocket serverSocket = new ServerSocket(portNumber);
  System.out.println(message("Established Server. Waiting for Client"));
  Socket clientSocket = serverSocket.accept();
  System.out.println(message("Client: ") + clientSocket);
  return new PrintWriter(clientSocket.getOutputStream(), true);
 }
 static String message(String msg) {
  return "[" + (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + "] " + msg;
 }
}
class FlagVector {
 boolean tokenRing;
 boolean needResource;
 FlagVector(boolean tokenRing) {
  this.tokenRing = tokenRing;
 }
}
class Process {
 PrintWriter nextOut;
 BufferedReader prevIn;
 FlagVector vector;
 Process(PrintWriter nextOut, BufferedReader prevIn, boolean tokenRing) {
  this.nextOut = nextOut;
  this.prevIn = prevIn;
  vector = new FlagVector(tokenRing);
  Thread processing = new Thread(new Runnable() {
   public void run() {
    try {
     java.util.Random random = new java.util.Random();
     while (true) { //CPU Burst long time = System.currentTimeMillis(); System.out.println(message("Busy Processing.")); int randomN1 = random.nextInt(10000); Thread.sleep(randomN1); 
      //I/O Burst System.out.println(message("Need Resource.")); vector.needResource = true; //Wait for token Ring while(!vector.tokenRing) Thread.sleep(500); randomN1 = random.nextInt(10000); System.out.println(message("Token Ring Received. Resource Locked for "+randomN1)); Thread.sleep(randomN1); 
      System.out.println(message("Resource Released"));
      vector.needResource = false;
     }
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  });
  Thread tokenDealing = new Thread(new Runnable() {
   public void run() {
    while (true) {
     try {
      if (!vector.tokenRing) {
       prevIn.readLine();
       vector.tokenRing = true;
       System.out.println(message("Received Token."));
      }
      while (vector.needResource) Thread.sleep(500);
      vector.tokenRing = false;
      nextOut.println();
      System.out.println(message("Token Passed."));
     } catch (Exception e) {
      e.printStackTrace();
     }
    }
   }
  });
  processing.start();
  tokenDealing.start();
 }
 String message(String msg) {
  return (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + ": " + msg;
 }
}
