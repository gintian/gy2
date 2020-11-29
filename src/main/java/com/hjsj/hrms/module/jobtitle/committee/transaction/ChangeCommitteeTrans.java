package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新增、保存（修改）、删除评委会
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class ChangeCommitteeTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String msg = "";
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String)this.getFormHM().get("type");//”1”/”2”/”3”(新增/修改/删除)
			
			if("1".equals(type)) {//新增
				String committee_name = (String)this.getFormHM().get("committee_name");//评委会名称
				String committee_type = String.valueOf(this.getFormHM().get("committee_type"));//类别
				String description = (String)this.getFormHM().get("description");//描述
				String b0110 = (String)this.getFormHM().get("b0110");//所属组织
				String create_fullname = (String)this.getFormHM().get("create_fullname");//创建人
				String create_time = (String)this.getFormHM().get("create_time");//创建日期
				String committee_id = committeeBo.createCommittee(committee_name, committee_type, description, b0110, create_fullname, create_time);
				committee_id = PubFunc.encrypt(committee_id);
				this.getFormHM().put("committee_id", committee_id);
				this.getFormHM().put("committee_name", committee_name);
			} else if("2".equals(type)) {//修改
				String committee_id = (String)this.getFormHM().get("committee_id");//评委会编号
				committee_id = PubFunc.decrypt(committee_id);
				String committee_name = (String)this.getFormHM().get("committee_name");//评委会名称
				String committee_type = String.valueOf(this.getFormHM().get("committee_type"));//类别
				String description = (String)this.getFormHM().get("description");//描述
				String b0110 = (String)this.getFormHM().get("b0110");//所属组织
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				list = committeeBo.getModifyList(committee_name, committee_type, description, b0110);
				msg = committeeBo.modifyCommittee(committee_id, list);
				
			} else if("3".equals(type)) {//删除
				String committee_id = (String)this.getFormHM().get("committee_id");//评委会编号
				committee_id = PubFunc.decrypt(committee_id);
				msg = committeeBo.deleteCommittee(committee_id);
			}
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
