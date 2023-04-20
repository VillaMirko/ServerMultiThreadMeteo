import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            do {
                System.out.println("SERVER PARTITO, in attesa");
                new Thread(new Serverino(server.accept())).start();
                /*In attesa tramite la server.accept, quando riceve una connessione,
                  la server.accept restituisce la socket che viene passata a Serverino*/
            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}