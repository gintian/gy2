package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：IsReGzTrans   
* 类描述：   判断当前工资帐套是否满足重发要求
* 创建人：zhaoxg   
* 创建时间：Oct 27, 2013 10:04:05 AM   
* 修改人：zhaoxg   
* 修改时间：Oct 27, 2013 10:04:05 AM   
* 修改备注：   
* @version    
*
 */
public class IsReGzTrans extends IBusiness {

	public void execute() throws GeneralException {
		String msg="0";//0:允许重发  1：没有对应的重发薪资  2：当前帐套处在审批状态，不允许重发
		String salaryid=(String)this.getFormHM().get("salaryid");
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		ArrayList datelist=gzbo.getSubDateList();
		boolean flag=gzbo.isApproving();
		if(datelist.size()==0){
			msg="1";
		}else if(flag){
			msg="2";
		}
		this.getFormHM().put("msg", msg);
	}

}
