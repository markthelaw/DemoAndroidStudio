/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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
package studio.law.mark.demoandroidstudio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import greenDao.demo.DaoMaster;
import greenDao.demo.DaoSession;
import greenDao.demo.PictureLocation;
import greenDao.demo.PictureLocationDao;
import studio.law.mark.demoandroidstudio.R;
import studio.law.mark.demoandroidstudio.helper.LoadPictures;
import studio.law.mark.demoandroidstudio.listener.InfiniteScrollListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGridFragment extends AbsListViewBaseFragment {

    public static final int INDEX = 1;
    DisplayImageOptions options;
    //GreenDao variables
    private PictureLocationDao pictureLocationDao;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private List<PictureLocation> pictureLocations;
    ImageAdapter imageAdapter;
    LoadPictures loadPictures;

    // use them to store next page url
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String url;
    private HashMap<String, Integer> nextPageUrls;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getActivity().getSharedPreferences("prefs", 0);
        editor = settings.edit();

        //load the url from greenDao
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(),
                "url-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        pictureLocationDao = daoSession.getPictureLocationDao();
        url = "http://www.reddit.com/r/sexygirls";

//        new LoadPictures().execute();
        //initial loading
        loadPictures = new LoadPictures(pictureLocationDao, url, editor);

//        pictureLocations = pictureLocationDao.queryBuilder().limit(5)
//                .orderAsc(PictureLocationDao.Properties.Url).list();

        try {
            pictureLocations = loadPictures.execute().get();
        } catch (InterruptedException e) {
            Log.i("Fail to load 1st time", e.toString());
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("Fail to load 1st time", e.toString());
        }
        Log.i("friend list", pictureLocations.toString());


        nextPageUrls = new HashMap<String, Integer>();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_grid, container, false);
        settings = getActivity().getSharedPreferences("prefs", 0);
        listView = (GridView) rootView.findViewById(R.id.grid);
        imageAdapter = new ImageAdapter(getActivity(), pictureLocations);
        ((GridView) listView).setAdapter(imageAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //needs to send in the image url to pager activity
                //position is not needed anymore, but we have it just for fun.
                String url1 = pictureLocations.get(position).getUrl();
                startImagePagerActivity(position, url1);
            }
        });

        //PauseOnScrollListener is a Android universal Image loader listener, it can wrap around my InfiniteScrollListener
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling, new InfiniteScrollListener(5) {
            @Override
            public void loadMore(int page, int totalItemsCount) throws ExecutionException, InterruptedException {
                Log.i("outside load more", "next page url is: " + settings.getString("nextPageURL",null) + "totalItemsCount: " + totalItemsCount + " ");
                if (totalItemsCount / 10 < page && settings.getString("nextPageURL",null)!=null && !nextPageUrls.containsKey(settings.getString("nextPageURL",null))) {
                    Log.i("inside load more", "next page url is: " + settings.getString("nextPageURL",null));

                    nextPageUrls.put(settings.getString("nextPageURL",null),0);
                    List<PictureLocation> newData = new LoadPictures(pictureLocationDao, settings.getString("nextPageURL", null), editor).execute().get();
                    pictureLocations.addAll(newData);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }));


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //applyScrollListener();
    }


    public class ImageAdapter extends ArrayAdapter<PictureLocation> {
        private List<PictureLocation> pictureLocations;
        private LayoutInflater inflater;

        public ImageAdapter(Context context, List<PictureLocation> pictureLocations) {

            super(context, 0, pictureLocations);
            inflater = LayoutInflater.from(getActivity());
            this.pictureLocations = pictureLocations;


        }

        @Override
        public int getCount() {
//			return imageUrls.length;
            return pictureLocations.size();
        }

//        @Override
//        public PictureLocation getItem(int position) {
//            return null;
//        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final PictureLocation pictureLocation = getItem(position);
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            //needs to feed ImageLoader the correct URL here.
            //how do we do this
            ImageLoader.getInstance()
                    .displayImage(pictureLocation.getUrl(), holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }


    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        Log.i("customLoadMoreDataFromApi", "inside customLoadMoreDataFromApi");
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

}