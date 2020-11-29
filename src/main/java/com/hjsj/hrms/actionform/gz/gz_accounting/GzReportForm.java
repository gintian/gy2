package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.hjsj.sys.Des;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class GzReportForm extends FrameForm {
	 /**薪资类别*/
    private String salaryid="-1";
    /** 薪资报表 定义 */
    private String  salaryReportName="";
    private String  isPrintWithGroup="0";  //是否按分组指标分页打印  1：是  0：否
    private String  isGroup="0";           //是否分组
    private String  f_groupItem="";          //第一分组指标
    private ArrayList f_groupItemList=new ArrayList();  //第一分组指标列表
    private String  s_groupItem="";          //第二分组指标
    private ArrayList s_groupItemList=new ArrayList();  //第二分组指标列表
    private String  reportStyleID="";         //表类id
    private String  reportDetailID="";        //工资报表id
    private String right_fields[]; 
    private ArrayList rightlist=new ArrayList();  
    
    private String gzGroupCodesetid="";
    private String gzGroupCodeitemid="";
    
    private String screenWidth="1024";
    private String screenHeight="768";
    private String currpage="1";
    private String html="";
    private String tabid="";
    private String turnPage="";
    private String a_code="";
    /**=1分析历史数据，=0分析归档数据*/
    private String archive;
    
    private String year;  //年
    private String month; //月
    private String count; //次
    private HashMap topDateTitleMap;//高级花名册日期型上标题Map集合
    
    private String gz_module="";
    private String condid="";//人员过滤
    private ArrayList condlist=new ArrayList();  //人员过滤条件
    
    private String zeroPrint="";//打印零（1.打印 0.不打印）
    private String printGrid="";//打印格线（1.打印 0.不打印）
    private String pageRows="20";//每页显示多少行
    private String checksalary="";//判断高级花名册所处位置
    private String filterWhl="";  //当前用户权限过滤条件
    private String noManagerFilterSql="";
    private String isAutoCount; //计算方式
    
    private String conditions; //过滤条件
    private String selecttime; //时间范围 1.所有 2.某年 3.某月 4.某次 5.区间
    private ArrayList conditionslist = new ArrayList(); ;
    private String summary; //汇总
    private String dbname; //人员库
    private String titlename; //标题名字
    private String privDb;//权限范围内的人员库b
    private String role;//角色
    private String privCode;//管理范围代码
    private String privCodeValue;//管理范围
    private String address;
    private String category; //薪资或保险类别
    private String manageUserName;
    private String priv_mode;
    private String emptyRow; //是否打印隔线
    private String groupPoint; //分组指标
    private String columnLine; //是否打印隔线
    private String pix; 
    private String column; //是否打印空记录
    private String sortitem; //调整顺序指标
    private String dataarea; //0.单行数据 1.多行数据
    private String model;//判断从审批进入报表还是从发放进入=0发放
    private String boscount;//薪资审批发放次数
    private String bosdate;//薪资审批业务日期
    private String orderby;//薪资顺序
  //调cs插件用到参数,以下参数还未用到，以后用吧
    private String historyFlag;
    private String yearFlag;
    private String monthFlag;
    private String countFlag;
    private String dateStart;
    private String dateEnd;
    //报表所有类型=0共有。=1私有
    private String ownerType;
    private String showUnitCodeTree;
    private String privSet;//传入cs插件，用户权限内的工资套类别串
    private String salaryDataTable;
    private String salaryDataTableCond;
    
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("salaryDataTableCond", this.getSalaryDataTableCond());
		this.getFormHM().put("salaryDataTable",this.getSalaryDataTable());
		this.getFormHM().put("privSet", this.getPrivSet());
		this.getFormHM().put("showUnitCodeTree", this.getShowUnitCodeTree());
		this.getFormHM().put("ownerType", this.getOwnerType());
		this.getFormHM().put("archive", this.getArchive());
		this.getFormHM().put("model",this.getModel());
		this.getFormHM().put("boscount", this.getBoscount());
		this.getFormHM().put("bosdate",this.getBosdate());
		 /** 薪资报表 定义 */
		this.getFormHM().put("salaryReportName",this.getSalaryReportName());
		this.getFormHM().put("isGroup",this.getIsGroup());
		this.getFormHM().put("isPrintWithGroup",this.getIsPrintWithGroup());
		this.getFormHM().put("f_groupItem",this.getF_groupItem());
		this.getFormHM().put("s_groupItem",this.getS_groupItem());
		this.getFormHM().put("reportStyleID",this.getReportStyleID());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("currpage",this.getCurrpage());
		this.getFormHM().put("zeroPrint",this.getZeroPrint());
		this.getFormHM().put("printGrid",this.getPrintGrid());
		this.getFormHM().put("pageRows",this.getPageRows());
		this.getFormHM().put("filterWhl",this.getFilterWhl());
		this.getFormHM().put("noManagerFilterSql", this.getNoManagerFilterSql());
		this.getFormHM().put("conditions",this.getConditions());
		this.getFormHM().put("selecttime",this.getSelecttime());
		this.getFormHM().put("isAutoCount",this.getIsAutoCount());
		this.getFormHM().put("dbname",this.getDbname());
		this.getFormHM().put("category",this.getCategory());
		this.getFormHM().put("condid",this.getCondid());
		this.getFormHM().put("orderby",this.getOrderby());
	}

	@Override
    public void outPutFormHM() {
		this.setSalaryDataTable((String)this.getFormHM().get("salaryDataTable"));
		this.setSalaryDataTableCond((String)this.getFormHM().get("salaryDataTableCond"));
		this.setPrivSet((String)this.getFormHM().get("privSet"));
		this.setShowUnitCodeTree((String)this.getFormHM().get("showUnitCodeTree"));
		this.setOwnerType((String)this.getFormHM().get("ownerType"));
		this.setDateStart((String)this.getFormHM().get("dateStart"));
		this.setDateEnd((String)this.getFormHM().get("dateEnd"));
		this.setCountFlag((String)this.getFormHM().get("countFlag"));
		this.setYearFlag((String)this.getFormHM().get("yearFlag"));
		this.setMonthFlag((String)this.getFormHM().get("monthFlag"));
		this.setHistoryFlag((String)this.getFormHM().get("historyFlag"));
		this.setArchive((String)this.getFormHM().get("archive"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setBoscount((String)this.getFormHM().get("boscount"));
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setPriv_mode((String)this.getFormHM().get("priv_mode"));
		this.setManageUserName((String)this.getFormHM().get("manageUserName"));
		this.setPrivCode((String)this.getFormHM().get("privCode"));
		this.setPrivCodeValue((String)this.getFormHM().get("privCodeValue"));
		this.setPrivDb((String)this.getFormHM().get("privDb"));
		this.setRole((String)this.getFormHM().get("role"));
		this.setFilterWhl((String)this.getFormHM().get("filterWhl"));
		this.setNoManagerFilterSql((String) this.getFormHM().get("noManagerFilterSql"));
//		System.out.println((String)this.getFormHM().get("filterWhl")+"asdf");
//		System.out.println((String) this.getFormHM().get("noManagerFilterSql")+"sdf");
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		
		this.setCurrpage((String)this.getFormHM().get("currpage"));
		this.setHtml((String)this.getFormHM().get("html"));
		this.setSalaryReportName((String)this.getFormHM().get("salaryReportName"));
		this.setIsGroup((String)this.getFormHM().get("isGroup"));
		this.setIsPrintWithGroup((String)this.getFormHM().get("isPrintWithGroup"));
		this.setF_groupItem((String)this.getFormHM().get("f_groupItem"));
		this.setS_groupItem((String)this.getFormHM().get("s_groupItem"));
		this.setF_groupItemList((ArrayList)this.getFormHM().get("f_groupItemList"));
		this.setS_groupItemList((ArrayList)this.getFormHM().get("s_groupItemList"));
		this.setReportStyleID((String)this.getFormHM().get("reportStyleID"));
		this.setReportDetailID((String)this.getFormHM().get("reportDetailID"));
		this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
		
		this.setScreenHeight((String)this.getFormHM().get("screenHeight"));
		this.setScreenWidth((String)this.getFormHM().get("screenWidth"));
		this.setGzGroupCodesetid((String)this.getFormHM().get("gzGroupCodesetid"));
		this.setGzGroupCodeitemid((String)this.getFormHM().get("gzGroupCodeitemid"));
		this.setTurnPage((String)this.getFormHM().get("turnPage"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
		this.setZeroPrint((String)this.getFormHM().get("zeroPrint"));
		this.setPrintGrid((String)this.getFormHM().get("printGrid"));
		this.setPageRows((String)this.getFormHM().get("pageRows"));
		this.setChecksalary((String)this.getFormHM().get("checksalary"));
		
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setTopDateTitleMap((HashMap)this.getFormHM().get("topDateTitleMap"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setSelecttime((String)this.getFormHM().get("selecttime"));
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setConditionslist((ArrayList)this.getFormHM().get("conditionslist"));
		this.setIsAutoCount((String)this.getFormHM().get("isAutoCount"));
		this.setTitlename((String)this.getFormHM().get("titlename"));
		this.setAddress((String)this.getFormHM().get("address"));
		this.setCategory((String)this.getFormHM().get("category"));
		this.setEmptyRow((String)this.getFormHM().get("emptyRow"));
		this.setGroupPoint((String)this.getFormHM().get("groupPoint"));
		this.setColumnLine((String)this.getFormHM().get("columnLine"));
		this.setPix((String)this.getFormHM().get("pix"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setDataarea((String)this.getFormHM().get("dataarea"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getF_groupItem() {
		return f_groupItem;
	}

	public void setF_groupItem(String item) {
		f_groupItem = item;
	}

	public ArrayList getF_groupItemList() {
		return f_groupItemList;
	}

	public void setF_groupItemList(ArrayList itemList) {
		f_groupItemList = itemList;
	}

	public String getIsPrintWithGroup() {
		return isPrintWithGroup;
	}

	public void setIsPrintWithGroup(String isPrintWithGroup) {
		this.isPrintWithGroup = isPrintWithGroup;
	}

	public String getReportDetailID() {
		return reportDetailID;
	}

	public void setReportDetailID(String reportDetailID) {
		this.reportDetailID = reportDetailID;
	}

	public String getReportStyleID() {
		return reportStyleID;
	}

	public void setReportStyleID(String reportStyleID) {
		this.reportStyleID = reportStyleID;
	}

	public String getS_groupItem() {
		return s_groupItem;
	}

	public void setS_groupItem(String item) {
		s_groupItem = item;
	}

	public ArrayList getS_groupItemList() {
		return s_groupItemList;
	}

	public void setS_groupItemList(ArrayList itemList) {
		s_groupItemList = itemList;
	}

	public String getSalaryReportName() {
		return salaryReportName;
	}

	public void setSalaryReportName(String salaryReportName) {
		this.salaryReportName = salaryReportName;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getRightlist() {
		return rightlist;
	}

	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}

	public String getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(String screenHeight) {
		this.screenHeight = screenHeight;
	}

	public String getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(String screenWidth) {
		this.screenWidth = screenWidth;
	}

	public String getGzGroupCodeitemid() {
		return gzGroupCodeitemid;
	}

	public void setGzGroupCodeitemid(String gzGroupCodeitemid) {
		this.gzGroupCodeitemid = gzGroupCodeitemid;
	}

	public String getGzGroupCodesetid() {
		return gzGroupCodesetid;
	}

	public void setGzGroupCodesetid(String gzGroupCodesetid) {
		this.gzGroupCodesetid = gzGroupCodesetid;
	}

	public String getCurrpage() {
		return currpage;
	}

	public void setCurrpage(String currpage) {
		this.currpage = currpage;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getTurnPage() {
		return turnPage;
	}

	public void setTurnPage(String turnPage) {
		this.turnPage = turnPage;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getCondid() {
		return condid;
	}

	public void setCondid(String condid) {
		this.condid = condid;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public String getZeroPrint() {
		return zeroPrint;
	}

	public void setZeroPrint(String zeroPrint) {
		this.zeroPrint = zeroPrint;
	}

	public String getPrintGrid() {
		return printGrid;
	}

	public void setPrintGrid(String printGrid) {
		this.printGrid = printGrid;
	}

	public String getChecksalary() {
		return checksalary;
	}

	public void setChecksalary(String checksalary) {
		this.checksalary = checksalary;
	}

	public String getPageRows() {
		return pageRows;
	}

	public void setPageRows(String pageRows) {
		this.pageRows = pageRows;
	}

	public String getFilterWhl() {
		return filterWhl;
	}

	public void setFilterWhl(String filterWhl) {
		this.filterWhl = filterWhl;
	}
	
	public HashMap getTopDateTitleMap() {
		return topDateTitleMap;
	}

	public void setTopDateTitleMap(HashMap topDateTitleMap) {
		this.topDateTitleMap = topDateTitleMap;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public ArrayList getConditionslist() {
		return conditionslist;
	}

	public void setConditionslist(ArrayList conditionslist) {
		this.conditionslist = conditionslist;
	}

	public String getSelecttime() {
		return selecttime;
	}

	public void setSelecttime(String selecttime) {
		this.selecttime = selecttime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIsAutoCount() {
		return isAutoCount;
	}

	public void setIsAutoCount(String isAutoCount) {
		this.isAutoCount = isAutoCount;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getTitlename() {
		return titlename;
	}

	public void setTitlename(String titlename) {
		this.titlename = titlename;
	}

	public String getPrivCode() {
		return privCode;
	}

	public void setPrivCode(String privCode) {
		this.privCode = privCode;
	}

	public String getPrivCodeValue() {
		return privCodeValue;
	}

	public void setPrivCodeValue(String privCodeValue) {
		this.privCodeValue = privCodeValue;
	}

	public String getPrivDb() {
		return privDb;
	}

	public void setPrivDb(String privDb) {
		this.privDb = privDb;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getManageUserName() {
		return manageUserName;
	}

	public void setManageUserName(String manageUserName) {
		this.manageUserName = manageUserName;
	}

	public String getPriv_mode() {
		return priv_mode;
	}

	public void setPriv_mode(String priv_mode) {
		this.priv_mode = priv_mode;
	}

	public String getEmptyRow() {
		return emptyRow;
	}

	public void setEmptyRow(String emptyRow) {
		this.emptyRow = emptyRow;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumnLine() {
		return columnLine;
	}

	public void setColumnLine(String columnLine) {
		this.columnLine = columnLine;
	}

	public String getGroupPoint() {
		return groupPoint;
	}

	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getDataarea() {
		return dataarea;
	}

	public void setDataarea(String dataarea) {
		this.dataarea = dataarea;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBoscount() {
		return boscount;
	}

	public void setBoscount(String boscount) {
		this.boscount = boscount;
	}

	public String getBosdate() {
		return bosdate;
	}

	public void setBosdate(String bosdate) {
		this.bosdate = bosdate;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

	public String getHistoryFlag() {
		return historyFlag;
	}

	public void setHistoryFlag(String historyFlag) {
		this.historyFlag = historyFlag;
	}

	public String getYearFlag() {
		return yearFlag;
	}

	public void setYearFlag(String yearFlag) {
		this.yearFlag = yearFlag;
	}

	public String getMonthFlag() {
		return monthFlag;
	}

	public void setMonthFlag(String monthFlag) {
		this.monthFlag = monthFlag;
	}

	public String getCountFlag() {
		return countFlag;
	}

	public void setCountFlag(String countFlag) {
		this.countFlag = countFlag;
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getShowUnitCodeTree() {
		return showUnitCodeTree;
	}

	public void setShowUnitCodeTree(String showUnitCodeTree) {
		this.showUnitCodeTree = showUnitCodeTree;
	}

	public String getPrivSet() {
		return privSet;
	}

	public void setPrivSet(String privSet) {
		this.privSet = privSet;
	}

	public String getNoManagerFilterSql() {
		return noManagerFilterSql;
	}

	public void setNoManagerFilterSql(String noManagerFilterSql) {
		this.noManagerFilterSql = noManagerFilterSql;
	}
	
	/**
	 * 高级花名册打印插件参数(加密)
	 * @return
	 */
	public String getMusterPreviewPluginParams() {
        String code=getA_code()!=null?getA_code():"";
        code=code.length()>2?code.substring(2):"";
          
        String cond= "all".equals(getCondid())?"":getCondid();
        String s = "<TABID>"+getTabid()+"</TABID>";
        s+="<RowCountMode>"+getIsAutoCount()+"</RowCountMode>";
        s+="<RowCount>"+getPageRows()+"</RowCount>"; 
        if("0".equals(getZeroPrint()))
            s+="<ShowZero>False</ShowZero>";
        else
            s+="<ShowZero>True</ShowZero>";
        if("0".equals(getPrintGrid()))
            s+="<ShowLines>False</ShowLines>";
        else
            s+="<ShowLines>True</ShowLines>";
        if("6".equals(getGz_module())){
            String[] db_Arr = getDbname().split(",");
            String dbpre="";
            for(int i=0;i<db_Arr.length;i++){
                if(db_Arr[i]!=null&&db_Arr[i].length()>0)
                    dbpre +="NBASE='"+db_Arr[i]+"' OR ";
            }
            dbpre = dbpre.substring(0,dbpre.length()-4);
            s+="<DBNAME>("+dbpre+")</DBNAME>";
            
            String[] ca_Arr = getCategory().split(",");
            String cate="";
            for(int i=0;i<ca_Arr.length;i++){
                if(ca_Arr[i]!=null&&ca_Arr[i].length()>0)
                    cate +="salaryid="+ca_Arr[i]+" OR ";
            }
            cate = cate.substring(0,cate.length()-4);
            s+="<SalaryS>("+cate+")</SalaryS>";
        }else{
            s+="<SalaryID>"+getSalaryid()+"</SalaryID>";
        }
        s+="<CustomOrder>"+getOrderby()+"</CustomOrder>";
        s+="<Where></Where>";
        s+="<CurOrgId>"+code+"</CurOrgId><Filter></Filter>";
        s+="<FilterID>"+cond+"</FilterID><FilterWhere></FilterWhere>";
        s+="<DataType>"+getModel()+"</DataType><A00Z2>"+getBosdate()+"</A00Z2>";
        s+="<A00Z3>"+getBoscount()+"</A00Z3><HistoryFlag>"+getHistoryFlag()+"</HistoryFlag>";
        s+="<Year>"+getYear()+"</Year><Month>"+getMonth()+"</Month><Times>"+getCount()+"</Times>";
        s+="<DateStart>"+getDateStart()+"</DateStart><DateEnd>"+getDateEnd()+"</DateEnd>";
        s = new Des().EncryPwdStr(s);  // 加密
        s = s.replaceAll(System.getProperty("line.separator"), "");  // 
	    return s;
	}

	public String getSalaryDataTable() {
		return salaryDataTable;
	}

	public void setSalaryDataTable(String salaryDataTable) {
		this.salaryDataTable = salaryDataTable;
	}

	public String getSalaryDataTableCond() {
		return salaryDataTableCond;
	}

	public void setSalaryDataTableCond(String salaryDataTableCond) {
		this.salaryDataTableCond = salaryDataTableCond;
	}

}
