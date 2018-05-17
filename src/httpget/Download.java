package httpget;

import java.io.*;
import static java.lang.System.exit;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Download implements Runnable{
    private String mainpage = "";
    private String webpage;
    private ArrayList<String> webextensions = new ArrayList<>();
    private String webdir;
    private ConcurrentLinkedQueue<Resource> urlQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<String, Boolean> register = new ConcurrentHashMap<>();
    
     /*Methods to keep a register of already downloaded web pages */
    public Download(String page, String dir, String type){
        webextensions.add(".html");
        webextensions.add(".htm");
        webextensions.add(".shtml");
        webextensions.add(".asp");
        webextensions.add(".aspx");
        webextensions.add(".pl");
        webextensions.add(".cgi");
        webextensions.add(".php");
        
        this.webpage = page;
        this.webdir = dir;
        Resource anchor = new Resource();
        anchor.setUrl(page);
        anchor.setDir(dir + "index.html");
        anchor.setType(type);
        this.urlQueue.add(anchor);
        register.put(page, Boolean.TRUE);
    }
    
    public void setWebPage(String page){
        this.webpage = page;
    }
    
   public void setMainPagel(String mainpage) {
        this.mainpage = mainpage;
  }
    
  @Override
  public void run() {
    /* Leo URL */
    Resource resource = this.urlQueue.poll();
    if(resource == null) {
      System.out.println("Termine de ejecutar... ");
      exit(0);
    }
    else{
      /* Descarga de la pagina */
      //System.out.println("Downloading: " + resource.getUrl() + "...");
      downloadResource(resource);
    }
  }
  
  private void verifyInRegister(Resource resource){
      if(register.get(resource.getUrl()) == null){
         register.put(resource.getUrl(), Boolean.TRUE);
         this.urlQueue.add(resource);
      }
  }
  
  
  private void downloadResource(Resource resource){
      try{
       String url = resource.getUrl();
       String dir = resource.getDir();
       String type = resource.getType();
       print("Fetching %s...", url);

       System.out.println(url);
       try{
            System.out.println("Connecting");
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .get();
            System.out.println("End Connection");
            Elements sources = document.select("[src]");
            Elements links = document.select("a[href]");
            links.addAll(document.select("link[href]"));
            
             for(Element link: links){
                Resource new_resource = new Resource();
                String attr = link.attr("abs:href");
                System.out.println("Href found: " + attr);
                if(false){
                    System.out.println("Web page founded");
                    String name = getPageName(attr);
                    String new_dir = dir.substring(0,dir.lastIndexOf("/")+1) + name;
                    createPageDir(new_dir + "/");
                    new_resource.setUrl(attr);
                    new_resource.setDir(new_dir);
                    new_resource.setType("html");
                }
                else{
                    System.out.println("External link founded");
                    String new_dir = dir.substring(0, dir.lastIndexOf("/")+1) + "External/" + getPageName(attr);
                    System.out.println(new_dir);
                    new_resource.setUrl(attr);
                    new_resource.setDir(new_dir);
                    new_resource.setType("href");
                }
                verifyInRegister(new_resource);
                link.attr("href", new_resource.getDir());
                new Client().justConnect();
             }
             
                   for(Element source : sources){
                Resource new_resource = new Resource();
                String attr = source.attr("abs:src");
                String append = "";
                switch(source.tagName()){
                    case "img":
                        //System.out.println("Image found: " + attr);
                        append = "Img";
                        break;
                    case "audio":
                        System.out.println("Audio found: " + attr);
                        append = "Audio";
                        break;
                    case "video":
                        System.out.println("Video found: " + attr);
                        append = "Video";
                        break;
                    case "script":
                        //System.out.println("Script found: " + attr);
                        append = "Script";
                        break;
                    case "embed":
                        System.out.println("Embed found: " + attr);
                        append = "Embed";
                        break;
                    case "iframe":
                        System.out.println("IFrame found: " + attr);
                        append = "IFrame";
                        break;
                    default:
                        System.out.println("Miscellaneous founded: " + attr);
                        append = "Miscellaneous";
                        break;
                 }

                new_resource.setUrl(attr);
                new_resource.setDir(dir.substring(0,dir.lastIndexOf("/")+1) + append + getSrcName(attr));
                new_resource.setType("source");
                System.out.println("Verifing");
                verifyInRegister(new_resource);
                System.out.println("End Verfing");
                //System.out.println(register.get(attr));
                source.attr("src", new_resource.getDir());
                new Client().justConnect();
            }  

            
             switch(type){
                 case "html":
                        System.out.println(dir);
                        String html = document.html();
                        PrintWriter out = new PrintWriter(dir);
                        out.println(html);
                        out.close();
                     break;
                 default:
                     System.out.println(dir);
                     saveSource(dir,url);
                     break;
             }
       }
       catch(Exception e){
           e.printStackTrace();
           System.out.println("It is a binary file :v");
           System.out.println(dir);
           saveSource(dir,url);
       }
      }
      catch(Exception e){
          e.printStackTrace();
      }
  }
    
    
    /* Function that prints into console the given string */
    private  void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
    
    /* Function that help us to trim the specified string */
    private String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    
    private String getSrcName(String src){
        String name = "";
        if(src.length() > 0){    
            //Extract the name of the image from the src attribute
            int indexname = src.lastIndexOf("/");
            if(indexname == src.length()){
                src = src.substring(1, indexname);
            }
            indexname = src.lastIndexOf("/");
            name = src.substring(indexname, src.length());
        }
        return name;
    }
    
    private String getPageName(String href){
        String name = "";
        if(href.length() > 0){    
            //Extract the name of the image from the src attribute
            int indexname = href.lastIndexOf("/");
            if(indexname == href.length()){
                href = href.substring(1, indexname);
            }
            indexname = href.lastIndexOf("/");
            name = href.substring(indexname+1, href.length());
        }
        return name;
    }
    
    private boolean createPageDir(String path){
        String directory = path;
        System.out.println(directory);
        boolean success;
        new File(directory).mkdir();
        new File(directory + "Img/").mkdir();
        new File(directory + "Audio/").mkdir();
        new File(directory + "Video/").mkdir();
        new File(directory + "Script/").mkdir();
        new File(directory + "Embed/").mkdir();
        new File(directory + "IFrame/").mkdir();
        new File(directory + "External/").mkdir();
        success  = new File(directory + "Miscellaneous/").mkdir();
        return success;
    }
    
    private boolean checkExtension(String extension){
        return false;
    }
    
    private void saveSource(String folder, String src) throws IOException{
        if(src.length() > 0){
            System.out.println("Saving Resource: " + folder);
            File f = new File(folder);
            f.getParentFile().mkdirs();            //f.getParentFile().mkdirs(); 
            f.createNewFile();

            //Open a URL Stream
            try{
                URL url = new URL(src);
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.setConnectTimeout(3000);
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
                InputStream in = httpcon.getInputStream();
                OutputStream out = new BufferedOutputStream(new FileOutputStream(folder));

                //Writting the bytes
                for(int b; (b = in.read()) != -1;){
                    out.write(b);
                }
                System.out.println("End Saving Resource: " + folder);
                //We close the streams
                out.close();
                in.close(); 
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
