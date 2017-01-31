package com.bergscott.android.guardianfeeder;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by bergs on 1/31/2017.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    String mUrl;

    /**
     * Create a new Article Loader
     * @param context calling context
     * @param url url string for Guardian API query
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    /**
     * This is on a background thread
     * @return list of Articles retrieved from Guardian API
     */
    public List<Article> loadInBackground() {
        // if url is empty return early
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        // fetch list of Articles from Guardian API
        return QueryUtils.extractArticles(mUrl);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
