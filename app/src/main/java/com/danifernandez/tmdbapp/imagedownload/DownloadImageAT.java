package com.danifernandez.tmdbapp.imagedownload;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageAT extends AsyncTask<String, Void, DownloadImageResult> {

	private ProgressDialog dialog;
	private Context context;
	private DownloadImageCompleteListener listener;
	private DownloadImageResult res;
	private int id;
	private boolean isSearch;
	private boolean useDialog;
	private String message;
			
	public DownloadImageAT(Context context, DownloadImageCompleteListener listener){
		this.context = context;
		this.listener = listener;
	}
	
	public void AddDialog(){
		useDialog = true;
    }
	
	public void setDialogMessage(String message){
		this.message = message; 
	}
	
	public void setId(int id){
		this.id = id;
	}

	public void setSearch(boolean search) {
		isSearch = search;
	}

	@Override
    protected void onPreExecute() {
		super.onPreExecute();
		if(useDialog){
			super.onPreExecute();
	        dialog = new ProgressDialog(context);
			if(message!=null){
				dialog.setMessage(message);
			}
			else{
				dialog.setMessage("Downloading image...");
			}
	        dialog.setIndeterminate(true);
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(true);
	    	dialog.show();
		}
    }
		
	@Override
    protected DownloadImageResult doInBackground(String... urls)
    {
        InputStream is = downloadImage(urls[0]);
		Bitmap bitmap = null;
        if(is != null){
			bitmap = BitmapFactory.decodeStream(is);
        }
        
        res = new DownloadImageResult(id, bitmap, isSearch);
        return res;
    }
	
	@Override
    protected void onPostExecute(DownloadImageResult result) {
		listener.onTaskComplete(result);
		if(useDialog){
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
		}
    }
	
	private InputStream downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            return is;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}