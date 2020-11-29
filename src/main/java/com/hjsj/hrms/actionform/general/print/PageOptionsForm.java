/**
 * 
 */
package com.hjsj.hrms.actionform.general.print;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.struts.action.FrameForm;


/**
 * <p>Title:PageOptionsForm</p>
 * <p>Description:页面设置参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-29:17:23:28</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PageOptionsForm extends FrameForm {
	
	/**打印页面设置参数*/
	private ReportParseVo parsevo=new ReportParseVo();	
	
	private String state;//报表参数，暂时0：考勤报表；1综合报表
    private String id;
    private String xmlstr;
    private String xmltype;
	private String sytle_title;
    private String  sytle_head;
    private String  sytle_tile;

	/**工资发放报表和工资分析用参数*/
    private String rsid;//表分类id
    private String rsdtlid;//表id




	public String getXmltype() {
		return xmltype;
	}


	public void setXmltype(String xmltype) {
		this.xmltype = xmltype;
	}


	public String getXmlstr() {
		return xmlstr;
	}


	public void setXmlstr(String xmlstr) {
		this.xmlstr = xmlstr;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	@Override
    public void outPutFormHM() {
		this.setRsid((String)this.getFormHM().get("rsid"));
		this.setRsdtlid((String)this.getFormHM().get("rsdtlid"));
		this.setParsevo((ReportParseVo) this.getFormHM().get("parsevo"));
        this.setState((String)this.getFormHM().get("state"));
        this.setId((String)this.getFormHM().get(("id")));
        this.setXmlstr((String)this.getFormHM().get("xmlstr"));
        this.setXmltype((String)this.getFormHM().get("xmltype"));
        this.setState((String)this.getFormHM().get("state"));        
        this.setSytle_head((String)this.getFormHM().get("sytle_head"));
        this.setSytle_title((String)this.getFormHM().get("sytle_title"));
        this.setSytle_tile((String)this.getFormHM().get("sytle_tile"));
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("rsid", this.getRsid());
		this.getFormHM().put("rsdtlid", this.getRsdtlid());
		this.getFormHM().put("parsevo",this.parsevo);	
        this.getFormHM().put("state",this.getState());     
        this.getFormHM().put("id",this.getId());
        
	}
	/**
	 * 
	 */	public ReportParseVo getParsevo() {
		return parsevo;
	}


	public void setParsevo(ReportParseVo parsevo) {
		this.parsevo = parsevo;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getSytle_head() {
		return sytle_head;
	}


	public void setSytle_head(String sytle_head) {
		this.sytle_head = sytle_head;
	}


	public String getSytle_tile() {
		return sytle_tile;
	}


	public void setSytle_tile(String sytle_tile) {
		this.sytle_tile = sytle_tile;
	}


	public String getSytle_title() {
		return sytle_title;
	}


	public void setSytle_title(String sytle_title) {
		this.sytle_title = sytle_title;
	}


	public String getRsid() {
		return rsid;
	}


	public void setRsid(String rsid) {
		this.rsid = rsid;
	}


	public String getRsdtlid() {
		return rsdtlid;
	}


	public void setRsdtlid(String rsdtlid) {
		this.rsdtlid = rsdtlid;
	}

}
