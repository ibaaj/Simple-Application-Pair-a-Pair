import java.net.*;
import java.io.*;
import java.util.*;


class Server extends Thread {
    private static boolean serverContinue = true;
    private static HashMap<Socket, String> clientsMap = new HashMap<Socket, String>();
    private Socket clientSocket;

    @SuppressWarnings("unchecked")
    private Server(Socket clientSoc) {


        String URLUDPONECLIENT = "";
        try {
            ObjectInputStream in = new ObjectInputStream(clientSoc.getInputStream());
            URLUDPONECLIENT = (String) in.readObject();
            System.out.println("Le serveur a bien reçu l'adresse UDP " + URLUDPONECLIENT);

        } catch (Exception ex) {
            System.out.println("Erreur lors de la communication de l'url UDP du client :(");
        }


        clientsMap.put(clientSoc, URLUDPONECLIENT);

        sendClientMap();

        System.out.println(clientsMap);
        clientSocket = clientSoc;

        start();
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("Syntax: java Server PORT");
            System.exit(1);
        }
        int serverPort = Integer.parseInt(args[0]);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Création de la connexion Socket:10008");
            try {
                while (serverContinue) {
                    serverSocket.setSoTimeout(10000);
                    System.out.println("En attente de connexion....");
                    try {
                        new Server(serverSocket.accept());
                    } catch (SocketTimeoutException ste) {
                        System.out.println("Temps écoulé");
                    }
                }
            } catch (IOException e) {
                System.err.println("Echec d'acceptation");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Impossible d'écouter le port 10008.");
            System.exit(1);
        } finally {
            try {
                System.out.println("Fermeture de la connexion socket ");
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Impossible de fermer la connexion au port 10008.");
                System.exit(1);
            }
        }
    }

    private static void sendClientMap() {

        ArrayList<String> data = new ArrayList<String>();

        for (String udpserv : clientsMap.values())
            data.add(udpserv);

        Iterator it = clientsMap.keySet().iterator();

        while (it.hasNext()) {
            try {
                Socket socket = (Socket) it.next();

                if (socket.isClosed())
                    continue;

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(data);
                out.flush();
            } catch (SocketException ignored) {
            } catch (ConcurrentModificationException ignored) {
            } catch (IOException ignored) {
            }
        }
    }

    public void run() {

        System.out.println("Ikezukuri d'un nouveau thread de communication"); // nouveau client

        try {
            String line;
            BufferedReader bufRead = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while ((line = bufRead.readLine()) != null) {
                if (line.equals("FIN")) {

                    System.out.println("Le client " +  clientSocket.getInetAddress() + ":"
                                          + clientSocket.getPort()
                                            + " a bien reçu ses 5 messages, et préviens le serveur");
                    break;
                }
            }
            clientsMap.remove(clientSocket);
            clientSocket.close();
            System.out.println("un client est parti ... sniiiiif");
            sendClientMap(); //envoi de la liste des clients

        } catch (IOException ignored) {
        }


    }

}
