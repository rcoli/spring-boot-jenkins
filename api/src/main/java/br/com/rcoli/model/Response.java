package br.com.rcoli.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Response implements Serializable {
	
	private static final long serialVersionUID = -6669438424821208880L;

	private String myString;
	
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
	private Date myDate;
	
	private Boolean myBoolean;
	
	private Integer myInteger;

	public Response() {
	}

	public String getMyString() {
		return myString;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}

	public Date getMyDate() {
		return myDate;
	}

	public void setMyDate(Date myDate) {
		this.myDate = myDate;
	}

	public Boolean getMyBoolean() {
		return myBoolean;
	}

	public void setMyBoolean(Boolean myBoolean) {
		this.myBoolean = myBoolean;
	}

	public Integer getMyInteger() {
		return myInteger;
	}

	public void setMyInteger(Integer myInteger) {
		this.myInteger = myInteger;
	}

	@Override
	public String toString() {
		return "Response [myString=" + myString + ", myDate=" + myDate + ", myBoolean=" + myBoolean + ", myInteger=" + myInteger + "]";
	}


}
