package studio.law.mark.demoandroidstudio.helper;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import greenDao.demo.DaoMaster;
import greenDao.demo.PictureLocation;
import greenDao.demo.PictureLocationDao;
import studio.law.mark.demoandroidstudio.activities.SimpleImageActivity;

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
    private ProgressDialog mProgressDialog;
    private Activity simpleImageActivity;
    private HashMap<String, Integer> nextPageUrls;

    private SharedPreferences settings;
    //constructor, provide the dao, and the reddit url to initialize this loader

    /**
     * @param pictureLocationDao
     * @param url
     * @param editor
     * @param simpleImageActivity
     */
    public LoadPictures(PictureLocationDao pictureLocationDao, String url, SharedPreferences.Editor editor, Activity simpleImageActivity, HashMap<String, Integer> nextPageUrls, SharedPreferences settings) {
        this.pictureLocationDao = pictureLocationDao;
        this.url = url;
        this.editor = editor;
        this.simpleImageActivity = simpleImageActivity;
        this.nextPageUrls = nextPageUrls;
        this.settings = settings;
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
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progressdialog
        mProgressDialog = new ProgressDialog(simpleImageActivity);
        // Set progressdialog title
        mProgressDialog.setTitle("Picture loading Title");
        // Set progressdialog message
        mProgressDialog.setMessage("Loading more...");
        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.show();

    }

    @Override
    protected List<PictureLocation> doInBackground(Void... params) {
        int count = 0;

        return this.getNextPage();
//        while (true) {
//            Log.i("inside doInBackground", "count is "  + count++);
//            if (!nextPageUrls.containsKey(url)) {
//                Log.i("inside if", "url is "  + url);
//                return this.getNextPage();
//
//            }else{
//                Log.i("inside else", "nextPageUrls is "  + nextPageUrls);
//                try {
//                    Thread.sleep(1000);
//                    //try to get a new url
//                    url = settings.getString("nextPageURL", null);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    protected void onPostExecute(List<PictureLocation> results) {

        Toast.makeText(simpleImageActivity, "loaded more", Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();

    }


}
