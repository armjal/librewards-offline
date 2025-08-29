package com.example.librewards.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;
import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;
import com.example.librewards.models.UserModel;
import com.example.librewards.repositories.RewardsRepository;
import com.example.librewards.repositories.UserRepository;

import java.util.List;


public class RewardsFragment extends FragmentExtended implements UserChangeListener {
    private static final String TITLE = "Rewards";
    private UserRepository userRepo;
    private UserModel user;
    private RewardsRepository rewardsRepo;
    private ViewUtils viewUtils;
    private TextView points;
    private TextView name;
    private Button rewardButton;
    private EditText rewardText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rewards, container, false);

        rewardButton = v.findViewById(R.id.rewardButton);
        rewardText = v.findViewById(R.id.rewardText);
        points = v.findViewById(R.id.points2);
        name = v.findViewById(R.id.nameRewards);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        UserChangeNotifier.addListener(this);
        user = (UserModel) getParcelable("user");
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        userRepo = new UserRepository(dbHelper);
        rewardsRepo = new RewardsRepository(dbHelper);
        viewUtils = new ViewUtils(requireContext());
        List<String> rewardCodes = rewardsRepo.refresh();

        name.setText(String.format(getString(R.string.welcome), user.getName()));
        points.setText(String.valueOf(user.getPoints()));

        rewardButton.setOnClickListener(v1 -> {
            String inputtedRewardCode = rewardText.getText().toString();
            if (validateRewardCode(rewardCodes, inputtedRewardCode)) {
                purchaseReward(inputtedRewardCode);
            }
        });
    }

    public void purchaseReward(String inputtedRewardCode) {
        if (user.getPoints() <= rewardsRepo.getCost(inputtedRewardCode)) {
            viewUtils.showPopup(getString(R.string.insufficientFunds));
        } else {
            userRepo.minusPoints(user, rewardsRepo.getCost(inputtedRewardCode));
            viewUtils.showPopup(String.format(getString(R.string.rewardCodeAccepted), userRepo.getPoints()));
            points.setText(String.valueOf(userRepo.getPoints()));
            UserChangeNotifier.notifyPointsChanged(userRepo.getPoints());
        }
    }

    private boolean validateRewardCode(List<String> rewardCodes, String inputtedRewardCode) {
        if (inputtedRewardCode.isEmpty()) {
            viewUtils.toastMessage(getString(R.string.emptyCode));
            return false;
        } else if (!rewardCodes.contains(inputtedRewardCode)) {
            viewUtils.toastMessage(getString(R.string.invalidCode));
            return false;
        }
        return true;
    }

    @Override
    public void onNameChanged(String newName) {
        String wholeName = String.format(getString(R.string.welcome), user.getName());
        name.setText(wholeName);
    }

    @Override
    public void onPointsChanged(int newPoints) {
        points.setText(String.valueOf(newPoints));
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.reward;
    }
}
