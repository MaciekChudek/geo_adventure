package com.maciekchudek.geoadventure;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MainMenuAdapter extends BaseExpandableListAdapter {

	List<AdventureMetaData> adventures;
	Object[] menus;		
	List<AdventureMetaData> loadMessages;
	Context context;
	
	public MainMenuAdapter(Context context, List<AdventureMetaData> adventures) {
		this.context = context;
		this.adventures = adventures;
		this.menus = Constants.MenuGroups.values();
		loadMessages = Constants.loadMessages;
	}

	
	
	@Override
	public int getGroupCount() {
		return menus.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(Constants.MenuGroups.LOAD.equals(menus[groupPosition])) return loadMessages.size();
		else return adventures.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return menus[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if(Constants.MenuGroups.LOAD.equals(menus[groupPosition])) return loadMessages.get(childPosition);
		else return adventures.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {		
		return groupPosition * 1000 + childPosition;
	}

	@Override
	public boolean hasStableIds() {		
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

        if (convertView == null) {
        	LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.main_menu_header, null);
        }
	    TextView txt = (TextView) convertView.findViewById(R.id.lblListHeader);
	    Constants.MenuGroups a = (Constants.MenuGroups)getGroup(groupPosition);
	    txt.setText(a.text);
	    
	    return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
			
        if (convertView == null) {
        	LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.main_menu_item, null);
        }
	    TextView name = (TextView) convertView.findViewById(R.id.adventure_menu_label_name);
		AdventureMetaData a = (AdventureMetaData)getChild(groupPosition, childPosition);
		name.setText(Html.fromHtml(a.htmlHeader()));
	    
	    return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}