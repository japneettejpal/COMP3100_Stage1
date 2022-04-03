    import java.io.*;
    import java.net.*;
    import java.util.ArrayList;
    public class MyClient_Stage1 {

      private int serverNumber;
      private static String serverType="yes";
      private static int serverID=0;
      private static int serverCore;
      private static int jobID;
      private static int largestServerCount;
      private static int schedule;
      private static ArrayList < String > servers = new ArrayList < String > ();
      private static boolean flag = false;

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
 
              if(flag==true){
                for (int i=0;i<serverCount;i++){
                   str=in.readLine();
                   }
              }else if(flag !=true){              
                  for (int i = 0; i < serverCount; i++) {
                  str = in.readLine();
                  servers.add(str);
                  String serverInfo[] = str.split(" ");
                  //finding the largest server
                  if (Integer.parseInt(serverInfo[4]) > serverCore) {
                    serverType = serverInfo[0];
                    serverID = Integer.parseInt(serverInfo[1]);
                    serverCore = Integer.parseInt(serverInfo[4]);
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
             
              dout.write(("SCHD " + jobID + " " + serverType + " " + serverID + "\n").getBytes());
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
       if(flag!=true){
              for (int i=0; i<servers.size();i++){
              String string [] = servers.get(i).split(" ");
               if (serverType.equals(string[0]) && serverCore == Integer.parseInt(string[4])) {
                    largestServerCount++;
                    flag=true;
              }
              }
             }
             }
             //to schedule jobs we will find the remainder
              public static int LRR(int schedule){
              schedule = jobID % largestServerCount;
              serverID = schedule;
       return schedule;      
      }
    }