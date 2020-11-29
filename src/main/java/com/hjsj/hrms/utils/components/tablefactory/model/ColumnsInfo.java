package com.hjsj.hrms.utils.components.tablefactory.model;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;

import java.io.Serializable;
import java.util.ArrayList;

public class ColumnsInfo implements Serializable,Cloneable{
	 
	 public static int SUMMARYTYPE_SUM = 1;//汇总 
	 public static int SUMMARYTYPE_AVERAGE = 2;//平均
	 public static int SUMMARYTYPE_MIN = 3;//最小
	 public static int SUMMARYTYPE_MAX = 4;//最大
	 
	 public static int LOADTYPE_BLOCK = 1;//页面显示此字段
	 public static int LOADTYPE_HIDDEN = 2;//页面隐藏此字段()
	 public static int LOADTYPE_ONLYLOAD = 3;//只加载数据
	 public static int LOADTYPE_NOTLOAD = 4;//不加载数据也不显示，只是栏目设置的备选指标
	 public static int LOADTYPE_ALWAYSLOAD = 5;//永远加载数据，不管栏目设置里是否显示。初始化是显示
	 public static int LOADTYPE_ALWAYSLOAD_HIDE = 6;//永远加载数据，不管栏目设置里是否显示。初始化时隐藏
	 
	 public int displayIndex = 0;
	
     private String columnId;  //对应的model属性
     private String columnDesc;//表格列描述
     private String descSuffix;//表格列描述 后面追加的显示信息

	 private String columnRealDesc;//column的原始显示信息
     private String hintText;//表头列提示信息
     private String fieldsetid;
     private String codesetId="0"; 
     private boolean codeRealValue=false; //无法匹配上代码时，是否直接显示原值
     public boolean getCodeRealValue() {
		return codeRealValue;
	}

	public void setCodeRealValue(boolean codeRealValue) {
		this.codeRealValue = codeRealValue;
	}

	private boolean vorg = false; 
     private boolean codeSetValid=false;//=true:代码选择树中只可以选叶子; =false:代码选择树中非叶子节点也可以选
     private String parentidFn; //调用CODESETTYPE_TREE型代码选择器时，设置前置方法手动传入上级代码
     private String afterCodeSelectFn;//调用CODESETTYPE_TREE型代码选择器时，选择代码后执行的方法
     /**
      * 自定义codesetid数据源  值为自定义类名称，需实现com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory接口
      * 如果设置了此参数，则优先走此参数
      */
     private String codesource;
     
     /**过滤类型
     *			    如果codesetid 为机构（UN、UM、@K）
     *			          0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
     *			          默认值为1
     *			    如果是普通代码类 
     *			          0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
     **/
     private String ctrltype;
     
     private String nmodule; //业务模块id
     
     /**
 	 * 此列操作数据
 	 * 格式:js 变量名，如js中定义变量 var dataList = [{dataValue:'',dataName:''}],此处传入"dataList"
 	 *
 	 */
     private Object operationData;
     
     private String columnType="A"; //类型 A、N、D、M
     private int inputType=0;//大文本时，0是简单文本，1是html文本
     private int columnLength=18;
     private int decimalWidth=0;
     private boolean allowBlank = true;//是否允许为空 
     private int columnWidth=100;//列宽 像素
     private String defaultValue;
     private boolean readOnly = false;
     private String textAlign;//布局：left;center;right.默认left
     private String rendererFunc;  //渲染函数
     private String validFunc;//编辑校验函数
     private String validRegEx;//校验规则：正则表达式
     
     private String disFormat="";
    

	/*
      * 在开启表格编辑功能下，此参数用于通过业务控制某个单元格是否可以编辑
      * 赋值处理方式：
      * if(editableValidFunc.equals("false")){
      *     此列均不可编辑
      * }else{
      *     例如赋值“aa”
      *     调用js方法 aa(param)来判断点击的单元格是否可编辑，如果aa()返回false，则不可编辑，返回true，则可以继续编辑。
      *     具体param参数格式见使用文档
      * }
      */
     private String editableValidFunc;
     
     
     /**
      *  SUMMARYTYPE_SUM 汇总 
	  *  SUMMARYTYPE_AVERAGE 平均
	  *  SUMMARYTYPE_MIN 最小
	  *  SUMMARYTYPE_MAX 最大
      */
     private int summaryType=0;//汇总方式   只用于统计数值型指标
     private String summaryRendererFunc;
     private int loadtype = 1;
     private boolean locked = false; //是否锁行
     private boolean group = false;//是否按照此列分组
     private boolean key = false;
     private boolean encrypted = false;//是否加密
     private boolean encrypted_64base = false;//是否加密到64base
     private boolean sortable = true;
     private boolean showSortType = false;//是否显示排序类型，默认不显示
     
     
     private boolean removable = false;//是否可以删除
     private String ordertype = "0";//排序方式 =0 无，=1 正序，=2 倒序 
     
     private boolean filterable = true;
     
     private boolean doFilterOnLoad = false;
     
     private ArrayList childColumns = new ArrayList();
     
     //此字段是否可以在 查询组件(querybox)中使用
     private boolean queryable = true;
     
     private String valueTranslator;
     
     /*日期字段 查询、过滤 忽略时分秒*/
     private boolean ignoreTime = true;
     /*关联此指标的联动指标*/
     private String childRelationField="";
	/*被此联动指标关联的指标*/
     private String fatherRelationField="";
     private String imppeople="";
     
     /*导出excel时是否导出此列。默认为true(导出)*/
     private boolean beExport = true;
     //大文本限制长度
     private int limitlength = 0;
     

	public boolean isBeExport() {
		return beExport;
	}

	public void setBeExport(boolean beExport) {
		this.beExport = beExport;
	}

	public String getImppeople() {
		return imppeople;
	}

	public void setImppeople(String imppeople) {
		this.imppeople = imppeople;
	}

	public int getLimitlength() {
		return limitlength;
	}

	public void setLimitlength(int limitlength) {
		this.limitlength = limitlength;
	}

	public ColumnsInfo(){
    	 
     }
     
     /**
      * @param fi
      * 初始化基本属性：itemid、itemtype、itemlength、decimalwidth、codesetid、fillabel、readonly
      */
     public ColumnsInfo(FieldItem fi){
	    	 this.setColumnId(fi.getItemid());
	    	 this.setColumnDesc(fi.getItemdesc());
	    	 this.setCodesetId(fi.getCodesetid());
	    	 this.setColumnType(fi.getItemtype());
	    	 this.setColumnLength(fi.getItemlength());
	    	 this.setDecimalWidth(fi.getDecimalwidth());
	    	 this.setAllowBlank(!fi.isFillable());
	    	 this.setReadOnly(fi.isReadonly());
	    	 this.setInputType(fi.getInputtype());
	    	 if(!"A00".equalsIgnoreCase(fi.getFieldsetid()))//A00字段不存在于数据字典 20160918 dengcan
	    		 this.setFieldsetid(fi.getFieldsetid());
	    	 //数值和日期默认居右
	    	 if("D".equals(fi.getItemtype()) || "N".equals(fi.getItemtype()))
	    		 this.setTextAlign("right");
	    	 
	    	 if("A".equals(fi.getItemtype()))
                 this.setTextAlign("left");
     }
	public String getColumnId() {
		return columnId;
	}
	/**
	 * 设置列对应id（itemid）
	 * @param columnId
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId.toLowerCase();
		//人员姓名字段默认显示 排序类型（拼音、笔画） 按钮
		if("a0101".equals(this.columnId))
			this.setShowSortType(true);
	}
	public String getColumnDesc() {
		return columnDesc;
	}
	/**
	 * 列头描述
	 * @param columnDesc
	 */
	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
		this.columnRealDesc = this.columnRealDesc==null?this.columnDesc:this.columnRealDesc;
	}
	public String getCodesetId() {
		return codesetId;
	}
	
	public String getColumnRealDesc() {
		return columnRealDesc;
	}

	public void setColumnRealDesc(String columnRealDesc) {
		this.columnRealDesc = columnRealDesc;
	}

	public String getCodesource() {
		return codesource;
	}
    /**
     * 设置代码数据来源 类名 （自定义时使用）
     * @param codesource
     */
	public void setCodesource(String codesource) {
		this.codesource = codesource;
	}

	public String getCtrltype() {
		return ctrltype;
	}
    /**
     * 控制方式：默认为1
     *   当代码类为机构时（UN、UM、@K）
     *      0：不控制，1：管理范围，2：操作单位，3：业务范围（当值为3时，nmodule参数必须设置）
     *   当代码类为普通代码时
     *      0：不控制，其他的值为控制（有效无效、有效时间内）
     * @param ctrltype
     */
	public void setCtrltype(String ctrltype) {
		this.ctrltype = ctrltype;
	}

	public String getNmodule() {
		return nmodule;
	}
    /**
     * 设置业务模块号，和ctrltype关联使用
     * @param nmodule
     */
	public void setNmodule(String nmodule) {
		this.nmodule = nmodule;
	}
    /**
     * 设置代码类
     * @param codesetId
     */
	public void setCodesetId(String codesetId) {
		this.codesetId = codesetId;
	}
	
	
	
	public Object getOperationData() {
		return operationData;
	}
	/**
	 * 此列操作数据
	 * 格式:js 变量名，如 var dataList = [{dataValue:'',dataName:''}]
	 *
	 */
	public void setOperationData(String operationData) {
		this.operationData = operationData;
	}
	
	/**
	 * 支持传入arraylist<CommonData>类型值
	 * @param operationData
	 */
	public void setOperationData(ArrayList<CommonData> operationData) {
		this.operationData = operationData;
	}

	
	public String getColumnType() {
		return columnType;
	}
	/**
	 * 设置代码类型：A、M、D、N
	 * @param columnType
	 */
	public void setColumnType(String columnType) {
		this.columnType = columnType;
		if("M".equals(columnType))
			 this.setSortable(false);
	}
	public boolean isAllowBlank() {
		return allowBlank;
	}
	/**
	 * 编辑时是否可以为空
	 * @param allowBlank
	 */
	public void setAllowBlank(boolean allowBlank) {
		this.allowBlank = allowBlank;
	}
	public String getRendererFunc() {
		return rendererFunc;
	}
	/**
	 * 自定义渲染方法名
	 * @param rendererFunc：js 方法名称
	 */
	public void setRendererFunc(String rendererFunc) {
		this.rendererFunc = rendererFunc;
	}
	public int getColumnLength() {
		return columnLength;
	}
	/**
	 * 设置此列编辑时长度限制
	 * @param columnLength
	 */
	public void setColumnLength(int columnLength) {
		this.columnLength = columnLength;
	}
	public int getDecimalWidth() {
		return decimalWidth;
	}
	/**
	 * 小数位数
	 * @param decimalWidth
	 */
	public void setDecimalWidth(int decimalWidth) {
		this.decimalWidth = decimalWidth;
	}
	public int getColumnWidth() {
		return columnWidth;
	}
	/**
	 * 列宽 
	 * @param columnWidth
	 */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * 设置默认值
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getTextAlign() {
		return textAlign;
	}
	/**
	 * 字体align left、center、right
	 * @param textAlign
	 */
	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}
	public String getValidFunc() {
		return validFunc;
	}
	/**
	 * 编辑校验函数
	 * @param validFunc
	 */
	public void setValidFunc(String validFunc) {
		this.validFunc = validFunc;
	}
	public String getValidRegEx() {
		return validRegEx;
	}
	/**
	 * 表计校验正则表达式
	 * @param validRegEx
	 */
	public void setValidRegEx(String validRegEx) {
		this.validRegEx = validRegEx;
	}
	public boolean isLocked() {
		return locked;
	}
	/**
	 * 是否锁列
	 * @param isLocked
	 */
	public void setLocked(boolean isLocked) {
		this.locked = isLocked;
	}
	public boolean isGroup() {
		return group;
	}
	/**
	 * 是否按此列分组
	 * @param isGroup
	 */
	public void setGroup(boolean isGroup) {
		this.group = isGroup;
	}
	public String getEditableValidFunc() {
		return editableValidFunc;
	}
	/**
	 * 编辑前通过此函数判断是否可以编辑
	 * @param editableValidFunc
	 */
	public void setEditableValidFunc(String editableValidFunc) {
		this.editableValidFunc = editableValidFunc;
	}
	public int getSummaryType() {
		return summaryType;
	}
	/**
	 * 汇总类型
	 * 
	 * @param summaryType
	 * SUMMARYTYPE_SUM 汇总 
	 * SUMMARYTYPE_AVERAGE 平均
	 * SUMMARYTYPE_MIN 最小
	 * SUMMARYTYPE_MAX 最大
	 */
	public void setSummaryType(int summaryType) {
		this.summaryType = summaryType;
	}

	/**
	 * 显示类型
	 * @return
	 */
	public int getLoadtype() {
		return loadtype;
	}
    /**
     * 设置此列显示类型
     * @param loadtype
     * LOADTYPE_BLOCK 页面显示此字段
	 * LOADTYPE_HIDDEN 页面隐藏此字段(暂时隐藏，页面可选则显示)
	 * LOADTYPE_ONLYLOAD 只加载数据(只查询数据，但是不显示，比如 a0100字段)
     */
	public void setLoadtype(int loadtype) {
		this.loadtype = loadtype;
	}

	/**
	 * 是否只读
	 * @param readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * 是否只读
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	/**
	 * 是否是主键
	 * @param isKey
	 */
	public boolean isKey() {
		return key;
	}

	/**
	 * 是否是主键
	 * @param isKey
	 */
	public void setKey(boolean isKey) {
		this.key = isKey;
	}

	
	public boolean isEncrypted() {
		return encrypted;
	}
	
	/**
	 * 是否加密
	 * @return
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.encrypted = isEncrypted;
	}
	
	public boolean isEncrypted_64base() {
		return encrypted_64base;
	}
	
	/**
	 * 是否加密_64Base
	 * @return
	 */
	public void setEncrypted_64base(boolean encrypted_64base) {
		this.encrypted_64base = encrypted_64base;
	}

	public boolean isSortable() {
		return sortable;
	}

	/**
	 * 是否可排序 
	 * @param isSortable
	 */
	public void setSortable(boolean isSortable) {
		this.sortable = isSortable;
	}

	public String getParentidFn() {
		return parentidFn;
	}

	public void setParentidFn(String parentidFn) {
		this.parentidFn = parentidFn;
	}

	public String getAfterCodeSelectFn() {
		return afterCodeSelectFn;
	}

	public void setAfterCodeSelectFn(String afterCodeSelectFn) {
		this.afterCodeSelectFn = afterCodeSelectFn;
	}

	public boolean isRemovable() {
		return removable;
	}

	public void setRemovable(boolean isRemovable) {
		this.removable = isRemovable;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	
	public ArrayList getChildColumns() {
		return childColumns;
	}

	public void addChildColumn(ColumnsInfo child) {
		this.childColumns.add(child);
	}
	
	public void setChildColumns(ArrayList childColumns) {
		this.childColumns = childColumns;
	}

	public boolean isCodeSetValid() {
		return codeSetValid;
	}

	public void setCodeSetValid(boolean codeSetValid) {
		this.codeSetValid = codeSetValid;
	}

	public ColumnsInfo clone() {  
		ColumnsInfo column = null;  
        try {  
        		column = (ColumnsInfo) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return column;  
    }

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public String getValueTranslator() {
		return valueTranslator;
	}

	public void setValueTranslator(String valueTranslator) {
		this.valueTranslator = valueTranslator;
	}

	public void setHintText(String hintText) {
		this.hintText = hintText;
	}

	public String getHintText() {
		return hintText;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public boolean isQueryable() {
		return queryable;
	}

	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}

	public boolean isVorg() {
		return vorg;
	}

	public void setVorg(boolean vorg) {
		this.vorg = vorg;
	}

	public boolean isDoFilterOnLoad() {
		return doFilterOnLoad;
	}

	public void setDoFilterOnLoad(boolean doFilterOnLoad) {
		this.doFilterOnLoad = doFilterOnLoad;
	}
	
	public String getDescSuffix() {
		return descSuffix;
	}

	public void setDescSuffix(String descSuffix) {
		this.descSuffix = descSuffix;
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}
	
	public boolean isShowSortType() {
		return showSortType;
	}

	public void setShowSortType(boolean showSortType) {
		this.showSortType = showSortType;
	}
	
	public boolean isIgnoreTime() {
		return ignoreTime;
	}

	public void setIgnoreTime(boolean ignoreTime) {
		this.ignoreTime = ignoreTime;
	}
	
	public String getSummaryRendererFunc() {
		return summaryRendererFunc;
	}

	public void setSummaryRendererFunc(String summaryRendererFunc) {
		this.summaryRendererFunc = summaryRendererFunc;
	}
	
	//栏目设置使用的参数
    private boolean fromDict = false;//是否是来自数据字典的列
	
	@Deprecated
	public boolean isFromDict() {
		return fromDict;
	}
	
	/**
	 * 此方法自2016-08-18起不再支持<br>
	 * 请使用setFieldsetid(String fieldsetid)方法
	 * @param isFromDict
	 */
	@Deprecated
	public void setFromDict(boolean isFromDict) {
		this.fromDict = isFromDict;
		if(isFromDict)
			this.fieldsetid="none";
	}
	
	
	/**
	 * 为兼容就程序，保留这个get方法，请使用getOperationData()方法
	 */
	@Deprecated
	public Object getCodesetData() {
		return operationData;
	}
	
	/**
	 * 为兼容就程序，保留此set方法，请使用setOperationData()方法
	 */
	@Deprecated
	public void setCodesetData(String codesetData) {
		this.operationData = codesetData;
	}
	
	public String getDisFormat() {
		return disFormat;
	}

	public void setDisFormat(String disFormat) {
		this.disFormat = disFormat;
	}
	public String getChildRelationField() {
		return childRelationField;
	}

	public void setChildRelationField(String childRelationField) {
		this.childRelationField = childRelationField;
	}
	
	public String getFatherRelationField() {
		return fatherRelationField;
	}
	
	public void setFatherRelationField(String fatherRelationField) {
		this.fatherRelationField = fatherRelationField;
	}
}
