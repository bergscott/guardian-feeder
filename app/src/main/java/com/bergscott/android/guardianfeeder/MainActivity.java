package com.bergscott.android.guardianfeeder;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(this, makeQueryString("board game"));
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        if (articles != null && !articles.isEmpty()) {
            // clear the article adapter and update it with the found articles
            mArticleAdapter.clear();
            mArticleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // loader reset, so clear existing data
        mArticleAdapter.clear();
    }

    private String makeQueryString(String query) {
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "test");

        return uriBuilder.toString();
    }
}
