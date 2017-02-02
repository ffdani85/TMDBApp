package com.danifernandez.tmdbapp.imagedownload;

import android.graphics.Bitmap;

public class DownloadImageResult{
	private int id;
	private Bitmap bitmap;
	private boolean isSearch;
	
	public DownloadImageResult(int id, Bitmap bitmap, boolean isSearch){
		this.id = id;
		this.bitmap = bitmap;
		this.isSearch = isSearch;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean search) {
		isSearch = search;
	}
}