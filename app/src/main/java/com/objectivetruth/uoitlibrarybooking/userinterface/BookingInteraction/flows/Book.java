package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventUserRequest;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventUserRequestType;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.BookRequestOptions;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

public class Book extends InteractionFragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private String comment = "";
    private static final String TIME_CELL_BUNDLE_KEY = "TIME_CELL_BUNDLE_KEY";
    private static final String MONTH_WORD_BUNDLE_KEY = "MONTH_WORD_BUNDLE_KEY";
    private static final String DAY_OF_MONTH_NUMBER_BUNDLE_KEY = "DAY_OF_MONTH_NUMBER_BUNDLE_KEY";

    private EditText groupNameET;
    private EditText groupCodeET;
    private ProgressBar createProgressBar;
    private Button createButton;
    private ImageButton groupCodeInfoImageButton;
    private TextView errorTextView;
    private ImageView pictureOfRoom;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private String durationSpinnerValue;
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_book, container, false);

        errorTextView = (TextView) view.findViewById(R.id.bookingInteraction_book_error_text);
        pictureOfRoom = (ImageView) view.findViewById(R.id.bookingInteraction_book_room_picture);
        groupCodeET = (EditText) view.findViewById(R.id.book_group_code_actual);
        groupNameET = (EditText) view.findViewById(R.id.bookingInteraction_book_groupname);
        createProgressBar = (ProgressBar) view.findViewById(R.id.bookingInteraction_book_create_loadingbar);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.bookingInteraction_book_roomnumber);
        if(roomNumberTextView != null) {roomNumberTextView.setText(timeCell.param_room.toUpperCase());}

        ImageButton commentButton = (ImageButton) view.findViewById(R.id.bookingInteraction_book_comment_button);
        _setupCommentButton(commentButton);

        createButton = (Button) view.findViewById(R.id.bookingInteraction_book_create_button);
        _setupCreateButton(createButton);

        groupCodeInfoImageButton = (ImageButton) view.findViewById(R.id.bookingInteraction_book_group_code_info);
        _setupGroupCodeInfoButton(groupCodeInfoImageButton);

        Spinner durationSpinner = (Spinner) view.findViewById(R.id.book_spinner_duration);
        _setupDurationSpinner(durationSpinner);
        return view;
    }

    static public Book newInstance(BookingInteractionEvent bookinginteractionEventWithDateInfo) {
        Book fragment = new Book();
        fragment.timeCell = bookinginteractionEventWithDateInfo.timeCell;
        fragment.monthWord = bookinginteractionEventWithDateInfo.monthWord;
        fragment.dayOfMonthNumber = bookinginteractionEventWithDateInfo.dayOfMonthNumber;
        return fragment;
    }

    protected void setupViewBindings() {
        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        switch(bookingInteractionEvent.type) {
                            case BOOK_ERROR:
                                _showErrorMessage(bookingInteractionEvent.message);
                                _showCreateButtonHideLoading();
                                break;
                            case BOOK_RUNNING:
                                _hideCreateButtonShowLoading();
                                break;
                            case BOOK:
                                _showCreateButtonHideLoading();
                                break;

                        }
                    }
                }));
    }

    protected void teardownViewBindings() {
        subscriptions.unsubscribe();
    }

    private void _showErrorMessage(String message) {
        Float SEE_THROUGH = 0.5f;
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(message);
        pictureOfRoom.setAlpha(SEE_THROUGH);
    }

    private void _hideErrorMessage() {
        Float FULLY_VISIBLE = 1.0f;
        errorTextView.setVisibility(View.INVISIBLE);
        errorTextView.setText("");
        pictureOfRoom.setAlpha(FULLY_VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        if(savedInstanceState != null) {
            _loadPreviousStateIfAvailable(savedInstanceState);
        }
    }

    private void _setupCommentButton(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _showCommentDialog();
            }
        });
    }

    private void _setupGroupCodeInfoButton(ImageButton groupCodeInfoButton) {
        groupCodeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _showGroupCodeInfoDialog();
            }
        });
    }

    private void _showGroupCodeInfoDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(R.string.INFO_BOOKING_INTERACTION_BOOK_GROUPCODE_INFO_PARAGRAPH);
        alert.setTitle(R.string.INFO_BOOKING_INTERACTION_BOOK_GROUPCODE_INFO_TITLE);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    private void _showCommentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText edittext = new EditText(getActivity());
        alert.setMessage(R.string.INFO_BOOKING_INTERACTION_BOOK_COMMENT_INFO_PARAGRAPH);
        alert.setTitle(R.string.INFO_BOOKING_INTERACTION_BOOK_COMMENT_INFO_TITLE);

        alert.setView(edittext);

        alert.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                comment = edittext.getText().toString();
                Timber.i("Comment changed by user to: " + comment);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Timber.i("Comment edit canceled by user. Comment will be " + comment);
            }
        });

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Timber.i("Comment edit canceled by user. Comment will be " + comment);
            }
        });
        alert.create().show();
    }

    private void _setupCreateButton(Button createButton) {
        if(createButton != null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_isFormFilledCorrectly()) {
                        _hideErrorMessage();
                        BookRequestOptions requestOptions = new BookRequestOptions(
                                groupNameET.getText().toString().trim(),
                                groupCodeET.getText().toString().trim(),
                                durationSpinnerValue,
                                comment
                        );
                        Timber.v(requestOptions.toString());
                        bookingInteractionModel.getBookingInteractionEventUserRequestSubject()
                                .onNext(new BookingInteractionEventUserRequest(
                                        timeCell,
                                        BookingInteractionEventUserRequestType.BOOK_REQUEST,
                                        dayOfMonthNumber,
                                        monthWord,
                                        requestOptions));
                    }else {
                        _showValidationErrorsAndAnimations();
                    }
                }
            });
        }
    }

    private void _loadPreviousStateIfAvailable(Bundle inState) {
        timeCell = inState.getParcelable(TIME_CELL_BUNDLE_KEY);
        monthWord = inState.getString(MONTH_WORD_BUNDLE_KEY);
        dayOfMonthNumber = inState.getString(DAY_OF_MONTH_NUMBER_BUNDLE_KEY);
    }

    private void _setupDurationSpinner(Spinner durationSpinner) {
        final int DURATION_SPINNER_DEFAULT_POSITION = 1;
        final String[] durationValues = getResources().getStringArray(R.array.BOOK_STR_ARRAY_DURATION_SPINNER_VALUES);
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.BOOK_STR_ARRAY_DURATION_SPINNER_LABELS, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                durationSpinnerValue = durationValues[position];
                Timber.d("Spinner value is now: "+ durationSpinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                // Do nothing
            }
        });
        durationSpinner.setSelection(DURATION_SPINNER_DEFAULT_POSITION);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TIME_CELL_BUNDLE_KEY, timeCell);
        outState.putString(DAY_OF_MONTH_NUMBER_BUNDLE_KEY, dayOfMonthNumber);
        outState.putString(MONTH_WORD_BUNDLE_KEY, monthWord);
    }

    private boolean _isFormFilledCorrectly() {
        return _isGroupCodeFilledCorrectly() && _isGroupNameFilledCorrectly();
    }

    private boolean _isGroupCodeFilledCorrectly() {
        return !groupCodeET.getText().toString().trim().isEmpty();
    }

    private boolean _isGroupNameFilledCorrectly() {
        return !groupNameET.getText().toString().trim().isEmpty();
    }

    private void _showValidationErrorsAndAnimations() {
        if(!_isGroupNameFilledCorrectly()) {
            Timber.d("GroupName not filled correctly, playing animation");
            YoYo.with(Techniques.Shake).duration(1000).playOn(groupNameET);
        }

        if(!_isGroupCodeFilledCorrectly()) {
            Timber.d("GroupCode not filled correctly, playing animation");
            YoYo.with(Techniques.Shake).delay(100).duration(900).playOn(groupCodeET);
            YoYo.with(Techniques.Shake).delay(100).duration(900).playOn(groupCodeInfoImageButton);
        }
    }

    private void _hideCreateButtonShowLoading() {
        createButton.setVisibility(View.INVISIBLE);
        createProgressBar.setVisibility(View.VISIBLE);
    }

    private void _showCreateButtonHideLoading() {
        createButton.setVisibility(View.VISIBLE);
        createProgressBar.setVisibility(View.INVISIBLE);
    }
}
