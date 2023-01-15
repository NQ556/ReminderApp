package com.example.meowminder;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meowminder.database.NoteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private View view;

    private TextView monthStr;
    private TextView yearStr;

    private TextView dateOne;
    private TextView dateTwo;
    private TextView dateThree;
    private TextView dateFour;

    private List<String> monthList;
    private List<String> yearList;
    private List<String> dateList;

    private LinearLayout moreButton;
    private LinearLayout noNoteLayout;

    private List<Note> notDoneList;

    private NoteAdapter noteAdapter;
    private RecyclerView noteRcv;

    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Initialize UI
        initializeUI();

        //Set current date to calendar
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //Set dates, month, year
        setDate(calendar);

        //Select other dates
        selectDate();

        //Set up recyclerview
        setUpRecyclerView();

        //Click on dates
        clickDateOne();
        clickDateTwo();
        clickDateThree();
        clickDateFour();

        return view;
    }

    private void initializeUI() {
        monthStr = (TextView) view.findViewById(R.id.month);
        yearStr = (TextView) view.findViewById(R.id.year);

        dateOne = (TextView) view.findViewById(R.id.date_1);
        dateTwo = (TextView) view.findViewById(R.id.date_2);
        dateThree = (TextView) view.findViewById(R.id.date_3);
        dateFour = (TextView) view.findViewById(R.id.date_4);

        moreButton = (LinearLayout) view.findViewById(R.id.more);
        noNoteLayout = (LinearLayout) view.findViewById(R.id.no_note_layout);

        noteRcv = (RecyclerView) view.findViewById(R.id.note_rcv);
    }

    private void setDate(Calendar calendar) {
        //Initialize lists
        monthList = new ArrayList<>();
        yearList = new ArrayList<>();
        dateList = new ArrayList<>();

        //Get day, month, year format
        String dateFormat = "dd/MM/yyyy";
        String dayFormat = "d";
        String monthFormat = "MMMM";
        String yearFormat = "yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        SimpleDateFormat simpleDayFormat = new SimpleDateFormat(dayFormat);
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat(monthFormat);
        SimpleDateFormat simpleYearFormat = new SimpleDateFormat(yearFormat);

        //Set current day, month, year
        monthStr.setText(simpleMonthFormat.format(calendar.getTime()));
        yearStr.setText(simpleYearFormat.format(calendar.getTime()));
        dateOne.setText(simpleDayFormat.format(calendar.getTime()));

        dateList.add(simpleDateFormat.format(calendar.getTime()));
        monthList.add(simpleMonthFormat.format(calendar.getTime()));
        yearList.add(simpleYearFormat.format(calendar.getTime()));

        //Get three next days
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dateTwo.setText(simpleDayFormat.format(calendar.getTime()));
        dateList.add(simpleDateFormat.format(calendar.getTime()));
        monthList.add(simpleMonthFormat.format(calendar.getTime()));
        yearList.add(simpleYearFormat.format(calendar.getTime()));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dateThree.setText(simpleDayFormat.format(calendar.getTime()));
        dateList.add(simpleDateFormat.format(calendar.getTime()));
        monthList.add(simpleMonthFormat.format(calendar.getTime()));
        yearList.add(simpleYearFormat.format(calendar.getTime()));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dateFour.setText(simpleDayFormat.format(calendar.getTime()));
        dateList.add(simpleDateFormat.format(calendar.getTime()));
        monthList.add(simpleMonthFormat.format(calendar.getTime()));
        yearList.add(simpleYearFormat.format(calendar.getTime()));
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        noteRcv.setLayoutManager(gridLayoutManager);

        //Get list of notes from database
        notDoneList = new ArrayList<>();
        notDoneList = NoteDatabase.getInstance(getActivity()).noteDAO().getNotDoneListByDate(dateList.get(0));

        //Set adapter
        noteAdapter = new NoteAdapter(notDoneList);
        noteRcv.setAdapter(noteAdapter);

        //Show no note layout if list is empty
        showNoNoteLayout();
    }

    private void showNoNoteLayout() {
        if (notDoneList.size() > 0)
        {
            noteRcv.setVisibility(View.VISIBLE);
            noNoteLayout.setVisibility(View.INVISIBLE);
        }

        else
        {
            noteRcv.setVisibility(View.INVISIBLE);
            noNoteLayout.setVisibility(View.VISIBLE);
        }
    }

    private void clickDateOne() {
        dateOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNoteBasedOnDate(0);
            }
        });
    }

    private void clickDateTwo() {
        dateTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNoteBasedOnDate(1);
            }
        });
    }

    private void clickDateThree() {
        dateThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNoteBasedOnDate(2);
            }
        });
    }

    private void clickDateFour() {
        dateFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNoteBasedOnDate(3);
            }
        });
    }

    private void loadNoteBasedOnDate(int index) {
        //Highlight and unhighlight
        highLightDate(index);

        //Set month, year
        monthStr.setText(monthList.get(index));
        yearStr.setText(yearList.get(index));

        //Show notes
        notDoneList.clear();
        List<Note> tmpList = new ArrayList<>();
        tmpList = NoteDatabase.getInstance(getActivity()).noteDAO().getNotDoneListByDate(dateList.get(index));
        notDoneList.addAll(tmpList);
        noteAdapter.notifyDataSetChanged();

        //Show no note layout if list is empty
        showNoNoteLayout();
    }

    private void unHighLightAll() {
        dateOne.setTextColor(getResources().getColor(R.color.pink));
        dateOne.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        dateTwo.setTextColor(getResources().getColor(R.color.pink));
        dateTwo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        dateThree.setTextColor(getResources().getColor(R.color.pink));
        dateThree.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        dateFour.setTextColor(getResources().getColor(R.color.pink));
        dateFour.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
    }

    private void highLightDate(int index) {
        unHighLightAll();

        switch (index)
        {
            case 0:
                dateOne.setTextColor(getResources().getColor(R.color.white));
                dateOne.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                break;

            case 1:
                dateTwo.setTextColor(getResources().getColor(R.color.white));
                dateTwo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                break;

            case 2:
                dateThree.setTextColor(getResources().getColor(R.color.white));
                dateThree.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                break;

            case 3:
                dateFour.setTextColor(getResources().getColor(R.color.white));
                dateFour.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                break;
        }
    }

    private void selectDate() {
        //Set up date picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDate(calendar);
            }
        };

        //Select date
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), R.style.date_picker_theme, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}