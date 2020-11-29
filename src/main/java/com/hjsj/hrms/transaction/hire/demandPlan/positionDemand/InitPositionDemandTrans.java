package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * 
 * <p>Title:InitPositionDemandTrans.java</p>
 * <p>Description:初始化用工需求接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 13, 2006 1:29:31 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class InitPositionDemandTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag="";
			if(hm.get("returnflag")!=null)
			{
				returnflag=(String)hm.get("returnflag");
			}
			else
			{
				returnflag=(String)this.getFormHM().get("reutrnflag");
			}
			this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
			String sp = (String)this.getFormHM().get("sp");
			String operateType="user";
			if(hm.get("operateType")!=null)
			{
				operateType=(String)hm.get("operateType");
				hm.remove("operateType");
				this.getFormHM().put("operateType",operateType);
			}
			else
			{
				operateType=(String)this.getFormHM().get("operateType");
			}
			
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			String moreLevelSP="0";
			if(map!=null&&map.get("moreLevelSP")!=null)
				moreLevelSP=(String)map.get("moreLevelSP");
			
			String hireMajor="";
			if(map.get("hireMajor")!=null&&((String)map.get("hireMajor")).length()>0)
				hireMajor=(String)map.get("hireMajor");
			this.getFormHM().put("hireMajor", hireMajor);
			RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
			
			if(vo==null|| "#".equals(vo.getString("str_value")))
			{
					throw GeneralExceptionHandler.Handle(new Exception("请到系统管理--通讯平台--电话邮箱设置指定电子邮箱！"));
			}
			
			String spRelation="";
			String[] zpspperson = null; 
			String spcount="0";
			String actortype="";
			if(map!=null&&map.get("spRelation")!=null)
				spRelation=(String)map.get("spRelation");
			
			if(spRelation!=null&&spRelation.length()>0){
				zpspperson = getSpcount(spRelation).split(":");
				spcount = zpspperson[0];
				actortype = zpspperson[1];
				if("1".equalsIgnoreCase(spcount)){
					this.getFormHM().put("actortype", actortype);
					this.getFormHM().put("appname", zpspperson[2]);
				}
			}
			
			String b_query=(String)hm.get("b_query");
			String b_query2=(String)hm.get("b_query2");
			String b_query3=(String)hm.get("b_query3");
			String query_desc="";
			if(b_query!=null)
				query_desc=b_query;
			if(b_query2!=null)
				query_desc=b_query2;
			if(b_query3!=null)
				query_desc=b_query3;
			String isSendMessage=(String)map.get("sms_notice");
			String codesetid=(String)hm.get("codeset");	
			String codeid=(String)hm.get("code");
			String model=(String)hm.get("model");					//1:用工需求模块  2：需求审核模块 3：审核查询模块
			String splistCtrl=query_desc;
			if(model==null){
				model="";
			}
			if("1".equals(query_desc)|| "2".equals(query_desc)|| "3".equals(query_desc))
				codeid="";
			/**自助平台的用户也可以用招聘*/
		/*	if(!operateType.equals("employ")&&(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().trim().length()<=2))
			{
				if(!(this.getUserView().isSuper_admin()))
					 throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
			}*/
			
			if(codeid!=null&&("-1".equals(codeid)|| "null".equals(codeid)))
				codeid="";
			if(!"link".equals(query_desc)){
				model=query_desc;	
				if(!this.userView.isSuper_admin())
				{
					/**业务用户*/
//					if(this.userView.getStatus()==0/*operateType.equals("user")*/)
//					{
//						codeid=this.getUserView().getUnit_id();
//						if(codeid==null||codeid.trim().length()==0||codeid.equalsIgnoreCase("UN"))
//						{
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//						}else if(codeid.trim().length()==3)
//						{
//							codeid="";
//						}
//						else if(codeid.indexOf("`")==-1)
//		            		codeid=codeid.substring(2);
//					}
//					else
//					{
//						codeid=this.getUserView().getManagePrivCodeValue();
//						codesetid=this.getUserView().getManagePrivCode();
//						if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//						{
//							/*String userDeptId=userView.getUserDeptId();
//							String userOrgId=userView.getUserOrgId();
//							if(userDeptId!=null&&!userDeptId.equalsIgnoreCase("null")&&userDeptId.trim().length()>0)
//							{
//								codeid=userDeptId;							
//							}
//							else if(userOrgId!=null&&userOrgId.trim().length()>0)
//							{
//								codeid=userOrgId;
//							}*/
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//						}
//					}
					codeid=this.userView.getUnitIdByBusi("7");
					String info=bo2.hasSetParam(this.userView);
					if(info!=null&&info.trim().length()>0){
						throw GeneralExceptionHandler.Handle(new Exception(info));
					}
				}
				query_desc="link";
				
			}
			else if("link".equals(query_desc)){
				if(!this.userView.isSuper_admin()&&(codeid==null||codeid.trim().length()<1))
				{					
//					if(this.userView.getStatus()==0/*operateType.equals("user")*/)
//					{
//						codeid=this.getUserView().getUnit_id();
//						if(codeid==null||codeid.trim().length()==0)
//						{
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//						}else if(codeid.trim().length()==3)
//						{
//							codeid="";
//						}
//						else if(codeid.indexOf("`")==-1)
//		            		codeid=codeid.substring(2);
//					}
//					else
//					{
//						codeid=this.getUserView().getManagePrivCodeValue();
//						codesetid=this.getUserView().getManagePrivCode();
///*						if(codeid==null||codeid.trim().length()==0)
//						{*/
//						if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//						{
//							/*String userDeptId=userView.getUserDeptId();
//							String userOrgId=userView.getUserOrgId();
//							if(userDeptId!=null&&!userDeptId.equalsIgnoreCase("null")&&userDeptId.trim().length()>0)
//							{
//								codeid=userDeptId;							
//							}
//							else if(userOrgId!=null&&userOrgId.trim().length()>0)
//							{
//								codeid=userOrgId;
//							}
//							else
//							{*/
//							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//							/*}*/
//						}
//						
//					}
					codeid=this.userView.getUnitIdByBusi("7");
					String info=bo2.hasSetParam(this.userView);
					if(info!=null&&info.trim().length()>0){
						throw GeneralExceptionHandler.Handle(new Exception(info));
					}
				}
			}
			
			PositionDemand positionDemand=new PositionDemand(this.getFrameconn());//zzk
			positionDemand.addCurrappusername("");//z03增加列currappusername  记录当前操作人员zzk
		/*	if(!this.userView.isSuper_admin()&&operateType.equals("employ")&&(codeid==null||codeid.trim().length()==0))
				 throw GeneralExceptionHandler.Handle(new Exception("您没有设置管理范围"));*/
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			String isstr=(String) hm.get("isstr");
			if(isstr!=null&&isstr.trim().length()>0){
				isstr=" z0319='"+isstr+"'";
			}
			hm.remove("isstr");
			String extendSql=(String)this.getFormHM().get("extendSql");
			if(extendSql!=null&&extendSql.trim().length()>0)
				extendSql=PubFunc.keyWord_reback(PubFunc.decrypt(extendSql));
			if(isstr!=null&&isstr.trim().length()>0){
				extendSql=isstr;
			}
			
	        if (extendSql != null && !"".equals(extendSql))
	            extendSql = "(" + extendSql + ")";
	         
			String orderSql=(String)this.getFormHM().get("orderSql");
			if(orderSql!=null&&orderSql.trim().length()>0)
				orderSql=PubFunc.keyWord_reback(PubFunc.decrypt(orderSql));
			String tablename="Z03";
			StringBuffer whl_sql=new StringBuffer(" where ");
			StringBuffer strsql=new StringBuffer("select z03.z0301 z0301a,z03.z0319 z0319a,z03.z0301 ctrlparama,z03.z0311 z0311a,z03.z0321 as z0321a,z03.z0325 as z0325a,z03.currappusername as currappusername,z03.currappuser, ");
			StringBuffer sss=new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				String temp="z03."+item.getItemid();
				String temp2="";
		//		if(temp.equalsIgnoreCase("z03.z0307"))
		//			temp2=Sql_switcher.numberToChar(Sql_switcher.year(temp))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(temp))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(temp))+" "+item.getItemid();
		//		else
					temp2=temp;
				sss.append(","+temp2);
			}
			strsql.append(sss.substring(1));
			strsql.append(" from Z03 left join z01 on z03.z0101=z01.z0101  where ");
			
			//所属单位 或 部门 在组织范围内的
			if("link".equals(query_desc)&&codeid!=null&&codeid.trim().length()>0)
			{
			       if(codeid.indexOf("`")==-1)                 // 	if(codeid!=null&&codeid.trim().length()>0)
					{
						//strsql.append(" ( Z0311 like '"+codeid+"%'  or ( ( z0303 is not null and z0303 like '"+codeid+"%') or ( z0305 is not null and z0305 like '"+codeid+"%')    ) )  "); //                   Z0311 like '"+codeid+"%' ");	
						//whl_sql.append(" ( Z0311 like '"+codeid+"%'  or ( ( z0303 is not null and z0303 like '"+codeid+"%') or ( z0305 is not null and z0305 like '"+codeid+"%')    ) ) "); //                   Z0311 like '"+codeid+"%' ");
			
			    	   
			//          strsql.append("  Z0311 like '"+codeid+"%' ");
			//    	   	whl_sql.append(" Z0311 like '"+codeid+"%' ");
			    	   	String _str=Sql_switcher.isnull("Z0336","''");
			    	    strsql.append(" ( ( Z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( Z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( Z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) ");
					    whl_sql.append(" ( ( Z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( Z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( Z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) ");
			    	   	
					}
					else
					{
						StringBuffer tempsql=new StringBuffer("");
						StringBuffer tempsql2=new StringBuffer("");
						StringBuffer tempsql3=new StringBuffer("");
					 	String _str=Sql_switcher.isnull("Z0336","''");
						String[] temp=codeid.split("`");
						for(int i=0;i<temp.length;i++)
						{
							tempsql.append(" or Z0311 like '"+temp[i].substring(2)+"%'");
							tempsql2.append(" or Z0321 like '"+temp[i].substring(2)+"%'");
							tempsql3.append(" or Z0325 like '"+temp[i].substring(2)+"%'");
						}
					//	strsql.append(" ( "+tempsql.substring(3)+" ) ");
					//	whl_sql.append(" ( "+tempsql.substring(3)+" ) ");
						strsql.append(" ( ( ( "+tempsql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempsql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempsql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
					   whl_sql.append(" ( ( ( "+tempsql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempsql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempsql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				    	   	
						
						/*
						strsql.append(" 1=1");	
						whl_sql.append(" 1=1"); */
					}
				 //   model=query_desc;
			}
			else 
			{
				strsql.append(" 1=1");	
				whl_sql.append(" 1=1");
				if(!"link".equals(query_desc))
					model=query_desc;
			}
				
			
			if("2".equals(model))
			{
				strsql.append(" and z03.z0319<>'01' ");
				whl_sql.append(" and z03.z0319<>'01' ");
				if(!this.getUserView().isSuper_admin())
				{
	    			/**需要多级审批，没有招聘主管权限的，只看到报批或驳回给自己的*/
		    		if("1".equals(moreLevelSP)&&!this.getUserView().haveTheRoleProperty("8"))
		    		{
		    			String nbase=this.getUserView().getDbname();
		  	    		String a0100=this.getUserView().getA0100();
		  	    		if(a0100==null||a0100.trim().length()==0){
		  	    			nbase="#_`";
		  	    			a0100="#_`";
		  	    		}
		    			strsql.append(" and ((( z03.z0319='02' or z03.z0319='07' ) and ( z03.currappuser='"+this.getUserView().getUserName()+"' ");
		     			whl_sql.append(" and ((( z03.z0319='02' or z03.z0319='07' )  and ( z03.currappuser='"+this.getUserView().getUserName()+"' ");
			    		if(this.getUserView().getDbname()!=null&&!"".equals(this.getUserView().getDbname())&&this.getUserView().getA0100()!=null&&!"".equals(this.getUserView().getA0100()))
		    			{
			    			strsql.append(" or UPPER(z03.currappuser)='"+this.getUserView().getDbname().toUpperCase()+this.getUserView().getA0100().toUpperCase()+"' ");
			    			whl_sql.append(" or UPPER(z03.currappuser)='"+this.getUserView().getDbname().toUpperCase()+this.getUserView().getA0100().toUpperCase()+"' ");
			    		}
		  	    		strsql.append(" ) ) ");
		  	    		
		  	    			
		  	    		strsql.append(" or (z03.z0319 in('03','04','06','09') and ( ','"+Sql_switcher.concat()+"UPPER(z03.appuser)"+Sql_switcher.concat()+"',' like '%,"+nbase.toUpperCase()+a0100.toUpperCase()+",%' " +
		  	    				" or  ','"+Sql_switcher.concat()+"UPPER(z03.appuser)"+Sql_switcher.concat()+"',' like '%,"+this.getUserView().getUserName().toUpperCase()+",%'   )) )");///zzk 凡是参与过招聘审批流程的的人，已批的需求都可以看到
			    		whl_sql.append(") ) ");
			    		whl_sql.append(" or (z03.z0319 in('03','04','06','09') and ( ','"+Sql_switcher.concat()+"UPPER(z03.appuser)"+Sql_switcher.concat()+"',' like '%,"+nbase.toUpperCase()+a0100.toUpperCase()+",%' " +
		  	    				" or  ','"+Sql_switcher.concat()+"UPPER(z03.appuser)"+Sql_switcher.concat()+"',' like '%,"+this.getUserView().getUserName().toUpperCase()+",%'   )) )");///zzk 凡是参与过招聘审批流程的的人，已批的需求都可以看到
	    			}
				}
				
			}
			
			strsql.append("   and (z03.z0101 is null or z03.z0101='' or z01.z0129<>'06') ");
			whl_sql.append("   and (z03.z0101 is null or z03.z0101='' or z01.z0129<>'06') ");
				
			
			
			
			
			if(extendSql!=null&&extendSql.trim().length()>3)
			{
				strsql.append(" and "+extendSql);
				whl_sql.append(" and "+extendSql);
			}
			if(orderSql!=null&&orderSql.trim().length()>3)
			{
				strsql.append(" "+orderSql);
				whl_sql.append(" "+orderSql);
			}
			else
			{
				strsql.append(" order by Z0301 desc");
				whl_sql.append(" order by Z0301 desc");
			}
			
			ArrayList fieldlist=new ArrayList();	
		 
			String a_z0303="";
			String a_z0305="";
			GregorianCalendar d = new GregorianCalendar();
			String a_creatData=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE); //创建日期
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(!this.getUserView().isSuper_admin())
			{
				String dbname=this.getUserView().getDbname().trim();
				if(dbname!=null&&!"".equals(dbname))
				{
					this.frowset=dao.search("select * from "+dbname+"A01 where a0100='"+this.getUserView().getUserId()+"'");
					if(this.frowset.next())
					{
						a_z0303=this.frowset.getString("b0110");
						a_z0305=this.frowset.getString("e0122");
					}
				}
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			 String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			 if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				 display_e0122="0";
			 int level=Integer.parseInt(display_e0122);
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				Field field=(Field)item.cloneField();
				if("0".equals(item.getState()))
					field.setVisible(false);
				/*if(model.equals("2")&&item.getItemid().equalsIgnoreCase("STATE"))
					field.setVisible(true);*/

				if("Z0319".equalsIgnoreCase(item.getItemid()))  //审批状态
				{
					field.setReadonly(true);  //此字段为只读状态
					//设置默认值（等待新程序）
					if(model!=null&& "2".equals(model))
					{
						field.setValue("02");
					}
					else
						field.setValue("01");
					
				}
				if("Z0303".equalsIgnoreCase(item.getItemid())&&!this.getUserView().isAdmin()) //所属单位
				{
				//	field.setReadonly(true);  //此字段为只读状态					
				//	field.setValue(a_z0303);
				}
				if("Z0305".equalsIgnoreCase(item.getItemid())&&!this.getUserView().isAdmin()) //所属部门
				{
				//	field.setReadonly(true);  //此字段为只读状态
				//	field.setValue(a_z0305);
				}
				if("Z0307".equalsIgnoreCase(item.getItemid())) //创建时间
				{
					field.setReadonly(true);  //此字段为只读状态
					//设置默认值（等待新程序）
					//field.setValue(arg0)
					field.setValue(d.getTime());
				}
				if("Z0309".equalsIgnoreCase(item.getItemid())) //创建人
				{
					field.setReadonly(true);  //此字段为只读状态
					field.setValue(this.getUserView().getUserName());
					
				}
				if("z0317".equalsIgnoreCase(item.getItemid())) //使用状态
				{
					field.setReadonly(true);  //此字段为只读状态
					field.setValue("0");
					
				}
				
				if("z0327".equalsIgnoreCase(item.getItemid())&&("1".equals(model)|| "3".equals(model))) //驳回原因
				{
					field.setReadonly(true);  
				}
				
				
				if("1".equals(model)&&("z0315".equalsIgnoreCase(item.getItemid())|| "z0323".equalsIgnoreCase(item.getItemid())|| "z0321".equalsIgnoreCase(item.getItemid())|| "z0325".equalsIgnoreCase(item.getItemid())))
					field.setReadonly(true);
				if(("2".equals(model)&&("z0323".equalsIgnoreCase(item.getItemid())|| "z0321".equalsIgnoreCase(item.getItemid())|| "z0325".equalsIgnoreCase(item.getItemid()))))
					field.setReadonly(true);
				if("z0301".equalsIgnoreCase(item.getItemid())|| "z0101".equalsIgnoreCase(item.getItemid()))
					field.setVisible(false);
				if("3".equals(model)&& "z0101".equalsIgnoreCase(item.getItemid()))
					field.setVisible(true);
				
				if("z0311".equalsIgnoreCase(item.getItemid())|| "z0321".equalsIgnoreCase(item.getItemid())|| "z0325".equalsIgnoreCase(item.getItemid()))
					field.setReadonly(true);
				if("z0325".equalsIgnoreCase(item.getItemid()))
					field.setLevel(level);
				else
					field.setLevel(0);
				if("z0336".equalsIgnoreCase(item.getItemid())||hireMajor.equalsIgnoreCase(item.getItemid()))
					field.setReadonly(true);
				fieldlist.add(field);
				if("Z0319".equalsIgnoreCase(item.getItemid())) {//审批状态  后加当前操作人员 zzk
					FieldItem item_currappuser=new FieldItem();
					item_currappuser.setItemtype("A");
					item_currappuser.setCodesetid("0");
					item_currappuser.setItemid("currappusername");
					item_currappuser.setItemdesc("当前操作人员");
					item_currappuser.setItemlength(20);
					field=(Field)item_currappuser.cloneField();
					field.setReadonly(true);
					fieldlist.add(field);
//					FieldItem currappuser=new FieldItem();
//					currappuser.setItemtype("A");
//					currappuser.setCodesetid("0");
//					currappuser.setItemid("currappuser");
//					currappuser.setItemdesc("当前操作人员ID");
//					currappuser.setItemlength(20);
//					currappuser.setVisible(false);
//					field=(Field)currappuser.cloneField();
//					fieldlist.add(field);
				} 
			}
			
			if("3".equals(model))  //审核查询模块
			{
				strsql.setLength(0);
				strsql.append("select z03.z0301 z0301a,z03.z0319 z0319a,z03.z0311 z0311a,z03.z0321 as z0321a,z03.z0325 as z0325a,z03.currappusername as currappusername,z03.currappuser,"+sss.substring(1)+" from z03 "+getAutitingSQL_where(codesetid,codeid,extendSql,orderSql,query_desc));
				whl_sql.setLength(0);
				whl_sql.append(getAutitingSQL_where(codesetid,codeid,extendSql,orderSql,query_desc));
			}
			Field editField=new Field("z0301a","查阅"); 
			editField.setReadonly(true);
			editField.setSortable(false);
			editField.setAlign("center");
			editField.setLevel(0);
			fieldlist.add(0,editField);

			
			editField=new Field("z0319a","查阅"); 
			editField.setReadonly(true);
			editField.setSortable(false);
			editField.setVisible(false);
			editField.setLevel(0);
			fieldlist.add(editField);
			editField=new Field("z0311a","查阅"); 
			editField.setReadonly(true);
			editField.setSortable(false);
			editField.setVisible(false);
			editField.setLevel(0);
			fieldlist.add(editField);
			editField=new Field("z0321a","查阅"); 
			editField.setReadonly(true);
			editField.setSortable(false);
			editField.setVisible(false);
			editField.setLevel(0);
			fieldlist.add(editField);
			editField=new Field("z0325a","查阅"); 
			editField.setReadonly(true);
			editField.setSortable(false);
			editField.setVisible(false);
			editField.setLevel(0);
			fieldlist.add(editField);
			if("2".equalsIgnoreCase(model)){
				Field editField2=new Field("ctrlparama","测评配置"); 
				editField2.setReadonly(true);
				editField2.setSortable(false);
				editField2.setAlign("center");
				editField2.setLevel(0);
				fieldlist.add(editField2);
				}
		
			if(map==null||map.get("hire_object")==null||((String)map.get("hire_object")).length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置招聘对象指标！"));
			String isOrgWillTableIdDefine="0";
			if(map!=null&&map.get("orgWillTableId")!=null&&!"#".equals((String)map.get("orgWillTableId")))
			{
				isOrgWillTableIdDefine="1";
			}
			/**招聘需求上报进行工资总额控制*/
			String isCtrlReportGZ="0";
			/**招聘需求上报进行编制控制*/
			String isCtrlReportBZ="0";
			if(map!=null&&map.get("isCtrlReportGZ")!=null)
			{
				isCtrlReportGZ=(String)map.get("isCtrlReportGZ");
			}
			if(map!=null&&map.get("isCtrlReportBZ")!=null)
			{
				isCtrlReportBZ=(String)map.get("isCtrlReportBZ");
			}
			String isCtrl="0";
			if("1".equals(isCtrlReportGZ)|| "1".equals(isCtrlReportBZ))
				isCtrl="1";
			this.getFormHM().put("isCtrl", isCtrl);
			PosparameXML pos = new PosparameXML(this.getFrameconn());  
			/**=1控制到部门，=0控制到单位*/
			String bzctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
			GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap gzhm = XMLbo.getValuesMap();
			/**=0控制到部门*/
			String gzctrl_type = "1";
			if(gzhm!=null)
				gzctrl_type=(String) gzhm.get("ctrl_type");
			if((bzctrl_type!=null&& "1".equals(bzctrl_type))||gzctrl_type!=null&& "0".equals(gzctrl_type))
				this.getFormHM().put("showUMCard","1");
			else
				this.getFormHM().put("showUMCard","0");
			
			this.getFormHM().put("moreLevelSP", moreLevelSP);
			this.getFormHM().put("isOrgWillTableIdDefine", isOrgWillTableIdDefine);
			ArrayList splist = this.getSpList(splistCtrl);
			this.getFormHM().put("splist",splist);
			this.getFormHM().put("sp_flag",(sp!=null&&!"".equals(sp)&&!"-1".equals(sp))?sp:"-1");
			this.getFormHM().put("sp", "");
			/**安全平台改造,将sql加密，由于招聘模块sql过多,所以只好将sql加密**/
			this.getFormHM().put("whl_sql",PubFunc.encrypt(whl_sql.toString()));
			this.getFormHM().put("fieldSize",/*String.valueOf(list.size()-2)*/String.valueOf(this.getFieldListSize(fieldlist)));
			this.getFormHM().put("fieldlist",fieldlist);
			/**安全平台改造,将sql放置到userView中**/
			this.userView.getHm().put("hire_sql", strsql.toString());
			/**这个sqldataset标签要用，所以还要放在fom中**/
			this.getFormHM().put("sql",strsql.toString());
			this.getFormHM().put("tablename",tablename);
			this.getFormHM().put("model",model);
			/**将这俩sql进行加密**/
			this.getFormHM().put("extendSql",PubFunc.encrypt(""));
			this.getFormHM().put("orderSql",PubFunc.encrypt(""));
			this.getFormHM().put("username",this.getUserView().getUserName());
			this.getFormHM().put("linkDesc",query_desc);
			//private String codeItemId;
			//private String codeSetId;
			this.getFormHM().put("codeItemId", codeid);
			this.getFormHM().put("codeSetId", codesetid);
			this.getFormHM().put("isSendMessage", isSendMessage);
			hm.remove("b_query");
			hm.remove("b_query2");
			hm.remove("b_query3");
			
			this.getFormHM().put("spRelation", spRelation);
			this.getFormHM().put("spcount", spcount);
			this.getFormHM().put("zpappfalg", getzpappflag(actortype));
			//更新待办任务表
			ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, this.userView);
			if(b_query2!=null)
				zpbo.updatingPendingTask("0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	//取得需求审核模块的sql语句 (已批 并指定计划 且 计划已发布的)
	public String getAutitingSQL_where(String codesetid,String codeid,String extendSql,String orderSql,String query_desc)
	{
		StringBuffer strsql=new StringBuffer(" where ");
		//所属单位 或 部门 在组织范围内的
		if("link".equals(query_desc)&&codeid!=null&&codeid.trim().length()>0)
		{
			if(codeid.indexOf("`")==-1)  
				strsql.append(" ( Z0311 like '"+codeid+"%'  or ( ( z0321 is not null and z0321 like '"+codeid+"%') or ( z0325 is not null and z0325 like '"+codeid+"%')    ) )    ");  // z0311 like '"+codeid+"%'  ");dml 2011-04-07 z0303，z0305不用
			else
			{
				StringBuffer tempsql=new StringBuffer("");
				String[] temp=codeid.split("`");
				for(int i=0;i<temp.length;i++)
				{
					tempsql.append(" or Z0311 like '"+temp[i].substring(2)+"%'");
					tempsql.append(" or  ( z0321 is not null and z0321 like '"+temp[i].substring(2)+"%')");//dml 2011-04-07  兼容校园招聘
					tempsql.append("or ( z0325 is not null and z0325 like '"+temp[i].substring(2)+"%') ");
				}
				strsql.append(" ( "+tempsql.substring(3)+" ) ");
			}
		}
		else
		{
			strsql.append(" 1=1  ");
		}
		if(extendSql!=null&&extendSql.trim().length()>3)
		{
			strsql.append(" and "+extendSql);
			
		}
		if(orderSql!=null&&orderSql.trim().length()>3)
		{
			strsql.append(" "+orderSql);
		}
		else
		{
			strsql.append(" order by Z0301 desc");
		}
		
		return strsql.toString();
	}
	public ArrayList getSpList(String queryDesc)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in('02','03','04','06','07','09'";
			
			if(!"2".equals(queryDesc)){//如果不是需求审核,就需要把起草状态添加进来
				sql = sql+",'01'";
			}
			sql = sql+")";
			
			ContentDAO ao = new ContentDAO(this.getFrameconn());
			list.add(new CommonData("-1","全部"));
			this.frowset = ao.search(sql);
			
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public int getFieldListSize(ArrayList fieldList)
	{
		int n=0;
		try
		{
			for(int i=0;i<fieldList.size();i++)
			{
				Field field=(Field)fieldList.get(i);
				if(field.isVisible())
					n++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	public HashMap getNbaseMap()
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search("select pre from dbname ");
			while(rs.next())
			{
				map.put(rs.getString("pre").toUpperCase(), "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 判断招聘需求审核页面是显示报批还是审批;若只有一个直接领导则同时获取直接领导的名字
	 * @param spRelationl
	 * @return count（大于0:报批|0：审批）+":"+Actortype（审批关系中参与者类型）+":"+appname（直接领导的名字）; 
	 * @throws GeneralException
	 */
	public String getSpcount(String spRelationl) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String count = "0";
		String Objectid = "";
		String Actortype = "";
		String appname = "";
		try {
			this.frowset=dao.search("select Actor_type from t_wf_relation  where relation_id='"+spRelationl+"'");
			if(this.frowset.next())
				Actortype = this.frowset.getString("Actor_type");
			
			if("1".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getDbname()+this.userView.getA0100();
			else if("4".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getUserName();
			
			this.frowset=dao.search("select count(1) count from t_wf_mainbody  where Actor_type='" + Actortype
					+ "' and Object_id='"+Objectid+"' and relation_id='"+spRelationl+"' and sp_grade='9'");
			if(this.frowset.next())
				count = this.frowset.getInt("count")+"";
			
			if("1".equalsIgnoreCase(count)){
				if("1".equalsIgnoreCase(Actortype)){//审批关系中的参与者为自助用户
					this.frowset=dao.search("select Mainbody_id from t_wf_mainbody  where  Actor_type='" + Actortype
							+ "' and Object_id='"+Objectid+"' and relation_id='"+spRelationl+"' and sp_grade='9'");
					if(this.frowset.next()){
						String dbname = this.frowset.getString("Mainbody_id").substring(0, 3);
						String a0100 = this.frowset.getString("Mainbody_id").substring(3, this.frowset.getString("Mainbody_id").length());
						this.frecset = dao.search("select a0101 from "+dbname+"a01 where a0100='"+a0100+"'");
						if(this.frecset.next())
							appname = this.frecset.getString("a0101");
					}
						
				} else if("4".equalsIgnoreCase(Actortype)){//审批关系的参与者为业务用户
					String username = "";
					String fullname = "";
					String nbase = "";
					String a0100 = "";
					
					StringBuffer sql = new StringBuffer();
					sql.append("Select username,a.fullName,a.a0100,a.nbase");
					sql.append(" from operuser a,t_wf_mainbody b");
					sql.append(" where a.username=b.Mainbody_id");
					sql.append(" and b.Actor_type='4'");
					sql.append(" and Object_id='" + Objectid + "'");
					sql.append(" and relation_id='" + spRelationl + "'");
					sql.append(" and SP_GRADE='9'");
					
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						username = this.frowset.getString("username");
						fullname = this.frowset.getString("fullName");
						nbase = this.frowset.getString("nbase");
						a0100 = this.frowset.getString("a0100");
					}
					
					if(fullname!=null&&fullname.length()>0)
						appname = fullname;
					else if(a0100!=null&&a0100.length()>0&&nbase!=null&nbase.length()>0){
						this.frecset = dao.search("select a0101 from " + nbase + "a01 where a0100='" + a0100 + "'");
						if (this.frecset.next())
							appname = this.frecset.getString("a0101");
					} else 
						appname = username;
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return count + ":" + Actortype + ":" + appname;
		
	}

	/**
	 * 判断当前用户类型和审批关系的参与者类型是否一致
	 * @param type 审批关系的参与者类型
	 * @return flag true：一致|false：不一致
	 */
	private String getzpappflag(String type) {
		
		String flag = "true";
		String a0100 = this.userView.getA0100();
		String dbname = this.userView.getDbname();
		int status = this.userView.getStatus();
		//审批关系的参与者类型为自助用户时，当前用户为业务用户但是关联了自助用户时默认为true
		if ("1".equalsIgnoreCase(type) && status != 4 && (a0100 == null
				|| a0100.length() < 4 || dbname == null || dbname.length() < 1))
			flag = "false";
		
		if ("4".equalsIgnoreCase(type) && status == 4)
			flag = "false";
		
		return flag;

	}
}
