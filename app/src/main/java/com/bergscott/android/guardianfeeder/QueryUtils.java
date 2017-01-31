package com.bergscott.android.guardianfeeder;

import android.os.Parcelable;
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

import static android.R.attr.author;

/**
 * helper functions for querying The Guardian API and parsing response
 */

public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";

    public static final String SAMPLE_JSON_RESPONSE = "{\"response\":{\"status\":\"ok\",\"userTier\":\"developer\",\"total\":23063,\"startIndex\":1,\"pageSize\":10,\"currentPage\":1,\"pages\":2307,\"orderBy\":\"relevance\",\"results\":[{\"id\":\"us-news/2016/sep/26/presidential-debates-nixon-kennedy-1960\",\"type\":\"article\",\"sectionId\":\"us-news\",\"sectionName\":\"US news\",\"webPublicationDate\":\"2016-09-26T15:57:34Z\",\"webTitle\":\"The Nixon-Kennedy presidential debates: from the archive, 1960\",\"webUrl\":\"https://www.theguardian.com/us-news/2016/sep/26/presidential-debates-nixon-kennedy-1960\",\"apiUrl\":\"https://content.guardianapis.com/us-news/2016/sep/26/presidential-debates-nixon-kennedy-1960\",\"tags\":[{\"id\":\"profile/richard-nelsson\",\"type\":\"contributor\",\"webTitle\":\"Richard Nelsson\",\"webUrl\":\"https://www.theguardian.com/profile/richard-nelsson\",\"apiUrl\":\"https://content.guardianapis.com/profile/richard-nelsson\",\"references\":[],\"bio\":\"<p>Richard Nelsson is information manager for the Guardian, and the editor of <a href=\\\"http://bookshop.theguardian.com/on-the-roof-of-the-world.html\\\">Those Who Dared: The Guardian Book of Adventure</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2014/12/9/1418152063733/Richard-Nelsson.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2014/12/9/1418152081615/Richard-Nelsson-L.png\",\"firstName\":\"richard\",\"lastName\":\"nelsson\"}],\"isHosted\":false},{\"id\":\"commentisfree/2016/sep/23/presidential-debates-real-time-reactions-notifications-mobile\",\"type\":\"article\",\"sectionId\":\"commentisfree\",\"sectionName\":\"Opinion\",\"webPublicationDate\":\"2016-09-23T21:43:23Z\",\"webTitle\":\"Get real-time reactions during the presidential debates | Guardian Mobile Innovation Lab\",\"webUrl\":\"https://www.theguardian.com/commentisfree/2016/sep/23/presidential-debates-real-time-reactions-notifications-mobile\",\"apiUrl\":\"https://content.guardianapis.com/commentisfree/2016/sep/23/presidential-debates-real-time-reactions-notifications-mobile\",\"tags\":[{\"id\":\"profile/us-mobile-innovation-lab\",\"type\":\"contributor\",\"webTitle\":\"Guardian US Mobile Innovation Lab\",\"webUrl\":\"https://www.theguardian.com/profile/us-mobile-innovation-lab\",\"apiUrl\":\"https://content.guardianapis.com/profile/us-mobile-innovation-lab\",\"references\":[],\"bio\":\"<p>The Mobile Innovation Lab is a small, experimental team housed in the Guardian US newsroom and set up to explore storytelling and the delivery of news on small screens. <a href=\\\"https://medium.com/the-guardian-mobile-innovation-lab\\\">More about the Guardian Mobile Innovation Lab.</a><br></p>\",\"firstName\":\"usmobileinnovationlab\",\"lastName\":\"guardian\"}],\"isHosted\":false},{\"id\":\"us-news/2016/sep/25/us-presidential-debates-famous-moments\",\"type\":\"article\",\"sectionId\":\"us-news\",\"sectionName\":\"US news\",\"webPublicationDate\":\"2016-09-25T10:00:31Z\",\"webTitle\":\"Make or break: the defining moments of presidential debates\",\"webUrl\":\"https://www.theguardian.com/us-news/2016/sep/25/us-presidential-debates-famous-moments\",\"apiUrl\":\"https://content.guardianapis.com/us-news/2016/sep/25/us-presidential-debates-famous-moments\",\"tags\":[{\"id\":\"profile/davidsmith\",\"type\":\"contributor\",\"webTitle\":\"David Smith\",\"webUrl\":\"https://www.theguardian.com/profile/davidsmith\",\"apiUrl\":\"https://content.guardianapis.com/profile/davidsmith\",\"references\":[],\"bio\":\"<p>David Smith is the Guardian's Washington correspondent<br>• <a href=\\\"https://pgp.theguardian.com/PublicKeys/David%20Smith.pub.txt\\\"> David Smith's public key</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2016/2/9/1455017350853/David-Smith.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2016/2/9/1455017367880/David-Smith-L.png\",\"firstName\":\"david\",\"lastName\":\"smith\"}],\"isHosted\":false},{\"id\":\"australia-news/2017/jan/01/cabinet-papers-1992-93-victory-for-true-believers-kicks-off-lasting-debates\",\"type\":\"article\",\"sectionId\":\"australia-news\",\"sectionName\":\"Australia news\",\"webPublicationDate\":\"2016-12-31T13:01:28Z\",\"webTitle\":\"Cabinet papers 1992-93: victory for 'True Believers' kicks off lasting debates\",\"webUrl\":\"https://www.theguardian.com/australia-news/2017/jan/01/cabinet-papers-1992-93-victory-for-true-believers-kicks-off-lasting-debates\",\"apiUrl\":\"https://content.guardianapis.com/australia-news/2017/jan/01/cabinet-papers-1992-93-victory-for-true-believers-kicks-off-lasting-debates\",\"tags\":[{\"id\":\"profile/gabrielle-chan\",\"type\":\"contributor\",\"webTitle\":\"Gabrielle Chan\",\"webUrl\":\"https://www.theguardian.com/profile/gabrielle-chan\",\"apiUrl\":\"https://content.guardianapis.com/profile/gabrielle-chan\",\"references\":[],\"bio\":\"<p>Gabrielle Chan is the chief political correspondent for Guardian Australia. She has been a journalist for 30 years</p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2015/5/20/1432098318579/Gabrielle_Chan_140x140.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2015/5/20/1432098388381/Gabrielle_Chan_L.png\",\"firstName\":\"gabrielle\",\"lastName\":\"chan\",\"twitterHandle\":\"gabriellechan\"}],\"isHosted\":false},{\"id\":\"us-news/2016/sep/02/presidential-debate-moderators-martha-raddatz-anderson-cooper\",\"type\":\"article\",\"sectionId\":\"us-news\",\"sectionName\":\"US news\",\"webPublicationDate\":\"2016-09-02T16:53:02Z\",\"webTitle\":\"The Clinton-Trump presidential debates: who are the moderators?\",\"webUrl\":\"https://www.theguardian.com/us-news/2016/sep/02/presidential-debate-moderators-martha-raddatz-anderson-cooper\",\"apiUrl\":\"https://content.guardianapis.com/us-news/2016/sep/02/presidential-debate-moderators-martha-raddatz-anderson-cooper\",\"tags\":[{\"id\":\"profile/tommccarthy\",\"type\":\"contributor\",\"webTitle\":\"Tom McCarthy\",\"webUrl\":\"https://www.theguardian.com/profile/tommccarthy\",\"apiUrl\":\"https://content.guardianapis.com/profile/tommccarthy\",\"references\":[],\"bio\":\"<p>Tom McCarthy joined the Guardian US in 2012. He was previously the newswriter on ABC News's Nightline. He has worked at the Daily Star (Beirut) and the Omaha World-Herald.</p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2015/8/31/1441007677105/Tom-McCarthy.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2015/8/31/1441007694432/Tom-McCarthy-R.png\",\"firstName\":\"tom\",\"lastName\":\"mccarthy\",\"twitterHandle\":\"TeeMcSee\"}],\"isHosted\":false},{\"id\":\"us-news/2016/oct/19/where-is-climate-change-in-the-trump-v-clinton-presidential-debates\",\"type\":\"article\",\"sectionId\":\"us-news\",\"sectionName\":\"US news\",\"webPublicationDate\":\"2016-10-19T12:00:07Z\",\"webTitle\":\"Why has climate change been ignored in the US election debates?\",\"webUrl\":\"https://www.theguardian.com/us-news/2016/oct/19/where-is-climate-change-in-the-trump-v-clinton-presidential-debates\",\"apiUrl\":\"https://content.guardianapis.com/us-news/2016/oct/19/where-is-climate-change-in-the-trump-v-clinton-presidential-debates\",\"tags\":[{\"id\":\"profile/oliver-milman\",\"type\":\"contributor\",\"webTitle\":\"Oliver Milman\",\"webUrl\":\"https://www.theguardian.com/profile/oliver-milman\",\"apiUrl\":\"https://content.guardianapis.com/profile/oliver-milman\",\"references\":[],\"bio\":\"<p>Oliver Milman is an environment reporter for the Guardian US. Follow him on Twitter: <a href=\\\"https://twitter.com/olliemilman\\\">@olliemilman</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2014/9/30/1412084830432/Oliver-Milman.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2014/9/30/1412084850932/Oliver-Milman-L.png\",\"firstName\":\"oliver\",\"lastName\":\"milman\",\"twitterHandle\":\"olliemilman\"}],\"isHosted\":false},{\"id\":\"science/political-science/2016/oct/24/hinkley-c-shows-the-value-of-social-science-in-the-most-toxic-public-debates\",\"type\":\"article\",\"sectionId\":\"science\",\"sectionName\":\"Science\",\"webPublicationDate\":\"2016-10-24T06:30:07Z\",\"webTitle\":\"Hinkley C shows the value of social science in the most toxic public debates\",\"webUrl\":\"https://www.theguardian.com/science/political-science/2016/oct/24/hinkley-c-shows-the-value-of-social-science-in-the-most-toxic-public-debates\",\"apiUrl\":\"https://content.guardianapis.com/science/political-science/2016/oct/24/hinkley-c-shows-the-value-of-social-science-in-the-most-toxic-public-debates\",\"tags\":[{\"id\":\"profile/andy-stirling\",\"type\":\"contributor\",\"webTitle\":\"Andy Stirling\",\"webUrl\":\"https://www.theguardian.com/profile/andy-stirling\",\"apiUrl\":\"https://content.guardianapis.com/profile/andy-stirling\",\"references\":[],\"bio\":\"<p><a href=\\\"http://www.sussex.ac.uk/spru/people/peoplelists/person/7513\\\">Andy Stirling</a> is professor of science and technology policy at the University of Sussex</p>\",\"firstName\":\"andy\",\"lastName\":\"stirling\"}],\"isHosted\":false},{\"id\":\"us-news/2016/oct/22/clinton-trump-debate-analysis-foreign-policy-economy-scandals\",\"type\":\"article\",\"sectionId\":\"us-news\",\"sectionName\":\"US news\",\"webPublicationDate\":\"2016-10-22T11:00:15Z\",\"webTitle\":\"What did Clinton and Trump talk about in the debates – and for how long?\",\"webUrl\":\"https://www.theguardian.com/us-news/2016/oct/22/clinton-trump-debate-analysis-foreign-policy-economy-scandals\",\"apiUrl\":\"https://content.guardianapis.com/us-news/2016/oct/22/clinton-trump-debate-analysis-foreign-policy-economy-scandals\",\"tags\":[{\"id\":\"profile/mazin-sidahmed\",\"type\":\"contributor\",\"webTitle\":\"Mazin Sidahmed\",\"webUrl\":\"https://www.theguardian.com/profile/mazin-sidahmed\",\"apiUrl\":\"https://content.guardianapis.com/profile/mazin-sidahmed\",\"references\":[],\"bio\":\"<p>Mazin Sidahmed is a breaking news fellow at the Guardian US.</p>\",\"firstName\":\"sidahmed\",\"lastName\":\"mazin\"},{\"id\":\"profile/nicole-puglise\",\"type\":\"contributor\",\"webTitle\":\"Nicole Puglise\",\"webUrl\":\"https://www.theguardian.com/profile/nicole-puglise\",\"apiUrl\":\"https://content.guardianapis.com/profile/nicole-puglise\",\"references\":[],\"bio\":\"<p>Nicole Puglise is a breaking news fellow at the Guardian US. Follow her on Twitter @nicolepuglise or email her at&nbsp;<a href=\\\"mailto:nicole.puglise@guardian.co.uk\\\">nicole.puglise@guardian.co.uk</a>.</p>\",\"firstName\":\"puglise\",\"lastName\":\"nicole\",\"twitterHandle\":\"nicolepuglise\"},{\"id\":\"profile/jan-diehm\",\"type\":\"contributor\",\"webTitle\":\"Jan Diehm\",\"webUrl\":\"https://www.theguardian.com/profile/jan-diehm\",\"apiUrl\":\"https://content.guardianapis.com/profile/jan-diehm\",\"references\":[],\"bio\":\"<p>Jan Diehm is a graphics producer for the Guardian US. Follow her on twitter&nbsp;<a href=\\\"https://twitter.com/jadiehm\\\">@jadiehm</a></p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2016/09/28/jan-diehm.jpg\",\"firstName\":\"diehm\",\"lastName\":\"jan\"}],\"isHosted\":false},{\"id\":\"politics/2016/jun/02/eu-referendum-tv-debates-when-where-watch-them\",\"type\":\"article\",\"sectionId\":\"politics\",\"sectionName\":\"Politics\",\"webPublicationDate\":\"2016-06-09T12:53:46Z\",\"webTitle\":\"EU referendum debates: when and where to watch them\",\"webUrl\":\"https://www.theguardian.com/politics/2016/jun/02/eu-referendum-tv-debates-when-where-watch-them\",\"apiUrl\":\"https://content.guardianapis.com/politics/2016/jun/02/eu-referendum-tv-debates-when-where-watch-them\",\"tags\":[{\"id\":\"profile/jessica-elgot\",\"type\":\"contributor\",\"webTitle\":\"Jessica Elgot\",\"webUrl\":\"https://www.theguardian.com/profile/jessica-elgot\",\"apiUrl\":\"https://content.guardianapis.com/profile/jessica-elgot\",\"references\":[],\"bio\":\"<p>Jessica Elgot is a&nbsp;political reporter&nbsp;for the Guardian. She was previously the Huffington Post UK's assistant&nbsp;news editor. She tweets from <a href=\\\"https://twitter.com/jessicaelgot\\\">@jessicaelgot</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2015/6/26/1435313697913/Jessica-Elgot.jpg\",\"bylineLargeImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2015/6/26/1435313719448/Jessica-Elgot-R.png\",\"firstName\":\"jessica\",\"lastName\":\"elgot\"}],\"isHosted\":false},{\"id\":\"uk-news/2016/mar/04/slough-debates-whether-to-change-its-name\",\"type\":\"article\",\"sectionId\":\"uk-news\",\"sectionName\":\"UK news\",\"webPublicationDate\":\"2016-03-04T17:54:54Z\",\"webTitle\":\"Slough debates whether to change its name\",\"webUrl\":\"https://www.theguardian.com/uk-news/2016/mar/04/slough-debates-whether-to-change-its-name\",\"apiUrl\":\"https://content.guardianapis.com/uk-news/2016/mar/04/slough-debates-whether-to-change-its-name\",\"tags\":[{\"id\":\"profile/marktran\",\"type\":\"contributor\",\"webTitle\":\"Mark Tran\",\"webUrl\":\"https://www.theguardian.com/profile/marktran\",\"apiUrl\":\"https://content.guardianapis.com/profile/marktran\",\"references\":[],\"bio\":\"<p>Mark Tran reports on general news. He was previously a reporter on the Guardian's Global development site (June 2011-March 2014). Before that, he worked as a correspondent for the Guardian in Washington (1984-90) and New York (1990-99). Follow <a href=\\\"http://twitter.com/marktran\\\">Mark on Twitter</a> and <a href=\\\"https://plus.google.com/104680581124639975151?rel=author\\\">Google+</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2007/11/08/mark_tran_140x140.jpg\",\"firstName\":\"mark\",\"lastName\":\"tran\",\"twitterHandle\":\"marktran\"}],\"isHosted\":false}]}}";

    public static final String SAMPLE_QUERY_URL = "http://content.guardianapis.com/search?q=trump&show-fields=byline&show-tags=contributor&api-key=test";

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
