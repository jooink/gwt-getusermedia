package com.jooink.experiments.getusermedia.client;

import com.jooink.experiments.getusermedia.client.Utils.UserMediaCallback;


public interface UserMediaHolder {
	public boolean getUserMedia(UserMediaCallback userMediaCallback);

	//public boolean hasUserMedia(); impossible to implement on ff

	public void releaseUserMedia();
}
