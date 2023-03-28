package com.hazyaz.easyreel;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowserFragment extends Fragment implements View.OnTouchListener, Handler.Callback  {

    String DownloadImageURL;
    WebViewClient client;
    private final Handler handler = new Handler(this);
    private static final int CLICK_ON_URL = 2;
    private static final int CLICK_ON_WEBVIEW = 1;
    WebView webView;
    String url="https://instagram.com/";
    Button downbtn;
    AlertDialog.Builder builder;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BrowserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowserFragment newInstance(String param1, String param2) {
        BrowserFragment fragment = new BrowserFragment();
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

        View v=inflater.inflate(R.layout.fragment_browser,container,false);

        webView=v.findViewById(R.id.webView);
        webView.setOnTouchListener(this);
        webView.getSettings().setJavaScriptEnabled(true);

        client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };
        webView.setWebViewClient(client);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(url);

        builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Welcome") .setTitle("Alert");
        builder.setMessage("Long press on post that you want to download!!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Alert!");
        alert.show();

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        if(webView != null){
                            if(webView.canGoBack()){
                                webView.goBack();
                            }else {
                                getActivity().onBackPressed();
                            }
                        }
                    }

                }
                return true;
            }
        });

         downbtn=v.findViewById(R.id.downbtn) ;

        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(URLUtil.isValidUrl(DownloadImageURL)){

                    DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                    mRequest.allowScanningByMediaScanner();
                    mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    mRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,""+System.currentTimeMillis()+".jpeg");
                    DownloadManager mDownloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                    mDownloadManager.enqueue(mRequest);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL))
                            .setDescription("Downloading")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);

                    long downloadId = mDownloadManager.enqueue(request);

                    Toast.makeText(getActivity(),"Image Downloaded Successfully...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Sorry.. Something Went Wrong...", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;

    }


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == CLICK_ON_URL) {
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW) {
            if(DownloadImageURL!=null)
            {
                if(DownloadImageURL.contains(".2885-15")) {
                    Toast.makeText(getActivity(), "Link copied Successfully", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.webView && event.getAction() == MotionEvent.ACTION_DOWN) {
            DownloadImageURL = webView.getHitTestResult().getExtra();
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
            if(DownloadImageURL!=null)
            {
                Toast.makeText(getActivity(), "Link copied Successfully", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }
}