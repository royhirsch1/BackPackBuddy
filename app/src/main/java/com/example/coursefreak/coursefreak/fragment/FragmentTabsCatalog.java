package com.example.coursefreak.coursefreak.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.coursefreak.coursefreak.Course;
import com.example.coursefreak.coursefreak.CourseLineAdapter;
import com.example.coursefreak.coursefreak.R;

import java.util.ArrayList;

public class FragmentTabsCatalog extends Fragment {

    public FragmentTabsCatalog() {
    }

    public static FragmentTabsCatalog newInstance() {
        FragmentTabsCatalog fragment = new FragmentTabsCatalog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.catalog, container, false);

        //Replace this code with real data
        final ArrayList<Course> res = new ArrayList<>();
        res.add(new Course("234123", "name", 12.5,
                4, 6, 80.9, "cate",
                "hw"));
        res.add(new Course("234100", "name2", 12.5,
                4, 6, 80.9, "cate",
                "hw"));
        final ListView lv = root.findViewById(R.id.catalogCoursesListView);
//        CourseLineAdapter cla = new CourseLineAdapter(getContext(), res, null);
//        lv.setAdapter(cla);


        return root;
    }
}