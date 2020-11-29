package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;

public class SaveCommentTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5100 = (String) this.getFormHM().get("R5100");
		r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		String nbase = (String) this.userView.getDbname();
		String a0100 = (String) this.userView.getA0100();
		String b0110 = (String) this.userView.getUnit_id();
		String e0122 = (String) this.userView.getUserDeptId();
		String e01a1 = (String) this.userView.getUserPosId();
		String a0101 = (String) this.userView.getUserFullName();
		String createtime = (String) this.getFormHM().get("createtime");
		String state = (String) this.getFormHM().get("state");
		String comment = (String) this.getFormHM().get("comment");
		this.getFormHM().put("comment", PubFunc.toHtml(comment));//jsp页面显示
		String id = (String) this.getFormHM().get("id");
		String oldId = id;
		int sum = 0;
		RecordVo vo = new RecordVo("tr_course_comments");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if (oldId == null || oldId.length() <= 0) {
				IDFactoryBean idFactory = new IDFactoryBean();
				id = idFactory.getId("tr_course_comments.id", "", this.frameconn);
				this.getFormHM().put("id", id);
				
			}
			vo.setString("a0100", a0100);
			vo.setInt("id", Integer.parseInt(id));
			vo.setInt("r5100", Integer.parseInt(r5100));
			vo.setString("nbase", nbase);
			vo.setString("b0110", b0110);
			vo.setString("e0122", e0122);
			vo.setString("e01a1", e01a1);
			vo.setString("a0101", a0101);
			vo.setString("comments", comment);
			vo.setInt("state", Integer.parseInt(state));
			Date date = DateUtils.getSqlDate(createtime, "yyyy-MM-dd HH:mm:ss");
			vo.setDate("createtime", date);
			
			if (oldId == null || oldId.length() <= 0) {
				sum = dao.addValueObject(vo);
			} else {
				sum = dao.updateValueObject(vo);
			}
			
			if(sum != 0)
			    this.getFormHM().put("flag", "ture");
			else
			    this.getFormHM().put("flag", "false");
			
			this.getFormHM().put("a0101", a0101);
			this.getFormHM().put("createtime", DateUtils.format(date, "yyyy年MM月dd日 HH:mm"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
