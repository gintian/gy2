package com.hjsj.hrms.module.gz.tax.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaxMxBo {
    Connection conn;
    UserView userView;
    //取得不同表中报税时间使用
    String tablename;
    String datetime;
    String salaryid;
    
    public String getTablename() {
    	return tablename;
    }
    
    
    public void setTablename(String tablename) {
    	this.tablename = tablename;
    }
	
	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public TaxMxBo(){
		
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public TaxMxBo(Connection frameconn, UserView userView) {
	    this.conn = frameconn;
	    this.userView = userView;
	}
	public TaxMxBo(Connection frameconn, UserView userView,String salaryid) {
	    this.conn = frameconn;
	    this.userView = userView;
	    this.salaryid = salaryid;
	}
	
	/**
	 * 获得报表输出菜单
	 * @return
	 */
	public String  getReportMenuJson()
	{
		StringBuffer items=new StringBuffer("<jsfn>{xtype:'button',text:'输出报表',menu:{items:[");
		int i=items.length();
//2016-9-30 zhanghua 取消此功能
		if(this.userView.hasTheFunction("3240405")||this.userView.hasTheFunction("3270405")) 
			items.append("{ icon:'../../../images/import.gif', text: '导入申报明细表', handler:function(){searchtax_me.uploadFile()}},");
		if(this.userView.hasTheFunction("3240411"))
			items.append("{ icon:'../../../images/export.gif', text: '导出Excel', handler:function(){searchtax_me.exportDetail() }},");
		if(this.userView.hasTheFunction("3240406")||this.userView.hasTheFunction("3270406")) 
			items.append("{ icon:'../../../images/export.gif', text: '导出申报明细表', handler:function(){searchtax_me.exportDetail('yes')} },");
		if(this.userView.hasTheFunction("3240406")||this.userView.hasTheFunction("3270406")) 
			items.append("{ icon:'../../../images/export.gif', text: '导出个税扣缴申报表', handler:function(){searchtax_me.exportDetail('geshui')} },");
		if(this.userView.hasTheFunction("3240407")||this.userView.hasTheFunction("3270407")) 
			items.append("{ icon:'../../../images/export.gif', text: '导出申报汇总表',  handler:function(){searchtax_me.exportCount()}},");
		if(this.userView.hasTheFunction("3240412"))
			items.append("{ icon:'../../../images/print.gif', text:'高级花名册', handler:function(){searchtax_me.printInform() }} ");
		if(i==items.length())
			return "";
		items.append("]}}</jsfn>");
		return items.toString();
	}
	
	
	public TableConfigBuilder getTableConfig(String salaryid, String date, String table, String taxMode){
		ArrayList<ColumnsInfo> columnList = this.getColumsList(salaryid);
        String orderBy = " order by dbid,a0000,a00z0,a00z1,b0110,e0122 ";
        String sql = "";

        sql = getSql(tablename,salaryid, date, taxMode).toString();
        TableConfigBuilder builder = new TableConfigBuilder("searchtax_id001", columnList, "SearchTax", userView, conn);
		builder.setLockable(true);
	    builder.setDataSql(sql);
	    builder.setOrderBy(orderBy);
	    if(salaryid!=null&&salaryid.length()>0){
	    	builder.setAutoRender(false);
	    	builder.setSetScheme(false);
	    }else{
	    	builder.setAutoRender(true);
	    	builder.setTitle("所得税管理");
	    	builder.setSetScheme(true);
	    }
		builder.setScheme(true);
	    builder.setEditable(true);
	    //过滤
	    builder.setColumnFilter(true);
	    if(this.userView.hasTheFunction("3240410"))
	    	builder.setShowPublicPlan(true); 
	    builder.setPageSize(20);
	    builder.setTableTools(this.getButtonList(salaryid));
	    builder.setSelectable(true);
		return builder;
	}

	public ArrayList<ColumnsInfo> getColumsList(String salaryId) {
        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
        //个税明细表获取固定字段
        ArrayList<Field> fieldList = searchCommonItemList();
        int i=fieldList.size();
        //个税明细表动态维护的指标
        ArrayList<Field> chglist = searchDynaItemList();
        if(chglist.size()>0){
            fieldList.addAll(chglist);
        }
        //fieldList = sortFieldList(fieldList,true);
        for (Field field : fieldList) {
            ColumnsInfo columnsInfo;
            String itemid = field.getName();
            String itemdesc = field.getLabel();
            String codesetId = field.getCodesetid();
            int digits = field.getDecimalDigits();
            String itemType = DataType.typeToName(field.getDatatype());
            itemType = toType(itemType);
            int columnLength=field.getLength();
            int columnWidth=0;
            if (DataDictionary.getFieldItem(itemid) != null) {
				columnWidth = DataDictionary.getFieldItem(itemid).getDisplaywidth()*7;
            } else {
				columnWidth = itemdesc.length() * 20 < 100 ? 100 : itemdesc.length() * 20;
            }// 显示长度

            columnsInfo = getColumnInfo(itemid, itemdesc, columnWidth, codesetId, itemType, columnLength, digits);

            if (!field.isVisible()) {
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
                columnsInfo.setEncrypted(true);
            }

            //针对固定字段设置为页面永远加载数据
            if (i > 0) {
                i--;
                if (field.isVisible())
                    columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
            }
            columnList.add(columnsInfo);
        }
        return columnList;
    }
	/**
	 * 查找对应的指标
	 * @param itemid
	 * @param fieldlist 
	 * @return
	 */
	private Field searchFieldById(String itemid, ArrayList fieldlist)
	{
		Field field=null;
		boolean flag=false;
		for(int i=0;i<fieldlist.size();i++)
		{
			field=(Field)fieldlist.get(i);

			if(itemid.equalsIgnoreCase(field.getName()))
			{
				flag=true;
				break;
			}
		}
		if(!flag)
			field=null;
		return field;
	}
	/**
	 * 按指标排序输出
	 * haveDisplay 是否插入栏目设置隐藏指标
	 * @return
	 */
	private ArrayList sortFieldList(ArrayList fieldlist,Boolean haveDisplay)
	{
		ArrayList sortlist=new ArrayList();
		ArrayList list=new ArrayList();
		//添加隐藏字段，现在凡是为隐藏的字段都显示，包括导出excel中，对于栏目设置表的没有的但显示的
		ArrayList hiddenList=new ArrayList();
		int datasize=0;
		//由于orderby中为固定字段，所以必须存在的字段，但可能不显示
		String needStr = ",a0000,a00z0,a00z1,b0110,e0122,";
		try
		{
			//根据页面设置排列字段顺序，excel导出使用
			String topStr="";
			String rowNumStr1="",rowNumStr2="";
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)//子查询取得当前用户的页面设置方案 若有私人方案取私人的，若没有则取公共的。 2016-10-21 zhanghua
			{
				rowNumStr1="(select  * from ";
				rowNumStr2=" where rownum=1 )";
			}
			else
			{			
				topStr=" top 1 ";
			}
//			Field field=null;
	        ContentDAO dao=new ContentDAO(this.conn);
	        RowSet rset = null;
	        ArrayList<String> dataList=new ArrayList<String>();
	        ArrayList templist=(ArrayList) fieldlist.clone();
	        
	        
			String strSql="select * from  T_SYS_TABLE_SCHEME_ITEM t1 inner join " +
					rowNumStr1+" (select  "+topStr+" * From T_SYS_TABLE_SCHEME where SUBMODULEID='searchtax_id001' and ((USERNAME='"+userView.getUserId()+"' AND IS_SHARE='0') or IS_SHARE='1') " +
					" ORDER BY T_SYS_TABLE_SCHEME.IS_SHARE) "+rowNumStr2+" t2 on t1.SCHEME_ID=t2.SCHEME_ID   ORDER BY t1.DISPLAYORDER";
			
			rset=dao.search(strSql);//取当前页面设置的字段顺序
			
			
			while (rset.next())
            {
                String itemid=rset.getString("itemid");
                if("1".equalsIgnoreCase(rset.getString("is_display"))||haveDisplay||needStr.indexOf("," + itemid.toLowerCase() + ",") != -1) {//仅添加不隐藏的列
                	dataList.add(itemid);
                	int j= 0;
	                while(fieldlist.size()>0&&j<fieldlist.size()){
						Field fi=(Field)fieldlist.get(j);
						if(fi.getName().equalsIgnoreCase(itemid)){
							//判断是否有显示名称，如果显示名称和指标名称相同displaydesc就没有值，去原来的值
							fi.setLabel(StringUtils.isBlank(rset.getString("displaydesc"))?fi.getLabel():rset.getString("displaydesc"));
							list.add(fi);
							templist.remove(fi);
							break;
						}
						j++;
					}
                }else {
                	hiddenList.add(itemid);
                }
                datasize++;
            }
			/*for(String str :dataList){//匹配字段顺序
				int j=0;
				while(fieldlist.size()>0&&j<fieldlist.size()){
					Field fi=(Field)fieldlist.get(j);
					if(fi.getName().equalsIgnoreCase(str)){
						list.add(fi);
						templist.remove(fi);
						break;
					}
					j++;
				}
			}*/
			if(list.size()>0){
				if(haveDisplay)
					list.addAll(templist);
				else {
					//对于设置的不显示的，并且栏目设置表中没有，实际存在的显示的指标添加
					for(int i = 0; i < templist.size(); i++) {
						Field fi=(Field)templist.get(i);
						if(!hiddenList.contains(fi.getName().toLowerCase()) && fi.isVisible()) {
							list.add(fi);
						}
					}
				}
				sortlist=list;
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if(sortlist.size()==0&&datasize!=0)
			sortlist.addAll(fieldlist);
		else if(list.size()==0)
		{
			sortlist = fieldlist;
		}

		return sortlist;
	}
	
	/**
	 * 按指标排序输出
	 * 此方法使用了field的Varible属性标识数值列是否需要合计。1为需要 0为不需要 zhanghua
	 * haveDisplay 是否插入栏目设置隐藏指标
	 * @return
	 */
	private ArrayList sortFieldListFindNotShow(ArrayList fieldlist,Boolean haveDisplay)
	{
		ArrayList sortlist=new ArrayList();
		ArrayList list=new ArrayList();
		//添加隐藏字段，现在凡是为隐藏的字段都显示，包括导出excel中，对于栏目设置表的没有的但显示的
		ArrayList hiddenList=new ArrayList();
		int datasize=0;
		//由于orderby中为固定字段，所以必须存在的字段，但可能不显示
		String needStr = ",a0000,a00z0,a00z1,b0110,e0122,";
		try
		{
			//根据页面设置排列字段顺序，excel导出使用
			String topStr="";
			String rowNumStr1="",rowNumStr2="";
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)//子查询取得当前用户的页面设置方案 若有私人方案取私人的，若没有则取公共的。 2016-10-21 zhanghua
			{
				rowNumStr1="(select  * from ";
				rowNumStr2=" where rownum=1 )";
			}
			else
			{			
				topStr=" top 1 ";
			}
//			Field field=null;
	        ContentDAO dao=new ContentDAO(this.conn);
	        RowSet rset = null;
	        ArrayList<String> dataList=new ArrayList<String>();
	        ArrayList templist=(ArrayList) fieldlist.clone();
	        
	        
			String strSql="select * from  T_SYS_TABLE_SCHEME_ITEM t1 inner join " +
					rowNumStr1+" (select  "+topStr+" * From T_SYS_TABLE_SCHEME where SUBMODULEID='searchtax_id001' and ((USERNAME='"+userView.getUserId()+"' AND IS_SHARE='0') or IS_SHARE='1') " +
					" ORDER BY T_SYS_TABLE_SCHEME.IS_SHARE) "+rowNumStr2+" t2 on t1.SCHEME_ID=t2.SCHEME_ID   ORDER BY t1.DISPLAYORDER";
			
			rset=dao.search(strSql);//取当前页面设置的字段顺序
			
			
			while (rset.next())
            {
                String itemid=rset.getString("itemid");
                if("1".equalsIgnoreCase(rset.getString("is_display"))||haveDisplay||needStr.indexOf("," + itemid.toLowerCase() + ",") != -1) {//仅添加不隐藏的列
                	dataList.add(itemid);
                	int j= 0;
	                while(fieldlist.size()>0&&j<fieldlist.size()){
						Field fi=(Field)fieldlist.get(j);
						if(fi.getName().equalsIgnoreCase(itemid)){
							//判断是否有显示名称，如果显示名称和指标名称相同displaydesc就没有值，去原来的值
							if(needStr.indexOf("," + itemid.toLowerCase() + ",") != -1 && "0".equalsIgnoreCase(rset.getString("is_display")))
								fi.setVisible(false);
							else
								fi.setLabel(StringUtils.isBlank(rset.getString("displaydesc"))?fi.getLabel():rset.getString("displaydesc"));
							
							// 使用Varible标识是否需要合计 zhanghua 2017-8-7
							if("1".equalsIgnoreCase(rset.getString("is_sum")))
								fi.setVarible(1);
							else
								fi.setVarible(0);
							list.add(fi);
							templist.remove(fi);
							break;
						}
						j++;
					}
                }else {
                	hiddenList.add(itemid);
                }
                datasize++;
            }
			/*for(String str :dataList){//匹配字段顺序
				int j=0;
				while(fieldlist.size()>0&&j<fieldlist.size()){
					Field fi=(Field)fieldlist.get(j);
					if(fi.getName().equalsIgnoreCase(str)){
						list.add(fi);
						templist.remove(fi);
						break;
					}
					j++;
				}
			}*/
			if(list.size()>0){
				if(haveDisplay)
					list.addAll(templist);
				else {
					//对于设置的不显示的，并且栏目设置表中没有，实际存在的显示的指标添加
					for(int i = 0; i < templist.size(); i++) {
						Field fi=(Field)templist.get(i);
						if(!hiddenList.contains(fi.getName().toLowerCase()) && fi.isVisible()) {
							list.add(fi);
						}
					}
				}
				sortlist=list;
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if(sortlist.size()==0&&datasize!=0)
			sortlist.addAll(fieldlist);
		else if(list.size()==0)
		{
			sortlist = fieldlist;
		}

		return sortlist;
	}

	/**
	 * 转换类型
	 * @param itemType
	 * @return
	 */
	private String toType(String itemType) {
		if("int".equals(itemType)||"float".equals(itemType)){
			itemType="N";
		}
		else if("string".equals(itemType)){
			itemType="A";
		}
		else if("date".equals(itemType)){
			itemType="D";
		}
		else if("clob".equals(DataType.CLOB)){
			itemType="M";
		}
		return itemType;
	}


    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId id
     * @param columnDesc 名称
     * @param columnDesc 显示列宽
     * @return
     */
    private ColumnsInfo getColumnInfo(String columnId, String columnDesc, int columnWidth, String codesetId, String columnType, int columnLength, int decimalWidth){
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);//显示列宽
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnLength(columnLength);// 显示长度
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
        columnsInfo.setReadOnly(false);// 是否只读
        columnsInfo.setFromDict(false);// 是否从数据字典里来
        columnsInfo.setLocked(false);//是否锁列
        if("N".equals(columnType)){
            columnsInfo.setTextAlign("right");
            columnsInfo.setDefaultValue("0");
        }
        if("D".equals(columnType))
            columnsInfo.setColumnLength(10);

        if("A0101".equals(columnId)) {
            columnsInfo.setLocked(true);
        }
        if("Tax_max_id".equals(columnId)){
            columnsInfo.setKey(true);//全选反选功能
        }
        if("B0110".equals(columnId.toUpperCase())||"E0122".equals(columnId.toUpperCase())||"NBASE".equals(columnId.toUpperCase())){
            columnsInfo.setEditableValidFunc("false");
        }
        if("b0110".equalsIgnoreCase(columnId)){
            columnsInfo.setColumnWidth(100);
        }
        if("ynse".equalsIgnoreCase(columnId)){
            columnsInfo.setColumnWidth(100);
            columnsInfo.setColumnLength(8);
            columnsInfo.setDecimalWidth(2);
        }
        if("basedata".equalsIgnoreCase(columnId)|| "sl".equalsIgnoreCase(columnId)|| "sskcs".equalsIgnoreCase(columnId)|| "sds".equalsIgnoreCase(columnId)){//这几个固定字段的长度固定了
            columnsInfo.setColumnLength(8);
            columnsInfo.setDecimalWidth(2);
        }

        return columnsInfo;
    }
	
	
	 public ArrayList getButtonList(String salaryid) {
	        ArrayList buttonList = new ArrayList();
	        buttonList.add(getReportMenuJson()) ;
	        if(StringUtils.isNotBlank(buttonList.get(0).toString()))
	        	buttonList.add("-"); 
	        if(this.userView.hasTheFunction("3240404")||this.userView.hasTheFunction("3270404"))
	        {
	        	buttonList.add(newButton("个税明细表结构设置",null,"searchtax_me.changeTable",null,"true"));
        		buttonList.add("-");
	        }
	        if(this.userView.hasTheFunction("3270403")||this.userView.hasTheFunction("3270408")||this.userView.hasTheFunction("3240403")||this.userView.hasTheFunction("3240408"))
	        {
	        	buttonList.add(newButton("删除",null,"searchtax_me.deleteData",null,"true"));
	        	buttonList.add("-"); 
	        }
	        if(this.userView.hasTheFunction("3240402")||this.userView.hasTheFunction("3270402"))
	        {
	        	buttonList.add(newButton("保存",null,"searchtax_me.saveEdit",null,"true"));
	        }
            ButtonInfo querybox = new ButtonInfo();
            querybox.setFunctionId("GZ00000510");
            querybox.setType(ButtonInfo.TYPE_QUERYBOX);
            querybox.setText("请输入姓名");
            buttonList.add(querybox);
	        return buttonList;
	    }
	
	/**
     * 
     * @Title:newButton
     * @Description：获取按钮
     * @author wangjl
     * @param text 按钮名称
     * @param id 
     * @param handler js方法
     * @param icon
     * @param getdata 事件触发时是否获取选中数据
     * @return
     */
    private ButtonInfo newButton(String text,String id,String handler,String icon,String getdata)
    {  
        ButtonInfo button = new ButtonInfo(text,handler); 
        if(getdata!=null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if(icon!=null)
            button.setIcon(icon);
        if(id!=null)    
            button.setId(id);
        return button;
    }
    
    /**
     * @param salaryid 
     * @param date 日期
     * @return 返回查询语句
     */
    public StringBuffer getSql(String tablename, String salaryid, String date, String taxMode){
    	StringBuffer sql = new StringBuffer();
    	sql.append("select ");
    	ArrayList<ColumnsInfo> columsList = getColumsList(salaryid);
    	for (ColumnsInfo columnsInfo : columsList) {
//    		if("D".equals(columnsInfo.getColumnType())){ 2016-11-18 zhanghua 24370
//    			sql.append(Sql_switcher.dateToChar("tab."+columnsInfo.getColumnId(),"YYYY-MM-DD")+" as "+columnsInfo.getColumnId()+",");
//    		}else{
//    			sql.append("tab."+columnsInfo.getColumnId()+",");
//    		}
    		if("ljse".equalsIgnoreCase(columnsInfo.getColumnId())) {//累计税额的保留两位小数，否则汇总数值不对
    			sql.append("cast(tab."+columnsInfo.getColumnId() + " as  decimal(18,2)) as " + columnsInfo.getColumnId()+",");
    		}else {
    			sql.append("tab."+columnsInfo.getColumnId()+",");
    		}
		}
    	sql.append(" tab.a0000,dbname.dbid as dbid");
    	sql.append(" from ");
    	sql.append(tablename+" tab");
    	sql.append(" left join ");
    	sql.append("dbname on ");
    	sql.append("tab.nbase=dbname.pre ");
    	sql.append("where "+this.getPrivPre());
    	if(StringUtils.isNotEmpty(salaryid)){
    		sql.append(" and salaryid='"+salaryid+"'");
    	}
//    	if("".equals(date)||StringUtils.isEmpty(date)){
////    		CommonData data = (CommonData) searchDeclareDateList().get(0);
////    		date = data.getDataValue();
//    		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
//    		java.util.Date  newDate = new java.util.Date();
//    		date = format.format(newDate);
//    	}
    	if("JEPWw5tnIio@3HJD@".equals(date)){
    		date = PubFunc.decrypt(date);
    	}
    	datetime = date;
    	sql.append(getFilterCond(datetime,taxMode));
		return sql;
    	
    }
    
    /**
	 * 取得权限过滤语句
	 * @return
	 * @see #hasModulePriv()
	 */
	public String getPrivPre()
	{
		StringBuffer pre=new StringBuffer();
		StringBuffer prelast=new StringBuffer(" (");
		try {
			ArrayList list = userView.getPrivDbList();
			StringBuffer nbases = new StringBuffer();
			for (Object object : list) {
				nbases.append("'"+object+"',");
			}
			nbases.setLength(nbases.length()-1);
			if(userView.isSuper_admin()){
				prelast.append("1=1)");
			}else
			{
				if(list==null||list.size()<=0)
				{
					pre.append("1=2");
				}
				else
				{
					String nunit=userView.getUnitIdByBusi("3");
					pre.append(" 1=2 ");
					String[] unitarr =nunit.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
						String codeid=unitarr[i];
						if(codeid==null|| "".equals(codeid))
							continue;
						if(codeid!=null&&codeid.trim().length()>2)
						{
							if("true".equalsIgnoreCase(this.getDeptID())){
								pre.append(" or (case when nullif(deptid,'') is not null then deptid ");
								if("UN".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" else B0110 end like  '"+codeid.substring(2)+"%') ");
								}
								else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" else e0122 end like  '"+codeid.substring(2)+"%') ");
								}
							}else{
								if("UN".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" or B0110  like  '"+codeid.substring(2)+"%' ");
								}
								else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
								{
									pre.append(" or e0122  like  '"+codeid.substring(2)+"%' ");
								}
							}
			         	}
						else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
						{
							pre.append(" or 1=1 ");
			         	}
					}
					pre.append(")");
					if(nbases.length()>0){
						//Oracle 数据库区分大小写
						pre.append(" and UPPER(nbase) in ("+nbases.toString().toUpperCase()+") ");
					} else {
						pre.append(" and 1=2 ");
					}
				}
				prelast.append(pre);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return prelast.toString();
	}
	
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate 日期
	 * @param taxMode 计税方式
	 * @return
	 */
	private String getFilterCond(String declaredate,String taxMode)
	{
		StringBuffer buf=new StringBuffer();
		if(StringUtils.isNotBlank(taxMode) && !"0".equals(taxMode)) {//计税方式
			buf.append(" and taxmode = ");
			buf.append(taxMode);
		}
		if(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate))
			return StringUtils.isBlank(buf.toString())?"":buf.toString();
		String[] datearr=StringUtils.split(declaredate, ".");
		String theyear=datearr[0];
		String themonth=datearr[1];
		buf.append(" and ");
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);
		return buf.toString();
	}
	
	/**
	 * 取得个税明细表的固定字段列表
	 * @return
	 */
	public ArrayList<Field> searchCommonItemList() {
		ArrayList<Field> templist=new ArrayList<Field>();
		StringBuffer format=new StringBuffer();	
		format.append("############");	
		
		Field field=new Field("A0101",ResourceFactory.getProperty("gz.columns.a0101"));
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("A0101").getItemlength());
		field.setVisible(true);
		templist.add(field);
		
		field=new Field("Tax_max_id","tax_id");
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(false);
		templist.add(field);
		
		field=new Field("nbase",ResourceFactory.getProperty("gz.columns.nbase"));
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		field.setCodesetid("@@");
		field.setVisible(true);
		field.setReadonly(true);
		templist.add(field);

		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(10);
		field.setVisible(false);
		templist.add(field);			
		
		field=new Field("B0110",DataDictionary.getFieldItem("b0110").getItemdesc());
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UN");
		field.setReadonly(true);		
		field.setVisible(true);
		templist.add(field);			

		field=new Field("E0122",DataDictionary.getFieldItem("e0122").getItemdesc());
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UM");
		field.setReadonly(true);		
		field.setVisible(true);
		templist.add(field);			

		field=new Field("Tax_date",ResourceFactory.getProperty("gz.columns.taxdate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);			
		templist.add(field);

		field=new Field("Declare_tax",ResourceFactory.getProperty("gz.columns.declaredate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);			
		templist.add(field);			
		
		field=new Field("TaxMode",ResourceFactory.getProperty("gz.columns.taxmode"));
		field.setLength(2);			
		field.setDatatype(DataType.STRING);
		field.setCodesetid("46");
		field.setReadonly(false);
		field.setVisible(true);			
		templist.add(field);

		Field tax_field = getTaxUnitField(false);
		if(tax_field != null) {
			templist.add(tax_field);
		}

		field=new Field("ynse",ResourceFactory.getProperty("gz.columns.ynse1"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		
		field=new Field("ynssde",ResourceFactory.getProperty("gz.columns.ynssde"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		field=new Field("ljsde",ResourceFactory.getProperty("gz.columns.ljsde"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		
		//税率
		field=new Field("Sl",ResourceFactory.getProperty("gz.columns.sl"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("basedata",ResourceFactory.getProperty("gz.columns.basedata"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("ljse",ResourceFactory.getProperty("gz.columns.ljse"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		//所得税
		field=new Field("Sds",ResourceFactory.getProperty("gz.columns.sds"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);		
		field.setLength(12);
		templist.add(field);
		
		
		
		//减免费用
		field=new Field("allowance","减免费用");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);		 
		field.setLength(12);
		templist.add(field); 
		
		field=new Field("A00Z0",ResourceFactory.getProperty("gz.columns.a00z0"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);	
		templist.add(field);

		//归属次数
		field=new Field("A00Z1",ResourceFactory.getProperty("gz.columns.a00z1"));
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(true);
		templist.add(field);
		//归属部门
		field=new Field("deptid",ResourceFactory.getProperty("gz.columns.lse0122"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UM");
		field.setReadonly(true);		
		field.setVisible(true);
		templist.add(field);
		
		field=new Field("Description",ResourceFactory.getProperty("gz.columns.desc"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setVisible(true);
		templist.add(field);
		
		
		
		
		
		
		
		
		//-------------------------------------------------------------  20181221
		field=new Field("lj_basedata","累计基本减除费用");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		/*
		field=new Field("znjy","子女教育");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("sylr","赡养老人");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("zfdklx","住房贷款利息");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("zfzj","住房租金");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("jxjy","继续教育");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		*/
		 
		field=new Field("Flag",ResourceFactory.getProperty("gz.columns.flag"));
		field.setDatatype(DataType.STRING);
		field.setLength(12);
		field.setVisible(true);
		field.setCodesetid("34");
		templist.add(field);
		
		return templist;
	}
	
	/**
	 * 因为导入个税明细的时候，如果第一次导入，会不显示计税单位指标，导致计税单位导不进去
	 * @param forceShow 是否强制显示个税
	 * @return
	 */
	public Field getTaxUnitField(boolean forceShow) {
		Field field = null;
		//对于从薪资发放进去的所得税管理，判断属性是否设置了计税单位参数，如果设置了，显示，没设置不现实
		if(forceShow) {
			field = setTaxUnitField();
		}else {
			if(StringUtils.isNotBlank(this.salaryid)) {
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(this.salaryid),this.userView);
				String tax_unit = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_UNIT);
				if(StringUtils.isNotBlank(tax_unit)) {
					field = setTaxUnitField();
				}
			}else {
				//对于从所得税管理进入的查找如果gz_tax_mx和taxarchive都没有值则不显示
				RowSet rset = null;
				try {
					int flag = 0;
					ContentDAO dao=new ContentDAO(this.conn);
					rset = dao.search("select taxunit from gz_tax_mx group by taxunit UNION select taxunit from taxarchive group by taxunit");
					while(rset.next()) {
						String taxunit = rset.getString("taxunit");
						if(!StringUtils.isBlank(taxunit)) {
							flag = 1;
							break;
						}
					}
					if(flag == 1) {
						field = setTaxUnitField();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					PubFunc.closeResource(rset);
				}
			}
		}
		return field;
	}
	
	private Field setTaxUnitField() {
		Field field=new Field("taxunit",ResourceFactory.getProperty("gz.columns.taxunit"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UN");
		field.setReadonly(false);		
		field.setVisible(true);
		
		return field;
	}

	/**
	 * 获取全部固定字段
	 * @return
	 * @author ZhangHua
	 * @date 15:07 2018/8/31
	 */
	public ArrayList<Field> searchAllCommonItemList() {
		ArrayList<Field> templist=new ArrayList<Field>();
		StringBuffer format=new StringBuffer();
		format.append("############");
		Field field=new Field("A0101",ResourceFactory.getProperty("gz.columns.a0101"));
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("A0101").getItemlength());
		field.setVisible(true);
		templist.add(field);

		field=new Field("Tax_max_id","tax_id");
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(false);
		templist.add(field);

		field=new Field("nbase",ResourceFactory.getProperty("gz.columns.nbase"));
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		field.setCodesetid("@@");
		field.setVisible(true);
		field.setReadonly(true);
		templist.add(field);

		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(10);
		field.setVisible(false);
		templist.add(field);

		field=new Field("A0000","A0000");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		field.setVisible(false);
		templist.add(field);

		field=new Field("B0110",DataDictionary.getFieldItem("b0110").getItemdesc());
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UN");
		field.setReadonly(true);
		field.setVisible(true);
		templist.add(field);

		field=new Field("E0122",DataDictionary.getFieldItem("e0122").getItemdesc());
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UM");
		field.setReadonly(true);
		field.setVisible(true);
		templist.add(field);

		field=new Field("Tax_date",ResourceFactory.getProperty("gz.columns.taxdate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);
		templist.add(field);

		field=new Field("Declare_tax",ResourceFactory.getProperty("gz.columns.declaredate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);
		templist.add(field);

		field=new Field("TaxMode",ResourceFactory.getProperty("gz.columns.taxmode"));
		field.setLength(2);
		field.setDatatype(DataType.STRING);
		field.setCodesetid("46");
		field.setReadonly(false);
		field.setVisible(true);
		templist.add(field);


		field=new Field("taxunit",ResourceFactory.getProperty("gz.columns.taxunit"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UN");
		field.setReadonly(false);
		field.setVisible(true);
		templist.add(field);


		field=new Field("ynse",ResourceFactory.getProperty("gz.columns.ynse1"));
		field.setDatatype(DataType.FLOAT);
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setVisible(true);
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		//税率
		field=new Field("Sl",ResourceFactory.getProperty("gz.columns.sl"));
		field.setDatatype(DataType.FLOAT);
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setVisible(true);
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);

		field=new Field("sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
		field.setDatatype(DataType.FLOAT);
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setVisible(true);
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);

		field=new Field("basedata",ResourceFactory.getProperty("gz.columns.basedata"));
		field.setDatatype(DataType.FLOAT);
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setVisible(true);
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		//所得税
		field=new Field("Sds",ResourceFactory.getProperty("gz.columns.sds"));
		field.setDatatype(DataType.FLOAT);
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);
		field.setLength(12);
		templist.add(field);

		field=new Field("A00Z0",ResourceFactory.getProperty("gz.columns.a00z0"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);
		templist.add(field);

		//归属次数
		field=new Field("A00Z1",ResourceFactory.getProperty("gz.columns.a00z1"));
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(true);
		templist.add(field);
		//归属部门
		field=new Field("deptid",ResourceFactory.getProperty("gz.columns.lse0122"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UM");
		field.setReadonly(true);
		field.setVisible(true);
		templist.add(field);

		field=new Field("Description",ResourceFactory.getProperty("gz.columns.desc"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setVisible(true);
		templist.add(field);
		field=new Field("Flag",ResourceFactory.getProperty("gz.columns.flag"));
		field.setDatatype(DataType.STRING);
		field.setLength(12);
		field.setVisible(true);
		field.setCodesetid("34");
		templist.add(field);
		return templist;
	}

	
	/**
	 * @return 返回个税明细表动态维护的指标
	 */
	public ArrayList<Field> searchDynaItemList() {
		ArrayList<Field> chglist=new ArrayList<Field>();
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				Document doc = PubFunc.generateDom(ctrlvo.getString("str_value"));
				
				String str_path="/param/items";
				XPath xpath=XPath.newInstance(str_path);	
				List childlist=xpath.selectNodes(doc);
				if(childlist.size()>0)
				{
					Element element=(Element)childlist.get(0);
					String columns=element.getText();
					String[] arr=StringUtils.split(columns, ",");
					for(int i=0;i<arr.length;i++)
					{
						Field field=this.searchItemById(arr[i]);
						if(field!=null)
						{
							if(field.getDataType()==DataType.FLOAT)
							{
								int num = field.getDecimalDigits();
								field.setFormat("####."+this.setFormat(num));
							}else if(field.getDataType()==DataType.INT)
							{
								field.setFormat("####");
							}
							chglist.add(field);
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return chglist;
	}
	
	public ArrayList<Field> getHiddenField() {
		ArrayList<Field> templist=new ArrayList<Field>();
		StringBuffer format=new StringBuffer();
		format.append("############");
		Field field=new  Field("znjy","子女教育");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("sylr","赡养老人");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("zfdklx","住房贷款利息");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("zfzj","住房租金");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("jxjy","继续教育");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		return templist;
	}
	
	private String setFormat(int decimal) {
		StringBuffer retformat = new StringBuffer();
		for(int i=0;i<decimal;i++){
			retformat.append("#");
		}
		return retformat.toString();
	}
	
	/**   
	 * @Title: searchItemById   
	 * @Description:每次查数据库。    
	 * @param @param item_id
	 * @return Field 
	 * @throws   
	*/
	private Field searchItemById(String item_id) {
		Field field = null;
		try {
			if (item_id == null || item_id.length() < 1) {
				return field;
			}
			StringBuffer format = new StringBuffer();
			format.append("############");
			FieldItem fieldItem= DataDictionary.getFieldItem(item_id);
			if (fieldItem!=null) {
				String itemid = fieldItem.getItemid();
				/** 指标隐藏时，把此字段设置为0 */
				field = new Field(itemid, fieldItem.getItemdesc());
				String type = fieldItem.getItemtype();
				String codesetid = fieldItem.getCodesetid();
				field.setCodesetid(codesetid);
				/** 字段为代码型,长度定为50 */
				if ("A".equals(type)) {
					field.setDatatype(DataType.STRING);
					if (codesetid == null || "0".equals(codesetid) || "".equals(codesetid))
						field.setLength(fieldItem.getItemlength());
					else
						field.setLength(50);
					field.setAlign("left");
				} else if ("M".equals(type)) {
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");
				} else if ("N".equals(type)) {
					field.setLength(fieldItem.getItemlength());
					field.setDecimalDigits(fieldItem.getDecimalwidth());
					if (fieldItem.getDecimalwidth() > 0) {
						field.setDatatype(DataType.FLOAT);
						format.setLength(fieldItem.getDecimalwidth());
						field.setFormat("####." + format.toString());
					} else {
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}
					field.setAlign("right");
				} else if ("D".equals(type)) {
					field.setLength(20);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");
				} else {
					field.setDatatype(DataType.STRING);
					field.setLength(fieldItem.getItemlength());
					field.setAlign("left");
				}
				/** 对人员库标识，采用“@@”作为相关代码类 */
				if ("nbase".equalsIgnoreCase(itemid))
					field.setCodesetid("@@");
				field.setVisible(fieldItem.isVisible());
				field.setSortable(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return field;
	}
	
	
	/**
	 * 取得个税明细表中最近报税时间
	 * @return
	 */
	public String searchMaxDeclareDate()
	{
		String str="";
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset = null;
		try
		{
			buf.append(" select "+Sql_switcher.year("max(Declare_tax)")+" as year,"+Sql_switcher.month("max(Declare_tax)")+" as month ");
			buf.append(" from ");
			buf.append(tablename+" tab");
			buf.append(" left join ");
			buf.append("dbname on ");
			buf.append("tab.nbase=dbname.pre ");
			buf.append("where "+this.getPrivPre());
			if(StringUtils.isNotEmpty(salaryid)){
				buf.append(" and salaryid='"+salaryid+"'");
			}
			rset=dao.search(buf.toString());
			if(rset.next()){
				String year=rset.getString("year"),month=rset.getString("month");
				if(StringUtils.isBlank(year)|| "null".equalsIgnoreCase(year)||StringUtils.isBlank(month)|| "null".equalsIgnoreCase(month))
					return "";
				str=year+"."+month;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(rset);
		}
		return str;
	}

	/**
	 * 删除数据
	 * @param doSelectAll 是否支持全选  目前已写死为false
	 * @param taxIds 所得税表Ids
	 * @param table 表名
	 * @param datetime 
	 */
	public void deleteData(Boolean doSelectAll, String taxIds,String table, String datetime) {
		ContentDAO dao = new ContentDAO(conn);
		try {
			String[] split = taxIds.split(",");
			List<String> list = Arrays.asList(split);
			ArrayList taxidlist = new ArrayList();
			StringBuffer taxids = new StringBuffer();
			for (String taxid : list) {
				ArrayList taxid2 = new ArrayList();
				if(doSelectAll){
					taxids.append(PubFunc.decrypt(taxid)+",");//解密
				}else{
					taxid2.add(PubFunc.decrypt(taxid));
				}
				taxidlist.add(taxid2);
			}
			if(doSelectAll){
				taxids.setLength(taxids.length()-1);
			}
			StringBuffer sql = new StringBuffer("delete from ");
			sql.append(table);
			sql.append(" where 1=1 ");
			if(doSelectAll){
				String[] datearr=StringUtils.split(datetime, ".");
				String theyear=datearr[0];
				String themonth=datearr[1];
				if(StringUtils.isNotEmpty(taxIds)){
					sql.append(" and tax_max_id not in("+taxids+") ");
				}
				sql.append(" and ");
				sql.append(Sql_switcher.year("Declare_tax"));
				sql.append("=");
				sql.append(theyear);
				sql.append(" and ");
				sql.append(Sql_switcher.month("Declare_tax"));
				sql.append("=");
				sql.append(themonth);
				dao.update(sql.toString());
			}else{
				sql.append(" and tax_max_id = ? ");
				dao.batchUpdate(sql.toString(), taxidlist);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取页面返回的sql和排序
	 * @return
	 */
	public HashMap<String, String> getPageSql() {
		String sql = "";
		String sortSql = "";
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("searchtax_id001");
			if(tableCache != null) {
				String combineSql = (String)tableCache.get("combineSql");
				if(!combineSql.trim().endsWith("myGridData")) {
					combineSql = combineSql.replace("myGridData.","");
					sql = combineSql.substring(combineSql.lastIndexOf("where") + 5, combineSql.length());
				}
				sortSql = tableCache.getSortSql();
			}
			map.put("sql", sql);
			map.put("sortSql", sortSql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 导出申报明细表
	 * @param table 要导出的表
	 * @param datetime 导出数据的时间
	 * @return 返回文件名
	 */
	public String exportDetail(String table, String datetime, String exporttype, String taxMode) {
		String fileName = this.userView.getUserName()+ "_gz.xls";
		try {
			ExportExcelUtil export = new ExportExcelUtil(conn,this.userView);
			if("1".equals(exporttype)){
				fileName = this.userView.getUserName()+"_gz.xls";
			}
			String sheetName = null;
			ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
			
			HashMap dropDownMap = null;
			String salaryid = this.salaryid;
			HashMap<String,Object> hashMap = getExportSql(table, salaryid, datetime, exporttype, taxMode);
			StringBuffer sql = (StringBuffer) hashMap.get("sql");
			ArrayList<LazyDynaBean> headList = (ArrayList<LazyDynaBean>) hashMap.get("headlist");
			
			HashMap<String, String> map = getPageSql();
			String sortSql = (String)map.get("sortSql");
			String sql_ = (String)map.get("sql");
			if(StringUtils.isNotBlank(sql_)) {
				if("1".equals(exporttype)){
					sql.append(" where ");
				}else {
					sql.append(" and ");
				}
				sql.append(sql_);
			}
			if(StringUtils.isNotBlank(sortSql))
				sql.append(" " + sortSql);
			else
				sql.append(" order by a0000,a00z0,a00z1,b0110,e0122 ");//xiegh 20170520  update:导出数据顺序要跟界面保持一致 bug:27907
			export.exportExcelBySql(fileName, sheetName, mergedCellList, headList, sql.toString(), dropDownMap, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
		
	}

	/**
	 * 同步个税归档表
	 * @author ZhangHua
	 * @date 10:45 2018/6/8
	 */
	public void syncSalaryTaxArchiveStrut() {
		try {
			DbWizard dbw = new DbWizard(this.conn);
			String _str = "/a0100/tax_date/a00z0/a00z1/tax_max_id/salaryid/a0000/b0110/e0122/a0101/declare_tax/taxitem/sskcs/ynse/basedata/sl/sds/taxmode/description/userflag/flag/a00z2/a00z3/ynse_field/deptid/";
			ArrayList<Field> al = searchAllCommonItemList();
			if (!dbw.isExistTable("taxarchive", false)) {
				Table table = new Table("taxarchive");
				for (Field field : al)
					table.addField(field);
				dbw.createTable(table);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList chgList = searchDynaItemList();
			HashMap amap = new HashMap();
			RowSet rowSet = dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData data = rowSet.getMetaData();
			ArrayList addList = new ArrayList();
			for (int i = 1; i <= data.getColumnCount(); i++) {
				String columnName = data.getColumnName(i).toLowerCase();
				amap.put(columnName, "1");
			}
			HashMap map = new HashMap();
			for (int i = 0; i < chgList.size(); i++) {
				Field field = (Field) chgList.get(i);
				FieldItem tempItem = DataDictionary.getFieldItem(field.getName().toLowerCase());
				if (tempItem != null) {
					if (amap.get(field.getName().toLowerCase()) == null)
						addList.add((FieldItem) tempItem.clone());
					map.put(tempItem.getItemid(), tempItem);
				}
			}
			
			
			if(amap.get("ljsde")==null)
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljsde","累计应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljse","累计预扣税额");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field); 
				dbw.addColumns(table); 
			}//
			
			
			if(amap.get("znjy")==null) //20181221
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("lj_basedata","累计基本减除费用");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("znjy","子女教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("sylr","赡养老人");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				
				field=new Field("zfdklx","住房贷款利息");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("zfzj","住房租金");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("jxjy","继续教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field); 
				
				dbw.addColumns(table); 
				
			}//
			
			
			
			if(amap.get("allowance")==null) //20181221
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("allowance","税收减免"); //残疾人个税减免
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				dbw.addColumns(table); 
			}
			
			
			
			
			if (addList.size() > 0) {
				Table table = new Table("gz_tax_mx");
				for (int i = 0; i < addList.size(); i++)
					table.addField(((FieldItem) addList.get(i)).cloneField());
				dbw.addColumns(table);
			}

			//将固定字段加入待同步列表
			FieldItem item;
			for (Field field : al) {
				item = new FieldItem();
				item.setItemid(field.getName());
				item.setItemdesc(field.getLabel());
				if (field.getDataType() == DataType.INT || field.getDataType() == DataType.FLOAT) {
					item.setItemtype("N");
					item.setDecimalwidth(field.getDecimalDigits());
				} else if (field.getDataType() == DataType.DATE)
					item.setItemtype("D");
				else if (field.getDataType() == DataType.STRING)
					item.setItemtype("A");
				item.setItemlength(field.getLength());
				map.put(item.getItemid(), item);
			}
			ArrayList alterList = new ArrayList();
			ArrayList resetList = new ArrayList();
			ArrayList delList = new ArrayList();

			rowSet = dao.search("select * from taxarchive where 1=2");
			data = rowSet.getMetaData();
			HashMap existMap = new HashMap();

			for (int i = 1; i <= data.getColumnCount(); i++) {
				String columnName = data.getColumnName(i).toLowerCase();

				if (_str.indexOf("/" + columnName + "/") == -1) {
					if (amap.get(columnName) == null && DataDictionary.getFieldItem(columnName) != null)
						delList.add(DataDictionary.getFieldItem(columnName));
				}

				existMap.put(columnName, "1");
				if (map.get(columnName) != null) {
					FieldItem tempItem = (FieldItem) map.get(columnName);
					int columnType = data.getColumnType(i);
					int size = data.getColumnDisplaySize(i);
					int scale = data.getScale(i);
					switch (columnType) {
						case java.sql.Types.INTEGER:
							if ("N".equals(tempItem.getItemtype())) {
								if (tempItem.getDecimalwidth() != scale)
									alterList.add(tempItem.cloneField());
								else if (size < tempItem.getItemlength() && tempItem.getItemlength() <= 10) //2013-11-23  如果指标长度改大了，需同步结构
								{
									alterList.add(tempItem.cloneField());
								}
							}
							if (!"N".equals(tempItem.getItemtype())) {
								if ("A".equals(tempItem.getItemtype()))
									alterList.add(tempItem.cloneField());
								else
									resetList.add(tempItem.cloneField());
							}
							break;
						case java.sql.Types.TIMESTAMP:
							if (!"D".equals(tempItem.getItemtype())) {
								resetList.add(tempItem.cloneField());
							}
							break;
						case java.sql.Types.VARCHAR:
							if ("A".equals(tempItem.getItemtype())) {
								if (tempItem.getItemlength() > size)
									alterList.add(tempItem.cloneField());
							} else
								resetList.add(tempItem.cloneField());
							break;
						case java.sql.Types.DOUBLE:
							if ("N".equals(tempItem.getItemtype())) {
								if (tempItem.getDecimalwidth() != scale)
									alterList.add(tempItem.cloneField());
								else if ((size - scale) < tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									alterList.add(tempItem.cloneField());
								}
							}
							if (!"N".equals(tempItem.getItemtype())) {
								if ("A".equals(tempItem.getItemtype()))
									alterList.add(tempItem.cloneField());
								else
									resetList.add(tempItem.cloneField());
							}


							break;
						case java.sql.Types.NUMERIC:
							if ("N".equals(tempItem.getItemtype())) {
								if (tempItem.getDecimalwidth() != scale)
									alterList.add(tempItem.cloneField());
								else if ((size - scale) < tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									alterList.add(tempItem.cloneField());
								}
							}
							if (!"N".equals(tempItem.getItemtype())) {
								if ("A".equals(tempItem.getItemtype()))
									alterList.add(tempItem.cloneField());
								else
									resetList.add(tempItem.cloneField());
							}
							break;
						case java.sql.Types.LONGVARCHAR:
							if (!"M".equals(tempItem.getItemtype())) {
								resetList.add(tempItem.cloneField());
							}
							break;
					}
				}
			}
			rowSet.close();

			Table table = new Table("taxarchive");
			if (Sql_switcher.searchDbServer() != 2)  //不为oracle
			{
				for (int i = 0; i < alterList.size(); i++)
					table.addField((Field) alterList.get(i));
				if (alterList.size() > 0)
					dbw.alterColumns(table);
				table.clear();
			} else {
				SalaryTypeBo bo=new SalaryTypeBo(this.conn,this.userView);
				bo.syncGzOracleField(data, map, "taxarchive");
			}
			for (int i = 0; i < resetList.size(); i++)
				table.addField((Field) resetList.get(i));
			if (resetList.size() > 0) {
				dbw.dropColumns(table);
				dbw.addColumns(table);
			}

			table.clear();
			int n = 0;
			for (int i = 0; i < addList.size(); i++) {
				FieldItem field = (FieldItem) addList.get(i);
				if (existMap.get(field.getItemid().toLowerCase()) == null) {
					table.addField(field.cloneField());
					n++;
				}
			}
			if (n > 0)
				dbw.addColumns(table);

			table.clear();
			n = 0;
			for (int i = 0; i < delList.size(); i++) {
				FieldItem field = (FieldItem) delList.get(i);
				table.addField(field.cloneField());
				n++;
			}
			if (n > 0)
				dbw.dropColumns(table);

			if (existMap.get("a00z2") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("A00Z2", "A00Z2");
				field.setDatatype(DataType.DATE);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("a00z3") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("A00Z3", "A00Z3");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("ynse_field") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("ynse_field", "ynse_field");
				field.setDatatype(DataType.STRING);
				field.setLength(5);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("deptid") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("deptid", "deptid");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("ynse") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("ynse", "ynse");
				field.setDatatype(DataType.FLOAT);
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("userflag") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("UserFlag", "UserFlag");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("declare_tax") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("declare_date", "declare_date");
				field.setDatatype(DataType.DATE);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("salaryid") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("salaryid", "salaryid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("taxmode") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("taxmode", "taxmode");
				field.setDatatype(DataType.STRING);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("description") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("description", "description");
				field.setDatatype(DataType.STRING);
				field.setLength(200);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("a0000") == null) {
				Table tbl = new Table("taxarchive");
				Field field=new Field("A0000","A0000");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setVisible(false);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			if (existMap.get("flag") == null) {
				Table tbl = new Table("taxarchive");
				Field field = new Field("flag", "flag");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			
			
			
			if(existMap.get("ljsde")==null)
			{
				Table tbl = new Table("taxarchive");
				Field field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("ljsde","累计应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("ljse","累计预扣税额");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field); 
				dbw.addColumns(tbl); 
			}//
			
			
			if(existMap.get("znjy")==null) //20181221
			{
				Table tbl = new Table("taxarchive");
				Field field=new Field("lj_basedata","累计基本减除费用");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("znjy","子女教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("sylr","赡养老人");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				
				field=new Field("zfdklx","住房贷款利息");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("zfzj","住房租金");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				
				field=new Field("jxjy","继续教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field); 
				
				dbw.addColumns(tbl); 
				
			}//
			 
			if(existMap.get("allowance")==null) //20181221
			{
				Table tbl = new Table("taxarchive");
				Field field=new Field("allowance","税收减免"); //残疾人个税减免
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				tbl.addField(field);
				dbw.addColumns(tbl);
			}
			
			
			for (int i = 0; i < chgList.size(); i++) {//插入动态维护的字段 2016-10-13 zhanghua
				Field field = (Field) chgList.get(i);
				if (existMap.get(field.getName().toLowerCase()) == null) {
					Table tbl = new Table("taxarchive");
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
	
	/**
	 * @param fromtable 表名
	 * @param salaryid
	 * @param datetime 时间
	 * @param exporttype 导出类型
	 * @return 返回导出表头list和sql
	 */
	public HashMap<String, Object> getExportSql(String fromtable, String salaryid ,String datetime , String exporttype, String taxMode){
		HashMap<String, Object> map = new HashMap<String, Object>();
		//导出excel头部列
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean = null;
		//个税明细表获取固定字段
        ArrayList<Field> itemList = searchCommonItemList();
        //个税明细表动态维护的指标
        ArrayList<Field> chglist = searchDynaItemList();
        if(chglist.size()>0){
        	itemList.addAll(chglist);
        }
        //由于orderby中为固定字段，所以必须存在的字段，但可能不显示sunjian
        itemList = sortFieldListFindNotShow(itemList,false);
		StringBuffer feildstr = new StringBuffer();
		String itemType = "A";
		String needStr = ",a0000,a00z0,a00z1,b0110,e0122,";
		
		for (Field field : itemList) {
			if(field.isVisible()){
				itemType = DataType.typeToName(field.getDataType());
				String itemid = field.getName();
				bean = new LazyDynaBean();
				bean.set("content", field.getLabel());// 列头名称:主键标识串，必选
	             bean.set("itemid", itemid);// 列头代码，必须与sql语句的查询字段一一对应，必?选；
	             bean.set("comment", itemid);// 列头注释:主键标识串，可?选；
				if("int".equals(itemType )||"float".equals(itemType)){
						bean.set("decwidth", field.getDecimalDigits()+"");
	                	itemType="N";
	                	if(field.getVarible()==1)//Varible 为1 认为需要合计
	                		bean.set("total", "1");//添加合计标识 zhanghua 2017-8-3
	                }
	                else if("string".equals(itemType)){
	                	itemType="A";
	                }
	                else if("date".equals(itemType)){
	                	itemType="D";//2016-11-18 zhanghua 24370
	                }
	                else if("clob".equals(itemType)){
	                	itemType="M";
	                }
	             bean.set("colType", itemType);// 该列数据类型 ，A是字符型，D是日期型，N是数字型，可选，默认是A；
	             bean.set("codesetid", field.getCodesetid());// 该列数据如果是代码类，设置其代码类id，默认为“0”，可选；
	             headList.add(bean);
	             if(!"0".equals(exporttype)){
		             //if(!(itemid.equalsIgnoreCase("Tax_max_id")||itemid.equalsIgnoreCase("A0100"))){//12.7.28 zhanghua 导出报错。
	            	 if(!("Tax_max_id".equalsIgnoreCase(itemid))){
			 			 if("1".equalsIgnoreCase(exporttype))//合并
			 			 {
			 				if(!"deptid".equalsIgnoreCase(itemid))
			 				    itemid= getitemstr(itemid,field.getDatatype());
			 				else
			 	    			itemid="max("+itemid+") as "+itemid;
			 			 }
			 			 feildstr.append(","+itemid);
		             }
	             }
	           //对于这些必须存在的指标，不显示的给了setvisible(false);不需要加入headlist,但要加入feildstr sunjian
			}else if(needStr.indexOf("," + field.getName().toLowerCase() + ",") != -1){
				String itemid = field.getName();
				if(!"0".equals(exporttype)){
		 			 if("1".equalsIgnoreCase(exporttype))//合并
		 				  itemid= getitemstr(itemid,field.getDatatype());
		 			 feildstr.append(","+itemid);
	             }
			}
		}
		map.put("headlist", headList);
		//导出明细
		if("0".equals(exporttype)){
			map.put("sql", getSql(fromtable, salaryid, datetime, taxMode));
			return map;
		}else{//按人员，计税时间，计税方式合并输出
			StringBuffer sqlsb = new StringBuffer("select max(dbid) as dbid,max(A0000) as a0000 ");
			//权限
			String pre = this.getPrivPre();
			//日期
			if("JEPWw5tnIio@3HJD@".equals(datetime)){
	    		datetime = PubFunc.decrypt(datetime);
	    	}
			String timewhere = getFilterCond(datetime, taxMode);
			sqlsb.append(feildstr+" from "+fromtable+" left join (select dbid,pre from dbname) dbname on "+fromtable+".nbase=dbname.pre ");
			sqlsb.append(" where ("+pre+")");
			if(timewhere!=null&&!"".equals(timewhere))
			{
				sqlsb.append(timewhere);
			}
			if(StringUtils.isNotEmpty(salaryid)){
				sqlsb.append(" and salaryid='"+salaryid+"'");
	    	}
			sqlsb.append(" group by nbase,a0100,"+Sql_switcher.dateToChar("tax_date","yyyy-MM")+",taxmode,taxunit");
			StringBuffer ss= new StringBuffer();
			ss.append(" select * from (");
			ss.append(sqlsb.toString());
			ss.append(") T");
			sqlsb.setLength(0);
			sqlsb.append(ss.toString());

			map.put("sql", sqlsb);
			return map;
		}
	}
	
	/**
	 * 获得要查询的字段
	 * @param itemid 
	 * @param datatype 类型
	 * @return
	 */
	public String getitemstr(String itemid,int datatype)
	{
		String itemstr= "";
		if("a00z1".equalsIgnoreCase(itemid)|| "sl".equalsIgnoreCase(itemid)|| "basedata".equalsIgnoreCase(itemid)|| "sskcs".equalsIgnoreCase(itemid)
				|| "ljse".equalsIgnoreCase(itemid)|| "lj_basedata".equalsIgnoreCase(itemid)|| "ljsde".equalsIgnoreCase(itemid))
		{
			itemstr="max("+itemid+") as "+itemid;
		}
		else if("ynse".equalsIgnoreCase(itemid)||datatype==DataType.FLOAT||datatype==DataType.INT||datatype==DataType.DOUBLE)
		{
				itemstr="sum("+itemid+") as "+itemid;
		}			
		else
		{
//			if(datatype==10){//日期类型 2016-11-18 zhanghua 24370
//				itemstr="max("+Sql_switcher.dateToChar(itemid,"YYYY-MM-DD")+") as "+itemid;
//			}else{
//				itemstr="max("+itemid+") as "+itemid;
//			}
			itemstr="max("+itemid+") as "+itemid;
		}
		return itemstr;
	}

	/**
	 * 导出申报汇总表
	 * @param fromtable 表名
	 * @param datetime 时间
	 * @return
	 */
	public String exportCount(String fromtable, String datetime,String salaryid, String taxMode) {
		ExportExcelUtil export = new ExportExcelUtil(conn,this.userView);
		String fileName = this.userView.getUserName()+"_gz.xls";
		String sheetName = "个税申报总表";
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		HashMap colStyleMap = new HashMap();
		LazyDynaBean bean = new LazyDynaBean(); 
		bean.set("content", "所得项目");// 列头名称:主键标识串，必选
        bean.set("itemid", "codeitemdesc");// 列头代码，必须与sql语句的查询字段一一对应，必?选；
        bean.set("comment", "codeitemdesc");// 列头注释:主键标识串，可?选；
        bean.set("colType", "A");// 该列数据类型 ，A是字符型，D是日期型，N是数字型，可选，默认是A；
        colStyleMap.put("align",HorizontalAlignment.RIGHT);
        HashMap headStyleMap = new HashMap();
        headStyleMap.put("columnWidth", 5000);
        headStyleMap.put("fontSize", 12);
        headStyleMap.put("fontName", "黑体");
        bean.set("colStyleMap", colStyleMap);
        bean.set("headStyleMap", headStyleMap);
        headList.add(bean);
		bean = new LazyDynaBean(); 
        bean.set("content", "税率");
        bean.set("itemid", "sl2");
        bean.set("comment", "sl");
        bean.set("colType", "A");
        colStyleMap = new HashMap<String, Short>();
        colStyleMap.put("align",HorizontalAlignment.RIGHT);
        bean.set("colStyleMap", colStyleMap);
        headStyleMap = new HashMap();
        headStyleMap.put("columnWidth", 2000);
        headStyleMap.put("fontSize", 12);
        headStyleMap.put("fontName", "黑体");
        bean.set("colStyleMap", colStyleMap);
        bean.set("headStyleMap", headStyleMap);
        headList.add(bean);
        bean = new LazyDynaBean();
        bean.set("content", "人数");
        bean.set("itemid", "rs");
        bean.set("comment", "rs");
        bean.set("colType", "N");
        headStyleMap = new HashMap();
        headStyleMap.put("columnWidth", 2000);
        headStyleMap.put("fontSize", 12);
        headStyleMap.put("fontName", "黑体");
        bean.set("headStyleMap", headStyleMap);
        headList.add(bean);
        bean = new LazyDynaBean(); 
        bean.set("content", "计税金额");
        bean.set("itemid", "ynse");
        bean.set("comment", "ynse");
        bean.set("colType", "N");
        bean.set("decwidth", "2");
        headStyleMap = new HashMap();
        headStyleMap.put("columnWidth", 4000);
        headStyleMap.put("fontSize", 12);
        headStyleMap.put("fontName", "黑体");
        bean.set("headStyleMap", headStyleMap);
        headList.add(bean);
        bean = new LazyDynaBean(); 
        bean.set("content", "纳税额");
        bean.set("itemid", "sds");
        bean.set("comment", "sds");
        bean.set("colType", "N");
        bean.set("decwidth", "2");
        headStyleMap = new HashMap();
        headStyleMap.put("columnWidth", 4000);
        headStyleMap.put("fontSize", 12);
        headStyleMap.put("fontName", "黑体");
        bean.set("headStyleMap", headStyleMap);
        headList.add(bean);
		HashMap dropDownMap = null;
		try {
			String timewhere = getFilterCond(datetime, taxMode);
			String sql = this.getoutsumtsql(fromtable,timewhere,salaryid);
			ArrayList dataList = this.getExportData(headList, sql);
			ArrayList mergedCellList = new ArrayList();
			bean = new LazyDynaBean(); 
			bean.set("content", sheetName);
			colStyleMap = new HashMap<String, Short>();
	        colStyleMap.put("align",Short.valueOf("2"));
	        colStyleMap.put("fontSize",18);
//	        colStyleMap.put("fontName","黑体");
//	        colStyleMap.put("isFontBold",true);
			bean.set("mergedCellStyleMap", colStyleMap);
			mergedCellList.add(bean);
			bean = new LazyDynaBean(); 
			String dtime= "all".equalsIgnoreCase(datetime)?"全部":datetime.replace(".", "年")+"月";
			bean.set("content", "时间："+dtime);
			bean.set("fromRowNum", 1);//从第几行开始，可选
			bean.set("toRowNum", 1);//从第几列开始，可选
			colStyleMap = new HashMap<String, Short>();
			colStyleMap.put("align",Short.valueOf("2"));
			colStyleMap.put("fontSize",12);
			bean.set("mergedCellStyleMap", colStyleMap);
			mergedCellList.add(bean);
			bean = new LazyDynaBean(); 
			SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
			bean.set("content", "制表时间："+df.format(new java.util.Date()));
			bean.set("fromRowNum", dataList.size()+5);
			bean.set("toRowNum", dataList.size()+5);
			bean.set("fromColNum", 3);
			bean.set("toColNum", 4);
			colStyleMap = new HashMap<String, Short>();
	        colStyleMap.put("border",Short.valueOf("-1"));
	        colStyleMap.put("fontSize",12);
			bean.set("mergedCellStyleMap", colStyleMap);
			mergedCellList.add(bean);
			export.exportExcel(fileName, sheetName, mergedCellList, headList, dataList, dropDownMap, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	/**
	 * 生成导出汇总表sql
	 * @param fromtable 表名
	 * @param timewhere 时间
	 * @return
	 */
	public String getoutsumtsql(String fromtable,String timewhere,String salaryid)
	{
		StringBuffer sqlsb = new StringBuffer();
		try
		{
			String pre=this.getPrivPre();
			int dbserver = Sql_switcher.searchDbServer();
	        sqlsb.append("select T.*,f.codeitemdesc");
	        sqlsb.append(","+Sql_switcher.numberToChar("SL")+" sl2 ");           
	        sqlsb.append(" from ((select sl,count(*) as rs,taxmode,");
	        sqlsb.append("sum(case when ynse>basedata then ynse-basedata else 0 end) as ynse,");
	        sqlsb.append("sum(sds) as sds ");
	        sqlsb.append(" from ");
	        sqlsb.append("(");
	        sqlsb.append("select "+Sql_switcher.sqlNull("TAXMODE", "0")+" as TAXMODE,A0100,");
	        sqlsb.append(Sql_switcher.dateToChar("declare_tax", "yyyy-mm"));
	        sqlsb.append(" as declare_tax,max("+Sql_switcher.isnull("SL","0")+" * 100) as SL,");		   
	        sqlsb.append("SUM(ABS("+Sql_switcher.sqlNull("ynse", "0")+")) as ynse,");
	        sqlsb.append("max("+Sql_switcher.isnull("basedata", "0")+") AS basedata,");
	        sqlsb.append("SUM(ABS("+Sql_switcher.isnull("sds", "0")+")) AS sds,");
	        sqlsb.append(Sql_switcher.dateToChar("tax_date", "yyyy-mm")+" as tax_date");
	        sqlsb.append(" from ");
	        sqlsb.append(fromtable);
	        sqlsb.append(" where ");
	        sqlsb.append(pre);
	        sqlsb.append(timewhere);
	        if(!"".equals(salaryid))
	        	sqlsb.append(" and SALARYID="+salaryid);
	        //根据页面的查询控制显示
			HashMap<String, String> map = getPageSql();
			String sql_ = (String)map.get("sql");
			if(StringUtils.isNotBlank(sql_)){
				sqlsb.append(" and " + sql_);
			}
			
	        sqlsb.append(" group by taxmode,nbase,a0100,declare_tax,tax_date) A");
			if(dbserver == Constant.ORACEL)
			{
				sqlsb.append(" group by rollup (taxmode, sl)");
			}
			else
			{			
				sqlsb.append(" group by  taxmode, sl with rollup");
			}
			sqlsb.append(") T left join (select codeitemdesc,codeitemid from codeitem where codesetid='46') f on f.codeitemid= T.taxmode");
			sqlsb.append(")");		
			sqlsb.append(" order by case when T.taxmode is null then 'z' else T.taxmode end,");
			sqlsb.append("case when T.sl is null then 99999 else T.sl end");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sqlsb.toString();
	}
	
	/**
	 * 获取个税明细表结构维护右侧指标
	 * @return
	 */
	public ArrayList<LazyDynaBean> getItems(){
		ArrayList<Field> fieldList = searchDynaItemList();
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		 LazyDynaBean ld = new LazyDynaBean();
//		String sql = "select itemid,itemdesc from salaryset where itemid = ?";
//		
//		ContentDAO dao = new ContentDAO(this.conn);
//		RowSet rs = null;
		try {
		for (Field field : fieldList) {
			ld.set("dataValue", field.getName());
			ld.set("dataName",  field.getLabel());
			itemList.add(ld);
			ld=new LazyDynaBean();
//			FieldItem item = new FieldItem();
//			ArrayList<String> list = new ArrayList<String>();
//			list.add(field.getName());
//				rs = dao.search(sql,list);
//				while(rs.next()){
//					ld.set("dataValue", rs.getString("itemid"));
//					ld.set("dataName", rs.getString("itemdesc"));
//					itemList.add(ld);
//					ld=new LazyDynaBean();
//				}
//			item.setItemid(field.getName());
//			item.setItemdesc(field.getLabel());
//			item.setCodesetid(field.getCodesetid());
//			item.setDecimalwidth(field.getDecimalDigits());
            
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		finally{
//			PubFunc.closeResource(rs);
//		}
		return itemList;
	}
	
	/***
	 * 取得是否支持按隶属部门进行所得税管理
	 * @return
	 */
	public String getDeptID()
	{
		String deptid="false";
		Document doc = this.getDoc();
		try
		{
			if(doc!=null)
			{
				String path ="/param";
				XPath xpath = XPath.newInstance(path);
				Element items = (Element)xpath.selectSingleNode(doc);
				if(items.getAttributeValue("deptid")!=null&&items.getAttributeValue("deptid").trim().length()>0)
				{
					deptid=items.getAttributeValue("deptid");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return deptid;
	}
	

	/**
	 * 获得指标类别 
	 * @return
	 */
	public ArrayList<HashMap<String,String>> getGzMxType(UserView userView)
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> hm = null;
		RowSet rs = null ;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String gztypeid = "";
			String gztypename = "";
			String sql = "select salaryid,Cname from salarytemplate where (CSTATE IS NULL OR CSTATE='') ";
			rs = dao.search(sql);
			while(rs.next())
			{
				if (!userView.isHaveResource(/*IResourceConstant.LAWRULE*/IResourceConstant.GZ_SET, rs.getString("salaryid")))
				{
					continue;
				}		
				hm = new HashMap<String,String>();
				gztypeid = rs.getString("salaryid");
				gztypename = rs.getString("Cname");
				hm.put("fieldsetid", rs.getString("salaryid"));
				hm.put("fieldsetdesc", rs.getString("Cname"));
				list.add(hm);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	/**
	 * 获得指标类别 
	 * 所得税管理页面设置使用
	 * 传入返回map的key
	 * @return
	 */
	public ArrayList<CommonData> getGzMxType(UserView userView,String dataValue,String dataName)
	{
		ArrayList<CommonData> list = new ArrayList<CommonData>();
		CommonData ld = new CommonData();
		RowSet rs = null ;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "select salaryid,Cname from salarytemplate where (CSTATE IS NULL OR CSTATE='') ";
			rs = dao.search(sql);
			while(rs.next())
			{
				if (!userView.isHaveResource(/*IResourceConstant.LAWRULE*/IResourceConstant.GZ_SET, rs.getString("salaryid")))
				{
					continue;
				}		
				ld = new CommonData(rs.getString("salaryid"),rs.getString("Cname"));
				list.add(ld);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 获取指标项
	 * @param value
	 * @return
	 */
	public ArrayList getGzMxprolist(String value){
		ArrayList list = new ArrayList();
		if("sysRecruit_item".equals(value))
		{
			HashMap<String,String> hm = new HashMap<String,String>();
			hm.put("itemid", "contactPerson");
			hm.put("itemdesc", "联系人");
			hm.put("itemtype", "A");
			hm.put("fieldsetid", "sys");
			hm.put("codesetid", "0");
			list.add(hm);
			
			hm = new HashMap<String,String>();
			hm.put("itemid", "phoneNo");
			hm.put("itemdesc", "联系人电话");
			hm.put("itemtype", "A");
			hm.put("fieldsetid", "sys");
			hm.put("codesetid", "0");
			list.add(hm);

			hm = new HashMap<String,String>();
			hm.put("itemid", "humanDepartment");
			hm.put("itemdesc", "联系人部门");
			hm.put("itemtype", "A");
			hm.put("fieldsetid", "sys");
			hm.put("codesetid", "0");
			list.add(hm);
			
			hm = new HashMap<String,String>();
			hm.put("itemid", "company");
			hm.put("itemdesc", "联系人单位");
			hm.put("itemtype", "A");
			hm.put("fieldsetid", "sys");
			hm.put("codesetid", "0");
			list.add(hm);
			
			hm = new HashMap<String,String>();
			hm.put("itemid", "sendDate");
			hm.put("itemdesc", "发件日期");
			hm.put("itemtype", "D");
			hm.put("fieldsetid", "sys");
			hm.put("codesetid", "0");
			list.add(hm);

		}else{
			RowSet rs = null;//2016-6-29 更换selectField.js控件，修改数据结构。
			try {
				ContentDAO dao = new ContentDAO(this.conn);
				ArrayList datalist = new ArrayList();
				datalist.add(value);
				//排除固定字段 itemid not in
				StringBuffer sql=new StringBuffer("select itemid,itemdesc,itemtype,fieldsetid from salaryset where itemid not in ('NBASE','A0100','tax_date','A00Z0','A00Z1','tax_max_id','salaryid','A0000','B0110','E0122','A0101') and salaryid =?");
				sql.append(" order by fieldid");
				rs=dao.search(sql.toString(),datalist);
				FieldItem item =DataDictionary.getFieldItem("e01a1");

				while(rs.next())
				{
					if(DataDictionary.getFieldItem(rs.getString(1).toLowerCase())!=null)
					{
//						if("1".equals(type)){
						    if("M".equals(rs.getString("itemtype")))
						    	 continue;
//						}
						if(!"0".equals(this.userView.analyseFieldPriv(rs.getString(1))))
							list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc").replaceAll("\r\n", "")));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(rs);
			}
		}
		return list;
	}
	/**
	 * 更新个税明细表字段
	 * @param field
	 * @throws JDOMException
	 */
	public void updateTaxMxField(String[] field,String deptid) throws JDOMException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String retstr = "";
		RecordVo ctrlvo = this.getRecordVo();
		if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
		{
			Document doc = this.getDoc();
			String parentpath = "/param/fields";
			Element fields = this.getSingleNode(doc,parentpath);
			String itemspath = "/param/items";
			Element items = this.getSingleNode(doc,itemspath); 
			StringBuffer itemssb =new StringBuffer();
			ArrayList columnlist = new  ArrayList();
			StringBuffer formats=new StringBuffer();	
			formats.append("############");	
			if(field!=null && field.length>0)
			{
				for(int t=0;t<field.length;t++)
				{
					String fieldtimeid = field[t].toString();	
					if(!("".equals(fieldtimeid)))
					{
						FieldItem fi=DataDictionary.getFieldItem(fieldtimeid);
						boolean flag = this.checkfield(fieldtimeid);
						//  如果是常量表中没有的指标
						//  添加指标
						if(!flag)
						{
							Element fieldelement  = new Element("field");
							fieldelement.setAttribute("id",fieldtimeid);
							fieldelement.setAttribute("visible","true");
							if(fi!=null)
							{
								fieldelement.setAttribute("width","80");
								fieldelement.setAttribute("title",fi.getItemdesc());
								Field ft=this.getField(false,fieldtimeid,fi.getItemdesc(),fi.getItemtype(),fi.getItemlength(),fi.getDecimalwidth());
								columnlist.add(ft);
							}
							else  // 如果是固定指标
							{
								//个税明细表获取固定字段
						        ArrayList<Field> fieldlist = searchCommonItemList();
						        //个税明细表动态维护的指标
						        ArrayList<Field> chglist = searchDynaItemList();
						        if(chglist.size()>0){
						        	fieldlist.addAll(chglist);
						        }
								for(int x=0;x<fieldlist.size();x++)
								{
									Field addfi = (Field)fieldlist.get(x);
									if(addfi.getName().equalsIgnoreCase(fieldtimeid))
									{
										fieldelement.setAttribute("width","80");
										fieldelement.setAttribute("title",addfi.getLabel());
										Field ft=this.getField(false,fieldtimeid,addfi.getLabel(),this.getvarType(addfi.getDataType()),addfi.getLength(),addfi.getDecimalDigits());
										columnlist.add(ft);
										break;
									}
								}								
							}
							fields.addContent(fieldelement);
							itemssb.append(","+fieldtimeid);
						}
					}					
				}
				if("".equals(items.getText()))
				{
					if(itemssb==null || "".equals(itemssb.toString()))
					{
						items.setText("");
					}else{
						items.setText(itemssb.substring(1).toString());
					}
				}else{
					items.setText(items.getText()+itemssb.toString());
				}
				String paramspath="/param";
				Element params = this.getSingleNode(doc,paramspath);
				if(params!=null)
				{
					params.setAttribute("deptid", deptid);
				}
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				retstr = outputter.outputString(doc);
				ArrayList alist = new ArrayList();
				alist.add(retstr);
				String sql = " update constant set str_value = ? where constant = 'GZ_TAX_MX'";	
				try
				{
					dao.update(sql,alist);
					this.create_update_Table("GZ_TAX_MX",columnlist,false,this.conn);
					if(!(itemssb==null || "".equals(itemssb.toString())))
					{
						this.updateGzTaxMx(itemssb.substring(1).toString(),dao);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			this.deleteTaxMxField(field);
		}
		else
		{
			this.createXml();
			this.updateTaxMxField(field,deptid);
		}
	}
    
	/**查询常量表中的GZ_TAX_MX
	 * @return  RecordVo
	 */
	public RecordVo getRecordVo()
	{
		RecordVo ctrlvo = null;
		try
		{
			ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ctrlvo;
	}
	/**获得Document对象
	 * @return  Document
	 */
	public Document getDoc()
	{
		Document doc=null;
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", this.conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				doc = PubFunc.generateDom(ctrlvo.getString("str_value"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return doc;
	}
	/**
	 * 获得单个结点
	 * @param doc
	 * @param path
	 * @return
	 */
	public Element getSingleNode(Document doc,String path)
	{
		Element fields= null;
		try
		{
			XPath xpath = XPath.newInstance(path);
			fields =  (Element)xpath.selectSingleNode(doc);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields;
	}
	/**
	 * 获得Element
	 * @param fieldtimeid
	 * @return
	 */
	public boolean checkfield (String fieldtimeid)
	{ 
		Document doc = null;
		boolean flag = false;
		try
		{
			doc = this.getDoc();
			String findpath = "/param/fields/field";
			XPath xPath = XPath.newInstance(findpath);
			List list = xPath.selectNodes(doc);
			for(Iterator it=list.iterator();it.hasNext();)
			{
				Element field = (Element)it.next();
				String temp = field.getAttributeValue("id");
				if(temp.equalsIgnoreCase(fieldtimeid))
				{
					flag = true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	public String getvarType(int varType)
	{
		String type="";
		if(varType==DataType.DATE)
			type="D";
		else if(varType==DataType.STRING)
			type="A";
		else if(varType==DataType.INT||varType==DataType.FLOAT)
			type="N";
		else if(varType==DataType.CLOB)
			type="M";
		else
			type="A";
		return type;
	}
	
	/**
	 * 从薪资历史表中导入数据，更新个税明细表
	 * @param fields
	 */
	public void updateGzTaxMx(String fieldstr,ContentDAO dao)
	{
		String[] fields = null;	
		try
		{
			int tempnum = fieldstr.split(",").length;
			if(tempnum>0)
			{
				fields = fieldstr.split(",");
				if(fields!=null && fields.length>0)
				{
					for(int t=0;t<fields.length;t++)
					{
						StringBuffer sqlsb = new StringBuffer();
						String field = fields[t].toString();
						sqlsb.append("update gz_tax_mx set "+field+"=( ");
						sqlsb.append("select "+field+" from salaryhistory t");
						sqlsb.append(" where gz_tax_mx.NBASE =t.NBASE ");
						sqlsb.append(" and gz_tax_mx.A0100 = t.A0100 ");
						sqlsb.append(" and gz_tax_mx.SalaryId = t.SalaryId ");
						sqlsb.append(" and  gz_tax_mx.A00Z0 = t.A00Z0");
						sqlsb.append(" and gz_tax_mx.A00Z1 = t.A00Z1 )");
						dao.update(sqlsb.toString());
					}
				}
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 删除个税明细表字段
	 * @param addfield
	 * @throws JDOMException
	 */
	public void deleteTaxMxField(String[] field) throws JDOMException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String retstr = "";
		RecordVo ctrlvo = this.getRecordVo();
		if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
		{
			Document doc = this.getDoc();
			String itemspath = "/param/items";
			XPath ixpath = XPath.newInstance(itemspath);
			Element items =  (Element)ixpath.selectSingleNode(doc);
			String fieldspath = "/param/fields/field";
			XPath fxpath = XPath.newInstance(fieldspath);
			List list =  fxpath.selectNodes(doc);
			String timestr = "";
			StringBuffer fieldsb = new StringBuffer();			
			ArrayList columnlist = new  ArrayList();
			if(items!=null )
			{
				timestr = items.getText();
				
					String[] xmlitems = timestr.split(",");
					for(int i=0;i<xmlitems.length;i++)
					{
						String xmlitemstr = xmlitems[i].toString();// items里面的指标
						if(field==null || "".equalsIgnoreCase(field[0]))  // 如果传回的数组为空，就表示删除全部非固定指标
						{
							for(Iterator it=list.iterator();it.hasNext();)
							{
								Element removetemp =(Element)it.next();// field里的指标
								String temp = removetemp.getAttributeValue("id");
								// items里面有，field里也有的
								if(temp.equalsIgnoreCase(xmlitemstr) && !("a0100".equalsIgnoreCase(temp))
										&& !("a0000".equalsIgnoreCase(temp))){
									removetemp.detach();
									columnlist.add(this.getField(xmlitemstr));	
									break;
								}
							}
						}
						else
						{
							boolean flag = this.checkfields(xmlitemstr,field);
							if(flag==false && !("a0100".equalsIgnoreCase(xmlitemstr))
									&& !("a0000".equalsIgnoreCase(xmlitemstr)))// 删除
							{
								for(Iterator it=list.iterator();it.hasNext();)
								{
									Element removetemp =(Element)it.next();
									String temp = removetemp.getAttributeValue("id");
									
									if(temp.equalsIgnoreCase(xmlitemstr))
									{
										removetemp.detach();
										columnlist.add(this.getField(xmlitemstr));	
										break;
									}
								}
							}
							else
							{
								fieldsb.append(","+xmlitemstr);
							}
						}						
					}
												
			}			
			if(fieldsb==null || "".equals(fieldsb.toString()))
			{
				items.setText("");
			}
			else
			{
				items.setText(fieldsb.substring(1).toString());
			}			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			retstr = outputter.outputString(doc);
			ArrayList alist = new ArrayList();
			alist.add(retstr);
			String sql = " update constant set str_value = ? where constant = 'GZ_TAX_MX'";			
			try
			{
				dao.update(sql,alist);
				this.create_update_Table("GZ_TAX_MX",columnlist,true,this.conn);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 * 获得要修改的字段信息
	 * @param xmlitem
	 * @return
	 */
	public Field getField(String xmlitem)
	{
		FieldItem fi=DataDictionary.getFieldItem(xmlitem);
		Field ft = null;
		if(fi!=null)
		{									
			 ft =this.getField(false,xmlitem,fi.getItemdesc(),fi.getItemtype(),fi.getItemlength(),fi.getDecimalwidth());			
		}
		else
		{
			//个税明细表获取固定字段
	        ArrayList<Field> fieldlist = searchCommonItemList();
	        //个税明细表动态维护的指标
	        ArrayList<Field> chglist = searchDynaItemList();
	        if(chglist.size()>0){
	        	fieldlist.addAll(chglist);
	        }
			for(int x=0;x<fieldlist.size();x++)
			{
				Field addfi = (Field)fieldlist.get(x);
				if(addfi.getName().equalsIgnoreCase(xmlitem))
				{
					ft=this.getField(false,xmlitem,addfi.getLabel(),this.getvarType(addfi.getDatatype()),addfi.getLength(),addfi.getDecimalDigits());
				}
			}
		}
		return ft;
	}
	
	/**
	 * 判断删除的指标,为true的不要删除 
	 * @param xmlitems
	 * @param fields
	 * @return
	 */
	public boolean checkfields(String xmlitems,String[] fields)
	{
		boolean flag = false;
		if(fields!=null && fields.length>0)
		{
			for(int i=0;i<fields.length;i++)
			{
				String field = fields[i].toString();
				if(field.equalsIgnoreCase(xmlitems))
				{
					flag = true;  // 如果传回的数组里有的，item里面也有的，就不要删除，falg为true
				}			
			}
		}
		return flag;
	}
	
	/**
	 * 创建新XML
	 */
	public void createXml()
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String xmlstr = "";
		Element param = new Element("param");
		Element items = new Element("items");
		Element fields = new Element("fields");
		ArrayList fieldlist = this.searchCommonItemList();
		for(Iterator it=fieldlist.iterator();it.hasNext();)
		{
			Element field = new Element("field");
			Field fi = (Field)it.next();
			String id = fi.getName();
			String visible = fi.isVisible()+"";
			String width = "80";
			String title = fi.getLabel();
			field.setAttribute("id",id);
			field.setAttribute("width",width);
			field.setAttribute("visible",visible);
			field.setAttribute("title",title);
			fields.addContent(field);
		}
		param.setAttribute("deptid","false");
		param.addContent(items);
		param.addContent(fields);
		Document doc = new Document(param);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		xmlstr = outputter.outputString(doc);
		String sql = "update constant set str_value = '"+xmlstr+"' where constant like 'gz_tax_mx' ";
		try
		{
			dao.update(sql);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 创建 或 更新 表
	 * @param tableName
	 * @param fieldList
	 * @param flag
	 * @param con
	 */
	public void create_update_Table(String tableName,ArrayList fieldList,boolean flag,Connection con)
	{
		
		try
		{
			DbWizard dbWizard = new DbWizard(con);
			DBMetaModel dbmodel=new DBMetaModel(con);
			Table table=new Table(tableName);
			if(!dbWizard.isExistTable(tableName,false))
			{
				
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field field=(Field)t.next();
					table.addField(field);
				}
				dbWizard.createTable(table);
				
				dbmodel.reloadTableModel(tableName);
				flag=false;
			}
			else
			{
				analyseTableStructure(fieldList,tableName,flag,dbWizard,dbmodel,con);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 分析表结构，进行动态更新
	 * @param new_fieldList
	 * @param tableName
	 * @param flag
	 * @param dbWizard
	 * @param dbmodel
	 */	
	public void analyseTableStructure(ArrayList new_fieldList,String tableName,boolean flag,DbWizard dbWizard,DBMetaModel dbmodel,Connection con)
	{
		Table table=new Table(tableName);
		try
		{
			String exist_tableNames=getExistColumnNameStr(tableName,con);
			exist_tableNames=exist_tableNames.toLowerCase();
			StringBuffer new_tableNames=new StringBuffer();
			int i=0;
			for(Iterator t=new_fieldList.iterator();t.hasNext();)
			{
				Field aField=(Field)t.next();
				String columnName=aField.getName();
				new_tableNames.append(columnName.toLowerCase()+" , ");
				if(exist_tableNames.indexOf(columnName.toLowerCase()+",")==-1)
				{
					i++;
					table.addField(aField);
				}
			}
			if(i>0)
			{
				dbWizard.addColumns(table);
			}
			table=new Table(tableName);
			if(flag)
			{
				i=0;
				String[] str_arr=exist_tableNames.split(",");
				for(int a=0;a<str_arr.length;a++)
				{
					if(new_tableNames.indexOf(str_arr[a].toLowerCase())!=-1)
					{
						i++;
						Field obj=getField(false,str_arr[a],str_arr[a],"N",15,4);
						table.addField(obj);
						
					}
				}
				if(i>0)
				{
					dbWizard.dropColumns(table);
				}
			}
			dbmodel.reloadTableModel(tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
//	得到表的列名字符串
	public String getExistColumnNameStr(String tableName,Connection con)
	{
		
		String sql="select * from "+tableName+" where 1=2";
		StringBuffer names=new StringBuffer("");
		RowSet recset = null;
		try
		{
			ContentDAO dao=new ContentDAO(con);
			recset=dao.search(sql);
			ResultSetMetaData metaData=recset.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				names.append(metaData.getColumnName(i)+",");
			}
			if(metaData!=null)
				metaData=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(recset);
		}
		return names.toString();
	}
	
	/**
	 * 
	 * @param primaryKey	是否是主键
	 * @param fieldname     列名
	 * @param fieldDesc     列描述
	 * @param type          数据类型
	 * @param length        长度
	 * @param decimalLength 小数点位数
	 * @return
	 */
	public Field getField(boolean primaryKey,String fieldname,String fieldDesc,String type,int length,int decimalLength)
	{
		Field obj=new Field(fieldname,fieldDesc);
		if("A".equals(type))
		{	
			obj.setDatatype(DataType.STRING);
			obj.setKeyable(primaryKey);	
			if(primaryKey)
				obj.setNullable(false);
			else 
				obj.setNullable(true);
			obj.setVisible(true);
			obj.setLength(length);
			
		}
		else if("M".equals(type))
		{
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);			
			obj.setVisible(true);
			obj.setAlign("left");					
		}
		else if("D".equals(type))
		{
			
			obj.setDatatype(DataType.DATE);
			obj.setKeyable(false);			
			obj.setVisible(true);												
		}	
		else if("N".equals(type))
		{
			obj.setDatatype(DataType.FLOAT);
			obj.setDecimalDigits(decimalLength);
			obj.setLength(length);							
			obj.setKeyable(primaryKey);		
			if(primaryKey)
				obj.setNullable(false);
			else 
				obj.setNullable(true);
			obj.setVisible(true);								
		}	
		else if("I".equals(type))
		{		
			obj.setDatatype(DataType.INT);
			obj.setKeyable(primaryKey);		
			if(primaryKey)
				obj.setNullable(false);
			else 
				obj.setNullable(true);
			obj.setVisible(true);	
		}		
		return obj;
	}
	/**
	 * 获取数据
	 * @param headList
	 * @param sql
	 * @return 
	 * @throws SQLException
	 */
	public ArrayList getExportData(ArrayList<LazyDynaBean> headList,String sql) throws SQLException{
		ContentDAO dao=new ContentDAO(this.conn);
		LazyDynaBean rowDataBean = null;
		LazyDynaBean dataBean = null;
		LazyDynaBean bean = new LazyDynaBean();
		Date d = null;
		RowSet rowSet = null;
		ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
		try {
			String itemid = "";
			String itemtype = "";
			String codesetid = "";
			int decwidth = 0;
			String dateFormat = "";
			SimpleDateFormat df = null;
			if(StringUtils.isBlank(sql))
			    return null;
			rowSet = dao.search(sql);
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";		
			
			while (rowSet.next()) {
				rowDataBean = new LazyDynaBean();
				for (int i = 0; i < headList.size(); i++) {
					dataBean=new LazyDynaBean();
					bean = (LazyDynaBean) headList.get(i);
					itemid = (String) bean.get("itemid");
					itemtype = (String) bean.get("colType");
					codesetid = (String) bean.get("codesetid");// 代码类id
					if(bean.get("decwidth") != null)
						decwidth = Integer.parseInt((String)bean.get("decwidth"));
					dateFormat = (String) bean.get("dateFormat");
					if (StringUtils.isEmpty(codesetid))
						codesetid = "0";
					
					if ("D".equals(itemtype)) {
						//日期型
						if (StringUtils.isEmpty(dateFormat))
							df = new SimpleDateFormat("yyyy-MM-dd");
						else
							df = new SimpleDateFormat(dateFormat);
						d = null;
						d = rowSet.getDate(itemid);
						if (d != null)
							dataBean.set("content", df.format(d));
						else
							dataBean.set("content", "");
						rowDataBean.set(itemid, dataBean);
					} else if ("A".equals(itemtype) && "0".equals(codesetid)) {
						if(rowSet.isLast()&&"codeitemdesc".equalsIgnoreCase(itemid)){
							dataBean.set("content", "合计");
						}else{
							DecimalFormat dformat = new DecimalFormat("0.0");
							String itemidR = rowSet.getString(itemid);
							if("sl2".equals(itemid)&&!rowSet.isLast()){
								dataBean.set("content", itemidR==null||itemidR==""?"小计":dformat.format(Double.parseDouble(itemidR))+"%");
							}else{
								dataBean.set("content", itemidR==null?"":itemidR+"%");
							}
						}
						rowDataBean.set(itemid, dataBean);
						
					} else if ("N".equals(itemtype)) {
						//数字型
						if (rowSet.getString(itemid) != null) {
							dataBean.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
						} else
							dataBean.set("content", "");
						rowDataBean.set(itemid, dataBean);
					} else {
						if (rowSet.getString(itemid) != null)
							dataBean.set("content", rowSet.getString(itemid));
						else
							dataBean.set("content", "");
						rowDataBean.set(itemid, dataBean);
					}
				}
				dataList.add(rowDataBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return dataList;
	}
	 /**
     * @author wangjl
     * @Description: 根据查询内容生成sql条件语句
     * @date 2016-4-11
     * @param valuesList
     *            查询内容
     * @return
     */
    public String getCondition(ArrayList<String> valuesList) {
        StringBuffer buf = new StringBuffer("");
        try {
        	buf.append(" and (");
            // 快速查询
            for (int i = 0; i < valuesList.size(); i++) {
                String queryVal = valuesList.get(i);
                queryVal = SafeCode.decode(queryVal);
                if(i==0){
                	buf.append(" myGridData.a0101 like ");
                }else{
                	buf.append(" or myGridData.a0101 like ");
                }
                buf.append("'%" + queryVal + "%'");
            }
            buf.append(" )");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
        return buf.toString();
    }

        /**
         * @param tablename2 要更新的数据所在的表
         * @param dataList 所有数据
         * @param datafields 要更新的字段
         */
        public String updateTaxData(String tablename2, ArrayList dataList, ArrayList<String> datafields) {
            try {
                ContentDAO dao=new ContentDAO(this.conn);
                for (int i=0;i<dataList.size();i++) {
                    DynaBean bean = (DynaBean) dataList.get(i);
                    HashMap map = PubFunc.DynaBean2Map(bean);
                    String tax_max_id = (String) map.get("tax_max_id_e");
                    tax_max_id = PubFunc.decrypt(tax_max_id);
                    StringBuffer buf = new StringBuffer();
                    buf.append(" update "+ tablename2+" set ");
		      //个税明细表获取固定字段
		        ArrayList<Field> fieldlist = searchCommonItemList();
		        //个税明细表动态维护的指标
		        ArrayList<Field> chglist = searchDynaItemList();
		        if(chglist.size()>0){
		        	fieldlist.addAll(chglist);
		        }
		        fieldlist = this.sortFieldList(fieldlist,true);
		        ArrayList valueList = new ArrayList();
				for(int j=0;j<fieldlist.size();j++)
				{
					Field field=(Field)fieldlist.get(j);
					int itemtype=field.getDatatype();
					String fieldName = field.getName().toLowerCase();
					String eFieldName=fieldName;
					if(!map.containsKey(fieldName))
						continue;
					if(!field.isVisible()){
						eFieldName = fieldName+"_e";
					}
					String fieldValue = map.get(eFieldName)==null?"":map.get(eFieldName).toString();
					if(!eFieldName.equals(fieldName)&&!"".equals(fieldValue)){
						fieldValue= PubFunc.decrypt(fieldValue);
						//fieldName = "a0100";
					}
					String[] split = fieldValue.split("`");
					fieldValue=split.length==0?"":split[0];
					if(!"tax_max_id_e".equals(fieldName))
					{
						if(itemtype==DataType.DATE)
						{
							buf.append(fieldName+"=?");
							buf.append(",");
						}else if(itemtype==DataType.INT||itemtype==DataType.FLOAT||itemtype==DataType.DOUBLE)
						{
							buf.append(fieldName+"=?");
							buf.append(",");
						}else
						{
							buf.append(fieldName+"=?");
							buf.append(",");
						}
						if(itemtype==DataType.DATE&&StringUtils.isNotEmpty(fieldValue))
						{
							Date sqlDate = DateUtils.getSqlDate(fieldValue, "yyyy-MM-dd");
							valueList.add(sqlDate);
						}else{
							valueList.add(fieldValue);
						}
					}
				}
				valueList.add(tax_max_id);
				buf.setLength(buf.length()-1);
				buf.append(" where tax_max_id=?");
				dao.update(buf.toString(),valueList);
				buf.setLength(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
