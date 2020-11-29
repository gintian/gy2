package com.hjsj.hrms.actionform.gz.voucher;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class VoucherForm extends FrameForm {
	/**指标列表*/
    private ArrayList fieldlist=new ArrayList();
    private ArrayList list=new ArrayList();
    private FormFile formfile;
    private String info;
    private String returnInfo;
    /**税表名称*/
    private String tablename;
    /**
     * 定义flag 1表示新增 2表示修改
     * */
    private String flag="1";
    /**
     * voucherList,代表可选科目目录
     * */
    private ArrayList voucherList ;

    /**
     * subject 每个分录所属的科目
     * */
    private String subject;
	/**
	 * 处理科目层级的问题
	 * subjectMap
	 * */
    private HashMap subjectMap;
    
	private String c_mark;//分录摘要
    private String fl_name;//分录名称
    private String c_itemsql;//计算公式
    private String c_where;//限制条件
    private String check_item;//辅助核算项目
    private String check_item_value;//辅助核算值
	
	/**
     * digestList 所有摘要内容
     * */
	/**查询语名*/
    private ArrayList digestList=new ArrayList();
   
    private String xmlValue; 
	
	private String sql;
    
    private PaginationForm voucherForm=new PaginationForm();
    /**凭证类型  interface_type
     * 1代表 财务凭证
     * 2代表按月汇总
     * */
    private String interface_type;
  
    /**当前页*/
    private int current=1;
    /** 凭证ID*/
    private String pn_id;
    /**分录ID*/
    private String fl_id;
	
	private ArrayList titlelist = new ArrayList();//用来存储要显示那些字段
    private String[] xmlArray=null;
    private ArrayList none_field;//用来排除不显示的字段
    private String titleValue;//用于在导出Excel中使用添加头部信息
    private String none_fieldValue;//用于在Excel中排除不显示的信息
    private String sqlValue;//Excel中的查询语句
    private ArrayList salarySetList;//计算公式中的薪资类别
    private ArrayList salaryItemList;//某个薪资类别中的可选字段
    private String itemflag;
    private String salaryid;
    private String itemdesc;
    private String codeitemId;
    private ArrayList nloanList;
    private String n_loan;//借贷方向
    private String clsflag;//用于判断是计算公式还是限制条件
    
	
	private HashMap tempMap;
	private String right_itemdesc;
	private ArrayList cgroupList;
	private String accname;
	private String accid;
	private String accgrade;
	private String i_id;
	private ArrayList salarysetList;//凭证定义，薪资类别
	private ArrayList dbList;//涉及到的人员库
	private ArrayList rightList;
	private ArrayList leftList;
	

    private String left_fields[];
	private String right_fields[];
	private ArrayList salaryList;
	private ArrayList xiangmuList;
	private String xiangmufields;
	private String flagtemp;
	private String no;
	private ArrayList salarySelectedList;//新增或者修改凭证界面上选中的薪资类别
	private ArrayList dbSelectedList;//新政或者修改凭证界面上选中的薪资类别
	
	private String c_name;//凭证的名字
	private String c_type;//凭证类别
	private String dbid;//已经被选中的人员库id（字符串形式）
	
    private String resalarySetArray;//已经被选中的薪资类别(字符串形式);
	private String webURL;//凭证参数的web接口
	private String webFunction;//凭证参数的web接口地址
	private String huizongItem;//已经选中的汇总指标
	private String voucherItem;//已经选中的凭证项目指标
	private String itFlag;//为了解决刷新新增修改凭证分录时的界面的问题，尝试··
	private ArrayList maintainList;//解决凭证项目中指标可以维护时，更改原有方案  将所有可维护指标的信息放入maintainList中
    private ArrayList b0110List = new ArrayList();//归属单位列表  哈药需求  zhaoxg add 2015-9-21
    private String b0110 = "";//归属单位  哈药需求  zhaoxg add 2015-9-21

    @Override
    public void inPutTransHM() {
		
		this.getFormHM().put("selectedList", this.getVoucherForm().getSelectedList());
		this.getFormHM().put("interface_type", this.getInterface_type());
		this.getFormHM().put("pn_id", this.getPn_id());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("subject",this.getSubject());
		this.getFormHM().put("c_mark",this.getC_mark());
		this.getFormHM().put("fl_name",this.getFl_name());
		this.getFormHM().put("titlelist", this.getTitlelist());
		this.getFormHM().put("none_field", this.getNone_field());
		this.getFormHM().put("xmlArray", this.getXmlArray());
		this.getFormHM().put("c_itemsql", this.getC_itemsql());
		this.getFormHM().put("itemflag", this.getItemflag());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("n_loan", this.getN_loan());
		this.getFormHM().put("interface_type",this.getInterface_type());
		this.getFormHM().put("formfile",this.getFormfile());
		this.getFormHM().put("accgrade",this.getAccgrade());
		this.getFormHM().put("accid",this.getAccid());
		this.getFormHM().put("accname",this.getAccname());
		this.getFormHM().put("i_id", this.getI_id());
		this.getFormHM().put("salarysetList", this.getSalarysetList());
		this.getFormHM().put("dbList", this.getDbList());
		this.getFormHM().put("rightList", this.getRightList());
		this.getFormHM().put("leftList", this.getLeftList());
		this.getFormHM().put("clsflag", this.getClsflag());
		this.getFormHM().put("fl_id", this.getFl_id());
		this.getFormHM().put("salaryList", this.getSalaryList());
		this.getFormHM().put("xiangmuList", this.getXiangmuList());
		this.getFormHM().put("xiangmufields", this.getXiangmufields());
		this.getFormHM().put("flagtemp", this.getFlagtemp());
		this.getFormHM().put("no", this.getNo());
		this.getFormHM().put("check_item",this.getCheck_item());
		this.getFormHM().put("check_item_value",this.getCheck_item_value());
		this.getFormHM().put("salarySelectedList", this.getSalarySelectedList());
		this.getFormHM().put("dbSelectedList", this.getDbSelectedList());
		this.getFormHM().put("c_name",this.getC_name());
		this.getFormHM().put("c_type",this.getC_type());
		this.getFormHM().put("dbid", this.getDbid());
		this.getFormHM().put("resalarySetArray", this.getResalarySetArray());
		this.getFormHM().put("webURL",this.getWebURL());
		this.getFormHM().put("webFunction",this.getWebFunction());
		this.getFormHM().put("huizongItem", this.getHuizongItem());
		this.getFormHM().put("voucherItem", this.getVoucherItem());
		this.getFormHM().put("itFlag", this.getItFlag());
		this.getFormHM().put("maintainList", this.getMaintainList());
		this.getFormHM().put("b0110", this.getB0110());
	}

	@Override
    public void outPutFormHM() {
		
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.getVoucherForm().setList((ArrayList)this.getFormHM().get("list"));
		this.setTitlelist((ArrayList) this.getFormHM().get("titlelist"));
		this.setXmlArray((String[]) this.getFormHM().get("xmlArray"));
		this.getVoucherForm().getPagination().gotoPage(current);
		this.setNone_field((ArrayList) this.getFormHM().get("none_field"));
		this.setVoucherList((ArrayList) this.getFormHM().get("voucherList"));
		this.setDigestList((ArrayList) this.getFormHM().get("digestList"));
		this.setPn_id((String) this.getFormHM().get("pn_id"));
		this.setFl_id((String) this.getFormHM().get("fl_id"));
		this.setC_mark((String) this.getFormHM().get("c_mark"));
		this.setFl_name((String) this.getFormHM().get("fl_name"));
		this.setSubject((String) this.getFormHM().get("subject"));
		this.setXmlValue((String) this.getFormHM().get("xmlValue"));
		this.setTitleValue((String) this.getFormHM().get("titleValue"));
		this.setNone_fieldValue((String) this.getFormHM().get("none_fieldValue"));
		this.setSqlValue((String) this.getFormHM().get("sqlValue"));
		this.setC_itemsql((String) this.getFormHM().get("c_itemsql"));
		this.setSalaryItemList((ArrayList) this.getFormHM().get("salaryItemList"));
		this.setSalarySetList((ArrayList) this.getFormHM().get("salarySetList"));
		this.setItemflag((String) this.getFormHM().get("itemflag"));
		this.setCodeitemId((String) this.getFormHM().get("codeitemId"));
		this.setNloanList((ArrayList) this.getFormHM().get("nloanList"));
		this.setTempMap((HashMap) this.getFormHM().get("tempMap"));
		this.setN_loan((String) this.getFormHM().get("n_loan"));
		this.setItemdesc((String) this.getFormHM().get("itemdesc"));
		this.setRight_itemdesc((String) this.getFormHM().get("right_itemdesc"));
		this.setCgroupList((ArrayList) this.getFormHM().get("cgroupList"));
		this.setInterface_type((String) this.getFormHM().get("interface_type"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setReturnInfo((String)this.getFormHM().get("returnInfo"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setAccgrade((String) this.getFormHM().get("accgrade"));
		this.setAccid((String) this.getFormHM().get("accid"));
		this.setAccname((String) this.getFormHM().get("accname"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setI_id((String) this.getFormHM().get("i_id"));
		this.setSalarysetList((ArrayList) this.getFormHM().get("salarysetList"));
		this.setDbList((ArrayList) this.getFormHM().get("dbList"));
		this.setLeftList((ArrayList) this.getFormHM().get("leftList"));
		this.setRightList((ArrayList) this.getFormHM().get("rightList"));
		this.setClsflag((String) this.getFormHM().get("clsflag"));
		this.setC_where((String) this.getFormHM().get("c_where"));
		this.setSalaryList((ArrayList) this.getFormHM().get("salaryList"));
		this.setXiangmuList((ArrayList) this.getFormHM().get("xiangmuList"));
		this.setXiangmufields((String) this.getFormHM().get("xiangmufields"));
		this.setFlagtemp((String) this.getFormHM().get("flagtemp"));
		this.setNo((String) this.getFormHM().get("no"));
		this.setSubjectMap((HashMap) this.getFormHM().get("subjectMap"));
		this.setCheck_item((String) this.getFormHM().get("check_item"));
		this.setCheck_item_value((String) this.getFormHM().get("check_item_value"));
		this.setSalarySelectedList((ArrayList) this.getFormHM().get("salarySelectedList"));
		this.setDbSelectedList((ArrayList) this.getFormHM().get("dbSelectedList"));
	    this.setC_name((String) this.getFormHM().get("c_name"));
	    this.setC_type((String) this.getFormHM().get("c_type"));
	    this.setDbid((String) this.getFormHM().get("dbid"));
	    this.setResalarySetArray((String) this.getFormHM().get("resalarySetArray"));
	    this.setWebURL((String) this.getFormHM().get("webURL"));
	    this.setWebFunction((String) this.getFormHM().get("webFunction"));
	    this.setHuizongItem((String) this.getFormHM().get("huizongItem"));
	    this.setVoucherItem((String) this.getFormHM().get("voucherItem"));
	    this.setItFlag((String) this.getFormHM().get("itFlag"));
	    this.setMaintainList((ArrayList) this.getFormHM().get("maintainList"));
		this.setB0110List((ArrayList) this.getFormHM().get("b0110List"));
		this.setB0110((String) this.getFormHM().get("b0110"));
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public PaginationForm getVoucherForm() {
		return voucherForm;
	}

	public void setVoucherForm(PaginationForm voucherForm) {
		this.voucherForm = voucherForm;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}
	
	public String getInterface_type() {
		return interface_type;
	}

	public void setInterface_type(String interface_type) {
		this.interface_type = interface_type;
	}

	public String getPn_id() {
		return pn_id;
	}

	public void setPn_id(String pn_id) {
		this.pn_id = pn_id;
	}
	
	public ArrayList getTitlelist() {
		return titlelist;
	}

	public void setTitlelist(ArrayList titlelist) {
		this.titlelist = titlelist;
	}

	public String[] getXmlArray() {
		return xmlArray;
	}

	public void setXmlArray(String[] xmlArray) {
		this.xmlArray = xmlArray;
	}
    public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	public ArrayList getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(ArrayList voucherList) {
		this.voucherList = voucherList;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getC_mark() {
		return c_mark;
	}

	public void setC_mark(String c_mark) {
		this.c_mark = c_mark;
	}
	
	public String getFl_name() {
		return fl_name;
	}

	public void setFl_name(String fl_name) {
		this.fl_name = fl_name;
	}
	
	
	public String getFl_id() {
		return fl_id;
	}

	public void setFl_id(String fl_id) {
		this.fl_id = fl_id;
	}
	
	public ArrayList getDigestList() {
		return digestList;
	}
	public void setDigestList(ArrayList digestList) {
		this.digestList = digestList;
	}
	
	public String getTitleValue() {
		return titleValue;
	}

	public void setTitleValue(String titleValue) {
		this.titleValue = titleValue;
	}

	public String getNone_fieldValue() {
		return none_fieldValue;
	}

	public void setNone_fieldValue(String none_fieldValue) {
		this.none_fieldValue = none_fieldValue;
	}

	public ArrayList getNone_field() {
		return none_field;
	}

	public void setNone_field(ArrayList none_field) {
		this.none_field = none_field;
	}
	
	public String getSqlValue() {
		return sqlValue;
	}

	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}

	public ArrayList getSalarySetList() {
		return salarySetList;
	}

	public void setSalarySetList(ArrayList salarySetList) {
		this.salarySetList = salarySetList;
	}

	public ArrayList getSalaryItemList() {
		return salaryItemList;
	}

	public void setSalaryItemList(ArrayList salaryItemList) {
		this.salaryItemList = salaryItemList;
	}
	
	
	public String getItemflag() {
		return itemflag;
	}

	public void setItemflag(String itemflag) {
		this.itemflag = itemflag;
	}
	
	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
	
	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}
	
	public String getCodeitemId() {
		return codeitemId;
	}

	public void setCodeitemId(String codeitemid) {
		this.codeitemId = codeitemid;
	}
	
	
	public ArrayList getNloanList() {
		return nloanList;
	}

	public void setNloanList(ArrayList nloanList) {
		this.nloanList = nloanList;
	}
	public HashMap getTempMap() {
		return tempMap;
	}

	public void setTempMap(HashMap tempMap) {
		this.tempMap = tempMap;
	}
	public String getN_loan() {
		return n_loan;
	}

	public void setN_loan(String n_loan) {
		this.n_loan = n_loan;
	}
	public String getRight_itemdesc() {
		return right_itemdesc;
	}

	public void setRight_itemdesc(String rightItemdesc) {
		right_itemdesc = rightItemdesc;
	}
	public ArrayList getCgroupList() {
		return cgroupList;
	}

	public void setCgroupList(ArrayList cgroupList) {
		this.cgroupList = cgroupList;
	}
	public FormFile getFormfile() {
		return formfile;
	}

	public void setFormfile(FormFile formfile) {
		this.formfile = formfile;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String getAccname() {
		return accname;
	}

	public void setAccname(String accname) {
		this.accname = accname;
	}

	public String getAccid() {
		return accid;
	}

	public void setAccid(String accid) {
		this.accid = accid;
	}

	public String getAccgrade() {
		return accgrade;
	}

	public void setAccgrade(String accgrade) {
		this.accgrade = accgrade;
	}

	public String getI_id() {
		return i_id;
	}

	public void setI_id(String i_id) {
		this.i_id = i_id;
	}

	public ArrayList getSalarysetList() {
		return salarysetList;
	}

	public void setSalarysetList(ArrayList salarysetList) {
		this.salarysetList = salarysetList;
	}

	public ArrayList getDbList() {
		return dbList;
	}

	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}

	public ArrayList getRightList() {
		return rightList;
	}

	public void setRightList(ArrayList rightList) {
		this.rightList = rightList;
	}

	public ArrayList getLeftList() {
		return leftList;
	}

	public void setLeftList(ArrayList leftList) {
		this.leftList = leftList;
	}
	public String getClsflag() {
		return clsflag;
	}
	public void setClsflag(String clsflag) {
		this.clsflag = clsflag;
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

	public ArrayList getSalaryList() {
		return salaryList;
	}

	public void setSalaryList(ArrayList salaryList) {
		this.salaryList = salaryList;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/voucher/searchvoucherdate".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null){
			this.setFlag("1");
		}
		if("/gz/voucher/searchvoucherdate".equals(arg0.getPath())&&arg1.getParameter("b_edit")!=null){
			this.setFlag("2");
		}
		if("/gz/voucher/financial_voucher".equals(arg0.getPath())
                &&arg1.getParameter("b_upload")!=null){
                arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
        }
		/* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 start */
		if("gz/voucher/financial_voucher".equals(arg0.getPath()) && StringUtils.isNotEmpty(arg1.getParameter("isclose"))) {
			arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		/* 安全问题 文件上传 财务凭证定义-设置-导入 xiaoyun 2014-9-16 end */
        return super.validate(arg0, arg1);
    }
	public String getC_where() {
		return c_where;
	}

	public void setC_where(String cWhere) {
		c_where = cWhere;
	}

	public String getC_itemsql() {
		return c_itemsql;
	}

	public void setC_itemsql(String cItemsql) {
		c_itemsql = cItemsql;
	}

	public ArrayList getXiangmuList() {
		return xiangmuList;
	}

	public void setXiangmuList(ArrayList xiangmuList) {
		this.xiangmuList = xiangmuList;
	}

	public String getXiangmufields() {
		return xiangmufields;
	}

	public void setXiangmufields(String xiangmufields) {
		this.xiangmufields = xiangmufields;
	}

	public String getFlagtemp() {
		return flagtemp;
	}

	public void setFlagtemp(String flagtemp) {
		this.flagtemp = flagtemp;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	public HashMap getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(HashMap subjectMap) {
		this.subjectMap = subjectMap;
	}
	public String getCheck_item() {
		return check_item;
	}

	public void setCheck_item(String checkItem) {
		check_item = checkItem;
	}

	public String getCheck_item_value() {
		return check_item_value;
	}

	public void setCheck_item_value(String checkItemValue) {
		check_item_value = checkItemValue;
	}

	public String getXmlValue() {
		return xmlValue;
	}

	public void setXmlValue(String xmlValue) {
		this.xmlValue = xmlValue;
	}
	public ArrayList getSalarySelectedList() {
        return salarySelectedList;
    }

    public void setSalarySelectedList(ArrayList salarySelectedList) {
        this.salarySelectedList = salarySelectedList;
    }
    public ArrayList getDbSelectedList() {
        return dbSelectedList;
    }

    public void setDbSelectedList(ArrayList dbSelectedList) {
        this.dbSelectedList = dbSelectedList;
    }
    public String getC_name() {
        return c_name;
    }

    public void setC_name(String cName) {
        c_name = cName;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String cType) {
        c_type = cType;
    }

    public String getDbid() {
        return dbid;
    }

    public void setDbid(String dbid) {
        this.dbid = dbid;
    }


    public String getResalarySetArray() {
        return resalarySetArray;
    }

    public void setResalarySetArray(String resalarySetArray) {
        this.resalarySetArray = resalarySetArray;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String getWebFunction() {
        return webFunction;
    }

    public void setWebFunction(String webFunction) {
        this.webFunction = webFunction;
    }
    public String getHuizongItem() {
        return huizongItem;
    }

    public void setHuizongItem(String huizongItem) {
        this.huizongItem = huizongItem;
    }

    public String getVoucherItem() {
        return voucherItem;
    }

    public void setVoucherItem(String voucherItem) {
        this.voucherItem = voucherItem;
    }
    public String getItFlag() {
        return itFlag;
    }

    public void setItFlag(String itFlag) {
        this.itFlag = itFlag;
    }
    public ArrayList getMaintainList() {
        return maintainList;
    }

    public void setMaintainList(ArrayList maintainList) {
        this.maintainList = maintainList;
    }

	public ArrayList getB0110List() {
		return b0110List;
	}

	public void setB0110List(ArrayList list) {
		b0110List = list;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
}
