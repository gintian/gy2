/*
 * 创建日期 2005-6-24
 *
 */
package com.hjsj.hrms.transaction.performance;

/**
 * @author luangaojiong
 *
 * 标度表对象Bean
 */
public class PerGradeBean {
	
	int grade_id=0;			//标度号
	String point_id="0";			//要素号
	double gradevalue=0;	//等级值
	String gradedesc="";	//标度内容
	String gradecode="";	//标度代码
	double top_value=0;		//上限值
	double bottom_value=0;	//下限值

	
	public void setGradecode(String gradecode)
	{
		this.gradecode=gradecode;
	}
	
	public String getGradecode()
	{
		return this.gradecode;
	}
	public void setGrade_id(int grade_id)
	{
		this.grade_id=grade_id;
	}
	
	public int getGrade_id()
	{
		return this.grade_id;
	}
	
	public void setPoint_id(String point_id)
	{
		this.point_id=point_id;
	}
	
	public String getPoint_id()
	{
		return this.point_id;
	}
	
	public void setGradevalue(double gradevalue)
	{
		this.gradevalue=gradevalue;
	}
	
	public double getGradevalue()
	{
		return this.gradevalue;
	}
	
	public void setGradedesc(String gradedesc)
	{
		this.gradedesc=gradedesc;
	}
	
	public String getGradedesc()
	{
		return this.gradedesc;
	}
	public void setTop_value(double top_value)
	{
		this.top_value=top_value;
	}
	
	public double getTop_value()
	{
		return this.top_value;
	}
	
	
	public void setBottom_value(double bottom_value)
	{
		this.bottom_value=bottom_value;
	}
	
	
	public double getBottom_value()
	{
		return this.bottom_value;
	}
	
}
