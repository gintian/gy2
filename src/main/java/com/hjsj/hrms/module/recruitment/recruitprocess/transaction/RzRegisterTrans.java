package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 入职列表
 * <p>Title: RzRegisterTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-31 下午03:45:06</p>
 * @author xiexd
 * @version 1.0
 */
public class RzRegisterTrans extends IBusiness {

	
	@Override
    public void execute() throws GeneralException {
		//职位ID
		String z0301=PubFunc.decrypt((String)this.getFormHM().get("z0301"));
		//人员序号
		String []a0100s = ((String) this.getFormHM().get("a0100s")).split(",");
		ArrayList a0100List = new ArrayList();
		//将人员序号数组转为集合形式
		for(int i=0;i<a0100s.length;i++)
		{
			a0100List.add(PubFunc.decrypt(a0100s[i]));
		}
		//应聘人员库
		String dbnames = PubFunc.decrypt(((String) this.getFormHM().get("nbases")).split(",")[0]);
		RecruitProcessBo bo = new RecruitProcessBo(this.frameconn, this.userView);
		//获取显示列
		String rzColumn = bo.getRzColumn();
		//获取值对象
		String rzValue = (String)bo.getRzRegister(a0100List, z0301, dbnames);
		this.getFormHM().put("rzColumn", rzColumn);
		this.getFormHM().put("rzValue",rzValue );
	}

}
