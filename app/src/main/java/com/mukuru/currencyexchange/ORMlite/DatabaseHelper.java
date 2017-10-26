package com.mukuru.currencyexchange.ORMlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyObject;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String PREFS_NAME = "CurrencyExchanfwPrefs";
	private static final String DATABASE_NAME = "currency_exchange.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, CurrencyObject.class);
			TableUtils.createTableIfNotExists(connectionSource, CurrencyDetail.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, CurrencyObject.class, true);
			TableUtils.dropTable(connectionSource, CurrencyDetail.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
	}
}
