package com.coursefreak.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class CourseViewReviewAdapter extends ArrayAdapter<Review> {
    public CourseViewReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Review review = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.courseview_review_item, parent, false);
        }
        // Lookup view for data population
        TextView reviewContent = (TextView) convertView.findViewById(R.id.review_text);
        TextView reviewerEmail = (TextView) convertView.findViewById(R.id.user_email);

        de.hdodenhof.circleimageview.CircleImageView userImg = convertView.findViewById(R.id.user_img);

        // Populate the data into the template view using the data object
        //TODO slice text - whole words only
        String edited_str = review.getReviewText().length()>30? review.getReviewText().substring(0,29)+"..." : review.getReviewText();
        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "fonts/ArimaMadurai-Bold.ttf");
        reviewContent.setTypeface(face);
        reviewContent.setText(edited_str);
        // TODO change to email
        reviewerEmail.setText(review.getUserID());
        reviewerEmail.setTypeface(face);

        //Random index TODO replace with real image data
        Random rand = new Random();

        int idx = rand.nextInt(10);
        int[] img_res = {
                R.drawable.avataaars0, R.drawable.avataaars1, R.drawable.avataaars2,
                R.drawable.avataaars3, R.drawable.avataaars4, R.drawable.avataaars5,
                R.drawable.avataaars6, R.drawable.avataaars7, R.drawable.avataaars8,
                R.drawable.avataaars9
        };
        userImg.setImageResource(img_res[idx]);
        // Return the completed view to render on screen
        return convertView;
    }
}
