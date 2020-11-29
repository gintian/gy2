package com.hjsj.hrms.actionform.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MusterForm extends FrameForm {
	/**信息群标识*/
	private String infor_Flag="1";
    /**记录集名称*/
    private String setname="A01";
    /**构库标识*/
    private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
    /**信息种类，对人员信息查询则选全部子集*/
    private String domainflag=Integer.toBinaryString(Constant.ALL_FIELD_SET);
    
    /**应用库表前缀*/
    private String dbpre;
    /**对历史记录进行查询*/
    private String history="0";
    /**是否包含主集信息*/
    private String repeat_mainset="0";
    /**花名册分类*/
    private String mustertype;
    /**使用标志,公用,还是私用*/
    private String used_flag;
	/**人员库列表*/
    private ArrayList dblist=new ArrayList();
    /**花名册分类列表*/
    private ArrayList typelist=new ArrayList();
    /**花名册指标*/
    private String[] right_fields;
    /**花名册排序指标*/
    private String[] sort_right_fields;
    /**花名册名称,对应的物理表的名称*/
    private String mustername;
    /**花名册名称描述*/
    private String mustertitle;
    /**查询语句*/
    private String sql;
    /**花名册指标列表*/
    private ArrayList fieldlist;
    private int fieldSize=0;
    /**花名册列表*/
    private ArrayList musterlist=new ArrayList();
    /**花名册表号数组*/
    private String[] tabid;
    /**当前选中的花名册号*/
    private String currid;
    /**花名册指标列表,不含固定字段,类型为CommonData*/
    private ArrayList mfieldlist=new ArrayList();
    /**常用查询条件列表  */
    private ArrayList condlist=new ArrayList();
    /**是否显示查询结果*/
    private String result;
    private String checktype;
    private String returncheck;
    private String treeCode;
    private String a_code;
    private String save_flag;
    private String moduleflag;
    private String coumsize;
    /**判断是否返回到哪个页面 0.不显示返回按钮 1.首页*/
    private String checkflag; 
    private String refleshtree; 
    private String chkflag; 
    private String strsql="";
    private String columns="";
    private String condid=""; //常用查询id
    private String sortitem="";
    private String countStr="";//总人数
    private String hflag="";//是否在合同中显示
    private ArrayList commonQueryList = new ArrayList();
    private String commonQueryId;
    private String returnvalue;
    /**其他模块调用花名册的情况非常多，用此参数判断是否要显示返回到导航图的按钮或者显示关闭按钮
     * */
    private String closeWindow;
	public String getMustername() {
		return mustername;
	}
	
	public void setMustername(String mustername) {
		this.mustername = mustername;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}
	  private PaginationForm tablistForm=new PaginationForm();
	@Override
    public void outPutFormHM() {
		this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
		 this.getTablistForm().setList((ArrayList)this.getFormHM().get("tablist"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	    this.setCloseWindow((String)this.getFormHM().get("closeWindow"));
		this.setCommonQueryList((ArrayList)this.getFormHM().get("commonQueryList"));
		this.setCommonQueryId((String)this.getFormHM().get("commonQueryId"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
		this.setInfor_Flag((String)this.getFormHM().get("inforkind"));
		this.setMustername((String)this.getFormHM().get("mustername"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		if((ArrayList)this.getFormHM().get("fieldlist")!=null&&((ArrayList)this.getFormHM().get("fieldlist")).size()>0)
		this.setFieldSize(((ArrayList)this.getFormHM().get("fieldlist")).size());
		this.setMusterlist((ArrayList)this.getFormHM().get("musterlist"));
		this.setMfieldlist((ArrayList)this.getFormHM().get("mfields"));
		this.setCurrid((String)this.getFormHM().get("currid"));
		this.setHistory((String)this.getFormHM().get("history"));
		this.setSort_right_fields((String[])this.getFormHM().get("sortfields"));
		this.setResult((String)this.getFormHM().get("result"));
		this.setChecktype((String)this.getFormHM().get("checktype"));
		this.setReturncheck((String)this.getFormHM().get("returncheck"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setTabid((String[])this.getFormHM().get("tabid"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSave_flag((String)this.getFormHM().get("save_flag"));
		this.setModuleflag((String)this.getFormHM().get("moduleflag"));
		this.setMustertitle((String)this.getFormHM().get("mustertitle"));
		this.setCoumsize((String)this.getFormHM().get("coumsize"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setRefleshtree((String)this.getFormHM().get("refleshtree"));
		this.setChkflag((String)this.getFormHM().get("chkflag"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setCountStr((String)this.getFormHM().get("countStr"));
		this.setHflag((String)this.getFormHM().get("hflag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("returnvalue", this.getReturnvalue());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("closeWindow", this.getCloseWindow());
		this.getFormHM().put("commonQueryId",this.getCommonQueryId());
		this.getFormHM().put("commonQueryList", this.getCommonQueryList());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("history",this.getHistory());	
		this.getFormHM().put("repeat_mainset",this.getRepeat_mainset());	
		this.getFormHM().put("mustertype",this.getMustertype());
		this.getFormHM().put("musterfields",this.getRight_fields());
		this.getFormHM().put("sortfields",this.getSort_right_fields());
		this.getFormHM().put("mustername",this.getMustername());
		this.getFormHM().put("used_flag",this.getUsed_flag());
		this.getFormHM().put("inforkind",this.getInfor_Flag());
		this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("fieldlist",this.getFieldlist());
		this.getFormHM().put("returncheck",this.getReturncheck());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("checkflag",this.getCheckflag());
		this.getFormHM().put("strsql",this.getStrsql());
		this.getFormHM().put("columns",this.getColumns());
		this.getFormHM().put("condid",this.getCondid());
		this.getFormHM().put("sortitem",this.getSortitem());
		this.getFormHM().put("hflag",this.getHflag());
		this.getFormHM().put("selectedList",this.getTablistForm().getSelectedList());
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/muster/emp_muster".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
			//liuy 2014-12-26 634：主页花名册，在第二页选择个花名册打开后返回，返回到第一页了  start
            /*backCurrentPage参数不为空时，返回到最近页*/
			if(arg1.getParameter("backCurrentPage")!=null&&!"".equals(arg1.getParameter("backCurrentPage"))){	
				if("1".equals(arg1.getParameter("backCurrentPage"))){					
					arg1.setAttribute("backCurrentPage", "");
				}else{
					if(this.getPagination()!=null)
						this.getPagination().firstPage();     
					if(this.getTablistForm()!=null)
						this.getTablistForm().getPagination().firstPage();
				}
			}else{//返回到首页				
				if(this.getPagination()!=null)
					this.getPagination().firstPage();     
				if(this.getTablistForm()!=null)
					this.getTablistForm().getPagination().firstPage();
			}
			//liuy end
        }else if("/general/muster/fillout_musterdata".equals(arg0.getPath()) && (arg1.getParameter("b_search")!=null)){
        	//add by wangchaoqun on 2014-10-17 将模式窗口中按钮设置为‘关闭’
        	arg1.setAttribute("targetWindow", "0"); //取消设置关闭  ie关闭按钮将浏览器关闭
        }
		
		return super.validate(arg0, arg1);
	}
	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getDomainflag() {
		return domainflag;
	}

	public void setDomainflag(String domainflag) {
		this.domainflag = domainflag;
	}

	public String getUsedflag() {
		return usedflag;
	}

	public void setUsedflag(String usedflag) {
		this.usedflag = usedflag;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}


	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getMustertype() {
		return mustertype;
	}

	public void setMustertype(String mustertype) {
		this.mustertype = mustertype;
	}

	public String getUsed_flag() {
		return used_flag;
	}

	public void setUsed_flag(String used_flag) {
		this.used_flag = used_flag;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String[] getSort_right_fields() {
		return sort_right_fields;
	}

	public void setSort_right_fields(String[] sort_right_fields) {
		this.sort_right_fields = sort_right_fields;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getMusterlist() {
		return musterlist;
	}

	public void setMusterlist(ArrayList musterlist) {
		this.musterlist = musterlist;
	}

	public String[] getTabid() {
		return tabid;
	}

	public void setTabid(String[] tabid) {
		this.tabid = tabid;
	}

	public ArrayList getMfieldlist() {
		return mfieldlist;
	}

	public void setMfieldlist(ArrayList mfieldlist) {
		this.mfieldlist = mfieldlist;
	}

	public String getCurrid() {
		return currid;
	}

	public void setCurrid(String currid) {
		this.currid = currid;
	}

	public int getFieldSize() {
		return fieldSize;
	}

	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getChecktype() {
		return checktype;
	}

	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	public String getReturncheck() {
		return returncheck;
	}

	public void setReturncheck(String returncheck) {
		this.returncheck = returncheck;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getSave_flag() {
		return save_flag;
	}

	public void setSave_flag(String save_flag) {
		this.save_flag = save_flag;
	}

	public String getModuleflag() {
		return moduleflag;
	}

	public void setModuleflag(String moduleflag) {
		this.moduleflag = moduleflag;
	}

	public String getMustertitle() {
		return mustertitle;
	}

	public void setMustertitle(String mustertitle) {
		this.mustertitle = mustertitle;
	}

	public String getCoumsize() {
		return coumsize;
	}

	public void setCoumsize(String coumsize) {
		this.coumsize = coumsize;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getRepeat_mainset() {
		return repeat_mainset;
	}

	public void setRepeat_mainset(String repeat_mainset) {
		this.repeat_mainset = repeat_mainset;
	}

	public String getRefleshtree() {
		return refleshtree;
	}

	public void setRefleshtree(String refleshtree) {
		this.refleshtree = refleshtree;
	}

	public String getChkflag() {
		return chkflag;
	}

	public void setChkflag(String chkflag) {
		this.chkflag = chkflag;
	}

	public String getStrsql()
	{
	
	    return strsql;
	}

	public void setStrsql(String strsql)
	{
	
	    this.strsql = strsql;
	}

	public String getColumns()
	{
	
	    return columns;
	}

	public void setColumns(String columns)
	{
	
	    this.columns = columns;
	}

	public String getCondid() {
		return condid;
	}

	public void setCondid(String condid) {
		this.condid = condid;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getCountStr() {
		return countStr;
	}

	public void setCountStr(String countStr) {
		this.countStr = countStr;
	}

	public String getHflag() {
		return hflag;
	}

	public void setHflag(String hflag) {
		this.hflag = hflag;
	}

	public ArrayList getCommonQueryList() {
		return commonQueryList;
	}

	public void setCommonQueryList(ArrayList commonQueryList) {
		this.commonQueryList = commonQueryList;
	}

	public String getCommonQueryId() {
		return commonQueryId;
	}

	public void setCommonQueryId(String commonQueryId) {
		this.commonQueryId = commonQueryId;
	}

	public String getCloseWindow() {
		return closeWindow;
	}

	public void setCloseWindow(String closeWindow) {
		this.closeWindow = closeWindow;
	}

	public PaginationForm getTablistForm() {
		return tablistForm;
	}

	public void setTablistForm(PaginationForm tablistForm) {
		this.tablistForm = tablistForm;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	
    /**
     * dataset标签属性值
     * 汉口银行专版选择全部人员库时，不能修改数据
     * @Title: getDataset_select   
     * @Description:    
     * @return
     */
    public boolean getDataset_select() {
        return MusterBo.isHkyh()&&"ALL".equals(dbpre)?false:true;
    }
    
    public boolean getDataset_editable() {
        return MusterBo.isHkyh()&&"ALL".equals(dbpre)?false:true;
    }
    
    public boolean getDataset_readonly() {
        return MusterBo.isHkyh()&&"ALL".equals(dbpre)?true:false;
    }
    
    /**
     * 是否显示保存、删除按钮
     * @return
     */
    public boolean getCanModifyData() {
        return MusterBo.isHkyh()&&"ALL".equals(dbpre)?false:true;
    }
    
    /**
     * 是否显示菜单
     * @return
     */
    public boolean getShowMenu() {
        return MusterBo.isHkyh()&&"ALL".equals(dbpre)?false:true;
    }    
}
