package com.hjsj.hrms.businessobject.info;

import com.hjsj.hrms.businessobject.infor.CleanPersonSetting;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.query.AutoCreateQueryResultTable;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InfoUtils {
    // 父节点id
    private String parentId = "";
	
	public static String getWhereInOrgSql(UserView userView,String infor_type)
	{
		 
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
		 {
			    StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				if("2".equals(infor_type)) {
                    wheresql.append("B01");
                } else if("3".equals(infor_type)) {
                    wheresql.append("K01");
                }
			   if(userView.getManagePrivCode()!=null&&userView.getManagePrivCode().trim().length()>0)
			   {
				   
				   if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				   {
					   if("2".equals(infor_type)) {
                           strwhere=wheresql.toString()+" where b0110 like '"+userView.getManagePrivCodeValue().trim()+"%' ";
                       } else if("3".equals(infor_type)) {
                           strwhere=wheresql.toString()+" where e01a1 like '"+userView.getManagePrivCodeValue().trim()+"%' ";
                       }
				   }
				   
			   }
			   else
			   {
				    
					strwhere=wheresql.toString()+" where 1=2 ";
			   }
			 
			 
		
			}else{
				 StringBuffer wheresql=new StringBuffer();
					wheresql.append(" from ");
					if("2".equals(infor_type)) {
                        wheresql.append("B01");
                    } else if("3".equals(infor_type)) {
                        wheresql.append("K01");
                    }
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
		
	}
	
	
	/**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
     * @return 返回查询串
     * */
    public static String getWhereINSql(UserView userView,String userbase){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                        factor+=userView.getManagePrivCodeValue();
                    }
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	 strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
    /**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
	 * @param isPriv
	 *        是否按照管理范围 0 按照，1不按
     * @return 返回查询串
     * */
    public static String getWhereINSql(UserView userView,String userbase,String isPriv){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin()&&"0".equals(isPriv))
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                        factor+=userView.getManagePrivCodeValue();
                    }
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	 strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
    /**
     * 兼职子集没有A000字段时
     * @param userbase
     * @param code
     * @param kind
     * @param orgtype
     * @param personsortfield
     * @param personsort
     * @param part_unit
     * @param part_setid
     * @param part_appoint
     * @param where_n
     * @return
     * @throws GeneralException
     */
    public String getWhereSQL(Connection conn,UserView userView,String userbase,String code,String kind,String orgtype,String personsortfield
        	,String personsort,String part_unit,String part_setid,String part_appoint,String where_n)throws GeneralException 
        	   
    {
    	StringBuffer union_Sql=new StringBuffer();
    	StringBuffer wheresql=new StringBuffer();
    	String strwhere="";   
    	String strsqlA010="select "+userbase+"A01.a0100";	
		DbNameBo dbNameBo=new DbNameBo(conn);
		if(userView.isSuper_admin()){                    //超级用户	
			//生成没有高级条件的from后的sql语句
			if(!"vorg".equals(orgtype))
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				if("2".equals(kind) && code!=null && code.length()>0)
				{
					wheresql.append(" where ((b0110 like '");
				    wheresql.append(code);
				    wheresql.append("%'");
				}			   
				else if("1".equals(kind)  && code!=null && code.length()>0)
				{
					wheresql.append(" where ((e0122 like '"); 
					wheresql.append(code);
					wheresql.append("%'");
				}				
				else if("0".equals(kind) && code!=null && code.length()>0)
				{
					wheresql.append(" where ((e01a1 like '");
					wheresql.append(code);
					wheresql.append("%'");
				}else if("2".equals(kind))
				{
					wheresql.append(" where ((1=1");
				}
				else
				{
					wheresql.append(" where ((1=1");
				}
			    strwhere=wheresql.toString();
			    if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0) {
                    strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
                }
			    union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLike(userView, userbase, code);
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		int iWhere=strWhere.indexOf("in");
		    		strWhere=strWhere.substring(iWhere+2);
		    		union_Sql.append(" or "+userbase+"A01.A0100 in( ");
		    		//union_Sql.append("select a0100 ");
		    		//union_Sql.append(strsql.toString());
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}	
		    	union_Sql.append(")");		    	
		    	union_Sql.append(" "+where_n+")");
		    	if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
		    	{
		    		union_Sql.append(" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where "+part_unit+"='"+code+"'");
		    		if(part_appoint!=null&&part_appoint.length()>0) {
                        union_Sql.append(" and "+part_appoint+"='0' ");
                    }
		    		union_Sql.append("  "+where_n+"))");
		    	}
			}else
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ,vorganization a,t_vorg_staff b");
				wheresql.append(" where ((a.codeitemid='"+code+"'"); 
				wheresql.append(" and b.B0110= a.codeitemid ");
				wheresql.append(" and b.state=1 ");				
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and "+userbase+"A01.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLike(userView, userbase, code);
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		/*union_Sql.append(" or "+userbase+"A01.a0100 in(select  "+userbase+"A01.a0100 ");
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(" ) ");*/
		    		int iWhere=strWhere.indexOf("in");
		    		strWhere=strWhere.substring(iWhere+2);
		    		union_Sql.append(" or "+userbase+"A01.A0100 in( ");
		    		//union_Sql.append("select a0100 ");
		    		//union_Sql.append(strsql.toString());
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}
		    	union_Sql.append(")");
		    	union_Sql.append(" "+where_n+")");		    	
			}
			
		}
		else{   
			if(!"vorg".equals(orgtype))
			{
			  ArrayList fieldlist=new ArrayList();
		        try
		        {
		           String expr="1";
		           String factor="";
		           if("2".equals(kind))
		           {
		    		 factor="B0110=";
		    		 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 factor+="%`";
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`B0110=`";	
					 }
		           }
				   else if("1".equals(kind)){
				   	    factor="E0122="; 
				   	 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 factor+="%`";
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`E0122=`";	
					 }
				   }
				   else if("0".equals(kind)){
				   	    factor="E01A1=";
				   	    if(code!=null && code.length()>0)
					    {
						 factor+=code;
						 factor+="%`";
					    }
					     else
					   {
					      expr="1+2";
					      factor+=code;
					      factor+="%`E01A1=`";	
					    }
				   }
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		            String priv_stwhere=strwhere;
		            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0) {
                        strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
                    }
		            union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);			
				    union_Sql.append(where_n);
				    //union_Sql.append(" UNION ");
				    DbNameBo dbbo=new DbNameBo(conn);
			    	String strWhere=dbbo.getQueryFromPartTimeLike(userView, userbase, code);
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or "+userbase+"A01.a0100 in(select  "+userbase+"A01.a0100 ");
			    		//union_Sql.append(strsql.toString());
			    		union_Sql.append(strWhere+" "+where_n);	
			    		union_Sql.append(" ) ");
			    	}
			    	if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
			    	{
			    		union_Sql.append(" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where "+part_unit+" like '"+code+"%' "+where_n+"");
			    		if(part_appoint!=null&&part_appoint.length()>0) {
                            union_Sql.append(" and "+part_appoint+"='0' ");
                        }
			    		//union_Sql.append(" and "+userbase+""+part_setid+".a0100 in (select a0100 "+priv_stwhere+")");//韩俊华提，只要管理人，对改部门有权限，就能对该部门兼职人员进行操作
			    		union_Sql.append("))");
			    	}
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
			}else
	        {
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ,vorganization a,t_vorg_staff b");
				wheresql.append(" where ((a.codeitemid='"+code+"'"); 
				wheresql.append(" and b.state=1 ");	
				wheresql.append(" and b.B0110= a.codeitemid ");
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and "+userbase+"A01.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLike(userView, userbase, code);
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		/*union_Sql.append(" or "+userbase+"A01.a0100 in(select "+userbase+"A01.a0100 ");
		    		//union_Sql.append(strsql.toString());
		    		union_Sql.append(strWhere+" "+where_n);		
		    		union_Sql.append(" ) ");*/
		    		int iWhere=strWhere.indexOf("in");
		    		strWhere=strWhere.substring(iWhere+2);
		    		union_Sql.append(" or "+userbase+"A01.A0100 in( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}
		    	union_Sql.append(")");
		    	union_Sql.append(" "+where_n+")");		    	
	        }
		}		
		return union_Sql.toString();
    }
    /**
     * 兼职sql
     * @param userbase
     * @param code
     * @return
     */
    public String getPartwhere(String userbase,String code,Connection conn,UserView userview,String result)
    {
    	String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		//兼职处理
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");
		list.add("setid");
		list.add("appoint");
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0) {
                flag=(String)map.get("flag");
            }
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0) {
                    part_unit=(String)map.get("unit");
                }
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0) {
                    part_setid=(String)map.get("setid");
                }
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0) {
                    appoint=(String)map.get("appoint");
                }
			}		
		}
    	StringBuffer union_Sql=new StringBuffer();
    	if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
    	{
    		union_Sql.append(" or ( "+userbase+"A01.a0100 in(select a0100 from "+userbase+""+part_setid+" where "+part_unit+"='"+code+"'");
    		if(appoint!=null&&appoint.trim().length()>0) {
                union_Sql.append(" and "+appoint+"='0' ");
            }
    		if("1".equals(result)){
    			union_Sql.append(" and a0100 in (select a0100 from ");
    			union_Sql.append(userview.getUserName()+userbase+"result)");
    		}
    		union_Sql.append("))");
    	}
    	return union_Sql.toString();
    }
    /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    public String searchCard(String infortype,Connection conn)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid)) {
                 cardid="-1";
             }
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
    
    
    /**根据传过的的指标串，分解成对应的指标对象*/
    public ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone(); 
            	 if("b0110".equalsIgnoreCase(item_0.getItemid()))
            	 {
            		 item_0.setItemdesc("单位或部门名称");
            	 }            	             
                 list.add(item_0);
            }
           
        }
        return list;
    }
    /**根据传过的的指标串，分解成对应的指标对象*/
    public ArrayList splitField(String strfields,Connection conn)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone(); 
            	 if("b0110".equalsIgnoreCase(item_0.getItemid()))
            	 {
            		 item_0.setItemdesc("单位或部门名称");
            	 }
            	 if(item_0.getCodesetid()!=null&&!"0".equals(item_0.getCodesetid()))
            	 {
            		 int count=getCodeSetidChildLen(item_0.getCodesetid(),conn);
            		 item_0.setItemlength(count);    
            		 //如果代码类的代码项不只有一级，则代码想的总数设置为999，前台查询指标显示为代码树，不再平铺显示
            		 int maxLv=getCodeitemMaxLevel(item_0.getCodesetid(),conn);
            		 if(maxLv > 1) {
                         item_0.setItemlength(999);
                     }
            	 }                 
                 list.add(item_0);
            }
           
        }
        return list;
    }
    /**
     * *根据传过的的指标串，分解成对应的指标对象
     * @param strfields
     * @param delfields 去除的指标
     * @return
     */
    public ArrayList splitField(String strfields,String delfields,Connection conn)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        if(delfields==null||delfields.length()<=0) {
            delfields="";
        }
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);            
            FieldItem item=DataDictionary.getFieldItem(fieldname);            
            if(delfields.toLowerCase().indexOf(fieldname.toLowerCase())!=-1) {
                continue;
            }
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone(); 
            	 if(item_0.getUseflag()==null||item_0.getUseflag().length()==0|| "0".equals(item_0.getUseflag())) {
                     continue;
                 }
            	 if(item_0.getCodesetid()!=null&&!"0".equals(item_0.getCodesetid()))
            	 {
            		 int count=getCodeSetidChildLen(item_0.getCodesetid(),conn);
            		 item_0.setItemlength(count);    
            		//如果代码类的代码项不只有一级，则代码想的总数设置为999，前台查询指标显示为代码树，不再平铺显示
            		 int maxLv=getCodeitemMaxLevel(item_0.getCodesetid(),conn);
            		 if(maxLv > 1) {
                         item_0.setItemlength(999);
                     }
            	 }
            	 item_0.setValue("");
            	 item_0.setViewvalue("");
                 list.add(item_0);
            }
           
        }
        return list;
    }
    /**
     * 得到查询指标
     * @param type
     * @return
     */
    public ArrayList selectField(String type)
	{
		 RecordVo vo=null;
	     if("2".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
         } else if("3".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE");
         } else if("4".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_HQUERYTEMPLATE");
         } else {
             vo= ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");
         }
	    if(vo!=null)
	    {
	    		String strfields=vo.getString("str_value");
	             ArrayList fieldlist=splitField(strfields);
	             return fieldlist;            
	    }
	    else
	    {
	             return new ArrayList();            	
	    }
  }
  
    /**
     * 得到查询指标
     * @param type
     * @return
     */
    public ArrayList selectField(String type,Connection conn)
	{
		 RecordVo vo=null;
	     if("2".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
         } else if("3".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE");
         } else {
             vo= ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");
         }
	    if(vo!=null)
	    {
	    		String strfields=vo.getString("str_value");
	             ArrayList fieldlist=splitField(strfields,conn);
	             return fieldlist;            
	    }
	    else
	    {
	             return new ArrayList();            	
	    }
  }
    /**
     * 得到查询指标
     * @param type
     * @return
     */
    public ArrayList selectField(String type,String delFields,Connection conn)
	{
		 RecordVo vo=null;
	     if("2".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
         } else if("3".equals(type)) {
             vo= ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE");
         } else {
             vo= ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");
         }
	    if(vo!=null)
	    {
	    		String strfields=vo.getString("str_value");
	             ArrayList fieldlist=splitField(strfields,delFields,conn);
	             return fieldlist;            
	    }
	    else
	    {
	             return new ArrayList();            	
	    }
    }
    /**
     * 清空查询指标
     * @param factorlist
     * @return
     */
    public ArrayList clearQueryList(ArrayList factorlist)
    {
    	if(factorlist==null) {
            return null;
        }
    	ArrayList list =new ArrayList();  
    	for(int i=0;i<factorlist.size();i++)
        {
              FieldItem item=(FieldItem)factorlist.get(i);
              item.setValue("");
              item.setViewvalue("");
              list.add(item);
        }
    	return list;
    }
    
    /**
     * 清空查询指标
     * @param factorlist
     * @return
     */
    public ArrayList cloneQueryList(ArrayList factorlist)
    {
    	if(factorlist==null) {
            return null;
        }
    	ArrayList list =new ArrayList();  
    	for(int i=0;i<factorlist.size();i++)
        {
              FieldItem item=(FieldItem)factorlist.get(i);              
              list.add(item.clone());
        }
    	return list;
    }
    public String getKindFormCodeSetId(String codesetid)
    {
    	String kind="";
    	if("UN".equalsIgnoreCase(codesetid)) {
            kind="2";
        } else if("UM".equalsIgnoreCase(codesetid)) {
            kind="1";
        } else if("@K".equalsIgnoreCase(codesetid)) {
            kind="0";
        } else {
            kind="2";
        }
    	return kind;
    }
    /**组合查询SQL*/
    public String combine_SQL(UserView userView,ArrayList list,String like,String dbpre,String strInfr) throws GeneralException
    {
        int j=1;
    	boolean bresult=true;
    	boolean blike=false;    	
    	if("1".equals(like)) {
            blike=true;
        }
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        ArrayList checklist=new ArrayList();
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            //特殊字符 过滤 一下，防止出错 （全角转半角） gdd 14-09-23
            item.setValue(PubFunc.hireKeyWord_filter_reback(item.getValue()));
            //System.out.println(item.getItemdesc());
            /**如果值未填的话，default是否为不查*/
            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype()))) {
                continue;
            }
            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype()))) {
                continue;
            }
            if(!"0".equals(item.getCodesetid())&&item.getValue()!=null&&item.getValue().indexOf("`")!=-1)
    		{
    			String checkwhere=factorMakeup(userView,item,"=","+",dbpre,strInfr);
    			if(checkwhere!=null&&checkwhere.length()>0) {
                    checklist.add(checkwhere);
                }
    		}else
    		{
    			if("D".equals(item.getItemtype()))
                {
                    int sf=analyFieldDate(item,strexpr,strfactor,j);
                    if(sf==1)
                    {
                    	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
                    }
                    j=j+sf;
                }else if(!"0".equals(item.getCodesetid())&&item.getViewvalue()!=null&&item.getViewvalue().length()>0/*&&blike*/)
                {
                	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr,blike);
                	j=j+sf;
                }
                else
                {
    	            /**组合表达式串*/
    	            if(j==1)
    	            {
    	                strexpr.append(j);
    	            }
    	            else
    	            {
    	                strexpr.append("*");
    	                strexpr.append(j);                
    	            }
    	            
    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
    	            {
    	            		String q_v=item.getValue().trim();
    	            		
    	            		if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
    	                    {
    			                
    	                    	strfactor.append(item.getItemid().toUpperCase());
    			                if("0".equals(item.getCodesetid())) {
                                    strfactor.append("=*");
                                } else {
                                    strfactor.append("=");
                                }
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("*`");	   			                	  
    	                    }
    	                    else
    	                    {
    	                    	
    			                strfactor.append(item.getItemid().toUpperCase());
    			                strfactor.append("=");
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("`");	  	                        
    	                    }
    	            }
    	            else
    	            {
    	                strfactor.append(item.getItemid().toUpperCase());
    	                strfactor.append("=");
    	                strfactor.append(PubFunc.getStr(item.getValue()));
    	                strfactor.append("`");
    	            }
    	            ++j;	            
                }
    		}
            
        }
        //System.out.println(strexpr.toString());
        //System.out.println(strfactor.toString());
        ArrayList fieldlist=new ArrayList();
        StringBuffer strwhere=new StringBuffer();
        if(!userView.isSuper_admin()&& "1".equals(strInfr))
        {
            String strpriv=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,false,bresult,fieldlist);
            strwhere.append(strpriv);
        }
        else
        {
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
        	strwhere.append(factorlist.getSqlExpression());
        }
        
        StringBuffer checksql=new StringBuffer();
        
        for(int i=0;i<checklist.size();i++)
        {
        	checksql.append(" and ("+checklist.get(i)+")");
        }       
        boolean isWhere=true;
        if(strwhere!=null&&strwhere.toString().trim().equalsIgnoreCase("FROM "+dbpre+"A01")) {
            isWhere=false;
        } else if(strwhere!=null&& "FROM B01".equalsIgnoreCase(strwhere.toString().trim())) {
            isWhere=false;
        } else if(strwhere!=null&& "FROM K01".equalsIgnoreCase(strwhere.toString().trim())) {
            isWhere=false;
        }
        if(checklist==null||checklist.size()<=0)
        {
        	if(!isWhere) {
                return"";
            }
        }else
        {
        	if(!isWhere)
        	{
        		strwhere.append(" WHERE 1=1 ");
        		strwhere.append(checksql.toString());
        	}else {
                strwhere.append(checksql.toString());
            }
        }        
        return strwhere.toString();
    }
    
    /**组合查询SQL*/
    public String combine_ExistsSQL(UserView userView,ArrayList list,String like,String dbpre,String strInfr) throws GeneralException
    {
        int j=1;
    	boolean bresult=true;
    	boolean blike=false;    	
    	if("1".equals(like)) {
            blike=true;
        }
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        ArrayList checklist=new ArrayList();
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            item.setValue(PubFunc.hireKeyWord_filter_reback(item.getValue()));
            //System.out.println(item.getItemdesc());
            /**如果值未填的话，default是否为不查*/
            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype()))) {
                continue;
            }
            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype()))) {
                continue;
            }
            if(!"0".equals(item.getCodesetid())&&item.getValue()!=null&&item.getValue().indexOf("`")!=-1)
    		{
    			String checkwhere=factorMakeup(userView,item,"=","+",dbpre,strInfr,blike);
    			if(checkwhere!=null&&checkwhere.length()>0) {
                    checklist.add(checkwhere);
                }
    		}else
    		{
    			if("D".equals(item.getItemtype()))
                {
                    int sf=analyFieldDate(item,strexpr,strfactor,j);
                    if(sf==1)
                    {
                    	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
                    }
                    j=j+sf;
                }else if(!"0".equals(item.getCodesetid())&&item.getViewvalue()!=null&&item.getViewvalue().length()>0)
                {
                	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr,blike);
                	j=j+sf;
                }
                else
                {
    	            /**组合表达式串*/
    	            if(j==1)
    	            {
    	                strexpr.append(j);
    	            }
    	            else
    	            {
    	                strexpr.append("*");
    	                strexpr.append(j);                
    	            }
    	            
    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
    	            {
    	            		String q_v=item.getValue().trim();
    	            		
    	            		if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
    	                    {
    			                
    	                    	strfactor.append(item.getItemid().toUpperCase());
    			                if("0".equals(item.getCodesetid())) {
                                    strfactor.append("=*");
                                } else {
                                    strfactor.append("=");
                                }
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("*`");	   			                	  
    	                    }
    	                    else
    	                    {
    	                    	
    			                strfactor.append(item.getItemid().toUpperCase());
    			                strfactor.append("=");
    			                strfactor.append(PubFunc.getStr(item.getValue()));
    			                strfactor.append("`");	  	                        
    	                    }
    	            }
    	            else
    	            {
    	                strfactor.append(item.getItemid().toUpperCase());
    	                strfactor.append("=");
    	                strfactor.append(PubFunc.getStr(item.getValue()));
    	                strfactor.append("`");
    	            }
    	            ++j;	            
                }
    		}
            
        }
        //System.out.println(strexpr.toString());
        //System.out.println(strfactor.toString());
        if((strexpr==null||strexpr.length()<=0)&&checklist.size()<=0) {
            return "";
        }
        ArrayList fieldlist=new ArrayList();
        StringBuffer strwhere=new StringBuffer();
        if(!userView.isSuper_admin()&& "1".equals(strInfr))
        {
            /*String strpriv=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,false,bresult,fieldlist);
            strwhere.append(strpriv);*/
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
        	strwhere.append(factorlist.getSqlExpression());        	
        }
        else
        {
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
        	factorlist.setSuper_admin(userView.isSuper_admin());
        	strwhere.append(factorlist.getSqlExpression());
        }
        
        StringBuffer checksql=new StringBuffer();
        
        for(int i=0;i<checklist.size();i++)
        {
        	checksql.append(" and ("+checklist.get(i)+")");
        }       
        boolean isWhere=true;
        if(strwhere!=null&&strwhere.toString().trim().equalsIgnoreCase("FROM "+dbpre+"A01")) {
            isWhere=false;
        } else if(strwhere!=null&& "FROM B01".equalsIgnoreCase(strwhere.toString().trim())) {
            isWhere=false;
        } else if(strwhere!=null&& "FROM K01".equalsIgnoreCase(strwhere.toString().trim())) {
            isWhere=false;
        }
        if(checklist==null||checklist.size()<=0)
        {
        	if(!isWhere) {
                return"";
            }
        }else
        {
        	if(!isWhere)
        	{
        		strwhere.append(" WHERE 1=1 ");
        		strwhere.append(checksql.toString());
        	}else{
        		if(strwhere!=null&&strwhere.toString().trim().equalsIgnoreCase("FROM "+dbpre+"A01 where 1=2")){
	        		String tmpsql=strwhere.substring(0,strwhere.toString().toUpperCase().indexOf("WHERE")+6);
		    		strwhere.setLength(0);
		    		strwhere.append(tmpsql+" 1=1 "+checksql.toString()); 
        		}else{
        			strwhere.append(checksql.toString());
        		}   
        	}
        }        
        if("1".equals(strInfr)) {
            return getPrivSqlExists(strwhere.toString(),dbpre+"A01","Q");
        }
        return strwhere.toString();
    }
    private int analyFieldDate(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos)
    {
        String s_str_date=item.getValue();
        String e_str_date=item.getViewvalue();
        s_str_date=s_str_date.replaceAll("\\.","-");
        e_str_date=e_str_date.replaceAll("\\.","-");
      
        try {
            Date s_date=DateStyle.parseDate(s_str_date);
            Date e_date=DateStyle.parseDate(e_str_date);          	
	        /**起始日期及终止日期格式全对*/
	        if(s_date!=null&&e_date!=null) {
	            if(strexpr.length()==0) {
	              strexpr.append(pos);
	              strexpr.append("*");
	              strexpr.append(pos+1);
	            
	            } else {
	                strexpr.append("*(");                
	                strexpr.append(pos);
	                strexpr.append("*");
	                strexpr.append(pos+1);  
	                strexpr.append(")");
	            }
	            
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append(">=");
	            strfactor.append(item.getValue().replaceAll("-","."));
	            strfactor.append("`");
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append("<=");
	            strfactor.append(item.getViewvalue().replaceAll("-",".")); 
	            strfactor.append("`");   
	            return 2;
	        } else if (isnumber(s_str_date) || isnumber(e_str_date)) {
	            if(strexpr.length()==0) {
	              strexpr.append(pos);
	              if (isnumber(s_str_date) && isnumber(e_str_date)) {
	                  strexpr.append("*");
	                  strexpr.append(pos+1);
                  }
	              
	            } else {
	                strexpr.append("*(");                
	                strexpr.append(pos);
	                if (isnumber(s_str_date) && isnumber(e_str_date)) {
	                    strexpr.append("*");
	                    strexpr.append(pos+1);  
	                }
	                
	                strexpr.append(")");
	            }
	            
	            if (isnumber(s_str_date)) {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append(">=$YRS[");
	                strfactor.append(item.getValue());
	                strfactor.append("]`");
	                
	            }
	            
	            if (isnumber(e_str_date)) {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append("<=$YRS[");
	                strfactor.append(item.getViewvalue()); 
	                strfactor.append("]`");
	            }
	                
	            return 2;
	        } else {
	            if(strexpr.length()==0) {
	              strexpr.append(pos);
	            } else {
	                strexpr.append("*");                
	                strexpr.append(pos);
	            }
	            
	            strfactor.append(item.getItemid().toUpperCase());
	            strfactor.append("=");
	            strfactor.append("`");
	            return 1;
	        }
        } catch(Exception ex) {
        	return 1;
        }
    }
    /**
     * 
     * @param item
     * @param strexpr
     * @param strfactor
     * @param pos
     * @return
     */
    private int analyFieldCodeValue(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos,String strInfr)
    {
        String str_Hz=item.getViewvalue();
        String sql="";
        if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
        {
          sql="select codeitemid from organization where codesetid='"+item.getCodesetid()+"' and (codeitemdesc like '%"+str_Hz+"%' or codeitemid like '%"+str_Hz+"%')";	
        }else
        {
        	sql="select codeitemid from codeitem where codesetid='"+item.getCodesetid()+"' and codeitemdesc like '%"+str_Hz+"%'";	
        }
        //item.setValue(s_str_date);
        //item.setViewvalue(e_str_date);
       
        List list=ExecuteSQL.executeMyQuery(sql);
        if(list!=null&&list.size()>0)
        {
        	if(pos>1) {
                strexpr.append("*");
            }
        	strexpr.append("("); 
        	for(int i=0;i<list.size();i++)
        	{
        		LazyDynaBean rec=(LazyDynaBean)list.get(i);
        		String codeitemid=(String)rec.get("codeitemid");
        		strexpr.append(pos++);
        		strexpr.append("+");
        		strfactor.append(item.getItemid().toUpperCase());
        		strfactor.append("=");
        		strfactor.append(""+codeitemid+"");
        		strfactor.append("`");
        	}
        	strexpr.setLength(strexpr.length()-1);
        	strexpr.append(")");
        	return list.size();
        }else
        {
        	return 0;
        }
        
    }
    private int analyFieldCodeValue(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos,String strInfr,boolean blike)
    {
        String str_Hz=item.getViewvalue();
        String str_Cd=item.getValue();
        String sql="";
        if(str_Hz.equalsIgnoreCase(str_Cd)){//zgd 2014-6-19 str_Hz与str_Cd两个值相同，则为手动输入；不同，则为通过选择代码型获取的值，此时，直接用item.getValue()（编码）进行sql拼装。
        	if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
        	{
        		if(blike){
        			str_Hz=str_Hz.replaceAll("\\*", "").replaceAll("\\?", "");
        			if("b0110".equalsIgnoreCase(item.getItemid())) {
                        sql="select codeitemid from organization where codesetid in ('UM','UN') and (codeitemdesc like '%"+str_Hz+"%' or codeitemid like '%"+str_Hz+"%')";
                    } else {
                        sql="select codeitemid from organization where codesetid='"+item.getCodesetid()+"' and (codeitemdesc like '%"+str_Hz+"%' or codeitemid like '%"+str_Hz+"%')";
                    }
        		}else{
        			if(str_Hz.indexOf("*")!=-1||str_Hz.indexOf("?")!=-1){
        				str_Hz=str_Hz.replaceAll("\\*", "%").replaceAll("\\?", "_");
        				if("b0110".equalsIgnoreCase(item.getItemid())) {
                            sql="select codeitemid from organization where codesetid in ('UM','UN') and (codeitemdesc='"+str_Hz+"' or codeitemid='"+str_Hz+"')";
                        } else {
                            sql="select codeitemid from organization where codesetid='"+item.getCodesetid()+"' and (codeitemdesc='"+str_Hz+"' or codeitemid='"+str_Hz+"')";
                        }
        			}else{
        				if("b0110".equalsIgnoreCase(item.getItemid())){
        					if(item.getValue()==null||item.getValue().length()<=2) {
                                sql="select codeitemid from organization where codesetid in ('UM','UN') and (codeitemdesc='"+str_Hz+"' or codeitemid='"+str_Hz+"')";
                            } else {
        						// WJH 2013-9-30 一般情况下值是   UNXXX，  UMXXXX格式的，  目前有时候直接是 XXXXX。
        						String pixString = item.getValue().substring(0,2).toUpperCase();
        						if("UN".equals(pixString) || "UM".equals(pixString) || "@K".equals(pixString)){
        							sql="select codeitemid from organization where codeitemid='"+item.getValue().substring(2)+"'";
        						}else{
        							sql="select codeitemid from organization where codeitemid='"+item.getValue()+"'";
        						}
        					}
        				}else {
                            sql="select codeitemid from organization where codesetid='"+item.getCodesetid()+"' and (codeitemdesc='"+str_Hz+"' or codeitemid='"+str_Hz+"')";
                        }
        			}
        		}
        		
        		if(StringUtils.isNotEmpty(parentId)) {
        		    sql += " and codeitemid like '" + parentId + "%'";
                }
        	}else
        	{
        		if(blike) {
                    sql="select codeitemid from codeitem where codesetid='"+item.getCodesetid()+"' and (codeitemdesc like '%"+str_Hz+"%' or codeitemid like '%"+str_Hz+"%')";
                } else {
                    sql="select codeitemid from codeitem where codesetid='"+item.getCodesetid()+"' and (codeitemdesc='"+str_Hz+"' or codeitemid='"+str_Hz+"')";
                }
        	}
        	//item.setValue(s_str_date);
        	//item.setViewvalue(e_str_date);
        	
        	List list=ExecuteSQL.executeMyQuery(sql);
        	if(list!=null&&list.size()>0)
        	{
        		if(pos>1) {
                    strexpr.append("*");
                }
        		strexpr.append("("); 
        		for(int i=0;i<list.size();i++)
        		{
        			LazyDynaBean rec=(LazyDynaBean)list.get(i);
        			String codeitemid=(String)rec.get("codeitemid");
        			strexpr.append(pos++);
        			strexpr.append("+");
        			strfactor.append(item.getItemid().toUpperCase());
        			strfactor.append("=");
        			strfactor.append(""+codeitemid+"");
        			strfactor.append("`");
        		}
        		strexpr.setLength(strexpr.length()-1);
        		strexpr.append(")");
        		return list.size();
        	}else
        	{
        		if(pos>1) {
                    strexpr.append("*");
                }
        		strexpr.append("("); 
        		strexpr.append(pos++);
        		strexpr.append(")");
        		strfactor.append(item.getItemid().toUpperCase());
        		strfactor.append("=");
        		strfactor.append("#");
        		strfactor.append("`");
        		return 1;
        	}
        }else{
        	if(pos>1) {
                strexpr.append("*");
            }
    		strexpr.append("("); 
    		strexpr.append(pos++);
    		strexpr.append(")");
    		strfactor.append(item.getItemid().toUpperCase());
    		strfactor.append("=");
    		strfactor.append(""+str_Cd+"");
    		strfactor.append("`");
    		return 1;
        }
        
    }
    /**分析字符串是否为数值型*/
    private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue.replaceAll("-","."));
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
    }
    
    public ArrayList clearFieldValueList(ArrayList list)
    {
    	if(list==null||list.size()<=0) {
            return new ArrayList();
        }
    	ArrayList reList=new ArrayList();
    	for(int i=0;i<list.size();i++)
       {
              FieldItem item=(FieldItem)list.get(i);
              //System.out.println(item.getItemdesc());
              /**如果值未填的话，default是否为不查*/
              item.setValue("");
              item.setViewvalue("");
              reList.add(item);  
    	}
    	return reList;
    }
    
    public String getCodeInifValue(Connection conn,UserView userView)
    {
    	ContentDAO dao=new ContentDAO(conn);
    	String code="";
    	RowSet rs=null;
    	try {
			if(userView.getManagePrivCodeValue() !=null && userView.getManagePrivCodeValue().length()>0) {
                rs= dao.search("select min(b0110) as b0110 from b01 where b0110 is not null and b0110<>'' and b0110 like '" + userView.getManagePrivCodeValue() + "%'");
            } else {
                rs = dao.search("select min(b0110) as b0110 from b01 where b0110 is not null and b0110<>''");
            }
			if(rs.next()) {
                code=rs.getString("b0110");
            }
			//System.out.println("code " + code);
			if(code==null || code.trim().length()==0)
			{
				if(userView.getManagePrivCodeValue() !=null && userView.getManagePrivCodeValue().length()>0) {
                    rs = dao.search("select min(codeitemid) as b0110 from organization where codeitemid is not null and codeitemid<>'' and codeitemid like '" + userView.getManagePrivCodeValue() + "%' and codesetid<>'@K'");
                } else {
                    rs = dao.search("select min(codeitemid) as b0110 from organization where codeitemid is not null and codeitemid<>''  and codesetid<>'@K'");
                }
				if(rs.next()) {
                    code=rs.getString("b0110");
                }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
    }
    /**
     * 兼职子集没有A000字段时
     * @param userbase
     * @param code
     * @param kind
     * @param orgtype
     * @param personsortfield
     * @param personsort
     * @param part_unit
     * @param part_setid
     * @param part_appoint
     * @param where_n
     * @return
     * @throws GeneralException
     */
    public String getWhereSQL(Connection conn,UserView userView,String userbase,String code,boolean isCodeLike,String kind,String orgtype,String personsortfield
        	,String personsort,String part_unit,String part_setid,String part_appoint,String where_n)throws GeneralException 
        	   
    {
    	StringBuffer union_Sql=new StringBuffer();
    	StringBuffer wheresql=new StringBuffer();
    	String main_Tablename=userbase+"A01";
    	String replace_name="A";
    	if(where_n!=null&&where_n.length()>=0)
    	{
    		where_n=where_n.toLowerCase().replaceAll(main_Tablename.toLowerCase(), replace_name);
    	}
    	String strwhere="";   
    	String strsqlA010="select A.a0100";	
		DbNameBo dbNameBo=new DbNameBo(conn);
		if(userView.isSuper_admin()){                    //超级用户	
			//生成没有高级条件的from后的sql语句
			if(!"vorg".equals(orgtype))
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A");
				if("2".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((b0110 like '");
					    wheresql.append(code);
					    wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((b0110 = '");
					    wheresql.append(code);
					    wheresql.append("'");
					}					
				}			   
				else if("1".equals(kind)  && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e0122 like '"); 
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e0122 = '"); 
						wheresql.append(code);
						wheresql.append("'");
					}
					
				}				
				else if("0".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e01a1 like '");
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e01a1 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
					
					
				}else if("2".equals(kind))
				{
					wheresql.append(" where ((1=1");
				}
				else
				{
					wheresql.append(" where ((1=1");
				}
			    strwhere=wheresql.toString();
			    if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&!"ALL".equalsIgnoreCase(personsort)) {
                    strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
                }
			    union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);					    
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A",kind);
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}	
		    	union_Sql.append(")");		    	
		    	union_Sql.append(" "+where_n+")");
		    	/*if(part_unit!=null&&part_unit.length()>0&&part_setid!=null&&part_setid.length()>0)
		    	{
		    		union_Sql.append(" or ( exists(select 1 from "+userbase+""+part_setid+" where "+part_unit+"='"+code+"'");
		    		if(part_appoint!=null&&part_appoint.length()>0)
		    			union_Sql.append(" and "+part_appoint+"='0' ");
		    		union_Sql.append(" and "+userbase+""+part_setid+".a0100=A.a0100");
		    		union_Sql.append("  "+where_n+"))");
		    	}*/
			}else
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and b.state=1 ");				
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}
		    	union_Sql.append(")");
		    	union_Sql.append(" "+where_n+")");		    	
			}
			
		}
		else{   
			if(!"vorg".equals(orgtype))
			{
			  ArrayList fieldlist=new ArrayList();
		        try
		        {
		           String expr="1";
		           String factor="";
		           if("2".equals(kind))
		           {
		    		 factor="B0110=";
		    		 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					 }
					 else
					 {
					    expr="1+2";
					    factor+=code;
					    factor+="%`B0110=`";	
					 }
		           }
				   else if("1".equals(kind)){
				   	    factor="E0122="; 
				   	 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`E0122=`";	
					 }
				   }
				   else if("0".equals(kind)){
				   	    factor="E01A1=";
				   	    if(code!=null && code.length()>0)
					    {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					    }
					     else
					   {
					      expr="1+2";
					      factor+=code;
					      factor+="%`E01A1=`";	
					    }
				   }
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);		            
		            strwhere=getPrivSqlExists(strwhere,main_Tablename,"A");
		            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0 &&!"ALL".equalsIgnoreCase(personsort)) {
                        strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
                    }
		            union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);			
				    union_Sql.append(where_n);
				    //union_Sql.append(" UNION ");
				    DbNameBo dbbo=new DbNameBo(conn);
			    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,kind,isCodeLike,"A");
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or exists( ");		    		
			    		union_Sql.append(strWhere);	
			    		union_Sql.append(")");
			    	}			    	
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
			}else
	        {
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.state=1 ");	
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
			    String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}	
		    	union_Sql.append(")");
		    	union_Sql.append(" "+where_n+")");		    	
	        }
		}	
		//System.out.println(union_Sql);
		return union_Sql.toString();
    }
    
    /**
     * 兼职子集没有A000字段时
     * @param userbase
     * @param code
     * @param kind
     * @param orgtype
     * @param personsortfield
     * @param personsort
     * @param part_unit
     * @param part_setid
     * @param part_appoint
     * @param where_n
     * @return
     * @throws GeneralException
     */
    public String getWhereSQLExists(Connection conn,UserView userView,String userbase,String code,boolean isCodeLike,String kind,String orgtype,String personsortfield
        	,String personsort)throws GeneralException 
        	   
    {
    	StringBuffer union_Sql=new StringBuffer();
    	StringBuffer wheresql=new StringBuffer();
    	String main_Tablename=userbase+"A01";
    	String replace_name="A";    	
    	String strwhere="";   
    	String strsqlA010="select A.*";	
		DbNameBo dbNameBo=new DbNameBo(conn);
		if(userView.isSuper_admin()){                    //超级用户	
			//生成没有高级条件的from后的sql语句
			if(!"vorg".equals(orgtype))
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A");
				if("2".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((b0110 like '");
					    wheresql.append(code);
					    wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((b0110 = '");
					    wheresql.append(code);
					    wheresql.append("'");
					}					
				}			   
				else if("1".equals(kind)  && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e0122 like '"); 
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e0122 = '"); 
						wheresql.append(code);
						wheresql.append("'");
					}
					
				}				
				else if("0".equals(kind) && code!=null && code.length()>0)
				{
					if(isCodeLike)
					{
						wheresql.append(" where ((e01a1 like '");
						wheresql.append(code);
						wheresql.append("%'");
					}else
					{
						wheresql.append(" where ((e01a1 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
					
					
				}else if("2".equals(kind))
				{
					wheresql.append(" where ((1=1");
				}else if("H".equals(kind))
				{
					wheresql.append(" where ((e01a1 in('"+code.replaceAll(",", "','"));
					wheresql.append("')");
				}
				else
				{
					wheresql.append(" where ((1=1");
				}
			    strwhere=wheresql.toString();
            	// WJH 2013-4-26  显示人员分类时兼职的其他类别的也显示出来了。  分类的条件应该放到最外面
	            /* if(personsortfield!=null && !personsortfield.equalsIgnoreCase("null") && personsortfield.length()>0&&personsort!=null&&!personsort.equalsIgnoreCase("All"))
	            {   
			    	strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
	            } */
			    union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);	
			    //if(personsort==null||personsort.length()<=0)
			    {
			    	DbNameBo dbbo=new DbNameBo(conn);
			    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A",kind);
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or exists( ");		    		
			    		union_Sql.append(strWhere);	
			    		union_Sql.append(")");
			    	}
			    }			    	
		    	union_Sql.append(")");		    	
		    	union_Sql.append(")");		    	
			}else
			{
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and b.state=1 ");				
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
		    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}
		    	union_Sql.append(")");
		    	union_Sql.append(")");		    	
			}
			
		}
		else{   
			if(!"vorg".equals(orgtype))
			{
			  ArrayList fieldlist=new ArrayList();
		        try
		        {
		           String expr="1";
		           String factor="";
		           if("2".equals(kind))
		           {
		    		 factor="B0110=";
		    		 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					 }
					 else
					 {
					    expr="1+2";
					    factor+=code;
					    factor+="%`B0110=`";	
					 }
		           }
				   else if("1".equals(kind)){
				   	    factor="E0122="; 
				   	 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`E0122=`";	
					 }
				   }
				   else if("0".equals(kind)){
				   	    factor="E01A1=";
				   	    if(code!=null && code.length()>0)
					    {
						 factor+=code;
						 if(isCodeLike) {
                             factor+="%`";
                         } else {
                             factor+="`";
                         }
					    }
					     else
					   {
					      expr="1+2";
					      factor+=code;
					      factor+="%`E01A1=`";	
					    }
				   }
				   else if("H".equals(kind))
				   {
					   factor="";
					   expr="";
					   String[] codelist = code.split(",");
					   for(int i=0;i<codelist.length;i++){
						   factor+="E01a1="+codelist[i]+"`";
						   expr+=(i+1)+"+";
					   }
				   	   
					   expr = expr.substring(0, expr.length()-1);
					}
				   else
				   {
					   if("UN".equals(userView.getManagePrivCode()))
						{
						   factor="B0110=";
						}else if("UM".equals(userView.getManagePrivCode()))
						{
							   factor="E0121=";
						}else if("@K".equals(userView.getManagePrivCode()))
						{
							   factor="E01A1=";
						}
				   }
		           if(factor==null||factor.length()<=0) {
                       return "";
                   }
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		       //     strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,isCodeLike,true,fieldlist);	
		           /*将第四个参数写死，表示高级设定 部门=""时   sql肯定是= 不可能是like*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,false,true,fieldlist);		
		            strwhere=getPrivSqlExists(strwhere,main_Tablename,"A");
	            	// WJH 2013-4-26  显示人员分类时兼职的其他类别的也显示出来了。  分类的条件应该放到最外面
		            /* if(personsortfield!=null && !personsortfield.equalsIgnoreCase("null") && personsortfield.length()>0&&personsort!=null&&!personsort.equalsIgnoreCase("All"))
		            {   
				    	strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
		            } */
		            union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);
				    //if(personsort==null||personsort.length()<=0)
				    {
				    	DbNameBo dbbo=new DbNameBo(conn);				    	
				    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,kind,isCodeLike,"A");
				    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				    	{
				    		union_Sql.append(" or exists( ");		    		
				    		union_Sql.append(strWhere);	
				    		union_Sql.append(")");
				    	}
				    }				    			    	
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
			}else
	        {
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 A,vorganization v,t_vorg_staff b");
				wheresql.append(" where ((v.codeitemid='"+code+"'"); 
				wheresql.append(" and b.state=1 ");	
				wheresql.append(" and b.B0110= v.codeitemid ");
				wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
				wheresql.append(" and A.A0100=b.a0100");
				strwhere=wheresql.toString();;
				union_Sql.append(strsqlA010.toString());
			    union_Sql.append(strwhere);			
			    //union_Sql.append(" UNION ");
			    DbNameBo dbbo=new DbNameBo(conn);
			    String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
		    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
		    	{
		    		union_Sql.append(" or exists( ");		    		
		    		union_Sql.append(strWhere);	
		    		union_Sql.append(")");
		    	}	
		    	union_Sql.append(")");
		    	union_Sql.append(")");		    	
	        }
		}			
		
		String sqlString = union_Sql.toString();
		
		// WJH 2013-4-26  显示人员分类时兼职的其他类别的也显示出来了。  分类的条件应该放到最外面
	    if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&personsort!=null&&!"All".equalsIgnoreCase(personsort))
	    {
		    
	    	// strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
	    	int posWhere = sqlString.toLowerCase().indexOf(" where ");
	    	if (posWhere > 0){
	    		sqlString = sqlString.substring(0, posWhere) + " where " + personsortfield + "='" + personsort + "' and (" + sqlString.substring(posWhere + " where ".length()) + ")";
	    	} else {
	    		sqlString = sqlString + " where " + personsortfield + "='" + personsort + "'";
	    	}
	    }		
		
		return sqlString;
    }
    
    public String queryFieldHtml(UserView userView,Connection conn,ArrayList fieldlist,String querytype,int viewnum)
    {
    	StringBuffer html=new StringBuffer();    	
    	if(fieldlist==null||fieldlist.size()<=0) {
            return "";
        }
    	int flag=0;
    	int s=0;
    	html.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"RecordRow\" id='aa' style='display:none'>");
    	html.append("<tr class=\"trShallow1\">"); 
    	html.append("<td align=\"center\" colspan=\"4\" height='20' class=\"RecordRow\" nowrap>"); 
    	html.append(ResourceFactory.getProperty("train.b_plan.selectsearch"));
    	html.append("</td>");
    	html.append("</tr>");
    	if("1".equals(querytype))
    	{
    		 html.append("<tr class=\"trShallow1\">"); 
    		 html.append("<td align=\"right\" height='28' nowrap>"); 
    		 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.dbase")+"&nbsp;");          
     		 html.append("</td>");
     		 html.append("<td align=\"left\"  nowrap>");   
     		 ArrayList perlist=getUserBaseList(userView,conn);
     		 html.append("<select name=\"userbase\" size=\"1\">");
     		 for(int i=0;i<perlist.size();i++)
     		 {
     			 CommonData da=(CommonData)perlist.get(i);
     			 html.append("<option value=\""+da.getDataValue()+"\">"+da.getDataName()+"</option>");
     		 }
     		 html.append("</select>");
     		 html.append("</td>");
     		 FieldItem item=DataDictionary.getFieldItem("a0101");
     		 html.append("<td align=\"right\" height='28' nowrap>"); 
   		     html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+item.getItemdesc()+"&nbsp;");          
    		 html.append("</td>");
    		 html.append("<td align=\"left\"  nowrap>");   
    		 html.append("<input type=\"text\" name=\"select_name\" size=\"31\" maxlength=\""+item.getItemlength()+"\" value=\"\"  class=\"textbox\">");
    		 html.append("</td>");
    		 html.append("</tr>");
    	}
    	String code=userView.getManagePrivCodeValue();
    	for(int i=0;i<fieldlist.size();i++)
    	{
    		 FieldItem fielditem=(FieldItem)fieldlist.get(i);
    		 String codesetid=fielditem.getCodesetid();
    		 String itemtpye=fielditem.getItemtype();
    		 String itemid=fielditem.getItemid();
    		 if(itemtpye!=null&& "D".equalsIgnoreCase(itemtpye))//时间
    		 {
    			 if(flag==0)
        		 {
    				 html.append("<tr class=\"trShallow1\">"); 
                     flag=1;          
                }else{
                    flag=0;           
                }
        		html.append("<td align=\"right\" height='28' nowrap>");        
        		html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
        		html.append("</td>");
        		html.append("<td align=\"left\"  nowrap>");   
                
        		html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].value\" maxlength=\"10\" size=\"12\" value=\"\"  class=\"textbox\" title=\"输入格式：2008.08.08\">");
        		html.append(ResourceFactory.getProperty("label.query.to"));
        		html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].viewvalue\" maxlength=\"10\" size=\"12\" value=\"\"  class=\"textbox\" title=\"输入格式：2008.08.08\"/>");
        		html.append("<INPUT type=\"radio\" name=\""+itemid+"\"  checked=\"true\">");
        		html.append(ResourceFactory.getProperty("label.query.age"));
        		html.append("<INPUT type=\"radio\" name=\""+itemid+"\" id=\"day\">");
        		html.append(ResourceFactory.getProperty("label.query.day"));
        		html.append("</td>");    
        		if(flag==0) {
                    html.append("</tr>");
                }
    		 }else if(itemtpye!=null&& "N".equalsIgnoreCase(itemtpye))
    		 {
    			 if(flag==0)
        		 {
    				 html.append("<tr class=\"trShallow1\">"); 
                     flag=1;          
                }else{
                    flag=0;           
                }
    			html.append("<td align=\"right\" height='28' nowrap>");        
         		html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
         		html.append("</td>"); 
         		html.append("<td align=\"left\" nowrap>");   
         		html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].value\" size=\"31\" maxlength=\""+fielditem.getItemlength()+"\" value=\"\"  class=\"textbox\">");
         		html.append("</td>"); 
         		if(flag==0) {
                    html.append("</tr>");
                }
    		 }else if(itemtpye!=null&& "M".equalsIgnoreCase(itemtpye))
    		 {
    			 if(flag==0)
        		 {
    				 html.append("<tr class=\"trShallow1\">"); 
                     flag=1;          
                }else{
                    flag=0;           
                }
    			html.append("<td align=\"right\" height='28' nowrap>");        
         		html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
         		html.append("</td>"); 
         		html.append("<td align=\"left\" nowrap>");   
         		html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].value\" size=\"31\" maxlength=\""+fielditem.getItemlength()+"\" value=\"\"  class=\"textbox\">");
         		html.append("</td>"); 
         		if(flag==0) {
                    html.append("</tr>");
                }
    		 }else if(itemtpye!=null&& "A".equalsIgnoreCase(itemtpye))
    		 {
    			 if(codesetid!=null&&!"0".equals(codesetid))
    			 {
    				 if("UN".equalsIgnoreCase(codesetid))
    				 {     
    					 if(flag==0)
                		 {
    						 html.append("<tr class=\"trShallow1\">"); 
                             flag=1;          
                         }else{
                            flag=0;           
                         }
    					 html.append("<td align=\"right\" height='28' nowrap>");        
    	             	 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
    	             	 html.append("</td>"); 
    	             	 html.append("<td align=\"left\" nowrap>"); 
    					 html.append("<input type=\"hidden\" name=\"queryfieldlist["+i+"].value\" value=\"\" class=\"text\">  ");                             
   					     html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].viewvalue\" maxlength=\"50\" size=\"31\" value=\"\" onchange=\"fieldcode(this,2);\" class=\"textbox\">");
                         if("b0110".equalsIgnoreCase(itemid))
                         {
                        	 html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialogOrgInputPos(\"UN\",\"queryfieldlist["+i+"].viewvalue\",\""+code+"\",1);' align=\"absmiddle\"/>");
                         }else
                         {
                        	 html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialog(\""+codesetid+"\",\"queryfieldlist["+i+"].viewvalue\",\"0\");' align=\"absmiddle\"/>");
                         } 
                         html.append("</td>"); 
                         if(flag==0) {
                             html.append("</tr>");
                         }
    				 }else if("UM".equalsIgnoreCase(codesetid))
    				 {
    					 if(flag==0)
                		 {
    						 html.append("<tr class=\"trShallow1\">");   
                             
                             flag=1;          
                         }else{
                            flag=0;           
                         }
    					 html.append("<td align=\"right\" height='28' nowrap>");        
    	             	 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
    	             	 html.append("</td>"); 
    	             	 html.append("<td align=\"left\" nowrap>"); 
    					 html.append("<input type=\"hidden\" name=\"queryfieldlist["+i+"].value\" value=\"\" class=\"text\">  ");                             
   					     html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].viewvalue\" maxlength=\"50\" size=\"31\" value=\"\" onchange=\"fieldcode(this,2);\" class=\"textbox\">");
                         if("e0122".equalsIgnoreCase(itemid))
                         {
                        	 html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialogOrgInputPos(\"UM\",\"queryfieldlist["+i+"].viewvalue\",\""+code+"\",1);' align=\"absmiddle\"/>");
                         }else
                         {
                        	 html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialog(\""+codesetid+"\",\"queryfieldlist["+i+"].viewvalue\",\"0\");' align=\"absmiddle\"/>");
                         } 
                         html.append("</td>"); 
                         if(flag==0) {
                             html.append("</tr>");
                         }
    				 }else if("@K".equalsIgnoreCase(codesetid))
    				 {
    					 if(flag==0)
                		 {
                             if(i%2==0){               
                            	 html.append("<tr class=\"trShallow1\">");           
                             }
                             else
                             {
                            	html.append("<tr class=\"trDeep1\"> "); 
                             }
                             flag=1;          
                         }else{
                            flag=0;           
                         }
    					 html.append("<td align=\"right\" height='28' nowrap>");        
    	             	 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
    	             	 html.append("</td>"); 
    	             	 html.append("<td align=\"left\" nowrap>");   
    					 html.append("<input type=\"hidden\" name=\"queryfieldlist["+i+"].value\" value=\"\" class=\"text\">  ");                             
   					     html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].viewvalue\" maxlength=\"50\" size=\"31\" value=\"\" onchange=\"fieldcode(this,2);\" class=\"textbox\">");
   					     html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialog(\""+codesetid+"\",\"queryfieldlist["+i+"].viewvalue\",\"0\");' align=\"absmiddle\"/>");
   					     html.append("</td>"); 
                         if(flag==0) {
                             html.append("</tr>");
                         }
    				 }else
    				 {
    					 int count=getCodeSetidChildLen(codesetid,conn);
    					 if(count>viewnum)
    					 {
    						 if(flag==0)
                    		 {
                                html.append("<tr class=\"trShallow1\">");   
                                flag=1;          
                             }else{
                                flag=0;           
                             }
        					 html.append("<td align=\"right\" height='28' nowrap>");        
        	             	 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
        	             	 html.append("</td>"); 
        	             	 html.append("<td align=\"left\" nowrap>");   
        					 html.append("<input type=\"hidden\" name=\"queryfieldlist["+i+"].value\" value=\"\" class=\"text\">  ");                             
       					     html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].viewvalue\" maxlength=\"50\" size=\"31\" value=\"\" onchange=\"fieldcode(this,2);\" class=\"textbox\">");
       					     html.append("&nbsp;<img src=\"/images/code.gif\" onclick='openInputCodeDialog(\""+codesetid+"\",\"queryfieldlist["+i+"].viewvalue\",\"0\");' align=\"absmiddle\"/>");
       					     html.append("</td>"); 
                             if(flag==0) {
                                 html.append("</tr>");
                             }
    					 }else
    					 {
    						 if(flag==1)
    						 {
    							 html.append("<td colspan=\"2\">");
                                 html.append("</td>");
                                 html.append("</tr>");
    						 }
    						 html.append("<tr class=\"trShallow1\">"); 
                             
    						 html.append("<td align=\"right\" height='28' nowrap>");        
        	             	 html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
        	             	 html.append("<input type=\"hidden\" name=\"queryfieldlist["+i+"].value\" value=\"\" class=\"text\">  ");   
        	             	 html.append("</td>"); 
       	             	     html.append("<td align=\"left\" colspan=\"3\" nowrap>");
       	             	     html.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">");
       	             	     ArrayList codelist=getCodeSetidChildList(codesetid,conn);
       	             	     html.append("<tr>");
       	             	     html.append("<td>");
       	             	     for(int t=0;t<codelist.size();t++)
        	             	 {
        	             		/*if(t==8)
        	             		{
        	             			html.append("</td>");        	             			
                                    html.append("</tr>");
                                    html.append("<tr>");
              	             	    html.append("<td>");
        	             		}*/
        	             		CommonData vo=(CommonData)codelist.get(t);
        	             		html.append("&nbsp;");
        	             		html.append(""); 
        	             		html.append("&nbsp;");
        	             		html.append(vo.getDataName()); 
        	             		html.append("<input type=\"checkbox\" name=\""+fielditem.getItemid()+"\" value=\""+vo.getDataValue()+"\" onclick=\"fieldCheckBox('queryfieldlist["+i+"].value','"+fielditem.getItemid()+"',this);\">");
        	             		
        	             	 }
       	             	     html.append("</td>");
                             html.append("</tr>");
        	             	 html.append("</table>"); 
        	             	 html.append("</td>"); 
        	             	 html.append("</tr>");
        	             	 flag=0;      						 
    					 }
    				 }
    			 }else
    			 {
    				 if(flag==0)
            		 {
    					 html.append("<tr class=\"trShallow1\">"); 
                         flag=1;          
                    }else{
                        flag=0;           
                    }
        			html.append("<td align=\"right\" height='28' nowrap>");        
             		html.append("&nbsp;&nbsp;&nbsp;&nbsp;"+fielditem.getItemdesc()+"&nbsp;");          
             		html.append("</td>"); 
             		html.append("<td align=\"left\" nowrap>");   
             		html.append("<input type=\"text\" name=\"queryfieldlist["+i+"].value\" size=\"31\" maxlength=\""+fielditem.getItemlength()+"\" value=\"\"  class=\"textbox\">");
             		html.append("</td>"); 
             		if(flag==0) {
                        html.append("</tr>");
                    }
    			 }
    		 }
    		 
    	}
    	if(flag==1)
    	{
    		 html.append("<td colspan=\"2\">");
             html.append("</td>");
             html.append("</tr>");
    	}
    	html.append("<tr class=\"trShallow1\">"); 
    	html.append("<td align=\"center\" colspan=\"4\" height='20'  nowrap>"); 
    	
    	html.append("<input type=\"checkbox\" name=\"querlike2\" value=\"true\" onclick=\"selectCheckBox(this,'querylike');\">");
    	html.append(" <input type=\"hidden\" name=\"querylike\" value=\"0\" class=\"text\">  ");
    	
    	html.append(ResourceFactory.getProperty("label.query.like"));
    	html.append("&nbsp;");html.append("&nbsp;");
    	html.append(" <Input type='button' value=\""+ResourceFactory.getProperty("button.query")+"\" onclick=\"query('1');\" class='mybutton' />  ");
    	html.append("&nbsp;");
    	html.append("&nbsp;");
    	html.append(" <Input type='button' value=\""+ResourceFactory.getProperty("button.sys.cond")+"\" onclick='selectQ();' class='mybutton' />  ");
        html.append("</td>");
    	html.append("</tr>");
    	html.append("</table>");
    	return html.toString();
    }
    private int getCodeSetidChildLen(String codesetid,Connection conn)
    {
    	String sql="select count(*) aa from codeitem where codesetid = '"+codesetid+"'";
    	RowSet rs=null;
    	int count=0;
    	try
    	{
    		ContentDAO dao=new ContentDAO(conn);
    		rs=dao.search(sql);
    		if(rs.next()) {
                count=rs.getInt("aa");
            }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    	}
    	return count;
    }

	/**
	 * 获取某代码类有多少层代码项
	 * 
	 * @param codesetid
	 *            代码类的编号
	 * @param conn
	 *            数据库链接
	 * @return
	 */
	private int getCodeitemMaxLevel(String codesetid, Connection conn) {
		String sql = "select max(layer) layer from codeitem where codesetid=?";
		RowSet rs = null;
		int layer = 1;
		try {
			ContentDAO dao = new ContentDAO(conn);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(codesetid);
			rs = dao.search(sql, paramList);
			if (rs.next()) {
                layer = rs.getInt("layer");
            }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return layer;
	}
	
    public ArrayList getCodeSetidChildList(String codesetid,Connection conn)
    {
    	String sql="select codeitemid,codeitemdesc  from codeitem where codesetid = '"+codesetid+"'";
    	RowSet rs=null;
    	
    	ArrayList list=new ArrayList();
    	try
    	{
    		ContentDAO dao=new ContentDAO(conn);
    		rs=dao.search(sql);
    		CommonData vo=null;
    		while(rs.next())
    	    {
    			vo=new CommonData();
    			vo.setDataName(rs.getString("codeitemdesc"));
    			vo.setDataValue(rs.getString("codeitemid"));
    			list.add(vo);
    	    }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    	}
    	return list;
    }
    private ArrayList getUserBaseList(UserView userView,Connection conn)
    {
    	ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        if(dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
        else {
            userbase="usr";
        }
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0) {
                cond.append(",");
            }
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0) {
            cond.append("''");
        }
        cond.append(")");
        cond.append(" order by dbid");  
        ContentDAO dao=new ContentDAO(conn);
        RowSet rs=null;
        ArrayList list=new ArrayList();
        CommonData da=null;
        try {
			rs=dao.search(cond.toString());
			while(rs.next())
			{
				da=new CommonData();
				da.setDataName(rs.getString("dbname"));
				da.setDataValue(rs.getString("pre"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
           if(rs!=null) {
               try {
                   rs.close();
               } catch (SQLException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           }
		}
		return list;
    }
    
    public ArrayList getUserBaseList(UserView userView,String dbpre,Connection conn)
    {
    	ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        if(dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
        else {
            userbase="usr";
        }
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0) {
                cond.append(",");
            }
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0) {
            cond.append("''");
        }
        cond.append(")");
        cond.append(" order by dbid");  
        ContentDAO dao=new ContentDAO(conn);
        RowSet rs=null;
        ArrayList list=new ArrayList();
        CommonData da=null;
        try {
			rs=dao.search(cond.toString());
			while(rs.next())
			{
				if(dbpre.toUpperCase().indexOf(rs.getString("pre").toUpperCase())==-1) {
                    continue;
                }
				da=new CommonData();
				da.setDataName(rs.getString("dbname"));
				da.setDataValue(rs.getString("pre"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
           if(rs!=null) {
               try {
                   rs.close();
               } catch (SQLException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           }
		}
		return list;
    }
    /**
     * 
     * @param itemid
     * @param itemvaleu
     * @param oper 
     * @param log *且 +或
     * @return
     */
    public String factorMakeup(UserView userView,FieldItem item,String oper,String log,String dbper,String strInfr) {
        String strwhere = null;
        try {
            strwhere = factorMakeup(userView, item, oper, log, dbper, strInfr, false);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return strwhere; 
    }
    public String factorMakeup(UserView userView,FieldItem item,String oper,String log,String dbper,String strInfr, boolean like) throws GeneralException
    {
    	String itemid=item.getItemid();
    	String itemvaleu=item.getValue();
    	String fieldsetid=item.getFieldsetid();
    	if(itemvaleu==null||itemvaleu.length()<=0) {
            return "";
        }
    	String [] itemvaleus=itemvaleu.split("`");
    	String strwhere="";
    	int s=0;
    	StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
    	for(int i=0;i<itemvaleus.length;i++)
    	{
    	    String value=itemvaleus[i];
    	    if(value==null||value.length()<=0) {
                continue;
            }
    	    if(s!=0)
            {
                sexpr.append(log);
            }
    	    sexpr.append(s+1);
    	    sfactor.append(itemid.toUpperCase());
            sfactor.append(oper);
            sfactor.append(value);
    	    sfactor.append("`");
    	    
    	    s++;
    	}
    	ArrayList fieldlist=new ArrayList();
    	if((!userView.isSuper_admin()))
        {
           // strwhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+sfactor.toString(),dbper,false,fieldlist);    //userView.getPrivSQLExpression(sexpr.toString()+"|"+sfactor.toString(),dbpre,bhis,bresult,fieldlist);
    		FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),dbper, false,like,true,Integer.parseInt(strInfr),userView.getUserId());
            strwhere=factorslist.getSqlExpression();
        }
        else
        {
        	FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),dbper,false,like,true,Integer.parseInt(strInfr),userView.getUserId());
            strwhere=factorslist.getSqlExpression();
        }
    	/*if(strwhere!=null&&strwhere.length()>0&&strwhere.indexOf("FROM")!=-1)
		{    		
            if(strInfr.equals("1"))
            {
            	if(fieldsetid.equalsIgnoreCase("A01"))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.toLowerCase().substring(strwhere.indexOf("WHERE")+5)+")";
            		}
            		else
            		  strwhere=dbper+"A01"+".a0100 in(select "+dbper+"A01"+".a0100 "+strwhere.toLowerCase()+")";
            	}  
            	else
            	  //strwhere=" exists (select "+dbper+fieldsetid+".a0100 "+strwhere.toLowerCase()+" and "+dbper+fieldsetid+".a0100="+dbper+"A01"+".a0100)";
            	  strwhere=dbper+"A01"+".a0100  in (select a0100 "+strwhere.toLowerCase()+")";
            }else if(strInfr.equals("2"))
            {
            	if(fieldsetid.equalsIgnoreCase("B01"))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.toLowerCase().substring(strwhere.indexOf("WHERE")+5)+")";
            		}else
            		 strwhere="B01"+".b0110 in(select "+"B01"+".b0110 "+strwhere.toLowerCase()+")";;
            	}  
            	else
            	    strwhere="exists(select "+fieldsetid+".b0110 "+strwhere.toLowerCase()+" and "+fieldsetid+".b0110=B01.b0110)";
            }else if(strInfr.equals("3"))
            {
            	if(fieldsetid.equalsIgnoreCase("K01"))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.toLowerCase().substring(strwhere.indexOf("WHERE")+5)+")";
            		}else
            		    strwhere="K01"+".e01a1 in(select "+"K01"+".e01a1 "+strwhere.toLowerCase()+")";;
            	}  
            	else
            		strwhere="exists(select "+fieldsetid+".e01a1 "+strwhere.toLowerCase()+" and "+fieldsetid+".e01a1=K01.e01a1)";
            	//strwhere="K01"+".e01a1 in(select "+"K01"+".e01a1 "+strwhere.toLowerCase()+")";;
            }    
			return strwhere;
		}*/
    	if(strwhere!=null&&strwhere.length()>0&&strwhere.indexOf("FROM")!=-1)//zgd 查询中代码项字符区分大小写。上面的方法无法将数据库中的代码项值转为小写
		{    		
            if("1".equals(strInfr))
            {
            	if("A01".equalsIgnoreCase(fieldsetid))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.substring(strwhere.indexOf("WHERE")+5)+")";
            		}
            		else {
                        strwhere=dbper+"A01"+".a0100 in(select "+dbper+"A01"+".a0100 "+strwhere+")";
                    }
            	}  
            	else
            	  //strwhere=" exists (select "+dbper+fieldsetid+".a0100 "+strwhere.toLowerCase()+" and "+dbper+fieldsetid+".a0100="+dbper+"A01"+".a0100)";
                {
                    strwhere=dbper+"A01"+".a0100  in (select a0100 "+strwhere+")";
                }
            }else if("2".equals(strInfr))
            {
            	if("B01".equalsIgnoreCase(fieldsetid))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.substring(strwhere.indexOf("WHERE")+5)+")";
            		}else {
                        strwhere="B01"+".b0110 in(select "+"B01"+".b0110 "+strwhere+")";
                    }
                    ;
            	}  
            	else {
                    strwhere="exists(select "+fieldsetid+".b0110 "+strwhere+" and "+fieldsetid+".b0110=B01.b0110)";
                }
            }else if("3".equals(strInfr))
            {
            	if("K01".equalsIgnoreCase(fieldsetid))
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            		{
            			strwhere="("+strwhere.substring(strwhere.indexOf("WHERE")+5)+")";
            		}else {
                        strwhere="K01"+".e01a1 in(select "+"K01"+".e01a1 "+strwhere+")";
                    }
                    ;
            	}  
            	else {
                    strwhere="exists(select "+fieldsetid+".e01a1 "+strwhere+" and "+fieldsetid+".e01a1=K01.e01a1)";
                }
            	//strwhere="K01"+".e01a1 in(select "+"K01"+".e01a1 "+strwhere.toLowerCase()+")";;
            }    
			return strwhere;
		}
    	return strwhere;
    }
    public String whereA0101(UserView userView,Connection conn,String userbase,String a0101value,String querylike)throws GeneralException
    {
    	 if(a0101value==null||a0101value.length()<=0) {
             return "";
         }
    	 /**【11398】员工管理：信息浏览 查询显示 在姓名框中搜出的信息 显示照片为空 点击显示信息后也为空 
    	  * mod by sunming 2015-7-24
    	  */
//    	 String select_name=PubFunc.getStr(a0101value);
    	 String select_name=PubFunc.keyWord_reback(a0101value);
    	 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		 String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		 String strwhere="";
    	 boolean bresult=true;
     	 boolean blike=false;   
     	 StringBuffer strfactor=new StringBuffer();
     	 /*if(a0101value.indexOf("*")!=-1||a0101value.indexOf("?")!=-1)
     	 {
     		strfactor.append("A0101="+a0101value+"`");  
     		querylike="0";
     	 }     	    
     	 else
     	 {*/
     	 /**【11398】员工管理：信息浏览 查询显示 在姓名框中搜出的信息 显示照片为空 点击显示信息后也为空 
    	  * mod by sunming 2015-7-24
    	  */
//     	strfactor.append("A0101="+a0101value+"`");
  		strfactor.append("A0101="+select_name+"`");
     	 //}
     		if(querylike!=null&& "1".equals(querylike)&&select_name.indexOf("|")==-1){//zgd 2014-3-18 多值查询下且点击了模糊查询时，blike还是为false，不做模糊查询处理。
     			blike=true;
     		}
     	FieldItem fi = DataDictionary.getFieldItem(pinyin_field);
		 if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )&&fi!=null&&!"0".equals(fi.getUseflag())&&fi.getUseflag().length()>0)
		 {
				PubFunc pf = new PubFunc();		
				String pinyinstr = pf.getPinyinCode(select_name.trim());				
				/*if(querylike!=null&&querylike.equals("1"))
				{
					strwhere=" ("+userbase+"A01.a0101 like '%"+select_name.trim()+"%' or "+userbase+"A01."+pinyin_field+" like '"+pinyinstr+"%' ) ";
				}else*/
				{
					strfactor.append(pinyin_field+"="+pinyinstr+"`");
					if(!userView.isSuper_admin())
			         {
			             //strwhere=userView.getPrivSQLExpression("1+2"+"|"+strfactor.toString(),userbase,false,bresult,new ArrayList());
			             FactorList factorlist=new FactorList("1+2",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
				         strwhere=factorlist.getSqlExpression();
			         }
			         else
			         {
			         	FactorList factorlist=new FactorList("1+2",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
			         	strwhere=factorlist.getSqlExpression();
			         }
				}
				
		  }else
		  {
				/*if(querylike!=null&&querylike.equals("1"))
				{
					strwhere=" "+userbase+"A01.a0101 like '%"+select_name.trim()+"%'";
				}else*/
				{
					 if(!userView.isSuper_admin())
			         {
			            // strwhere=userView.getPrivSQLExpression("1"+"|"+strfactor.toString(),userbase,false,blike,bresult,new ArrayList());
			           //wangrd 2014-02-24
					     FactorList factorlist=new FactorList("1",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
	                        strwhere=factorlist.getSqlExpression();
			         }
			         else
			         {
			         	FactorList factorlist=new FactorList("1",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
			         	strwhere=factorlist.getSqlExpression();
			         }
				}
				
		 }		
		 if(strwhere!=null&&strwhere.indexOf("WHERE")!=-1)
		 {
			 //strwhere=strwhere.substring(strwhere.indexOf("WHERE")+5);
			 strwhere = userbase+"A01.a0100 "+" in (select "+userbase+"A01.a0100 "+strwhere+")";//LiWeichao 2011-07-18 14:53:23
		 }
    	 return strwhere;
    }
    
    /**
     * 
     * @param userView
     * @param conn
     * @param userbase
     * @param a0101value
     * @param querylike
     * @return
     * @throws GeneralException
     */
    public String whereA0101NoPriv(UserView userView,Connection conn,String userbase,String a0101value,String querylike)throws GeneralException
    {
    	 if(a0101value==null||a0101value.length()<=0) {
             return "";
         }
    	 String select_name=PubFunc.getStr(a0101value);
    	 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		 String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		 String strwhere="";
    	 boolean bresult=true;
     	 boolean blike=false;   
     	 StringBuffer strfactor=new StringBuffer();
     	 if(a0101value.indexOf("*")!=-1)
     	 {
     		strfactor.append("A0101="+a0101value+"`");  
     		querylike="0";
     	 }     	    
     	 else
     	 {
     		strfactor.append("A0101=*"+a0101value+"*`");
     	 }
     		
		 if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
		 {
				PubFunc pf = new PubFunc();		
				String pinyinstr = pf.getPinyinCode(select_name.trim());				
				if(querylike!=null&& "1".equals(querylike))
				{
					strwhere=" ("+userbase+"A01.a0101 like '%"+select_name.trim()+"%' or "+userbase+"A01."+pinyin_field+" like '"+pinyinstr+"%' ) ";
				}else
				{
					strfactor.append(pinyin_field+"="+pinyinstr+"*`");
//					if(!userView.isSuper_admin())
//			         {
//			             strwhere=userView.getPrivSQLExpression("1+2"+"|"+strfactor.toString(),userbase,false,bresult,new ArrayList());
//			            
//			         }
//			         else
//			         {
			         	FactorList factorlist=new FactorList("1+2",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
			         	strwhere=factorlist.getSqlExpression();
//			         }
				}
				
		  }else
		  {
				if(querylike!=null&& "1".equals(querylike))
				{
					strwhere=" "+userbase+"A01.a0101 like '%"+select_name.trim()+"%'";
				}else
				{
//					 if(!userView.isSuper_admin())
//			         {
//			             strwhere=userView.getPrivSQLExpression("1"+"|"+strfactor.toString(),userbase,false,bresult,new ArrayList());
//			            
//			         }
//			         else
//			         {
			         	FactorList factorlist=new FactorList("1",strfactor.toString(),userbase,false,blike,bresult,1,userView.getUserId());
			         	strwhere=factorlist.getSqlExpression();
//			         }
				}
				
		 }		
		 if(strwhere!=null&&strwhere.indexOf("WHERE")!=-1)
		 {
			 strwhere=strwhere.substring(strwhere.indexOf("WHERE")+5);
		 }
    	 return strwhere;
    }
    
    /**
	 * 组合SQL语句头
	 * 
	 * @param set 涉及指标集合
	 * @return 
	 */
	public String getSqlTitle(HashSet set,String dbPre,Connection conn) {
		StringBuffer sql = new StringBuffer();	
		if(set==null) {
            return "";
        }
		Iterator it = set.iterator();
		ContentDAO dao=new ContentDAO(conn);
		while(it.hasNext()){
			String itemid = (String) it.next();
			String itemdesc = "";
			if(itemid == null || "".equals(itemid)){
				continue;
			}
			if("B0110".equalsIgnoreCase(itemid)){
				itemdesc = "单位";
			}else if("E0122".equalsIgnoreCase(itemid)){
				itemdesc = "部门";
			}else if("E01A1".equalsIgnoreCase(itemid)){
				itemdesc = "职位";
			}else{
				//指标描述信息
				String strsql = "select itemdesc  from fielditem where Upper(itemid)='"+ itemid.toUpperCase() + "'";
				itemdesc = this.getItemDesc(strsql,dao);
			}
			sql.append(" , ");
			sql.append(itemid);
			//sql.append("."+itemid+" as ");
			sql.append(".codeitemdesc as ");
			switch(Sql_switcher.searchDbServer())
		    {
				  case Constant.MSSQL:
			      {
			        sql.append("'"+itemdesc+"' ");
			        break;
			      }
				  case Constant.ORACEL:
				  { 
					  sql.append("\""+itemdesc+"\" ");
					  break;
				  } case Constant.DB2:
				  {
					  sql.append("\""+itemdesc+"\" ");
					  break;
				  }
		    }
		}
		if (sql.length() > 0) {// 删除最后一个逗号
			sql.deleteCharAt(sql.length() - 1);
		}
		return sql.toString();
	}
    public String getItemDesc(String sql,ContentDAO dao) {
		
		String itemdesc = "";
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				itemdesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return itemdesc;
	}
    public String getFieldSetID(String sql,ContentDAO dao) {
    	RowSet rs=null;
		String fieldsetid = "";
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				fieldsetid = rs.getString("fieldsetid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return fieldsetid;
	}
    /**
	 * 判断指标是否式代码型
	 * 
	 * @param fielditemid
	 * @return
	 */
	public boolean isCodeitem(String fielditemid,ContentDAO dao) {
		boolean b = false;
		if("B0110".equalsIgnoreCase(fielditemid)){
			return false;
		}else if("E0122".equalsIgnoreCase(fielditemid)){
			return false;
		}else if("E01A1".equalsIgnoreCase(fielditemid)){
			return false;
		}
		String sql = "select codesetid  from fielditem where  itemid='"
				+ fielditemid + "'";
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				String codesetid = rs.getString("codesetid");
				if ("0".equals(codesetid)) {
					return b;
				} else {
					b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return b;
	}
    public boolean sqlCheckFactor(String type,String dbpre,String expr,String factor,UserView userView,Connection conn)throws GeneralException
    {
    	String strwhere="";
        boolean bhis=false;       
        boolean blike=false;
        boolean bresult=false;
        String filterfield="";
        boolean isCorrect=false;
        ArrayList fieldlist=new ArrayList(); 
        /**1人员　2:单位 3:职位*/
        ContentDAO dao=new ContentDAO(conn);
        try
        {
	        if("1".equals(type))
	        {
	            
	        		if((!userView.isSuper_admin()))
	                {
	                    strwhere=userView.getPrivSQLExpression(expr+"|"+factor,dbpre,bhis,blike,bresult,fieldlist);
	                   
	                }
	                else
	                {
	                    FactorList factorlist=new FactorList(expr,factor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
	                    
	                    strwhere=factorlist.getSqlExpression();
	                } 
	        }else if("2".equals(type))
	        {
	        	
	            FactorList factorlist=new FactorList(expr,factor,"",bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
	            fieldlist=factorlist.getFieldList();
	            strwhere=factorlist.getSqlExpression();
	            /**权限分析及过滤*/
	           
	        }
	        else if("3".equals(type))
	        {
	           
	            FactorList factorlist=new FactorList(expr,factor,"",bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
	            fieldlist=factorlist.getFieldList();
	            strwhere=factorlist.getSqlExpression();            
	        }      
	        if(userView.getStatus()==0)
	        {
	        	String sql="select 1 "+strwhere;
	            	dao.search(sql);
	            	isCorrect=true;
	        }else {
                isCorrect=true;
            }
        }catch(Exception e)
        {
        	e.printStackTrace();
        	//throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }
    /**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String nflag,String templetid,Connection conn)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag="+nflag+" and templetid="+templetid+" ");
			
			ContentDAO dao=new ContentDAO(conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}
		}catch(Exception e)
		{
		}
		return fieldlist;
	}
	/**
	 * 得到构建子集名称
	 * @param fieldsetid
	 * @return
	 */
	public String getFieldSetCustomdesc(Connection conn,String fieldsetid)
	{
		String sql="select fieldsetdesc,customdesc from fieldset where fieldsetid='"+fieldsetid+"'";		
		RowSet rs=null;
		String desc=null;
		ContentDAO dao=new ContentDAO(conn);
		try
		{
			rs=dao.search(sql);
			if(rs.next())
			{
				desc=rs.getString("customdesc");
				desc=desc!=null&&desc.length()>0?desc:rs.getString("fieldsetdesc");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return desc;
	}
	/**
	 * 保存查询结果
	 * @param type
	 * @param dbpre
	 */
	public void saveQueryResult(String type, String dbpre,String sql,Connection conn,UserView userView)throws GeneralException
	{
		if(userView.getStatus()==4)
		{
			try
			{
				String tabldName = "t_sys_result";
				Table table = new Table(tabldName);
				DbWizard dbWizard = new DbWizard(conn);
				if (!dbWizard.isExistTable(table)) {
					return;
				}
				/**=0 人员 1 单位 2 岗位*/

				String flag="0";
				if("2".equalsIgnoreCase(type))
				{
					flag="1";
				}
				else if("3".equalsIgnoreCase(type))
				{
					flag="2";
				}
				String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
				if("1".equalsIgnoreCase(type))
				{
					str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";
				}
				ContentDAO dao = new ContentDAO(conn);
				dao.delete(str, new ArrayList());
				StringBuffer buf_sql = new StringBuffer("");
				if ("1".equals(type)) {
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from ("+sql+") myset");
				} else if ("2".equals(type)) {
					buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag,nbase) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'B',b0110 as obj_id,1 as flag,'B' from ("+sql+") myset");
				} else if ("3".equals(type)) {
					buf_sql.append("insert into " + tabldName+ " (username,nbase,obj_id,flag,nbase)");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'K',e01a1 as obj_id,2 as flag,'K' from("+sql+") myset");
				}
				dao.insert(buf_sql.toString(), new ArrayList());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
	    	if("2".equals(type)) {
                dbpre="B";
            }
	    	if("3".equals(type)) {
                dbpre="K";
            }
	    	String tablename=userView.getUserName()+dbpre+"result";

	    	AutoCreateQueryResultTable.execute(conn, tablename, type);
	    	AutoCreateQueryResultTable.addPrimaryKey(conn, tablename, type);
	    	StringBuffer inssql=new StringBuffer();
	    	ContentDAO dao=new ContentDAO(conn);
	    	try
	    	{
		    	inssql.append("insert into ");
		    	inssql.append(tablename);
		    	inssql.append("(");
		    	if("2".equals(type))
			    {
		    		inssql.append("B0110)");
			    	inssql.append(" select ");
			    	inssql.append("B0110  from (");
			    	inssql.append(sql);
				    inssql.append(") myset");
		    	}
	    		else if("3".equals(type))
	    		{
		    		inssql.append("E01A1)");
		    		inssql.append(" select ");
		    		inssql.append("E01A1  from (");
		    		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
	    		else 
	    		{
		    		inssql.append("A0100)");
		    		inssql.append(" select ");
		    		inssql.append("A0100  from (");
		    		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
	    		dao.update("delete from "+tablename);			
	    		dao.update(inssql.toString());
	    	}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(ex);
	    	}
		}
	}
	public String getPrivSqlExists(String chwhere,String old_table,String replace_table)
	{
		/*String s_top=strwhere.substring(0,strwhere.indexOf(old_table)+6);
        strwhere=strwhere.substring(strwhere.indexOf(old_table)+6);		            
        strwhere=strwhere.replaceAll(old_table, replace_table);
        strwhere=s_top+" "+replace_table+" "+strwhere.substring(0,strwhere.indexOf("WHERE")+5)+"("+strwhere.substring(strwhere.indexOf("WHERE")+5)+")";
        */
		String s_top=chwhere.substring(0,chwhere.indexOf(old_table)+6);
        chwhere=chwhere.substring(chwhere.indexOf(old_table)+6);
        chwhere=chwhere.replaceAll(old_table+"\\.", replace_table+".");
        chwhere=chwhere.replaceAll(old_table, old_table+" "+replace_table);
        chwhere=s_top+" "+replace_table+" "+chwhere;
        //System.out.println(chwhere);
		return chwhere;
	}
	/**
	 * 得到唯一性指标
	 * @param conn
	 * @return
	 */
	public FieldItem getOnlyFieldItem(Connection conn)
	{
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);		
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标		
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		if(onlyname==null||onlyname.length()<=0) {
            return null;
        }
		 if(uniquenessvalid==null) {
             uniquenessvalid="";
         }
		 String uniquenesscheck="";		 
		 if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
			 return null;
		 }
		 else{
			FieldItem item= DataDictionary.getFieldItem(onlyname);
            if(item!=null) {
                return item;
            } else {
                return null;
            }
		 }
		
	}
	/**
	 * 取得某个人的分类指标的值
	 * @param nbase
	 * @param a0100
	 * @param fenlei_value
	 * @param dao
	 * @return
	 */
	public String getOneselfFenleiType(String nbase,String a0100,String fenlei_value,ContentDAO dao)
	{
		if(fenlei_value==null||fenlei_value.length()<=0)
		{
			fenlei_value=fenleiPriv(dao);
		}
		if(fenlei_value==null||fenlei_value.length()<=0)
		{
			return "";
		}
		if(a0100==null||a0100.length()<=0|| "A0100".equalsIgnoreCase(a0100)) {
            return "";
        }
		String arrs[]=fenlei_value.split(",");
	    if(arrs==null||arrs.length!=2) {
            return "";
        }
	    String field=arrs[0];
	    String type="";
	    RowSet rs=null;
	    try
	    {
	    	rs=dao.search("select "+field+" as field from "+nbase+"A01 where a0100='"+a0100+"'");
	    	if(rs.next()) {
                type=rs.getString("field");
            }
	    	type=type!=null?type:"";
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	    {
	    	if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    }
	    return type;
	}
	private String fenleiPriv(ContentDAO dao)
    {
    	String sql="select * from constant where upper(constant)='SYS_INFO_PRIV' and type='1'";
    	String value="";
		RowSet rs=null;
		try {
			rs=dao.search(sql);			
			if(rs.next())
			{
				value=rs.getString("str_value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return value;
    		
    }
	 /**
	  * 得到某个子集下的某个分类指标权限
	  * @param userview
	  * @param name
	  * @param type
	  * @return
	  */
	 public ArrayList getSubPrivFieldList(UserView userview,String name,String type) {
	     return getSubPrivFieldList(userview, name, type, 0);
	 }
	 /**
      * 得到某个子集下的某个分类指标权限
      * @param userview
      * @param name 
      * @param type
      * @param isEmpRole
      * @return
      */
     public ArrayList getSubPrivFieldList(UserView userview,String name,String type, int isEmpRole)
     {
        ArrayList list = DataDictionary.getFieldList(name, 1);
        if (list == null) {
            return null;
        }
        FieldItem item = null;
        String fieldname = null;
        ArrayList privlist = new ArrayList();
        if(!userview.isSuper_admin()) {
        	 if(1 == isEmpRole) {
                 privlist=userview.getPrivFieldList(name, 0);
             } else {
            	 for (int i = 0; i < list.size(); ++i) {
            		 item = (FieldItem)list.get(i);
            		 fieldname = item.getItemid().toUpperCase();
            		 //1读，2写，-1无权限
            		 String flag = userview.analyseSubFieldPriv(type,fieldname)+"";
            		 if ("-1".equals(flag)) {
                         continue;
                     }
            		 item.setPriv_status(Integer.parseInt(flag));
            		 privlist.add(item);
            	 }
             }
        }else {
            if(1 == isEmpRole) {
                privlist=userview.getPrivFieldList(name, 0);
            } else {
                privlist=userview.getPrivFieldList(name);
            }
        }
        
         return privlist;
     }
	 /**
	  * 得到某个分类下子集的权限
	  * @param userview
	  * @param type
	  * @param domain
	  * @return
	  */
	 public ArrayList getPrivFieldSetList(UserView userview,String type,int domain) {
	     return getPrivFieldSetList(userview, type, domain, 0);     
	 }
	 /**
      * 得到某个分类下子集的权限
      * @param userview
      * @param type
      * @param domain
      * @return
      */
     public ArrayList getPrivFieldSetList(UserView userview,String type,int domain, int isEmpRole)
     {
         ArrayList list = DataDictionary.getFieldSetList(1, domain);
         if (list == null) {
             return null;
         }
         
         ArrayList privlist = new ArrayList();
         if(!userview.isSuper_admin()) {
        	 if(1 == isEmpRole) {
                 privlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
             } else {
        		 for (int i = 0; i < list.size(); ++i) {
        			 FieldSet fieldset = (FieldSet)list.get(i);
        			 int flag = userview.analyseSubTablePriv(type,fieldset.getFieldsetid());
        			 if(flag==-1) {
                         continue;
                     }
        			 
        			 fieldset.setPriv_status(flag);
        			 privlist.add(fieldset);
        		 }
        	 }
         } else {
             if(1 == isEmpRole) {
                 privlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
             } else {
                 privlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
             }
         }       
         return privlist;
     }
	 
	 
	 public String combineSql(UserView userView,ArrayList list,String like,String dbpre,String strInfr) throws GeneralException{
		 
		 int j=1;
	    	boolean bresult=true;
	    	boolean blike=false;    	
	    	if("1".equals(like)) {
                blike=true;
            }
	        StringBuffer strexpr=new StringBuffer();
	        StringBuffer strfactor=new StringBuffer();
	        ArrayList checklist=new ArrayList();
	        for(int i=0;i<list.size();i++)
	        {
	            FieldItem item=(FieldItem)list.get(i);
	            //System.out.println(item.getItemdesc());
	            /**如果值未填的话，default是否为不查*/
	            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype()))) {
                    continue;
                }
	            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype()))) {
                    continue;
                }
	            if(!"0".equals(item.getCodesetid())&&item.getValue()!=null&&item.getValue().indexOf("`")!=-1)
	    		{
	    			String checkwhere=factorMakeup(userView,item,"=","+",dbpre,strInfr);
	    			if(checkwhere!=null&&checkwhere.length()>0) {
                        checklist.add(checkwhere);
                    }
	    		}else
	    		{
	    			if("D".equals(item.getItemtype()))
	                {
	                    int sf=analyFieldDate(item,strexpr,strfactor,j);
	                    if(sf==1)
	                    {
	                    	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
	                    }
	                    j=j+sf;
	                }else if(!"0".equals(item.getCodesetid())&&item.getViewvalue()!=null&&item.getViewvalue().length()>0/*&&blike*/)
	                {
	                	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr,blike);
	                	j=j+sf;
	                }
	                else
	                {
	    	            /**组合表达式串*/
	    	            if(j==1)
	    	            {
	    	                strexpr.append(j);
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");
	    	                strexpr.append(j);                
	    	            }
	    	            
	    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
	    	            {
	    	            		String q_v=item.getValue().trim();
	    	            		
	    	            		if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
	    	                    {
	    			                
	    	                    	strfactor.append(item.getItemid().toUpperCase());
	    			                if("0".equals(item.getCodesetid())) {
                                        strfactor.append("=*");
                                    } else {
                                        strfactor.append("=");
                                    }
	    			                strfactor.append(PubFunc.getStr(item.getValue()));
	    			                strfactor.append("*`");	   			                	  
	    	                    }
	    	                    else
	    	                    {
	    	                    	
	    			                strfactor.append(item.getItemid().toUpperCase());
	    			                strfactor.append("=");
	    			                strfactor.append(PubFunc.getStr(item.getValue()));
	    			                strfactor.append("`");	  	                        
	    	                    }
	    	            }
	    	            else
	    	            {
	    	                strfactor.append(item.getItemid().toUpperCase());
	    	                strfactor.append("=");
	    	                strfactor.append(PubFunc.getStr(item.getValue()));
	    	                strfactor.append("`");
	    	            }
	    	            ++j;	            
	                }
	    		}
	            
	        }
	        System.out.println(strexpr.toString());
	        System.out.println(strfactor.toString());
	        ArrayList fieldlist=new ArrayList();
	        StringBuffer strwhere=new StringBuffer();
	        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
	        	factorlist.setSuper_admin(true);
	        	strwhere.append(factorlist.getSqlExpression());
	        
	        StringBuffer checksql=new StringBuffer();
	        
	        for(int i=0;i<checklist.size();i++)
	        {
	        	checksql.append(" and ("+checklist.get(i)+")");
	        }       
	        boolean isWhere=true;
	        if(strwhere!=null&&strwhere.toString().trim().equalsIgnoreCase("FROM "+dbpre+"A01")) {
                isWhere=false;
            } else if(strwhere!=null&& "FROM B01".equalsIgnoreCase(strwhere.toString().trim())) {
                isWhere=false;
            } else if(strwhere!=null&& "FROM K01".equalsIgnoreCase(strwhere.toString().trim())) {
                isWhere=false;
            }
	        if(checklist==null||checklist.size()<=0)
	        {
	        	if(!isWhere) {
                    return"";
                }
	        }else
	        {
	        	if(!isWhere)
	        	{
	        		strwhere.append(" WHERE 1=1 ");
	        		strwhere.append(checksql.toString());
	        	}else {
                    strwhere.append(checksql.toString());
                }
	        }        
	        return strwhere.toString();
	 }
	 
	 public String getSql(UserView userView,ArrayList list,String like,String dbpre,String strInfr) throws GeneralException
	    {
		   
	        int j=1;
	    	boolean bresult=true;
	    	boolean blike=false;  
	    	
	    	if("1".equals(like)) {
                blike=true;
            }
	        StringBuffer strexpr=new StringBuffer();
	        StringBuffer strfactor=new StringBuffer();
	        ArrayList checklist=new ArrayList();
	        for(int i=0;i<list.size();i++)
	        {
	            FieldItem item=(FieldItem)list.get(i);
	            //System.out.println(item.getItemdesc());
	            /**如果值未填的话，default是否为不查*/
	            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype()))) {
                    continue;
                }
	            if(((item.getValue()==null|| "".equals(item.getValue()))&&(item.getViewvalue()==null|| "".equals(item.getViewvalue())))&&("D".equals(item.getItemtype()))) {
                    continue;
                }
	            if(!"0".equals(item.getCodesetid())&&item.getValue()!=null&&item.getValue().indexOf("`")!=-1)
	    		{
	    			String checkwhere=factorMakeup(userView,item,"=","+",dbpre,strInfr);
	    			if(checkwhere!=null&&checkwhere.length()>0) {
                        checklist.add(checkwhere);
                    }
	    		}else
	    		{
	    			if("D".equals(item.getItemtype()))
	                {
	                    int sf=analyFieldDate(item,strexpr,strfactor,j);
	                    if(sf==1)
	                    {
	                    	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
	                    }
	                    j=j+sf;
	                }else if(!"0".equals(item.getCodesetid())&&item.getViewvalue()!=null&&item.getViewvalue().length()>0/*&&blike*/)
	                {
	                	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr,blike);
	                	j=j+sf;
	                }
	                else
	                {
	    	            /**组合表达式串*/
	    	            if(j==1)
	    	            {
	    	                strexpr.append(j);
	    	            }
	    	            else
	    	            {
	    	                strexpr.append("*");
	    	                strexpr.append(j);                
	    	            }
	    	            
	    	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
	    	            {
	    	            		String q_v=item.getValue().trim();
	    	            		
	    	            		if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
	    	                    {
	    			                
	    	                    	strfactor.append(item.getItemid().toUpperCase());
	    			                if("0".equals(item.getCodesetid())) {
                                        strfactor.append("=*");
                                    } else {
                                        strfactor.append("=");
                                    }
	    			                strfactor.append(PubFunc.getStr(item.getValue()));
	    			                strfactor.append("*`");	   			                	  
	    	                    }
	    	                    else
	    	                    {
	    	                    	
	    			                strfactor.append(item.getItemid().toUpperCase());
	    			                strfactor.append("=");
	    			                strfactor.append(PubFunc.getStr(item.getValue()));
	    			                strfactor.append("`");	  	                        
	    	                    }
	    	            }
	    	            else
	    	            {
	    	                strfactor.append(item.getItemid().toUpperCase());
	    	                strfactor.append("=");
	    	                strfactor.append(PubFunc.getStr(item.getValue()));
	    	                strfactor.append("`");
	    	            }
	    	            ++j;	            
	                }
	    		}
	            
	        }
	        //System.out.println(strexpr.toString());
	        //System.out.println(strfactor.toString());
	        if((strexpr==null||strexpr.length()<=0)&&checklist.size()<=0) {
                return "";
            }
	        ArrayList fieldlist=new ArrayList();
	        StringBuffer strwhere=new StringBuffer();
	        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
	        	factorlist.setSuper_admin(true);
	        	strwhere.append(factorlist.getSqlExpression());
	        
	        StringBuffer checksql=new StringBuffer();
	        
	        for(int i=0;i<checklist.size();i++)
	        {
	        	checksql.append(" and ("+checklist.get(i)+")");
	        }       
	        boolean isWhere=true;
	        if(strwhere!=null&&strwhere.toString().trim().equalsIgnoreCase("FROM "+dbpre+"A01")) {
                isWhere=false;
            } else if(strwhere!=null&& "FROM B01".equalsIgnoreCase(strwhere.toString().trim())) {
                isWhere=false;
            } else if(strwhere!=null&& "FROM K01".equalsIgnoreCase(strwhere.toString().trim())) {
                isWhere=false;
            }
	        if(checklist==null||checklist.size()<=0)
	        {
	        	if(!isWhere) {
                    return"";
                }
	        }else
	        {
	        	if(!isWhere)
	        	{
	        		strwhere.append(" WHERE 1=1 ");
	        		strwhere.append(checksql.toString());
	        	}else {
                    strwhere.append(checksql.toString());
                }
	        }        
	        return strwhere.toString();
	    }
	 /**
	  * tianye add
	  * 根据指标分类和指标id 查询出部门或单位（支持关联部门的指标，可以传入单位信息）
	  * @param codesetid
	  * @param codevalue
	  * @return 
	  */
	 	public static CodeItem getUMOrUN(String codesetid, String codevalue){
	 		CodeItem codeItem =AdminCode.getCode(codesetid,codevalue) ;
	 		if(codeItem==null&& "UM".equals(codesetid)){
	 			codeItem = AdminCode.getCode("UN",codevalue) ;
	 		}
	 		return codeItem;
	 	}
	 	
	 	public static void rec01Log(String prefix,String id,String sbase,String dbase,Connection conn){
	 		
	 		try{
	 			ContentDAO dao = new ContentDAO(conn);
		 		List fieldlist = DataDictionary.getFieldList(prefix+"01", Constant.USED_FIELD_SET);
		 		StringBuffer fieldstr = new StringBuffer();
		 		for(int i=0;i<fieldlist.size();i++){
		 			FieldItem item = (FieldItem)fieldlist.get(i);
		 			fieldstr .append(item.getItemid()+",");
		 		}
		 		fieldstr.append("CreateTime,ModTime,CreateUserName,ModUserName,state,");
		 		if("A".equals(prefix)){
		 			fieldstr.append("UserName,UserPassword,a0000,a0100,");
		 		}
		 		DbWizard dbWizard = new DbWizard(conn);
        		boolean flag = dbWizard.isExistField(prefix+"01log", "guidkey",false);
        		if(flag) {
                    fieldstr.append("guidkey,");
                }
		 		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 		 String 	date =sdf.format(new java.util.Date());
		 		StringBuffer sql = new StringBuffer();
		 		sql.append("INSERT INTO "+prefix+"01Log("+fieldstr.toString()+"SBASE, DBASE, SETID, ModTime1) ");
		 		sql.append("SELECT "+fieldstr.toString()+"'"+sbase+"','" + dbase+"','"+ prefix+"01',"
		 		          +Sql_switcher.dateValue(date)+
		 		         " From " + sbase + prefix+"01");
		 		sql.append(" where ");
		 		if("A".equalsIgnoreCase(prefix)) {
                    sql.append(" a0100='");
                } else if("B".equalsIgnoreCase(prefix)) {
                    sql.append(" b0110='");
                } else if("K".equalsIgnoreCase(prefix)) {
                    sql.append(" e01a1='");
                }
		 		sql.append(id+"'");
		 		dao.update(sql.toString());
	 		}catch(Exception e){
	 			e.printStackTrace();
	 		}finally{
	 			
	 		}
	 		
	 	}
	 	/**
         * 获取序号维护生成的值
         * @param itemid 指标id 
         * @param userbase 人员库
         * @param fieldsetId 人员表
         * @param conn 数据库链接
         * @param a0100 人员编号
         * @param idg 序号维护对象
         * @return
         */
        public String getSequenceableValue(String itemid, String userbase, String fieldsetId, Connection conn,
                String a0100, String i9999, IDGenerator idg) {
            String value = "";
            try {
                ContentDAO dao = new ContentDAO(conn);
                FieldItem _fieldItem=DataDictionary.getFieldItem(itemid.toLowerCase());
                RecordVo pvo = new RecordVo(userbase+fieldsetId);
                pvo.setString("a0100",a0100);
                if(!"A01".equalsIgnoreCase(fieldsetId.toUpperCase())) {
                    pvo.setString("i9999",i9999);
                }
                    
                pvo=dao.findByPrimaryKey(pvo);
                
                value = pvo.getString(itemid);
                if(StringUtils.isNotEmpty(value)) {
                    return value;
                }
                
                String prefix_field=_fieldItem.getSeqprefix_field();
                int prefix=_fieldItem.getPrefix_field_len();
                String prefix_value="";
                String temp="";
                if(prefix_field!=null&&prefix_field.trim().length()>0) {
                    prefix_value=pvo.getString(prefix_field.toLowerCase());
                }
                
                if(prefix_value==null) {
                    prefix_value="";
                }
                
                temp=prefix_value;
                
                if(prefix_value.length()>prefix&&prefix_field!=null&&prefix_field.length()>0) {
                    prefix_value=prefix_value.substring(0,prefix);
                }
                
                String backfix="";
                if(prefix_value!=null&&prefix_value.length()>0) {
                    backfix="_"+prefix_value;
                }
                
                RecordVo idFactory=new RecordVo("id_factory");
                idFactory.setString("sequence_name", _fieldItem.getFieldsetid().toUpperCase()+"."+_fieldItem.getItemid().toUpperCase()+backfix);
                String sequ_value="";
                /**如果该序号还没建立，取没有前缀的序号*/
                if(dao.isExistRecordVo(idFactory)) {
                    sequ_value=idg.getId(_fieldItem.getFieldsetid().toUpperCase()+"."+_fieldItem.getItemid().toUpperCase()+backfix);
                } else {
                    sequ_value=idg.getId(_fieldItem.getFieldsetid().toUpperCase()+"."+_fieldItem.getItemid().toUpperCase());
                }
                
                value=prefix_value+sequ_value;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return value;
        }
        
        /**
         * 过滤查询结果
         * @param type
         * @param dbpre
         * @param sql
         */
        public void filterQueryResultsaveQueryResult(String type, String dbpre,String sql,Connection conn,UserView userView)throws GeneralException {
            ContentDAO dao=new ContentDAO(conn);
            if(userView.getStatus()==4) {
                String tabldName = "t_sys_result";
                Table table = new Table(tabldName);
                DbWizard dbWizard = new DbWizard(conn);
                if (!dbWizard.isExistTable(table)) {
                    return;
                }
                
                /**=0 人员=1 单位=2 岗位*/
                String flag="0";
                if("2".equalsIgnoreCase(type)) {
                    flag="1";
                } else if("3".equalsIgnoreCase(type)) {
                    flag="2";
                }
                
                StringBuffer str = new StringBuffer("delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
                if("1".equalsIgnoreCase(type)) {
                    str.append(" and UPPER(nbase)='"+dbpre.toUpperCase()+"'");
                }
                
                if("2".equals(type)) {
                    str.append(" and obj_id not in ");
                    str.append(" (select ");
                    str.append("B0110  from (");
                    str.append(sql);
                    str.append(") myset)");
                } else if("3".equals(type)) {
                    str.append(" and obj_id not in ");
                    str.append(" (select ");
                    str.append("E01A1  from (");
                    str.append(sql);
                    str.append(") myset)");     
                } else  {
                    str.append(" and obj_id not in ");
                    str.append(" (select ");
                    str.append("A0100  from (");
                    str.append(sql);
                    str.append(") myset)");         
                }
                
                try {
                    dao.update(str.toString());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                if("2".equals(type)) {
                    dbpre="B";
                }
                if("3".equals(type)) {
                    dbpre="K";
                }
                String tablename = userView.getUserName()+dbpre+"result";
                
                AutoCreateQueryResultTable.execute(conn, tablename, type);
                StringBuffer delsql=new StringBuffer();
               
                try {
                    delsql.append("delete from  ");
                    delsql.append(tablename);
                    if("2".equals(type)) {
                        delsql.append(" where B0110 not in ");
                        delsql.append(" (select ");
                        delsql.append("B0110  from (");
                        delsql.append(sql);
                        delsql.append(") myset)");
                    } else if("3".equals(type)) {
                        delsql.append(" where E01A1 not in ");
                        delsql.append(" (select ");
                        delsql.append("E01A1  from (");
                        delsql.append(sql);
                        delsql.append(") myset)");      
                    } else  {
                        delsql.append(" where A0100 not in ");
                        delsql.append(" (select ");
                        delsql.append("A0100  from (");
                        delsql.append(sql);
                        delsql.append(") myset)");          
                    }
                    dao.update(delsql.toString());
                } catch(Exception ex) {
                    ex.printStackTrace();
                    throw GeneralExceptionHandler.Handle(ex);
                }
            }
            
        }

    /**
     * 设置兼职后在显示单位显示数据时，如果有兼职信息则用兼职子集中i0000的值替换a01中a0000的值
     * 
     * @param conn
     *            数据库链接
     * @param nbase
     *            人员库
     * @param kind
     *            组织机构类型
     * @param isLike
     *            是否模糊查询
     * @param code
     *            机构编码
     * @return
     */
    public String getPartSql(Connection conn, String nbase, String kind, boolean isLike, String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }

        StringBuffer partSql = new StringBuffer();
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
        String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");
        /** 兼职单位字段 */
        String unitField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "unit");
        // 兼职部门
        String deptField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "dept");
        // 兼职职位
        String posField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
        /** 任免标识字段 */
        String appointField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
        if (StringUtils.isEmpty(flag) || "false".equalsIgnoreCase(flag)) {
            return "";
        }

        String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
        if (StringUtils.isEmpty(setid) || DataDictionary.getFieldSetVo(setid) == null
                || "0".equalsIgnoreCase(DataDictionary.getFieldSetVo(setid).getUseflag())) {
            return "";
        }

        DbWizard db = new DbWizard(conn);
        if(!db.isExistField(nbase + setid, "i0000", false)) {
            return "";
        }
        
        FieldItem unitFieldItem = DataDictionary.getFieldItem(unitField);
        if (unitFieldItem == null || "0".equalsIgnoreCase(unitFieldItem.getUseflag())) {
            return "";
        }

        FieldItem appointFieldItem = DataDictionary.getFieldItem(appointField);
        if (appointFieldItem == null || "0".equalsIgnoreCase(appointFieldItem.getUseflag())) {
            return "";
        }

        String field = "B0110";
        if ("2".equals(kind)) {
            field = "B0110";
        } else if ("1".equals(kind)) {
            field = "e0122";
        } else if ("0".equals(kind)) {
            field = "e01a1";
        }

        partSql.append("select " + nbase + "A01.*,I0000," + unitField);
        partSql.append(" from " + nbase + "A01");
        partSql.append(" left join ");
        partSql.append("(select I0000,a0100," + unitField + " from " + nbase + setid + " A  where I9999 = (");
        partSql.append("select max(I9999) from " + nbase + setid + " B");
        partSql.append(" where A.A0100 = B.A0100 and (");
        if (isLike) {
            partSql.append(unitField + " like '" + code + "%'");
            if (StringUtils.isNotEmpty(deptField)) {
                partSql.append(" or " + deptField + " like '" + code + "%'");
            }

            if (StringUtils.isNotEmpty(posField)) {
                partSql.append(" or " + posField + " like '" + code + "%'");
            }

        } else {
            partSql.append(unitField + "='" + code + "'");
            if (StringUtils.isNotEmpty(deptField)) {
                partSql.append(" or " + deptField + "='" + code + "'");
            }

            if (StringUtils.isNotEmpty(posField)) {
                partSql.append(" or " + posField + "='" + code + "'");
            }

        }

        partSql.append(") and " + appointField + " = '0'");
        partSql.append("and not A0100 in (select  A0100 from USRA01 where " + field + " LIKE '" + code + "%'))) P");
        partSql.append(" on " + nbase + "a01.a0100=P.a0100");

        return partSql.toString();
    }
    /**
     * 获取批量删除子集记录的筛选条件
     * @param userView 登录用户
     * @param dataRangeType 权限范围
     * @param subsetDataType 子集记录范围
     * @param dbname 人员库
     * @param setname 子集
     * @param selectId 选择的人员
     * @param whereValue 筛选子集记录的条件
     * @param conn 数据库链接
     * @return
     */
    public String getSubsetDataWhere(UserView userView, String dataRangeType, String subsetDataType,
            String dbname, String setname, String selectId, String whereValue, Connection conn) {
        StringBuffer sqlStr = new StringBuffer();
        String tablename = dbname + setname;
        try {
            if ("1".equals(dataRangeType)) {
                String resultTable = "t_sys_result";
                if (userView.getStatus() != 4) {
                    resultTable = userView.getUserName() + dbname + "result";
                }
                
                sqlStr.append(" and a0100 in (");
                
                if (userView.getStatus() == 4) {
                    sqlStr.append("select obj_id from " + resultTable);
                    sqlStr.append(" where flag=0");
                    sqlStr.append(" and UPPER(nbase)='" + dbname.toUpperCase());
                    sqlStr.append("' and UPPER(username)='" + userView.getUserName().toUpperCase() + "'");
                } else {
                    sqlStr.append("select a0100 from " + resultTable);
                }
                
                sqlStr.append(")");
            } else if ("2".equals(dataRangeType)) {
                String[] selectIds = selectId.split(",");
                sqlStr.append(" and a0100 in ('#'");
                for(int i = 0; i < selectIds.length; i++) {
                    if(StringUtils.isEmpty(selectIds[i])) {
                        continue;
                    }
                    
                    sqlStr.append(",'" + selectIds[i] + "'");
                }
                
                sqlStr.append(")");
                
            }
            
            if(!userView.isSuper_admin()) {
                sqlStr.append(" and " + codeWhere(userView, conn, setname, dbname));
            }
            if ("1".equals(subsetDataType)) {
                sqlStr.append(" and exists (select 1 from (");
                sqlStr.append("select MAX(A0100) as a0100,MAX(I9999) as i9999 from ");
                sqlStr.append(tablename);
                sqlStr.append(" group by A0100) b");
                sqlStr.append(" where " + tablename + ".a0100=b.a0100");
                sqlStr.append(" and " + tablename + ".I9999=b.i9999)");
            } else if ("2".equals(subsetDataType)) {
                if(StringUtils.isNotEmpty(whereValue)) {
                    String[] values = whereValue.split("::");
                    boolean like = false;
                    if("1".equals(values[2])) {
                        like =true;
                    }
                    
                    FactorList factorlist = new FactorList(PubFunc.keyWord_reback(values[0]), PubFunc.reBackWord((values[1])),dbname,
                            true, like, true, 1, userView.getUserName());
                    
                    String where = factorlist.getSqlExpression();
                    if(StringUtils.isNotEmpty(where)) {
                        where = where.replace(tablename + ".", "a.").replace(tablename, tablename + " a");
                        sqlStr.append(" and exists (");
                        sqlStr.append("select " + dbname +"A01.a0100 ");
                        sqlStr.append(where);
                        sqlStr.append(" and " + tablename + ".a0100=a.a0100 and " + tablename + ".i9999=a.i9999");
                        sqlStr.append(")");
                    } else {
                        sqlStr.append(" and a0100 in ('#')");
                    }
                    
                } else {
                    sqlStr.append(" and a0100 in ('#')");
                }
                    
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        
        return sqlStr.toString();
    }
    /**
     * 获取人员权限的sql
     * @param userView 登录用户
     * @param conn 数据库链接
     * @param setname 子集
     * @param dbname 人员库
     * @return
     * @throws GeneralException
     */
    public String codeWhere(UserView userView, Connection conn, String setname, String dbname) throws GeneralException {
        InfoUtils infoUtils = new InfoUtils();
        String personsortfield = new SortFilter().getSortPersonField(conn);
        String codesetid = userView.getManagePrivCode();
        String kind = "2";
        if ("UN".equalsIgnoreCase(codesetid)) {
            kind = "2";
        } else if ("UM".equalsIgnoreCase(codesetid)) {
            kind = "1";
        } else if ("@K".equalsIgnoreCase(codesetid)) {
            kind = "0";
        }
        
        ArrayList<String> list = new ArrayList<String>();
        list.add("flag");
        list.add("unit");// 兼职单位
        list.add("setid");// 兼职子集
        list.add("appoint");// 兼职标识
        list.add("pos");// 兼职职务
        String part_setid = "";
        String part_unit = "";
        String appoint = " ";
        String flag = "";
        String part_pos = "";
        // 兼职处理
        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(conn);
        HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME, list);
        if (map != null && map.size() != 0) {
            if (map.get("flag") != null && ((String) map.get("flag")).trim().length() > 0) {
                flag = (String) map.get("flag");
            }
            if (flag != null && "true".equalsIgnoreCase(flag)) {
                if (map.get("unit") != null && ((String) map.get("unit")).trim().length() > 0) {
                    part_unit = (String) map.get("unit");
                }
                
                if (map.get("setid") != null && ((String) map.get("setid")).trim().length() > 0) {
                    part_setid = (String) map.get("setid");
                }
                
                if (map.get("appoint") != null && ((String) map.get("appoint")).trim().length() > 0) {
                    appoint = (String) map.get("appoint");
                }
                
                if (map.get("pos") != null && ((String) map.get("pos")).trim().length() > 0) {
                    part_pos = (String) map.get("pos");
                }
            }
        }
        
        String term_Sql = infoUtils.getWhereSQL(conn, userView, dbname,
                userView.getManagePrivCodeValue(), true, kind, "org", personsortfield, "All", part_unit,
                part_setid, appoint, "");

        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("A0100 in (");
        sqlstr.append(term_Sql);
        sqlstr.append(")");

        return sqlstr.toString();
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 删除人员数据
     * 
     * @param conn
     *            数据库链接
     * @param usrNbase
     *            删除数据的人员库
     * @param toUserNbase
     *            目标人员库:=del:删除人员数据操作；=其他的人员库：人员移库操作进行删除人员数据
     * @param selfinfolist
     *            人员信息
     * @param userView
     *            登录用户
     * @return a0100s 被删人员信息的人员姓名
     */
    public String deletePersonInfo(Connection conn,String usrNbase,String toUserNbase, ArrayList selfinfolist,
            UserView userView) {
        StringBuffer a0100s = new StringBuffer();
        String a0101s = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            List infoSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            StringBuffer deletesql = new StringBuffer();
            // 联动兼职数 兼职控制编制
            /** 兼职参数 */
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String partflag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");// 是否启用，true启用
            // 兼职岗位占编 1：占编
            String takeup_quota = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "takeup_quota");
            String ps_parttime = "0";
            if ("true".equals(partflag) && "1".equals(takeup_quota)) {
                ps_parttime = "1";
            }

            String pos_ctrl = sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
            DbNameBo db = new DbNameBo(conn);
            String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");// 兼职子集
            PosparameXML pos = new PosparameXML(conn);
            String dbs = pos.getValue(PosparameXML.AMOUNTS, "dbs");
            dbs = dbs != null && dbs.trim().length() > 0 ? dbs : "";
            String pos_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos").toLowerCase();// 兼任兼职
            /** 任免标识字段 */
            String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint").toLowerCase();
            String pos_value = "";
            FieldItem pos_field_item = DataDictionary.getFieldItem(pos_field);
            FieldItem appoint_field_item = DataDictionary.getFieldItem(appoint_field);
            
            for (int i = 0; i < selfinfolist.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) selfinfolist.get(i);
                if (!CleanPersonSetting.cleanByA0100((String) rec.get("a0100"), usrNbase, null)) {
                    throw GeneralExceptionHandler.Handle(new Exception("同步人员面板信息错误！"));
                }

                // 删除附件 2014-05-04 wangrd
                MultiMediaBo mediabo = new MultiMediaBo(conn, userView);
                if ("DEL".equals(toUserNbase)) {
                    mediabo.deleteMultimediaFileByA0100("A", "A01", usrNbase, rec.get("a0100").toString(), 0);
                }

                // 删除人员业务数据 zxj 提前到删除A01信息前，有部分业务表关联的是guidkey，否则a01删掉后就取不到guidkey了
                db.delBusiTableData(usrNbase, rec.get("a0100").toString());
                a0100s.append("'" + rec.get("a0100") + "',");
                a0101s += (String) ((String) rec.get("a0101") != null ? rec.get("a0101") + "," : "");
                String org_id = (String) ((String) rec.get("b0110") != null ? rec.get("b0110") : "");
                String dept_id = (String) ((String) rec.get("e0122") != null ? rec.get("e0122") : "");
                String pos_id = (String) (rec.get("e01a1") != null ? rec.get("e01a1") : "");
                InfoUtils.rec01Log("A", rec.get("a0100").toString(), usrNbase, toUserNbase, conn);
                for (int j = 0; j < infoSetList.size(); j++) {
                    FieldSet fieldset = (FieldSet) infoSetList.get(j);
                    if ("true".equals(pos_ctrl) && "1".equals(ps_parttime)) {
                        if (setid.equals(fieldset.getFieldsetid())) {
                            if (dbs.toUpperCase().indexOf(usrNbase.toUpperCase()) != -1) {
                                if (pos_field.length() > 0 && appoint_field.length() > 0 && pos_field_item != null
                                        && "1".equals(pos_field_item.getUseflag()) && appoint_field_item != null
                                        && "1".equals(appoint_field_item.getUseflag())) {
                                    rs = dao.search("select distinct " + pos_field + " from " + usrNbase + setid
                                            + " where a0100='" + rec.get("a0100").toString() + "' and " + appoint_field
                                            + "='0'");
                                    while (rs.next()) {
                                        pos_value = rs.getString(pos_field);
                                        db.dateLinkage(pos_value, 1, "-");
                                    }
                                }
                            }
                        }
                    }

                    deletesql.setLength(0);
                    deletesql.append("delete from ");
                    deletesql.append(usrNbase);
                    deletesql.append(fieldset.getFieldsetid());
                    deletesql.append(" where a0100='");
                    deletesql.append(rec.get("a0100").toString());
                    deletesql.append("'");
                    dao.update(deletesql.toString());
                    if (!conn.getAutoCommit()) {
                        conn.commit();
                    }
                }

                if (dbs.toUpperCase().indexOf(usrNbase.toUpperCase()) != -1) {
                    db.dateLinkage("", pos_id, 1, "-");
                }
                // 删除 不直接入库人员修改的信息
                dao.update("delete from  t_hr_mydata_chg  where Upper(NBase)='" + usrNbase.toUpperCase()
                        + "' and A0100='" + rec.get("a0100").toString() + "'");
            }

            RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbname = "";
            if (vo != null) {
                dbname = vo.getString("str_value");
            }

            DbWizard dbw = new DbWizard(conn);
            // 删除应聘人员的数据,删除数据后为了防止程序依赖太严重没有对招聘管理中简历数量进行处理（PositionBo，saveCandiatesNumber(String
            // z0301,int opt)）
            if ("DEL".equals(toUserNbase) && dbname.equals(usrNbase)) {
                a0100s.setLength(a0100s.length() - 1);
                if (dbw.isExistTable("zp_pos_tache", false)) {
                    dao.update("delete from zp_pos_tache where a0100 in(" + a0100s + ") and nbase='" + dbname + "'");
                }
                if (dbw.isExistTable("zp_evaluation", false)) {
                    dao.update("delete from zp_evaluation  where a0100 in(" + a0100s + ") and nbase='" + dbname + "'");
                }
                if (dbw.isExistTable("zp_exam_assign", false)) {
                    dao.update("delete from zp_exam_assign  where a0100 in(" + a0100s + ") and nbase='" + dbname + "'");
                }
                if (dbw.isExistTable("z63", false)) {
                    dao.update("delete from z63  where a0100 in(" + a0100s + ") and nbase='" + dbname + "'");
                }
                if (dbw.isExistTable("z05", false)) {
                    dao.update("delete from z05  where a0100 in(" + a0100s + ") and nbase='" + dbname + "'");
                }
                if (dbw.isExistTable("zp_attachment", false)) {
                    dao.update("delete from zp_attachment where guidkey in(select guidkey from " + dbname
                            + "A01 where A0100 in(" + a0100s + "))");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return a0101s;
    }
}

