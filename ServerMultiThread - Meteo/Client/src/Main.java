import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Scanner;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Scanner tastiera = new Scanner(System.in);  //Stream della tastiera

        try {

            /*Manda una richiesta di connessione.
              Dopo che il server ha fatto le sue cose scrive da tastiera e riceve una risposta*/

            Socket clientSocket = new Socket("localhost", 5000);
            System.out.println("SERVER CONTATTATO");
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String temperatura, temperaturaMedia, stringa;
            int giornoTemperaturaUtente, meseTemperaturaUtente, annoTemperaturaUtente;


            do {

                //Inserimento temperatura attuale
                System.out.print("INSERISCI LA TEMPERATURA: ");
                temperatura = tastiera.nextLine();

                //Scrittura sullo stream
                writer.println(System.currentTimeMillis() + ":" + temperatura);
                writer.flush();     /*Svuota la stream quando finisce il messaggio.
                                      Avvisa il server che il messaggio è finito e d'inviarlo*/

                //Lettura dallo stream
                stringa = reader.readLine();

                //Individuazione data temperatura media
                giornoTemperaturaUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getDayOfMonth();

                meseTemperaturaUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getMonthValue();

                annoTemperaturaUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();

                //Individuazione temperatura media
                temperaturaMedia = stringa.split(":")[1];

                //Stampa data e temperatura media
                System.out.println(giornoTemperaturaUtente + "/" + meseTemperaturaUtente + "/" + annoTemperaturaUtente
                        + " | TEMPERATURA MEDIA OGGI: " + temperaturaMedia);

            } while (true);

        } catch (Exception e) {
            System.err.println("eccezione " + e.getMessage());
        }
    }
}