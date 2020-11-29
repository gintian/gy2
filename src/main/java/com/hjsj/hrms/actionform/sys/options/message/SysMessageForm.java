package com.hjsj.hrms.actionform.sys.options.message;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;

public class SysMessageForm extends FrameForm{
	private String start_date;
	private String days;
	private String constant;
	private String flag;
	private String view_hr;//显示范围业务
	private String view_em;//显示范围自助
	private String textConstant;// 纯文本内容
	private String backgroudimage;
	private FormFile bgimage;
	public String getView_em() {
		return view_em;
	}
	public void setView_em(String view_em) {
		this.view_em = view_em;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	@Override
    public void outPutFormHM()
	{
		this.setDays((String)this.getFormHM().get("days"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setConstant((String)this.getFormHM().get("constant"));
		this.setView_em((String)this.getFormHM().get("view_em"));
		this.setView_hr((String)this.getFormHM().get("view_hr"));
		this.setBackgroudimage((String)this.getFormHM().get("backgroudimage"));
		this.setBgimage(null);
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("days",this.getDays());
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("constant",this.getConstant());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("view_hr", this.getView_hr());
		this.getFormHM().put("view_em", this.getView_em());
		this.getFormHM().put("textConstant", this.getTextConstant());
		this.getFormHM().put("bgimage", bgimage);
	}
	public String getConstant() {
		return constant;
	}
	public void setConstant(String constant) {
		this.constant = constant;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getView_hr() {
		return view_hr;
	}
	public void setView_hr(String view_hr) {
		this.view_hr = view_hr;
	}
	public String getTextConstant() {
		return textConstant;
	}
	public void setTextConstant(String textConstant) {
		this.textConstant = textConstant;
	}
	public String getBackgroudimage() {
		return backgroudimage;
	}
	public void setBackgroudimage(String backgroudimage) {
		this.backgroudimage = backgroudimage;
	}
	public FormFile getBgimage() {
		return bgimage;
	}
	public void setBgimage(FormFile bgimage) {
		this.bgimage = bgimage;
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		// TODO Auto-generated method stub
		try{
		String realurl = request.getSession().getServletContext().getRealPath("/UserFiles/Image");
		   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		   {
		  	  realurl=request.getSession().getServletContext().getResource("/UserFiles/Image").getPath();//.substring(0);
		      if(realurl!=null){
		      if(realurl.indexOf(':')!=-1)
		  	  {
				 realurl=realurl.substring(1);   
		   	  }
		  	  else
		   	  {
				 realurl=realurl.substring(0);      
		   	  }
		      int nlen=realurl.length();
		  	  StringBuffer buf=new StringBuffer();
		   	  buf.append(realurl);
		  	  buf.setLength(nlen-1);
		   	  realurl=buf.toString();
		   	  }
		   }
		   this.getFormHM().put("path", realurl);
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.validate(mapping, request);
	}
	
	
}
