package com.hjsj.hrms.transaction.kq.register.pigeonhole;

import com.hjsj.hrms.interfaces.kq.SaveActivPigeonholeXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 考勤归档配置保存
 * <p>Title:SavePigeonholeTrans.java</p>
 * <p>Description>:SavePigeonholeTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 26, 2011 3:12:40 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SavePigeonholeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		this.getFormHM().put("pigeonhole_flag","save_false");
		ArrayList filedlist=(ArrayList)this.getFormHM().get("list");		
		String setlist=(String)this.getFormHM().get("setlist");
		String temp_table_oper=(String)this.getFormHM().get("temp_table");
		String bytesid=(String)this.getFormHM().get("bytesid");		
		SaveActivPigeonholeXml activeXml=new SaveActivPigeonholeXml(this.getFrameconn(),temp_table_oper);
		String act_xml=activeXml.createXml("Q05",setlist,filedlist);		
		updateActiveTable(act_xml,bytesid);//修改归档业务
		this.getFormHM().put("pigeonhole_flag","save_true");
		/***********操作考勤归档临时数据表**********/		
		/*Pigeonhole pigeonhole =new Pigeonhole(this.getFrameconn(),this.userView);
		String data_table_name=pigeonhole.getTmpTableName(this.userView.getUserId(),"Pigeonh");
		String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());	
		*//*************得到归档的次数和对应的月**************//*
		String month_pigeonhole=RegisterDate.getKqMonth(this.getFrameconn(),kq_duration);
		String num_pigeonhole=RegisterDate.getKqNum(this.getFrameconn(),kq_duration);		
		//ArrayList kqPrivDbList=getKqAllPrivDbList(kq_duration);
		ArrayList kqPrivDbList=this.userView.getPrivDbList();
		String pigeonhole_flag="true";
		try
		{
			for(int i=0;i<kqPrivDbList.size();i++)
			{
				  String nbase= kqPrivDbList.get(i).toString();		
				  if(!nbase.equalsIgnoreCase("XXX"))
					  continue;
				  pigeonhole.insertInitTempData(data_table_name,kq_duration,nbase,tempCloumn,"");
				  //pigeonhole.delectInIt(nbase,setlist,month_pigeonhole,num_pigeonhole,data_table_name);
				  pigeonhole.insertInitDestData(data_table_name,setlist,kq_duration,nbase,month_pigeonhole,this.userView.getUserFullName(),num_pigeonhole);
				  pigeonhole.updateI9999(nbase,setlist,month_pigeonhole,num_pigeonhole,true);
				  pigeonhole.updateDestData(temp_table_oper,data_table_name,nbase,setlist,month_pigeonhole,this.userView.getUserFullName(),num_pigeonhole);
			}
		}catch(Exception e)
		{
			pigeonhole_flag="false";
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.pigeonhole.save.lost"),"",""));
		}
		pigeonhole.dropTable(data_table_name);
		this.getFormHM().put("pigeonhole_flag",pigeonhole_flag);
		*/
	}
	/**
	 * 修改归档临时表
	 * @param filedlist
	 * @param temp_table
	 */
    public String saveTempDate(ArrayList filedlist,String temp_table)
    {
    	 DynaBean dbean=null;
    	 String srcFldId=null;
    	 String destFldId=null;
    	 String destFldName=null;
    	 ArrayList list=new ArrayList();
    	 StringBuffer update=new StringBuffer();
    	 update.append("update "+temp_table+" set");
    	 update.append(" DestFldId=?,DestFldName=?");
    	 update.append(" where SrcFldId=?");
    	 StringBuffer tempCloumn=new StringBuffer();
    	 try
         {
 	        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
 	        tempCloumn.append("a0100,i9999");
 			for(int i=0;i<filedlist.size();i++)
 		    {
 				ArrayList one_list=new ArrayList();
 				dbean=(LazyDynaBean)filedlist.get(i);
 				srcFldId=(String)dbean.get("srcfldid");	
 				destFldId=(String)dbean.get("destfldid");	
 				destFldName=(String)dbean.get("destfldname");	
 				if(destFldId!=null&&destFldId.length()>0&&destFldName!=null&&destFldName.length()>0)
 				{
 					one_list.add(destFldId);
 					one_list.add(destFldName);
 					one_list.add(srcFldId);
 					list.add(one_list);
 					tempCloumn.append(","+srcFldId);
 				}
 		    }
 			dao.batchUpdate(update.toString(),list);
         }catch(Exception e)
         {
        	 e.printStackTrace();
         }
         return tempCloumn.toString();
    }
    /**
     * 修改归档业务表
     * @param act_xml
     * @param id
     * @throws GeneralException
     */
    public void updateActiveTable(String act_xml,String id)throws GeneralException
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn()); 
    	try
    	{
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
                blobutils.modifyClob("kq_archive_schema", "bytes", act_xml, "id=" + id);
            } else {
                StringBuffer sql = new StringBuffer();
                sql.append("update kq_archive_schema");
                sql.append(" set bytes = '" + act_xml + "'");
                sql.append(" where id = '" + Integer.parseInt(id) + "'");
                dao.update(sql.toString());
            }
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	   throw GeneralExceptionHandler.Handle(e);
    	}
    }
}
