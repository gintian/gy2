package com.hjsj.hrms.module.gz.salaryreport.transaction;


import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 *
 * <p>Title:InitSalaryReportTrans.java</p>
 * <p>Description>:初始化薪资报表参数</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 14, 2016 2:04:38 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class InitSalaryReportTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String bosdate=(String)this.getFormHM().get("bosdate");
			bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
			String count=(String)this.getFormHM().get("count");
			count = PubFunc.decrypt(SafeCode.decode(count));
			//=0表示薪资，=1表示保险
			String gz_module=(String) this.getFormHM().get("gz_module");
			//model=0工资发放进入，=1工资审批进入，=3是工资历史数据
			String model = (String) this.getFormHM().get("model");
			if("0".equals(model) || "3".equalsIgnoreCase(model)){
				gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
			}
			String a_code = "";
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
			String priv = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag"); // 人员范围权限过滤标志 1：有
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET,"user");
			if(priv.length()==0)
				priv="0";
			if(manager!=null&&manager.length()>0&&manager.equalsIgnoreCase(this.userView.getUserName()))
				priv="0";
			else if(StringUtils.isBlank(manager))//非共享账套展示全部数据。 zhanghua 2017-8-9 30394
				priv="0";

			if("0".equals(priv)){
				a_code="UN";
			}
			SalaryReportBo salaryReportBo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
			ArrayList<LazyDynaBean> listReportTree=salaryReportBo.listReportTree(gz_module,model);

			//判断是否有新增修改权限
			String addPower="0",delPower="0",editPower="0";
			if(this.getUserView().isSuper_admin()){
                addPower="1";
                delPower="1";
                editPower="1";
            }else if("0".equals(gz_module)){
			    if(this.getUserView().hasTheFunction("32402050301")){
                    addPower="1";
                }
			    if(this.getUserView().hasTheFunction("32402050302")){
                    editPower="1";
                }
			    if(this.getUserView().hasTheFunction("32402050303")){
                    delPower="1";
                }
            }else if("1".equals(gz_module)){
                if(this.getUserView().hasTheFunction("32502050301")){
                    addPower="1";
                }
                if(this.getUserView().hasTheFunction("32502050302")){
                    editPower="1";
                }
                if(this.getUserView().hasTheFunction("32502050303")){
                    delPower="1";
                }
            }



			this.userView.getHm().put("gzmodel", model);//0 发放 1 审批 3历史数据
			this.getFormHM().put("treeData", listReportTree);
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("bosdate", bosdate);
			this.getFormHM().put("count", count);
			this.getFormHM().put("addPower", addPower);
			this.getFormHM().put("delPower", delPower);
			this.getFormHM().put("editPower", editPower);
			this.getFormHM().put("a_code", a_code);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
