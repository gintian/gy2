package com.hjsj.hrms.actionform.org.autostatic.mainp;

import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ProjectForm extends FrameForm {
	private ArrayList fieldlist=new ArrayList();//子集列表
	private String fieldname; //子集id
	private String type;//项目类型
	private String formula; //公式
	private String fielditemid; //项目id
	private String fielditem;//项目名称
	private ArrayList usedlist = new ArrayList(); //项目列表
	private String stat_methods; //统计方式
	private ArrayList statlist = new ArrayList(); //统计方式列表
	private ArrayList rangelist = new ArrayList(); //范围
	private String rangeid="";//范围
	private String statid; //统计方式

	private String[] strexpression_arr;//字符串表达式名称
	private String[] numexpression1_arr;//数值表达式名称
	private String[] numexpression2_arr;//数值表达式名称
	private String[] dateexpression1_arr;//日期表达式名称
	private String[] dateexpression2_arr;//日期表达式名称
	private String[] incrementalItem_arr;//整数值表达式名称

	private String[] datestr_arr;//数值表达式名称
	
	private String includechild; //是否包含下级
	
	/**记录集名称*/
    private String setname="A01";
	private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
	/**信息种类，对人员信息查询则选全部子集*/
	private String domainflag=Integer.toBinaryString(Constant.ALL_FIELD_SET);
	/**选中的字段值对列表*/
	private ArrayList fieldsetlist=new ArrayList();
	/**字段名数组*/
	private String left_fields[];
	/**选中的字段名数组*/
	private String right_fields[];  
	
	private String expression;
	
	private ArrayList operlist=new ArrayList();
	
	private ArrayList factorlist=new ArrayList();
	
	private String expre; //传递值
	private String savecrond="0"; //判断是否关闭统计条件窗口 0为不关闭,1为关闭
	
	/**获取计算项目中参考项目list*/
	private ArrayList fielditemlist=new ArrayList();
	private String reference;
	
	/**代码*/
	private String code;
	private String codeid;
	 
	/**选中的指标列表*/
    private ArrayList selectedlist=new ArrayList();
    
    private String[] codesetid_arr; //代码id
    private String[] itemid_arr; //子标id
    private String setid;//子集id
    private ArrayList setlist=new ArrayList();//子集list
    private String[] code_maxarr; //极大值代码
    private String[] code_minarr; //极小值代码
    private String itemid;
    private ArrayList itemlist = new ArrayList();
    private String checktemp;
    private String standid; //标准表id
    private ArrayList standlist = new ArrayList();//标准表
    private String standhlid; //标准表id
    private ArrayList standidlist = new ArrayList();//标准表
    private String[] hfactor_arr; //横一
    private String[] vfactor_arr; //纵一
    private String[] s_hfactor_arr; //横二
    private String[] s_vfactor_arr; //纵二
    private String codearr; //代码指标
    private String[] item; //结果指标
    private String strarr; //非代码型指标
    private String strid; //指标id
    private String[] strid_arr; //指标id
    private String tabid; 
    private String salaryid; 
    
    private ArrayList targetsetlist = new ArrayList(); //目标子集
    private String targetsetid=""; 

	private String targetitemid=""; //目标指标
	private String checkfalg=""; 
    private String mode="";//控制函数出现的参数
    
    private String param;//区分是不是从编制管理过来 许建2010-5-2
    private ArrayList fieldsetlistunit=new ArrayList();//部门子集 xieguiquan2010-7-1
    private ArrayList fieldsetlistpos=new ArrayList();//职位子集xieguiquan2010-7-1
    private String fieldnameunit; //部门子集id xieguiquan2010-7-1
    private String fieldnamepos; //职务子集id xieguiquan2010-7-1
    private String[] strid2_arr; //指标id
    
    private ArrayList list = new ArrayList();
    private ArrayList desc = new ArrayList();
    private ArrayList codes = new ArrayList();
    private String src = "";
    private String dest = "";
    private String people = "";
    private String mess = "";
    private ArrayList  arrs = new ArrayList();
    private HashMap hs = new HashMap(); 
    private ArrayList ass = new ArrayList();
    private boolean flag = true;
    private String d1 = "";

    private String belong_to_unit = "";//人员归属单位
    private ArrayList belong_to_unit_list = new ArrayList();//人员归属单位集合
    
    private ArrayList tempfieldsetlist = new ArrayList();//取临时变量，为了函数向导（取自于）

	public String getD1() {
		return d1;
	}

	public void setD1(String d1) {
		this.d1 = d1;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public ArrayList getAss() {
		return ass;
	}

	public void setAss(ArrayList ass) {
		this.ass = ass;
	}

	public HashMap getHs() {
		return hs;
	}

	public void setHs(HashMap hs) {
		this.hs = hs;
	}

	public ArrayList getArrs() {
		return arrs;
	}

	public void setArrs(ArrayList arrs) {
		this.arrs = arrs;
	}

	public ProjectForm() {
	        CommonData vo=new CommonData("=","=");
	        operlist.add(vo);
	        vo=new CommonData(">",">");
	        operlist.add(vo);  
	        vo=new CommonData(">=",">=");
	        operlist.add(vo); 
	        vo=new CommonData("<","<");
	        operlist.add(vo);
	        vo=new CommonData("<=","<=");
	        operlist.add(vo);   
	        vo=new CommonData("<>","<>");
	        operlist.add(vo);
	    }

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setFieldlist((ArrayList)hm.get("fieldlist"));
		this.setFieldname((String)hm.get("filedname"));
		this.setType((String)hm.get("type"));
		this.setFielditemid((String)hm.get("fielditemid"));
		this.setFielditem((String)hm.get("fielditem"));
		this.setUsedlist((ArrayList)hm.get("usedlist"));
		this.setStat_methods((String)hm.get("stat_methods"));
		this.setStatlist((ArrayList)hm.get("statlist"));
		this.setStrexpression_arr((String[])hm.get("strexpression_arr"));
		this.setNumexpression1_arr((String[])hm.get("numexpression1_arr"));
		this.setNumexpression2_arr((String[])hm.get("numexpression2_arr"));;
		this.setDateexpression1_arr((String[])hm.get("dateexpression1_arr"));
		this.setDateexpression2_arr((String[])hm.get("dateexpression2_arr"));
		this.setIncrementalItem_arr((String[])hm.get("incrementalItem_arr"));
		this.setDatestr_arr((String[])hm.get("datestr_arr"));
		this.setSetname((String)hm.get("setname"));
		this.setExpression((String)hm.get("expression"));
		this.setExpre((String)hm.get("expre"));
		this.setSavecrond((String)hm.get("savecrond"));
		this.setFactorlist((ArrayList)hm.get("factorlist"));
		this.setRight_fields((String[])hm.get("right_fields"));
		if(hm.get("fieldsetlist")!=null){
            this.setFieldsetlist((ArrayList)hm.get("fieldsetlist"));
		}
		if(hm.get("tempfieldsetlist")!=null){
            this.setTempfieldsetlist((ArrayList)hm.get("tempfieldsetlist"));
		}
		this.setFielditemlist((ArrayList)hm.get("fielditemlist"));
		this.setReference((String)hm.get("reference"));
		this.setSelectedlist((ArrayList)hm.get("selectedlist"));
		this.setCodeid((String)hm.get("codeid"));
		this.setCode((String)hm.get("code"));
		this.setIncludechild((String)hm.get("includechild"));
		
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setItemid_arr((String[])this.getFormHM().get("itemid_arr"));
		this.setSetlist((ArrayList)hm.get("setlist"));
		this.setSetid((String)hm.get("setid"));
		this.setCode_maxarr((String[])hm.get("code_maxarr"));
		this.setCode_minarr((String[])hm.get("code_minarr"));
		this.setItemlist((ArrayList)hm.get("itemlist"));
		this.setItemid((String)hm.get("itemid"));
		this.setChecktemp((String)hm.get("checktemp"));
		this.setStandid((String)hm.get("standid"));
		this.setStandlist((ArrayList)hm.get("standlist"));
		this.setHfactor_arr((String[])hm.get("hfactor_arr"));
		this.setVfactor_arr((String[])hm.get("vfactor_arr"));
		this.setS_hfactor_arr((String[])hm.get("s_hfactor_arr"));
		this.setS_vfactor_arr((String[])hm.get("s_vfactor_arr"));
		this.setCodearr((String)hm.get("codearr"));
		this.setStandhlid((String)hm.get("standhlid"));
		this.setStandidlist((ArrayList)hm.get("standidlist"));
		this.setItem((String[])hm.get("item"));
		this.setStrarr((String)hm.get("strarr"));
		this.setStrid((String)hm.get("strid"));
		this.setStrid_arr((String[])hm.get("strid_arr"));
		this.setStrid2_arr((String[])hm.get("strid2_arr"));
		this.setTabid((String)hm.get("tabid"));
		this.setSalaryid((String)hm.get("salaryid"));
		this.setTargetsetlist((ArrayList)hm.get("targetsetlist"));
		this.setTargetsetid((String)hm.get("targetsetid"));
		this.setTargetitemid((String)hm.get("targetitemid"));
		this.setCheckfalg((String)hm.get("checkfalg"));
		this.setRangelist((ArrayList)hm.get("rangelist"));
		this.setRangeid((String)hm.get("rangeid"));
		this.setRangeid((String)hm.get("statid"));
		if(hm.get("fieldsetlistunit")!=null){
            this.setFieldsetlistunit((ArrayList)hm.get("fieldsetlistunit"));
		}
		if(hm.get("fieldsetlistpos")!=null){
            this.setFieldsetlistpos((ArrayList)hm.get("fieldsetlistpos"));
		}
		this.setList((ArrayList)hm.get("list"));
		this.setDesc((ArrayList)hm.get("desc"));
		this.setSrc((String)hm.get("src"));
		this.setDest((String)hm.get("dest"));
		this.setPeople((String)hm.get("people"));
		this.setCodes((ArrayList)hm.get("codes"));
		this.setMess((String)hm.get("mess"));
		this.setArrs((ArrayList)hm.get("arrs"));
		this.setHs((HashMap)hm.get("has"));
		this.setAss((ArrayList)hm.get("ass"));
		this.setFlag(true);
		this.setD1((String)hm.get("d1"));
		this.setBelong_to_unit((String)hm.get("belong_to_unit"));
		if(hm.get("belong_to_unit_list")!=null){
			this.setBelong_to_unit_list((ArrayList)hm.get("belong_to_unit_list"));
		}
	}
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		if(this.getPagination()!=null)
		hm.put("selitem",(ArrayList)this.getPagination().getSelectedList());
		hm.put("fieldname",this.getFieldname());
		hm.put("type",this.getType());
		hm.put("formula",this.getFormula());
		hm.put("setname",this.getSetname());
		hm.put("fielditemid",this.getFielditemid());
		hm.put("expression",this.getExpression());
		hm.put("stat_methods",this.getStat_methods());
		hm.put("right_fields",this.getRight_fields());
		hm.put("factorlist",this.getFactorlist());
		hm.put("mode",this.getMode());
		hm.put("param", param);
		hm.put("fieldnameunit",this.getFieldnameunit());
		hm.put("fieldnamepos",this.getFieldnamepos());
		hm.put("list", this.getList());
		hm.put("desc", this.getDesc());
		hm.put("src", this.getSrc());
		hm.put("dest", this.getDest());
		hm.put("people", this.getPeople());
		hm.put("codes", this.getCodes());
		hm.put("arrs", this.getArrs());
		hm.put("d1", this.getD1());
		hm.put("belong_to_unit", this.getBelong_to_unit());
		hm.put("tempfieldsetlist", this.getTempfieldsetlist());
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public ArrayList getUsedlist() {
		return usedlist;
	}
	public void setUsedlist(ArrayList usedlist) {
		this.usedlist = usedlist;
	}
	public String getFielditemid() {
		return fielditemid;
	}
	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
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
	public String getFielditem() {
		return fielditem;
	}
	public void setFielditem(String fielditem) {
		this.fielditem = fielditem;
	}
	public String getDomainflag() {
		return domainflag;
	}
	public void setDomainflag(String domainflag) {
		this.domainflag = domainflag;
	}
	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}
	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
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

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpre() {
		return expre;
	}

	public void setExpre(String expre) {
		this.expre = expre;
	}

	public String getSavecrond() {
		return savecrond;
	}

	public void setSavecrond(String savecrond) {
		this.savecrond = savecrond;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public ArrayList getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(ArrayList selectedlist) {
		this.selectedlist = selectedlist;
	}
	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIncludechild() {
		return includechild;
	}

	public void setIncludechild(String includechild) {
		this.includechild = includechild;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String[] getItemid_arr() {
		return itemid_arr;
	}

	public void setItemid_arr(String[] itemid_arr) {
		this.itemid_arr = itemid_arr;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
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

	public String[] getCode_maxarr() {
		return code_maxarr;
	}

	public void setCode_maxarr(String[] code_maxarr) {
		this.code_maxarr = code_maxarr;
	}

	public String[] getCode_minarr() {
		return code_minarr;
	}

	public void setCode_minarr(String[] code_minarr) {
		this.code_minarr = code_minarr;
	}

	public String getChecktemp() {
		return checktemp;
	}

	public void setChecktemp(String checktemp) {
		this.checktemp = checktemp;
	}

	public String[] getHfactor_arr() {
		return hfactor_arr;
	}

	public void setHfactor_arr(String[] hfactor_arr) {
		this.hfactor_arr = hfactor_arr;
	}

	public String[] getS_hfactor_arr() {
		return s_hfactor_arr;
	}

	public void setS_hfactor_arr(String[] s_hfactor_arr) {
		this.s_hfactor_arr = s_hfactor_arr;
	}

	public String[] getS_vfactor_arr() {
		return s_vfactor_arr;
	}

	public void setS_vfactor_arr(String[] s_vfactor_arr) {
		this.s_vfactor_arr = s_vfactor_arr;
	}

	public String getStandid() {
		return standid;
	}

	public void setStandid(String standid) {
		this.standid = standid;
	}

	public ArrayList getStandlist() {
		return standlist;
	}

	public void setStandlist(ArrayList standlist) {
		this.standlist = standlist;
	}

	public String[] getVfactor_arr() {
		return vfactor_arr;
	}

	public void setVfactor_arr(String[] vfactor_arr) {
		this.vfactor_arr = vfactor_arr;
	}

	public String getCodearr() {
		return codearr;
	}

	public void setCodearr(String codearr) {
		this.codearr = codearr;
	}

	public ArrayList getStandidlist() {
		return standidlist;
	}

	public void setStandidlist(ArrayList standidlist) {
		this.standidlist = standidlist;
	}

	public String[] getItem() {
		return item;
	}

	public void setItem(String[] item) {
		this.item = item;
	}

	public String getStandhlid() {
		return standhlid;
	}

	public void setStandhlid(String standhlid) {
		this.standhlid = standhlid;
	}

	public String getStrarr() {
		return strarr;
	}

	public void setStrarr(String strarr) {
		this.strarr = strarr;
	}

	public String getStrid() {
		return strid;
	}

	public void setStrid(String strid) {
		this.strid = strid;
	}

	public String[] getStrexpression_arr() {
		return strexpression_arr;
	}

	public void setStrexpression_arr(String[] strexpression_arr) {
		this.strexpression_arr = strexpression_arr;
	}

	public String[] getDateexpression1_arr() {
		return dateexpression1_arr;
	}

	public void setDateexpression1_arr(String[] dateexpression1_arr) {
		this.dateexpression1_arr = dateexpression1_arr;
	}

	public String[] getDateexpression2_arr() {
		return dateexpression2_arr;
	}

	public void setDateexpression2_arr(String[] dateexpression2_arr) {
		this.dateexpression2_arr = dateexpression2_arr;
	}

	public String[] getDatestr_arr() {
		return datestr_arr;
	}

	public void setDatestr_arr(String[] datestr_arr) {
		this.datestr_arr = datestr_arr;
	}

	public String[] getNumexpression1_arr() {
		return numexpression1_arr;
	}

	public void setNumexpression1_arr(String[] numexpression1_arr) {
		this.numexpression1_arr = numexpression1_arr;
	}

	public String[] getNumexpression2_arr() {
		return numexpression2_arr;
	}

	public void setNumexpression2_arr(String[] numexpression2_arr) {
		this.numexpression2_arr = numexpression2_arr;
	}

	public String[] getStrid_arr() {
		return strid_arr;
	}

	public void setStrid_arr(String[] strid_arr) {
		this.strid_arr = strid_arr;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String[] getIncrementalItem_arr() {
		return incrementalItem_arr;
	}

	public void setIncrementalItem_arr(String[] incrementalItem_arr) {
		this.incrementalItem_arr = incrementalItem_arr;
	}

	public String getTargetitemid() {
		return targetitemid;
	}

	public void setTargetitemid(String targetitemid) {
		this.targetitemid = targetitemid;
	}

	public String getTargetsetid() {
		return targetsetid;
	}

	public void setTargetsetid(String targetsetid) {
		this.targetsetid = targetsetid;
	}

	public ArrayList getTargetsetlist() {
		return targetsetlist;
	}

	public void setTargetsetlist(ArrayList targetsetlist) {
		this.targetsetlist = targetsetlist;
	}

	public String getCheckfalg() {
		return checkfalg;
	}

	public void setCheckfalg(String checkfalg) {
		this.checkfalg = checkfalg;
	}

	public String getRangeid() {
		return rangeid;
	}

	public void setRangeid(String rangeid) {
		this.rangeid = rangeid;
	}

	public ArrayList getRangelist() {
		return rangelist;
	}

	public void setRangelist(ArrayList rangelist) {
		this.rangelist = rangelist;
	}

	public String getStatid() {
		return statid;
	}

	public void setStatid(String statid) {
		this.statid = statid;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	

	public ArrayList getFieldsetlistunit() {
		return fieldsetlistunit;
	}

	public void setFieldsetlistunit(ArrayList fieldsetlistunit) {
		this.fieldsetlistunit = fieldsetlistunit;
	}

	public ArrayList getFieldsetlistpos() {
		return fieldsetlistpos;
	}

	public void setFieldsetlistpos(ArrayList fieldsetlistpos) {
		this.fieldsetlistpos = fieldsetlistpos;
	}

	public String getFieldnameunit() {
		return fieldnameunit;
	}

	public void setFieldnameunit(String fieldnameunit) {
		this.fieldnameunit = fieldnameunit;
	}

	public String getFieldnamepos() {
		return fieldnamepos;
	}

	public void setFieldnamepos(String fieldnamepos) {
		this.fieldnamepos = fieldnamepos;
	}

	public String[] getStrid2_arr() {
		return strid2_arr;
	}

	public void setStrid2_arr(String[] strid2_arr) {
		this.strid2_arr = strid2_arr;
	}
	
	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}
	
    
	public ArrayList getDesc() {
		return desc;
	}

	public void setDesc(ArrayList desc) {
		this.desc = desc;
	}
	
    

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public ArrayList getCodes() {
		return codes;
	}

	public void setCodes(ArrayList codes) {
		this.codes = codes;
	}
	
	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getBelong_to_unit() {
		return belong_to_unit;
	}

	public void setBelong_to_unit(String belong_to_unit) {
		this.belong_to_unit = belong_to_unit;
	}

	public ArrayList getBelong_to_unit_list() {
		return belong_to_unit_list;
	}

	public void setBelong_to_unit_list(ArrayList belong_to_unit_list) {
		this.belong_to_unit_list = belong_to_unit_list;
	}

	public ArrayList getTempfieldsetlist() {
		return tempfieldsetlist;
	}

	public void setTempfieldsetlist(ArrayList tempfieldsetlist) {
		this.tempfieldsetlist = tempfieldsetlist;
	}
	
	
}
