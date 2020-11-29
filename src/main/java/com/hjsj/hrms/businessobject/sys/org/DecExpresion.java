package com.hjsj.hrms.businessobject.sys.org;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class DecExpresion {
	private String expre ;
	private Connection conn;
	public DecExpresion(Connection conn,String itemid){
		this.conn = conn;
		this.expre = getExpre(itemid.toUpperCase());
	}
	public DecExpresion(String expre){
		this.expre = expre;
	}
	public DecExpresion(){
	}
	/**
	 * 根据itemid从数据库中获取expression字段
	 * @param itemid 
	 * @return expresion 
	 * @throws Exception
	 */
	public String getExpre(String itemid){
		String expresion = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search("select Expression from fielditem where useflag=1 and itemid='"+itemid+"'");
			if(rs.next()){
				String expres = rs.getString("Expression");
				expres=PubFunc.keyWord_reback(expres);
				if(expres!=null&&expres.length()>1){
					expresion = expres;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return expresion;
	}
	/**
	 * 分解expression生成ArrayList
	 * @return type 
	 */
	public ArrayList decList(){
		ArrayList list = new ArrayList();
		if(expre.length()>0){
			String[] exp = expre.split("::"); 
			if(exp.length>0){
				for(int i=0;i<exp.length;i++){
					list.add(exp[i]);
				}
			}
		}
		return list;
	}
	/**
	 * 分解expression获取type值
	 * @return type 
	 */
	public String expreType(){
		String type = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>0){
			type=(String)exp.get(0);
		}
		return type;
	}
	/**
	 * 分解expression获取mode值
	 * @return mode 
	 */
	public String expreMode(){
		String mode = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>1){
			mode=(String)exp.get(1);
			mode= "-".equals(mode)?"":mode;
		}
		return mode;
	}
	/**
	 * 分解expression字段field|precision或expr|factor
	 * @return field 
	 */
	public String[] fpef(String fieldPre){
		String[] fPre = null;
		if(fieldPre!=null){
			fPre = fieldPre.split("\\|");
		}
		return fPre;
	}
	/**
	 * 分解expression获取field值
	 * @return field 
	 */
	public String expreField(){
		String field = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>2){
			String[] fi = fpef((String)exp.get(2));
			if(fi!=null&&fi.length>0){
				field = fi[0];
			}
		}
		return field;
	}
	/**
	 * 分解expression获取precision值
	 * @return precision 
	 */
	public String exprePrecision(){
		String precision = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>2){
			String[] pre = fpef((String)exp.get(2));
			if(pre!=null&&pre.length>1){
				precision = pre[1];
			}
		}
		return precision;
	}
	/**
	 * 分解expression获取expr值
	 * @return expr 
	 */
	public String expreExpr(){
		String expr = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>3){
			String[] ex = fpef((String)exp.get(3));
			if(ex!=null&&ex.length>0){
				expr = ex[0];
			}
		}
		return expr;
	}
	/**
	 * 分解expr存入ArrayList
	 * @return expr 
	 */
	public ArrayList decExpr(){
		ArrayList list = new ArrayList();
		String expr = expreExpr();
		if(expr!=null&&expr.length()>0){
			String[] ex = expr.split("`");
			if(ex.length>0){
				for(int i=0;i<ex.length;i++){
					list.add(ex[i]);
				}
			}
		}
		return list;
	}
	/**
	 * 分解expr获取expr存入ArrayList
	 * @return list 
	 */
	public ArrayList decExpr(String expr){
		ArrayList list = new ArrayList();
		if(expr!=null&&expr.length()>0){
			String[] ex = expr.split("`");
			if(ex.length>0){
				for(int i=0;i<ex.length;i++){
					list.add(ex[i]);
				}
			}
		}
		return list;
	}
	/**
	 * 分解expr获取itemid存入ArrayList
	 * @return list 
	 */
	public ArrayList decItemid(){
		ArrayList list = new ArrayList();
		ArrayList expre = (ArrayList)decExpr();
		for(int i=0;i<expre.size();i++){
			list.add(getitemid(expre.get(i).toString())); 
		}
		return list;
	}
	/**
	 * 分解expr获取itemid存入ArrayList
	 * @return list 
	 */
	public ArrayList decItemid(ArrayList expre){
		ArrayList list = new ArrayList();
		for(int i=0;i<expre.size();i++){
			list.add(getitemid(expre.get(i).toString())); 
		}
		return list;
	}
	/**
	 * 分解expr获取itemid存入String[]
	 * @return list 
	 */
	public String[] decArrayItemid(){
		ArrayList list = decItemid();
		String[] fielditem = new String[list.size()];
		for(int i=0;i<list.size();i++){
			fielditem[i]=(String)list.get(i);
		}
		return fielditem;
	}
	/**
	 * 分解expr获取itemid存入String[]
	 * @return list 
	 */
	public String[] decArrayItemid(ArrayList list){
		String[] fielditem = new String[list.size()];
		for(int i=0;i<list.size();i++){
			fielditem[i]=(String)list.get(i);
		}
		return fielditem;
	}
	/**
	 * 分解expression获取factor值
	 * @return factor 
	 */
	public String expreFactor(){
		String factor = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>3){
			String[] fa = fpef((String)exp.get(3));
			if(fa!=null&&fa.length>1){
				factor = fa[1];
			}
		}
		return factor;
	}
	/**
	 * 将expr和factor合成一个字符串
	 * @return port 
	 */
	public String portExpFac(){
		String port = "";
		ArrayList expr = (ArrayList)decExpr();
		ArrayList factor = decFactor();
		for(int i=0;i<factor.size();i++){
			String ex = "";
			String fa = (String)factor.get(i);
			for(int j=0;j<expr.size();j++){
				if(fa.equals((j+1)+"")){
					port += (String)expr.get(j);
				}else{
					ex = fa;
				}
			}
			port +=ex;
		}
		port = port.replaceAll("\\*"," and ").replaceAll("\\+"," or ").replaceAll("\\!"," not ");
		return port;
	}
	/**
	 * 将expr和factor合成一个字符串
	 * @return port 
	 */
	public String portExpFac(ArrayList expr,ArrayList factor){
		String port = "";
		for(int i=0;i<factor.size();i++){
			String ex = "";
			String fa = (String)factor.get(i);
			for(int j=0;j<expr.size();j++){
				if(fa.equals((j+1)+"")){
					port += (String)expr.get(j);
				}else{
					ex = fa;
				}
			}
			port +=ex;
		}
		port = port.replaceAll("\\*"," and ").replaceAll("\\+"," or ").replaceAll("\\!"," not ");
		return port;
	}
	
	/**
	 * 分解expression获取InCludeChild值
	 * @return InCludeChild 
	 */
	public String expreInCludeChild(){
		String InCludeChild = "";
		ArrayList exp = (ArrayList)decList();
		if(exp.size()>4){
			 InCludeChild =(String)exp.get(4);
		}
		InCludeChild = InCludeChild.trim().length()>0?InCludeChild:"1";
		return InCludeChild;
	}
	/**
	 * 将factor分解,组成一个List
	 * @return list 
	 */
	public ArrayList decFactor(String factor){
		ArrayList list = new ArrayList();
		String[] b = factor.split("|");
		for(int i=0;i<b.length;i++){
			if(b[i]!=null&&b[i].length()>0){
				if(isNum(b[i])){
					if(isNum(b[i-1])){
						continue;
					}
					String fa = b[i];
					if(i+1==b.length){
						list.add(fa);
						break;
					}
					if(isNum(b[i+1])){
						fa = b[i]+b[i+1];
					}
					list.add(fa);
				}else{
					list.add(b[i]);
				}
			}
		}
		return list;
	}
	/**
	 * 将factor分解,组成一个List
	 * @return list 
	 */
	public ArrayList decFactor(){
		ArrayList list = new ArrayList();
		String factor = expreFactor();
		String[] b = factor.split("|");
		for(int i=0;i<b.length;i++){
			if(b[i]!=null&&b[i].length()>0){
				if(isNum(b[i])){
					if(isNum(b[i-1])){
						continue;
					}
					String fa = b[i];
					if(i+1==b.length){
						list.add(fa);
						break;
					}
					if(isNum(b[i+1])){
						fa = b[i]+b[i+1];
					}
					list.add(fa);
				}else{
					list.add(b[i]);
				}
			}
		}
		return list;
	}
	/**
	 * 判断是否为数字
	 */
	public boolean isNum(String msg){
		Pattern pattern = Pattern.compile("[0-9]*");
		if(msg!=null&&msg.length()>0){
			Matcher isNum = pattern.matcher(msg);
			if( !isNum.matches() ){
				return false;
			}
			return true;
		}else{
			return false;
		}
	} 
	/**
	 * 分解expr获取itemid存入ArrayList
	 * @return list 
	 */
	public String getitemid(String expre){
		String itemid = "";
		String[] pat = {"=",">",">=","<","<=","<>"};
		if(expre!=null&&expre.length()>0){
			for(int i=0;i<pat.length;i++){
				String[] ex =  expre.split(pat[i]);
				if(!ex[0].equals(expre)){
					itemid = ex[0];
				}
			}
		}
		return itemid;
	}

}
