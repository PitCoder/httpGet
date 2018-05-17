package httpget;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
  private String webPage = "";
  private String webDir = "";
  private Socket client;
  
  public Client() {    
  }
  
  public Client(String webPage, String webDir) {    
    this.webPage = webPage;
    this.webDir = webDir;
  }

  /* This method only applies for the anchor web page */
  public void connect() throws IOException {
      this.client = new Socket("127.0.0.1", 8000);    
      DataOutputStream dos = new DataOutputStream(client.getOutputStream());
      dos.writeUTF(webPage);
      dos.writeUTF(webDir);
      dos.flush();
      dos.close();
  }
  
  /* This method allow us to open a new thread connection, to process an element of the Queue */
  public void justConnect() throws IOException {
      System.out.println("Creating new thread");
      this.client = new Socket("127.0.0.1", 8000);      
      this.client.close();
  }
  
  public void disconnect() throws IOException{
    this.client.close();
  }
}