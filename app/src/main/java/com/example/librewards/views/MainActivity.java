package com.example.librewards.views;

import static com.example.librewards.FirstStartHandler.handleFirstStart;
import static java.util.Objects.requireNonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager2.widget.ViewPager2;

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

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;
import com.example.librewards.models.UserModel;
import com.example.librewards.views.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        popupNameContainer = findViewById(R.id.popupNameContainer);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageView helpButton = findViewById(R.id.helpButton);
        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);

        userModel = new UserModel();

        handleFirstStart(this, this::onFirstStart);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        List<FragmentExtended> fragments = List.of(new TimerFragment(), new RewardsFragment());
        viewPagerAdapter.addFragments(fragments);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
            {
                tab.setText(fragments.get(position).getTitle());
                tab.setIcon(fragments.get(position).getIcon());
            }
        ).attach();

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
}
