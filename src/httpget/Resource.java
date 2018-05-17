package httpget;

public class Resource {
    private String url;
    private String dir;
    private String type;
    
    public Resource(){
    }
    
    public String getUrl(){
        return this.url;
    }
    
    public String getDir(){
        return this.dir;
    }
    
    public String getType(){
        return this.type;
    }
    
     public void setUrl(String url){
        this.url = url;
    }
    
    public void setDir(String dir){
        this.dir = dir;
    }
    
    public void setType(String type){
        this.type = type;
    }
}
