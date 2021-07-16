package server;

import common.MsgLog;
import common.UserObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class UserHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final UserObj currentUser;
    private int healthNumber;
    private final ClientCallback clientCallback;

    public UserHandler(Socket socket, ClientCallback clientCallback) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        currentUser = new UserObj();
        this.clientCallback = clientCallback;
    }

    @Override
    public void run() {
        try {
            boolean loggedIn = false;

            while (!loggedIn) {
                String selectedOption = in.readLine();
                String password;
                if (selectedOption.equals(String.valueOf(MsgLog.SignUp.log))) {
                    healthNumber = Integer.parseInt(in.readLine());
                    password = in.readLine();
                    String name = in.readLine();
                    String county = in.readLine();

                    if (!ClientCRUD.getCountiesName(county)) {
                        out.println(MsgLog.InvalidCounty.log);
                  }  if(password.length()<6){
                        out.println(MsgLog.InvalidPassword.log);
                    }if(String.valueOf(healthNumber).length()!=9){
                        out.println(MsgLog.InvalidHealthNumber.log);
                    } else {
                        UserObj user = new UserObj(healthNumber,
                                password, name, county.toLowerCase(), false, new ArrayList<>());
                        boolean temp = ClientCRUD.addNewUser(user);
                        if (temp) {
                            loggedIn = true;
                            currentUser.setHealthNumber(healthNumber);
                            out.println(MsgLog.AccountCreated.log);
                        } else {
                            out.println(MsgLog.UserAlreadyExists.log);
                        }
                    }
                } else if (selectedOption.equals(String.valueOf(MsgLog.SignIn.log))) {
                    healthNumber = Integer.parseInt(in.readLine());
                    password = in.readLine();
                    if (ClientCRUD.checkLogin(healthNumber, password)) {
                        out.println(MsgLog.SucceedLogin.log);
                        loggedIn = true;
                        currentUser.setHealthNumber(healthNumber);
                    } else {
                        out.println(MsgLog.InvalidLogin.log);
                    }
                }

                out.println(ClientCRUD.getCounty(healthNumber));
                String received;
                while (!socket.isClosed()) {
                    received = String.valueOf(in.readLine());
                    if (received.equals(String.valueOf(MsgLog.DefinedAsInfected.log))) {
                        if (ClientCRUD.updateUserHealthState(currentUser)) {
                            clientCallback.onClientInfected(healthNumber);
                            out.println("Successfully updated health state!");
                        } else {
                            out.println("Error on updating health state!");
                        }
                    } else if (received.equals(String.valueOf(MsgLog.DefineContact.log))) {

                        if (ClientCRUD.defineContact(Integer.parseInt(in.readLine()), currentUser)) {
                            out.println("New contact added!");
                        } else {
                            out.println("Contact already exist on the list or the contact is not yet registered");
                        }
                    } else if (received.equals(String.valueOf(MsgLog.MakeTest.log))) {
                        out.println("Test result: " + ClientCRUD.resultOfAnTest(currentUser));
                    } else if (received.equals(String.valueOf(MsgLog.Logout.log))) {
                        loggedIn = false;
                        socket.close();
                    }
                    else if( received.equals(String.valueOf(MsgLog.YouHaveBeenInContactWithInfectedPerson.log))){
                        clientCallback.onClientInfected(healthNumber);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] client.Client disconnected");
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the contacts list of this client
     *
     * @param clientId - new infected client
     */
    public void notifyClientInfected(int clientId) {
        ArrayList<Integer> contactsList = ClientCRUD.getContactsList(this.healthNumber);
        boolean shouldNotify = contactsList.contains(clientId);
        if (shouldNotify) {
            out.println("You have been in contact with an infected person");
        }
        else {
            out.println("No notifications!");
        }
    }
}
