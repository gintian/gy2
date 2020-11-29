/*
 * Created on 2006-4-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.report;

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
 * 
 * <p>Title:报表表达式分析</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 21, 2006:9:17:21 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportAnalyse {
	
	private Connection conn;//DB连接
	private String reportId;//报表号
	private String reportName;//报表名称
	private String fieldAnalyses;//指标分析结果
	private String exprAnalyses;//表达式分析结果
	private int rowNumber;
	private int columnNumber;
	
	public ReportAnalyse(){	
	}
	public ReportAnalyse(Connection conn ,String reportId ,String reportName){
		this.conn=conn;
		this.reportId=reportId;
		this.reportName=reportName;
	}
	
	/**
	 * 指标分析
	 * @throws GeneralException
	 */
	public void fieldAnalyse() throws GeneralException{
		
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("select cfactor from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(this.reportId));

		StringBuffer sb = new StringBuffer();
		HashSet set = new HashSet();
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
								set.add(temp1);
							}	
					}//end for 
				}
			}//end while 
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
		
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){
			String temp = (String)iterator.next();
			if(temp == null || "".equals(temp)){
				
			}else{
				//封装指标对象
				FieldItem fieldItem = DataDictionary.getFieldItem(temp);
				//null则表明此指标为构库
				//System.out.println("fieldItem=" + fieldItem);
				
				if(fieldItem ==null){//未构库的指标
					if(temp.startsWith("yk")){ //临时变量
						String chz = this.isTempVariable(temp);
						if(chz == null || "".equals(chz)){
						}else{
							sb.append("<tr><td width='25%'><p align='center'>");
							sb.append(ResourceFactory.getProperty("kq.wizard.variable")+".");
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
			
		this.setFieldAnalyses(sb.toString());
	}
	
	//获得条件因子字符串中的指标
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
		
	//表达式分析
	public void exprAnalyse() throws GeneralException{
		
		StringBuffer returnString = new StringBuffer();
		//表总条件单元格设置信息
		int rleft = 0;//左边线标识
		int rtop = 0;//顶边线标识
		int rwidth =  0;//宽度
		int rheight = 0;//高度
		
		StringBuffer sql = new StringBuffer();	
		sql.delete(0,sql.length());
		sql.append("select * from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(this.reportId));
		sql.append(" and flag = 0 ");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			if(rs.next()){
				 //表总条件单元格设置信息
				 rleft = rs.getInt("rleft");//左边线标识
				 rtop = rs.getInt("rtop");//顶边线标识
				 rwidth = rs.getInt("rwidth");//宽度
				 rheight = rs.getInt("rheight");//高度
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

		returnString.append(this.reportHeadAnalyse());
		returnString.append(this.reportRowsAnalyse(rleft,rwidth));
		returnString.append(this.reportColumnsAnalyse(rtop,rheight));
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
		//returnString.insert(0,"<tr><td width='100%' colspan='4'>"
		returnString.insert(0,"<tr><td width='100%' colspan='4' style='padding-left:5px;'>"
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
				+ResourceFactory.getProperty("reportanalyse.expranalyse") +" " +
				+ this.getRowNumber()+ ResourceFactory.getProperty("reportanalyse.row") 
				+"x" + this.getColumnNumber() + ResourceFactory.getProperty("reportanalyse.column") 
				+ "</td></tr>");
		this.setExprAnalyses(returnString.toString());
	}
	
	//报表表达式分析-表总条件分析
	private String reportHeadAnalyse() throws GeneralException{
		StringBuffer headInfo = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("select * from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(this.reportId));
		sql.append(" and flag = 0");
	
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			if(rs.next()){
				String hz = rs.getString("hz");//单元格汉字描述
				if(hz == null || "".equals(hz)){
					hz="";
				}else{
					hz = hz.replaceAll("`","");
				}
				String expr = rs.getString("cexpr1");//表达式
				String expr1 = rs.getString("cexpr2");//公式
				String cfactor = rs.getString("cfactor");//条件因子				
				String fieldItemInfo = this.getFieldItemInfo(cfactor);
				
				/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
				headInfo.append("<tr><td width='100%' colspan='4' style='padding-left:5px;'>");
				//headInfo.append("<tr><td width='100%' colspan='4'>");
				/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
				headInfo.append(ResourceFactory.getProperty("reportanalyse.ztj"));
				headInfo.append(hz);
				if(cfactor == null ){
					headInfo.append("<br>");
					headInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
				}else if ( "FORMULA".equals(cfactor)){//如果条件因子是公式
					headInfo.append(ResourceFactory.getProperty("reportanalyse.ljjsexpr"));
					headInfo.append("<br>");
					headInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
					headInfo.append(expr1);					
				}else{					
					headInfo.append("<br>");
					headInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
					headInfo.append(expr);
					headInfo.append("<br>");
					headInfo.append(fieldItemInfo);
				}		
				headInfo.append("<br></td></tr>");
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
		return headInfo.toString();
	}
	
	//报表表达式分析-行分析
	private String reportRowsAnalyse(int rleft ,int rwidth) throws GeneralException{
		StringBuffer rowsInfo = new StringBuffer();
		int i=1;
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("select * from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(this.reportId));
		sql.append(" and flag = 2 and flag1 <> 4");
		sql.append(" order by rtop ");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				String hz = rs.getString("hz");//单元格汉字描述
				if(hz == null || "".equals(hz)){
					hz="";
				}else{
					hz = hz.replaceAll("`","");
				}
				String expr = rs.getString("cexpr1");//表达式
				String expr1 = rs.getString("cexpr2");//公式
				String cfactor = rs.getString("cfactor");//条件因子				
				String fieldItemInfo = this.getFieldItemInfo(cfactor);
				
				if(rs.getInt("rleft") + rs.getInt("rwidth") == (rleft + rwidth)){
					/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
					rowsInfo.append("<tr><td width='100%' colspan='4' style='padding-left:5px;'>");
					/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
					rowsInfo.append(ResourceFactory.getProperty("reportanalyse.di"));
					rowsInfo.append(i);
					rowsInfo.append(ResourceFactory.getProperty("reportanalyse.row"));
					rowsInfo.append(" : ");
					rowsInfo.append(hz);
					if(cfactor == null ){
						rowsInfo.append("<br>");
						rowsInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
					}else if("FORMULA".equals(cfactor)){//如果条件因子是公式
						rowsInfo.append(ResourceFactory.getProperty("reportanalyse.ljjsexpr"));
						rowsInfo.append("<br>");
						rowsInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
						rowsInfo.append(expr1);					
					}else{
						rowsInfo.append(ResourceFactory.getProperty("reportanalyse.yjtj"));							
						rowsInfo.append("<br>");
						rowsInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
						rowsInfo.append(expr);
						rowsInfo.append("<br>");
						rowsInfo.append(fieldItemInfo);
					}	
					
					if(rs.getInt("rwidth")< rwidth){//可能有二级条件
						int left = rs.getInt("rleft");
						int top = rs.getInt("rtop");
						String temp = this.rowAnalyse(left,top);
						if(temp== null || "".equals(temp)){
						}else{
							/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
							//rowsInfo.append("<tr><td width='100%' colspan='4'>");
							rowsInfo.append("<tr><td width='100%' colspan='4' style='padding-left:5px;'>");
							/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
							rowsInfo.append(ResourceFactory.getProperty("reportanalyse.di"));
							rowsInfo.append(i);
							rowsInfo.append(ResourceFactory.getProperty("reportanalyse.row"));
							rowsInfo.append(" : ");
							rowsInfo.append(temp);
						}
					}	
					i++;
				}else{
					continue;
				}
				rowsInfo.append("<br></td></tr>");
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
		this.setRowNumber(i-1);
		return rowsInfo.toString();
	}

	//分析行是否有二级条件
	private String rowAnalyse(int left , int top) throws GeneralException{
		
		StringBuffer sbr = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from tgrid2 where (rleft+rwidth)= ");
		sb.append(left);
		sb.append(" and rtop <= ");
		sb.append(top);
		sb.append(" order by rtop desc");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sb.toString());
			if(rs.next()){
				String hz = rs.getString("hz");//单元格汉字描述
				if(hz == null || "".equals(hz)){
					hz="";
				}else{
					hz = hz.replaceAll("`","");
				}
				String expr = rs.getString("cexpr1");//表达式
				String expr1 = rs.getString("cexpr2");//公式
				String cfactor = rs.getString("cfactor");//条件因子				
				String fieldItemInfo = this.getFieldItemInfo(cfactor);
				
				if(cfactor == null ){	
				}else if("FORMULA".equals(cfactor)){
				}else{
					sbr.append(hz);
					sbr.append(ResourceFactory.getProperty("reportanalyse.ejtj"));							
					sbr.append("<br>");
					sbr.append(ResourceFactory.getProperty("reportanalyse.expr"));
					sbr.append(expr);
					sbr.append("<br>");
					sbr.append(fieldItemInfo);
				}
				
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
		
		return sbr.toString();
		
	}

	//报表表达式分析-列分析
	private String reportColumnsAnalyse(int rtop ,int rheight) throws GeneralException{
		StringBuffer columnInfo = new StringBuffer();
		int i=1;
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("select * from tgrid2 where tabid = ");
		sql.append(Integer.parseInt(this.reportId));
		sql.append(" and flag = 1 and flag1 <> 4");
		sql.append(" order by rleft ");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				String hz = rs.getString("hz");//单元格汉字描述
				if(hz == null || "".equals(hz)){
					hz="";
				}else{
					hz = hz.replaceAll("`","");
				}
				String expr = rs.getString("cexpr1");//表达式
				String expr1 = rs.getString("cexpr2");//公式
				String cfactor = rs.getString("cfactor");//条件因子				
				String fieldItemInfo = this.getFieldItemInfo(cfactor);
				
				if(rs.getInt("rtop") + rs.getInt("rheight") == (rtop + rheight)){
					/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
					columnInfo.append("<tr><td width='100%' colspan='4' style='padding-left:5px;'>");
					/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
					columnInfo.append(ResourceFactory.getProperty("reportanalyse.di"));
					columnInfo.append(i);
					columnInfo.append(ResourceFactory.getProperty("reportanalyse.column"));
					columnInfo.append(" : ");
					columnInfo.append(hz);
					if(cfactor == null ){
						columnInfo.append("<br>");
						columnInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
					}else if("FORMULA".equals(cfactor)){//如果条件因子是公式
						columnInfo.append(ResourceFactory.getProperty("reportanalyse.ljjsexpr"));
						columnInfo.append("<br>");
						columnInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
						columnInfo.append(expr1);					
					}else{
						columnInfo.append(ResourceFactory.getProperty("reportanalyse.yjtj"));							
						columnInfo.append("<br>");
						columnInfo.append(ResourceFactory.getProperty("reportanalyse.expr"));
						columnInfo.append(expr);
						columnInfo.append("<br>");
						columnInfo.append(fieldItemInfo);
					}
					
					//验证是否有二级条件
					if(rs.getInt("rheight") < rheight){//可能有二级条件
						int top = rs.getInt("rtop");
						int left = rs.getInt("rleft");
						String temp = this.columnAnalyse(top,left);
						if(temp== null || "".equals(temp)){
						}else{
							/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
							columnInfo.append("<tr><td width='100%' colspan='4' style='padding-left:5px;'>");
							/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
							columnInfo.append(ResourceFactory.getProperty("reportanalyse.di"));
							columnInfo.append(i);
							columnInfo.append(ResourceFactory.getProperty("reportanalyse.column"));
							columnInfo.append(" : ");
							columnInfo.append(temp);
						}
					}
					i++;
				}else{
					continue;
				}
				columnInfo.append("<br></td></tr>");
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
		this.setColumnNumber(i-1);
		return columnInfo.toString();
	}
	
	//分析列是否有二级条件
	private String columnAnalyse (int top , int left) throws GeneralException{
		//select * from tgrid2 where (rtop+rheight)=106 and rleft <= 275
		//select * from tgrid2 where rleft
		StringBuffer sbr = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from tgrid2 where (rtop+rheight)= ");
		sb.append(top);
		sb.append(" and rleft <= ");
		sb.append(left);
		sb.append(" order by rleft desc");
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sb.toString());
			if(rs.next()){
				String hz = rs.getString("hz");//单元格汉字描述
				if(hz == null || "".equals(hz)){
					hz="";
				}else{
					hz = hz.replaceAll("`","");
				}
				String expr = rs.getString("cexpr1");//表达式
				String expr1 = rs.getString("cexpr2");//公式
				String cfactor = rs.getString("cfactor");//条件因子				
				String fieldItemInfo = this.getFieldItemInfo(cfactor);
				
				if(cfactor == null ){	
				}else if("FORMULA".equals(cfactor)){
				}else{
					sbr.append(hz);
					sbr.append(ResourceFactory.getProperty("reportanalyse.ejtj"));							
					sbr.append("<br>");
					sbr.append(ResourceFactory.getProperty("reportanalyse.expr"));
					sbr.append(expr);
					sbr.append("<br>");
					sbr.append(fieldItemInfo);
				}
				
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
		return sbr.toString();
	}
	
	private String getFieldItemValue(String temp){
		if(temp == null || "".equals(temp)){
			return null;
		}
		int n = 0; 
		for(int i = 0; i< temp.length(); i++){
			if("><=".indexOf(temp.charAt(i))!= -1){
				if(">=".indexOf(temp.charAt(i+1))!=-1){
					n = i +2;
					break;
				}else{
					n = i +1;
					break;
				}
			}
		}
		return temp.substring(n,temp.length());

	}
	
	//未构库指标使用
	private int getTempValue(String temp){
		if(temp == null || "".equals(temp)){
			return 0;
		}
		int n = 0; 
		for(int i = 0; i< temp.length(); i++){
			if("><=".indexOf(temp.charAt(i))!= -1){
				n=i;
			}
		}
		return n;

	}
	//获得指标分析信息
	private String getFieldItemInfo(String cfactor) throws GeneralException{
		StringBuffer sb = new StringBuffer();
		if(cfactor != null && !"".equals(cfactor)){
			//切割字符传
			String [] fielditems = cfactor.split("`");
			//遍例
			for(int i = 0 ; i< fielditems.length ; i++){
				String tempx = fielditems[i];
				//获得指标
				String temp = this.getFieldItem(fielditems[i]);	
				
				if(temp != null){
					//得到指标对象
					FieldItem fieldItem = DataDictionary.getFieldItem(temp);
					if(fieldItem != null){
						//指标描述
						String fieldItemDesc = fieldItem.getItemdesc();
						//指标对应代码类ID,对应不是代码类的为0
						String codesetid = fieldItem.getCodesetid();
						
						//获得条件因子中的对应的设置值
						String temp1= this.getFieldItemValue(fielditems[i]);
						//获得设置值的描述
						String str = this.getFieldItemValueDesc(codesetid,temp1);
				/*		
						System.out.println("*****************");
						System.out.println("指标=" + temp);
						System.out.println("指标描述=" + fieldItemDesc);
						System.out.println("指标对应代码类=" + codesetid);
						System.out.println("条件因子中对应的设置值=" + temp1);
						System.out.println("条件因子设置值的描述信息=" + str);
						System.out.println("*****************");*/
						
						StringBuffer sbr = new StringBuffer(fielditems[i]);
						
						sbr.insert(0,"[");
						if(fieldItemDesc != null){
							sbr.insert(temp.length()+1,"("+ fieldItemDesc +")]" );
						}
						if(str != null){
							sbr.append("(");
							sbr.append(str);
							sbr.append(")");
						}
						if(temp1.indexOf("*")!= -1){
							sbr.append("\t\t");
							sbr.append(ResourceFactory.getProperty("reportanalyse.ms"));
							sbr.append("\t\t");
						}
						if(!"0".equals(codesetid)){
							sbr.append("\t\t");
							sbr.append(codesetid);
						}
						sb.append("&nbsp;&nbsp;");
						sb.append( i+1);
						sb.append(":   ");
						sb.append(sbr.toString());
						sb.append("<br>");
					}else{//指标未构库
						StringBuffer s = new StringBuffer();
						//System.out.println("xxxxxx=" + tempx);
						if(tempx.startsWith("yk")){//临时变量
							String tvinfo = this.getTempVariableInfo(tempx);
							s.append(tvinfo);
							sb.append("&nbsp;&nbsp;");
							sb.append( i+1);
							sb.append(":   ");
							sb.append(s.toString());
							sb.append("<br>");
						}else{
							if(tempx == null || "".equals(tempx)){
								sb.append("&nbsp;&nbsp;");
								sb.append( i+1);
								sb.append(":   ");
								sb.append("");
								sb.append("<br>");
							}else{
								s.append("[");
								int n = this.getTempValue(tempx);
								if(n == 0){
									sb.append("&nbsp;&nbsp;");
									sb.append( i+1);
									sb.append(":   ");
									sb.append(tempx);
									sb.append("<br>");
								}else{
									s.append(tempx.substring(0,n));
									s.append("()]");
									s.append(tempx.substring(n,tempx.length()));
									sb.append("&nbsp;&nbsp;");
									sb.append( i+1);
									sb.append(":   ");
									sb.append(s.toString());
									sb.append("<br>");
								}
								
							}
							
						}
					
					}
				
				}
			}//end for
		}//end if
		
		return sb.toString();
		
	}
	
	
	//获得指定代码项描述信息
	private String getFieldItemValueDesc(String codesetid ,String codeitemid) throws GeneralException{
		//select codeitemdesc from codeitem where codesetid='AX' and codeitemid = '1'
		String temp=null;
		StringBuffer sql = new StringBuffer();
		sql.delete(0,sql.length());
		//System.out.println("codeitemid=" + codeitemid);
		if(codeitemid == null || "0".equals(codesetid) || codeitemid.indexOf('*')!= -1 || "NULL".equals(codeitemid)){
			return null;
		}else{
			//部门表
			if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
				sql.append("select codeitemdesc from organization where codesetid='");
				sql.append(codesetid);
				sql.append("' and codeitemid = '");
				sql.append(codeitemid);
				sql.append("'");
			}else{
				//代码项表
				sql.append("select codeitemdesc from codeitem where codesetid='");
				sql.append(codesetid);
				sql.append("' and codeitemid = '");
				sql.append(codeitemid);
				sql.append("'");
			}
			
		}
	//	System.out.println("sql=" + sql.toString());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			if(rs.next()){
				temp = rs.getString("codeitemdesc");
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
		
		if(temp == null || "".equals(temp)){
			temp = ResourceFactory.getProperty("kq.wizard.noCode");
		}
		return temp;
		
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
	
	/**
	 * 获取临时变量描述信息
	 * @param temp  描述字符串
	 * @return      规范化字符串
	 */
	private String getTempVariableInfo(String temp){
		StringBuffer result = new StringBuffer();
		if(temp == null || "".equals(temp)){
			return "";
		}
		int n = 0; 
		for(int i = 0; i< temp.length(); i++){
			if("><=".indexOf(temp.charAt(i))!= -1){
				n = i;
				break;
			}
		}
		if(n == 0){
			result.append(temp);
		}else{
			result.append("[");		
			result.append(temp.substring(0,n));
			result.append("()]");
			result.append(temp.substring(n,temp.length()));
		}
		return result.toString();
	}
	
	
	
	
	
	
	
	
	

	/**
	 * 表达式分析的字符串表现形式
	 */
	public String getReportExprAnalyseResult(){
		StringBuffer result = new StringBuffer();
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 start */
		result.append("<tr><td style='padding-left:5px;'>");
		//result.append("<tr><td>");
		/* 报表管理-自动生成-提取数据-表达式分析 xiaoyun 2014-7-1 end */
		result.append(this.reportId);
		result.append(".");
		result.append(this.reportName);
		result.append("<br>");
		result.append("指标及代码项:");
		result.append("</td></tr>");
		result.append(this.getFieldAnalyses());
		result.append(this.getExprAnalyses());
		return result.toString();
		
	}
	
	
	
	public String getFieldAnalyses() {
		return fieldAnalyses;
	}

	public void setFieldAnalyses(String fieldAnalyses) {
		this.fieldAnalyses = fieldAnalyses;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getExprAnalyses() {
		return exprAnalyses;
	}

	public void setExprAnalyses(String exprAnalyses) {
		this.exprAnalyses = exprAnalyses;
	}
	
	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
}
