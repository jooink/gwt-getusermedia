package com.jooink.experiments.getusermedia.client;

import com.jooink.experiments.getusermedia.client.Utils.UserMediaCallback;
import com.jooink.experiments.getusermedia.client.Utils.UserMediaStreamCallback;



//keeps the url of the usermedia to avoid unecessary 'getUserMedia' requests

public class UserMediaHolderChrome implements UserMediaHolder {
	private boolean userMediaAvailable = false;
	private String userMediaUrl;
	private UserMediaStream userMediaStream;
	
	
	public boolean getUserMedia(final UserMediaCallback userMediaCallback) {
		
		if(!userMediaAvailable) {
			boolean hasUserMedia = Utils.getUserVideoStream( new UserMediaStreamCallback() {
				
				@Override
				public void onSuccess(UserMediaStream stream) {
					userMediaAvailable = true;
					userMediaStream = stream;
					userMediaUrl = Utils.createUrl(userMediaStream);
					userMediaCallback.onSuccess(userMediaUrl);
				}
				
				@Override
				public void onFail() {
					userMediaAvailable = false;
					userMediaCallback.onFail();
				}
			});
			return hasUserMedia;
		} else {
			userMediaCallback.onSuccess(userMediaUrl);
			return true;
		}
		
	}

	@Override
	public void releaseUserMedia() {
		userMediaAvailable = false;
		if(userMediaUrl != null)
			Utils.revokeUrl(userMediaUrl);
		if(userMediaStream != null)
			userMediaStream.stop();
	}

	public boolean hasUserMedia() {
		return userMediaAvailable;
	}


}
