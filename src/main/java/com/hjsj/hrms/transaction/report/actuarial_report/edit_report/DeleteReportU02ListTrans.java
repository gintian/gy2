package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteReportU02ListTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm=this.getFormHM();
    	ArrayList list=(ArrayList)hm.get("data_table_record");	
    	ContentDAO dao=new ContentDAO(this.getFrameconn());	
    	String names="";
    	int j=0;
    	try
		{
    		for(int i=0;i<list.size();i++)
        	{
        		RecordVo vo=(RecordVo)list.get(i);
        		//System.out.println("vo.getString:"+vo.getString("editflag"));
        		if(vo.getString("editflag")!=null&&("1".equalsIgnoreCase(vo.getString("editflag"))|| "2".equalsIgnoreCase(vo.getString("editflag"))))
        		{
        			//判断上期是否存在该记录
        			String u0200 = vo.getString("u0200");
        			String name =  vo.getString("u0203");
        			vo =dao.findByPrimaryKey(vo);
        			int id = vo.getInt("id");
        			
        			RecordVo vo2 = new RecordVo("tt_cycle");
        			vo2.setInt("id", id);
        			vo2 =dao.findByPrimaryKey(vo2);
        			
        			int kmethod =vo2.getInt("kmethod");
        			if(kmethod==0){
        			String sql = "select id from u02 where u0200='"+u0200+"'and id< "+id;
        			this.frowset = dao.search(sql);
        			if(this.frowset.next()){
        				 vo2 = new RecordVo("tt_cycle");
            			vo2.setInt("id", this.frowset.getInt("id"));
            			vo2 =dao.findByPrimaryKey(vo2);
            			 kmethod =vo2.getInt("kmethod");
            			if(kmethod==0){
            				if(j>10){
            					names =names+"...,";
            					break;
            				}else{
            			names =names+name+",";
            				}
            				j++;
            			}
        			}
        			}
//        		  //同时把状态表改为正在编辑
//      				if(i==0){
//      				String escope=vo.getString("escope");				
//      				String report_id="U02_"+escope;
//      				String id=vo.getString("id");
//      				String unitcode=vo.getString("unitcode");
//      				System.out.println("report_id:"+report_id+"id:"+id+"unitcode:"+unitcode);
//      				RecordVo vo2=new RecordVo("tt_calculation_ctrl");
//					vo2.setInt("id", Integer.parseInt(id));
//					vo2.setString("unitcode",unitcode);
//					vo2.setString("report_id",report_id);
//					vo2=dao.findByPrimaryKey(vo2);
//					vo2.setInt("flag", 0);	
//					dao.updateValueObject(vo2);
//      				}
      			
        		}
        		else
        			throw new GeneralException("选择数据为不可编辑状态,删除数据失败！");
        	}
    		if(names.length()>0){
    			names = names.substring(0,names.length()-1);
    			throw new GeneralException("上期已存在以下人员:"+names+"\r\n删除失败！");
    		}
    		for(int i=0;i<list.size();i++)
        	{
        		RecordVo vo=(RecordVo)list.get(i);
        		//System.out.println("vo.getString:"+vo.getString("editflag"));
        		if(vo.getString("editflag")!=null&&("1".equalsIgnoreCase(vo.getString("editflag"))|| "2".equalsIgnoreCase(vo.getString("editflag"))))
        		{
        			
        		  dao.deleteValueObject(vo);

        		}
        		else
        			throw new GeneralException("选择数据为不可编辑状态,删除数据失败！");
        	}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
    	
    }

}
