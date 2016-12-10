package com.bergscott.android.guardianfeeder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieve the list view from the xml layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        ArrayList<Article> articles = new ArrayList<Article>();
        articles.add(new Article("Food is Good", "http://www.theguardian.com", "John Doe", 1481388238000L, "Opinion"));
        articles.add(new Article("Food is Bad", "http://www.theguardian.com", "Jane Doe", 1481588238000L, "Opinion"));
        articles.add(new Article("Food is Alright", "http://www.theguardian.com", "Food Man", 1481388238000L, "Opinion"));
        articles.add(new Article("Don't Eat Food", "http://www.theguardian.com", "Article Author", 1481388238000L, "Politics"));
        articles.add(new Article("Go Food!", "http://www.theguardian.com", "John Doe", 1481788238000L, "Sports"));

        // create a new ArticleAdapter and set it to the list view
        mArticleAdapter = new ArticleAdapter(this, articles);
        articleListView.setAdapter(mArticleAdapter);
    }
}
