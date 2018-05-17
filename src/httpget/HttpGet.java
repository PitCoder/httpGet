package httpget;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class HttpGet {
    static private String webpage;
    static private String webdir;
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Especifique la pagina a descargar: ");
        HttpGet.webpage = input.nextLine();
        System.out.print("Especifique el nombre de la pagina local: ");
        HttpGet.webdir = input.nextLine();
        
        createPageDir();
        Client client = new Client(HttpGet.webpage, HttpGet.webdir);
        try{
            client.connect();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        //Download download = new Download(HttpGet.webpage, HttpGet.webdir);
        //download.init();
    }
    
    static private boolean createPageDir(){
        HttpGet.webdir = "/home/ESCOM/Documents/Repositorios/httpGet/Download/" + HttpGet.webdir + "/";
        System.out.println(HttpGet.webdir);
        boolean success;
        new File(HttpGet.webdir).mkdir();
        new File(HttpGet.webdir + "Img/").mkdir();
        new File(HttpGet.webdir + "Audio/").mkdir();
        new File(HttpGet.webdir + "Video/").mkdir();
        new File(HttpGet.webdir + "Script/").mkdir();
        new File(HttpGet.webdir + "Embed/").mkdir();
        new File(HttpGet.webdir + "IFrame/").mkdir();
        new File(HttpGet.webdir + "External/").mkdir();
        success  = new File(HttpGet.webdir + "Miscellaneous/").mkdir();
        return success;
    }
}
