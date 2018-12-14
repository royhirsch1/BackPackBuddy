package com.example.coursefreak.coursefreak;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GuestCoursesRecyclerAdapter extends RecyclerView.Adapter<GuestCoursesRecyclerAdapter.ViewHolder> {
    private List<Course> courseList;
    private Context context;
    private List<GuestCoursesRecyclerAdapter.ViewHolder> views;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckedTextView courseNameTitle;
        public ImageButton moreInfoButton;
        public CheckedTextView coursePercentageTitle;
        public CheckedTextView courseReviewsTitle;
        public ConstraintLayout courseInfoLayout;
        public Double percents;
        public ViewHolder(View v) {
            super(v);
            this.courseNameTitle       = (CheckedTextView) v.findViewById(R.id.textCourseName);
            this.coursePercentageTitle = (CheckedTextView) v.findViewById(R.id.textPercentageRate);
            this.courseReviewsTitle    = (CheckedTextView) v.findViewById(R.id.textReviewNumber);
            this.moreInfoButton        = (ImageButton) v.findViewById(R.id.buttonMoreInfo);
            this.courseInfoLayout      = (ConstraintLayout) v.findViewById(R.id.courseRowLayout);
            this.percents = 0.0;
            // Onclicklistener will be set in Activity.
        }
    }

    public GuestCoursesRecyclerAdapter(List<Course> courses, Context ctx) {
        this.courseList = courses;
        this.views = new ArrayList<>();
        this.context = ctx;
    }

    public List<GuestCoursesRecyclerAdapter.ViewHolder> getViews() {
        return this.views;
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull GuestCoursesRecyclerAdapter.ViewHolder viewHolder, int i) {
        Course c = this.courseList.get(i);
        viewHolder.courseNameTitle.setText(c.getName());
        Double percentPositive = getPositiveReviewPercent(c.pos, c.neg);
        viewHolder.percents = percentPositive;
        viewHolder.coursePercentageTitle.setText(percentPositive.toString());
        viewHolder.courseReviewsTitle.setText("Reviews: ".concat(c.reviewNumber.toString()));
        this.views.add(viewHolder);
    }

    @NonNull
    @Override
    public GuestCoursesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.course_row_guest, viewGroup,false);
        return new ViewHolder(item);
    }

    @Override
    public int getItemCount() {
        return this.courseList.size();
    }

    private static Double getPositiveReviewPercent(Integer pos, Integer neg) {
//        int p = Integer.getInteger(pos);
//        int n = Integer.getInteger(neg);
        if(pos + neg == 0) {
            return 0.0;
        }
        return ((double)pos) / (pos + neg);
    }

}
