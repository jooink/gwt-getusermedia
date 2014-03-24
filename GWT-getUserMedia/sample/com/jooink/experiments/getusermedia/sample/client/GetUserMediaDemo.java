package com.jooink.experiments.getusermedia.sample.client;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadedMetadataEvent;
import com.google.gwt.event.dom.client.LoadedMetadataHandler;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.jooink.experiments.getusermedia.client.UserMediaHolder;
import com.jooink.experiments.getusermedia.client.UserMediaHolderImpl;
import com.jooink.experiments.getusermedia.client.Utils.UserMediaCallback;


/**
 * trivial sample of video/canvas & getUSerMedia
 */
public class GetUserMediaDemo implements EntryPoint {




	//caching a canvas
	private Canvas canvas = Canvas.createIfSupported();

	private ImagesStrip images ;

	private static final int tiles_w = 160;
	private static final int tiles_h = 120;

	private UserMediaHolder userMediaHolder = GWT.create(UserMediaHolderImpl.class);


	@Override
	public void onModuleLoad() {

		






		int h = Window.getClientHeight();
		int w = Window.getClientWidth();
//
		int Nx = w/160;
		int Ny = h/120;

		images = new ImagesStrip(Nx*Ny, tiles_w + "px", tiles_h + "px");
	
	
	
		RootLayoutPanel.get().add(images);


		Button cam = new Button("Capture My Cam");
		cam.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!userMediaHolder.getUserMedia(userMediaCallback)) 
					Window.alert("FAIL: your browser \""+ Window.Navigator.getUserAgent()+"\" does not support WebRTC's getUserMedia :(");
			}
		});

		PopupPanel pp = new PopupPanel(false, true);
		pp.add(cam);
		pp.center();
		pp.show();
//



	}




	private  String makePicture(Video video, Canvas canvas) {
		
		int w = video.getVideoWidth();
		int h = video.getVideoHeight();
		canvas.setWidth(  w + "px");
		canvas.setHeight( h + "px");
		
		canvas.getCanvasElement().setWidth(w);
		canvas.getCanvasElement().setHeight(h);
		
		Context2d ctx = canvas.getContext2d();
		drawVideo(ctx, video.getVideoElement(), 0, 0, video.getVideoWidth(), video.getVideoHeight(), 0, 0, video.getVideoWidth(), video.getVideoHeight());
		
		
		return canvas.getCanvasElement().toDataUrl("image/png");
		
	}



	private final native void drawVideo(Context2d ctx, VideoElement video, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh) /*-{
		ctx.drawImage(video, sx,sy,sw,sh,dx, dy,dw,dh);
	}-*/;
	private final native void drawVideo(Context2d ctx, VideoElement video,double dx, double dy, double dw, double dh) /*-{
		ctx.drawImage(video,dx, dy,dw,dh);
	}-*/;

	private static final native void nativelyAddListner(VideoElement videoElement) /*-{
		alert('aggiungo a' + videoElement);
		videoElement.addEventListener('loadedmetadata', function() { alert('ecchilo');});
		videoElement.addEventListener('progress', function() { alert('progress');}, false);
	}-*/;


	private  final UserMediaCallback userMediaCallback = new UserMediaCallback()  {

		public void onSuccess(String s) {	

			final PopupPanel popupPanel = new PopupPanel(false,true);
			final PopupPanel waiting_popupPanel = new PopupPanel(false,true);

			final Video video = Video.createIfSupported();

			video.setSize("640px", "480px"); //just to keep the popup at a decent size


			
			waiting_popupPanel.setWidget(new HTML("Waiting for video to become available"));
			
			
			
			//waiting_popupPanel.center();
			//waiting_popupPanel.show();
			
			final VerticalPanel vp = new VerticalPanel();
			vp.add(video);
			final HorizontalPanel hp = new HorizontalPanel();
			Anchor click = new Anchor("make a picture");
			click.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					String data = makePicture(video, canvas);
					
					
					
					Anchor done = new Anchor("done");
					done.getElement().getStyle().setColor("red");
					done.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							popupPanel.hide();
						}
					});
					
					vp.clear();
					vp.add(new Image(data));							
					vp.add(hp);
					hp.clear();
					hp.add(done);
					hp.setCellHorizontalAlignment(done, HasHorizontalAlignment.ALIGN_CENTER);
				}
			});

			
			Anchor close = new Anchor("close");
			close.addClickHandler(new ClickHandler() {	
				@Override
				public void onClick(ClickEvent event) {
					popupPanel.hide();
					userMediaHolder.releaseUserMedia();
				}
			});

			Anchor timely_circular = new Anchor("stream (circular)");
			timely_circular.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					final Timer timer = new Timer() {
						@Override
						public void run() {
							String data = makePicture(video,canvas);
							images.push_circular(data);
						}
					};

					Anchor stop = new Anchor("stop");
					stop.getElement().getStyle().setColor("red");
					stop.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							timer.cancel();
							popupPanel.hide();
						}
					});

					hp.clear();
					hp.add(stop);
					hp.setCellHorizontalAlignment(stop, HasHorizontalAlignment.ALIGN_CENTER);
					video.getVideoElement().setWidth(tiles_w);
					video.getVideoElement().setHeight(tiles_h);
					video.setSize(tiles_w + "px", tiles_h + "px");
					popupPanel.center();
					String data = makePicture(video,canvas);
					images.push_circular(data);
					timer.scheduleRepeating(250);
				}
			});

			Anchor timely_moving = new Anchor("stream (moving)");
			timely_moving.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					final Timer timer = new Timer() {
						@Override
						public void run() {
							String data = makePicture(video,canvas);
							images.push_shift(data);
						}
					};

					Anchor stop = new Anchor("stop");
					stop.getElement().getStyle().setColor("red");
					stop.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							timer.cancel();
							popupPanel.hide();
						}
					});

					hp.clear();
					hp.add(stop);
					hp.setCellHorizontalAlignment(stop, HasHorizontalAlignment.ALIGN_CENTER);
					video.getVideoElement().setWidth(tiles_w);
					video.getVideoElement().setHeight(tiles_h);
					video.setSize(tiles_w + "px", tiles_h + "px");
					popupPanel.center();
					String data = makePicture(video,canvas);
					images.push_shift(data);
					timer.scheduleRepeating(250);
				}
			});


			hp.setWidth("100%");

			hp.add(click);
			hp.setCellHorizontalAlignment(click, HasHorizontalAlignment.ALIGN_LEFT);
			hp.setCellWidth(click, "25%");

			hp.add(timely_circular);
			hp.setCellHorizontalAlignment(timely_circular, HasHorizontalAlignment.ALIGN_CENTER);
			hp.setCellWidth(timely_circular, "25%");

			hp.add(timely_moving);
			hp.setCellHorizontalAlignment(timely_moving, HasHorizontalAlignment.ALIGN_CENTER);
			hp.setCellWidth(timely_moving, "25%");

			hp.add(close);
			hp.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_RIGHT);
			hp.setCellWidth(close, "25%");

			vp.add(hp);

			popupPanel.setWidget(vp);
			popupPanel.center();					
			popupPanel.show();

			//nativelyAddListner(video.getVideoElement());
			//Window.alert("This demo dose not works until LoadedMetadata is MERGED");	
			video.addLoadedMetadataHandler(new LoadedMetadataHandler() {
				
				@Override
				public void onLoadedMetadata(LoadedMetadataEvent event) {
					//Window.alert("Metadata Loaded !!!");
					//REQUIRED FOR CHROME !!!
					video.play();
					Scheduler.get().scheduleEntry(new RepeatingCommand() {
						
						@Override
						public boolean execute() {
							int w = video.getVideoElement().getVideoWidth();
							int h = video.getVideoElement().getVideoHeight();
							if(w*h != 0) { 
								initUiWhenVideoHAsMetadata();							
								return false;	
							} 
							System.err.println("deferring");
							return true;
						}
					});
					
				}
				
				private void initUiWhenVideoHAsMetadata() {
					
					
					int w = video.getVideoElement().getVideoWidth();
					int h = video.getVideoElement().getVideoHeight();

					

					video.setSize(w+"px", h+"px");
					
					popupPanel.center();
					waiting_popupPanel.hide();
					Window.alert("video size detected: " + w+"px X "+  h+"px");
					
					
				}
			});
			
			
			
			video.getVideoElement().setSrc(s);
			video.getVideoElement().play();


		}



		public void onFail() {
			Window.alert("FAIL: please let me access your camera");
		}
	};


	
	
	
	

	public class ImagesStrip implements IsWidget {
		private FlowPanel panel = new FlowPanel();
		private ArrayList<Image> images = new ArrayList<Image>();
		@Override
		public Widget asWidget() {
			return panel;
		}

		private int current = 0;
		private int len;
		private String width;
		private String height;
		public ImagesStrip(int len, String w, String h) {
			this.len = len;
			this.width = w;
			this.height = h;
		}


		public void push_circular(String url) {
			int pos = current%len;

			if(images.size() <= pos) {
				Image img = new Image();
				img.setSize(width, height);
				panel.add(img);
				images.add(img);
			}
			images.get(pos).setUrl(url);
			current++;
		}

		public void push_shift(String url) {

			int pos = current%len;

			if(images.size() <= pos) {
				Image img = new Image();
				img.setSize(width, height);
				panel.add(img);
				images.add(img);
			} 
			if(current > len) {
				Image img = images.get(pos);
				panel.remove(img);
				panel.add(img);
			}
			images.get(pos).setUrl(url);


			current++;
		}


	}


}
