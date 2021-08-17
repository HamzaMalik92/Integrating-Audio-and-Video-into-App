package com.example.ingetratingaudiovideoinandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener {

  Button playBtn, stopMusicBtn, playMusicBtn;
  VideoView videoView;
  MediaController mediaController;
  MediaPlayer mediaPlayer;
  SeekBar volumeSeekBar, backAndForthSeekBar;
  AudioManager audioManager;
  Timer timer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    playBtn = findViewById(R.id.playBtn);
    playMusicBtn = findViewById(R.id.playMusicBtn);
    stopMusicBtn = findViewById(R.id.pauseMusicBtn);
    videoView = findViewById(R.id.videoView);
    volumeSeekBar = findViewById(R.id.volumeSeekBar);
    backAndForthSeekBar = findViewById(R.id.backAndForthSeekBar);
    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.rainsound);
    mediaPlayer.setOnCompletionListener(MainActivity.this);
    audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    int currentVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    volumeSeekBar.setMax(maxVolume);
    //    volumeSeekBar.setMin(minVolume);
    volumeSeekBar.setProgress(currentVolume);

    volumeSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    backAndForthSeekBar.setOnSeekBarChangeListener(MainActivity.this);
    backAndForthSeekBar.setMax(mediaPlayer.getDuration());

    playBtn.setOnClickListener(MainActivity.this);
    playMusicBtn.setOnClickListener(MainActivity.this);
    stopMusicBtn.setOnClickListener(MainActivity.this);
    mediaController = new MediaController(MainActivity.this);
  }

  @Override
  public void onClick(View view) {

    switch (view.getId()) {
      case R.id.playBtn:
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rcetwater);

        videoView.setVideoURI(videoUri);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.start();

        break;

      case R.id.playMusicBtn:
        mediaPlayer.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(
            new TimerTask() { // timerTask will execute in a new thread
              @Override
              public void run() {
                backAndForthSeekBar.setProgress(mediaPlayer.getCurrentPosition());
              }
            },
            0,
            1000); // delay = 0 mean start as soon as btn is clicked
        // and execute task over period of 1 sec ( after 1 sec )
        break;

      case R.id.pauseMusicBtn:
        mediaPlayer.pause();

        timer.cancel();

        break;
    }
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    mediaPlayer.seekTo(i); // update music playing position
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    mediaPlayer.pause();
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    mediaPlayer.start();
  }

  @Override
  public void onCompletion(MediaPlayer mediaPlayer) { // execute when music ends
    timer.cancel();
    Toast.makeText(this, "Music Ended", Toast.LENGTH_SHORT).show();
  }
}
