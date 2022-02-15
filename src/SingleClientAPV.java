import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SingleClientAPV {

    private static final int SERVER_PORT = 6342;

    public static void main(String[] args){

        DataOutputStream toServer;
        DataInputStream fromServer;
        Scanner input = new Scanner(System.in);
        String message;

        // attempt to connect to the server
        try{
            Socket socket = new Socket("localhost", SERVER_PORT);

            // create input stream to receive data from server
            fromServer = new DataInputStream(socket.getInputStream());

            toServer = new DataOutputStream(socket.getOutputStream());

            // start sending and receiving messages to/from server
            while(true){
                System.out.println("Send command to server:\t");
                System.out.print("C:\t");
                message = input.nextLine();
                message = message.trim();
                toServer.writeUTF(message);
                if(message.equals("SHUTDOWN")){
                    message = fromServer.readUTF();
                    System.out.println("S:\t" + message);
                    socket.close();
                    break;
                }
                else if(message.equals("LIST") || message.equals("LIST -all")){
                    message = fromServer.readUTF();
                    if(message.equals("Error:\t" + "You are not logged in. Can't use LIST command.")
                            || message.equals("Error:\t" + "310 message error format: " + "Invalid format for LIST command.")){
                        System.out.println("S:\t" + message);
                    }
                    else{
                        System.out.println("S:\t" + message);

                        while (!message.equals("END")){
                            message = fromServer.readUTF();
                            if(!message.equals("END")){
                                System.out.println("\t" + message);
                            }
                        }
                    }
                }
                else {
                    // received message
                    message = fromServer.readUTF();
                    System.out.println("S:\t" + message);
                }
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }// end try-catch
    }// end main
}
