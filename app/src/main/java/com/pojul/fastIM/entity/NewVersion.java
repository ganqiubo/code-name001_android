package com.pojul.fastIM.entity;

import java.sql.ResultSet;

public class NewVersion extends BaseEntity{

	private String versionCode;
	private String note;

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public void setBySql(ResultSet rs) {
		// TODO Auto-generated method stub
		super.setBySql(rs);
		if(rs == null) {
			return;
		}
		versionCode = getString(rs, "version_code");
		note = getString(rs, "note");
	}
	
	
	
}
