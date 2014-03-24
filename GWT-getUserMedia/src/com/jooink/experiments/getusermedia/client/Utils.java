package com.jooink.experiments.getusermedia.client;




public class Utils {
	
	public interface UserMediaCallback {
		public void onSuccess(String url);
		public void onFail();
	}

	
	public interface UserMediaStreamCallback {
		public void onSuccess(UserMediaStream url);
		public void onFail();
	}

	static {
		init();
	}
	
	private native static void init() /*-{
	
	   navigator.getMedia = ( navigator.getUserMedia ||
                    navigator.webkitGetUserMedia ||
                    navigator.mozGetUserMedia ||
                    navigator.msGetUserMedia);

	}-*/;
	
	public static boolean getUserVideo(final UserMediaCallback callback) {
		return Utils.getUserVideoStream(new UserMediaStreamCallback() {
			
			@Override
			public void onSuccess(UserMediaStream stream) {
				callback.onSuccess(Utils.createUrl(stream));
			}
			
			@Override
			public void onFail() {
				callback.onFail();
			}
		});
    };
	
	
	
//	public native static boolean getUserVideoStream(UserMediaStreamCallback callback) /*-{
//	
//	  if(navigator.getMedia) {
//	  	var constant ={video: { mandatory: {width: {max: 320}}}};
//	  	alert(JSON.stringify(constant));
// 		navigator.getMedia(
// 			constant, 
// 			function(stream) {
// 				$entry(callback.@com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback::onSuccess(Lcom/jooink/experiments/getusermedia/client/UserMediaStream;)(stream));
//			}, 
//			function(e) { 
//				alert(e);
//				console.log(e); 
//				
//				$entry(callback.@com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback::onFail()());
//			});
//		return true;
//	} else {
//		return false;
//	}
//	}-*/;
//	
	
	public native static boolean getUserVideoStream(UserMediaStreamCallback callback) /*-{
	
	  if(navigator.getMedia) {
 		navigator.getMedia(
 			{video: true, toString: function() {return "video";}}, 
 			function(stream) {
 				$entry(callback.@com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback::onSuccess(Lcom/jooink/experiments/getusermedia/client/UserMediaStream;)(stream));
			}, 
			function(e) {
				$entry(callback.@com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback::onFail()());
			});
		return true;
	} else {
		return false;
	}
	}-*/;

	
	public native static String createUrl(UserMediaStream jso) /*-{
			return $wnd.URL.createObjectURL(jso);
	}-*/;

	public native static void revokeUrl(String s) /*-{
		 $wnd.URL.revokeObjectURL(s);
	}-*/;
	
	
}
