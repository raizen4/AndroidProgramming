package projects.boldurbogdan.newsapp;

/**
 * Created by boldurbogdan on 13/06/2016.
 */
public class Header_item_list {
    private String title;
    private String image_url;
    private String website_url;





    public Header_item_list(String title, String url_img, String site){
        this.title=title;
        this.image_url=url_img;
        this.website_url=site;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return image_url;
    }

    public void setImg(String url_img){
        this.image_url=url_img;
    }
    public String getWebsite_url() {
        return website_url;
    }

}




