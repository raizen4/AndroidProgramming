package projects.boldurbogdan.newsapp;

/**
 * Created by boldurbogdan on 26/06/2016.
 */
public class Site_Item {
    private String name;
    private String url;
    public Site_Item(String name,String url){
        this.name=name;
        this.url=url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url =url;
    }
}
