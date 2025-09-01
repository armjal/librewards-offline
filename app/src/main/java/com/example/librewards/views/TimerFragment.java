package com.example.librewards.views;

import static com.example.librewards.utils.PointsCalculator.calculatePointsFromDuration;
import static com.example.librewards.views.utils.ViewUtils.showPopup;
import static com.example.librewards.views.utils.ViewUtils.toastMessage;

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

import com.example.librewards.R;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.notifiers.UserChangeListener;
import com.example.librewards.data.notifiers.UserChangeNotifier;
import com.example.librewards.data.repositories.CodesRepository;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.data.repositories.StopCodesRepository;
import com.example.librewards.data.repositories.UserRepository;
import com.example.librewards.views.utils.FragmentExtended;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerFragment extends FragmentExtended implements UserChangeListener {
    private static final String TITLE = "Timer";
    @Inject
    public UserRepository userRepo;
    @Inject
    public StartCodesRepository startCodesRepo;
    @Inject
    public StopCodesRepository stopCodesRepo;
    private TextView points;
    private TextView name;
    private Button startButton;
    private Button stopButton;
    private EditText timerCodeText;
    private Chronometer timer;
    private UserModel user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        timer = v.findViewById(R.id.timer);
        timerCodeText = v.findViewById(R.id.timerCodeText);
        startButton = v.findViewById(R.id.startButton);
        stopButton = v.findViewById(R.id.stopButton);
        points = v.findViewById(R.id.points);
        name = v.findViewById(R.id.nameTimer);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        UserChangeNotifier.addListener(this);
        user = (UserModel) getParcelable("user");

        name.setText(String.format(getString(R.string.welcome), user.getName()));
        points.setText(String.valueOf(user.getPoints()));
        startCodesRepo.checkForUpdates();
        stopCodesRepo.checkForUpdates();

        startButton.setOnClickListener(v2 -> {
            String inputtedStartCode = timerCodeText.getText().toString();
            if (isValidCode(startCodesRepo, inputtedStartCode)) {
                startTimer();
                startCodesRepo.delete(inputtedStartCode);
            }
        });

        stopButton.setOnClickListener(v1 -> {
            String inputtedStopCode = timerCodeText.getText().toString();
            if (isValidCode(stopCodesRepo, inputtedStopCode)) {
                long totalDuration = stopTimer();
                int pointsEarned = calculatePointsFromDuration(totalDuration);
                stopCodesRepo.delete(inputtedStopCode);
                userRepo.addPoints(user, pointsEarned);
                announceAccumulatedPoints(pointsEarned, totalDuration, user.getPoints());
            }
        });
    }

    public void startTimer() {
        changeTimerToDesiredState("start");
        timer.setOnChronometerTickListener(chronometer -> {
            if ((SystemClock.elapsedRealtime() - timer.getBase()) >= 500000) {
                enforceTimerDayLimit();
            }
        });
    }

    public long stopTimer() {
        changeTimerToDesiredState("stop");
        return SystemClock.elapsedRealtime() - timer.getBase();
    }

    private boolean isValidCode(CodesRepository codesRepo, String inputtedCode) {
        if (inputtedCode.isEmpty()) {
            toastMessage(requireContext(), getString(R.string.emptyCode));
            return false;
        } else if (codesRepo.get(inputtedCode).isEmpty()) {
            toastMessage(requireContext(), getString(R.string.invalidCode));
            return false;
        }
        return true;
    }

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

    public void enforceTimerDayLimit() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        enableStartButton();
        showPopup(requireContext(), getString(R.string.noCodeEnteredInADay));
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
        String wholeName = String.format(getString(R.string.welcome), user.getName());
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
            popUpMessage = String.format(getString(R.string.congratsMessage), timeSpentMinutes, "minute", pointsEarned
                    , totalPoints);

        } else if (timeSpentMinutes > 1) {
            popUpMessage = String.format(getString(R.string.congratsMessage), timeSpentMinutes, "minutes",
                    pointsEarned, totalPoints);

        } else {
            popUpMessage = getString(R.string.unfortunatelyMessage);
        }
        showPopup(requireContext(), popUpMessage);
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
