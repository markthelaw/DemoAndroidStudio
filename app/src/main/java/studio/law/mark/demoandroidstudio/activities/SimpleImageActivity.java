/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package studio.law.mark.demoandroidstudio.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import studio.law.mark.demoandroidstudio.Constants;
import studio.law.mark.demoandroidstudio.R;
import studio.law.mark.demoandroidstudio.fragments.ImageGridFragment;
import studio.law.mark.demoandroidstudio.fragments.ImagePagerFragment;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class SimpleImageActivity extends FragmentActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Context mContext;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout1);
        mDrawerList = (ListView) findViewById(R.id.left_drawer1);
        mContext = SimpleImageActivity.this;

        //set a shadow
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        Toolbar mToolbar = (Toolbar) findViId(R.id.toolbar);
        //nav drawer icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */

//                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.i("inside onDrawerClosed", "1");
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i("inside onDrawerOpened", "1");
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);


        //indicator of which image view mode we want to display.
        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        String url = getIntent().getStringExtra(Constants.Extra.SUB_URL);
        Fragment fr;
        String tag;
        int titleRes;
        switch (frIndex) {
            default:
//			case ImageListFragment.INDEX:
//				tag = ImageListFragment.class.getSimpleName();
//				fr = getSupportFragmentManager().findFragmentByTag(tag);
//				if (fr == null) {
//					fr = new ImageListFragment();
//				}
//				titleRes = R.string.ac_name_image_list;
//				break;
            case ImageGridFragment.INDEX:
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                    Bundle args = new Bundle();
                    args.putString(Constants.Extra.SUB_URL, url);
                    fr.setArguments(args);
                }
                titleRes = R.string.ac_name_image_grid;
                break;
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.ac_name_image_pager;
                break;
//			case ImageGalleryFragment.INDEX:
//				tag = ImageGalleryFragment.class.getSimpleName();
//				fr = getSupportFragmentManager().findFragmentByTag(tag);
//				if (fr == null) {
//					fr = new ImageGalleryFragment();
//				}
//				titleRes = R.string.ac_name_image_gallery;
//				break;
        }

        setTitle(titleRes);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame1, fr, tag).commit();
    }


//create the option menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }



    private class DrawerItemClickListener implements ListView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("inside onDrawerItemClick", "position = " + position);
            //activate the different sub reddits
            Intent intent;

            switch(position){
                default:


                //sexygirls
                case 0:
                    intent = new Intent(SimpleImageActivity.this, SimpleImageActivity.class);
                    intent.putExtra(Constants.Extra.SUB_URL, "http://www.reddit.com/r/sexygirls");
                    startActivity(intent);
                    break;

                case 1:
                    intent = new Intent(SimpleImageActivity.this, SimpleImageActivity.class);
                    intent.putExtra(Constants.Extra.SUB_URL, "http://www.reddit.com/r/sexy");
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(SimpleImageActivity.this, SimpleImageActivity.class);
                    intent.putExtra(Constants.Extra.SUB_URL, "http://www.reddit.com/r/Goddesses");
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(SimpleImageActivity.this, SimpleImageActivity.class);
                    intent.putExtra(Constants.Extra.SUB_URL, "http://www.reddit.com/r/Mila_Kunis");
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(SimpleImageActivity.this, SimpleImageActivity.class);
                    intent.putExtra(Constants.Extra.SUB_URL, "http://www.reddit.com/r/PrettyGirls");
                    startActivity(intent);
                    break;





            }
        }
    }
}