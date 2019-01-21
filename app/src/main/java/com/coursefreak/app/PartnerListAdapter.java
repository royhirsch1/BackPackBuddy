package com.coursefreak.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PartnerListAdapter extends ArrayAdapter<CoursePartner> {
    public PartnerListAdapter(Context context, ArrayList<CoursePartner> partners) {
        super(context, 0, partners);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CoursePartner partner = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.partner_list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.partner_name);
        TextView email = (TextView) convertView.findViewById(R.id.partner_email);

        //TODO reverse order and show name once we add this field
        name.setText(partner.getEmail());
        email.setText("");
        //email.setText(partner.getEmail());

        de.hdodenhof.circleimageview.CircleImageView partnerImg = convertView.findViewById(R.id.partner_img);

        //Random index TODO replace with real image data
        Random rand = new Random();

        int idx = rand.nextInt(3);
        int[] img_res = {
                R.drawable.icon_fox, R.drawable.icon_panda, R.drawable.icon_monkey
        };
        partnerImg.setImageResource(img_res[idx]);
        // Return the completed view to render on screen
        return convertView;
    }
}
