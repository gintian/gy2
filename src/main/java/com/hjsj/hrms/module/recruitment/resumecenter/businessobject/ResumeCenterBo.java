package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title:ResumeCenterBo</p>
 * <p>Description:简历中心类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-01-22</p>
 * @author wangcq
 * @version 1.0
 * 
 */
public class ResumeCenterBo {
	private Connection conn;
	/**应聘情况集合（可扩展）   key为对应区域   value为查询内容
    （status; //简历状态  0：未处理（新简历） 1：猎头招聘  2：内部招聘
     create_time;  //应聘时间  0:最近一周   1：最近两周   2：最近一月*/
	private HashMap schemeMap = new HashMap();    
	private UserView userview;
	private String fromModule="resumeCenter"; // resumeCenter：简历中心 talents:人才库
	private ContentDAO dao;
	public ResumeCenterBo(){}
	
	public ResumeCenterBo(Connection conn,UserView userview,String fromModule){
		this.conn = conn;
		this.userview=userview;
		this.fromModule=fromModule;
		dao = new ContentDAO(conn);
	}
	
	/**
	 * 获取默认查询指标
	 * @return
	 * @throws GeneralException 
	 */
	public String getDefaultQuery(String from) {
		ArrayList<HashMap<String,String>> defaultQuery = new ArrayList<HashMap<String,String>>();
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
		
			RecruitUtilsBo bo = new RecruitUtilsBo(conn);
			
			//查询模板预置
	        HashMap<String,String> hm = new HashMap<String,String>();
	        FieldItem fieldItem = null;
	        if("resumeCenter".equals(from)){
	        	hm = new HashMap<String,String>();
	        	hm.put("fieldsetid", "Z03");
	        	hm.put("itemid", "Z0325");
	        	hm.put("itemdesc", "需求部门");
	        	hm.put("itemtype", "A");
	        	hm.put("codesetid", "UM");
	        	defaultQuery.add(hm);
	        	
	        	hm = new HashMap<String,String>();
	        	hm.put("fieldsetid", "Z03");
	        	hm.put("itemid", "Z0351");
	        	hm.put("itemdesc", "职位名称");
	        	hm.put("itemtype", "A");
	        	hm.put("codesetid", "0");
	        	defaultQuery.add(hm);
	        	fieldItem = bo.getFieldItem(dbname, "A04", "A0410");
	        	if(fieldItem!=null){
			        hm = new HashMap<String,String>();
			        hm.put("fieldsetid", "A04");
			        hm.put("itemid", "A0410");
			        hm.put("itemdesc", fieldItem.getItemdesc());
			        hm.put("itemtype", fieldItem.getItemtype());
			        hm.put("codesetid", fieldItem.getCodesetid());
		        	defaultQuery.add(hm);
	        	}
	        	fieldItem = bo.getFieldItem(dbname, "A04", "A0405");
	        	if(fieldItem!=null){
			        hm = new HashMap<String,String>();
			        hm.put("fieldsetid", "A04");
			        hm.put("itemid", "A0405");
			        hm.put("itemdesc", fieldItem.getItemdesc());
			        hm.put("itemtype", fieldItem.getItemtype());
			        hm.put("codesetid", fieldItem.getCodesetid());
		        	defaultQuery.add(hm);
	        	}
		        
		        hm = new HashMap<String,String>();
		        hm.put("fieldsetid", "A01");
		        hm.put("itemid", "A0107");
		        hm.put("itemdesc", "性别");
		        hm.put("itemtype", "A");
		        hm.put("codesetid", "AX");
		        defaultQuery.add(hm);
		        
		        hm = new HashMap<String,String>();
		        hm.put("fieldsetid", "A01");
		        hm.put("itemid", "A0111");
		        hm.put("itemdesc", "出生日期");
		        hm.put("itemtype", "D");
		        hm.put("formatlength", "10");
		        defaultQuery.add(hm);
		        hm = new HashMap<String,String>();
		        hm.put("itemid", "Z0103");
		        hm.put("itemdesc", "招聘批次");
		        hm.put("itemtype", "A");
		        hm.put("codesetid", "0");
		        defaultQuery.add(hm);
	        }else if("talents".equals(from)){
	        	fieldItem = bo.getFieldItem(dbname, "A04", "A0410");
	        	if(fieldItem!=null){
			        hm = new HashMap<String,String>();
			        hm.put("fieldsetid", "A04");
			        hm.put("itemid", "A0410");
			        hm.put("itemdesc", fieldItem.getItemdesc());
			        hm.put("itemtype", fieldItem.getItemtype());
			        hm.put("codesetid", fieldItem.getCodesetid());
		        	defaultQuery.add(hm);
	        	}
	        	fieldItem = bo.getFieldItem(dbname, "A04", "A0405");
	        	if(fieldItem!=null){
			        hm = new HashMap<String,String>();
			        hm.put("fieldsetid", "A04");
			        hm.put("itemid", "A0405");
			        hm.put("itemdesc", fieldItem.getItemdesc());
			        hm.put("itemtype", fieldItem.getItemtype());
			        hm.put("codesetid", fieldItem.getCodesetid());
		        	defaultQuery.add(hm);
	        	}
	 	        
	 	        hm = new HashMap<String,String>();
	 	        hm.put("fieldsetid", "A01");
	 	        hm.put("itemid", "A0107");
	 	        hm.put("itemdesc", "性别");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "AX");
	 	        defaultQuery.add(hm);
	        }
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return JSON.toString(defaultQuery);
	}
	/**
	 * 获取预置查询指标
	 * @return
	 */
	public String getOptionalQuery(String from){
		ArrayList<HashMap<String,String>> optionalQuery = new ArrayList<HashMap<String,String>>();
		//查询模板预置
		HashMap<String,String> hm = new HashMap<String,String>();
		ArrayList<FieldItem> fieldList = this.getFieldList(from);
		for (FieldItem fieldItem : fieldList) {
			if(StringUtils.isNotEmpty(fieldItem.getItemdesc())){
				hm = new HashMap<String,String>();
	 	        hm.put("fieldsetid", fieldItem.getFieldsetid());
	 	        hm.put("itemid", fieldItem.getItemid());
	 	        hm.put("itemdesc", fieldItem.getItemdesc());
	 	        hm.put("itemtype", fieldItem.getItemtype());
	 	        hm.put("codesetid", fieldItem.getCodesetid());
	 	        optionalQuery.add(hm);
			}
		}
		return JSON.toString(optionalQuery);
	}
	 /**
     * 获取招聘批次
     * @return
     */
    public String getBatchQuery(){
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet search = null;
    	ArrayList<HashMap<String,String>> batchQuery = new ArrayList<HashMap<String,String>>();
    	HashMap<String,String> hm = new HashMap<String,String>();
    	StringBuffer sql = new StringBuffer();
    	sql.append("select z0101,z0103 from z01");
    	sql.append(" where z0129<>'01'");
    	RecruitPrivBo rpb = new RecruitPrivBo();
		try {
			sql.append(" and ").append(rpb.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD));
			sql.append(" order by Z0107 desc ");
			search = dao.search(sql.toString());
			while(search.next()){
				hm = new HashMap<String,String>();
				hm.put("codeitemid", "custom_"+search.getString("z0101"));
				hm.put("codeitemdesc", search.getString("z0103"));
				batchQuery.add(hm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return JSON.toString(batchQuery);
    }	
	
	
	/**
	 * 获取相应字段内容，并放入list
	 * @param from 模块标志
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldList(String from) {
		ArrayList columnList = new ArrayList();
		try {
			ArrayList fieldList = new ArrayList();
			String subModuleId = "";
			if("resumeCenter".equalsIgnoreCase(from))
			{			
				subModuleId = "zp_resume_191130_00001";
			}else if("talents".equalsIgnoreCase(from)){
				subModuleId = "zp_talent_191130_00001";
			}
			TableFactoryBO tableBo = new TableFactoryBO(subModuleId, this.userview, conn);
			HashMap scheme = tableBo.getTableLayoutConfig();
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
				
			fieldList.add("z0301"+"`"+"Z03");
        	fieldList.add("z0319"+"`"+"Z03");
        	fieldList.add("z0381"+"`"+"Z03");
			RecruitUtilsBo bo = new RecruitUtilsBo(conn);
			if(scheme!=null)
	        {
	        	Integer scheme_str = (Integer)scheme.get("schemeId");
	        	int schemeId = scheme_str.intValue();;
	        	ArrayList columnConfigLst = tableBo.getTableColumnConfig(schemeId);
	        	//为了把a0100放到a0101后面
	        	boolean flag = false;
	        	boolean isnew = false;
	        	for(int i=0;i<columnConfigLst.size();i++)
	        	{
	        		ColumnConfig column = (ColumnConfig)columnConfigLst.get(i);
	        		fieldList.add(column.getItemid()+"`"+column.getFieldsetid());
	        		if("link_id".equalsIgnoreCase(column.getItemid()))
	        			isnew = true;
	        		if("a0101".equalsIgnoreCase(column.getItemid())&&!flag) {
	        			fieldList.add("a0100");
	        			flag = true;
	        		}
	        	}
	        	if(!flag)
	        		fieldList.add("a0100");
	        	fieldList.add("nbase");
	        	//处理栏目设置,发现没有必须的指标时删除栏目设置，走默认情况
	        	if(!isnew) {
	        		ArrayList<String> arrayList = new ArrayList<String>();
	        		arrayList.add(subModuleId);
	        		arrayList.add(userview.getUserName());
	        		StringBuffer sql = new StringBuffer();
	        		sql.append("delete from t_sys_table_scheme_item ");
	        		sql.append(" where scheme_id in (select scheme_id from t_sys_table_scheme where submoduleid=? and username=?)");
	        		int delete = dao.delete(sql.toString(), arrayList);
	        		sql.setLength(0);
        			int delete2 = dao.delete("delete from t_sys_table_scheme where submoduleid=? and username=?", arrayList);
        			//防止前面删除不成功造成递归死循环
        			if(delete>0 && delete2>0)
        				return getFieldList(from);
	        	}
	        }else{
	        	fieldList.add("A0101"+"`"+"A01");
	        	fieldList.add("a0100");
	        	fieldList.add("nbase");
	        	fieldList.add("z0351"+"`"+"Z03");
	        	fieldList.add("thenumber`");
	        	fieldList.add("suitable`");
	        	fieldList.add("applyState`");
	        	fieldList.add("link_id`");
	        	fieldList.add("resume_flag`");
	        	fieldList.add("z0321"+"`"+"Z03");
	        	fieldList.add("z0325"+"`"+"Z03");
	        	fieldList.add("recdate`");
	        	fieldList.add("createtime_zp"+"`");
	        	fieldList.add("A0107"+"`"+"A01");
	        	fieldList.add("A0405"+"`"+"A04");
	        	fieldList.add("C0101");
	        	fieldList.add("A0435"+"`"+"A04");
	        	fieldList.add("A0410"+"`"+"A04");
	        	fieldList.add("A1915");
	        	fieldList.add("C0104");
	        	fieldList.add(this.getEmailItemId());
	        }	
			
			fieldList.add("resumenum`");
			if("resumeCenter".equalsIgnoreCase(from))
			{			
				fieldList.add("istalents`");
			}
			fieldList.add("EmailAddress`");
			fieldList.add("status`");
			for(int i=0; i<fieldList.size(); i++){
				String fieldColumn = (String)fieldList.get(i);
				FieldItem fi = null;
				if(StringUtils.equalsIgnoreCase(fieldColumn, "recdate`"))
				{
					fi = new FieldItem("", "recdate");
					fi.setUseflag("1");
					fi.setItemtype("D");
					fi.setItemlength(18);
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "createtime_zp`"))
				{
					fi = new FieldItem("", "createtime_zp");
					fi.setUseflag("1");
					fi.setItemtype("D");
					fi.setItemlength(18);
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "applyState`")){
					fi = new FieldItem("", "applyState");
					fi.setUseflag("1");
					fi.setItemtype("A");
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "link_id`"))
				{
					fi = new FieldItem("", "link_id");
					fi.setUseflag("1");
					fi.setItemtype("A");
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "resume_flag`"))
				{
					fi = new FieldItem("", "resume_flag");
					fi.setUseflag("1");
					fi.setItemtype("A");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "resumenum`"))
				{
					fi = new FieldItem("", "resumenum");
					fi.setUseflag("1");
					fi.setItemtype("A");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "isTalents`"))
				{
					fi = new FieldItem("", "istalents");
					fi.setUseflag("1");
					fi.setItemtype("A");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "EmailAddress`"))
				{
					fi = new FieldItem("", "EmailAddress");
					fi.setUseflag("1");
					fi.setItemtype("A");
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "status`"))
				{
					fi = new FieldItem("", "status");
					fi.setUseflag("1");
					fi.setItemtype("A");
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "suitable`"))
				{
					fi = new FieldItem("", "suitable");
					fi.setUseflag("1");
					fi.setItemtype("A");
					fi.setCodesetid("0");
				}else if(StringUtils.equalsIgnoreCase(fieldColumn, "thenumber`"))
				{
					fi = new FieldItem("", "thenumber");
					fi.setUseflag("1");
					fi.setItemtype("N");
					fi.setCodesetid("0");
				}
				else {
		        	//防止从业务字典中取字段取到同名字段
					String[] split = fieldColumn.split("`");
					if(split.length>1) {
						if("z0101".equalsIgnoreCase(split[0]))
							fi = bo.getFieldItem(dbname, "z01", "z0103");
						else
							fi = bo.getFieldItem(dbname, split[1], split[0]);
						
					}
					else {
						fi = DataDictionary.getFieldItem(split[0]);
						if("a0100".equals(split[0])||"nbase".equals(split[0]))
							fi.setFieldsetid("A01");
						
					}
						
				}
				if(fi==null || "0".equalsIgnoreCase(fi.getUseflag()))
					continue;
				columnList.add(fi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnList;
	}
	/**
	 * 获取邮件地址指标
	* @Title:getEmailItemId
	* @Description：
	* @author xiexd
	* @return
	 */
	public String getEmailItemId()
	{
		String emailId = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return emailId;
	}
	/**
	 * 根据字段内容获取
	 * @param fielditems 表头字段列表
	 * @param othModule  其它模块进入标志
	 * @return
	 */
	public ArrayList getColumnList(ArrayList fielditems,boolean othModule,String from){
		ArrayList columns = new ArrayList();
		String subModuleId = "";
		//自定义字段，防止业务字典取到别的表中的同名字段
		String myField= "'link_id','resume_flag','thenumber'";
		if("resumeCenter".equalsIgnoreCase(from))
		{			
			subModuleId = "zp_resume_191130_00001";
		}else if("talents".equalsIgnoreCase(from)){
			subModuleId = "zp_talent_191130_00001";
		}
		TableFactoryBO tableBo = new TableFactoryBO(subModuleId, this.userview, conn);
		HashMap scheme = tableBo.getTableLayoutConfig();
		if(scheme!=null)
        {
        	Integer scheme_str = (Integer)scheme.get("schemeId");
        	int schemeId = scheme_str.intValue();
        	ArrayList columnConfigLst = tableBo.searchCombineColumnsConfigs(schemeId, null);
        	columnConfigLst.add("a0100");
        	columnConfigLst.add("nbase");
        	columnConfigLst.add("z0301");
        	columnConfigLst.add("z0319");
        	columnConfigLst.add("z0381");
        	columnConfigLst.add("resumenum");
        	if("resumeCenter".equalsIgnoreCase(from))
        	{			
        		columnConfigLst.add("istalents");
        	}
        	columnConfigLst.add("EmailAddress");
        	columnConfigLst.add("status");
        	String mergedesc = "";
        	int mergedescIndex = 0;
            int num = 0;
            FieldItem temp = new FieldItem();
			for(int i=0;i<columnConfigLst.size();i++)
			{
				temp = new FieldItem();
	            temp.setUseflag("1");
	            temp.setItemtype("A");
			    ColumnConfig column = null;
			    String itemId = "";
			    
			    Object columnObj = columnConfigLst.get(i);
			    if (columnObj == null)
			        continue;
			    
			    if(columnObj instanceof ColumnConfig) {
			        column = (ColumnConfig)columnObj;
			        itemId = column.getItemid();
			    } else
			        itemId = columnObj.toString();
			    
				FieldItem item= null;
				ColumnsInfo info = new ColumnsInfo();
				if(!(myField.indexOf("'"+itemId+"'")>-1))
					item = DataDictionary.getFieldItem(itemId);
				info = getColumn(item==null? temp:item, itemId);
    			if(info!=null && column!=null)
    			{
    				info.setLoadtype(Integer.parseInt(("".equals((String)column.getIs_lock())||(String)column.getIs_lock()==null)?"0":(String)column.getIs_lock()));
    				info.setColumnWidth(column.getDisplaywidth());
    				info.setTextAlign(column.getAlign()+"");
    				String order = "";
    				if("1".equalsIgnoreCase(column.getIs_order()))
    				{
    					order = "true";
    				}else{
    					order = "false";
    				}
    				info.setSortable(Boolean.parseBoolean(order));
    				if("1".equals(column.getIs_sum()))
    				{
    					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
    				}else if("2".equals(column.getIs_sum())){
    					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
    				}else if("3".equals(column.getIs_sum())){
    					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
    				}else if("4".equals(column.getIs_sum())){
    					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
    				}
    				if(!"M".equalsIgnoreCase(info.getColumnType()))
    					info.setSortable(true);
    				if(column.getMergedesc()!=null&&column.getMergedesc().length()>0){
    					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
    					{
    						addTopHeadList(columns, mergedesc, mergedescIndex, num, info);
    						num+=1;
    						mergedescIndex = i;
    						continue;
    					}else{	        						
    						mergedesc = column.getMergedesc();
    						mergedescIndex = i;
    					}
    				}
    			}
    			columns.add(info);
	        }
        }else{
        	for(int i=0;i<fielditems.size();i++){
        		FieldItem fi = (FieldItem) fielditems.get(i);
        		ColumnsInfo info = getColumn(fi, fi.getItemid());
        		if("D".equalsIgnoreCase(info.getColumnType()))
                {
                    info.setTextAlign("left");
                }
        		columns.add(info);
        	}
		}
		return columns;
	}
	
	private ColumnsInfo getColumn(FieldItem item, String itemId) {
		item.setItemid(itemId);
		ColumnsInfo column = new ColumnsInfo(item);
		if("a0100".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setEncrypted(true);
			column.setOrdertype("1");
		}else if("a0101".equals(itemId)){
			column.setRendererFunc("searchResume");
			column.setLocked(true);
			column.setColumnWidth(120);
			column.setOrdertype("1");
		}else if(itemId.equals(this.getEmailItemId())){
			column.setRendererFunc("sendEmail");
		}else if("z0301".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setEncrypted(true);
		}else if("nbase".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setEncrypted(true);
		}else if("thenumber".equals(itemId)){
			column.setColumnDesc("志愿号");
			column.setColumnType("N");
			column.setOrdertype("1");
		}else if("recdate".equals(itemId)){
			column.setColumnDesc("应聘时间");
			column.setColumnType("D");
			column.setColumnLength(18);
		}else if("createtime_zp".equals(itemId)){
			column.setColumnDesc("注册时间");
			column.setColumnType("D");
			column.setColumnLength(18);
		}else if("a0405".equals(itemId)){
			column.setColumnDesc("最高学历");
		}else if("a1915".equals(itemId)){
			column.setColumnDesc("最近工作单位");
			column.setColumnWidth(160);
		}else if("z0303".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("z0351".equals(itemId)){
			column.setColumnWidth(150);
			column.setRendererFunc("searchPosition");
		}else if("z0319".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("z0381".equals(itemId)){
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setEncrypted(true);
		}else if("link_id".equals(itemId)){
			column.setColumnDesc("流程环节");
			column.setColumnWidth(150);
			column.setTextAlign("left");
			column.setOperationData(getFilterData("link"));
		}else if("resume_flag".equals(itemId)){
			column.setColumnDesc("流程状态");
			column.setOperationData(getFilterData(""));
		}else if("resumenum".equals(itemId)){
			column.setFilterable(false);
			column.setColumnDesc("职位申请数");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("istalents".equals(itemId)){
			column.setFilterable(false);
			column.setColumnDesc("是否存在人才库");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("EmailAddress".equals(itemId))
		{
			column.setFilterable(false);
			column.setColumnDesc("EitemilAddress");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("status".equals(itemId))
		{
			column.setFilterable(false);
			column.setColumnDesc("status");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		}else if("z0325".equalsIgnoreCase(itemId)||"z0321".equalsIgnoreCase(itemId)){
        	column.setCtrltype("3");
        	column.setNmodule("7");
		}if("suitable".equalsIgnoreCase(itemId)){
			column = new ColumnsInfo(item);
			column.setColumnDesc("简历筛选");
			column.setColumnLength(1);
			column.setDecimalWidth(0);
			column.setReadOnly(true);
			column.setColumnWidth(100);
		}else if("z0101".equalsIgnoreCase(itemId)) {
			item = DataDictionary.getFieldItem("Z0103","Z01");
			column = new ColumnsInfo(item);
		}if("applyState".equalsIgnoreCase(itemId)){
			ArrayList<CommonData> operationData = new ArrayList<CommonData>();
			operationData.add(new CommonData("已接受职位申请","已接受职位申请"));
			operationData.add(new CommonData("已拒绝职位申请","已拒绝职位申请"));
			operationData.add(new CommonData("未处理","未处理"));
			column = new ColumnsInfo(item);
			column.setColumnDesc("申请状态");
			column.setOperationData(operationData);
		}
		return column;
	
	}

	/**
	 * 添加合并列头
	 * @param columns
	 * @param mergedesc
	 * @param mergedescIndex
	 * @param num
	 * @param info
	 */
	private void addTopHeadList(ArrayList columns, String mergedesc,
			int mergedescIndex, int num, ColumnsInfo info) {
		ArrayList tableheadlist = new ArrayList();
		Object obj = columns.get(mergedescIndex-num);
		String name = obj.getClass().getName();
		ColumnsInfo columstemp = null;
		if("java.util.HashMap".equalsIgnoreCase(name)) {
			ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
		    for (int n = 0; n < list.size(); n++) {
		    	columstemp = list.get(n);
		    	tableheadlist.add(columstemp);
		    }
		}
		else{
			columstemp = (ColumnsInfo)columns.get(mergedescIndex-num);
			tableheadlist.add(columstemp);
		}
		tableheadlist.add(info);
		HashMap topHead = new HashMap();
		topHead.put("text",mergedesc);
		topHead.put("items", tableheadlist);
		columns.remove(mergedescIndex-num);//当合并时移除最后一列
		columns.add(topHead);
	}
	
	
	
	/**
	 * 获得应聘情况栏相关内容 
	 * @return
	 */
	public ArrayList getQueryScheme(){
		ArrayList queryscheme = new ArrayList(); 
		if("resumeCenter".equalsIgnoreCase(this.fromModule)) // resumeCenter：简历中心 talents:人才库
		{
			HashMap map1 = new HashMap();
			map1.put("position", "0,1");     //位置  0代表第一大类，1代表最近一周
			map1.put("category", "create_time");  //类别
			map1.put("name", "最近一周");    //名称
			HashMap map2 = new HashMap();
			map2.put("position", "0,2");
			map2.put("category", "create_time");
			map2.put("name", "最近两周");
			HashMap map3 = new HashMap();
			map3.put("position", "0,3");
			map3.put("category", "create_time");
			map3.put("name", "最近一月");
			HashMap map4 = new HashMap();
			map4.put("position", "1,1");
			map4.put("category", "status");
			map4.put("name", "新简历");
			HashMap map5 = new HashMap();
			map5.put("position", "1,2");
			map5.put("category", "status");
			map5.put("name", "猎头推荐");
			HashMap map6 = new HashMap();
			map6.put("position", "2,1");
			map6.put("category", "positionType");
			map6.put("name", "我的职位");
			HashMap map7 = new HashMap();
			map7.put("position", "2,2");
			map7.put("category", "positionType");
			map7.put("name", "未应聘职位");
			queryscheme.add(map1);
			queryscheme.add(map2);
			queryscheme.add(map3);
			queryscheme.add(map4);
			queryscheme.add(map6);
			queryscheme.add(map7);
		}
		else
		{
			
			HashMap map1 = new HashMap();
			map1.put("position", "0,1");     //位置  0代表第一大类，1代表一个月内
			map1.put("category", "create_time");  //类别
			map1.put("name", "一个月内");    //名称
			HashMap map2 = new HashMap();
			map2.put("position", "0,2");	//三个月内
			map2.put("category", "create_time");
			map2.put("name", "三个月内");
			HashMap map3 = new HashMap();
			map3.put("position", "0,3");
			map3.put("category", "create_time");
			map3.put("name", "半年内");
			HashMap map4 = new HashMap();
			map4.put("position", "0,4");
			map4.put("category", "create_time");
			map4.put("name", "半年以上");
			
			
			HashMap map5 = new HashMap();
			map5.put("position", "1,1");
			map5.put("category", "status");
			map5.put("name", "公共人才库");
			HashMap map6 = new HashMap();
			map6.put("position", "1,2");
			map6.put("category", "status");
			map6.put("name", "我的人才库");
			queryscheme.add(map1);
			queryscheme.add(map2);
			queryscheme.add(map3);
			queryscheme.add(map4);
			queryscheme.add(map5);
			queryscheme.add(map6);
		}
		ArrayList schemeList = new ArrayList();
		HashMap schemeLenMap = getSchemeLenMap(queryscheme);
		for(int i=0; i<queryscheme.size(); i++){
			LazyDynaBean abean = new LazyDynaBean();
			String regional="";
			String category="";
			HashMap smap = (HashMap)queryscheme.get(i);
			Iterator it = smap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if(StringUtils.equals(key, "position")){
					String[] params = value.split(",");
					regional = params[0];   //查询方案所在区域
					abean.set("event", "Global.setSchemeArr("+params[0]+","+params[1]+")");
					abean.set("id", params[0]+params[1]);
					abean.set("regional", params[0]);
					abean.set("size", schemeLenMap.get(params[0]));
				}else if(StringUtils.equals(key, "category")){
					category = value;      //查询方案对应内容
				}else if(StringUtils.equals(key, "name")){
					abean.set(key, value);
				}
			}
			if(schemeMap.get(regional) == null)
			    schemeMap.put(regional, category);
			schemeList.add(abean);
		}
		return schemeList;
	}
	
	/**
	 * 获得应聘情况每个区域的个数
	 * @param queryscheme 应聘情况
	 * @return
	 */
	public HashMap getSchemeLenMap(ArrayList queryscheme){
		HashMap schemeLenMap = new HashMap();
		for(int i=0; i<queryscheme.size(); i++){
			HashMap smap = (HashMap)queryscheme.get(i);
			Iterator it = smap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if(StringUtils.equals(key, "position")){
					String[] params = value.split(",");
					if(schemeLenMap.get(params[0]) == null)
						schemeLenMap.put(params[0], params[1]);
					if(schemeLenMap.get(params[0]) != null && Integer.parseInt((String)schemeLenMap.get(params[0]))<Integer.parseInt(params[1])){
						schemeLenMap.remove(params[0]);
						schemeLenMap.put(params[0], params[1]);
					}
				}
			}
		}
		return schemeLenMap;
	}
	
	
	/**
	 * 获得人员简历 | 人才库的过滤条件 
	 * @return
	 */
	public String getFilterWhl(String pre) throws GeneralException
	{
		StringBuffer whrstr=new StringBuffer("");
		
		//从权限方法中获取对应记录
		RecruitPrivBo bo = new RecruitPrivBo();
		
		if("resumeCenter".equalsIgnoreCase(this.fromModule)){
			whrstr.append(" and ");
			whrstr.append(bo.getResumeWhr("0", this.userview,""));
		}else if("talents".equalsIgnoreCase(this.fromModule))
		{
		}
		else
		{
			whrstr.append(" and 1=2 ");
		}
		return whrstr.toString();
	}
	
	
	
	
	/**
	 * 根据表头列、人员库、条件语句组合成相应查询语句
	 * @param whrMap 具体查询内容集合
	 * @param fielditems 表头字段列表
	 * @param pre  应聘人员库
	 * @param conditionStr  条件语句
	 * @param whrstr 查询范围SQL
	 * @param positionStr   招聘职位进入简历中心的筛选条件
	 * @param from 模块标志
	 * @return
	 */
	public String getQueryStr(HashMap whrMap, ArrayList fielditems, String pre, String conditionStr, String whrstr, String positionStr,String from){
		StringBuffer queryStr = new StringBuffer();  //查询sql
	
		StringBuffer fields = new StringBuffer();   //查询字段
		StringBuffer tables = new StringBuffer();   //查询表
		StringBuffer whereStr = new StringBuffer();  //where语句
		StringBuffer myZpPosSql = new StringBuffer(); //获取我的职位条件
		getMyZpPosSql(myZpPosSql, whrMap);
		whereStr.append(" where 1=1 ");
		//a0100固定有
		fields.append(pre + "a01.a0100 as a0100,");
		tables.append(pre + "a01");
		//人员库固定有
		fields.append("'" + pre + "' nbase ");
		fields.append(",zp_pos_tache.thenumber as thenumber");
		//显示出每个人的每一条应聘记录
		tables.append(" left join  zp_pos_tache   on "+pre + "a01.A0100=zp_pos_tache.A0100 ");
		tables.append(" and zp_pos_tache.nbase='"+pre + "'"); 
		tables.append(" left join z03  on z03.z0301=zp_pos_tache.zp_pos_id "); 
		tables.append(" left join z01 on z03.z0101= z01.z0101 "); 
		tables.append(" left join zp_flow_links zpfl on zp_pos_tache.link_id = zpfl.id ");  
		tables.append(" left join zp_flow_status zpfs on zp_pos_tache.resume_flag=zpfs.status and zpfl.id=zpfs.link_id ");  
		tables.append(" left join (select COUNT(*) counts,zppt.A0100 from zp_pos_tache zppt where nbase='"+pre+"' group by A0100) zppt1 on zppt1.A0100 =  "+pre + "a01 .A0100 ");
		if("resumeCenter".equalsIgnoreCase(from))
		{			
			tables.append(" left join (select COUNT(*) counts,a0100 from zp_talents where nbase='"+pre+"' group by A0100) zpt on zpt.a0100 = "+pre + "A01.A0100 ");
		}
		boolean appendA0405 = false;
		boolean appendA0410 = false;
		for(int j=0;j<fielditems.size();j++){
			FieldItem fi = (FieldItem) fielditems.get(j);
			if(!StringUtils.contains(fields.toString(), fi.getItemid().toLowerCase())||"status".equals(fi.getItemid().toLowerCase()))
			{				
				if("applyState".equalsIgnoreCase(fi.getItemid())) {
					fields.append(", case when zp_pos_tache.status='1' then '已接受职位申请' when zp_pos_tache.status='2' then '已拒绝职位申请' else '未处理' end as applyState , zp_pos_tache.status applyStateCode ");
				}else if("resume_flag".equalsIgnoreCase(fi.getItemid())){				
					fields.append(", case when "+Sql_switcher.isnull("zpfs.custom_name","'0'")+"='0' then '' else zpfs.custom_name end as resume_flag");
				}else if("link_id".equalsIgnoreCase(fi.getItemid()))
				{				
					fields.append(", zpfl.custom_name link_id,zpfl.node_id node_id");
				}else if("resumeNum".equalsIgnoreCase(fi.getItemid())){
					fields.append(","+Sql_switcher.isnull("zppt1.counts","0")+" "+fi.getItemid());
				}else if("isTalents".equalsIgnoreCase(fi.getItemid())){
					if("resumeCenter".equalsIgnoreCase(from)){						
						fields.append(","+Sql_switcher.isnull("zpt.counts","0")+" "+fi.getItemid());
					}
				}else if("EmailAddress".equalsIgnoreCase(fi.getItemid())){
					if(!"#".equals(this.getEmailItemId()))
						fields.append(",'"+this.getEmailItemId()+"' "+fi.getItemid());
				}else if("status".equalsIgnoreCase(fi.getItemid())){
					fields.append(", zpfs.status "+fi.getItemid());
				}else if("A0405".equalsIgnoreCase(fi.getItemid())){
					appendA0405 = true;
					fields.append(",A04_education.education,"+fi.getItemid());
				}else if("A0410".equalsIgnoreCase(fi.getItemid())){
					appendA0410 = true;
					fields.append(",A04_professional.professional,"+fi.getItemid());
				}else if("suitable".equalsIgnoreCase(fi.getItemid()))
    			{
					fields.append(",case  when zp_pos_tache."+fi.getItemid()+"='1' then '符合' when zp_pos_tache."+fi.getItemid()+"='0' then '不符合'  else '' end "+fi.getItemid());
    			}else if("z0101".equalsIgnoreCase(fi.getItemid())) {
    				fields.append("," + pre + fi.getFieldsetid() + "." +fi.getItemid());
    				fields.append(",z01.z0103 ");
    			}else{
    				//加上别名，防止有重复有指标名称不明确
    				//防止数据库运行过中招聘优化包将z05中的字段加入表头集合中产生错误，先过滤掉
    				if(StringUtils.isNotEmpty(fi.getFieldsetid()) && fi.getFieldsetid().toUpperCase().startsWith("Z05"))
    					continue;
    				else if(StringUtils.isNotEmpty(fi.getFieldsetid()) && fi.getFieldsetid().toUpperCase().startsWith("A"))
    					fields.append("," + pre + fi.getFieldsetid() + "." +fi.getItemid());
    				else if(StringUtils.isNotEmpty(fi.getFieldsetid()))
    					fields.append("," + fi.getFieldsetid() + "." +fi.getItemid());
    				else if(StringUtils.isNotEmpty(fi.getItemid())&& "createtime_zp".equals(fi.getItemid()))
    					fields.append("," + pre + "A01.createtime createtime_zp ");
    				else
    					fields.append("," +fi.getItemid());
				}
			}
			if(!StringUtils.contains(tables.toString(), fi.getFieldsetid().toLowerCase())){
				if(fi.isPerson()){      //人员子集
					String tablename = pre + fi.getFieldsetid().toLowerCase();
					tables.append(" left join (select a1.* from   "+tablename+" a1 where  a1.i9999=(select MAX(b1.I9999) from "+tablename+" b1 where b1.A0100=a1.a0100)) "+tablename +" on "+pre + "a01.A0100="+tablename+".A0100");

				}
			}
		}
		if(appendA0405)
			tables.append(" left join (select codeitemid,codeitemdesc education from codeitem where codesetid='AM') A04_education on A04_education.codeitemid = a0405");
		if(appendA0410)
			tables.append(" left join (select codeitemid,codeitemdesc professional from codeitem where codesetid='AI') A04_professional on A04_professional.codeitemid = a0410");
		queryStr.append("select "+pre+"A01.guidkey," + fields.toString() + " from " + tables.toString() + whereStr.toString() + whrstr /*+ conditionStr + positionStr*/);//因为导出简历excel要获取查询指标所以拼接查询方案和筛选条件改到交易类中进行
		return queryStr.toString();
	}
	
	/**
	 * 根据页面选择条件生成相应sql语句的where条件
	 * @param flag 搜索位置标识   1：搜索框   2：查询栏  3：应聘情况栏
	 * @param whrMap  具体查询内容集合
	 * @param export 为"export"时为简历中心导出生成sql
	 * @return
	 */
	public String getConditionStr(String flag, HashMap whrMap,String positionDesc, String export) throws GeneralException{
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String dbname="";  //应聘人员库
		if(vo!=null)
			dbname=vo.getString("str_value");
		else
			throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
		StringBuffer str = new StringBuffer("");
		if(StringUtils.equals(flag, "1")){    //搜索框
		    flag="3";
		}else if(StringUtils.equals(flag, "2")){  //查询栏
			
		}
		RecruitPrivBo bo = new RecruitPrivBo();
		str.append(bo.getHirePrivSql(this.userview, conn).replaceAll("z\\.", "z03\\."));
		
		if(StringUtils.equals(flag, "3")){   //应聘情况栏
			if("talents".equalsIgnoreCase(this.fromModule))  //来自人才库
			{
				str.append(getTalentsCondition(whrMap));
			}
			else
			{
				String schemeValues = (String)whrMap.get("schemeValues");
				String[] schemes = schemeValues.split(",");
				String create_time = "";
				String status = "";
				String positionType = "";
				Iterator it = schemeMap.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry)it.next();
					String key = (String)entry.getKey();
					String value = (String)entry.getValue();
					if(StringUtils.equalsIgnoreCase(value, "create_time")){
						create_time = schemes[Integer.parseInt(key)];
					}else if(StringUtils.equalsIgnoreCase(value, "status")){
						status = schemes[Integer.parseInt(key)];
					}else if(StringUtils.equalsIgnoreCase(value, "positionType")){
						positionType = schemes[Integer.parseInt(key)];
					}
				}
				
				if(StringUtils.isNotEmpty(create_time) && !StringUtils.equals(create_time, "0"))
				    str.append(" and ");
				
				if(StringUtils.equalsIgnoreCase(create_time, "1")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), "recdate") + "<=" + 7);
				}
				
				if(StringUtils.equalsIgnoreCase(create_time, "2")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), "recdate") + "<=" + 14);
				}
				
				if(StringUtils.equalsIgnoreCase(create_time, "3")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), "recdate") + "<=" + 30);
				}
				
				if(StringUtils.isNotEmpty(status) && !StringUtils.equals(status, "0")&&!"export".equals(export)){
					if(StringUtils.equalsIgnoreCase(status, "1"))
						str.append(" and ( zp_pos_tache.status='0' or zp_pos_tache.status is null )");
					if(StringUtils.equalsIgnoreCase(status, "2"))
						str.append(" and relation_type=2 and nullif(recusername,'') is not null and recdate is not null ");
			/*		if(StringUtils.equalsIgnoreCase(status, "3")) //内部推荐暂不支持
						str.append(" and nullif(recusername,'') is not null and recdate is not null  "); */
				}
				JSONObject obj = JSONObject.fromObject(positionDesc); 
				String zp_pos_id = obj.getString("zp_pos_id");  //zhangcq 2016/8/30 如果从其他模块进入简历中心 不拼接我的职位
				if(StringUtils.isNotEmpty(positionType) && !StringUtils.equals(positionType, "0") && StringUtils.isEmpty(zp_pos_id)
						&&!"export".equals(export))
				{
			        //拼接我的职位（创建人）
					getMyZpPosSql(str, null);
				}
			}
		}
		
		String searchBoxContent = (String)whrMap.get("searchBox");
        if(StringUtils.isNotEmpty(searchBoxContent)){
            FieldItem fi = DataDictionary.getFieldItem("a0410");
            String codesetid = "";
            if(fi!=null)
			{
            	codesetid = fi.getCodesetid();
			}
            str.append(" and (a0101 like '%"+searchBoxContent+"%' ");
            FieldItem c0103 = DataDictionary.getFieldItem("c0103");
            if(c0103!=null)
			{
            	str.append(" or c0103 like '%"+searchBoxContent+"%' ");
			}
            FieldItem c0102 = DataDictionary.getFieldItem("c0102");
            if(c0102!=null)
			{
            	str.append(" or c0102 like '"+searchBoxContent+"%' ");
			}
            FieldItem z0351 = DataDictionary.getFieldItem("z0351");
            if(z0351!=null)
			{
            	str.append(" or z0351 like '%"+searchBoxContent+"%' ");
			}
            str.append(" or (select codeitemdesc from codeitem where codesetid='"+codesetid+"' ");
            FieldItem a0410 = DataDictionary.getFieldItem("a0410");
            if(a0410!=null)
			{
            	str.append(" and codeitemid='a0410' ");
			}
            str.append(" ) like '%"+searchBoxContent+"%' " );
            FieldItem a0435 = DataDictionary.getFieldItem("a0435");
            if(a0435!=null)
			{
            	str.append(" or a0435 like '%"+searchBoxContent+"%' ");
			}
            str.append(") ");
        }
		
		 if(!StringUtils.equals(flag, "3")&& "talents".equalsIgnoreCase(this.fromModule)) //人才库没选择查询方案时需通过如下条件进行过滤
		 {
			 str.append(getTalentsCondition(null));
		 }
		
		return str.toString();
	}

	/**
	 * 获取我的职位条件
	 * @param str
	 * @param whrMap 
	 */
	private void getMyZpPosSql(StringBuffer str, HashMap whrMap) {
		try {
			RecruitPrivBo bo = new RecruitPrivBo();
			//str.append(" and ( z0309='"+this.userview.getUserName()+"' "); 
			str.append(" and ( 1=2 ");
			if(whrMap!=null)
				str.append(" or "+bo.getResumeWhr("0", this.userview,""));
			//负责人、招聘成员、部门负责人
			if(this.userview.getA0100().length()>0)
			{
				str.append(" or z0301 in ( select z0301 from zp_members ");
				str.append(" where a0100 = '"+this.userview.getA0100()+"' and nbase='"+this.userview.getDbname()+"' )");
			}
			str.append(" )");
			str.append(bo.getHirePrivSql(this.userview, conn).replaceAll("z\\.", "z03\\."));
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	
	
	private String getTalentsCondition(HashMap whrMap) throws GeneralException
	{
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String dbname=vo.getString("str_value");
		StringBuffer str = new StringBuffer(" and  exists (select null from zp_talents where zp_talents.nbase='"+dbname+"' and zp_talents.a0100="+dbname+"A01.a0100 ");

		RecruitPrivBo bo = new RecruitPrivBo();
		if(whrMap==null){
			str.append(" and ");
			str.append(bo.getResumeWhr("1", this.userview,""));
			str.append(" ) ");			
		}else
		{ 
			String schemeValues = (String)whrMap.get("schemeValues");
			String[] schemes = schemeValues.split(",");
			String create_time = "";
			String status = "";
			Iterator it = schemeMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if(StringUtils.equalsIgnoreCase(value, "create_time")){
					create_time = schemes[Integer.parseInt(key)];
				}else if(StringUtils.equalsIgnoreCase(value, "status")){
					status = schemes[Integer.parseInt(key)];
				}
			}
			str.append(" and ");
			str.append(bo.getResumeWhr("1", this.userview,status));
			str.append(" ) ");
			
			if(StringUtils.isNotEmpty(create_time) && !StringUtils.equals(create_time, "0"))
			{
				str.append(" and ");
				String sendTime=" case when "+dbname+"A01.modTime>"+dbname+"A01.createTime then "+dbname+"A01.modTime else "+dbname+"A01.createTime end ";
				if(StringUtils.equalsIgnoreCase(create_time, "1")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), sendTime) + "<=" + 30);
				}
				if(StringUtils.equalsIgnoreCase(create_time, "2")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), sendTime) + "<=" + 90);
				}
				if(StringUtils.equalsIgnoreCase(create_time, "3")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), sendTime) + "<=" + 180);
				} 
				if(StringUtils.equalsIgnoreCase(create_time, "4")){
					str.append(Sql_switcher.diffDays(Sql_switcher.sqlNow(), sendTime) + ">" + 180);
				} 
			}
		}
		
		
		return str.toString();
	}
	
	/**
	 * 将其它模块进入简历中心时的条件转换为相应的sql条件
	 * @param positionDesc  条件描述，json字符串格式
	 * @return
	 */
	public String getPositionStr(String positionDesc) {
		String positionStr = "";
		if(StringUtils.isNotEmpty(positionDesc)){
			JSONObject obj = JSONObject.fromObject(positionDesc);
			String zp_pos_id = obj.getString("zp_pos_id");
			if(StringUtils.isNotEmpty(zp_pos_id))
				positionStr += " and zp_pos_tache.zp_pos_id='"+zp_pos_id+"' ";
		}
		return positionStr;
	}
	
	/**
	 * 移出人才库
	 * @param a0100
	 * @param nbase
	 * @param dao
	 * @param value_ns
	 * @return 1：移出成功  0：没有移除
	 */
	public int deleteZpTalents(String a0100,String nbase,ContentDAO dao,ArrayList value_ns)
	{
		int number=0;
		try
		{
			String sql="delete from zp_talents where a0100=? and nbase=? ";  
			if(!this.userview.isSuper_admin()) //不为超级用户
			{
				sql+=" and create_user='"+this.userview.getUserName()+"'";
			}
			number=dao.delete(sql,value_ns);
		} 
		catch(Exception e){
			e.printStackTrace(); 
		}
		return number;
	}
	
	/**
	 * 获取表格控件上方的菜单按钮
	 * @param flag 是否来自导航栏（非导航栏进入时提供返回按钮）
	 * @return
	 */
	public ArrayList getButtonList(boolean flag)
	{
		ArrayList buttonList=new ArrayList();
		if("talents".equalsIgnoreCase(this.fromModule))  //来自人才库
		{
			if(this.userview.hasTheFunction("3110302")){
				buttonList.add(newButton("删除",null,"Global.deleteRecords",null,"true"));
			}
			if(this.userview.hasTheFunction("3110306")){				
				buttonList.add(newButton("推荐职位",null,"Global.recommendOtherPosition",null,"true"));
			}
			if(this.userview.hasTheFunction("3110303")){
				buttonList.add(ButtonInfo.BUTTON_SPLIT); 
				buttonList.add(newButton("移出人才库",null,"Global.removeTalents",null,"true")); 
			}
			if(this.userview.hasTheFunction("3110309")){
				buttonList.add(ButtonInfo.BUTTON_SPLIT);
				buttonList.add(newButton("发送通知",null,"Global.sendNotice",null,"true")); 
			}

		}
		else if("resumeCenter".equalsIgnoreCase(this.fromModule))  //来简历中心
		{
			ArrayList menuList = new ArrayList();
			RecruitUtilsBo ubo = new RecruitUtilsBo(conn);
			if(userview.isSuper_admin()||userview.hasTheFunction("311020102")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("数据迁移", "Global.showResumeWindow()", "/images/export.gif", null, null ,new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311020101")){
			LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出Excel", "Global.exportWin()", "/images/export.gif", null, null ,new ArrayList());
			menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311020104")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出简历PDF", "Global.exportResumePDF()", "/images/export.gif", null, null, new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311020107")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出简历WORD", "Global.exportResumeWORD()", "/images/export.gif", null, null, new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311020105")){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("text", "打印简历");
				bean.set("id", "printAXId");
				bean.set("handler", "Global.printAX()");
//				LazyDynaBean bean = ubo.getMenuBean("打印简历", "Global.printAX()", "", null, null, new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311020106")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("生成公示材料", "Global.selectField()", "", null, null, new ArrayList());
				menuList.add(bean);
			}
			
			if(userview.isSuper_admin()||userview.hasTheFunction("311020107")){
				LazyDynaBean analyseBean = RecruitUtilsBo.getMenuBean("统计分析", null, null, "cusMenu", "analyse", new ArrayList());
				menuList.add(analyseBean);
        	}
			
			String menuStr = RecruitUtilsBo.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), "aaaa", menuList);
			buttonList.add(menuStr);
		   /* if(this.userview.hasTheFunction("311020103"))
		    	buttonList.add(newButton("导入简历","importResumeId","",null,"false"));*/
		    
			if(this.userview.hasTheFunction("3110202"))
				buttonList.add(newButton("删除",null,"Global.deleteRecords",null,"true"));
			
			if(this.userview.hasTheFunction("3110203") || this.userview.hasTheFunction("3110204"))
			    buttonList.add(ButtonInfo.BUTTON_SPLIT);
			
			if(this.userview.hasTheFunction("3110203"))
			    buttonList.add(newButton("接受职位申请",null,"Global.acceptPositionApply",null,"true"));
			
			if(this.userview.hasTheFunction("3110204"))
			    buttonList.add(newButton("拒绝职位申请",null,"Global.rejectPositionApply",null,"true"));
			
			if(this.userview.hasTheFunction("3110206"))
			    buttonList.add(newButton("推荐职位",null,"Global.recommendOtherPosition",null,"true"));

			if(this.userview.hasTheFunction("3110205")){
				buttonList.add(ButtonInfo.BUTTON_SPLIT);
				buttonList.add(newButton("转人才库",null,"Global.turnTalents",null,"true")); 
			}
			if(this.userview.hasTheFunction("3110209")){
				buttonList.add(ButtonInfo.BUTTON_SPLIT);
				buttonList.add(newButton("发送通知",null,"Global.sendNotice",null,"true")); 
			}
		}
		
		if(flag){
			buttonList.add(ButtonInfo.BUTTON_SPLIT);
			buttonList.add(newButton("返回",null,"Global.back",null,"true"));
		}
		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setFunctionId("ZP0000002112");
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		queryBox.setText("请输入姓名、职位、学历、专业、毕业学校...");
		buttonList.add(queryBox);
		buttonList.add(new ButtonInfo("<div id='searchbox' style='display:none;'></div>"));
		
		return buttonList;
	}
	
	
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
	 
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public HashMap getSchemeMap() {
		return schemeMap;
	}

	public void setSchemeMap(HashMap schemeMap) {
		this.schemeMap = schemeMap;
	}

	/**
	 * 获取导出需要的表头数据
     * @param fieldList 表格中的列
     * @param mergedList 合并列
     * @param flag 是否是合并列中包含的列
     * @param index 为了防止excel表头列序号出错 fromColNum
	 * @return
	 */
    public ArrayList<LazyDynaBean> getExcleHeadList(ArrayList fieldList, ArrayList<LazyDynaBean> mergedList, boolean flag ,int index) {
        int num = 0;
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            Object obj = fieldList.get(i);
            String name = obj.getClass().getName();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if("java.util.HashMap".equalsIgnoreCase(name)) {
                //获取合并列中包含的指标
                ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
                headList.addAll(getExcleHeadList(list, mergedList, true, ind + num));
                num = num + list.size();
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String itemid = info.getColumnId();
                if(itemid==null || "a0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid) || "z0301".equalsIgnoreCase(itemid)
                        || "z0319".equalsIgnoreCase(itemid) || "z0381".equalsIgnoreCase(itemid) || "resumenum".equalsIgnoreCase(itemid)
                        || "istalents".equalsIgnoreCase(itemid) || "emailaddress".equalsIgnoreCase(itemid) || "status".equalsIgnoreCase(itemid)
                        || 4 == info.getLoadtype())
                    continue;
                
                bean.set("itemid", itemid);
                bean.set("content", info.getColumnDesc()+ "");
                if("z0351".equalsIgnoreCase(itemid))
                    bean.set("content", "申请职位");
                
                bean.set("codesetid", info.getCodesetId());
                bean.set("colType", info.getColumnType());
                if("N".equalsIgnoreCase(info.getColumnType()) && StringUtils.isNotEmpty(info.getFieldsetid()) 
                        && info.getFieldsetid().startsWith("A"))
                    bean.set("colType", "A");
                else if("D".equalsIgnoreCase(info.getColumnType())) {
                	 int length = info.getColumnLength();
                     String dateFormat = "yyyy-MM-dd";
                     if(length == 4)
                    	 dateFormat = "yyyy";
                     else if(length == 7)
                    	 dateFormat = "yyyy-MM";
                     else if(length == 16)
                    	 dateFormat = "yyyy-MM-dd HH:mm";
                     else if(length >= 18)
                    	 dateFormat = "yyyy-MM-dd HH:mm:ss";
                     
                	bean.set("dateFormat", dateFormat);
                }
                
                bean.set("decwidth", info.getDecimalWidth() + "");
                if(mergedList != null && mergedList.size() > 0){
                    if(flag) {
                        //设置合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 1);
                        bean.set("toRowNum", 1);
                      //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + index);
                        bean.set("toColNum", ind + index);
                    } else {
                        //设置非合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 0);
                        bean.set("toRowNum", 1);
                        //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + num);
                        bean.set("toColNum", ind + num);
                    }
                    
                }

                headList.add(bean);
                ind++;
            }
            
            
        }
        
        return headList;

    }
    
  
	/**
	 * 获取符合要求的人的id
	 * @param conditionStr 
	 * @param whrMap 具体查询内容集合
	 * @param positionDesc
	 * @return
	 * @throws GeneralException
	 */
	public String getExportWhereStr(String conditionStr) throws GeneralException{
		conditionStr = conditionStr.replaceAll("zp_pos_tache", "f");
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String dbname=vo.getString("str_value");
		StringBuffer str = new StringBuffer(" and  "+dbname+"a01.a0100 in (");
		str.append("select distinct a.A0100 from "+dbname+"a01 a");
		str.append(" left join (select d.* from  zp_pos_tache d where d.THENUMBER=(select MIN(e.THENUMBER) from zp_pos_tache e where d.A0100=e.a0100)) f on a.A0100=f.A0100");
		str.append(" left join Z03 c on f.ZP_POS_ID=c.Z0301");
		str.append(" where 1=1 ");
		str.append(conditionStr);
		str.append(")");
		return str.toString();
	}
	/**
	 * 获取人员子集及对应的子集指标
	 * @param fieldList 简历中心显示列
	 * @return
	 */
    public HashMap<String, String> getSubFieldMap(ArrayList fieldList) {
        HashMap<String, String> subFieldMap = new HashMap<String, String>();
        for(int i = 0; i < fieldList.size(); i++) {
        	Object obj = fieldList.get(i);
        	String name = obj.getClass().getName();
        	//合并列在表头中为hashmap，非hashmap的都为非合并列
        	if("java.util.HashMap".equalsIgnoreCase(name)) {
        		ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
		        for (int n = 0; n < list.size(); n++) {
		            ColumnsInfo column = list.get(n);
		            String itemid = column.getColumnId();
		            String fieldsetId = column.getFieldsetid();
		            if(StringUtils.isEmpty(fieldsetId) || !fieldsetId.startsWith("A") || "A01".equalsIgnoreCase(fieldsetId))
		                continue;
		            
		            getSubFieldMap(subFieldMap, itemid,fieldsetId);
		        }
		        continue;
        	}
            ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
		    
            String itemid = info.getColumnId();
            String fieldsetId = info.getFieldsetid();
            if(StringUtils.isEmpty(fieldsetId) || !fieldsetId.startsWith("A") || "A01".equalsIgnoreCase(fieldsetId))
                continue;
            
            getSubFieldMap(subFieldMap, itemid, fieldsetId);
        }
        
        return subFieldMap;
    }

	private void getSubFieldMap(HashMap<String, String> subFieldMap,
			String itemid, String fieldsetId) {
		String items = subFieldMap.get(fieldsetId);
		if(StringUtils.isEmpty(items))
		    subFieldMap.put(fieldsetId, "," + itemid);
		else {
		    items = items + "," + itemid;
		    subFieldMap.put(fieldsetId, items);
		}
	}
    /**
     * 获取导出需要的表头的合并列
     * @param fieldList 表头的列头
     * @return
     */
    public ArrayList<LazyDynaBean> getExcleMergedList(ArrayList fieldList) {
        ArrayList<LazyDynaBean> mergedList = new ArrayList<LazyDynaBean>();
        int num = 0;
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            Object obj = fieldList.get(i);
            String name = obj.getClass().getName();
            
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if("java.util.HashMap".equalsIgnoreCase(name)) {
                ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
                LazyDynaBean bean = new LazyDynaBean();
                //设置合并列的起始行
                bean.set("fromRowNum", 0);
                //设置合并列的起始列
                bean.set("fromColNum", ind + num);
                //设置合并列的终止行
                bean.set("toRowNum", 0);
                //设置合并列的终止列
                bean.set("toColNum", ind + num + list.size() - 1);
                //设置合并列的名称
                bean.set("content", ((HashMap)obj).get("text"));
                mergedList.add(bean);
                num = num + list.size() - 1;
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                String itemid = info.getColumnId();
                if("a0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid) || "z0301".equalsIgnoreCase(itemid)
                        || "z0319".equalsIgnoreCase(itemid) || "z0381".equalsIgnoreCase(itemid) || "resumenum".equalsIgnoreCase(itemid)
                        || "istalents".equalsIgnoreCase(itemid) || "emailaddress".equalsIgnoreCase(itemid) || "status".equalsIgnoreCase(itemid)
                        || 4 == info.getLoadtype())
                    continue;
            }
            
            ind++;
        }
        
        return mergedList;

    }
    /**
     * 获取日期指标的格式
     * @param fi 日期指标
     * @return
     */
    public String getDateFromat(FieldItem fi) {
        String fromat = "";
        if(fi == null)
            return fromat;
        
        int length = fi.getItemlength();
        if(4 == length)
            fromat = "yyyy";
        else if(7 == length)
            fromat = "yyyy-MM";
        else if(10 == length)
            fromat = "yyyy-MM-dd";
        else if(16 == length)
            fromat = "yyyy-MM-dd hh:mm";
        else if(18 == length)
            fromat = "yyyy-MM-dd hh:mm:ss";
        
        return fromat;
    }
    /**
     * 获取栏目设置的排序指标拼接排序的sql
     * @param dbname 招聘人员库
     * @param scheme_id 
     * @return
     */
    public String getOrderSql(String dbname, String scheme_id) {
        String orderSql = "";
        RowSet rs = null;
        try{
            String sql = "select 1 from t_sys_table_scheme " +
            		"where submoduleid = '"+scheme_id+"' and is_share = 0 and username = '"
                    + this.userview.getUserName() + "'";
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            if(rs.next())
                sql = "select itemid,fieldsetid,is_order from t_sys_table_scheme_item where scheme_id = (" +
                		"select scheme_id from t_sys_table_scheme " +
                		"where submoduleid = '"+scheme_id+"' and is_share = '0' and username = '"
                    + this.userview.getUserName() + "') and is_display = '1' " +
                    		"and is_order<>0 order by displayorder";
            else
                sql = "select itemid,fieldsetid,is_order from t_sys_table_scheme_item where scheme_id = (" +
                		"select scheme_id from t_sys_table_scheme " +
                		"where submoduleid = '"+scheme_id+"' and is_share = '1') " +
                		"and is_display = '1' and is_order<>0 order by displayorder";
                    
             rs = dao.search(sql);
             while(rs.next()) {
                 String itemid = rs.getString("itemid");
                 String fieldsetid = rs.getString("fieldsetid");
                 String isOrder = rs.getString("is_order");
                 
                 if(StringUtils.isNotEmpty(fieldsetid) && fieldsetid.startsWith("A"))
                     orderSql += dbname + fieldsetid + "." + itemid;
                 else
                     orderSql += itemid;
                 
                 if("1".equalsIgnoreCase(isOrder))
                     orderSql += ",";
                 
                 if("2".equalsIgnoreCase(isOrder))
                     orderSql += " desc,";
             }
            
             if(orderSql.endsWith(","))
                 orderSql = orderSql.substring(0, orderSql.length() - 1);
             
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return orderSql;
    }
    
    /**
     * 获取信息公示备用指标
     * @param columns 
     * @param columns 
     * @return
     */
    public String getNoticeField(ArrayList columns){
    	String defaultItem = "A0101,A0121,A0127,Z0351,Z0321,Z0325,";
    	ArrayList exceptItems = new ArrayList();
    	HashMap map = new HashMap();
    	for (Object obj : columns) {
    		String name = obj.getClass().getName();
    		if("java.util.HashMap".equalsIgnoreCase(name)) {
    			ArrayList<ColumnsInfo> list = (ArrayList<ColumnsInfo>) ((HashMap) obj).get("items");
    		    for (int n = 0; n < list.size(); n++) {
    		    	ColumnsInfo info = list.get(n);
    		    	if(ColumnsInfo.LOADTYPE_ONLYLOAD==info.getLoadtype())
    		    		continue;
    		    	if(DataDictionary.getFieldItem(info.getColumnId())==null)
    		    		continue;
    		    	map = new HashMap<String, String>();
    	    		map.put("dataValue", info.getColumnId());
    	    		map.put("dataName", info.getColumnDesc());
    	    		map.put("selected", "0");
    	    		if(defaultItem.indexOf(info.getColumnId().toUpperCase()+",")>-1)
    	    			map.put("selected", "1");
    		    }
    		}else{
    			ColumnsInfo info = (ColumnsInfo) obj;
	    		if(info.getColumnId()==null || ColumnsInfo.LOADTYPE_ONLYLOAD==info.getLoadtype())
	    			continue;
	    		if(DataDictionary.getFieldItem(info.getColumnId())==null)
	    			continue;
	    		map = new HashMap<String, String>();
	    		map.put("dataValue", info.getColumnId());
	    		map.put("dataName", info.getColumnDesc());
	    		map.put("selected", "0");
	    		if(defaultItem.indexOf(info.getColumnId().toUpperCase()+",")>-1)
	    			map.put("selected", "1");
    		}
    		exceptItems.add(map);
		}
		return JSON.toString(exceptItems);
    }
    
    /**
     * 信息公示分组指标
     * @return
     */
    public String getSelectField(){
    	ArrayList list = new ArrayList();
    	String[] items = new String[]{"Z0325","Z0321","Z0351"};
    	FieldItem fieldItem = null;
    	for (String item : items) {
			fieldItem = DataDictionary.getFieldItem(item);
			if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())){
				CommonData data = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
				list.add(data);
			}
		}
		return JSONArray.fromObject(list).toString();
    }
    
    /**
	 * @param flag 
	 * @return
	 */
	private ArrayList<CommonData> getFilterData(String flag) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet search = null;
		ArrayList<CommonData> operationData = new ArrayList<CommonData>();
		HashMap<String, String> map = new HashMap<String, String>();
		//状态自定义过滤类型
		try {
			StringBuffer sql= new StringBuffer();
			sql.append("select distinct codeitemid,custom_name from ");
			if("link".equals(flag)) {
				sql.append(" zp_flow_links lin left join codeitem on node_id=codeitemid ");
				sql.append(" where codeitemid=parentid and codesetid='36' ");
			}
			else {
				sql.append(" zp_flow_status sta left join codeitem on status=codeitemid ");
				sql.append(" where codesetid='36'");
			}
			//系统暂时不支持体检环节
			sql.append(" and parentid<>'09' order by codeitemid asc");
			
			search = dao.search(sql.toString(), new ArrayList());
			CommonData date = new CommonData();
			while(search.next()){
				if(map.get(search.getString("custom_name"))!=null)
					continue;
				map.put(search.getString("custom_name"), "1");
				date = new CommonData(search.getString("custom_name"),search.getString("custom_name"));
				operationData.add(date);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return operationData;
	}
   
}
