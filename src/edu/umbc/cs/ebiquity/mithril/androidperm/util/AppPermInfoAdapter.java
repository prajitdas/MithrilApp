package edu.umbc.cs.ebiquity.mithril.androidperm.util;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.umbc.cs.ebiquity.mithril.androidperm.R;

public class AppPermInfoAdapter extends ArrayAdapter<PermissionInfo> {
	private List<PermissionInfo> permList;
	private Context context;
	private PackageManager packageManager;
	
	public AppPermInfoAdapter(Context context, int resource, 
			List<PermissionInfo> objects) {
		super(context, resource, objects);

		this.context = context;
		this.permList = objects;
		packageManager = context.getPackageManager();
	}
	
	@Override
	public int getCount() {
		return ((permList != null) ? permList.size() : 0);
	}

	@Override
	public PermissionInfo getItem(int position) {
		return 	((permList != null) ? permList.get(position) : null);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.app_list_item, null);
		}
		
		PermissionInfo data = permList.get(position);
		
		if(data != null) {
			TextView appName = (TextView) view.findViewById(R.id.app_name);
			TextView packageName = (TextView) view.findViewById(R.id.app_package);
			ImageView iconView = (ImageView) view.findViewById(R.id.app_icon);
			
			appName.setText(data.loadLabel(packageManager));
			packageName.setText(data.packageName);
			iconView.setImageDrawable(data.loadIcon(packageManager));
		}
		return view;
	}

}