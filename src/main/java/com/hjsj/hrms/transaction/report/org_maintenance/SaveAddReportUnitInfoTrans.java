
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:保存增加的填报单位信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 12, 2006:8:46:47 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class SaveAddReportUnitInfoTrans extends IBusiness {

	
	//保存用户添加的填报单位信息
	public void execute() throws GeneralException {
	
		String parentCode = ((String)this.getFormHM().get("parentCode")).trim();
		String unitName = ((String)this.getFormHM().get("addUnitName")).trim();
		String unitCode = ((String)this.getFormHM().get("addUnitCode")).trim();
		
		String start_date=((String)this.getFormHM().get("start_date")).trim();
		String end_date=((String)this.getFormHM().get("end_date")).trim();
		
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		//String  ss =hm.get("addUnitName").toString().trim();
		String len = ((String)this.getFormHM().get("len")).trim();
		if("0".equals(len)){
			this.getFormHM().put("delflag" ,parentCode );
			this.getFormHM().put("addFlag","no");
		}else{
			if(unitCode == null || "".equals(unitCode)){
				GeneralException e = new GeneralException(ResourceFactory.getProperty("saveaddreportinfo.unitcodenull"));
				throw GeneralExceptionHandler.Handle(e);
			}else if(unitName == null || "".equals(unitName)){
				GeneralException e = new GeneralException(ResourceFactory.getProperty("eidt_report.info9")+"!");
				throw GeneralExceptionHandler.Handle(e);
			}else{
	
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				StringBuffer sql = new StringBuffer();
				
				//验证用户添加的单位编码是否与DB中存在的冲突
				sql.append("select * from tt_organization where unitcode = '");
				sql.append(parentCode +unitCode);
				sql.append("'");
				
				try{	
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next()){//如果DB中存在用户所添加的单位编码
						//提示错误信息
					
						GeneralException e = new GeneralException(ResourceFactory.getProperty("saveaddreportinfo.unitcodeexist"));
						throw GeneralExceptionHandler.Handle(e);
					}else{
						sql.delete(0,sql.length());
						sql.append("insert into tt_organization (unitcode , unitid , ");
						sql.append("unitname ,parentid,childid,grade,flag,reporttypes,b0110,a0000,start_date,end_date) values (?,?,?,?,?,?,?,?,?,?,?,?)");
						ArrayList sqlvalue=new ArrayList();
						sqlvalue.add(parentCode +unitCode);//填报单位编码
						sqlvalue.add(new Integer(this.getUnitID()));//填报单位编号
						sqlvalue.add(unitName);	//填报单位名称
						if("".equals(parentCode) || parentCode == null ){////填报单位父
							sqlvalue.add(unitCode);
						}else{
							sqlvalue.add(parentCode);
						}
						sqlvalue.add(null);//填报单位子
						sqlvalue.add(new Integer(this.getGrade(parentCode)));//填报单位等级
						sqlvalue.add(null);
						sqlvalue.add(null);
						sqlvalue.add(null);
						sqlvalue.add(new Integer(this.getUnitID()));
						
						sqlvalue.add(Date.valueOf(start_date));
						sqlvalue.add(Date.valueOf(end_date));
					 
						
						
						try{	
							dao.insert(sql.toString(),sqlvalue);
						}catch(Exception e){
						   e.printStackTrace();
						   throw GeneralExceptionHandler.Handle(e);
						}
						this.getFormHM().put("addUnitCode" ,unitName);
						this.getFormHM().put("addUnitName" ,unitCode);
						this.getFormHM().put("unitCodeFalg" ,parentCode +unitCode );
						this.getFormHM().put("addFlag","yes");
					}
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}				
			}
		}
	}
	
	
	/**
	 * 获得单位编号
	 */
	public synchronized int getUnitID() throws GeneralException{
		int num = 0;  //序号默认为0
		String sql="select max(unitid) as num  from tt_organization";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				num = this.frowset.getInt("num");
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num+1;		
	}
	
	/**
	 * 	获取填报单位号码
	 * @param parentCode
	 * @return
	 * @throws GeneralException
	 */
	public  int getGrade(String parentCode) throws GeneralException{
		int num=1;
		StringBuffer sql = new StringBuffer();
		sql.append("select grade from tt_organization where unitcode = '");
		sql.append(parentCode);
		sql.append("'");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				num = this.frowset.getInt("grade")+1;
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	

		return num;
		
	}
}
