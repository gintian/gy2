/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course.scorm;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * <p>
 * Title:ShowScormTrans
 * </p>
 * <p>
 * Description:显示scorm课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class ShowScormTrans extends IBusiness {

	/**
	 * 
	 */
	public ShowScormTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		// 参数map
		Map map = (Map)this.getFormHM().get("requestPamaHM");
		
		// 获得分类
		String classes = (String) map.get("classes");
		
		// 获得课程id
		String r5000 = (String) map.get("r5000");
		
		// 获得课件id
		String r5100 = (String) map.get("r5100");
		
		// 课件课程id
		String scoId = (String) map.get("a_code");
		
		// 课件课程url
		String htmhref = (String) map.get("htmhref");
		
		// 获得课程信息
		List infoList = (List) this.getFormHM().get("infoList");
		
		for (int i = 0; i < infoList.size(); i++) {
			String strInfo = (String)infoList.get(i);
			String[] str = strInfo.split(";&;");
			if (scoId.equals(str[0])) {
				this.getFormHM().put("currentNum", (i + 1) + "");
			}
		}
		
//		System.out.println(getPath(classes)+ r5100 + "/" + htmhref);
		
		// 保存参数
		this.getFormHM().put("classes", classes);
		this.getFormHM().put("r5100", r5100);
		this.getFormHM().put("r5000", r5000);
		this.getFormHM().put("scoId", scoId);
		this.getFormHM().put("src", getPath(r5100)+ PubFunc.decrypt(SafeCode.decode(r5100)) + "/" + htmhref);
		
	}
	
	/**
	 * 根据分类获得路径
	 * @param classes
	 * @return
	 */
	private String getPath(String r51) {
		if(r51 != null && r51.length() > 0)
			r51 = PubFunc.decrypt(SafeCode.decode(r51));
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
