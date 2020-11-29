package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class CountInfo {

	private UserView userView=null;
	private Connection conn=null;
	public CountInfo()
	{
		
	}
	public CountInfo(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	public void countKQInfo(String start_date,String end_date,String table,String codeWhere)throws GeneralException
	{
	    ArrayList kq_target_list=getTargetList();
	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    	
    	ArrayList columnlist= new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++)
    	{
    		FieldItem fielditem = (FieldItem) fielditemlist.get(i);

            if (!("i9999").equalsIgnoreCase(fielditem.getItemid())) {
                columnlist.add(fielditem);
            }
   	    }
	    HashMap hash=new HashMap();
        //		判断是否已经报批	
	        
        if ("Q03".equalsIgnoreCase(table)) {//日明细计算
            for(int r=0;r<this.userView.getPrivDbList().size();r++)
            {
                String userbase=this.userView.getPrivDbList().get(r).toString();
                
                String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);		   
            	if(!userView.isSuper_admin())
            	{   
            		String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
            		ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.conn,whereB0110,"b0110");
            		for(int t=0;t<orgidb0110List.size();t++)
            		{
            			String b0110_one=orgidb0110List.get(t).toString();			
            			String nbase=RegisterInitInfoData.getOneB0110Dase(hash,this.userView,userbase,b0110_one,this.conn);
            			/********按照该单位的人员库的操作*********/
            			if(nbase!=null&&nbase.length()>0)
            			{
            				oneCountKQ(start_date,end_date,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist,table,"");	 
            			}
            		}
            	}else
            	{  
            		ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN,this.conn);
            		for(int n=0;n<b0100list.size();n++)
            		{
            			String b0110_one=b0100list.get(n).toString();
            			String nbase=RegisterInitInfoData.getOneB0110Dase(hash,this.userView,userbase,b0110_one,this.conn);
            			/********按照该单位的人员库的操作*********/
            			if(nbase!=null&&nbase.length()>0)
            			{
            				oneCountKQ(start_date,end_date,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist,table,"");	
            			}
            		}
            	} 	  	   
	   	    }
        }else {//数据处理计算
            //ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN,this.conn);
            //for(int n=0;n<b0100list.size();n++)
            //{
            //String b0110_one=b0100list.get(n).toString();
            //String nbase=RegisterInitInfoData.getOneB0110Dase(hash,this.userView,userbase,b0110_one,this.conn);
            //if(nbase!=null&&nbase.length()>0)
            //{
            String whereIN = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, table);
            oneCountKQ(start_date,end_date,whereIN,"","","2",kq_target_list,columnlist,table,codeWhere);	
            //}
            //}
        }
	}
	/**
	 * 
	 * @param datelist  时间
	 * @param whereIN  操作人员权限
	 * @param e0122  部门
	 * @param kq_target_list  考勤项目指标里面是个HashMap,HashMap存了两个属性 fielditemid=指标项;c_expr=公式;
	 * @param filedlist  指标项集
	 * @throws GeneralException
	 */
	    public void oneCountKQ(String start_date,String end_date, String whereIN,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist,String dest_table,String codeWhere)throws GeneralException
	    {
    		RowSet rs = null;
        	ContentDAO dao = new ContentDAO(this.conn);
        	try
            {	
        		StringBuffer sqlwhr=new StringBuffer();
        		sqlwhr.append(" where 1=1");
        		
        		if (StringUtils.isNotEmpty(userbase)) {
                    sqlwhr.append(" and nbase='"+userbase+"'");
                }
        		
        		if (StringUtils.isNotEmpty(code)) {
        		    if("1".equals(kind))
        		    {
        		        sqlwhr.append(" and e0122 like '"+code+"%'");
        		    }else if("0".equals(kind))
        		    {
        		        sqlwhr.append(" and e01a1 like '"+code+"%'");	
        		    }else
        		    {
        		        sqlwhr.append(" and b0110 like '"+code+"%'");	
        		    }
        		}
        		
             	sqlwhr.append(" and Q03Z0 >='"+start_date+"'");
             	sqlwhr.append(" and Q03Z0 <='"+end_date+"'"); 
             	
             	if ("Q03".equalsIgnoreCase(dest_table)) {
             		sqlwhr.append(" and q03z5 in('01','07')"); 
				}else {
					if(codeWhere != null && codeWhere.length() > 0) {
                        sqlwhr.append(" and " + codeWhere);
                    }
				}
             	
             	// 集中处理时，只计算当前用户的数据
             	if (dest_table.toUpperCase().startsWith("KQ_")) {
             	    sqlwhr.append(" and cur_user='").append(this.userView.getUserName()).append("'");
             	} else if (dest_table.toUpperCase().startsWith("KT_")) {
             	    // 分用户处理模式，处理全表数据，不需要加条件
             	} else {
             	    // 日明细表Q03计算
             	    if(!this.userView.isSuper_admin())
             	    {
             	        if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1) {
             	            sqlwhr.append(" and EXISTS(select a0100 "+whereIN+" and "+userbase+"A01.a0100="+dest_table+".a0100)");
             	        } else {
             	            sqlwhr.append(" and EXISTS(select a0100 "+whereIN+" where "+userbase+"A01.a0100="+dest_table+".a0100)");
             	        }
             	    }
             	}
             	
        	    for(int i=0;i<kq_target_list.size();i++) {
                	HashMap onemap=(HashMap)kq_target_list.get(i);
                	String fielditemid=onemap.get("fielditemid").toString();
                	FieldItem item = DataDictionary.getFieldItem(fielditemid);
                	if(item==null) {
                        continue;
                    }
                	
                	if(item.getUseflag()!=null&& "0".equals(item.getUseflag())) {
                        continue;
                    }
                	
                	String c_expr=onemap.get("c_expr").toString();	
                	if(c_expr==null||c_expr.length()<=0) {
                        c_expr="";
                    }
                	
                	int s=c_expr.indexOf("^");
                	if(s>0)
                	{
                		c_expr=c_expr.substring(0,s);
                	}else
                	{
                		continue;
                	}
                	
                	if(fielditemid==null||fielditemid.length()<=0)
                	{
                		continue;
                	}else if(c_expr==null||c_expr.length()<=0)
                	{
                		continue;
                	}else
                	{
                		int lxtype=getLXtype(filedlist,fielditemid);    
                        //  解析公式
                    	YksjParser yp = new YksjParser(
                    			this.userView//Trans交易类子类中可以直接获取userView
                    			,filedlist
                    			,YksjParser.forNormal
                    			,lxtype//此处需要调用者知道该公式的数据类型
                    			,YksjParser.forPerson
                    			,"","");
                    	yp.setCon(this.conn);
                    	yp.setTempTableName(dest_table);
                        yp.setStdTmpTable(dest_table);
                    	c_expr=c_expr.trim();	    
                    	//System.out.println(c_expr);
                    	yp.run(c_expr);                	
                    	String FSQL=yp.getSQL();
                    	if (FSQL == null || "".equals(FSQL)) {
                            continue;
                        }
                    	
                    	StringBuffer sql=new StringBuffer();
                    	sql.append(getUpdateSQL(fielditemid,FSQL,kind,whereIN,dest_table));
                        sql.append(sqlwhr);
                     	dao.update(sql.toString());
                	}        	
                }  
        	    
	        	if (!"Q03".equalsIgnoreCase(dest_table)) {
	        	    	/*
	        		 * szk查询控制结果的指标
	        		 * 21迟到，23早退，25旷工
	        		 */
	        	    //修正迟到
	        		amendKqResultDesc(dao, "21", dest_table, sqlwhr.toString());
	        		//修改早退
	        		amendKqResultDesc(dao, "23", dest_table, sqlwhr.toString());
	        		//修改旷工
	        		amendKqResultDesc(dao, "25", dest_table, sqlwhr.toString());
	        	}
            } catch(Exception e) {
            	e.printStackTrace();	            	 
            	throw GeneralExceptionHandler.Handle(e);
            } finally {
	    	    KqUtilsClass.closeDBResource(rs);
	        } 
	    }
	    
    /**
     * 修正计算后，如迟到、早退、旷工为零，那么从结果描述中去除相应的汉字描述
     * @param dao
     * @param itemid 考勤规则编号
     * @param destTab 要更新的目标表
     * @param sqlwhr 更新的数据范围Sql条件
     */
    private void amendKqResultDesc(ContentDAO dao, String itemid, String destTab, String sqlwhr) {
        if (StringUtils.isEmpty(itemid)) {
            return;
        }
        
        String itemDesc = "";
        if ("21".equalsIgnoreCase(itemid)) {
            itemDesc = "迟到";
        } else if ("23".equalsIgnoreCase(itemid)) {
            itemDesc = "早退";
        } else if ("25".equalsIgnoreCase(itemid)) {
            itemDesc = "旷工";
        } else {
            return;
        }
        
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        ArrayList params = new ArrayList();
        try {
            sql.append("select fielditemid from kq_item");
            sql.append(" where item_id=?");
            sql.append(" and fielditemid is not null");
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql.append(" and fielditemid<>''");
            }
            params.add(itemid);
            rs = dao.search(sql.toString(), params);
            if (rs.next()) {
                sql.setLength(0);
                params.clear();
                
                sql.append("update " + destTab);
                sql.append(" set IsOk = ");
                sql.append("(case when IsOk like '%" + itemDesc + "+%' then replace(IsOk,'" + itemDesc + "+','') ");
                sql.append(" when IsOk like '%+" + itemDesc + "%' then replace(IsOk,'+" + itemDesc + "','') ");
                sql.append(" when IsOk = '" + itemDesc + "' then '正常' else IsOk end) ");
                sql.append(" " + sqlwhr);
                sql.append(" and(");
                sql.append(Sql_switcher.isnull(rs.getString("fielditemid"), "0")+"=0)");
                dao.update(sql.toString());
            } 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }   
	    
	    public String getUpdateSQL(String fielditemid,String FSQL,String kind,String whereIN,String dest_table)
	    {
	    	StringBuffer updateSQL=new StringBuffer();
	    	updateSQL.append("update "+dest_table+" set "+fielditemid+"="+FSQL);    	
 	
	    	return updateSQL.toString();
	    }
	    
	    /**
	     * 得到考勤规则中的考勤指标和公式
	     * @return
	     * @throws GeneralException
	     */

	    public ArrayList getTargetList()throws GeneralException
	    {
	    	StringBuffer kq_Target= new StringBuffer();
	    	kq_Target.append("select item_name,fielditemid,c_expr from kq_item ");    	
	    	kq_Target.append(" where "+Sql_switcher.isnull("fielditemid","'aa'")+"<>'aa' order by displayorder");
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	RowSet rowSetF=null; 
	    	ArrayList targetlist= new ArrayList();
	    	String fielditemid="";
	    	ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET); 
	    	try{
	    		rowSetF=dao.search(kq_Target.toString());
	        	while(rowSetF.next())
	        	{
	        		HashMap map=new HashMap();
	        		String c_expr=Sql_switcher.readMemo(rowSetF,"c_expr");
	        		for(int i=0;i<fielditemlist.size();i++)
		   	    	{
		   	   	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   	   	          fielditemid=rowSetF.getString("fielditemid");	   	   	 
		   	   	          //System.out.println(fielditemid);
		   	   	          if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
		   	   	          {
		   	   	            if(c_expr!=null&&c_expr.length()>0)
		        		    {
		   	   	                map.put("item_name", rowSetF.getString("item_name"));
		        			    map.put("fielditemid",rowSetF.getString("fielditemid"));
		            		    map.put("c_expr",Sql_switcher.readMemo(rowSetF,"c_expr"));
		            		    targetlist.add(map);
		            		    continue;
		        		     }
		   	   	         }
		   	    	}
	        		
	        	}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(e);
	    	}finally
	        {
	    	    KqUtilsClass.closeDBResource(rowSetF);
	        } 
	    	return targetlist;
	    }   	    
	    public  int getLXtype(ArrayList columnlist,String fielditemid)
		{
			int lxtype=YksjParser.INT;		
			for(int r=0;r<columnlist.size();r++)
	 		{
	 	   	   FieldItem fielditem=(FieldItem)columnlist.get(r); 	   	   
	 	   	   
	 	   	   if(!fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                   continue;
               }
	 	   	   
	 	   	   String itemType = fielditem.getItemtype();
	 	   	   if ("N".equalsIgnoreCase(itemType)) {
	 	   		  if(fielditem.getDecimalwidth()>0)
    	   	      {
     	   		      lxtype=YksjParser.FLOAT;
     	   		      break;
    	   		  }else
    	   		  {   
    	   			  lxtype=YksjParser.INT;	  
    	   			  break;
    	   		  }	
	 	   	   } else if ("A".equalsIgnoreCase(itemType)) {
	 	   	       lxtype = YksjParser.STRVALUE;
	 	   	   } else if ("D".equalsIgnoreCase(itemType)) {
	 	   	       lxtype = YksjParser.DATEVALUE;
	 	   	   } else if ("M".equalsIgnoreCase(itemType)) {
	 	   	       lxtype = YksjParser.MEMO;
	 	   	   }
	 	   	   
	 	   	   break;
	 		}
			return lxtype;
		} 
}
