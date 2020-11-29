package com.hjsj.hrms.actionform.sys.cms;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:ChannelForm</p>
 * <p>Description:频道内容</p>
 * <p>Company:hjsj</p>
 * <p>Create time: 2007-04-07  8:52:02 am</p>
 * @author Lizhenwei
 * @version 1.0
 */

public class ChannelForm extends FrameForm{
	private String contentChannelTree = "";//频道内容树代码
	private int channel_id = 0;//频道号
	private int parent_id = 0;//上级节点号
	private String name = "";//频道名称
	private int visible = 0;//显示标识 0:不显示,1:显示
	private String function_id = "";//功能号
	private int visible_type = 0;//频道显示方式 0:平铺,1: 菜单
	private String icon_url = "";//图片
	private int icon_width = 0;//宽度
	private int icon_height = 0;//高度
	private int menu_width=70;//菜单宽度
	private RecordVo channel_vo=new RecordVo("t_cms_channel");
	private ArrayList visible_type_list = new ArrayList();//显示方式列表
	private String[] right_fields ;//由js方法调整顺序后的频道列表
	private ArrayList list = new ArrayList();//要调整顺序的频道列表
	private String isTop;//判断是否是顶级节点 yes:是，no:不是
	private FormFile logofile;
	private String projectPath;
	private String path;
	private FormFile oneFile;
	private FormFile twoFile;
	private String hbType;//文件格式
	private String lfType;//文件格式
	@Override
    public void outPutFormHM(){
		this.setOneFile((FormFile)this.getFormHM().get("oneFile"));
		this.setTwoFile((FormFile)this.getFormHM().get("twoFile"));
		this.setPath((String)this.getFormHM().get("path"));
		this.setLogofile((FormFile)this.getFormHM().get("logofile"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setVisible_type_list((ArrayList)this.getFormHM().get("visible_type_list"));
		this.setChannel_vo((RecordVo)this.getFormHM().get("channel_vo"));
		this.setContentChannelTree((String)this.getFormHM().get("contentChannelTree"));
		this.setChannel_id(Integer.parseInt((String)this.getFormHM().get("channel_id")));
		this.setParent_id(new Integer((String)this.getFormHM().get("parent_id")).intValue());
		this.setName((String)this.getFormHM().get("name"));
		this.setVisible(new Integer((String)this.getFormHM().get("visible")).intValue());
		this.setFunction_id((String)this.getFormHM().get("function_id"));
		this.setVisible_type(new Integer((String)this.getFormHM().get("visible_type")).intValue());
		this.setIcon_url((String)this.getFormHM().get("icon_url"));
		this.setIcon_width(new Integer((String)this.getFormHM().get("icon_width")).intValue());
		this.setIcon_height(new Integer((String)this.getFormHM().get("icon_height")).intValue());
		this.setMenu_width(new Integer((String)this.getFormHM().get("menu_width")).intValue());
		this.setIsTop((String)this.getFormHM().get("isTop"));
	}
	@Override
    public void inPutTransHM(){
		this.getFormHM().put("hbType", this.getHbType());
		this.getFormHM().put("lfType", this.getLfType());
		this.getFormHM().put("oneFile", this.getOneFile());
		this.getFormHM().put("twoFile", this.getTwoFile());
		this.getFormHM().put("path", this.getPath());
		this.getFormHM().put("logofile", this.getLogofile());
		this.getFormHM().put("channel_id",this.getChannel_id()+"");
		this.getFormHM().put("parent_id",this.getParent_id()+"");
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("visible",this.getVisible()+"");
		this.getFormHM().put("function_id",this.getFunction_id());
		this.getFormHM().put("visible_type",this.getVisible_type()+"");
		this.getFormHM().put("icon_url",this.getIcon_url());
		this.getFormHM().put("icon_width",this.getIcon_width()+"");		
		this.getFormHM().put("icon_height",this.getIcon_height()+"");
		this.getFormHM().put("menu_width", String.valueOf(this.getMenu_width()));
		this.getFormHM().put("list",this.getList());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("isTop",this.getIsTop());
		this.getFormHM().put("projectPath",this.getProjectPath());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String pajs=arg1.getSession().getServletContext().getRealPath("/images");
		this.setProjectPath(SafeCode.encode(pajs));
		return super.validate(arg0, arg1);
	}
	public int getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	public String getFunction_id() {
		return function_id;
	}
	public void setFunction_id(String function_id) {
		this.function_id = function_id;
	}
	public int getIcon_height() {
		return icon_height;
	}
	public void setIcon_height(int icon_height) {
		this.icon_height = icon_height;
	}
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	public int getIcon_width() {
		return icon_width;
	}
	public void setIcon_width(int icon_width) {
		this.icon_width = icon_width;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParent_id() {
		return parent_id;
	}
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public int getVisible_type() {
		return visible_type;
	}
	public void setVisible_type(int visible_type) {
		this.visible_type = visible_type;
	}
	public String getContentChannelTree() {
		return contentChannelTree;
	}
	public void setContentChannelTree(String contentChannelTree) {
		this.contentChannelTree = contentChannelTree;
	}
	public RecordVo getChannel_vo() {
		return channel_vo;
	}
	public void setChannel_vo(RecordVo channel_vo) {
		this.channel_vo = channel_vo;
	}
	public ArrayList getVisible_type_list() {
		return visible_type_list;
	}
	public void setVisible_type_list(ArrayList visible_type_list) {
		this.visible_type_list = visible_type_list;
	}
	public int getMenu_width() {
		return menu_width;
	}
	public void setMenu_width(int menu_width) {
		this.menu_width = menu_width;
	}
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
	public String getIsTop() {
		return isTop;
	}
	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}
	public FormFile getLogofile() {
		return logofile;
	}
	public void setLogofile(FormFile logofile) {
		this.logofile = logofile;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public FormFile getOneFile() {
		return oneFile;
	}
	public void setOneFile(FormFile oneFile) {
		this.oneFile = oneFile;
	}
	public FormFile getTwoFile() {
		return twoFile;
	}
	public void setTwoFile(FormFile twoFile) {
		this.twoFile = twoFile;
	}
	public String getHbType() {
		return hbType;
	}
	public void setHbType(String hbType) {
		this.hbType = hbType;
	}
	public String getLfType() {
		return lfType;
	}
	public void setLfType(String lfType) {
		this.lfType = lfType;
	}

}
