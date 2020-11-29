package com.hjsj.hrms.utils.analyse;

public class YearMonthCount {

	private int year;

	private int month;
	
	private int day=1;

	private int count;

	public YearMonthCount(int year, int month, int count) {
		super();
		this.year = year;
		this.month = month;
		this.count = count;
	}

	public YearMonthCount(int year, int month, int day,int count) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.count=count;
	}
	
	public String getDate()
	{
		return year+"-"+month+"-"+day;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
