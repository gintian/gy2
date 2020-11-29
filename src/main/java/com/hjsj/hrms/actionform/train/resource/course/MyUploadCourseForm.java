package com.hjsj.hrms.actionform.train.resource.course;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:MyUploadCourseForm
 * </p>
 * <p>
 * Description:上传课程form
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-05-31 09:44:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyUploadCourseForm extends FrameForm {

	// sql语句
	private String sql;
	
	// sql中的列
	private String columns;
	
	// sql条件
	private String strWhere;
	
	// sql语句中的排序
	private String orderBy;
	
	// DIY课程分类
	private String diyType;
	
	// 课程名称
	private String courseName;
	
	// 上传课程名称
	private String uploadCourseName;
	
	// 上传课程描述
	private String uploadCourseDesc;
	
	// 课程分类列表
	private ArrayList courseTypeList;
	
	// 课程分类列
	private String courseType;
	
	// 外部链接url
	private String url;
	
	// 课程ID
	private String lessonId;
	
	// 文本课件内容
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLessonId() {
		return lessonId;
	}

	public void setLessonId(String lessonId) {
		this.lessonId = lessonId;
	}

	public ArrayList getCourseTypeList() {
		return courseTypeList;
	}

	public void setCourseTypeList(ArrayList courseTypeList) {
		this.courseTypeList = courseTypeList;
	}

	public String getUploadCourseDesc() {
		return uploadCourseDesc;
	}

	public void setUploadCourseDesc(String uploadCourseDesc) {
		this.uploadCourseDesc = uploadCourseDesc;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDiyType() {
		return diyType;
	}

	public void setDiyType(String diyType) {
		this.diyType = diyType;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put(sql, this.getSql());
		this.getFormHM().put(columns, this.getColumns());
		this.getFormHM().put(strWhere, this.getStrWhere());
		this.getFormHM().put(orderBy, this.getOrderBy());
		this.getFormHM().put("diyType", this.getDiyType());
		this.getFormHM().put("courseName", this.getCourseName());
		this.getFormHM().put("uploadCourseName", this.getUploadCourseName());
		this.getFormHM().put("uploadCourseDesc", this.getUploadCourseDesc());
		this.getFormHM().put("courseType", this.getCourseType());
		this.getFormHM().put("url", this.getUrl());
		this.getFormHM().put("text", this.getText());
		
		
	}

	@Override
    public void outPutFormHM() {
		this.setSql((String) this.getFormHM().get("sql"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setStrWhere((String) this.getFormHM().get("strWhere"));
		this.setOrderBy((String) this.getFormHM().get("orderBy"));
		this.setDiyType((String) this.getFormHM().get("diyType"));
		this.setCourseName((String) this.getFormHM().get("courseName"));
		this.setCourseTypeList((ArrayList) this.getFormHM().get("courseTypeList"));
		this.setLessonId((String) this.getFormHM().get("lessonId"));
		this.setText("");
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

		try {
			// 点击“上传课程”菜单时，返回到首页
			if ("/train/resource/myupload/myuploadcourse".equals(arg0.getPath())
					&& arg1.getParameter("b_query") != null) {
				if (this.getPagination() != null)
					this.getPagination().firstPage();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrWhere() {
		return strWhere;
	}

	public void setStrWhere(String strWhere) {
		this.strWhere = strWhere;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getUploadCourseName() {
		return uploadCourseName;
	}

	public void setUploadCourseName(String uploadCourseName) {
		this.uploadCourseName = uploadCourseName;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
