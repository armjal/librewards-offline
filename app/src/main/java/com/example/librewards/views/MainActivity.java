package com.example.librewards.views;


import static com.example.librewards.FirstStartHandler.handleFirstStart;

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
import com.example.librewards.repositories.StartCodesRepository;
import com.example.librewards.repositories.StopCodesRepository;
import com.example.librewards.repositories.UserRepository;
import com.example.librewards.views.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private UserRepository userRepo;
    private StartCodesRepository startCodesRepo;
    private StopCodesRepository stopCodesRepo;
    private ViewUtils viewUtils;
    private EditText enterName;
    private Button nameButton;
    private FrameLayout popupNameContainer;
    private UserModel user;
    private TimerFragment timerFragment;
    private RewardsFragment rewardsFragment;
    private RewardsRepository rewardsRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        userRepo = new UserRepository(dbHelper);
        startCodesRepo = new StartCodesRepository(dbHelper);
        stopCodesRepo = new StopCodesRepository(dbHelper);
        rewardsRepo = new RewardsRepository(dbHelper);
        viewUtils = new ViewUtils(this);
        popupNameContainer = findViewById(R.id.popupNameContainer);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageView helpButton = findViewById(R.id.helpButton);
        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);

        rewardsRepo.populate();
        user = userRepo.getUser();
        handleFirstStart(this, this::onFirstStart);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);

        setupTimerFragment();
        setupRewardsFragment();
        List<FragmentExtended> fragments = List.of(timerFragment, rewardsFragment);
        passBundle(fragments, bundle);
        viewPagerAdapter.addFragments(fragments);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
        {
            tab.setText(fragments.get(position).getTitle());
            tab.setIcon(fragments.get(position).getIcon());
        }
        ).attach();

        helpButton.setOnClickListener(v -> viewUtils.showPopup(getString(R.string.helpInfo)));
    }

    private void setupTimerFragment() {
        timerFragment = new TimerFragment();
        timerFragment.setUserRepo(userRepo);
        timerFragment.setStartCodesRepo(startCodesRepo);
        timerFragment.setStopCodesRepo(stopCodesRepo);
    }

    private void setupRewardsFragment() {
        rewardsFragment = new RewardsFragment();
        rewardsFragment.setUserRepo(userRepo);
        rewardsFragment.setRewardsRepo(rewardsRepo);
    }

    private void passBundle(List<FragmentExtended> fragments, Bundle bundle) {
        for (FragmentExtended f : fragments) {
            f.setArguments(bundle);
        }
    }

    public void onFirstStart() {
        requireUserToEnterName();
        dbHelper.processTransaction(() -> {
            startCodesRepo.populate();
            stopCodesRepo.populate();
        });
    }

    public void requireUserToEnterName() {
        popupNameContainer.setVisibility(View.VISIBLE);
        nameButton.setOnClickListener(v -> {
            if (enterName.length() == 0) {
                viewUtils.toastMessage(getString(R.string.noNameEntered));
            } else {
                String userName = enterName.getText().toString();
                user.setName(userName);
                userRepo.addName(userName);
                popupNameContainer.setVisibility(View.INVISIBLE);
                viewUtils.showPopup(getString(R.string.helpInfo));
            }
        });
    }
}
