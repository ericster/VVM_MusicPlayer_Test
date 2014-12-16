package com.androidhive.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class VVM_MusicPlayer_Test extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	final static String TAG = "VVM_MusicPlayer";
	final Context context = this;
	private ImageButton btnPlay;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;

	// Media Player
	private  MediaPlayer mp;
	private boolean mInitialized = false;
	private long mPosOverride = -1L;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private Utilities utils;
    private String filePath = "/storage/emulated/0/hello.amr";

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "************ onCreate() **************");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		

		// Media Player
		mp = new MediaPlayer();
		utils = new Utilities();
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		
		// Getting all songs list
//		songsList = songManager.getPlayList();
		
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(!mInitialized){
					    Log.v(TAG, " MediaPlayer is started first time" );
						playVoiceMail2();
					    mInitialized = true;
				}
				else{
					Log.v(TAG, " MediaPlayer is clicked");
				    if(mp.isPlaying()){
						Log.v(TAG, " pause if media is playing ");
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
				    }else{
					// Resume song
						Log.v(TAG, " resumed with an existing MediaPlayer");
						mp.start();
						updateProgressBar();

						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
						
				   }
				}
			}
		});
		
	}
	
	
	public void  playVoiceMail(){
		// AudioManager
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// Play song
		try {
        	mp.reset();
        	mp.setAudioStreamType(am.STREAM_VOICE_CALL);
        	/*
        	 * TODO: to use API
        	 */

/*        	File file = new File(filePath);
        	FileInputStream inputStream = new FileInputStream(file);
        	mediaPlayer.setDataSource(inputStream.getFD());
        	inputStream.close();
        	*/

			mp.setDataSource(filePath);
			mp.prepare();
			mp.start();
			
			am.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			// Displaying Song title
			String voiceMailTitle = "Voicemail testing";
			String songTitle = voiceMailTitle;
        	songTitleLabel.setText(songTitle);
			
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private void initMediaPlayer() {           
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mp.reset();
            mp.setAudioStreamType(am.STREAM_VOICE_CALL);
            try {
				mp.setDataSource(filePath);
				mp.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            mp.start();
    }

	public void  playVoiceMail2(){
		// AudioManager
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// Play song
        	/*
        	 * TODO: to use API
        	 */
        	String filePath = "/storage/emulated/0/hello.amr";

/*        	File file = new File(filePath);
        	FileInputStream inputStream = new FileInputStream(file);
        	mediaPlayer.setDataSource(inputStream.getFD());
        	inputStream.close();
        	*/
        	
            OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
                private  MediaPlayer mMediaPlayer = mp;
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {        
                    case AudioManager.AUDIOFOCUS_GAIN:            // resume playback           
                        Log.v(TAG, " == AUDIOFOCUS_GAIN == ");
                        if (mMediaPlayer == null) {
                            Toast.makeText(context, "Playback from scratch by focuss gain ", Toast.LENGTH_SHORT).show();
                            initMediaPlayer();            
                        }
                        else if (!mMediaPlayer.isPlaying()) {
                            Toast.makeText(context, "Playback resume by focuss gain ", Toast.LENGTH_SHORT).show();
                            mMediaPlayer.start();            
                        }
                        //mMediaPlayer.setVolume(1.0f, 1.0f);            
                        break;        
                    case AudioManager.AUDIOFOCUS_LOSS:            // Lost focus for an unbounded amount of time: stop playback and release media player            
                        Log.v(TAG, " == AUDIOFOCUS_LOSS == ");
                        if (mMediaPlayer.isPlaying()) {
                            Toast.makeText(context, "Playback interrupted by focus loss", Toast.LENGTH_SHORT).show();

//                            mMediaPlayer.stop();     
                            mMediaPlayer.pause();      
						    btnPlay.setImageResource(R.drawable.btn_play);
                        }
//                        mMediaPlayer.release();            
//                        mMediaPlayer = null;            
                        break;        
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:            // Lost focus for a short time, but we have to stop           
                        Log.v(TAG, " == AUDIOFOCUS_LOSS_TRANSIENT == ");
                        // playback. We don't release the media player because playback            
                        // is likely to resume            
                        if (mMediaPlayer.isPlaying()) {
                            Toast.makeText(context, "Playback paused by focus loss (transient)", Toast.LENGTH_SHORT).show();
                            mMediaPlayer.pause();      
                        }
                        break;        
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:            // Lost focus for a short time, but it's ok to keep playing            
                        Log.v(TAG, " == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK == ");
                        // at an attenuated level            
                        if (mMediaPlayer.isPlaying())  {
                            Toast.makeText(context, "Playback paused by focus loss (duck)", Toast.LENGTH_SHORT).show();
                            mMediaPlayer.pause();            
                            //mMediaPlayer.setVolume(0.1f, 0.1f); 
                        }
                        break;    
                    }   
                }
            };

            // Request audio focus for playback
            int result = am.requestAudioFocus(afChangeListener,
                                             // Use the music stream.
                                             AudioManager.STREAM_VOICE_CALL,
                                             // Request permanent focus.
                                             AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                                             //AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // Start playback.
                try {
                    mp.reset();
                    mp.setAudioStreamType(am.STREAM_VOICE_CALL);
                    //mp.setAudioStreamType(am.STREAM_MUSIC);
                    mp.setDataSource(filePath);
                    mp.prepare();
                    mp.start();

                    // Displaying Song title
                    String voiceMailTitle = "Voicemail testing";
                    String songTitle = voiceMailTitle;
                    songTitleLabel.setText(songTitle);

                    // Changing Button Image to pause image
                    btnPlay.setImageResource(R.drawable.btn_pause);

                    // set Progress bar values
                    songProgressBar.setProgress(0);
                    songProgressBar.setMax(100);

                    // Updating progress bar
                    updateProgressBar();
                    //am.abandonAudioFocus(afChangeListener);

                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// check for repeat is ON or OFF
//		if(isRepeat){
//			// repeat is on play same song again
//			playSong(currentSongIndex);
//		} else if(isShuffle){
//			// shuffle is on - play a random song
//			Random rand = new Random();
//			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
//			playSong(currentSongIndex);
//		} else{
//			// no repeat or shuffle ON - play next song
//			if(currentSongIndex < (songsList.size() - 1)){
//				playSong(currentSongIndex + 1);
//				currentSongIndex = currentSongIndex + 1;
//			}else{
//				// play first song
//				playSong(0);
//				currentSongIndex = 0;
//			}
//		}
	}
	
	@Override
	protected void onPause() {
		Log.v(TAG, "************ onPause() **************");
		// TODO Auto-generated method stub
		super.onPause();
		mp.pause();
		btnPlay.setImageResource(R.drawable.btn_play);
//	    mp.release();
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "************ onStop() **************");
		// TODO Auto-generated method stub
		super.onStop();
		// remove updateTask for other music player
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	protected void onStart() {
		Log.v(TAG, "************ onStart() **************");
		// TODO Auto-generated method stub
		super.onStart();
//		updateProgressBar();
	}
	
	@Override
	protected void onResume() {
		Log.v(TAG, "************ onResume() **************");
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	 public void onDestroy(){
		Log.v(TAG, "************ onDestroy() **************");
	 super.onDestroy();
	    mp.release();
	 }
	
}