package com.hjsj.hrms.transaction.train.resource.course.myupload;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:UploadLessonsTrans
 * </p>
 * <p>
 * Description:上传课程
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-01 11:22:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class UploadLessonsTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		// 获得链接后的参数集合
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		// 课程id
		String lessonId = "";
		
		// 是否是新增
		boolean isInsert = true;
		
		// dao
		ContentDAO dao  = new ContentDAO(this.frameconn);
		
		// 课程分类列表
		ArrayList courseTypeList = new ArrayList();
		
		// 获得lessonId，如果是链接后的就按照修改操作，如果是新创建的，就按照新增操作
		lessonId = (String) hm.get("lessonId");
		if (lessonId == null || lessonId.length() <= 0) {
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());			 
			lessonId = idg.getId("R50.R5000");
		}
		
		// 获得课程类型列表，代码57
		String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='57' and invalid=1";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				CommonData data = new CommonData();
				data.setDataName(this.frowset.getString("codeitemdesc"));
				data.setDataValue(this.frowset.getString("codeitemid"));
				courseTypeList.add(data);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("lessonId", lessonId);
		this.getFormHM().put("courseTypeList", courseTypeList);
	}

}
