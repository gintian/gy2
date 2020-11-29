package com.hjsj.hrms.actionform.general.inform.search;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SearchInformForm extends FrameForm {
	private String fieldid;
	private ArrayList setlist=new ArrayList();//子集列表
	private String item_field[]; //指标列表
	private String type; //区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
	private String tablename; //库前缀
	private String a_code; //公司部门代码
	private String flag; //判断常用查询框为修改框还是查询框
	private String titleid; 
	ArrayList titlelist = new ArrayList();
	private String id; //常用查询id
	private String title; //常用查询名称
	private String tablestr; 
	private String check; 
	private String ps_flag;//结构编制参数设置判断
	private String sexpr="";//因子表达式
	private String sfactor="";//计算条件
	private String itemkey="";//人员过滤条件
	private String nbase="";//人员库以","隔开
	private String checkflag="";//1.为培训学员（以后用于扩展）3.为班子成员
	private String sqlstr="";//查询人员sql
	private String wherestr="";//查人员条件
	private String fieldSetId;
	private String fieldSetDesc;
	/**当以单位部门职位作为查询指标时，是否进行权限控制=0控制=1不控制*/
	private String privflag="0";
	private String no_manager_priv;       //true：不按管理范围  false：按管理范围
	private ArrayList factorlist=new ArrayList();
	//是否显示二次查询 默认为0；=1：显示；=2：不显示。
	private String secondflag = "0"; 
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setPrivflag((String)this.getFormHM().get("privflag"));
		this.setNo_manager_priv((String)this.getFormHM().get("no_manager_priv"));
		this.setFieldSetDesc((String)this.getFormHM().get("fieldSetDesc"));
		this.setFieldSetId((String)this.getFormHM().get("fieldSetId"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setItem_field((String[])this.getFormHM().get("item_field"));
		this.setType((String)this.getFormHM().get("type"));
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setId((String)this.getFormHM().get("id"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setTitleid((String)this.getFormHM().get("titleid"));
		this.setTitlelist((ArrayList)this.getFormHM().get("titlelist"));
		this.setTablestr((String)this.getFormHM().get("tablestr"));
		this.setCheck((String)this.getFormHM().get("check"));
		this.setPs_flag((String)this.getFormHM().get("ps_flag"));
		this.setSexpr((String)this.getFormHM().get("sexpr"));
		this.setSfactor((String)this.getFormHM().get("sfactor"));
		this.setItemkey((String)this.getFormHM().get("itemkey"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setSecondflag((String)this.getFormHM().get("secondflag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("privflag", this.getPrivflag());
		this.getFormHM().put("no_manager_priv", this.getNo_manager_priv());
		this.getFormHM().put("fieldSetDesc",this.getFieldSetDesc());
		this.getFormHM().put("fieldSetId",this.getFieldSetId());
		this.getFormHM().put("sexpr",this.getSexpr());
		this.getFormHM().put("sfactor",this.getSfactor());
		this.getFormHM().put("itemkey",this.getItemkey());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("checkflag",this.getCheckflag());
		this.getFormHM().put("factorlist", this.getFactorlist());
		this.getFormHM().put("secondflag", this.getSecondflag());
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String[] getItem_field() {
		return item_field;
	}

	public void setItem_field(String[] item_field) {
		this.item_field = item_field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleid() {
		return titleid;
	}

	public void setTitleid(String titleid) {
		this.titleid = titleid;
	}

	public ArrayList getTitlelist() {
		return titlelist;
	}

	public void setTitlelist(ArrayList titlelist) {
		this.titlelist = titlelist;
	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getPs_flag() {
		return ps_flag;
	}

	public void setPs_flag(String ps_flag) {
		this.ps_flag = ps_flag;
	}

	public String getSexpr() {
		return sexpr;
	}

	public void setSexpr(String sexpr) {
		this.sexpr = sexpr;
	}

	public String getSfactor() {
		return sfactor;
	}

	public void setSfactor(String sfactor) {
		this.sfactor = sfactor;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getFieldSetId() {
		return fieldSetId;
	}

	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}

	public String getFieldSetDesc() {
		return fieldSetDesc;
	}

	public void setFieldSetDesc(String fieldSetDesc) {
		this.fieldSetDesc = fieldSetDesc;
	}

	public String getPrivflag() {
		return privflag;
	}

	public void setPrivflag(String privflag) {
		this.privflag = privflag;
	}

	public String getNo_manager_priv() {
		return no_manager_priv;
	}

	public void setNo_manager_priv(String no_manager_priv) {
		this.no_manager_priv = no_manager_priv;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		
		try{
			
			if(request.getParameter("winState") != null && request.getParameter("winState").trim().length()>0){
				request.setAttribute("targetWindow", request.getParameter("winState"));
			}
			
			 if ("/general/inform/search/gmsearcher".equals(mapping.getPath()) && request.getParameter("b_search") != null)
			    {
				if (this.getPagination() != null)
				    this.getPagination().firstPage();
			    }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.validate(mapping, request);
	}

    public String getSecondflag() {
        return secondflag;
    }

    public void setSecondflag(String secondflag) {
        this.secondflag = secondflag;
    }

}
