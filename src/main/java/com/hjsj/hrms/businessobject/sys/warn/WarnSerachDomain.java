package com.hjsj.hrms.businessobject.sys.warn;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 配合住总接口
 * @author Owner
 *
 */
public class WarnSerachDomain {
   private String login_username="username";
   public WarnSerachDomain()
   {
	  RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
      if(login_vo==null) {
          return;
      }
      String login_name = login_vo.getString("str_value");
      int idx=login_name.indexOf(",");
      if(idx!=-1)
      {
    	  login_username=login_name.substring(0,idx);
    	  if(login_username!=null&& "#".equals(login_username)) {
              login_username="username";
          }
          
      }
   }
   /**
    * 得到用户管理中自助用户的登陆用户名
    * @param conn1
    * @param domainStr
    * @param nbaseStr
    * @return
    */
   public ArrayList getUserNames(Connection conn1,String domainStr,String nbaseStr,String wid)
   {
	   if(domainStr==null||domainStr.length()<=0) {
           return null;
       }
	   ArrayList list=new ArrayList();	   
	   //t_sys_staff_in_role,t_sys_function_priv
	   String[] domains=domainStr.split(",");
	   ContentDAO dao=new ContentDAO(conn1);
	   for(int i=0;i<domains.length;i++)
	   {
		   String one_domain=domains[i];
		   if(one_domain==null||one_domain.length()<=0) {
               continue;
           }
		   if(one_domain.toUpperCase().indexOf("RL")!=-1)
		   {
			   //是不是如果分配了角色就按角色名称登陆呢
			   String roleid=getRoleid(one_domain);
			   getUserBase(conn1,roleid,nbaseStr,wid,list);
		   }
	   }	      
	   return list;
   }
   private String getRoleid(String domainstr)
   {
	   return domainstr.substring(2);
   }
   private ArrayList getUserBase(Connection conn1,String roleid,String nbaseStr,String wid,ArrayList list)
   {
	   /*String sql="select o.username username,o.a0100 a0100,o.nbase nbase from operuser o left join t_sys_staff_in_role t on o.username=t.staff_id ";
	   sql=sql+"where t.status='0' and t.role_id='"+roleid+"'";*/
	   String sql="select staff_id from t_sys_staff_in_role t where t.status='1' and t.role_id='"+roleid+"'";
	   try{
		   ContentDAO dao=new ContentDAO(conn1);
		   RowSet rs=dao.search(sql);		  
		   String staff_id="";		   
		   while(rs.next())
		   {
			   staff_id=rs.getString("staff_id");
			   String name= getUserName(staff_id,conn1,nbaseStr,wid);
			   if(name!=null&&name.length()>0) {
                   list.add(name);
               }
			   /*if(rs.getString("a0100")!=null&&rs.getString("a0100").length()>0&&rs.getString("nbase")!=null&&rs.getString("nbase").length()>0)
			   {
				   if(rs.getString("username")==null||rs.getString("username").length()<=0)
					   continue;
				   if(getIsDbPriv(rs.getString("username"),nbaseStr, dao))
				   {
					  String name= getLoginName(dao, rs.getString("a0100"),rs.getString("nbase"));
					  if(name!=null&&name.length()>0)
						  list.add(name);					  
				   }				   
			   }*/
			   
		   }
	   }catch(Exception e)
	   {
		e.printStackTrace();   
	   }
	   return list;
   }
   private String getUserName(String staff_id,Connection conn1,String nbaseStr,String wid)
   {
	   String nbase=staff_id.substring(0,3);
	   String a0100=staff_id.substring(3);
	   ContentDAO dao=new ContentDAO(conn1);
	   String name = getLoginName(dao,a0100,nbase);
	   UserView userview=new UserView(name,conn1);
	   RowSet rs=null;
	   boolean isCorrect =false;
	   try {
		  if(!userview.canLogin()) {
              return null;
          }
		  ArrayList dblist=userview.getPrivDbList();
		 
		  for(int i=0;i<dblist.size();i++)
		  {
			 nbase=dblist.get(i).toString();
			 String where=getWhereINSql(userview,nbase);
			 StringBuffer sql=new StringBuffer();
			 sql.append("select 1 from hrpwarn_result where wid='"+wid+"' and nbase='"+nbase+"' ");
			 sql.append(" and a0100 in (select "+nbase+"A01.a0100 "+where+")");			
			 rs=dao.search(sql.toString());
			 if(rs.next())
			 {
				 isCorrect=true; 
				 break;
			 }				
		  }
		
	   } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	   if(isCorrect) {
           return name;
       } else {
           return "";
       }
   }
   private boolean getIsDbPriv(String username,String nbaseStr,ContentDAO dao)
   {
	   String sql="select dbpriv from t_sys_function_priv where id='"+username+"'";
	   String dbpriv="";
	   try
	   {
		   RowSet rs=dao.search(sql);
		   if(rs.next()) {
               dbpriv=rs.getString("dbpriv");
           }
		   if(dbpriv==null||dbpriv.length()<=0) {
               return false;
           }
		   String dbprivs[]=dbpriv.split(",");
		   for(int i=0;i<dbprivs.length;i++)
		   {
			   String userbase=dbprivs[i];
	    		if(userbase!=null&&userbase.length()>0&&nbaseStr.indexOf(userbase)!=-1){
	    			return true;
	    		}
		   }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return false;
   }
   private String getLoginName(ContentDAO dao,String a0100,String nbase)
   {
	  String sql="select "+this.login_username+" username from "+nbase+"A01 where a0100='"+a0100+"'" ; 
	  String name="";
	  try
	   {
		   RowSet rs=dao.search(sql);
		   
		   if(rs.next())
		   {
			   name=rs.getString("username");
		   }
	   }catch(Exception e)
	   {
		   
	   }
	   return name;
   }
   private String getWhereINSql(UserView userView,String userbase){
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
}
