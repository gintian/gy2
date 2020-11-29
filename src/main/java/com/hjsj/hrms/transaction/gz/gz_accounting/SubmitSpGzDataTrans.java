package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/**
 * 
 * 
 *<p>Title:SubmitSpGzDataTrans.java</p> 
 *<p>Description:提交薪资审批中的数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 11, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SubmitSpGzDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String bosdate=(String)this.getFormHM().get("bosdate");  //业务日期(发放日期)
			String count=(String)this.getFormHM().get("count");		 //发放次数
			String selectGzRecords=(String)this.getFormHM().get("selectGzRecords");
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			if(selectGzRecords!=null)
				selectGzRecords=selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		//总额控制,以后用
		/*	SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			if(bo.getIsControl().equals("1")&&isControl.equals("1"))
			{
				LazyDynaBean belongTime=new LazyDynaBean();
				String[] temps=bosdate.split("\\.");
				belongTime.set("year",temps[0]);
				belongTime.set("month",String.valueOf(Integer.parseInt(temps[1])));
				belongTime.set("day",String.valueOf(Integer.parseInt(temps[2])));
				belongTime.set("count",count);
				bo.setCurrentPayState(1);
				String info=bo.calculateTotal(belongTime,selectGzRecords.split("#"));
				if(!info.equals("success"))
					throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
			}*/
			
			String[] records=selectGzRecords.split("#");
			Calendar d=Calendar.getInstance();
			StringBuffer buf2=new StringBuffer("");
			HashSet dateSet=new HashSet();
			for(int f=0;f<records.length;f++)
			{
				if(records[f].length()>0)
				{
					String[] temp=records[f].split("/");
					d.setTimeInMillis(Long.parseLong(temp[2]));
					buf2.append(" or (A0100='"+temp[0]+"' and lower(nbase)='"+temp[1].toLowerCase()+"'  and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+temp[3]+" )");
					dateSet.add(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+temp[3]);
				}
			}
			/** 总额计算  */
			ArrayList dateList=new ArrayList();
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			StringBuffer where=new StringBuffer("");
			if(buf2.length()>0)
				where.append(" and salaryid="+salaryid+" and ( "+buf2.substring(3)+" ) ");
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				dateList=bo.getDateList(where.toString(),dateSet,true);
			}
			/** 总额计算  */
			bo.calculateTotalSum(dateList);
			gzbo.submitGzDataFromHistory(bosdate,count,selectGzRecords.split("#"));
			
			
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
