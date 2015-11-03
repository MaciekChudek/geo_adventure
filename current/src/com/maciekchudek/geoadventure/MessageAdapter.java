/**
* Manages an array of adventure messages
*  
*/

package com.maciekchudek.geoadventure;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.maciekchudek.geoadventure.R;

public class MessageAdapter extends ArrayAdapter<Message> {

	Context context;
	int resourceID;
	List<Message> messages;
	int closedLetter;
	int openLetter;

	public MessageAdapter(Context context, int resource, List<Message> objects) {
		super(context, resource, objects);
		this.context = context;
		resourceID = resource;
		messages = objects;
		closedLetter = R.drawable.closed_envelope;
		openLetter = R.drawable.open_envelope;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		DataHolder d;
		
		if(row != null){ //reuse the data if it's already there
			d = (DataHolder)row.getTag();
		}else{ //create the data anew			
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resourceID, parent, false);			
			d = new DataHolder();
			d.messageReadImage = (ImageView)row.findViewById(R.id.messageReadImage);
			d.messageTitle = (TextView)row.findViewById(R.id.messageTitle);
			row.setTag(d);
		}		
		
		Message m = messages.get(position);
		d.messageTitle.setText(m.title);
        if (m.read == 1){
        	d.messageReadImage.setImageResource(openLetter);
        }else{
        	d.messageReadImage.setImageResource(closedLetter);
        }		
		
		return row;
	}

	static class DataHolder {
		ImageView messageReadImage;
		TextView messageTitle;
	}

}
