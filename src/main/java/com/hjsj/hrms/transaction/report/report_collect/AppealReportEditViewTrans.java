package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TableAnalyse;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AppealReportEditViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String operateObject="2";
			String unitcode="";
			String user=this.userView.getUserName();
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			String _username=SafeCode.decode((String) hm.get("username"));
//			String _tabid=(String) hm.get("code");
//			【59568】VFS+UTF-8报表管理：下级报批的报表，审核人去审批，点击“编辑报表参数”，返回，审核中没有了驳回，登录用户“ch一般”
//			hm.remove("username");
			hm.remove("code");
			if(_username!=null&&!"".equals(_username)){
				userView = new UserView(_username, this.frameconn);
				userView.canLogin();
			}
			RecordVo a_selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			if(a_selfVo==null){
				throw new Exception(ResourceFactory.getProperty("edit_report.info11")+"!");
			}
			if(hm.get("a_code")!=null)
			{
				unitcode=(String)hm.get("a_code");		
			}
			else
			{				
					unitcode=a_selfVo.getString("unitcode");
			}
			String usrID=this.getUserView().getUserId();
			String tabid=(String)this.getFormHM().get("tabid");	
//			if(_tabid!=null&&!_tabid.equals("")){
//				tabid=_tabid;
//			}
			if(hm.get("tabid")!=null)
			{
				tabid=(String)hm.get("tabid");
			}
			else {
				tabid = (String)this.getFormHM().get("tabid");
				if(tabid==null|| "".equals(tabid)|| "0".equals(tabid))
				return;
			}
			
			//---------------------报表上报是否支持审批  zhaoxg 2013-1-26 -------------------
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String username = "";
			String sql = "select * from treport_ctrl where currappuser = '"+this.userView.getUserName()+"' and status = '4' and tabid = "+tabid+" and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				username = rs.getString("username");
			}
			if(username==null|| "".equals(username)){
				username = this.userView.getUserName();
			}
			this.getFormHM().put("username", username);
			this.getFormHM().put("username1", username);
			String  status=getStatus(unitcode,tabid);
			this.getFormHM().put("status", status);
			//Report_isApproveBo bo = new Report_isApproveBo();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String isApprove = sysbo.getValue(Sys_Oth_Parameter.ISAPPROVE);
			String relation_id = sysbo.getValue(Sys_Oth_Parameter.APPROVEID);
			String isApproveflag = "";
			boolean isUpapprove = isUpapprove(username,relation_id,user); 
			if("true".equals(isApprove)&&("".equals(relation_id)||relation_id==null)){
				isApproveflag = "1";//显示上报按钮
			}else if("true".equals(isApprove)&&!"".equals(relation_id)&&relation_id!=null){
				if(isMainbody(relation_id,username)&&isApp(tabid,unitcode,user)&&!isUpapprove&&!"1".equals(status)&&!"3".equals(status)){
					isApproveflag = "2";
				}else if(!isApp(tabid,unitcode,user)&&!isUpapprove&&("-1".equals(status)|| "1".equals(status)|| "3".equals(status)|| "4".equals(status))){
					isApproveflag = "5";
				}else{
					isApproveflag = "3";//显示批准按钮
				}
				
			}else{
				isApproveflag = "4";
			}
			boolean isShenpi = isShenpi(tabid,unitcode,user);
			if(isShenpi){
				if(isUpapprove){
					this.getFormHM().put("isUpapprove", "true");
					isApproveflag = "5";
				}else{
					this.getFormHM().put("isUpapprove", "false");
				}
			}else{
				this.getFormHM().put("isUpapprove", "");
			}

			this.getFormHM().put("isApproveflag", isApproveflag);
			//----------------------------------------------------------------------------
			/////////////////////////
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			
			if(unitcode.equals(a_selfVo.getString("unitcode")))
			{
				this.getFormHM().put("freeze","false");
				this.getFormHM().put("pigeonhole","true");
				this.getFormHM().put("appeal2","true");
			}
			else
			{
				this.getFormHM().put("appeal2","false");
				this.getFormHM().put("freeze","true");
				this.getFormHM().put("pigeonhole","false");
			
			}

			//	取得填报单位信息
			RecordVo selfVo=tt_organization.getSelfUnit2(unitcode);		
			
			
			ArrayList tabList=new ArrayList();	
			boolean isNoRight=isNoRight(selfVo,tabid);
			String[] tt=getReportName_unitName(unitcode,tabid);
			if(isNoRight)
			{
				this.getFormHM().put("unitcode",unitcode);
				this.getFormHM().put("unitcode1",unitcode);
				this.getFormHM().put("selfUnitcode",a_selfVo.getString("unitcode"));
				
				/*ArrayList tableList=tnameExtendBo.getTableNameList(selfVo.getString("reporttypes").substring(0,selfVo.getString("reporttypes").lastIndexOf(",")),this.getUserView());
				boolean isExistTabid=false;
				for(Iterator t=tableList.iterator();t.hasNext();)
				{
					DynaBean bean=(DynaBean)t.next();
					if(((String)bean.get("tabid")).equals(tabid))
						isExistTabid=true;
					CommonData vo=new CommonData((String)bean.get("tabid"),(String)bean.get("tabid")+":"+(String)bean.get("name"));
					tabList.add(vo);
				}
				if(tabid.equals("0")||!isExistTabid)
					tabid=getFirstTable(tableList);*/
	
				TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid,usrID,this.getUserView().getUserName(),"view");
			
				TableAnalyse tableAnalyse=new TableAnalyse(this.getFrameconn(),2,Integer.parseInt(tabid),tnameBo);
				//ContentDAO dao=new ContentDAO(this.getFrameconn());
				RowSet recset=dao.search("select tsortid from tname where tabid="+tabid);
				String sortid="-1";
				if(recset.next())
					sortid=recset.getString("tsortid");
				tableAnalyse.analyseTable(sortid);
				//String  status=getStatus(unitcode,tabid);
				//同步该表页面显示的参数，只同步参数长度变大，只修改字符和数值型
				 HashMap param_map=tnameBo.getParamMap();
				 if(param_map!=null){
						java.util.Iterator it = param_map.entrySet().iterator();
						ResultSetMetaData   rsmd =null;
						while (it.hasNext()) {
							Map.Entry entry = (Map.Entry) it.next();
							String keys = (String) entry.getKey();
							HashMap map_values = (HashMap)entry.getValue();
							
							 if(map_values!=null&&map_values.get("paramtype")!=null)
							 {
								 if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))||((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts")))
								 {
									 if(map_values.get("paramscope")!=null&&map_values.get("paramename")!=null&&map_values.get("paramlen")!=null){
										 //需要同步的表全局参数tt_p表类参数"tt_s"+tsortid表内参数"tt_t"+tabid
										 int paramscope = Integer.parseInt(""+map_values.get("paramscope"));
										 switch(paramscope){
										 case  0:{
											 Table table=new Table("tt_p");
											 Field field=null;
												if(dbWizard.isExistTable("tt_p",false)){ 
													if(!dbWizard.isExistField("tt_p",""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}
														 dbWizard.addColumns(table);
													}else{
														this.frowset=dao.search(" select "+map_values.get("paramename")+" from tt_p ");
														   rsmd = this.frowset.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}
										 break;
										 }
										 case  1:{
											 Table table=new Table("tt_s"+sortid);
											 Field field=null;
												if(dbWizard.isExistTable("tt_s"+sortid,false)){ 
													if(!dbWizard.isExistField("tt_s"+sortid,""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}
														 dbWizard.addColumns(table);
													}else{
														this.frowset=dao.search(" select "+map_values.get("paramename")+" from tt_s"+sortid);
														   rsmd = this.frowset.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}
											 break;
											 }
										 case  2:{
											 Table table=new Table("tt_t"+tabid);
											 Field field=null;
												if(dbWizard.isExistTable("tt_t"+tabid,false)){ 
													if(!dbWizard.isExistField("tt_t"+tabid,""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}
														 dbWizard.addColumns(table);
													}else{
														this.frowset=dao.search(" select "+map_values.get("paramename")+" from tt_t"+tabid);
														   rsmd = this.frowset.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}
											 break;
											 }
										 }
									 }
								 }
								
							 }
						
							}
							
						}
			
				String       htmlCode=tnameBo.getReportHtml(status,this.userView.getUserName(),operateObject,unitcode,a_selfVo.getString("unitcode"));
				
				
				String rows=String.valueOf(tnameBo.getColInfoBGrid().size());
				String cols=String.valueOf(tnameBo.getRowInfoBGrid().size());
				ArrayList paramnameList=tnameBo.getParamenameList();
				StringBuffer param_str=new StringBuffer("");
				for(Iterator t=paramnameList.iterator();t.hasNext();)
				{
					param_str.append("/"+(String)t.next());
				}
				
				this.getFormHM().put("reportName",tt[0]);
				this.getFormHM().put("unitName",tt[1]);
				this.getFormHM().put("narch",String.valueOf(tnameBo.getTnameVo().getInt("narch")));  //报表类型
				this.getFormHM().put("htmlCode",htmlCode);
				this.getFormHM().put("tabid",tabid);
				this.getFormHM().put("rows",rows);
				this.getFormHM().put("cols",cols);
				this.getFormHM().put("param_str",param_str.toString().length()>0?param_str.substring(1):param_str.toString());
				this.getFormHM().put("status",status);
				this.getFormHM().put("operateObject",operateObject);
				this.getFormHM().put("tabList",tabList);
				this.getFormHM().put("isPopedom","1");
				this.getFormHM().put("isCollectCheck","1");
				
			}
			else
			{

				this.getFormHM().put("tabid",tabid);
				this.getFormHM().put("rows","0");
				this.getFormHM().put("cols","0");
				
				this.getFormHM().put("isPopedom","0");		
				//this.getFormHM().put("htmlCode",ResourceFactory.getProperty("report_collect.unitNoRight")+"！");
				this.getFormHM().put("htmlCode",ResourceFactory.getProperty("report_collect.CurrentUnit")+":"+tt[1]+" 对 "+tt[0]+" "+ResourceFactory.getProperty("report_collect.info5")+"!");
				
				this.getFormHM().put("tabList",new ArrayList());
			}
			
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			this.getFormHM().put("returnflag", dxt);
			
			String selfstatus=this.getStatus(a_selfVo.getString("unitcode"), tabid);
			this.getFormHM().put("selfstatus", selfstatus);
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	
	
	
	public String[] getReportName_unitName(String unitid,String tabid)
	{
		String[] temp=new String[2];
		String sql="select name,unitname from tname,tt_organization where tabid="+tabid+" and tt_organization.unitcode ='"+unitid+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				temp[0]=this.frowset.getString("name");
				temp[1]=this.frowset.getString("unitname");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	
	
	
	/**
	 * 判断 该组织是否对表具有权限
	 * @param selfVo
	 * @param tabid
	 * @return
	 */
	public boolean isNoRight(RecordVo selfVo,String tabid)
	{
		boolean isRight=false;
		String report=selfVo.getString("report");//dml
		if(this.userView.isHaveResource(IResourceConstant.REPORT,tabid))
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search("select tsortid from tname where tabid="+tabid);
				if(this.frowset.next())
				{
					String tsortid=this.frowset.getString("tsortid");
					if(selfVo.getString("reporttypes")!=null)
					{
						if((","+selfVo.getString("reporttypes")).indexOf(","+tsortid+",")!=-1)//dml 2011-03-29
							isRight=true;
					}
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(isRight&&report.indexOf(","+tabid+",")==-1)//dml 2011-03-29
			isRight=true;
		else 
			isRight=false;
		
		
		return isRight;
	}
	
	public String getFirstTable(ArrayList list)
	{
			String tabid="0";
			if(list.size()>0)
			{
				DynaBean bean=(DynaBean)list.get(0);
				tabid=(String)bean.get("tabid");
			}
			return tabid;
	}

	public String getStatus(String unitcode,String tabid)
	{
		String status="-1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			this.frowset=dao.search("select * from treport_ctrl where tabid="+tabid+" and unitcode='"+unitcode+"'");
			if(this.frowset.next())
				status=this.frowset.getString("status");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}
	/**
	 * 判断审批关系中是否定义用户的审批主体 zhaoxg 2013-1-26
	 * @param relation_id 审批关系id
	 * @return
	 */
	public boolean isMainbody(String relation_id,String username){
		boolean isMainbody = false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String sql = "select * from t_wf_mainbody where relation_id = "+relation_id+"";
			RowSet rs = dao.search(sql);
			while(rs.next()){
				if(rs.getString("object_id").equals(username)){
					isMainbody = true;
				}
				
			}
			rs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return isMainbody;
	}
	/**
	 * 判断当前用户是不是顶级审批人
	 */
	public boolean isUpapprove(String username,String relation_id,String user){
		boolean isUpapprove = false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String sp_grade = "";
			String max = "";
			String sql = "select sp_grade from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+username+"' and mainbody_id = '"+user+"'";
			String sqll = "select max(sp_grade) as max from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+username+"'";
			RowSet rs = dao.search(sql);
			RowSet rowset = dao.search(sqll);
			if(rs.next()){
				sp_grade = rs.getString("sp_grade");
			}
			if(rowset.next()){
				max = rowset.getString("max");
				if(max!=null&&!"".equals(sp_grade)){
					if(max.equals(sp_grade)){
						isUpapprove = true;
					}
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return isUpapprove;
	}
	/**
	 * 当前登录用户有没有上报权限
	 */
	public boolean isApp(String tabid,String unitcode,String username){
		boolean isapp = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(username.equals(user)){
					isapp = true;
				}
				if(user==null|| "".equals(user)){
					if(username.equals(this.userView.getUserName())){
						isapp = true;
					}else{
						isapp = false;
					}
				}
				if("1".equals(status)){
					isapp = false;
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return isapp;
	}
	/**
	 * 当前登录用户是否有审批和驳回权限
	 * @param tabid
	 * @param unitcode
	 * @return
	 */
	public boolean isShenpi(String tabid,String unitcode,String username){
		boolean isShenpi = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(username.equals(user)&& "4".equals(status)){
					isShenpi = true;
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return isShenpi;
	}
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve() throws GeneralException, SQLException{
		String approve = "";

		ResultSet rs = null;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		try{
			String sql = "select appuser,username from treport_ctrl";
			rs = dao.search(sql.toString());
			while(rs.next()){
				String appuser = rs.getString("appuser");
				if(appuser!=null){
					String[] aa = appuser.split(";");
					for(int i=0;i<aa.length;i++){
						if(aa[i].equals(this.userView.getUserFullName())){
							approve = rs.getString("username");
						}
					}
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return approve;
	}
}
	

