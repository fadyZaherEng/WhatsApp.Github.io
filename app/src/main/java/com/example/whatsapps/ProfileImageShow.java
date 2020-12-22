package com.example.whatsapps;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileImageShow extends AppCompatActivity {

    @BindView(R.id.profile_PictureWeb)
    WebView profile_PictureWeb;
    @BindView(R.id.profile_PictureImage)
    ImageView profile_PictureImage;
    @BindView(R.id.imageToolBar)
    Toolbar imageToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_show);
        ButterKnife.bind(this);

        profile_PictureWeb.getSettings().setUseWideViewPort(true);
        profile_PictureWeb.getSettings().setDisplayZoomControls(false);
        profile_PictureWeb.getSettings().setBuiltInZoomControls(true);
        profile_PictureWeb.setInitialScale(1);


        setSupportActionBar(imageToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        if (getIntent().getStringExtra("image")!=null) {
            profile_PictureWeb.loadUrl(getIntent().getStringExtra("image"));
        }
        else {
            Picasso.get().load(getIntent().getStringExtra("image")).placeholder(R.drawable.ic_profile).into(profile_PictureImage);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

}