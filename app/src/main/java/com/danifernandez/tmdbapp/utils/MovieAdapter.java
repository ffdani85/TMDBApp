package com.danifernandez.tmdbapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danifernandez.tmdbapp.R;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

	private Context context;
    private int layoutResourceId;
    private ArrayList<Movie> data = null;

    public MovieAdapter(Context context, int layoutResourceId, ArrayList<Movie> data, View rootView) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

	public void updateData(ArrayList<Movie> data){
		this.data = data;
		notifyDataSetChanged();
	}

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Movie getItem(int position) {
        Movie item = this.data.get(position);
        return item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MovieHolder holder = null;
        
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new MovieHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtYear = (TextView) row.findViewById(R.id.txtYear);
            holder.txtOverview = (TextView) row.findViewById(R.id.txtOverview);

            row.setTag(holder);
        }
        else {
            holder = (MovieHolder)row.getTag();
        }
        
        Movie movie = getItem(position);
        holder.txtTitle.setText(movie.getTitle());
		holder.txtYear.setText(movie.getYear());
		holder.txtOverview.setText(movie.getOverview());
        Bitmap bm = movie.getBitmap();
        if(bm!=null){
			holder.imgIcon.setImageBitmap(bm);
    	}
    	else{
			// use default movie image
            holder.imgIcon.setImageResource(R.mipmap.ic_image_black_48dp);
    	}
        return row;
    }
    
    static class MovieHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtYear;
        TextView txtOverview;
    }

}