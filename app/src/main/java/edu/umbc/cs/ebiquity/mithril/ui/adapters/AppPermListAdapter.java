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

/**
 * Created by Prajit on 11/9/2016.
 */

public class AppPermListAdapter extends ArrayAdapter<PermissionInfo> {
    private List<PermissionInfo> permList;
    private Context context;
    private PackageManager packageManager;

    public AppPermListAdapter(Context context, int resource, List<PermissionInfo> objects) {
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

        PermissionInfo data = permList.get(position);

        if (data != null) {
            TextView permissionName = (TextView) view.findViewById(R.id.perm_name);
            TextView permissionDescription = (TextView) view.findViewById(R.id.perm_description);
            TextView permissionProtectionLevel = (TextView) view.findViewById(R.id.protection_level);
            TextView permissionGroup = (TextView) view.findViewById(R.id.perm_group);

            permissionName.setText(data.loadLabel(packageManager));

            if (data.loadDescription(packageManager) != null)
                permissionDescription.setText(data.loadDescription(packageManager));
            else
                permissionDescription.setText(R.string.no_description_available_txt);

            String protctionLevel = new String();

            switch (data.protectionLevel) {
                /**
                 * Colors from: https://design.google.com/articles/evolving-the-google-identity/
                 */
                case PermissionInfo.PROTECTION_NORMAL:
                    protctionLevel = "normal";
                    //Google Green
                    view.setBackgroundColor(Color.parseColor("#34A853"));
                    break;
                case PermissionInfo.PROTECTION_DANGEROUS:
                    protctionLevel = "dangerous";
                    //Google Red
                    view.setBackgroundColor(Color.parseColor("#EA4335"));
                    break;
                case PermissionInfo.PROTECTION_SIGNATURE:
                    //Google Blue
                    protctionLevel = "signature";
                    view.setBackgroundColor(Color.parseColor("#4285F4"));
                    break;
                case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                    //Google Yellow
                    protctionLevel = "signatureOrSystem";
                    view.setBackgroundColor(Color.parseColor("#FBBC05"));
                    break;
                case PermissionInfo.PROTECTION_FLAG_SYSTEM:
                    protctionLevel = "system";
                    view.setBackgroundColor(Color.CYAN);
                    break;
                default:
                    protctionLevel = "<unknown>";
                    view.setBackgroundColor(Color.GRAY);
                    break;
            }

            permissionProtectionLevel.setText(protctionLevel);

            if (data.group != null)
                permissionGroup.setText(data.group);
        }
        return view;
    }
}
