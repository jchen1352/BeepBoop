package org.jeff.beepboop.Walkthrough;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jeff.beepboop.R;


public class WalkthroughSlideTwo extends Fragment {

    private Bitmap bitmap;

    public WalkthroughSlideTwo() {
        // Required empty public constructor
    }

    public static WalkthroughSlideTwo newInstance() {
        WalkthroughSlideTwo fragment = new WalkthroughSlideTwo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_walkthrough_slide_two, container, false);
        if (getActivity() != null) {
            ImageView imageView = v.findViewById(R.id.slide_two_image);
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slide_one);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1200, 350, false));
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDetach();
    }
}
