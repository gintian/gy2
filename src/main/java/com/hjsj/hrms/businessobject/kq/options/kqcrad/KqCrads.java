package com.hjsj.hrms.businessobject.kq.options.kqcrad;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqCrads {
	private Connection conn=null;
	public KqCrads(){}
	public KqCrads(Connection conn)
	{
		this.conn=conn;
	}
	 public String  getOrgId(String a_code,String nbase,UserView userView,HashMap formHM)throws GeneralException
	 {
		 String b0110="";
		 RowSet rs=null;
	    	try
	    	{
	    		if(a_code==null||a_code.length()<=0){
	    			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.conn);
	    			b0110=managePrivCode.getPrivOrgId();  
	    		}else{
	    			if(a_code.indexOf("UN")!=-1)
	        		{
	    				if(!"UN".equals(a_code))
	    				{
	    					b0110=a_code.substring(2);
	    				}
	    				
	        		}else if(a_code.indexOf("UM")!=-1)
	        		{
	        			b0110=a_code.substring(2);
	        			
	        		}else if(a_code.indexOf("@K")!=-1)
	        		{
	        			String code=a_code.substring(2);
	        			String orgSql="SELECT parentid,codeitemid from organization where codeitemid='"+ code +"'";
	        			ContentDAO dao=new ContentDAO(this.conn);
	        			rs=dao.search(orgSql);
	        			if(rs.next())
	        			{
	        				b0110=rs.getString("parentid");
	        			}
	        			
	        		}else if(a_code.indexOf("EP")!=-1)
	        		{
	        			if(nbase==null||nbase.length()<=0)
	        			{
	        				return "";
	        			}else
	        			{
	        				String code=a_code.substring(2);
		        			String sql="select b0110,e0122 from "+nbase+"A01 where a0100='"+code+"'";
		        			ContentDAO dao=new ContentDAO(this.conn);
		        			rs=dao.search(sql);
		        			if(rs.next())
		        			{
		        				b0110=rs.getString("e0122");
		        				if(b0110==null||b0110.length()<=0)
		        				{
		        					b0110=rs.getString("b0110");
		        				}
		        			}
	        			}
	        			
	        		}else if(a_code.indexOf("GP")!=-1)
	        		{
	        			String code=a_code.substring(2);
	        			String sql="select b0110,e0122 from kq_group_emp where group_id='"+code+"'";
	        			ContentDAO dao=new ContentDAO(this.conn);
	        			rs=dao.search(sql);
	        			if(rs.next())
	        			{
	        				b0110=rs.getString("e0122");
	        				if(b0110==null||b0110.length()<=0)
	        				{
	        					b0110=rs.getString("b0110");
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
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		   }  
	    	return b0110;    	
	}
	 
	 public String getQueryString(ArrayList dblist, UserView userView,String code,String kind,String kq_type,String card_no,String kq_gno)throws GeneralException 
		{
		  StringBuffer strsql=new StringBuffer();	
		 
		  
		  try
		  {
			  
			    if(dblist.size()<=0||dblist==null)
			    {   
			    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.param.nosave.userbase"),"",""));
			    }else
			    {
			    	for(int i=0;i<dblist.size();i++)
					{
						String nbase=(String)dblist.get(i);
						String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
						strsql.append("select '");
						strsql.append(nbase);
						strsql.append("' as nbase,");
						strsql.append("a0100,a0101");
						if(kq_gno!=null&&kq_gno.length()>0)
						{
							strsql.append(","+Sql_switcher.isnull(kq_gno,"''")+" as "+kq_gno);
						}
						strsql.append(" from ");
						strsql.append(nbase);
						strsql.append("a01");
						strsql.append(" where ");						
						if("1".equals(kind))
						{
							strsql.append("e0122 like '"+code+"%'");
						}else if("0".equals(kind))
						{
							strsql.append("e01a1 like '"+code+"%'");
						}
						else if("2".equals(kind))
						{
							strsql.append("b0110 like '"+code+"%'");
							//strsql.append("b0110 like '214%'");
						}else if("a01".equals(kind))
						{
							strsql.append("a0100 = '"+code+"'");
						}
						/*if(kq_type!=null&&kq_type.length()>0)
						{
									strsql.append(" and "+kq_type+"='02'");
						}*/
						strsql.append(" and ("+card_no+" is null or "+card_no+"='')");
						strsql.append(" and a0100 in(select a0100 "+whereIN+")");					
						strsql.append(" UNION ");
					}
					strsql.setLength(strsql.length()-7);					
			    }
						
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }	  	 
		  return strsql.toString();
		}	 	
	 /**
	  * 修改考勤卡状态
	  * @param card_no
	  * @param status
	  */
	 public void upKqCards(String card_no,String status)
	 {
		 StringBuffer sql=new StringBuffer();
		 sql.append("update kq_cards set ");
		 sql.append(" status=? ");
		 sql.append(" where card_no=?");
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 ArrayList list=new ArrayList();
			 list.add(status);
			 list.add(card_no);
			 dao.update(sql.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
		/***
		 * 
		 * 添加考勤卡
		 * 
		 * @param card_no
		 * @param status
		 * 
		 * xiexd 2014.09.29
		 */
		public void addKqCards(String card_no, String status){
			StringBuffer sql = new StringBuffer();
			sql.append("insert into kq_cards ");
			sql.append(" values(?,?) ");
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				ArrayList list = new ArrayList();
				list.add(card_no);
				list.add(status);
				dao.insert(sql.toString(), list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/***
		 * 查询考勤卡数据
		 * @param card_no
		 * @param status
		 * @return
		 * xiexd 2014.09.29
		 */
		public int searchKqCards(String card_no, String status){
			int num = 0;
			StringBuffer sql = new StringBuffer();
			sql.append("select * from kq_cards where ");
			sql.append(" card_no=? ");
			sql.append(" and (status=? ");
			sql.append(" or status='-1') ");
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				ArrayList list = new ArrayList();
				list.add(card_no);
				list.add(status);
				RowSet rs= dao.search(sql.toString(), list);
				if(rs.next())
				{
					num = 1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return num;
		}

	 /**
	  * 得到组织名称
	  * @param codeitemid
	  * @return
	  * @throws GeneralException
	  */
	 public String getCodeitemDesc(String codeitemid) throws GeneralException
	   {
		   if(codeitemid==null||codeitemid.length()<=0) {
               return "";
           }
		   String sql="select codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		   String codeitemdesc="";
		   RowSet rs=null;
		   try
		   {
			   ContentDAO dao=new ContentDAO(this.conn);
			   rs=dao.search(sql);
			   if(rs.next())
			   {
				   codeitemdesc=rs.getString("codeitemdesc");  
			   }
		   }catch(Exception e)
		   {
			   throw GeneralExceptionHandler.Handle(e);
		   }finally
		   {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		   }  
		   return codeitemdesc;
	   }
	 public ArrayList getCardList(String status)
	 {
		 	int id_len=0;
	    	KqCardLength kqCardLength=new KqCardLength(this.conn);
	    	try {
				id_len=kqCardLength.tack_CardLen();
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
		    String sql="select card_no from kq_cards where status='"+status+"' and "+Sql_switcher.isnull("card_no","'####'")+"<>'####'"+" and "+ Sql_switcher.length("card_no") + "=" + id_len;
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	ArrayList card_list=new ArrayList();
	    	RowSet rs=null;
	    	try
	    	{
	    		rs=dao.search(sql);
	    		CommonData vo=null;
	    		vo = new CommonData();
				vo.setDataName("");
				vo.setDataValue("");
				card_list.add(vo);
	    		while(rs.next())
	        	{
	    			vo = new CommonData();
	    			vo.setDataName(rs.getString("card_no"));
	    			vo.setDataValue(rs.getString("card_no"));
	    			card_list.add(vo);
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
                        e.printStackTrace();
                    }
                }
		    }  
	    	return card_list;
	 }
	 /***************************
      * 得到用户的基本信息
      * @param userbase 库前缀
      * @param code 部门
      * @A0100 员工编号 
      * 直接this.getFormHM().put();
      * 
      * */
     public String getSingleMessage(String userbase,String A0100){
    	 StringBuffer sql=new StringBuffer();
    	 sql.append("select b0110,e0122,e01a1,a0101 ");
    	 sql.append(" from "+userbase+"A01 ");
    	 sql.append(" where a0100='"+A0100+"'");    	 
    	 String b0110="";
    	 String e0122="";
    	 String e01a1="";
    	 String a0101="";
    	 ContentDAO dao = new ContentDAO(this.conn);
    	 RowSet rs=null;
 		 try {
 			rs = dao.search(sql.toString());
 			if(rs.next()){
 				b0110=(String)rs.getString("b0110");
 				e0122=(String)rs.getString("e0122");
 				e01a1=(String)rs.getString("e01a1");
 				a0101=(String)rs.getString("a0101"); 				
 			}
 		 }catch(Exception e){
 			 e.printStackTrace();
 		 }finally
		 {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		   }   		
 		 b0110=AdminCode.getCodeName("UN",b0110);
 		 e0122=AdminCode.getCodeName("UM",e0122);
 		 e01a1=AdminCode.getCodeName("@K",e01a1); 		
 		 return b0110+"&nbsp;"+e0122+"&nbsp;"+e01a1+"&nbsp;"+a0101;
     }
     /**
      * 发卡，换卡，转换数据
      * @param itemlist
      * @param valuelist
      * @return
      */
     public ArrayList checkDate(ArrayList itemlist,ArrayList valuelist){
 		ArrayList list = new ArrayList();
 		for(int i=0;i<itemlist.size();i++){
 			if(itemlist.get(i)!=null&&itemlist.get(i).toString().length()>0){
 				String itemid = (String)itemlist.get(i);
 				FieldItem fielditem=DataDictionary.getFieldItem(itemid);
 				if(fielditem!=null){
 					String values = (String)valuelist.get(i);
 					if("D".equalsIgnoreCase(fielditem.getItemtype())){
 						if(values!=null&&values.trim().length()>1){
 							fielditem.setValue(values);
 						}else{
 							fielditem.setValue("");
 						}
 					}else{
 						fielditem.setValue(values);
 					}
 					list.add(fielditem);
 				}
 			}
 		}
 		return list;
 	}
}
