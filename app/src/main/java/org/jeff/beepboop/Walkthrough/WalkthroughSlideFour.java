package org.jeff.beepboop.Walkthrough;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jeff.beepboop.R;
import org.jeff.beepboop.Toolbox.BitmapScaler;

import java.util.BitSet;

public class WalkthroughSlideFour extends Fragment {

    private Bitmap bitmap;

    public WalkthroughSlideFour() {
        // Required empty public constructor
    }

    public static WalkthroughSlideFour newInstance() {
        WalkthroughSlideFour fragment = new WalkthroughSlideFour();
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
        View v = inflater.inflate(R.layout.fragment_walkthrough_slide_four, container, false);
        if (getActivity() != null) {
            ImageView imageView = v.findViewById(R.id.slide_four_image);
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slide_three);
            //imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1200, 300, false));
            imageView.setImageBitmap(BitmapScaler.scaleToFitHeight(bitmap, 400));
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
