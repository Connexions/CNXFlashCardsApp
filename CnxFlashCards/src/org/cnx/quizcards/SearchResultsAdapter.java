/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseAdapter {
    
    private ArrayList<SearchResult> searchResults;
    
    private LayoutInflater inflater;
    
    
    /** Constructor */
    public SearchResultsAdapter(Context context, ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
        inflater = LayoutInflater.from(context);
    }
    

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    
    /** Constructor */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_results_row, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtAuthors = (TextView) convertView.findViewById(R.id.authors);
            holder.txtUrl = (TextView) convertView.findViewById(R.id.url);
 
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        holder.txtTitle.setText(searchResults.get(position).getTitle());
        holder.txtAuthors.setText(searchResults.get(position).getAuthors());
        holder.txtUrl.setText(searchResults.get(position).getUrl());
 
        return convertView;
    }
 
    static class ViewHolder {
        TextView txtTitle;
        TextView txtAuthors;
        TextView txtUrl;
    }
}
