package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 项目名称 ：ehr7.x
 * 类名称：AddSalaryTypeTrans
 * 类描述：新增工资类别
 * 创建人： lis
 * 创建时间：2015-11-12
 */
public class IsHaveNameTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			//校验
			String name=SafeCode.decode((String)this.getFormHM().get("name"));//薪资名称
			String gz_module=(String)this.getFormHM().get("gz_module");//模块号，薪资类别是0
			String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别id
			if(!"-1".equals(salaryid))
				salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String type=(String)this.getFormHM().get("type");//0是重命名，1是新增
			String msg="0";
			
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
			
			boolean flag = bo.isHaveName(name, gz_module,type,salaryid);
			if(flag)//已经存在
			{
				if("0".equalsIgnoreCase(gz_module))
				{
					msg=ResourceFactory.getProperty("gz.templateset.havegz");
				}else
					msg=ResourceFactory.getProperty("gz.templateset.havebx");//2016-12-26 24996 zhanghua
			}
			
			this.getFormHM().put("msg", msg);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
}
