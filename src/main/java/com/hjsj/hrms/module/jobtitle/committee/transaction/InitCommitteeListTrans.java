package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 加载当前操作人作人所在单位范围内的院级聘委会
 * @author Administrator
 *
 */
public class InitCommitteeListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);
			String b0110 = this.userView.getUnitIdByBusi("9");//取得所属单位
			/** 当前操作人所在单位范围内的院级聘委会 **/
			ArrayList<HashMap<String, String>> committeeList = committeeBo.getCommittee(b0110);//获取评委会
			CommonData commData=null;
			List list = new ArrayList();
			for(HashMap<String, String> map : committeeList){
				String id = map.get("committee_id");
				String name = map.get("committee_name");
				commData = new CommonData(id,name);
				list.add(commData);
			}
			this.getFormHM().put("committeeList",committeeList);	
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
