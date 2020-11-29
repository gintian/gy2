package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：VerifyTrans 
 * 类描述： 审核校验
 * 创建人：zhaoxg
 * 创建时间：Aug 7, 2015 6:08:56 PM
 * 修改人：zhaoxg
 * 修改时间：Aug 7, 2015 6:08:56 PM
 * 修改备注： 
 * @version
 */
public class VerifyTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			String salaryid=(String)this.getFormHM().get("salaryid"); 
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
			gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
		    String accountingdate = (String)this.getFormHM().get("appdate"); 
		    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
		    String accountingcount = (String)this.getFormHM().get("count"); 
		    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
		    String viewtype = (String)this.getFormHM().get("viewtype"); // 页面区分 0:薪资发放  1:审批  2:上报
		    viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
		    
		    SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
		    SalaryTemplateBo gzbo=bo.getSalaryTemplateBo();
		    String tableName = gzbo.getGz_tablename();
		    StringBuffer filtersql = new StringBuffer();
		    if("1".equals(viewtype)){//薪资审批
		    	String collectPoint = (String) this.getFormHM().get("collectPoint");
		    	String selectID = (String) this.getFormHM().get("selectID");
		    	String cound = (String) this.getFormHM().get("cound");//人员筛选
		    	GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
		    	tableName = "salaryhistory";
		    	//filtersql.append(gzbo.getWhlByUnits(tableName,true));//审批审核如果根据当前用户的权限来审核的话，在审核只能审核一部分
		    	filtersql.append(spbo.getCollectSPPriv(gzbo, selectID,cound, collectPoint,salaryid,accountingdate,accountingcount));
		    }
		    if("0".equals(viewtype) || "2".equals(viewtype)){//薪资发放
		    	filtersql.append(gzbo.getfilter(tableName));
		    }
		    if(("0".equals(viewtype) || "2".equals(viewtype))&&(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))) {//薪资发放，非管理员
				filtersql.append(gzbo.getWhlByUnits(tableName, true));
				ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.getFrameconn(),salaryid,this.userView);
				filtersql.append(aorgbo.getSalarySql(accountingdate, gz_module));
			}
		    
		    HashMap map = bo.verify(viewtype, accountingdate, accountingcount, filtersql.toString());
			String filename=(String)map.get("filename");
			filename = SafeCode.encode(PubFunc.encrypt(filename));
			this.getFormHM().put("fileName",filename);
			String msg=(String)map.get("msg");
			this.getFormHM().put("msg",msg);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
