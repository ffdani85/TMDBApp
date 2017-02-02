package com.danifernandez.tmdbapp.ws;

public class WebServiceResult{
	private int status;
	private String response, ws_name;
	private long ts;
	
	public WebServiceResult(int status, String response){
		this.status = status;
		this.response = response;
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public String getResponse(){
		return response;
	}
	
	public void setResponse(String response){
		this.response = response;
	}

	public String getName(){
		return ws_name;
	}

	public void setName(String name){
		this.ws_name = name;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
}