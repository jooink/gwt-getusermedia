package com.jooink.experiments.getusermedia.sample.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.jooink.experiments.getusermedia.client.Utils;
import com.jooink.experiments.getusermedia.client.Utils.UserMediaCallback;

/**
 * trivial sample of video/canvas & getUSerMedia
 */
public class GetUserMediaMinimal implements EntryPoint {


	@Override
	public void onModuleLoad() {

		boolean res = Utils.getUserVideo(new UserMediaCallback() {
			@Override
			public void onSuccess(String url) {
				Video video = Video.createIfSupported();
				video.getVideoElement().setSrc(url);
				video.getVideoElement().play();
				RootPanel.get().add(video);	
			}			
			@Override
			public void onFail() {
				Window.alert("FAIL: please let me access your camera");
			}
		});

		if(!res)
			Window.alert("FAIL: your browser \""+ Window.Navigator.getUserAgent()+"\" does not support WebRTC's getUserMedia :(");
		
	}

}
