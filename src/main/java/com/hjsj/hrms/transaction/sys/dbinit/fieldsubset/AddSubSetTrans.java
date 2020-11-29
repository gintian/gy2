package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

;
/**
 * <p>Title:查询出集信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class AddSubSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//指标体系，新建子集，根据dev_flag限制自动生成的指标集代号  jingq upd 2015.01.26
		String dev_flag = SystemConfig.getPropertyValue("dev_flag");
		String infor = (String)this.getFormHM().get("infor");
		SubSetBo subset = new SubSetBo(this.getFrameconn());
		ArrayList subsetList=subset.getsubsetList(infor);
		this.getFormHM().put("subsetList", subsetList);
		String codevalue = subset.getcodevalue(subsetList,dev_flag);
		this.getFormHM().put("dev_flag", dev_flag);
		this.getFormHM().put("code", codevalue);
		this.getFormHM().put("qobj", "0");
		this.getFormHM().put("isrefresh","no");
		this.getFormHM().put("multimedia_file_flag","0");//支持附件

	}

}
