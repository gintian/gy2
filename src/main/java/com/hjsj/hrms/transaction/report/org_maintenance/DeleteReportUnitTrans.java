
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>Title:删除填报单位信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 18, 2006:9:43:45 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class DeleteReportUnitTrans extends IBusiness {

	//删除填报单位信息
	public void execute() throws GeneralException {
		//要删除的填报单位集合
		ArrayList delreportlist=(ArrayList)this.getFormHM().get("selectedlist");
		
		ArrayList reportUId=new ArrayList();
		 
		if(delreportlist==null||delreportlist.size()==0){
			if(this.getFormHM().get("codeflag")== null || "".equals(this.getFormHM().get("codeflag"))){
				this.getFormHM().put("delflag",null);
			}else{
				this.getFormHM().put("delflag",this.getFormHM().get("codeflag"));
			}
			
			return;
		} 
        String temp = null;
        
        StringBuffer delsql=new StringBuffer();
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        DbWizard dbWizard=new DbWizard(this.getFrameconn());
        try
        {
        	//temp删除后的页面定位使用
        //	RecordVo voo=(RecordVo)delreportlist.get(0);
        	LazyDynaBean voo=(LazyDynaBean)delreportlist.get(0);
        	temp = this.getParentID((String)voo.get("unitcode"));
        	//uid  list
        	
        	for(int i=0;i<delreportlist.size();i++){  		
        	//	RecordVo vo=(RecordVo)delreportlist.get(i);
        		LazyDynaBean vo=(LazyDynaBean)delreportlist.get(i);
        		String unitCode =(String)vo.get("unitcode");
        		reportUId.add(unitCode);
        		ArrayList unitCodeList = null;
        		ArrayList tabidList = null;
        		
        		//删除填报单位对应的数据
        		unitCodeList = this.getUnitCodeList(unitCode);
        		
        		if(unitCodeList == null){        			
        		}else{
        			for(int j = 0 ; j< unitCodeList.size(); j++){
            			String uc = (String)unitCodeList.get(j);
            		//	System.out.println("uc=" + uc);
            			tabidList = this.getTabidList(uc);
            			if(tabidList == null ){	
            			}else{
            				for(int k = 0 ; k<tabidList.size(); k++){
    	    					 String tid = (String)tabidList.get(k);
    	    					 Table table=new Table("tt_"+tid);
    	    	        		 if(dbWizard.isExistTable(table.getName(),false))
    	    	        	     {
    	    	        			delsql.delete(0,delsql.length());
    	    	        			delsql.append("delete from tt_");
    	    	                	delsql.append(tid);
    	    	             		delsql.append(" where unitcode = '");
    	    	             		delsql.append(uc);
    	    	             		delsql.append("'");
    	    	             		//System.out.println("delete=" + delsql.toString());
    	    	             		dao.delete(delsql.toString(),new ArrayList());
    	    	        		 }
            				}
            			}
            		}
        		}
        		
        		
        		//删除填报单位表中填报单位
        		delsql.delete(0,delsql.length());
        		delsql.append("delete from tt_organization where unitcode like  '");
           		delsql.append(unitCode);
        		delsql.append("%'");
        		dao.delete(delsql.toString(),new ArrayList());
        		
        		//更新用户表信息
        		delsql.delete(0,delsql.length());
        		delsql.append("update operuser set unitcode=null where unitcode like '");
           		delsql.append(unitCode);
        		delsql.append("%'");
        		dao.update(delsql.toString());
        		
        		//删除填报单位上报信息
        		delsql.delete(0,delsql.length());
        		delsql.append("delete from treport_ctrl where unitcode like  '");
           		delsql.append(unitCode);
        		delsql.append("%'");
        		dao.delete(delsql.toString(),new ArrayList());
        		
        	}
     
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    this.getFormHM().put("reportUId",reportUId);
	    this.getFormHM().put("addFlag","no");
	  	this.getFormHM().put("delflag",temp);  
	}
	
	/**
	 * 获得一个填报单位对应的填报单位编码集合
	 * 删除时要连同当前填报单位的子单位也删除
	 * @param unitCode 填报单位编码
	 * @return         至少有一个
	 * @throws GeneralException 
	 */
	public ArrayList getUnitCodeList(String unitCode) throws GeneralException{
		ArrayList list = new ArrayList();
	//	list.add(unitCode);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql = "select unitcode  from tt_organization where unitcode like '" + unitCode +"%'";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				list.add((String)this.frowset.getString("unitcode"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception sqle) {
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);
	    }
		return list;
	}
	
	
	/**
	 * 获得一个填报单位对应的报表ID集合
	 * @param unitCode 填报单位编码
	 * @return         0个或多个
	 * @throws GeneralException 
	 */
	public ArrayList getTabidList(String unitCode) throws GeneralException{
		ArrayList list = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String reporttypes = null;
		
		//查询报表类型
		String sql = "select reporttypes from tt_organization where unitcode = '" + unitCode +"'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				reporttypes = (String)this.frowset.getString("reporttypes");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception sqle) {
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);
	    }
		
		//获得报表对应的报表ID
		if(reporttypes == null || "".equals(reporttypes)){//没有对应的报表
			return null;
		}else{
			if(reporttypes.charAt(reporttypes.length()-1) == ','){
				reporttypes = reporttypes.substring(0,reporttypes.length()-1);
			}
			String sql1 ="select tabid from tname where tsortid in("+reporttypes+")";
			try {
				this.frowset = dao.search(sql1);
				while(this.frowset.next()){
					list.add((String)this.frowset.getString("tabid"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}catch(Exception sqle) {
			     sqle.printStackTrace();
			     throw GeneralExceptionHandler.Handle(sqle);
		    }
		}
		
		return list;
	}
	
	
	
	/**
	 * 获取父填报单位编码  
	 * @param unitcode 填报单位编码
	 * @return
	 * @throws GeneralException
	 */
	public String getParentID(String unitcode) throws GeneralException{
		String temp=null;
		StringBuffer strsql = new StringBuffer();
		strsql.delete(0,strsql.length());	
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			//SQL
			strsql.append("select parentid from  tt_organization where  unitcode= '");
			strsql.append(unitcode);
			strsql.append("'");
		//	System.out.println(strsql.toString());
			//执行SQL
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next()){
				temp=(String)this.frowset.getString("parentid");
				if(temp.equals(unitcode)){
					temp = null;
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return temp;
	}
}
