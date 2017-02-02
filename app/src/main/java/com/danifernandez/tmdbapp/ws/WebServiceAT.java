package com.danifernandez.tmdbapp.ws;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.danifernandez.tmdbapp.utils.MyLog;
import com.danifernandez.tmdbapp.ws.WebService.RequestMethod;

import org.json.JSONObject;

import java.util.ArrayList;

public class WebServiceAT extends AsyncTask<Void, Void, WebServiceResult> {

	private ProgressDialog dialog;
	private boolean useDialog, useName;
	private String wsName, message;
	private Context context;

	private String url;
	private RequestMethod method;
	private ArrayList<KeyValue> headers;
	private ArrayList<KeyValue> params;
	private JSONObject json;
	private WebServiceCompleteListener listener;

	public WebServiceAT(Context context, WebServiceCompleteListener listener, String url, RequestMethod method){
		this.context = context;
		this.listener = listener;
		this.url = url;
		this.method = method;
		this.useDialog = false;
		this.useName = false;
		this.headers = new ArrayList<>();
		this.params = new ArrayList<>();
	}
	
	public void addDialog(){
		if(context!=null){
			useDialog = true;
		}
    }
	
	public void setDialogMessage(String message){
		this.message = message; 
	}
	
	public void addName(String name){
		wsName = name;
		useName = true;
	}

	public void addHeader(String name, String value){
		headers.add(new KeyValue(name, value));
	}

	public void addParam(String name, String value) {
		params.add(new KeyValue(name, value));
	}

	public void addJSON(JSONObject json){
		this.json = json;
	}

	@Override
    protected void onPreExecute() {
		if(useDialog){
			super.onPreExecute();
	        dialog = new ProgressDialog(context);
			if(message!=null){
				dialog.setMessage(message);
			}
			else{
				dialog.setMessage("Loading...");
			}
	        dialog.setIndeterminate(true);
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(true);
	    	dialog.show();
		}
    }
	
	@Override
    protected WebServiceResult doInBackground(Void... nothing) {
		
		WebService webService = new WebService(url);
		
		try {
			for(KeyValue header : headers){
				webService.addHeader(header.getKey(), header.getValue());
			}
			if(method.equals(RequestMethod.GET) || method.equals(RequestMethod.DELETE)){
				for(KeyValue param : params){
					webService.addParam(param.getKey(), param.getValue());
				}
			}
			else if(method.equals(RequestMethod.POST) || method.equals(RequestMethod.PUT)) {
				webService.addJSON(json);
			}
			if(!isCancelled()) {
				webService.execute(method);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		WebServiceResult ws_res = new WebServiceResult(webService.getResponseCode(),webService.getResponse());
		if(isCancelled()){
			MyLog.d("WebServiceAT", "doInBackground. after execute. is cancelled");
			ws_res.setStatus(-1);
			ws_res.setResponse(null);
		}
		if(useName){
			ws_res.setName(wsName);
		}
		
		return ws_res;
    }
	
	@Override
    protected void onPostExecute(WebServiceResult result) {
		MyLog.d("WebServiceAT", "onPostExecute");
		listener.onTaskComplete(result);
		if(useDialog){
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
		}
    }

	@Override
	protected void onCancelled() {
		MyLog.d("WebServiceAT", "onCancelled. name: " + this.wsName);
		super.onCancelled();
	}
}