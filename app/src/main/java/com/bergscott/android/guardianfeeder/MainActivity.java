package com.bergscott.android.guardianfeeder;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    private ArticleAdapter mArticleAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;
    private EditText mSearchBar;
    private String mPreviousSearch = "board game";

    private final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieve the list view from the xml layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        // create a new ArticleAdapter and set it to the list view
        mArticleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mArticleAdapter);

        // find the empty state TextView and set it to the article list view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_state_text_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // create an onItemClickListener to open the article's url in a browser when clicked
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri webpage = Uri.parse(mArticleAdapter.getItem(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // find the search bar and set the ime to search
        mSearchBar = (EditText) findViewById(R.id.query_bar);
        mSearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView searchBar, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startQueryFromSearchBar();
                }
                return false;
            }
        });

        // find the search button and set up its on click listener to start the API query
        Button searchButton = (Button) findViewById(R.id.query_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQueryFromSearchBar();
            }
        });

        // find the progress bar view for use in onLoadFinished
        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        Bundle bundle = new Bundle();
        bundle.putString("queryString", mPreviousSearch);
        getLoaderManager().initLoader(0, bundle, this);
    }

    private void startQueryFromSearchBar() {
        startQuery(mSearchBar.getText().toString());
    }

    private void startQuery(String keywords) {
        // hide the soft keyboard
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // if there is no text in the search bar, notify user and return early
        if (TextUtils.isEmpty(keywords)) {
            Toast.makeText(this, "No Search Keywords Entered", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the current network status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // if connected to network, start the loader that will handle updating the article
        // information through a http request on a background thread
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!TextUtils.equals(keywords, mPreviousSearch)) {
                Bundle bundle = new Bundle();
                bundle.putString("queryString", keywords);
                mArticleAdapter.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                getLoaderManager().restartLoader(0, bundle, this);
                mPreviousSearch = keywords;
            }
        } else {
            // no network connection, hide progress spinner and set text of empty state view
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_network);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(this, makeQueryString(bundle.getString("queryString")));
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // hide the progress spinner
        mProgressBar.setVisibility(View.GONE);

        if (articles != null && !articles.isEmpty()) {
            // clear the article adapter and update it with the found articles
            mArticleAdapter.clear();
            mArticleAdapter.addAll(articles);
        }

        mEmptyStateTextView.setText(R.string.empty_articles_list);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // loader reset, so clear existing data
        mArticleAdapter.clear();
    }

    /**
     * Make a String url query for the Guardian api
     * @param query user's search terms for query
     * @return string url query
     */
    private String makeQueryString(String query) {
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // add the user's search terms to the response
        uriBuilder.appendQueryParameter("q", query);
        // include the byline in the response
        uriBuilder.appendQueryParameter("show-fields", "byline");
        // include contributor information in the response
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        // use the test api key
        uriBuilder.appendQueryParameter("api-key", "test");

        return uriBuilder.toString();
    }
}
