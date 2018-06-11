package httpget;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{

  private ServerSocket server_socket;
  public static final int PORT = 8000;  // Puerto en el que se trabajará
  private Download download;

  public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.init();
  }
  
  private void init() throws IOException {
    this.server_socket = new ServerSocket(PORT);
    // Thread Pool : Limit - 15
    ExecutorService executor = Executors.newFixedThreadPool(15);
    System.out.println("Waiting for requests...");
    //This flag excecutes once and it is only done for the anchor web page
    boolean flag = true;
    
    while (true) {      
      Socket connection = this.server_socket.accept();
      /* This is only done for the anchor web page */
      if (flag) {
        DataInputStream input = new DataInputStream(connection.getInputStream());
        String page = input.readUTF();
        String directory = input.readUTF();
        System.out.println("Downloading: " + page);
        System.out.println("Saving to directory: " + directory);
        //String pagina = entrada.readLine().substring(2);  // Ofset desconocido
        this.download = new Download(page, directory, "href");
        this.download.setMainPagel(page);
        //descarga.setPaginaPrincipal(pagina);
        flag = false;           
      }
     /* Then we execute the thread */  
      Thread tarea = new Thread(this.download);
      executor.execute(tarea);
    }    
  }
}
