package com.pojul.fastIM.entity;

import android.database.Cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.utils.FileClassUtil;
import com.pojul.objectsocket.utils.LogUtil;
import com.pojul.objectsocket.constant.StorageType;

public class BaseEntity{

	public void setBySql(ResultSet rs) {
	}

	public int getInt(ResultSet rs, String columnName) {
		try {
			int columnIndex = rs.findColumn(columnName);
			if (columnIndex > 0) {
				return rs.getInt(columnIndex);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.d(this.getClass().getName(), e.toString());
		}
		return 0;
	}

	public String getString(ResultSet rs, String columnName) {
		try {
			int columnIndex = rs.findColumn(columnName);
			if (columnIndex > 0) {
				return rs.getString(columnIndex);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.d(this.getClass().getName(), e.toString());
		}
		return null;
	}

	public StringFile getStringFile(ResultSet rs, String columnName) {
		try {
			int columnIndex = rs.findColumn(columnName);
			if (columnIndex > 0 && rs.getString(columnIndex) != null) {
				StringFile mStringFile = new StringFile(StorageType.SERVER);
				mStringFile.setFilePath(rs.getString(columnIndex));
				return mStringFile;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.d(this.getClass().getName(), e.toString());
		}
		return null;
	}

	public void setBySql(Cursor cursor) {
	}

	public int getInt(Cursor cursor, String columnName) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex != -1) {
			return cursor.getInt(columnIndex);
		}
		return 0;
	}

	public String getString(Cursor cursor, String columnName) {
		try {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex >= 0 && !"null".equals(cursor.getString(columnIndex))) {
				return cursor.getString(columnIndex);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.d(this.getClass().getName(), e.toString());
		}
		return null;
	}

	public Long getLong(Cursor cursor, String columnName) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex != -1) {
			return cursor.getLong(columnIndex);
		}
		return 0L;
	}

	public Double getDouble(Cursor cursor, String columnName) {
		try{
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex != -1 && !"null".equals(cursor.getString(columnIndex))) {
				double doubleVal = Double.parseDouble(cursor.getString(columnIndex));
				return doubleVal;
			}
		}catch (Exception e){
			return 0d;
		}
		return 0d;
	}

	public StringFile getStringFile(Cursor cursor, String columnName) {
		try {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex >= 0 && cursor.getString(columnIndex) != null && !"null".equals(cursor.getString(columnIndex))) {
				String path = cursor.getString(columnIndex);
				StringFile mStringFile = new StringFile(StorageType.SERVER);
				if(!FileClassUtil.isHttpUrl(path)){
					mStringFile.setStorageType(StorageType.LOCAL);
					mStringFile.setFileName(FileClassUtil.getFileName(path, StorageType.LOCAL));
				}
				mStringFile.setFilePath(path);
				return mStringFile;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.d(this.getClass().getName(), e.toString());
		}
		return null;
	}

}
