import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class SingleServerAPV {

    private static final int SERVER_PORT = 6342;

    public static void main(String[] args){
        createCommunicationLoop();
    }// end main

    //Communication loop to connect to client and listen for commands and execute
    public static void createCommunicationLoop(){
        try{
            // Create Server Socket
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            System.out.println("Server started at " + new Date() + "\n");

            // listen for a connection
            // using a regular client socket
            Socket socket = serverSocket.accept();

            //now prepare to send and receive data on output streams
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());

            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

            String currentUser = "*****";
            boolean flag = false;


            // server loop listening for the client and responding
            while(true){
                String strReceived = inputFromClient.readUTF();
                System.out.println(strReceived);
                String[] strRecArray = strReceived.split(" ");
                String command = strRecArray[0];

                // LOGIN command
                if(command.equals("LOGIN") && !flag){
                    if(strRecArray.length == 3){
                        Scanner inFile;
                        try {
                            inFile = new Scanner(new File("logins.txt"));
                            String input;

                            while(inFile.hasNext()){
                                input = inFile.nextLine();
                                String[] inFileCredentials = input.split(" ");
                                if(strRecArray[1].equals(inFileCredentials[0]) && strRecArray[2].equals(inFileCredentials[1])){
                                    flag = true;
                                }
                            }
                            if(flag){
                                outputToClient.writeUTF("SUCCESS");
                                currentUser = strRecArray[1];
                            }
                            else {
                                outputToClient.writeUTF("Unsuccessful login. Username and Password not found.");
                            }
                            inFile.close();
                        }
                        catch (FileNotFoundException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else {
                        outputToClient.writeUTF("Failed to provide username and/or password.");
                    }
                }
                else if (command.equals("LOGIN")){
                    outputToClient.writeUTF("Failure to login: Somebody is already logged in");
                }// end LOGIN command
                //SOLVE command
                else if (command.equals("SOLVE") && flag) {
                    if(strRecArray.length == 1){
                        outputToClient.writeUTF("Error:\t" + "301 message format error: " + "No valid -c or -r flag found.");
                    }
                    // solve for circle flag
                    else if (strRecArray[1].equals("-c")) {
                        if(strRecArray.length == 3) {
                            double radius;
                            // try to convert string to double
                            try{
                                FileWriter fw = new FileWriter(currentUser+"_solutions.txt", true);
                                PrintWriter pw = new PrintWriter(fw);

                                radius = Double.parseDouble(strRecArray[2]);
                                String resultCircumference = circleCircumference(radius);
                                String resultArea= circleArea(radius);

                                outputToClient.writeUTF("Circle's circumference is " + resultCircumference +
                                        " and area is " + resultArea);
                                pw.println("radius " + Double.toString(radius) + ":\t" + "Circle's circumference is " + resultCircumference +
                                        " and area is " + resultArea);
                                pw.close();
                            }
                            // exception if string can not be converted to double
                            catch(NumberFormatException ex) {
                                outputToClient.writeUTF("Error:\t" + "301 message format error: " + "Invalid number for radius of a circle");
                            }
                            catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        else if(strRecArray.length == 2) {
                            FileWriter fw = new FileWriter(currentUser+"_solutions.txt", true);
                            PrintWriter pw = new PrintWriter(fw);

                            // display error if no radius was entered
                            outputToClient.writeUTF("Error:\t" + "No radius found");//301 error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            pw.println("Error:\t" + "No radius found");
                            pw.close();
                        }
                        else {
                            outputToClient.writeUTF("Error:\t" + "301 message format error: " + "No valid radius found. Please enter a single number for the radius");
                        }
                    }
                    // solve for rectangle flag
                    else if (strRecArray[1].equals("-r")) {
                        if(strRecArray.length == 2){
                            FileWriter fw = new FileWriter(currentUser+"_solutions.txt", true);
                            PrintWriter pw = new PrintWriter(fw);

                            outputToClient.writeUTF("Error:\t" + "No sides found"); //301 error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            pw.println("Error:\t" + "No sides found");
                            pw.close();
                        }
                        else if(strRecArray.length == 4) {

                            double side1, side2;
                            // try to convert string to double
                            try {
                                FileWriter fw = new FileWriter(currentUser+"_solutions.txt", true);
                                PrintWriter pw = new PrintWriter(fw);

                                side1 = Double.parseDouble(strRecArray[2]);
                                side2 = Double.parseDouble(strRecArray[3]);
                                String resultPerimeter = rectanglePerimeter(side1, side2);
                                String resultAreaRectangle = rectangleArea(side1, side2);

                                outputToClient.writeUTF("Rectangle's perimeter is " + resultPerimeter +
                                        " and area is " + resultAreaRectangle);
                                pw.println("sides " + side1 + ", " + side2 + ":\t" + "Rectangle's perimeter is " + resultPerimeter +
                                        " and area is " + resultAreaRectangle);
                                pw.close();
                            }
                            //exception if string can not be converted to double
                            catch(NumberFormatException ex) {
                                outputToClient.writeUTF("Error:\t" + "301 message error format: " + "Invalid number for one or both of the sides.");
                            }
                        }
                        else if(strRecArray.length == 3) {
                            double side1, side2;
                            //try to convert string to double
                            try {
                                FileWriter fw = new FileWriter(currentUser+"_solutions.txt", true);
                                PrintWriter pw = new PrintWriter(fw);
                                side1 = Double.parseDouble(strRecArray[2]);
                                side2 = side1;
                                String resultPerimeter = rectanglePerimeter(side1, side2);
                                String resultAreaRectangle = rectangleArea(side1, side2);

                                outputToClient.writeUTF("Rectangle's perimeter is " + resultPerimeter +
                                        " and area is " + resultAreaRectangle);
                                pw.println("sides " + side1 + ", " + side2 + ":\t" + "Rectangle's perimeter is " + resultPerimeter +
                                        " and area is " + resultAreaRectangle);
                                pw.close();
                            }
                            catch (NumberFormatException ex) {
                                outputToClient.writeUTF("Error:\t" + "301 message error format: " + "Invalid number for one or both sides.");
                            }
                            catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        else {
                            // invalid number of sides
                            outputToClient.writeUTF("Error:\t" + "301 message format error: " + "Invalid number of sides ");
                        }
                    }
                    else {
                        // no -c or -r flag found
                        outputToClient.writeUTF("Error:\t" + "301 message format error: " + "No valid -c or -r flag found.");
                    }
                }
                else if (command.equals("SOLVE")){
                    outputToClient.writeUTF("Error:\t" + "You are not logged in. Can't use SOLVE command.");
                }// end SOLVE command
                // LIST command
                else if (command.equals("LIST") && flag) {

                    if (currentUser.equals("root")) {
                        // list all solutions for all usernames in logins.txt
                        if(strRecArray.length == 2 && strRecArray[1].equals("-all")) {
                            ArrayList<String> users = new ArrayList<>();
                            getAllUsernames(users);
                            for(String names : users){
                                Scanner inFile;
                                try {
                                    inFile = new Scanner(new File( names + "_solutions.txt"));
                                    String input;

                                    outputToClient.writeUTF(names);
                                    File file = new File(names + "_solutions.txt");
                                    if (file.length() ==  0){
                                        outputToClient.writeUTF("\tNo interactions yet.");
                                    }
                                    else {
                                        while(inFile.hasNext()){
                                            input = inFile.nextLine();
                                            outputToClient.writeUTF("\t" + input);
                                        }
                                        inFile.close();
                                    }
                                }
                                catch (FileNotFoundException ex) {
                                    System.out.println(ex.getMessage());
                                }// end try-catch
                            }
                            outputToClient.writeUTF("END");
                        }
                        // list solutions for root only
                        else if (strRecArray.length == 1) {
                            Scanner inFile;
                            try {
                                inFile = new Scanner(new File( currentUser + "_solutions.txt"));
                                String input;

                                outputToClient.writeUTF(currentUser);
                                File file = new File(currentUser + "_solutions.txt");
                                if (file.length() ==  0){
                                    outputToClient.writeUTF("\tNo interactions yet.");
                                }
                                else {
                                    while(inFile.hasNext()){
                                        input = inFile.nextLine();
                                        outputToClient.writeUTF("\t" + input);
                                    }
                                    inFile.close();
                                }
                                outputToClient.writeUTF("END");
                            }
                            catch (FileNotFoundException ex) {
                                System.out.println(ex.getMessage());
                            }// end try-catch
                        }
                        else {
                            outputToClient.writeUTF("Error:\t" + "310 message error format: " + "Invalid format for LIST command.");
                        }
                    }
                    // list solutions for currentUser
                    else if (strRecArray.length == 1) {
                        Scanner inFile;
                        try {
                            inFile = new Scanner(new File( currentUser + "_solutions.txt"));
                            String input;

                            outputToClient.writeUTF(currentUser);
                            File file = new File(currentUser + "_solutions.txt");
                            if (file.length() ==  0){
                                outputToClient.writeUTF("\tNo interactions yet.");
                            }
                            else {
                                while(inFile.hasNext()){
                                    input = inFile.nextLine();
                                    outputToClient.writeUTF("\t" + input);
                                }
                                inFile.close();
                            }
                            outputToClient.writeUTF("END");
                        }
                        catch (FileNotFoundException ex) {
                            System.out.println(ex.getMessage());
                        }// end try-catch
                    }
                    else  {
                        outputToClient.writeUTF("Error:\t" + "310 message error format: " + "Invalid format for LIST command.");
                    }
                }
                else if (command.equals("LIST")) {
                    outputToClient.writeUTF("Error:\t" + "You are not logged in. Can't use LIST command.");
                }// end LIST command
                // LOGOUT command
                else if(command.equals("LOGOUT") && strRecArray.length == 1) {
                    if(flag) {
                        flag = false;
                        outputToClient.writeUTF("200 OK");
                    }
                    else {
                        outputToClient.writeUTF("Error:\t"+ "Nobody is logged in.");
                    }
                }// end LOGOUT command
                //SHUTDOWN command
                else if(command.equals("SHUTDOWN")){
                    System.out.println("Shutting down server...");
                    outputToClient.writeUTF("200 OK");
                    serverSocket.close();
                    break; // get out of while loop
                }// end SHUTDOWN command
                else {
                    System.out.println("Error:\t" + "300 invalid command");
                    outputToClient.writeUTF("Error:\t" + "300 invalid command");
                }
            } // end server loop
        }
        catch(IOException ex){
            ex.printStackTrace();
        } //end try-catch
    }// end createCommunicationLoop

    //Pre-condition: user requests to solve circle with valid radius
    //Post-condition: returns the calculated area for the requested circle as result
    public static String circleArea(double r) {
        String result;
        double area = r * r * Math.PI;
        result = String.format("%.2f", area);
        return result;
    }// end circleArea

    //Pre-condition: user requests to solve circle with valid radius
    //Post-condition: returns the calculated circumference for the requested circle as result
    public static String circleCircumference(double r) {
        String result;
        double circumference = 2 * r * Math.PI;
        result = String.format("%.2f", circumference);
        return result;
    }// end circleCircumference

    //Pre-condition: user requests to solve rectangle with valid sides
    //Post-condition: returns the calculated perimeter for the requested rectangle as result
    public static String rectanglePerimeter(double s1, double s2) {
        String result;
        double perimeter = 2 * (s1 + s2);
        result = String.format("%.2f", perimeter);
        return result;
    }// end rectanglePerimeter

    //Pre-condition: user requests to solve rectangle with valid sides
    //Post-condition: returns the calculated area for the requested rectangle as result
    public static String rectangleArea(double length, double width) {
        String result;
        double area = length * width;
        result = String.format("%.2f", area);
        return result;
    }// end rectangleArea

    //Pre-condition: communication loop is active, root requests LIST -all
    //               Array List is declared
    //Post-condition: fills ArrayList with all the usernames in logins.txt
    public static void getAllUsernames (ArrayList<String> un) {
        Scanner inFile;
        try {
            inFile = new Scanner(new File("logins.txt"));
            String input;

            while(inFile.hasNext()){
                input = inFile.nextLine();
                String[] user = input.split(" ");
                un.add(user[0]);
            }
            inFile.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }// end try-catch
    }// end getAllUsernames
}