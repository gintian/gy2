package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.sql.RowSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个描述字段信息的数据结构
 * 
 * @author lzy
 * 
 */
class FieldDESC {
	// 字段名称
	public String fieldName;

	// 标识字段的值在sql语句中是否需要加引号
	public boolean typeFlg;

	// 字段值
	public String fieldValue;
}

public class ParseXmlTrans extends IBusiness {

	private HashMap hashMap = null; //封装了用户选中的报表ID和接收方式 键值对应
	private Document doc = null;
	private String flg = "";

	/*
	// 用来记录tsortid原值和改变后的值
	private HashMap tsortidMap = new HashMap();
	// 用来记录tparamid原值和改变后的值
	private HashMap tparamidMap = new HashMap();
	// 用来记录tabid原值和改变后的值
	private HashMap tabidMap = new HashMap();
*/
	
	public void execute() throws GeneralException {
		/*
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String tabids = (String)hm.get("check");
		String operate = (String)hm.get("operate");
		
		System.out.println("tabids=" + tabids + " operate=" + operate);
		*/
		
		// 用来标识每条记录是追加还是覆盖的哈希表
		hashMap = (HashMap) this.getFormHM().get("map");
		
		
		//System.out.println(hashMap.size());
		java.util.Iterator it = hashMap.entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String keys = (String) entry.getKey();
			String values = (String)entry.getValue();
			
			//System.out.println(keys +" "+values);
			
			String [] k = keys.split(",");
			String [] v = values.split(",");
			for(int i=0; i< k.length ; i++){
				hashMap.put(k[i],v[i]);
				//System.out.println(k[i] + "   " + v[i]);
			}
			
		}
		
		//上传表样文件对象
		FormFile form_file = (FormFile) getFormHM().get("file");
		
		//selectTabid = (String[]) getFormHM().get("selectTabid");
		try {
			//生成DOM对象
			byte[] data = form_file.getFileData();
			String source = new String(data);
			StringReader sr = new StringReader(source);
			InputSource isource = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = null;
			doc = builder.parse(isource);

			// 解析tsort结点(修改或创建表类表记录信息)
			processTsortOrTparam("tsort", "tsortid");
			
			// 解析tparam结点
			processTsortOrTparam("tparam", "paramid");
			//processTsortOrTparam("tparam", "paramename");
			
			processTab();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 该方法用来解析xml文件中的<tsort>或<tparam>结点，通过tableName标识
	 * @param tableName    表名
	 * @param key          主键名
	 */
	public void processTsortOrTparam(String tableName, String key) {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		NodeList tablist = doc.getElementsByTagName(tableName);
		Element element = (Element) tablist.item(0);
		String columnsStr = element.getAttribute("columns"); //表的字段信息
		
	//	System.out.println("字段信息=" + columnsStr);
		
		String[] columnsArray = columnsStr.split("γ");//字段集合
		
		NodeList recordList = element.getElementsByTagName("record");//字段数据信息
		
		for (int i = 0; i < recordList.getLength(); i++) {
			//字段值字符串
			String valueStr = recordList.item(i).getFirstChild().getNodeValue();
			//System.out.println("字段值信息=" + valueStr);
			
			String[] valueArray = valueStr.split("γ");//一条记录数据集合
			
			if ("tsort".equalsIgnoreCase(tableName)) {//表类处理(有则覆盖,无则添加)

				int num = update(columnsArray, valueArray, tableName, dao,
						" where " + key + " = " + valueArray[0]);
				
			//	System.out.println(num);
				//tsortidMap.put(valueArray[0], valueArray[0]);
				if (num <= 0) {
					//重新设置主键
					//valueArray[0] = getId("tsort", "tsortid", valueArray[0]);
					//System.out.println(valueArray[0]);
					insert(columnsArray, valueArray, tableName, dao);
				}
			}else if ("tparam".equalsIgnoreCase(tableName)) {//表参数处理(有则覆盖,无则添加)
				String pid = this.isParam(dao,valueArray[5]);
				if(pid== null){
					valueArray[4] = this.getId("tparam" , "paramid");
					insert(columnsArray, valueArray, tableName, dao);
				}else{
					valueArray[4] = pid;
					update(columnsArray, valueArray, tableName, dao,
							" where " + key + " = " + valueArray[4] );
				}
			}

		}
	}

	/**
	 * 判断表参数是否存在,
	 * @param dao
	 * @param paramename
	 * @return 返回存在的参数ID
	 */
	public String isParam(ContentDAO dao,String paramename){
		String pid = null; 
		if(paramename == null || "".equals(paramename)){
			return pid;
		}
		String sql="select paramid from tparam where paramename='" + paramename +"'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				pid = String.valueOf(this.frowset.getInt("paramid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pid;
	}

	/**
	 * 处理tab的子结点 报表外框信息（tgrid3） 单元格信息表（tgrid2） 计算公式（tformula） 行校验公式（rowchk）
	 * 列校验公式（colchk） 表间校验公式（tcheck） 报表标题信息（tpage
	 * @param tabElement 父节点tab
	 * @param tableName  表名
	 */
	public void processTabSub(Element tabElement, String tableName) {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		NodeList nl = tabElement.getElementsByTagName(tableName);	
		Element element = (Element) nl.item(0);
		
		//字段信息
		String columnsStr = element.getAttribute("columns");
		String[] columnsArray = columnsStr.split("γ");
		//System.out.println(tableName + "表字段信息:" + columnsStr);
		
		//数据集合
		NodeList recordList = element.getElementsByTagName("record");
		
		for (int i = 0; i < recordList.getLength(); i++) {
			//一条数据值
			String valueStr = recordList.item(i).getFirstChild().getNodeValue();
			String[] valueArray = valueStr.split("γ");
			//System.out.println("数据值=" + valueStr);
			
			if ("append".equalsIgnoreCase(flg)) { //添加
				if ("tgrid2".equalsIgnoreCase(tableName)
						|| "tgrid3".equalsIgnoreCase(tableName)
						|| "tpage".equalsIgnoreCase(tableName)
						|| "tcheck".equalsIgnoreCase(tableName)
						|| "colchk".equalsIgnoreCase(tableName)
						|| "rowchk".equalsIgnoreCase(tableName)){
					
					//新的报表ID
					//valueArray[0] = (String) tabidMap.get(valueArray[0]);
				
				}else if ("tformula".equalsIgnoreCase(tableName)) {
					//新的报表ID
					//valueArray[5] = (String) tabidMap.get(valueArray[5]);
					//新增计算公式ID
					valueArray[0] = this.getId("tformula", "expid",valueArray[0]);
				}
				
				insert(columnsArray, valueArray, tableName, dao);
				
			} else { //覆盖
				String sql="";
				if("tformula".equalsIgnoreCase(tableName)){
					sql =" delete from " + tableName + " where tabid = " + valueArray[valueArray.length-1] ;
				}else{
				sql =" delete from " + tableName + " where tabid = " + valueArray[0] ;
				}
				if(i==0)
				{
			    	delete(dao,sql);
				}
				if ("tformula".equalsIgnoreCase(tableName)) {
					//新增计算公式ID
					valueArray[0] = this.getId("tformula", "expid");
				}
				insert(columnsArray, valueArray, tableName, dao);
				/*
				if (tableName.equalsIgnoreCase("tgrid2"))
					update(columnsArray, valueArray, tableName, dao,
							"where tabid = " + valueArray[0] + " and gridno="
									+ valueArray[1]);
				if (tableName.equalsIgnoreCase("tgrid3"))
					update(columnsArray, valueArray, tableName, dao,
							" where tabid" + " = " + valueArray[0]);
				if (tableName.equalsIgnoreCase("tformula")) {
					update(columnsArray, valueArray, tableName, dao,
							" where tabid" + " = " + valueArray[5]
									+ " and expid = " + valueArray[0]);
				}
				if (tableName.equalsIgnoreCase("rowchk")
						|| tableName.equalsIgnoreCase("colchk")
						|| tableName.equalsIgnoreCase("tcheck")) {
					update(columnsArray, valueArray, tableName, dao,
							" where tabid" + " = " + valueArray[0]
									+ " and condid = " + valueArray[1]);
				}*/
			}
		}
	}

	/**
	 * 处理tab结点
	 * 
	 * @param table
	 *            表名
	 * @param fieldDesc
	 *            字段描述
	 */
	public void processTab() {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		NodeList tablist = doc.getElementsByTagName("tab");
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tablist.getLength(); i++) {

			Element tabElement = (Element) tablist.item(i);
			NamedNodeMap nodeMap = tabElement.getAttributes();
			
			String[] fieldArray = new String[nodeMap.getLength()];//字段信息
			String[] valueArray = new String[nodeMap.getLength()];//值信息
			//tname 表操作
			for (int j = 0; j < nodeMap.getLength(); j++) {
				fieldArray[j] = nodeMap.item(j).getNodeName();
				valueArray[j] = nodeMap.item(j).getNodeValue();
			}
			
			//获得样式接收方式 (添加/覆盖) valueArray[16] tabid
			String myFlg = (String) hashMap.get(valueArray[16]);
			
			//System.out.println("myFlg=" + myFlg);
			
			if (myFlg == null || "".equals(myFlg)) {
				continue;
			}
			
			flg = myFlg;
			if ("append".equalsIgnoreCase(flg)) { //添加
				// 获得tabid的新值
				//valueArray[16] = getId("tname", "tabid", valueArray[16]);
				// 将sortid转换成新值
				//valueArray[18] = (String) tsortidMap.get(valueArray[18]);
				insert(fieldArray, valueArray, "tname", dao);
			} else {  //覆盖
				sb.setLength(0);
				update(fieldArray, valueArray, "tname", dao, " where tabid="
						+ valueArray[16]);
			}
			
			processTabSub(tabElement, "tgrid2");
			processTabSub(tabElement, "tgrid3");
			processTabSub(tabElement, "tformula");
			processTabSub(tabElement, "tcheck");
			processTabSub(tabElement, "rowchk");
			processTabSub(tabElement, "colchk");
			processTabSub(tabElement, "tpage");
			
		}
	}
	
	
	/**
	 * 
	 * @param tableName
	 *            表名
	 * @param fieldName
	 *            主键名
	 * @return
	 */
	public String getId(String tableName, String fieldName) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String str = "";
		try {
			String sql="select max(" + fieldName + ") from "+ tableName;
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				str = String.valueOf(rs.getInt(1) + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return str;
	}


	/**
	 * 
	 * @param tableName
	 *            表名
	 * @param fieldName
	 *            主键名
	 * @param oldId
	 *            在xml中原来的值
	 * @return
	 */
	public String getId(String tableName, String fieldName, String oldId) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String str = "";
		try {
			String sql="select max(" + fieldName + ") from "+ tableName;
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				str = String.valueOf(rs.getInt(1) + 1);
				/*
				if (tableName.equalsIgnoreCase("tsort")){
					tsortidMap.put(oldId, str);
				}else if (tableName.equalsIgnoreCase("tparam")){
					tparamidMap.put(oldId, str);
				}else if (tableName.equalsIgnoreCase("tname")) {
					tabidMap.put(oldId, str);
				}
				
				*/
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 用来向数据insert数据
	 * 
	 * @param fieldArray
	 *            字段列表
	 * @param valueArray
	 *            值列表
	 * @param tableName
	 *            表名
	 * @param dao
	 *            数据存取对象
	 */
	public void insert(String[] fieldArray, String[] valueArray,
			String tableName, ContentDAO dao) {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into " + tableName);
		StringBuffer sbFieldName = new StringBuffer("");
		StringBuffer sbValue = new StringBuffer("");
		boolean flg = false;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		try {
			rs1 = dao.search("select * from " + tableName
					+ " where 1 > 2");
			rsmd = rs1.getMetaData();
			String tabid="";
			
			for (int i = 0; i < fieldArray.length; i++) {
				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					if (!rsmd.getColumnName(j).equalsIgnoreCase(fieldArray[i]))
						continue;
					//System.out.println("rrrrr=" + valueArray[i]);
					if(valueArray[i]!=null)
					{
						valueArray[i]=replaceCodeToSign(valueArray[i]);
					}
					
					if("tabid".equalsIgnoreCase(fieldArray[i]))
						tabid=valueArray[i];
					
					
					switch (rsmd.getColumnType(j)) {
					
						case Types.TINYINT:
						case Types.SMALLINT:
						case Types.INTEGER:
						case Types.BIGINT:
						case Types.FLOAT:
						case Types.DOUBLE:
						case Types.DECIMAL:
						case Types.NUMERIC:
						case Types.REAL: {
							if (flg) {
								sbFieldName.append(",");
								sbValue.append(",");
							} else {
								flg = true;
							}
							sbFieldName.append(fieldArray[i]);
							if(valueArray[i] == null || "".equals(valueArray[i])
									|| "null".equals(valueArray[i].trim())){
								sbValue.append(" null ");
							}else{
								sbValue.append(valueArray[i]);
							}
							
							break;
						}
						case Types.CLOB:{
							if (flg) {
								sbFieldName.append(",");
								sbValue.append(",");
							} else {
								flg = true;
							}
							sbFieldName.append(fieldArray[i]);
							if ( valueArray[i]==null || "".equals(valueArray[i]) || "null".equalsIgnoreCase(valueArray[i].trim())) {
								sbValue.append("' '");
							} else {
								sbValue.append("'" + valueArray[i] + "'");
							}
							break;
						}
						case Types.DATE:
						case Types.TIME:
						case Types.TIMESTAMP:
						case Types.CHAR:
						case Types.VARCHAR:
						case Types.LONGVARCHAR: {
							if (flg) {
								sbFieldName.append(",");
								sbValue.append(",");
							} else {
								flg = true;
							}
	
							sbFieldName.append(fieldArray[i]);
					
							if ( valueArray[i]==null || "".equals(valueArray[i]) || "null".equalsIgnoreCase(valueArray[i].trim())) {
								sbValue.append("' '");
							} else {
								sbValue.append("'" + valueArray[i] + "'");
							}
	
							break;
						}
						default:
							break;
					}
				}
			}
		//	System.out.println("---" + sb.toString() + "(" + sbFieldName
				//	+ ") values (" + sbValue + ")");
			dao.update(sb.toString() + "(" + sbFieldName + ") values ("
					+ sbValue + ")");
			String unitcode="";
			RowSet recset=dao.search("select unitcode from operuser where userName='"+this.getUserView().getUserName()+"'");
			if(recset.next())
				unitcode=recset.getString(1);
			RecordVo vo = new RecordVo("treport_ctrl");
			vo.setString("unitcode", unitcode);
			vo.setInt("tabid",Integer.parseInt(tabid));
			vo = dao.findByPrimaryKey(vo);
			vo.setInt("status", 0);
			dao.updateValueObject(vo);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 删除数据
	 * @param dao
	 * @param sql
	 */
	public void delete(ContentDAO dao ,String sql){
		if(sql == null || "".equals(sql)){
			return;
		}
	//	System.out.println(sql);
		try {
			dao.delete(sql,new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用来向数据update数据
	 * 
	 * @param fieldArray     字段列表
	 * @param valueArray     值列表
	 * @param tableName      表名
	 * @param dao   数据存取对象
	 */
	public int update(String[] fieldArray, String[] valueArray,
			String tableName, ContentDAO dao, String whereStr) {
		int num = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("update " + tableName + " set ");
		boolean flg = false;
		Statement stmt = null;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		try {
			stmt = this.getFrameconn().createStatement();
			rs1 = stmt.executeQuery("select * from " + tableName
					+ " where 1 > 2");
			rsmd = rs1.getMetaData();
			for (int i = 0; i < fieldArray.length; i++) {
				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					if (!rsmd.getColumnName(j).equalsIgnoreCase(fieldArray[i]))
						continue;
					
					/*if(tableName.equalsIgnoreCase("tformula") && fieldArray[i].equalsIgnoreCase("expid") ){
						continue;
					}
					*/
				//	System.out.println("数据=" + valueArray[i]);
					if(valueArray[i]!=null)
					{
						valueArray[i]=replaceCodeToSign(valueArray[i]);
					}
					switch (rsmd.getColumnType(j)) {
						case Types.TINYINT:
						case Types.SMALLINT:
						case Types.INTEGER:
						case Types.BIGINT:
						case Types.FLOAT:
						case Types.DOUBLE:
						case Types.DECIMAL:
						case Types.NUMERIC:
						case Types.REAL: {
							if (flg) {
								sb.append(",");
							} else {
								flg = true;
							}
							if(valueArray[i] == null || "".equals(valueArray[i])
									|| "".equals(valueArray[i].trim())){
								sb.append(fieldArray[i] + "= null ");	
							}else{
								sb.append(fieldArray[i] + "=" + valueArray[i]);	
							}
													
							break;
						}
						case Types.DATE:
						case Types.TIME:
						case Types.TIMESTAMP:
						case Types.CHAR:
						case Types.CLOB:{
							if (flg) {
								sb.append(",");
							} else {
								flg = true;
							}
							if ( valueArray[i]==null || "".equals(valueArray[i])
									|| "null".equalsIgnoreCase(valueArray[i].trim())) {
								sb.append(fieldArray[i] + "=' '");
							} else {
								sb.append(fieldArray[i] + "='" + valueArray[i]
										+ "'");
							}
							break;
						}	
						case Types.VARCHAR:
						case Types.LONGVARCHAR: {
							if (flg) {
								sb.append(",");
							} else {
								flg = true;
							}
							if ( valueArray[i]==null || "".equals(valueArray[i])
									|| "null".equalsIgnoreCase(valueArray[i].trim())) {
								sb.append(fieldArray[i] + "=' '");
							} else {
								sb.append(fieldArray[i] + "='" + valueArray[i]
										+ "'");
							}
							break;
						}
						default:
							break;
					}
				}
			}
			//System.out.println(sb.toString() + " " + whereStr);
			num = dao.update(sb.toString() + " " + whereStr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return num;
	}

	
	
	public String replaceCodeToSign(String str)
	{
		String tempStr=str;
		tempStr=tempStr.replaceAll("&lt;", "<");
		tempStr=tempStr.replaceAll("&gt;", ">");
		return tempStr;
	}
	
	
	
	public static boolean findTabid(String[] targetArray, String findValue) {
		for (int i = 0; i < targetArray.length; i++) {
			if (targetArray[i].equalsIgnoreCase(findValue)) {
				return true;
			}
		}
		return false;
	}

}
