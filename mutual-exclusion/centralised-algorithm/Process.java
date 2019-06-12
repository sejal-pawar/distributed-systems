import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
class Process {
 public int pid;
 PrintWriter out;
 BufferedReader in ;
 Process(int pid, PrintWriter out, BufferedReader in ) {
  this.pid = pid;
  this.out = out;
  this.in = in ;
  running();
 }
 void running() {
  try {
   java.util.Random random = new java.util.Random();
   while (true) {
    boolean flag = true;
    long time = System.currentTimeMillis();
    System.out.println(message("Busy Processing."));
    int randomN1 = random.nextInt(10000);
    while (System.currentTimeMillis() - time != randomN1) {}
    if (flag) {
     out.println("request " + (new Timestamp(System.currentTimeMillis())));
     System.out.println(message("Request for resource sent. Waiting for reply from Co-ordinator."));
     while ( in .readLine() == null) {}
     System.out.println(message("Resource locked."));
     int randomN2 = random.nextInt(10000);
     long currTime = System.currentTimeMillis();
     while (System.currentTimeMillis() - currTime != randomN2) {}
     out.println("release");
     System.out.println(message("Resource Released."));
     flag = false;
    }
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
 String message(String msg) {
  return (new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))) + ": " + msg;
 }
 public static void main(String[] args) throws IOException {
  if (args.length != 2) {
   System.err.println("Usage: java Process <host name> <port number>");
   System.exit(1);
  }
  String hostName = args[0];
  int portNumber = Integer.parseInt(args[1]);
  try (
   Socket echoSocket = new Socket(hostName, portNumber); PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
  ) {
   int id = Integer.parseInt( in .readLine());
   System.out.println("This is Client " + id + "!");
   Process p = new Process(id, out, in );
  } catch (UnknownHostException e) {
   System.err.println("Don't know about host " + hostName);
   System.exit(1);
  } catch (IOException e) {
   System.err.println("Couldn't get I/O for the connection to " +
    hostName);
   System.exit(1);
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}
