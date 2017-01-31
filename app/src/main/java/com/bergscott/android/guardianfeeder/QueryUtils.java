package com.bergscott.android.guardianfeeder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * helper functions for querying The Guardian API and parsing response
 */

public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
            Locale.US);

    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects that has been built up from parsing a JSON response.
     * @param urlString url to query Guardian API
     * @return list of Articles
     */
    public static ArrayList<Article> extractArticles(String urlString) {

        // Create an empty ArrayList to add Articles to
        ArrayList<Article> articles = new ArrayList<Article>();

        Log.v(LOG_TAG, "Query url: " + urlString);
        // Retrieve a jsonResponse from the Guardian API
        String jsonResponse = getArticleJson(urlString);

        try {
            // get root JSONObject
            JSONObject root = new JSONObject(jsonResponse);

            // get response JSONObject
            JSONObject JSONResponse = root.getJSONObject("response");

            // get JSONArray of Articles
            JSONArray articlesArray = JSONResponse.getJSONArray("results");

            // for each Article in the array,
            for (int i = 0; i < articlesArray.length(); i++) {
                // get the article JSONObject at the current index of the array
                JSONObject currentArticle = articlesArray.getJSONObject(i);

                // get the article title from the JSON object
                String title = currentArticle.getString("webTitle");

                // get the article url from the JSON object
                String url = currentArticle.getString("webUrl");

                // get the author containing tags JSONArray from the article object
                JSONArray tagsArray = currentArticle.getJSONArray("tags");

                String author = null;

                // search for the author first in the first element of the tags array
                if (tagsArray.length() > 0) {
                    // get the contributor tag object
                    JSONObject authorTag = tagsArray.getJSONObject(0);
                    // get the author's name from the  tag JSONObject
                    author = authorTag.getString("webTitle");
                } else if (currentArticle.has("fields")) {
                    // if there is no contributor tag, get the author's name from the byline field
                    // get the fields jsonObject
                    JSONObject fieldsObject = currentArticle.getJSONObject("fields");
                    // get the author's name from the "byline" element
                    author = fieldsObject.getString("byline");
                } else {
                    // use the content type in place of the author's name
                    author = currentArticle.getString("type");
                }

                // get the publication date from the JSON Object
                String date = currentArticle.getString("webPublicationDate");

                // get the section from the JSON Object
                String section = currentArticle.getString("sectionName");

                // create a new Article object from the extracted properties and add it to the list
                // of Articles
                articles.add(new Article(title, url, author, convertDateTime(date), section));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("MainActivity.java", "Problem parsing the earthquake JSON results", e);
        }

        return articles;
    }

    private static String getArticleJson(String urlString) {
        URL url = makeURLFromString(urlString);
        String jsonResponse = null;

        if (url != null) {
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request", e);
            }
        }
        return jsonResponse;
    }

    private static URL makeURLFromString(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem forming URL from String", e);
            return null;
        }
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Guardian JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;

            try {
                line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem reading the http response input stream.", e);
            }
        }

        return output.toString();
    }

    private static long convertDateTime(String dateTime) {
        // replace the Z signifying a time offset of zero with "+0000"
        dateTime = dateTime.replace("Z", "+0000");

        // convert date string to milliseconds and return it
        return DATE_FORMAT.parse(dateTime, new ParsePosition(0)).getTime();
    }

}
