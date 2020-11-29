/*
 * Created on 2006-5-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchAllPersonListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbname=(String)this.getFormHM().get("dbname");
		String inforkind=(String)this.getFormHM().get("inforkind");
		StringBuffer sql=new StringBuffer();
		ArrayList personlist=new ArrayList();
		DbWizard db=new DbWizard(this.frameconn);
		try{
		 if(userView.getStatus()!=4){
			if("1".equals(inforkind)){
				if(dbname==null||dbname.length()<=0)
					throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择人员库,请选择!","",""));	
				
				if(db.isExistTable(this.userView.getUserName()+dbname+"Result", false)){//有结果集临时表 changxy 25268
					sql.append("select ");
					sql.append(dbname);
					sql.append("A01.a0100,");
					sql.append(dbname);
					sql.append("A01.a0101 from ");
					sql.append(this.userView.getUserName());
					sql.append(dbname);
					sql.append("Result,");
					sql.append(dbname);
					sql.append("A01 where ");
					sql.append(this.userView.getUserName());
					sql.append(dbname);
					sql.append("Result.a0100=");
					sql.append(dbname);
					sql.append("A01.a0100 ");
					
					if(!this.userView.isSuper_admin()){
						sql.append(" and "+this.userView.getUserName()+dbname+"Result");
						sql.append(".a0100 in (select A0100 ");
						sql.append(	this.userView.getPrivSQLExpression(dbname, false));//业务用户 人员权限修改后临时表对应人员范围未修改，现关联人员权限查询临时表数据
						sql.append(")");
					}
					sql.append(" order by  ");
					sql.append(dbname);
					sql.append("a01.a0000");
				}
			}else if("2".equals(inforkind)) {
				if(db.isExistTable(this.userView.getUserName()+"BResult", false)){
					sql.append("select ");
					sql.append(this.userView.getUserName());
					sql.append("BResult.b0110,organization.codeitemdesc from ");
					sql.append(this.userView.getUserName());
					sql.append("BResult,organization where organization.codeitemid=");
					sql.append(this.userView.getUserName());
					sql.append("BResult.b0110 order by b0110");
				}
	        }    	
		    else if("4".equals(inforkind))
		    {
		    	if(db.isExistTable(this.userView.getUserName()+"KResult", false)){
		    		sql.append("select ");
		    		sql.append(this.userView.getUserName());
		    		sql.append("KResult.E01A1,organization.codeitemdesc from ");
		    		sql.append(this.userView.getUserName());
		    		sql.append("KResult,organization where organization.codeitemid=");
		    		sql.append(this.userView.getUserName());
		    		sql.append("KResult.E01A1");       
		    	}
		    }else if("5".equals(inforkind))
		    {
		    	String plan_id=(String)this.getFormHM().get("plan_id");
		    	sql.append("select object_id,a0101 from per_result_"+plan_id);
		    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
		    	String where=bo.getPrivWhere(this.userView);		    	
		    	if(where!=null&&where.length()>0)
		    		sql.append(" where 1=1 "+where);
		    	sql.append(" order by a0000");
		    }
			
		  }else
		  {
			  if("1".equals(inforkind)){
					if(dbname==null||dbname.length()<=0)
						throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择人员库,请选择!","",""));	
					sql.append("select ");
	            	sql.append(dbname);
	            	sql.append("A01.a0100,");
	            	sql.append(dbname);
	            	sql.append("A01.a0101 from t_sys_result ");            	
	            	sql.append(",");
	            	sql.append(dbname);
	            	sql.append("A01 where UPPER(t_sys_result.nbase)='"+dbname.toUpperCase()+"' and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' ");            	
	            	sql.append("and t_sys_result.flag=0 and t_sys_result.obj_id=");
	            	sql.append(dbname);
	            	sql.append("A01.a0100 ");
	            	sql.append(" AND ");
	            	sql.append(dbname);
	            	sql.append("A01.a0100 in (select A0100 ");
	            	sql.append(	this.userView.getPrivSQLExpression(dbname, false));//自助用户 人员权限修改后临时表未修改，现关联人员权限查询临时表数据
	            	sql.append(")");
	            	sql.append(" order by  ");
	            	sql.append(dbname);
	            	sql.append("a01.a0000");
			  }else if("2".equals(inforkind)) {
				  sql.append("select t_sys_result.obj_id as b0110,organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=1  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' ");
		        }    	
			    else if("4".equals(inforkind))
			    {
			    	sql.append("select t_sys_result.obj_id as e01a1,organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=2  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' ");     
			    }else if("5".equals(inforkind))
			  {
			    	String plan_id=(String)this.getFormHM().get("plan_id");
			    	sql.append("select object_id,a0101 from per_result_"+plan_id);
			    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
			    	String where=bo.getPrivWhere(this.userView);		    	
			    	if(where!=null&&where.length()>0)
			    		sql.append(" where 1=1 "+where);
			    	sql.append(" order by a0000");
			  }
		  }
		  if("6".equals(inforkind))  // 基准岗位
		  {
		    	String codeset=new CardConstantSet().getStdPosCodeSetId();
	    		sql.append("select t_sys_result.obj_id as h0100,codeitemdesc from t_sys_result,CodeItem "+
	    				   " where CodeItem.codeitemid=t_sys_result.obj_id and codesetid='"+codeset+"'"+
	    				        " and t_sys_result.flag=5"+// 基准岗位
	    				        " and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
		  }

		  if(sql!=null&&sql.length()>0)
		  {
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  this.frowset=dao.search(sql.toString());
			  int num=0;
			  if("1".equals(inforkind))
		      {
				  if(this.frowset.next()){
					 this.frowset.previous();
		     	   while(this.frowset.next())
		     	  {
		     		  CommonData dataobj = new CommonData(this.frowset.getString("a0100"),this.frowset.getString("a0101")!=null&&this.frowset.getString("a0101").length()>0?this.frowset.getString("a0101"):"  ");
		     		  personlist.add(dataobj);
		     		  num++;
		     		  if(num>=500)
		     			  break;
		     	  } 
				  }else{
		     		  if(this.userView.getStatus()==4){
		     			CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
		     			String a0100 = cps.checkA0100("",dbname, this.userView.getA0100(), "");
		 	        	if(a0100==null||"".equals(a0100)) {//自助用户对于无人员范围权限的 只查自己
		 	        		a0100=this.userView.getA0100();
		 	        		
		 	        	}
		 	        	//越权时校验人员库和A0100与userview的人员库A0100是否相同 相同时查看自己 否则显示为空
		 	        	if(dbname.equalsIgnoreCase(this.userView.getDbname())&&a0100.equals(this.userView.getA0100())) {
		 	        		CommonData dataobj = new CommonData(a0100,getA0101(a0100,dbname));
		 	        		personlist.add(dataobj);
		 	        	}
		     		  } 
				  }
		     	   
		      }else if("2".equals(inforkind)) {
		         	 while(this.frowset.next())
		        	  {
		        		 CommonData dataobj = new CommonData(this.frowset.getString("b0110"),this.frowset.getString("codeitemdesc"));
		        		 personlist.add(dataobj);
		        		 num++;
			     		  if(num>=500)
			     			  break;
		        	  } 
		      }else if("4".equals(inforkind)){
		    	  String uplevel ="0";
				    if ("4".equals(inforkind)){
				    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
				    	uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
				    }
				    if(uplevel==null||uplevel.length()==0)
			    		uplevel="0";
		 	      while(this.frowset.next())
		          {
		 	    	 String codedesc=this.frowset.getString("codeitemdesc");
						String itemid=this.frowset.getString("e01a1");
						if ("4".equals(inforkind)){
							if(com.hrms.frame.utility.AdminCode.getCode("@K", itemid, Integer.parseInt(uplevel))!=null)
								codedesc=com.hrms.frame.utility.AdminCode.getCode("@K", itemid, Integer.parseInt(uplevel)).getCodename();
						}
			           CommonData dataobj = new CommonData(itemid,codedesc);
		        	personlist.add(dataobj);
		        	 num++;
		     		  if(num>=500)
		     			  break;
		          } 
		 	  }else if("5".equals(inforkind)){
			 	      while(this.frowset.next())
			          {
			            CommonData dataobj = new CommonData(this.frowset.getString("object_id"),this.frowset.getString("a0101"));
			        	personlist.add(dataobj);
			        	 num++;
			     		  if(num>=500)
			     			  break;
			          } 
			  }else {
		 	      while(this.frowset.next())
		          {
		            CommonData dataobj = new CommonData(this.frowset.getString(1),this.frowset.getString(2));
		        	personlist.add(dataobj);
		        	 num++;
		     		  if(num>=500)
		     			  break;
		          } 
			  }
		  }
		  
		 this.getFormHM().put("personlist",personlist);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(this.frameconn);
		}

	}
	//员工管理登记表员工自助进入时权限设置其他单位部门权限 无自己部门权限时，登记表内容默认显示权限范围内第一条记录 姓名下拉列表也应对应
	public String getA0101(String a0100,String dbname){
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		String name="";
		try {
			rs=dao.search("select A0101 from "+dbname+"A01 where a0100="+a0100);
			if(rs.next()){
				name=rs.getString("A0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

}
