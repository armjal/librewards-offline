package com.example.librewards.views;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;
import com.example.librewards.controllers.codes.CodesManager;
import com.example.librewards.controllers.codes.StartCodesManager;
import com.example.librewards.controllers.codes.StopCodesManager;
import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;
import com.example.librewards.repositories.TimerRepository;
import com.example.librewards.repositories.UserRepository;

public class TimerFragment extends FragmentExtended implements UserChangeListener, TimerView {
    private static final String TITLE = "Timer";
    private TextView points;
    private TextView name;
    private Button startButton;
    private Button stopButton;
    private EditText timerCodeText;
    private Chronometer timer;
    private ViewUtils viewUtils;

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
        DatabaseHelper myDb = new DatabaseHelper(requireContext());
        UserRepository userRepo = new UserRepository(myDb);
        TimerRepository timerRepo = new TimerRepository(myDb);
        viewUtils = new ViewUtils(requireContext());

        String wholeName = getString(R.string.Hey) + " " + userRepo.getName();
        name.setText(wholeName);
        points.setText(String.valueOf(userRepo.getPoints()));

        StartCodesManager startCodesManager = new StartCodesManager(timerRepo);
        StopCodesManager stopCodesManager = new StopCodesManager(timerRepo);
        TimerHandler timerHandler = new TimerHandler(this, startCodesManager, stopCodesManager);

        startCodesManager.refreshCodes();
        stopCodesManager.refreshCodes();

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
                int totalPoints = timerHandler.saveTotalPointsFromDuration(userRepo);
                announceAccumulatedPoints(timerHandler.getPointsEarned(), totalDuration, totalPoints);
                UserChangeNotifier.notifyPointsChanged(totalPoints);
            }
        });
    }

    private boolean isValidCode(CodesManager codesManager, String inputtedCode) {
        if (inputtedCode.isEmpty()) {
            viewUtils.toastMessage(getString(R.string.emptyCode));
            return false;
        } else if (codesManager.notInCodesList(inputtedCode)) {
            viewUtils.toastMessage(getString(R.string.invalidCode));
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
        viewUtils.showPopup("No stop code was entered for 24 hours. The timer has been reset");
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

    private void announceAccumulatedPoints(int pointsEarned, long totalTimeSpentAtLibrary, int totalPoints) {
        int timeSpentMinutes = ((int) totalTimeSpentAtLibrary / 1000) / 60;
        String popUpMessage;

        if (timeSpentMinutes == 1) {
            popUpMessage = String.format(getString(R.string.congratsMessage), timeSpentMinutes, "minute", pointsEarned, totalPoints);

        } else if (timeSpentMinutes > 1) {
            popUpMessage = String.format(getString(R.string.congratsMessage), timeSpentMinutes, "minutes", pointsEarned, totalPoints);

        } else {
            popUpMessage = getString(R.string.unfortunatelyMessage);
        }
        viewUtils.showPopup(popUpMessage);
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
