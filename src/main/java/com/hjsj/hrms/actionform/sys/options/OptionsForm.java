/**
 * 
 */
package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:OptionsForm</p>
 * <p>Description:系统参数配置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-24:9:04:33</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class OptionsForm extends FrameForm {

	/**信息群列表*/
	private ArrayList list=new ArrayList();
	/**子集前缀*/
	private String classpre;
	
	private String[] right_fields;
	
	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		this.right_fields=new String[0];
		this.getFormHM().put("lock", arg1.getSession().getServletContext().getAttribute("lock"));
	}


	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setList((ArrayList)this.getFormHM().get("inforlist"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("classpre",this.getClasspre());
	}

	public String getClasspre() {
		return classpre;
	}

	public void setClasspre(String classpre) {
		this.classpre = classpre;
	}

}
