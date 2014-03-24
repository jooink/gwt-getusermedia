package com.jooink.experiments.getusermedia.client;

import com.jooink.experiments.getusermedia.client.Utils.UserMediaCallback;
import com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback;



//keeps the url of the usermedia to avoid unecessary 'getUserMedia' requests
//on ff if you remove the video holding the stream from the dom the video is no more available !!!
//but the camera still is on

public class UserMediaHolderImpl implements UserMediaHolder {
	private String userMediaUrl;
	private UserMediaStream userMediaStream;

	
	public UserMediaHolderImpl() {
		//Window.alert("constructing");
	}

	public boolean getUserMedia(final UserMediaCallback userMediaCallback) {

		releaseUserMedia(); //we call id to cleanup ...

		boolean hasUserMedia = Utils.getUserVideoStream( new UserMediaStreamCallback() {
			@Override
			public void onSuccess(UserMediaStream stream) {
				userMediaStream = stream;
				userMediaUrl = Utils.createUrl(userMediaStream);
				
				
				userMediaCallback.onSuccess(userMediaUrl);
			}

			@Override
			public void onFail() {
				userMediaCallback.onFail();
			}
		});
		return hasUserMedia;

	}

	@Override
	public void releaseUserMedia() {
		
		//Window.alert("url: " + userMediaUrl);
		if(userMediaUrl != null)
			Utils.revokeUrl(userMediaUrl);

		//Window.alert("st: " + userMediaStream);
		if(userMediaStream != null)
			userMediaStream.stop();
	}




}
