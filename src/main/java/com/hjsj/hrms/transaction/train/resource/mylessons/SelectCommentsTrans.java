package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SelectCommentsTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		// note为笔记，comment为评论
		String opt = (String) hm.get("opt");
		// 课程id
		String lessonId = (String) hm.get("lesson");
		lessonId = PubFunc.decrypt(SafeCode.decode(lessonId));
		String commentSql = "";
	    
	    String commentWhere = "";
	    
	    String commentOrder = "";
	    
	    String commentColumns = "";
	    StringBuffer buff = new StringBuffer();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if ("note".equalsIgnoreCase(opt)) {
				commentSql = "select id,a0101,comments,"+Sql_switcher.dateToChar("createtime", "yyyy-MM-dd HH:mm:ss")+" createtime";
				
				buff.delete(0, buff.length());
				buff.append("from tr_course_comments where r5100 in (");
				buff.append("select r5100 from r51 where r5000 ='");
				buff.append(lessonId);
				buff.append("') and a0100='");
				buff.append(this.userView.getA0100());
				buff.append("' and nbase='");
				buff.append(this.userView.getDbname());
				buff.append("' and state=1");
				
				commentWhere = buff.toString();
				commentOrder = "order by id desc";
				
				commentColumns = "id,a0101,comments,createtime";
				this.getFormHM().put("isNote", "1");
			} else {
				
				commentSql = "select id,a0101,comments,"+Sql_switcher.dateToChar("createtime", "yyyy-MM-dd HH:mm:ss")+" createtime";
				
				buff.delete(0, buff.length());
				buff.append("from tr_course_comments where r5100 in (");
				buff.append("select r5100 from r51 where r5000 ='");
				buff.append(lessonId);
				buff.append("')");
				buff.append(" and state=0");
				
				commentWhere = buff.toString();
				commentOrder = "order by id desc";
				
				commentColumns = "id,a0101,comments,createtime";
				
				this.getFormHM().put("isNote", "0");
				
			}
			String moduleFlag = "0";
			if(hm.containsKey("moduleFlag")){
				moduleFlag = (String)hm.get("moduleFlag");
				hm.remove("moduleFlag");
			}
			this.getFormHM().put("moduleFlag", moduleFlag);
			this.getFormHM().put("commentSql", commentSql);
			this.getFormHM().put("commentWhere", commentWhere);
			this.getFormHM().put("commentOrder", commentOrder);
			this.getFormHM().put("commentColumns", commentColumns);
			
			
			// 查询课程名称
			String sql = "select r5003 from r50 where r5000=" + lessonId;
			this.frowset = dao.search(sql);
			String lessonName = "";
			if (this.frowset.next()) {
				lessonName = this.frowset.getString("r5003");
				lessonName = lessonName == null ? "" : lessonName;
			}
			
			this.getFormHM().put("lessonName", lessonName);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
