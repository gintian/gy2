package com.hjsj.hrms.transaction.train.report;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.report.TrainReportBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SearchTrainReportTrans.java</p>
 * <p>Description:培训报表查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-08-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchTrainReportTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	// 查询的参数
    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    String itemType=(String)hm.get("comefrom");
    hm.remove("comefrom");
    if("default".equalsIgnoreCase(itemType)){
    	this.getFormHM().put("year","");
    	this.getFormHM().put("quarter", "");
    	this.getFormHM().put("pageform", new PaginationForm());
    }
    
	String year = (String) this.getFormHM().get("year");
	String quarter = (String) this.getFormHM().get("quarter");
	String code = (String) this.getFormHM().get("a_code");
	String type = (String) this.getFormHM().get("reportId");
	TrainReportBo bo = new TrainReportBo(this.getFrameconn(),type);
	
	/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
	if("".equals(code)&&!this.userView.isSuper_admin()){
//		if(userView.getStatus()==4){
//			code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}else if(userView.getStatus()==0){
//			String codeall = userView.getUnit_id();
//			if(codeall!=null&&codeall.length()>3)
//				code=codeall.split("`")[0];
//			else
//				code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}
		TrainCourseBo tb = new TrainCourseBo(this.userView);
		code = tb.getUnitIdByBusi();
		if(code==null||code.length()<3)
			throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
	}
	
	this.getFormHM().put("titles", bo.getTitle());
	try
	{
	    ArrayList setlist = bo.search(code,year,quarter);
	    //xiexd 2014.09.24将sql保存至服务器
		this.userView.getHm().put("key_train_sql1",bo.getStrSql());
	    this.getFormHM().put("strSql", bo.getStrSql());
	    this.getFormHM().put("setlist", setlist);
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}

    }

}
