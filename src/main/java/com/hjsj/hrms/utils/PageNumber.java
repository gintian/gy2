package com.hjsj.hrms.utils;	//分页bean
public class PageNumber 
{
	int rowCount=1,		//总的记录数
		pageSize=1,		//每页显示的记录数
		showPage=1,		//设置预显示的页码数
		pageCount=1;	//分页之后的总页数
	
	public void setRowCount(int n){
		rowCount=n;
	}
	public int getRowCount(){
		return rowCount;
	}
	public void setPageCount(int r,int p){
		rowCount=r;
		pageSize=p;
		pageCount=(rowCount%pageSize)==0?(rowCount/pageSize):(rowCount/pageSize+1);
		//计算分页之后的总页数
	}
	public int getPageCount(){
		return pageCount;
	}
	public void setShowPage(int n){
		showPage=n;
	}
	public int getShowPage(){
		return showPage;
	}
	public void setPageSize(int n){
		pageSize=n;
	}
	public int getPageSize(){
		return pageSize;
	}
}
