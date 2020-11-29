package com.hjsj.hrms.actionform.sys.customreport;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:CustomReportForm
 * </p>
 * <p>
 * Description:自定制报表form类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-6
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class CustomReportForm extends FrameForm {
	
	/**业务模块集合,取自t_hr_subsys（子系统信息表）表*/
	private ArrayList businessModuleList;
	/**业务模块默认值*/
	private String businessModuleValue;
	/**分页*/
	private PaginationForm customReportForm = new PaginationForm();
	/**分页*/
	private PaginationForm roleForm = new PaginationForm();
	/**自制报表的内容信息*/
	private ArrayList infoList;
	/**报表类型,=0(自定制),=1(统计报表),=2(登记表),=3(高级花名册)*/
	private Integer reportType;
	/**所有报表类型*/
	private ArrayList reportTypeList;
	/**报表名称*/
	private String name;
	/**描述*/
	private String description;
	/**模板文件*/
	private FormFile templateFile;
	/**sql语句文件*/
	private FormFile sqlFile;
	/**发布状态，1为发布，0为未发布*/
	private Integer flag;
	/**关联报表号，高级花名册、统计报表、登记表表格号*/
	private int linkTabid;
	/**选中的记录*/
	private ArrayList selectList;
	/**是否是修改,1为修改，其他为增加*/
	private String isEdit;
	/**记录id*/
	private String id;
	/**数接点*/
	private String treeCode;
	/**关联的id*/
	private Integer link_tabid;
	/**用户树*/
	private String userTree;
	/**高级花名册对应的模块号*/
	private HashMap hMap;
	/**是否上传了sql xml文件*/
	private String sqlfileExist;
	/**扩展名*/
	private String ext;
	/**登记表id对应的flaga的值*/
	private HashMap fMap;
	/**统计报表需要的参数*/
	private String htmlCode="";
	private String rows="0";
	private String cols="0";
	private String tabid="0";
	private String param_str="";
	private String narch="0";
	
	/**当选择一个报表时，有权限的用户*/
	private String privusers = "";
	/**角色列表*/
	private ArrayList roleList;
	/**记录数*/
	private int countrows;
	/**有权限的角色*/
	private String rolesHas;
	
	/**总共选择了几张自定义报表*/
	private String num;
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getParam_str() {
		return param_str;
	}

	public void setParam_str(String param_str) {
		this.param_str = param_str;
	}

	public String getNarch() {
		return narch;
	}

	public void setNarch(String narch) {
		this.narch = narch;
	}

	public String getHtmlCode() {
		return htmlCode;
	}

	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public HashMap getFMap() {
		return fMap;
	}

	public void setFMap(HashMap map) {
		fMap = map;
	}

	public HashMap getHMap() {
		return hMap;
	}

	public void setHMap(HashMap map) {
		hMap = map;
	}

	public Integer getLink_tabid() {
		return link_tabid;
	}

	public void setLink_tabid(Integer link_tabid) {
		this.link_tabid = link_tabid;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList getSelectList() {
		return selectList;
	}

	public void setSelectList(ArrayList selectList) {
		this.selectList = selectList;
	}

	/**
	 *  
	 */
	public CustomReportForm() {
		super();
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setBusinessModuleList((ArrayList) this.getFormHM().get("businessModuleList"));
		this.setBusinessModuleValue((String) this.getFormHM().get("businessModuleValue"));
		this.setInfoList((ArrayList) this.getFormHM().get("infoList"));
		this.setReportTypeList((ArrayList) this.getFormHM().get("reportTypeList"));
		this.getCustomReportForm().setList(this.getInfoList());
		this.setReportTypeList(this.getReportTypeList());
		this.setId((String) this.getFormHM().get("id"));
		this.setReportType((Integer) this.getFormHM().get("reportType"));
		this.setName((String) this.getFormHM().get("name"));
		this.setDescription((String) this.getFormHM().get("description"));
		this.setFlag((Integer) this.getFormHM().get("flag"));
		this.getFormHM().remove("flag");
		this.setIsEdit((String) this.getFormHM().get("isEdit"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setLink_tabid((Integer) this.getFormHM().get("link_tabid"));
		this.setUserTree((String) this.getFormHM().get("usertree"));
		this.setHMap((HashMap) this.getFormHM().get("hMap"));
		this.setFMap((HashMap) this.getFormHM().get("fMap"));
		this.setHtmlCode((String)this.getFormHM().get("htmlCode"));
		this.setRows((String)this.getFormHM().get("rows"));
		this.setCols((String)this.getFormHM().get("cols"));
		if(this.getFormHM().get("tabid")!=null)
			this.setTabid((String)this.getFormHM().get("tabid"));
		this.setParam_str((String)this.getFormHM().get("param_str"));
		this.setNarch((String)this.getFormHM().get("narch"));
		this.setSqlfileExist((String) this.getFormHM().get("sqlfileExist"));
		this.setExt((String) this.getFormHM().get("ext"));
		this.setPrivusers((String) this.getFormHM().get("privusers")); 
		this.getFormHM().remove("privusers");
		this.setRoleList((ArrayList) this.getFormHM().get("rolelist"));
		if (this.getRoleList() != null) {
			this.roleForm.setList(this.getRoleList());
			//this.setCountrows(this.getRoleList().size());
			
		}
		this.setRolesHas((String) this.getFormHM().get("rolesHas"));
		this.setCountrows(this.getPagerows());
		this.setNum((String) this.getFormHM().get("num"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
 	    this.getFormHM().put("businessModuleValue", this.getBusinessModuleValue());
 	    if (this.getCustomReportForm().getPagination() != null) {
 	     this.getFormHM().put("selectList", this.getCustomReportForm().getSelectedList());
 	    }
 	    this.getFormHM().put("reportType", this.getReportType());
 	    this.getFormHM().put("name", this.getName());
 	    this.getFormHM().put("description", this.getDescription());
 	    this.getFormHM().put("flag", this.getFlag());
 	    this.getFormHM().put("templateFile", this.getTemplateFile());
 	    this.getFormHM().put("sqlFile", this.getSqlFile());
 	    this.getFormHM().put("id", this.getId());
 	    this.getFormHM().put("link_tabid", this.getLink_tabid());
 	   this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("rows",this.getRows());
		this.getFormHM().put("cols",this.getCols());
		this.getFormHM().remove("sqlfileExist");
		this.getFormHM().remove("ext");
		this.setPrivusers("");
 	    	   
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{	
        if("/system/options/customreport".equals(arg0.getPath())&& arg1.getParameter("b_search")!=null && arg1.getParameter("toFirst")!=null && "yes".equals(arg1.getParameter("toFirst")))
        {
            if(this.getCustomReportForm().getPagination() != null)
            	//返回第一页
            	this.getCustomReportForm().getPagination().firstPage();
        }	
        
        return super.validate(arg0, arg1);
   }
	public ArrayList getBusinessModuleList() {
		return businessModuleList;
	}

	public void setBusinessModuleList(ArrayList businessModuleList) {
		this.businessModuleList = businessModuleList;
	}

	public String getBusinessModuleValue() {
		return businessModuleValue;
	}

	public void setBusinessModuleValue(String businessModuleValue) {
		this.businessModuleValue = businessModuleValue;
	}

	public PaginationForm getCustomReportForm() {
		return customReportForm;
	}

	public void setCustomReportForm(PaginationForm customReportForm) {
		this.customReportForm = customReportForm;
	}

	public ArrayList getInfoList() {
		return infoList;
	}

	public void setInfoList(ArrayList infoList) {
		this.infoList = infoList;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public ArrayList getReportTypeList() {
		return reportTypeList;
	}

	public void setReportTypeList(ArrayList reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FormFile getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(FormFile templateFile) {
		this.templateFile = templateFile;
	}

	public FormFile getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(FormFile sqlFile) {
		this.sqlFile = sqlFile;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public int getLinkTabid() {
		return linkTabid;
	}

	public void setLinkTabid(int linkTabid) {
		this.linkTabid = linkTabid;
	}

	public String getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}

	public String getUserTree() {
		return userTree;
	}

	public void setUserTree(String userTree) {
		this.userTree = userTree;
	}

	public String getSqlfileExist() {
		return sqlfileExist;
	}

	public void setSqlfileExist(String sqlfileExist) {
		this.sqlfileExist = sqlfileExist;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getPrivusers() {
		return privusers;
	}

	public void setPrivusers(String privusers) {
		this.privusers = privusers;
	}

	public ArrayList getRoleList() {
		return roleList;
	}

	public void setRoleList(ArrayList roleList) {
		this.roleList = roleList;
	}

	public PaginationForm getRoleForm() {
		return roleForm;
	}

	public void setRoleForm(PaginationForm roleForm) {
		this.roleForm = roleForm;
	}

	public int getCountrows() {
		return countrows;
	}

	public void setCountrows(int countrows) {
		this.countrows = countrows;
	}

	public String getRolesHas() {
		return rolesHas;
	}

	public void setRolesHas(String rolesHas) {
		this.rolesHas = rolesHas;
	}
	
}