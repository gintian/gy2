package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 *  新建预算交易类
 * <p>Title:AddBudgetTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 24, 2012 5:11:37 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
public class AddBudgetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			ArrayList yearNumlist=new ArrayList();//预算年份容器
			ArrayList budgetTypelist=new ArrayList();//预算类别
			ArrayList firstMonthlist=new ArrayList();//开始月份
			/*
			 * 获得自系统年份起的年份列
			 */
			for(int i=0;i<5;i++){
				Date d = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy");//获得系统年份
				String da=sdf.format(d).toString();
				int a=Integer.parseInt(da)+i;
				String m=String.valueOf(a);
				CommonData obj=new CommonData(m,m+ResourceFactory.getProperty("datestyle.year"));//前台展示 值，后台传输 键
				yearNumlist.add(obj);
			}
			
			/*
			 * 设置预算类别
			 */
			for(int i=1;i<4;i++){
				String str="";
				if(i==1){
					str=ResourceFactory.getProperty("gz.budget.budgeting.yearc");
				}
				if(i==2){
					str=ResourceFactory.getProperty("gz.budget.budgeting.yearz");
					
				}
				if(i==3){
					str=ResourceFactory.getProperty("gz.budget.budgeting.tbtz");
				}
				String m=String.valueOf(i);
				CommonData obj=new CommonData(m,str);
				budgetTypelist.add(obj);
			}
			/*
			 * 设置起始月份
			 */
			for(int ii=1;ii<13;ii++){
				String m=String.valueOf(ii);
				CommonData obj=new CommonData(m,m+ResourceFactory.getProperty("datestyle.month"));
				firstMonthlist.add(obj);
			}
			  this.getFormHM().put("yearNumlist", yearNumlist);
			  this.getFormHM().put("budgetTypelist", budgetTypelist);
			  this.getFormHM().put("firstMonthlist", firstMonthlist);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}

