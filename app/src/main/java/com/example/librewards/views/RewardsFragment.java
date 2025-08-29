package com.example.librewards.views;

import static com.example.librewards.views.utils.ViewUtils.showPopup;
import static com.example.librewards.views.utils.ViewUtils.toastMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.librewards.R;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.notifiers.UserChangeListener;
import com.example.librewards.data.notifiers.UserChangeNotifier;
import com.example.librewards.data.repositories.RewardsRepository;
import com.example.librewards.data.repositories.UserRepository;
import com.example.librewards.views.utils.FragmentExtended;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RewardsFragment extends FragmentExtended implements UserChangeListener {
    private static final String TITLE = "Rewards";
    @Inject
    public UserRepository userRepo;
    @Inject
    public RewardsRepository rewardsRepo;
    private UserModel user;
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

        name.setText(String.format(getString(R.string.welcome), user.getName()));
        points.setText(String.valueOf(user.getPoints()));

        rewardButton.setOnClickListener(v1 -> {
            String inputtedRewardCode = rewardText.getText().toString();
            if (validateRewardCode(inputtedRewardCode)) {
                purchaseReward(inputtedRewardCode);
            }
        });
    }

    public void purchaseReward(String inputtedRewardCode) {
        if (user.getPoints() <= rewardsRepo.getCost(inputtedRewardCode)) {
            showPopup(requireContext(), getString(R.string.insufficientFunds));
        } else {
            userRepo.minusPoints(user, rewardsRepo.getCost(inputtedRewardCode));
            showPopup(requireContext(), String.format(getString(R.string.rewardCodeAccepted), userRepo.getPoints()));
            points.setText(String.valueOf(userRepo.getPoints()));
        }
    }

    private boolean validateRewardCode(String inputtedRewardCode) {
        if (inputtedRewardCode.isEmpty()) {
            toastMessage(requireContext(), getString(R.string.emptyCode));
            return false;
        } else if (rewardsRepo.getCode(inputtedRewardCode).isEmpty()) {
            toastMessage(requireContext(), getString(R.string.invalidCode));
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
