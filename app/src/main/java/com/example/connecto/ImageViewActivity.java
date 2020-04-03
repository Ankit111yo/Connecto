package com.example.connecto;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class ImageViewActivity extends AppCompatActivity {
    int f=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent=getIntent();
        String url= Objects.requireNonNull(intent.getExtras()).getString("url");
        ImageView imageView=findViewById(R.id.image);
        final ImageView back=findViewById(R.id.backButton);

        RelativeLayout relativeLayout=findViewById(R.id.relativeLayout);
        back.setEnabled(false);

        Uri uri = Uri.parse(url);
        Glide.with(ImageViewActivity.this).load(uri).into(imageView);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(f==0)
                {
                    f=1;
                    back.setVisibility(View.VISIBLE);
                    back.setEnabled(true);
                }
                else
                {
                    f=0;
                    back.setVisibility(View.INVISIBLE);
                    back.setEnabled(false);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewActivity.this.finish();
            }
        });
    }
}
