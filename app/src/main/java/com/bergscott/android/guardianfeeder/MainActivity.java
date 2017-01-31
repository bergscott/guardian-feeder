package com.bergscott.android.guardianfeeder;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private ArticleAdapter mArticleAdapter;

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

        // fetch Article list from guardian API and update list adapter in background thread
        new ArticleAsyncTask().execute(QueryUtils.SAMPLE_QUERY_URL);
    }

    private class ArticleAsyncTask extends AsyncTask<String, Void, ArrayList<Article>> {
        @Override
        protected ArrayList<Article> doInBackground(String... urlStrings) {
            // return null if no url is passed in as a parameter
            if (urlStrings.length == 0 || urlStrings[0] == null) {
                return null;
            }
            return QueryUtils.extractArticles(urlStrings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            if (articles == null) {
                return;
            }
            mArticleAdapter.clear();
            mArticleAdapter.addAll(articles);
        }
    }
}
