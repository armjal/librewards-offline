package com.example.librewards.views;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;

public abstract class FragmentExtended extends Fragment {
    public abstract String getTitle();

    public abstract int getIcon();

    public Parcelable getParcelable(String key){
        Parcelable parcelable = this.requireArguments().getParcelable(key);

        if (parcelable == null){
            throw new IllegalStateException(String.format("Parcelable '%1$s' not found in arguments", key));
        }
        return parcelable;
    }
}
