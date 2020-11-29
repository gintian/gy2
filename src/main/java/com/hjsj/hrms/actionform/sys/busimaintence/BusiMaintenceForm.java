package com.hjsj.hrms.actionform.sys.busimaintence;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class BusiMaintenceForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	  private String sql;
	  private String where;
	  private String column;
	  private String orderby;
	  
	  private String bsql;
	  private String bwhere;
	  private String bcolumn;
	  private String borderby;
	  private PaginationForm pageListForm = new PaginationForm();
	  private String busiTree;
	  /** 删除子系统信息*/
	  private String obj;
	  /*
	   树状菜单
	   */
	  private String userType;
	  /*
	  =0/=null用户模式，=1开发商模式
	  */
	  private RecordVo busiTableVo;
	  /*
	  子集Maintence
	  */
	  private RecordVo busiFieldVo;
//	  	指标
	  private ArrayList tableList;
//	  	子集List
	  private ArrayList	contractedFieldList=new ArrayList();;
//	  	某个确定的子集的所有构库指标
	  private ArrayList	uncontractedFiledList=new ArrayList();;
//	  	某个确定子集的所有未构库指标
	  private ArrayList codesetList=new ArrayList();;//某个确定的管理所有已购库的子集
	  private ArrayList uncodesetList=new ArrayList();;//某个管理确定的子集
	  private ArrayList fieldVoList;
	  private String codesetsel;
	  private String relating;
	  private String filedid;
	  private String date;
	  private String useflag;
	  private String selcheck;
	  private String[] classper;
	  private String fieldsel;
//	子集名称
	  private String itemsel;
	  private String abkflag;
	  private String itemid;

//	构库修改、删除使用下拉选择框
	  private String sysel;
	  private String zijisel;
//	  构库修改返回标志，
	  private String returnvalue;
	  
	  private String[] right_fields;
	   
	  private String[] left_fields;
//	  修改子集放回标志
	  private String returnvalue1;
	  /**操作方式，新建构库还是修改构库*/
	  private String operation;
	    
	  
	  private String mid;
	  private String mname;
	  private String setdesc;
	  private String changeflag;
	  private String setid;
	  private ArrayList mList = new ArrayList();
	  private ArrayList moduleList = new ArrayList();
	  private ArrayList mfieldlist=new ArrayList();
	  private String id;
	  private ArrayList syselist=new ArrayList();
	  private ArrayList zijilist=new ArrayList();
	  private String editflag;
	  private String fieldsetid;
	  //指标排序
	  private ArrayList sortlist = new ArrayList();
	  private String[] sort_fields;
	  /**业务子集id*/
	  private String fsetid;
	  /** 业务子集*/
	  private ArrayList subsyslist = new ArrayList();
	  private String setname ;
	  private ArrayList busitablelist = new ArrayList(); //指标
	  private String busfields;
	  private String str; //删除子集
	  private String isrefresh;
	  private String type;
	  //构库子集id
	  private String tableid;
	  //构库后的子集名称
	  private String tablename;
	  private String mainid;
	  
	  private String formula;
	  private ArrayList itemlist = new ArrayList();
	  private String fielditemid;
	  private String itemsetid;
	  
	  //统计导入指标总数
	 public String fielditemcount;
	 

	public String getFielditemcount() {
		return fielditemcount;
	}

	public void setFielditemcount(String fielditemcount) {
		this.fielditemcount = fielditemcount;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getRight_fields() {
			return right_fields;
		}

		public void setRight_fields(String[] right_fields) {
			this.right_fields = right_fields;
		}

		public String[] getLeft_fields() {
			return left_fields;
		}

		public void setLeft_fields(String[] sort_right_fields) {
			this.left_fields = sort_right_fields;
		}

	@Override
    public void outPutFormHM() {
		HashMap hm=this.getFormHM();
		this.setModuleList((ArrayList)hm.get("moduleList"));
		this.setSetdesc((String)hm.get("setdesc"));
		this.setSetid((String)hm.get("setid"));
		this.setMid((String)hm.get("mid"));
		this.setMList((ArrayList)hm.get("mList"));
		this.setChangeflag((String)hm.get("changeflag"));
		this.setUserType(hm.get("userType")==null?"0":(String)hm.get("userType"));
		this.setOperation((String)hm.get("operation"));
		this.setBusiTree((String) hm.get("busitree"));
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setOrderby((String)hm.get("orderby"));
		this.setBsql((String) hm.get("bsql"));
		this.setBwhere((String) hm.get("bwhere"));
		this.setBcolumn((String) hm.get("bcolumn"));
		this.setBorderby((String)hm.get("borderby"));
		this.setBusiFieldVo((RecordVo) hm.get("busiFieldVo"));
		this.setCodesetsel((String) hm.get("codesetsel"));
		this.setRelating((String) hm.get("relating"));
		this.setFiledid((String) hm.get("filedid"));
		this.setDate((String)hm.get("date"));
		this.setUseflag((String) hm.get("useflag"));
		this.setBusiTableVo((RecordVo) hm.get("busiTable"));
		this.setSelcheck((String) hm.get("selcheck"));
		this.setFieldsel((String) hm.get("fieldsel"));
		this.setItemsel((String) hm.get("itemsel"));
		this.setZijisel((String) hm.get("zijisel"));
		this.setReturnvalue((String) hm.get("returnvalue"));
		this.setReturnvalue1((String)hm.get("returnvalue1"));
		this.setMname((String)hm.get("mname"));
//		this.setSysel((String)hm.get("sysel"));
//		this.getPagination().setSelect((ArrayList) hm.get("selitem"));
		this.setCodesetList((ArrayList)this.getFormHM().get("codesetList"));
		this.setUncodesetList((ArrayList)this.getFormHM().get("uncodesetList"));
		this.setContractedFieldList((ArrayList)this.getFormHM().get("contractedFieldList"));
		this.setUncontractedFiledList((ArrayList)this.getFormHM().get("uncontractedFiledList"));
		this.setId((String)this.getFormHM().get("id"));
		this.setSyselist((ArrayList)this.getFormHM().get("syselist"));
		this.setZijilist((ArrayList)this.getFormHM().get("zijilist"));
		this.setEditflag((String)this.getFormHM().get("editflag"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));	
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSetname((String) this.getFormHM().get("setname"));
		this.setBusitablelist((ArrayList)this.getFormHM().get("busitablelist"));
		this.setBusfields((String)this.getFormHM().get("busfields"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setFsetid((String) hm.get("fsetid"));
		this.setSubsyslist((ArrayList)this.getFormHM().get("subsyslist"));
		this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
		this.setType((String) hm.get("type"));
		this.setTableid((String) hm.get("tableid"));
		this.setTablename((String) hm.get("tablename"));
		this.setMainid((String) hm.get("mainid"));
		this.setFormula((String)hm.get("formula"));
		this.setItemlist((ArrayList)hm.get("itemlist"));
		this.setFielditemcount((String)hm.get("fielditemcount"));//统计指标总数  wangb 20180511 
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("fieldVo",this.getBusiFieldVo());
		hm.put("busiTable",this.getBusiTableVo());
		hm.put("classper",this.getClassper());
		hm.put("itemid",this.getItemid());
		hm.put("abkflag",this.getAbkflag());
		hm.put("fieldid",this.getFiledid());
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		hm.put("ownflag",this.getUserType());
		hm.put("cfield",this.getRight_fields());
		hm.put("ucfield",this.getLeft_fields());
		hm.put("operation",this.getOperation());
		hm.put("mid",this.getMid());
		hm.put("mList",this.getMList());
		hm.put("setid",this.getSetid());
		hm.put("setdesc", this.getSetdesc());
		hm.put("changeflag",this.getChangeflag());
        hm.put("moduleList", this.getModuleList());
        hm.put("mname",this.getMname());
        hm.put("id", this.getId());
        hm.put("obj", this.getObj());
        hm.put("right_fields", right_fields);
        hm.put("left_fields",left_fields);
        hm.put("zijisel", this.getZijisel());
        hm.put("returnvalue", this.getReturnvalue());
        hm.put("editflag", this.getEditflag());
        hm.put("str", this.getStr());
        hm.put("fsetid", this.getFsetid());
        hm.put("userType", userType);
        hm.put("fieldsetid", fieldsetid);
        hm.put("formula", formula);
        hm.put("fielditemid", fielditemid);
        hm.put("itemsetid", itemsetid);
        hm.put("fielditemcount",fielditemcount);//统计指标总数  wangb 20180511 
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/system/busimaintence/showbusifield".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/system/busimaintence/showbusifield".equals(arg0.getPath())&&arg1.getParameter("b_input")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/system/busimaintence/showbusiname".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**一级页面定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/system/busimaintence/ShowSubsys".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**二级页面定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/system/busimaintence/addbusifield".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null)
        {
			/**添加指标定位最后一页*/
        	if(this.getPagination()!=null)
                this.getPagination().lastPage();//?
        }
		if("/system/busimaintence/showbusifield".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null&& "links".equals(arg1.getParameter("b_query")))
        {
        	if(this.getPagination()!=null)
                this.getPagination().lastPage();//?
        }
		return super.validate(arg0, arg1);
	}

	public RecordVo getBusiFieldVo() {
		return busiFieldVo;
	}

	public void setBusiFieldVo(RecordVo busiFieldVo) {
		this.busiFieldVo = busiFieldVo;
	}

	public RecordVo getBusiTableVo() {
		return busiTableVo;
	}

	public void setBusiTableVo(RecordVo busiTableVo) {
		this.busiTableVo = busiTableVo;
	}

	public String getBusiTree() {
		return busiTree;
	}

	public void setBusiTree(String busiTree) {
		this.busiTree = busiTree;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public ArrayList getContractedFieldList() {
		return contractedFieldList;
	}

	public void setContractedFieldList(ArrayList contractedFieldList) {
		this.contractedFieldList = contractedFieldList;
	}

	public ArrayList getFieldVoList() {
		return fieldVoList;
	}

	public void setFieldVoList(ArrayList fieldVoList) {
		this.fieldVoList = fieldVoList;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getTableList() {
		return tableList;
	}

	public void setTableList(ArrayList tableList) {
		this.tableList = tableList;
	}

	public ArrayList getUncontractedFiledList() {
		return uncontractedFiledList;
	}

	public void setUncontractedFiledList(ArrayList uncontractedFiledList) {
		this.uncontractedFiledList = uncontractedFiledList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getCodesetsel() {
		return codesetsel;
	}

	public void setCodesetsel(String codesetsel) {
		this.codesetsel = codesetsel;
	}

	public String getRelating() {
		return relating;
	}

	public void setRelating(String relating) {
		this.relating = relating;
	}

	public String getFiledid() {
		return filedid;
	}

	public void setFiledid(String filedid) {
		this.filedid = filedid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUseflag() {
		return useflag;
	}

	public void setUseflag(String useflag) {
		this.useflag = useflag;
	}

	public String getSelcheck() {
		return selcheck;
	}

	public void setSelcheck(String selcheck) {
		this.selcheck = selcheck;
	}

	public String[] getClassper() {
		return classper;
	}

	public void setClassper(String[] classper) {
		this.classper = classper;
	}

	public String getFieldsel() {
		return fieldsel;
	}

	public void setFieldsel(String fieldsel) {
		this.fieldsel = fieldsel;
	}

	public String getItemsel() {
		return itemsel;
	}

	public void setItemsel(String itemsel) {
		this.itemsel = itemsel;
	}

	public String getAbkflag() {
		return abkflag;
	}

	public void setAbkflag(String abkflag) {
		this.abkflag = abkflag;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getSysel() {
		return sysel;
	}

	public void setSysel(String sysel) {
		this.sysel = sysel;
	}

	public String getZijisel() {
		return zijisel;
	}

	public void setZijisel(String zijisel) {
		this.zijisel = zijisel;
	}

	public ArrayList getMfieldlist() {
		return mfieldlist;
	}

	public void setMfieldlist(ArrayList mfieldlist) {
		this.mfieldlist = mfieldlist;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getReturnvalue1() {
		return returnvalue1;
	}

	public void setReturnvalue1(String returnvalue1) {
		this.returnvalue1 = returnvalue1;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getChangeflag() {
		return changeflag;
	}

	public void setChangeflag(String changeflag) {
		this.changeflag = changeflag;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public ArrayList getMList() {
		return mList;
	}

	public void setMList(ArrayList list) {
		mList = list;
	}

	public String getSetdesc() {
		return setdesc;
	}

	public void setSetdesc(String setdesc) {
		this.setdesc = setdesc;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList moduleList) {
		this.moduleList = moduleList;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public ArrayList getCodesetList() {
		return codesetList;
	}

	public void setCodesetList(ArrayList codesetList) {
		this.codesetList = codesetList;
	}

	public ArrayList getUncodesetList() {
		return uncodesetList;
	}

	public void setUncodesetList(ArrayList uncodesetList) {
		this.uncodesetList = uncodesetList;
	}

	public ArrayList getSyselist() {
		return syselist;
	}

	public void setSyselist(ArrayList syselist) {
		this.syselist = syselist;
	}

	public ArrayList getZijilist() {
		return zijilist;
	}

	public void setZijilist(ArrayList zijilist) {
		this.zijilist = zijilist;
	}

	public String getEditflag() {
		return editflag;
	}

	public void setEditflag(String editflag) {
		this.editflag = editflag;
	}
	
	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public ArrayList getSortlist() {
		return sortlist;
	}
	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}
	
	public String getSetname()
    {
    
        return setname;
    }
    public void setSetname(String setname)
    {
    
        this.setname = setname;
    }
    public ArrayList getBusitablelist()
    {
    
        return busitablelist;
    }
    public void setBusitablelist(ArrayList busitablelist)
    {
    
        this.busitablelist = busitablelist;
    }
    public String getBusfields() {
		return busfields;
	}
	public void setBusfields(String busfields) {
		this.busfields = busfields;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public String[] getSort_fields() {
		return sort_fields;
	}
	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}
	public String getFsetid() {
		return fsetid;
	}

	public void setFsetid(String fsetid) {
		this.fsetid = fsetid;
	}
	public ArrayList getSubsyslist()
    {
        return subsyslist;
    }
    public void setSubsyslist(ArrayList subsyslist)
    {
        this.subsyslist = subsyslist;
    }
    
    public String getIsrefresh() {
		return isrefresh;
	}
	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getMainid() {
		return mainid;
	}
	public void setMainid(String mainid) {
		this.mainid = mainid;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getFielditemid() {
		return fielditemid;
	}

	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}

	public String getItemsetid() {
		return itemsetid;
	}

	public void setItemsetid(String itemsetid) {
		this.itemsetid = itemsetid;
	}
	
	public String getBsql() {
		return bsql;
	}

	public void setBsql(String bsql) {
		this.bsql = bsql;
	}

	public String getBwhere() {
		return bwhere;
	}

	public void setBwhere(String bwhere) {
		this.bwhere = bwhere;
	}

	public String getBcolumn() {
		return bcolumn;
	}

	public void setBcolumn(String bcolumn) {
		this.bcolumn = bcolumn;
	}

	public String getBorderby() {
		return borderby;
	}

	public void setBorderby(String borderby) {
		this.borderby = borderby;
	}

}
