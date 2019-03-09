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
import org.jeff.beepboop.Toolbox.DeviceDimensionsHelper;

public class WalkthroughSlideOne extends Fragment {

    private Bitmap bitmap;

    public WalkthroughSlideOne() {
        // Required empty public constructor
    }

    public static WalkthroughSlideOne newInstance() {
        WalkthroughSlideOne fragment = new WalkthroughSlideOne();
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
        View v = inflater.inflate(R.layout.fragment_walkthrough_slide_one, container, false);
        if (getActivity() != null) {
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
            ImageView imageView = v.findViewById(R.id.slide_one_image);
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.beep_boop_logo);
            imageView.setImageBitmap(BitmapScaler.scaleToFitWidth(bitmap, screenWidth/2));
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600, 600, false));
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
