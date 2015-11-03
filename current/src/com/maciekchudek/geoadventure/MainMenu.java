package com.maciekchudek.geoadventure;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class MainMenu {
	
	
	ExpandableListView mainMenuListView;
	MainMenuAdapter mainMenuAdapter;
	DatabaseInterface dbi;
	List<AdventureMetaData> adventures;
	View mainMenuView;
	Context parent;
	
	public MainMenu(Context context, DatabaseInterface dbi, ViewGroup parentContainer) {		
		this.parent = context;
		mainMenuView = View.inflate(parent, R.layout.main_menu, null);
		parentContainer.addView(mainMenuView);
		this.dbi = dbi;
		// find views by ids
		mainMenuListView = (ExpandableListView) mainMenuView.findViewById(R.id.main_menu_list_view);
		// attach listeners
		mainMenuListView.setOnChildClickListener(menuItemSelectedListener);
		// populate the menu
		populateMenus();
	}
	
	public void populateMenus() {
		adventures = dbi.getAdventuresMetaData();
		if (adventures.size() == 0)
			adventures.add(Constants.no_installed_adventures_message);

		mainMenuAdapter = new MainMenuAdapter(parent, adventures);
		mainMenuListView.setAdapter(mainMenuAdapter);
	}

	private OnChildClickListener menuItemSelectedListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView listViewParent, View v,
				int groupPosition, int childPosition, long id) {

			Constants.MenuGroups action = (Constants.MenuGroups) mainMenuAdapter
					.getGroup(groupPosition);
			AdventureMetaData adventure = (AdventureMetaData) mainMenuAdapter
					.getChild(groupPosition, childPosition);

			if (adventure.special_message != null) {
				Constants.showMessage(parent, adventure.name,
						adventure.special_message);
			} else {
				switch (action) {
				case DELETE:
					deleteAdventure(adventure);
					break;
				case PLAY:
					//Constants.showMessage(parent.getContext(), "play",adventure.name);
					Intent intent = new Intent(parent, GameActivity.class);
					intent.putExtra(Constants.INTENT_EXTRA_ADVENTURE_ID, adventure.id);
					parent.startActivity(intent);
					break;
				case LOAD:
					Constants.showMessage(parent, adventure.name,
							adventure.special_message);
					break;
				}
			}
			return true;
		}
	};

	private void deleteAdventure(final AdventureMetaData adventure) {

		new AlertDialog.Builder(parent)
				.setTitle("Delete this adventure?")
				.setMessage(
						"If you proceed this adventure and all the progress you have made on it will be irrevocably deleted from this device.")
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dbi.deleteAdventure(adventure.id);
								populateMenus();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}
}
