package com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import org.apache.axis.utils.StringUtils;
import org.apache.tools.zip.ZipOutputStream;

import javax.sql.RowSet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ExamineeBo {
	Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public ExamineeBo(Connection conn, UserView userview){
    	 this.conn = conn;
         this.dao = new ContentDAO(this.conn);
         this.userview = userview;
    }
    /**
	 * 获取默认查询指标
	 * @return
	 */
	public ArrayList getDefaultQuery(){
		ArrayList<HashMap<String,String>> defaultQuery = new ArrayList<HashMap<String,String>>();
		//查询模板预置
        HashMap<String,String> hm = new HashMap<String,String>();
        String[] itemIds = new String[]{"Z0325","Z0351","A0410","A0405",};
        FieldItem fieldItem = null;
        for(int i = 0;i<itemIds.length;i++){
			fieldItem = DataDictionary.getFieldItem(itemIds[i]);
			if(fieldItem!=null){
				hm = new HashMap<String,String>();
				hm.put("fieldsetid", fieldItem.getFieldsetid());
		        hm.put("itemid", fieldItem.getItemid());
		        hm.put("itemdesc", fieldItem.getItemdesc());
		        hm.put("itemtype", fieldItem.getItemtype());
		        hm.put("codesetid", fieldItem.getCodesetid());
				defaultQuery.add(hm);
			}
        }
        hm = new HashMap<String,String>();
        hm.put("itemid", "hall_id");
        hm.put("itemdesc", "考场号");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        defaultQuery.add(hm);
		return defaultQuery;
	}
	/**
	 * 获取预置查询指标
	 * @return
	 */
	public ArrayList getOptionalQuery(){
		ArrayList<HashMap<String,String>> optionalQuery = new ArrayList<HashMap<String,String>>();
		//查询模板预置
		HashMap<String,String> hm = new HashMap<String,String>();
		try {
			ArrayList<ColumnsInfo> arrayList = getColumnList();
			FieldItem fieldItem = null;
			for (ColumnsInfo info : arrayList) {
				String itemId = info.getColumnId();
				 if("idcard".equalsIgnoreCase(itemId)){    //身份证号 A0177
					 	itemId = "A0177";
			        }else if("major".equalsIgnoreCase(itemId)){ //专业A0410
			        	itemId = "A0410";
			        }else if("degree".equalsIgnoreCase(itemId)){//学问A0405
			        	itemId = "A0405";
			        }
				 if(itemId.contains("hall_id")||itemId.contains("seat_id")||itemId.contains("subject_")){
		        	if("hall_id".equals(itemId)){
		        		hm = new HashMap<String,String>();
		        		hm.put("itemid", itemId);
		        		hm.put("itemdesc", "考场号");
		        		hm.put("itemtype", "A");
		        		hm.put("codesetid", "0");
		        	}else if("seat_id".equals(itemId)){
		        		hm = new HashMap<String,String>();
		        		hm.put("itemid", itemId);
		        		hm.put("itemdesc", "座位号");
		        		hm.put("itemtype", "A");
		        		hm.put("codesetid", "0");
		        	}else if(itemId.contains("subject_") || itemId.contains("subject_".toUpperCase())){
    	        		String coluName = this.getCourseName(itemId.split("_")[1]);
    	        		hm = new HashMap<String,String>();
    	        		hm.put("itemid", itemId);
	        			if(!itemId.contains("_date") && !itemId.contains("_date".toUpperCase())){
	        				hm.put("itemdesc", coluName);
	        				hm.put("codesetid", "79");
	        				hm.put("itemtype", "A");
	        			}else{
	        				hm.put("itemdesc", coluName+"考试时间");
	        				hm.put("codesetid", "0");
	        				hm.put("itemtype", "A");
	        			}
		        	}
		        }else{
		        	fieldItem = DataDictionary.getFieldItem(itemId);
		        	if(fieldItem!=null){
						hm = new HashMap<String,String>();
						hm.put("fieldsetid", fieldItem.getFieldsetid());
				        hm.put("itemid", fieldItem.getItemid());
				        hm.put("itemdesc", fieldItem.getItemdesc());
				        hm.put("itemtype", fieldItem.getItemtype());
				        hm.put("codesetid", fieldItem.getCodesetid());
		        	}
		        }
				if(!"Z0301".equalsIgnoreCase(itemId)&&fieldItem!=null&&fieldItem.isVisible()){
			        optionalQuery.add(hm);
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return optionalQuery;
	}
	
    //获取tableName表中业务字典里所有相关列
    public ArrayList getColumn(String tableName) throws GeneralException{
		ArrayList columnList = new ArrayList();
		try {
			ArrayList fieldList = DataDictionary.getFieldList(tableName,Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item = (FieldItem) fieldList.get(i);
				String colunmName = item.getItemid();
				if (item != null && "1".equals(item.getState()) && "1".equals(item.getUseflag()))
					columnList.add(colunmName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return columnList;
    }
    /**
     * 获取指定表中所有的列名
     * @param tablename
     * @return
     * @throws GeneralException 
     */
    public HashMap getAssignAllColumns(String tablename) throws GeneralException{
    	HashMap res = new HashMap();
    	ArrayList columnList = new ArrayList();
    	ArrayList columnType = new ArrayList();
    	
    	//String sql = "select name from syscolumns where id=(select max(id) from sysobjects where xtype='u' and name='"+tablename+"')";
    	String sql = "select * from "+tablename+" where 1=2";
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql);
    		ResultSetMetaData rsmd = rs.getMetaData();
    		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
    			columnList.add(rsmd.getColumnName(i));
    			columnType.add(rsmd.getColumnTypeName(i));
			}
    		res.put("name", columnList);
    		res.put("type", columnType);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return res;
    }
    /**
     * 获取考生管理列表页所有列
     * @return
     * @throws GeneralException 
     */
    public ArrayList getColumnList() throws GeneralException{
    	ArrayList list = new ArrayList();
        ArrayList columnList = new ArrayList();
        
        TableFactoryBO tableBo = new TableFactoryBO("zp_exam_assign_00001", this.userview, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        
        String itemStr = "A0101`idcard`Z6301`Z0321`Z0325`Z0351`major`degree`hall_id`seat_id`exam_hall_id`hall_name`create_time`create_user`Z6300";
        if(scheme!=null)
        {
        	Integer scheme_str = (Integer)scheme.get("schemeId");
        	int schemeId = scheme_str.intValue();;
        	ArrayList columnConfigLst = tableBo.searchCombineColumnsConfigs(schemeId, null);
        	list = columnConfigLst;
        	list.add("nbase");
        	list.add("A0100");
        	list.add("exam_hall_id"); //考场ID
        }else{
        	list.add("A0101");
        	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
            FieldItem fieldItem = null;
            if(!StringUtils.isEmpty(chk))
            	fieldItem = DataDictionary.getFieldItem(chk);
            if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
            	list.add("idcard");//身份证号
        	list.add("Z6301");//准考证
        	list.add("Z0321");//报考单位
        	list.add("Z0325");//报考部门
        	list.add("Z0351"); //申请职位
        	fieldItem = DataDictionary.getFieldItem("A0410");
        	if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
        		list.add("major");//专业
        	fieldItem = DataDictionary.getFieldItem("A0405");
        	if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
        		list.add("degree");//学历
        	list.add("hall_id"); //考场号
        	list.add("seat_id"); //考场号
        	
            //获取业务字典里所有相关列
        	HashMap res = this.getAssignAllColumns("zp_exam_assign");
            ArrayList zp_exam_assign_column = (ArrayList) res.get("name");
            ArrayList z63_column = this.getColumn("Z63");
            
            for(int i=0;i<zp_exam_assign_column.size();i++){
            	String itemId = (String)zp_exam_assign_column.get(i);
            	if(!itemStr.contains(itemId.toLowerCase()) && !itemStr.contains(itemId.toUpperCase()))
            		list.add(itemId);
            }
            for(int i=0;i<z63_column.size();i++){
            	String itemId = (String)z63_column.get(i);
				if (list.indexOf(itemId.toUpperCase()) < 0
						&& list.indexOf(itemId.toLowerCase()) < 0
						&& (!itemStr.contains(itemId.toLowerCase()) 
						&& !itemStr.contains(itemId.toUpperCase())))
            		list.add(itemId);
            }
        }
            
            try {
            	String mergedesc = "";
            	int mergedescIndex = 0;
                int num = 0;
                String coluName = "";
    	        for (int i = 0; i < list.size(); i++) {
    	        	String itemId = "";
    	        	boolean isedit = false;//是否可以进行编辑
    	        	ColumnsInfo info = new ColumnsInfo();
    	        	FieldItem item = null;
    	        	//当前用户有自定义栏目设置时
    	        	if(scheme!=null){
    	        		if(!"nbase".equals(list.get(i)) && !"A0100".equals(list.get(i)) && !"exam_hall_id".equals(list.get(i)) && !"Z6300".equals(list.get(i))){
    	        			ColumnConfig column = (ColumnConfig)list.get(i);
    	        			itemId = column.getItemid();
    	        			item = DataDictionary.getFieldItem(itemId);
    	        			if(item!=null){
    	        				info = new ColumnsInfo(item);
    	        				info.setColumnDesc(column.getDisplaydesc());
    	        				if("1".equals(column.getIs_lock()))
    	        				{
    	        					info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
    	        				}else if("0".equals(column.getIs_lock()))
    	        				{
    	        					info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
    	        				}
    		    				info.setLoadtype(Integer.parseInt(("".equals((String)column.getIs_lock())||(String)column.getIs_lock()==null)?"0":(String)column.getIs_lock()));
    	        				info.setColumnWidth(column.getDisplaywidth());
    	        				info.setTextAlign(column.getAlign()+"");
    	        				String order = "";
    		    				if("1".equalsIgnoreCase(column.getIs_order()))
    		    					order = "true";
    		    				else
    		    					order = "false";
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
    	        				if(column.getMergedesc()!=null&&column.getMergedesc().length()>0){
    	        					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
    	        					{
    	        						ArrayList tableheadlist = new ArrayList( );
    	        						tableheadlist.add(columnList.get(mergedescIndex-num));
    	        						tableheadlist.add(info);
    	        						HashMap topHead = new HashMap();
    	        						topHead.put("text",mergedesc);
    	        						topHead.put("items", tableheadlist);
    	        						columnList.remove(mergedescIndex-num);//当合并时移除最后一列
    	        						columnList.add(topHead);
    	        						num+=1;
    	        						continue;
    	        					}else{	        						
    	        						mergedesc = column.getMergedesc();
    	        						mergedescIndex = i;
    	        					}
    	        				}
    	        			}
    		        	}else{
    		        		itemId = (String) list.get(i);
    		            	item = DataDictionary.getFieldItem(itemId);	
    		            	info = new ColumnsInfo(item);
    		        	}
    	            }else{
    	            	itemId = (String) list.get(i);
    	            	item = DataDictionary.getFieldItem(itemId,"Z63");
    	            	if(item!=null && itemStr.indexOf(itemId)<0)
    	            	{	            		
    	            		info = new ColumnsInfo(item);
    	            		if("Z6303".equalsIgnoreCase(itemId)){
    	            			info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
    	            			info.setColumnType("A");
    	            			isedit = true;
    	            		}else if("Z0301".equalsIgnoreCase(itemId)){
    	            			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    	            			info.setEncrypted(true);
    	            			info.setColumnType("A");
    	            		}else{
   	            				info.setColumnType(item.getItemtype());    	            				
    	            			if(!"Z6317".equalsIgnoreCase(itemId))
    	            				info.setDecimalWidth(2);
    	            			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
    	            			isedit = true;
    	            		}
    	            	}
    	            }
    	        	if("A0101".equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId);
	        			info.setColumnType("A");
	        			info.setColumnDesc("姓名");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
	        			info.setLocked(true);
    	        	}else if("Z6301".equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId);
    	            	info.setColumnWidth(120);
	        			info.setColumnType("A");
	        			info.setColumnDesc("准考证号");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
	        			info.setOrdertype("1");
    	        	}else if("Z0321".equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId);
	        			info.setColumnType("A");
	        			info.setCodesetId("UN");
	        			info.setCtrltype("3");
	        			info.setNmodule("7");
	        			info.setColumnDesc("报考单位");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
	        			info.setOrdertype("1");
    	            }else if("Z0325".equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId);
	        			info.setColumnType("A");
	        			info.setCodesetId("UM");
	        			info.setCtrltype("3");
	        			info.setNmodule("7");
	        			info.setColumnDesc("报考部门");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
	        			info.setOrdertype("1");
    	            }else if(itemId.contains("subject_") || itemId.contains("subject_".toUpperCase())){
    	        		coluName = this.getCourseName(itemId.split("_")[1]);
	        			info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
	        			if(!itemId.contains("_date") && !itemId.contains("_date".toUpperCase())){
	        				info.setColumnDesc(coluName);
	        				info.setCodesetId("79");
	        			}else
    	        			info.setColumnDesc(coluName+"考试时间");
	        			info.setRendererFunc("examinee_me.dealShowResult");
	        			info.setTextAlign("center");
    	            }else if("idcard".equalsIgnoreCase(itemId) || "idcard".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setColumnDesc("证件号码");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("Z0351".equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId);
	        			info.setColumnType("A");
	        			info.setColumnDesc("申请职位");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
	        			info.setOrdertype("1");
    	            }else if("major".equalsIgnoreCase(itemId) || "major".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setCodesetId("AI");
	        			info.setColumnDesc("专业");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("degree".equalsIgnoreCase(itemId) || "degree".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setCodesetId("AM");
	        			info.setColumnDesc("学历");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("positionName".equalsIgnoreCase(itemId) || "positionName".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setColumnDesc("申请职位");
	        			info.setFromDict(true);
	        			info.setFilterable(true);
    	            }else if("hall_id".equalsIgnoreCase(itemId) || "hall_id".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
	        			info.setColumnDesc("考场号");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("hall_name".equalsIgnoreCase(itemId) || "hall_name".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setColumnDesc("考场名称");
	        			info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("exam_hall_id".equalsIgnoreCase(itemId) || "exam_hall_id".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("N");
	        			info.setColumnDesc("考场编号");
	        			info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if("seat_id".equalsIgnoreCase(itemId) || "seat_id".toUpperCase().equalsIgnoreCase(itemId)){
    	            	info.setColumnId(itemId.toLowerCase());
	        			info.setColumnType("A");
	        			info.setColumnDesc("座位号");
	        			info.setFromDict(false);
	        			info.setFilterable(true);
    	            }else if ("nbase".equalsIgnoreCase(itemId)
								|| "A0100".equalsIgnoreCase(itemId)
								|| "nbase".toUpperCase().equalsIgnoreCase(itemId)
								|| "Z6300".equalsIgnoreCase(itemId)) {
					info = new ColumnsInfo(item);
					info.setColumnId(itemId.toLowerCase());
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					info.setEncrypted(true);
				}
    	        	if(!isedit)
    	        		info.setEditableValidFunc("false");//是否可以编辑
    	            columnList.add(info);
    	        }//for end
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
        return columnList;
    }
    /**
     * 显示考生列表前判断是否有科目，即代码79是否有值，有的话将一级代码codeitemid动态创建考场安排表中的subject_codeitemid和subject_codeitemid_date
     * @throws GeneralException 
     */
    public boolean isHasSubjects() throws GeneralException{
    	boolean res = false;
    	String sql = "select * from codeitem where codesetid='79' and codeitemid = parentid and invalid=1 order by codeitemid asc";
    	ArrayList currentItem= new ArrayList();
    	//获取zp_exam_assign所有列名
    	HashMap zp_exam_assign = this.getAssignAllColumns("zp_exam_assign");
    	ArrayList columns = (ArrayList) zp_exam_assign.get("name");
    	
    	RowSet rs = null;
    	String codeitemid = "";
    	DbWizard dbWizard = new DbWizard(this.conn);
    	Table table = new Table("zp_exam_assign");
    	
    	Field temp = null;
    	try{
    		rs = dao.search(sql);
    		//插入字段
    		while(rs.next()){
    			res = true;
    			codeitemid = rs.getString("codeitemid");
    			currentItem.add("subject_"+codeitemid);
    			currentItem.add("subject_"+codeitemid+"_date");
    			//防止插入的字段是大写
    			currentItem.add("subject_".toUpperCase()+codeitemid);
    			currentItem.add(("subject_"+codeitemid+"_date").toUpperCase());
    			
    			if(columns.contains("subject_"+codeitemid) || columns.contains("SUBJECT_"+codeitemid))
    				continue;
    			
    			temp = new Field("subject_"+codeitemid, "考试科目");
    	        temp.setDatatype(DataType.STRING);
    	        temp.setLength(30);
    	        table.addField(temp);
    	        
    	        temp = new Field("subject_"+codeitemid+"_date", "考试科目日期");
    	        temp.setDatatype(DataType.STRING);
    	        temp.setLength(30);
    	        table.addField(temp);
    		}
    		if(table.getCount()>0)
    			dbWizard.addColumns(table);
    		
    		table.clear();

    		//删除不存在的指标字段
    		columns.removeAll(currentItem);
    		String colName = "";
    		for (int i = 0; i < columns.size(); i++) {
    			colName = (String) columns.get(i);
    			if(colName.contains("subject_".toLowerCase()) || colName.contains("subject_".toUpperCase())){
    				temp = new Field(colName, "");
    				table.addField(temp);
    			}
			}
    		if(table.getCount()>0)
    			dbWizard.dropColumns(table);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return res;
    }
    /**
     * 获取考试课程名称
     * @param codeitemid
     * @return
     */
    public String getCourseName(String codeitemid){
    	String res = "";
    	String sql = "select * from codeitem where codesetid='79' and codeitemid = '"+codeitemid+"'";
    	
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql);
    		if(rs.next())
    			res = rs.getString("codeitemdesc");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return res;
    }
    /**
     * @throws GeneralException 
     * @param flag 
     * 
     * @Title: getExamineeSql   
     * @Description:    用来获取列表数据的sql语句
     * @param columnList
     * @return String    
     * @throws
     */
    public String getExamineeSql(ArrayList columnList) throws GeneralException {
    	//z63所有列
    	 ArrayList z63_column = this.getColumn("Z63");
    	//获取招聘人员库
    	RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String nbase="";  //应聘人员库
		if(vo!=null)
			nbase=vo.getString("str_value");
		
        StringBuffer strsql = new StringBuffer("select ");
        
        String publishSql = "";
        
        StringBuffer columnsSql = new StringBuffer(); // 字段 sql
        try{
        for (int i = 0; i < columnList.size(); i++) {
        	String infoId = "";
        	Object o = columnList.get(i);
        	if(o instanceof HashMap){
        		HashMap hm = (HashMap) columnList.get(i);
        		ArrayList list = (ArrayList)hm.get("items");
        		for(int j=0;j<list.size();j++){
        			ColumnsInfo info = (ColumnsInfo) list.get(j);
            		infoId = info.getColumnId();
                    columnsSql.append("exam."+infoId+" ,");
        		}
        	}else{        		
        		ColumnsInfo info = (ColumnsInfo) columnList.get(i);
        		infoId = info.getColumnId();
        		if("idcard".equalsIgnoreCase(infoId)){
        			//身份证
        			String temp1 = getSpecColumn(infoId);
        			columnsSql.append(temp1+" ,");
        			
        		}else if("major".equalsIgnoreCase(infoId)){
        			String temp2 = getSpecColumn(infoId);
        			columnsSql.append(temp2+" ,");
        		}else if("degree".equalsIgnoreCase(infoId)){
        			String temp3 = getSpecColumn(infoId);
        			columnsSql.append(temp3+" ,");
        		}else if(z63_column.contains(infoId) && !"A0101".equals(infoId.toUpperCase())){
        			columnsSql.append("z."+infoId+",");
        		}else{
        			columnsSql.append("exam."+infoId+" ,");
        		}
        	}
            
            
        }
        if(columnsSql.length()>0)
            columnsSql.setLength(columnsSql.length()-1);
        strsql.append(columnsSql);
        strsql.append(" from zp_exam_assign exam ");
        strsql.append(" left join z63 z on exam.nbase = z.nbase and exam.A0100 = z.A0100 and exam.z0301 = z.z0301 ");
        strsql.append(publishSql);
        strsql.append("left join "+nbase+"A01 a01 on exam.a0100=a01.a0100 ");
        strsql.append("left join ( select a1.* from "+nbase+"A04 a1,(select max(i9999) i9999,a0100 from "+nbase+"A04 group by a0100) a2 where a1.a0100=a2.a0100 and a1.i9999=a2.i9999) a04 on exam.a0100=a04.a0100 where ");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return strsql.toString();
    }
    
    /**
     * 
     * @Title: getSpecColumn   
     * @Description:    获取特殊列的字段
     * @param infoId
     * @return String    
     * @throws
     */
    private String getSpecColumn(String infoId) {
        String str = "";
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
        if("idcard".equalsIgnoreCase(infoId)){    //身份证号 A0177
            str = Sql_switcher.isnull(Sql_switcher.numberToChar("a01."+chk), "'未知'")+" as idcard";
        }else if("major".equalsIgnoreCase(infoId)){ //专业A0410
            str = Sql_switcher.isnull(Sql_switcher.numberToChar("a04.A0410"), "'未知'")+" as major";
        }else if("degree".equalsIgnoreCase(infoId)){//学问A0405
        	 str = Sql_switcher.isnull("a04.A0405", "'未知'")+" as degree";
        }
        
        return str;
    }
    
    /**
     * 查询功能按钮
     * @param isModule 
     * @return
     */
    public ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();
	    if(userview.hasTheFunction("3110807")){
	        buttonList.add("-"); 
	        if(userview.hasTheFunction("311080702"))
	            buttonList.add(newButton("删除考生",null,"examinee_me.deleteExaminees",null,"true"));
	        if(userview.hasTheFunction("311080703")){
	        	buttonList.add("-"); 
	        	//buttonList.add(newButton("分派考场",null,"Global.deletePosition",null,"true"));
	        	buttonList.add(newButton("清除考场分派记录",null,"examinee_me.clearExamHallRecord",null,"true"));
	        }
	        if(userview.hasTheFunction("311080704")){
	        	buttonList.add("-"); 
	        	buttonList.add(newButton("生成准考证号码",null,"examinee_me.isExitExamNo",null,"true"));
	        }
	        if(userview.hasTheFunction("311080711")){
	        	buttonList.add("-"); 
	        	buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.save"),ButtonInfo.FNTYPE_SAVE,"ZP0000002566"));
	        }
	        /*if(userview.hasTheFunction("311080705"))
	        	buttonList.add(newButton("打印准考证",null,"examinee_me.printExamNoCards",null,"true"));
	      
	        if(userview.hasTheFunction("311080709")){
	        	buttonList.add("-"); 
	        	buttonList.add(newButton("导入成绩",null,"examinee_me.importData",null,"true"));
	        }
	        if(userview.hasTheFunction("311080707")){
	        	buttonList.add("-"); 
	        	buttonList.add(newButton("计算总分和排名",null,"examinee_me.deleteExaminees",null,"true"));
	        }
	        if(userview.hasTheFunction("311080708")){
	        	buttonList.add("-"); 
	        	buttonList.add(newButton("批量修改考试时间",null,"examinee_me.updateExamTime",null,"true"));
	        }*/
	    }
	    ButtonInfo queryBox = new ButtonInfo();
	    queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
	    queryBox.setFunctionId("ZP0000002556");
	    queryBox.setText("请输入姓名、准考证号、报考单位、报考部门、报考职位或考场号...");
	    buttonList.add(queryBox);
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
    /**
     * 查询条件sql
     * @param flag 搜索位置标识      2：查询栏    3、招聘批次
     * @param batchId 批次id
     * @param searchStr 当flag=2时，00：全部；01：考场未安排；02：已安排；03：准考证未生成；04：已生成；05：成绩未录入；06：已录入
     * @return
     * @throws GeneralException 
     */
    public String getWhere(String flag,String searchStr,String batchId) throws GeneralException{
    	StringBuffer res = new StringBuffer(" 1=1");
    	try{
	    	if(StringUtils.isEmpty(flag)&&StringUtils.isEmpty(searchStr) || StringUtils.isEmpty(batchId))
	    		return res.toString();
	    	if("2".equals(flag)){
	    		HashMap z63Columns = this.getAssignAllColumns("Z63");
	    		ArrayList z63 = (ArrayList) z63Columns.get("name");
	    		ArrayList z63Type = (ArrayList) z63Columns.get("type");
	    		String temp = "";
	    		String[] searchIds = searchStr.split(",");
	    		
	    		for(int k = 0;k<searchIds.length;k++){
	    			if(StringUtils.isEmpty(searchIds[k]))
	    				continue;
	    			
	    			if("01".equals(searchIds[k]))
	    				res.append(" and exam.hall_id is null and exam.exam_hall_id is null");
	    			else if("02".equals(searchIds[k]))
	    				res.append(" and exam.hall_id is not null and exam.exam_hall_id is not null");
	    			else if("03".equals(searchIds[k]))
	    				res.append(" and z.Z6301 is null");
	    			else if("04".equals(searchIds[k]))
	    				res.append(" and z.Z6301 is not null");
	    			else if("05".equals(searchIds[k])){
	    				res.append(" and (");
	    				for (int i = 0; i < z63.size(); i++) {
	    					temp = (String)z63Type.get(i);
	    					//除了主键序号外的所有数值型字段
	    					if(!"Z63000".equals(z63.get(i)) && "float,double,int,number,numeric".contains(temp.toLowerCase()))
	    						res.append("z."+z63.get(i)+" is null and ");
	    				}
	    				res.setLength(res.length()-4);
	    				res.append(" )");
	    			}
	    			else if("06".equals(searchIds[k])){
	    				res.append(" and (");
	    				for (int i = 0; i < z63.size(); i++) {
	    					temp = (String)z63Type.get(i);
	    					//除了主键序号外的所有数值型字段
	    					if(!"Z63000".equals(z63.get(i)) && "float,double,int,number,numeric".contains(temp.toLowerCase()))
	    						res.append("z."+z63.get(i)+" is not null or ");
	    				}
	    				res.setLength(res.length()-4);
	    				res.append(" )");
	    			}else if("00".equals(searchIds[k])){
	    				//查询指定的批次(查询全部批次时不用添加条件)
	    				if(!"all".equalsIgnoreCase(batchId.trim()))
	    					res.append(" and exam.z0301 in (select z0301 from z03 where z0101 ='"+batchId.trim()+"')");
	    			}
	    		}
	    		if(!StringUtils.isEmpty(batchId) && !"all".equalsIgnoreCase(batchId))//未选择批次的时候且不是选择查询全部 不用添加批次筛选
	    			res.append(" and exam.z0301 in (select z0301 from z03 where z0101 = '"+batchId.trim()+"')");	
	    	}else if("3".equals(flag)){
	    		if("all".equalsIgnoreCase(searchStr))
	    			return res.toString();
    			res.append(" and exam.z0301 in (select z0301 from z03 where z0101 = '"+searchStr.trim()+"')");
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return res.toString();
    }
   
    /**
     * 删除考生
     * @param a0100s
     * @param nbases
     * @param z0301s
     * @throws GeneralException 
     */
    public void deleteExaminee(String[] a0100s,String[] nbases,String[] z0301s) throws GeneralException{
    	String sql = "delete from zp_exam_assign where a0100=? and nbase=? and z0301=? ";
    	String z63Sql = "delete from Z63 where a0100=? and nbase=? and z0301=? ";
    	
    	ArrayList sqlList = new ArrayList();
    	sqlList.add(sql);
    	sqlList.add(z63Sql);
    	ArrayList values = new ArrayList();
    	
    	try{
    		for (int i = 0; i < a0100s.length; i++) {
    			values.clear();
				values.add(PubFunc.decrypt(a0100s[i]));
				values.add(PubFunc.decrypt(nbases[i]));
				//针对还未申请职位的考生
				if(StringUtils.isEmpty(z0301s[i]))
					values.add("");
				else
					values.add(PubFunc.decrypt(z0301s[i]));
				
				this.updatePeopleNumOfHall(values);
				dao.delete(sql, values);
				dao.delete(z63Sql, values);
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 更新考场中分派人数
     * @param values
     * @throws GeneralException
     */
    public void updatePeopleNumOfHall(ArrayList values) throws GeneralException{
    	//更新考场中已分配人数
    	String hallSql = "update zp_exam_hall set people_num = people_num - 1 where id=(select exam_hall_id from zp_exam_assign where A0100=? and nbase=? and Z0301=?)";
    	try{
    		dao.update(hallSql, values);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 获取指定批次号下所有考场中的考生详细信息及生成的流水号
     * @param batchId    批次号
     * @return
     * @throws GeneralException
     */
    public ArrayList createExamNo(String batchId) throws GeneralException{
    	ArrayList res = new ArrayList();
    	
    	String sql = "select * from zp_exam_hall hall,zp_exam_assign ass " +
    			"where  ass.exam_hall_id=hall.id and hall.batch_id=? order by hall.hall_id,ass.seat_id asc";
    	ArrayList values = new ArrayList();
    	values.add(batchId);
    	
    	RowSet rs = null;
    	HashMap hm = null;
    	try{
    		StringBuffer flowNum = new StringBuffer("");
    		
    		rs = dao.search(sql,values);
    		while(rs.next()){
    			hm = new HashMap();
    			flowNum.setLength(0);
    			flowNum.append("A"+batchId);
    			//获取座位号,座位号作为准考证号的一部分,避免随机分配座位号时，座位号与准考证号不一致的问题。
    			String seat_id = rs.getString("seat_id");
    			hm.put("a0100", rs.getString("a0100"));
    			hm.put("nbase", rs.getString("nbase"));
    			hm.put("z0301", rs.getString("z0301"));
    			//拼接四位流水号
    			if(seat_id.length() == 1)
    				flowNum.append("000"+seat_id);
    			else if(seat_id.length() == 2)
    				flowNum.append("00" + seat_id);
    			else if(seat_id.length() == 3)
    				flowNum.append("0" + seat_id);
    			else
    				flowNum.append(seat_id);
    			hm.put("flowNum", flowNum.toString());
    			res.add(hm);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return res;
    }
    /**
     * 更新z63表中准考证号
     * @param infos  通过createExamNo返回的list  元素是hashmap
     * @return
     * @throws GeneralException
     */
    public void upateExamNo(String batchId) throws GeneralException{
    	//如果存在存储过程那么调用存储过程生成准考证号
    	if(this.isExistPro("CREATE_ZP_EXAMNO")){
    		String sqlCall = "{call  CREATE_ZP_EXAMNO(?)}";
            CallableStatement cstmt = null; // 存储过程
            try {
                cstmt = this.conn.prepareCall(sqlCall);
                //招聘批次
                cstmt.setString(1, batchId);
                cstmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new GeneralException("", "调用存储过程出错！" + e.getMessage(), "", "");
            } finally {
                PubFunc.closeDbObj(cstmt);
            }
    		
    	}else{
    		
	    	String sql = "update Z63 set z6301 = ? where a0100=? and nbase=? and z0301=?";
	    	ArrayList values = new ArrayList();
	    	
	    	ArrayList infos = this.createExamNo(batchId);
	    	HashMap hm = null;
	    	try{
	    		for (int i = 0; i < infos.size(); i++) {
	    			hm = (HashMap) infos.get(i);
	    			values.clear();
	    			values.add((String)hm.get("flowNum"));
	    			values.add((String)hm.get("a0100"));
	    			values.add((String)hm.get("nbase"));
	    			values.add((String)hm.get("z0301"));
	    			dao.update(sql, values);
				}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(e);
	    	}
    	}
    }
    /**
     * 判断批次下是否有已生成准考证的考生
     * @param batch_id
     * @return
     * @throws GeneralException
     */
    public boolean isExitExamNo(String batch_id) throws GeneralException{
    	boolean res = false;
    	
    	String sql = "select * from zp_exam_assign ass,zp_exam_hall hall,Z63 z63 " +
    			" where ass.exam_hall_id = hall.id and ass.A0100 = z63.A0100 and ass.nbase = z63.nbase and ass.Z0301 = z63.Z0301 and z63.Z6301 is not null and hall.batch_id='"+batch_id+"'";
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql);
    		if(rs.next())
    			res = true;
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return res;
    }
    /**
     * 清楚考场分派记录
     * @param a0100s
     * @param nbases
     * @param z0301s
     * @throws GeneralException
     */
    public void clearHallRecord(String[] a0100s,String[] nbases,String[] z0301s) throws GeneralException{
    	//考生sql
    	String assignSql = "update zp_exam_assign set exam_hall_id = null,hall_id = null,hall_name = null,seat_id=null  where a0100=? and nbase=? and z0301=? ";
    	//清楚考试科目时间
    	String assignTimeSql = this.getClearExamTimeSql();
    	//招聘考试成绩表
    	String z63Sql = "update Z63 set Z6301 = null  where a0100=? and nbase=? and z0301=? ";
    	//清楚考试成绩和排名
    	String z63ScoreSql = this.getZ63ScoreSql();
    	
    	ArrayList values = new ArrayList();
    	try{
    		for (int i = 0; i < a0100s.length; i++) {
    			values.clear();
				values.add(PubFunc.decrypt(a0100s[i]));
				values.add(PubFunc.decrypt(nbases[i]));
				//针对还未申请职位的考生
				if(StringUtils.isEmpty(z0301s[i]))
					values.add("");
				else
					values.add(PubFunc.decrypt(z0301s[i]));
				//如果已清除过记录则继续
				if(this.isAlreadyCleared(values))
					continue;
				
				this.updatePeopleNumOfHall(values);
				dao.update(assignSql, values);
				dao.update(assignTimeSql, values);
				dao.update(z63Sql, values);
				//不删除排名
//				dao.update(z63ScoreSql, values);
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 判断指定人员是否已经清除过考场分配记录
     * @param values
     * @return
     * @throws GeneralException
     */
    public boolean isAlreadyCleared(ArrayList values) throws GeneralException{
    	boolean res = false;
    	String sql = "select * from zp_exam_assign where  a0100=? and nbase=? and z0301=? and exam_hall_id = null and hall_id = null and hall_name = null and seat_id=null";
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql, values);
    		if(rs.next())
    			res = true;
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return res;
    }
    /**
     * 获取zp_exam_assign中所有科目考试时间的字段
     * @return
     * @throws GeneralException
     */
    public ArrayList getExamTime() throws GeneralException{
    	ArrayList timeList = new ArrayList();
    	
    	ArrayList courseItem = this.getAllExamSub();
    	CommonData commonData = null; 
    	for (int i = 0; i < courseItem.size(); i++) {
    		commonData = (CommonData) courseItem.get(i);
    		timeList.add("subject_"+commonData.getDataValue()+"_date");
		}
    	return timeList;
    }
    /**
     * 获取清除z63中所有科目成绩的sql语句
     * @return
     * @throws GeneralException
     */
    public String getZ63ScoreSql() throws GeneralException{
    	StringBuffer res = new StringBuffer("update z63 set ");
    	
    	try{
	    	HashMap z63Columns = this.getAssignAllColumns("Z63");
			ArrayList z63 = (ArrayList) z63Columns.get("name");
			ArrayList z63Type = (ArrayList) z63Columns.get("type");
			
			String temp = "";
			for (int i = 0; i < z63.size(); i++) {
				temp = (String)z63Type.get(i);
				//除了主键序号外的所有数值型字段
				if(!"Z63000".equals(z63.get(i))
						&& ("float,double,int,number".contains(temp.toLowerCase()) 
								|| "float,double,int,number".contains(temp.toUpperCase()) ))
					res.append(z63.get(i)+" = null,");
			}
			res.setLength(res.length()-1);
			res.append(" where a0100=? and nbase=? and z0301=? ");
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return res.toString();
    }
    /**
     * 获取zp_exam_assign  清楚考生所有科目考试时间sql
     * @return
     * @throws GeneralException
     */
    public String getClearExamTimeSql() throws GeneralException{
    	StringBuffer sql = new StringBuffer("update zp_exam_assign set ");
    	
    	ArrayList timeList= null;
    	try{
    		timeList = this.getExamTime();
    		
    		String temp = "";
    		for (int i = 0; i < timeList.size(); i++) {
    			temp = (String) timeList.get(i);
    			sql.append(temp+"=null,");
			}
    		sql.setLength(sql.length()-1);
    		sql.append("  where a0100=? and nbase=? and z0301=? ");
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return  sql.toString();
    }
    /**
     * 获取所有考试科目(一级科目)
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllExamSub() throws GeneralException{
    	ArrayList res = new ArrayList();
		
    	String sql ="select codeitemid,codeitemdesc from codeitem where codesetid='79' and invalid=1 and parentid = codeitemid";
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql);
    		
    		CommonData commonData = null;
    		while(rs.next()){
    			commonData = new CommonData();
    			commonData.setDataName(rs.getString("codeitemdesc") + "考试时间");
    			commonData.setDataValue(rs.getString("codeitemid"));
    			res.add(commonData);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return res;
    }
    /**
     * 批量修改考试时间
     * @param batch_id
     * @param subId
     * @param examTime
     * @throws GeneralException
     */
    public void updateExamTime(String batch_id,String subId,String examTime,String updateTextId,String codeItemId,String a0100s,String nbases,String z0301s) throws GeneralException{
        StringBuilder sql = new StringBuilder();
        
        
        FieldItem item=DataDictionary.getFieldItem(subId.toLowerCase());
		if(item==null) {//修改考试时间的
			//可能是考试科目的代码值，设置为时间
			String itemCode = AdminCode.getCodeName("79", subId);
			if(!StringUtils.isEmpty(itemCode)) {
				String tableFilterSql = getPageFilterSql(a0100s,nbases,z0301s,"zp_exam_assign");//根据页面显示的进行查询
				sql.append("update zp_exam_assign");
		        sql.append(" set subject_").append(subId).append("_date='").append(examTime).append("'");
		        sql.append(tableFilterSql);
		        sql.append(" and subject_").append(subId).append(" is not null");
		        if(Sql_switcher.searchDbServer()==Constant.MSSQL){
		        	sql.append(" and subject_").append(subId).append(" <> '' ");
		        }
		        sql.append(" and zp_exam_assign.z0301 in (select z03.z0301");
		        sql.append(" from z03 z03 left join zp_exam_assign ass");
		        sql.append(" on z03.z0301=ass.z0301");
		        sql.append(" where z03.z0101='").append(batch_id).append("')");
			}
		}else {
			String tableFilterSql = getPageFilterSql(a0100s,nbases,z0301s,"z63");//根据页面显示的进行查询
			sql.append("update z63 set " + item.getItemid() + " = '" + updateTextId + "'" + tableFilterSql);
		}
    	
    	try{
    		dao.update(sql.toString());
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    
    /**
     * 可能是手动选择的，也可能是根据查询框得到的，得到限制的sql
     * @param a0100s
     * @param nbases
     * @param z0301s
     * @return
     */
    private String getPageFilterSql(String a0100s,String nbases,String z0301s,String otheName) {
    	String tableSql = "";
    	StringBuffer sbf = new StringBuffer();
    	if(!StringUtils.isEmpty(a0100s)) {//通过点击选择人员修改
    		String[] a0100Array = a0100s.split(",");
    		String[] nbaseArray = nbases.split(",");
    		String[] z0301Array = z0301s.split(",");
    		sbf.append(" where 1=2");
    		for(int i = 0; i < a0100Array.length; i++) {
    			//根据条件确定这些人
    			sbf.append(" or ");
    			if(a0100Array.length >1 && i == 0)
    				sbf.append(" ( ");
    			sbf.append(" (a0100='" + PubFunc.decrypt(a0100Array[i]) + "' and nbase='" + PubFunc.decrypt(nbaseArray[i]) + "' and z0301='" + PubFunc.decrypt(z0301Array[i]) + "')");
    		}
    		if(a0100Array.length >1)
				sbf.append(" ) ");
    	}else {
    		TableDataConfigCache tableCache = (TableDataConfigCache)this.userview.getHm().get("zp_exam_assignList");//查询框和表格查询的sql
			tableSql = tableCache.getTableSql();
			tableSql = "select nbase,a0100,z0301 from ("+tableSql+") myGridData where 1=1 ";
			if(!StringUtils.isEmpty(tableCache.getQuerySql()))
				tableSql = tableSql+tableCache.getQuerySql();
			sbf.append(" where EXISTS ( select a0100 from (" + tableSql + ") filSql");
			sbf.append(" where " + otheName + ".nbase = filSql.nbase and " + otheName + ".a0100 = filSql.a0100 and " + otheName + ".z0301 = filSql.z0301)");
    	}
    	return sbf.toString();
    }
    /**
     * 得到准考证模板id
     * @return
     * @throws GeneralException
     */
    public String getExamCardId() throws GeneralException{
    	//获取准考证模板
    	ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
    	HashMap map=xmlBo.getAttributeValues();
    	String admissionCard="#";
        if(map.get("admissionCard")!=null&&!"".equals((String)map.get("admissionCard")))
        {
            admissionCard=(String)map.get("admissionCard");
        }
        return admissionCard;
    }
    /**
     * 将多个文件压缩到压缩包中
     * @param inFileName  需要压缩文件的路径
     * @param outFileName  压缩包名称，需带后缀名
     * @throws Exception
     */
    public  void  inputFilesToZip(String[] inFileName,String outFileName)throws   Exception{
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        ParameterSetBo paramBo = new ParameterSetBo(this.conn);
		try{
			//压缩包文件流必须放在循环外部，这样压缩的文件才会在同一个压缩包里
            fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outFileName);
            bos = new BufferedOutputStream(fos, 1024);
            zos = new ZipOutputStream(bos);
            //解决中文乱码问题
			zos.setEncoding("GBK");
			File file = null;
			for (int i = 0; i < inFileName.length; i++) {
				file = new File(inFileName[i]);
				
				//将文件压入压缩包
				if(file.isFile())
					paramBo.fileToZip(file,zos,true);  
				else if(file.isDirectory())
					paramBo.directoryToZip(file, zos);
				//将压入压缩包后的原文件（临时文件）删除
				file.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
    		PubFunc.closeResource(zos);
    		PubFunc.closeResource(bos);
    		PubFunc.closeResource(fos);
    	}
    }
    /**
     * 判断给定的字段是否处于给定表中，且是否属于数值型字段
     * @param tableName
     * @param fieldName
     * @return 0 代表不在这张表    1代表是该表中的数值型字段        2是该表字段，但非数值型
     * @throws GeneralException
     */
    public String isNumberField(String tableName,String fieldName) throws GeneralException{
    	HashMap z63Columns = this.getAssignAllColumns(tableName);
		ArrayList z63 = (ArrayList) z63Columns.get("name");
		ArrayList z63Type = (ArrayList) z63Columns.get("type");
		
		if(!z63.contains(fieldName.toLowerCase()) && !z63.contains(fieldName.toUpperCase()))
			return "0";
		
		String type = z63.indexOf(fieldName.toUpperCase()) >= 0 ? 
				  (String) z63Type.get(z63.indexOf(fieldName.toUpperCase()))
				: (String) z63Type.get(z63.indexOf(fieldName.toLowerCase()));
		if("float,double,int,number".contains(type.toLowerCase())
				|| "float,double,int,number".contains(type.toUpperCase()))
			return "1";
		else 
			return "2";
    }
    /**
     * 返回时分数据
     * @return
     */
    public HashMap generateHourMins(){
    	HashMap res = new HashMap();
    	CommonData commonData = null;
    	
    	ArrayList temList = new ArrayList();
    	//时
    	for(int i = 0;i<24;i++){
    		commonData = new CommonData();
    		if(i < 10){
    			commonData.setDataValue("0"+i);
    			commonData.setDataName("0"+i);
    		}else{ 
    			commonData.setDataValue(i+"");
    			commonData.setDataName(i+"");
    		}
    		temList.add(commonData);
    	}
    	res.put("hour",JSONArray.fromObject(temList));
    	
    	//分
    	ArrayList temList1 = new ArrayList();
    	for (int j = 0; j < 59; j++) {
			if(j%5 == 0){
				commonData = new CommonData();
				if(j<10){
					commonData.setDataValue("0"+j);
					commonData.setDataName("0"+j);
				}else{
					commonData.setDataValue(j+"");
	    			commonData.setDataName(j+"");
				}
				temList1.add(commonData);
			}
		}
    	res.put("minute",JSONArray.fromObject(temList1));
    	
    	return res;
    }
    
    /**
     * 判断是否存在某个存储过程
     * 
     * @return
     */
    private boolean isExistPro(String proName) {
        boolean isExists = false;

        StringBuffer sql = new StringBuffer();
        if (Sql_switcher.searchDbServer() == Constant.ORACEL)
            sql.append("select * from user_objects where object_name = '" + proName.toUpperCase() + "'");
        else if (Sql_switcher.searchDbServer() == Constant.MSSQL)
            sql.append("select * from dbo.sysobjects where id = object_id(N'[dbo].[" + proName + "]')");

        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        try {
            rs = dao.search(sql.toString());
            isExists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return isExists;
    }
    
    /**
     * 获取所有成绩子集内容
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllExam(String tableName) throws GeneralException{
    	ArrayList res = new ArrayList();
    	ArrayList list = new ArrayList();
    	ArrayList listHidden = new ArrayList();
    	RowSet rs = null;
    	String itemstr = "nbase,A0100,A0101,Z0321,Z0325,Z0301,Z0351,Z6301";//Z63子集不能修改的字段
    	try{
    		//找出栏目设置表中的共享或者非共享
    		String strSalarySql= "select itemid,is_display from t_sys_table_scheme_item where scheme_id = ";
    		
    		strSalarySql+= Sql_switcher.isnull(" (select scheme_id from t_sys_table_scheme where submoduleid = 'zp_exam_assignList' and is_share = '0' and username = '" + this.userview.getUserName() + "') "
    				, " (select scheme_id from t_sys_table_scheme where submoduleid = 'zp_exam_assignList' and is_share = '1' ) ");
    		
    		strSalarySql+= "  order by displayorder";
    		rs=dao.search(strSalarySql);
    		
    		while(rs.next()){
    			if("1".equals(rs.getString("is_display")))
    				list.add(rs.getString("itemid").toUpperCase());//找出栏目设置中所有显示的codeitemid，为了后面新增的指标可以正确的添加仅批量修改，而不是所有不显示也添加了
    			else
    				listHidden.add(rs.getString("itemid").toUpperCase());//找出栏目设置中所有不显示的codeitemid
    		}
    		
    		CommonData commonData = null;
    		ArrayList fieldList = DataDictionary.getFieldList(tableName,Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item = (FieldItem) fieldList.get(i);
				String colunmName = item.getItemid();
				//不再上面的不能修改字段中
				if (item != null && "1".equals(item.getState()) && "1".equals(item.getUseflag()) && itemstr.indexOf(item.getItemid().toUpperCase()) == -1) {
					//得根据栏目设置，否则栏目设置不显示的就没必要修改了
					if(list.size() == 0 || list.indexOf(item.getItemid().toUpperCase()) != -1) {//没有栏目设置或者由栏目设置的时候list不为空，如果这个在栏目设置中则插入
						commonData = new CommonData();
		    			commonData.setDataName(item.getItemdesc());
		    			commonData.setDataValue(item.getItemid());
		    			res.add(commonData);
					}else if(listHidden.indexOf(item.getItemid().toUpperCase()) == -1){//对于有栏目设置，新增了指标之后不再栏目设置中的也添加批量修改（不显示的不能添加）
						commonData = new CommonData();
		    			commonData.setDataName(item.getItemdesc());
		    			commonData.setDataValue(item.getItemid());
		    			res.add(commonData); 
					}
				}
			}
    		res.addAll(getAllExamSub());
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return res;
    }
    
    /**
     * 批量修改的时候如果是代码类型的时候获取所对应的代码的集合
     * @param itemid
     * @return
     * @throws GeneralException
     */
    public ArrayList<CommonData> getcodeItemList(String itemid) throws GeneralException {
    	ArrayList<CommonData> list=new ArrayList<CommonData>();
		RowSet frowset = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			if(item==null) {
				//可能是考试科目的代码值，设置为时间
				String itemCode = AdminCode.getCodeName("79", itemid);
				if(!StringUtils.isEmpty(itemCode)) {
					list.add(new CommonData("D","D"));
					return list;
				}else 
					return null;
			}
			String codesetid=item.getCodesetid();
			if(!"0".equals(codesetid))
			{
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			    String backdate = sdf.format(new Date());
			    
				StringBuffer sql = new StringBuffer();
				ArrayList sqlParams = new ArrayList();
				
				sql.append("select codeitemid,codeitemdesc");
				sql.append(" from codeitem");
				sql.append(" where codesetid=?");
				sql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date");
				sql.append(" ORDER BY a0000,codeitemid");
				
				sqlParams.add(codesetid);
				
				frowset=dao.search(sql.toString(), sqlParams);
				while(frowset.next())
				{
					list.add(new CommonData(frowset.getString(1),frowset.getString(1)+":"+frowset.getString(2)));
				}
				
			}else if("N".equalsIgnoreCase(item.getItemtype())) {//可能传过来的是数值型，这里做标记
				list.add(new CommonData("N","N"));
			}else if("D".equalsIgnoreCase(item.getItemtype())) {//可能传过来的是日期型，这里做标记
				list.add(new CommonData("D","D"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return list;
    }
}
