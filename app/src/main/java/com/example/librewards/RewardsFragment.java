package com.example.librewards;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;

import java.util.List;


public class RewardsFragment extends Fragment implements UserChangeListener {
    private static final String REWARDS_TAG = RewardsFragment.class.getSimpleName();

    private DatabaseHelper myDb;
    private TextView points;
    private TextView name;
    private String textToEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rewards, container, false);
        //Assigns the field to the view's specified in the fragment_timer XML file file
        myDb = new DatabaseHelper(requireActivity());
        Button rewardButton = v.findViewById(R.id.rewardButton);
        EditText editText = v.findViewById(R.id.rewardText);
        points = v.findViewById(R.id.points2);
        points.setText(String.valueOf(myDb.getPoints()));
        name = v.findViewById(R.id.nameRewards);
        //Adds the codes from the text file to the database and updates the database every time in case there are new codes or costs in the text file
        List<String> rewardsCodes = addNewRewardCodes();
        myDb.updateRewardCodes(rewardsCodes);

        //Sets actions on clicking the "Reward" Button
        rewardButton.setOnClickListener(v1 -> {
            if(editText.length() == 0){
                toastMessage("No code was entered, please try again");
            }
            else if(!rewardsCodes.contains(editText.getText().toString())) {
                toastMessage(getString(R.string.invalidCode));
            } else {
                if (myDb.getPoints() <= myDb.getCost(editText.getText().toString())) {
                    showPopup(getString(R.string.insufficientFunds));
                }
                else {
                    myDb.minusPoints(myDb.getCost(editText.getText().toString()));
                    showPopup("Code accepted, keep it up! Your new points balance is: " + myDb.getPoints());
                    points.setText(String.valueOf(myDb.getPoints()));
                    UserChangeNotifier.notifyPointsChanged(myDb.getPoints());
                }
            }
        });
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        UserChangeNotifier.addListener(this);

        String wholeName = getString(R.string.Hey) + " " + myDb.getName();
        name.setText(wholeName);

    }

    public void showPopup(String text){
        Dialog popup = new Dialog(requireActivity());
        requireNonNull(popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(R.layout.popup_layout);
        ImageView closeBtn = popup.findViewById(R.id.closeBtn);
        TextView popupText = popup.findViewById(R.id.popupText);
        setTextToEdit(text);
        popupText.setText(getTextToEdit());
        closeBtn.setOnClickListener(v -> popup.dismiss());
        popup.show();

    }

    private List<String> addNewRewardCodes(){
        ListFromFile listFromFile;
        List<String> newList;
        listFromFile = new ListFromFile(requireActivity());
        newList = listFromFile.readRewardsLine(getString(R.string.rewardcodes_file_name));
        for (String s : newList)
            Log.d(REWARDS_TAG, s);
        return newList;
    }

    public void toastMessage(String message){
        Toast.makeText(requireActivity() ,message,Toast.LENGTH_LONG).show();
    }

    public void setTextToEdit(String textToEdit) {
        this.textToEdit = textToEdit;
    }

    public String getTextToEdit() {
        return textToEdit;
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
}
