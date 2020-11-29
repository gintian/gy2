package com.hjsj.hrms.businessobject.general.deci.leader;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.leader.LeaderUtils;
import com.hjsj.hrms.businessobject.sys.report.DyParameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LeadberOperation {

	private Connection conn;
	private UserView userView;
	public LeadberOperation(){}
	public LeadberOperation(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	/**
	 * 
	 * @param candid_setid  子集
	 * @param candid_codesetid 指标项
	 * @param candid_value  指标值
	 * @param display_list  显示项
	 * @param code  
	 * @param kind
	 * @return
	 */
	
	public HashMap getLeadberMap(String db_field,String candid_setid,String candid_codesetid,String candid_value,ArrayList display_list,String code,String kind)
	{
		String nbase="";
		StringBuffer columns=new StringBuffer();
		StringBuffer select_str=new StringBuffer();
		StringBuffer select_columns=new StringBuffer();
		ArrayList fieldlist=new ArrayList();
		ArrayList nbaselist = new ArrayList();
		if(db_field.length()<=0) {
            nbaselist.add("Usr");
        } else{
			String[] ss = db_field.split(",");
			for(int i =0;i<ss.length;i++){
				nbaselist.add(ss[i]);
			}
		}
		boolean b0110_is=false;
		boolean e0122_is=false;
		boolean e01a1_is=false;
		if(display_list!=null&&display_list.size()>0)
		{
			int n=0;
			for(int i=0;i<display_list.size();i++)
			{
				CommonData data=(CommonData)display_list.get(i);
				String itemid=data.getDataValue();			
				if("b0110".equalsIgnoreCase(itemid)) {
                    b0110_is=true;
                }
				if("e0122".equalsIgnoreCase(itemid)) {
                    e0122_is=true;
                }
				if("e01a1".equalsIgnoreCase(itemid)) {
                    e01a1_is=true;
                }
				FieldItem fielditem=DataDictionary.getFieldItem(itemid);
				fielditem.setVisible(true);
				fieldlist.add(fielditem);
				//String itemsetid=fielditem.getFieldsetid();			
				columns.append(itemid+",");
				select_columns.append(itemid+i+"."+itemid+" as "+itemid+",");
				n++;
			}
			columns.setLength(columns.length()-1);
			select_columns.setLength(select_columns.length()-1);
			FieldItem fielditem00=null;
			if(!b0110_is)
			{
				fielditem00=DataDictionary.getFieldItem("b0110");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append("b0110,");
				select_columns.append(",b0110"+(n++)+".b0110 as b0110");
			}
			if(!e0122_is)
			{
				fielditem00=DataDictionary.getFieldItem("e0122");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append("e0122,");
				select_columns.append(",e0122"+(n++)+".e0122 as e0122");
			}
			if(!e01a1_is)
			{
				fielditem00=DataDictionary.getFieldItem("e01a1");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append("e01a1,");
				select_columns.append(",e01a1"+(n++)+".e01a1 as e01a1");
			}
			select_columns.append(",AAA.a0000,AAA.a0100");
			for(int r=0;r<nbaselist.size();r++)
			{
				nbase=nbaselist.get(r).toString();
				select_str.append("select '"+nbase+"' as nbase,");
				select_str.append(select_columns.toString());
				
				select_str.append(" from "+nbase+candid_setid);
				select_str.append(" left join (select A.a0100,a0000,"+Sql_switcher.isnull("b0110","''")+" as b0110");
				select_str.append(","+Sql_switcher.isnull("e0122","''")+" as e0122,"+Sql_switcher.isnull("e01a1","''")+" as e01a1,a0101 from "+nbase+"A01"+" A");
				select_str.append(") AAA");
				select_str.append(" on ");
				select_str.append(nbase+candid_setid+".a0100=AAA.a0100 ");
				String itemid="";
				String oldItemid="";
				for(int i=0;i<fieldlist.size();i++)
				{
				   	FieldItem fielditem=(FieldItem)fieldlist.get(i);
				   	if(fielditem==null) {
                        continue;
                    }
					String setid=fielditem.getFieldsetid();		
					itemid=fielditem.getItemid();
					if("A01".equals(setid))
					{
						select_str.append(" left join (select "+itemid+",A.a0100 from "+nbase+setid+" A");
						select_str.append(") "+itemid+i);
						select_str.append(" on ");
					}else
					{
						select_str.append(" left join (select "+itemid+",A.a0100 from "+nbase+setid+" A,");
						select_str.append("(select a0100,max(i9999) as i9999 from "+nbase+setid+" group by a0100)B");
						select_str.append(" where A.a0100=B.a0100 and A.i9999=B.i9999) "+itemid+i);
						select_str.append(" on ");
					}
					if(i==0)
					{
						select_str.append(nbase+candid_setid+".A0100="+itemid+i+".A0100 ");
					}else
					{
						//select_str.append(oldItemid+".a0100="+itemid+i+".a0100 ");
						select_str.append(nbase+candid_setid+".A0100="+itemid+i+".A0100 ");
					}
					
					oldItemid=itemid+i;
				}
				if(!"A01".equalsIgnoreCase(candid_setid))
				{
					select_str.append(",(select a0100,Max(i9999) as i9999 from "+nbase+candid_setid+"  Group by a0100) D");
				}
				select_str.append(" where "+nbase+candid_setid+"."+candid_codesetid.toUpperCase()+"='"+candid_value.toUpperCase()+"'");
				String whereCode="";
			    if("2".equals(kind)) {
                    whereCode=" and AAA.B0110 like '"+code.toUpperCase()+"%'";
                } else if("1".equals(kind)) {
                    whereCode=" and AAA.E0122 like '"+code.toUpperCase()+"%'";
                } else if("0".equals(kind)) {
                    whereCode=" and AAA.E01A1 like '"+code.toUpperCase()+"%'";
                } else {
                    whereCode=" and AAA.B0100 like '"+code.toUpperCase()+"%'";
                }
			    select_str.append(whereCode);
			    String whereIN=InfoUtils.getWhereINSql(userView,nbase);
			    select_str.append(" and AAA.A0100 in(select A0100 "+whereIN+") "); 
				if(!"A01".equalsIgnoreCase(candid_setid))
				{
					select_str.append(" and "+nbase+candid_setid+".A0100=D.A0100 and "+nbase+candid_setid+".I9999=D.I9999");
				}			
				select_str.append(" union ");
			}		
			select_str.setLength(select_str.length()-7);
		}
		//System.out.println(select_str.toString());	
		HashMap map=new HashMap();
		map.put("select_str",select_str.toString());
		map.put("column",columns.toString());
		map.put("fieldlist",fieldlist);
		map.put("order","order by B0110,E0122,A0000");
		return map;
	}
	/**
	 * 
	 * @param fieldlist 显示指标
	 * @param sql  
	 * @param curpage  当前页
	 * @param pagesize  显示个数
	 * @return
	 */
	public ArrayList beanList(ArrayList fieldlist,String sql)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		if(sql!=null&&sql.length()>0){
			sql="select * from ( "+sql+" )AA order by AA.b0110,AA.e0122,AA.a0000";		
			try
			{
				RowSet rs=dao.search(sql);
				while(rs.next())
				{
					DynaBean bean = new LazyDynaBean();
					for(int i=0;i<fieldlist.size();i++)
					{
						FieldItem fielditem=(FieldItem)fieldlist.get(i);
						if(!fielditem.isVisible()) {
                            continue;
                        }
						//bean.set(fielditem.getItemid(),rs.getString(fielditem.getItemid()));
						String itemid=fielditem.getItemid();					
						String typeitem=fielditem.getItemtype();
						String codesetid = fielditem.getCodesetid();
						String itemidValue="";
						if("b0110".equalsIgnoreCase(itemid))
						{
							itemidValue=AdminCode.getCodeName("UN", rs.getString(itemid));
							if(itemidValue.length()==0) {
                                itemidValue=getEmpOrgorDept(rs.getString(itemid));
                            }
						}else if("e0122".equalsIgnoreCase(itemid))
						{
							itemidValue=AdminCode.getCodeName("UM", rs.getString(itemid));
							if(itemidValue.length()==0) {
                                itemidValue=getEmpOrgorDept(rs.getString(itemid));
                            }
						}else if("e01a1".equalsIgnoreCase(itemid))
						{
							itemidValue=rs.getString(itemid);
							/*itemidValue=AdminCode.getCodeName("@K", rs.getString(itemid));
							if(itemidValue.length()==0)
								itemidValue=getEmpOrgorDept(rs.getString(itemid));*/
						}
						else{
							if("D".equalsIgnoreCase(typeitem))
							{
								Date itemate= rs.getDate(itemid);
								if(itemate!=null) {
                                    itemidValue=DateUtils.format(itemate,"yyyy.MM.dd");
                                }
							}else if("A".equalsIgnoreCase(typeitem)&&codesetid.length()>0&&!"0".equals(codesetid)){
								itemidValue=AdminCode.getCodeName(codesetid, rs.getString(itemid));
							}
							else {
                                itemidValue= rs.getString(itemid);
                            }
						}
						if(itemidValue==null|| "null".equals(itemidValue)) {
                            itemidValue="";
                        }
						bean.set(itemid,itemidValue);
						bean.set("a0100",rs.getString("A0100"));
						bean.set("nbase",rs.getString("nbase"));
					}
					list.add(bean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}	
	public String beanlistHtml(ArrayList display_list,ArrayList fieldlist,ArrayList beanlist,int curpage,int pagesize,int sumsize,String code,String kind)
	{
		
		//查询设置了岗位说明书模板
		boolean beTemplate = false;
		String sqls="select str_value from constant where upper(constant)='ZP_POS_TEMPLATE'";
	    List posTList = ExecuteSQL.executeMyQuery(sqls, conn);
		for(int i=0;i<posTList.size();i++){
			LazyDynaBean ldb = (LazyDynaBean)posTList.get(i);
			String template = (String)ldb.get("str_value");
			if(template != null && template.length()>0) {
                beTemplate = true;
            }
		}
		
		ContentDAO dao=new ContentDAO(this.conn);
		String[] display = new String[display_list.size()];
		for(int x=0;x<display_list.size();x++){
			StringBuffer sql = new StringBuffer();
			sql.append("select itemtype from fielditem where Upper(itemid) = '");
			sql.append(((CommonData)display_list.get(x)).getDataValue().toString().toUpperCase()+"'");
			try {
				RowSet rs = dao.search(sql.toString());
				if(rs.next()) {
                    display[x]=rs.getString("itemtype");
                } else {
                    display[x]="A";
                }
			} catch (SQLException e) {e.printStackTrace();}
		}
		
		StringBuffer html=new StringBuffer();
		int sumpage=sumsize/pagesize;
		int mod=sumsize%pagesize;
		if(mod>0) {
            sumpage=sumpage+1;
        }
		if(curpage>sumpage) {
            curpage=sumpage;
        } else if(curpage<=1) {
            curpage=1;
        }
		DynaBean bean = new LazyDynaBean();		
		for(int i=0;i<beanlist.size();i++)
		{
			if(i>=(curpage-1)*pagesize&&(i<(curpage)*pagesize))
			{
				
				bean=(DynaBean)beanlist.get(i);
				String a0100 = (String)bean.get("a0100");
				String nbase = (String)bean.get("nbase");
				String b0110 = (String)bean.get("b0110");
				b0110 = b0110==null||b0110.length()<1?"":b0110;
				String e0122 = (String)bean.get("e0122");
				e0122 = e0122==null||e0122.length()<1?"":e0122;
				if(i%2==0) {
                    html.append("<tr class='trShallow'>");
                } else {
                    html.append("<tr class='trDeep'>");
                }
				for(int r=0;r<fieldlist.size();r++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(r);
					if(!fielditem.isVisible()) {
                        continue;
                    }
					String value=(String)bean.get(fielditem.getItemid());
					if(value==null||value.length()<=0|| "null".equalsIgnoreCase(value)) {
                        value="";
                    }
					String itemtype = display[r];
					String place = "";
					if("A".equalsIgnoreCase(itemtype)|| "M".equalsIgnoreCase(itemtype)) {
                        place = "left";
                    } else if("D".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype)) {
                        place = "right";
                    }
					
					html.append("<td align='"+place+"' class='RecordRow' nowrap>");
					if("a0101".equalsIgnoreCase(fielditem.getItemid()))
						//html.append(" <a href='/workbench/browse/showselfinfo.do?b_search=link&userbase="+bean.get("nbase")+"&a0100="+bean.get("a0100")+"&flag=notself&returnvalue=1' target='_blank'>&nbsp;"+value+"</a>");
                    {
                        html.append(" <a href='javascript:void(0);' onclick=winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase="+bean.get("nbase")+"&flag=notself&returnvalue=','"+bean.get("a0100")+"','_blank');>&nbsp;"+value+"&nbsp;</a>");
                    } else if("e01a1".equalsIgnoreCase(fielditem.getItemid())){
						LeaderUtils ld = new LeaderUtils();
						String partjob = ld.getPartJob(a0100, nbase, code, kind, b0110, e0122);
						String viewvalue=AdminCode.getCodeName("@K",value);
						if(viewvalue.length()==0) {
                            viewvalue=getEmpOrgorDept(value);
                        }
						if(viewvalue.length()==0) {
                            html.append("&nbsp;"+viewvalue+"&nbsp;");
                        } else if(beTemplate)//判断是否设置了岗位说明书模板
                        {
                            html.append(" <a href='javascript:void(0);' onclick=openwin('/workbench/browse/showposinfo.do?b_browse=link&npage=1&a0100="+value+"');>&nbsp;"+viewvalue+"&nbsp;</a>");
                        } else {
                            html.append("&nbsp;"+viewvalue+"&nbsp;");
                        }
						if(viewvalue.length()>0 && partjob.length()>0) {
                            html.append(partjob);
                        }
					}else {
                        html.append("&nbsp;"+value+"&nbsp;");
                    }
					html.append("</td>");
				}
				
				html.append("<td align='center' class='RecordRow' nowrap><a href=\"###\" onclick='openwin1(\"/general/inform/synthesisbrowse/mycard.do?b_mysearch=link`userbase="+((String)bean.get("nbase"))+"`a0100="+PubFunc.encrypt(bean.get("a0100").toString())+"`multi_cards=-1`inforkind=1`npage=1`userpriv=noinfo`flick=1\");'>");
				html.append("<img src=\"/images/view.gif\" border=0>");
				html.append("</a></td>");
				html.append("</tr>");
			}
		}
		return html.toString();
	}
	public String getItemValueDesc(String itemid,String itemValue,FieldItem fielditem){
		String value="";
		String codesetId = "0";
		boolean bflag = false;
		String sql ="select codesetid from fielditem where Upper(itemid)='"+itemid.trim().toUpperCase()+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			 RowSet rs = dao.search(sql);
			if (rs.next()) {
				codesetId = rs.getString("codesetid").trim();
				if(codesetId == null){
					codesetId = "0";
				}
				if(!"0".equals(codesetId)){//代码型
					bflag = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(bflag){//代码型
			
			/*String strsql = "select codeitemdesc from codeitem where Upper(codesetid)='"
				+codesetId.toUpperCase()+"' and Upper(codeitemid)='"+itemValue.toUpperCase()+"'";
			System.out.println(strsql);
			RowSet rss=null;
			try {
				 rss = dao.search(strsql);
				if (rss.next()) {
					value = rss.getString("codeitemdesc");
					if(value == null){
						value = "";
					}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rss!=null)
					try {
						rss.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}*/
			value=AdminCode.getCodeName(codesetId, itemValue);
		}else{
			String itemType = fielditem.getItemtype();
			
			/*System.out.println(itemValue);
			System.out.println("itemid="+ itemid +"         itemType=" + itemType);*/
			
			if ("A".equalsIgnoreCase(itemType)){
				value= itemValue;
			}else if("D".equalsIgnoreCase(itemType)){
				String length = fielditem.getItemlength()+"";
				value = itemValue.substring(0,Integer.parseInt(length));
			}else if("N".equalsIgnoreCase(itemType)){
				String length = fielditem.getDecimalwidth()+"";
				value= this.formatValue(itemValue,Integer.parseInt(length));
			}else if("M".equalsIgnoreCase(itemType)){
				value= itemValue;
			}else{
				value= itemValue;
			}
			
			
		}
		
		return value;
	}
	/**
	 * 获取规范的表达式的值,自动四舍五入
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String formatValue(String exprValue , int flag){
		
		StringBuffer sb = new StringBuffer();	
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}	
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
	//	System.out.println("传入数据=" + exprValue + "小数位=" + flag + "规范化数据为=" + dstr) ;
		double dv=Double.parseDouble(dstr);
		if(dv==0)
		{
			dstr="0"+dstr;
		}
		return dstr;
	}
	public int getLeadberConnt(String db_field,String candid_setid,String candid_codesetid,String candid_value,String code,String kind)
	{
		int count=0;
		StringBuffer sql=new StringBuffer();
		ArrayList nbaselist=new ArrayList();
		if(db_field.length()<=0) {
            nbaselist.add("Usr");
        } else{
			String[] ss = db_field.split(",");
			for(int i =0;i<ss.length;i++){
				nbaselist.add(ss[i]);
			}
		}
		String nbase="";
		for(int r=0;r<nbaselist.size();r++)
		{
			nbase=nbaselist.get(r).toString();
			sql.append("select count(distinct "+nbase+candid_setid+".a0100) as count1");
			sql.append(" from "+nbase+candid_setid);
			sql.append(" left join (select A.a0100,a0000,"+Sql_switcher.isnull("b0110","''")+" as b0110");
			sql.append(","+Sql_switcher.isnull("e0122","''")+" as e0122,"+Sql_switcher.isnull("e01a1","''")+" as e01a1,a0101 from "+nbase+"A01"+" A");
			sql.append(") AAA");
			sql.append(" on ");
			sql.append(nbase+candid_setid+".A0100=AAA.A0100 ");
			if(!"A01".equalsIgnoreCase(candid_setid))
			{
				sql.append(",(select a0100,Max(i9999) as i9999 from "+nbase+candid_setid+"  Group by a0100) D");
			}
			sql.append(" where "+nbase+candid_setid+"."+candid_codesetid.toUpperCase()+"='"+candid_value.toUpperCase()+"'");
			String whereCode="";
		    if("2".equals(kind)) {
                whereCode=" and AAA.B0110 like '"+code.toUpperCase()+"%'";
            } else if("1".equals(kind)) {
                whereCode=" and AAA.E0122 like '"+code.toUpperCase()+"%'";
            } else if("0".equals(kind)) {
                whereCode=" and AAA.E01A1 like '"+code.toUpperCase()+"%'";
            } else {
                whereCode=" and AAA.B0100 like '"+code.toUpperCase()+"%'";
            }
		    String whereIN=InfoUtils.getWhereINSql(userView,nbase);
		    sql.append(whereCode);
		    sql.append(" and AAA.A0100 in(select a0100 "+whereIN+") "); 
			if(!"A01".equalsIgnoreCase(candid_setid))
			{
				sql.append(" and "+nbase+candid_setid+".A0100=D.A0100 and "+nbase+candid_setid+".I9999=D.I9999");
			}	
		    sql.append(" union ");
		}
		sql.setLength(sql.length()-7);		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rs=dao.search(sql.toString());
			while(rs.next())
			{
				count=count+rs.getInt("count1");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * 得到人员信息
	 * @param candid_setid
	 * @param candid_codesetid
	 * @param candid_value
	 * @param code
	 * @param kind
	 * @return
	 */
	public ArrayList getLeadberEmpMess(String db_field,String candid_setid,String candid_codesetid,String candid_value,String code,String kind)
	{
		StringBuffer sql=new StringBuffer();
		ArrayList nbaselist=new ArrayList();
		if(db_field.length()<=0) {
            nbaselist.add("Usr");
        } else{
			String[] ss = db_field.split(",");
			for(int i =0;i<ss.length;i++){
				nbaselist.add(ss[i]);
			}
		}
		ArrayList list=new ArrayList();
		String nbase="";
		for(int r=0;r<nbaselist.size();r++)
		{
			nbase=nbaselist.get(r).toString();
			sql.append("select distinct "+nbase+candid_setid+".a0100 as a0100,AAA.a0101 as a0101,'"+nbase+"' as nbase,AAA.B0110,AAA.E0122,AAA.A0000");
			sql.append(" from "+nbase+candid_setid);			
			sql.append(" left join (select A.a0100,a0000,"+Sql_switcher.isnull("b0110","''")+" as b0110");
			sql.append(","+Sql_switcher.isnull("e0122","''")+" as e0122,"+Sql_switcher.isnull("e01a1","''")+" as e01a1,a0101 from "+nbase+"A01"+" A");
			sql.append(") AAA");
			sql.append(" on ");
			sql.append(nbase+candid_setid+".A0100=AAA.A0100 ");
			if(!"A01".equalsIgnoreCase(candid_setid))
			{
				sql.append(",(select a0100,Max(i9999) as i9999 from "+nbase+candid_setid+"  Group by a0100) D");
			}
			sql.append(" where "+nbase+candid_setid+"."+candid_codesetid.toUpperCase()+"='"+candid_value.toUpperCase()+"'");
			String whereCode="";
		    if("2".equals(kind)) {
                whereCode=" and AAA.B0110 like '"+code.toUpperCase()+"%'";
            } else if("1".equals(kind)) {
                whereCode=" and AAA.E0122 like '"+code.toUpperCase()+"%'";
            } else if("0".equals(kind)) {
                whereCode=" and AAA.E01A1 like '"+code.toUpperCase()+"%'";
            } else {
                whereCode=" and AAA.B0100 like '"+code.toUpperCase()+"%'";
            }
		    String whereIN=InfoUtils.getWhereINSql(userView,nbase);
		    sql.append(whereCode);
		    sql.append(" and AAA.A0100 in(select a0100 "+whereIN+") "); 
			if(!"A01".equalsIgnoreCase(candid_setid))
			{
				sql.append(" and "+nbase+candid_setid+".A0100=D.A0100 and "+nbase+candid_setid+".I9999=D.I9999");
			}	
		    sql.append(" union ");
		}		
		sql.setLength(sql.length()-7);		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rs=null;
			if(com.hrms.hjsj.utils.Sql_switcher.searchDbServer()==1) {
                rs=dao.search(sql.toString()+" order by AAA.B0110,AAA.E0122,AAA.A0000");
            } else {
                rs=dao.search(sql.toString()+" order by B0110,E0122,A0000");
            }
			while(rs.next())
			{
				DynaBean bean = new LazyDynaBean();	
				bean.set("nbase",rs.getString("nbase"));
				bean.set("a0100",rs.getString("a0100"));
				bean.set("a0101",rs.getString("a0101"));
				list.add(bean);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String beanPhotoHtml(String url,ArrayList beanlist,int curpage,int pagesize,int sumsize,int trNum)
	{
		if(sumsize<=0) {
            return "";
        }
		StringBuffer html=new StringBuffer();
		DynaBean bean=null;
		try
		{
			int n=0;
			for(int i=0;i<beanlist.size();i++)
			{
				if(i>=(curpage-1)*pagesize&&(i<(curpage)*pagesize))
				{
					bean=(DynaBean)beanlist.get(i);
					String dbname=(String)bean.get("nbase");
					String a0100=(String)bean.get("a0100");					
					if(n==0||n==(pagesize/trNum)) {
                        html.append("<tr >");
                    }
					//String filename=ServletUtilities.createPhotoFile(dbname+"A00",a0100,"P",null);
					String filename=createPhotoFile(dbname+"A00",a0100, "p");
					filename = PubFunc.encrypt(filename);//liuy 2015-5-7 9434：领导桌面：关键人才/后备干部页面，点击【成员照片】，后台报错，人员照片显示不出来
					StringBuffer photourl=new StringBuffer();
				    if(!"".equals(filename)){
				        	//photourl.append(url);
				        	photourl.append("/servlet/DisplayOleContent?filename=");
				        	photourl.append(filename);
				    }else{
				        	photourl.append("/images/photo.jpg");
				    }
				    LazyDynaBean lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_w",this.conn);
					String photo_w=(String)lazyDynaBean.get("photo_w");
					photo_w=photo_w!=null&&photo_w.trim().length()>0?photo_w:"85";
					 
					lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_h",this.conn);
					String photo_h=(String)lazyDynaBean.get("photo_h");
					photo_h=photo_h!=null&&photo_h.trim().length()>0?photo_h:"120";
				    
					html.append("<td align='center'  nowrap>");
					html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0'>");
					html.append("<tr>");
					html.append("<td align='center' bgcolor='' nowrap><div class='photo'>");
					html.append("<a href='/workbench/browse/showselfinfo.do?b_search=link&userbase="+bean.get("nbase")+"&a0100="+a0100+"&flag=notself&returnvalue=' target='_blank'>");
					
					html.append(" <a href='javascript:void(0)' onclick=winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase="+bean.get("nbase")+"&flag=notself&returnvalue=','"+bean.get("a0100")+"','_blank');>");
					html.append("<img src='");
					html.append(photourl.toString());
					html.append("' ");
					html.append(" border='0' height='");
					html.append(photo_h);
					html.append("' width='");
					html.append(photo_w);
					html.append("' border=0></img>");
					html.append("</a></div>");
					html.append("</td>");
					html.append("</tr>");
					html.append("<tr>");
					html.append("<td align='center' nowrap>");
					html.append(bean.get("a0101"));
					html.append("</td>");
					html.append("</tr>");
					html.append("</table>");
					html.append("</td>");
					if(n==(pagesize-1)||n==(pagesize/trNum-1)) {
                        html.append("</tr>");
                    }
					n++;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return html.toString();
	}
	/**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(String a00tab, String a0100, String flag) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        RowSet rs = null;          
        PreparedStatement pstmt=null;
        java.io.FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole from ");
            strsql.append(a00tab);
            strsql.append(" where A0100='");
            strsql.append(a0100.toUpperCase());
            strsql.append("' and Flag='");
            strsql.append(flag.toUpperCase());
            strsql.append("'");
            ContentDAO dao=new ContentDAO(this.conn);
           
            rs=dao.search(strsql.toString());   
            if (rs.next()) {
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));             
                InputStream in = null;
                try {
	                in = rs.getBinaryStream("Ole");                
	                fout = new java.io.FileOutputStream(tempFile);                
	                int len;
	                byte buf[] = new byte[1024];
	            
	                while ((len = in.read(buf, 0, 1024)) != -1) {
	                    fout.write(buf, 0, len);
	               
	                }
                } finally {
                	PubFunc.closeIoResource(in);
                	PubFunc.closeIoResource(fout);
                }
               
                filename= tempFile.getName();                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            PubFunc.closeIoResource(fout);
        }
       
        return filename;
    }
    /**
	 * 获取员工部门
	 * @return
	 */
	public String getEmpOrgorDept(String codeItemId){
		String dept = "";
		if (codeItemId==null) {
            return "";
        }
		String sql = "select codeitemdesc  from organization where Upper(codeitemid)='" +codeItemId.toUpperCase()+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			 RowSet rs = dao.search(sql);
			if (rs.next()) {
				dept = rs.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dept;
	}
	public String getLeaderWhereIn(String nbase,String candid_setid,String candid_codesetid,String candid_value)
	{
		StringBuffer sql=new StringBuffer();
		sql.append(nbase+"A01.A0100 in (");
		sql.append("select "+nbase+candid_setid+".a0100 from "+nbase+candid_setid);
		if(!"A01".equalsIgnoreCase(candid_setid))
		{
		   sql.append(",(select a0100,max(i9999) as i9999 from "+nbase+candid_setid+" group by a0100)D");
		}
		sql.append(" where "+nbase+candid_setid+"."+candid_codesetid.toUpperCase()+"='"+candid_value.toUpperCase()+"'");		
		if(!"A01".equalsIgnoreCase(candid_setid))
		{
			sql.append(" and "+nbase+candid_setid+".A0100=D.A0100 and "+nbase+candid_setid+".I9999=D.I9999");
		}	
		sql.append(")");
		return sql.toString();
	}
}
