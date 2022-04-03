    import java.io.*;
    import java.net.*;
    import java.util.ArrayList;
    public class MyClient_Stage1 {

      private int ServerNum;
      private static String ServerType=" ";
      private static int SerID=0;
      private static int ServerCore;
      private static int jobID;
      private static int HighestSerCount;
      private static int schedule;
      private static ArrayList < String > ServerLargestList = new ArrayList < String > ();
      private static boolean haveJob = false;

      public static void main(String[] args) {
        try {
          Socket s = new Socket("127.0.0.1", 50000);
          DataOutputStream dout = new DataOutputStream(s.getOutputStream());
          BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

          System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
          System.out.println("Connection Established" );
         
          //sending messages threeway-handshake
          dout.write(("HELO\n").getBytes());
          dout.flush();
          String str = in .readLine();
          System.out.println(str);

          String username = System.getProperty("user.name");
          System.out.println("SENT: AUTH");
          dout.write(("AUTH " + username + "\n").getBytes());
          str = in .readLine();
          System.out.println("RCVD: " + str);

          dout.write(("REDY\n").getBytes());
          dout.flush();
          str = in .readLine();


          //while str is not none keep runnning the loop
          while (!str.equals("NONE")) {
            System.out.println(str);
            System.out.println("RCVD: " + str);
            String[] jobInfo = str.split(" ");
            jobID = Integer.parseInt(jobInfo[2]);

           if (jobInfo[0].equals("JOBN")) {

              dout.write(("GETS Capable: " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6] + "\n").getBytes());
              dout.flush();
              str = in .readLine();
              System.out.println("RCVD: " + str);

              String[] Info = str.split(" ");
              int serverCount = Integer.parseInt(Info[1]);

              dout.write(("OK\n").getBytes());
              dout.flush();
 
              if(haveJob==true){
                for (int i=0;i<serverCount;i++){
                   str=in.readLine();
                   }
              }else if(haveJob !=true){              
                  for (int i = 0; i < serverCount; i++) {
                  str = in.readLine();
                  ServerLargestList.add(str);
                  String serverInfo[] = str.split(" ");
                  //finding the largest server
                  if (Integer.parseInt(serverInfo[4]) > ServerCore) {
                    ServerType = serverInfo[0];
                    SerID = Integer.parseInt(serverInfo[1]);
                    ServerCore = Integer.parseInt(serverInfo[4]);
                  }
                 
                }
               
              }
             
               CountLargestServer();
             
              dout.write(("OK\n").getBytes());
              dout.flush();
              System.out.println(str);
              str = in.readLine();
              System.out.println(str);
              System.out.println("RCVD: " + str);

              LRR(schedule);
             
              dout.write(("SCHD " + jobID + " " + ServerType + " " + SerID + "\n").getBytes());
              dout.flush();
              str = in .readLine();
              System.out.println("RCVD: " + str);

              dout.write(("REDY\n").getBytes());
              dout.flush();
              str = in .readLine();
              System.out.println(str);
            } else if (jobInfo[0].equals("JCPL")) {  // if JCPL then send REDY to ds-server
              dout.write(("REDY\n").getBytes());
              dout.flush();
              str = in .readLine();
            }
          }

          System.out.println("SENT: QUIT");
          dout.write(("QUIT\n").getBytes());
          str = in .readLine();
          System.out.println("RCVD: " + str);

          in .close();
          dout.close();
          s.close();

        } catch (Exception e) {
          System.out.println(e);
        }
      }
     
      // counting how many largest servers are there
      public static void CountLargestServer(){
       if(haveJob!=true){
              for (int i=0; i<ServerLargestList.size();i++){
              String string [] = ServerLargestList.get(i).split(" ");
               if (ServerType.equals(string[0]) && ServerCore == Integer.parseInt(string[4])) {
                    HighestSerCount++;
                    haveJob=true;
              }
              }
             }
             }
             //to schedule jobs we will find the remainder
              public static int LRR(int schedule){
              schedule = jobID % HighestSerCount;
              SerID = schedule;
       return schedule;      
      }
    }