package com.github.deckyfx.simpleadapter;

import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AdapterUtil {
    public static final String LOG_TAG                              = "SimpleAdapter";

    public static final class DEFAULT_LIST_VIEW {
        public static final int SIMPLE_LIST_ITEM_1                  = android.R.layout.simple_list_item_1;
        public static final int SIMPLE_LIST_ITEM_2                  = android.R.layout.simple_list_item_2;
        public static final int SIMPLE_SPINER_ITEM                  = android.R.layout.simple_spinner_item;
        public static final int SIMPLE_DROPDOWN_ITEM_1LINE          = android.R.layout.simple_dropdown_item_1line;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_1       = android.R.layout.simple_expandable_list_item_1;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_2       = android.R.layout.simple_expandable_list_item_2;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_1        = android.R.layout.simple_list_item_activated_1;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_2        = android.R.layout.simple_list_item_activated_2;
        public static final int SIMPLE_LIST_ITEM_CHECKED            = android.R.layout.simple_list_item_checked;
        public static final int SIMPLE_LIST_ITEM_MULTIPLE_CHOICE    = android.R.layout.simple_list_item_multiple_choice;
        public static final int SIMPLE_LIST_ITEM_SINGLE_CHOICE      = android.R.layout.simple_list_item_single_choice;
        public static final int SIMPLE_SELECTABLE_LIST_ITEM         = android.R.layout.simple_selectable_list_item;
        public static final int SIMPLE_SPINNER_DROPDOWN_ITEM        = android.R.layout.simple_spinner_dropdown_item;
    }

    public static AbstractViewHolder createViewHolderInstance(Class<? extends AbstractViewHolder> klas, View itemView) {
        try {
            Constructor<? extends AbstractViewHolder> ctor  = klas.getDeclaredConstructor(View.class);
            // force it to public
            ctor.setAccessible(true);
            return ctor.newInstance(itemView);
        } catch (NoSuchMethodException x) {
            Log.e(LOG_TAG,"Default Constructor(android.widget.View) not found for " + klas.getCanonicalName());
            Log.e(LOG_TAG,"if ViewHolder is an inner class try to declare it as static");
            Log.e(LOG_TAG, Log.getStackTraceString(x));
        } catch (InstantiationException x) {
            Log.e(LOG_TAG, Log.getStackTraceString(x));
        } catch (InvocationTargetException x) {
            Log.e(LOG_TAG, Log.getStackTraceString(x));
        } catch (IllegalAccessException x) {
            Log.e(LOG_TAG,"IllegalAccessException for " + klas.getCanonicalName() + ",\n try to make the class public");
            Log.e(LOG_TAG, Log.getStackTraceString(x));
        }
        Log.e(LOG_TAG,"Failed to initiate View Holder " + klas.getCanonicalName());
        return null;
    }

    public AnimationSet createDefaultScrollAnimation() {
        AnimationSet scrollAnimation = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(800);
        scrollAnimation.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(600);
        scrollAnimation.addAnimation(animation);
        return scrollAnimation;
    }
}
