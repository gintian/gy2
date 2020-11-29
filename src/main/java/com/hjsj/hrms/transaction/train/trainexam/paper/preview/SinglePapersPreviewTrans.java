package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.*;

public class SinglePapersPreviewTrans extends IBusiness {

	public void execute() throws GeneralException {
		Map map = (HashMap) this.getFormHM().get("requestPamaHM");
		String paper_id = (String) map.get("paper_id");
		paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
		String flag = (String) map.get("flag");
		String r5300 = (String) map.get("r5300");//试卷id
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String exam_type = (String)map.get("exam_type");
		String returnId = (String)map.get("returnId");
		
		int count = 0;
		ArrayList typeList = new ArrayList();//类型位置顺序
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r5301"/*,r5303*/+",r5304,r5305,r5317 from r53 where r5300="+r5300;
		try {
			
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				String r5317 = this.frowset.getString("r5317");
				if (r5317 == null || r5317.length() <= 0) {
					r5317 = "2";
				}
				this.getFormHM().put("title", this.frowset.getString("r5301"));
				this.getFormHM().put("examscore", String.valueOf(this.frowset.getInt("r5304")));
				this.getFormHM().put("isSingle", r5317);
				int timeLength = this.frowset.getInt("r5305");
				this.getFormHM().put("examtime", String.valueOf(timeLength));
				this.getFormHM().put("remaintime", String.valueOf(timeLength*60));//剩余时间 秒
				//String examdescribe = this.frowset.getString("r5303");
				//examdescribe=examdescribe!=null&&examdescribe.length()>1?examdescribe.replace("\r\n", "<br/>"):"";
				//this.getFormHM().put("examdescribe", examdescribe);

					String sql2 = "select r5506,r5507 from r55 where r5400="+ paper_id + " and a0100='"
							+ this.userView.getA0100() + "' and nbase='"+ this.userView.getDbname() + "'";
					this.frowset = dao.search(sql2);
					Date start = null;
					if (this.frowset.next()) {
						start = this.frowset.getDate("r5506");
					}
				
				Calendar ca = Calendar.getInstance();
				if(start != null){
					ca.setTime(start);
				}
				int startHour =  ca.get(Calendar.HOUR_OF_DAY);
				int startMinute = ca.get(Calendar.MINUTE);
				int startSecond = ca.get(Calendar.SECOND);
				String startTime ="";
				if (startHour > 9) {
					startTime += startHour;
				} else {
					startTime += "0" + startHour;
				}
				startTime += ":";
				
				if (startMinute > 9) {
					startTime += startMinute;
				} else {
					startTime += "0" + startMinute;
				}
				startTime += ":";
				
				if (startSecond > 9) {
					startTime += startSecond;
				} else {
					startTime += "0" + startSecond;
				}

				ca.add(Calendar.MINUTE, timeLength);
				int endHour =  ca.get(Calendar.HOUR_OF_DAY);
				int endMinute = ca.get(Calendar.MINUTE);
				int endSecond = ca.get(Calendar.SECOND);
				String endTime ="";
				if (endHour > 9) {
					endTime += endHour;
				} else {
					endTime += "0" + endHour;
				}
				endTime += ":";
				
				if (endMinute > 9) {
					endTime += endMinute;
				} else {
					endTime += "0" + endMinute;
				}
				endTime += ":";
				
				if (endSecond > 9) {
					endTime += endSecond;
				} else {
					endTime += "0" + endSecond;
				}
				
				this.getFormHM().put("startTime", DateUtils.format(ca.getTime(), "yyyy-MM-dd") + " " + startTime);
				this.getFormHM().put("endTime", DateUtils.format(ca.getTime(), "yyyy-MM-dd") + " " + endTime);
			}
			
			//类型位置顺序
			sql="select type_id from tr_exam_question_type where r5300="+r5300+" order by norder";
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				typeList.add(String.valueOf(this.frowset.getInt("type_id")));
			}
			
			//该试卷试题数
			sql="select count(1) from tr_exam_paper where r5300="+r5300;
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				count = this.frowset.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("exam_type", exam_type);
		this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
		this.getFormHM().put("returnId", returnId);
		this.getFormHM().put("count", String.valueOf(count));
		this.getFormHM().put("typeList", typeList);
	}
}
