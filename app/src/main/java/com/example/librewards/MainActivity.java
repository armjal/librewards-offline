package com.example.librewards;

import static com.example.librewards.FirstStartHandler.handleFirstStart;
import static java.util.Objects.requireNonNull;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.librewards.models.UserModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private DatabaseHelper myDb;
    private String textToEdit;
    private EditText enterName;
    private Button nameButton;
    private FrameLayout popupNameContainer;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        popupNameContainer = findViewById(R.id.popupNameContainer);
        popupNameContainer.setVisibility(View.INVISIBLE);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageView helpButton = findViewById(R.id.helpButton);

        userModel = new UserModel();
        TimerFragment timerFragment = new TimerFragment();
        RewardsFragment rewardsFragment = new RewardsFragment();


        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);

        handleFirstStart(this, this::onFirstStart);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(timerFragment, "Timer");
        viewPagerAdapter.addFragment(rewardsFragment, "Rewards");
        viewPager.setAdapter(viewPagerAdapter);

        requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.timer);
        requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.reward);
        helpButton.setOnClickListener(v -> showPopup(getString(R.string.helpInfo)));
    }

    public void onFirstStart(){
        showPopupName();
        myDb.addInitialCodes();
    }

    public void showPopupName(){
        popupNameContainer.setVisibility(View.VISIBLE);
        nameButton.setOnClickListener(v -> {
            if(enterName.length() == 0) {
                toastMessage("No name was entered, please try again");
            } else {
                String userName = enterName.getText().toString();
                myDb.addName(userName);
                popupNameContainer.setVisibility(View.INVISIBLE);
                userModel.setName(userName);
                showPopup(getString(R.string.helpInfo));
            }

        });
    }
    //Method that creates a popup
    public void showPopup(String text){
        Dialog popup = new Dialog(this);
        requireNonNull(popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(R.layout.popup_layout);
        ImageView closeBtn = popup.findViewById(R.id.closeBtn);
        TextView popupText = popup.findViewById(R.id.popupText);
        setTextToEdit(text);
        popupText.setText(getTextToEdit());
        closeBtn.setOnClickListener(v -> popup.dismiss());
        popup.show();

    }
    public void setTextToEdit(String textToEdit) {
        this.textToEdit = textToEdit;
    }

    public String getTextToEdit() {
        return textToEdit;
    }

    public void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
