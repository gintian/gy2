/**
 * 
 */
package com.hjsj.hrms.businessobject.general.cadrerm;

import org.jdom.Document;

import java.io.InputStream;
import java.sql.Connection;


/**
 * 中组部干部任免表生成
 * 1.文本形式
 * 2.word形式
 * @author Owner
 *
 */
public class CadreAppointAndRemove {
	
	private Connection conn;
	private String dbPre;
	private String userName;
	private String url;
	
	/**
	 * Word输出构造器
	 */
	public CadreAppointAndRemove(Connection conn ,String dbPre , String userName,String url) {
		this.conn = conn;
		this.dbPre = dbPre;
		this.userName = userName;
		this.url = url;
	}

	/**
	 * ZIP包下载输出构造器
	 */
	public CadreAppointAndRemove(Connection conn ,String dbPre , String userName) {
		this.conn = conn;
		this.dbPre = dbPre;
		this.userName = userName;
	}
	
	public String getCadreName(String cadreId){
		String cadreName = "";
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre);
		cadreName = citi.getCadreName(cadreId);
		return cadreName;
	}
	
	/**
	 * 干部图片流
	 * @param cadreId
	 * @return
	 */
	public InputStream cadreImageStream(String cadreId){
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre);
		return citi.getCadreImageStream(cadreId);
	}
	
	/**
	 * 干部图片
	 * @param cadreId
	 * @return
	 */
	public String cadreImageStr(String cadreId){
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre);
		return citi.getCadreImageStr(cadreId);
	}
	
	/**
	 * 干部Lrm文件
	 * @param cadreId
	 * @return
	 */
	public String cadreInfoTableToLrm (String cadreId){
		String resultStr = "";
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre);
		CadreInfoTable cit = citi.initCadreTableInfo(cadreId);
		resultStr = cit.toTxt(userName);
		return resultStr;
	}
	
	/**
	 * 干部word文件
	 * @param cadreId
	 * @return
	 */
	public void cadreInfoTableToWord (String cadreId,Document doc){
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre,url);
		CadreInfoTable cit = citi.initCadreTableInfo(cadreId);
		cit.toWord(doc ,userName);
	}
	
	/**
	 * 干部word文件
	 * @param cadreId
	 * @return
	 */
	public String cadreInfoTableToWord (String cadreId){
		String resultStr = "";
		CadreInfoTableInit citi = new CadreInfoTableInit(conn,dbPre,url);
		CadreInfoTable cit = citi.initCadreTableInfo(cadreId);
		resultStr = cit.toWord(userName);
		return resultStr;
	}
}
