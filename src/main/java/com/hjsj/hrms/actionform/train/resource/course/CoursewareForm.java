/**
 * 
 */
package com.hjsj.hrms.actionform.train.resource.course;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class CoursewareForm extends FrameForm {

	private String sqlstr;
	private String tablename;
	private ArrayList itemlist = new ArrayList();
	private String a_code;
	private String r5000;
	private FormFile file;
	private String path_name;
	private String path_old;
	private String r5100;
	private Map r5105;
	private String r5115;
	
	private String strsql;
	private String columns;
	private String strwhere;
	private String isParent;//是否为上级分类下的课程
	
	private String filePath;
	
	private String fileContent;
	private String url;
	private String courseDesc;
	private String courseName;
	private String courseType;
	private String textContent;
	private String textName ; 
	private String check;
	//上传控件中文件上传服务器后生成的地址
	private String newPath;
	
	
	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseDesc() {
		return courseDesc;
	}

	public void setCourseDesc(String courseDesc) {
		this.courseDesc = courseDesc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	/**
	 * 
	 */
	public CoursewareForm() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("itemlist", this.getItemlist());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("r5100", this.getR5100());
		this.getFormHM().put("r5105", this.getR5105());
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("r5115", this.getR5115());
		this.getFormHM().put("path", this.getPath_name());
		this.getFormHM().put("path_old", this.getPath_old());
		this.getFormHM().put("url", this.getUrl());
		this.getFormHM().put("courseDesc", this.getCourseDesc());
		this.getFormHM().put("courseName", this.getCourseName());
		this.getFormHM().put("courseType", this.getCourseType());
		this.getFormHM().put("textContent", this.getTextContent());
		this.getFormHM().put("textName", this.getTextName());
		this.getFormHM().put("check", this.getCheck());
		this.getFormHM().put("newPath", this.getNewPath());
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */

	@Override
    public void outPutFormHM() {
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setR5000((String)this.getFormHM().get("id"));
		this.setR5100((String)this.getFormHM().get("r5100"));
		this.setR5105((Map)this.getFormHM().get("r5105"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setR5115((String)this.getFormHM().get("r5115"));
		this.setIsParent((String)this.getFormHM().get("isParent"));
		this.setFileContent((String) this.getFormHM().get("fileContent"));
		this.setFilePath((String) this.getFormHM().get("filePath"));
		this.setUrl((String)this.getFormHM().get("url"));
		this.setCourseDesc((String)this.getFormHM().get("courseDesc"));
		this.setCheck((String)this.getFormHM().get("check"));
		this.setNewPath((String)this.getFormHM().get("newPath"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/resource/courseware".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }
	
	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getR5100() {
		return r5100;
	}

	public void setR5100(String r5100) {
		this.r5100 = r5100;
	}

	public Map getR5105() {
		return r5105;
	}

	public void setR5105(Map r5105) {
		this.r5105 = r5105;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getR5115() {
		return r5115;
	}

	public void setR5115(String r5115) {
		this.r5115 = r5115;
	}

	public String getPath_name() {
		return path_name;
	}

	public void setPath_name(String path_name) {
		this.path_name = path_name;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getPath_old() {
		return path_old;
	}

	public void setPath_old(String path_old) {
		this.path_old = path_old;
	}

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

}
