package com.hjsj.hrms.businessobject.sys.fieldsubset;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * <p>Title:指标维护</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 24, 2008:2:09:23 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */

public class IndexBo {
	private Connection conn = null;
	
	public IndexBo (Connection a_con){
		this.conn = a_con;
	}
	/*
	 * 指标最大值算法
	 * 添加dev_flag
	 * jingq 2015.01.27
	 * 
	 * guodd update 2015-08-05 算法改变
	 */
	public String getindex(String setid, String dev_flag) {
		String indexC = "";
		String ret = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if (setid != null && !"".equals(setid)) {
				ArrayList list = new ArrayList();
//				RowSet rowSet = dao
//						.search("select itemid,(select max(itemid) from fielditem where fieldsetid = '"
//								+ setid
//								+ "') as AAA from fielditem where fieldsetid = '"+setid+"'");
//				while(rowSet.next()) {
//					ret = rowSet.getString(2);
//					list.add(rowSet.getString(1));
//				}
				
				//guodd 2015-08-05 
				//xus 20/4/14 达梦数据库
				String sql = "";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字 
						sql = "select itemid,(select max(regexp_substr(itemid, '"+setid+"[0-9][A-Z]')) from fielditem ) maxvalue " +
								"from (SELECT itemid, regexp_substr(itemid, '"+setid+"[0-9][[:alnum:]]') as reg FROM fielditem) sourceData " +
								"where reg is not NULL order by itemid DESC";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z 
						sql = "select itemid,NULL maxvalue " +
								"from (SELECT itemid, regexp_substr(itemid, '"+setid+"[A-W][A-Z]') as reg FROM fielditem) sourceData " +
								"where reg is not NULL order by itemid DESC";
					}
				}else{
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字 
						sql = "select itemid,(select max(itemid)  from fielditem where  itemid like '"+setid+"[0-9][A-Z]' ) maxvalue from fielditem where  itemid like '"+setid+"[0-9]%' order by itemid desc";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z  
						sql = "select itemid,null maxvalue from fielditem where itemid like '"+setid+"[A-W][A-Z]' order by itemid desc ";
					}
				}
				rs = dao.search(sql);
				while(rs.next()){
					list.add(rs.getString("itemid"));
				    ret = rs.getString("maxvalue");
				}
				if(ret==null && !list.isEmpty()) {
                    ret = list.get(0).toString();
                }
				SubSetBo subbo = new SubSetBo(conn);
				indexC = subbo.toAdd(ret, setid, dev_flag, list);//toAdd(ret, setid,dev_flag,list);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return indexC;
	}
	
	/**
	 * 业务字典计算指标代码号
	 * @param setid
	 * @param dev_flag
	 * @return
	 */
	public String getBusiIndex(String setid,String dev_flag){
		String indexC = "";
		String ret = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if (setid != null && !"".equals(setid)) {
				ArrayList list = new ArrayList();
				
				//guodd 2015-08-05 
				String sql = "";
				if(Sql_switcher.searchDbServer()== Constant.ORACEL){
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字
						sql = "select itemid,(select max(regexp_substr(itemid, '"+setid+"[0-9][[:alnum:]]')) from t_hr_busifield) maxvalue " +
								"from (SELECT itemid, regexp_substr(itemid, '"+setid+"[0-9][[:alnum:]]') as reg FROM t_hr_busifield where fieldsetid='"+setid+"') sourceData " +
								"where reg is not NULL order by itemid DESC";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z 内置指标有存在小写的都要加上 wangb 20170915 31427
						sql = "select itemid,NULL maxvalue " +
//								"from (SELECT itemid, regexp_substr(itemid, '"+setid+"[A-W][A-Z]') as reg FROM t_hr_busifield) sourceData " +
								"from (SELECT upper(itemid) itemid, regexp_substr(itemid, '"+setid+"[A-Wa-w][A-Za-z]') as reg FROM t_hr_busifield) sourceData " +
								"where reg is not NULL order by itemid DESC";
					}
				}else{
					if("1".equals(dev_flag)){//开发模式下，按照开发商模式生成代码规则：库前缀+数字+字母或数字 内置指标有存在小写的都要加上 wangb 20170915 31427
//						sql = "select itemid,(select max(itemid)  from t_hr_busifield where  itemid like '%"+setid+"[0-9][0-9A-Z]' ) maxvalue from t_hr_busifield where  itemid like '"+setid+"[0-9]%' order by itemid desc";
						sql = "select itemid,(select max(itemid)  from t_hr_busifield where  itemid like '%"+setid+"[0-9][0-9A-Za-z]' ) maxvalue from t_hr_busifield where  itemid like '"+setid+"[0-9]%' order by itemid desc";
					}else{//用户模式下，代码规则为后两位必须为字母并且倒数第二位不能为X、Y、Z 内置指标有存在小写的都要加上 wangb 20170915 31427
//						sql = "select itemid,null maxvalue from t_hr_busifield where itemid like '"+setid+"[A-W][A-Z]' order by itemid desc ";
						sql = "select itemid,null maxvalue from t_hr_busifield where itemid like '"+setid+"[A-Wa-w][A-Za-z]' order by itemid desc ";
					}
				}
				rs = dao.search(sql);
				while(rs.next()){
					list.add(rs.getString("itemid"));
				    ret = rs.getString("maxvalue");
				}
				if(ret==null && !list.isEmpty()) {
                    ret = list.get(0).toString();
                }
				SubSetBo subbo = new SubSetBo(conn);
				//业务字典 新增指标代号 公式不能走指标体系的  wangb 20170607   28397
				indexC = subbo.createBuCode(ret,setid,dev_flag);/*indexC = subbo.createNewCode(ret,setid,dev_flag);*/
				if(indexC==null){
					 if ("1".equals(dev_flag)) {
						 indexC = setid + "02";
					 }else{
						 indexC = setid + "AA";
					 }
					
					 while(list.contains(indexC)){//如果存在，说明被占用，生成新的代码，再次判断
						list.remove(indexC);//去掉已经存在的
						//业务字典 新增指标代号 公式不能走指标体系的  wangb 20170607   28397
						indexC =  subbo.createBuCode(indexC,setid,dev_flag);//生成新的代码 /*indexC =  subbo.createNewCode(indexC,setid,dev_flag);*/
						if(indexC==null) {
                            break;
                        }
					 }
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return indexC;
		
	}
	
	
	/*
	 * 根据dev_flag获取指标代码  jingq upd 2015.01.27
	 */
	private int index = 0;
	public String toAdd(String indexC,String setid,String dev_flag,ArrayList list){
		if(index<=100){
			index++;
			String ascii = "";
			if (indexC == null || indexC == "") {
				if("".equals(dev_flag)||"0".equals(dev_flag)){
					ascii = setid + "A" + 1;
				} else if("1".equals(dev_flag)){
					ascii = setid + 0 + 1;
				}
			} else {
				char buf[] = indexC.toUpperCase().toCharArray();
				if (buf.length == 5) {
					if("".equals(dev_flag)||"0".equals(dev_flag)){//用户模式
						if(buf[3]>='0'&&buf[3]<='9'||buf[3]=='X'||buf[3]=='Y'||buf[3]=='Z'){
							buf[4] = '0';
							buf[3] = 'A';
							buf = setCode(buf,2);
						} else if(buf[3]=='W'){
							if(buf[4]=='9'){
								buf[4] = 'A';
							} else if(buf[4]=='Z'){
								buf[4] = '0';
								buf[3] = 'A';
								buf = setCode(buf,2);
							} else {
								buf[4] = (char) (buf[4]+1);
							}
						} else {
							if(buf[4]=='9'){
								buf[4] = 'A';
							} else if(buf[4]=='Z'){
								buf[4] = '0';
								buf[3] = (char) (buf[3]+1);
							} else {
								buf[4] = (char) (buf[4]+1);
							}
						}
					} else if("1".equals(dev_flag)){//开发模式
						if(buf[3]=='9'){
							if(buf[4]=='9'){
								buf[4] = 'A';
							} else if(buf[4]=='Z'){
								buf[4] = '0';
								buf[3] = '0';
								buf = setCode(buf,2);
							} else {
								buf[4] = (char) (buf[4]+1);
							}
						} else if(buf[3]<'9'&&buf[3]>='0'){
							if(buf[4]=='9'){
								buf[4] = 'A';
							} else if(buf[4]=='Z'){
								buf[4] = '0';
								buf[3] = (char) (buf[3]+1);
							} else {
								buf[4] = (char) (buf[4]+1);
							}
						} else {
							buf[4] = '0';
							buf[3] = '0';
							buf = setCode(buf,2);
						}
					}
				} else {
					return "";
				}
				ascii = String.valueOf(buf);
			}
			if("00000".equals(ascii)){//如果超过最大限制，则找数据库中不存在的
				if("".equals(dev_flag)||"0".equals(dev_flag)){
					ascii = setid+"A0";
					ascii = toAdd(ascii,setid,dev_flag,list);
				} else if("1".equals(dev_flag)){
					ascii = setid+"01";
					ascii = toAdd(ascii,setid,dev_flag,list);
				}
			}
			if(list.contains(ascii)){//数据库中是否已有生成的指标代号
				ascii = toAdd(ascii,setid,dev_flag,list);
			}
			return ascii;
		} else {
			return "";
		}
	}
	
	/*
	 * 指标代号前三位+1算法　　jingq add 2015.01.27
	 */
	public char[] setCode(char[] list,int index){
		if(index>=0){
			if(list[index]=='9'){
				list[index] = 'A';
			} else if(list[index]=='Z'){
				list[index] = '0';
				list = setCode(list,index-1);
			} else {
				list[index] = (char) (list[index]+1);
			}
		}
		return list;
	}

	
	private static boolean isNumeric(String indexC) {
		for(int i=indexC.length();--i>=0;){
		      int chr=indexC.charAt(i);
		      if(chr<48 || chr>57) {
                  return false;
              }
		   }
		return true;
	}
	
	/*
	 * 日期格式
	 */
	public ArrayList getdate(){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		try {
			da = new CommonData();
			da.setDataName("YYYY.MM.DD HH:MM:SS");
			da.setDataValue("18");
			list.add(da);
			da = new CommonData();
			da.setDataName("YYYY.MM.DD HH:MM");
			da.setDataValue("16");
			list.add(da);
			da = new CommonData();
			da.setDataName("YYYY.MM.DD");
			da.setDataValue("10");
			list.add(da);
			da = new CommonData();
			da.setDataName("YYYY.MM");
			da.setDataValue("7");
			list.add(da);
			da = new CommonData();
			da.setDataName("YYYY");
			da.setDataValue("4");
			list.add(da);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: getIinputtypeMList   
	 * @Description:M型代码输入方式集合
	 * @return List
	 */
	public List getInputtypeMList() {
		List list = new ArrayList();
		try {
			CommonData da = new CommonData();
			// 简单编辑器
			da.setDataName(ResourceFactory.getProperty("kjg.title.input.default"));
			da.setDataValue("0");
			list.add(da);
			da = new CommonData();
			// HTML编辑器
			da.setDataName(ResourceFactory.getProperty("kjg.title.input.html"));
			da.setDataValue("1");
			list.add(da);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * 相关代码类
	 */
	public ArrayList getjoincode(){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		StringBuffer buf = new StringBuffer();
		
		buf.append("select CodeSetId,CodeSetDesc,Maxlength from codeset order by codesetid");
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			RowSet st = dao.search(buf.toString());
			da.setDataName(" ");
			da.setDataValue("#$");
			list.add(da);
			while(st.next()){
				da = new CommonData();
				da.setDataName(st.getString("CodeSetId")+ " " +  st.getString("CodeSetDesc"));
				da.setDataValue(st.getString("CodeSetId"));
				list.add(da);
			}
			da = new CommonData();
			da.setDataName(ResourceFactory.getProperty("kjg.title.newcode"));  //新建代码
			da.setDataValue("xinjian");
			list.add(da);
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/*
	 * 验证指标代码是否存在
	 */
	public boolean checkcode(String indexcode){
		boolean flag = false;
		try
		{
			String sql = "select itemid from fielditem where itemid = '"+indexcode+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
		
	}
	/*
	 * 验证指标名称是否存在
	 */
	public boolean checkname(String indexname){
		boolean flag = false;
		try
		{
			String sql = "select itemdesc from fielditem where itemdesc = '"+indexname+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
		
	}
	/*
	 * 验证指标名称是否存在
	 */
	public boolean checkName(String indexid,String indexname){
		boolean flag = false;
		try
		{
			StringBuffer standItemdesc = new StringBuffer(",");
			if(!"A01".equalsIgnoreCase(indexid)){
				standItemdesc.append(ResourceFactory.getProperty("b0110.label")+",");
				standItemdesc.append(ResourceFactory.getProperty("e01a1.label")+",");
				standItemdesc.append(ResourceFactory.getProperty("e0122.label")+",");
			}
			standItemdesc.append(ResourceFactory.getProperty("h0100.label")+",");
			if(standItemdesc.toString().indexOf(indexname)!=-1)
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
		
	}
	/*
	 * 代码类长度
	 */
	public String getlength(String obj){
		String changdu="";
		String objlength = obj;
		
		try{
			if("@K".equals(objlength)|| "UM".equals(objlength)|| "UN".equals(objlength)){
				changdu="30";
				
			}else if("@@".equals(objlength)){
				changdu="3";
			}else {
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select MAX("+Sql_switcher.length("codeitemid")+") from codeitem where codesetid='"+objlength+"'");
				if(rowSet.next()){
					changdu=rowSet.getString(1);
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return changdu;
		
	}
	/*
	 * displayid字段顺序
	 */
	public int initial(String cDX){
		String ret="";
		int re = 0;
		try{
		String sql = "SELECT MAX(displayid) AS AAA FROM fielditem WHERE fieldsetid= '"+cDX+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		rs= dao.search(sql);
		if (rs.next()) {
            ret = rs.getString(1);
        }
		if(ret==null||ret==""){
			ret="1";
			re = Integer.parseInt(ret);
		}else{
		re = Integer.parseInt(ret)+1;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return re;
		
	}
	/*
	 * 字符类型保存
	 */
	public void wordApp(String indexcode,String indexname,String content,String itemlength,String itemtype,String fieldsetid,int cDX,String bitianxiang){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,itemmemo,reserveitem,displaywidth)");
		sql.append(" values('");
		sql.append(cDX);
		sql.append("','");
		sql.append(fieldsetid);
		sql.append("','");
		sql.append(indexcode);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append("11111111111111111111");
		sql.append("','");
		sql.append(itemtype);
		sql.append("','");
		sql.append(indexname);
		sql.append("','");
		sql.append(itemlength);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(content);
		sql.append("','");
		sql.append(bitianxiang);
		sql.append("','");
		sql.append("14");
		sql.append("')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
			//DataDictionary.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 代码类型保存 codeApp
	 */
	public void codeApp(String indexcode,String indexname,String content,int cDX,String fieldsetid,String joincodename,String codelength,String codeitemtype,String bitianxiang){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,itemmemo,reserveitem,displaywidth)");
		sql.append(" values('");
		sql.append(cDX);
		sql.append("','");
		sql.append(fieldsetid);
		sql.append("','");
		sql.append(indexcode);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append("11111111111111111111");
		sql.append("','");
		sql.append(codeitemtype);
		sql.append("','");
		sql.append(indexname);
		sql.append("','");
		sql.append(codelength);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(joincodename);
		sql.append("','");
		sql.append(content);
		sql.append("','");
		sql.append(bitianxiang);
		sql.append("','");
		sql.append("14");
		sql.append("')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
			//DataDictionary.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 数字类型保存
	 */
	public void dataApp(String indexcode,String indexname,String content,int cDX,String fieldsetid,String numberlength,String decimalwidth,String intitemtype,String bitianxiang){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,itemmemo,reserveitem,displaywidth)");
		sql.append(" values('");
		sql.append(cDX);
		sql.append("','");
		sql.append(fieldsetid);
		sql.append("','");
		sql.append(indexcode);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append("11111111111111111111");
		sql.append("','");
		sql.append(intitemtype);
		sql.append("','");
		sql.append(indexname);
		sql.append("','");
		sql.append(numberlength);
		sql.append("','");
		sql.append(decimalwidth);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(content);
		sql.append("','");
		sql.append(bitianxiang);
		sql.append("','");
		sql.append("14");
		sql.append("')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
			//DataDictionary.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 日期型保存
	 */
	public void dateApp(String indexcode,String indexname,String content,int cDX,String fieldsetid,String datelength,String dateitemtype,String bitianxiang){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,itemmemo,reserveitem,displaywidth)");
		sql.append(" values('");
		sql.append(cDX);
		sql.append("','");
		sql.append(fieldsetid);
		sql.append("','");
		sql.append(indexcode);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append("11111111111111111111");
		sql.append("','");
		sql.append(dateitemtype);
		sql.append("','");
		sql.append(indexname);
		sql.append("','");
		sql.append(datelength);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(content);
		sql.append("','");
		sql.append(bitianxiang);
		sql.append("','");
		sql.append("14");
		sql.append("')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
			//DataDictionary.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 备注型保存
	 */
	public void bzApp(String indexcode,String indexname,String content,int cDX,String fieldsetid,String bzitemtype,String bitianxiang,String inputtype,int itemlen){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into fielditem(displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,itemmemo,reserveitem,displaywidth,inputtype)");
		sql.append(" values('");
		sql.append(cDX);
		sql.append("','");
		sql.append(fieldsetid);
		sql.append("','");
		sql.append(indexcode);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append("11111111111111111111");
		sql.append("','");
		sql.append(bzitemtype);
		sql.append("','");
		sql.append(indexname);
		sql.append("','");
		sql.append(itemlen);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(0);
		sql.append("','");
		sql.append(content);
		sql.append("','");
		sql.append(bitianxiang);
		sql.append("','");
		sql.append("14");
		sql.append("','");
		sql.append(inputtype);
		sql.append("')");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update(sql.toString());
			//DataDictionary.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//修改页面需要的相关代码类值
	public String getCodeStr(ContentDAO dao,String sel) throws GeneralException{
		StringBuffer sbsel=new StringBuffer();
		sbsel.append("<select id=\"code\" name=\"busiFieldVo.string(codesetid)\" onchange=\"getcodelen();\"  style=\"width:150px\">");
		ArrayList codesetlist=this.getCodesetList(dao);
		sbsel.append("<option value=\"\">");
		sbsel.append("");
		sbsel.append("</option>");
		String[] tempsel=sel.split("/");
		for(int i=0;i<codesetlist.size();i++){
			DynaBean dynabean=(DynaBean) codesetlist.get(i);
			String codesetid=(String) dynabean.get("codesetid");
			String codesetdesc=(String) dynabean.get("codesetdesc");
			String len=(String) dynabean.get("codesetlen");
			if(tempsel[0].equalsIgnoreCase(codesetid)){
				sbsel.append("<option value=\""+codesetid+"/"+len+"\" selected=\"selected\">");
			}else{
				sbsel.append("<option value=\""+codesetid+"/"+len+"\">");
			}
			sbsel.append(codesetid+codesetdesc);
			sbsel.append("</option>");
		}
		sbsel.append("<option value=\"newcode\">");
		sbsel.append(ResourceFactory.getProperty("codemaintence.codeset.add")); //新建代码类
		sbsel.append("</option>");
		sbsel.append("</select>");
		return sbsel.toString();
	}
	public ArrayList getCodesetList(ContentDAO dao) throws GeneralException{
		
		String sql="select * from codeset order by codesetid";
		ArrayList codesetlist=(ArrayList) ExecuteSQL.executeMyQuery(sql);
		ArrayList recodesetlist=new ArrayList();
		for(int i=0;i<codesetlist.size();i++){
			DynaBean dynabean=(DynaBean) codesetlist.get(i);
			String codesetid=(String) dynabean.get("codesetid");
			dynabean.set("codesetlen",this.getChildLen(dao,codesetid));
			recodesetlist.add(dynabean);
		}
		return recodesetlist;
	}
	public String getChildLen(ContentDAO dao,String codesetid) throws GeneralException {
		String len = "";
		try {
			if("@K".equals(codesetid)|| "UM".equals(codesetid)|| "UN".equals(codesetid)){
				len="30";
				
			}else{
			/*	switch(Sql_switcher.searchDbServer()){
					case Constant.MSSQL:{  */
 						RowSet rs = dao
							.search("select MAX("+Sql_switcher.length("codeitemid")+") as len from codeitem where codesetid='"
								+ codesetid + "'");
							if (rs.next()) {
									int is = rs.getInt("len");
									len = new Integer(is).toString();
							}
						//break;
			//		}
			/*		case Constant.ORACEL:{
						RowSet rs = dao
							.search("select MAX(length(codeitemid)) as len from codeitem where codesetid='"
								+ codesetid + "'");
							if (rs.next()) {
									int is = rs.getInt("len");
									len = new Integer(is).toString();
							}
						break;
					}
					case Constant.DB2:{
						RowSet rs = dao
						.search("select MAX(length(codeitemid)) as len from codeitem where codesetid='"
							+ codesetid + "'");
						if (rs.next()) {
								int is = rs.getInt("len");
								len = new Integer(is).toString();
						}
					break;
					}
				}*/
			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return len;
	}
	//修改时间
	public String getDateSel(String len){
		StringBuffer sbdate=new StringBuffer();
		sbdate.append("<select name=\"date\" onchange=\"getdatelen();\">");
		if("18".equals(len)){
			sbdate.append("<option value=\"18\" selected=\"selected\">YYYY.MM.DD HH:MM:SS</option>");
		}else{
			sbdate.append("<option value=\"18\">YYYY.MM.DD HH:MM:SS</option>");
		}
		if("16".equals(len)){
			sbdate.append("<option value=\"16\" selected=\"selected\">YYYY.MM.DD HH:MM</option>");
		}else{
			sbdate.append("<option value=\"16\">YYYY.MM.DD HH:MM</option>");
		}
		if("10".equals(len)){
			sbdate.append("<option value=\"10\" selected=\"selected\">YYYY.MM.DD</option>");
		}else{
			sbdate.append("<option value=\"10\">YYYY.MM.DD</option>");
		}
		if("7".equals(len)){
			sbdate.append("<option value=\"7\" selected=\"selected\">YYYY.MM</option>");
		}else{
			sbdate.append("<option value=\"7\">YYYY.MM</option>");
		}
		if("4".equals(len)){
			sbdate.append("<option value=\"4\" selected=\"selected\">YYYY</option>");
		}else{
			sbdate.append("<option value=\"4\">YYYY</option>");
		}
		
		sbdate.append("</select>");

		
		return sbdate.toString();
	}
	//修改指标检查名字
	public boolean checkupname(String indexname,String itemid,String setid){
		boolean flag = false;
		try
		{
			String sql = "select itemdesc from fielditem where itemdesc = '"+indexname+"' and itemid <> '"+itemid+"'";// and fieldsetid='"+setid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
		
	}
	/*
	 * 指标排序
	 * 根据fieldsetid字段取出list
	 */
	public ArrayList sortList(String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String sqlstr = "select itemid,itemdesc from fielditem where fieldsetid='"+fieldsetid+"' order by displayid";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean = (DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("itemid").toString(),
						dynabean.get("itemdesc").toString());
				list.add(dataobj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * 指标集排序
	 */
	public ArrayList sortSet(String infor){
		ArrayList list = new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.conn);
		String sqlstr = "select fieldsetid,customdesc from fieldset where fieldsetid like '"+infor+"%' order by displayorder";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean = (DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("fieldsetid").toString(),
						dynabean.get("customdesc").toString());
				list.add(dataobj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	/*
	 * 验证代码类长度是否为空
	 */
	public boolean codelength(String obj){
		boolean flag = true;
		try{
			if("@@".equals(obj)|| "@K".equals(obj)|| "UM".equals(obj)|| "UN".equals(obj)|| "0".equals(obj)){
				flag = false;
			}else{
				String sql="select * from codeitem where codesetid ='"+obj+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs= dao.search(sql);
				while(rs.next()){
					flag=false;
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}
