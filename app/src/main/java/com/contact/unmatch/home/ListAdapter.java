package com.contact.unmatch.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contact.unmatch.R;
import com.contact.unmatch.model.Message;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    ArrayList<Message> m_message_list;
    int m_width = 0;

    public ListAdapter(Context applicationContext, ArrayList<Message> message_list, int width) {
        this.context = context;
        m_message_list = message_list;
        inflter = (LayoutInflater.from(applicationContext));
        m_width = width;
    }
    @Override
    public int getCount() {
        return m_message_list.size();
    }

    @Override
    public Object getItem(int position) {
        return m_message_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.detail_layout, null);
        TextView name = (TextView)view.findViewById(R.id.contact_name);
        name.setText(m_message_list.get(i).getMessage());
        name.setMaxWidth(m_width);
        return view;
    }
}
