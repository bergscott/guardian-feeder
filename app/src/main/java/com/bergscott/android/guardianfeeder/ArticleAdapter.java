package com.bergscott.android.guardianfeeder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@link ArrayAdapter} implementation for a list of theguardian.com news articles
 */

class ArticleAdapter extends ArrayAdapter<Article> {

    /* date format for displaying the date of an article */
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    public ArticleAdapter(Context context, List<Article> articleList) {
        super(context, 0, articleList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        // inflate the xml view if needed
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item,
                    parent, false);
        }

        // get the current Article from the list
        Article currentArticle = getItem(position);

        // get the title of the article and update its text view
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.article_title);
        titleTextView.setText(currentArticle.getTitle());

        // get the section of the article and update its text view
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.article_section);
        sectionTextView.setText(currentArticle.getSection());

        // get the author of the article and update its text view
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.article_author);
        if (currentArticle.getAuthor() == null) {
            authorTextView.setVisibility(View.GONE);
        } else {
            authorTextView.setVisibility(View.VISIBLE);
            authorTextView.setText(currentArticle.getAuthor());
        }

        // get the article's date and convert it to a new Date object
        Date articleDate = new Date(currentArticle.getDateInMilliseconds());

        // get the date of the article and update its text view
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.article_date);
        dateTextView.setText(DATE_FORMAT.format(articleDate));

        return listItemView;
    }
}
