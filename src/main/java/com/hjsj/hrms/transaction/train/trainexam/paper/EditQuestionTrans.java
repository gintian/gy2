package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class EditQuestionTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5300 = (String)this.getFormHM().get("r5300");//试卷
		String type_id = (String)this.getFormHM().get("type_id");//试题类型
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String order = (String)hm.get("order");//题型顺序(即：第几部分)
		//System.out.println(r5300+"--"+type_id+"--"+order);
		String title = QuestionesBo.getTitle(type_id, r5300, order);
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		//System.out.println(bo.getTitle(type_id, r5300, order));
		
		String columns="r5200,r5205,r5213,r5207,norder";//试题编号,试题内容,试题分数,试题选项,试题顺序
		String strsql="select r.r5200,r5205,r5213,r5207,t.norder";
		String strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200 where r5300="+r5300+" and t.type_id="+type_id;
		
		 getCount(strwhere);
		
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("strsql", strsql);
		this.getFormHM().put("strwhere", strwhere);
		this.getFormHM().put("order_by", " order by t.norder");
		this.getFormHM().put("title", title);
	}
	
	private void getCount(String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			int num = 0;
			this.frowset = dao.search("select count(1) num"+strwhere);
			if(this.frowset.next()){
				num = this.frowset.getInt("num");
			}
			this.getFormHM().put("end", num+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
