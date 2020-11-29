package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

public class GzAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String approveObject=(String)this.getFormHM().get("approveObject");
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
		 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String filterWhl=PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("filterWhl")));
			if(filterWhl==null&&filterWhl.trim().length()==0)
				filterWhl="";
			//------------------浙江交投 -----------------------
			LazyDynaBean bean=gzbo.getSalaryPayDate(this.getFrameconn(), gzbo.getGz_tablename(),salaryid);
			String name=bean.get("year")+"年"+bean.get("month")+"月"+bean.get("count")+"次  "+bean.get("name");//待办名  “2014年06月1次 月度奖金（薪资）”			
			DbNameBo.autoAddZ1(this.getFrameconn(), this.userView, gzbo.getGz_tablename(), salaryid+"",gzbo.getManager(), gzbo,approveObject, filterWhl);
			gzbo.gzDataAppeal(approveObject,filterWhl);			
			bean.set("sql", "select * from "+gzbo.getGz_tablename()+" where A00Z2="+Sql_switcher.dateValue((String) bean.get("a00z2"))+" and A00Z3='"+bean.get("count")+"' and (sp_flag='01' or sp_flag='07')");
			LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, approveObject,salaryid,bean,"1");
			PendingTask pt = new PendingTask();
			if("add".equals(_bean.get("flag"))){
				pt.insertPending("G"+_bean.get("pending_id"),"G",name,this.userView.getUserName(),approveObject,(String)_bean.get("url"), 0, 1, "薪资审批", this.userView);

			}else if("update".equals(_bean.get("flag"))){
				pt.updatePending("G", "G"+_bean.get("pending_id"), 0, "薪资审批", this.userView);
			}		
			if("update".equals(_bean.get("selfflag"))){
				pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
			}
//			String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
//			if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
//				String username = gzbo.getZizhuUsername(approveObject);
//				WeiXinBo.sendMsgToPerson(username, name, "", "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", (String)_bean.get("url"));
//			}
			//-------------------------浙江交投 end---------------------------------------
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
