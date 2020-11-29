package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
/**
 * 
 *<p>Title:个别计算</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class PersonalComputeGzTrans extends IBusiness {

	public void execute() throws GeneralException {
//		HashMap hm=this.getFormHM();
//		String name=(String)hm.get("gz_table_table");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String selectGzRecords=(String)this.getFormHM().get("selectGzRecords");
		String itemids=(String)hm.get("itemids");
//		ArrayList list=(ArrayList)hm.get("gz_table_record");
		/* 薪资发放/个别计算，计算不出来值，后台报越界错误 xiaoyun 2014-9-29 start */
		//String[] temps=selectGzRecords.split("#");
		String[] temps = PubFunc.keyWord_reback(selectGzRecords).split("#");
		/* 薪资发放/个别计算，计算不出来值，后台报越界错误 xiaoyun 2014-9-29 end */
		try
		{
			String name=(String)this.getFormHM().get("tablename");
			StringBuffer whl=new StringBuffer("");
		/*    if(list!=null&&list.size()>0)
		    {
		    	for(int i=0;i<list.size();i++)
		    	{
		    		RecordVo vo=(RecordVo)list.get(i);
		    		String nbase=vo.getString("nbase");
		    		String a0100=vo.getString("a0100");
		    		Date a00z0=vo.getDate("a00z0");
		    		Calendar d=Calendar.getInstance();
		    		d.setTime(a00z0);
		    		int year=d.get(Calendar.YEAR);
		    		int month=d.get(Calendar.MONTH)+1;
		    		int  a00z1=vo.getInt("a00z1");
		    	
		    		whl.append(" or ("+name+".a0100='"+a0100+"' and "+Sql_switcher.year(name+".a00z0")+"="+year+" and "+Sql_switcher.month(name+".a00z0")+"="+month+" and "+name+".a00z1="+a00z1+" and "+name+".nbase='"+nbase+"'  )");
		    	}
		    }*/
			for(int i=0;i<temps.length;i++)
			{
				String[] temp=temps[i].split("/");
				long d=Long.parseLong(temp[2]);
				Calendar dd=Calendar.getInstance();
				dd.setTimeInMillis(d);
				int year=dd.get(Calendar.YEAR);
	    		int month=dd.get(Calendar.MONTH)+1;
				
				whl.append(" or ("+name+".a0100='"+temp[0]+"' and "+Sql_switcher.year(name+".a00z0")+"="+year+" and "+Sql_switcher.month(name+".a00z0")+"="+month+" and "+name+".a00z1="+temp[3]+" and upper("+name+".nbase)='"+temp[1].toUpperCase()+"'  )");
			}
			
		    String strwhere=" where  ( "+whl.substring(3)+" )";
		    int index=name.lastIndexOf("_");
			String salaryid=name.substring(index+1);	
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/**需要审批*/ 
			if(gzbo.isApprove())
				strwhere+=" and sp_flag in('01','04','07')";
			else {
                //参数“允许修改发放结束已提交数据” 控制已提交的数据是否能计算  wangrd  2013-11-21
                if (!gzbo.isAllowEditSubdata()) {
                   strwhere+=" and  sp_flag in ('01','07')";  
                }   
			}
			
			ArrayList itemidList=new ArrayList();
			String[] str_arry=itemids.split(",");
			for(int i=0;i<str_arry.length;i++)
			{
				if(str_arry[i]!=null&&str_arry[i].trim().length()>0)
					itemidList.add(str_arry[i]);
			}
			
			gzbo.computing(strwhere,itemidList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
