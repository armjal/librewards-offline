/*Author: Arman Jalilian
Date of Completion: 07/06/2020
Module Code: CSC3122
Application Name: Lib Rewards
Application Purpose: Rewards students as they spend time at the library
Class Name: MainActivity
Class Purpose: The main activity is where the fragments are called within. It is also where the navigation is called and set from.
 */
package com.example.librewards;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimerFragment.TimerListener, RewardsFragment.RewardsListener{
    private static final String FIRST_START_PREFS_BOOL = "firstStart";
    private static final String LIBREWARDS_PREFS = "librewards_prefs";
    DatabaseHelper myDb;
    Dialog popup;
    private TimerFragment timerFragment;
    private RewardsFragment rewardsFragment;
    private String textToEdit;
    private EditText enterName;
    private Button nameButton;
    private FrameLayout popupNameContainer;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageView helpButton = findViewById(R.id.helpButton);

        sharedPreferences = this.getSharedPreferences(LIBREWARDS_PREFS, Context.MODE_PRIVATE);

        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);
        popupNameContainer = findViewById(R.id.popupNameContainer);
        popupNameContainer.setVisibility(View.INVISIBLE);

        timerFragment = new TimerFragment();
        rewardsFragment = new RewardsFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(timerFragment, "Timer");
        viewPagerAdapter.addFragment(rewardsFragment, "Rewards");
        viewPager.setAdapter(viewPagerAdapter);

        requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.timer);
        requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.reward);
        onFirstStartShowPopup();
        //Help button on standby in case a user required information about the application
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(getString(R.string.helpInfo));
            }
        });
    }

    private void onFirstStartShowPopup(){
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START_PREFS_BOOL, true);
        if (firstStart) {
            showPopupName();

        }
    }
    //Custom popup that asks for the users name on first start-up
    public void showPopupName(){
        popupNameContainer.setVisibility(View.VISIBLE);
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterName.length() != 0) {
                    //Adds the name given to the database
                    myDb.addName(enterName.getText().toString());
                    popupNameContainer.setVisibility(View.INVISIBLE);
                    //Sets the names in the fragments instantly as they will be the first ones on show once the popup dismisses
                    timerFragment.initialSetName();
                    rewardsFragment.initialSetName();
                    //Once the popup closes the "Help" popup opens to give the user information before they start
                    showPopup(getString(R.string.helpInfo));
                }
                else{
                    toastMessage("No name was entered, please try again");
                }
            }
        });
        markAsNoLongerFirstStart();
    }

    private void markAsNoLongerFirstStart(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_START_PREFS_BOOL, false);
        editor.apply();
    }

    //Method that creates a popup
    public void showPopup(String text){
        popup = new Dialog(this);
        requireNonNull(popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(R.layout.popup_layout);
        ImageView closeBtn = popup.findViewById(R.id.closeBtn);
        TextView popupText = popup.findViewById(R.id.popupText);
        setTextToEdit(text);
        popupText.setText(getTextToEdit());
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
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

    //Using the interface in both fragments, the main activity is able to facilitate communication between the two fragments. Here, it sets the points in each fragment each time
    //it's updated
    @Override
    public void onPointsRewardsSent(int points) {
        timerFragment.updatePoints(points);
    }

    @Override
    public void onPointsTimerSent(int points) {
        rewardsFragment.updatedPoints(points);
    }

    //Using a tab layout tutorial from YouTube user @Coding In Flow, I was able to create a tab layout and customise it so it fit my theme.
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

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
