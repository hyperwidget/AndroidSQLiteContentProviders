package cs.ecl.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class CoursesProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "cs.ecl.provider.Courses";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/courses");

	public static final String _ID = "_id";
	public static final String DESC = "desc";
	public static final String CODE = "code";
	public static final String ROOM = "room";

	private static final int COURSES = 1;
	private static final int COURSE_ID = 2;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "courses", COURSES);
		uriMatcher.addURI(PROVIDER_NAME, "courses/#", COURSE_ID);
	}

	// for using SQLite database
	private SQLiteDatabase coursesDB;
	private static final String DATABASE_NAME = "Courses";
	private static final String DATABASE_TABLE = "descs";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table "
			// + DATABASE_TABLE + " (_id integer primary key autoincrement, "
			+ DATABASE_TABLE + " (_id integer primary key, "
			+ "desc text not null, code text not null, room text null);";

	// for using SQLite database
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS descs");
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri arg0/* uri */, String arg1/* selection */, String[] arg2/* delectionArgs */) {
		int count = 0;
		switch (uriMatcher.match(arg0)) {
		case COURSES:
			count = coursesDB.delete(DATABASE_TABLE, arg1, arg2);
			break;
		case COURSE_ID:
			String id = arg0.getPathSegments().get(1);
			count = coursesDB.delete(DATABASE_TABLE, _ID + " = " + id
					+ (!TextUtils.isEmpty(arg1) ? " AND (" + arg1 + ')' : ""),
					arg2);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		getContext().getContentResolver().notifyChange(arg0, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		// get all courses
		case COURSES:
			return "vnd.android.cursor.dir/vnd.ecl.courses ";
			// get one course
		case COURSE_ID:
			return "vnd.android.cursor.item/vnd.ecl.courses ";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// add a new course
		long rowID = coursesDB.insert(DATABASE_TABLE, "", values);

		// if added successfully
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		coursesDB = dbHelper.getWritableDatabase();
		return (coursesDB == null) ? false : true;

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DATABASE_TABLE);

		if (uriMatcher.match(uri) == COURSE_ID)
			// -- if getting one course --
			sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));

		if (sortOrder == null || sortOrder == "")
			sortOrder = DESC;

		Cursor cl = sqlBuilder.query(coursesDB, projection, selection,
				selectionArgs, null, null, sortOrder);

		// register to watch a content URI for changes
		cl.setNotificationUri(getContext().getContentResolver(), uri);
		return cl;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case COURSES:
			count = coursesDB.update(DATABASE_TABLE, values, selection,
					selectionArgs);
			break;
		case COURSE_ID:
			count = coursesDB.update(
					DATABASE_TABLE,
					values,
					_ID
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
