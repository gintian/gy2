package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryReportBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Vector;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:工资报表取数 </p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 18, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetGzReportDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");  // 1:表头描述    2:工资报表数据记录 3.修改表头显示顺序和宽度
			String rsid=(String)this.getFormHM().get("rsid"); //报表种类编号 
			String rsdtlid=(String)this.getFormHM().get("rsdtlid"); //报表编号 
			String salaryid=(String)this.getFormHM().get("salaryid");
			if("1".equals(opt))
			{
				SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
				ArrayList list=null;
				if("4".equals(rsid))
				{
					String baseid=(String)this.getFormHM().get("baseid");
					if(baseid.length()==0)
					{
						Vector baseConditionVector=gzbo.getReportItemVector("A");
						baseid=(String)((LazyDynaBean)baseConditionVector.get(0)).get("value");
					}
					list=gzbo.getGzAnalyseHeadDescList(baseid);
				}
				else
					list=gzbo.getTableHeadDescList(rsid,rsdtlid);
				this.getFormHM().put("tableHeadList",list);
			}
			else if("2".equals(opt))
			{
				SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
				String empfiltersql=(String)this.getFormHM().get("empfiltersql");
				empfiltersql=PubFunc.keyWord_reback(SafeCode.decode(SafeCode.decode(empfiltersql)));
				String noManagerFilterSql=(String)this.getFormHM().get("noManagerFilterSql");
				noManagerFilterSql=PubFunc.keyWord_reback(SafeCode.decode(SafeCode.decode(noManagerFilterSql)));
				String userName=(String)this.getFormHM().get("userName");
				String priv_mode=(String)this.getFormHM().get("priv_mode");
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
				if(priv_mode==null)
				{
		            priv_mode=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
				}
				String codeitemid=(String)this.getFormHM().get("codeitemid");
				String codesetid=(String)this.getFormHM().get("codesetid");
				String condid=(String)this.getFormHM().get("condid");
				String groupValues=(String)this.getFormHM().get("groupValues");
				String orderSql=(String)this.getFormHM().get("orderSql");
				String role=(String)this.getFormHM().get("role");
				String privDb = (String)this.getFormHM().get("privDb");
				String privCode = (String)this.getFormHM().get("privCode");
				String privCodeValue=(String)this.getFormHM().get("privCodeValue");
				/**model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。*/
				String model=(String)this.getFormHM().get("model");
				String boscount=(String)this.getFormHM().get("boscount");
				String bosdate=(String)this.getFormHM().get("bosdate");
				String spSQL=gzbo.getSpSQL(this.getUserView(), boscount, bosdate,model);
				ArrayList list=new ArrayList();
				SalaryTemplateBo gbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				HistoryDataBo hbo = new HistoryDataBo(this.frameconn, this.userView);
			//	String priv_str= hbo.getPrivPre("1");
				String priv_str= hbo.getPrivPre("1",salaryid);
				gzbo.setUnit_sql(priv_str);
				if("1".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid)|| "3".equals(rsid)|| "13".equals(rsid))
					list=gzbo.getRecordList(rsid, rsdtlid, codeitemid, codesetid, userName,condid,groupValues,orderSql,SafeCode.decode(noManagerFilterSql),SafeCode.decode(empfiltersql),role,privCode,privCodeValue,privDb,priv_mode,manager,model,spSQL,gbo);
				else if("4".equals(rsid))		//人员结构工资分析表
				{
					Vector reportItemVector=gzbo.getReportItemVector("N");
					Vector baseConditionVector=gzbo.getReportItemVector("A");
					String itemid=(String)this.getFormHM().get("itemid");
					String baseid=(String)this.getFormHM().get("baseid");
					if(itemid.length()==0)
						itemid=(String)((LazyDynaBean)reportItemVector.get(0)).get("value");
					if(baseid.length()==0)
						baseid=(String)((LazyDynaBean)baseConditionVector.get(0)).get("value");
					list=gzbo.getGzAnalyseList(rsid,codeitemid,codesetid,userName,condid,itemid,baseid,SafeCode.decode(noManagerFilterSql),SafeCode.decode(empfiltersql),role,privCode,privCodeValue,privDb,priv_mode,manager,model,spSQL);
					this.getFormHM().put("reportItemVector", reportItemVector);
					this.getFormHM().put("baseConditionVector", baseConditionVector);
					
					FieldItem item=DataDictionary.getFieldItem(baseid.toLowerCase());
					this.getFormHM().put("chartTitle", " ");
					this.getFormHM().put("baseid",baseid);
				}
				int recordRows = gzbo.getRecordRows();
				String reportName="人员结构工资分析表";
				if("1".equals(rsid)|| "2".equals(rsid)|| "3".equals(rsid)|| "12".equals(rsid)|| "13".equals(rsid))
					reportName=gzbo.getReportdetailVo(rsdtlid,rsid).getString("rsdtlname");
				if("1".equals(rsid))
					reportName="";
				this.getFormHM().put("reportName",reportName);
				
				
				byte[] bytes =PubFunc.zipBytes_object(list);
				this.getFormHM().put("data_bytes",bytes);
				this.getFormHM().put("recordNums", recordRows+"");
				Vector manFilterVector=new Vector();
				ArrayList condList=gzbo.getCondlist();
				for(int i=0;i<condList.size();i++)
				{
					manFilterVector.addElement((LazyDynaBean)condList.get(i));
				}
				/**新建人员筛选条件暂时未加*/
				/*LazyDynaBean tempbean = new LazyDynaBean();
				tempbean.set("value", "new");
				tempbean.set("name","新建...");
				manFilterVector.addElement(tempbean);*/
				if("3".equals(rsid)|| "13".equals(rsid)) //汇总表类
				{
					LazyDynaBean groupBean=gzbo.getGroupBean(rsid,rsdtlid);
					this.getFormHM().put("groupBean",groupBean);
				}
				this.getFormHM().put("manFilterVector",manFilterVector);   // 过虑条件列表
				if(gzbo.getReportdetailvo()==null)
					this.getFormHM().put("bgroup","0");
				else
					this.getFormHM().put("bgroup",String.valueOf(gzbo.getReportdetailvo().getInt("bgroup")));
			}
			else if("3".equals(opt))
			{
				SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid);
				String isResetSort=(String)this.getFormHM().get("isResetSort");
				ArrayList currentcolList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("cur_head_byte"));
				ArrayList tableHeadList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("table_head_byte"));
				ArrayList currentColWidthList=PubFunc.unzipBytes_object((byte[])this.getFormHM().get("currentColWidth_byte"));
				if("1".equals(isResetSort))
					gzbo.resetTableColumnSort(currentcolList,tableHeadList,rsdtlid);
				gzbo.resetTableColumnWidth(currentColWidthList,currentcolList,tableHeadList,rsdtlid);
			}
			else if("5".equals(opt))
			{
				String value=(String)this.getFormHM().get("oldcodeitemid");
				SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
				String newvalue=gzbo.getUnByPosition(value);
				if(newvalue!=null&&!"".equals(newvalue))
				{
					this.getFormHM().put("newcodeitemid",newvalue.substring(2));
					this.getFormHM().put("codesetid",newvalue.substring(0,2));
				}
				else
				{
					this.getFormHM().put("newcodeitemid","");
					this.getFormHM().put("codesetid","");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
 