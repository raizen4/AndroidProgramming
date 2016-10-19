package projects.boldurbogdan.rssfeed;

/**
 * Created by boldurbogdan on 12/06/2016.
 */
    public class Header_item_list {
        private String title;
        private String image_url;


        public Header_item_list(String title, String url_img){
            this.title=title;
            this.image_url=url_img;
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

    }


