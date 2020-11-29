package com.hjsj.hrms.actionform.sys.options.template;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:TemplateForm.java</p>
 * <p>Description:通知摸板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007.03.20 15:04 pm</p>
 * @author Lizhenwei
 * @version 1.0
 */

public class TemplateSetForm extends FrameForm{
	 
	private String str_sql ="";
	private String str_whl ="";
	private String name = "";//摸板名称
	private String title = "";//邮件标题
	private String address = "";//邮件地址
	private String content = "";//摸板内容
	private String zpLoop = "";//招聘环节
	private String zpLoopNew="";
	private String template_type="";//摸板类型
	private String id = "";//子系统号FK
	private String b0110 = "";//归属单位编码
	private ArrayList alist = new ArrayList();//招聘模板列表
	private String selected_template_id_array[] = new String[0];//选择删除模板列表
	private ArrayList zpLoop_list = new ArrayList();//招聘环节列表
	private String codeitemid = "";//招聘环节id
	private String codeitemdesc = "";//招聘环节名称
	private ArrayList zbj_list = new ArrayList();//指标集列表
	private ArrayList zb_list = new ArrayList();//指标列表
	//------------------------------
	private String zbj_id = "";//指标集id
	private String zb_id = "";//指标id
	private String type="";
	@Override
    public void outPutFormHM(){
		this.setZpLoopNew((String)this.getFormHM().get("zpLoopNew"));
		this.setType((String)this.getFormHM().get("type"));
	    this.setZb_id((String)this.getFormHM().get("zb_id"));
	    this.setZbj_id((String)this.getFormHM().get("zbj_id"));
		this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setZpLoop_list((ArrayList)this.getFormHM().get("zpLoop_list"));
	    this.setAlist((ArrayList)this.getFormHM().get("alist"));
		this.setStr_sql((String)this.getFormHM().get("str_sql"));
		this.setStr_whl((String)this.getFormHM().get("str_whl"));
		this.setName((String)this.getFormHM().get("name"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setAddress((String)this.getFormHM().get("address"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setZpLoop((String)this.getFormHM().get("zpLoop"));
		this.setTemplate_type((String)this.getFormHM().get("template_type"));
		this.setId((String)this.getFormHM().get("id"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setZb_list((ArrayList)this.getFormHM().get("zb_list"));
		this.setZbj_list((ArrayList)this.getFormHM().get("zbj_list"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zpLoopNew",this.getZpLoopNew());
		this.getFormHM().put("type",this.getType());
	    this.getFormHM().put("zpLoop_list",this.getZpLoop_list());
		this.getFormHM().put("codeitemdesc",this.getCodeitemdesc());
		this.getFormHM().put("codeitemid",this.getCodeitemid());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("address",this.getAddress());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("zpLoop",this.getZpLoop());
		this.getFormHM().put("template_type",this.getTemplate_type());
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("selected_template_id_array",this.getSelected_template_id_array());
		this.getFormHM().put("zb_list",this.getZb_list());
		this.getFormHM().put("zbj_list",this.getZbj_list());
		this.getFormHM().put("zb_id",this.getZb_id());
		this.getFormHM().put("zbj_id",this.getZbj_id());
	}


	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplate_type() {
		return template_type;
	}
	public void setTemplate_type(String template_type) {
		this.template_type = template_type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getZpLoop() {
		return zpLoop;
	}
	public void setZpLoop(String zpLoop) {
		this.zpLoop = zpLoop;
	}

	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}

	public String getStr_whl() {
		return str_whl;
	}

	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}

	public ArrayList getAlist() {
		return alist;
	}

	public void setAlist(ArrayList alist) {
		this.alist = alist;
	}

	public String[] getSelected_template_id_array() {
		return selected_template_id_array;
	}

	public void setSelected_template_id_array(String[] selected_template_id_array) {
		this.selected_template_id_array = selected_template_id_array;
	}
	public ArrayList getZpLoop_list() {
		return zpLoop_list;
	}

	public void setZpLoop_list(ArrayList zpLoop_list) {
		this.zpLoop_list = zpLoop_list;
	}

	public String getCodeitemdesc() {
		return codeitemdesc;
	}

	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public ArrayList getZb_list() {
		return zb_list;
	}
	public void setZb_list(ArrayList zb_list) {
		this.zb_list = zb_list;
	}
	public ArrayList getZbj_list() {
		return zbj_list;
	}
	public void setZbj_list(ArrayList zbj_list) {
		this.zbj_list = zbj_list;
	}
	public String getZb_id() {
		return zb_id;
	}
	public void setZb_id(String zb_id) {
		this.zb_id = zb_id;
	}
	public String getZbj_id() {
		return zbj_id;
	}
	public void setZbj_id(String zbj_id) {
		this.zbj_id = zbj_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getZpLoopNew() {
		return zpLoopNew;
	}
	public void setZpLoopNew(String zpLoopNew) {
		this.zpLoopNew = zpLoopNew;
	}
}