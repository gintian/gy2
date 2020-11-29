package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @ClassName: IsReGzTrans 
 * @Description: TODO( 判断当前工资帐套是否满足重发要求) 
 * @author lis 
 * @date 2015-8-10 下午01:36:24
 */
public class IsReGzTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid=SafeCode.decode(salaryid); //解码
		salaryid =PubFunc.decrypt(salaryid); //解密
		SalaryAccountBo salaryAccountBo=new SalaryAccountBo(this.getFrameconn(),this.userView,Integer.parseInt(salaryid));
		ArrayList datelist=salaryAccountBo.getSubDateList();
		boolean flag=salaryAccountBo.isApproving();
		//您还没有提交过薪资，不需要重发
		if(datelist.size()==0){
			throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("gz_new.gz_accounting.noReapte")));
		}else if(flag){
			throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("gz_new.gz_accounting.approving")));
		}
		String salaryIsSubed="true";  //薪资是否为已提交状态
		if(salaryAccountBo.isSalaryPayed(salaryid))
			salaryIsSubed="false";//有未提交的数据，如果重新发放则会覆盖原来的数据
		this.getFormHM().put("salaryIsSubed", salaryIsSubed);
	}

}
