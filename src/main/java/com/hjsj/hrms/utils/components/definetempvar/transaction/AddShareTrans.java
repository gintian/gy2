package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：AddShareTrans
 * 类描述：将临时变量添加为共享
 * 创建人： lis
 * 创建时间：2015-10-29
 */
public class AddShareTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String isok = "1";
			String error = "";
			String type=(String)this.getFormHM().get("type");//1：薪资发放，3:人事异动模块 ，5:数据采集
			String nid = (String)this.getFormHM().get("nid");//临时变量id
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			/** begin 相同名字的临时变量不能设置为共享 **/
			String tempVariableCName = "";//临时变量英文名字
			String tempVariableChz = "";//临时变量中文名字
			String tempCstate = "";
			String formula = "";
			String templetId = "";//人事异动模版id，gaohy,2016-1-6
			StringBuffer sb = new StringBuffer("");
			sb.append("select cname,chz,cstate,cvalue,templetID from midvariable where nid= "+nid);
			this.frowset = dao.search(sb.toString());
			if (this.frowset.next()) {
				tempVariableCName = frowset.getString("cname");
				tempVariableChz = frowset.getString("chz");
				tempCstate=this.frowset.getString("cstate")==null ?"":this.frowset.getString("cstate");
				formula = Sql_switcher.readMemo(this.frowset, "cvalue")==null?"":Sql_switcher.readMemo(this.frowset, "cvalue");
				Integer dd = new Integer(this.frowset.getInt("templetID"));
				if(dd==null){
				    templetId="";  
				}else{
				    templetId=dd.toString();
				}
			}
			sb.setLength(0);
			if("1".equals(type)) {
				sb.append("select cname from midvariable where nflag=0 and templetId = 0 ");
			}else if("3".equals(type)){//人事异动，gaohy,2016-1-6
				sb.append("select cname from midvariable where nflag=0 and templetId<>0  ");
			}
			sb.append(" and ( chz='"+tempVariableChz+"' or cname='"+tempVariableCName+"' ) and nid<>"+nid+" ");
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				String cname = this.frowset.getString("cname")==null?"":this.frowset.getString("cname");
				if(cname.equals(tempVariableCName)){
					error=ResourceFactory.getProperty("gz_new.gz_repeatVar");//有重复的临时变量，不允许设置为共享
				} else {
					error=ResourceFactory.getProperty("gz_new.gz_hasSameName");//已经有相同名称的临时变量，请更名再设为共享
				}
				isok = "0";
				this.formHM.put("base", error);
				return;
			}
			/** end 相同名字的临时变量不能设置为共享 **/
			
			if("1".equals(isok)){//如果没有重名的，那么要继续检查
				/**  begin 检查当前临时变量中引用的临时变量是否全部是共享的  **/
				//查出所有的中文临时变量名字和英文临时变量名字
				ArrayList cnameList = new ArrayList();//英文名称集合
				ArrayList chzlist = new ArrayList();//中文名称集合
				StringBuffer sbquery = new StringBuffer("");
				sbquery.append("select cname,chz from midvariable where nid<>"+nid+" ");
				if("1".equals(type)){//如果是从薪资类别模板进入的
					sbquery.append(" and nflag=0 and templetId = 0 and (cstate ='"+tempCstate+"' or cstate is null)");
				}else if("3".equals(type)){//人事异动，gaohy,2016-1-6
					sbquery.append(" and nflag=0 and templetId <> 0 and (templetId ="+templetId+" or cstate = '1')");//查询的是该模版下的临时变量和模块中共享的临时变量
				}
				//查询其他临时变量（排除当前临时变量）
				this.frowset = dao.search(sbquery.toString());
				while(this.frowset.next()){
					String cname = this.frowset.getString("cname")==null?"":this.frowset.getString("cname");
					String chz = this.frowset.getString("chz")==null?"":this.frowset.getString("chz");
					if(StringUtils.isNotBlank(cname)){
						cnameList.add(cname);
					}
					if(StringUtils.isNotBlank(chz)){
						chzlist.add(chz);
					}
				}
				if(!"".equals(formula)){
					String notsharecondition = "";
					if("1".equals(type)) {
						notsharecondition = " and cstate = '"+tempCstate+"'";
					}else if("3".equals(type)){//人事异动，gaohy,2016-1-6
						notsharecondition = " and (cstate is null or cstate='')";
					}
					ArrayList<String> ergodiclist = new ArrayList<String>();
					ergodiclist.add(tempVariableCName);
					ergodiclist.add(tempVariableChz);
					ArrayList templist = getMidVariableList(this.getFrameconn(),formula,chzlist,cnameList,notsharecondition,new ArrayList(),ergodiclist);
					if(templist!=null && templist.size()>0){
						isok = "0";
						StringBuffer msg = new StringBuffer("");
						int tempcount = templist.size();
						for(int i=0;i<tempcount;i++){
							String strtemp = (String)templist.get(i);
							msg.append(strtemp+",");
						}
						msg.setLength(msg.length()-1);
						//该临时变量引用了以下不共享的临时变量,请先将这些临时变量设置为共享！
						error = ResourceFactory.getProperty("gz_new.gz_thisVarHasUse")+"："+msg.toString()+"，"+ResourceFactory.getProperty("gz_new.gz_setShare");
						this.formHM.put("base", error);
						return;
					}
				}
				/**  end  检查当前临时变量中引用的临时变量是否全部是共享的  **/
			}
			this.formHM.put("base", "ok");
			//设置为共享
			if("3".equals(type))//人事异动 lis
				dao.update("update midvariable set cState='1' where nId="+nid);
			else
				dao.update("update midvariable set cState=null where nId="+nid);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/*
	 * 返回所有未共享的临时变量  
	 *conn：链接
	 *formula：表达式
	 *cmidvariablelist：所有的中文临时变量
	 *emidvariablelist：所有的英文临时变量
	 *notsharecondition：不共享的条件
	 *notsharelist：不共享的临时变量的中文名字，也就是函数的返回结果
	 *ergodiclist:已经遍历过的临时变量（以该临时变量为基础，遍历其表达式中的临时变量），防止死循环  注意，中文名字和英文名字都要加上
	 *处理逻辑：先找到表达式中所有不共享的，并存储起来。再把所有的临时变量（共享的和不共享的）的表达式中的所有不共享的再存起来。
	  **/
	public static ArrayList getMidVariableList(Connection conn,String formula,ArrayList cmidvariablelist,ArrayList emidvariablelist,String notsharecondition ,ArrayList notsharelist,ArrayList ergodiclist){
		RowSet rs = null;
		try{
			//先找到表达式中所有的临时变量
			ArrayList templist = getAllMidVariableList(formula,cmidvariablelist,emidvariablelist);
			//再选出所有共享的临时变量，进入递归,并加入到sharelist中。不共享的临时变量就不参与递归了。不共享的临时变量需要加入到notsharelist中
			ArrayList clist = (ArrayList)templist.get(0);
			ArrayList elist = (ArrayList)templist.get(1);
			String cstring = getStringByList(clist);
			String estring = getStringByList(elist);
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sb = new StringBuffer("");
			//把当前表达式中所有临时变量的非共享的先存起来
			sb.append("select nid,cname,chz from midvariable where (cname in ("+estring+") or chz in ("+cstring+"))");//cname是英文名字 chz是中文名字
			sb.append(" "+notsharecondition);
			rs = dao.search(sb.toString());
			while(rs.next()){
				String chz = rs.getString("chz")==null?"":rs.getString("chz");
				if("".equals(chz))
					continue;
				if(!notsharelist.contains(chz)){
					notsharelist.add(chz);
				}
			}
			//对这个表达式中所有的临时变量进行递归。要先找出每个临时变量的表达式
			sb.setLength(0);
			sb.append("select nid,cname,chz,cvalue from midvariable where (cname in ("+estring+") or chz in ("+cstring+"))");
			sb.append(" "+notsharecondition);
			rs = dao.search(sb.toString());
			ArrayList enamelist = new ArrayList();
			ArrayList cnamelist = new ArrayList();
			ArrayList valuelist = new ArrayList();
			while(rs.next()){
				String ename = rs.getString("cname");
				String cname = rs.getString("chz");
				String value = Sql_switcher.readMemo(rs, "cvalue");
				enamelist.add(ename);
				cnamelist.add(cname);
				valuelist.add(value);
			}
			int n = enamelist.size();
			for(int i=0;i<n;i++){
				String tempename = (String)enamelist.get(i);
				String tempcname = (String)cnamelist.get(i);
				if(!(ergodiclist.contains(tempename) || ergodiclist.contains(tempcname))){
					String value = (String)valuelist.get(i);
					ergodiclist.add(tempename);
					ergodiclist.add(tempcname);
					getMidVariableList(conn,value,cmidvariablelist,emidvariablelist,notsharecondition,notsharelist,ergodiclist);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return notsharelist;
	}
	/**
	 * 找到表达式中的所有临时变量。  
	 * cmidvariablelist:包含临时变量表中所有临时变量的中文名字
	 * emidvariablelist:英文名字
	 * */
	public static ArrayList getAllMidVariableList(String formula,ArrayList cmidvariablelist,ArrayList emidvariablelist){
		ArrayList list = new ArrayList();
		ArrayList clist = new ArrayList();//中文临时变量列表
		ArrayList elist = new ArrayList();//英文临时变量列表
		try {
			if("".equals(formula)){
				return list;
			}
			//先处理中文名
			int count1 = cmidvariablelist.size();
			for(int i=0;i<count1;i++){
				String tempvarname = (String)cmidvariablelist.get(i);
				int lastindex = 0;//记录上一次到了那个位置
				int index = formula.indexOf(tempvarname,lastindex);
				while(index!=-1){//如果找到了，说明cfactor中可能用到了该临时变量，则要进一步判断
					char leftchar = '+';//得到左边字符
					char rightchar = ' ';
					if(index==0){
						leftchar = '+';
						int tempindex = index+tempvarname.length();
						rightchar = ' ';
						/** 如果是开头的第一个就是该临时变量那么左侧默认赋值上“+” 右侧是否应该根据formula来判断呢？所以我觉得这个方法应该是这个样子的**/
						if(tempindex<formula.length()){
							rightchar=formula.charAt(tempindex);
						}
					}else{
						leftchar = formula.charAt(index-1);
						int tempindex = index+tempvarname.length();
						rightchar = ' ';
						if(tempindex<formula.length()){
							rightchar=formula.charAt(tempindex);
						}
					}//得到左边字符
					if(PubFunc.checkChar(leftchar,rightchar)){//如果公式中正好有这个变量
						clist.add(tempvarname);
						break;
					}
					lastindex = index+1;
					index = formula.indexOf(tempvarname,lastindex);//之所以用while，是因为index是不断变化的
				}
			}
			//再处理英文名
			int count2 = cmidvariablelist.size();
			for(int i=0;i<count2;i++){
				String tempvarname = (String)emidvariablelist.get(i);
				int lastindex = 0;//记录上一次到了那个位置
				int index = formula.indexOf(tempvarname,lastindex);
				while(index!=-1){//如果找到了，说明cfactor中可能用到了该临时变量，则要进一步判断
					char leftchar = '+';//得到左边字符
					if(index==0){
						leftchar = '+';
					}else{
						leftchar = formula.charAt(index-1);
					}//得到左边字符
					int tempindex = index+tempvarname.length();
					char rightchar = ' ';//formula.charAt(tempindex);
					if(tempindex<formula.length()){
						rightchar=formula.charAt(tempindex);
					}
					
					if(PubFunc.checkChar(leftchar,rightchar)){//如果公式中正好有这个变量
						elist.add(tempvarname);
						break;
					}
					lastindex = index+1;
					index = formula.indexOf(tempvarname,lastindex);//之所以用while，是因为index是不断变化的
				}
			}
			list.add(clist);
			list.add(elist);
			
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * 通过list获得sql语句处理的字符串  
	 * */
	public static String getStringByList(ArrayList list){
		StringBuffer str = new StringBuffer("");
		if(list==null || list.size()==0){
			str.append("''");
		}else{
			int n = list.size();
			for(int i=0;i<n;i++){
				String temp = (String)list.get(i);
				str.append("'"+temp+"',");
			}
			str.setLength(str.length()-1);
		}
		return str.toString();
	}
}
