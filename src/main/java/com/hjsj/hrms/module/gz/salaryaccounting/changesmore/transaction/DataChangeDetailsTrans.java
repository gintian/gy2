package com.hjsj.hrms.module.gz.salaryaccounting.changesmore.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.changesmore.businessobject.ChangesmoreBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 项目名称 ：hcm7.x
 * 类名称：DataChangeDetailsTrans
 * 类描述：数据比对 明细表
 * 创建人： zhanghua
 * 创建时间：2016-11-09
 */
public class DataChangeDetailsTrans  extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		String flag = (String) this.getFormHM().get("flag");
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
		/**比对指标**/
		String fieldItem=(String) this.getFormHM().get("fieldItem");
		
		ChangesmoreBo bo = new ChangesmoreBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		String tableName= "1".equals(type)?gzbo.getGz_tablename():"SALARYHISTORY";
		//拼接组织机构查询条件
		String selectID = (String) this.getFormHM().get("selectID");
		String deptSql="";
		SalaryPropertyBo PropertyBo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
		String deptid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
		String orgid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");//归属单位
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
		if(!"0".equals(uniquenessvalid))
			onlyname=bo.getOnlyName(onlyname);
		else
			onlyname="";
		if("a0101".equalsIgnoreCase(onlyname))
			onlyname="";
		if("0".equals(flag)){
			HashMap columns = bo.getColumnListDetail(deptid,orgid,onlyname);
			this.getFormHM().put("fields", columns.get("fields"));
			this.getFormHM().put("column", columns.get("column"));
		}else{
			int page = Integer.parseInt((String)this.formHM.get("page"));
			int limit = Integer.parseInt((String)this.formHM.get("limit"));
			String sort = (String) this.formHM.get("sort");
			if(selectID!=null&&selectID.trim().length()>0){
				deptSql=bo.getDeptSqlWhere(selectID, deptid,orgid);
			}
			boolean ispriv=false;
			if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
				ispriv=true;
			HashMap map=bo.getChangeDetailsDataList(tableName, appdate, count, type, fieldItem, deptSql, deptid, orgid,ispriv,onlyname,page,limit,sort);
			this.getFormHM().put("data", map.get("dataList"));
			this.getFormHM().put("totalCount", map.get("totalCount"));
		}
	}
}
