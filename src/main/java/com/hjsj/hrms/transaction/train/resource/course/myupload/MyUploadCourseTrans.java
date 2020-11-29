package com.hjsj.hrms.transaction.train.resource.course.myupload;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * <p>
 * Title:MyUploadCourseTrans
 * </p>
 * <p>
 * Description:按照登录名查询上传DIY课程
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-05-31 10:36:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyUploadCourseTrans extends IBusiness {

	public void execute() throws GeneralException {

		Map hm = (Map)this.getFormHM().get("requestPamaHM");
		
		// DIY分类
		String diyType = "";
		
		// 课程名称
		String courseName = "";
		
		// sql中的列
		String columns = "";
		
		// sql中的排序语句
		String orderBy = "";
		
		// sql语句
		StringBuffer sqlBuff = new StringBuffer();
		
		// sql的where条件
		StringBuffer strWhere = new StringBuffer();

		// 获得用户名
		String userName = this.userView.getUserName();
		String userA0100 = this.userView.getA0100();
        
		// 获得查询的课程名称
		courseName = (String) this.getFormHM().get("courseName");
        if(hm.containsKey("init")){
        	courseName="";
        	this.getFormHM().put("courseName", courseName);
        }
        hm.remove("init");
		// 查询语句
		sqlBuff.append("select r5000,r5003,r5012,");
		sqlBuff.append("create_time,r5022 ");

		// where条件，必须为diy课程+用户名
		strWhere.append("from r50 where r5037='1' ");
		strWhere.append("and create_user='");
		strWhere.append(userA0100);
		strWhere.append("' ");
		
		if (courseName != null && courseName.trim().length() > 0) {
			strWhere.append(" and r5003 like '%");
			strWhere.append(courseName);
			strWhere.append("%'");
		}

		// 排序
		orderBy = " order by r5000 desc";

		// 所有列
		columns = "r5000,r5003,r5012,create_time,r5022";
		
		// 获得参数设置的DIY课程分类
		ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
		diyType = constant.getNodeAttributeValue("/param/diy_course",
				"codeitemid");

		this.getFormHM().put("sql", sqlBuff.toString());
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("strWhere", strWhere.toString());
		this.getFormHM().put("orderBy", orderBy);
		this.getFormHM().put("diyType", diyType);
	}

}
