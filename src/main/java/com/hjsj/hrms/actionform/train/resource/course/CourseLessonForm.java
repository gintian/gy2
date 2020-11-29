/**
 * 
 */
package com.hjsj.hrms.actionform.train.resource.course;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class CourseLessonForm extends FrameForm {

	private String src;
	//是否存在src no不存在 
	private String exist;
	
	// 课程分类
	private String classes;
	
	// 课件id
	private String r5100;
	
	// 课程id
	private String r5000;
	
	// 课件单元id
	private String scoId;
	
	// 是否是学习0为否，1为是
	private String isLearn;
	
	// 课件的信息，javascript字符窜
	private String courseInfo;
	
	// scrom课件当前位置
	private String currentNum;
	
	// scorm课件包含的最大课程数
	private String maxNum;
	
	// 信息集合
	private List infoList;
	
	public List getInfoList() {
		return infoList;
	}

	public void setInfoList(List infoList) {
		this.infoList = infoList;
	}

	public String getIsLearn() {
		return isLearn;
	}

	public void setIsLearn(String isLearn) {
		this.isLearn = isLearn;
	}

	public String getScoId() {
		return scoId;
	}

	public void setScoId(String scoId) {
		this.scoId = scoId;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getR5100() {
		return r5100;
	}

	public void setR5100(String r5100) {
		this.r5100 = r5100;
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	/**
	 * 
	 */
	public CourseLessonForm() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("exist", this.getExist());
		this.getFormHM().put("classes", this.getClasses());
		this.getFormHM().put("r5000", this.getR5000());
		this.getFormHM().put("r5100", this.getR5100());
		this.getFormHM().put("scoId", this.getScoId());
		this.getFormHM().put("isLearn", this.getIsLearn());
		this.getFormHM().put("src", this.getSrc());
		this.getFormHM().put("courseInfo", this.getCourseInfo());
		this.getFormHM().put("currentNum", this.getCurrentNum());
		this.getFormHM().put("maxNum", this.getMaxNum());
		this.getFormHM().put("infoList", this.getInfoList());
		
		
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */

	@Override
    public void outPutFormHM() {
	    this.setExist((String) this.getFormHM().get("exist"));
		this.setClasses((String) this.getFormHM().get("classes"));
		this.setR5000((String) this.getFormHM().get("r5000"));
		this.setR5100((String) this.getFormHM().get("r5100"));
		this.setScoId((String) this.getFormHM().get("scoId"));
		this.setIsLearn((String) this.getFormHM().get("isLearn"));
		this.setSrc((String) this.getFormHM().get("src"));
		this.setCourseInfo((String) this.getFormHM().get("courseInfo"));
		this.setMaxNum((String) this.getFormHM().get("maxNum"));
		this.setCurrentNum((String) this.getFormHM().get("currentNum"));
		this.setInfoList((List)this.getFormHM().get("infoList"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }
	

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getCourseInfo() {
		return courseInfo;
	}

	public void setCourseInfo(String courseInfo) {
		this.courseInfo = courseInfo;
	}

	public String getCurrentNum() {
		return currentNum;
	}

	public void setCurrentNum(String currentNum) {
		this.currentNum = currentNum;
	}

	public String getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(String maxNum) {
		this.maxNum = maxNum;
	}

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }
	
}
