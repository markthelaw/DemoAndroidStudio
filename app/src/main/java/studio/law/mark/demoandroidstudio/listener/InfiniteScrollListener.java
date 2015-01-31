package studio.law.mark.demoandroidstudio.listener;

import android.util.Log;
import android.widget.AbsListView;

import java.util.concurrent.ExecutionException;

/**
 * Created by GTR on 1/27/2015.
 */


public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;

    public InfiniteScrollListener(int bufferItemCount) {
        this.bufferItemCount = bufferItemCount;
    }

    public abstract void loadMore(int page, int totalItemsCount) throws ExecutionException, InterruptedException;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

        Log.i("inside InfiniteScroll onScroll", "inside InfiniteScroll onScroll");
        if (totalItemCount < itemCount) {
            this.itemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true; }
        }
        Log.i("bunch of string", "they are " + firstVisibleItem + " visibleItemCounter " + visibleItemCount + " totalItemCount " + totalItemCount +" itemCount: " +itemCount);

        if (isLoading && (totalItemCount >= itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
            try {
                loadMore(currentPage + 1, totalItemCount);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("load more", "loaded");
            isLoading = true;
        }
    }
}
