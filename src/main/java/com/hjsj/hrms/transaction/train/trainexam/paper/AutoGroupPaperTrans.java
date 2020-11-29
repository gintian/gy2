package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.AutoGroupPaperBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AutoGroupPaperTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String flag="ok";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		AutoGroupPaperBo bo = null;
		try {
			//组卷
			if("ok".equals(flag)){
				String sql = "select r5304,r5305 from r53 where r5300="+r5300;
				this.frowset = dao.search(sql);
				int examscore = 0;
				int examtime = 0;
				if(this.frowset.next()){
					examscore = this.frowset.getInt("r5304");
					examtime = this.frowset.getInt("r5305");
				}
				bo = new AutoGroupPaperBo(this.getFrameconn(),this.getUserView(),r5300,examscore,examtime);
				flag = bo.getGroupPaperIds();
				//System.out.println(bo.getIdsList());
				if("ok".equals(flag)){
					ArrayList idsList = bo.getIdsList();
					ArrayList typeIdsList = bo.getTypeIdsList();
					sql = "delete tr_exam_paper where r5300="+r5300;
					dao.delete(sql, new ArrayList());
					
					for (int i = 0; i < idsList.size(); i++) {
						dao.insert("insert into tr_exam_paper(r5200,r5300,type_id,norder) values ("+idsList.get(i)+","+r5300+","+typeIdsList.get(i)+","+(getMaxExamPaper(dao)+1)+")", new ArrayList());
						//修改试题信息的使用次数和最后使用时间
						dao.update("update r52 set r5214=r5214+1,r5215="+Sql_switcher.dateValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))+" where r5200="+idsList.get(i));
					}
				}else
					flag=SafeCode.encode(flag);
			}
		} catch (SQLException e) {
			flag="error";
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	
	private int getMaxExamPaper(ContentDAO dao){
		int i=0;
		try {
			this.frowset = dao.search("select max(norder) m from tr_exam_paper");
			if(this.frowset.next())
				i=this.frowset.getInt("m");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
}
