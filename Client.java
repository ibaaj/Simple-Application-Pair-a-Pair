import java.io.*;
import java.net.*;
import java.util.*;



class Client {
    private static String UDPURLClient = null;
    private static int UDPPortClient;
    private static Socket echoSocket = null;

    private static ArrayList<String> MsgReceived = new ArrayList<String>();

    private static ArrayList<String> clientsMap = new ArrayList<String>();
    private static ArrayList<String> IPPacketReceived = new ArrayList<String>();
    private static String message;


    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        if (args.length == 0 || args.length == 1 || args[0].lastIndexOf(':') == -1 || args[0].equals("-h")) {
            System.out.println("Syntax : java Client IPTCPSERVER:PORT UDPCLIENTPORT");
            System.out.println("Exit");
            System.exit(1);
        }

        String ipClient = null;
        try {
            ipClient = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Impossible de récupérer l'hôte du client :'( '");
        }

        UDPURLClient = ipClient + ":" + args[1];
        UDPPortClient = Integer.parseInt(args[1]);

        String TCPHostServer = args[0].substring(0, args[0].lastIndexOf(':'));
        int TCPPortServer = Integer.parseInt(args[0].substring(args[0].lastIndexOf(':') + 1));

        UdpServ();

        message = "Le message secret venant de " + UDPURLClient;

        try {
            System.out.println("T-Client>Tentative de connexion à l'hôte: " + TCPHostServer + " port: " + TCPPortServer);
            echoSocket = new Socket(TCPHostServer, TCPPortServer);

            ObjectOutputStream out = new ObjectOutputStream(echoSocket.getOutputStream());
            out.writeObject(UDPURLClient);
            out.flush();

            while (!echoSocket.isClosed()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(echoSocket.getInputStream());
                    clientsMap = (ArrayList<String>) ois.readObject();
                    System.out.println("T-Client>obtention de la liste des clients : \n " + clientsMap);
                    sendToAllClients();
                } catch (IOException e) {
                    System.out.println("IOE" + e);
                } catch (ClassNotFoundException e) {
                    System.out.println("CNFE" + e);
                }
            }
        } catch (IOException e) {
            System.out.println(" Connexion refusé à l'hôte: " + TCPHostServer + " port: " + TCPPortServer);
        }


    }

    private static void UdpServ() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    DatagramSocket serverSocket = new DatagramSocket(UDPPortClient);

                    System.out.println("U-Serveur>Initilisation du serveur udp du client : " + UDPURLClient);

                    byte[] receiveData;
                    byte[] sendData;

                    while (true) {
                        receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                        serverSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData());
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();

                        System.out.println("U-Serveur>Message de " + IPAddress + ":" + port + "@" + sentence);
                        sentence = message;

                        sendData = sentence.getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

                        System.out.println("U-Serveur>Envoi du message de notre serveur à " + IPAddress + ":" + port);

                        serverSocket.send(sendPacket);
                    }

                } catch (Exception ex) {
                    System.out.println("U-Serveur>Le Port UDP " + UDPPortClient + " est occupé :(.");
                    System.exit(1);
                }
            }
        });

        t.start();
    }

    private static void sendToAllClients() {

        clientsMap.removeAll(IPPacketReceived);


        for (int i = 0; i < clientsMap.size(); ++i) {
            String client = clientsMap.get(i);
            String host = client.substring(0, client.lastIndexOf(':'));
            int hostPort = Integer.parseInt(client.substring(client.lastIndexOf(':') + 1));
            String currentHost = host + ":" + hostPort;

            if (!UDPURLClient.equals(currentHost))
                UdpRequest(host, hostPort);

        }
    }

    private static void UdpRequest(String server, int port) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(server);

            byte[] sendData;
            byte[] receiveData = new byte[1024];

            String sentence = "[Demande] Le serveur " + UDPPortClient + " veut votre message.";
            sendData = sentence.getBytes();

            System.out.println("U-Client>Envoi de " + sendData.length + " bytes vers " + IPAddress + ":" + port);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            System.out.println("U-Client>Attente du packet de retour....");
            clientSocket.setSoTimeout(10000);


            clientSocket.receive(receivePacket);
            receiveData = receivePacket.getData();

            MsgReceived.add(new String(receiveData));

            System.out.println("U-Client>Le serveur "
                    + receivePacket.getAddress()
                    + ":" + receivePacket.getPort()
                    + " a envoyé :");
            System.out.println(new String(receiveData));


            String udpurl = receivePacket.getAddress() + ":" + receivePacket.getPort();
            IPPacketReceived.add(udpurl.substring(1));

            clientSocket.close();

            if (IPPacketReceived.size() == 5) {
                System.out.println("T-Client> Envoi du signal de fin de traitement au serveur...");
                new PrintWriter(echoSocket.getOutputStream(), true).println("FIN");
                echoSocket.close();

                System.out.println("Client>Liste des messages reçus : ");
                for (int i = 0; i < MsgReceived.size(); ++i)
                    System.out.println("- " + MsgReceived.get(i));

            }

        } catch (SocketTimeoutException ste) {
            System.out.println("U-Client>Timeout - packet perdu :'/.'");
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }


}
