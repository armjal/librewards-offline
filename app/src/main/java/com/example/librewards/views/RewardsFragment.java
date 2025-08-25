package com.example.librewards.views;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;
import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;
import com.example.librewards.repositories.UserRepository;

import java.util.List;


public class RewardsFragment extends FragmentExtended implements UserChangeListener{
    private static final String TITLE = "Rewards";
    private DatabaseHelper myDb;
    private UserRepository userRepo;
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
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState){
        myDb = new DatabaseHelper(requireActivity());
        userRepo = new UserRepository(myDb);
        List<String> rewardCodes = myDb.refreshRewardCodes();

        points.setText(String.valueOf(userRepo.getPoints()));

        rewardButton.setOnClickListener(v1 -> {
            String inputtedRewardCode = rewardText.getText().toString();
            if(validateRewardCode(rewardCodes, inputtedRewardCode)) {
                purchaseReward(inputtedRewardCode);
            }
        });
        UserChangeNotifier.addListener(this);

        String wholeName = getString(R.string.Hey) + " " + userRepo.getName();
        name.setText(wholeName);
    }

    public void purchaseReward(String inputtedRewardCode){
            if (userRepo.getPoints() <= myDb.getRewardCost(inputtedRewardCode)) {
                showPopup(getString(R.string.insufficientFunds));
            } else {
                userRepo.minusPoints(myDb.getRewardCost(inputtedRewardCode));
                showPopup("Code accepted, keep it up! Your new points balance is: " + userRepo.getPoints());
                points.setText(String.valueOf(userRepo.getPoints()));
                UserChangeNotifier.notifyPointsChanged(userRepo.getPoints());
            }
        }

    private boolean validateRewardCode(List<String> rewardCodes, String inputtedRewardCode){
        if (inputtedRewardCode.isEmpty()) {
            toastMessage("No code was entered, please try again");
            return false;
        } else if (!rewardCodes.contains(inputtedRewardCode)) {
            toastMessage(getString(R.string.invalidCode));
            return false;
        }
        return true;
    }

    public void showPopup(String text){
        Dialog popup = new Dialog(requireActivity());
        requireNonNull(popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(R.layout.popup_layout);
        ImageView closeBtn = popup.findViewById(R.id.closeBtn);
        TextView popupText = popup.findViewById(R.id.popupText);
        popupText.setText(text);
        closeBtn.setOnClickListener(v -> popup.dismiss());
        popup.show();

    }

    public void toastMessage(String message){
        Toast.makeText(requireActivity() ,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNameChanged(String newName) {
        String wholeName = getString(R.string.Hey) + " " + newName;
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
