package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>Title:DefineSalaryReport.java</p>
 * <p>Description>:薪资报表定义</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 19, 2016 5:41:33 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class DefineSalaryReport extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");   // new:  edit:
			String id=(String)this.getFormHM().get("rsdtlid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			
			SalaryReportBo bo=new SalaryReportBo(this.getFrameconn(),salaryid,this.userView);
			String  salaryReportName="";
			
			String  isPrintWithGroup="0";  //是否按分组指标分页打印  1：是  0：否
			String  f_groupItem="";          //第一分组指标
			ArrayList f_groupItemList=new ArrayList();  //第一分组指标列表
			String  s_groupItem="";          //第二分组指标
			ArrayList s_groupItemList=new ArrayList();  //第二分组指标列表
			String  reportStyleID="";         //表类id
			String  reportDetailID="";        //工资报表id
			ArrayList rightlist=new ArrayList();
			String  isGroup="0";
			LazyDynaBean abean=null;
			String ownerType="1";
			//由于在同步结构的时候没有同步reportitem表，导致出错
			bo.synReportSet(salaryid,id);
			
			if("edit".equals(opt))
			{
				abean=bo.getGzDetailBydID(id);
				reportStyleID=(String)abean.get("rsid");
				reportDetailID=(String)abean.get("rsdtlid");
				
				salaryReportName=(String)abean.get("rsdtlname");
				isPrintWithGroup=(String)abean.get("bgroup");
				f_groupItem=(String)abean.get("fgroup");
				s_groupItem=(String)abean.get("sgroup");
				isGroup=bo.getIsGroup((String)abean.get("ctrlparam"));
				ownerType=bo.analyseXML((String)abean.get("ctrlparam"));
				
			}
			else
			{
				reportStyleID=id;
				if("1".equals(reportStyleID))
					salaryReportName="工资条";
				else if("2".equals(reportStyleID))
					salaryReportName="工资发放签名表";
				else if("3".equals(reportStyleID))
					salaryReportName="工资汇总表";
				else if("12".equals(reportStyleID))
					salaryReportName="保险明细表";
				else if("13".equals(reportStyleID))
					salaryReportName="保险汇总表";
			}
			rightlist=bo.getReportSalarySet(reportStyleID,reportDetailID);
			if("2".equals(reportStyleID)|| "12".equals(reportStyleID))  //工资发放签名表
			{
				f_groupItemList=bo.getGroupItemList("0",salaryid);
				
			}
			if("3".equals(reportStyleID)|| "13".equals(reportStyleID)) //工资汇总表
			{
				f_groupItemList=bo.getGroupItemList("0",salaryid);
				if((f_groupItem==null||f_groupItem.length()==0)&&f_groupItemList.size()>0){//如果第一分组词为空，则默认选择第一个
					LazyDynaBean bean = (LazyDynaBean) f_groupItemList.get(0);
					f_groupItem = (String) bean.get("id");
				}
				s_groupItemList=bo.getGroupItemList("1",salaryid);
			}
			
			this.getFormHM().put("isGroup",isGroup);
			this.getFormHM().put("salaryReportName",salaryReportName);
			this.getFormHM().put("isPrintWithGroup",isPrintWithGroup);
			this.getFormHM().put("f_groupItem",f_groupItem);
			this.getFormHM().put("f_groupItemList",f_groupItemList);
			this.getFormHM().put("s_groupItem",s_groupItem);
			this.getFormHM().put("s_groupItemList",s_groupItemList);
			this.getFormHM().put("reportStyleID",reportStyleID);
			this.getFormHM().put("reportDetailID",reportDetailID);
			this.getFormHM().put("data",rightlist);
	        this.getFormHM().put("ownerType", ownerType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
