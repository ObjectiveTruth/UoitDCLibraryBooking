package com.objectivetruth.uoitlibrarybooking;


import android.support.v4.app.Fragment;


public class Calendar_Generic_Page_Fragment extends Fragment {
/*	final String TAG = "Calendar_generic_Page_fragment";
	String pageNumberStr = null;
	int pageNumberInt;
	boolean firstTimeRunning = true; //this is to know when to add fragments
	AppCompatActivity mActivity;
	String[][] correspondingArr;
	String[][] arrayToUse;
    ImageView sorryCartoonIV;
    it.sephiroth.android.library.widget.HListView hListView;
	ArrayList<CalendarMonth> calendarCache = null;
	boolean hasRooms = true;
	CalendarAdapter mCalendarAdapter = null;
    TableFixHeaders tableFixHeaders;
    int oldScrollPositionX = -1;
    int oldScrollPositionY = -1;
	static AsyncRoomInteraction mAsyncRoomInteraction = null;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View rootView;
        Timber.i("Page position " + pageNumberStr);
		mCalendarAdapter = new CalendarAdapter(getActivity());
        if(hasRooms){
            rootView = inflater.inflate(R.layout.calendar_home_fragment, container, false);
            tableFixHeaders = (TableFixHeaders) rootView.findViewById(R.id.calendar_table);
            int CAN_BE_ANY_NUMBER = 0;
            tableFixHeaders.setAdapter(mCalendarAdapter);
            int cellHeight = mCalendarAdapter.getHeight(CAN_BE_ANY_NUMBER);

            if(oldScrollPositionX < 0){
                int currentTimeCell = mCalendarAdapter.currentTimeCell;
                int newScrollY = cellHeight * currentTimeCell;
                Timber.i("First time Updating, scrolling Y to: " + newScrollY + " for current time");
                tableFixHeaders.scrollTo(0, newScrollY);
            }
            else{
                Timber.i("Not first time Updating, scrolling X to: " + oldScrollPositionX + " and scrolling Y to " + oldScrollPositionY);
                tableFixHeaders.scrollTo(oldScrollPositionX, oldScrollPositionY);
            }



        }
        //if no rooms
        else{
            rootView = inflater.inflate(R.layout.calendar_home_fragment, container, false);
            tableFixHeaders = (TableFixHeaders) rootView.findViewById(R.id.calendar_table);
            hListView = (it.sephiroth.android.library.widget.HListView) rootView.findViewById(R.id.roomsListHorizontal);
            sorryCartoonIV = (ImageView) rootView.findViewById(R.id.sorryCartoon);
            String[] roomsListArray = new String[mCalendarAdapter.getColumnCount()];
            for(int i = 0; i < roomsListArray.length; i++){
                roomsListArray[i] = arrayToUse[0][i+1];
                Timber.i("roomsList Entry " + i + ": " + roomsListArray[i]);
            }
            AdapterHorizontalRooms adapter = new AdapterHorizontalRooms(getActivity(), roomsListArray);
            hListView.setAdapter(adapter);
            hListView.setVisibility(View.VISIBLE);
            hListView.setSelector(new ColorDrawable(0x0));
            //hListView.getLayoutParams().width = roomsListArray.length * getResources().getDimensionPixelSize(R.dimen.table_width);
            sorryCartoonIV.setVisibility(View.VISIBLE);
            tableFixHeaders.setVisibility(View.INVISIBLE);


            *//*RelativeLayout.LayoutParams params =  (RelativeLayout.LayoutParams) tableFixHeaders.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);*//*

        }

*//*        if(hasRooms){
            int CAN_BE_ANY_NUMBER = 0;
            rootView = inflater.inflate(R.layout.calendar_home_fragment, container, false);
            tableFixHeaders = (TableFixHeaders) rootView.findViewById(R.id.calendar_table);
            tableFixHeaders.setAdapter(mCalendarAdapter);

            int cellHeight = mCalendarAdapter.getHeight(CAN_BE_ANY_NUMBER);
            int currentTimeCell = mCalendarAdapter.currentTimeCell;
            tableFixHeaders.scrollTo(0, cellHeight * currentTimeCell);

        }
        else{
            rootView = inflater.inflate(R.layout.guidelines_policies, container, false);
        }*//*


		return rootView;

	}


	public Calendar_Generic_Page_Fragment newInstance(int pageNumberInt, AppCompatActivity mActivity){

		Calendar_Generic_Page_Fragment fragment = new Calendar_Generic_Page_Fragment();
		fragment.pageNumberInt = pageNumberInt;
		fragment.pageNumberStr = String.valueOf(pageNumberInt);
		fragment.mActivity = mActivity;
		//Log.i("CalendarGeneric", "First I'm " + fragment.pageNumberStr);
		return fragment;
	}
	public Calendar_Generic_Page_Fragment newInstance(int pageNumberInt, AppCompatActivity mActivity, ArrayList<CalendarMonth> calendarCache){
		
		Calendar_Generic_Page_Fragment fragment = new Calendar_Generic_Page_Fragment();
		fragment.calendarCache = calendarCache;
		fragment.pageNumberInt = pageNumberInt;
		fragment.pageNumberStr = String.valueOf(pageNumberInt);
		fragment.mActivity = mActivity;
		//Log.i("CalendarGeneric", "First I'm " + fragment.pageNumberStr);
		return fragment;
	}
    static class ViewHolder{
        TextView onlyTextView;
    }
	public class CalendarAdapter extends FixedTableAdapter {
		public int currentTimeCell = 6;
		private String TAG = "CalendarAdapter";
		private int numberOfItems;
		private final int width;
		private final int height;
		private String[][] calendarData;
		private int columnCount = -1;
        private final static int OFFSET_FROM_TOP = 3;


        private final static String REGEX_FOR_TIME = "^(?:\\d|[01]\\d|2[0-3]):[0-5]\\d (PM|AM)";

		public CalendarAdapter(Context context) {
			super(context);
            //Getting the hour of the day


            //No current info, using DB to update
            if(calendarCache == null){
				try{
                    SimpleDateFormat sdf = new SimpleDateFormat("h");
                    String hourOfDay = sdf.format(new Date());

                    Timber.i("Hour of day right now is " + hourOfDay);
					String day = "day" + String.valueOf(pageNumberInt + 1);
*//*					SQLiteDatabase db = MainActivity.mdbHelper.getReadableDatabase();
					Cursor c = db.query(MainActivity.mdbHelper.CALENDAR_TABLE_NAME, new String[]{day, day+"source"}, null, null, null, null, null);*//*

                    //There's something in the DB
					if(c.moveToFirst()){
						Timber.i("CalendarData == null, first time creating TableFixedHeaders View, starting database Parse..");
						//Log.i(TAG, "count = " + String.valueOf(c.getCount()-4));
						int i = 0;
						c.moveToPosition(3);
						numberOfItems = Integer.parseInt(c.getString(c.getColumnIndex(day)));
						Timber.i("Number of items : " + numberOfItems);
						
						c.moveToNext();
						columnCount = Integer.parseInt(c.getString(c.getColumnIndex(day)));
                        Timber.i("Column Count " + columnCount);
						calendarData = new String[numberOfItems/columnCount][columnCount];

						correspondingArr = new String[numberOfItems/columnCount][columnCount];
						calendarData[(i/columnCount)][(i%columnCount)] = c.getString(c.getColumnIndex(day));
                        Timber.v("Calendar Data - Entry [0][0]: " + calendarData[0][0] + " .Also the column count, will get changed to nothing on getView()");
                        String currentStringFromDb; // = c.getString(c.getColumnIndex(day+"source"));
                        //changing it to "" because the first entry is the event validation not an actual entry
                        //Real entries start after this one
                        correspondingArr[(i/columnCount)][(i%columnCount)] = "";
                        //Replace any invalid entries from null to ""
                        *//*if(currentStringFromDb == null){

                        }
                        else{
                            correspondingArr[(i/columnCount)][(i%columnCount)] = currentStringFromDb;
                        }*//*

						i++;
						while(c.moveToNext()){
							
							//Log.i(TAG, c.getString(c.getColumnIndex(day)));
                            currentStringFromDb = c.getString(c.getColumnIndex(day));
							calendarData[(i/columnCount)][(i%columnCount)] = currentStringFromDb;
                            Timber.v("Calendar Data - Entry [" + i/columnCount + "][" + i%columnCount + "]: " + calendarData[(i/columnCount)][(i%columnCount)]);
                            //Use the Regex to find all time styles
                            if(currentStringFromDb.matches(REGEX_FOR_TIME)){
                                Timber.v("Matched: " + currentStringFromDb + " to the Regex: " + REGEX_FOR_TIME);
                                //The string splitting is finding the hour from the string expected 11:42 PM
                                //Splits the : then takes the 0th element
                                if(currentStringFromDb.split(":")[0].equalsIgnoreCase(hourOfDay)){
                                    currentTimeCell = (i/columnCount) - OFFSET_FROM_TOP;
                                    Timber.v("The closest hour " + hourOfDay + " was matched to " + currentStringFromDb + " and is row " + currentTimeCell);
                                }
                            }
                            else{
                                //Timber.i("Didn't match --" + currentStringFromDb + "--");
                            }
                            Timber.v("The closest hour " + hourOfDay + " was matched to row " + currentTimeCell);
                            currentStringFromDb = c.getString(c.getColumnIndex(day+"source"));
                            //Replace any invalid entries from null to ""
                            if(currentStringFromDb == null){
                                correspondingArr[(i/columnCount)][(i%columnCount)] = "";
                            }
                            else{
                                correspondingArr[(i/columnCount)][(i%columnCount)] = currentStringFromDb;
                            }



							i++;
						}
						//Log.i(TAG, "count = " + String.valueOf(i-1));
						arrayToUse = calendarData;
						hasRooms = true;
					}
					else{
						//arrayToUse = numbers;
					}
					c.close();
				}catch(Exception e){
					e.printStackTrace();
					//arrayToUse = numbers;
				}
			}
            //Not the first time updating, using the current value available
			else{
                Timber.i("CalendarData != null, Not the first time creating TableFixedHeaders View, using current CalendarData Entries");
                Timber.i("Becauese its not the first time updating, will set currentTimeCell to -1 and use the getActualScroll to reset the page position");
				try{
					//Log.i(TAG, "EUREKA!!!! NOW DELETE ME");
					numberOfItems = calendarCache.get(pageNumberInt).dataLength;
                    Timber.i("Number of Items: " + numberOfItems);
					columnCount = calendarCache.get(pageNumberInt).columnCount;
                    Timber.i("Column Count: " + columnCount);
					calendarData = new String[numberOfItems/columnCount][columnCount];
					correspondingArr = new String[numberOfItems/columnCount][columnCount];
					for(int i = 0; i < calendarCache.get(pageNumberInt).data.length; i ++){
						calendarData[(i/columnCount)][(i%columnCount)] = calendarCache.get(pageNumberInt).data[i];
                        currentTimeCell = -1;



						correspondingArr[(i/columnCount)][(i%columnCount)] = calendarCache.get(pageNumberInt).source[i];
					}
					arrayToUse = calendarData;

					hasRooms = true;
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}

			Resources resources = context.getResources();

			width = resources.getDimensionPixelSize(R.dimen.table_width);
			height = resources.getDimensionPixelSize(R.dimen.table_height);
            if(numberOfItems == columnCount){
                hasRooms = false;
            }

			
		}

		@Override
		public View getView(int row, int column, View recycleView, ViewGroup parent) {
			final int mRow = row+1;
			final int mColumn = column+1;
            Object span;
            boolean isLockIcon = false;

            /*//***************
            //ViewHolder Pattern
            ViewHolder holder;
            View convertView = recycleView;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.calendar_table_item_actual, parent, false);
                holder = new ViewHolder();
                holder.onlyTextView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
			}
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            //End ViewHolder Pattern
            /*//***************

			String content = getCellString(mRow,mColumn);

			//Make sure there's noething strange
            if (content==null){
				content = "";
			}
            //Timber.v("Incoming getView() string: " + content);
            if(content.equalsIgnoreCase("\"")) {
                isLockIcon = true;
                content = "";
                holder.onlyTextView.setBackgroundResource(R.drawable.ic_lock_closed_small);
            }
			else if(row == -1 && column == -1){
				content = "";
			}
            else{
                holder.onlyTextView.setBackgroundColor(0x00000000);
            }

            SpannableString cellSpanString = new SpannableString(content);

			if((row > -1) && (column > -1)){
                *//*if(column % 2 == 0){
                    convertView.setBackgroundResource(R.drawable.top_border_dark);
                }
                else if(column% 2 == 1){
                    convertView.setBackgroundResource(R.drawable.top_border_white);
                }*//*


                //Timber.v("mRow: " + mRow + ", mColumn: " + mColumn);
                if(correspondingArr[mRow][mColumn] == null){
                    //Timber.v("its NULL");
                    correspondingArr[mRow][mColumn] = "";
                }*//*
                else{
                    Timber.v("Contents: " + correspondingArr[mRow][mColumn]);
                }*//*

                //Contains Something that's clickable
				if(!correspondingArr[mRow][mColumn].isEmpty()){

                    //empty
                    if(correspondingArr[mRow][mColumn].equalsIgnoreCase("=book.aspx")){
                        convertView.setBackgroundResource(R.drawable.cell_book_selector);
                    }
                    //partial
                    else if(correspondingArr[mRow][mColumn].equalsIgnoreCase("=joinorleave")){
                        convertView.setBackgroundResource(R.drawable.cell_joinorleave_selector);
                    }
                    //booked
                    else if(correspondingArr[mRow][mColumn].equalsIgnoreCase("=viewleaveorjoin")){
                        convertView.setBackgroundResource(R.drawable.cell_viewleaveorjoin_selector);
                    }
                    //lock icon is before this is for non-empty

                    span = new StyleSpan(Typeface.BOLD);
					cellSpanString.setSpan(span, 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //Dialog for actual events present
					convertView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            //final TextView confirmationTextView = new TextView(mActivity);
                            final String linkString = correspondingArr[mRow][mColumn];
                            //final ImageView roomPic = new ImageView(mActivity);
                            int stateStart = linkString.indexOf("starttime=");
                            int stateEnd = linkString.indexOf("&", stateStart + 1);


                            String timeDiag = linkString.substring(stateStart + 10, stateEnd).replace("%20", " ");

                            stateStart = linkString.indexOf("room=");
                            stateEnd = linkString.indexOf("&", stateStart + 1);
                            String roomDiag = linkString.substring(stateStart + 5, stateEnd);
                            String diagTitle = roomDiag + " at " + timeDiag + "?";
                            RoomFragmentDialog roomFragDia = RoomFragmentDialog.newInstance(roomDiag, diagTitle
                                    , pageNumberInt
                                    , linkString
                                    , mRow
                                    , mColumn
                            );
                            FragmentManager fragMan = getChildFragmentManager();
                            roomFragDia.show(fragMan, null);

                        }

                    });
				}
                //Means it says "CLOSED" or has the lock icon
				else{
                    convertView.setOnClickListener(null);
                    if(isLockIcon){
                        convertView.setBackgroundResource(R.drawable.cell_lock_selector);
                    }
                    else{
                        convertView.setBackgroundResource(R.drawable.top_border_white);
                        span = new ForegroundColorSpan(Color.LTGRAY);
                        cellSpanString.setSpan(span, 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

					//holder.onlyTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
				}
				
			}
			else{
                if(row == -1 && column == -1){
                    convertView.setBackgroundResource(R.drawable.cell_corner_top_left);
                    convertView.setOnClickListener(null);
                }
                else if(row ==-1 && column > -1){

                    //Library Room cells (infoOnly)
                    convertView.setBackgroundResource(R.drawable.cell_rooms_selector);
                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String roomDiag = arrayToUse[mRow][mColumn];
                            String diagTitle = roomDiag;
                            RoomsInfoOnlyDialog roomFragDia = RoomsInfoOnlyDialog.newInstance(roomDiag, diagTitle);
                            FragmentManager fragMan = getChildFragmentManager();
                            roomFragDia.show(fragMan, null);


                        }
                    });
                    *//*if(column % 2 == 0){
                        convertView.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    }
                    else{
                        convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }*//*
                }
                else{
                    convertView.setBackgroundResource(R.drawable.top_border_dark);
                    convertView.setOnClickListener(null);
                    *//*if(column % 2 == 0){
                        convertView.setBackgroundResource(R.drawable.top_border_dark);
                    }
                    else{
                        convertView.setBackgroundResource(R.drawable.top_border_white);
                    }*//*
                }


				
			}
            holder.onlyTextView.setText(cellSpanString);
            //Timber.v("GetView Outgoing String: " + cellSpanString);
*//*			if(hasRooms == false){
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}*//*
			
			return convertView;
		}




		*//**
		 * Sets the text to the view.
		 * 
		 * @param view
		 * @param text
		 *//*
		private void setText(View view, String text) {
			((RobotoTextView) view.findViewById(android.R.id.text1)).setText(text);
		}
		@Override
		public int getRowCount() {
*//*			if(numberOfItems ==4){
				return 1;
			}
			else{
				return (numberOfItems/columnCount)-1;	
			}*//*
            int returnInt = (numberOfItems/columnCount)-1;
            //Timber.v("GetRowCount Returns: " + returnInt);
            return returnInt;

			
		}

		@Override
		public int getColumnCount() {
*//*
			if(numberOfItems == 4){
				return 1;
			}
			else{
				return columnCount-1;
			}
*//*
            return columnCount -1;
		}

		@Override
		public int getWidth(int column) {
			return width;
		}

		@Override
		public int getHeight(int row) {
			return height;
		}

		@Override
		public String getCellString(int row, int column) {
				return calendarData[row][column];
		}
		
		@Override
		public int getLayoutResource(int row, int column) {
			
			final int layoutResource;
			switch (getItemViewType(row, column)) {
				case 0:
					layoutResource = R.layout.item_table1_header;
				break;
				case 1:
					layoutResource = R.layout.item_table1;
				break;
				case 2:
					layoutResource = R.layout.calendar_table_item_actual;
				break;
				default:
					throw new RuntimeException("wtf?");
			}
			return layoutResource;
		}

		@Override
		public int getItemViewType(int row, int column) {
			if (row < 0) {
				return 0;
			}
			else if(column < 0){
				return 1;
			}
			else {
				return 2;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}
		
		public void selectPositionAt(int mRow, int mColumn){
			//row and column are using 0 as as the far top and far left column. 
			//Here's an example:
			//[0][0]  |  [0][1]
			//[0][1]  |  [1][1]
			
			final String linkString = correspondingArr[mRow][mColumn];
			//final ImageView roomPic = new ImageView(mActivity);
			int stateStart = linkString.indexOf("starttime=");
			int stateEnd = linkString.indexOf("&", stateStart+1);
			
			
			String timeDiag = linkString.substring(stateStart+10, stateEnd).replace("%20", " ");
			
			stateStart = linkString.indexOf("room=");
			stateEnd = linkString.indexOf("&", stateStart+1);
			String roomDiag = linkString.substring(stateStart+5, stateEnd);
			
			
			
			//confirmationTextView.setText("Room: " + roomDiag + " at " + timeDiag);
			
			*//*LinearLayout ll=new LinearLayout(mActivity);
		        ll.setOrientation(LinearLayout.VERTICAL);
		        ll.addView(roomPic);*//*
			RoomFragmentDialog roomFragDia = RoomFragmentDialog.newInstance(roomDiag, "Room: " + roomDiag + " at " + timeDiag + "?"
                    , pageNumberInt
                    , linkString
                    , mRow
                    , mColumn
            );
			FragmentManager fragMan = getChildFragmentManager();
			roomFragDia.show(fragMan, null);
		}
	}

    
    public static class RoomFragmentDialog extends DialogFragment{
        @Inject Tracker googleAnalyticsTracker;
    	private final String TAG = "RoomFragment";
    	ListView listView;
    	String roomNameString;
    	int pageNumberInt;
    	String diagTitle;
    	String linkString;
    	int shareRow;
    	int shareColumn;
        boolean isInfoOnly = false;
        boolean isMovingForward = false;
    	
    	
    	
    	public static RoomFragmentDialog newInstance(String room, String diagTitle, int pageNumberInt, String linkString, int shareRow, int shareColumn){
    		RoomFragmentDialog frag = new RoomFragmentDialog();
			frag.roomNameString = room;
			frag.shareRow = shareRow;
			frag.shareColumn = shareColumn;
			frag.diagTitle = diagTitle;
			frag.pageNumberInt = pageNumberInt;
			frag.linkString = linkString;
    		return frag;
    	}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        }

        @Override
        public void onStop() {
            MainActivity.isDialogShowing = false;
            Timber.i("RoomInfoFragmentDialog Stopped");
            Timber.v("isUserMovingForward = " + isMovingForward);
            super.onStop();
            if(!isMovingForward){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OttoBusSingleton.getInstance().post(new LinkedCalendarDialogsClosedEvent());
                    }

                }, 100);
            }
        }

        @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
                Timber.i("=================================================");
                if(isInfoOnly){

                    Timber.i("RoomInfoFragmentDialog Being Created, isInfoOnly = true");
                }
                else{
                    Timber.i("RoomInfoFragmentDialog Being Created, isInfoOnly = false");
                }
                Timber.v("Contains the following info:");
                Timber.v("roomNameString = " + roomNameString);
                Timber.v("shareRow = " + shareRow);
                Timber.v("shareColumn = " + shareColumn);
                Timber.v("diagTitle = " + diagTitle);
                Timber.v("pageNumberInt = " + pageNumberInt);
                Timber.v("linkString = " + linkString);

				View rootView = inflater.inflate(R.layout.room_landing_diag, container, false);
				
				//final BitmapFactory.Options options = new BitmapFactory.Options();
				final TextView roomName = (TextView) rootView.findViewById(R.id.room_landing_top_title);
				final TextView floorNumber = (TextView) rootView.findViewById(R.id.room_landing_floor_number);
				final TextView seatingCap = (TextView) rootView.findViewById(R.id.room_landing_seating_capacity);
				final TextView minBookers = (TextView) rootView.findViewById(R.id.room_landing_min_bookers);
				final TextView MaxTime = (TextView) rootView.findViewById(R.id.room_landing_max_time);
				final TextView comments = (TextView) rootView.findViewById(R.id.room_landing_comments);
				final ImageView libpic = (ImageView) rootView.findViewById(R.id.room_landing_room_picture);
				final Button negativeButton = (Button) rootView.findViewById(R.id.negative_button);
				final Button positiveButton = (Button) rootView.findViewById(R.id.positive_button);
				
				SQLiteDatabase db = MainActivity.mdbHelper.getReadableDatabase();
				Cursor c = db.query(MainActivity.mdbHelper.ROOMS_TABLE_NAME, null, null, null, null, null, null, null);
                boolean roomExists = false;
				while(roomExists == false && c.moveToNext()){
					//Log.i(TAG, c.getString(c.getColumnIndex(MainActivity.mdbHelper.ROOM_NAME)));
					if(c.getString(c.getColumnIndex(MainActivity.mdbHelper.ROOM_NAME)).equalsIgnoreCase(roomNameString)){

                        roomExists = true;
						
						//roomName.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.ROOM_NAME)));
						//roomName.setVisibility(View.GONE);
						floorNumber.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.FLOOR)));
						seatingCap.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.SEATING_CAP)) + " people");
						minBookers.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.MIN_BOOKERS)) + " people");
						MaxTime.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.MAX_TIME)));
						//Bitmap bm = LoadImageFromStorage(c.getString(c.getColumnIndex(MainActivity.mdbHelper.IMAGEFILE)));
						
						libpic.setImageResource(getResources().getIdentifier(roomNameString.toLowerCase(),"drawable",  getActivity().getPackageName()));
						
						
						comments.setText(c.getString(c.getColumnIndex(MainActivity.mdbHelper.COMMENT)));
					}
				}
                Timber.v("Room " + roomNameString + " exists: " + roomExists);
				c.close();
				rootView.setScrollbarFadingEnabled(false);
				roomName.setText(diagTitle);
				if(!roomExists){
                    floorNumber.setText("?");
                    seatingCap.setText("? people");
                    minBookers.setText("? people");
                    MaxTime.setText("? Hours");
                    comments.setText(R.string.error_room_no_exist_comment);
                    String errorDescript =  "Room: " + diagTitle + " was requested, but not found in the rooms database!";
                    Timber.e(new IllegalStateException(errorDescript), errorDescript);
                }

				if(isInfoOnly){
                    negativeButton.setText(android.R.string.ok);
                    positiveButton.setVisibility(View.GONE);
                    googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ActivityRoomInteraction")
                                    .setAction("RoomInfoOnly Opened")
                                    .setLabel(diagTitle)
                                    .build()
                    );
                }
                else{

                    googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ActivityRoomInteraction")
                                    .setAction("RoomDialog Opened")
                                    .setLabel(diagTitle)
                                    .build()
                    );
                    positiveButton.setOnClickListener(new OnClickListener(){

                        @Override
                        public void onClick(View arg0) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            if(isNetworkAvailable(getActivity())){
                                //If there's a valid username/password combo
                                if(sharedPreferences.getString(USER_USERNAME, null) != null &&
                                        sharedPreferences.getString(USER_PASSWORD, null) != null &&
                                        sharedPreferences.getString(USER_INSTITUTION, null) != null){
                                    getDialog().dismiss();
                                    isMovingForward = true;
                                    new AsyncRoomInteraction(getActivity(), pageNumberInt, shareRow, shareColumn).execute(linkString);
                                }
                                else{
                                    Toast.makeText(getActivity(), R.string.error_please_log_in, Toast.LENGTH_LONG).show();
                                    //((MainActivity) getActivity()).displayMyAccountHint();
                                }


                            }
                            else{
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Connectivity Issue")
                                        .setMessage("Couldn't access the internet, try double checking the internet settings and try again.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        }
                        public boolean isNetworkAvailable(Context ctx){
                            ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = cm.getActiveNetworkInfo();
                            if (netInfo != null && netInfo.isConnectedOrConnecting()&& cm.getActiveNetworkInfo().isAvailable()&& cm.getActiveNetworkInfo().isConnected())
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }

                    });
                }

				negativeButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						getDialog().dismiss();
						
						
					}
					
				});


            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            if(isInfoOnly){
                getDialog().getWindow()
                        .getAttributes().windowAnimations = R.style.ActionBarIconDialogAnimation;
            }
            else{
                getDialog().getWindow()
                        .getAttributes().windowAnimations = R.style.DialogAnimation;
            }
            MainActivity.isDialogShowing = true;
			return rootView;
		}

    }

    public static class RoomsInfoOnlyDialog extends RoomFragmentDialog{

        public static RoomsInfoOnlyDialog newInstance(String room, String diagTitle){
            RoomsInfoOnlyDialog frag = new RoomsInfoOnlyDialog();
            int DUMMYVALUE = 1;
            String DUMMYSTRING = "thisisastring";
            frag.roomNameString = room;
            frag.shareRow = DUMMYVALUE;
            frag.shareColumn = DUMMYVALUE;
            frag.diagTitle = diagTitle;
            frag.pageNumberInt = DUMMYVALUE;
            frag.linkString = DUMMYSTRING;
            frag.isInfoOnly = true;
            return frag;
        }
    }
    public class AdapterHorizontalRooms extends ArrayAdapter<String> {
        String[] data;
        Context mContext;
        ViewHolder holder;
        public AdapterHorizontalRooms(Context context, String[] objects) {
            super(context, R.layout.calendar_table_item_actual, objects);
            this.data = objects;
            this.mContext = context;

        }

        @Override
        public View getView(int position, View recycleView, ViewGroup parent) {
            final String content = data[position];
            View convertView = recycleView;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_table_item_actual, parent, false);
                holder = new ViewHolder();
                holder.textViewOnly= (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textViewOnly.setText(content);
            convertView.setBackgroundResource(R.drawable.cell_rooms_selector);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String roomDiag = content;
                    String diagTitle = roomDiag;
                    Calendar_Generic_Page_Fragment.RoomsInfoOnlyDialog roomFragDia = Calendar_Generic_Page_Fragment.RoomsInfoOnlyDialog.newInstance(roomDiag, diagTitle);
                    FragmentManager fragMan = getChildFragmentManager();
                    roomFragDia.show(fragMan, null);


                }
            });
            return convertView;
        }

        class ViewHolder{
            TextView textViewOnly;
        }
    }*/

}
