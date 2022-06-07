    //Name: Japneet Sachdev
    //StudentID: 45769125
    //Stage 1 Assessment

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
     
     
      //commands for the client -server to print
      private static String recieve = "RCVD: ";
      private static String first = "HELO\n";
      private static String second = "REDY\n";
      private static String third = "OK\n";
      private static String fourth = "AUTH ";
      private static String fifth = "NONE";
      private static String stop = "QUIT\n";
     

      public static void main(String[] args) {
        try {
          // default IP address and port
          Socket ClientSocket = new Socket("127.0.0.1", 50000);
          DataOutputStream outCommunication = new DataOutputStream(ClientSocket.getOutputStream());
          BufferedReader inCommunication = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));

          System.out.println("Target IP: " + ClientSocket.getInetAddress() + " Target Port: " + ClientSocket.getPort() +"\n");
          System.out.println("Connection Established" );
         
          //sending messages threeway-handshake
          outCommunication.write((first).getBytes());
          outCommunication.flush();
          String in_Com = inCommunication .readLine();
          System.out.println(in_Com);

          String username = System.getProperty("user.name");
          System.out.println("SENT: AUTH");
          outCommunication.write((fourth + username + "\n").getBytes());
          in_Com = inCommunication .readLine();
          System.out.println(recieve + in_Com);

          outCommunication.write((second).getBytes());
          outCommunication.flush();
          in_Com = inCommunication .readLine();


          //while str is not none keep runnning the loop
          while (!in_Com.equals("NONE")) {
            System.out.println(in_Com);
            System.out.println(recieve + in_Com);
            String[] jobInfo = in_Com.split(" ");
            jobID = Integer.parseInt(jobInfo[2]);

           if(jobInfo[0].equals("JOBN")){
           
              String GETSCAP = "GETS Capable: " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6] + "\n";
              outCommunication.write(GETSCAP.getBytes());
              outCommunication.flush();
             
              in_Com = inCommunication .readLine();
              System.out.println(recieve + in_Com);

              String[] simulator = in_Com.split(" ");
              int numSer = Integer.parseInt(simulator[1]);

              outCommunication.write((third).getBytes());
              outCommunication.flush();
 
              if(haveJob==true){
                for (int i=0;i<numSer;i++){
                   in_Com=inCommunication.readLine();
                   }
              }else if(haveJob !=true){              
                  for (int i = 0; i < numSer; i++) {
                  in_Com = inCommunication.readLine();
                //adding the largest server to lists
                  ServerLargestList.add(in_Com);
                  String sInfo[] = in_Com.split(" ");
                  //finding the largest server
                  if (Integer.parseInt(sInfo[4]) > ServerCore) {
                    ServerType = sInfo[0];
                    SerID = Integer.parseInt(sInfo[1]);
                    ServerCore = Integer.parseInt(sInfo[4]);
                  }
                 
                }
               
              }
              // Calculates the largest server count in the array list
              CountLargestServer();
             
              outCommunication.write((third).getBytes());
              outCommunication.flush();
              System.out.println(in_Com);
              in_Com = inCommunication.readLine();
              System.out.println(in_Com);
              System.out.println(recieve + in_Com);

              LRR(schedule);
              String dispatch = "SCHD " + jobID + " " + ServerType + " " + SerID + "\n";
              outCommunication.write(dispatch.getBytes());
              outCommunication.flush();
              in_Com = inCommunication .readLine();
              System.out.println(recieve + in_Com);

              outCommunication.write((second).getBytes());
              outCommunication.flush();
              in_Com = inCommunication .readLine();
              System.out.println(in_Com);
            } else if (jobInfo[0].equals("JCPL")) {  // if JCPL then send REDY to ds-server
              outCommunication.write((second).getBytes());
              outCommunication.flush();
              in_Com = inCommunication .readLine();
            }
          }

          System.out.println("SENT: QUIT");
          outCommunication.write((stop).getBytes());
          in_Com = inCommunication .readLine();
          System.out.println(recieve + in_Com);

          System.out.println("Connection is terminated\n");
          inCommunication .close();
          outCommunication.close();
          ClientSocket.close();

        } catch (Exception e) {
          System.out.println("Something is wrong"+e);
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

