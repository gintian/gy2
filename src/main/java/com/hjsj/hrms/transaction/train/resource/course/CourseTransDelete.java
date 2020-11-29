/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.general.ftp.FtpMediaBo;
import com.hjsj.hrms.businessobject.train.MediaServerParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * <p>
 * Title:CourseTransDelete
 * </p>
 * <p>
 * Description:删除培训课程记录
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class CourseTransDelete extends IBusiness {

	/**
	 * 
	 */
	public CourseTransDelete() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String sels = (String) hm.get("sel");
		String[] sel = sels.split(",");
		ArrayList list = new ArrayList();
		String id = "";
		int n = 0;
		for (int i = 0; i < sel.length; i++) {
			if (n > 0)
				id += ",";

			id += PubFunc.decrypt(SafeCode.decode(sel[i]));
			n++;

			if (n == 1000) {
				list.add(id);
				id = "";
				n = 0;
			}

		}

		if (id.length() > 0) {
			list.add(id);
		}

		ContentDAO cd = new ContentDAO(this.getFrameconn());
		if (!checkIsDel(cd, list))
			return;
		String msg = deletecode(cd, list);// 删除代码类中的课程
		String flag = msg.substring(msg.indexOf("?") + 1, msg.length());
		if ("true".equals(flag)) {
			deleteCourseware(cd, list);// 删除课件
			deleteSelect(cd, list);// 删除学员学习课程的信息
			deleteJobLesson(cd, list);// 删除岗位职务关联的课程
			deleteClassCourse(cd, list);// 删除培训课程信息表中相关的数据
			try {
			    //从系统中清除要删除内容中的图片
				ArrayList sqlList = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					String ids = (String) list.get(i);
					String sql = "delete from r50 where r5000 in (" + ids + ")";
					String selsql = "select imageurl from r50 where r5000 in("+ ids + ") and imageurl!='null' ";
					RowSet rs = cd.search(selsql);
		            if(!VfsService.existPath())
		                throw new GeneralException("没有配置多媒体存储路径！");
		            
					while (rs.next()) {
						String fileid = rs.getString("imageurl");
						if(StringUtils.isNotEmpty(fileid))
							VfsService.deleteFile(this.userView.getUserName(), rs.getString("imageurl"));
					}

					sqlList.add(sql);
				}
				cd.batchUpdate(sqlList);
			} catch (Exception e) {
				e.printStackTrace();
				GeneralExceptionHandler.Handle(e);
			}
		}
		String ids = msg.substring(0, msg.indexOf("?"));
		ids = ids.replace("'", "");
		this.getFormHM().put("fg", flag);
		this.getFormHM().put("ids", ids);
	}

	public boolean checkIsDel(ContentDAO dao, ArrayList list)
			throws GeneralException {
		boolean tmpFlag = true;
		StringBuffer tmpstring = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String sels = (String) list.get(i);
			String sql = "select r5003,r5022 from r50 where r5000 in (" + sels
					+ ")";
			try {
				this.frowset = dao.search(sql);
				while (this.frowset.next()) {
					String r5022 = this.frowset.getString("r5022");
					if ("04".equals(r5022)) {
						tmpstring.append("[" + this.frowset.getString("r5003") + "]\n");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//20170420 linbz 9385 提示信息优化
		if (tmpstring.length() > 0){
			StringBuffer tmps = new StringBuffer();
			tmps.append("只能删除起草、暂停、已报批、已批状态的记录，以下记录不符合！");
			tmps.append(tmpstring.toString());
			throw GeneralExceptionHandler.Handle(new Exception(tmps.toString()));
		}
		return tmpFlag;
	}

	public void deleteSelect(ContentDAO dao, ArrayList list) {
		try {
			ArrayList sqllist = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "delete from tr_selected_lesson where r5000 in("
						+ sels + ")";// 删除学员学习课程的信息
				sqllist.add(sql);
				// 删除课程下学员自测分数记录
				String delzc = "delete from tr_selfexam_paper where r5300 in (select r5300 from tr_lesson_paper where r5000 in ("
						+ sels + "))";
				sqllist.add(delzc);
				// 删除自测考试答案记录
				String delzw = "delete from tr_exam_answer where exam_type = 1 and r5300 in (select r5300 from tr_lesson_paper where r5000 in ("
						+ sels + "))";
				sqllist.add(delzw);
			}

			dao.batchUpdate(sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteCourseware(ContentDAO dao, ArrayList list) {
		try {
			ArrayList sqllist = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "delete from r51 where r5000 in(" + sels + ")";// 删除课件
				sqllist.add(sql);
			}
			deleteSlectedCourse(dao, list);// 删除学员学习课件的信息
			deleteFiles(dao, list);
			dao.batchUpdate(sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteSlectedCourse(ContentDAO dao, ArrayList list) {
		RowSet rs = null;
		try {
			ArrayList sqllist = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "select r5100 from r51 where r5000 in(" + sels
						+ ")";
				rs = dao.search(sql);
				while (rs.next()) {
					int r5100 = rs.getInt("r5100");
					//删除学员学习scorm课件信息
					String sqls = "delete from tr_selected_course_scorm where r5100=" + r5100;
                    sqllist.add(sqls);
                    //删除学员学习课件信息
					sqls = "delete from tr_selected_course where r5100=" + r5100;
					sqllist.add(sqls);
				}
			}
			dao.batchUpdate(sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除课程时，删除分类树中代表该课程的节点
	 * 
	 * @param dao
	 * @param sels
	 * @return
	 */
	public String deletecode(ContentDAO dao, ArrayList list) {
		String flag = "true";
		RowSet rs = null;
		String msg = "";
		try {
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "select codeitemid from r50 where r5000 in("
						+ sels + ")";// 查询课程关联的代码类的id
				rs = dao.search(sql);
				String codeid = "";
				while (rs.next()) {
					String cid = rs.getString("codeitemid");
					if (cid != null || (!"".equals(cid))) {
						codeid += "'" + cid + "',";
					}
				}
				if (codeid != null && (!"".equals(codeid))) {
					codeid = codeid.substring(0, codeid.length() - 1);
					msg += SafeCode.encode(PubFunc.encrypt(codeid.substring(1,
							codeid.length() - 1)));
				}

				if (codeid != null && (!"".equals(codeid))) {
					String sqll = "delete from codeitem where codesetid = '55' and codeitemid in ("
							+ codeid + ")";
					dao.delete(sqll, new ArrayList());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = "false";
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		msg += "?" + flag;
		return msg;
	}

	/**
	 * 删除岗位/职务关联的培训课程
	 * 
	 * @param dao
	 * @param sels
	 */
	public void deleteJobLesson(ContentDAO dao, ArrayList list) {
		try {
			ArrayList sqllist = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "delete from tr_job_course where r5000 in(" + sels
						+ ")";
				sqllist.add(sql);
			}
			dao.batchUpdate(sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void deleteFiles(ContentDAO dao, ArrayList list) {
		for (int i = 0; i < list.size(); i++) {
			String codes = (String) list.get(i);
			String sql = "select r5113,r5105 from r51 where r5000 in(" + codes
					+ ")";
			try {
				this.frowset = dao.search(sql);
				while (this.frowset.next()) {
					String r5113 = this.frowset.getString("r5113");
					String r5105 = this.frowset.getString("r5105");
					if (r5113 != null && r5113.length() > 0) {
						r5113 = r5113.replace("\\", "\\\\");
						File file = new File(r5113);
						if (file.exists()) {
							file.delete();
						}
						if (r5113.substring(
								r5113.toLowerCase().lastIndexOf("\\") + 1)
								.length() < 1) {
							delete(r5113);// .substring(0,
											// url.toLowerCase().lastIndexOf("\\")+1)+sel.split(",")[i]);
						}
						if (MediaServerParamBo.getMediaServerAddress() != null
								&& MediaServerParamBo.getMediaServerAddress()
										.length() > 0) {
							if ("3".equals(r5105)) {
								String filePath = "";
								String sep = System
										.getProperty("file.separator");
								filePath = r5113.substring(
										r5113.lastIndexOf("coureware") + 11,
										r5113.lastIndexOf(sep));

								String path = MediaServerParamBo.getFilePath();
								if (path != null && !path.endsWith("/")) {
									path += "/";
								}

								path = path.replaceAll("/",
										Matcher.quoteReplacement(sep));

								String fileName = "";

								if (r5113 != null) {
									int index = r5113.lastIndexOf("/");
									if (index == -1) {
										index = r5113.lastIndexOf("\\");
									}
									fileName = r5113.substring(index + 1,
											r5113.length());
								}

								// ftp删除
								if (MediaServerParamBo.getFtpServerAddress() != null
										&& MediaServerParamBo
												.getFtpServerAddress().length() > 0) {
									FtpMediaBo bo = new FtpMediaBo(
											MediaServerParamBo
													.getFtpServerAddress(),
											Integer.parseInt(MediaServerParamBo
													.getFtpServerPort()),
											MediaServerParamBo
													.getFtpServerUserName(),
											MediaServerParamBo
													.getFtpServerPwd());
									bo.deleteFile(path + filePath, fileName);
								} else {// 本地删除
                
									file = new File(path + filePath + fileName);
									if (file.exists()) {
										file.delete();
									}
								}

							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 如果是ZIP文件 删除ZIP自解压文件
	public void delete(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			if (file.listFiles().length > 0) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						delete(files[i].getAbsolutePath());
					}

					files[i].delete();
				}

			} else {
				file.delete();
			}
		}
		file.delete();
	}

	/**
	 * 删除培训班下培训课程信息表中的相关数据
	 * 
	 * @param dao
	 * @param sels
	 *            课程编号
	 */
	public void deleteClassCourse(ContentDAO dao, ArrayList list) {
		String codeitemids = "";
		RowSet rs = null;
		try {
			ArrayList sqllist = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				String sels = (String) list.get(i);
				String sql = "select codeitemid from r50 where r5000 in("
						+ sels + ")";
				rs = dao.search(sql);
				while (rs.next()) {
					String cid = rs.getString("codeitemid");
					if (cid != null || (!"".equals(cid))) {
						codeitemids += "'" + cid + "',";
					}
				}
				if (codeitemids != null && (!"".equals(codeitemids))) {
					codeitemids = codeitemids.substring(0,
							codeitemids.length() - 1);

					String sqll = "update r41 set r4118=null where";
					this.frowset = dao
							.search("select 1 from t_hr_busifield where FieldSetId='R41' and itemid='R4118' and state =1 and useflag=1");
					if (this.frowset.next()) {
						sqll += " r4118 in (" + codeitemids + ")";
						sqllist.add(sqll);
					}
				}
				sql = "select codesetid from t_hr_relatingcode where UPPER(codetable)='R50'";
				this.frowset = dao.search(sql);
				while (this.frowset.next()) {
					String codesetid = this.frowset.getString("codesetid");
					String sqlfield = "select itemid from t_hr_busifield where fieldsetid='R41' and codesetid = '"
							+ codesetid
							+ "' and codeflag='1' and state ='1' and useflag='1'";
					rs = dao.search(sqlfield);
					while (rs.next()) {
						String itemid = rs.getString("itemid");
						if (itemid != null || (!"".equals(itemid))) {
							String sqll = "update r41 set " + itemid
									+ "=null where " + itemid + " in (" + sels
									+ ")";
							sqllist.add(sqll);
						}
					}
				}
			}
			dao.batchUpdate(sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
