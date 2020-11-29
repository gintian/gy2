package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class TotalLookTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String model=(String)this.getFormHM().get("model");  //1:综合评定(总分排名)  2：查询使用
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String treeCode = (String)hm.get("a_code");
		treeCode=treeCode!=null?treeCode:"";
		hm.remove("a_code");
		
		String setid = (String)this.getFormHM().get("setid");
		setid=setid!=null?setid:"";
		
		ArrayList dblist = dbList();
		String dbname = (String)this.getFormHM().get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		if(dbname.length()<1&&dblist.size()>0){
			CommonData temp=(CommonData)dblist.get(0);
			if(temp!=null)
				dbname = temp.getDataValue();
		}

		StringBuffer sqlstr = new StringBuffer();
		StringBuffer wherestr = new StringBuffer();
		StringBuffer column = new StringBuffer();
		StringBuffer orderby = new StringBuffer();

		ArrayList fielditemlist=getFieldList(setid);
		String maintable=dbname+"A01";
		String itemtable=dbname+setid;
		if(setid==null||setid.trim().length()<=0)
		{
		//	return;
			itemtable=dbname+"A01";
		}
		String fields=getFields(fielditemlist, maintable,model);
		sqlstr.append("select ");
		sqlstr.append(fields);
		wherestr.append(" from ");
		wherestr.append(itemtable);
		wherestr.append(" a right join ");
		wherestr.append(maintable);
		wherestr.append(" on ");
		wherestr.append(maintable);
		wherestr.append(".A0100=");
		wherestr.append(" a");
		wherestr.append(".A0100 ");
		
		String timeitemid=(String)this.getFormHM().get("timeitemid");
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"no";
		timeitemid=timeitemid!=null&&timeitemid.trim().length()>0?timeitemid:"";
		
		String fieldid = (String)this.getFormHM().get("fieldid");
		fieldid=fieldid!=null&&fieldid.trim().length()>0?fieldid:"";
		
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=sortitem!=null?sortitem:"";
		sortitem=SafeCode.decode(sortitem);
		
		String sortid = (String)this.getFormHM().get("sortid");
		sortid=sortid!=null&&sortid.trim().length()>0?sortid:"1";
		
		String highsearch = (String)this.getFormHM().get("highsearch");
		highsearch=highsearch!=null&&highsearch.trim().length()>0?highsearch:"";
		

		String search = (String)hm.get("search");
		search=search!=null?search:"";
		hm.remove("search");
		
		if(sortitem.trim().length()>5)
			fieldid="no";
		
		if("all".equalsIgnoreCase(search))
		{
			itemid="no";
			timeitemid="";
			highsearch="";
		}
		
		this.getFormHM().put("highsearch", highsearch);
		column.append(fields);
		ArrayList itemList = new ArrayList();
		CommonData temp=new CommonData("no","");
		itemList.add(temp);
		boolean checkfield = false;
		boolean checkitem = false;
		
		
		
		ArrayList timeFieldList=new ArrayList();
		timeFieldList.add(new CommonData("no",""));
		ArrayList list=this.userView.getPrivFieldList(setid,Constant.USED_FIELD_SET);
		if(list!=null)
		{
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)list.get(i);
				if(fielditem!=null){
					if(fielditem.getItemid().equalsIgnoreCase(fieldid)){
						checkfield = true;
					}
					if(fielditem.getItemid().equalsIgnoreCase(itemid)){
						checkitem = true;
					}
					if("M".equalsIgnoreCase(fielditem.getItemtype()))
						continue;
					
					if("2".equalsIgnoreCase(model)&& "D".equalsIgnoreCase(fielditem.getItemtype()))
					{
						timeFieldList.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
					}
					
					temp=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					itemList.add(temp);
				}
			}
		}
		if("2".equalsIgnoreCase(model))  //如果为查询使用模块  需加上主集代码型指标
		{
			ArrayList listitem=this.userView.getPrivFieldList("A01",Constant.USED_FIELD_SET);
			for(int i=0;i<listitem.size();i++){
				FieldItem fielditem = (FieldItem)listitem.get(i);
				if(fielditem!=null){
					if(fielditem.getItemid().equalsIgnoreCase(fieldid)){
						checkfield = true;
					}
					if(fielditem.getItemid().equalsIgnoreCase(itemid)){
						checkitem = true;
					}
					if("M".equalsIgnoreCase(fielditem.getItemtype()))
						continue;
					temp=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					itemList.add(temp);
				}
			}
		}
		if("2".equalsIgnoreCase(model))
			this.getFormHM().put("timeFieldList",timeFieldList);
		
		
		
		if(fieldid.trim().length()>0&&!"no".equalsIgnoreCase(fieldid)&&checkfield){
			
			if("1".equals(model)||timeitemid.trim().length()<1|| "no".equalsIgnoreCase(timeitemid))
			{
				FieldItem item=DataDictionary.getFieldItem(fieldid.toLowerCase());
				if("0".equals(sortid)){
					if(item.isMainSet())
						orderby.append(" order by "+maintable+"."+fieldid);
					else
						orderby.append(" order by a."+fieldid);
					orderby.append(" asc ");
				}else if("1".equals(sortid)){
					if(item.isMainSet())
						orderby.append(" order by "+maintable+"."+fieldid);
					else
						orderby.append(" order by a."+fieldid);
					orderby.append(" desc");
				}else{
					orderby.append(" order by "+maintable+".A0000");
				}
			}else{
				FieldItem item=DataDictionary.getFieldItem(fieldid.toLowerCase());
				String target="a1";
				if(item!=null&& "N".equalsIgnoreCase(item.getItemtype()))
					target="a2";
				if("0".equals(sortid)){
					orderby.append(" order by "+target+"."+fieldid);
					orderby.append(" asc ");
				}else if("1".equals(sortid)){
					orderby.append(" order by "+target+"."+fieldid);
					orderby.append(" desc");
				}else{
					orderby.append(" order by a1.A0000");
				}
			}
		}else{
			if("1".equals(model)||timeitemid.trim().length()<1|| "no".equalsIgnoreCase(timeitemid)){
				if("1".equals(model)){
					if(checkMainItem(sortitem))
						sortitem="";
				}
				if(checkMainItem(sortitem,setid))
					sortitem="";
				if(sortitem.trim().length()>5){
					orderby.append(" order by ");
					String sortArr[] = sortitem.split("`");
					for(int i=0;i<sortArr.length;i++){
						if(sortArr[i]!=null){
							String itemArr[] = sortArr[i].split(":");
							if(itemArr.length==3){
								FieldItem item=DataDictionary.getFieldItem(itemArr[0].toLowerCase());
								if(item.isMainSet())
									orderby.append(maintable+".");
								else
									orderby.append("a.");
								if("0".equals(itemArr[2])){
									orderby.append(itemArr[0]);
									orderby.append(" asc,");
								}else if("1".equals(itemArr[2])){
									orderby.append(itemArr[0]);
									orderby.append(" desc,");
								}
							}
						}
					}
					String orderbyaa=orderby.substring(0,orderby.length()-1);
					orderby.setLength(0);
					orderby.append(orderbyaa);
				}else
					orderby.append(" order by "+maintable+".A0000");
			}else{
				if(checkMainItem(sortitem,setid))
					sortitem="";
				if(sortitem.trim().length()>0){
					if(sortitem.trim().length()>5){
						orderby.append(" order by ");
						String sortArr[] = sortitem.split("`");
						for(int i=0;i<sortArr.length;i++){
							if(sortArr[i]!=null){
								String itemArr[] = sortArr[i].split(":");
								if(itemArr.length==3){
									FieldItem item=DataDictionary.getFieldItem(itemArr[0].toLowerCase());
									String target="a1";
									if(item!=null&& "N".equalsIgnoreCase(item.getItemtype()))
										target="a2";
									if("0".equals(itemArr[2])){
										orderby.append(target+"."+itemArr[0]);
										orderby.append(" asc,");
									}else if("1".equals(itemArr[2])){
										orderby.append(target+"."+itemArr[0]);
										orderby.append(" desc,");
									}
								}
							}
						}
						String orderbyaa=orderby.substring(0,orderby.length()-1);
						orderby.setLength(0);
						orderby.append(orderbyaa);
					}
				}else
					orderby.append(" order by a1.A0000");
			}
		}
		
		String where = whereStr(dbname,treeCode);
		if(where!=null&&where.trim().length()>0){
			wherestr.append(" where a.A0100 in(select A0100 ");
			wherestr.append(where+")");
		}
		if(highsearch.trim().length()>1){
			highsearch=SafeCode.decode(highsearch);
			if(where!=null&&where.trim().length()>5){
				wherestr.append(" and a.A0100 in(select ");
				wherestr.append(dbname+"A01.A0100 ");
				wherestr.append(highsearch);
				wherestr.append(")");
			}else{
				wherestr.append(" where a.A0100 in(select ");
				wherestr.append(dbname+"A01.A0100 ");
				wherestr.append(highsearch);
				wherestr.append(")");
			}
		}
		
		
		String codeid = "";
		String fromnum = "";
		String tonum = "";
		String fromdate = "";
		String todate = "";
		String searchtext = "";
		if(itemid!=null&&itemid.trim().length()>0&&!"no".equalsIgnoreCase(itemid)&&checkitem){
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			if(fielditem!=null){
				if("A".equalsIgnoreCase(fielditem.getItemtype())){
					if(!fielditem.isCode()){
						searchtext = (String)this.getFormHM().get("searchtext");
						searchtext=searchtext!=null?searchtext:"";
						if(searchtext.length()>0){
							if(wherestr.indexOf("where")!=-1)
								wherestr.append(" and ");
							else
								wherestr.append(" where ");
							wherestr.append(fielditem.getItemid()+" like '");
							wherestr.append(searchtext+"%'");
						}
						
					}else{
						 codeid = (String)this.getFormHM().get("codeid");
						 codeid=codeid!=null&&codeid.trim().length()>0?codeid:"";
						 if(codeid.length()>0){
							 if(wherestr.indexOf("where")!=-1)
								 wherestr.append(" and ");
							 else
								 wherestr.append(" where ");
							 wherestr.append(fielditem.getItemid()+"='");
							 wherestr.append(codeid+"'");
						 }
					}
				}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
					fromdate = (String)this.getFormHM().get("fromdate");
					fromdate=fromdate!=null?fromdate:"";
					
					todate = (String)this.getFormHM().get("todate");
					todate=todate!=null?todate:"";
					if(wherestr.indexOf("where")!=-1)
						wherestr.append(" and ");
					else
						wherestr.append(" where ");
					wherestr.append(fielditem.getItemid());
					wherestr.append(" BETWEEN ");
					wherestr.append(Sql_switcher.dateValue(fromdate+" 00:00:00"));
					wherestr.append(" AND "+Sql_switcher.dateValue(todate+" 23:59:59"));
				}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
					fromnum = (String)this.getFormHM().get("fromnum");
					fromnum=fromnum!=null?fromnum:"0";
					
					tonum = (String)this.getFormHM().get("tonum");
					tonum=tonum!=null?tonum:"0";
					if(wherestr.indexOf("where")!=-1)
						wherestr.append(" and ");
					else
						wherestr.append(" where ");
					 wherestr.append(fielditem.getItemid()+" BETWEEN '"+fromnum+"' AND '"+tonum+"'");
				}
			}
		}
		this.getFormHM().put("codeid", codeid);
		this.getFormHM().put("fromnum", fromnum);
		this.getFormHM().put("tonum", tonum);
		this.getFormHM().put("fromdate", fromdate);
		this.getFormHM().put("todate", todate);
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("timeitemid",timeitemid);
		
		
		this.getFormHM().put("fieldid", fieldid);
		this.getFormHM().put("sortList", sortList());
		this.getFormHM().put("itemList", itemList);
		this.getFormHM().put("sortid", sortid);
		this.getFormHM().put("dblist", dblist);
		this.getFormHM().put("sortitem",sortitem);
		
		if("2".equals(model)&&timeitemid.trim().length()>0&&!"no".equalsIgnoreCase(timeitemid))
		{
			String sql_str=getSql_str(maintable,fielditemlist);
			String sql_where=getSql_whl(maintable,fielditemlist,sqlstr.toString(),wherestr.toString(),timeitemid);
			sqlstr.setLength(0);
			wherestr.setLength(0);
			sqlstr.append(sql_str);
			wherestr.append(sql_where);
		}else
		{
			this.getFormHM().put("fromScope", "");
			this.getFormHM().put("toScope","");
		}
	/*	sqlstr.setLength(0);
		sqlstr.append("select a1.A0100,b0110,e0122,e01a1,a0101,i9999,c0401,a0405,a0440,a0420,a0410,a0415,a0430,a0435,a2.a0425,a0445,a0455,a0444 ");
		wherestr.setLength(0);
		wherestr.append(" from (select * from (select UsrA01.A0100,b0110,e0122,e01a1,a0101,i9999,c0401,a0405,a0440,a0420,a0410,a0415,a0430,a0435,a0425,a0445,a0455,a0444 from UsrA04 a right join UsrA01 on UsrA01.A0100= a.A0100 ");
		wherestr.append("  where a.A0100 in(select A0100  FROM UsrA01 WHERE (((((UsrA01.B0110 IS NULL OR UsrA01.B0110='') OR (UsrA01.B0110 LIKE '%')))))) and e0122='010101' ");
	    wherestr.append(" ) aa where aa.i9999=(select max(bb.i9999) from  ");
		wherestr.append(" ( ");
		wherestr.append(" select UsrA01.A0100,b0110,e0122,e01a1,a0101,i9999,c0401,a0405,a0440,a0420,a0410,a0415,a0430,a0435,a0425,a0445,a0455,a0444 from UsrA04 a right join UsrA01 on UsrA01.A0100= a.A0100  ");
		wherestr.append("  where a.A0100 in(select A0100  FROM UsrA01 WHERE (((((UsrA01.B0110 IS NULL OR UsrA01.B0110='') OR (UsrA01.B0110 LIKE '%')))))) and e0122='010101' ");
		wherestr.append(" ) bb where aa.a0100=bb.a0100 ) ");
		wherestr.append(" ) a1, ");
		wherestr.append(" ( ");
		wherestr.append(" select UsrA01.A0100 ,sum(isnull(a0425,0)) a0425 ");
		wherestr.append("  from UsrA04 a right join UsrA01 on UsrA01.A0100= a.A0100  ");
		wherestr.append(" 		 where a.A0100 in(select A0100  FROM UsrA01 WHERE (((((UsrA01.B0110 IS NULL OR UsrA01.B0110='') OR (UsrA01.B0110 LIKE '%')))))) and e0122='010101' ");
		wherestr.append(" 		 group by UsrA01.a0100 ");
		wherestr.append(" 		) a2 where a1.a0100=a2.a0100 ");
		*/
		this.getFormHM().put("sqlstr", sqlstr.toString());
		this.getFormHM().put("wherestr", wherestr.toString());
		this.getFormHM().put("column", column.toString());
		this.getFormHM().put("orderby", orderby.toString());
		StringBuffer excelsql = new StringBuffer();
		excelsql.append(sqlstr.toString());
		excelsql.append(" ");
		excelsql.append(wherestr.toString());
		excelsql.append(" ");
		excelsql.append(orderby.toString());
		this.getFormHM().put("ecxelsql",SafeCode.encode(excelsql.toString()));
		this.getFormHM().put("fieldList", fielditemlist);
		this.getFormHM().put("tablename",itemtable);
		this.getFormHM().put("treeCode", treeCode);
	}
	private ArrayList sortList(){
		ArrayList sortlist = new ArrayList();
		CommonData temp=new CommonData("1","降序");
		sortlist.add(temp);
		temp=new CommonData("0","升序");
		sortlist.add(temp);
		return sortlist;
	}
	public String getSql_whl(String maintable,ArrayList list,String sqlstr,String whl,String scopeitemid)
	{
		StringBuffer str=new StringBuffer("");
		str.append(" from (select * from ( "+sqlstr+whl+" ) aa where aa.i9999=(select max(bb.i9999) from ("+sqlstr+whl+") bb where  aa.a0100=bb.a0100 ) ) a1,");
		str.append(" ( select "+maintable+".A0100 ");
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if(field.getDatatype()==3||field.getDatatype()==4||field.getDatatype()==5||field.getDatatype()==6||field.getDatatype()==7)
			{
				if("i9999".equalsIgnoreCase(field.getName()))
					str.append(",count("+Sql_switcher.isnull(field.getName(),"0")+") "+field.getName());
				else
					str.append(",sum("+Sql_switcher.isnull(field.getName(),"0")+") "+field.getName());
			}
		}
		str.append(" "+whl);
		
		if(scopeitemid!=null&&scopeitemid.trim().length()>0&&!"no".equalsIgnoreCase(scopeitemid))
		{
		//	StringBuffer wherestr=new StringBuffer("");
			String fromScope = (String)this.getFormHM().get("fromScope");
			fromScope=fromScope!=null?fromScope:"";
			
			String toScope = (String)this.getFormHM().get("toScope");
			toScope=toScope!=null?toScope:"";
		/*	wherestr.append(" and ");
			wherestr.append(scopeitemid);
			wherestr.append(" BETWEEN ");
			wherestr.append(Sql_switcher.dateValue(fromScope+" 00:00:00"));
			wherestr.append(" AND "+Sql_switcher.dateValue(toScope+" 23:59:59"));*/
			str.append(" "+getBetweenDateStr(scopeitemid,fromScope,toScope));
			
			
			this.getFormHM().put("fromScope", fromScope);
			this.getFormHM().put("toScope", toScope);
			
		}
		else
		{
			this.getFormHM().put("fromScope", "");
			this.getFormHM().put("toScope","");
		}
		
		str.append(" group by "+maintable+".a0100 	) a2");
		str.append(" where a1.a0100=a2.a0100 ");
		return str.toString();
	}
	
	
	public String getBetweenDateStr(String itemid,String fromScope,String toScope)
	{
		StringBuffer str=new StringBuffer("");
		if(fromScope.length()>0)
		{
			String[] temp=fromScope.split("-");
			str.append(" and ("+Sql_switcher.year(itemid)+">"+temp[0]);
			str.append(" or ("+Sql_switcher.year(itemid)+"="+temp[0]+" and "+Sql_switcher.month(itemid)+">"+temp[1]+") ");
			str.append(" or ("+Sql_switcher.year(itemid)+"="+temp[0]+" and "+Sql_switcher.month(itemid)+"="+temp[1]+" and "+Sql_switcher.day(itemid)+">="+temp[2]+"  ) )");
		}
		
		if(toScope.length()>0)
		{
			String[] temp=toScope.split("-");
			str.append(" and ("+Sql_switcher.year(itemid)+"<"+temp[0]);
			str.append(" or ("+Sql_switcher.year(itemid)+"="+temp[0]+" and "+Sql_switcher.month(itemid)+"<"+temp[1]+") ");
			str.append(" or ("+Sql_switcher.year(itemid)+"="+temp[0]+" and "+Sql_switcher.month(itemid)+"="+temp[1]+" and "+Sql_switcher.day(itemid)+"<="+temp[2]+"  ) )");
		}
		return str.toString();
	}
	
	
	
	
	public String getSql_str(String maintable,ArrayList list)
	{
		StringBuffer str=new StringBuffer("select ");
		StringBuffer buf=new StringBuffer("");
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if("A0100".equalsIgnoreCase(field.getName()))
			{
				buf.append("a1.A0100");
			}
			else
			{
				if(field.getDatatype()==3||field.getDatatype()==4||field.getDatatype()==5||field.getDatatype()==6||field.getDatatype()==7)
					buf.append("a2."+field.getName());
				else
					buf.append(field.getName());
			}
			buf.append(",");
		}//for i loop end.
		buf.setLength(buf.length()-1);
		str.append(buf.toString());
		
		return str.toString();
	}

	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname){
				
		ArrayList fieldlist=new ArrayList();
		Field tempfield=new Field("A0100","A0100");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(8);
		tempfield.setReadonly(true);			
		tempfield.setVisible(false);
		fieldlist.add(tempfield);

		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);		
		if(fieldset==null)
			return fieldlist;
		ArrayList list = gzbo.itemListvalue(fieldset);
		
		int I9999 = 1;
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();

			if("B0110".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
			}else if("E0122".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
			}else if("E01A1".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
			}else if("A0101".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
			}
			if("0".equals(this.userView.analyseFieldPriv(itemid)))
				continue;
			if("1".equals(this.userView.analyseFieldPriv(itemid)))
				field.setReadonly(true);
			if(!"2".equals(this.userView.analyseTablePriv(setname)))
				field.setReadonly(true);
			field.setSortable(true);
			fieldlist.add(field);
			if(!fieldset.isMainset()){
				if("A0101".equalsIgnoreCase(itemid)&&I9999>0){
					Field itemfield=new Field("i9999","序号");
					itemfield.setDatatype(DataType.INT);
					itemfield.setCodesetid("0");
					itemfield.setReadonly(true);
					itemfield.setVisible(true);
					fieldlist.add(itemfield);
				}
			}
		}//i loop end.
		return fieldlist;
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @param model //1:综合评定(总分排名)  2：查询使用
	 * @return
	 */
	private String getFields(ArrayList list,String maintable,String model)
	{
		StringBuffer buf=new StringBuffer();
		
		if("2".equals(model))
		{	
			buf.append(maintable);
			buf.append(".A0000,");
			
			
			ArrayList alist=this.userView.getPrivFieldList("A01",Constant.USED_FIELD_SET);
			for(int i=0;i<alist.size();i++)
			{
				FieldItem fielditem=(FieldItem)alist.get(i);
				if(fielditem!=null){
					if("A".equalsIgnoreCase(fielditem.getItemtype())&&!"0".equals(fielditem.getCodesetid()))
					{
						if(!"b0110".equalsIgnoreCase(fielditem.getItemid())&&!"e0122".equalsIgnoreCase(fielditem.getItemid())&&!"e01a1".equalsIgnoreCase(fielditem.getItemid()))
						{
							buf.append(maintable);
							buf.append("."+fielditem.getItemid()+",");
						}
					}
				}	
			}
			
		}
		
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if("A0100".equalsIgnoreCase(field.getName()))
			{
				buf.append(maintable);
				buf.append(".A0100");
			}
			else
			{
					buf.append(field.getName());
			}
			buf.append(",");
		}//for i loop end.
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	private ArrayList dbList()
	{
		ArrayList dblist = new ArrayList();
		/**库前缀列表*/
		ArrayList list=this.userView.getPrivDbList();
		for(int i=0;i<list.size();i++)
		{
			String pre=(String)list.get(i);
			CommonData data=new CommonData(pre,AdminCode.getCodeName("@@", pre));
			dblist.add(data);
		}//for i loop end.
		return dblist;
	}
	private String whereStr(String dbname,String a_code){
		String wherestr = "";
		StringBuffer sexpr=new StringBuffer();
		StringBuffer sfactor=new StringBuffer();
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);

			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					sexpr.append("B0110=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sexpr.append("E0122=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sexpr.append("E01A1=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						sexpr.append(codearr[1]+"=");
						sexpr.append(codearr[2]);
						sexpr.append("*`");
						sfactor.append("1");
					}
				}
			}else{
				sexpr.append("B0110=");
				sexpr.append(value);
				sexpr.append("*`B0110=`");
				sfactor.append("1+2");
			}
		}	
		/**过滤条件*/
		try {
			wherestr=userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),dbname,false,true,new ArrayList());
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wherestr;
	}
	private boolean checkMainItem(String sortitem){
		boolean checkflag=false;
		if(sortitem.trim().length()>5){
			String sortArr[] = sortitem.split("`");
			for(int i=0;i<sortArr.length;i++){
				if(sortArr[i]!=null){
					String itemArr[] = sortArr[i].split(":");
					if(itemArr.length==3){
						FieldItem item=DataDictionary.getFieldItem(itemArr[0].toLowerCase());
						if(item.isMainSet()){
							checkflag=true;
							break;
						}
					}
				}
			}
		}
		return checkflag;
	}
	private boolean checkMainItem(String sortitem,String setid){
		boolean checkflag=false;
		if(sortitem.trim().length()>5){
			String sortArr[] = sortitem.split("`");
			for(int i=0;i<sortArr.length;i++){
				if(sortArr[i]!=null){
					String itemArr[] = sortArr[i].split(":");
					if(itemArr.length==3){
						FieldItem item=DataDictionary.getFieldItem(itemArr[0].toLowerCase());
						if(!item.isMainSet()){
							if(!setid.equalsIgnoreCase(item.getFieldsetid())){
								checkflag=true;
							}
						}
					}
				}
			}
		}
		return checkflag;
	}
}
