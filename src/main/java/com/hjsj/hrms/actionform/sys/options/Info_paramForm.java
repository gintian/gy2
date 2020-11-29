/**
 * 
 */
package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class Info_paramForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String [] right_fields;
	private ArrayList browsefields=new ArrayList();
	private ArrayList rightlist=new ArrayList();
	private String edition;
	private String tableset;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		if(this.getFormHM().get("browsefields")!=null)
		  this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
		this.setEdition((String)this.getFormHM().get("edition"));
		this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("right_fields",this.right_fields);
		this.getFormHM().put("edition", this.edition);

		this.getFormHM().put("tableset", this.getTableset());

	}

	public String[] getRight_fields() {
		return this.right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getBrowsefields() {
		return this.browsefields;
	}

	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		right_fields=new String[0];
		super.reset(arg0, arg1);
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}


	public ArrayList getRightlist() {
		return rightlist;
	}

	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}


	public String getTableset() {
		return tableset;
	}

	public void setTableset(String tableset) {
		this.tableset = tableset;
	}



}
