package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdypda.R;

import java.util.List;

/**
 * Created by DengJf on 2018/3/14.
 */

public class MainAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int childResources;
    private int groupResources;
    private List<String> groupTitles;
    private List<List<String>> titles;
    private List<List<Integer>> imgs;

    public MainAdapter(Context context, int childResources, int groupResources, List<String> groupTitles, List<List<String>> titles, List<List<Integer>> imgs) {
        this.context = context;
        this.childResources = childResources;
        this.groupResources = groupResources;
        this.groupTitles = groupTitles;
        this.titles = titles;
        this.imgs = imgs;
    }

    @Override
    public int getGroupCount() {
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return titles.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return titles.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*1000+childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        if (convertView==null){
            view= LayoutInflater.from(context).inflate(groupResources,null);
        }else {
            view=convertView;
        }
        TextView groupTitle=(TextView)view.findViewById(R.id.title);
        groupTitle.setText(groupTitles.get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        if (convertView==null){
            view= LayoutInflater.from(context).inflate(childResources,null);
        }else {
            view=convertView;
        }
        ImageView imageView=(ImageView)view.findViewById(R.id.img);
        TextView titleText=(TextView)view.findViewById(R.id.title);
        imageView.setImageResource(imgs.get(groupPosition).get(childPosition));
        titleText.setText(titles.get(groupPosition).get(childPosition));
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
