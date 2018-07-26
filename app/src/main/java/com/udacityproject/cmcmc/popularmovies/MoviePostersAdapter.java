package com.udacityproject.cmcmc.popularmovies;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacityproject.cmcmc.popularmovies.Model.MovieInfo;
import com.udacityproject.cmcmc.popularmovies.database.FavoritesContract;
import com.udacityproject.cmcmc.popularmovies.database.FavoritesContract.FavoritesEntry;

import java.util.ArrayList;
import java.util.List;

public class MoviePostersAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<MovieInfo> mPosters;
    private boolean mFromDb;
    private Cursor mDBCursor;

    public MoviePostersAdapter(Context theContext, ArrayList<MovieInfo> thePosters, Cursor theDbCursor, boolean fromDb){
        this.mContext = theContext;
        this.mPosters = thePosters;
        this.mDBCursor = theDbCursor;
        this.mFromDb = fromDb;
    }
    public MoviePostersAdapter(Context theContext, ArrayList<MovieInfo> thePosters){
        this.mContext = theContext;
        this.mPosters = thePosters;
    }

    public void useFavoritesDb(boolean fromDb){
        mFromDb = fromDb;
    }
    public void swapCursor(Cursor newCursor){
        if(newCursor != null) {
            if(mDBCursor != null)
                mDBCursor.close();
            mDBCursor = newCursor;
        }
    }

    @Override
    public int getCount() {
        if(mFromDb)
            return mDBCursor.getCount();
        return mPosters.size();
    }

    @Override
    public Object getItem(int position) {
        if(mFromDb){
            if(mDBCursor.moveToPosition(position)){
                MovieInfo tempMovie = new MovieInfo(
                        mDBCursor.getInt(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIEID)),
                        mDBCursor.getDouble(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_VOTEAVG)),
                        mDBCursor.getString(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE)),
                        mDBCursor.getString(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_POSTERPATH)),
                        mDBCursor.getString(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_OVERVIEW)),
                        mDBCursor.getString(mDBCursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASEDATE))
                );
                tempMovie.set_rowId(mDBCursor.getInt(mDBCursor.getColumnIndex(FavoritesEntry._ID)));
                return tempMovie;
            }
            else{
                Log.d("fart", "Couldn't move to position");
                return null;
            }
        }
        return mPosters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView samplePoster = (ImageView) convertView;
        if (samplePoster == null) {
            Log.d("fart", "IMG["+position+"] is null. Creating new ImageView");
            samplePoster = new ImageView(mContext);
            samplePoster.setScaleType(ImageView.ScaleType.FIT_START);
            samplePoster.setAdjustViewBounds(true);
        }

        String imgApiBasePath = mContext.getResources().getString(R.string.base_url_images);
        String posterSize = mContext.getResources().getStringArray(R.array.poster_sizes)[3];
        String imgpath = imgApiBasePath + posterSize + ((MovieInfo) getItem(position)).getPoster_path();
        Log.d("fart", "imgpath:"+ imgpath);

        Picasso.get()
                .load(imgpath)
                .into(samplePoster);

        return samplePoster;
    }

    public void setItems(ArrayList<MovieInfo> theItems){
        this.mPosters = theItems;
    }
    public ArrayList<MovieInfo> getItems(){
        return mPosters;
    }

}
