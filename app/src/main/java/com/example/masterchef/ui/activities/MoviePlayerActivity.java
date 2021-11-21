package com.example.masterchef.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.masterchef.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MoviePlayerActivity extends AppCompatActivity {

    private Uri videoUri;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private ProgressBar progressBar;
    private ImageView btFullScreen;
    private Boolean flag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        playerView = findViewById(R.id.playerView);
        progressBar = findViewById(R.id.progress_bar);
        btFullScreen = findViewById(R.id.bt_fullscreen);

        //make activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //get url
        Intent intent = getIntent();
        if (intent != null){
            String url =   intent.getStringExtra("videoUrl");
            videoUri = Uri.parse(url);
        }


        //init load control
        LoadControl loadControl = new DefaultLoadControl();
        //Initialize band width meter
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //Initialize track selector
        TrackSelector trackSelector = new DefaultTrackSelector(
                new AdaptiveTrackSelection.Factory(bandwidthMeter)
        );
        //Initialize simple exo player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                MoviePlayerActivity.this,trackSelector,loadControl
        );
        //Initialize data source factory
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(
                "exoplayer_video"
        );
        //Initialize extractor factory
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        //Initialize media factory
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,factory,extractorsFactory,null,null);

        //set Player
        playerView.setPlayer(simpleExoPlayer);
        //keep screen on
        playerView.setKeepScreenOn(true);
        //Prepare media
        simpleExoPlayer.prepare(mediaSource);
        //play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.EventListener(){

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //check condition
                if (playbackState == Player.STATE_BUFFERING){
                    //when buffering
                    //show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                }else if (playbackState == Player.STATE_READY){
                    //when ready
                    //hide progress bar
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    //when flag is true
                    //set enter full screen image
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_24));
                    //set portrait orientation
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //set flag value as false
                    flag = false;
                }else {
                    //when flag is false
                    //set exit full screen image
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
                    //set landscape orientation
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //set flag value as true
                    flag = true;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop video when ready
        simpleExoPlayer.setPlayWhenReady(false);
        //get playback state
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        //get playback state
        simpleExoPlayer.getPlaybackState();
    }
}