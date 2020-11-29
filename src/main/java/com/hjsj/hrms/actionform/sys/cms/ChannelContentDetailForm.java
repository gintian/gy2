package com.hjsj.hrms.actionform.sys.cms;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
/**
 * <p>Title:ChannelContentDetailForm</p>
 * <p>Description:频道内容明细</p>
 * <p>Company:hjsj</p>
 * <p>Create time:2007-04-07 9:25:00 am</p>
 * @author Lizhenwei
 * @version 1.0
 */
public class ChannelContentDetailForm extends FrameForm{
	/**chenmengqing changed at 20070414*/
	private String content_id ;//明细号
	private String channel_id ;//频道号
	/**
	 * 内容列表
	 * 支持分页显示
	 */
    private PaginationForm contentListForm=new PaginationForm(); 
    /**频道内容*/
    private RecordVo contentvo=new RecordVo("t_cms_content");
    
	private String title = "";//标题
	private String content ="";//内容
	private String out_url ="";//超链地址
	private String params ="";//链接参数
	private Date news_date = null;//新闻发布时间
	private String create_user = "";//发布人
	private int visible = 0;//显示标识
	private int content_type = 0;//内容展现方式
	private ArrayList list = new ArrayList();//频道明细内容列表
	private String selected_content_id_array[] = new String[0];//选择删除的明细id列表
	private String icon_url ="";//图片链接
	private String readOnly="";//是否可修改 false：不可，true：可以
	private String disabled = "";//是否可修改 false：不可，true：可以
	private String rowsCount = "";
	private ArrayList move_list = new ArrayList();//要调整顺序的频道明细列表
	private String[] right_fields;//由js方法调整顺序后的频道明细列表
	private String display = "";
	private String content_display = ""; 
	
	private ArrayList content_type_list = new ArrayList();
	private String path;
	private ArrayList zpReportList=new ArrayList();//招聘公告集合
	private String zpReport=""; //招聘公告 1.首页。2.社会。3.校园。
	private String zpReportContent="";//公告内容
	private String b_save="";
	public String getB_save() {
		return b_save;
	}
	public void setB_save(String b_save) {
		this.b_save = b_save;
	}
	public String getZpReportContent() {
		return zpReportContent;
	}
	public void setZpReportContent(String zpReportContent) {
		this.zpReportContent = zpReportContent;
	}
	public String getZpReport() {
		return zpReport;
	}
	public void setZpReport(String zpReport) {
		this.zpReport = zpReport;
	}
	@Override
    public void outPutFormHM(){
		this.setMove_list((ArrayList)this.getFormHM().get("move_list"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.getContentListForm().setList((ArrayList)this.getFormHM().get("list"));
		this.setContentvo((RecordVo)this.getFormHM().get("contentvo"));
		this.setContent_display((String)this.getFormHM().get("content_display"));
		this.setDisplay((String)this.getFormHM().get("display"));
		/*
		this.setList((ArrayList)this.getFormHM().get("list"));	
		this.setRowsCount((String)this.getFormHM().get("rowsCount"));
		this.setDisabled((String)this.getFormHM().get("disabled"));
		this.setContent_type_list((ArrayList)this.getFormHM().get("content_type_list"));
		this.setReadOnly((String)this.getFormHM().get("readOnly"));
		this.setIcon_url((String)this.getFormHM().get("icon_url"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setContent_id(new Integer((String)this.getFormHM().get("content_id")).intValue());
		this.setChannel_id(new Integer((String)this.getFormHM().get("channel_id")).intValue());
		this.setTitle((String)this.getFormHM().get("title"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setOut_url((String)this.getFormHM().get("out_url"));
		this.setParams((String)this.getFormHM().get("params"));
		this.setNews_date((Date)this.getFormHM().get("news_date"));
		this.setCreate_user((String)this.getFormHM().get("create_user"));
		this.setVisible(Integer.parseInt((String)this.getFormHM().get("visible")));
		this.setContent_type(new Integer((String)this.getFormHM().get("content_type")).intValue());
		*/
		this.setZpReportList((ArrayList)this.getFormHM().get("zpReportList"));
		this.setZpReport((String)this.getFormHM().get("zpReport"));
		this.setZpReportContent((String)this.getFormHM().get("zpReportContent"));
	}
	@Override
    public void inPutTransHM(){
		this.getFormHM().put("move_list",this.getMove_list());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("content_id",this.getContent_id());
		this.getFormHM().put("channel_id",this.getChannel_id());
		this.getFormHM().put("contentvo", this.getContentvo());
		this.getFormHM().put("selectedlist", this.getContentListForm().getSelectedList());
		this.getFormHM().put("display",this.getDisplay());
		 this.getFormHM().put("path",this.getPath());
		/*
		this.getFormHM().put("icon_url",this.getIcon_url());
		this.getFormHM().put("selected_content_id_array",this.getSelected_content_id_array());
		this.getFormHM().put("list",this.getList());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("out_url",this.getOut_url());
		this.getFormHM().put("params",this.getParams());
		this.getFormHM().put("news_date",this.getNews_date());
		this.getFormHM().put("create_user",this.getCreate_user());
		this.getFormHM().put("visible",this.getVisible()+"");
		this.getFormHM().put("content_type",this.getContent_type()+"");
		*/
		 this.getFormHM().put("zpReport", this.getZpReport());
		 this.getFormHM().put("zpReportContent", this.getZpReportContent());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String pajs=arg1.getSession().getServletContext().getRealPath("/UserFiles");
		this.setPath(SafeCode.encode(pajs));
		return super.validate(arg0, arg1);
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent_id() {
		return content_id;
	}
	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}
	public int getContent_type() {
		return content_type;
	}
	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public Date getNews_date() {
		return news_date;
	}
	public void setNews_date(Date news_date) {
		this.news_date = news_date;
	}
	public String getOut_url() {
		return out_url;
	}
	public void setOut_url(String out_url) {
		this.out_url = out_url;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public String[] getSelected_content_id_array() {
		return selected_content_id_array;
	}
	public void setSelected_content_id_array(String[] selected_content_id_array) {
		this.selected_content_id_array = selected_content_id_array;
	}
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	public String getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	public ArrayList getContent_type_list() {
		return content_type_list;
	}
	public void setContent_type_list(ArrayList content_type_list) {
		this.content_type_list = content_type_list;
	}
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	public String getRowsCount() {
		return rowsCount;
	}
	public void setRowsCount(String rowsCount) {
		this.rowsCount = rowsCount;
	}
	public PaginationForm getContentListForm() {
		return contentListForm;
	}
	public void setContentListForm(PaginationForm contentListForm) {
		this.contentListForm = contentListForm;
	}
	public RecordVo getContentvo() {
		return contentvo;
	}
	public void setContentvo(RecordVo contentvo) {
		this.contentvo = contentvo;
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		/**清零,checkbox*/
		//this.getContentvo().clearValues();
		this.getContentvo().setInt("visible",1);
		super.reset(arg0, arg1);
	}
	public ArrayList getMove_list() {
		return move_list;
	}
	public void setMove_list(ArrayList move_list) {
		this.move_list = move_list;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getContent_display() {
		return content_display;
	}
	public void setContent_display(String content_display) {
		this.content_display = content_display;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ArrayList getZpReportList() {
		return zpReportList;
	}
	public void setZpReportList(ArrayList zpReportList) {
		this.zpReportList = zpReportList;
	}
	

}
