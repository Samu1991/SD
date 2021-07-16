package client;

import common.MsgLog;
import server.ClientCRUD;
import common.UserObj;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Client {

    public static void main(String[] args) throws IOException {
        String ip = Objects.requireNonNull(Server.getConfig("Unicast")).get(1);
        int port = Integer.parseInt(Objects.requireNonNull(Server.getConfig("Unicast")).get(0));

        Socket socket = new Socket(ip, port);

        try (socket) {
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            boolean loggedIn = false;
            UserObj user = new UserObj();

            while (!loggedIn) {
                loggedIn = userLoginMenu(keyboard, in, out, user);
            }

            printAllCountiesInfo();//Broadcast info
            printUserCountyInfo(in);// multicast info

            while (!socket.isClosed()) {
                userMenu(keyboard, out, in);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static void printSocketInfo(BufferedReader in) {
        new Thread(() -> {
            try {
                System.out.println(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void printUserCountyInfo(BufferedReader in) throws IOException {
        String county = in.readLine();
        ArrayList<Object> configsList;
        configsList = ClientCRUD.findCounty(county);

        if (configsList != null) {
            new Thread(new MulticastReceiver(String.valueOf(configsList.get(0)), Integer.parseInt(String.valueOf(configsList.get(1))))).start();
        }
    }

    private static void printAllCountiesInfo() {
        new Thread(new BroadcastReceiver()).start();
    }

    private static void userMenu(BufferedReader keyboard, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("\n**** Menu ****\n1 - Define close contact\n2 - Define as infected"
                + "\n3 - Realise Covid Test\n4 - See Notifications \n5 - Logout\nOption: ");

        String option = keyboard.readLine();
        switch (option) {
            case "1" -> {
                out.println(MsgLog.DefineContact.log);
                System.out.println("Contact Health Number: ");
                out.println(keyboard.readLine());
                System.out.println(in.readLine());
            }
            case "2" -> {
                out.println(MsgLog.DefinedAsInfected.log);
                System.out.println(in.readLine());
                System.out.println(in.readLine());
            }
            case "3" -> {
                out.println(MsgLog.MakeTest.log);
                System.out.println(in.readLine());
            }
            case "4" -> {
                out.println(MsgLog.YouHaveBeenInContactWithInfectedPerson.log);
                printSocketInfo(in); // check if the user have been in contact with an infected person
            }
            case "5" -> {
                System.out.println("Stay healthy :)");
                System.exit(0);
            }

            default -> System.out.println("Try the options above!");
        }
    }

    private static boolean userLoginMenu(BufferedReader keyboard, BufferedReader in, PrintWriter out, UserObj user) throws IOException {
        String bufferInput;
        System.out.println("*** Away From Me Covid ***\n1 - Sign In\n2 - Sign Up\n3 - Exit\n"
                + "Option: ");
        String option = keyboard.readLine();
        switch (option) {
            case "1" -> {
                out.println(MsgLog.SignIn.log);
                System.out.print("\nHealth Number: ");
                out.println(bufferInput = keyboard.readLine());
                user.setHealthNumber(Integer.parseInt(bufferInput));
                System.out.print("\nPassword: ");
                out.println(bufferInput = keyboard.readLine());
                user.setPassword(bufferInput);
                String msg = in.readLine();

                if (Integer.parseInt(msg) == MsgLog.InvalidLogin.log) {
                    System.out.println("Invalid Login. Check fields above\n");
                    return false;
                } else {
                    return true;
                }
            }
            case "2" -> {
                out.println(MsgLog.SignUp.log);
                System.out.print("\nHealth Number: ");
                out.println(bufferInput = keyboard.readLine());
                user.setHealthNumber(Integer.parseInt(bufferInput));
                System.out.print("Password: ");
                out.println(bufferInput = keyboard.readLine());
                user.setPassword(bufferInput);
                System.out.print("Name: ");
                out.println(bufferInput = keyboard.readLine());
                user.setName(bufferInput);
                System.out.print("County: ");
                out.println(bufferInput = keyboard.readLine());
                user.setCounty(bufferInput);
                user.setHealthState(false);

                String msg = in.readLine();
                if (Integer.parseInt(msg) == MsgLog.InvalidCounty.log) {
                    System.out.println("Invalid County! Try Again");
                }
                if (Integer.parseInt(msg) == MsgLog.AccountCreated.log) {
                    System.out.println("Account created!");
                    return true;
                }
                if (Integer.parseInt(msg) == MsgLog.UserAlreadyExists.log) {
                    System.out.println("This user already exists!");
                    return false;
                }
                if (Integer.parseInt(msg) == MsgLog.InvalidPassword.log){
                    System.out.println("Invalid Password");
                }
                if (Integer.parseInt(msg) == MsgLog.InvalidHealthNumber.log){
                    System.out.println("Invalid Health Number!");
                }
            }
            case "3" -> System.exit(0);
            default -> {
                System.out.println("Try the options above!");
                return false;
            }
        }
        return false;
    }
}
