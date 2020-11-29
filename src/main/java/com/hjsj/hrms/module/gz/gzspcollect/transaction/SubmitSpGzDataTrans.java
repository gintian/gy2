package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SubmitSpGzDataTrans 
 * 类描述： 个别提交
 * 创建人：zhaoxg
 * 创建时间：Jan 13, 2016 3:50:16 PM
 * 修改人：zhaoxg
 * 修改时间：Jan 13, 2016 3:50:16 PM
 * 修改备注： 
 * @version
 */
public class SubmitSpGzDataTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid"); 
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		    String accountingdate = (String)this.getFormHM().get("appdate"); 
		    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
		    String accountingcount = (String)this.getFormHM().get("count"); 
		    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
			String selectGzRecords=(String)this.getFormHM().get("selectGzRecords");
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			if(selectGzRecords!=null)
				selectGzRecords=selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/");
			SalaryAccountBo gzbo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			String[] records=selectGzRecords.split("#");
			StringBuffer buf=new StringBuffer("");
			HashSet dateSet=new HashSet();
			for(int f=0;f<records.length;f++)
			{
				if(records[f].length()>0)
				{
					String[] temp=records[f].split("/");
					buf.append(" or (A0100='"+PubFunc.decrypt(temp[0])+"' and lower(nbase)='"+PubFunc.decrypt(temp[1]).toLowerCase()+"'  and A00Z0 = "+Sql_switcher.dateValue(temp[2])+" and A00z1='"+temp[3]+"' )");
					dateSet.add(temp[2]+"-"+temp[3]);
				}
			}
			
			
			// 总额计算 
			/*
			ArrayList dateList=new ArrayList();
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),Integer.parseInt(salaryid));
			StringBuffer where=new StringBuffer("");
			if(buf.length()>0)
				where.append(" and salaryid="+salaryid+" and ( "+buf.substring(3)+" ) ");
			SalaryCtrlParamBo ctrlparam=gzbo.getSalaryTemplateBo().getCtrlparam();
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if(isControl.equals("1"))
			{
				dateList=bo.getDateList(where.toString(),dateSet,true);
			}
			
			bo.calculateTotalSum(dateList);
			*/
			ArrayList<String []> changeList=gzbo.submitGzDataFromHistory(accountingdate,accountingcount,selectGzRecords.split("#"));
			StringBuffer strLog=new StringBuffer();
			strLog.append("共提交"+records.length+"条记录。");
			for(String [] str:changeList){
				strLog.append("<br/>{");
				strLog.append(str[0]+",");
				strLog.append(str[1]+",");
				strLog.append(str[2]+",");
				strLog.append(str[3]);
				strLog.append("}");
			}
			if(records.length>changeList.size())
				strLog.append("<br/>...");
			
			this.getFormHM().put("@eventlog",strLog.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
