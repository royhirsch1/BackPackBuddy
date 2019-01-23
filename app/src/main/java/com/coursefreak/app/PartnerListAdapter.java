package com.coursefreak.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PartnerListAdapter extends ArrayAdapter<CoursePartner> {
    public PartnerListAdapter(Context context, ArrayList<CoursePartner> partners) {
        super(context, 0, partners);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final CoursePartner partner = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.partner_list_item, parent, false);
        }
        // Lookup view for data population
        TextView email = (TextView) convertView.findViewById(R.id.partner_email);

        //TODO reverse order and show name once we add this field
        email.setText(partner.getEmail());
        //email.setText(partner.getEmail());

        de.hdodenhof.circleimageview.CircleImageView partnerImg = convertView.findViewById(R.id.partner_img);

        //Random index TODO replace with real image data
        Random rand = new Random();

        int idx = rand.nextInt(3);
        int[] img_res = {
                R.drawable.icon_fox, R.drawable.icon_panda, R.drawable.icon_monkey
        };
        partnerImg.setImageResource(img_res[idx]);

        ImageButton copyBtn = convertView.findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(getContext(),partner.getEmail());
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        // Return the completed view to render on screen
        return convertView;


    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
