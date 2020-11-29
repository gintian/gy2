/**
 * 调整试卷显示顺序 LiWeichao 2011-10-25 17:08:50
 */
package com.hjsj.hrms.transaction.train.trainexam.paper;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SortExamPaperTrans extends IBusiness {

	/**
	 * flag=1 考试试卷排序   flag=2 添加题型 题型排序
	 */
	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String type = (String)this.getFormHM().get("type");
		String type_id = (String)this.getFormHM().get("type_id");
		String strwhere = (String)this.getFormHM().get("strwhere");
		strwhere = PubFunc.keyWord_reback(strwhere);
		
		if("1".equals(flag))//flag=1 考试试卷排序
			sortExamItem(type,r5300,strwhere);
		if("2".equals(flag))//添加题型 题型排序
			sortQuestionTypeItem(type,r5300,type_id,strwhere);
		
		this.getFormHM().put("flag", "ok");
	}

	
	private void sortQuestionTypeItem(String type, String r5300,
			String type_id, String strwhere) {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sff = new StringBuffer();
		sff.append("select te.norder ");
		sff.append(strwhere);
		sff.append(" and te.r5300="+r5300);
		sff.append(" and te.type_id="+type_id);
		try {
			this.frowset = dao.search(sff.toString(),1,1);
			if(this.frowset.next()){
				int norder = this.frowset.getInt("norder");
				
				sff.setLength(0);
				sff.append("select te.r5300,te.type_id,te.norder ");
				sff.append(strwhere);
				if("up".equals(type)){
					sff.append(" and te.norder<"+norder);
					sff.append(" order by te.norder desc");
				}else{
					sff.append(" and te.norder>"+norder);
					sff.append(" order by te.norder asc");
				}
				
				this.frowset = dao.search(sff.toString(),1,1);
				if(this.frowset.next()){
					int tmpNorder = this.frowset.getInt("norder");
					int tmpR5300 = this.frowset.getInt("r5300");
					int tmpTypeId = this.frowset.getInt("type_id");
					dao.update("update tr_exam_question_type set norder="+norder+" where r5300="+tmpR5300+" and type_id="+tmpTypeId);
					dao.update("update tr_exam_question_type set norder="+tmpNorder+" where r5300="+r5300+" and type_id="+type_id);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private void sortExamItem(String type,String r5300,String strwhere){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sff = new StringBuffer();
		sff.append("select norder ");
		sff.append(strwhere);
		sff.append(" and r5300="+r5300);
		try {
			this.frowset = dao.search(sff.toString(),1,1);
			if(this.frowset.next()){
				int norder = this.frowset.getInt("norder");
				
				sff.setLength(0);
				sff.append("select r5300,norder ");
				sff.append(strwhere);
				if("up".equals(type)){
					sff.append(" and norder<"+norder);
					sff.append(" order by norder desc");
				}else{
					sff.append(" and norder>"+norder);
					sff.append(" order by norder asc");
				}
				
				this.frowset = dao.search(sff.toString(),1,1);
				if(this.frowset.next()){
					int tmpNorder = this.frowset.getInt("norder");
					int tmpR5300 = this.frowset.getInt("r5300");
					dao.update("update r53 set norder="+norder+" where r5300="+tmpR5300);
					dao.update("update r53 set norder="+tmpNorder+" where r5300="+r5300);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
