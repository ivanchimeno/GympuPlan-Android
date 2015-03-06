package com.ivanchimeno.gympuplan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ivanchimeno.gympuplan.lib.GympuWrapper;


public class ScheduleViewerFragment extends android.support.v4.app.Fragment
{
    // Images are displayed inside a WebView control and not
    // an ImageView in order to take advantage of the pinch
    // zoom functionality.
    private WebView mWebView;

    private ProgressDialog mProgressDialog;

    // Holds the bitmap url to download and display.
    private String[] bitmapUrls;

    public ScheduleViewerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule_viewer, container, false);

        // Get the WebView.
        mWebView = (WebView)rootView.findViewById(R.id.webView);

        // Start the progress dialog before loading data.
        mProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.please_wait), getResources().getString(R.string.downloading_schedule));

        // Enable pinch zooming on the WebView and disable the visibility
        // of the zoom controls.
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.setWebViewClient(new WebViewClient()
        {
            // load url
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }

            // when finish loading page
            public void onPageFinished(WebView view, String url)
            {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });

        // Generate the html code and load it inside the WebView.
        String html = GympuWrapper.Instance().GenerateHTML(bitmapUrls);
        mWebView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

        return rootView;
    }

    public void setBitmapUrls(String[] bitmapUrls)
    {
        this.bitmapUrls = bitmapUrls;
    }
}
