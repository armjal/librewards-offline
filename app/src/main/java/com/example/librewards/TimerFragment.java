package com.example.librewards;

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
import androidx.fragment.app.Fragment;

import com.example.librewards.models.UserChangeListener;
import com.example.librewards.models.UserChangeNotifier;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment implements UserChangeListener {
    public static final String TIMER_TAG = TimerFragment.class.getSimpleName();

    private List<String> currStartCodes = new ArrayList<>();
    private List<String> currStopCodes = new ArrayList<>();

    private String textToEdit;
    private TextView points;
    private TextView name;
    private DatabaseHelper myDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        //Assigns the field to the view's specified in the fragment_timer XML file file
        Chronometer stopwatch = v.findViewById(R.id.stopwatch);
        EditText editText = v.findViewById(R.id.startText);
        Button startButton = v.findViewById(R.id.startButton);
        Button stopButton = v.findViewById(R.id.stopButton);
        myDb = new DatabaseHelper(requireActivity().getApplicationContext());
        points = v.findViewById(R.id.points);
        points.setText(String.valueOf(myDb.getPoints()));
        name = v.findViewById(R.id.nameTimer);

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
            //Sets actions on clicking the "Start" Button
            startButton.setOnClickListener(v2 -> {
                //Checks if there is any text inputted
                if(editText.length() == 0){
                toastMessage("No code was entered, please try again");
                }
                //Checks if the current start code table in the database contains the code that has been inputted
                else if (currStartCodes.contains(editText.getText().toString())) {
                    //Removes the code from the database as it has already been used once
                    currStartCodes.remove(editText.getText().toString());
                    myDb.deleteCode(getString(R.string.start_codes_table), editText.getText().toString());
                    //Clears the input text
                    editText.setText(null);
                    editText.setHint("Please enter the stop code");
                    //Starts the stopwatch
                    stopwatch.setBase(SystemClock.elapsedRealtime());
                    stopwatch.start();
                    //Switches from the 'Start' button to the 'Stop' button
                    startButton.setVisibility(View.INVISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                    //All actions to be taken place once the stopwatch has started
                    stopwatch.setOnChronometerTickListener(chronometer -> {
                        //Checks if the stopwatch has gone over 24 hours. If so, the stopwatch resets back to its original state
                        if ((SystemClock.elapsedRealtime() - stopwatch.getBase()) >= 500000) {
                            stopwatch.setBase(SystemClock.elapsedRealtime());
                            stopwatch.stop();
                            stopButton.setVisibility(View.INVISIBLE);
                            startButton.setVisibility(View.VISIBLE);
                            showPopup("No stop code was entered for 24 hours. The timer has been reset");
                        }
                        stopButton.setOnClickListener(v1 -> {
                            //Checks if there is any text inputted
                            if(editText.length() == 0){
                                toastMessage("No code was entered");
                            }
                            //Checks if the current stop code table in the database contains the code that has been inputted
                            if (currStopCodes.contains(editText.getText().toString())) {
                                //Removes the code from the database as it has already been used once
                                currStopCodes.remove(editText.getText().toString());
                                myDb.deleteCode(getString(R.string.stop_codes_table), editText.getText().toString());

                                //'totalTime' gets the total duration spent at the library in milliseconds
                                long totalTime = SystemClock.elapsedRealtime() - stopwatch.getBase();
                                int pointsEarned = PointsCalculator.calculateFromDuration(totalTime);
                                announceAccumulatedPoints(pointsEarned, totalTime);
                                myDb.addPoints(pointsEarned);
                                points.setText(String.valueOf(myDb.getPoints()));
                                stopwatch.setBase(SystemClock.elapsedRealtime());
                                stopwatch.stop();
                                //Clears the input text and resets to original state
                                editText.setText(null);
                                editText.setHint("Please enter the start code");
                                //Listener to communicate with Rewards Fragment and give the points to display in there
                                UserChangeNotifier.notifyPointsChanged(myDb.getPoints());
                                stopButton.setVisibility(View.INVISIBLE);
                                startButton.setVisibility(View.VISIBLE);

                            }
                            //If the stop code entered is not in the database, a toast will show
                            else{
                                toastMessage("The code you entered is not valid, please try again");
                            }
                        });
                    });
                }
                //If the start code entered is not in the database, a toast will show
                else{
                    toastMessage(getString(R.string.invalidCode));
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

}