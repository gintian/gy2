package com.hjsj.hrms.utils.components.tablefactory.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.*;

/**
 * 表格工具TableFactory 功能类
 * 2015-05-29
 * @author guodd
 * 
 */
public class TableFactoryBO {

	public final static String TABLEGRIDCONFIG = "tableGridConfig"; 
	
	String subModuleId;
	UserView userView;
	Connection conn;
	StringBuilder columnsid = new StringBuilder(); 
	
	/**
	 * 存储 栏目设置 备选指标  ArrayList<FieldItem>
	 */
	public static HashMap SchemeItemDataHM = new HashMap();
	
	public TableFactoryBO(String subModuleId,UserView userView,Connection conn){
		this.subModuleId = subModuleId;
		this.userView = userView;
		this.conn = conn;
	}
	
    /**
     * 获取表格的列设置方案参数
     * @return HashMap or null
     */
	public HashMap getTableLayoutConfig(){
		
		//私人方案
		HashMap config  = searchTableConfig(0);
		//没有私人方案查询公共方案
		if(config == null)
			config = searchTableConfig(1);
		
		//返回结果
		return config;
	}
	
	/**
	 * 获取 表格 栏目设置  参数
	 * @param isShare  共享标识   1 公共   0 私人
	 * @return HashMap or null
	 */
	public HashMap searchTableConfig(int isShare){
		StringBuilder sqlb = new StringBuilder();
		HashMap tableConfig = null;
		RowSet rs = null;
		try{
			sqlb.append(" select scheme_id,rows_per_page from t_sys_table_scheme where submoduleid='");
			sqlb.append(this.subModuleId);
			sqlb.append("' and is_share=");
			sqlb.append(isShare);
			
			if(isShare == 0){
				sqlb.append(" and username='");
				sqlb.append(this.userView.getUserName());
				sqlb.append("' ");
			}
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sqlb.toString());
			
			//如果没有记录，返回null
			if(!rs.next()){
				return tableConfig;
			}
			
			tableConfig = new HashMap();
			tableConfig.put("pageRows",new Integer(rs.getInt("rows_per_page")));
			tableConfig.put("schemeId",new Integer(rs.getInt("scheme_id")));
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		
		return tableConfig;
	}
	
	/**
	 * 检查栏目设置表是否缺字段。 因为后期加了三个字段
	 */
	public void checkSchemeItemTableColumn(){
		try{
			DbWizard db = new DbWizard(conn);
			ContentDAO dao = new ContentDAO(conn);
			List sqls = new ArrayList();
			if(!db.isExistField("t_sys_table_scheme_item", "is_fromdict",false)){
			     sqls.add("alter table t_sys_table_scheme_item add is_fromdict varchar(1)");
			     sqls.add("alter table t_sys_table_scheme_item add is_removable varchar(1)");
			     sqls.add("update t_sys_table_scheme_item set is_fromdict=1,is_removable=0");
			     dao.batchUpdate(sqls);
			}
			//需要跟上边分开判断，不是一起加的
			if(!db.isExistField("t_sys_table_scheme_item", "fieldsetid",false)){
			     sqls.add("alter table t_sys_table_scheme_item add fieldsetid varchar(10)");
			}
			dao.batchUpdate(sqls);
		}catch(Exception e){
				//e.printStackTrace();
		}
	}
	
	/**
	 * 获取表格保存栏目设置参数
	 * @param schemeId
	 * @return
	 */
	public ArrayList getTableColumnConfig(int schemeId){
		//检查表字段是否完整
		checkSchemeItemTableColumn();
		return searchColumnsConfigs(schemeId);
	}
	
	/**
	 * 根据列对象集合获取栏目设置参数
	 * @param schemeId 栏目设置方案id
	 * @param originColumn 目前界面显示的列对象，用于补充方案
	 * @param doCompare 是否跟orginColumn对比，追加方案中没有的指标
	 * @return
	 */
	public ArrayList getSchemeColumnConfig(int schemeId,HashMap originColumn,boolean doCompare){
		ArrayList configs = getTableColumnConfig(schemeId);
		
		//备份，下面要操作此map，防止改变原对象数据
		originColumn = (HashMap)originColumn.clone();
		
		for(int i=0;i<configs.size();i++){
			ColumnConfig cc = (ColumnConfig)configs.get(i);
			String itemid = cc.getItemid();
			
			if(!originColumn.containsKey(itemid.toLowerCase()) && !"1".equals(cc.getIs_removable())){
				configs.remove(i);
				i--;
				continue;
			}
			
			//如果有fieldset值，检查item是否构库
			String fieldset = cc.getFieldsetid();
			FieldItem item = null;
			if(fieldset!=null && fieldset.length()>1){
				if("none".equals(fieldset))
					item = DataDictionary.getFieldItem(itemid);
				else
					item = DataDictionary.getFieldItem(itemid,fieldset);
				
				if(item==null || "0".equals(item.getUseflag())){
					configs.remove(i);
					i--;
					continue;
				}
			}
			
			ColumnsInfo oci = (ColumnsInfo)originColumn.get(itemid);
			if(oci==null){
				if((cc.getDisplaydesc()==null || cc.getDisplaydesc().length()<1) && item!=null){
					cc.setDisplaydesc(item.getItemdesc());
					cc.setItemdesc(item.getItemdesc());
				}
				continue;
			}
			
			if(oci.getLoadtype()==ColumnsInfo.LOADTYPE_ONLYLOAD || oci.getColumnId().endsWith("_e")){
				configs.remove(i);
				i--;
				continue;
			}
			
			cc.setItemtype(oci.getColumnType());
			if(cc.getDisplaydesc()==null || cc.getDisplaydesc().length()<1){
				cc.setDisplaydesc(oci.getColumnRealDesc());
				cc.setItemdesc(oci.getColumnRealDesc());
			}
			if((cc.getDisplaydesc()==null || cc.getDisplaydesc().length()<1) && item!=null){
				cc.setDisplaydesc(item.getItemdesc());
				cc.setItemdesc(item.getItemdesc());
			}
			originColumn.remove(itemid);
		}
		
		if(!doCompare)
			return configs;
		
		
		Collection coll = originColumn.values();
		Iterator ite = coll.iterator();
		while(ite.hasNext()){
			ColumnsInfo ci = (ColumnsInfo)ite.next();
			if(ci.getLoadtype()==ColumnsInfo.LOADTYPE_ONLYLOAD || ci.getColumnId().endsWith("_e"))
				continue;
			
			ColumnConfig cc = new ColumnConfig();
			cc.setItemid(ci.getColumnId());
			cc.setItemdesc(ci.getColumnRealDesc());
			cc.setDisplaydesc(ci.getColumnRealDesc());
			cc.setDisplaywidth(ci.getColumnWidth());
			cc.setIs_order(ci.getOrdertype());
			cc.setIs_sum(ci.getSummaryType() == 0?"0":"1");
			cc.setMergedesc("");
			cc.setFieldsetid(ci.getFieldsetid());
			cc.setIs_removable("0");
			
			if(ci.getTextAlign()==null || "left".equals(ci.getTextAlign())){
				cc.setAlign(1);
			}else if("center".equals(ci.getTextAlign())){
				cc.setAlign(2);
			}else{
				cc.setAlign(3);
			}
			
			switch(ci.getLoadtype()){
				case 1:
				case 6:
					cc.setIs_display("1");
					break;
				default:
					cc.setIs_display("0");
			}
			configs.add(cc);
		}
		
		return configs;
	}
	
	
	/**
	 * 此方法已废弃，请使用替代方法：getTableColumnConfig(int schemeId)
	 */
	@Deprecated
	public ArrayList searchCombineColumnsConfigs(int schemeId,HashMap columns){
		return getTableColumnConfig(schemeId);
	}
	
	/**
	 * 根据 schemeid查询列 设置
	 * @param schemeId
	 * @param columnMap 
	 * @return
	 */
	private ArrayList searchColumnsConfigs(int schemeId){
		ArrayList columnsConfigs = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			StringBuilder sql = new StringBuilder();
			sql.append("select * from t_sys_table_scheme_item where scheme_id=");
			sql.append(schemeId);
			sql.append(" order by displayorder ");// 按 displayorder 排序
			rs = dao.search(sql.toString());
			
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String fieldsetid  = rs.getString("fieldsetid");
				fieldsetid = fieldsetid==null?"":fieldsetid;	
				String itemtype = "A";
				
				if(fieldsetid.length()>0){
					FieldItem item = null;
					if("none".equals(fieldsetid))
						item = DataDictionary.getFieldItem(itemid);
					else
						item = DataDictionary.getFieldItem(itemid,fieldsetid);
					
					if(item==null || "0".equals(item.getUseflag())){
						continue;
					}
					itemtype = item.getItemtype();
				}
				
				ColumnConfig column = new ColumnConfig();
				column.setItemid(itemid);
				column.setFieldsetid(fieldsetid);
				column.setItemtype(itemtype);
				column.setIs_display(rs.getString("is_display"));
				column.setDisplaywidth(rs.getInt("displaywidth"));
				column.setAlign(rs.getInt("align"));
				column.setIs_order(rs.getString("is_order"));
				column.setIs_sum(rs.getString("is_sum"));
				column.setDisplaydesc(rs.getString("displaydesc"));
				column.setItemdesc(rs.getString("itemdesc"));
				column.setMergedesc(rs.getString("mergedesc"));
				column.setIs_lock(rs.getString("is_lock"));
				column.setIs_removable(rs.getString("is_removable"));
				columnsConfigs.add(column);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return columnsConfigs;
	}
	
	/**
	 * 解析ColumnsInfo，收集表格相关参数
	 * @param configMap
	 * @param columns ColumnsInfo对象集合
	 * @param mergeDesc  初始传null
	 */
	public void doColumnCollect(HashMap configMap,ArrayList columns,String mergeDesc){
		for(int i=0;i<columns.size();i++){
			
			//兼容以前多层级时，父节点用HashMap的情况
			if(!(columns.get(i) instanceof ColumnsInfo)){
				HashMap columnBox = (HashMap)columns.get(i);
				ArrayList columnList = (ArrayList)columnBox.get("items");
				doColumnCollect(configMap,columnList,(String)columnBox.get("text"));
				continue;
			}
			
			ColumnsInfo c = (ColumnsInfo)columns.get(i);
			if(c.getChildColumns().size()>0){
				doColumnCollect(configMap,c.getChildColumns(),c.getColumnDesc());
				continue;
			}
			
			if(configMap.containsKey("schemeColumnList") &&  c.getColumnId() != null && !c.isEncrypted() && c.getLoadtype()!=ColumnsInfo.LOADTYPE_ONLYLOAD){
				ArrayList schemeColumnList = (ArrayList)configMap.get("schemeColumnList");
				ColumnConfig cc = ColumnsInfo2ColumnConfig(c,mergeDesc);
				schemeColumnList.add(cc);
			}
			
			String dataIndex = c.getColumnId();
			
			if(c.getColumnId()==null){
				if(configMap.containsKey("emptyColumnList")){
					ArrayList emptyColumn = (ArrayList)configMap.get("emptyColumnList");
					emptyColumn.add(c);
				}
				continue;
			}
			if(c.isEncrypted())//如果是加密的，列id在后面加个 "_e",用于区分不加密的列
					dataIndex+="_e";
			
			if(configMap.containsKey("dataFields")){
				ArrayList dataFields = (ArrayList)configMap.get("dataFields");
				//字段集合
				dataFields.add(dataIndex);
			}
			
			if(configMap.containsKey("columnMap")){
				HashMap columnMap = (HashMap)configMap.get("columnMap");
				columnMap.put(c.getColumnId(),c);
			}
			
			if(configMap.containsKey("primaryKeys") && c.isKey()){
				StringBuffer primaryKeys = (StringBuffer)configMap.get("primaryKeys");
				primaryKeys.append(dataIndex).append(",");
			}
				
			if(configMap.containsKey("queryFields") && c.isQueryable()){
				HashMap queryFields = (HashMap)configMap.get("queryFields");
				FieldItem f = new FieldItem();
				f.setItemid(c.getColumnId());
				f.setFieldsetid(c.getFieldsetid());
				f.setItemtype(c.getColumnType());
				f.setItemlength(c.getColumnLength());
				f.setCodesetid(c.getCodesetId());
				f.setUseflag("1");
				queryFields.put(c.getColumnId(), f);
			}
		}
	}
	
	public ArrayList createSchemeConfigsByColumnList(ArrayList columnList){
		HashMap configMap = new HashMap();
		configMap.put("schemeColumnList", new ArrayList());
		doColumnCollect(configMap,columnList,null);
		return (ArrayList)configMap.get("schemeColumnList");
	}
	
	private ColumnConfig ColumnsInfo2ColumnConfig(ColumnsInfo c,String mergeDesc){
		
		ColumnConfig cc = new ColumnConfig();
		cc.setItemid(c.getColumnId());
		if(mergeDesc!=null)
			cc.setMergedesc(mergeDesc);
		if(c.getTextAlign()==null || "left".equals(c.getTextAlign())){
			cc.setAlign(1);
		}else if("center".equals(c.getTextAlign())){
			cc.setAlign(2);
		}else{
			cc.setAlign(3);
		}
		
		cc.setDisplaydesc(c.getColumnRealDesc());
		cc.setDisplaywidth(c.getColumnWidth());
		switch(c.getLoadtype()){
		case 1:
			cc.setIs_display("1");
			break;
		case 5:
			cc.setIs_display("1");
			break;
		default:
			cc.setIs_display("0");
			break;
		}
		
		cc.setIs_order(c.getOrdertype());
		cc.setIs_sum(c.getSummaryType() == 0?"0":"1");
		cc.setItemdesc(c.getColumnRealDesc());
		if("N".equals(c.getColumnType()) && c.getOperationData()!=null)
			cc.setItemtype("A");
		else
			cc.setItemtype(c.getColumnType());
		
		if("D".equals(c.getColumnType()))
			cc.setAlign(1);
		
		cc.setIs_lock(c.isLocked()?"1":"0");
		cc.setFieldsetid(c.getFieldsetid());
		cc.setIs_removable(c.isRemovable()?"1":"0");
		
		return cc;
	}
	
	/**
	 * 根据配置生成新的tablecolumns
	 * @param columns  列（columnsInfo）
	 * @param columnsConfis 列方案参数（columnconfig）
	 * @param lockItem 
	 * @return
	 */
	public ArrayList rebuildColumns(HashMap columnsMap,ArrayList columnsConfis,int schemeId){
		ArrayList newColumns = new ArrayList();
		String columnId = "";
		ColumnsInfo ci = null;
		ColumnConfig cc = null;
		ArrayList mergeColumns = new ArrayList();
		Boolean mergeLockState = null;
		String mergeDesc = "";
		int mergeIndex = 0;
		for(int i=0;i<columnsConfis.size();i++){
			cc = (ColumnConfig)columnsConfis.get(i);
			columnId = cc.getItemid();
			if(!columnsMap.containsKey(columnId))
				  continue;
			
			ci = (ColumnsInfo)columnsMap.get(columnId);
			
			    	//是否显示
				if("1".equals(cc.getIs_display())){
					ci.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				}else{
					//如果此列为 总是加载数据列，设置为只加载数据
					if(ci.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD || ci.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE)
						ci.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
					else
						ci.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
					
					if(cc.getMergedesc()==null || cc.getMergedesc().length()==0)
						cc.setMergedesc(mergeDesc);
				}
			    	
				//数据布局  左对齐、居中、 右对齐
				switch(cc.getAlign()){
				case 1:
					ci.setTextAlign("left");
					break;
				case 2:
					ci.setTextAlign("center");
					break;
				case 3:
					ci.setTextAlign("right");
					break;
				}
				    
				//数据布局  左对齐、居中、 右对齐
				if(cc.getDisplaywidth()>0)
					ci.setColumnWidth(cc.getDisplaywidth());
				//排序方式 无，正序，倒序
				ci.setOrdertype(cc.getIs_order());
				//是否汇总
				if("1".equals(cc.getIs_sum()))
					ci.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
				else
					ci.setSummaryType(0);
				
				if(cc.getDisplaydesc()!=null && cc.getDisplaydesc().length()>0)//update by xiegh on 20170922 bug:31316
					ci.setColumnDesc(cc.getDisplaydesc());
				
				ci.displayIndex=i;
				
				//锁列
				if("1".equals(cc.getIs_lock()))
					ci.setLocked(true);
				else
					ci.setLocked(false);
				
				//如果不是二级表头
				if(cc.getMergedesc()==null || cc.getMergedesc().length()==0){
					if(!mergeColumns.isEmpty() || mergeColumns.size()==1){
						if(mergeColumns.size()==1){
							newColumns.add(mergeColumns.get(0));
						}else{
							ColumnsInfo compositedColumn = new ColumnsInfo();
							compositedColumn.setColumnDesc(mergeDesc);
							compositedColumn.setLocked(mergeLockState);
							compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
							newColumns.add(compositedColumn);
						}
						mergeColumns.clear();
						mergeDesc = "";
						mergeLockState = null;
						mergeIndex = 0;
					}
					newColumns.add(ci);
					columnsMap.remove(columnId);
					continue;
				}
				
				// 如果 复合表头 list 为空， 或者   与上一个 columns 复合表头名称相同，则添加进复合表头list
				if(mergeColumns.size()==0 || (cc.getMergedesc().equals(mergeDesc) &&  i == mergeIndex+1)){
					mergeColumns.add(ci);
					mergeDesc = cc.getMergedesc();
					mergeIndex = i;
					if(mergeLockState==null || mergeLockState.booleanValue())
						mergeLockState = Boolean.valueOf(ci.isLocked());
					columnsMap.remove(columnId);
					continue;
				}else if(!cc.getMergedesc().equals(mergeDesc)){
					//如果 复合表头名称跟上一次的不一样了，那就是一个新的复合表头。将以前的 复合表头list 里的column 组合成复合表头，存入 newColumns 中，并重新初始化复合表头的一些参数
					if(mergeColumns.size()==1){
						newColumns.add(mergeColumns.get(0));
					}else{
						ColumnsInfo compositedColumn = new ColumnsInfo();
						compositedColumn.setColumnDesc(mergeDesc);
						compositedColumn.setLocked(mergeLockState);
						compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
						newColumns.add(compositedColumn);
					}
					
					mergeColumns.clear();
					
					mergeColumns.add(ci);
					mergeDesc = cc.getMergedesc();
					mergeLockState = Boolean.valueOf(ci.isLocked());
					mergeIndex = i;
					columnsMap.remove(columnId);
				}else{//剩下的情况就不需要处理了，都是不符合复合表头定义规则的，直接当做普通列
					newColumns.add(ci);
					columnsMap.remove(columnId);
				}
				
		}
		
		if(!mergeColumns.isEmpty()){
			ColumnsInfo compositedColumn = new ColumnsInfo();
			compositedColumn.setColumnDesc(mergeDesc);
			compositedColumn.setLocked(mergeLockState);
			compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
			newColumns.add(compositedColumn);
		}
		
		//一些自定义的列，不参与栏目设置，直接放到最后
		if(!columnsMap.isEmpty()){
			Collection next = columnsMap.values();
			newColumns.addAll(next);
			addColumn2Scheme(next,schemeId);
		}
		
		return newColumns;
	}
	
	private void addColumn2Scheme(Collection coll,int schemeId){
		RowSet rs = null;
		try{
			//schemeId 为代码从数据库直接获取，不涉及用户输入，不用考虑sql注入
			String sql = "select max(displayorder) maxorder from t_sys_table_scheme_item where scheme_id="+schemeId;
			ContentDAO dao = new ContentDAO(conn);
			
			rs = dao.search(sql);
			rs.next();
			
			int maxorder = rs.getInt("maxorder");
			
			sql = " insert into t_sys_table_scheme_item(scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_removable,fieldsetid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Object[] columns = coll.toArray();
			ArrayList values = new ArrayList();
			for(int i=0;i<columns.length;i++){
				ColumnsInfo c = (ColumnsInfo)columns[i];
				if(c.getColumnId() == null || c.isEncrypted() || c.getLoadtype()==ColumnsInfo.LOADTYPE_ONLYLOAD){
					continue;
				}
				
				ArrayList value = new ArrayList();
				value.add(schemeId);
				value.add(c.getColumnId());
				value.add(maxorder++);
				value.add("");
				value.add(1);
				value.add(c.getColumnWidth());
				value.add("1");
				value.add("0");
				value.add("0");
				value.add("");
				value.add("");
				value.add("0");
				value.add("0");
				value.add(c.getFieldsetid());
				values.add(value);
			}
			
			if(values.size()>0){
				dao.batchInsert(sql, values);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		
	}
	
	
	/**
	 * 保存栏目设置
	 * @param tableConfig 参数
	 * @param isShare 是否共享
	 */
	public synchronized int saveTableConfig(DynaBean tableConfig,int isShare){
		StringBuilder sqlbdr = new StringBuilder();
		int scheme_id = 0;
		boolean isExsit = true;
		RowSet rs = null;
		Savepoint sp = null;
		ArrayList values = new ArrayList();
		try{
			conn.setAutoCommit(false);
			sp = conn.setSavepoint();
			ContentDAO dao = new ContentDAO(conn);
			scheme_id = Integer.parseInt(tableConfig.get("schemeId").toString());
			if(scheme_id==-1){
				isExsit = false;
				sqlbdr.append("select max(scheme_id) max_scheme_id from t_sys_table_scheme ");
				rs = dao.search(sqlbdr.toString());
				rs.next();
				scheme_id = rs.getInt("max_scheme_id")+1;
				sqlbdr.setLength(0);
			}
			
			if(isExsit){
				sqlbdr.append(" update t_sys_table_scheme set rows_per_page=?,username=? where scheme_id=?");//lockitemid=?,
				values.add(tableConfig.get("pageRows"));
				values.add(userView.getUserName());
				values.add(new Integer(scheme_id));
				dao.update(sqlbdr.toString(), values);
			}else{
				sqlbdr.append(" insert into t_sys_table_scheme(scheme_id,submoduleid,username,is_share,rows_per_page) values(?,?,?,?,?) ");//lockitemid,
				values.add(new Integer(scheme_id));
				values.add(subModuleId);
				values.add(userView.getUserName());
				values.add(new Integer(isShare));
				values.add(tableConfig.get("pageRows"));
				dao.insert(sqlbdr.toString(), values);
			}
			sqlbdr.setLength(0);
			values.clear();
			
			if(isExsit){
				sqlbdr.append(" delete t_sys_table_scheme_item where scheme_id="+scheme_id);
				dao.update(sqlbdr.toString());
				sqlbdr.setLength(0);
			}
			
			ArrayList objs = (ArrayList)tableConfig.get("columnsConfigs");
			DynaBean item;
			String disDes;
			String iteDes;
			sqlbdr.append(" insert into t_sys_table_scheme_item(scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_removable,fieldsetid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for(int i=0;i<objs.size();i++){
				item = (DynaBean)objs.get(i);
				
				disDes = item.get("displaydesc").toString();
				iteDes = item.get("itemdesc").toString();
				if(disDes.equals(iteDes))
					disDes = iteDes = "";
				
				ArrayList value = new ArrayList();
				value.add(new Integer(scheme_id));
				value.add(item.get("itemid"));
				value.add(new Integer(i));
				value.add(disDes);
				value.add(item.get("is_display"));
				value.add(new Integer(item.get("displaywidth").toString()));
				value.add(item.get("align"));
				value.add(item.get("is_order"));
				value.add(item.get("is_sum"));
				value.add(iteDes);
				value.add(item.get("mergedesc").toString());
				value.add(item.get("is_lock"));
				value.add(item.get("is_removable"));
				value.add(item.get("fieldsetid"));
				values.add(value);
			}
			
			dao.batchInsert(sqlbdr.toString(), values);
			
			conn.commit();
			
		}catch(Exception e){
			e.printStackTrace();
			try {
				conn.rollback(sp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			PubFunc.closeResource(rs);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return scheme_id;
	}
	
	/**
	 * 获取 个人栏目设置方案id
	 * @return
	 */
	public int getPersonalPlanSchemeID(){
		int schemeId = -1;
		StringBuilder sql = new StringBuilder();
		ArrayList values = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			sql.append("select scheme_id from t_sys_table_scheme where submoduleid=? and username=? and is_share=0 ");
			values.add(subModuleId);
			values.add(userView.getUserName());
			rs = dao.search(sql.toString(), values);
			if(rs.next())
				schemeId = rs.getInt("scheme_id");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		
		return schemeId;
	}
	
	/**
	 * 更新 列宽度
	 * @param scheme_id 方案id
	 * @param itemid  列
	 * @param width  新的宽度
	 */
	public void updateColumnWidth(int scheme_id,String itemid,int width){
		StringBuilder sql = new StringBuilder();
		ArrayList values = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			sql.append(" update t_sys_table_scheme_item set displaywidth=? where scheme_id=? and itemid=? ");
			values.add(new Integer(width));
			values.add(new Integer(scheme_id));
			values.add(itemid);

			dao.update(sql.toString(), values);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 更新锁列状态
	 * @param scheme_id 方案id
	 * @param itemid  列
	 * @param lockstate 锁列标识
	 */
	public void updateColumnLocked(int scheme_id,String itemid,String lockstate){
		StringBuilder sql = new StringBuilder();
		ArrayList values = new ArrayList();
		String is_lock = "1";
		String formula = "<=";
		if("unlock".equals(lockstate)){
			is_lock = "0";
			formula = ">=";
		}
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			sql.append(" update t_sys_table_scheme_item set is_lock=? where scheme_id=? and displayorder");
			sql.append(formula);
			sql.append("(select displayorder from t_sys_table_scheme_item where scheme_id=? and itemid=? )");
			
			values.add(is_lock);
			values.add(new Integer(scheme_id));
			values.add(new Integer(scheme_id));
			values.add(itemid);

			dao.update(sql.toString(), values);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 栏目设置 列拖动 位置变动操作
	 * @param scheme_id 方案id
	 * @param itemid    变动指标
	 * @param nextid    后面的指标
	 * @param is_lock   是否锁列
	 */
	public void updateColumnMove(int scheme_id,String itemid,String nextid,String is_lock){
		StringBuilder sql = new StringBuilder();
		ArrayList values = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			sql.append(" select itemid,displayorder from t_sys_table_scheme_item where scheme_id=? and (itemid=? or itemid=?) ");
			values.add(new Integer(scheme_id));
			values.add(itemid);
			values.add(nextid);
			rs = dao.search(sql.toString(),values);
            int itemorder = -1;
            int nextorder = -1;
            rs.last();
            if(rs.getRow()!=2 && !"-1".equals(nextid))
            	return;
            rs.beforeFirst();
            while(rs.next()){
            	if(rs.getString("itemid").equals(itemid))
            		itemorder = rs.getInt("displayorder");
            	else
            		nextorder = rs.getInt("displayorder");
            }
            sql.setLength(0);
            values.clear();
            
            int dbserver = Sql_switcher.searchDbServer();
            int updaterows = 0;
            // nextorder 为-1 说明是移动到最后了，没有下一个了
            if(nextorder == -1){
            	sql.append("update t_sys_table_scheme_item set displayorder=displayorder-1 where scheme_id=? and displayorder>?");
            	values.add(scheme_id);
            	values.add(itemorder);
                updaterows = dao.update(sql.toString(), values);
                sql.setLength(0);
                values.clear();
                sql.append("update t_sys_table_scheme_item set displayorder=displayorder+?,is_lock=? where scheme_id=? and itemid=? ");
                values.add(new Integer(updaterows));
                values.add(is_lock);
                values.add(new Integer(scheme_id));
                values.add(itemid);
                dao.update(sql.toString(), values);
                
                sql.setLength(0);
                values.clear();
                
                if(dbserver==Constant.ORACEL){
                	sql.append(" DECLARE  up_mergedesc varchar(200); ");
                	sql.append(" TYPE my_scheme_type IS RECORD(me_mergedesc varchar(200),me_displayorder int); ");
                	sql.append(" my_row my_scheme_type; ");
                	sql.append(" BEGIN ");
                	sql.append(" select nvl(mergedesc,''),displayorder into my_row from t_sys_table_scheme_item WHERE scheme_id=? and itemid=?; ");
                	sql.append(" select nvl(mergedesc,'') into up_mergedesc from t_sys_table_scheme_item WHERE scheme_id=? and displayorder=my_row.me_displayorder-1; ");
                	sql.append(" IF up_mergedesc<>my_row.me_mergedesc then ");
                	sql.append(" update t_sys_table_scheme_item set mergedesc='' where scheme_id=? and itemid=?; ");
                	sql.append(" END IF;");
                	sql.append(" END;");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                }else{
                	sql.append(" declare @upmergedesc varchar(200),@desplayorder int,@mergedesc varchar(200);  ");
                	sql.append(" select @mergedesc=isnull(mergedesc,''),@desplayorder=displayorder from t_sys_table_scheme_item where scheme_id=? and itemid=?;  ");
                	sql.append(" select @upmergedesc=isnull(mergedesc,'') from t_sys_table_scheme_item where scheme_id=? and displayorder=@desplayorder-1; ");
                	sql.append(" if @upmergedesc<>@mergedesc and len(@mergedesc)>0 update t_sys_table_scheme_item set mergedesc='' where scheme_id=? and itemid=? ");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                }
                dao.update(sql.toString(), values);
                return;
            }
            
            //向上移动
            if(nextorder<itemorder){
            	sql.append("update t_sys_table_scheme_item set displayorder = displayorder+1  where scheme_id=? and displayorder>=? and displayorder<?");
            	values.add(new Integer(scheme_id));
            	values.add(new Integer(nextorder));
            	values.add(new Integer(itemorder));
            	updaterows = dao.update(sql.toString(), values);
            	sql.setLength(0);
            	values.clear();
            	sql.append("update t_sys_table_scheme_item set displayorder=displayorder-?,is_lock=? where scheme_id=? and itemid=? ");
            	values.add(new Integer(updaterows));
                values.add(is_lock);
                values.add(new Integer(scheme_id));
                values.add(itemid);
                dao.update(sql.toString(), values);
                
                sql.setLength(0);
                values.clear();
                
                if(dbserver==Constant.ORACEL){
                	sql.append("  DECLARE  upmergedesc VARCHAR(200);downmergedesc VARCHAR(200); ");
                	sql.append("  TYPE myscheme IS RECORD(medisplayorder int,memergedesc varchar(200) ); ");
                	sql.append("  myrow myscheme; ");
                	sql.append("  BEGIN ");
                	sql.append("  select displayorder,nvl(mergedesc,'') into myrow from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and itemid=?; ");
                	sql.append("  select nvl(mergedesc,'') into downmergedesc from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and displayorder=myrow.medisplayorder+1; ");
                	sql.append("  IF myrow.medisplayorder=0 THEN ");
                	sql.append("  IF myrow.memergedesc<>downmergedesc THEN ");
                	sql.append("  update T_SYS_TABLE_SCHEME_ITEM set mergedesc='' where scheme_id=? and itemid=?; ");
                	sql.append("  END IF;");
                	sql.append("  ELSE ");
                	sql.append("  select nvl(mergedesc,'') into upmergedesc from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and displayorder=myrow.medisplayorder-1; ");
                	sql.append("  IF upmergedesc=downmergedesc then ");
                	sql.append("  update T_SYS_TABLE_SCHEME_ITEM set mergedesc=upmergedesc where scheme_id=? and itemid=?;");
                	sql.append("  ELSIF myrow.memergedesc<>downmergedesc THEN ");
                	sql.append("  update T_SYS_TABLE_SCHEME_ITEM set mergedesc='' where scheme_id=? and itemid=?; ");
                	sql.append("  end if; ");
                	sql.append("  end if; ");
                	sql.append("  END; ");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(itemid);
                }else{
                	sql.append("  declare @upmergedesc varchar(200),@downmergedesc varchar(200),@mymergedesc varchar(200),@mydisplayorder int; ");
                	sql.append("  select @mymergedesc = isnull(mergedesc,''),@mydisplayorder=displayorder from t_sys_table_scheme_item where scheme_id=? and itemid=?; ");
                	sql.append("  select @downmergedesc = isnull(mergedesc,'') from t_sys_table_scheme_item where scheme_id=? and displayorder=@mydisplayorder+1;");
                	sql.append("  if @mydisplayorder=0 and @downmergedesc<>@mymergedesc ");
                	sql.append("  update t_sys_table_scheme_item set mergedesc='' where scheme_id=? and itemid=?; ");
                	sql.append("  else ");
                	sql.append("  begin ");
                	sql.append("  select @upmergedesc=isnull(mergedesc,'') from t_sys_table_scheme_item where scheme_id=? and displayorder=@mydisplayorder-1; ");
                	sql.append("  if @upmergedesc=@downmergedesc ");
                	sql.append("  update t_sys_table_scheme_item set mergedesc=@upmergedesc where scheme_id=? and itemid=?;");
                	sql.append("  else if @mymergedesc<>@downmergedesc ");
                	sql.append("  update t_sys_table_scheme_item set mergedesc='' where scheme_id=? and itemid=?;");
                	sql.append("  end ");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(itemid);
                }
                dao.update(sql.toString(), values);
                
            }else{//向下移动
            	sql.append("update t_sys_table_scheme_item set displayorder = displayorder-1  where scheme_id=? and displayorder>? and displayorder<?");
            	values.add(new Integer(scheme_id));
            	values.add(new Integer(itemorder));
            	values.add(new Integer(nextorder));
            	updaterows = dao.update(sql.toString(), values);
            	sql.setLength(0);
            	values.clear();
            	sql.append("update t_sys_table_scheme_item set displayorder=displayorder+?,is_lock=? where scheme_id=? and itemid=? ");
            	values.add(new Integer(updaterows));
                values.add(is_lock);
                values.add(new Integer(scheme_id));
                values.add(itemid);
                dao.update(sql.toString(), values);
                
                sql.setLength(0);
                values.clear();
                
                if(dbserver==Constant.ORACEL){
                	sql.append("  DECLARE  upmergedesc varchar(200);downmergedesc varchar(200); ");
                	sql.append("  TYPE myscheme IS RECORD(medisplayorder int,memergedesc varchar(200) ); ");
                	sql.append("  myrow myscheme; ");
                	sql.append("  BEGIN ");
                	sql.append("  select displayorder,nvl(mergedesc,'') into myrow from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and itemid=?; ");
                	sql.append("  select nvl(mergedesc,'') into downmergedesc from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and displayorder=myrow.medisplayorder+1; ");
                	sql.append("  select nvl(mergedesc,'') into upmergedesc from T_SYS_TABLE_SCHEME_ITEM where scheme_id=? and displayorder=myrow.medisplayorder-1; ");
                	sql.append("  IF upmergedesc=downmergedesc then ");
                	sql.append("  update T_SYS_TABLE_SCHEME_ITEM set mergedesc=upmergedesc where scheme_id=? and itemid=?;");
                	sql.append("  ELSIF upmergedesc<>myrow.memergedesc THEN ");
                	sql.append("  update T_SYS_TABLE_SCHEME_ITEM set mergedesc='' where scheme_id=? and itemid=?; ");
                	sql.append("  end if; ");
                	sql.append("  END; ");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(itemid);
                }else{
                	sql.append("  declare @upmergedesc varchar(200),@downmergedesc varchar(200),@mymergedesc varchar(200),@mydisplayorder int; ");
                	sql.append("  select @mymergedesc = mergedesc,@mydisplayorder=displayorder from t_sys_table_scheme_item where scheme_id=? and itemid=?; ");
                	sql.append("  select @downmergedesc = mergedesc from t_sys_table_scheme_item where scheme_id=? and displayorder=@mydisplayorder+1; ");
                	sql.append("  select @upmergedesc = mergedesc from t_sys_table_scheme_item where scheme_id=? and displayorder=@mydisplayorder-1; ");
                	sql.append("  if @upmergedesc=@downmergedesc ");
                	sql.append("  update t_sys_table_scheme_item set mergedesc=@upmergedesc where scheme_id=? and itemid=?;");
                	sql.append("  else if @upmergedesc<>@mymergedesc ");
                	sql.append("  update t_sys_table_scheme_item set mergedesc='' where scheme_id=? and itemid=?;");
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(scheme_id);
                	values.add(itemid);
                	values.add(scheme_id);
                	values.add(itemid);
                }
                dao.update(sql.toString(), values);
            }
            
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 生成 多维统计 sql查询语句 
	 * @param hItems 横向 参数
	 * @param vItems 纵向参数
	 * @param analyseType 统计类型
	 * @param itemType 统计指标
	 * @return
	 */
	public String createPlanAnalyseSql(ArrayList hItems,ArrayList vItems,int analyseType,String itemType){
		StringBuilder sql = new StringBuilder();
		StringBuilder columnName = new StringBuilder();
		//String keyitems = "";
		try{
		
		sql.append("select ");
	    HashMap db = null;
	    ArrayList child = null;
	    for(int i=0;i<hItems.size();i++){
		    	db = (HashMap)hItems.get(i);
		    	//itemid = "item"+db.get("staticId").toString();
		    	//keyitems+=itemid+",";
		    	child = (ArrayList)db.get("child");
		    	
		    	String itemSql = getItemAnalyseSql(db,"data",columnName);
		    	//sql.append(",");
		    	sql.append(itemSql);
		    
		    	if(child.isEmpty()){
		    		continue;
		    	}
		    	
		    	for(int k=0;k<child.size();k++){
		    		db = (HashMap)child.get(k);
		    		//itemid = "item"+db.get("staticId").toString();
		    		//keyitems+=itemid+",";
		    		String citemSql = getItemAnalyseSql(db,"data",columnName);
			    	//sql.append(",");
			    	sql.append(citemSql);
		    	}
	    }
	    
	    for(int i=0;i<vItems.size();i++){
		    	db = (HashMap)vItems.get(i);
		    	//itemid = "item"+db.get("staticId").toString();
		    	//keyitems+=itemid+",";
		    	child = (ArrayList)db.get("child");
		    	String itemSql = getItemAnalyseSql(db,"data",columnName);
		    	//sql.append(",");
		    	sql.append(itemSql);
		    	if(child.isEmpty()){
		    		continue;
		    	}
		    	
		    	for(int k=0;k<child.size();k++){
		    		db = (HashMap)child.get(k);
		    		//itemid = "item"+db.get("staticId").toString();
		    		//keyitems+=itemid+",";
		    		String citemSql = getItemAnalyseSql(db,"data",columnName);
			    	//sql.append(",");
			    	sql.append(citemSql);
		    	}
	    }
	    if(analyseType>1){
	    	sql.append(itemType);
	    	sql.append(" as num ");
	    }else
	        sql.append(" 1 as num "); 
	    
	    TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
	    
	    String sourceSql = tableCache.getTableSql();
	    
	    sql.append(" from (");
	    sql.append(sourceSql);
	    sql.append(") data ");
		
//	    anaStr.append(" select ");
//		switch(analyseType){
//		case 1:
//			anaStr.append(" COUNT(num) compnum, ");
//			break;
//		case 2:
//			anaStr.append(" MAX(num) compnum, ");
//			break;
//		case 3:
//			anaStr.append(" MIN(num) compnum, ");
//			break;
//		case 4:
//			anaStr.append(" AVG(num) compnum, ");
//			break;
//		case 5:
//			anaStr.append(" SUM(num) compnum, ");
//			break;
//		}
//		
//		anaStr.append(" * ");
//		//anaStr.deleteCharAt(anaStr.length()-1);
//		anaStr.append(" from (");
//		anaStr.append(sql);
//		anaStr.append(" ) c group by ");
//		anaStr.append(columnName+" num ");
//		anaStr.append(" order by ");
//		anaStr.append(columnName+" num ");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sql.toString();
	}
	
	/**
	 * 将 条件 因子表达式转成sql语句
	 * @param item  统计项
	 * @param tableName  表名
	 * @return
	 */
	private String getItemAnalyseSql(HashMap item,String tableName,StringBuilder columnName){
		StringBuffer sb = new StringBuffer();
		//sb.append(" case ");
		//String itemid = "item"+item.get("staticId").toString();
		ArrayList conds = (ArrayList)item.get("conds");
		try {
			
//			HashMap cond = null;
//			FactorList parser = null;
//			for(int k=0;k<conds.size();k++){
//			    cond = (HashMap)conds.get(k);
//				String factor = cond.get("factor").toString();
//				String expr = cond.get("expr").toString();
//				parser = new FactorList(expr,factor, userView.getUserName());
//			    String condSql = parser.getSingleTableSqlExpression("data");
//			    sb.append(" when ");
//			    sb.append(condSql);
//			    sb.append(" then '"+cond.get("condName")+"' ");
//			}
//			
//			sb.append(" end as "+itemid);
			
			HashMap cond = null;
			FactorList parser = null;
			for(int k=0;k<conds.size();k++){
			    cond = (HashMap)conds.get(k);
				String factor = cond.get("factor").toString();
				String expr = cond.get("expr").toString();
				parser = new FactorList(expr,factor, userView.getUserName());
			    String condSql = parser.getSingleTableSqlExpression("data");
			    sb.append(" case when ");
			    sb.append(condSql);
			    sb.append(" then 'cond_"+cond.get("staticItemId")+"' end as cond_"+cond.get("staticItemId")+",");
			    columnName.append("cond_"+cond.get("staticItemId")+",");
			}
			
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	
	/**
	 * 根据 定义的 统计条件 生成对应条件的统计表
	 * @param hItems 横向 统计参数
	 * @param vItems 纵向统计参数
	 * @param analyseType 统计类型
	 * @param itemType 统计指标
	 * @return
	 */
	public ArrayList createAnalyseChartData(ArrayList hItems,ArrayList vItems,int analyseType,String itemType){
		
	    ArrayList chartList = new ArrayList();
		for(int i=0;i<hItems.size();i++){
			HashMap item = (HashMap)hItems.get(i);
			ArrayList childList = (ArrayList)item.get("child");
			if(childList.isEmpty()){
				String dataXml = getChartXml(item,null,analyseType,itemType);
				chartList.add(dataXml);
				continue;
			}
			for(int k=0;k<childList.size();k++){
				HashMap citem = (HashMap)childList.get(k);
				String dataXml = getChartXml(item,citem,analyseType,itemType);
				chartList.add(dataXml);
			}
		}
		
		for(int i=0;i<vItems.size();i++){
			HashMap item = (HashMap)vItems.get(i);
			ArrayList childList = (ArrayList)item.get("child");
			if(childList.isEmpty()){
				String dataXml = getChartXml(item,null,analyseType,itemType);
				chartList.add(dataXml);
				continue;
			}
			for(int k=0;k<childList.size();k++){
				HashMap citem = (HashMap)childList.get(k);
				String dataXml = getChartXml(item,citem,analyseType,itemType);
				chartList.add(dataXml);
			}
		}
		
		
		return chartList;
	}
	
	/**
	 * 根据 横纵指标生成统计表
	 * @param upItem  一级 统计项
	 * @param downItem 二级 统计项
	 * @param analyseType  统计类型（个数、最大...）
	 * @param itemType  统计指标
	 * @return
	 */
	private String getChartXml(HashMap upItem,HashMap downItem,int analyseType,String itemType){
		String chartXml = "";
		//String upItemid = "item"+upItem.get("staticId").toString();
		ArrayList upCodeList = (ArrayList)upItem.get("conds");//统计条件
		String downItemid = null;
		ArrayList downCodeList = null;
		if(downItem!=null){
			downItemid = "item"+downItem.get("staticId").toString();
			downCodeList = (ArrayList)downItem.get("conds");
		}
				
		//创建查询sql语句
		StringBuilder sql = new StringBuilder();
		StringBuilder groupColumn = new StringBuilder();
		sql.append("select ");
//		switch(analyseType){
//		case 1:
//			sql.append(" COUNT(num) compnum, ");
//			break;
//		case 2:
//			sql.append(" MAX(num) compnum, ");
//			break;
//		case 3:
//			sql.append(" MIN(num) compnum, ");
//			break;
//		case 4:
//			sql.append(" AVG(num) compnum, ");
//			break;
//		case 5:
//			sql.append(" SUM(num) compnum, ");
//			break;
//		}
//		sql.append(upItemid);
//		if(downItemid!=null)
//			sql.append(","+downItemid);
//		sql.append(" * ");
//		sql.append(" from ( select ");
		
		String upSql = getItemAnalyseSql(upItem,"data",groupColumn);
		sql.append(upSql);
		
		if(downItemid!=null){
			String downSql = getItemAnalyseSql(downItem,"data",groupColumn);
			sql.append(downSql);
		}
		if(analyseType>1){
	    	sql.append(itemType);
	    	sql.append(" as num ");
	    }else
	        sql.append(" 1 as num "); 
		groupColumn.append(" num ");
		//表格参数
        TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
	    //表格数据sql
	    String sourceSql = tableCache.getTableSql();
	    
	    sql.append(" from (");
	    sql.append(sourceSql);
	    sql.append(") data ");
//	    sql.append(" group by ");
//	    sql.append(groupColumn);
//	    sql.append(" order by ");
//	    sql.append(groupColumn);
	    
	    ContentDAO dao = new ContentDAO(conn);
	    RowSet rs = null;
	    ArrayList valueList = new ArrayList();
	    try{
	    	
    		//String title = upItem.get("itemName").toString();
	    	rs = dao.search(sql.toString());
	    	//生成统计数据
	    	
	    	HashMap  analyseData = new HashMap();
	    	
	    	ArrayList xData = new ArrayList();
	    	ArrayList legendData = new ArrayList();
	    	
	    	
	    	//如果存在二级统计项目，生成分组柱状图 数据
	    	if(downItemid!=null){
	    		//title+="-"+downItem.get("itemName");
	    		for(int i=0;i<upCodeList.size();i++){
	    			HashMap upCodeConfig = (HashMap)upCodeList.get(i);
	    			String upStaticItemId = upCodeConfig.get("staticItemId").toString();
	    			String upName = upCodeConfig.get("condName").toString();
	    			xData.add(upName);
	    			//ArrayList group = new ArrayList();
	    			//group.add(upName);
	    			for(int k=0;k<downCodeList.size();k++){
	    			     HashMap dnCodeConfig = (HashMap)downCodeList.get(k);
	    			     String dnStaticItemId = dnCodeConfig.get("staticItemId").toString();
		    			String dnName = dnCodeConfig.get("condName").toString();
		    			
		    			if(!legendData.contains(dnName))
		    				legendData.add(dnName);
		    			
	    				//boolean flag = false;
	    				double value = 0;
	    				boolean b = true;
	    				int totalCount = 0;
	    				if(rs.first()){
	    					do{
	    						if(rs.getString("cond_"+upStaticItemId)==null || rs.getString("cond_"+dnStaticItemId)==null/* || rs.getObject("num")==null*/)
	    							continue;
	    						double itemValue = rs.getDouble("num");
	    						if(b && analyseType!=4){
	    							value = itemValue;
	    							b=false;
	    							continue;
	    						}
	    						switch(analyseType){
		    						case 2:
		    								value = itemValue>value?itemValue:value;
		    								break;
		    						case 3:
		    								value = itemValue<value?itemValue:value;
		    								break;
		    						case 4:
		    								value+=itemValue;
		    								totalCount++;
		    								break;
		    						default://1:count 5:sum 都是求和，走这里
		    								value+=itemValue;
	    						}
	    					}while(rs.next());
	    				}
	    				if(analyseType==4 && totalCount != 0)
	    					value = value/totalCount;
	    				
	    				ArrayList data;
	    				if(analyseData.containsKey(dnName))
	    					data = (ArrayList)analyseData.get(dnName);
	    				else
	    					data = new ArrayList();
	    				data.add(value+"");
	    				analyseData.put(dnName, data);
	    				
	    				//if(!flag)
	    				//group.add(new CommonData(value+"",dnName));
	    			}
	    			
	    			analyseData.put("legendData", legendData);
	    			analyseData.put("xAxisData", xData);
	    			valueList.add(analyseData);
	    		}
	    		
    		}else{//只有一集统计项目，生成简单统计饼图 数据
  	    			     		   
  	    		   for(int i=0;i<upCodeList.size();i++){
  	    			   HashMap upCodeConfig = (HashMap)upCodeList.get(i);
   	    			 	String upStaticItemId = upCodeConfig.get("staticItemId").toString();
   	    			 	String upName = upCodeConfig.get("condName").toString();
  	    			 	double value = 0;
  	    			 	boolean b = true;
  	    			 	int totalCount = 0;
	 	    			//boolean flag = false;
	 	    			if(rs.first()){
	 	    				do{
	 	    					if(rs.getObject("cond_"+upStaticItemId)==null/* || rs.getObject("num")==null*/)
	 	    						continue;
	 	    					if(b && analyseType!=4){
	 	    						value = rs.getDouble("num");
	 	    						b = false;
	 	    						continue;
	 	    					}
	 	    					double itemValue = rs.getDouble("num");
	    						switch(analyseType){
		    						case 2:
		    								value = itemValue>value?itemValue:value;
		    								break;
		    						case 3:
		    								value = itemValue<value?itemValue:value;
		    								break;
		    						case 4:
		    								value+=itemValue;
		    								totalCount++;
		    								break;
		    						default://1:count 5:sum 都是求和，走这里
		    								value+=itemValue;
	    						}
//	 	    						valueList.add(new CommonData(rs.getDouble("compnum")+"",upName));
//	 	    						flag = true;
//	 	    						break;
	 	    				}while(rs.next());
	 	    			}
	 	    			if(analyseType==4 && totalCount>1)
	    					value = value/totalCount;
	 	    			//if(!flag)
	 	    			//	valueList.add(new CommonData(value+"",upName));
	 	    			
	 	    			HashMap item = new HashMap();
	 	    			item.put("name", upName);
	 	    			item.put("value", value+"");
	 	    			valueList.add(item);
  	    		   }
    		}
	    	
	    	chartXml = JSON.toString(valueList);
	    	/*
	    	//生成统计图
	        AnychartBo acb = new AnychartBo();
	        acb.setHeight(400);
	        acb.setTitle(title);
	        if(analyseType!=1){
		        	FieldItem item = DataDictionary.getFieldItem(itemType);
		        	acb.setNumDecimals(item.getDecimalwidth());
	        }
	        if(downItemid!=null){//分组柱状图
		        	acb.setChart_type(ChartConstants.VERTICAL_BAR);
		        	chartXml = acb.outGroupBarXml(valueList);
	        }else if(analyseType==2 || analyseType==3){//最大最小时显示柱状图
	        	    acb.setChart_type(ChartConstants.VERTICAL_BAR);
	        	    chartXml = acb.outBarXml(valueList, "false", "");
	        }else{//饼图
	        		acb.setChart_type(ChartConstants.PIE);
	        		chartXml = acb.outPieXml(valueList,"flase",false);
	        }
	        
	        */
	    	
	    }catch(Exception e){
	    		e.printStackTrace();
	    }finally{
	    		PubFunc.closeResource(rs);
	    }
	    
	    
		return chartXml;
	}
	
	/**
	 * 查询  统计分析 方案
	 * @return
	 */
	public ArrayList searchAnalysePlan(){
		
		ArrayList valueList = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		StringBuilder sb = new StringBuilder();
		
		sb.append("select static_plan_id,name from t_sys_table_static_plan where submoduleid=? and (username=? or is_share=1)");
		valueList.add(this.subModuleId);
		valueList.add(this.userView.getUserName());
		RowSet rs = null;
		try{
			rs = dao.search(sb.toString(), valueList);
			valueList.clear();
			while(rs.next()){
                HashMap plan = new HashMap();
                plan.put("value", rs.getInt("static_plan_id")+"");
                plan.put("name", rs.getString("name"));
                valueList.add(plan);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			valueList.clear();
		}finally{
			PubFunc.closeResource(rs);
		}
		return valueList;
	}
	
	/**
	 * 保存统计方案
	 * @param planName 方案名称
	 * @param is_share 是否共享
	 * @param analyseParams  方案参数
	 * @return
	 */
	public String  saveAnalysePlan(String planName,int is_share,DynaBean analyseParams){
		Element root = new Element("params");
		Document doc = new Document(root);
		Element rule = new Element("rule");
		root.addContent(rule);
		
		String planId = "";
		//统计类型
		int analyseType = ((Integer)analyseParams.get("analyseType")).intValue();
		//计算指标
	    String itemType = (String)analyseParams.get("itemType");
	    	itemType = itemType==null?"":itemType;
	    ArrayList  vItems = (ArrayList)analyseParams.get("v");
	    ArrayList hItems = (ArrayList)analyseParams.get("h");
	    
	    ArrayList saveSql = new ArrayList();
	    ContentDAO dao = new ContentDAO(conn);
	    Savepoint sp = null;
	    RowSet rs = null;
		try{
			conn.setAutoCommit(false);
			//设置 还原点。如果中途出错，还原数据
			sp = conn.setSavepoint();
			IDFactoryBean idBean = new IDFactoryBean();
			//横向数据保存
			for(int i=0;i<hItems.size();i++){
				DynaBean db = (DynaBean)hItems.get(i);
				String itemid = db.get("itemid").toString();
				String static_id = idBean.getId("t_sys_table_static.static_id", "", conn);;
				if(itemid.indexOf("static_")!=-1){
					 String linkStaticId = itemid.split("_")[1];
				     String sql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) select "+static_id+" as static_id,submoduleid,static_name,"+i+" as norder from t_sys_table_static  where static_id ="+linkStaticId;
				     saveSql.add(sql);
				     sql = "select static_item_id from t_sys_table_static_item where static_id="+linkStaticId;
				     rs = dao.search(sql);
				     while(rs.next()){
				    	 	String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
				    	 	sql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) select "
				    	 	       +static_item_id+" as static_item_id,"
				    	 		   +static_id+" as static_id,expr,factor,item_desc,norder from t_sys_table_static_item where static_item_id="
				    	 	       +rs.getInt("static_item_id");
				    	 	saveSql.add(sql);
				     }
				}else{
					FieldItem fi = DataDictionary.getFieldItem(itemid);
					ArrayList codeList = (ArrayList)db.get("code");
					String staticsql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) values("+static_id+",'"+this.subModuleId+"','"+fi.getItemdesc()+"',"+i+")";
					saveSql.add(staticsql);
					for(int k=0;k<codeList.size();k++){
						String code = codeList.get(k).toString();
						String codeName = AdminCode.getCodeName(fi.getCodesetid(), code);
						String factor = itemid+"="+code+"*";
						String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
						String itemsql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) values("+static_item_id+","+static_id+",'1','"+factor+"','"+codeName+"',"+k+")";
						saveSql.add(itemsql);
					}
				}
				Element H = new Element("H");
				H.setAttribute("id",static_id);
				rule.addContent(H);
				//二级 统计
				ArrayList childList = (ArrayList)db.get("child");
				for(int c=0;c<childList.size();c++){
					DynaBean cdb = (DynaBean)childList.get(c);
					String citemid = cdb.get("itemid").toString();
					String cstatic_id = idBean.getId("t_sys_table_static.static_id", "", conn);
					if(citemid.indexOf("static_")!=-1){
						 String linkStaticId = citemid.split("_")[1];
					     String sql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) select "+cstatic_id+" as static_id,submoduleid,static_name,"+i+" as norder from t_sys_table_static where static_id ="+linkStaticId;
					     saveSql.add(sql);
					     sql = "select static_item_id from t_sys_table_static_item where static_id="+linkStaticId;
					     rs = dao.search(sql);
					     while(rs.next()){
					    	 	String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
					    	 	sql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) select "
					    	 	       +static_item_id+" as static_item_id,"
					    	 		   +cstatic_id+" as static_id,expr,factor,item_desc,norder from t_sys_table_static_item where static_item_id="
					    	 	       +rs.getInt("static_item_id");
					    	 	saveSql.add(sql);
					     }
					}else{
						FieldItem cfi = DataDictionary.getFieldItem(citemid);
						ArrayList ccodeList = (ArrayList)cdb.get("code");
						String cstaticsql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) values("+cstatic_id+",'"+this.subModuleId+"','"+cfi.getItemdesc()+"',"+c+")";
						saveSql.add(cstaticsql);
						for(int d=0;d<ccodeList.size();d++){
							String code = ccodeList.get(d).toString();
							String codeName = AdminCode.getCodeName(cfi.getCodesetid(), code);
							String factor = citemid+"="+code+"*";
							String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
							String itemsql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) values("+static_item_id+","+cstatic_id+",'1','"+factor+"','"+codeName+"',"+d+")";
							saveSql.add(itemsql);
						}
					}
					Element cH = new Element("H");
					cH.setAttribute("id",cstatic_id);
					H.addContent(cH);
				}
			}
			//纵向数据保存
			for(int i=0;i<vItems.size();i++){
				DynaBean db = (DynaBean)vItems.get(i);
				String itemid = db.get("itemid").toString();
				String static_id = idBean.getId("t_sys_table_static.static_id", "", conn);
				if(itemid.indexOf("static_")!=-1){
					 String linkStaticId = itemid.split("_")[1];
				     String sql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) select "+static_id+" as static_id,submoduleid,static_name,"+i+" as norder from t_sys_table_static where static_id ="+linkStaticId;
				     saveSql.add(sql);
				     sql = "select static_item_id from t_sys_table_static_item where static_id="+linkStaticId;
				     rs = dao.search(sql);
				     while(rs.next()){
				    	 	String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
				    	 	sql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) select "
				    	 	       +static_item_id+" as static_item_id,"
				    	 		   +static_id+" as static_id,expr,factor,item_desc,norder from t_sys_table_static_item where static_item_id="
				    	 	       +rs.getInt("static_item_id");
				    	 	saveSql.add(sql);
				     }
				}else{
					FieldItem fi = DataDictionary.getFieldItem(itemid);
					ArrayList codeList = (ArrayList)db.get("code");
					String staticsql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) values("+static_id+",'"+this.subModuleId+"','"+fi.getItemdesc()+"',"+i+")";
					saveSql.add(staticsql);
					for(int k=0;k<codeList.size();k++){
						String code = codeList.get(k).toString();
						String codeName = AdminCode.getCodeName(fi.getCodesetid(), code);
						String factor = itemid+"="+code+"*";
						String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
						String itemsql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) values("+static_item_id+","+static_id+",'1','"+factor+"','"+codeName+"',"+k+")";
						saveSql.add(itemsql);
					}
				}
				Element V = new Element("V");
				V.setAttribute("id",static_id);
				rule.addContent(V);
				//二级 统计
				ArrayList childList = (ArrayList)db.get("child");
				for(int c=0;c<childList.size();c++){
					DynaBean cdb = (DynaBean)childList.get(c);
					String citemid = cdb.get("itemid").toString();
					String cstatic_id = idBean.getId("t_sys_table_static.static_id", "", conn);
					if(citemid.indexOf("static_")!=-1){
						 String linkStaticId = citemid.split("_")[1];
					     String sql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) select "+cstatic_id+" as static_id,submoduleid,static_name,"+i+" as norder from t_sys_table_static where static_id ="+linkStaticId;
					     saveSql.add(sql);
					     sql = "select static_item_id from t_sys_table_static_item where static_id="+linkStaticId;
					     rs = dao.search(sql);
					     while(rs.next()){
					    	 	String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
					    	 	sql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) select "
					    	 	       +static_item_id+" as static_item_id,"
					    	 		   +cstatic_id+" as static_id,expr,factor,item_desc,norder from t_sys_table_static_item where static_item_id="
					    	 	       +rs.getInt("static_item_id");
					    	 	saveSql.add(sql);
					     }
					}else{
						FieldItem cfi = DataDictionary.getFieldItem(citemid);
						ArrayList ccodeList = (ArrayList)cdb.get("code");
						String cstaticsql = "insert into t_sys_table_static(static_id,submoduleid,static_name,norder) values("+cstatic_id+",'"+this.subModuleId+"','"+cfi.getItemdesc()+"',"+c+")";
						saveSql.add(cstaticsql);
						for(int d=0;d<ccodeList.size();d++){
							String code = ccodeList.get(d).toString();
							String codeName = AdminCode.getCodeName(cfi.getCodesetid(), code);
							String factor = citemid+"="+code+"*";
							String static_item_id = idBean.getId("t_sys_table_static_item.static_item_id","",conn);
							String itemsql = "insert into t_sys_table_static_item(static_item_id,static_id,expr,factor,item_desc,norder) values("+static_item_id+","+cstatic_id+",'1','"+factor+"','"+codeName+"',"+d+")";
							saveSql.add(itemsql);
						}
					}
					Element cV = new Element("V");
					cV.setAttribute("id",cstatic_id);
					V.addContent(cV);
				}
			}
			
			
			dao.batchUpdate(saveSql);
			
			//生成对应 关系的 xml
			XMLOutputter outputter = new XMLOutputter();
	        	Format format0=Format.getPrettyFormat();
	        	format0.setEncoding("UTF-8");
	        	outputter.setFormat(format0);
	        	String planConn = outputter.outputString(doc);	
	        	
	        	String sql = "insert into t_sys_table_static_plan(static_plan_id,submoduleid,name,fielditem,static_type,param,username,is_share) values(?,?,?,?,?,?,?,?) ";
	        	ArrayList valueList = new ArrayList();
	        	planId = idBean.getId("t_sys_table_static_plan.static_plan_id", "", conn);
	        	valueList.add(planId);
	        	valueList.add(this.subModuleId);
	        	valueList.add(planName);
	        	valueList.add(itemType);
	        	valueList.add(analyseType);
	        	valueList.add(planConn);
	        	valueList.add(this.userView.getUserName());
	        	valueList.add(is_share);
			//保存到方案表	
	        	dao.insert(sql, valueList);
				
	        	conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try {
				conn.rollback(sp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return planId;
	}
	
	/**
	 * 通过方案id查询方案参数
	 * @param planId
	 * @return
	 */
	public HashMap getAnalysePlanConfig(String planId){
		HashMap planConfig = new HashMap();
		String sql = "select name,fielditem,static_type,param from t_sys_table_static_plan where static_plan_id="+planId;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(!rs.next())
				return null;
			String fielditem = rs.getString("fielditem");
			String static_type = rs.getString("static_type");
			planConfig.put("planId", planId);
			planConfig.put("name", rs.getString("name"));
			planConfig.put("analyseType", Integer.parseInt(static_type));
			planConfig.put("itemType", fielditem);
			
			
			String param = rs.getString("param");
			Document doc = PubFunc.generateDom(param);
			
			String path="/params/rule/H";
			XPath hxPath = XPath.newInstance(path);
			List HElements = hxPath.selectNodes(doc);
			ArrayList hconfig = new ArrayList();
			for(int i=0;i<HElements.size();i++){
				Element item = (Element)HElements.get(i);
				String static_id = item.getAttributeValue("id");
				HashMap itemInfo = getItemInfo(static_id);
				List childs = item.getChildren();
				ArrayList childList = new ArrayList();
				for(int k=0;k<childs.size();k++){
				    Element child = (Element)childs.get(k);
				    String cstatic_id = child.getAttributeValue("id");
				    HashMap citemInfo = getItemInfo(cstatic_id);
				    citemInfo.put("direction", "H");
				    citemInfo.put("level", 2);
				    childList.add(citemInfo);
				}
				itemInfo.put("child", childList);
				itemInfo.put("direction", "H");
				itemInfo.put("level", 1);
				hconfig.add(itemInfo);
			}
			
			path="/params/rule/V";
			XPath vxPath = XPath.newInstance(path);
			List VElements = vxPath.selectNodes(doc);
			ArrayList vconfig = new ArrayList();
			for(int i=0;i<VElements.size();i++){
				Element item = (Element)VElements.get(i);
				String static_id = item.getAttributeValue("id");
				HashMap itemInfo = getItemInfo(static_id);
				List childs = item.getChildren();
				ArrayList childList = new ArrayList();
				for(int k=0;k<childs.size();k++){
				    Element child = (Element)childs.get(k);
				    String cstatic_id = child.getAttributeValue("id");
				    HashMap citemInfo = getItemInfo(cstatic_id);
				    itemInfo.put("direction", "V");
					itemInfo.put("level", 2);
				    childList.add(citemInfo);
				}
				itemInfo.put("child", childList);
				itemInfo.put("direction", "V");
				itemInfo.put("level", 1);
				vconfig.add(itemInfo);
			}
			
			planConfig.put("h", hconfig);
			planConfig.put("v", vconfig);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		
		
		
		return planConfig;
	}
	
	/**
	 * 查询统计项信息
	 * @param static_id
	 * @return
	 */
	private HashMap getItemInfo(String static_id){
		HashMap itemInfo = new HashMap();
		String sql = "select static_name from t_sys_table_static  where static_id="+static_id;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if(!rs.next()){
				return null;
			}
			itemInfo.put("staticId", static_id);
			itemInfo.put("itemName", rs.getString("static_name"));
			
			sql = "select static_item_id,factor,expr,item_desc from t_sys_table_static_item where static_id="+static_id;
			
			rs = dao.search(sql);
			ArrayList conds = new ArrayList();
			while(rs.next()){
				HashMap itemMap = new HashMap();
                String factor = rs.getString("factor");
                String item_desc = rs.getString("item_desc");
                itemMap.put("staticItemId", rs.getInt("static_item_id")+"");
                itemMap.put("factor", factor);
                //String itemid = factor.split("=")[0];
                //itemInfo.put("itemid", itemid);
                itemMap.put("condName", item_desc);
                itemMap.put("expr", rs.getString("expr"));
                String [] facs = factor.split("`");
                HashMap codedesc = new HashMap();
                for(int i=0;i<facs.length;i++){
                	    String itemid = facs[i].substring(0, 5);
                	    FieldItem fi = DataDictionary.getFieldItem(itemid);
                	    if(fi !=null && !"0".equals(fi.getCodesetid())){
                	    	    int  index = 0;
                	    	    if(facs[i].indexOf("<>")!=-1)
                	    	    	    index = facs[i].indexOf("<>")+2;
                	    	    	else if(facs[i].indexOf(">=")!=-1)
                	    	    		index = facs[i].indexOf(">=")+2;
                	    	    	else if(facs[i].indexOf("<=")!=-1)
                	    	    		index = facs[i].indexOf("<=")+2;
                	    	    	else if(facs[i].indexOf(">")!=-1)
                	    	    		index = facs[i].indexOf(">")+1;
                	    	    	else if(facs[i].indexOf("<")!=-1)
                	    	    		index = facs[i].indexOf("<")+1;
                	    	    	else 
                	    	    		index = facs[i].indexOf("=")+1;
                	    	    		
                	    	    
                	    	    String codevalue = facs[i].substring(index);
                	    	    if(codevalue.length()>0 && codevalue.indexOf("*")==codevalue.length()-1)
                	    	    	codevalue = codevalue.substring(0, codevalue.length()-1);
                	    	    String codename = AdminCode.getCodeName(fi.getCodesetid(), codevalue);
                	    	    if(codename!=null && codename.length()>0)
                	    	    		codedesc.put(facs[i], codename);
                	    }
                }
                itemMap.put("codedesc", codedesc);
                conds.add(itemMap);  
			}
			itemInfo.put("conds", conds);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return itemInfo;
	}
	
	/**
	 * 更新统计方案
	 * @param planConfig 变动数据
	 * @param planId 方案id
	 * @param planName 方案名称
	 */
	public void updatePlanConfig(DynaBean planConfig,String planId,String planName){
		
		Savepoint sp = null;
		RowSet rs = null;
		try{
			conn.setAutoCommit(false);
			sp = conn.setSavepoint();//设置还原点
			
			ContentDAO dao = new ContentDAO(conn);
			String itemType = (String)planConfig.get("itemType");
			String analyseType = planConfig.get("analyseType").toString();
			// 删除的统计项
			ArrayList  deleteItems = (ArrayList)planConfig.get("deleteItems");
			// 删除的统计条件
	        ArrayList deleteConds = (ArrayList)planConfig.get("deleteCond");
	        // 新增的统计项
	        ArrayList insertItems = (ArrayList)planConfig.get("insertItems");
	        // 新增的统计条件
	        ArrayList insertConds = (ArrayList)planConfig.get("insertCond");
	        // 修改的统计条件
	        ArrayList updateConds = (ArrayList)planConfig.get("updateCond");
	        // 修改名称的统计项
	        ArrayList updateItems = (ArrayList)planConfig.get("updateItems");
	        
	        //检查方案是否存在
	        String sql = "select param,fielditem,static_type from t_sys_table_static_plan where static_plan_id="+planId;
	        rs = dao.search(sql);
	        if(!rs.next())
	        	return;
	        
	        //创建 连接 关系doc
	        String paramXml = rs.getString("param");
			Document doc = PubFunc.generateDom(paramXml);
			XPath xpath = null;
			
			//删除选中的统计指标
	        deletePlanItems(deleteItems,dao);
	        ArrayList childItems = new ArrayList();
	        //是包含下级指标
	        for(int i=0;i<deleteItems.size();i++){
	        	String  staticId = deleteItems.get(i).toString();
	        	xpath = xpath.newInstance("/params/rule//*[@id="+staticId+"]");
	        	Element node = (Element)xpath.selectSingleNode(doc);
	        	List childs = node.getChildren();
	        	if(!childs.isEmpty()){
	        		for(int k=0;k<childs.size();k++){
	        	        Element cd = (Element)childs.get(k);
	        			childItems.add(cd.getAttributeValue("id"));
	        		}
	        	}
	        	node.getParentElement().removeContent(node);
	        }
	        //删除二级指标
	        deletePlanItems(childItems,dao);
	        
	        // 删除条件
	        deletePlanConds(deleteConds,dao);
	        
	        //插入选中的统计指标
	        insertPlanItems(insertItems,dao,doc);
	        //插入统计条件
	        insertPlanConds(insertConds,dao);
	        //修改统计条件
	        updatePlanConds(updateConds,dao);
	        //修改统计项名称
	        updatePlanItems(updateItems,dao);
	        XMLOutputter outputter = new XMLOutputter();
	        	Format format0=Format.getPrettyFormat();
	        	format0.setEncoding("UTF-8");
	        	outputter.setFormat(format0);
	        	//重新生成 连接关系 xml
	        	String planConn = outputter.outputString(doc);	
	        	// 更新 方案 信息
	        	sql = "update t_sys_table_static_plan set param=?,fielditem=?,static_type=?,name=? where static_plan_id=?";
	        	ArrayList valueList = new ArrayList();
	        	valueList.add(planConn);
	        	valueList.add(itemType==null?"":itemType);
	        	valueList.add(analyseType);
	        	valueList.add(planName);
	        	valueList.add(planId);
	        	dao.update(sql,valueList);
	        
	        conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try {
				conn.rollback(sp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			PubFunc.closeResource(rs);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void deletePlanItems(ArrayList items,ContentDAO dao) throws SQLException{
			String sql = "";
			for(int i=0;i<items.size();i++){
			  	sql = "delete t_sys_table_static where static_id="+items.get(i);
			  	dao.delete(sql, new ArrayList());
			  	sql = "delete t_sys_table_static_item where static_id="+items.get(i);
			  	dao.delete(sql, new ArrayList());
			}
	}
	
	private void deletePlanConds(ArrayList conds,ContentDAO dao) throws SQLException{
		String sql = "";
		for(int i=0;i<conds.size();i++){
			sql = "delete t_sys_table_static_item where static_item_id="+conds.get(i);
		  	dao.delete(sql, new ArrayList());
		}
	}
	
	private void insertPlanItems(ArrayList items,ContentDAO dao,Document doc) throws Exception{
		String sql = "";
		IDFactoryBean id = new IDFactoryBean();
		for(int i=0;i<items.size();i++){
			DynaBean item = (DynaBean)items.get(i);
		    HashMap itemMap =  PubFunc.DynaBean2Map(item);
		    String staticid = id.getId("t_sys_table_static.static_id", "", this.conn);
		    String itemid = itemMap.get("itemid").toString();
		    FieldItem fi = DataDictionary.getFieldItem(itemid);
		    String name = fi.getItemdesc();
			sql = "insert into t_sys_table_static(static_id,static_name,submoduleid) values("+staticid+",'"+name+"','"+this.subModuleId+"')";
			dao.insert(sql, new ArrayList());
			ArrayList conds = (ArrayList)itemMap.get("conds");
			ArrayList condsConfig = new ArrayList();
			for(int k=0;k<conds.size();k++){
				String cond = conds.get(k).toString();
			    DynaBean condconf = new LazyDynaBean();
			    condconf.set("ownerStaticId", staticid);
			    condconf.set("factor",itemid+"="+cond+"*");
			    condconf.set("expr","1");
			    String condName = AdminCode.getCodeName(fi.getCodesetid(), cond);
			    condconf.set("condName", condName);
			    condsConfig.add(condconf);
			}
			insertPlanConds(condsConfig,dao);
			if(itemMap.containsKey("upStaticId")){
					String upStaticId = itemMap.get("upStaticId").toString();
			    	XPath xpath = XPath.newInstance("/params/rule//*[@id="+upStaticId+"]");
			    	Element  parent= (Element)xpath.selectSingleNode(doc);
			    	String direction = parent.getName();
			    	if(parent!=null){
			    		Element e = new Element(direction);
			    		e.setAttribute("id",staticid);
			    		parent.addContent(e);
			    	}
			    	continue;
			}
			String direction = itemMap.get("direction").toString();
			Element itemEl = new Element(direction);
			itemEl.setAttribute("id",staticid);
			XPath xpath = XPath.newInstance("/params/rule");
			Element rule = (Element)xpath.selectSingleNode(doc);
			rule.addContent(itemEl);
			
			ArrayList child = (ArrayList)itemMap.get("child");
			if(child==null || child.isEmpty())
				continue;
			for(int d=0;d<child.size();d++){
				DynaBean dy = (DynaBean)child.get(d);
			    String cstaticid = id.getId("t_sys_table_static.static_id", "", this.conn);
			    String citemid = dy.get("itemid").toString();
			    FieldItem cfi = DataDictionary.getFieldItem(citemid);
			    String cname = cfi.getItemdesc();
				sql = "insert into t_sys_table_static(static_id,static_name,submoduleid) values("+cstaticid+",'"+cname+"','"+this.subModuleId+"')";
				dao.insert(sql, new ArrayList());
				ArrayList cconds = (ArrayList)dy.get("conds");
				ArrayList ccondsConfig = new ArrayList();
				for(int k=0;k<cconds.size();k++){
					String cond = cconds.get(k).toString();
				    DynaBean condconf = new LazyDynaBean();
				    condconf.set("ownerStaticId", cstaticid);
				    condconf.set("factor",itemid+"="+cond+"*");
				    condconf.set("expr","1");
				    String condName = AdminCode.getCodeName(cfi.getCodesetid(), cond);
				    condconf.set("condName", condName);
				    ccondsConfig.add(condconf);
				}
				insertPlanConds(ccondsConfig,dao);
				Element citemEl = new Element(direction);
				citemEl.setAttribute("id",cstaticid);
				itemEl.addContent(citemEl);
			}
			
			
		}
	}
	private void insertPlanConds(ArrayList conds,ContentDAO dao) throws Exception{
		IDFactoryBean id = new IDFactoryBean();
		String sql = "";
		RowSet rs = null;
		try{
			int norder = 0;
			String preStaticid = null;
			for(int i=0;i<conds.size();i++){
				DynaBean cond = (DynaBean)conds.get(i);
				String staticid = cond.get("ownerStaticId").toString();
				String statciitemid = id.getId("t_sys_table_static_item.static_item_id", "", this.conn);
				String factor = cond.get("factor").toString();
				String expr = cond.get("expr").toString();
				String condName = cond.get("condName").toString();
				
				sql = "select count(1) from t_sys_table_static where static_id="+staticid;
				rs = dao.search(sql);
				if(!rs.next())
					continue;
				if(!staticid.equals(preStaticid)){
					sql = "select max(norder) maxnorder from t_sys_table_static_item where static_id="+staticid;
					if(rs.next())
						norder = rs.getInt("maxnorder");
				}
				
				sql = " insert into t_sys_table_static_item(static_item_id,static_id,factor,expr,item_desc,norder) values("+statciitemid+","+staticid+",'"+factor+"','"+expr+"','"+condName+"',"+norder+" ) ";
				dao.insert(sql, new ArrayList());
				norder++;
			}
		}catch(Exception e){
			throw e;
		}finally{
		  PubFunc.closeResource(rs);
		}
	}
	
	public void deleteAnalysePlan(String planId){
		RowSet rs = null;
		Savepoint sp = null;
		try{
			conn.setAutoCommit(false);
			sp = conn.setSavepoint();
			ContentDAO dao = new ContentDAO(conn);
			 
		    String sql = "select param from t_sys_table_static_plan where static_plan_id="+planId;
		    rs = dao.search(sql);
		    
		    if(!rs.next())
		    	return;
		    String paramXml = rs.getString("param");
			Document doc = PubFunc.generateDom(paramXml);
			XPath xpath = XPath.newInstance("/params/rule//*");
			List statics = xpath.selectNodes(doc);
			Element el = null;
			String staticId = null;
			ArrayList sqls = new ArrayList();
			for(int i=0;i<statics.size();i++){
				el = (Element)statics.get(i);
				staticId = el.getAttributeValue("id");
				sql = "delete t_sys_table_static where static_id ="+staticId;
				sqls.add(sql);
				sql = "delete t_sys_table_static_item where static_id="+staticId;
				sqls.add(sql);
			}
			
			sql = "delete t_sys_table_static_plan where static_plan_id ="+planId;
			sqls.add(sql);
			doc = null;
			dao.batchUpdate(sqls);
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try {
				conn.rollback(sp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			PubFunc.closeResource(rs);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updatePlanConds(ArrayList conds,ContentDAO dao) throws Exception{
		String sql = "update t_sys_table_static_item set item_desc=?,factor=?,expr=? where static_item_id=?";
		List values = new ArrayList();
		for(int i=0;i<conds.size();i++){
			DynaBean cond = (DynaBean)conds.get(i);
			String staticItemId = cond.get("staticItemId").toString();
			String factor = cond.get("factor").toString();
			String expr = cond.get("expr").toString();
			String condName = cond.get("condName").toString();
			ArrayList value = new ArrayList();
			value.add(condName);
			value.add(factor);
			value.add(expr);
			value.add(staticItemId);
			values.add(value);
		}
		dao.batchUpdate(sql, values);
	}
	
	private void updatePlanItems(ArrayList items,ContentDAO dao) throws Exception{
		String sql = "update t_sys_table_static set static_name = ? where static_id=?";
		List values = new ArrayList();
		for(int i=0;i<items.size();i++){
			DynaBean db = (DynaBean)items.get(i);
			String staticName = db.get("staticName").toString();
			String staticId = db.get("staticId").toString();
			ArrayList value = new ArrayList();
			value.add(staticName);
			value.add(staticId);
			values.add(value);
		}
		dao.batchUpdate(sql, values);
	}
	
	/**
	 * 查询 统计分析 方案 统计项
	 * @return
	 */
	public ArrayList searchAnalysePlanItems(){
		ArrayList values = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select param,name from t_sys_table_static_plan where submoduleid=? and (username=? or is_share=?)";
			values.add(subModuleId);
			values.add(userView.getUserName());
			values.add(new Integer(1));	
			
			rs = dao.search(sql, values);
			values.clear();
			while(rs.next()){
				String param = rs.getString("param");
				Document doc = PubFunc.generateDom(param);
				XPath xpath = XPath.newInstance("/params/rule//*");
				List elements = xpath.selectNodes(doc);
				StringBuffer s = new StringBuffer("select static_id,static_name from t_sys_table_static where static_id in(");
				for(int i=0;i<elements.size();i++){
					Element item = (Element)elements.get(i);
					String staticId = item.getAttributeValue("id");
					s.append(staticId+",");
				}
				s.append("-1)");
				List data = ExecuteSQL.executeMyQuery(s.toString());
				while(!data.isEmpty()){
					LazyDynaBean ldb = (LazyDynaBean)data.get(0);
					HashMap item = new HashMap();
					item.put("name",ldb.get("static_name")+"(方案："+rs.getString("name")+")");
					item.put("value", "static_"+ldb.get("static_id"));
					item.put("isplan", "1");
					
					String condsql = "select item_desc from t_sys_table_static_item where static_id="+ldb.get("static_id");
					List condList = ExecuteSQL.executeMyQuery(condsql);
					ArrayList list = new ArrayList();
					for(int i=0;i<condList.size();i++) {
						LazyDynaBean cond = (LazyDynaBean)condList.get(i);
						list.add(cond.get("item_desc"));
					}
					item.put("condList",list);
					values.add(item);
					data.remove(0);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			values.clear();
		}finally{
			PubFunc.closeResource(rs);
		}
		
		return values;
	}
	
}
