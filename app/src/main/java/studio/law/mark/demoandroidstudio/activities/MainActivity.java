package studio.law.mark.demoandroidstudio.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import greenDao.demo.DaoMaster;
import greenDao.demo.DaoSession;
import greenDao.demo.PictureLocation;
import greenDao.demo.PictureLocationDao;
import studio.law.mark.demoandroidstudio.Constants;
import studio.law.mark.demoandroidstudio.R;
import studio.law.mark.demoandroidstudio.adapter.GalleryAdapter;
import studio.law.mark.demoandroidstudio.fragments.ImageGridFragment;


public class MainActivity extends Activity {
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Context mContext;
    private Button webCrawlerButton;
    private Button imageButton;
    // Declare Variables
    private ViewPager viewPager;
    private PagerAdapter adapter;


    private int[] imageIDs = {
            R.drawable.six_eight_nine, R.drawable.ten
    };

    //GreenDao variables
    private PictureLocationDao pictureLocationDao;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;
    private DaoSession daoSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Navigation Drawer Fragment related codes
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mContext = MainActivity.this;
        //Gallery Code
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new GalleryAdapter(MainActivity.this, imageIDs);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        //button
        webCrawlerButton = (Button) this.findViewById(R.id.webCrawlerButton);
        webCrawlerButton.setOnClickListener(webCrawlerButtonHandler);

        //test image view
        imageButton = (Button) this.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(imageButtonHandler);

    }


    View.OnClickListener webCrawlerButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            //call async
            new WebCrawl().execute();


            Log.i("inside webCrawlerButtonHandler", "inside webCrawlerButtonHandler");


        }
    };

    View.OnClickListener imageButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            //call image activity
            Intent intent = new Intent(MainActivity.this, SimpleImageActivity.class);
            intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
            startActivity(intent);

            Log.i("inside imageButtonHandler", "inside imageButtonHandler");


        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }


    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position

        //Navigation Fragment
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);

            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }


    //Async Task to grab the String
    private class WebCrawl extends
            AsyncTask<Void, Void, Void> {
        //test url
        String url = "http://ck101.com/thread-3166378-1-1.html";
        String url2 = "http://freedwallpaper.com/girl-background-hd-images/";
        String url3 = "http://www.reddit.com/r/sexygirls";
        //folder location
        String folder = "";

        @Override
        protected Void doInBackground(Void... params) {
            //grab instance of greenDao
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext,
                    "url-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            pictureLocationDao = daoSession.getPictureLocationDao();

//            Log.i("created url-db", helper.toString());
//            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "CrawlerImages");
//            directory.mkdirs();
//            Log.d("directory is: ", directory.toString());
//            folder = directory.toString();

            String regex1 = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
//            String regex = "[^\\s]+(\\\\.(?i)(jpg|png|gif|bmp))$)\";.*\\.jpe?g";

            String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
            try {
                Document doc = Jsoup.connect(url3).userAgent(userAgent).get();
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
                    Element a = nextPage.getElementsByAttribute("href").first();
                    String nextPageLink = a.attr("href");
                    //href is the nextPage
                    Log.i("nextPageLink", nextPageLink);
                    String nextPageHref = nextPage.attr("href");
                    if (nextPageHref != null) {
                        Log.i("nextPageHref", nextPageHref);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute() {
            // mDisplay.append(msg + "\n");


        }
    }

}
