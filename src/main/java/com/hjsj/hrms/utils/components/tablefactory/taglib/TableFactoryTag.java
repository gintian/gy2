package com.hjsj.hrms.utils.components.tablefactory.taglib;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;
import org.apache.struts.taglib.TagUtils;
import org.dom4j.Document;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author guodd
 * @Description:生成表格控件
 * @date 2014-12-3
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class TableFactoryTag extends BodyTagSupport{

	private final int EXT_STORE_FIELD = 1;
	private final int SQL_SELECT_FIELD = 2;
	
	
	
	/**
	 * form 名（数据来源）
	 */
	private String formName;
	/**
	 *  配置文件名称
	 */
	private String constantName;
	/**
	 * 显示列属性名称 Ps：form中ArrayList columns = new ArrayList(); 这里就传入 “columns”
	 */
	private String columnProperty;
	/**
	 * “模块前缀”+“_”+“小模块目录名”+“_”+“五位数字”
	 * 表格唯一标识，用于栏目设置，布局设置 和 取数据用，必须唯一
	 */
	private String subModuleId;
	/**
	 *  数据sql 属性名称
	 */
	private String sqlProperty;
	/**
	 *  排序语句
	 */
	private String orderbyProperty;
	/**
	 *  数据list 属性名称。 数据结构：ArrayList<lazyDynaBean>
	 */
	private String dataProperty;
	
	/**
	 * 自定义参数
	 */
	private String customParamsProperty;
	/**
	 * 主键索引 直接传入值，用逗号隔开
	 */
	private String indexkey="";
	
	/**
	 * 页数，为了跟属性名称参数区分，此参数名称末尾加上str
	 */
	private Integer pagesize = new Integer(20);
	
    /**
     * js属性，创建的对象将指向此参数
     */
	private String jsObjName;
	/**
	 * 是否自动渲染，true：自动全屏渲染，false：不渲染
	 */
	private boolean autoRender = false;
	
	/**
	 * title
	 */
	private String title;
	
	/**
	 * 最大行高值
	 */
	private Integer tdMaxHeight = new Integer(30);
	
	
	/**
	 * 是否启用栏目设置
	 */
	private boolean isScheme = false;
	/**
	 * 是否可以对栏目设置方案进行设置
	 */
	private boolean isSetScheme = true;
	/**
	 * 栏目设置备选指标 空代表没有
	 */
	private String schemeItemKey;
	
	/**
	 * 栏目设置添加指标 functionid
	 */
	private String itemKeyFunctionId;

	private boolean showPublicPlan = false;
	//栏目设置保存后的回调事件
	private String schemeSaveCallback;
	/**
	 * 是否启用布局设置
	 */
	private boolean isLayout = false;
	
	/**
	 * 是否启用统计分析功能
	 */
	private boolean isAnalyse = false;
	
	/**
	 * 是否启用单指标统计
	 */
	private boolean fieldAnalyse = false;
	/**
	 * 业务模块号
	 */
	private String moduleId;
	
	private boolean isColumnFilter=false;
	
	/***
	 * 设置 栏目设置的位置
	 * title menubar  toolbar
	 * */
	private String schemePosition;
	private String beforeBuildComp;
	
	//配置参数对象
	Document constantXml;
	//表格表头对象
	ArrayList tableColumns ;
	//表格数据对象
	ArrayList tableData = null;
	//数据查询SQL语句
	String tableSql="";
	//排序语句
	String tableOrderBy="";
	HashSet dataFields = new HashSet();
	HashMap columnMap = new HashMap();
	UserView userView = null;
	HashMap customParamHM = null;
	
	Boolean editable = Boolean.FALSE;
	Boolean lockable = Boolean.FALSE;
	
	//是否汇总计算
	boolean doSummary = false;
	//分组指标
	ColumnsInfo groupColumn = null;
	
	//分页信息在form中的属性名称
	private String paginationProperty="pageable";
    //当前页
	private int currentPage=1;
	private String toolPosition;
	ArrayList buttons = new ArrayList();
	
	public int doStartTag() throws JspException {
		 doSummary = false;
		 groupColumn = null;
		 tableData = null;
		 customParamHM = null;
		 tableSql = "";
		 tableOrderBy = "";
		 dataFields.clear();
		 editable = Boolean.FALSE;
		 lockable = Boolean.FALSE;
		 columnMap.clear();
		 buttons.clear();
		 toolPosition = null;
		return super.doStartTag();
	}
	
	public int doEndTag() throws JspException {
		
		Category log = Category.getInstance(TableFactoryTag.class.getName());
		Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			//读取参数
			userView = (UserView)TagUtils.getInstance().lookup(this.pageContext,WebConstant.userView,"session");
			ArrayList tableColumns = (ArrayList)TagUtils.getInstance().lookup(this.pageContext, this.formName,this.columnProperty,null);
//			TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, conn);
//			tableColumns = originalTableColumns;
//			if(isScheme){
//				HashMap layoutConfig = tfb.getTableLayoutConfig();
//				if(layoutConfig!=null){
//					ArrayList columnsConfigs = tfb.searchCombineColumnsConfigs(((Integer)layoutConfig.get("schemeId")).intValue(),null);
//					tableColumns = tfb.rebuildColumns((ArrayList)originalTableColumns.clone(),columnsConfigs);
//					pagesize = new Integer(layoutConfig.get("pageRows").toString());
//				}
//			}
				
			if(this.dataProperty != null){
				tableData = (ArrayList)TagUtils.getInstance().lookup(this.pageContext,this.formName, this.dataProperty,null);
			}
			if(this.sqlProperty != null)
            		tableSql = (String)TagUtils.getInstance().lookup(this.pageContext,this.formName, this.sqlProperty,null);
			if(this.orderbyProperty != null)
            		tableOrderBy = (String)TagUtils.getInstance().lookup(this.pageContext,this.formName, this.orderbyProperty,null);
			if(this.customParamsProperty!=null)
				customParamHM = (HashMap)TagUtils.getInstance().lookup(this.pageContext,this.formName, this.customParamsProperty,null);
			//分页信息对象  xuj add 2015-1-27
			Pageable pageable = null;
			try{
				pageable = (Pageable)TagUtils.getInstance().lookup(this.pageContext,this.formName, this.paginationProperty,null);
			}catch(Exception e){
				//忽略
			}
			if(pageable==null){
				log.debug("The "+this.formName+" bean no property is name"+this.paginationProperty+" for Pageable Object!");
			}else{
				pageable.setSql_str(tableSql);
				pageable.setDataList(tableData);
			}
			
			if(tableData==null && tableSql.length()==0)
				throw new JspException("必须存在一个数据来源：dataProperty 或 sqlProperty。");
			
			TableConfigBuilder table = new TableConfigBuilder(subModuleId, tableColumns, jsObjName, userView,conn);
            	table.setTitle(title);
            	table.setPageSize(pagesize);
            	table.setCurrentPage(currentPage);
            	table.setConstantName(constantName);
            	table.setAutoRender(autoRender);
            	table.setTableTools(buttons);
            	table.setDataList(tableData);
            	table.setDataSql(tableSql);
            	table.setIndexKey(indexkey);
            	table.setOrderBy(tableOrderBy);
            	table.setPageable(pageable);
            	table.setEditable(editable);
            	table.setScheme(isScheme);
            	table.setSetScheme(isSetScheme);
            	table.setSchemeItemKey(schemeItemKey);
            	table.setItemKeyFunctionId(itemKeyFunctionId);
            	table.setShowPublicPlan(showPublicPlan);
            	table.setSchemeSaveCallback(schemeSaveCallback);
    			table.setAnalyse(isAnalyse);
    			table.setFieldAnalyse(fieldAnalyse);
    			table.setModuleId(moduleId);
    			table.setColumnFilter(isColumnFilter);
    			table.setSchemePosition(schemePosition);
            	String configStr = table.createExtTableConfig();
            	if(this.beforeBuildComp!=null)
            		configStr = configStr.substring(0, configStr.length()-1)+",beforeBuildComp:"+this.beforeBuildComp+"}";
            	String param = jsObjName+" = new BuildTableObj("+configStr+");";
	    		pageContext.getOut().println(param.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
		return SKIP_BODY;
	}
	
	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setConstantName(String constantName) {
		this.constantName = constantName;
	}

	public void setColumnProperty(String columnProperty) {
		this.columnProperty = columnProperty;
	}

	public void setSqlProperty(String sqlProperty) {
		this.sqlProperty = sqlProperty;
	}

	public void setOrderbyProperty(String orderbyProperty) {
		this.orderbyProperty = orderbyProperty;
	}

	public void setDataProperty(String dataProperty) {
		this.dataProperty = dataProperty;
	}

	public void setIndexkey(String indexkey) {
		this.indexkey = indexkey;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = new Integer(pagesize);
	}

	
	public void setPaginationProperty(String paginationProperty) {
		this.paginationProperty = paginationProperty;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public String getFormName(){
		return this.formName;
	}
	
	public void setButtons(ArrayList buttons){
		this.buttons = buttons;
	}

	public void setJsObjName(String jsObjName) {
		this.jsObjName = jsObjName;
	}

	public void setAutoRender(boolean autoRender) {
		this.autoRender = autoRender;
	}

	public void setTdMaxHeight(int tdMaxHeight) {
		this.tdMaxHeight = new Integer(tdMaxHeight);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubModuleId(String subModuleId) {
		this.subModuleId = subModuleId;
	}

	public void setIsScheme(boolean isScheme) {
		this.isScheme = isScheme;
	}

	public void setIsLayout(boolean isLayout) {
		this.isLayout = isLayout;
	}

	public void setShowPublicPlan(boolean showPublicPlan) {
		this.showPublicPlan = showPublicPlan;
	}

	/**
	 * 栏目设置备选指标
	 */
	public String getSchemeItemKey() {
		return schemeItemKey;
	}
	/**
	 * 栏目设置备选指标
	 */
	public void setSchemeItemKey(String schemeItemKey) {
		this.schemeItemKey = schemeItemKey;
	}
	
	public String getItemKeyFunctionId() {
		return itemKeyFunctionId;
	}

	public void setItemKeyFunctionId(String itemKeyFunctionId) {
		this.itemKeyFunctionId = itemKeyFunctionId;
	}

	/**
	 * 是否启用统计分析功能
	 * @param isAnalyse
	 */
	public void setIsAnalyse(boolean isAnalyse) {
		this.isAnalyse = isAnalyse;
	}

	/**
	 * 统计分析业务模块号
	 * @param analyseBusiId
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setIsSetScheme(boolean isSetScheme) {
		this.isSetScheme = isSetScheme;
	}

	public void setIsColumnFilter(boolean isColumnFilter){
		this.isColumnFilter = isColumnFilter;
	}

	public void setSchemeSaveCallback(String schemeSaveCallback) {
		this.schemeSaveCallback = schemeSaveCallback;
	}

	public void setCustomParamsProperty(String customParamsProperty) {
		this.customParamsProperty = customParamsProperty;
	}

	public String getSchemePosition() {
		return schemePosition;
	}

	/**
	 * @param
	 *  title 代表在title显示
     *  toolbar代表在toolbar显示
     *  menubar代表在menubar显示 
     * @throws
     */
	public void setSchemePosition(String schemePosition) {
		this.schemePosition = schemePosition;
	}

	public void setBeforeBuildComp(String beforeBuildComp) {
		this.beforeBuildComp = beforeBuildComp;
	}

	public void setFieldAnalyse(boolean fieldAnalyse) {
		this.fieldAnalyse = fieldAnalyse;
	}
	
	
}
