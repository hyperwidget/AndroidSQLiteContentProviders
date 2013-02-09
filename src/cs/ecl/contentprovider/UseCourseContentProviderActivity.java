package cs.ecl.contentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class UseCourseContentProviderActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_use_content_provider);

		// add the 1st course
		ContentValues values = new ContentValues();
		values.put(CoursesProvider.DESC,
				"Enterprise Development with Java and EJB");
		values.put(CoursesProvider.CODE, "EJB605");
		values.put(CoursesProvider.ROOM, "T2110");
		Uri uri = getContentResolver().insert(CoursesProvider.CONTENT_URI,
				values);

		// add the 2nd course
		values.clear();
		values.put(CoursesProvider._ID, 2);
		values.put(CoursesProvider.DESC, "Introduction to Eclipse Development");
		values.put(CoursesProvider.CODE, "ECL500");
		values.put(CoursesProvider.ROOM, "S2152");
		uri = getContentResolver().insert(CoursesProvider.CONTENT_URI, values);

		// add the 3rd course
		values.clear();
		values.put(CoursesProvider._ID, 3);
		values.put(CoursesProvider.DESC,
				"Introduction to Java for C++ Programmers");
		values.put(CoursesProvider.CODE, "JAC444");
		values.put(CoursesProvider.ROOM, "T2108");
		// this way can be used for being accessed from different packages
		uri = getContentResolver().insert(
				Uri.parse("content://cs.ecl.provider.Courses/courses"), values);

		// query added courses
		Toast.makeText(this, "Added Courses:", Toast.LENGTH_SHORT).show();
		Uri allDescs = Uri.parse("content://cs.ecl.provider.Courses/courses");
		Cursor cl = managedQuery(allDescs, null, null, null, "desc");
		if (cl.moveToFirst()) {
			do {
				Toast.makeText(
						this,
						cl.getString(cl.getColumnIndex(CoursesProvider._ID))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.DESC))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.CODE))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.ROOM)),
						Toast.LENGTH_LONG).show();
			} while (cl.moveToNext());
		}

		// update a courses's details - call the update() method using a
		// content URI that indicates the courses's ID:
		ContentValues editedValues = new ContentValues();
		editedValues.put(CoursesProvider.CODE, "DPS914");
		editedValues.put(CoursesProvider.ROOM, "T2108");
		getContentResolver().update(
				Uri.parse("content://cs.ecl.provider.Courses/courses/2"),
				editedValues, null, null);

		getContentResolver().delete(
				Uri.parse("content://cs.ecl.provider.Courses/courses/3"), null,
				null);

		// query courses after deleting and updating
		Toast.makeText(this, "Courses after deleting and updating:",
				Toast.LENGTH_SHORT).show();
		allDescs = Uri.parse("content://cs.ecl.provider.Courses/courses");
		cl = managedQuery(allDescs, null, null, null, "desc");
		if (cl.moveToFirst()) {
			do {
				Toast.makeText(
						this,
						cl.getString(cl.getColumnIndex(CoursesProvider._ID))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.DESC))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.CODE))
								+ ", "
								+ cl.getString(cl
										.getColumnIndex(CoursesProvider.ROOM)),
						Toast.LENGTH_LONG).show();
			} while (cl.moveToNext());
		}

		// delete all courses, simply omit the course's ID in your content URI:
		getContentResolver().delete(
				Uri.parse("content://cs.ecl.provider.Courses/courses"), null,
				null);
	}
}