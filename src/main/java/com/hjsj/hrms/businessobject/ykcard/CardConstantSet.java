package com.hjsj.hrms.businessobject.ykcard;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.util.ArrayList;

public class CardConstantSet {
	private UserView userView;
    private Connection conn;
	public CardConstantSet(){}
	public CardConstantSet(UserView userView,Connection conn)
	{
		   this.userView=userView;
		   this.conn=conn;
	}
	/**
	 * 通过表编号的到表信息
	 * @param cardno
	 * @return CommonData
	 */
	 public ArrayList getCardlist(String  cardno)
	 {
	    	ArrayList list=new ArrayList();
	    	if(cardno==null||cardno.length()<=0) {
                return list;
            }
	    	ContentDAO dao=new ContentDAO (this.conn);
    		String cardnos[]=cardno.split("`");
    		if(cardnos==null||cardnos.length<=0) {
                return list;
            }
    		RowSet rs=null;
	    	try
	    	{
	    		for(int i=0;i<cardnos.length;i++)
		    	{
		    		StringBuffer sql=new StringBuffer();
			    	sql.append("select tabid,name from rname where flagA='A'");
			    	sql.append(" and tabid='"+cardnos[i]+"'");
			    	rs=dao.search(sql.toString());
			    	CommonData dataobj=null;
			    	while(rs.next())
			    	{
			    		String tabid=rs.getString("tabid");
						if(this.userView.isHaveResource(IResourceConstant.CARD, tabid))
						{
							dataobj=new CommonData();
				            dataobj.setDataName("("+rs.getString("tabid")+")"+rs.getString("name"));
				        	dataobj.setDataValue(rs.getString("tabid"));
				        	list.add(dataobj);
						}
			    		   
			    	 }
		    	}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally{
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
	 public ArrayList getCardlistSelfinfo(String  cardno)
	 {
	    	ArrayList list=new ArrayList();
	    	if(cardno==null || cardno.length()==0) {
                return list;
            }
	    	ContentDAO dao=new ContentDAO (this.conn);
    		String cardnos[]=cardno.split("`");
    		if(cardnos==null||cardnos.length<=0) {
                return list;
            }
    		RowSet rs=null;
	    	try
	    	{
	    		for(int i=0;i<cardnos.length;i++)
		    	{
		    		StringBuffer sql=new StringBuffer();
			    	sql.append("select tabid,name from rname where flagA='A'");
			    	sql.append(" and tabid='"+cardnos[i]+"'");
			    	rs=dao.search(sql.toString());
			    	CommonData dataobj=null;
			    	while(rs.next())
			    	{
			    		String tabid=rs.getString("tabid");
						dataobj=new CommonData();
				        dataobj.setDataName("("+rs.getString("tabid")+")"+rs.getString("name"));
				        dataobj.setDataValue(rs.getString("tabid"));
				        list.add(dataobj);
			    		   
			    	 }
		    	}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally{
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
	  * 通过表编号的到表信息
	  * @param cardno
	  * @return String
	  */
	 public String getCardMesslist(String cardno)
	 {
	    	StringBuffer mess=new StringBuffer();
	    	if(cardno==null||cardno.length()<=0) {
                return "";
            }
	    	String cardnos[]=cardno.split("`");
	    	if(cardnos==null||cardnos.length<=0) {
                return "";
            }
	    	ContentDAO dao=new ContentDAO (this.conn);
	    	RowSet rs=null;
	    	try
	    	{
	    		for(int i=0;i<cardnos.length;i++)
		    	{

			    	StringBuffer sql=new StringBuffer();
			    	sql.append("select tabid,name from rname where flagA='A'");
			    	sql.append(" and tabid ='"+cardnos[i]+"'");
			    	rs=dao.search(sql.toString());			    	
			        while(rs.next())
			    	{
			    		   mess.append("("+rs.getString("tabid")+")"+rs.getString("name")+"<br>");
			    	}
		    	}
	    	   //mess.append("<br>");薪酬表设置-表格方式
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally{
	    		if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
	    	return mess.toString();
	  }
	 /**
	  * 代码类列表的信息
	  * @param cardnolist
	  * @return
	  */
	 public ArrayList getCardMessList(ArrayList cardnolist)
	 {
		 ArrayList list=new ArrayList();
		 if(cardnolist==null||cardnolist.size()<=0) {
             return list;
         }
		 for(int i=0;i<cardnolist.size();i++)
		 {
			 String cardno=(String)cardnolist.get(i);
			 if(cardno==null||cardno.length()<=0)
			 {
				 list.add("");
				 continue;
			 }				 
			 String mess=getCardMesslist(cardno);
			 if(mess==null||mess.length()<=0) {
                 list.add("");
             } else {
                 list.add(mess);
             }
		 }
		 return list;
	 }
	 /***
	  * 得到人员基本信息的制定属性的值
	  * @param a0100
	  * @param nbase
	  * @param fieldname
	  * @return
	  */
	 public String getA01Attribute(String a0100,String nbase,String fieldname)
	 {
		 StringBuffer sql=new StringBuffer();
		 sql.append("select "+fieldname+" from ");
		 sql.append(nbase+"A01 ");
		 sql.append(" where a0100='"+a0100+"'");
		 RowSet rs=null;
		 String value="";
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 rs=dao.search(sql.toString());
			 if(rs.next())
			 {
				 value=rs.getString(fieldname);
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally{
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
	  * 得到个人可显示的薪酬列表
	  * @param conn
	  * @param userview
	  * @param flag
	  * @param nbase
	  * @param a0100
	  * @param b0110
	  * @return
	  */
	public ArrayList setCardidSelect(Connection conn, UserView userview,
			String flag, String nbase, String a0100, String b0110) {
		return this.setCardidSelect(conn, userview, flag, nbase, a0100, b0110, false);
	}
	 
	public ArrayList setCardidSelect(Connection conn, UserView userview,
			String flag, String nbase, String a0100, String b0110, boolean isMobile) {
		flag = flag == null || flag.length() == 0 ? "0" : flag;
		XmlParameter xml = new XmlParameter("UN", b0110, "00");
		xml.ReadOutParameterXml("SS_SETCARD", conn, "all");
		ArrayList list = new ArrayList();
		ArrayList codenamelist = xml.getCodenamelist();
		String cardid = "";
		if (isMobile) {
            cardid = xml.getMobcardid();
        }
		cardid = cardid == null || cardid.length()==0 ? xml.getCard_id() : cardid;
		if ("1".equals(flag) && codenamelist != null && codenamelist.size() > 0) {
			String name = (String) codenamelist.get(0);
			String codeitemid = getA01Attribute(a0100, nbase, name);
			ContentDAO dao = new ContentDAO(conn);
			String relating = xml.getRelating();
			b0110 = getRelatingValue(dao, a0100, nbase, relating, b0110);
			XmlParameter xmlParameter = new XmlParameter(conn, b0110, "");
			cardid = xmlParameter.getCardno(name, codeitemid, isMobile);
		}
		list = getCardlist(cardid);
		return list;
	}
	 
	 public ArrayList setCardidSelect(Connection conn,UserView userview,String flag,String nbase,String a0100,String b0110,XmlParameter xml)
	 {
			if(flag==null||flag.length()<=0) {
                flag="0";
            }
			ArrayList list=new ArrayList();
			ArrayList codenamelist=xml.getCodenamelist();
			String cardid=xml.getCard_id();			
			String relating=xml.getRelating();
			if("1".equals(flag)&&codenamelist!=null&&codenamelist.size()>0)
			{
				String name=(String)codenamelist.get(0);			
				String codeitemid=getA01Attribute(a0100,nbase,name);
				ContentDAO dao=new ContentDAO(conn);
				b0110=getRelatingValue(dao,a0100,nbase,relating,b0110);
				XmlParameter xmlParameter =new XmlParameter(conn,b0110,"");
				// WJH 2013-4-20  XML中保存文本都是小写字母
				// cardid=xmlParameter.getCardno(name,codeitemid);
				cardid=xmlParameter.getCardno(name,codeitemid.toLowerCase());			
			}
			list=getCardlist(cardid);
			return list;
	 }
	 
	public ArrayList setCardidSelectSelfinfo(Connection conn, UserView userview,
			String flag, String nbase, String a0100, String b0110) {
		return this.setCardidSelectSelfinfo(conn, userview, flag, nbase, a0100, b0110, false);
	}

	public ArrayList setCardidSelectSelfinfo(Connection conn, UserView userview, 
			String flag, String nbase, String a0100, String b0110, boolean isMobile) {
		flag = flag == null || flag.length() == 0 ? "0" : flag;
		XmlParameter xml = new XmlParameter("UN", b0110, "00");
		xml.ReadOutParameterXml("SS_SETCARD", conn, "all");
		ArrayList codenamelist = xml.getCodenamelist();
		String cardid = "";
		if (isMobile) {
            cardid = xml.getMobcardid();
        }
		cardid = cardid == null || cardid.length()==0 ? xml.getCard_id() : cardid;
		if ("1".equals(flag) && codenamelist != null && codenamelist.size() > 0) {
			String name = (String) codenamelist.get(0);
			String codeitemid = getA01Attribute(a0100, nbase, name);
			ContentDAO dao = new ContentDAO(conn);
			String relating = xml.getRelating();
			b0110 = getRelatingValue(dao, a0100, nbase, relating, b0110);
			XmlParameter xmlParameter = new XmlParameter(conn, b0110, "");
			cardid = xmlParameter.getCardno(name, codeitemid, isMobile);
		}
		return this.getCardlistSelfinfo(cardid);
	}
	 
	 public ArrayList setCardidSelectSelfinfo(Connection conn,UserView userview,String flag,String nbase,String a0100,String b0110,XmlParameter xml) {
		 return this.setCardidSelectSelfinfo(conn, userview, flag, nbase, a0100, b0110, xml, false);
	 }
	 
	public ArrayList setCardidSelectSelfinfo(Connection conn, UserView userview, String flag, 
			String nbase, String a0100, String b0110, XmlParameter xml, boolean isMobile) {
		String cardid = "";
		if ("0".equals(flag)) { // 按单位显示登记表
			// 取电脑设置
			cardid = xml.getCard_id();
			// 取移动端设置
			if (isMobile) {
                cardid = xml.getMobcardid();
            }
			// 当移动端为空时，取电脑设置
			if (isMobile && cardid != null && cardid.length() == 0) {
                cardid = xml.getCard_id();
            }
		} else { // 按类型显示登记表 
			ArrayList codenamelist = xml.getCodenamelist();
			if (codenamelist != null && codenamelist.size() > 0) {
				String name = (String) codenamelist.get(0);
				String codeitemid = getA01Attribute(a0100, nbase, name);
				ContentDAO dao = new ContentDAO(conn);
				String relating = xml.getRelating();
				b0110 = getRelatingValue(dao, a0100, nbase, relating, b0110);
				XmlParameter xmlParameter = new XmlParameter(conn, b0110, "");
				cardid = xmlParameter.getCardno(name, codeitemid.toLowerCase(), isMobile);//全部改为小写 防止因为大小写的问题导致查不出登记表号 changxy 20170308
				// 当移动端为空时，默认走电脑端的设置
				if (isMobile && cardid != null && cardid.length() == 0) {
                    cardid = xmlParameter.getCardno(name, codeitemid.toLowerCase());
                }
			}
		}
		return this.getCardlistSelfinfo(cardid);
	}
	 
	 public ArrayList getMustlist(String  mustid,String inforkind,boolean viewTablid)
	 {
		    ArrayList list=new ArrayList();
		    int nModule = 0;
			if ("1".equals(inforkind)) // 人员库
			{
				inforkind = "A";
				nModule = 3;
			} else if ("3".equals(inforkind)) // 职位库
			{
				inforkind = "K";
				nModule = 1;
			} else if ("2".equals(inforkind)) // 单位库
			{
				inforkind = "B";
				nModule = 2;

			}
	    	if(mustid==null||mustid.length()<=0) {
                return list;
            }
	    	ContentDAO dao=new ContentDAO (this.conn);
 		    String cardnos[]=mustid.split("`");
 		    if(cardnos==null||cardnos.length<=0)
	    	{
	    		  return list;
	    	}
 		    RowSet recset = null;
 		    try {
 		    	
 		    	for(int i=0;i<cardnos.length;i++)
 		    	{
 		    		StringBuffer strsql=new StringBuffer();
 			    	strsql.append("select tabid,cname from muster_name where flagA='");
 					strsql.append(inforkind);
 					strsql.append("'");
 					if ("A".equals(inforkind)) {
                        strsql.append(" and nModule=" + nModule);
                    }
 					/* 此三条记录不予显示 */
 					strsql.append(" and tabid!=1000 and tabid!=1010 and tabid!=1020");
 					strsql.append(" and tabid ='"+cardnos[i]+"'");
 					recset = dao.search(strsql.toString());
 					CommonData dataobj=null;
 					while (recset.next()) {
 						
 					    dataobj=new CommonData();
 					    dataobj.setDataValue(recset.getString("tabid"));
 					   	if(viewTablid) {
                            dataobj.setDataName("("+recset.getString("tabid")+")"+recset.getString("cname"));
                        } else {
                            dataobj.setDataName(recset.getString("cname"));
                        }
 					    
 						list.add(dataobj);
 					}
 		    	}
			} catch (Exception ex) {
				ex.printStackTrace();			
			}finally{
	    		if(recset!=null) {
                    try {
                        recset.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
			return list;
	 }
	 public String getMustmess(String  mustid,String inforkind)
	 {
		    StringBuffer mess=new StringBuffer();
		    int nModule = 0;
			if ("1".equals(inforkind)) // 人员库
			{
				inforkind = "A";
				nModule = 3;
			} else if ("3".equals(inforkind)) // 职位库
			{
				inforkind = "K";
				nModule = 1;
			} else if ("2".equals(inforkind)) // 单位库
			{
				inforkind = "B";
				nModule = 2;

			}
	    	if(mustid==null||mustid.length()<=0) {
                return "";
            }
	    	ContentDAO dao=new ContentDAO (this.conn);
 		    String cardnos[]=mustid.split("`");
 		    if(cardnos==null||cardnos.length<=0)
	    	{
	    		  return "";
	    	}	
			RowSet recset = null;
			try {
				mess.append("<br>");
				for(int i=0;i<cardnos.length;i++)
		    	{
					StringBuffer strsql=new StringBuffer();
			    	strsql.append("select tabid,cname from muster_name where flagA='");
					strsql.append(inforkind);
					strsql.append("'");
					if ("A".equals(inforkind)) {
                        strsql.append(" and nModule=" + nModule);
                    }
					/* 此三条记录不予显示 */
					strsql.append(" and tabid!=1000 and tabid!=1010 and tabid!=1020");
					strsql.append(" and tabid ='"+cardnos[i]+"'");
					recset = dao.search(strsql.toString());					
			    	while(recset.next())
			    	{
			    		   mess.append("("+recset.getString("tabid")+")"+recset.getString("cname")+"<br>");
			    	}			    	
		    	}
				mess.append("<br>");
			} catch (Exception ex) {
				ex.printStackTrace();			
			}finally{
	    		if(recset!=null) {
                    try {
                        recset.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
			return mess.toString();
	 }
	 /**
	  * 花名册信息的信息
	  * @param cardnolist
	  * @return
	  */
	 public ArrayList getMessMessList(ArrayList messlist)
	 {
		 ArrayList list=new ArrayList();
		 if(messlist==null||messlist.size()<=0) {
             return list;
         }
		 for(int i=0;i<messlist.size();i++)
		 {
			 String mustid=(String)messlist.get(i);
			 if(mustid==null||mustid.length()<=0)
			 {
				 list.add("");
				 continue;
			 }				 
			 String mess=getMustmess(mustid,"1");
			 if(mess==null||mess.length()<=0) {
                 list.add("");
             } else {
                 list.add(mess);
             }
		 }
		 return list;
	 }
	 /**
	  * 显示数据调用
	  * @param conn
	  * @param userview
	  * @param nbase
	  * @param a0100
	  * @param b0110
	  * @param intoflag
	  * @return
	  */
	 public ArrayList getMustCommDataList(String nbase,String a0100,String intoflag) throws GeneralException
	 {
		 intoflag="1";
		 ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.conn);
		 String b0110=managePrivCode.getB0110FromA0100(a0100,nbase);		 
		 if(b0110==null||b0110.length()<=0)
		 {
			b0110=managePrivCode.getPrivOrgId();  
		 }	
		 ArrayList list=new ArrayList();
		 XmlParameter xml=new XmlParameter("UN",b0110,"00");
		 xml.ReadOutParameterXml("SS_SETCARD",conn,"all");	
		 String musterid=xml.getMusterid();	
		 ArrayList codenamelist=xml.getCodenamelist();
		 String mustflag=xml.getMustflag();
		 if(mustflag==null||mustflag.length()<=0) {
             mustflag="0";
         }
				
		 if("1".equals(mustflag)&&codenamelist!=null&&codenamelist.size()>0)
		 {
				String name=(String)codenamelist.get(0);			
				String codeitemid=getA01Attribute(a0100,nbase,name);
				if(StringUtils.isEmpty(codeitemid)) {
					throw GeneralExceptionHandler.Handle(new Exception("当前人员【"+DataDictionary.getFieldItem(name).getItemdesc()+"】内容为空，无权查看薪酬表！"));
				}
				XmlParameter xmlParameter =new XmlParameter(conn,b0110,"");
				musterid=xmlParameter.getMustered(name,codeitemid.toLowerCase());//全部转为小写 防止由于大小写的问题 查不出设置的高级花名册表 changxy
		 }
		 list=getMustlist(musterid,intoflag,false);
		 return list;
	 }
	 /**
	  * 得到相关un连接
	  * @param dao
	  * @param a0100
	  * @param nbase
	  * @param relating
	  * @return
	  */
	 public String getRelatingValue(ContentDAO dao,String a0100,String nbase,String relating,String b0110)
	 {
		 if(relating==null||relating.length()<=0) {
             return b0110;
         }
		 if("b0110".equals(relating)) {
             return b0110;
         }
		 if(nbase==null||nbase.length()<=0) {
             nbase="Usr";
         }
		 String sql="select "+relating+" as relat from "+nbase+"A01 where a0100='"+a0100+"'";
		 RowSet rs=null;
		 try
		 {
			 rs=dao.search(sql);
			 if(rs.next()) {
                 relating=rs.getString("relat");
             }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally{
	    		if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
		 return relating;
	 }
	 public String getSearchRelating(ContentDAO dao)
	 {
		 String sql="select str_value from constant where constant='relating'";
		 String relating="";
		 RowSet rs=null;
		 try
		 {
			 rs=dao.search(sql);
			 if(rs.next()) {
                 relating=rs.getString("str_value");
             }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally{
	    		if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
		 if(relating==null||relating.length()<=0) {
             relating="b0110";
         }
		 return relating;
	 }
	 public boolean getInsertRelating(ContentDAO dao,String relating)
	 {
		 boolean isCorrect=true;
		 String del="delete from constant where constant='relating'";
		 String sql="insert into  constant(constant,str_value,describe)values('relating','"+relating+"','薪酬表相关un连接')";
		 try
		 {
			dao.delete(del, new ArrayList());
			dao.insert(sql, new ArrayList());
		 }catch(Exception e)
		 {
			 isCorrect=false;
			 e.printStackTrace();
		 }		 
		 return isCorrect;
	 }
	 /**
	  * 登记表参数rname.extendattr:
      * <DISPLAY_ZERO>显示/打印零,True|False,默认True</DISPLAY_ZERO>
      * <CONTINUOUS_NUMBERING>多报表页码连续编号True|False,默认False</CONTINUOUS_NUMBERING>
      * <AUTO_SIZE>单元格字体自适应True|False,默认True</AUTO_SIZE>
	  * @param extendattr
	  * @return
	  */
	 public LazyDynaBean getRnameExtendAttrBean(Connection conn,String tabid)
	 {
		  String extendattr="";
		  StringBuffer sql=new StringBuffer();
		  sql.append("select extendattr  from rname where tabid='"+tabid+"'");		
		  ContentDAO dao=new ContentDAO(conn);
		  RowSet rs=null;
		  try
		  {
				
				rs=dao.search(sql.toString());
				if(rs.next())
				{
					extendattr=Sql_switcher.readMemo(rs, "extendattr");
				}
		  }catch(Exception e)
		  {
			e.printStackTrace();  
		  }finally{
	    		if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
	    	}
		  if(extendattr==null||extendattr.length()<=0) {
              return null;
          }
		  extendattr=extendattr.toLowerCase();
	      LazyDynaBean rec=new LazyDynaBean();
	      if(extendattr!=null&&extendattr.length()>0)
		  {
				String display_zero="";//显示/打印零,True|False,默认True
				String continuous_numbering="";//多报表页码连续编号True|False,默认False
				String auto_size="";	//单元格字体自适应True|False,默认True			
				if(extendattr.indexOf("<display_zero>")!=-1&&extendattr.indexOf("</display_zero>")!=-1)
				{
					int i=extendattr.indexOf("<display_zero>");
		    		int s=extendattr.indexOf(">",i);
		    		int e=extendattr.indexOf("</display_zero>");
		    		display_zero=extendattr.toString().substring(s+1,e);
					//display_zero=extendattr.substring(extendattr.indexOf("<display_zero>")+5,extendattr.indexOf("</display_zero>"));
					if(display_zero!=null&& "false".equalsIgnoreCase(display_zero)) {
                        rec.set("display_zero", "0");
                    } else {
                        rec.set("display_zero", "1");
                    }
				}
				if(extendattr.indexOf("<continuous_numbering>")!=-1&&extendattr.indexOf("</continuous_numbering>")!=-1)
				{
					int i=extendattr.indexOf("<continuous_numbering>");
		    		int s=extendattr.indexOf(">",i);
		    		int e=extendattr.indexOf("</continuous_numbering>");
		    		continuous_numbering=extendattr.toString().substring(s+1,e);
					//continuous_numbering=extendattr.substring(extendattr.indexOf("<continuous_numbering>")+9,extendattr.indexOf("</continuous_numbering>"));
					rec.set("continuous_numbering", continuous_numbering);
				}
				if(extendattr.indexOf("<auto_size>")!=-1&&extendattr.indexOf("</auto_size>")!=-1)
				{
					int i=extendattr.indexOf("<auto_size>");
		    		int s=extendattr.indexOf(">",i);
		    		int e=extendattr.indexOf("</auto_size>");
		    		auto_size=extendattr.toString().substring(s+1,e);
					//auto_size=extendattr.substring(extendattr.indexOf("<auto_size>")+13,extendattr.indexOf("</auto_size>"));
					if(auto_size!=null&& "false".equalsIgnoreCase(auto_size)) {
                        rec.set("auto_size", "0");
                    } else {
                        rec.set("auto_size", "1");
                    }
				}				
		 }
	     return rec;
	 }
	 
	/**
	 * 取岗位体系(基准岗位)代码类
	 * @return
	 */
	public String getStdPosCodeSetId(){
		return ConstantParamter.getRealConstantVo("PS_C_CODE").getString("str_value");
	}
}
