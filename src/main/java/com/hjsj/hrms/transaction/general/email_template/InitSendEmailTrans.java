package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:InitSendEmailTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-25 13:27:27</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class InitSendEmailTrans extends IBusiness{

	public void execute() throws GeneralException
	{
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		try
		{

			String optvalue=(String)this.getFormHM().get("optValue");
			String salaryid=(String)map.get("salaryid");
			String code=(String)map.get("a_code");
			String init =(String)map.get("b_init");
			String queryYearValue="";
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			bo.updateTableCloumns();
			ArrayList templateList=bo.getEmailTemplateList(2);
			String columns="personid,id,a0100,b0110,e0122,subject,send_ok,a0101,nbase,I9999,address";
			String templateId="";
			String sendok="3";
			String queryvalue="0";
			String queryName="";
			String filterId=(String)this.getFormHM().get("filterId");
			BankDiskSetBo bbo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());
			ArrayList filterList = bbo.getFilterCondList(salaryid);
			String beforeSql=(String)this.getFormHM().get("beforeSql");
			String _beforeSql=PubFunc.decrypt((SafeCode.decode(beforeSql)));
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			String order_by = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER, this.userView);
			if(order_by !=null&&order_by.trim().length()>0)
				order_by = "s."+order_by.replaceAll(",", ",s.");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				 tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				 tableName=manager+"_salary_"+salaryid;
			if(manager.trim().length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
				 priv_mode="1";
			CashListBo clb = new CashListBo(this.getFrameconn(),"0",salaryid);
			clb.setUserview(this.userView);
			String privSql=clb.getPrivSql(this.userView, gzbo);
			
			if("init".equals(init))
			{
	    		if(templateList.size()>=2)
	     			templateId=String.valueOf(bo.getMinTemplateId("2"));
			}
			else if("init2".equals(init))
			{
				templateId=(String)map.get("id");
			}else if("init3".equals(init))
			{
				templateId = (String)this.getFormHM().get("templateId");
				sendok = (String)this.getFormHM().get("sendok");
				queryvalue=(String)this.getFormHM().get("queryvalue");
				queryName=(String)this.getFormHM().get("queryName");
				queryYearValue=(String)this.getFormHM().get("queryYearValue");
			}
			if(!"0".equals(optvalue))
			{
				if("init2".equals(init))
				{
					templateId=(String)map.get("id");
				}
				else
				{
	     			templateId = (String)this.getFormHM().get("templateId");
				}
				sendok = (String)this.getFormHM().get("sendok");
				queryvalue=(String)this.getFormHM().get("queryvalue");
				queryName=(String)this.getFormHM().get("queryName");
			}
			String timesql = "";
			if(!("0".equals(queryvalue)||queryvalue==null))
			{
				timesql = Sql_switcher.month("e.send_time")+"="+queryvalue;
				
			}	
			ArrayList queryListYear=bo.getYearList();
			if(queryListYear.size()>0&&StringUtils.isBlank(queryYearValue))
				queryYearValue=((CommonData)queryListYear.get(0)).getDataValue();
			if(StringUtils.isNotBlank(queryYearValue)){
				if(timesql.length()>0)
					timesql=timesql+" and "+Sql_switcher.year("e.send_time")+"="+queryYearValue;
				else
					timesql=Sql_switcher.year("e.send_time")+"="+queryYearValue;
			}
			String codesql = clb.getCodeSql("s", code);
			templateId= "".equals(templateId.trim())?"-2":templateId;//若不存在邮件模板 则给予一个不可能的id 使记录为空 zhanghua 2016-11-21
			String sql=bo.getSearchPersonByCondSql("send_ok",sendok,templateId,codesql,tableName,timesql,priv_mode,privSql,queryName,order_by,_beforeSql);
//			String sql = bo.getSelect_sqlAndWhere_sqlAndOrder_sql(codesql,templateId==null||templateId.equals("")?null:templateId,tableName,timesql,this.userView.getUserName(),sendok,priv_mode,privSql,queryName,order_by,_beforeSql);
			String[] sql_arr=sql.split("#");
			ArrayList querylist = bo.getMonthList();
			//String ss  =sql_arr[0]+" "+sql_arr[1]+" "+sql_arr[2];
		    //System.out.println(ss);
			this.getFormHM().put("querylist",querylist);
			this.getFormHM().put("queryvalue",queryvalue);
			this.getFormHM().put("sendok",sendok);
			this.getFormHM().put("select_sql",sql_arr[0]);
			
			this.getFormHM().put("queryListYear",queryListYear);
			this.getFormHM().put("queryYearValue",queryYearValue);
			
			this.getFormHM().put("where_sql",sql_arr[1]);
			this.getFormHM().put("order_sql",sql_arr[2]);
			this.getFormHM().put("columns",columns);
			this.getFormHM().put("code",code);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("id",templateId==null?"":templateId);
			this.getFormHM().put("templateId",templateId==null?"":templateId);
			this.getFormHM().put("templateList",templateList);
			this.getFormHM().put("queryName",queryName);
			this.getFormHM().put("order_by", order_by);
			this.getFormHM().put("filterId",filterId);
			this.getFormHM().put("filterList",filterList);
			this.getFormHM().put("beforeSql",beforeSql);
			this.getFormHM().put("tableName",tableName);
			//this.getFormHM().put("privdbpre",privdbpre);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			map.remove("code");
		}
	
		//select * from su_salary_1 left join (select * from email_content where id=2) s on su_salary_1.a0100=s.a0100 
	}

}
