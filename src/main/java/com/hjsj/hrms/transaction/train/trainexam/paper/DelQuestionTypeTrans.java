/**
 * 删除体型 LiWeichao 2011-10-25 17:08:50
 */
package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DelQuestionTypeTrans extends IBusiness {


	public void execute() throws GeneralException {
		String sels = (String) this.getFormHM().get("sels");
		String r5300 = (String) this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			if(sels!=null&&sels.length()>0)
				sels = sels.substring(0, sels.length()-1);
			
			dao.delete("delete from tr_exam_question_type where r5300="+r5300+" and type_id in ("+sels+")", new ArrayList());
			
			//删除对应的试题
			dao.delete("delete from tr_exam_paper where r5300="+r5300+" and type_id in("+sels+")", new ArrayList());
			
			//修改改试卷的平均难度值
			//String sql="update r53 set r5306=(select avg(r5203) m from tr_exam_paper t,r52 r where t.r5200=r.r5200 and r5300="+r5300+") where r5300="+r5300;
			//dao.update(sql);
			
			this.getFormHM().put("flag", "ok");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.getFormHM().put("flag", "error");
			e.printStackTrace();
		}
	}
}
