import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Scanner;

public class Serverino implements Runnable { //Implementa runnable
    private Socket client;                                  //Socket.accept
    private PrintWriter writer;                             //Scrittore stream
    private BufferedReader reader;                          //Lettore stream
    private BufferedWriter fileWriter;                      //Scrittore del file Temperature.txt
    private Scanner fileReader;                             //Lettore del file Temperature.txt
    private File temperature;                               //File Temperature.txt
    private String stringa;                                 //Stringa lettura stream
    private int cont;                                       //Contatore temperature giornaliere
    private long temperatura;                               //Temperatura
    private long temperaturaMedia;                          //Temperatura media
    private int giornoUtente, meseUtente, annoUtente;       //Data della temperatura inserita dall'utente
    private int giornoFile, meseFile, annoFile;             //Data della temperatura letta dal fileReader

    public Serverino(Socket client) {
        this.client = client;
        this.temperature = new File("E:\\Programmazione\\IntelliJ\\Programmi\\IntelliJ\\ServerMultithread - Meteo\\Server\\src\\Temperature.txt");
        this.cont = 0;
        this.temperaturaMedia = 0;

        try {

            //Creazione file nel caso non esista
            if (!temperature.exists()) {
                temperature.createNewFile();
            }

            //Creazione lettore e scrittore sullo stream
            this.writer = new PrintWriter(new PrintWriter(client.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            //Creazione lettore e scrittore per Temperature.txt
            this.fileWriter = new BufferedWriter(new FileWriter(temperature, true));
            this.fileReader = new Scanner(temperature);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void run() {

        try {

            //Log
            System.out.println("CONNESSIONE RICEVUTA");

            do {

                //Lettura stringa dallo stream
                stringa = reader.readLine();

                System.out.println("MESSAGGIO RICEVUTO: " + stringa);

                //Scrittura timestamp e temperatura su file "Temperature.txt"
                fileWriter.write("\n" + stringa);
                fileWriter.flush();

                //Individuazione data della temperatura
                giornoUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getDayOfMonth();

                meseUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getMonthValue();

                annoUtente = Instant
                        .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();

                while (fileReader.hasNext()) {

                    stringa = fileReader.next();

                    //Individuazione data della temperatura
                    giornoFile = Instant
                            .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .getDayOfMonth();

                    meseFile = Instant
                            .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .getMonthValue();

                    annoFile = Instant
                            .ofEpochMilli(Long.parseLong(stringa.split(":")[0]))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .getYear();

                    //Individuazione temperatura
                    temperatura = Long.parseLong(stringa.split(":")[1]);

                    if (giornoUtente == giornoFile && meseUtente == meseFile && annoUtente == annoFile) {
                        //Incremento contatore
                        cont++;
                        //Calcolo temperatura media
                        temperaturaMedia = ((temperaturaMedia * (cont - 1)) + temperatura) / cont;
                        //Log
                        System.out.println("TEMPERATURA MEDIA: " + temperaturaMedia);
                    }
                }

                //Scrittura sullo stream
                writer.println(System.currentTimeMillis() + ":" + temperaturaMedia);
                writer.flush();

                //Reset impostazioni
                fileReader = new Scanner(temperature);
                temperaturaMedia = 0;
                cont = 0;

            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
