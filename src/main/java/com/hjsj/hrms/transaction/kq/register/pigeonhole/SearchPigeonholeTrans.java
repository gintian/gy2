package com.hjsj.hrms.transaction.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.register.pigeonhole.Pigeonhole;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 浏览归档配置
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 2, 2006:10:17:07 AM</p>
 * @author sx
 * @version 1.0
 *
 */
public class SearchPigeonholeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		Pigeonhole pigeonhole=new Pigeonhole(this.getFrameconn(),this.getUserView());
		String re_flag=(String)this.getFormHM().get("re_flag");
		if(re_flag==null||re_flag.length()<=0)
			re_flag="";
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
        this.getFormHM().put("re_flag",re_flag);
        
	}   
    /**
	 * 得到归档方案表里的数据,添加到BS归档方案临时表里面
	 *
	 */
    public ArrayList getPigeonholeXml()
    {
    	String sql="select id,bytes from kq_archive_schema where status=1";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());	
    	ArrayList list=new ArrayList();
    	
    	try
    	{
    	     this.frecset=dao.search(sql);
    	     if(this.frecset.next())
    	     {
    	    	 
    	    	 this.getFormHM().put("bytesid",this.frecset.getString("id"));
    	    	 /**int ch = 0;
    	    	 InputStream isByte=this.frecset.getBinaryStream("bytes"); 
    	    	 BufferedReader br = new BufferedReader(new InputStreamReader(isByte,"GB2312")); 
    	    	 while((ch = br.read())!=-1)
   	    	     {
   	    		 System.out.print((char)ch); 
   	    	     } */
    	    	 String xpath="/ArchScheme/RelaSet";
    	    	/* byte isB[]=this.frecset.getBytes("bytes");    	    	
    	    	 ByteArrayInputStream input=new ByteArrayInputStream(isB);    	*/  
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
    						 one_list.add(relaFld.getAttributeValue("DestFldId").toUpperCase());//归档字段代码
    						 one_list.add(relaFld.getAttributeValue("DestCodeSet"));//归档的字段类型		
    						 one_list.add(relaFld.getAttributeValue("SrcFldId").toUpperCase());//原表的字段代码                         
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
