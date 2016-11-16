package edu.umbc.cs.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;

/**
 * Created by Prajit on 11/9/2016.
 */

public class AppPermListAdapter extends ArrayAdapter<PermData> {
    private List<PermData> permList;
    private Context context;

    public AppPermListAdapter(Context context, int resource, List<PermData> objects) {
        super(context, resource, objects);

        this.context = context;
        this.permList = objects;
    }

    @Override
    public int getCount() {
        return ((permList != null) ? permList.size() : 0);
    }

    @Override
    public PermData getItem(int position) {
        return ((permList != null) ? permList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.app_detail_list_item, null);
        }

        PermData data = permList.get(position);

        if (data != null) {
            TextView permissionName = (TextView) view.findViewById(R.id.perm_name);
            TextView permissionDescription = (TextView) view.findViewById(R.id.perm_description);
            TextView permissionProtectionLevel = (TextView) view.findViewById(R.id.protection_level);
            TextView permissionGroup = (TextView) view.findViewById(R.id.perm_group);

            permissionName.setText(data.getPermissionName());

            if (data.getPermissionDescription() != null)
                permissionDescription.setText(data.getPermissionDescription());
            else
                permissionDescription.setText(R.string.no_description_available_txt);

            if (data.getPermissionProtectionLevel() == "normal") {
                permissionProtectionLevel.setText("normal");
                view.setBackgroundColor(Color.GREEN);
            } else if (data.getPermissionProtectionLevel() == "dangerous") {
                permissionProtectionLevel.setText("dangerous");
                view.setBackgroundColor(Color.RED);
            } else if (data.getPermissionProtectionLevel() == "signature") {
                permissionProtectionLevel.setText("signature");
                view.setBackgroundColor(Color.BLUE);
            } else if (data.getPermissionProtectionLevel() == "privileged") {
                permissionProtectionLevel.setText("privileged");
                view.setBackgroundColor(Color.YELLOW);
            } else if (data.getPermissionProtectionLevel() == "unknown") {
                permissionProtectionLevel.setText("unknown");
                view.setBackgroundColor(Color.GRAY);
            }

            if (data.getPermissionGroup() != null)
                permissionGroup.setText(data.getPermissionGroup());
        }
        return view;
    }
}
