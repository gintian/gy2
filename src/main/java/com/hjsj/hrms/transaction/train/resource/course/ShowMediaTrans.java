package com.hjsj.hrms.transaction.train.resource.course;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:SaveCoursewareTrans
 * </p>
 * <p>
 * Description:保存添加的培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class ShowMediaTrans extends IBusiness {

	public void execute() throws GeneralException {

		
		Map map = (HashMap) this.getFormHM().get("requestPamaHM");
		String aCode = (String ) map.get("a_code");
		String r5100 = (String) map.get("r5100");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String filePath = "";
		String fileContent = "";
		try {
			String sql="select * from r51 where r5100='"+r5100+"'";
			this.frowset = dao.search(sql);
			
			if (this.frowset.next()) {
				filePath = this.frowset.getString("r5113");
				fileContent = this.frowset.getString("r5111");
			}
			
			if (fileContent == null) {
				fileContent = "";
			}
			
			if (filePath != null) {
				int index = filePath.lastIndexOf("/");
				if (index == -1) {
					index = filePath.lastIndexOf("\\");
				}
				filePath = filePath.substring(index + 1, filePath.length());
			} else {
				filePath = "";
			}
			
			String abPath = "/";
			if (aCode != null && aCode.length() > 0) {
				if (aCode != null && aCode.length() > 0) {
					for (int i = 0; i < aCode.length() / 2; i++) {
						abPath += aCode.substring(0, 2 * (i + 1)) + "/";
					}
				}
			}
			
			if (abPath.length() > 1) {
				filePath = abPath + filePath;
			}
			
			this.getFormHM().put("filePath", filePath);
			this.getFormHM().put("fileContent", fileContent);
			this.getFormHM().put("a_code", aCode);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
