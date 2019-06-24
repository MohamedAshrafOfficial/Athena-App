package emad.athena.Tools.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PoppinsLightFont extends android.support.v7.widget.AppCompatButton {
    public PoppinsLightFont(Context context) {
        super(context);
    }

    public PoppinsLightFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "english_light.TTF"));


    }
}