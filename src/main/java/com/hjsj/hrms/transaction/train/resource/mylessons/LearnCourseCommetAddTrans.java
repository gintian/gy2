package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.sql.SQLException;


/**
 * 
 * <p>Title: LearnCourseCommetAddTrans </p>
 * <p>Description:  添加在线学习内容的交易类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-18 下午5:35:39</p>
 * @author liuyang
 * @version 1.0
 */
public class LearnCourseCommetAddTrans extends IBusiness {


	@Override
	public void execute() throws GeneralException {
		String flag = (String) this.getFormHM().get("flag");
		String courseid = (String) this.getFormHM().get("courseid");
		courseid = PubFunc.decrypt(SafeCode.decode(courseid));
		String nbase = (String) this.userView.getDbname();
		String a0100 = (String) this.userView.getA0100();
		String b0110 = (String) this.userView.getUnit_id();
		String e0122 = (String) this.userView.getUserDeptId();
		String e01a1 = (String) this.userView.getUserPosId();
		String a0101 = (String) this.userView.getUserFullName();
		java.util.Calendar c=java.util.Calendar.getInstance(); 
		java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currData = f.format(c.getTime());
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
			}
			
			vo.setString("a0100", a0100);
			vo.setInt("id", Integer.parseInt(id));
			vo.setInt("r5100", Integer.parseInt(courseid));
			vo.setString("nbase", nbase);
			vo.setString("b0110", b0110);
			vo.setString("e0122", e0122);
			vo.setString("e01a1", e01a1);
			vo.setString("a0101", a0101);
			vo.setString("comments", comment);
			vo.setInt("state", Integer.parseInt(flag));
			Date date = DateUtils.getSqlDate(currData, "yyyy-MM-dd HH:mm:ss");
			vo.setDate("createtime", date);
			
			if (oldId == null || oldId.length() <= 0) {
				sum = dao.addValueObject(vo);
			} else {
				sum = dao.updateValueObject(vo);
			}
			courseid=SafeCode.encode(PubFunc.encrypt(courseid));
			this.getFormHM().put("courseid", courseid);
			this.getFormHM().put("flag", flag);
		} catch (GeneralException e1) {
		    e1.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e1);
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw GeneralExceptionHandler.Handle(e1);
		}
	}
	
}
