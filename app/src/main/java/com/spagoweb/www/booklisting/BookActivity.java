package com.spagoweb.www.booklisting;

/**
 * Created by Robe on 23/07/2017.
 */

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Tag for log messages
     */
    public static final String LOG_TAG = BookActivity.class.getName();

    /**
     * URL for book data from the dataset
     */
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Find a reference to the {@link ListView} in the layout
        ListView itemsView = (ListView) findViewById(R.id.list_view);

        // Set empty state on loading app at start
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        itemsView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Initialize adapter
        itemsView.setAdapter(mAdapter);

        // Upon App start, indicate to user that no books have been search yet
        mEmptyStateTextView.setText(R.string.no_books);

        // Find the View that shows the search button
        Button actionSearch = (Button) findViewById(R.id.search_button);

        getLoaderManager().initLoader(BOOK_LOADER_ID, null, BookActivity.this);

        // Set a click listener on that button
        actionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //close keyboard after click search button
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {

                    // clear the Empty State text
                    mEmptyStateTextView.setText("");

                    // clear the Empty State text
                    mAdapter.clear();

                    // restart loader if one is running or start a new one if it is not running
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);

                    // show progress bar until books are loaded
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.VISIBLE);

                } else {
                    //No Network connectivity. Clear adapter and let user know
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_internet_connection);

                    // hide progress bar
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        EditText searchInput = (EditText) findViewById(R.id.edit_text_search);
        String searchText = searchInput.getText().toString();

        //Clear text in mEmptyStateTextView
        mEmptyStateTextView.setText("");

        Uri baseUri = Uri.parse(BOOK_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchText);
        uriBuilder.appendQueryParameter("maxResults", "25");

        return new BookLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        /* Hide loading indicator because the data has been loaded. */
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        /* Set empty state text to display "No books found.". */
        mEmptyStateTextView.setText(R.string.no_books);

        /* Clear the adapter of previous book data. */
        mAdapter.clear();

        /*
            If there is a valid list of {@link Book}s, then add them to the adapter's
            data set. This will trigger the ListView to update.
         */
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        /* Loader reset, so we can clear out our existing data. */
        mAdapter.clear();
    }
}