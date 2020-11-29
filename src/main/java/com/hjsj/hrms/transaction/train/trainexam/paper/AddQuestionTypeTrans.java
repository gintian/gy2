package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddQuestionTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
	    TrainCourseBo bo = new TrainCourseBo();
	    
		ArrayList questiontypes = new ArrayList();
		ArrayList itemlist = new ArrayList();
		try{
			String r5300 = (String)this.getFormHM().get("r5300");
			r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select type_id,type_name from tr_question_type order by norder");
			while(this.frowset.next()){
				CommonData cd = new CommonData(this.frowset.getInt("type_id")+"",this.frowset.getString("type_name"));
				questiontypes.add(cd);
			}
			String columns = "type_id,type_name,max_num,score,know_ids,norder";
			String strsql = "select te.type_id,type_name,max_num," + bo.floatTochar(Sql_switcher.isnull("score","0.0")) + " score,know_ids,te.norder";
			String strwhere =" from tr_exam_question_type te left join tr_question_type tq on te.type_id=tq.type_id where te.r5300=" + r5300;
			
			getStateInfo(strwhere);
			
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("strsql", strsql);
			this.getFormHM().put("strwhere", strwhere);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("questiontypes", questiontypes);
			this.getFormHM().put("itemlist", itemlist);
			
			this.getFormHM().put("order_by", "order by te.norder");
		}
	}
	
	private void getStateInfo(String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select te.norder "+strwhere;
		try {
			this.frowset = dao.search(sql+" order by te.norder");
			if(this.frowset.next())
				this.getFormHM().put("start", this.frowset.getInt("norder")+"");
			
			this.frowset = dao.search(sql+" order by te.norder desc");
			if(this.frowset.next())
				this.getFormHM().put("end", this.frowset.getInt("norder")+"");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}