package com.hjsj.hrms.businessobject.train.trainexam.exam.mytest;

import com.hjsj.hrms.businessobject.train.resource.MyLessonBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 * Title:QuestionesBo
 * </p>
 * <p>
 * Description:自测考试业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-22
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyTestBo {
	
	// 数据库连接
	private Connection conn;
	
	public MyTestBo(){
		
	}
	
	public MyTestBo(Connection conn) {
		this.conn = conn;
	}
	
	
	/**
	 * 根据课程号查询试卷编号
	 * @param r5000 课程号
	 * @return 试卷编号
	 */ 
	public String getR5300ByR5000(String r5000) {
		String r5300 = "";
		ContentDAO dao = new ContentDAO(this.conn);
		
		RowSet rs = null;
		try {
			String sql = "select r5300 from tr_lesson_paper where r5000=" + r5000;
			rs = dao.search(sql);
			if (rs.next()) {
				r5300 = "" + rs.getInt("r5300");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return r5300;
	}
	
	/**
	 * 根据课程号查询试卷编号
	 * @param r5000 课程号
	 * @return 试卷编号
	 */ 
	public String getStringByR5000(String r5000,String columName) {
		String r5300 = "";
		ContentDAO dao = new ContentDAO(this.conn);
		
		RowSet rs = null;
		try {
			String sql = "select " + columName + " from r50 where r5000=" + r5000;
			rs = dao.search(sql);
			if (rs.next()) {
				r5300 =  rs.getString(columName);
				r5300 = r5300 == null ? "" : r5300;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return r5300;
	}
	
	/**
	 * 判断答案是否正确
	 * @param answer
	 * @param quest
	 * @return
	 */
	public boolean judge(String answer, String quest) {
		// 用户答案
		answer = answer == null ? "" : answer;
		if (answer.startsWith(",")) {
			answer = answer.substring(1);
		}
		
		// 试题正确答案
		quest = quest == null ? "" : quest;
		if(StringUtils.isNotEmpty(quest) && quest.length() > 1) {
            String[] quests = quest.split(",");
            quest = "";
            for(int i = 0; i < quests.length; i++) {
                if(StringUtils.isEmpty(quests[i])) {
                    continue;
                }
                
                char[] ch = quests[i].toCharArray();
                if (ch[0] >= 'A' && ch[0] <= 'Z') {
                    quest += quests[i] + ",";
                }
            }
        }
		
		if (quest.startsWith(",")) {
			quest = quest.substring(1);
		}
		
		// 判断答案是否正确
		boolean judge = true;
		
		if (answer.split(",").length == quest.split(",").length) {
			String []qu = quest.split(",");
			for (int i = 0; i < qu.length; i++) {
				if (! answer.contains(qu[i])) {
					judge = false;
					break;
				}
			}
		} else {
			judge = false;
		}
		
		return judge;
	}
	
	
	/**
	 * 更新总分数
	 * @param userView
	 * @param paper_id
	 */
	public void updateScore(UserView userView, String paper_id, String type, String r5300, String r5000, String submit) {
		ContentDAO dao = new ContentDAO(this.conn);
		CallableStatement cstmt = null;
		try {
		//  更新总分
			String update = "update tr_selfexam_paper set score=(select sum(score) from tr_exam_answer where a0100=? and nbase=? and exam_no=? and exam_type=?) where paper_id=?";
			ArrayList sqlList  = new ArrayList();
			sqlList.add(userView.getA0100());
			sqlList.add(userView.getDbname());
			sqlList.add(Integer.valueOf(paper_id));
			sqlList.add(Integer.valueOf(type));
			sqlList.add(Integer.valueOf(paper_id));
			dao.update(update, sqlList);
			//自测考试归档（enableArch不为1时，不进行归档 ）
			MyLessonBo bo = new MyLessonBo(this.conn, userView);
			String enableArch = bo.getDisableExamOrEnableArch("");
			if(!"1".equals(submit) || StringUtils.isEmpty(r5000) || !"1".equals(enableArch)) {
                return;
            }
			
			if(!isExistPro()) {
                throw new GeneralException("", "不存在prc_train_score_arch存储过程，不能归档！", "", "");
            }
			
			String score = getScore(userView, r5300, paper_id);
			//课程分类
			String codeitem = "";
			//课程对应的codeitemid
			String courseid = "";
			String code = getCodeitemid(r5000);
			if(!StringUtils.isEmpty(code) && code.indexOf(":") > 0){
			    String[] codes = code.split(":");
			    codeitem = codes[0] == null ? "" : codes[0];
			    courseid = codes[1] == null ? "" : codes[1];
			}
			
			try {
    			cstmt = this.conn.prepareCall("{call prc_train_score_arch(?,?,?,?,?,?,?)} ");
    			cstmt.setString(1, userView.getDbname());
    			cstmt.setString(2, userView.getA0100());
    			cstmt.setString(3, courseid);
    			cstmt.setString(4, codeitem);
    			cstmt.setString(5, r5300);
    			cstmt.setString(6, paper_id);
    			cstmt.setString(7, score);
    			cstmt.execute();
			} catch (Exception e) {
	            e.printStackTrace();
	            throw new GeneralException("", "调用存储过程出错！" + e.getMessage(), "", "");
	        }
			    
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(cstmt);
		}
	}
	
	/**
	 * 更新总分数
	 * @param userView
	 * @param paper_id
	 */
	public void updateTestScore(UserView userView, String paper_id, String type) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
		//  更新总分
		    StringBuffer sql = new StringBuffer();
            sql.append("update r55 set r5504=(");
            sql.append(" select sum(score) from tr_exam_answer a left join");
            sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200 and ");
            sql.append(" a.r5300=p.r5300 where a.a0100=?");
            sql.append(" and a.nbase=?");
            sql.append(" and a.exam_no=?");
            sql.append(" and a.exam_type=?)");
            sql.append(" where r5400=? and a0100=? and nbase=?");
            
            ArrayList sqlList  = new ArrayList();
            sqlList.add(userView.getA0100());
            sqlList.add(userView.getDbname());
            sqlList.add(Integer.valueOf(paper_id));
            sqlList.add(Integer.valueOf(type));
            sqlList.add(Integer.valueOf(paper_id));
            sqlList.add(userView.getA0100());
            sqlList.add(userView.getDbname());
            dao.update(sql.toString(), sqlList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查考试时间是否允许考试
	 * @param r5400
	 * @return
	 */
	public String checkTime(String r5400) {
		String flag = "";
		Connection conns = null;
		RowSet rs = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			long now = new Date().getTime();

			String sql = "select r5405,r5406 from r54 where r5400="+r5400;
			rs = dao.search(sql);
			if (rs.next()) {
				long start = rs.getTimestamp("r5405").getTime();
				long end = rs.getTimestamp("r5406").getTime();
				if (now >= start && now < end) {
					flag = "1";
				} else if (now < start){
					flag = "2";
				} else if (now > end) {
					flag = "3";
				}
					 
			} else {
				flag = "0";
				//throw GeneralExceptionHandler.Handle(new GeneralException(flag));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conns != null) {
					conns.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	/**
	 * 自动阅卷
	 * @param userView
	 * @param paper_id
	 * @param type
	 */
	public void autoComputeExam(UserView userView, String paper_id, Boolean autoRelease){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			
			//查询试卷是由什么类型的试题组成的
			String sqlstr = " select ques_type from ( select type_id from tr_exam_answer A " +
					" left join tr_exam_paper B on A.R5300=B.R5300 where A.a0100=? and A.nbase=? and A.exam_no=? and A.exam_type=? group by type_id) C" +
					" left join tr_question_type D on C.type_id=D.type_id group by ques_type";
			ArrayList sqlList  = new ArrayList();
			sqlList.add(userView.getA0100());
			sqlList.add(userView.getDbname());
			sqlList.add(Integer.valueOf(paper_id));
			sqlList.add(Integer.valueOf(2));
			
			rs = dao.search(sqlstr,sqlList);
			// true：纯客观题    ||  false：客观主观题都有
			boolean computeFlag = true;
		    while(rs.next()){
		    	if(1 == rs.getInt("ques_type")){
		    		computeFlag = false;
		    		break;
		    	}
		    }
			
		    sqlList.clear();
		    int r5515 = 0;
		    if(computeFlag){
		    	r5515 = 1;
				if(autoRelease.booleanValue())//设置为自动发布成绩  ，该人员状态直接变为 发布
                {
                    r5515 = 2;
                }
		    } 
		    
		    StringBuffer sql = new StringBuffer();
            sql.append("update r55 set r5503=");
            sql.append("(select sum(score) from tr_exam_answer a left join");
            sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200");
            sql.append(" and a.r5300=p.r5300 where a.a0100=?");
            sql.append(" and a.nbase=?");
            sql.append(" and a.exam_no=?");
            sql.append(" and a.exam_type=2 and p.ques_type=2),");               
            sql.append(" r5504=");
            sql.append("(select sum(score) from tr_exam_answer a left join");
            sql.append(" (select r5200,r5300,ques_type from tr_exam_paper c left join tr_question_type d on c.type_id=d.type_id) p on a.r5200=p.r5200");
            sql.append(" and a.r5300=p.r5300 where a.a0100=?");
            sql.append(" and a.nbase=?");
            sql.append(" and a.exam_no=?),");
            sql.append(" r5515=?");
            sql.append(" where a0100=?");
            sql.append(" and nbase=?");
            sql.append(" and r5400=?");
		    
		    sqlList.add(userView.getA0100());
			sqlList.add(userView.getDbname());
			sqlList.add(Integer.valueOf(paper_id));
			sqlList.add(userView.getA0100());
			sqlList.add(userView.getDbname());
			sqlList.add(Integer.valueOf(paper_id));
			sqlList.add(Integer.valueOf(r5515));
			sqlList.add(userView.getA0100());
			sqlList.add(userView.getDbname());
			sqlList.add(Integer.valueOf(paper_id));
			
		    dao.update(sql.toString(),sqlList);
		    
		}catch(Exception e){
			GeneralExceptionHandler.Handle(e);
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				GeneralExceptionHandler.Handle(e);
			}
		}
	}
	/**
	 * 获取课程分类及课程对应的codeitemid
	 * @param r5000 课程编号
	 * @return 返回值格式：课程分类：对应的codeitemid
	 */
	private String getCodeitemid(String r5000) {
        if(StringUtils.isEmpty(r5000)) {
            return "";
        }
        
        RowSet rs = null;
	    String codeitemid = "";
        try{
            String sql = "select r5004,codeitemid from r50 where r5000=?";
            ArrayList<String> valuelist = new ArrayList<String>();
            valuelist.add(r5000);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql, valuelist);
            if(rs.next()) {
                codeitemid = rs.getString("r5004") + ":" +rs.getString("codeitemid");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return codeitemid;
    }
	/**
	 * 获取当次自测考试成绩
	 * @param userView  当前登录用户
	 * @param r5300     考试试卷编号
	 * @param paperid  当前是第几次考试
	 * @return
	 */
	private String getScore(UserView userView, String r5300, String paperid){
	    String score = "0";
	    String sql = "select score from tr_selfexam_paper where nbase=? and a0100=? and r5300=? and paper_id=?";
	    RowSet rs = null;
	    try {
	        ArrayList<String> valuelist = new ArrayList<String>();
	        valuelist.add(userView.getDbname());
	        valuelist.add(userView.getA0100());
	        valuelist.add(r5300);
	        valuelist.add(paperid);
	        ContentDAO dao = new ContentDAO(this.conn);
	        rs = dao.search(sql, valuelist);
	        if(rs.next()) {
                score = rs.getString("score");
            }
	    } catch (Exception e) {
	        e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
	    
	    return score;
	}
	
	/**
     * 判断是否存在某个存储过程
     * 
     * @return
     */
    public boolean isExistPro() {
        boolean isExists = false;

        StringBuffer sql = new StringBuffer();
        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
            sql.append("select * from user_objects where object_name = 'prc_train_score_arch'");
        } else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
            sql.append("SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[prc_train_score_arch]')");
        }

        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        try {
            rs = dao.search(sql.toString());
            isExists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return isExists;
    }
}
