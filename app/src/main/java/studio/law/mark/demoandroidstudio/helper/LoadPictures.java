package studio.law.mark.demoandroidstudio.helper;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import greenDao.demo.PictureLocation;
import greenDao.demo.PictureLocationDao;

/**
 * Created by GTR on 1/28/2015.
 */
public class LoadPictures extends AsyncTask<Void, Void, List<PictureLocation>> {

    String regex1 = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
    private PictureLocationDao pictureLocationDao;
    private String url;
    private String nextPageURL;
    private SharedPreferences.Editor editor;

    //constructor, provide the dao, and the reddit url to initialize this loader

    /**
     * @param pictureLocationDao
     * @param url
     */
    public LoadPictures(PictureLocationDao pictureLocationDao, String url, SharedPreferences.Editor editor) {
        this.pictureLocationDao = pictureLocationDao;
        this.url = url;
        this.editor = editor;
    }

    /**
     * @return nextPage list of urls
     */
    public List<PictureLocation> getNextPage() {
        List<PictureLocation> pictureLocations = new ArrayList<PictureLocation>();
        try {
            Document doc;
            if (nextPageURL != null && nextPageURL != "") {
                doc = Jsoup.connect(nextPageURL).userAgent(userAgent).get();
            } else {
                doc = Jsoup.connect(url).userAgent(userAgent).get();
            }
            Elements links = doc.select("a[href]"); // a with href
            int counter = 0;
            HashMap<String, Integer> checkForDups = new HashMap<String, Integer>();
            for (Element link : links) {
                Log.i("links ", link.attr("abs:href"));
                if (link.attr("abs:href").matches(regex1)) {
                    Log.i("found a jpg", link.attr("abs:href"));
//                      don't download the images yet
//                      getAndSaveImages(link.attr("abs:href"), folder);
//                      let's put it in a db
                    //there are double images, so if its new image, we put in hashmap, so know its not found before.
                    if (!checkForDups.containsKey(link.attr("abs:href"))) {
                        Log.i("first add", link.attr("abs:href"));
                        pictureLocations.add(new PictureLocation(null, "name" + counter, link.attr("abs:href")));
                        PictureLocation pictureLocation = new PictureLocation(null, "name" + counter, link.attr("abs:href")
                        );
                        pictureLocationDao.insert(pictureLocation);
                        checkForDups.put(link.attr("abs:href"), 1);
                    } else {
                        Log.i("inside else statement", "already added");
                    }
                }
            }
            Element nextPage = doc.getElementsByClass("nextprev").first();

            if (nextPage != null) {
                Log.i("nextPage1", nextPage.toString());
                Elements hrefs = nextPage.getElementsByAttribute("href");
                for (Element href : hrefs) {
                    if (href.attr("rel").equalsIgnoreCase("nofollow next")) {
                        Log.i("next page rel", href.attr("rel"));
                        String nextPageLink = href.attr("href");
                        if (nextPageLink != null) {
                            nextPageURL = nextPageLink;

                            editor.putString("nextPageURL", nextPageLink);
                            editor.commit();
                            Log.i("nextPageURL", nextPageURL);

                        }
                    }
                }
                //String nextPageLink = hrefs.attr("href");
                //href is the nextPage

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictureLocations;
    }

    @Override
    protected List<PictureLocation> doInBackground(Void... params) {
        return this.getNextPage();
    }
}
