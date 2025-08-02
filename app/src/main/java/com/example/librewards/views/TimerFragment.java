package com.example.librewards.views;

import static com.example.librewards.views.ViewUtils.toastMessage;
import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.ListFromFile;
import com.example.librewards.R;
import com.example.librewards.controllers.codes.CodesManager;
import com.example.librewards.controllers.codes.StartCodesManager;
import com.example.librewards.controllers.codes.StopCodesManager;
import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;

public class TimerFragment extends FragmentExtended implements UserChangeListener, TimerView {
    private static final String TITLE = "Timer";

    private String textToEdit;
    private TextView points;
    private TextView name;
    private Button startButton;
    private Button stopButton;
    private EditText timerCodeText;
    private Chronometer timer;
    private DatabaseHelper myDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        timer = v.findViewById(R.id.timer);
        timerCodeText = v.findViewById(R.id.startText);
        startButton = v.findViewById(R.id.startButton);
        stopButton = v.findViewById(R.id.stopButton);
        points = v.findViewById(R.id.points);
        name = v.findViewById(R.id.nameTimer);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        UserChangeNotifier.addListener(this);
        myDb = new DatabaseHelper(requireContext());

        String wholeName = getString(R.string.Hey) + " " + myDb.getName();
        name.setText(wholeName);
        points.setText(String.valueOf(myDb.getPoints()));

        StartCodesManager startCodesManager = new StartCodesManager(myDb);
        StopCodesManager stopCodesManager = new StopCodesManager(myDb);
        TimerHandler timerHandler = new TimerHandler(this, startCodesManager, stopCodesManager);

        startCodesManager.refreshCodes(new ListFromFile(requireContext()));
        stopCodesManager.refreshCodes(new ListFromFile(requireContext()));

        startButton.setOnClickListener(v2 -> {
            String inputtedStartCode = timerCodeText.getText().toString();
            if (isValidCode(startCodesManager, inputtedStartCode)) {
                timerHandler.start(inputtedStartCode);
            }
        });

        stopButton.setOnClickListener(v1 -> {
            String inputtedStopCode = timerCodeText.getText().toString();
            if (isValidCode(stopCodesManager, inputtedStopCode)) {
                long totalDuration = timerHandler.stop(inputtedStopCode);
                int pointsEarned = timerHandler.saveTotalPointsFromDuration(myDb);
                points.setText(String.valueOf(myDb.getPoints()));
                announceAccumulatedPoints(pointsEarned, totalDuration);
                UserChangeNotifier.notifyPointsChanged(pointsEarned);
            }
        });
    }

    private boolean isValidCode(CodesManager codesManager, String inputtedCode) {
        if (inputtedCode.isEmpty()) {
            toastMessage(getString(R.string.emptyCode), requireContext());
            return false;
        } else if (codesManager.notInCodesList(inputtedCode)) {
            toastMessage(getString(R.string.invalidCode), requireContext());
            return false;
        }
        return true;
    }

    @Override
    public void changeTimerToDesiredState(String desiredState) {
        String userCodeRequest = "Please enter the %s code";
        timer.setBase(SystemClock.elapsedRealtime());
        timerCodeText.setText(null);

        if (desiredState.equals(getString(R.string.stop))) {
            timer.stop();
            enableStartButton();
            timerCodeText.setHint(String.format(userCodeRequest, getString(R.string.start)));

        } else if (desiredState.equals(getString(R.string.start))) {
            timer.start();
            enableStopButton();
            timerCodeText.setHint(String.format(userCodeRequest, getString(R.string.stop)));
        }
    }

    @Override
    public void enforceTimerDayLimit() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        enableStartButton();
        showPopup("No stop code was entered for 24 hours. The timer has been reset");
    }

    @Override
    public Chronometer getTimer() {
        return timer;
    }

    private void enableStartButton() {
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    private void enableStopButton() {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
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

    private void announceAccumulatedPoints(int pointsEarned, long totalTimeSpentAtLibrary) {
        int timeSpentMinutes = ((int) totalTimeSpentAtLibrary / 1000) / 60;
        if (timeSpentMinutes == 1) {
            showPopup("Well done, you spent " + timeSpentMinutes + " minute at the library and have earned " + pointsEarned + " points!\nYour new points balance is: " + myDb.getPoints());

        } else if (timeSpentMinutes > 1) {
            showPopup("Well done, you spent " + timeSpentMinutes + " timeSpentMinutes at the library and have earned " + pointsEarned + " points!\nYour new points balance is: " + myDb.getPoints());

        } else {
            showPopup("Unfortunately you have not spent the minimum required time at the library to receive points!");
        }
    }

    public void showPopup(String text) {
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

    public void setTextToEdit(String textToEdit) {
        this.textToEdit = textToEdit;
    }

    public String getTextToEdit() {
        return textToEdit;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.timer;
    }
}
