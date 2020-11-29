package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <p>Title:报表指标分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 14, 2006:9:54:26 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportFieldAnalyse {
	
	private Connection conn; //DB连接
	private HashSet set ;    //报表指标集合
	
	
	/**
	 * 指标分析
	 * @param conn  DB连接
	 * @param tabid 报表ID
	 * @param set   报表指标分析结果集合
	 */
	public ReportFieldAnalyse(Connection conn ,HashSet set){
		this.conn = conn;
		this.set = set;
	}
	

	public void fieldAnalyse(String tabid ) throws GeneralException{
		
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("select cfactor from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(tabid));
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				String temp = rs.getString("cfactor");//条件因子
				if(temp != null && !"".equals(temp)){
					String [] fielditem = temp.split("`");//切割字符串
					for(int i = 0 ; i< fielditem.length ; i++){
						String temp1 = this.getFieldItem(fielditem[i]);//获得指标
							if(temp1 != null){//不是NULL
								this.set.add(temp1);
							}	
					}//end for 
				}
			}//end while 
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 获得指标的描述信息
	 * @param set  指标集
	 * @return
	 * @throws GeneralException 
	 */
	public  String getReportFieldAnalyseValues() throws GeneralException{
		StringBuffer sb = new StringBuffer();
		Iterator iterator = this.set.iterator();
		while(iterator.hasNext()){
			String temp = (String)iterator.next();
			if(temp == null || "".equals(temp)){
			}else{
				//封装指标对象
				FieldItem fieldItem = DataDictionary.getFieldItem(temp);
				//null则表明此指标为构库	
				if(fieldItem ==null){//未构库的指标
					if(temp.startsWith("yk")){ //临时变量
						String chz = this.isTempVariable(temp);
						if(chz == null || "".equals(chz)){
						}else{
							sb.append("<tr><td width='25%'><p align='center'>");
							sb.append(ResourceFactory.getProperty("label.gz.variable")+".");
							sb.append(temp);//指标项
							sb.append("</p></td>");
							sb.append("<td width='25%'><p align='center'>");
							sb.append("&nbsp;");
							sb.append("</p></td>");
							sb.append("<td width='25%'><p align='center'>");
							sb.append("√");
							sb.append("</p></td>");
							sb.append("<td width='25%'><p align='center'>");
							sb.append(chz);
							sb.append("</p></td></tr>");
						}
					}else{
						sb.append("<tr><td width='25%'><p align='center'>");
						sb.append("???.");
						sb.append(temp.toUpperCase());//指标项
						sb.append("</p></td>");
						sb.append("<td width='25%'><p align='center'>");
						sb.append("&nbsp;");
						sb.append("</p></td>");
						sb.append("<td width='25%'><p align='center'>");
						sb.append("×");
						sb.append("</p></td>");
						sb.append("<td width='25%'><p align='center'>");
						sb.append("&nbsp;");
						sb.append("</p></td></tr>");
					}
					
					
				}else{
					sb.append("<tr><td width='25%'><p align='center'>");
					sb.append(fieldItem.getFieldsetid());//指标集
					sb.append(".");
					sb.append(fieldItem.getItemid().toUpperCase());//指标项
					sb.append("</p></td>");
					sb.append("<td width='25%'><p align='center'>");
					if("0".equals(fieldItem.getCodesetid())){
						sb.append("&nbsp;");
					}else{
						sb.append(fieldItem.getCodesetid());//代码类
					}					
					sb.append("</p></td>");
					sb.append("<td width='25%'><p align='center'>");
					if("1".equals(fieldItem.getUseflag())){//构库标识
						sb.append("√");
					}else{
						sb.append("×");
					}
					sb.append("</p></td>");
					sb.append("<td width='25%'><p align='center'>");
					sb.append(fieldItem.getItemdesc());//指标描述
					sb.append("</p></td></tr>");
				}

			}
		}
			
		return sb.toString();
	}
	
	/**
	 * 获得条件因子字符串中的指标
	 * @param temp 指标信息
	 * @return
	 */
	private String getFieldItem(String temp){
		String str = "";
		if(temp.indexOf(">=")!= -1){
			str = ">=";
		}else if(temp.indexOf("<=")!= -1){
			str = "<=";
		}else if(temp.indexOf("<>")!= -1){
			str = "<>";
		}else if(temp.indexOf(">")!= -1){
			str = ">";
		}else if(temp.indexOf("<")!= -1){
			str = "<";
		}else if(temp.indexOf("=")!= -1){
			str = "=";
		}else{//空
			str="";
		}
		if("".equals(str)){
			return null;
		}else{
			return temp.substring(0,temp.indexOf(str));
		}
	}

	/**
	 * 判断是否是临时变量,返回临时变量的描述信息
	 * @param field
	 * @return
	 * @throws GeneralException
	 */
	public String isTempVariable(String field) throws GeneralException{
		String chz = "";
		String sql = "select chz from midvariable where cname = '" +field+ "'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			if(rs.next()){
				chz = rs.getString("chz");
			}
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
		return chz;
	}
}
