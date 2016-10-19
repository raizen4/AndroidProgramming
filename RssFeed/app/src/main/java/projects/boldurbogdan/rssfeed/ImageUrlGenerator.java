package projects.boldurbogdan.rssfeed;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by boldurbogdan on 12/06/2016.
 */
    public class ImageUrlGenerator {
        private String img_url;

        public ImageUrlGenerator(String url_for_download){
            this.img_url=url_for_download;
        }

        public String download(){

            Document doc= null;
            try {
                doc = Jsoup.connect(img_url).get();
                Element img=doc.getElementsByTag("img").first();
                String src=img.absUrl("src");
                return src;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }




    }





