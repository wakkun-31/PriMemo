package com.wata.primemo.model;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class CustomDragEditText extends EditText {

    public CustomDragEditText(Context context) {
        super(context);
    }

    public CustomDragEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public CustomDragEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        switch(event.getAction()){
            case DragEvent.ACTION_DRAG_STARTED:
                // クリップデータがnullの場合は、EditText(元はTextView)自体のドラッグイベントは無視する
                // クリップデータがMIMETYPE_TEXT_PLAIN = "text/plain"であれば、商品カゴからの
                // ドラックと判断して無視する
                if(event.getClipDescription() != null){
                    if(event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                       return false;
                    }
                    return  true;
                }
                return false;
            default:
                return super.onDragEvent(event);

        }
    }
}
