package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class TempvarBo {
	/**
	 * 生成midvariable表的主键nid
	 * @return int 主键nid
	 * @throws GeneralException
	 */
	public int getid(Connection conn){
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select max(nid) as nid from midvariable";
		ArrayList dylist = null;
		int n=0;
		try {
			dylist = dao.searchDynaList(sqlstr);
			if(dylist!=null&&dylist.size()>0){
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					n=Integer.parseInt(dynabean.get("nid").toString().length()==0?"0":dynabean.get("nid").toString())+1;
				}
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 根据nid获取midvariable表的公式
	 * @return String 公式
	 * @throws GeneralException
	 */
	public String cValue(Connection conn,String nid){
		String cvalue="";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select cvalue from midvariable where nid="+nid+"";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				cvalue=dynabean.get("cvalue").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cvalue;
	}
	/**
	 * 根据codesetid获取codesetdesc
	 * @return String 代码翻译成汉字
	 * @throws GeneralException
	 */
	public String codeTozh(Connection conn,String codesetid){
		String codesetdesc="";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select codesetdesc from codeset where codesetid='"+codesetid+"'";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				codesetdesc=dynabean.get("codesetdesc").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(codesetid!=null&&codesetid.trim().length()>0){
			return codesetdesc;
		}else{
			return "";
		}
		
	}
	/**
	 * 根据cstate字段获取List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList sortList(Connection conn,String cstate,String type,String nflag){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		if(!"3".equals(type)){
			sqlstr = "select nId,cHz from midvariable where TempletID=0 and (cState='"+cstate+"' or cState is null) and nFlag="+nflag+" order by sorting";
		}else{
			//sqlstr = "select nId,cHz from midvariable where TempletID='"+cstate+"' and nFlag="+nflag+" order by sorting";
			sqlstr = "select nId,cHz from midvariable where TempletID<>0 and (TempletID='"+cstate+"' or cstate = '1') and nFlag="+nflag+" order by sorting";
		}
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("nid").toString(),
						dynabean.get("chz").toString());
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据templetid字段获取List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList tempvarList(Connection conn,String templetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select nId,cHz from midvariable where TempletID='"+templetid+"' order by sorting";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("nid").toString()+":"+dynabean.get("chz").toString(),
						dynabean.get("chz").toString());
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 从临时变量中取得对应指标列表（人事异动）
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(Connection conn,String tabid){
		ArrayList fieldlist=new ArrayList();
		RowSet rset = null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetId <> 0 and (templetid=");
			buf.append(tabid);
			buf.append(" or cstate = '1')");
			//buf.append(" order by sorting");
			ContentDAO dao=new ContentDAO(conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return fieldlist;
	}
	
	
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList fieldList(Connection conn){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag=1";
		ArrayList dylist = null;
		CommonData obj1=new CommonData("","");
		list.add(obj1);
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String fieldsetid = dynabean.get("fieldsetid").toString();
				String customdesc = dynabean.get("customdesc").toString();
				CommonData dataobj = new CommonData(fieldsetid,fieldsetid+":"+customdesc);
				list.add(dataobj);
			}
			CommonData dataobj = new CommonData("tempvar","临时变量集");
			list.add(dataobj);
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		CommonData dataobj = new CommonData("","");
//		list.add(0,dataobj);
		return list;
	}
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList fieldListTemp(UserView userView,String nflag){//当nflag为4时，只需要单位信息集
		ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		
		ArrayList listset = new ArrayList();
		if(!"4".equals(nflag))
			listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET));
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET));
		if(!"4".equals(nflag))
			listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET));
			
		for(int i=0;i<listset.size();i++){
			 FieldSet fieldset = (FieldSet)listset.get(i);
			 if(fieldset==null)
				 continue;
			 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }
			 if(userView.analyseTablePriv(fieldset.getFieldsetid())==null)
				 continue;
			 if("0".equals(userView.analyseTablePriv(fieldset.getFieldsetid())))
				 continue;
			 
			 CommonData obj=new CommonData(fieldset.getFieldsetid()
						,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
			 fieldsetlist.add(obj);
		}
		/**增加临时变量可以在下拉列表中显示xcs**/
		fieldsetlist.add(new CommonData("tempvar","临时变量"));
		return fieldsetlist;
	}
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList subsetFieldList(Connection conn){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select fieldsetid,fieldsetdesc from fieldset where useflag=1";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String fieldsetid = dynabean.get("fieldsetid").toString();
				String fieldsetdesc = dynabean.get("fieldsetdesc").toString();
				if(!("a01".equalsIgnoreCase(fieldsetid)))
				{
					CommonData dataobj = new CommonData(fieldsetid,fieldsetid+":"+fieldsetdesc);
					list.add(dataobj);
				}
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList fieldList(UserView uv,String infor){
		ArrayList list= new ArrayList();
		/**表授权*/
		if("1".equals(infor)){
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			list.addAll(uv.getPrivFieldSetList(Constant.UNIT_FIELD_SET));//xuj add 2010-8-12
			list.addAll(uv.getPrivFieldSetList(Constant.POS_FIELD_SET));
		}else if("2".equals(infor))
			list=uv.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		else if("3".equals(infor))
			list=uv.getPrivFieldSetList(Constant.POS_FIELD_SET);
		else if("4".equals(infor))
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("5".equals(infor)){
			FieldSet fieldset= DataDictionary.getFieldSetVo("r45");
			fieldset.setUseflag("1");
			list.add(fieldset);
		}
		ArrayList setlist=new ArrayList();	
		for(int i=0;i<list.size();i++)
		{
			FieldSet fieldset=(FieldSet)list.get(i);
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
//			liwc 业务指标不存在授权
//			if(uv.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
//				continue;
			CommonData temp=new CommonData(fieldset.getFieldsetid(),
					fieldset.getFieldsetid()+":"+fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end
		return setlist;
	}
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList sortFieldList(UserView uv,String infor){
		ArrayList list= new ArrayList();
		/**表授权*/
		if("1".equals(infor))
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("2".equals(infor))
			list=uv.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		else if("3".equals(infor))
			list=uv.getPrivFieldSetList(Constant.POS_FIELD_SET);
		else if("4".equals(infor))
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("6".equals(infor))//liuy 2015-1-30 7246：组织机构-岗位管理-基准岗位-高级花名册-取数-下一步-勾选排序指标-选择排序指标（没有列出基准岗位子集和指标）
			list=uv.getPrivFieldSetList(Constant.JOB_FIELD_SET);			
		else if("5".equals(infor)){
			FieldSet fieldset= DataDictionary.getFieldSetVo("r45");
			fieldset.setUseflag("1");
			list.add(fieldset);
		}
		ArrayList setlist=new ArrayList();	
		for(int i=0;i<list.size();i++)
		{
			FieldSet fieldset=(FieldSet)list.get(i);
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData temp=new CommonData(fieldset.getFieldsetid(),
					fieldset.getFieldsetid()+":"+fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end
		return setlist;
	}
	
	public ArrayList sortFieldList1(UserView uv,String infor){
		ArrayList list= new ArrayList();
		/**表授权*/
		if("1".equals(infor))
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("2".equals(infor))
			list=uv.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		else if("3".equals(infor))
			list=uv.getPrivFieldSetList(Constant.POS_FIELD_SET);
		else if("4".equals(infor))
			list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("5".equals(infor)){
			FieldSet fieldset= DataDictionary.getFieldSetVo("r45");
			fieldset.setUseflag("1");
			list.add(fieldset);
		}
		ArrayList setlist=new ArrayList();	
		for(int i=0;i<list.size();i++)
		{
			FieldSet fieldset=(FieldSet)list.get(i);
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData temp=new CommonData(fieldset.getFieldsetid(),
					fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end
		return setlist;
	}
	
	/**
	 * 获取以构库的子集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList fieldList(UserView uv){
		ArrayList list= new ArrayList();
		/**表授权*/
		list=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		ArrayList setlist=new ArrayList();	
		for(int i=0;i<list.size();i++)
		{
			FieldSet fieldset=(FieldSet)list.get(i);
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end
		return setlist;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList(String fieldsetid){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList(String fieldsetid,UserView uv){
        return  getItemList(fieldsetid,uv,"0");
	}
	/**
	 * 根据子集获取子标
	 * @param fieldsetid 表名
	 * @param uv  用户
	 * @param flag 模块调用标识
	 * @return
	 */
	public ArrayList getItemList(String fieldsetid,UserView uv,String flag){
	    flag = flag!=null&&flag.length()>0?flag:"0";
		ArrayList list = new ArrayList();
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
//				if(uv.analyseFieldPriv(itemid).equals("0"))
//					continue;
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				/*
				 * 【6127】自助服务/员工信息维护，计算，公式，选择指标，指标没有经过权限控制，不对。
				 * 修改为有读或写权限的指标才能显示     jingq add 2014.12.18
				 * r45 培训费用表培训 模块的指标不需要权限控制        chenxg  add 2014-01-05
				 */
				if(!"r45".equalsIgnoreCase(fieldsetid) && "0".equalsIgnoreCase(uv.analyseFieldPriv(itemid))){
					continue;
				}
				
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList(String fieldsetid,UserView uv,String type){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if(type!=null&&type.equalsIgnoreCase(fielditem.getItemtype())){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
						list.add(dataobj);
					}
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList1(String fieldsetid,UserView uv){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList1(String fieldsetid,UserView uv,String type){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			//dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if(type!=null&&type.equalsIgnoreCase(fielditem.getItemtype())){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
						list.add(dataobj);
					}
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList itemList2(String fieldsetid,UserView uv){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			//dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
//				if(uv.analyseFieldPriv(itemid,0).equals("1")||uv.analyseFieldPriv(itemid,1).equals("1"))
//					continue;
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemList2(String fieldsetid,UserView uv,String field,String field1){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			//dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				
				String itemid = fielditem.getItemid();
				if(itemid.equalsIgnoreCase(field)||itemid.equalsIgnoreCase(field1)){
					continue;
				}
				String itemdesc = fielditem.getItemdesc();
//				if(uv.analyseFieldPriv(itemid,0).equals("1")||uv.analyseFieldPriv(itemid,1).equals("1"))
//					continue;
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemList3(String fieldsetid,UserView uv){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		if(fieldsetid!=null&&fieldsetid.length()>0){
			//dylist = uv.getPrivFieldList(fieldsetid,Constant.USED_FIELD_SET);
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		try {
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
//				if(uv.analyseFieldPriv(itemid,0).equals("1")||uv.analyseFieldPriv(itemid,1).equals("1"))
//					continue;
				/*if("1".equalsIgnoreCase(uv.analyseFieldPriv(itemid))){//读权限
					continue;
				}*/
				/*	【6127】自助服务/员工信息维护，计算，点开后，新增计算公式，选择指标，指标没有经过权限控制，不对。
				 *	 修改为指标有读或写权限才显示    jingq upd 2014.12.18
				 */
				if("0".equalsIgnoreCase(uv.analyseFieldPriv(itemid))){
					continue;
				}
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子标获取代码
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeList(Connection conn,String itemid){
		ArrayList list = new ArrayList();
		
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}
		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		String codesetid ="";
		if(fielditem==null){
		    fielditem=getMidVariableList(conn,itemid,"0");// getMidVariableList(conn);
		/*
		    for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(itemid.equalsIgnoreCase(item.getItemid())){
					fielditem=item;
					break;
				}
			}*/
			
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' and invalid=1");
							if(AdminCode.isRecHistoryCode(codesetid)){//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
								String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
								sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
							}
							sqlstr.append(" order by a0000");
							
							/*
							// 关联代码类 JinChunhai 2012.07.23
							sqlstr.append("select codesetid as codeitemid,codesetdesc as codeitemdesc from codeset "); 
							sqlstr.append(" where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' ");
							*/
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(conn);

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
//							String codeitemid = SafeCode.encode(dynabean.get("codeitemid").toString());
//							String codeitemdesc = SafeCode.encode(dynabean.get("codeitemdesc").toString());
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
							list.add(dataobj);
						}
						CommonData dataobj = new CommonData("","");
						list.add(0,dataobj);

					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
				if("escope".equals(itemid)){
					dataobj = new CommonData("1","1"+":"+"离休人员");
					list.add(dataobj);
					dataobj = new CommonData("2","2"+":"+"退休人员");
					list.add(dataobj);
					dataobj = new CommonData("3","3"+":"+"内退人员");
					list.add(dataobj);
					dataobj = new CommonData("4","4"+":"+"遗嘱");
					list.add(dataobj);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	* <p>Description: 人事异动-计算公式-联动代码型下拉框（根据代码id：a0121获得代码）</p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-16 下午05:56:29
	 */
	public ArrayList codeListFormula(Connection conn,String itemid){
		ArrayList list = new ArrayList();
		HashMap<String,String> dataobj = new HashMap<String,String>();
		if(itemid==null||itemid.length()<1){
			//CommonData dataobj = new CommonData("","");
			dataobj.put("id", "");
			dataobj.put("name", "");
			list.add(dataobj);
			return list;
		}
		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		String codesetid ="";
		if(fielditem==null){
		    fielditem=getMidVariableList(conn,itemid,"0");// getMidVariableList(conn);
		/*
		    for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(itemid.equalsIgnoreCase(item.getItemid())){
					fielditem=item;
					break;
				}
			}*/
			
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' and invalid=1");
							if(AdminCode.isRecHistoryCode(codesetid)){//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
								String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
								sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
							}
							//29443 为什么按a0000排序，这里造成乱序，先注释掉
//							sqlstr.append(" order by a0000");
							
							/*
							// 关联代码类 JinChunhai 2012.07.23
							sqlstr.append("select codesetid as codeitemid,codesetdesc as codeitemdesc from codeset "); 
							sqlstr.append(" where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' ");
							*/
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(conn);

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
//							String codeitemid = SafeCode.encode(dynabean.get("codeitemid").toString());
//							String codeitemdesc = SafeCode.encode(dynabean.get("codeitemdesc").toString());
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							//CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
							dataobj = new HashMap<String,String>();
							dataobj.put("id", codeitemid);
							dataobj.put("name", codeitemid+":"+codeitemdesc);
							list.add(dataobj);
						}
						//CommonData dataobj = new CommonData("","");
						if(list.size()==0){
							dataobj = new HashMap<String,String>();
							dataobj.put("id", "");
							dataobj.put("name", "");
							list.add(0,dataobj);
						}
					}else{
						//CommonData dataobj = new CommonData("","");
						dataobj = new HashMap<String,String>();
						dataobj.put("id", "");
						dataobj.put("name", "");
						list.add(dataobj);
					}
				}else{
					//CommonData dataobj = new CommonData("","");
					dataobj = new HashMap<String,String>();
					dataobj.put("id", "");
					dataobj.put("name", "");
					list.add(dataobj);
				}
			}else{
				
				//CommonData dataobj = new CommonData("","");
				dataobj = new HashMap<String,String>();
				if("escope".equals(itemid)){
					dataobj = new HashMap<String,String>();
					dataobj.put("id", "1");
					dataobj.put("name", "1"+":"+"离休人员");
					//dataobj = new CommonData("1","1"+":"+"离休人员");
					list.add(dataobj);
					//dataobj = new CommonData("2","2"+":"+"退休人员");
					dataobj = new HashMap<String,String>();
					dataobj.put("id", "2");
					dataobj.put("name", "2"+":"+"退休人员");
					list.add(dataobj);
					//dataobj = new CommonData("3","3"+":"+"内退人员");
					dataobj = new HashMap<String,String>();
					dataobj.put("id", "3");
					dataobj.put("name", "3"+":"+"内退人员");
					list.add(dataobj);
					//dataobj = new CommonData("4","4"+":"+"遗嘱");
					dataobj = new HashMap<String,String>();
					dataobj.put("id", "4");
					dataobj.put("name", "4"+":"+"遗嘱");
					list.add(dataobj);
				}else{
					dataobj.put("id", "");
					dataobj.put("name", "");
					list.add(dataobj);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据子标获取浮动条标题
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList titleList(Connection conn,String itemid){
		ArrayList list = new ArrayList();
		
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}
		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		StringBuffer str_value=new StringBuffer();
		String codesetid ="";
		if(fielditem==null){
		
		    fielditem=getMidVariableList(conn,itemid,"0");// getMidVariableList(conn);
	        /*
		    ArrayList fieldlist = getMidVariableList(conn);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(itemid.equalsIgnoreCase(item.getItemid())){
					fielditem=item;
					break;
				}
			}*/
			
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
							
							/*
							// 关联代码类 JinChunhai 2012.07.23
							sqlstr.append("select codesetid as codeitemid,codesetdesc as codeitemdesc from codeset "); 
							sqlstr.append(" where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' ");
							*/
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(conn);

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							str_value.append("`"+codeitemid+"~"+codeitemdesc+"");
							CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
							list.add(dataobj);
						}
						CommonData dataobj = new CommonData("","");
						list.add(0,dataobj);

					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
				if("escope".equals(itemid)){
					dataobj = new CommonData("1","1"+":"+"离休人员");
					list.add(dataobj);
					dataobj = new CommonData("2","2"+":"+"退休人员");
					list.add(dataobj);
					dataobj = new CommonData("3","3"+":"+"内退人员");
					list.add(dataobj);
					dataobj = new CommonData("4","4"+":"+"遗嘱");
					list.add(dataobj);
				}
			}
			if(str_value.length()>0&&str_value.length()<10000){
				list.add(SafeCode.encode(str_value.substring(1)));
			}
			
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据临时变量id获取代码
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeTempList(Connection conn,String itemid){
		ArrayList list = new ArrayList();
		
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer bufsql = new StringBuffer();
		bufsql.append("select codesetid from midvariable where ");
		if(itemid.indexOf("yk")!=-1){
			bufsql.append(" cName='"+itemid+"'");
		}else if(itemid.indexOf("YK")!=-1){
			bufsql.append(" cName='"+itemid+"'");
		}else{
			bufsql.append(" nid='"+itemid+"'");
		}
		try {
			ArrayList relist = dao.searchDynaList(bufsql.toString());
			String codesetid = "";
			for(Iterator it=relist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				codesetid = dynabean.get("codesetid").toString();
			}
			if(codesetid!=null||codesetid.trim().length()>0){
				StringBuffer sqlstr = new StringBuffer();
				if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
					sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
					sqlstr.append(codesetid);
					sqlstr.append("' order by codeitemid");
				}else if("@@".equalsIgnoreCase(codesetid)){
					sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
				}else
				{
					
					sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
					sqlstr.append(codesetid);
					sqlstr.append("' and invalid=1");
					if(AdminCode.isRecHistoryCode(codesetid)){//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
							String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
							sqlstr.append(" and " +Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
					}
					sqlstr.append(" order by codeitemid");
					
					/*
					// 关联代码类 JinChunhai 2012.07.23
					sqlstr.append("select codesetid as codeitemid,codesetdesc as codeitemdesc from codeset "); 
					sqlstr.append(" where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' ");
					*/
				}
				ArrayList dylist = null;
				
				dylist = dao.searchDynaList(sqlstr.toString());
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String codeitemid = dynabean.get("codeitemid").toString();
					String codeitemdesc = dynabean.get("codeitemdesc").toString();
					CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
					list.add(dataobj);
				}
				CommonData dataobj = new CommonData("","");
				list.add(0,dataobj);
				
			}else{
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(Connection conn){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select nid,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 ");
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("nid"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	
	
	/**
     * 从临时变量中取得对应指标列表
     * @return FieldItem对象列表
     * @throws GeneralException
     */
    public FieldItem getMidVariableList(Connection conn,String itemid,String nflag){
        FieldItem item=null;
        try{
            StringBuffer buf=new StringBuffer();
            buf.append("select nid,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
            buf.append(" midvariable where nflag="+nflag+"  and  ");
            Pattern pattern = Pattern.compile("[0-9]+");
            if(pattern.matcher(itemid.trim()).matches()) //整型 用nid
            {
                buf.append(" nid="+itemid);
            }
            else
                buf.append(" cname='"+itemid+"'"); 
            ContentDAO dao=new ContentDAO(conn);
            RowSet rset=dao.search(buf.toString());
            if(rset.next())
            {
                item=new FieldItem();
                item.setItemid(rset.getString("nid"));
                item.setFieldsetid("A01");//没有实际含义
                item.setItemdesc(rset.getString("chz"));
                item.setItemlength(rset.getInt("fldlen"));
                item.setDecimalwidth(rset.getInt("flddec"));
                item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
                switch(rset.getInt("ntype"))
                {
                case 1://
                    item.setItemtype("N");
                    item.setCodesetid("0");
                    break;
                case 2:
                    item.setItemtype("A");
                    item.setCodesetid("0");
                    break;
                case 3:
                    item.setItemtype("D");
                    item.setCodesetid("0");
                    break;
                case 4:
                    item.setItemtype("A");
                    item.setCodesetid(rset.getString("codesetid"));
                    break;
                }
                item.setVarible(1);
                
            }// while loop end.
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return item;
    }
	
	
	/**
	 * 取得“代码转名称2”这个公式的下拉列表
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeListForFormula(String itemid){
		ArrayList list = new ArrayList();
		CommonData dataobj = new CommonData("","");
		list.add(dataobj);
		if(itemid!=null && itemid.length()>0){
			FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
			String codesetid ="";
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				dataobj = new CommonData(codesetid,codesetid);
				list.add(dataobj);
			}
		}	
		return list;
	}
}
