/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.updownfile;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HtmlFileForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	 private RecordVo boardvo=new RecordVo("resource_list");
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
	
		
		
	}
     
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		

	}
	private FormFile file;
	
	
	public FormFile getFile()
	{
		return this.file;
	}
	public void setFile(FormFile file)
	{
		this.file=file;
	}
	
	private String fname;
	
	public String getFname()
	{
		return this.fname;
	}
	
	public void setFname(String fname)
	{
		this.fname=fname;
	}
	private String size;
	
	public String getSize()
	{
		return this.size;
	}
	
	public void setSize(String size)
	{
		this.size=size;
	}
  

}
