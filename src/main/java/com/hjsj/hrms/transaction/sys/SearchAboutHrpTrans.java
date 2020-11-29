package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.general.template.ITemplateConstant;
import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
/**
 * <p>Title:SearchAboutHrpTrans</p>
 * <p>Description:产品注册信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2005:4:04:52 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchAboutHrpTrans extends IBusiness {
    public SearchAboutHrpTrans() {
    }

    public void execute() throws GeneralException {
//    	HashMap map=this.getFormHM();
//    	String aaa=(String)map.get("aaa");
//    	List list0=(List)map.get("list");
//    	DynaBean dybean=(DynaBean)map.get("emp");
    	
        ArrayList list=new ArrayList();
        CommonData vo=new CommonData();
        vo.setDataName("x");
        vo.setDataValue("80");
        list.add(vo);
        CommonData vo1=new CommonData();
        vo1.setDataName("y");
        vo1.setDataValue("400");
        list.add(vo1);
        CommonData vo2=new CommonData();
        vo2.setDataName("z");
        vo2.setDataValue("1000");
        list.add(vo2);
        CommonData vo3=new CommonData();
        vo3.setDataName("e");
        vo3.setDataValue("20");
        list.add(vo3);  
//        this.getFormHM().clear();
//        this.getFormHM().put("Boolean", new Boolean(true));
//        this.getFormHM().put("Integer", new Integer(20));
//        this.getFormHM().put("Float",new Float(21.99));
//        this.getFormHM().put("Double",new Double(21.99));

        this.getFormHM().put("list",list);
        //CodeItem codeitem=AdminCode.getCode("UM","0010701",3);
        //System.out.println("--->"+codeitem.getCodename());
        //System.out.println("--->"+codeitem.getCodeitem());
        //testToBuilder();
//        HashMap map=new HashMap();
//        map.put("a1", "sss");
//        map.put("b1", "bbb");
//        String temp=(String)map.get("a1");
//        map.remove("a1");
        
        //testTemplateTable();
        
        try
        {	

        	//在群集环境下测试序号生成器规则，解决序号重复的问题
        	
//        	IDGenerator idg=new IDGenerator(2,this.getFrameconn());
//        	String value=idg.getId("SUGGEST.ID");
//        	System.out.println(this.userView.getUserName()+"-->id="+value);
//        	ArrayList lista=idg.getId("SUGGEST.ID", 10);
//        	for(int i=0;i<lista.size();i++)
//        		System.out.println(this.userView.getUserName()+"--->="+lista.get(i));
			
        	/*
        	DbWizard dbw=new DbWizard(this.getFrameconn());
  	        DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());	        	
			Table table=new Table("test");
			table.setBTemporary(true);
			  Field field=new Field("setid");
			  field.setDatatype("string");
			  field.setLength(50);	
			  field.setNullable(false);
			  field.setKeyable(true);
			  table.addField(field);
			  dbw.createTable(table);
			  dbmodel.reloadTableModel("test");			  
        	*/
        	/*
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer strsql=new StringBuffer();
			strsql.append("select username from operuser");
			RowSet rowset=null;
			System.out.println("rowset begin=====================");				
			rowset=dao.search(strsql.toString());
			while(rowset.next())
			{
			  System.out.println("----->"+rowset.getString("username"));
			}	
			rowset.close();
			rowset=dao.search(strsql.toString());
			while(rowset.next())
			{
			  System.out.println("----->"+rowset.getString("username"));
			}	
			rowset.close();		
			*/		
//	        ArrayList sqllist=new ArrayList();
//	        sqllist.add("insert into test(a,b,c) values('1','2','3')");
//	        System.out.println(Integer.MAX_VALUE);
//	        System.out.println(Integer.MIN_VALUE);
//	        dao.batchUpdate(sqllist);

//			RecordVo recvo=new RecordVo("salaryhistory");
//			recvo.setInt("a00z1",1);
//			recvo.setString("nbase","usr");
//			recvo.setInt("salaryid",1);
//			recvo.setString("a0100","000000001");
//
//			recvo.setDate("a00z0","2003-10-10 12:20:20");
//			recvo=dao.findByPrimaryKey(recvo);
	        Des des=new Des();
	        String productno=des.getProductSerialNo(1);
	        this.getFormHM().put("productno", productno);
	        //String a0100 =DbNameBo.insertMainSetA0100("otha01", this.getFrameconn());
	       // System.out.println("--->"+this.userView.getUserName()+"="+a0100);
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        //updateActiveTable("ssss","1");
    }

   private void testToBuilder()
   {
     try
     {
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        String strsql="select * from fieldset";
	        RowSet rset=dao.search(strsql);
	        RowSetToXmlBuilder toxml=new RowSetToXmlBuilder(this.getFrameconn());
	        System.out.println("===>="+toxml.outPutXml(rset, "fieldset"));
     }
     catch(Exception ex)
     {
     	ex.printStackTrace();
     }	   
   }
	/**
	 * @throws GeneralException
	 */
	private void testTemplateTable() throws GeneralException {
		TemplateTableBo template_bo=new TemplateTableBo(this.getFrameconn(),54,this.userView);
        template_bo.getAllTemplate(ITemplateConstant.CAREER,ITemplateConstant.SALARY);
        ArrayList pagelist=template_bo.getAllTemplatePage();
        for(int i=0;i<pagelist.size();i++)
        {
        	TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
        	ArrayList celllist=pagebo.getAllCell();
        	for(int j=0;j<celllist.size();j++)
        	{
        		TemplateSetBo setbo=(TemplateSetBo)celllist.get(j);
        		System.out.println("setbo="+setbo.toString());
        	}
        	ArrayList fieldlist=pagebo.getAllFieldItem();
        	for(int k=0;k<fieldlist.size();k++)
        	{
        		FieldItem item=(FieldItem)fieldlist.get(k);
        		System.out.println("fieldItem=>"+item.toString());
        	}
        }
        template_bo.createTempTemplateTable(this.userView.getUserName());
	}

    /**
     * 修改归档业务表
     * @param act_xml
     * @param id
     * @throws GeneralException
     */
    public void updateActiveTable(String act_xml,String id)throws GeneralException
    {
    	String update="update kq_archive_schema set content=? where id=?";
    	try
    	{
        	ContentDAO dao=new ContentDAO(this.getFrameconn());     		
    		ArrayList list=new ArrayList();
        	switch (Sql_switcher.searchDbServer())
        	{
    		    case Constant.ORACEL:
    			//适用于ORACEL
    		    	Blob blob=getOracleBlob(id,act_xml);
    		    	list.add(blob);
    			  break;
    		    default:
        	      list.add(act_xml.getBytes());
    		      break;
    		}
        	list.add(id);

    	    
    		dao.update(update,list);
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	   throw GeneralExceptionHandler.Handle(e);
    	}
    }
	private Blob getOracleBlob(String id, String str)throws FileNotFoundException, IOException 
	{
		     
		     InputStream isByte = new ByteArrayInputStream(str.getBytes());
		     
             StringBuffer strSearch = new StringBuffer();
     		strSearch.append("select content from resource_list where contentid='");
    		strSearch.append(id);
    		strSearch.append("' FOR UPDATE");

             StringBuffer strInsert = new StringBuffer();
     		strInsert
			.append("update  resource_list set content=EMPTY_BLOB() where contentid='");
     		strInsert.append(id);
     		strInsert.append("'");
             OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
              Blob blob =
            	  blobutils.readBlob(strSearch.toString(), strInsert.toString(), isByte); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
            return blob;
   }	
    
}
