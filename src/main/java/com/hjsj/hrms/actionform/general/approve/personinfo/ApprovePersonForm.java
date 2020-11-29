package com.hjsj.hrms.actionform.general.approve.personinfo;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class ApprovePersonForm extends FrameForm {
	private String abkflag;
	private String pdbflag;
	private String sql;
	private String where;
	private String column;
	private String treeCode;
	private String setname;
	private ArrayList setnamelist;
	private ArrayList setidlist;
	private PaginationForm pageListForm = new PaginationForm();
	private ArrayList itemnamelist;
	private ArrayList itemidlist;
	private String unitid;
	private String departmentid;
	private String selstr;
	private String state;
	private String stateselstr;
	private String code="";
	//从控制面板（我的任务）进来 task = 1；从员工审核进来task=0.
	private String task;

	/*
	 * 翻译叶面信息 (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private ArrayList codeidlist;
	private String a0100;
	private String action;
	private String setprv;
	private String bcmessage;
	private String pinfo;
	private String ff;
	private String a_code;
	private String inputchinfor;
	private ArrayList spflaglist;
	private String sp_flag;
	private String fcheck;
	private String chg_id;
	private String setid;
	private ArrayList oldFieldList=new ArrayList();
	private ArrayList newFieldList=new ArrayList();
	private ArrayList fieldlist=new ArrayList();
	private ArrayList keylist=new ArrayList();
	private ArrayList cenlist=new ArrayList();
	private String keyid;
	private String typeid;
	private ArrayList typelist=new ArrayList();
	private String sp_idea;
	private String checkflag;
	private String allflag;
	private ArrayList sequenceList=new ArrayList();
	private String sequenceid;
	private String userid;
	private String viewitem;
	private String kind;
	private String returnflag;//主页、任务列表调用  返回标志 2014-05-15 wangrd
	
	private String checked_may_reject="";//审核后可驳回 2010.01.08 s.xin加
	private String returnVa = "";
	/**按人员姓名查询*/
	private String employeeName;
	
	private String uplevel;
	
	
	
	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	//信息审批
	private TreeMap changelist;
    private String b0110desc;
    private String e0122desc;
    private String username;
    private String nbase;
    private ArrayList showlist;
    private String showinfo = "all";

    private String redundantInfo="";//超编信息（只记录批量批准时，强制时超编后不入库信息提示到前台）
	public String getRedundantInfo() {
		return redundantInfo;
	}

	public void setRedundantInfo(String redundantInfo) {
		this.redundantInfo = redundantInfo;
	}

    public String getShowinfo() {
		return showinfo;
	}

	public void setShowinfo(String showinfo) {
		this.showinfo = showinfo;
	}


	public ArrayList getShowlist() {
		return showlist;
	}

	public void setShowlist(ArrayList showlist) {
		this.showlist = showlist;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getB0110desc() {
		return b0110desc;
	}

	public void setB0110desc(String b0110desc) {
		this.b0110desc = b0110desc;
	}

	public String getE0122desc() {
		return e0122desc;
	}

	public void setE0122desc(String e0122desc) {
		this.e0122desc = e0122desc;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public TreeMap getChangelist() {
		return changelist;
	}

	public void setChangelist(TreeMap changelist) {
		this.changelist = changelist;
	}

	public String getReturnVa() {
		return returnVa;
	}

	public void setReturnVa(String returnVa) {
		this.returnVa = returnVa;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setSelstr((String) hm.get("selstr"));
		this.setSetnamelist((ArrayList) hm.get("setnamelist"));
		this.setSetidlist((ArrayList) hm.get("setidlist"));
		this.setPdbflag((String)hm.get("pdbflag"));
		this.setItemnamelist((ArrayList)hm.get("itemdesclist"));
		this.setItemidlist((ArrayList)hm.get("itemidlist"));
		this.setCodeidlist((ArrayList)hm.get("codeidlist"));
		this.setStateselstr((String) hm.get("stateselstr"));
		this.setState((String) hm.get("state"));
		this.setA0100((String) hm.get("a0100"));
		this.setBcmessage((String)hm.get("bcmessage"));
		this.setFf((String) hm.get("ff"));
		this.setA_code((String) hm.get("a_code"));
		this.setInputchinfor((String)hm.get("inputchinfor"));
		this.setSpflaglist((ArrayList)hm.get("spflaglist"));
		this.setSp_flag((String)hm.get("sp_flag"));
		this.setFcheck((String)hm.get("fcheck"));
		this.setChg_id((String)hm.get("chg_id"));
		this.setOldFieldList((ArrayList)hm.get("oldFieldList"));
		this.setNewFieldList((ArrayList)hm.get("newFieldList"));
		this.setFieldlist((ArrayList)hm.get("fieldlist"));
		this.setSetid((String)hm.get("setid"));
		this.setKeylist((ArrayList)hm.get("keylist"));
		this.setKeyid((String)hm.get("keyid"));
		this.setCenlist((ArrayList)hm.get("cenlist"));
		this.setTreeCode((String)hm.get("treeCode"));
		this.setTypelist((ArrayList)hm.get("typelist"));
		this.setTypeid((String)hm.get("typeid"));
		this.setSp_idea((String)hm.get("sp_idea"));
		this.setCheckflag((String)hm.get("checkflag"));
		this.setAllflag((String)hm.get("allflag"));
		this.setSequenceList((ArrayList)this.getFormHM().get("sequenceList"));
	    this.setSequenceid((String) this.getFormHM().get("sequenceid"));
	    this.setViewitem((String)hm.get("viewitem"));
	    this.setKind((String)hm.get("kind"));
	    this.setCode((String)hm.get("code"));
	    this.setChecked_may_reject((String)this.getFormHM().get("checked_may_reject"));
	    this.setEmployeeName((String) this.getFormHM().get("employeeName"));
	    this.setChangelist((TreeMap)hm.get("changelist"));
	    this.setB0110desc((String)hm.get("b0110desc"));
	    this.setE0122desc((String)hm.get("e0122desc"));
	    this.setUsername((String)hm.get("username"));
	    this.setNbase((String)hm.get("nbase"));
	    this.setShowlist((ArrayList)hm.get("showlist"));
	    this.setUplevel((String)hm.get("uplevel"));
	    this.setRedundantInfo((String)hm.get("redundantInfo"));
	    this.setTask((String)hm.get("task"));
	    this.setReturnflag((String)hm.get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("abkflag",this.getAbkflag());
		hm.put("pdbflag",this.getPdbflag());
		hm.put("setname",this.getSetname());
		hm.put("unitid",this.getUnitid());
		hm.put("departmentid",this.getDepartmentid());
		if(this.getPagination()!=null){
		hm.put("sel_update_info",(ArrayList)this.getPagination().getSelectedList());
		}
		hm.put("state",this.getState());
		hm.put("sp_flag",this.getSp_flag());
		hm.put("oldFieldList",this.getOldFieldList());
		hm.put("newFieldList",this.getNewFieldList());
		hm.put("cenlist",this.getCenlist());
		hm.put("sp_idea",this.getSp_idea());
		hm.put("allflag",this.getAllflag());
		hm.put("ff",this.getFf());
		hm.put("userid",this.getUserid());
		hm.put("code",this.getCode());
		hm.put("kind",this.getKind());
		hm.put("returnVa", this.getReturnVa());
		hm.put("employeeName", this.getEmployeeName());
		hm.put("showinfo", this.getShowinfo());
		hm.put("showlist", this.getShowlist());
		hm.put("changelist", this.getChangelist());
		hm.put("b0110", this.getB0110desc());
		hm.put("e0122", this.getE0122desc());
		hm.put("name",this.getUsername());
		hm.put("redundantInfo", this.getRedundantInfo());
		hm.put("task", this.getTask());
	}

	public String getAbkflag() {
		return abkflag;
	}

	public void setAbkflag(String abkflag) {
		this.abkflag = abkflag;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public ArrayList getItemnamelist() {
		return itemnamelist;
	}

	public void setItemnamelist(ArrayList itemnamelist) {
		this.itemnamelist = itemnamelist;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getPdbflag() {
		return pdbflag;
	}

	public void setPdbflag(String pdbflag) {
		this.pdbflag = pdbflag;
	}

	public String getSelstr() {
		return selstr;
	}

	public void setSelstr(String selstr) {
		this.selstr = selstr;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public ArrayList getSetnamelist() {
		return setnamelist;
	}

	public void setSetnamelist(ArrayList setnamelist) {
		this.setnamelist = setnamelist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getUnitid() {
		return unitid;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public ArrayList getSetidlist() {
		return setidlist;
	}

	public void setSetidlist(ArrayList setidlist) {
		this.setidlist = setidlist;
	}

	public ArrayList getItemidlist() {
		return itemidlist;
	}

	public void setItemidlist(ArrayList itemidlist) {
		this.itemidlist = itemidlist;
	}

	public ArrayList getCodeidlist() {
		return codeidlist;
	}

	public void setCodeidlist(ArrayList codeidlist) {
		this.codeidlist = codeidlist;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateselstr() {
		return stateselstr;
	}

	public void setStateselstr(String stateselstr) {
		this.stateselstr = stateselstr;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/general/approve/personinfo/setre".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
        if("/general/approve/personinfo/sum".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();      
            this.setKind("");
          
            this.getFormHM().put("kind", "");
            this.getFormHM().put("code", "");
        }
        if("/general/approve/personinfo/sumre".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
        if("/general/approve/personinfo/showstatret".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
        if(!("/general/approve/personinfo/approve".equals(arg0.getPath())&&arg1.getParameter("app")!=null))
        {
            /**清除姓名,*/
            this.getFormHM().remove("employeeName"); 
            this.setEmployeeName("");
        }
        if("/general/approve/personinfo/approve".equals(arg0.getPath())&&arg1.getParameter("param")!=null)
        {
        	if ("0".equals(arg1.getParameter("param"))) {
	        	/**定位到首页,*/
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();
        	}
        }
        
		return super.validate(arg0, arg1);
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPinfo() {
		return pinfo;
	}

	public void setPinfo(String pinfo) {
		this.pinfo = pinfo;
	}

	public String getSetprv() {
		return setprv;
	}

	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}

	public String getBcmessage() {
		return bcmessage;
	}

	public void setBcmessage(String bcmessage) {
		this.bcmessage = bcmessage;
	}

	public String getFf() {
		return ff;
	}

	public void setFf(String ff) {
		this.ff = ff;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getInputchinfor() {
		return inputchinfor;
	}

	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}

	public ArrayList getSpflaglist() {
		return spflaglist;
	}

	public void setSpflaglist(ArrayList spflaglist) {
		this.spflaglist = spflaglist;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getFcheck() {
		return fcheck;
	}

	public void setFcheck(String fcheck) {
		this.fcheck = fcheck;
	}

	public String getChg_id() {
		return chg_id;
	}

	public void setChg_id(String chg_id) {
		this.chg_id = chg_id;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getNewFieldList() {
		return newFieldList;
	}

	public void setNewFieldList(ArrayList newFieldList) {
		this.newFieldList = newFieldList;
	}

	public ArrayList getOldFieldList() {
		return oldFieldList;
	}

	public void setOldFieldList(ArrayList oldFieldList) {
		this.oldFieldList = oldFieldList;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getKeyid() {
		return keyid;
	}

	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

	public ArrayList getKeylist() {
		return keylist;
	}

	public void setKeylist(ArrayList keylist) {
		this.keylist = keylist;
	}

	public ArrayList getCenlist() {
		return cenlist;
	}

	public void setCenlist(ArrayList cenlist) {
		this.cenlist = cenlist;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public String getSp_idea() {
		return sp_idea;
	}

	public void setSp_idea(String sp_idea) {
		this.sp_idea = sp_idea;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getAllflag() {
		return allflag;
	}

	public void setAllflag(String allflag) {
		this.allflag = allflag;
	}

	public String getSequenceid() {
		return sequenceid;
	}

	public void setSequenceid(String sequenceid) {
		this.sequenceid = sequenceid;
	}

	public ArrayList getSequenceList() {
		return sequenceList;
	}

	public void setSequenceList(ArrayList sequenceList) {
		this.sequenceList = sequenceList;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getViewitem() {
		return viewitem;
	}

	public void setViewitem(String viewitem) {
		this.viewitem = viewitem;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChecked_may_reject() {
		return checked_may_reject;
	}

	public void setChecked_may_reject(String checked_may_reject) {
		this.checked_may_reject = checked_may_reject;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

    @Override
    public String getReturnflag() {
        return returnflag;
    }

    @Override
    public void setReturnflag(String returnflag) {
        this.returnflag = returnflag;
    }

}
