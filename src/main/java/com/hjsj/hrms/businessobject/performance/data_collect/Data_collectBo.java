package com.hjsj.hrms.businessobject.performance.data_collect;
/**
 * @author xuchangshun 
 * */

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Data_collectBo {
	private String xml;//数据库中的xml内容
	private Document doc;//用于处理xml 并用来获得xml中的内容
	public Data_collectBo(){
		
	}
	
	/*
	 * 获得审计状态列表
	 * fieldsetid :要统计的那个年月变化子集的id
	 * conn :数据库的连接
	 */
	public ArrayList getAudit(String fieldsetid,Connection conn){
		try{
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs=null;
			String sql="select * from fieldSet where changeFlag in (1,2) and fieldsetid='"+fieldsetid+"'";
			rs=dao.search(sql);
			if(!rs.next()){
				return null;
			}
			ArrayList auditList = new ArrayList();
			sql="select itemid,itemdesc from fielditem where itemtype='A'and codesetid='23' and fieldsetid='"+fieldsetid+"' and useflag = '1'";
			rs=dao.search(sql);
			while(rs.next()){
				CommonData audit = new CommonData(rs.getString(1),rs.getString(2));
				auditList.add(audit);
			}
			if(rs!=null){
				rs.close();
			}
			return auditList;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * 获取人员信息库列表
	 * dbid[] 已经被选中的人员库的id数组
	 */
	public ArrayList getDbList(String dbid[],Connection conn){
		try{
			ArrayList dbList = new ArrayList();
			
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			String sql ="select dbid,dbname,pre from dbname order by dbid";
			
				rs=dao.search(sql);
				while(rs.next()){
					HashMap tMap = new HashMap();
					tMap.put("dbname", rs.getString(2));
					tMap.put("dbid",rs.getString(1));
					tMap.put("isSelect", "0");
					if((dbid.length>0)){
						for(int i=0;i<dbid.length;i++){
							if(rs.getString(3).equals(dbid[i])){
								tMap.put("isSelect", "1");
							}
						}
					}
					dbList.add(tMap);
				}
				return dbList;
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return null;
	}
	/*
	 * 获得xml，从数据库中获得str_value
	 * str_value:存放参数设置中的数据
	 * */
	private String initXML(Connection conn) {
		StringBuffer temp_xml = new StringBuffer();
		String ss=null;//ss 临时处理数据用  没有特殊意义
		String sql ="select str_value from Constant where constant='DATA_COLLECT_SCOPE'";
		ContentDAO dao = new ContentDAO(conn);
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				ss=rs.getString(1);
			}
			if(rs!=null){
				rs.close();
			}
			if(ss==null||"".equals(ss)){//为了解决当数据库中xml数据为空的时候 StringReader会出现读取错误
				ss="<?xml version=\"1.0\" encoding=\"GB2312\"?><Params></Params>";
			}
			temp_xml.append(ss);
			xml = temp_xml.toString();
			doc = PubFunc.generateDom(xml);
			return xml;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	/*
	 * 获得xml各节点属性
	 * */
	public ArrayList getXmlValue(Connection conn){
		initXML(conn);
		Element root = doc.getRootElement();
		List scopeList=root.getChildren("scope");
		ArrayList ValueList = new ArrayList();
		for(int i=0;i<scopeList.size();i++){
			HashMap tempMap = new HashMap();
			Element element = (Element) scopeList.get(i);
			String cexpr = element.getTextTrim();
			tempMap.put("cexpr", cexpr);
			tempMap.put("dbid",element.getAttributeValue("cbase"));
			tempMap.put("state_id", element.getAttributeValue("state_id"));
			tempMap.put("set_id",element.getAttributeValue("set_id"));
			tempMap.put("flag",element.getAttributeValue("flag"));
			ValueList.add(tempMap);
		}
		return ValueList;
	}
}
