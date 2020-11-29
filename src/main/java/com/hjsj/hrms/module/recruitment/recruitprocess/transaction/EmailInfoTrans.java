package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EmailInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class EmailInfoTrans extends IBusiness  {

	@Override
    public void execute() throws GeneralException {
		try {
			// TODO Auto-generated method stub

			EmailInfoBo bo =new EmailInfoBo(this.frameconn,this.userView);
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			UserView user = this.userView;
			this.getFormHM().remove("requestPamaHM");
			String flag = (String)hm.get("flag");//标志
			String a0100 = PubFunc.decrypt((String)hm.get("a0100"));//人员编号
			String a0101 = (String)hm.get("a0101");//人员姓名
			String c0102 = (String)hm.get("c0102");//收件人邮箱
			String nbase = PubFunc.decrypt((String)hm.get("nbase"));//人员库
			String z0301 = PubFunc.decrypt((String)hm.get("z0301"));//职位编号
			String userName = user.getUserFullName();//发件人邮箱
			String userPhone= user.getUserTelephone();//发件人电话
			LazyDynaBean infoBean = bo.getInfo(a0100,z0301,nbase);
			String codeitemdesc = (String)infoBean.get("UN");//单位名字
			String z0325 = (String)infoBean.get("UM");//需求部门
			String z0351 = (String)infoBean.get("Z0351");//职位名称
			String z0375 = (String)infoBean.get("Z0375");//到岗时间
			LazyDynaBean bean = new LazyDynaBean();
			if(flag!=null&& "sendOffer".equalsIgnoreCase(flag))
			{
				bean = bo.offerModel(a0100, codeitemdesc, a0101, z0325, z0351, z0375, userName, userPhone, c0102);
			}
			this.getFormHM().put("emailInfo", bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
