package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* <p>Title:ImportExpertsDataTrans </p>
* <p>Description: 引入内部专家</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 3, 2015 9:12:23 AM
 */
public class ImportExpertsDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String ids = (String) this.getFormHM().get("ids");//人员id串
		
		ExpertsBo bo = new ExpertsBo(this.getFrameconn(),this.userView);
		
		ArrayList idlist = bo.getIdlist();

		ArrayList<String>selectedIdList=new ArrayList<String>();//所选人员的w0101
		ArrayList<String>selectedIdListEncrypt=new ArrayList<String>();//所选人员的w0101

		String msg = bo.importExpert(ids,idlist,selectedIdList);

		for(String str:selectedIdList){
			selectedIdListEncrypt.add(PubFunc.encrypt(str));
		}

		this.getFormHM().put("selectedIdList",selectedIdListEncrypt);
		this.getFormHM().put("msg", msg);
	}
}
