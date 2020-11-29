package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.resource.MyLessonBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class LearnLessonsTrans extends IBusiness {

	public void execute() throws GeneralException {
        if (this.userView.getA0100() == null || this.userView.getA0100().length() <= 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","非自助用户不能使用此功能！","",""));
        }

	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String opt = (String) hm.get("opt");
		if (StringUtils.isEmpty(opt))
		    opt = "all";
		
		String flag = (String) hm.get("flag");
		
		hm.remove("flag");
		
		StringBuffer myLessonSql = new StringBuffer();
		StringBuffer myLessonWhere = new StringBuffer();
		String myLessonOrder = "";
		String myLessonColumns = "";
		
		StringBuffer buff = new StringBuffer();
		StringBuffer buff1 = new StringBuffer();
		StringBuffer buff2 = new StringBuffer();
		StringBuffer sbuff = new StringBuffer();
		RowSet answerRowSet = null;
		RowSet scor = null;
		String nbase = this.userView.getDbname();
		String id = this.userView.getA0100();
		String disableExam = "0";
		//陈旭光修改：学习课程结束优化通过标识是考试通过的并且关联相关的考试试卷，考试通过后再更改为已学
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql1 = "select R5501,R5503,r5400 from r55 where nbase='"+nbase+"' and A0100='"+id+"'"; 
			this.frowset = dao.search(sql1);
			while (this.frowset.next()) {
				int r5400 = frowset.getInt("r5400");
				float score = frowset.getFloat("R5501") + frowset.getFloat("R5503");
				buff.delete(0, buff.length());
				buff.append("select r5000 from tr_lesson_paper where r5300=(");
				buff.append("select r5300 from r54 where r5400=");
				buff.append(r5400);
				buff.append(")");
				answerRowSet = dao.search(buff.toString());
				
				while (answerRowSet.next()) {
					int r5000 = answerRowSet.getInt("r5000");
					String sql0 = "select R5028,r5000 from r50 where R5018='2' and r5000=" + r5000;
					scor = dao.search(sql0);
					if (scor != null) {
						while (scor.next()) {
							float sc = scor.getFloat("R5028");
							if (score >= sc) {
								StringBuffer updata = new StringBuffer();
								updata.append("update tr_selected_lesson set state=2 where r5000=" + r5000);
								updata.append(" and nbase='" + nbase);
								updata.append("' and a0100='" + id + "'");
								dao.update(updata.toString());
							}
						}
					}
				}
			}			
				//陈旭光修改：更新通过标识不是考试通过的课程的状态
				buff1.append("update tr_selected_lesson set state=2 where nbase='" +nbase);
				buff1.append("' and a0100='" +id+"' and");
				buff1.append(" exists (select r5000  from r50 ");
				buff1.append(" where R5018<>'2' and tr_selected_lesson.r5000=r50.r5000 ) and lprogress >= 100");
				//陈旭光修改：更新通过标识是考试通过的且关联试卷但未关联考试计划的 课程的状态
				sbuff.append("update tr_selected_lesson set state=2 where nbase='" +nbase);
				sbuff.append("' and a0100='" +id+"' and r5000 in (");
				sbuff.append("select r50.r5000 from r50 join tr_lesson_paper a on r50.r5000=a.r5000 where r50.r5018 ='2' and a.r5300 not in (select r5300 from r54)");
				sbuff.append(") and lprogress >= 100");
				//陈旭光修改：更新通过标识是考试通过的但未关联试卷的 课程的状态
				buff2.append("update tr_selected_lesson set state=2 where nbase='" +nbase);
				buff2.append("' and a0100='" +id+"' and");
				buff2.append(" exists (select r5000  from r50 ");
				buff2.append(" where R5018 = '2' and not exists (select r5000 from tr_lesson_paper a where r50.r5000 = a.r5000) and tr_selected_lesson.r5000=r50.r5000 ) and lprogress >= 100");
				dao.update(buff1.toString());
				dao.update(buff2.toString());
				MyLessonBo bo = new MyLessonBo(this.frameconn, this.userView);
				String enableArch = bo.getDisableExamOrEnableArch("");
				//未勾选自测考试合格自动归档时，考试自动通过的课程关联了试卷但试卷未关联考试计划，则学习进度到100%后更改为已学
				if(!"1".equals(enableArch))
				    dao.update(sbuff.toString());
//				String sql = "update tr_selected_lesson set state=2 where lprogress >= 100";
//				dao.update(sql);
			
		
			if ("ing".equalsIgnoreCase(opt)) {
			    myLessonSql.append("select a.id,a.r5000, b.r5003,a.learnednum,a.lprogress,a.state,b.R5024,b.r5004,");
			    myLessonSql.append(Sql_switcher.dateToChar("a.start_date", "yyyy-MM-dd")+" start_date,");
			    myLessonSql.append(Sql_switcher.dateToChar("a.end_date", "yyyy-MM-dd")+" end_date");
			    
			    myLessonWhere.append(" from r50 b left join tr_selected_lesson a");
			    myLessonWhere.append(" on a.r5000=b.r5000"); 
			    myLessonWhere.append(" where (a.state=0 or a.state=1) and a.nbase='");
			    myLessonWhere.append(this.userView.getDbname());		    
			    myLessonWhere.append("' and a.a0100='");
			    myLessonWhere.append(this.userView.getA0100());
			    myLessonWhere.append("' and b.r5022='04'");
			    //陈旭光修改：正学课程分为正学必修和正学选修
			    if("1".equalsIgnoreCase(flag)){
			    	myLessonWhere.append(" and a.lesson_from<>1");
			    }else if("2".equalsIgnoreCase(flag)){
			    	myLessonWhere.append(" and a.lesson_from=1");
			    }
			    
				myLessonColumns = "id,r5000,r5003,start_date,end_date,learnednum,lprogress,state,R5024,r5004";
				//获取进度是否要达到100%才允许考试
				disableExam = bo.getDisableExamOrEnableArch("my");
				
				this.getFormHM().put("isLearned", "0");
				this.getFormHM().put("disableExam", disableExam);
			} else if ("ed".equalsIgnoreCase(opt)) {
				myLessonSql.append("select a.id,a.r5000, b.r5003,a.start_date,a.end_date,a.learnednum,a.state,b.R5024,b.r5004");
				myLessonWhere.append(" from (");
				myLessonWhere.append(" select id,r5000,"+Sql_switcher.dateToChar("start_date", "yyyy-MM-dd")+" start_date,"+Sql_switcher.dateToChar("end_date", "yyyy-MM-dd")+" end_date,learnednum,state from tr_selected_lesson "); 
				myLessonWhere.append(" where state=2 and nbase='");
				myLessonWhere.append(this.userView.getDbname());
				myLessonWhere.append("' and a0100='");
				myLessonWhere.append(this.userView.getA0100());
				myLessonWhere.append("'");
				//陈旭光修改：已学课程分为已学必修和已学选修
			    if("3".equalsIgnoreCase(flag)){
			    	myLessonWhere.append(" and lesson_from<>1");
			    }else if("4".equalsIgnoreCase(flag)){
			    	myLessonWhere.append(" and lesson_from=1");
			    }
				myLessonWhere.append(") a left join( select r5000,r5003,r5004, ");
				myLessonWhere.append("R5024 from r50 ) b on a.r5000=b.r5000");
				
				myLessonColumns = "id,r5000,r5003,start_date,end_date,learnednum,state,R5024,r5004";
				this.getFormHM().put("isLearned", "1");
			}else {
                ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
                String selectItems = constantbo.getNodeAttributeValue("/param/lesson_hint", "viewItems");
                ArrayList itemList = new ArrayList();
                myLessonSql.append("select a.id,a.r5000,a.state,a.lesson_from, b.r5003,a.learnednum," + Sql_switcher.isnull("a.lprogress", "0") + " lprogress,a.state,b.R5024,b.r5004,");
                myLessonSql.append(Sql_switcher.dateToChar("a.start_date", "yyyy-MM-dd")+" start_date,");
                myLessonSql.append(Sql_switcher.dateToChar("a.end_date", "yyyy-MM-dd")+" end_date,");
                
                if(StringUtils.isNotEmpty(selectItems)) {
                    String[] viewItems = selectItems.split(",");
                    for(int i = 0; i < viewItems.length; i++) {
                        if("SCORE".equalsIgnoreCase(viewItems[i])) {
                            FieldItem fi = new FieldItem();
                            fi.setItemid("SCORE");
                            fi.setItemdesc("成绩");
                            fi.setItemlength(3);
                            fi.setItemtype("N");
                            fi.setDecimalwidth(2);
                            fi.setCodesetid("0");
                            itemList.add(fi);
                        } else {
                            FieldItem fi = DataDictionary.getFieldItem(viewItems[i], "R50");
                            if(fi == null)
                                continue;
                            
                            myLessonSql.append("b." + fi.getItemid() + ",");
                            myLessonColumns += fi.getItemid() + ",";
                            itemList.add(fi);
                        }
                    }
                    
                }
                
                myLessonSql.setLength(myLessonSql.length() - 1);
                
                myLessonWhere.append(" from r50 b left join tr_selected_lesson a");
                myLessonWhere.append(" on a.r5000=b.r5000"); 
                myLessonWhere.append(" where a.nbase='");
                myLessonWhere.append(this.userView.getDbname());            
                myLessonWhere.append("' and a.a0100='");
                myLessonWhere.append(this.userView.getA0100());
                myLessonWhere.append("' and b.r5022='04'");
                
                String state = (String) hm.get("state");
                hm.remove("state");
                if(StringUtils.isEmpty(state))
                    state = (String) this.getFormHM().get("state");
                
                state = StringUtils.isEmpty(state) ? "1" : state;
                if("1".equalsIgnoreCase(state))
                    myLessonWhere.append(" and (a.state=0 or a.state=1)");
                else if("2".equalsIgnoreCase(state))
                    myLessonWhere.append(" and a.state='2'");
                
                String searchLesson = (String) hm.get("searchLesson");
                hm.remove("searchLesson");
                if(StringUtils.isNotEmpty(searchLesson)) {
                    searchLesson = PubFunc.hireKeyWord_filter_reback(searchLesson);
                    searchLesson = PubFunc.convertCharacterUrlSpecial(searchLesson);
                    searchLesson = PubFunc.hireKeyWord_filter(searchLesson);
                    myLessonWhere.append(" and b.r5003 like '%" + searchLesson + "%'");
                }
                
                myLessonColumns += "id,r5000,state,lesson_from,r5003,start_date,end_date,learnednum,lprogress,state,R5024,r5004";
                
                this.getFormHM().put("isLearned", "0");
                this.getFormHM().put("state", state);
                this.getFormHM().put("searchLesson", searchLesson);
                this.getFormHM().put("viewItemList", itemList);
            }
			
			myLessonOrder = " order by id";
			
			this.getFormHM().put("myLessonSql", myLessonSql.toString());	    
			this.getFormHM().put("myLessonWhere", myLessonWhere.toString());		    
			this.getFormHM().put("myLessonOrder", myLessonOrder);
			this.getFormHM().put("myLessonColumns", myLessonColumns);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (answerRowSet != null)
					answerRowSet.close();
				if (scor != null)
					scor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
