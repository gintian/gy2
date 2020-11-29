package com.hjsj.hrms.actionform.kq.team;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ArrayGroupForm extends FrameForm
{
	private String treeGroupCode;//树形菜单，在HtmlMenu中
	private String sqlstr;
	private String column;
	private String where;
	private PaginationForm recordListForm=new PaginationForm();    
	private ArrayList vo_list=new ArrayList();
	private ArrayList fieldlist=new ArrayList();
	private String a_code;
	private String group_id;
	private String name;
	private String org_id;
	private String org_name;
	private String save_flag;
	/**选择类型check radio世间0、1、2*/
	private String selecttype="2";
	private String select_type="0";
	/**加载人0 、1*/
	private String flag="1";
	/**加载用户库
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
	 * */
	private String dbtype="0";
	/**权限过滤*/
	private String priv="1"; 
	private String org_code;
	private String kq_type;
	private ArrayList dlist=new ArrayList();
	private String a0101_s;
	private String dbper;
	private ArrayList kq_list=new ArrayList();
	private String select_pre;
	//自动分配班组
	private String fil;
	private String start_date;
    private String end_date;
    //班组名称
    private String groupName;
    private String hostid; //班组指标
    private String nbase;
    //调换班组
	private ArrayList classlist = new ArrayList(); //代码类
	private String joincodename; //名字
	private String classId; //班组id
	private String start_date_save;
	private String end_date_save;
	private String zhji;
	private String return_code="";
	
	private String uplevel = "";
	
	private ArrayList codesetid = new ArrayList(); //班子所属机构类别
	
	private String unCodeitemid;// 用于 传递选中的  单位，部门，岗位编号 到 文件 ，班组设置
	private String codeid; // 用于临时存放满足条的，单位，部门，岗位的编号
	
	
	public String getCodeid() {
		return codeid;
	}
	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
	public String getUnCodeitemid() {
		return unCodeitemid;
	}
	public void setUnCodeitemid(String unCodeitemid) {
		this.unCodeitemid = unCodeitemid;
	}
	public ArrayList getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(ArrayList codesetid) {
		this.codesetid = codesetid;
	}
	public ArrayList getKq_list() {
		return kq_list;
	}
	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getA0101_s() {
		return a0101_s;
	}
	public void setA0101_s(String a0101_s) {
		this.a0101_s = a0101_s;
	}
	public String getDbper() {
		return dbper;
	}
	public void setDbper(String dbper) {
		this.dbper = dbper;
	}
	public ArrayList getDlist() {
		return dlist;
	}
	public void setDlist(ArrayList dlist) {
		this.dlist = dlist;
	}
	public String getKq_type() {
		return kq_type;
	}
	public void setKq_type(String kq_type) {
		this.kq_type = kq_type;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPriv() {
		return priv;
	}
	public void setPriv(String priv) {
		this.priv = priv;
	}
	public String getSelecttype() {
		return selecttype;
	}
	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}
	public String getSave_flag() {
		return save_flag;
	}
	public void setSave_flag(String save_flag) {
		this.save_flag = save_flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	
	@Override
    public void outPutFormHM()
	{
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setTreeGroupCode((String)this.getFormHM().get("treeGroupCode"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("vo_list"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setGroup_id((String)this.getFormHM().get("group_id"));
		this.setName((String)this.getFormHM().get("name"));
		this.setSave_flag((String)this.getFormHM().get("save_flag"));
		this.setOrg_code((String)this.getFormHM().get("org_code"));
		this.setKq_type((String)this.getFormHM().get("kq_type"));
		this.setDlist((ArrayList)this.getFormHM().get("dlist"));
		this.setA0101_s((String)this.getFormHM().get("a0101_s"));
		this.setDbper((String)this.getFormHM().get("dbper"));
		this.setOrg_id((String)this.getFormHM().get("org_id"));
		this.setOrg_name((String)this.getFormHM().get("org_name"));
		this.setSelect_pre((String)this.getFormHM().get("select_pre"));
		this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
		this.setFil((String)this.getFormHM().get("fil"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setGroupName((String)this.getFormHM().get("groupName"));
		this.setHostid((String)this.getFormHM().get("hostid"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setClasslist((ArrayList)this.getFormHM().get("classlist"));
		this.setJoincodename((String)this.getFormHM().get("joincodename"));
		this.setClassId((String)this.getFormHM().get("classId"));
		this.setStart_date_save((String)this.getFormHM().get("start_date_save"));
		this.setEnd_date_save((String)this.getFormHM().get("end_date_save"));
		this.setZhji((String)this.getFormHM().get("zhji"));
		this.setReturn_code((String)this.getFormHM().get("return_code"));
		this.setSelect_type((String)this.getFormHM().get("select_type"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setCodesetid((ArrayList)this.getFormHM().get("codesetid"));
		
		this.setUnCodeitemid((String)this.getFormHM().get("unCodeitemid"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
	}
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("name",this.getName());
		if(this.getPagination()!=null)			
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("group_id",this.getGroup_id());
		this.getFormHM().put("save_flag",this.getSave_flag());
		this.getFormHM().put("a0101_s",this.getA0101_s());
		this.getFormHM().put("dbper",this.getDbper());
		this.getFormHM().put("org_id", this.getOrg_id());
		this.getFormHM().put("select_pre", select_pre);
		this.getFormHM().put("fil", this.getFil());
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("joincodename",this.getJoincodename());
		this.getFormHM().put("classId",this.getClassId());
		this.getFormHM().put("start_date_save",this.getStart_date_save());
		this.getFormHM().put("end_date_save",this.getEnd_date_save());
		this.getFormHM().put("zhji",this.getZhji());
		this.getFormHM().put("return_code", this.getReturn_code());
		this.getFormHM().put("select_type", this.select_type);
		this.getFormHM().put("codesetid", this.getCodesetid());
    }
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public ArrayList getVo_list() {
		return vo_list;
	}
	public void setVo_list(ArrayList vo_list) {
		this.vo_list = vo_list;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/team/array_set/search_array".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }	   
	    //kq/team/array_set/search_array.do?b_query
	    if("/kq/team/array_set/search_array".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        if(this.recordListForm.getPagination()!=null)
	         this.recordListForm.getPagination().firstPage();//?
	    }	
	    if("/kq/team/array_group/search_array_emp_data".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.getFormHM().put("selectedinfolist",new ArrayList());
	        
	    }	
	    if("/kq/team/array_group/load_emp_data_record".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }	
	    if("/kq/team/array_set/search_array_data".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.recordListForm.getPagination()!=null)
	         this.recordListForm.getPagination().firstPage();//?
	    }
	    if("/kq/team/array_group/load_host_data_record".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    
	    //zxj 20141117 几个页面混用form， 班组页面是list分页，班组人员是db分页，导致分页选中数据混乱，删除选中班组人员放到这里
	    if("/kq/team/array_set/search_array_data".equals(arg0.getPath())&&arg1.getParameter("b_delete")!=null)
        {
	        this.getFormHM().put("selected_vo_list",(ArrayList)this.getRecordListForm().getSelectedList());
        }
	    
	    return super.validate(arg0, arg1);
	}
	public String getTreeGroupCode() {
		return treeGroupCode;
	}
	public void setTreeGroupCode(String treeGroupCode) {
		this.treeGroupCode = treeGroupCode;
	}
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getFil() {
		return fil;
	}
	public void setFil(String fil) {
		this.fil = fil;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public ArrayList getClasslist() {
		return classlist;
	}
	public void setClasslist(ArrayList classlist) {
		this.classlist = classlist;
	}
	public String getJoincodename() {
		return joincodename;
	}
	public void setJoincodename(String joincodename) {
		this.joincodename = joincodename;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getStart_date_save() {
		return start_date_save;
	}
	public void setStart_date_save(String start_date_save) {
		this.start_date_save = start_date_save;
	}
	public String getEnd_date_save() {
		return end_date_save;
	}
	public void setEnd_date_save(String end_date_save) {
		this.end_date_save = end_date_save;
	}
	public String getZhji() {
		return zhji;
	}
	public void setZhji(String zhji) {
		this.zhji = zhji;
	}
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getSelect_type() {
		return select_type;
	}
	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
}
