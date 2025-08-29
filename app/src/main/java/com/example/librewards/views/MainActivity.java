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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    public DatabaseHelper dbHelper;
    @Inject
    public UserRepository userRepo;
    @Inject
    public StartCodesRepository startCodesRepo;
    @Inject
    public StopCodesRepository stopCodesRepo;
    @Inject
    public RewardsRepository rewardsRepo;
    private ViewUtils viewUtils;
    private EditText enterName;
    private Button nameButton;
    private FrameLayout popupNameContainer;
    private UserModel user;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        viewUtils = new ViewUtils(this);
        setupViews();

        rewardsRepo.populate();
        user = userRepo.getUser();
        handleFirstStart(this, this::onFirstStart);
        requireUserNameIfNotSet();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);

        List<FragmentExtended> fragments = List.of(new TimerFragment(), new RewardsFragment());
        setupTabLayout(fragments);
        passBundle(fragments, bundle);

        helpButton.setOnClickListener(v -> viewUtils.showPopup(getString(R.string.helpInfo)));
    }

    public void onFirstStart() {
        requireUserToEnterName();
        dbHelper.processTransaction(() -> {
            startCodesRepo.populate();
            stopCodesRepo.populate();
        });
    }

    private void requireUserNameIfNotSet() {
        if (user.getName().isEmpty()) {
            requireUserToEnterName();
        }
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

    private void setupViews() {
        popupNameContainer = findViewById(R.id.popupNameContainer);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        helpButton = findViewById(R.id.helpButton);
        enterName = findViewById(R.id.enterName);
        nameButton = findViewById(R.id.nameButton);
    }

    private void setupTabLayout(List<FragmentExtended> fragments) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.addFragments(fragments);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
        {
            tab.setText(fragments.get(position).getTitle());
            tab.setIcon(fragments.get(position).getIcon());
        }
        ).attach();
    }

    private void passBundle(List<FragmentExtended> fragments, Bundle bundle) {
        for (FragmentExtended f : fragments) {
            f.setArguments(bundle);
        }
    }
}
