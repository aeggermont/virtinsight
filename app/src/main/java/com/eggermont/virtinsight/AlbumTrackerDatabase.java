package com.eggermont.virtinsight;

import android.content.Context;
import android.provider.BaseColumns;


/**
 * This class is used to keep track of the different table schemas
 * and column names for the database
 */

public final class AlbumTrackerDatabase {

	private Context mContext;
	private AlbumTrackerDatabase(Context c) {
		mContext = mContext;
	}


	// Albums table definitions
	public static final class VirtAlbums implements BaseColumns{
		private VirtAlbums() {}

		public static final String ALBUMS_TABLE_NAME = "tbl_albums";
		public static final String ALBUM_TITLE_NAME = "tbl_album_title";
		public static final String ALBUM_DESCRIPTION = "tbl_album_description";
		public static final String ALBUM_DATE_ADDED = "tbl_album_dateadded";
		public static final String DEFAULT_SORT_ORDER = "tbl_album_title DESC";

	}

	public static final class AlbumEvents implements BaseColumns{
		private AlbumEvents() {}

		public static final String ALBUMEVENTS_TABLE_NAME = "tbl_album_events";
		public static final String ALBUMEVENTS_ALBUM_ID = "tbl_album_album_id";
		public static final String ALBUMEVENTS_AUDIO_SPEECH = "tbl_album_event_speech";
		public static final String ALBUMEVENTS_EVENT_GPS = "tbl_album_event_gps";
		public static final String ALBUMEVENTS_DATE_ADDED = "tbl_album_event_date_added";
		public static final String ALBUMEVENTS_PHOTO_LINK = "tbl_album_event_photo_link";
		public static final String ALBUMEVENTS_AUDIO_LINK = "tbl_album_event_audio_link";
		public static final String DEFAULT_SORT_ORDER = "tbl_album_album_id ASC";
	}
}