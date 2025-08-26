package com.example.librewards.views;

import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.FirstStartHandler.handleFirstStart;
import static com.example.librewards.resources.TimerCodes.startCodes;
import static com.example.librewards.resources.TimerCodes.stopCodes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager2.widget.ViewPager2;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;
import com.example.librewards.models.UserModel;
import com.example.librewards.repositories.RewardsRepository;
import com.example.librewards.repositories.TimerRepository;
import com.example.librewards.repositories.UserRepository;
import com.example.librewards.views.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private UserRepository userRepo;
    private TimerRepository timerRepo;
    private RewardsRepository rewardsRepo;
    private ViewUtils viewUtils;
    private EditText enterName;
    private Button nameButton;
    private FrameLayout popupNameContainer;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        userRepo = new UserRepository(dbHelper);
        timerRepo = new TimerRepository(dbHelper);
        rewardsRepo = new RewardsRepository(dbHelper);
        viewUtils = new ViewUtils(this);
        popupNameContainer = findViewById(R.id.popupNameContainer);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageView helpButton = findViewById(R.id.helpButton);
        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);

        userModel = userRepo.getUser();
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

        helpButton.setOnClickListener(v -> viewUtils.showPopup(getString(R.string.helpInfo)));
    }

    public void onFirstStart() {
        requireUserToEnterName();
        dbHelper.processTransaction(() -> {
            timerRepo.storeTimerCodes(startCodes, START_CODES_TABLE_NAME);
            timerRepo.storeTimerCodes(stopCodes, STOP_CODES_TABLE_NAME);
            rewardsRepo.storeRewards();
        });
    }

    public void requireUserToEnterName() {
        popupNameContainer.setVisibility(View.VISIBLE);
        nameButton.setOnClickListener(v -> {
            if (enterName.length() == 0) {
                viewUtils.toastMessage(getString(R.string.noNameEntered));
            } else {
                String userName = enterName.getText().toString();
                userModel.setName(userName);
                userRepo.addName(userName);
                popupNameContainer.setVisibility(View.INVISIBLE);
                viewUtils.showPopup(getString(R.string.helpInfo));
            }
        });
    }
}
