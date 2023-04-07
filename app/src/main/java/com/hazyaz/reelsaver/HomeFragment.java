package com.hazyaz.reelsaver;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    String URL = "NULL";
    ImageView mparticularphoto;
    EditText getphotolink;
    Button getphoto;
    Button downloadphoto;
    String photourl="1";
    private Uri uri2;
    int cnt=0;
    private InterstitialAd mInterstitialAd;

    VideoView mparticularreel;
    private MediaController mediaController;
    String reelurl="1";

    ImageView emptyImage;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressBar spinner;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_home,null);

        getphoto =v.findViewById(R.id.downloadbtn);
        getphotolink =v.findViewById(R.id.editText);
       // downloadphoto =v.findViewById(R.id.downloadphoto);
        mparticularphoto =v.findViewById(R.id.particularphoto);
        emptyImage = v.findViewById(R.id.emptyscreen);

        emptyImage.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://hazyaztechnologies.in/contact/")));
        });


        //reels
        emptyImage.setVisibility(View.VISIBLE);
        mparticularreel =v.findViewById(R.id.particularreel);
        mediaController=new MediaController(getContext());
        mediaController.setAnchorView(mparticularreel);


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        getphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(getContext(),"ca-app-pub-2675887677224394/2129805825", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;
                                mInterstitialAd.show(getActivity());
                               Log.i("reelads", "onAdLoaded");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.d("reelads", loadAdError.toString());
                                mInterstitialAd = null;
                            }
                        });

                URL=getphotolink.getText().toString().trim();
                if(URL.equals(""))
                {
                    Toast.makeText(getContext(),"Enter URL",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(URL.contains("/p/")) {
                        mparticularreel.setVisibility(View.INVISIBLE);
                        mparticularphoto.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.INVISIBLE);

                        cnt=0;
                    }
                    else{
                        mparticularphoto.setVisibility(View.INVISIBLE);
                        mparticularreel.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.INVISIBLE);


                        cnt=1;
                    }
                    String result2= StringUtils.substringBefore(URL,"/?");
                    URL=result2+"/?__a=1&__d=dis";
                    processdata();
                }
            }
        });

        return v;
    }

    private void processdata() {
        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                MainURL mainURL = gson.fromJson(response, MainURL.class);
                if(cnt==0) {
                    photourl = mainURL.getGraphql().getShortcode_media().getDisplay_url();
                    //For loading Image
                    uri2 = Uri.parse(photourl);
                    Glide.with(getContext()).load(uri2).into(mparticularphoto);
                }
                else if(cnt==1)
                {
                    photourl = mainURL.getGraphql().getShortcode_media().getVideo_url();
                    uri2 = Uri.parse(photourl);
                    mparticularreel.setMediaController(mediaController);
                    mparticularreel.setVideoURI(uri2);
                    mparticularreel.requestFocus();
                    mparticularreel.start();
                }


                if(!photourl.equals("1"))
                {
                    DownloadManager.Request request=new DownloadManager.Request(uri2);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    //request.setDescription("...");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    if(cnt==1) {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "" + System.currentTimeMillis() + ".mp4");
                    }
                    else if(cnt==0){
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "" + System.currentTimeMillis() + ".jpg");
                    }
                    DownloadManager manager=(DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(getContext(),"Downloaded",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(),"No Photo to download!",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Not able to fetch!!", Toast.LENGTH_SHORT);
            }
        });
        RequestQueue queue= Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}