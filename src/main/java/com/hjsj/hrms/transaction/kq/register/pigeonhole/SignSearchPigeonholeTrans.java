package com.hjsj.hrms.transaction.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.register.pigeonhole.Pigeonhole;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SignSearchPigeonholeTrans extends IBusiness {
	private  String error_return="/kq/register/browse_registerdata.do?b_search=link";	
	public void execute() throws GeneralException 
	{
		
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");		
		this.getFormHM().put("pigeonhole_flag","1");
		this.getFormHM().put("pigeonhole_flag2","0");		
		if(selectedinfolist==null||selectedinfolist.size()==0)
		{
			try
			{
				selectedinfolist=(ArrayList)this.getFormHM().get("singlist");
			}catch(Exception e)
			{
				  String error_message=ResourceFactory.getProperty("kq.register.noselect.manager");	
		 		  this.getFormHM().put("error_message",error_message);
		 	      this.getFormHM().put("error_return",this.error_return);  
		 	      this.getFormHM().put("error_flag","2");
		 	      this.getFormHM().put("error_stuts","1");
		 	      return;
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));
			}			
			this.getFormHM().put("singlist","");
			if(selectedinfolist==null||selectedinfolist.size()==0)
			{
				  String error_message=ResourceFactory.getProperty("kq.register.noselect.manager");	
		 		  this.getFormHM().put("error_message",error_message);
		 	      this.getFormHM().put("error_return",this.error_return);  
		 	      this.getFormHM().put("error_flag","2");
		 	      this.getFormHM().put("error_stuts","1");
		 	      return;
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));	
			}
			
		}
			try
			{
				for(int i=0;i<selectedinfolist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);
					String q03z5=rec.get("q03z5").toString()!=null&&rec.get("q03z5").toString().length()>0?rec.get("q03z5").toString():"01";
					if("03".equals(q03z5)|| "04".equals(q03z5)|| "02".equals(q03z5))
					{
					  continue;	
					}else
					{
						String error_message=ResourceFactory.getProperty("kq.org.employee.noapprove");	
				 		  this.getFormHM().put("error_message",error_message);
				 	      this.getFormHM().put("error_return",this.error_return);  
				 	      this.getFormHM().put("error_flag","2");
				 	      this.getFormHM().put("error_stuts","1");
				 	      return;
						//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.org.employee.noapprove"),"",""));
					}
				}
			}catch(Exception e)
			{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.pigeonhole.save.lost"),"",""));
			}
			
		
		Pigeonhole pigeonhole=new Pigeonhole(this.getFrameconn(),this.userView);
		String temp_table=pigeonhole.createTempTable(this.userView.getUserName());//建立临时表		
		pigeonhole.insertActivPigeonhole(temp_table);//插入信息
		ArrayList xmlList=getPigeonholeXml();
		pigeonhole.updateActivPigeonhole(temp_table,xmlList);//修改
		String sqlstr="select SrcFldType,SrcFldId,SrcFldName,DestFldId,DestFldName";
		String wherestr=" from "+temp_table;
		String column="SrcFldType,SrcFldId,SrcFldName,DestFldId,DestFldName";
		this.getFormHM().put("po_sqlstr",sqlstr);
		this.getFormHM().put("po_wherestr",wherestr);
		this.getFormHM().put("po_column",column);
		this.getFormHM().put("temp_table",temp_table);		
		this.getFormHM().put("po_userlist",selectedinfolist);
		this.getFormHM().put("error_flag","0");
		
	}   
    /**
	 * 得到归档方案表里的数据,添加到BS归档方案临时表里面
	 *
	 */
    public ArrayList getPigeonholeXml()
    {
    	String sql="select id,bytes from kq_archive_schema";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ArrayList list=new ArrayList();
    	
    	try
    	{
    	     this.frecset=dao.search(sql);
    	     if(this.frecset.next())
    	     {
    	    	
    	    	 this.getFormHM().put("bytesid",this.frecset.getString("id"));
    	    	 /*int ch = 0;
    	    	 InputStream isByte=this.frecset.getBinaryStream("bytes"); 
    	    	 BufferedReader br = new BufferedReader(new InputStreamReader(isByte,"GB2312")); 
    	    	 while((ch = br.read())!=-1)
   	    	     {
   	    		 System.out.print((char)ch); 
   	    	     }*/
    	    	 String xpath="/ArchScheme/RelaSet";
    	    	 String xmlContent=Sql_switcher.readMemo(this.frecset,"bytes");
    	    	 	 
    	    	 if(xmlContent!=null&&xmlContent.length()>0)
    	    	 {
    	    		 Document doc = PubFunc.generateDom(xmlContent);     				
     				 XPath reportPath = XPath.newInstance(xpath);
     				 List setlist=reportPath.selectNodes(doc);  
     				 Iterator i = setlist.iterator();
    				 if(i.hasNext())
    				 {   
    					 Element childR=(Element)i.next();
    					 childR.getAttributeValue("SrcFldSet");//原表表名
    					 String DestFldSet=childR.getAttributeValue("DestFldSet");//归档目标表名
    					 this.getFormHM().put("destfld",DestFldSet);
    					 List fldlist=childR.getChildren();					 
    					 Iterator tt = fldlist.iterator();
    					 while(tt.hasNext())
    					 {
    						 ArrayList one_list=new ArrayList();
    						 Element relaFld=(Element)tt.next();
                             
    						 one_list.add(relaFld.getAttributeValue("DestFldName"));//归档的字段名称
    						 one_list.add(relaFld.getAttributeValue("DestFldId"));//归档字段代码
    						 one_list.add(relaFld.getAttributeValue("DestCodeSet"));//归档的字段类型		
    						 one_list.add(relaFld.getAttributeValue("SrcFldId"));//原表的字段代码                         
    						 list.add(one_list);
    					}
    				 }
    	    	 }
    	     }else
    	     {
    	    	 inItPigeonholeXml();
    	    	 this.getFormHM().put("bytesid","1");
    	     }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    } 
   public void inItPigeonholeXml()
   {
	   String insert="insert into kq_archive_schema (id,status) values (?,?)";
	   ArrayList list=new ArrayList ();
	   list.add("1");
	   list.add("1");
	   ContentDAO dao=new ContentDAO(this.getFrameconn());	
	   try
	   {
		  dao.insert(insert,list);
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }

}
