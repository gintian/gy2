package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteNotesTrans extends IBusiness {

	public void execute() throws GeneralException {
	    String a0100 = (String) this.userView.getA0100();
        String r5100 = (String) this.getFormHM().get("R5100");
        r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
        String nbase = (String) this.userView.getDbname();
		
		String id = (String) this.getFormHM().get("id");
		String upId = "";
		String nextId = "";
		String comment = "";
		int i = 0;
		RecordVo vo = new RecordVo("tr_course_comments");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer buff = new StringBuffer();
			if (id != null && id.length() > 0){
				
				buff.append("delete from tr_course_comments where a0100='");
				buff.append(a0100);
				buff.append("' and nbase='");
				buff.append(nbase);
				buff.append("' and r5100=");
				buff.append(r5100);
				buff.append(" and id = ");
				buff.append(id);
				
				i = dao.delete(buff.toString(), new ArrayList());
				
				buff.delete(0, buff.length());
				buff.append("select max(id) id from tr_course_comments where state=1 and a0100='");
				buff.append(a0100);
				buff.append("' and nbase='");
				buff.append(nbase);
				buff.append("' and r5100=");
				buff.append(r5100);
				buff.append(" and id <");
				buff.append(id);
				
				this.frowset = dao.search(buff.toString());
				if (this.frowset.next()) {
					upId = this.frowset.getString("id");					
				}
				
				buff.delete(0, buff.length());
				buff.append("select min(id) id from tr_course_comments where state=1 and a0100='");
				buff.append(a0100);
				buff.append("' and nbase='");
				buff.append(nbase);
				buff.append("' and r5100=");
				buff.append(r5100);
				buff.append(" and id >");
				buff.append(id);
				
				this.frowset = dao.search(buff.toString());
				if (this.frowset.next()) {
					id = this.frowset.getString("id");	
					if(id!=null&&id.length()>0){
						vo.setInt("id", Integer.parseInt(id));
						vo = dao.findByPrimaryKey(vo);
						comment = vo.getString("comments");
					}
				}
				
				buff.delete(0, buff.length());
				buff.append("select min(id) id from tr_course_comments where state=1 and a0100='");
				buff.append(a0100);
				buff.append("' and nbase='");
				buff.append(nbase);
				buff.append("' and r5100=");
				buff.append(r5100);
				buff.append(" and id >");
				buff.append(id);
				
				this.frowset = dao.search(buff.toString());
				if (this.frowset.next()) {
					nextId = this.frowset.getString("id");					
				}
			}
			
			if(i != 0)
			    this.getFormHM().put("flag", "ture");
			else
			    this.getFormHM().put("comment", "flase");
			
			this.getFormHM().put("comment", comment);
			this.getFormHM().put("id", id);
			this.getFormHM().put("upId", upId);
			this.getFormHM().put("nextId", nextId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
