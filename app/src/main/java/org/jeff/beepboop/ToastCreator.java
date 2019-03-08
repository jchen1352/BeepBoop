package org.jeff.beepboop;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ToastCreator {

    Toast toast;

    ToastCreator(Context context, String text, int length) {
        toast = Toast.makeText(context, text, length);
        View view = toast.getView();
        view.setBackground(context.getDrawable(R.drawable.box1));
        toast.setView(view);
    }

    public void show() {
        toast.show();
    }

    public void cancel() {
        toast.cancel();
    }
}
