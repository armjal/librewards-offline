package com.example.librewards.views;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.ListFromFile;
import com.example.librewards.PointsCalculator;
import com.example.librewards.R;
import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends FragmentExtended implements UserChangeListener {
    private static final String TITLE = "Timer";
    public static final String TIMER_TAG = TimerFragment.class.getSimpleName();
    private List<String> currStartCodes = new ArrayList<>();
    private List<String> currStopCodes = new ArrayList<>();

    private String textToEdit;
    private TextView points;
    private TextView name;
    private DatabaseHelper myDb;
    private Button startButton;
    private Button stopButton;
    private EditText timerCodeText;
    private Chronometer timer;

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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        UserChangeNotifier.addListener(this);
        myDb = new DatabaseHelper(requireActivity().getApplicationContext());

        String wholeName = getString(R.string.Hey) + " " + myDb.getName();
        name.setText(wholeName);
        points.setText(String.valueOf(myDb.getPoints()));

        //Gets all of the codes that are currently in the database and adds them to a list
        addCurrCodes(currStartCodes,getString(R.string.start_codes_table));
        addCurrCodes(currStopCodes,getString(R.string.stop_codes_table));
        //Gets all of the codes listed in the text files and add them to a list
        List<String> originalStartCodes = addNewCodes(getString(R.string.startcodes_file_name));
        List<String> originalStopCodes = addNewCodes(getString(R.string.stopcodes_file_name));
        //Checks if the text files have any codes different to the ones currently in the database and updates the
        //database if so. This is the method that would be used once the codes need to be refreshed. This
        //would happen every once in a while
        currStartCodes = checkForUpdates(currStartCodes, originalStartCodes, getString(R.string.start_codes_table));
        currStopCodes = checkForUpdates(currStopCodes,originalStopCodes, getString(R.string.stop_codes_table));

        startButton.setOnClickListener(v2 -> {
            String inputtedStartCode = timerCodeText.getText().toString();
            if(validateTimerCode(inputtedStartCode, currStartCodes)) {
                //Removes the code from the database as it has already been used once
                currStartCodes.remove(inputtedStartCode);
                myDb.deleteCode(getString(R.string.start_codes_table), inputtedStartCode);
                changeTimerState(getString(R.string.start));
                timer.setOnChronometerTickListener(chronometer -> enforceTimerDayLimit());

        stopButton.setOnClickListener(v1 -> {
            String inputtedStopCode = timerCodeText.getText().toString();
            if(validateTimerCode(inputtedStopCode, currStopCodes)) {
                currStopCodes.remove(inputtedStopCode);
                myDb.deleteCode(getString(R.string.stop_codes_table), inputtedStopCode);

                //'totalTime' gets the total duration spent at the library in milliseconds
                long totalTime = SystemClock.elapsedRealtime() - timer.getBase();
                int pointsEarned = PointsCalculator.calculateFromDuration(totalTime);
                announceAccumulatedPoints(pointsEarned, totalTime);
                myDb.addPoints(pointsEarned);
                points.setText(String.valueOf(myDb.getPoints()));
                changeTimerState(getString(R.string.stop));
                //Listener to communicate with Rewards Fragment and give the points to display in there
                UserChangeNotifier.notifyPointsChanged(myDb.getPoints());
            }
        });
            }
        });
    }
    
    private void changeTimerState(String desiredState){
        String userCodeRequest = "Please enter the %s code";
        timer.setBase(SystemClock.elapsedRealtime());
        timerCodeText.setText(null);

        if(desiredState.equals(getString(R.string.stop))){
            timer.stop();
            stopButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            timerCodeText.setHint(String.format(userCodeRequest, R.string.start));

        } else if(desiredState.equals(getString(R.string.start))){
            timer.start();
            startButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            timerCodeText.setHint(String.format(userCodeRequest, getString(R.string.stop)));
        }
    }

    private void enforceTimerDayLimit() {
        if ((SystemClock.elapsedRealtime() - timer.getBase()) >= 500000) {
            timer.setBase(SystemClock.elapsedRealtime());
            timer.stop();
            stopButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            showPopup("No stop code was entered for 24 hours. The timer has been reset");
        }
    }

    private boolean validateTimerCode(String inputtedCode, List<String> codes) {
        if(inputtedCode.isEmpty()){
            toastMessage("No code was entered, please try again");
            return false;
        }
        else if (!codes.contains(inputtedCode)) {
            toastMessage(getString(R.string.invalidCode));
            return false;
        }
        return true;
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

    //Method to check if the text file has been updated with new codes or not
    public List<String> checkForUpdates(List<String> currCodes, List<String> originalCodes, String table){
        List<String> tempCodes = new ArrayList<>();
        //Loop to check if the elements in the 'currCodes' list exactly matches those in the text file. The ones that
        //match get added into a temporary list
        for(int i = 0; i<currCodes.size(); i++){
            for (int j = 0; j<originalCodes.size(); j++){
                if(originalCodes.get(j).equals(currCodes.get(i))){
                    tempCodes.add(currCodes.get(i));
                }
            }
        }
        //Temporary list is compared with the current codes list. If they are not an
        //exact match, the codes update using the method in the DatabaseHelper class
        if(!(currCodes.equals(tempCodes))){
            currCodes = originalCodes;
            myDb.updateCodes(table,originalCodes);
        }
        return currCodes;

    }
    //Method to add the current codes that are in the database to a list
    private void addCurrCodes(List<String> codeList, String table) {
        Cursor c = myDb.getAllData("codes", table);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            codeList.add(c.getString(c.getColumnIndex("codes")));
            c.moveToNext();
        }
    }
    private void announceAccumulatedPoints(int pointsEarned, long totalTimeSpentAtLibrary) {
        int timeSpentMinutes = ((int) totalTimeSpentAtLibrary / 1000) /60;
        if(timeSpentMinutes == 1){
            showPopup("Well done, you spent "+ timeSpentMinutes +" minute at the library and have earned " + pointsEarned + " points!\nYour new points balance is: " + myDb.getPoints());

        }
        else if(timeSpentMinutes > 1){
            showPopup("Well done, you spent "+ timeSpentMinutes +" timeSpentMinutes at the library and have earned " + pointsEarned + " points!\nYour new points balance is: " + myDb.getPoints());

        }
        else{
            showPopup("Unfortunately you have not spent the minimum required time at the library to receive points!");

        }
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
    private List<String> addNewCodes(String path){
        List<String> newList;
        ListFromFile listFromFile = new ListFromFile(requireActivity().getApplicationContext());
        newList = listFromFile.readLine(path);
        for (String s : newList)
            Log.d(TIMER_TAG, s);
        return newList;
    }

    public void setTextToEdit(String textToEdit) {
        this.textToEdit = textToEdit;
    }

    public String getTextToEdit() {
        return textToEdit;
    }

    //Custom Toast message
    public void toastMessage(String message){
        Toast.makeText(requireActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
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
