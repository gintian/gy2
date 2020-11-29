package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryRecordByTimeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{

             HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
             //String id=(String)map.get("timeid");
             String sendok=(String)this.getFormHM().get("sendok");
             String templateId=(String)this.getFormHM().get("templateId");
             String code =(String)this.getFormHM().get("code");
             String salaryid=(String)this.getFormHM().get("salaryid");
             String tableName=this.userView.getUserName()+"_salary_"+salaryid;
             
             String queryYearValue=(String)this.getFormHM().get("queryYearValue");
             String queryvalue=(String)this.getFormHM().get("queryvalue");
             
 			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
 			//bo.updateTableCloumns();
 			ArrayList templateList=bo.getEmailTemplateList(2);
 			String columns="personid,id,a0100,b0110,e0122,subject,send_ok,a0101,nbase,I9999,address";
 			String s=bo.queryRecordByTime(queryYearValue,queryvalue);
 			//String templateId=String.valueOf(bo.getMinTemplateId())/*(String)map.get("templateId")*/;
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
			  String queryName=(String)this.getFormHM().get("queryName");
			  String codesql = clb.getCodeSql("s", code);
			  String filterId=(String)this.getFormHM().get("filterId");
			BankDiskSetBo bbo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());
			ArrayList filterList = bbo.getFilterCondList(salaryid);
			String beforeSql=(String)this.getFormHM().get("beforeSql");
			String _beforeSql=PubFunc.decrypt(SafeCode.decode(beforeSql));
 			String sql = bo.getSelect_sqlAndWhere_sqlAndOrder_sql(codesql,templateId,tableName,s,this.userView.getUserName(),sendok,priv_mode,privSql,queryName,order_by,_beforeSql);
 			String[] sql_arr=sql.split("#");
 			
 			ArrayList queryListYear=bo.getYearList();
			if(StringUtils.isBlank(queryYearValue))
				queryYearValue=((CommonData)queryListYear.get(0)).getDataValue();
			
 			/**手动选人，没用*/
 			//String privdbpre=bo.getPrivPre(this.userView.getPrivDbList());
 			/**根据人员库查人，没用*/
 			/*ArrayList nbaselist = bo.getNbaseList();
 			this.getFormHM().put("nbaselist",nbaselist);*/
 			//this.getFormHM().put("nbase",nbase);
 			ArrayList querylist = bo.getMonthList();
 			this.getFormHM().put("querylist",querylist);
 			this.getFormHM().put("queryvalue",queryvalue);
 			this.getFormHM().put("queryListYear",queryListYear);
			this.getFormHM().put("queryYearValue",queryYearValue);
 			this.getFormHM().put("sendok",sendok);
 			this.getFormHM().put("select_sql",sql_arr[0]);
 			this.getFormHM().put("where_sql",sql_arr[1]);
 			this.getFormHM().put("order_sql",sql_arr[2]);
 			this.getFormHM().put("columns",columns);
 			this.getFormHM().put("code",code);
 			this.getFormHM().put("salaryid",salaryid);
 			this.getFormHM().put("id",templateId);
 			this.getFormHM().put("templateId",templateId);
 			this.getFormHM().put("templateList",templateList);
            this.getFormHM().put("optValue","1");
            this.getFormHM().put("queryName",queryName);
            this.getFormHM().put("order_by", order_by);
            this.getFormHM().put("filterId",filterId);
			this.getFormHM().put("filterList",filterList);
			this.getFormHM().put("beforeSql",beforeSql);
			this.getFormHM().put("tableName",tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
