package com.udacityproject.cmcmc.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.udacityproject.cmcmc.popularmovies.database.FavoritesContract.FavoritesEntry;
import static com.udacityproject.cmcmc.popularmovies.database.FavoritesContract.FavoritesEntry.TABLE_NAME;


public class FavoritesContentProvider extends ContentProvider {
    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;
    private FavoritesDbHelper mFavoritesDbHelper;

    // CDeclare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        // Complete onCreate() and initialize a TaskDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable

        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FAVORITES:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase theDb = mFavoritesDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case FAVORITES:
                retCursor = theDb.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,null, sortOrder);
                break;
            case FAVORITE_WITH_ID:
//                String id = uri.getPathSegments().get(1);
                String theSelection = FavoritesEntry.COLUMN_MOVIEID + "=?";
//                String[] mSelectionArgs = new String[]{id};
//                if(selection != null && selectionArgs != null){
//                    mSelection = selection;
//                    mSelectionArgs = selectionArgs;
//                }
                retCursor = theDb.query(TABLE_NAME,
                        projection,
                        theSelection,
                        selectionArgs,
                        null,null, sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase theDB = mFavoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int theDeleteCount;
        switch (match){
            case FAVORITE_WITH_ID:
//                String theID = uri.getPathSegments().get(1);
                String theSelection = FavoritesEntry.COLUMN_MOVIEID + "=?";
//                String[] theSelectionArgs = new String[]{theID};

                theDeleteCount = theDB.delete(TABLE_NAME, theSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // done (3) Notify the resolver of a change and return the number of items deleted
        if(theDeleteCount != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return theDeleteCount;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
