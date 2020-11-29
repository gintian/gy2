package com.hjsj.hrms.actionform.gz.premium;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:BonusParamForm.java</p>
 * <p>Description:奖金参数设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-07-02 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class PremiumParamForm extends FrameForm
{
    //人员库
    private ArrayList stat_dbpre = new ArrayList();    
    //奖金子集
    private ArrayList setidList = new ArrayList();
    private String setid = "";
    // 下发标识指标
    private  String dist_field = "";
 //上报标识指标   
    private  String rep_field = "";
//封存标示指标
    private  String keep_save_field = "";
  //奖金总额指标
    private  String bonus_sum_field = "";
  //下发奖金总额指标
    private  String dist_sum_field ="";
  //节余指标
    private String surplus_field ="";
  //单位登记表号
    private String cardid ="";
    //奖金核算单位标志指标
    private String checkUn_field="";
    
  //人员月奖表共享工资类别 
    private String salaryid="";
    private ArrayList dist_fieldList = new ArrayList();
    private ArrayList rep_fieldList = new ArrayList();
    private ArrayList keep_save_fieldList = new ArrayList();
    private ArrayList bonus_sum_fieldList = new ArrayList();
    private ArrayList dist_sum_fieldList = new ArrayList();
    private ArrayList surplus_fieldList = new ArrayList();
    private ArrayList cardidList = new ArrayList();
    private ArrayList salaryidList = new ArrayList();
    
    // 表对象
    private RecordVo codeitemVo = new RecordVo("codeitem");
    
    private ArrayList codeDataList = new ArrayList();
    
    private String codeLen="0";
    private String smode ="";
    
    
    //导入公式
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 
	private String formula;//计算公式
	
	private String[] codesetid_arr;
	
	private String itemid;
	private ArrayList itemlist = new ArrayList();
	

	
	private String runflag;
	private String item;
	
	/**
	 * 提供增加公式的项目选项
	 * */
	private String formulaitemid;
	private ArrayList formulaitemlist = new ArrayList();
	
	 /***
	  * 临时中的项目调整顺序
	  * */
	private String[] sort_fields; 
	private ArrayList sortlist = new ArrayList();
	
	private String conditions; //计算条件
	private String itemname; 
	
	/**
	 * 选择薪资标准表
	 * */
	private String standardid;
	private ArrayList standardlist = new ArrayList();
	
	/**
	 * 选择税率表
	 * */
	private String taxid;
	private ArrayList taxlist = new ArrayList();
    //导入公式补充
	private String fmode="";//公式类型
    
    //复合查询
	   /**应用库表前缀*/
    private String[] dbpre;
    /**查询结果显示字段列表*/
    private ArrayList showlist=new ArrayList();
    
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
    /**查询结果*/
    private String result="0"; 
    /**历史记录*/
    private String history="0";
    /**人员单位及职位标识
     * =1人员,=2单位,=3职位
     * */
    private String type="1";
    /**权限范围内的人员库*/
    private ArrayList dblist=new ArrayList();
    /**数据集名称*/
    private String setname;
    /**显示应用库前缀,主要用于跨库查询*/
    private String show_dbpre;
	
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
    private String expression;   
    /**查询类型
     * =1简单查询
     * =2通用查询
     */
    private String query_type="1";
    /**条件定义控制符*/
    private String define="0";
    /**factor list*/
    private ArrayList factorlist=new ArrayList();    
    /**关系操作符*/
    private ArrayList operlist=new ArrayList();
    /**逻辑操作符*/
    private ArrayList logiclist=new ArrayList(); 
    /**选中的指标列表*/
    private ArrayList selectedlist=new ArrayList();
    
    /**常用条件列表*/
    private ArrayList selectedCondlist=new ArrayList();
    /**常用条件列表*/
    private String condname="";    
    private ArrayList condlist=new ArrayList(); 
    private String keyid="";
    /**表达式：1+2|A0405=`A0107=1`
     * 重新解释查询表达式
     * */
    private String expr;
    
    
    /**过滤检索表达式*/
    private String filter_factor;
    /**条件名称*/
    private String name;
    private String checkselect="0";
    private String columns="";
    private String chpriv="";
    //复合查询补充
    private ArrayList list = new ArrayList();
    private String  flag ="";
    //统计
    private String hzname="";
    private String stat_methods; //统计方式
    private ArrayList statlist = new ArrayList(); //统计方式列表
    private String expresion ="";
    //按钮置灰
    private String tag="0";   
    public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getRunflag() {
		return runflag;
	}

	public void setRunflag(String runflag) {
		this.runflag = runflag;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getFormulaitemid() {
		return formulaitemid;
	}

	public void setFormulaitemid(String formulaitemid) {
		this.formulaitemid = formulaitemid;
	}

	public ArrayList getFormulaitemlist() {
		return formulaitemlist;
	}

	public void setFormulaitemlist(ArrayList formulaitemlist) {
		this.formulaitemlist = formulaitemlist;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getStandardid() {
		return standardid;
	}

	public void setStandardid(String standardid) {
		this.standardid = standardid;
	}

	public ArrayList getStandardlist() {
		return standardlist;
	}

	public void setStandardlist(ArrayList standardlist) {
		this.standardlist = standardlist;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public ArrayList getTaxlist() {
		return taxlist;
	}

	public void setTaxlist(ArrayList taxlist) {
		this.taxlist = taxlist;
	}

	@Override
    public void inPutTransHM()
    {
	this.getFormHM().put("stat_dbpre", this.getStat_dbpre());
	this.getFormHM().put("setidList", this.setidList);
	this.getFormHM().put("setid", this.getSetid());
	this.getFormHM().put("dist_fieldList", this.dist_fieldList);
	this.getFormHM().put("rep_fieldList", this.rep_fieldList);
	this.getFormHM().put("keep_save_fieldList", this.keep_save_fieldList);
	this.getFormHM().put("bonus_sum_fieldList", this.bonus_sum_fieldList);
	this.getFormHM().put("dist_sum_fieldList", this.dist_sum_fieldList);
	this.getFormHM().put("surplus_fieldList", this.surplus_fieldList);
	this.getFormHM().put("cardidList", this.cardidList);
	this.getFormHM().put("salaryidList", this.salaryidList);
	this.getFormHM().put("dist_field", this.dist_field);
	this.getFormHM().put("rep_field", this.rep_field);
	this.getFormHM().put("keep_save_field", this.keep_save_field);
	this.getFormHM().put("bonus_sum_field", this.bonus_sum_field);
	this.getFormHM().put("dist_sum_field", this.dist_sum_field);
	this.getFormHM().put("surplus_field", this.surplus_field);
	this.getFormHM().put("cardid", this.cardid);

	this.getFormHM().put("codeDataList", this.getCodeDataList());
	this.getFormHM().put("codeitemVo", this.getCodeitemVo());
	this.getFormHM().put("codeLen", this.getCodeLen());
	
	//公式
	if(this.getPagination()!=null)
		this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
	this.getFormHM().put("formula",this.getFormula());
	
	//复合查询
	this.getFormHM().put("type",this.getType());
	this.getFormHM().put("result",this.getResult());
	this.getFormHM().put("history",this.getHistory());
	this.getFormHM().put("like",this.getLike());
	this.getFormHM().put("dbpre",this.getDbpre());
	this.getFormHM().put("show_dbpre",this.getShow_dbpre());
	this.getFormHM().put("expression",this.getExpression());
	this.getFormHM().put("right_fields",this.getRight_fields());
    this.getFormHM().put("factorlist",this.getFactorlist());		
    this.getFormHM().put("query_type",this.getQuery_type());
    this.getFormHM().put("condname",this.getCondname()); 
    this.getFormHM().put("condid",this.getKeyid());       
    this.getFormHM().put("expr",this.getExpr());
    this.getFormHM().put("filter_factor",this.filter_factor);
    this.getFormHM().put("name",this.getName());
    this.getFormHM().put("checkselect",this.getCheckselect());
    
    //统计
    this.getFormHM().put("stat_methods",this.getStat_methods());
    this.getFormHM().put("expresion",this.getExpresion());
    this.getFormHM().put("hzname",this.getHzname());
    this.getFormHM().put("checkUn_field",this.getCheckUn_field());
    }

    @Override
    public void outPutFormHM()
    {
    this.setCheckUn_field((String)this.getFormHM().get("checkUn_field"));
	this.setStat_dbpre((ArrayList) this.getFormHM().get("stat_dbpre"));
	this.setSetidList((ArrayList) this.getFormHM().get("setidList"));
	this.setSetid((String) this.getFormHM().get("setid"));
	this.setDist_fieldList((ArrayList) this.getFormHM().get("dist_fieldList"));
	this.setRep_fieldList((ArrayList) this.getFormHM().get("rep_fieldList"));
	this.setKeep_save_fieldList((ArrayList) this.getFormHM().get("keep_save_fieldList"));
	this.setBonus_sum_fieldList((ArrayList) this.getFormHM().get("bonus_sum_fieldList"));
	this.setDist_sum_fieldList((ArrayList) this.getFormHM().get("dist_sum_fieldList"));
	this.setSurplus_fieldList((ArrayList) this.getFormHM().get("surplus_fieldList"));
	this.setCardidList((ArrayList) this.getFormHM().get("cardidList"));
	this.setSalaryidList((ArrayList) this.getFormHM().get("salaryidList"));
	
	this.setDist_field((String) this.getFormHM().get("dist_field"));
	this.setRep_field((String) this.getFormHM().get("rep_field"));
	this.setKeep_save_field((String) this.getFormHM().get("keep_save_field"));
	this.setBonus_sum_field((String) this.getFormHM().get("bonus_sum_field"));
	this.setDist_sum_field((String) this.getFormHM().get("dist_sum_field"));
	this.setSurplus_field((String) this.getFormHM().get("surplus_field"));
	this.setCardid((String) this.getFormHM().get("cardid"));

	this.setCodeDataList((ArrayList) this.getFormHM().get("codeDataList"));
	this.setCodeitemVo((RecordVo)this.getFormHM().get("codeitemVo"));
	this.setCodeLen((String) this.getFormHM().get("codeLen"));
	this.setSmode((String) this.getFormHM().get("smode"));
    
    
    //公式
	this.setSql((String) this.getFormHM().get("sql"));
	this.setWhere((String) this.getFormHM().get("where"));
	this.setColumn((String) this.getFormHM().get("column"));
	this.setOrderby((String)this.getFormHM().get("orderby"));
	this.setFormula((String)this.getFormHM().get("formula"));
	this.setItemid((String)this.getFormHM().get("itemid"));
	this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
	this.setSalaryid((String)this.getFormHM().get("salaryid"));
	this.setRunflag((String)this.getFormHM().get("runflag"));
	this.setItem((String)this.getFormHM().get("item"));
	this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
	this.setFormulaitemid((String)this.getFormHM().get("formulaitemid"));
	this.setFormulaitemlist((ArrayList)this.getFormHM().get("formulaitemlist"));
	this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
	this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
	this.setConditions((String)this.getFormHM().get("conditions"));
	this.setItemname((String)this.getFormHM().get("itemname"));
	
	this.setStandardid((String)this.getFormHM().get("standardid"));
	this.setStandardlist((ArrayList)this.getFormHM().get("standardlist"));
	
	this.setTaxid((String)this.getFormHM().get("taxid"));
	this.setTaxlist((ArrayList)this.getFormHM().get("taxlist"));
	//导入公式补充
	this.setFmode((String)this.getFormHM().get("fmode"));
	
    //复合查询
	this.setDblist((ArrayList)this.getFormHM().get("dblist"));
	this.setShowlist((ArrayList)this.getFormHM().get("showlist"));
	this.setSetname((String)this.getFormHM().get("setname"));
	this.setSql((String)this.getFormHM().get("sql"));
	this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
	this.setExpression((String)this.getFormHM().get("expression"));
	this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
	this.setSelectedCondlist((ArrayList)this.getFormHM().get("selectedCondlist"));
    this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
    this.setExpr((String)this.getFormHM().get("expr"));
    this.setName((String)this.getFormHM().get("name"));
    this.setColumns((String)this.getFormHM().get("columns"));
    this.setOrderby((String)this.getFormHM().get("orderby"));
    this.setChpriv((String)this.getFormHM().get("chpriv"));
  //复合查询补充
    this.setList((ArrayList)this.getFormHM().get("list"));
    this.setFlag((String)this.getFormHM().get("flag"));
    //统计
    
    this.setHzname((String)this.getFormHM().get("hzname"));
    this.setStat_methods((String)this.getFormHM().get("stat_methods"));
    this.setStatlist((ArrayList)this.getFormHM().get("statlist"));
    this.setExpresion((String)this.getFormHM().get("expresion"));
    this.setTag((String)this.getFormHM().get("tag"));
    
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);  
    	this.setHistory("0");
		this.setLike("0");
		this.setResult("0");
		String[] temp=new String[1];
	//	this.setDbpre(temp);
		String temp_type=arg1.getParameter("type");
		if(temp_type!=null)
		{
			if(!temp_type.equalsIgnoreCase(this.getType())&&this.getSelectedlist()!=null)
			{
				this.setExpression("");
				this.getSelectedlist().clear();
			}
		}	
		this.setKeyid("");	
		this.setExpr("");
		this.setCondname("");
		this.setDefine("0");
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/premium/param/formula".equals(arg0.getPath())&&arg1.getParameter("b_import")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}


  

  

    public ArrayList getStat_dbpre()
    {
    
        return stat_dbpre;
    }

    public void setStat_dbpre(ArrayList stat_dbpre)
    {
    
        this.stat_dbpre = stat_dbpre;
    }

   
    public ArrayList getCodeDataList()
    {
    
        return codeDataList;
    }

    public void setCodeDataList(ArrayList codeDataList)
    {
    
        this.codeDataList = codeDataList;
    }

    public RecordVo getCodeitemVo()
    {
    
        return codeitemVo;
    }

    public void setCodeitemVo(RecordVo codeitemVo)
    {
    
        this.codeitemVo = codeitemVo;
    }
    public String getCodeLen()
    {
    
        return codeLen;
    }
    public void setCodeLen(String codeLen)
    {
    
        this.codeLen = codeLen;
    }

	
	

	public String getDist_field() {
		return dist_field;
	}

	public void setDist_field(String dist_field) {
		this.dist_field = dist_field;
	}

	public String getRep_field() {
		return rep_field;
	}

	public void setRep_field(String rep_field) {
		this.rep_field = rep_field;
	}

	public String getKeep_save_field() {
		return keep_save_field;
	}

	public void setKeep_save_field(String keep_save_field) {
		this.keep_save_field = keep_save_field;
	}

	public String getBonus_sum_field() {
		return bonus_sum_field;
	}

	public void setBonus_sum_field(String bonus_sum_field) {
		this.bonus_sum_field = bonus_sum_field;
	}

	public String getDist_sum_field() {
		return dist_sum_field;
	}

	public void setDist_sum_field(String dist_sum_field) {
		this.dist_sum_field = dist_sum_field;
	}

	public String getSurplus_field() {
		return surplus_field;
	}

	public void setSurplus_field(String surplus_field) {
		this.surplus_field = surplus_field;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public ArrayList getDist_fieldList() {
		return dist_fieldList;
	}

	public void setDist_fieldList(ArrayList dist_fieldList) {
		this.dist_fieldList = dist_fieldList;
	}

	public ArrayList getRep_fieldList() {
		return rep_fieldList;
	}

	public void setRep_fieldList(ArrayList rep_fieldList) {
		this.rep_fieldList = rep_fieldList;
	}

	public ArrayList getKeep_save_fieldList() {
		return keep_save_fieldList;
	}

	public void setKeep_save_fieldList(ArrayList keep_save_fieldList) {
		this.keep_save_fieldList = keep_save_fieldList;
	}

	public ArrayList getBonus_sum_fieldList() {
		return bonus_sum_fieldList;
	}

	public void setBonus_sum_fieldList(ArrayList bonus_sum_fieldList) {
		this.bonus_sum_fieldList = bonus_sum_fieldList;
	}

	public ArrayList getDist_sum_fieldList() {
		return dist_sum_fieldList;
	}

	public void setDist_sum_fieldList(ArrayList dist_sum_fieldList) {
		this.dist_sum_fieldList = dist_sum_fieldList;
	}

	public ArrayList getSurplus_fieldList() {
		return surplus_fieldList;
	}

	public void setSurplus_fieldList(ArrayList surplus_fieldList) {
		this.surplus_fieldList = surplus_fieldList;
	}

	public ArrayList getCardidList() {
		return cardidList;
	}

	public void setCardidList(ArrayList cardidList) {
		this.cardidList = cardidList;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getSetidList() {
		return setidList;
	}

	public void setSetidList(ArrayList setidList) {
		this.setidList = setidList;
	}

	public String[] getDbpre() {
		return dbpre;
	}

	public void setDbpre(String[] dbpre) {
		this.dbpre = dbpre;
	}

	public ArrayList getShowlist() {
		return showlist;
	}

	public void setShowlist(ArrayList showlist) {
		this.showlist = showlist;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getShow_dbpre() {
		return show_dbpre;
	}

	public void setShow_dbpre(String show_dbpre) {
		this.show_dbpre = show_dbpre;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getQuery_type() {
		return query_type;
	}

	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}

	public String getDefine() {
		return define;
	}

	public void setDefine(String define) {
		this.define = define;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}

	public ArrayList getLogiclist() {
		return logiclist;
	}

	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}

	public ArrayList getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(ArrayList selectedlist) {
		this.selectedlist = selectedlist;
	}

	public ArrayList getSelectedCondlist() {
		return selectedCondlist;
	}

	public void setSelectedCondlist(ArrayList selectedCondlist) {
		this.selectedCondlist = selectedCondlist;
	}

	public String getCondname() {
		return condname;
	}

	public void setCondname(String condname) {
		this.condname = condname;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public String getKeyid() {
		return keyid;
	}

	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getFilter_factor() {
		return filter_factor;
	}

	public void setFilter_factor(String filter_factor) {
		this.filter_factor = filter_factor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCheckselect() {
		return checkselect;
	}

	public void setCheckselect(String checkselect) {
		this.checkselect = checkselect;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getChpriv() {
		return chpriv;
	}

	public void setChpriv(String chpriv) {
		this.chpriv = chpriv;
	}

	public String getFmode() {
		return fmode;
	}

	public void setFmode(String fmode) {
		this.fmode = fmode;
	}

	public String getStat_methods() {
		return stat_methods;
	}

	public void setStat_methods(String stat_methods) {
		this.stat_methods = stat_methods;
	}

	public ArrayList getStatlist() {
		return statlist;
	}

	public void setStatlist(ArrayList statlist) {
		this.statlist = statlist;
	}

	public String getExpresion() {
		return expresion;
	}

	public void setExpresion(String expresion) {
		this.expresion = expresion;
	}

	public String getSmode() {
		return smode;
	}

	public void setSmode(String smode) {
		this.smode = smode;
	}

	public String getHzname() {
		return hzname;
	}

	public void setHzname(String hzname) {
		this.hzname = hzname;
	}

	public ArrayList getSalaryidList() {
		return salaryidList;
	}

	public void setSalaryidList(ArrayList salaryidList) {
		this.salaryidList = salaryidList;
	}

	public String getCheckUn_field() {
		return checkUn_field;
	}

	public void setCheckUn_field(String checkUn_field) {
		this.checkUn_field = checkUn_field;
	}	
}
