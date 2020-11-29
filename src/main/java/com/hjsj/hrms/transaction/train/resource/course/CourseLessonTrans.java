package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.train.resource.ScormXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class CourseLessonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 参数map
		Map map = (Map)this.getFormHM().get("requestPamaHM");
		
		// 获得分类
		String classes = (String) map.get("classes");
		classes = PubFunc.decrypt(SafeCode.decode(classes));
		// 获得课程id
		String r5000 = (String) map.get("r5000");
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		
		// 获得课件id
		String r5100 = (String) map.get("r5100");
		r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		
		// 是否是学习
		String isLearn = (String) map.get("isLearn");
		
		// 根据r5100，获得所有学习目录及连接
		StringBuffer buff = new StringBuffer();
		buff.append("select * from r51 where r5100=");
		buff.append(r5100);
		
		try {
			String xml = "";
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(buff.toString());
			if (this.frowset.next()) {
				xml = this.frowset.getString("xmlcontent");
			}
			
			if (xml == null || xml.length() <= 0) {
			    this.getFormHM().clear();
			    this.getFormHM().put("classes", classes);
		        this.getFormHM().put("r5100", SafeCode.encode(PubFunc.encrypt(r5100)));
		        this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
			    this.getFormHM().put("exist","1");
			    this.getFormHM().put("isLearn", isLearn);
				return;
			}
			
			ScormXMLBo bo = new ScormXMLBo(xml, "mo");
			List list = bo.getAllScoHref();
			
			if ("0".equals(isLearn)) {// 浏览
				if (list.size() == 0) {
					this.getFormHM().put("src", "");
					this.getFormHM().put("exist","2");
				} else {
					String path = (String) list.get(0);
					String[] str = path.split(";&;");
					this.getFormHM().put("src", getPath(r5100)+ r5100 + "/" + str[1]);
					this.getFormHM().put("scoId", str[0]);
				}
			} else {// 学习，找到第一个未学的课件
				if (list.size() == 0) {
					this.getFormHM().put("src", "");
					this.getFormHM().put("exist","2");
				} else {
					// 查询已学的课件
					buff.delete(0, buff.length());
					buff.append("select scoid,lesson_status from tr_selected_course_scorm ");
					buff.append("where a0100=? and nbase=? and r5100=?");
					
					List dataList = new ArrayList();
					dataList.add(this.userView.getA0100());
					dataList.add(this.userView.getDbname());
					dataList.add(r5100);
					
					Map dataMap = new HashMap();
					this.frowset = dao.search(buff.toString(), dataList);
					while (this.frowset.next()) {
						dataMap.put(this.frowset.getString("scoid"), this.frowset.getString("lesson_status"));
					}
					
					// 默认看第一节
					String path = (String) list.get(0);
					String[] str = path.split(";&;");
					this.getFormHM().put("src", getPath(r5100)+ r5100 + "/" + str[1]);
					this.getFormHM().put("scoId", str[0]);

					
					for (int i = 0; i < list.size(); i++) {
						path = (String) list.get(i);
						str = path.split(";&;");
						if (!"2".equals(dataMap.get(str[0]).toString())) {
							this.getFormHM().put("src", getPath(r5100)+ r5100 + "/" + str[1]);
							this.getFormHM().put("scoId", str[0]);
							break;
						}
					}
				}
			}
			
			// 将课程拼成javascript数组字符窜，方便javascript使用
			StringBuffer script = new StringBuffer();
			script.append("[");
			for (int i = 0; i < list.size(); i++) {
				String str = (String) list.get(i);
				String[] st = str.split(";&;");
				script.append("['");
				script.append(st[0]);
				script.append("',");
				script.append("'");
				script.append(getPath(r5100)+ r5100 + "/" + st[1]);
				script.append("'");
				script.append("]");
				if (i != list.size() - 1) {
					script.append(",");
				}
			}
			script.append("]");
			this.getFormHM().put("courseInfo", SafeCode.encode(script.toString()));
			this.getFormHM().put("currentNum", "1");
			this.getFormHM().put("maxNum", list.size()+"");
			this.getFormHM().put("infoList", list);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 保存参数
		this.getFormHM().put("exist","0");
		this.getFormHM().put("classes", classes);
		this.getFormHM().put("r5100", SafeCode.encode(PubFunc.encrypt(r5100)));
		this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
		this.getFormHM().put("isLearn", isLearn);
		
		
	}
	
	/**
	 * 根据分类获得路径
	 * @param classes
	 * @return
	 */
	private String getPath(String r51) {
		String sql = "select * from r51 where r5100=" + r51;
		String path = "/coureware/";
		
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				path = this.frowset.getString("r5113");
				int i = path.lastIndexOf("/");
				if (i == -1) {
					i = path.lastIndexOf("\\");
				}
				path = path.substring(0, i + 1);
				path = path.replaceAll(Matcher.quoteReplacement("\\"), "/");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return path;
	}
}
