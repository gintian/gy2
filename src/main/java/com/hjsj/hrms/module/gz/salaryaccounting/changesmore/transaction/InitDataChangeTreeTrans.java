package com.hjsj.hrms.module.gz.salaryaccounting.changesmore.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.changesmore.businessobject.ChangesmoreBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 项目名称 ：hcm7.x
 * 类名称：InitDataChangeTreeTrans
 * 类描述：数据比对 主页面机构树获取
 * 创建人： zhanghua
 * 创建时间：2016-11-09
 */
public class InitDataChangeTreeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String imodule = (String) this.getFormHM().get("imodule");
			imodule = PubFunc.decrypt(SafeCode.decode(imodule));
			/**type=1 薪资发放中的数据比对  =2薪资审批中的数据比对**/
			String type = (String) this.getFormHM().get("type");
			/**业务日期**/
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			/**次数**/
			String count = (String) this.getFormHM().get("count");
			count = PubFunc.decrypt(SafeCode.decode(count));
			 
			String node=(String) this.getFormHM().get("node");
			ChangesmoreBo bo = new ChangesmoreBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryPropertyBo PropertyBo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String deptid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//TODO 归属部门 1111
			String orgid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");//归属单位
			if(deptid.trim().length()==0)
				deptid="e0122";
			if(orgid.trim().length()==0)
				orgid="b0110";
			String tableName= "1".equals(type)?gzbo.getGz_tablename():"SALARYHISTORY";
			boolean ispriv=false;
			if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
				ispriv=true;
			
			String strSql=bo.getMainTreeDataSql(tableName, type, appdate, count,node,ispriv,deptid,orgid);
			ArrayList dataList=bo.getTreeData(strSql,node);
			this.getFormHM().put("data", dataList);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}