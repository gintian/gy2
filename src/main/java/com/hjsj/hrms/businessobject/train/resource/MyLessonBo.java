package com.hjsj.hrms.businessobject.train.resource;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:TrainProjectBo.java
 * </p>
 * <p>
 * Description:我的课程
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-09-20 13:00:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyLessonBo {
	private Connection conn;
	private UserView userView;

	public MyLessonBo() {

	}

	public MyLessonBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 获得试听课程名称
	 * @param lessonId
	 * @param b0110 like "UN01`UN0201`"
	 * @param nbase
	 * @return
	 */
	public String getLessonName(String lessonId,String b0110) {
		String name = "";
		Connection conn = null;
		RowSet rs = null;
		RowSet rss = null;
		
		String[] t = b0110.split("`");
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select R5003 from R50 where R5000="+lessonId); 
			if(b0110!=null && b0110.length()>0 && b0110.indexOf("UN`") == -1){
				StringBuffer sql = new StringBuffer();
				sql.append(" and ((( ");
				for(int i=0;i<t.length;i++){
					sql.append(" r5020="+Sql_switcher.substr("'"+t[i].substring(2)+"'", "1", Sql_switcher.length("r5020"))+" or");
				}
				buff.append(sql.substring(0, sql.length()-2));
				buff.append(" ) and r5014=2) or "+Sql_switcher.isnull("R5014", "1")+"<>2 or r5020 is null)");
			}else if(b0110==null || b0110.length()<1) {
                buff.append(" and 1=2 ");
            }
			rs = dao.search(buff.toString());
			if (rs.next()) {
				name = rs.getString("r5003");
			}

			name = name == null ? "" : name;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (rss != null) {
					rss.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return name;
	}
	
	/**
	 * 获得学习课程名称
	 * @param lessonId
	 * @param a0100
	 * @param nbase
	 * @return
	 */
	public String getMyLessonName(String lessonId,String a0100,String nbase) {
		String name = "";
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select r5003 from r50 where r5000=(");
			buff.append("select r5000 from tr_selected_lesson where r5000=");
			buff.append(lessonId);
			buff.append(" and a0100='" + a0100 + "' and nbase='" + nbase + "')");
			rs = dao.search(buff.toString());
			if (rs.next()) {
				name = rs.getString("r5003");
			}

			name = name == null ? "" : name;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return name;
	}

	/**
	 *  获得DIY课程名称
	 * @param lessonId
	 * @param a0100
	 * @return
	 */
	public String getDiyLessonName(String lessonId,String a0100) {
		String name = "";
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select r5003 from r50 where r5000=");
			buff.append(lessonId);
			buff.append(" and r5037='1' and create_user='");
			buff.append(a0100);
			buff.append("' ");
			rs = dao.search(buff.toString());
			if (rs.next()) {
				name = rs.getString("r5003");
			}

			name = name == null ? "" : name;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return name;
	}
	/**
	 * 获得课程描述
	 * 
	 * @param lessonId
	 * @return
	 */
	public String getLessonDesc(String lessonId) {
		String desc = "";
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select r5012 from r50 where r5000=");
			buff.append(lessonId);
			rs = dao.search(buff.toString());
			if (rs.next()) {
				desc = rs.getString("r5012");
			}

			desc = desc == null ? "" : desc;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return desc;

	}

	/**
	 * 获得所有的评论
	 * 
	 * @param CourseId
	 * @return
	 */
	public ArrayList getCommentList(String CourseId) {
		ArrayList commentList = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff
					.append("select  A0101,createtime,comments from tr_course_comments where state=0 and r5100=");
			buff.append(CourseId);
			rs = dao.search(buff.toString());
			while (rs.next()) {
				HashMap map = new HashMap();
				String a0101 = rs.getString("a0101");
				map.put("a0101", a0101);

				Timestamp date = rs.getTimestamp("createtime");// Date("createtime");
				if (date != null) {
					String tim = DateUtils.format(date, "yyyy年MM月dd日 HH:mm");
					map.put("createtime", tim);
				}

				String comments = rs.getString("comments");
				comments = comments == null ? "" : comments;
				map.put("comments", PubFunc.toHtml(comments));

				commentList.add(map);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return commentList;
	}

	/**
	 * 获得课程课件列表
	 * 
	 * @param CourseId
	 * @return
	 */
	public ArrayList getCourseList(String lessonId, UserView userView) {
		ArrayList commentList = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select t.r5100,r.r5117,r.R5119,r.r5103,r.r5113,t.state,r.r5105,t.learnedhour from");
			buff.append(" tr_selected_course t left join r51 r on ");
			buff.append("t.r5100=r.r5100 where t.a0100='");
			buff.append(userView.getA0100());
			buff.append("' and t.nbase='");
			buff.append(userView.getDbname());
			buff.append("' and r.r5000=");
			buff.append(lessonId);
			rs = dao.search(buff.toString());
			while (rs.next()) {
				HashMap map = new HashMap();
				String r5103 = rs.getString("r5103");
				r5103 = r5103 == null ? "" : r5103;
				map.put("r5103", r5103);

				String r5100 = rs.getString("r5100");
				r5100 = r5100 == null ? "" : r5100;
				map.put("r5100", r5100);

				String r5113 = rs.getString("r5113");
				r5113 = r5113 == null ? "" : r5113;
				map.put("r5113", r5113);

				String r5117 = rs.getString("r5117");
				r5117 = r5117 == null ? "" : r5117;
				map.put("r5117", r5117);

				String R5119 = rs.getString("r5119");
				R5119 = R5119 == null ? "" : R5119;
				map.put("r5119", R5119);

				String state = rs.getString("state");
				state = state == null ? "" : state;
				map.put("state", state);

				String r5105 = rs.getString("r5105");
				r5105 = r5105 == null ? "" : r5105;
				map.put("r5105", r5105);

				String learnedhour = rs.getString("learnedhour");
				learnedhour = (learnedhour == null || "".equals(learnedhour)) ? "0"
						: learnedhour;
				map.put("learnedhour", learnedhour);

				commentList.add(map);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return commentList;
	}

	/**
	 * 获得课程课件列表 当前没有选择的课程
	 * 
	 * @param CourseId
	 * @return
	 */

	public ArrayList getNoCheckCourseList(String lessonId) {
		ArrayList commentList = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		String sql = "select * from r50 r,r51 t where r.r5000 = t.r5000 and t.r5000 = '"
				+ lessonId + "'";
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next()) {
				HashMap map = new HashMap();
				String r5103 = rs.getString("r5103");
				r5103 = r5103 == null ? "" : r5103;
				map.put("r5103", r5103);

				String r5100 = rs.getString("r5100");
				r5100 = r5100 == null ? "" : r5100;
				map.put("r5100", r5100);

				String r5113 = rs.getString("r5113");
				r5113 = r5113 == null ? "" : r5113;
				map.put("r5113", r5113);

				String r5117 = rs.getString("r5117");
				r5117 = r5117 == null ? "" : r5117;
				map.put("r5117", r5117);

				String R5119 = rs.getString("r5119");
				R5119 = R5119 == null ? "" : R5119;
				map.put("r5119", R5119);

				String r5105 = rs.getString("r5105");
				r5105 = r5105 == null ? "" : r5105;
				map.put("r5105", r5105);

				commentList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return commentList;
	}

	/**
	 * 获得上传课程课件列表
	 * 
	 * @param CourseId
	 * @return
	 */

	public ArrayList getUploadCourseList(String lessonId, UserView userView) {
		ArrayList commentList = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select r.r5100,r.r5117,r.r5119,r.r5103,r.r5105,r.r5113 from ");
			buff.append("r51 r,r50 r1 where r.r5000 = r1.r5000 ");
			buff.append("and r1.create_user = '");
			buff.append(userView.getA0100());
			buff.append("' and r.r5000 =");
			buff.append(lessonId);

			rs = dao.search(buff.toString());
			while (rs.next()) {
				HashMap map = new HashMap();
				String r5103 = rs.getString("r5103");
				r5103 = r5103 == null ? "" : r5103;
				map.put("r5103", r5103);

				String r5100 = rs.getString("r5100");
				r5100 = r5100 == null ? "" : r5100;
				map.put("r5100", r5100);

				String r5113 = rs.getString("r5113");
				r5113 = r5113 == null ? "" : r5113;
				map.put("r5113", r5113);

				String r5117 = rs.getString("r5117");
				r5117 = r5117 == null ? "" : r5117;
				map.put("r5117", r5117);

				String R5119 = rs.getString("r5119");
				R5119 = R5119 == null ? "" : R5119;
				map.put("r5119", R5119);

				String r5105 = rs.getString("r5105");
				r5105 = r5105 == null ? "" : r5105;
				map.put("r5105", r5105);

				commentList.add(map);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return commentList;
	}

	/**
	 * 获得课程课件列表
	 * 
	 * @param CourseId
	 * @return
	 */
	public ArrayList getAllCourseList(String lessonId) {
		ArrayList commentList = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select r.r5100,r.r5117,r.R5119,r.r5103 from");
			buff.append(" r51 r where r.r5000=");
			buff.append(lessonId);
			rs = dao.search(buff.toString());
			while (rs.next()) {
				HashMap map = new HashMap();
				String r5103 = rs.getString("r5103");
				r5103 = r5103 == null ? "" : r5103;
				map.put("r5103", r5103);

				String r5100 = rs.getString("r5100");
				r5100 = r5100 == null ? "" : r5100;
				map.put("r5100", r5100);

				String r5117 = rs.getString("r5117");
				r5117 = r5117 == null ? "" : r5117;
				map.put("r5117", r5117);

				String R5119 = rs.getString("r5119");
				R5119 = R5119 == null ? "" : R5119;
				map.put("r5119", R5119);

				commentList.add(map);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return commentList;
	}

	public HashMap handlerPath(String filePath) {
		HashMap map = new HashMap();
		String ppath = "";
		try {

			if (filePath != null && filePath.length() > 0) {
				
				String osName = System.getProperty("os.name");
			    if(osName.toLowerCase().indexOf("windows")>-1){
			    	ppath = filePath.substring(filePath.lastIndexOf("coureware\\") - 1);
			    } else {
			        if(filePath.lastIndexOf("coureware\\") > -1) {
                        ppath = filePath.substring(filePath.lastIndexOf("coureware\\") - 1);
                    } else {
                        ppath = filePath.substring(filePath.lastIndexOf("coureware/") - 1);
                    }
			            
			    }
			    
				ppath = ppath.replaceAll("\\\\", "/");
			}


			if (filePath != null) {

				int index = filePath.lastIndexOf("coureware/");
				if (index == -1) {
					index = filePath.lastIndexOf("coureware\\");
				}
				filePath = filePath.substring(index + 9, filePath.length());
				filePath = filePath.replaceAll("\\\\", "/");
			} else {
				filePath = "";
			}

			map.put("filePath", filePath);
			map.put("ppath", ppath);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 该课件是否有笔记
	 * 
	 * @param courseId
	 * @param userView
	 * @return
	 */
	public boolean isHashNote(String courseId, UserView userView) {
		boolean flag = false;
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select * from");
			buff.append(" tr_course_comments t where state=1 and t.a0100='");
			buff.append(userView.getA0100());
			buff.append("' and t.nbase='");
			buff.append(userView.getDbname());
			buff.append("' and t.r5100=");
			buff.append(courseId);
			rs = dao.search(buff.toString());
			if (rs.next()) {
				flag = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return flag;
	}

	/**
	 * 增加播放次数
	 */
	public void addPlayCount(String courseId) {
		
		if(courseId == null || courseId.length() < 1) {
            return;
        }

		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("update r51 set R5119=");
			buff.append(Sql_switcher.isnull("R5119", "0"));
			buff.append("+ 1 where R5100=");
			buff.append(courseId);
			dao.update(buff.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 是否显示考试图标
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isShow(String id) {
		boolean flag = false;
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select * from tr_lesson_paper t where r5000=");
			buff.append(id);
			buff.append(" and exists(select 1 from r53 where r5300=");
			buff.append("t.r5300 and r5311='04'");
			buff.append(")");
			rs = dao.search(buff.toString());
			if (rs.next()) {
				flag = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return flag;

	}

	/**
	 * 更新课件学习状态为正学
	 */
	public static void updateLearnedState(String courseId, UserView userview) {
		if(courseId == null || courseId.length() < 1) {
            return;
        }
		
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("update tr_selected_course set state=1 where r5100=");
			buff.append(courseId);
			buff.append(" and a0100='");
			buff.append(userview.getA0100());
			buff.append("' and nbase='");
			buff.append(userview.getDbname());
			buff.append("' and state=0");

			dao.update(buff.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到当前用户课程数
	 * 
	 * @return
	 */
	private int getUserCourseCount(boolean msg, String flg) {
		int courseCount = 0;
		boolean m = msg;
		String f = flg;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(*) FROM tr_selected_lesson");
			sql.append(" WHERE nbase='");
			sql.append(this.userView.getDbname());
			sql.append("' AND A0100='");
			sql.append(this.userView.getA0100());
			// 陈旭光修改：查询时将正学、已学课程细分为正学必修、正学选修、已学必修、已学选修
			if (m == true) {
				sql.append("' AND (state=0 or state=1)");
				sql.append(" AND EXISTS(SELECT 1 FROM R50 WHERE R50.R5000=tr_selected_lesson.R5000 AND R5022='04')");
				if ("1".equals(f)) {
					sql.append(" AND lesson_from <> 1");
				} else if ("2".equals(f)) {
					sql.append(" AND lesson_from = 1");
				}
			} else {
				sql.append("' AND state=2");
				if ("1".equals(f)) {
					sql.append(" AND lesson_from <> 1");
				} else if ("2".equals(f)) {
					sql.append(" AND lesson_from = 1");
				}
			}

			rs = dao.search(sql.toString());
			if (rs.next()) {
                courseCount = rs.getInt(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return courseCount;
	}
	/**
	 * 判断课程是否可以浏览（课件为文本类或者非文本类的课件的链接非空时可浏览）
	 * @param r5000
	 * @return
	 */
    public static boolean checkShow(String r5000) {
        boolean flag = false;
        RowSet rs = null;
        Connection conn=null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT R5105,R5113 FROM R51");
            sql.append(" WHERE R5000='");
            sql.append(r5000);
            sql.append("'");
            rs = dao.search(sql.toString());
            if(rs.next()){
                String r5105 = rs.getString("R5105");
                String r5113 = rs.getString("R5113");
                if("2".equalsIgnoreCase(r5105) || (!"2".equalsIgnoreCase(r5105) && r5113 != null && r5113.length() > 0)) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
        return flag;

    }
	/**
	 * 得到当前用户正学课程数
	 * 
	 * @return
	 */
	public int getUserLearningCourseCount() {
		return getUserCourseCount(true, "0");
	}

	/**
	 * 得到当前用户已学课程数
	 * 
	 * @return
	 */
	public int getUserLearnedCourseCount() {
		return getUserCourseCount(false, "0");
	}

	// 陈旭光修改：将正学、已学课程细分为正学必修、正学选修、已学必修、已学选修
	/**
	 * 得到当前用户正学必修课程数
	 * 
	 * @return
	 */
	public int getUserLearningReqCourseCount() {
		return getUserCourseCount(true, "1");
	}

	/**
	 * 得到当前用户正学选修课程数
	 * 
	 * @return
	 */
	public int getUserLearningOptCourseCount() {
		return getUserCourseCount(true, "2");
	}

	/**
	 * 得到当前用户已学必修课程数
	 * 
	 * @return
	 */
	public int getUserLearnedReqCourseCount() {
		return getUserCourseCount(false, "1");
	}

	/**
	 * 得到当前用户已学选修课程数
	 * 
	 * @return
	 */
	public int getUserLearnedOptCourseCount() {
		return getUserCourseCount(false, "2");
	}
	/**
	 * 用video标签播放视频时，将文件路径中的文件的后缀名换为MP4
	 * @param filePath 文件路径
	 * @return
	 */
	public static String filePathToMp4(String filePath){
		String ext = filePath.substring(filePath.lastIndexOf("."));
		if(!".mp4".endsWith(ext.toLowerCase()) && !".mp3".endsWith(ext.toLowerCase())) {
            filePath = filePath.substring(0,filePath.lastIndexOf(".")) + ".mp4";
        }
	
		return filePath;
	}
	/**
	 * 获取个人考试是否允许归档或是否需要课程学习进度达到100%后才允许个人考试
	 * @param flag =my：查询是否需要课程学习进度达到100%后才允许个人考试|其他值：获取个人考试是否允许归档
	 * @return
	 */
	public String getDisableExamOrEnableArch(String flag) {
	    String value = "0";
	    try {
	        ConstantXml constantbo = new ConstantXml(this.conn,"TR_PARAM");
	        value = constantbo.getNodeAttributeValue("/param/lesson_hint", "enable_arch");
	        if("my".equals(flag)&&"1".equals(value)) {
                value = constantbo.getNodeAttributeValue("/param/lesson_hint", "disable_exam_learning");
            }
	        
	        value = value==null ? "0" : value;
	            
	    } catch (Exception e) {
	        e.printStackTrace();
        }
	    
        return value;
    }
	
	/**
     * 查询自考最高的成绩
     * @param r5000 课程id
     * @param userView 登录用户
     * @return
     */
    public static String getScore(String r5000, UserView userView) {
        float score = (float) 0.00;
        //判断课程是否关联考试        
        if (!isShow(r5000.toString())) {	       
	       return "";
        }
        
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            MyTestBo bo = new MyTestBo(conn);
            String r5300 = bo.getR5300ByR5000(r5000);
            r5300 = StringUtils.isEmpty(r5300) ? "''" : r5300;
            
            StringBuffer myTestSql = new StringBuffer(); 
            myTestSql.append("select MAX(" + floatTochar(Sql_switcher.isnull("score", "0.0"), "9999.99") + ") score");
            myTestSql.append(" from tr_selfexam_paper ");
            myTestSql.append(" where nbase='");
            myTestSql.append(userView.getDbname());
            myTestSql.append("' and a0100='");
            myTestSql.append(userView.getA0100());
            myTestSql.append("' and r5300=");
            myTestSql.append(r5300);
            
            ContentDAO dao = new ContentDAO(conn);
            rs =dao.search(myTestSql.toString());
            if(rs.next()){
            	 String  newScore = "";
                 newScore = rs.getString("score");
                //判断课程考试是否有成绩
                 if(StringUtils.isEmpty(newScore)) {
                     return "";
                 }
                 
                 score = Float.valueOf(newScore);               
            }else {
                return "";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
       
        return String.valueOf(score);
    }
    /**
     * 拼接sql语句
     * @param itemid 指标id
     * @param f 指标格式
     * @return
     */
    private static String floatTochar(String itemid,String f){
        StringBuffer strvalue = new StringBuffer();
        switch (Sql_switcher.searchDbServer())
        {
            case 1:
                strvalue.append("CAST(");
                strvalue.append(itemid);
                strvalue.append(" AS NUMERIC(8,1))");
                break;
            case 2:
                strvalue.append("TRIM(TO_CHAR(");
                strvalue.append(itemid);
                strvalue.append(",'"+f+"'))");
                break;
            case 3:
                strvalue.append("CHAR(INT(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
        }
        return strvalue.toString();
    }
}
