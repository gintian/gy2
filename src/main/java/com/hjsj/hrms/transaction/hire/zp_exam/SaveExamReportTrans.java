/*
 * Created on 2005-9-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveExamReportTrans</p>
 * <p>Description:保存录入的成绩</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveExamReportTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		  HashMap hm=(HashMap)this.getFormHM();
		  ArrayList formslist=(ArrayList)hm.get("forms");
		  String columns=(String)hm.get("columns");
		  int records=getRecords(formslist);
		  String[] fields=(String[])columns.split(",");
		  ArrayList sumscorelist=new ArrayList();                 /*核算保存总分数list压回页面*/        
		  try{
			  if(records>0)
			  {
			  	String[] recordsupdatestr=new String[records];    /*生成各个记录的修改sql*/
			  	float[] sumscores=new float[records];                 /*核算保存总分数*/
			  	for(int i=0;i<recordsupdatestr.length;i++)        
			  	{
    		  	    recordsupdatestr[i]="update zp_exam_report set ";	
			  	}
			  	/*各考试项的子句*/
			  	for(int i=1;i<formslist.size();i++)
			  	{
			  		ArrayList fieldrecordlist=(ArrayList)formslist.get(i);
			  		for(int j=0;j<fieldrecordlist.size();j++)
			  		{
			  			//if(i!=formslist.size()-2)
			  			//{
			  				recordsupdatestr[j]+=fields[i+1];
			  				recordsupdatestr[j]+="=";
			  				if(fieldrecordlist.get(j)!=null && fieldrecordlist.get(j).toString().length()>0)
			  			    	recordsupdatestr[j]+=fieldrecordlist.get(j);
			  				else
			  					recordsupdatestr[j]+="0";
			  				recordsupdatestr[j]+=",";
			  				if(fieldrecordlist.get(j)!=null && fieldrecordlist.get(j).toString().length()>0)
			  					sumscores[j]+=Float.parseFloat(fieldrecordlist.get(j).toString());
			  				else
			  					sumscores[j]+=0;
			  			/*}
			  			else
			  			{
			  				recordsupdatestr[j]+=fields[i+1];
			  				recordsupdatestr[j]+="=";
			  				recordsupdatestr[j]+=fieldrecordlist.get(j);
			  				if(fieldrecordlist.get(j)!=null)
			  					sumscores[j]+=Float.parseFloat(fieldrecordlist.get(j).toString());
		  			    }*/
			  		}
			  	}
			  	ArrayList fieldsrecordidvaluelist=(ArrayList)formslist.get(0);
			  	/*where和总分子句*/
			  	for(int i=0;i<fieldsrecordidvaluelist.size();i++)          
			  	{	
			  		recordsupdatestr[i]+="sum_score=";
			  		for(int j=1;j<formslist.size();j++)
				  	{
			  			if(j!=formslist.size()-1)
			  			 recordsupdatestr[i]+=fields[j+1] + "+";
			  			else
			  			 recordsupdatestr[i]+=fields[j+1];
			  		}
			  	  recordsupdatestr[i]+=" where a0100='";
			      recordsupdatestr[i]+=fieldsrecordidvaluelist.get(i).toString();
			  	  recordsupdatestr[i]+="'";
			  	}
			  	ContentDAO dao=new ContentDAO(this.getFrameconn());
			  	for(int i=0;i<recordsupdatestr.length;i++)
			  	{
			  		System.out.println(recordsupdatestr[i]);
			  		dao.update(recordsupdatestr[i]);
			  	}
			    for(int i=0;i<sumscores.length;i++)
				{
			    	sumscorelist.add(String.valueOf(sumscores[i]));
				}
			  }			
		  }catch(Exception e)
		  {
		  	e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		  }
		  this.getFormHM().put("sumscorelist",sumscorelist);		
	}
	private int getRecords(ArrayList formslist)
	{
		if(formslist!=null && formslist.size()>0)
			return ((ArrayList)formslist.get(0)).size();
		else
			return 0;
	}
}
