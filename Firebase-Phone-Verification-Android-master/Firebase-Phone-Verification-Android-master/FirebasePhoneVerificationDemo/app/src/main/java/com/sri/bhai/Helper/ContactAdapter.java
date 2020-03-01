package com.sri.bhai.Helper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sri.bhai.R;
import com.sri.bhai.model.ContactItem;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<ContactItem> implements Filterable {

    private Context context;
    private List<ContactItem> contacts, filterList;
    private LayoutInflater inflater;
    private ContactFilter filter;

    public ContactAdapter(Context context, List<ContactItem> contacts) {
        super(context, R.layout.row_item, contacts);
        this.contacts = contacts;
        this.context = context;
        filterList = new ArrayList<>();
        this.filterList.addAll(contacts);
    }


    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ContactFilter();
        return filter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_item, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.photo = (ImageView) view.findViewById(R.id.photo);
        viewHolder.number = (TextView) view.findViewById(R.id.phone);
        viewHolder.check = (CheckBox) view.findViewById(R.id.check);
        viewHolder.check.setVisibility(View.GONE);
        viewHolder.name.setText(contacts.get(position).getName());
        viewHolder.number.setText(contacts.get(position).getChecked());
        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    Log.e("postion",""+contacts.get(position).getPhone());
                }

            }
        });
        return view;
    }




    public class ViewHolder {
        ImageView photo;
        TextView name, number;
        CheckBox check;
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String data = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            if (data.length() > 0) {
                List<ContactItem> filteredList = new ArrayList<>(filterList);
                List<ContactItem> nList = new ArrayList<>();
                int count = filteredList.size();
                for (int i = 0; i < count; i++) {
                    ContactItem item = filteredList.get(i);
                    String name = item.getName().toLowerCase();
                    String phone = item.getPhone().toLowerCase();
                    if (name.startsWith(data) || phone.startsWith(data))
                        nList.add(item);
                }
                results.count = nList.size();
                results.values = nList;
            } else {
                List<ContactItem> list = new ArrayList<>(filterList);
                results.count = list.size();
                results.values = list;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts = (ArrayList<ContactItem>) results.values;
            clear();
            for (int i = 0; i < contacts.size(); i++) {
                ContactItem item = (ContactItem) contacts.get(i);
                add(item);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}