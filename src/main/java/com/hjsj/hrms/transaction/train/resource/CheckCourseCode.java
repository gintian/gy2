package com.hjsj.hrms.transaction.train.resource;
/**
 * 检测左侧分类树中的节点是否代表了一门课程或这个分类是否存在
 */

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CheckCourseCode extends IBusiness{

	public void execute() throws GeneralException {
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
		String sql = "select r5000 from r50 where codeitemid='"+codeitemid+"'";
		String sqls = "select codeitemid from codeitem where codesetid='55'";
		if(codeitemid!=null&&!"".equals(codeitemid)){
			sqls+=" and codeitemid='"+codeitemid+"'";
		}
		String flag = "true";
		if (codeitemid == null || "".equals(codeitemid)) {
			flag = "true";
		}else{
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sqls);// 检测该分类是否存在

				if (!this.frowset.next()) {
					flag = "false";
					return;
				}
				this.frowset = dao.search(sql);// 检测该分类是否是一门课程
				if (this.frowset.next()) {
					flag = "false";
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("flag", flag);
	}

}
