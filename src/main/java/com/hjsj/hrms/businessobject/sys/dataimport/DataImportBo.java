/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * <p>
 * Title:DataImportBo
 * </p>
 * <p>
 * Description:数据导入业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-29
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class DataImportBo {

	// 数据库连接
	private Connection conn;
	
	// 
	private int countNum = 0;
	
	//同步流程数据并涉及到移库操作，需将移库的人员信息记录下来  dengcan
	private HashSet operateDataSet=new HashSet();
	/**导入数据用的临时变表名*/
   private String importTempTable="";
	public DataImportBo() {

	}

	public DataImportBo(Connection conn) {
		this.conn = conn;
	}

	private String getLetter() {
		String []lets = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		if (countNum < lets.length) {			
			countNum ++;
		} else {
			countNum = 0;
		}
		
		return lets[countNum];
	}
	/**
	 * 获取人员库列表
	 * 
	 * @return ArrayList<CommonData>
	 */
	public ArrayList getNbaseList() {
		ArrayList nbaseList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select dbname,pre from dbname";
			rs = dao.search(sql);
			while (rs.next()) {
				String dbName = rs.getString("dbname");
				String pre = rs.getString("pre");
				CommonData data = new CommonData();
				data.setDataName(dbName);
				data.setDataValue(pre);
				nbaseList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nbaseList;
	}

	/**
	 * 获取数据库类型列表
	 * 
	 * @return ArrayList<CommonData>
	 */
	public ArrayList getDbTypeList() {
		ArrayList dbTypeList = new ArrayList();
		CommonData data = new CommonData();
		data.setDataName("ORACLE");
		data.setDataValue("oracle");
		dbTypeList.add(data);

		data = new CommonData();
		data.setDataName("SQLSERVER");
		data.setDataValue("mssql");
		dbTypeList.add(data);

		return dbTypeList;
	}

	/**
	 * 获取任意表的字段名称
	 * 
	 * @return
	 */
	public ArrayList getTableFieldList(String tableName) {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList fieldList = new ArrayList();
		RowSet rs = null;
		try {
			String sql = "SELECT syscolumns.name names,systypes.name type,syscolumns.isnullable, "
					+ "syscolumns.length  FROM syscolumns, systypes  WHERE syscolumns.xusertype"
					+ " = systypes.xusertype  AND syscolumns.id = object_id('"
					+ tableName + "')";

			if (Sql_switcher.searchDbServer() == 2) {
				sql = "select COLUMN_NAME names,DATA_TYPE,DATA_PRECISION,DATA_SCALE,NULLABLE "
						+ " from user_tab_columns "
						+ " where table_name ='"
						+ tableName.toUpperCase() + "'";
			}
			rs = dao.search(sql);
			///wangrd 20190720  特殊处理A01表，如果传过来xxxA01,目前前台界面不显示指标描述，不方便做映射关系，改成同子集界面一样,
			//1、显示指标描述 2、按指标顺序显示，其他系统字段放到最后。
			if(StringUtils.isNotBlank(tableName) && (tableName.toUpperCase().endsWith("A01"))&& tableName.length()==6) {
				String set= tableName.substring(3, tableName.length());
				ArrayList dic = DataDictionary.getFieldList(set.toUpperCase(), Constant.USED_FIELD_SET);
				for (int i = 0; i < dic.size(); i++) {
                    FieldItem item = (FieldItem) dic.get(i);
                    CommonData data = new CommonData();
                    data.setDataName(item.getItemdesc());
                    data.setDataValue(item.getItemid());
                    fieldList.add(data);
                }                
			}
			
			while (rs.next()) {
				String filedName = rs.getString("names");
				//已加载的就不再加载了，保证显示顺序与指标集一致。
				if (isAddedTheField(fieldList,filedName)){
					continue;					
				}
				CommonData data = new CommonData();
				data.setDataValue(filedName);
				FieldItem item =DataDictionary.getFieldItem(filedName);
				if (item!=null) {
					data.setDataName(item.getItemdesc());	
				}
				else {
					data.setDataName(filedName);	
				}			
				fieldList.add(data);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return fieldList;

	}
	
	private boolean isAddedTheField(ArrayList fieldList, String fieldname) {
		boolean b = false;
		try {
			for (int i = 0; i < fieldList.size(); i++) {
				CommonData item = (CommonData) fieldList.get(i);
				if (item.getDataValue().equalsIgnoreCase(fieldname)) {
					b = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	private boolean bWriteLog=false;//是否记录日志，慧聪网有问题，记录了日志，如果不启用，则设为false
	public void logError(String info) {
		try {
			if (bWriteLog) {
				Category.getInstance(this.getClass()).error(info);
			}
		} catch (Exception e) {
		} finally {
		}
	}
	
	
	/**
	 * 导入数据
	 * @param conn  eHR数据库连接
	 * @param jobId 作业类ID
	 * @param ehrWhere eHR系统数据保护条件
	 * @param extWhere 外部系统数据过滤条件
	 * @param flag   空（默认）按常用规则导入数据，gz:为薪资临时表导入数据  rsyd:人事异动
	 * @param paramBean  参数
	 */
	public void importData(Connection conn, String jobId, String ehrWhere,
			String extWhere,String flag,LazyDynaBean paramBean)throws GeneralException {
		try {
			  innerImportData(conn,jobId,ehrWhere,extWhere,flag,paramBean);
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(e);	
			}finally{
			}
	}

	/** 目前无法定位慧聪网的问题，将这个方法改成带返回值的方法，并由importData改名为innerImportData wangrd 20170515 
	 * 为了兼容已发布成品的调用方法，在此方法上面加了同名方法
	 * 导入数据
	 * @param conn  eHR数据库连接
	 * @param jobId 作业类ID
	 * @param ehrWhere eHR系统数据保护条件
	 * @param extWhere 外部系统数据过滤条件
	 * @param flag   空（默认）按常用规则导入数据，gz:为薪资临时表导入数据  rsyd:人事异动
	 * @param paramBean  参数
	 * returnflag ：1 成功 2：失败
	 */
	public String innerImportData(Connection conn, String jobId, String ehrWhere,
			String extWhere,String flag,LazyDynaBean paramBean)throws GeneralException {
		String returnFlag="2";//默认返回 失败
		try {
			// 获取所有参数
			ConstantXml constantXml = new ConstantXml(conn, "IMPORTINFO", "params");
			orderParamNodeById(constantXml);
			
			List listEl = constantXml.getAllChildren("/params");			
			
			for (int i = 0; i < listEl.size(); i++) {
				Element els = (Element) listEl.get(i);
				String id = els.getAttributeValue("id");
				Map connMap = new HashMap();
				// 作业类
				String jobClass = constantXml
						.getTextValue("/params/param[@id='" + id
								+ "']/jobclass");

				// 是否启用
				String enable = constantXml.getTextValue("/params/param[@id='"
						+ id + "']/enable");

				logError("importlog准备导入:"+jobClass+":"+jobId);
				if (!"1".equals(enable) || !jobId.equalsIgnoreCase(jobClass)) {
					logError("importlog无需导入:"+jobClass+":"+jobId);					
					continue;
				}
				
				logError("importlog开始导入:"+jobClass+":"+jobId+":"+enable);
				// 名称
				String name = constantXml.getTextValue("/params/param[@id='"
						+ id + "']/name");

				// 人员库
				String nbase = constantXml.getTextValue("/params/param[@id='"
						+ id + "']/nbase");

				// 数据库类型
				String dbType = constantXml.getTextValue("/params/param[@id='"
						+ id + "']/dbtype");
				connMap.put("dbType", dbType);

				// 数据库URL
				String dbUrl = constantXml.getTextValue("/params/param[@id='"
						+ id + "']/dburl");
				connMap.put("dbUrl", dbUrl);

				// 数据库用户名
				String userName = constantXml
						.getTextValue("/params/param[@id='" + id + "']/dbuser");
				connMap.put("userName", userName);

				// 数据库密码
				String password = constantXml
						.getTextValue("/params/param[@id='" + id + "']/dbpwd");
				connMap.put("password", password);

				// ehr表名
				String ehrTable = constantXml
						.getTextValue("/params/param[@id='" + id
								+ "']/mappings/ehrtable");

				// 外部表名
				String extTable = constantXml
						.getTextValue("/params/param[@id='" + id
								+ "']/mappings/exttable");

				// hr关联指标
				String hrRelation = constantXml.getNodeAttributeValue(
						"/params/param[@id='" + id + "']/mappings/relation",
						"hr");

				// 外部系统关联指标
				String extRelation = constantXml.getNodeAttributeValue(
						"/params/param[@id='" + id + "']/mappings/relation",
						"ext");

				// 外部系统过滤条件
				String srcTabCond = constantXml
						.getTextValue("/params/param[@id='" + id
								+ "']/mappings/srctabcond");
				/* xml里保存公式<>符号是经过转码的，此处需要转回来，否则sql报错 guodd 2019-05-08*/
				srcTabCond = PubFunc.toReplaceStr(srcTabCond);
				
				// hr数据保护条件
				String tagTabCond = constantXml
						.getTextValue("/params/param[@id='" + id
								+ "']/mappings/tagtabcond");
				/* xml里保存公式<>符号是经过转码的，此处需要转回来，否则sql报错 guodd 2019-05-08*/
				tagTabCond = PubFunc.toReplaceStr(tagTabCond);
				
				// 指标关联
				List list = constantXml.getAllChildren("/params/param[@id='"
						+ id + "']/mappings");				

				// 字段关联关系
				Map refMap = new HashMap();
				// 主键列表
				ArrayList keyList = new ArrayList();
				// ehr字段集合
				ArrayList ehrFields = new ArrayList();
				// 外部系统字段集合
				ArrayList extFields = new ArrayList();
				// 主键集合
				Map keyMap = new HashMap();
				// 默认值
				Map defMap = new HashMap();

				ArrayList ehrAllFields = new ArrayList();

				for (int j = 0; j < list.size(); j++) {
					Element el = (Element) list.get(j);
					if ("fieldref".equals(el.getName())) {
						String hrfield = el.getAttributeValue("hrfield");
						String extfield = el.getAttributeValue("extfield");
						String ispk = el.getAttributeValue("ispk");
						String defvalue = el.getAttributeValue("defaultvalue");

						ehrAllFields.add(hrfield);

						if (extfield != null && extfield.trim().length() > 0) {
							refMap.put(extfield, hrfield);
							extFields.add(extfield);
							ehrFields.add(hrfield);
						}

						if ("1".equals(ispk)) {
							keyMap.put(hrfield, ispk);
							keyList.add(hrfield);
						}

						if (defvalue != null && defvalue.trim().length() > 0) {
							defMap.put(hrfield, defvalue);
						}

					}
				}

				// 只执行已启动的配置连接
				if ("1".equals(enable)) {
					
					// 添加eHR系统过滤条件
					if (ehrWhere != null && ehrWhere.trim().length() > 0) {
						if (tagTabCond != null && tagTabCond.trim().length() > 0) {
							tagTabCond = tagTabCond + " and (" + ehrWhere + ")";
						} else {
							tagTabCond =  ehrWhere;
						}
					} 
					
					// 添加外部系统过滤条件
					if (extWhere != null && extWhere.trim().length() > 0) {
						if (srcTabCond != null && srcTabCond.trim().length() > 0) {
							srcTabCond = srcTabCond + " and (" + extWhere + ")";
						} else {
							srcTabCond = extWhere;
						}
					}
					logError("importlog中间表过滤条件:"+srcTabCond);
					if("gz".equalsIgnoreCase(flag)){
						String salaryid=(String)paramBean.get("salaryid");
						String username=(String)paramBean.get("username");
						ehrTable=username+"_salary_"+salaryid;
						
					}
					logError("importlog开始导入:"+jobClass);
					// 导入数据
					innerImportData(nbase, connMap, ehrTable, extTable, hrRelation,
							extRelation, srcTabCond, tagTabCond, refMap,
							keyList, ehrFields, extFields, keyMap, defMap,
							ehrAllFields,flag,paramBean);
					logError("importlog导入完成:"+jobClass);

				}
			}
			
			/**
			 * 如果是同步人事异动数据并执行移库操作
			 */
			if("rsyd".equalsIgnoreCase(flag)&&paramBean!=null&&paramBean.get("to_nbase")!=null&&((String)paramBean.get("to_nbase")).trim().length()>0)
			{
				DbNameBo dbbo=new DbNameBo(this.conn);
				
				for(Iterator t=this.operateDataSet.iterator();t.hasNext();)
				{
					String str=(String)t.next();
					String[] temps=str.split("_");
			//		System.out.println(temps[1]+"  "+temps[0]+"  "+(String)paramBean.get("to_nbase"));
					logError("importlog需要移库:"+(String)paramBean.get("to_nbase"));
					dbbo.moveDataBetweenBase2(temps[1],temps[0],(String)paramBean.get("to_nbase"),"1");
					logError("importlog移库完成:"+(String)paramBean.get("to_nbase"));
				}
			}
			
			returnFlag="1";
		} catch (Exception e) {
			logError("importlog出错:"+e.getMessage()+ " returnFlag :"+returnFlag);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}finally{
			try{
				//if(this.conn!=null)
					//this.conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return returnFlag;
	}
	
	//为了兼容以前的调用方法暂时保留，无调用时 ，可以删除
	public void importData(String nbase, Map extConnMap, String ehrTable,
			String extTable, String hrRelation, String extRelation,
			String srcTabCond, String tagTabCond, Map refMap,
			ArrayList keyList, ArrayList ehrFields, ArrayList extFields,
			Map keyMap, Map defMap, ArrayList ehrAllFields,String flag,LazyDynaBean paramBean) throws Exception {
			try {	
				innerImportData(nbase, extConnMap, ehrTable,
						extTable, hrRelation, extRelation,
						srcTabCond, tagTabCond, refMap,
						keyList, ehrFields, extFields,
						keyMap, defMap, ehrAllFields,flag,paramBean);
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);	
			}finally{
				try{
					//if(this.conn!=null)
						//this.conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		
	}
	

	/**
	 * 导入数据
	 * 
	 * @param nbase
	 *            人员库，多个以逗号分隔
	 * @param extConnMap
	 *            外部系统数据库连接
	 * @param jobClass
	 *            作业类
	 * @param ehrTable
	 *            ehr表名
	 * @param extTable
	 *            外部系统表名
	 * @param hrRelation
	 *            hr系统主键关联字段
	 * @param extRelation
	 *            外部系统主键关联字段
	 * @param srcTabCond
	 *            外部系统数据过滤条件
	 * @param tagTabCond
	 *            hr系统数据保护条件
	 * @param refMap
	 *            字段关联关系
	 * @param keyList
	 *            主键集合
	 * @param ehrFields
	 *            ehr字段集合
	 * @param extFields
	 *            外部系统字段集合
	 * @param keyMap
	 *            主键集合
	 * @param defMap
	 *            默认值集合
	 * @param flag   空（默认）按常用规则导入数据，gz:为薪资临时表导入数据  rsyd:人事异动
	 * @param paramBean  参数
	 */
	public String innerImportData(String nbase, Map extConnMap, String ehrTable,
			String extTable, String hrRelation, String extRelation,
			String srcTabCond, String tagTabCond, Map refMap,
			ArrayList keyList, ArrayList ehrFields, ArrayList extFields,
			Map keyMap, Map defMap, ArrayList ehrAllFields,String flag,LazyDynaBean paramBean) throws Exception {

		Connection extConn = null;
		String returnFlag="2";
		try {
			// 获取外部系统数据库连接参数
			String dbType = extConnMap.get("dbType").toString();
			String dbUrl = extConnMap.get("dbUrl").toString();
			String userName = extConnMap.get("userName").toString();
			String password = extConnMap.get("password").toString();
			//55155 表名如果有点的话，oracle点前面的会当成用户解析，导致建表错误。将点转换为下划线 guodd 2019-11-12
			String impTab = "t#" + getLetter() +"_" + extTable.replace(".","_");//"t#job_imp";// "t_" + extTable + "_to_" + tagTable +
										// "importdata"
            this.setImportTempTable(impTab);
			if(ehrTable.trim().length()>3&& "a00".equalsIgnoreCase(ehrTable.substring(ehrTable.length()-3))) {
                ehrTable=ehrTable.substring(3);
            }
			// 创建临时表
			String[] nbases = nbase.split(",");
			String tagTable = "";
			if (!isMainSet(ehrTable)) {   //不是子集
				tagTable = ehrTable;
			} else {
				tagTable = nbases[0] + ehrTable;
			} 
			
			this.createTemp(tagTable, impTab, SystemConfig.getProperty("dbserver"),flag);

			// 添加关联指标
			if (hrRelation != null && hrRelation.trim().length() > 0) {
				this.addClonum(impTab, hrRelation);
			}

			logError("importlog连接外部数据库:");
			// 创建外部系统数据库连接
			if ("mssql".equalsIgnoreCase(dbType)) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} else if ("oracle".equalsIgnoreCase(dbType)) {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			}

			extConn = DriverManager.getConnection(dbUrl, userName, password);
			logError("importlog连接成功");
			logError("importlog数据同步到临时表");
			this.syncToTemp(extTable, impTab, extConn, this.conn, srcTabCond,
					extFields, refMap, hrRelation, extRelation, defMap,dbType);
			 
			// 更新临时表其他必要信息及默认值
			if(paramBean==null) {
                paramBean=new LazyDynaBean();
            }
			paramBean.set("ehrTable",ehrTable);
			logError("按条件更新临时表");
			updateCondToTemp(impTab, nbases, isMainSet(ehrTable), ehrTable, keyMap,
					keyList, hrRelation, ehrAllFields, ehrFields,flag,paramBean,tagTabCond.toLowerCase());
			
			deleteTemp(impTab, tagTabCond,keyList,refMap,tagTable);
			updateTemp(impTab, defMap, nbases, hrRelation,flag,paramBean);
		

			// 删除临时表中不需要更新的数据
			if("gz".equalsIgnoreCase(flag))
			{
				Set keySet = refMap.keySet();
				StringBuffer whereBuf = new StringBuffer();
				StringBuffer insertColumn=new StringBuffer();
				StringBuffer selectColumn = new StringBuffer();
				StringBuffer whereBuf2=new StringBuffer();
			    StringBuffer deleteBuf = new StringBuffer();
				String gztablename=(String)paramBean.get("username")+"_salary_"+(String)paramBean.get("salaryid");
				for(Iterator it=keySet.iterator();it.hasNext();)
				{
					String key = (String)it.next();
					if("psncode".equalsIgnoreCase(key)|| "deptcode".equalsIgnoreCase(key)|| "unitcode".equalsIgnoreCase(key))
					{
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
					    	whereBuf.append(" and S."+(String)refMap.get(key)+"=T."+(String)refMap.get(key));
					    	whereBuf2.append(" and T."+(String)refMap.get(key)+"=S."+(String)refMap.get(key));
						}else{
							whereBuf.append(" and "+gztablename+"."+(String)refMap.get(key)+"=S."+(String)refMap.get(key));
						}
						deleteBuf.append(" and "+impTab+"."+(String)refMap.get(key)+"=b."+(String)refMap.get(key));
						if("deptcode".equalsIgnoreCase(key)) {
                            paramBean.set("deptcodefield", (String)refMap.get(key));
                        }
						if("unitcodefield".equalsIgnoreCase(key)) {
                            paramBean.set("unitcodefield", (String)refMap.get(key));
                        }
						
					}
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						insertColumn.append(",S."+(String)refMap.get(key));
						selectColumn.append(",T."+(String)refMap.get(key));
					}else{
						insertColumn.append(","+(String)refMap.get(key)+"=S."+(String)refMap.get(key));
					}
				}
				String gzperiod=(String)paramBean.get("gzperiod");
				String year=gzperiod.substring(0,4);
				String month=gzperiod.substring(4,6);
				StringBuffer updateBuf = new StringBuffer();
				 ContentDAO dao = new ContentDAO(this.conn);
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					
					updateBuf.append(" update "+gztablename+" S set(");
					updateBuf.append(insertColumn.toString().substring(1));
				    updateBuf.append(")=(select ");
				    updateBuf.append(selectColumn.toString().substring(1));
				    updateBuf.append(" from "+impTab+" T where S.a00z0=T.A00z0 "+whereBuf.toString()+" ");
				    updateBuf.append(" and "+Sql_switcher.year("S.a00z0")+"="+year+" and "+Sql_switcher.month("S.a00z0")+"="+month+")");
				    updateBuf.append(" where exists (");
				    updateBuf.append(" select null from "+impTab+" T where ");
				    updateBuf.append(" T.A00z0=S.a00z0 "+whereBuf2+"");
				    updateBuf.append(" and "+Sql_switcher.year("T.a00z0")+"="+year+" and "+Sql_switcher.month("T.a00z0")+"="+month+")");
				    dao.update(updateBuf.toString());
				    updateBuf.setLength(0);
					updateBuf.append(" delete from "+impTab+" where exists (select null from  "+gztablename+" b where "+impTab+".a00z0=b.a00z0 ");
					updateBuf.append(deleteBuf.toString()+")");
					dao.delete(updateBuf.toString(), new ArrayList());
					
				}else{
					
					updateBuf.append(" UPDATE "+gztablename+" SET ");
					updateBuf.append(insertColumn.toString().substring(1));
					updateBuf.append(" FROM "+gztablename+" LEFT JOIN "+impTab+" S ON "+gztablename+".a00z0=S.a00z0 "+whereBuf.toString());
					updateBuf.append(" and "+Sql_switcher.year(gztablename+".a00z0")+"="+year+" and "+Sql_switcher.month(gztablename+".a00z0")+"="+month);
					dao.update(updateBuf.toString());
					updateBuf.setLength(0);
				    updateBuf.append(" delete from "+impTab+" where exists (select null from  "+gztablename+" b where "+impTab+".a00z0=b.a00z0 ");
				    updateBuf.append(deleteBuf.toString()+")");
					dao.delete(updateBuf.toString(), new ArrayList());
				}
				
			}
			if(flag!=null&& "gz".equalsIgnoreCase(flag)) //导入薪资临时数据  dengcan
			{
				impGzDataToTmp(paramBean,impTab,nbase,hrRelation);
			}
			logError("importlog从临时表更新:");
			// 将临时表信息更新到目标表中
			updateToTag(impTab, nbases, isMainSet(ehrTable), ehrTable, keyMap,
					keyList, hrRelation, ehrAllFields, ehrFields,flag,paramBean);
			
			
			if(flag!=null&& "gz".equalsIgnoreCase(flag)) //更新薪资临时数据  dengcan
			{
				updateSalaryTmpData(paramBean,nbase,refMap);
			}
			if(!"gz".equalsIgnoreCase(flag))
		    	// 删除临时表
            {
                dropTable(impTab);
            }
			returnFlag="1";
			logError("importlog: returnflag:"+ returnFlag);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (extConn != null) {
					extConn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnFlag;
	}
	
	
	
	/**
	 * 更新薪资临时数据
	 * @param paramBean
	 */
	private void updateSalaryTmpData(LazyDynaBean paramBean,String nbase,Map refMap)
	{
		try
		{
			String username=(String)paramBean.get("username");
			String salaryid=(String)paramBean.get("salaryid");
			String gzperiod=(String)paramBean.get("gzperiod");
			String unitcode=(String)paramBean.get("unitcode");
			String deptcode=(String)paramBean.get("deptcode");
			String deptcodefield="";
			String unitcodefield="";
			String psncodefield="";
			Set keySet = refMap.keySet();
			for(Iterator it=keySet.iterator();it.hasNext();)
			{
				String key = (String)it.next();
				if("unitcode".equalsIgnoreCase(key))
				{
					unitcodefield=(String)refMap.get(key);
					
				}
				if("deptcode".equalsIgnoreCase(key)) {
                    deptcodefield=(String)refMap.get(key);
                }
				if("psncode".equalsIgnoreCase(key)) {
                    psncodefield=(String)refMap.get(key);
                }
			}
			String gz_tablename=username+"_salary_"+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update("update "+gz_tablename+" set userflag='"+username+"'");
			StringBuffer buf=new StringBuffer("");
			
			buf.append("update "+gz_tablename+" set b0110_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
			buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".b0110 and organization.codesetid='UN' )");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("update "+gz_tablename+" set e0122_o=(select a0000 from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
			buf.append(" where exists (select null from organization where organization.codeitemid="+gz_tablename+".e0122 and organization.codesetid='UM' )");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("update "+gz_tablename+" set dbid=(select dbid from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase)  )");
			buf.append(" where exists (select null from dbname where upper(dbname.pre)=upper("+gz_tablename+".nbase) )");
			dao.update(buf.toString());
			
			String year=gzperiod.substring(0,4);
			String month=gzperiod.substring(4,6);
			buf.setLength(0);
			buf.append(" update "+gz_tablename+" set sp_flag='01',sp_flag2='02' where lower(userflag)='"+username.toLowerCase()+"' ");
			buf.append(" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month);
			buf.append(" and "+unitcodefield+"='"+unitcode+"' and "+deptcodefield+"='"+deptcode+"' ");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("delete from salaryhistory where salaryid="+salaryid+" and lower(userflag)='"+username.toLowerCase()+"' ");
			buf.append(" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month);
			buf.append(" and "+unitcodefield+"='"+unitcode+"' and "+deptcodefield+"='"+deptcode+"' ");
			dao.update(buf.toString());			
			buf.setLength(0);
			buf.append("update "+gz_tablename+" set a00z2=a00z0 where lower(userflag)='"+username.toLowerCase()+"' ");
			buf.append(" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month);
			buf.append(" and "+unitcodefield+"='"+unitcode+"' and "+deptcodefield+"='"+deptcode+"' ");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("update "+gz_tablename+" set a00z3=a00z1 where lower(userflag)='"+username.toLowerCase()+"' ");
			buf.append(" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month);
			buf.append(" and "+unitcodefield+"='"+unitcode+"' and "+deptcodefield+"='"+deptcode+"' ");
			dao.update(buf.toString());
			buf.setLength(0);
			String[] arr= nbase.split(",");
			for(int i=0;i<arr.length;i++)
			{
				buf.setLength(0);
				String dbpre=arr[i];
				if(dbpre==null||"".equals(dbpre)) {
                    continue;
                }
				
				buf.append(" update "+gz_tablename+" set a0101=(select a0101 from "+dbpre+"A01 where "+gz_tablename+".a0100="+dbpre+"A01.A0100) where UPPER("+gz_tablename+".nbase)='"+dbpre.toUpperCase()+"' and exists ");
				buf.append("(select null from "+gz_tablename+" where upper(nbase)='"+dbpre.toUpperCase()+"')");
				dao.update(buf.toString());
				
				buf.setLength(0);
				buf.append(" update "+gz_tablename+" set a0000=(select a0000 from "+dbpre+"A01 where "+gz_tablename+".a0100="+dbpre+"A01.A0100) where UPPER("+gz_tablename+".nbase)='"+dbpre.toUpperCase()+"' and  exists ");
				buf.append("(select null from "+gz_tablename+" where upper(nbase)='"+dbpre.toUpperCase()+"')");
				dao.update(buf.toString());
			}
			buf.setLength(0);
			buf.append("delete from "+gz_tablename);
			buf.append(" where ");
			buf.append("  "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month);
			buf.append(" and "+unitcodefield+"='"+unitcode+"' and "+deptcodefield+"='"+deptcode+"' and "+psncodefield+" not in (select "+psncodefield+" from ");
			buf.append(this.getImportTempTable()+")");
			dao.delete(buf.toString(), new ArrayList());
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	private void deleteTemp(String tempTable,String ehrCond, ArrayList keyList, Map refMap, String ehrTable) {
		try {
				if (ehrCond != null && ehrCond.trim().length() > 0) {
				StringBuffer buff = new StringBuffer();
				buff.append("delete from ");
				buff.append(tempTable);
				if (keyList.size() > 0) {
					buff.append(" where ");
					buff.append(" EXISTS( select 1 from (select ");
					for (int i = 0; i < keyList.size(); i++) {
						if (i!=0) {
							buff.append(",");
						}
						buff.append((String)keyList.get(i));
					}
					buff.append(" from ");
					buff.append(ehrTable);
					buff.append(" where ");
					buff.append(ehrCond);
					buff.append(") hcmtable where ");
					for (int i = 0; i < keyList.size(); i++) {
						if (i!=0) {
							buff.append(" and ");
						}
						buff.append(" hcmtable.");
						buff.append(keyList.get(i));
						buff.append(" = ");
						buff.append(tempTable);
						buff.append(".");
						buff.append(keyList.get(i));
					}
					buff.append(")");
				} else {
					if(StringUtils.isNotEmpty(ehrCond)) {
						buff.append(" where ");
						buff.append(ehrCond);
					}else {
						buff.append(" where 1=2");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(buff.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 更新临时表中与薪资业务有关的数据  a00z0,a00z1,a00z2,a00z3,a0100,nbase
	 * @param paramBean
	 */
	private void impGzDataToTmp(LazyDynaBean paramBean,String tmpTable,String nbase,String hrRelation)throws Exception  
	{
		
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String gzperiod=(String)paramBean.get("gzperiod");
			String salaryid=(String)paramBean.get("salaryid");
			String username=(String)paramBean.get("username");
			
			String date=gzperiod.substring(0,4)+"-"+gzperiod.substring(4, 6)+"-01";
			String count="1";
			dao.update("update "+tmpTable+" set userflag='"+username+"',a00z0="+Sql_switcher.dateValue(date)+",a00z2="+Sql_switcher.dateValue(date)+",a00z1=1,a00z3=1,sp_flag='01',sp_flag2='02'");
			String[] arr=nbase.split(",");
			for(int i=0;i<arr.length;i++){
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				//String sql = "update "+tmpTable+" set nbase='"+arr[i]+"' where "+hrRelation+" in (select "+hrRelation+" from "+arr[i]+"a01)";
				dao.update("update "+tmpTable+" set nbase='"+arr[i]+"' where "+hrRelation+" in (select "+hrRelation+" from "+arr[i]+"a01)");
				StringBuffer sql0=new StringBuffer();
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql0.append("update "+tmpTable+" set a0100=(select a0100 from "+arr[i]+"A01 where ");
					sql0.append("  "+arr[i]+"A01."+hrRelation+"="+tmpTable+"."+hrRelation+") where UPPER("+tmpTable+".nbase)='"+arr[i].toUpperCase()+"'");
					sql0.append(" and  exists (select null from "+tmpTable+" where UPPER(nbase)='"+arr[i].toUpperCase()+"')"); 
				}
				else
				{
					sql0.append("update "+tmpTable+" set "+tmpTable+".a0100="+arr[i]+"A01.a0100"); 
					sql0.append(" from "+arr[i]+"A01 ");
					sql0.append(" where  "+arr[i]+"A01."+hrRelation+"="+tmpTable+"."+hrRelation);
				}
				dao.update(sql0.toString());
			}
			dao.delete("delete from "+tmpTable+" where nbase is null or a0100 is null ", new ArrayList());
		}
		catch (Exception e) {
			throw new Exception("将临时表信息更新到目标表时失败！" + e.getMessage());
		} 
	}
	
	
	
	
	
	 
	
	

	private void updateToTag(String src, String[] nbases, boolean isMainSet,
			String ehrTable, Map keyMap, ArrayList keyList, String hrRelation,
			ArrayList ehrAllFields, ArrayList ehrFields,String flag,LazyDynaBean paramBean) throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rsMax = null;
		if (isMainSet) { //是子集
			try {

				for (int i = 0; i < nbases.length; i++) {
					 
					// 新增记录
					String tagTable = nbases[i] + ehrTable;
					StringBuffer sql = new StringBuffer();
					
					rs=dao.search("select * from "+src+" where 1=2");
					ResultSetMetaData mt=rs.getMetaData();
					HashMap blobMap=new HashMap();
					HashMap blobDataMap=new HashMap();
					for(int n=1;n<=mt.getColumnCount();n++)
					{
						if(mt.getColumnType(n)==java.sql.Types.BLOB) {
                            blobMap.put(mt.getColumnName(n).toLowerCase(),"1");
                        }
					}
					
					
					
					// 更新记录
					sql.delete(0, sql.length());
					sql.append("update ");
					sql.append(tagTable);
					sql.append(" set ");
					for (int j = 0; j < ehrFields.size(); j++) {
					 
						
						if(blobMap.get(((String)ehrFields.get(j)).toLowerCase())!=null) {
                            continue;
                        }
						
						sql.append(ehrFields.get(j));
						sql.append("=");
						sql.append("(select ");
						sql.append(ehrFields.get(j));
						sql.append(" from ");
						sql.append(src);
						sql.append(" sr where ");
						//sql.append(" sr.a0100="+tagTable+".a0100 and ");
						for (int k = 0; k < keyList.size(); k++) {
							sql.append(" sr.");
							sql.append(keyList.get(k));
							sql.append("=");
							sql.append(tagTable);
							sql.append(".");
							sql.append(keyList.get(k));

							if (k != keyList.size() - 1) {
								sql.append(" and ");
							}
						}
						sql.append(")");
						if (j != ehrFields.size() - 1) {
							sql.append(",");
						}
					}

					sql.append(" where exists(select 1 from ");
					sql.append(src);
					sql.append(" t where ");
					for (int k = 0; k < keyList.size(); k++) {
						sql.append(" t.");
						sql.append(keyList.get(k));
						sql.append("=");
						sql.append(tagTable);
						sql.append(".");
						sql.append(keyList.get(k));

						if (k != keyList.size() - 1) {
							sql.append(" and ");
						}
					}

					sql.append(")");
					dao.update(sql.toString());
					
					if(blobMap.size()>0)
					{
						sql.delete(0, sql.length());
						sql.append("select  sr."+(String)blobMap.keySet().iterator().next());
						sql.append(","+tagTable+".a0100,"+tagTable+".i9999 from "+src+" sr,"+tagTable);
						sql.append(" where ");
						for (int k = 0; k < keyList.size(); k++) {
								sql.append(" sr.");
								sql.append(keyList.get(k));
								sql.append("=");
								sql.append(tagTable);
								sql.append(".");
								sql.append(keyList.get(k));

								if (k != keyList.size() - 1) {
									sql.append(" and ");
								}
						}
						RowSet rowSet=dao.search(sql.toString());	 
						while(rowSet.next())
						{
							blobDataMap.put(rowSet.getString("a0100")+"_"+rowSet.getInt("i9999"),rowSet.getBlob(1));
						}
						
					}
					
					
					sql.setLength(0);
					sql.append("select s.* from ( select * from ");
					sql.append(src);
					sql.append(" where " + hrRelation + " in (select "
							+ hrRelation + " from " + nbases[i]
							+ "a01)) s left join ");
					sql.append(tagTable);
					sql.append(" t on ");
					for (int j = 0; j < keyList.size(); j++) {
						sql.append("s.");
						sql.append(keyList.get(j));
						sql.append("=");
						sql.append("t.");
						sql.append(keyList.get(j));

						if (j != keyList.size() - 1) {
							sql.append(" and ");
						}
					}

					sql.append(" where ");
					for (int j = 0; j < keyList.size(); j++) {
						if (Sql_switcher.searchDbServer() == 1) {
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append("='' or ");
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append(" is null ");
						} else {
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append(" is null ");
						}

						if (j != keyList.size() - 1) {
							sql.append(" or ");
						}
					}

					rs = dao.search(sql.toString());
					sql.delete(0, sql.length());
					
//					ehrAllFields.add("a0100");
//					ehrAllFields.add("i9999");
					
					sql.append("insert into ");
					sql.append(tagTable);
					sql.append("(");
					for (int j = 0; j < ehrAllFields.size(); j++) {
						
						if(blobMap.get(((String)ehrAllFields.get(j)).toLowerCase())!=null) {
                            continue;
                        }
						
						sql.append(ehrAllFields.get(j));
						if (j != ehrAllFields.size() - 1) {
							sql.append(",");
						}
					}
					sql.append(") values(");

					for (int j = 0; j < ehrAllFields.size(); j++) {
						
						if(blobMap.get(((String)ehrAllFields.get(j)).toLowerCase())!=null) {
                            continue;
                        }
						sql.append("?");
						if (j != ehrAllFields.size() - 1) {
							sql.append(",");
						}
					}
					sql.append(")");
					while (rs.next()) {
						String a0100 = rs.getString("a0100");
						rsMax = dao.search("select max(i9999) mx from "
								+ tagTable + " where a0100='" + a0100 + "'");
						int max = 1;
						if (rsMax.next()) {
							String maxStr = rsMax.getString("mx");
							if (maxStr != null) {
								max = Integer.parseInt(maxStr) + 1;
							}
						}

						ArrayList list = new ArrayList();
						for (int j = 0; j < ehrAllFields.size(); j++) {
							if ("i9999".equalsIgnoreCase(ehrAllFields.get(j).toString())) {
								list.add(max + "");
							} else {
								
								if(blobMap.get(((String)ehrAllFields.get(j)).toLowerCase())!=null)
								{
									blobDataMap.put(a0100+"_"+max,rs.getBlob(ehrAllFields.get(j)
											.toString()));
									continue;
								}
								else {
                                    list.add(rs.getObject(ehrAllFields.get(j)
                                        .toString()));
                                }
							}
						}

						dao.insert(sql.toString(), list);
					}
					
					if(blobMap.size()>0)
					{
					 String itemid=(String)blobMap.keySet().iterator().next();
					 OracleBlobUtils blobutils=new OracleBlobUtils(this.conn); 
					 for(Iterator t=blobDataMap.keySet().iterator();t.hasNext();)
					 {
						
						 String key=(String)t.next();
						 String[] temps=key.split("_");
						 Blob blob=(Blob)blobDataMap.get(key);
						 String searchsql="select "+itemid+" from "+tagTable+" where a0100='"+temps[0]+"' and i9999="+temps[1]+"  FOR UPDATE ";
						 String strInsert="update "+tagTable+" set "+itemid+"=EMPTY_BLOB() where a0100='"+temps[0]+"' and i9999="+temps[1]+"";
						 InputStream ism = null;
						 try{							 
							 ism = blob.getBinaryStream();
							 Blob ablob=blobutils.readBlob(searchsql,strInsert.toString(),ism); 
							 String sqls = "update "+tagTable+" set "+itemid+"=? where a0100='"+temps[0]+"' and i9999="+temps[1]+"";
							 
							
							 ArrayList list = new ArrayList();
							 list.add(ablob);
							 dao.update(sqls, list);
							 
						 }finally{
							 PubFunc.closeIoResource(ism);
						 }
					 }
					}	
					
					
					
					
					/**
					 * 如果是同步人事异动数据并执行移库操作，需将移库的人员信息记录下来 
					 */
					if("rsyd".equalsIgnoreCase(flag)&&paramBean.get("to_nbase")!=null&&((String)paramBean.get("to_nbase")).trim().length()>0)
					{
						sql.setLength(0);
						sql.append("  select distinct a0100 from ");
						sql.append(src);
						sql.append(" where " + hrRelation + " in (select "
							+ hrRelation + " from " + nbases[i]
							+ "a01 )");
						rs=dao.search(sql.toString());
						while(rs.next()) {
                            operateDataSet.add(nbases[i].toLowerCase()+"_"+rs.getString("a0100"));
                        }
					 
					}
					
					
 
				
				}
				
			} catch (Exception e) {
				logError("将临时表信息更新到目标表时失败:"+e.getMessage());
				throw new Exception("将临时表信息更新到目标表时失败！" + e.getMessage());
			}
		} else {
			try {
				// 新增记录

				StringBuffer sql = new StringBuffer();
				
				HashMap map = new HashMap();
				for (int i = 0; i < ehrFields.size(); i++) {
					map.put(ehrFields.get(i).toString(), "1");
				}

				for (int i = 0; i < keyList.size(); i++) {
					map.put(keyList.get(i).toString(), "1");
				}
				
				
				if(!"A01".equalsIgnoreCase(ehrTable.trim().substring(3)))
				{
				
					sql.append("insert into ");
					sql.append(ehrTable);
					sql.append("(");
	
					Iterator its = map.entrySet().iterator();
					while (its.hasNext()) {
						Map.Entry entry = (Map.Entry) its.next();
						sql.append(entry.getKey());
	
						if (its.hasNext()) {
							sql.append(",");
						}
					}
	
					sql.append(")");
					sql.append("select ");
					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {
						sql.append("s.");
						Map.Entry entry = (Map.Entry) it.next();
						sql.append(entry.getKey());
	
						if (it.hasNext()) {
							sql.append(",");
						}
					}
					sql.append(" from ");
					
					if (hrRelation != null && hrRelation.trim().length() > 0) {
						sql.append("( select * from ");
						sql.append(src);
						sql.append(" where " + hrRelation + " in (");
						for (int i = 0; i < nbases.length; i++) {
							sql.append("select " + hrRelation + " from " + nbases[i]
									+ "a01");
							if (i != nbases.length - 1) {
								sql.append(" union ");
							}
						}
						sql.append(")) ");
					} else {
						sql.append(src);
					}
					sql.append(" s left join ");
					sql.append(ehrTable);
					sql.append(" t on ");
					for (int j = 0; j < keyList.size(); j++) {
						sql.append("s.");
						sql.append(keyList.get(j));
						sql.append("=");
						sql.append("t.");
						sql.append(keyList.get(j));
	
						if (j != keyList.size() - 1) {
							sql.append(" and ");
						}
					}
	
					sql.append(" where ");
					for (int j = 0; j < keyList.size(); j++) {
						if (Sql_switcher.searchDbServer() == 1) {
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append("='' or ");
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append(" is null ");
						} else {
							sql.append("t.");
							sql.append(keyList.get(j));
							sql.append(" is null ");
						}
	
						if (j != keyList.size() - 1) {
							sql.append(" or ");
						}
					}
					dao.update(sql.toString());
				
				}
				
				// 更新记录
				sql.delete(0, sql.length());
				sql.append("update ");
				sql.append(ehrTable);
				sql.append(" set ");
				for (int j = 0; j < ehrFields.size(); j++) {
					
					if(flag!=null&& "gz".equalsIgnoreCase(flag)) //导入薪资临时数据  dengcan
					{
						if("appprocess".equalsIgnoreCase((String)ehrFields.get(j))) {
                            continue;
                        }
					}
					
					sql.append(ehrFields.get(j));
					sql.append("=");
					sql.append("(select ");
					sql.append(ehrFields.get(j));
					sql.append(" from ");
					sql.append(src);
					sql.append(" sr where ");
					
					if(!"A01".equalsIgnoreCase(ehrTable.trim().substring(3)))
					{
						for (int k = 0; k < keyList.size(); k++) {
							sql.append(" sr.");
							sql.append(keyList.get(k));
							sql.append("=");
							sql.append(ehrTable);
							sql.append(".");
							sql.append(keyList.get(k));
	
							if (k != keyList.size() - 1) {
								sql.append(" and ");
							}
						}
					}
					else
					{
						sql.append(" sr.");
						sql.append(hrRelation);
						sql.append("=");
						sql.append(ehrTable);
						sql.append(".");
						sql.append(hrRelation);
					}
					
					
					sql.append(")");
					if (j != ehrFields.size() - 1) {
						sql.append(",");
					}
				}

				sql.append(" where exists(select 1 from ");
				sql.append(src);
				sql.append(" t where ");
				
				if(!"A01".equalsIgnoreCase(ehrTable.trim().substring(3)))
				{
					
					for (int k = 0; k < keyList.size(); k++) {
						sql.append(" t.");
						sql.append(keyList.get(k));
						sql.append("=");
						sql.append(ehrTable);
						sql.append(".");
						sql.append(keyList.get(k));
	
						if (k != keyList.size() - 1) {
							sql.append(" and ");
						}
					}
				}
				else
				{
					sql.append(" t.");
					sql.append(hrRelation);
					sql.append("=");
					sql.append(ehrTable);
					sql.append(".");
					sql.append(hrRelation);
				}
				sql.append(")");

				dao.update(sql.toString());
				
				
				
				/**
				 * 如果是同步人事异动数据并执行移库操作，需将移库的人员信息记录下来 
				 */
				if("rsyd".equalsIgnoreCase(flag)&&paramBean.get("to_nbase")!=null&&((String)paramBean.get("to_nbase")).trim().length()>0)
				{ 
					if("A01".equalsIgnoreCase(ehrTable.trim().substring(3)))
					{
						sql.setLength(0);
						sql.append("  select distinct a0100 from ");
						sql.append(src);
						sql.append(" where " + hrRelation + " in (select "
							+ hrRelation + " from " + ehrTable+" )" );
						rs=dao.search(sql.toString());
						while(rs.next()) {
                            operateDataSet.add(ehrTable.substring(0,3).toLowerCase()+"_"+rs.getString("a0100"));
                        }
					}
				}
				
				
			} catch (Exception e) {
				logError("将临时表信息更新到目标表时失败:"+e.getMessage());
				throw new Exception("将临时表信息新增到目标表时失败！" + e.getMessage());
			}
		}
	}
	
	private void updateCondToTemp(String src, String[] nbases, boolean isMainSet,
			String ehrTable, Map keyMap, ArrayList keyList, String hrRelation,
			ArrayList ehrAllFields, ArrayList ehrFields,String flag,LazyDynaBean paramBean,String tagTabCond) throws Exception {
	    
	    //没有定义目标表保护条件
	    if(tagTabCond==null|| "".equals(tagTabCond.trim())) {
            return;
        }
	    
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rsMax = null;
		if (isMainSet) { //是子集
			try {

				for (int i = 0; i < nbases.length; i++) {
					 
					// 新增记录
					String tagTable = nbases[i] + ehrTable;
					StringBuffer sql = new StringBuffer();
					StringBuffer valuesql = new StringBuffer();
					StringBuffer wheresql = new StringBuffer();
					
					sql.append("SELECT * FROM ");
					sql.append(tagTable);
					if(tagTabCond!=null&&!"".equals(tagTabCond.trim())) {
                        sql.append(" WHERE " + tagTabCond);
                    }
					rs=dao.search(sql.toString());
					ResultSetMetaData mt=rs.getMetaData();
					
					sql.setLength(0);
					sql.append("update "+src+" set ");
					//valuesql.append(" values (");
					wheresql.append(" where 1=1");
					ArrayList values = new ArrayList();
					
					int w=0;
					while(rs.next()){
						ArrayList value = new ArrayList();
						ArrayList condvalue = new ArrayList();
						for(int n=1;n<=mt.getColumnCount();n++)
						{
							if(!(mt.getColumnType(n)==java.sql.Types.BLOB)){
								if(tagTabCond.indexOf(mt.getColumnName(n).toLowerCase())!=-1){
									if(w==0){
										sql.append(mt.getColumnName(n)+"=?,");
										//valuesql.append("?,");
									}
									value.add(rs.getObject(mt.getColumnName(n)));
								}
								if(keyList.toString().toLowerCase().indexOf(mt.getColumnName(n).toLowerCase())!=-1/*||"a0100".equalsIgnoreCase(mt.getColumnName(n).toLowerCase())*/){
									if(w==0){
										wheresql.append(" and "+mt.getColumnName(n)+"=?");
									}
									condvalue.add(rs.getObject(mt.getColumnName(n)));
								}
							}
						}
						w++;
						value.addAll(condvalue);
						values.add(value);
					}
					if(w>0){
						sql.setLength(sql.length()-1);
						sql.append(wheresql);
						dao.batchUpdate(sql.toString(), values);
					}
					
			
				}
				
			} catch (Exception e) {
				throw new Exception("将目标表保护条件信息更新到临时表时失败！" + e.getMessage());
			}
		} else {
			try {
				
				//暂不考虑
			} catch (Exception e) {
				throw new Exception("将目标表保护条件信息更新到临时表时失败！" + e.getMessage());
			}
		}
	}

	private void updateTemp(String ehrTable, Map defMap, String[] nbases,
			String hrRelation,String flag,LazyDynaBean paramBean) throws Exception {
		DbWizard dbw = new DbWizard(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		try {

			// 处理默认值

			if (!defMap.isEmpty()) {
				
				Iterator it = defMap.entrySet().iterator();
				while (it.hasNext()) {
					sql.setLength(0);
					Map.Entry entry = (Map.Entry) it.next();
					
					String str = (String)entry.getValue();
					if (str.startsWith("#")&&str.endsWith("#")) {
						continue;
					}
					
					String itemid=((String)entry.getKey()).toLowerCase(); 
					FieldItem item=DataDictionary.getFieldItem(itemid);
					if(item!=null)
					{
						sql.append("update "+ehrTable+" set "+itemid+"=");
						if("A".equalsIgnoreCase(item.getItemtype()))
						{
								sql.append(" '"+(String)entry.getValue()+"' where "+itemid+" is null or "+itemid+"=''");
								dao.update(sql.toString()); 
						}
						else if("N".equalsIgnoreCase(item.getItemtype()))
						{
							sql.append((String)entry.getValue()+" where "+itemid+" is null ");
							dao.update(sql.toString()); 
						}
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{
							
							if("[当天]".equalsIgnoreCase((String)entry.getValue())){
								sql.append(Sql_switcher.sqlNow()+" where "+itemid+" is null ");
							}else{
								sql.append(Sql_switcher.dateValue((String)entry.getValue())+" where "+itemid+" is null ");
							}
							dao.update(sql.toString()); 
						} 
						  
					}
				}
				/*
				sql.append("update ");
				sql.append(ehrTable);
				sql.append(" set ");
				Iterator it = defMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					sql.append(entry.getKey());
					sql.append("=");
					sql.append(entry.getValue());
					if (it.hasNext()) {
						sql.append(",");
					}
				} 
				dao.update(sql.toString()); 
				*/
				sql.delete(0, sql.length());
				
				
			}
           if(!"gz".equalsIgnoreCase(flag)){
				String _ehrTable=(String)paramBean.get("ehrTable"); // 判断是否导入人员主集信息
				if (dbw.isExistField(ehrTable, "a0100", false)) {
					updateField("a0100", ehrTable, nbases, hrRelation);
					if("A01".equalsIgnoreCase(_ehrTable.trim().substring(3)))
					{
						impNewMainsetInfo(ehrTable,hrRelation,_ehrTable);
					}
				}
	
				if (dbw.isExistField(ehrTable, "nbase", false)) {
	
					sql.append("update ");
					sql.append(ehrTable);
					sql.append(" set nbase=( ");
					for (int i = 0; i < nbases.length; i++) {
						sql.append("select '" + nbases[i] + "' nbase from ");
						sql.append(nbases[i]);
						sql.append("a01 where ");
						sql.append(nbases[i]);
						sql.append("a01.");
						sql.append(hrRelation);
						sql.append("=");
						sql.append(ehrTable);
						sql.append(".");
						sql.append(hrRelation);
	
						if (i != nbases.length - 1) {
							sql.append(" union ");
						}
					}
					sql.append(")");
	
					dao.update(sql.toString());
				}
           }
			if (flag==null||(!"rsyd".equalsIgnoreCase(flag)&&!"gz".equalsIgnoreCase(flag)&&!"dataImport".equalsIgnoreCase(flag))) {
				
				if (dbw.isExistField(ehrTable, "b0100", false)) {
					updateField("b0100", ehrTable, nbases, hrRelation);
				}
	
				if (dbw.isExistField(ehrTable, "e0122", false)) {
					updateField("e0122", ehrTable, nbases, hrRelation);
				}
	
				if (dbw.isExistField(ehrTable, "e01a1", false)) {
					updateField("e01a1", ehrTable, nbases, hrRelation);
				}
			}

		} catch (Exception e) {
			logError("importlog："+e.getMessage());
			throw new Exception("更新临时表默认值及其它信息失败！" + e.getMessage());
		}
	}
	/**
	 * 新增人员主集信息
	 * @param tmpTable
	 * @param hrRelation
	 * @param ehrTable
	 */
	private void impNewMainsetInfo(String tmpTable,String hrRelation,String ehrTable)throws Exception 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rowSet=dao.search("select * from "+tmpTable+" where nullif(a0100,'') is null");
			while(rowSet.next())
			{
				String relation=(String)rowSet.getString(hrRelation);
				String a0100=DbNameBo.insertMainSetA0100(ehrTable,this.conn);
				dao.update("update "+tmpTable+" set a0100='"+a0100+"' where "+hrRelation+"='"+relation+"'");
				dao.update("update "+ehrTable+" set "+hrRelation+"='"+relation+"' where a0100='"+a0100+"'");
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		}
		catch (Exception e) {
			throw new Exception("主集新增人员信息出错！" + e.getMessage());
		}
	}
	

	private void updateField(String field, String ehrTable, String[] nbases,
			String hrRelation) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("update ");
		sql.append(ehrTable);
		sql.append(" set " + field + "=(");
		for (int i = 0; i < nbases.length; i++) {
			sql.append("select " + field + " from ");
			sql.append(nbases[i]);
			sql.append("a01 where ");
			sql.append(nbases[i]);
			sql.append("a01.");
			sql.append(hrRelation);
			sql.append("=");
			sql.append(ehrTable);
			sql.append(".");
			sql.append(hrRelation);

			if (i != nbases.length - 1) {
				sql.append(" union ");
			}
		}
		sql.append(")");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
		} catch (Exception e) {
			throw new Exception("更新指标失败！" + e.getMessage());
		}

	}

	 
	
	
	/**
	 * 将数据保存到临时表中
	 * 
	 * @param extTable
	 * @param ehrTable
	 * @param extConn
	 * @param ehrConn
	 * @param srcTabCond
	 * @param extFields
	 * @param refMap
	 * @param hrRelation
	 * @param extRelation
	 */
	private void syncToTemp(String extTable, String ehrTable,
			Connection extConn, Connection ehrConn, String srcTabCond,
			ArrayList extFields, Map refMap, String hrRelation,
			String extRelation, Map defMap,String dbType) throws Exception {
		RowSet extRs = null;
		try {
			ContentDAO extDao = new ContentDAO(extConn);
			ContentDAO ehrDao = new ContentDAO(ehrConn);            
			DbWizard dbw = new DbWizard(ehrConn);
			String ole_field="";
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			
			HashMap exitsMap=new HashMap();
			for (int i = 0; i < extFields.size(); i++) {
				String field = (String) extFields.get(i);
				
				sql.append(field);
				sql.append(" ");
				sql.append((String)refMap.get(field));
				
				exitsMap.put(((String)refMap.get(field)).trim().toLowerCase(),"1");
				sql.append(",");
			}

			// 需要特殊处理的字段
			Map handMap = new HashMap();
			
			Iterator it = defMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String itemid=((String)entry.getKey()).toLowerCase();
				if(exitsMap.get(itemid)!=null) {
                    continue;
                }
				exitsMap.put(itemid, "1");
				
				handMap.put(itemid.toLowerCase(), entry.getValue());
				
				FieldItem item=DataDictionary.getFieldItem(itemid);
				if(item!=null)
				{
					if("N".equalsIgnoreCase(item.getItemtype())) {
                        sql.append(entry.getValue());
                    } else if("A".equalsIgnoreCase(item.getItemtype())) {
                        sql.append(" '"+entry.getValue()+"' ");
                    } else if("D".equalsIgnoreCase(item.getItemtype())){
						if("mssql".equalsIgnoreCase(dbType)){
							if("[当天]".equalsIgnoreCase((String)entry.getValue())) {
                                sql.append("GetDate()");
                            } else{
								//sql.append("'"+entry.getValue()+"'");
								sql.append("convert(datetime,'"+entry.getValue()+"')");
							}
						}else{
							if("[当天]".equalsIgnoreCase((String)entry.getValue())) {
                                sql.append("SYSDATE");
                            } else{
								String value=(String)entry.getValue();
								if (value.length() > 10) {
									 value = "TO_DATE('" + value + "', 'YYYY-MM-DD HH24:MI:SS')"; 
								}else {
                                    value = "TO_DATE('" + value + "', 'YYYY-MM-DD')";
                                }
								sql.append(value);
							}
						}
					}
					sql.append(" ");
					sql.append(entry.getKey());
					sql.append(",");
				}
			}
			if(hrRelation != null && hrRelation.trim().length() > 0 && exitsMap.get(hrRelation.toLowerCase())==null)
			{
				sql.append(extRelation);
				sql.append(" ");
				sql.append(hrRelation);
			}
			else {
                sql.setLength(sql.length()-1);
            }
			
			sql.append(" from ");
			sql.append(extTable);
			if (srcTabCond != null && srcTabCond.trim().length() > 0) {
				sql.append(" where ");
				sql.append(srcTabCond);
			}

			// 查询外部数据
			ArrayList list = new ArrayList();
		//	System.out.println(sql.toString());
 			extRs = extDao.search(sql.toString());

			boolean isI9999=false;
			sql.delete(0, sql.length());
			sql.append("insert into ");
			sql.append(ehrTable);
			sql.append("(");
			for (int i = 1; i <= extRs.getMetaData().getColumnCount(); i++) {
				String colName = extRs.getMetaData().getColumnName(i);
				int columnType=extRs.getMetaData().getColumnType(i);
				if(columnType==java.sql.Types.BLOB)
				{
					ole_field=colName;
					continue;
				}
				else {
                    sql.append(colName);
                }
				if (i != extRs.getMetaData().getColumnCount()) {
					sql.append(",");
				}
			}
			
			if(ole_field.length()>0&&dbw.isExistField(ehrTable, "seq_num",false)) {
                sql.append(",seq_num");
            }
			
			sql.append(") values(");
			for (int i = 1; i <= extRs.getMetaData().getColumnCount(); i++) {
				int columnType=extRs.getMetaData().getColumnType(i);
				if(columnType==java.sql.Types.BLOB)
				{
					continue;
				}
				else {
                    sql.append("?");
                }
				if (i != extRs.getMetaData().getColumnCount()) {
					sql.append(",");
				}
			}
			
			if(ole_field.length()>0&&dbw.isExistField(ehrTable, "seq_num",false)) {
                sql.append(",?");
            }
			
			sql.append(")");

			
			 
			Blob blob=null;
			int seq_num=0;
			ArrayList blobList=new ArrayList();  //目前只支持一张表里只有一个Blob字段的数据导入
			while (extRs.next()) {
					seq_num++;
					ArrayList metaList = new ArrayList();
					for (int i = 1; i <= extRs.getMetaData().getColumnCount(); i++) {
						int columnType=extRs.getMetaData().getColumnType(i);
						String st = extRs.getMetaData().getColumnName(i);
						if(columnType==java.sql.Types.BLOB)
						{
							blobList.add(extRs.getBlob(i));
							continue;
						} else if (handMap.containsKey(st.toLowerCase())) {
							String defValue = (String) handMap.get(st.toLowerCase());
							if (defValue.startsWith("#") && defValue.endsWith("#")) {//id主键，来自id_factory中
								String idStr = defValue.replaceAll("#", "");
								IDGenerator idg=new IDGenerator(2,ehrConn);
								metaList.add(idg.getId(idStr));
							} else {
								metaList.add(extRs.getObject(i));
							}
							
							
						}
						else {
                            metaList.add(extRs.getObject(i));
                        }
					}
					if(ole_field.length()>0&&dbw.isExistField(ehrTable, "seq_num",false)) {
                        metaList.add(new Integer(seq_num));
                    }
					
					list.add(metaList);
					
					if (list.size() > 200) {
						ehrDao.batchInsert(sql.toString(), list);
						list.clear();
					}
			 }
			 ehrDao.batchInsert(sql.toString(), list);
			 if(ole_field.length()>0)
			 {
				 
				 OracleBlobUtils blobutils=new OracleBlobUtils(this.conn); 
				 for(int i=0;i<blobList.size();i++)
				 {
					 InputStream is = null;
					 try{
					 blob=(Blob)blobList.get(i);
					 is = blob.getBinaryStream();
					 String searchsql="select "+ole_field+" from "+ehrTable+" where seq_num="+(i+1)+"  FOR UPDATE ";
					 String strInsert="update "+ehrTable+" set "+ole_field+"=EMPTY_BLOB() where seq_num="+(i+1);
					 Blob ablob=blobutils.readBlob(searchsql,strInsert.toString(),is); 
					 
					 ContentDAO dao = new ContentDAO(this.conn);
					 ArrayList listb = new ArrayList();
					 listb.add(ablob);
					 dao.update("update "+ehrTable+" set "+ole_field+"=? where seq_num="+(i+1),listb); 
					 
					 }catch(Exception e){
						 e.printStackTrace();
					 }finally{
						 PubFunc.closeIoResource(is); 
					 }
				 }
				 
			 }
		    

		} catch (Exception e) {
			throw new Exception("将外部数据("+extTable+")导入到临时表失败！" + e.getMessage());
		} finally {
			try {
				if (extRs != null) {
					extRs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void dropTable(String tableName) {
		DbWizard dbw = new DbWizard(this.conn);
		if (dbw.isExistTable(tableName, false)) {
			dbw.dropTable(tableName);
		}
	}

	private void addClonum(String tableName, String fieldName) {
		FieldItem item = DataDictionary.getFieldItem(fieldName, "A01");
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		try {
			if (!dbw.isExistField(tableName, fieldName, false)) {
				Table table = new Table(tableName);
				if (item != null) {
					table.addField(item);
				} else {
					Field field = new Field(fieldName);
					field.setDatatype(DataType.STRING_NAME);
					field.setLength(50);

					table.addField(field);
				}
				dbw.addColumns(table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dbmodel.reloadTableModel(tableName);

	}

	/**
	 * 是否是子集
	 * 
	 * @param setId
	 * @return
	 */
	public boolean isMainSet(String setId) {
		RowSet rs = null;

		boolean flag = false;
		try {

			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select fieldsetid from fieldset where fieldsetid='");
			sql.append(setId.toUpperCase());
			sql.append("'");
			
			if("a00".equalsIgnoreCase(setId)) {
                flag=true;
            }

			rs = dao.search(sql.toString());

			if (rs.next()) {
				String str = rs.getString("fieldsetid");
				if (str.toUpperCase().startsWith("A")
						&& !"a01".equalsIgnoreCase(str)) {
					flag = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return flag;

	}

	/**
	 * 根据原表创建临时表
	 * 
	 * @param srcTable
	 * @param tarTable
	 * @param dbType
	 * @throws Exception
	 */
	private void createTemp(String srcTable, String tarTable, String dbType,String flag)
			throws Exception { 
		DbWizard dbw = new DbWizard(this.conn);
		DBMetaModel dbmodel = new DBMetaModel(this.conn);
		try {
			if (dbw.isExistTable(tarTable, false)) {
				dbw.dropTable(tarTable);
			}

			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			if ("mssql".equalsIgnoreCase(dbType)) {
				sql.append("select * into ");
				sql.append(tarTable);
				sql.append(" from ");
				sql.append(srcTable);
				sql.append(" where 1=2");
			} else {
				sql.append("create table ");
				sql.append(tarTable);
				sql.append(" as ( select * from ");
				sql.append(srcTable);
				sql.append(" where 1=2)");
			}

			dao.update(sql.toString());
			dbw.dropPrimaryKey(tarTable);
			if ("mssql".equalsIgnoreCase(dbType)) 
			{
				if (dbw.isExistField(tarTable, "a0100",false)) {
					dao.update("ALTER TABLE " + tarTable
							+ " ALTER COLUMN a0100 varchar(8) null");
				}
				if (dbw.isExistField(tarTable, "i9999",false)) {
					dao.update("ALTER TABLE " + tarTable
							+ " ALTER COLUMN i9999 int null");
				}
				if("gz".equalsIgnoreCase(flag)){
					if (dbw.isExistField(tarTable, "nbase",false)) {
						dao.update("ALTER TABLE " + tarTable+ " ALTER COLUMN nbase varchar(3) null");
					}else{
						dao.update("ALTER TABLE " + tarTable+ " add nbase varchar(3) null");
					}
					if (dbw.isExistField(tarTable, "a00z0",false)) {
						dao.update("ALTER TABLE " + tarTable+ " ALTER COLUMN a00z0 datetime null");
					}else{
						dao.update("ALTER TABLE " + tarTable+ " add  a00z0 datetime null");
					}
					if (dbw.isExistField(tarTable, "a00z1",false)) {
						dao.update("ALTER TABLE " + tarTable+ " ALTER COLUMN a00z1 int null ");
					}else{
						dao.update("ALTER TABLE " + tarTable+ " add a00z1 int null ");
					}
				}
			}
			else
			{
				try
				{
					if (dbw.isExistField(tarTable, "a0100",false)) {
						dao.update("ALTER table "+tarTable+" drop column a0100");
						
						dao.update("ALTER TABLE " + tarTable
								+ " add  a0100 varchar(8) null");
					}
					if (dbw.isExistField(tarTable, "i9999",false)) {
						
						dao.update("ALTER table "+tarTable+" drop column i9999");
						
						dao.update("ALTER TABLE " + tarTable
								+ " add  i9999 int null");
						
					}
					if("gz".equalsIgnoreCase(flag)){
						if (dbw.isExistField(tarTable, "nbase",false)) {
							dao.update("ALTER table "+tarTable+" drop column nbase");
							dao.update("ALTER TABLE " + tarTable+ " add nbase varchar(3) null");
						}
						if (dbw.isExistField(tarTable, "a00z0",false)) {
							dao.update("ALTER table "+tarTable+" drop column a00z0");
							dao.update("ALTER TABLE " + tarTable+ " add a00z0 date null");
						}
						if (dbw.isExistField(tarTable, "a00z1",false)) {
							dao.update("ALTER table "+tarTable+" drop column a00z1");
							dao.update("ALTER TABLE " + tarTable+ " add a00z1 int null ");
						}
					}
				}
				catch(Exception ee)
				{
					
				}
				
			}
			
			RowSet rowSet=dao.search("select * from "+tarTable+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int n=1;n<=mt.getColumnCount();n++)
			{
				if(mt.getColumnType(n)==java.sql.Types.BLOB)
				{
					dao.update("ALTER TABLE " + tarTable
							+ " add  seq_num int null");
					break;
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
			
			dbmodel.reloadTableModel(tarTable);

		} catch (Exception e) {
			throw new Exception("创建临时表失败("+srcTable+")！" + e.getMessage());
		}

	}
	public String getImportTempTable() {
		return importTempTable;
	}

	public void setImportTempTable(String importTempTable) {
		this.importTempTable = importTempTable;
	}

	/**
	 * 导入方案列表按id排序
	 * @param schemaList
	 * @author zxj
	 */
	public void orderById(ArrayList schemaList)
    {
	    try
	    {
	    	
	    	Map map = new TreeMap();
	    	for(int i = 0; i < schemaList.size(); i++) {
	    		HashMap map1 = (HashMap)schemaList.get(i);
	    		 Integer id = Integer.valueOf(((String)map1.get("id")));
	    		map.put(id, map1);
	    	}
	    	
	    	Iterator it = map.entrySet().iterator();
	    	schemaList.clear();
	    	while (it.hasNext()) {
	    		Map.Entry entry = (Map.Entry)it.next();
	    		schemaList.add(entry.getValue());
	    	}
	    	
	    	
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	
    }
	
	/**
	 * 得到方案列表中最大值id
	 * @param schemaList
	 * @author zxj
	 */
	public String getMaxId(ArrayList schemaList)
    {
        String maxId = "";
        
        try
        {
            if (null != schemaList && 0 < schemaList.size())
            {
                HashMap map = (HashMap)schemaList.get(schemaList.size() - 1);
                maxId = (String)map.get("id");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return maxId;
    }
	
	/**
	 * 对方案xml中params的子节点按id强制排序,凡涉及到方案顺序的，都应调用此方法排序
	 * @param nodeList
	 * @author zxj
	 */
	public void orderParamNodeById(ConstantXml constantXml)
    {
        if (null == constantXml) {
            return;
        }
        
        List nodeList = constantXml.getAllChildren("/params");
        
        ArrayList ids = new ArrayList();
        for (int i = 0; i < nodeList.size(); i++)
        {
            Element el = (Element) nodeList.get(i);
            Integer id = Integer.valueOf(el.getAttributeValue("id"));
            ids.add(id);            
        }
        Collections.sort(ids);
        
        //按顺序移除填加一遍，打到排序的目的
        Element root = constantXml.getRootNode();
        for(int i = 0; i < ids.size(); i++)
        {
            String strId = ((Integer)ids.get(i)).toString();
            
            Element el = constantXml.getElement("/params/param[@id='" + strId +"']");
            if (null == el) {
                continue;
            }
            
            root.removeContent(el);
            root.addContent(el);
        }
    }
}
