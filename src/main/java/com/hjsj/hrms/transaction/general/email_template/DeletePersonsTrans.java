package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class DeletePersonsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String sendok=(String)this.getFormHM().get("sendok");
			//String nbase=(String)this.getFormHM().get("nbase");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String code=(String)this.getFormHM().get("code");
			String templateId=(String)this.getFormHM().get("id");
			String queryvalue=(String)this.getFormHM().get("queryvalue");
			String selectid=(String)this.getFormHM().get("selectid");
			String queryName=(String)this.getFormHM().get("queryName");
			
			String queryYearValue=(String)this.getFormHM().get("queryYearValue");//年份过滤
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String columns="personid,id,a0100,b0110,e0122,subject,send_ok,a0101,nbase,I9999,address";
			String order_by=(String)this.getFormHM().get("order_by");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			bo.deletePersonFromEmail_content(selectid,templateId,this.userView.getUserName());
			//String sql=bo.getSearchPersonByCondSql("send_ok",sendok,templateId,code,tableName);
			String timesql=bo.queryRecordByTime(queryYearValue,queryvalue);
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				 tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				 tableName=manager+"_salary_"+salaryid;
			CashListBo clb = new CashListBo(this.getFrameconn(),"0",salaryid);
			clb.setUserview(this.userView);
			String privSql=clb.getPrivSql(this.userView, gzbo);
			String codesql = clb.getCodeSql("s", code);
			String filterId=(String)this.getFormHM().get("filterId");
			BankDiskSetBo bbo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());
			ArrayList filterList = bbo.getFilterCondList(salaryid);
			String beforeSql=(String)this.getFormHM().get("beforeSql");
			/* 薪资发放/发送邮件.选中一个人，点删除历史记录，界面刷新后一条记录都看不到了 xiaoyun 2014-9-29 start */
			String _beforeSql = "";
			//beforeSql=PubFunc.keyWord_reback(SafeCode.decode(beforeSql));
			if(StringUtils.isNotEmpty(beforeSql)) {
				_beforeSql = PubFunc.decrypt(SafeCode.decode(beforeSql));
			}
			//String sql = bo.getSelect_sqlAndWhere_sqlAndOrder_sql(codesql,templateId,tableName,timesql,this.userView.getUserName(),sendok,priv_mode,privSql,queryName,order_by,beforeSql);
			String sql = bo.getSelect_sqlAndWhere_sqlAndOrder_sql(codesql,templateId,tableName,timesql,this.userView.getUserName(),sendok,priv_mode,privSql,queryName,order_by,_beforeSql);
			/* 薪资发放/发送邮件.选中一个人，点删除历史记录，界面刷新后一条记录都看不到了 xiaoyun 2014-9-29 end */
			String[] sql_arr=sql.split("#");
			ArrayList querylist=bo.getMonthList();
			ArrayList queryListYear=bo.getYearList();
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
			this.getFormHM().put("queryName", queryName);
			this.getFormHM().put("order_by", order_by);
			this.getFormHM().put("filterId",filterId);
			this.getFormHM().put("filterList",filterList);
			/* 薪资发放/发送邮件.选中一个人，点删除历史记录，界面刷新后一条记录都看不到了 xiaoyun 2014-9-29 start */
			//this.getFormHM().put("beforeSql",SafeCode.encode(beforeSql));
			this.getFormHM().put("beforeSql",beforeSql);
			/* 薪资发放/发送邮件.选中一个人，点删除历史记录，界面刷新后一条记录都看不到了 xiaoyun 2014-9-29 end */
			this.getFormHM().put("tableName",tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
