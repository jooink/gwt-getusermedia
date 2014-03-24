package com.jooink.experiments.getusermedia.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class UserMediaStream extends JavaScriptObject {
	protected UserMediaStream() {}
	
	public native void stop() /*-{
		if(this.stop) 
			this.stop();
	}-*/;
}