package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 9, 2007:3:55:54 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitEmployResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String value=(String)this.getFormHM().get("value");
			String viewvalue=(String)this.getFormHM().get("viewvalue");
			String isShow = (String)this.getFormHM().get("isShow");
			String posID = (String)this.getFormHM().get("posID");
			String professional = (String)this.getFormHM().get("professional");
			String returnflag="";

			if(hm.get("returnflag")!=null)
			{
				returnflag=(String)hm.get("returnflag");
			}
			else
			{
				returnflag=(String)this.getFormHM().get("returnflag");
			}
			updateTable();
			String queryType=(String)this.getFormHM().get("queryType");
			if(queryType==null|| "".equals(queryType))
				queryType="0";
			this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
			
			String z0301=(String)hm.get("z0301");
			String personType=(String)hm.get("personType");  // 0:应聘库 1：人才库   4:我的收藏夹
			/**招聘岗位,查看应聘,z0301进行了加密,所以要解密回来**/
			if(z0301!=null&&!"-1".equals(z0301)){//只有查看应聘时才会传递特殊的加密后用工需求号进来
				z0301=PubFunc.decrypt(z0301);
			}
			String operate=(String)hm.get("operate");
			hm.remove("operate");
			String employType="0";							//0：业务平台  1：自助平台
			String hireObjectId="-1";//-1 01校园招聘 02社会招聘 03 内部招聘
			

			
			if(operate!=null&& "init".equals(operate))
			{
				value="";
				viewvalue="";
				posID="";
				if(hm.get("employType")!=null)
				{
					employType=(String)hm.get("employType");
					hm.remove("employType");
					this.getFormHM().put("employType",employType);
				}
				else
					this.getFormHM().put("employType","0");
				
				this.getFormHM().put("hireObjectId","-1");   //2008/03/20  dengcan
				this.getFormHM().put("isSelectedAll","0");
			}
			else
				hireObjectId=(String)this.getFormHM().get("hireObjectId");
			
			String flag="1";
			if("01".equals(hireObjectId)&& "".equals(professional)){
				flag="0";
			}else if(!"01".equals(hireObjectId)&& "".equals(posID)){
				flag="0";
			}
			this.getFormHM().put("posIDList",getPosIDList(value));
			if(hm.get("employType")==null)
				employType=(String)this.getFormHM().get("employType");
			else
				employType=(String)hm.get("employType");
			
			String resume_state_item=(String)this.getFormHM().get("resume_state_item");
			EmployResumeBo bo=new EmployResumeBo(this.getFrameconn());
			
//			权限控制
			/*if(!this.userView.isSuper_admin()&&employType.equals("0")&&(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().length()<=2))
				 throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));*/
			
			
			
			
			
			
			
			StringBuffer str_sql=new StringBuffer("select zpt.resume_flag");
			StringBuffer str_whl=new StringBuffer(" from zp_pos_tache zpt ");
			StringBuffer order_str=new StringBuffer("");
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=xmlBo.getAttributeValues();
			String schoolPosition="";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)
				schoolPosition=(String)map.get("schoolPosition");
			String hireMajor="";
			if(map.get("hireMajor")!=null)
				hireMajor=(String)map.get("hireMajor");
			if(resume_state_item==null||resume_state_item.trim().length()==0)
			{
				if(map.get("resume_state")!=null)
					resume_state_item=(String)map.get("resume_state");
				
			}
			String active_field="";//简历激活状态指标
			if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
			{
				active_field=(String)map.get("active_field");
			}
			ArrayList resumeStateList=(ArrayList)this.getFormHM().get("resumeStateList");
			String    resumeState=(String)this.getFormHM().get("resumeState");//resumeState:简历状态 10：未选 /11：待选/12：已选/13:未通过(前面这四个关联到代码类36,额外添加的   -1：未选岗位；-2：已选岗位;-3：全部)
			if(resumeStateList.size()==0||(operate!=null&& "init".equals(operate)))
			{
				resumeStateList=bo.getResumeStateList(active_field);
				resumeState="10";
				this.getFormHM().put("isShowCondition","none");
			}
			HashMap resumeFieldMap=(HashMap)this.getFormHM().get("resumeFieldMap");
			ArrayList resumeFieldList=(ArrayList)this.getFormHM().get("resumeFieldList");
			String fielditem1=(String)this.getFormHM().get("fielditem1");
			String fielditem2=(String)this.getFormHM().get("fielditem2");
			String fielditem3=(String)this.getFormHM().get("fielditem3");
			String fielditem4=(String)this.getFormHM().get("fielditem4");
			String fielditem5=(String)this.getFormHM().get("fielditem5");
			String order_item=(String)this.getFormHM().get("order_item");
			String order_desc=(String)this.getFormHM().get("order_desc");
			ArrayList posConditionList=(ArrayList)this.getFormHM().get("posConditionList");
			
			if(order_desc==null||order_desc.length()==0){
				order_desc="asc";
			}
			
			
			if(resumeFieldList.size()==0||(operate!=null&&("init".equals(operate)|| "init2".equals(operate)))|| "-1".equals(resumeState))
			{
				String[] resumes=bo.getResumeFieldStr();//获得浏览简历指标
				resumeFieldMap=bo.getResumeFieldMap(resumes,personType,resumeState);//设置浏览简历指标的相关信息
				resumeFieldList=bo.getResumeFieldList(resumeFieldMap,resumes,personType,resumeState);
				if(resumeFieldList.size()==0||(operate!=null&&("init".equals(operate)|| "init2".equals(operate))))
				{
					int size=resumeFieldList.size();
					fielditem1=getCommonDataValue(resumeFieldList,0);
					if(size>=2)
						fielditem2=getCommonDataValue(resumeFieldList,1);
					else
						fielditem2=getCommonDataValue(resumeFieldList,size-1);
					if(size>=3)
						fielditem3=getCommonDataValue(resumeFieldList,2);
					else
						fielditem3=getCommonDataValue(resumeFieldList,size-1);
					if(size>=4)
						fielditem4=getCommonDataValue(resumeFieldList,3);
					else
						fielditem4=getCommonDataValue(resumeFieldList,size-1);
					if(size>=5)
						fielditem5=getCommonDataValue(resumeFieldList,4);
					else
						fielditem5=getCommonDataValue(resumeFieldList,size-1);
					
					if("init".equalsIgnoreCase(operate)){
						order_desc="asc";
					}
				}
			}
			
			ArrayList orderItemList=bo.getOrderItemList(fielditem1,fielditem2,fielditem3,fielditem4,fielditem5,resumeFieldMap,personType);
			if("12".equals(resumeState)&& "1".equals(queryType))
			{
				orderItemList.add(new CommonData("username","操作用户"));
			}
			if("-1".equals(resumeState)&& "0".equals(personType))
			{
					orderItemList.remove(0);
					if(hireMajor!=null&&hireMajor.trim().length()>0)
			    	{
			    		FieldItem item = DataDictionary.getFieldItem(hireMajor.toLowerCase());
			    		if(item!=null)
			    		{
			    			orderItemList.remove(0);
			    		}
			    	}
			}
			if(order_item==null||order_item.trim().length()==0||(operate!=null&& "init".equals(operate))||("zp_pos_id".equalsIgnoreCase(order_item)&& "-1".equals(resumeState)))
			{
				if("0".equals(personType)&&!"-1".equals(resumeState))
					order_item="apply_date";
				else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
					order_item="createtime";
				//order_desc="desc";
			//	order_item="A0101";
			}
			if(!((("zp_pos_id".equalsIgnoreCase(order_item)||("username".equalsIgnoreCase(order_item)&& "12".equals(resumeState))||order_item.equalsIgnoreCase(hireMajor))&& "0".equals(personType))|| "A0101".equalsIgnoreCase(order_item)||order_item.equalsIgnoreCase(fielditem1)||order_item.equalsIgnoreCase(fielditem2)||order_item.equalsIgnoreCase(fielditem3)||order_item.equalsIgnoreCase(fielditem4)||order_item.equalsIgnoreCase(fielditem5)))
				order_item=getCommonDataValue(orderItemList,0);
			
			
			String personTypeField="";
			if(map!=null&&map.get("person_type")!=null)
				personTypeField=(String)map.get("person_type");
			if(personTypeField==null|| "".equals(personTypeField))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置人才库标识指标！"));
			if(posConditionList.size()==0||(operate!=null&& "init".equals(operate)))
			{
				PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
				posConditionList=positionDemand.getResetPosConditionList("0","-2");
			}
			
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));

			StringBuffer sb=this.userView.getDbpriv();//用户人员库权限
			if(!this.userView.isSuper_admin()&&!sb.toString().contains(dbname)){
				throw GeneralExceptionHandler.Handle(new Exception("您没有操作应聘人员库权限!"));
			}
			
			ArrayList list=new ArrayList();
			list.add(fielditem1);list.add(fielditem2);list.add(fielditem3);list.add(fielditem4);list.add(fielditem5);
			
			
			String upValue=(String)this.getFormHM().get("upValue");  //子集代码型 以上
			String vague=(String)this.getFormHM().get("vague");  //1 ：模糊查询 
			
			String select=bo.getSql_select(dbname,list,resumeFieldMap,personType,resumeState);
			String from="";
			
			/*********************应聘时间查询*******************/
			String applyStartDate =(String) this.getFormHM().get("applyStartDate");
			String applyEndDate =(String) this.getFormHM().get("applyEndDate");
			if(operate!=null&& "init".equals(operate)){
				applyStartDate="";
				applyEndDate="";
			}
				this.getFormHM().put("applyStartDate", applyStartDate);
				this.getFormHM().put("applyEndDate", applyEndDate);
				boolean bQueryApplyDate = false;
			if(!("".equals(applyStartDate.trim())&& "".equals(applyEndDate.trim()))){
				if("".equals(applyStartDate.trim()))
					applyStartDate="1900-01-01";
				if("".equals(applyEndDate.trim()))
					applyEndDate="9999-12-31";
				//from+=" and zpt.apply_date between "+Sql_switcher.dateValue(applyStartDate)+" and "+Sql_switcher.dateValue(applyEndDate);
/*				PositionDemand posDemand=new PositionDemand(this.getFrameconn());
				from+=" and "+posDemand.getDataValue("zpt.apply_date",">=",applyStartDate);
				from+=" and "+posDemand.getDataValue("zpt.apply_date","<=",applyEndDate);*/
/*				findtime="from zp_resume_pack zpp inner join  (select a0100 from zp_pos_tache where  apply_date between '"+applyStartDate+"' and '"+applyEndDate+"' group by a0100  ) zpt "+
				"on zpp.a0100=zpt.a0100 inner join "+dbname+"a01 on "+dbname+" A01.A0100=zpt.A0100";*/


			}
			
			if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId()))
			{
				/**
				 * modify dml 2012-3-31 15:50:33
				 * reason 因增加业务管理范围导致权限规则改边
				 * */
//	    		if(this.userView.getStatus()==0)
//	    		{
//		    		String codeid=this.getUserView().getUnit_id();
//	    			if(codeid==null||codeid.trim().length()==0||codeid.equalsIgnoreCase("UN"))
//		    		{
//		    			codeid="UN-0";
//		    			throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					
//		    		}
//	    			from=bo.getSql_from(dbname,list,resumeFieldMap,resumeState,posConditionList,personType,z0301,codeid,upValue,vague,this.getUserView(),queryType);
//    			}
//    			else if(this.userView.getStatus()==4/*employType.equals("1")*/)
//	    		{
//		    		String codeid=this.getUserView().getManagePrivCodeValue();
//		    		String codesetid=this.getUserView().getManagePrivCode();
//		    		if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//		    		{
//	    				codeid="-0";
//		    			codesetid="UN";
//		    			throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//		    		}
//		    		else if(codesetid!=null&&(codeid==null||codeid.equals("")))
//	    			{
//	    					codeid="";
//	    			}
//				
//		    		from=bo.getSql_from(dbname,list,resumeFieldMap,resumeState,posConditionList,personType,z0301,codesetid+codeid,upValue,vague,this.getUserView(),queryType);
//	    		}
	    		String code=this.userView.getUnitIdByBusi("7");	    	
	    		if(code==null||code.trim().length()==0|| "UN".equalsIgnoreCase(code))
	    		{
	    			throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围!"));
	    		}
	    		
	    		if(code.trim().length()==3)//业务用户如果设置的操作单位为全部可能出现UM`或者UN`的情况
				{
					code="";
				}
	    		from=bo.getSql_from(dbname,list,resumeFieldMap,resumeState,posConditionList,personType,z0301,code,upValue,vague,this.getUserView(),queryType,hireObjectId,applyStartDate,applyEndDate);
			}
    		else
    		{
    			from=bo.getSql_from(dbname,list,resumeFieldMap,resumeState,posConditionList,personType,z0301,"UN",upValue,vague,this.getUserView(),queryType,hireObjectId,applyStartDate,applyEndDate);
    		}
			
			if(!"-1".equals(hireObjectId))
			{
				if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState)|| "-3".equals(resumeState))
				{
					from+=" and "+dbname+"A01.a0100 in (select a0100 from zp_pos_tache where zp_pos_id in (select z0301 from z03 where "+(String)map.get("hire_object")+"='"+hireObjectId+"'))";
				}
				else
				{
					from+=" and "+(String)map.get("hire_object")+"='"+hireObjectId+"'";
				}
				
			}
			String conditionSQL = "";
			PositionDemand pd=new PositionDemand(this.getFrameconn());
            String sql=pd.getSqlByCondition(posConditionList,dbname,upValue,vague);
			if(bo.getWhlConditionCount(sql)>0) //如果有条件
			{
				if("0".equals(personType)&&!"-1".equals(resumeState)&&!"-3".equals(resumeState))
					conditionSQL=sql;
				else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState)|| "-2".equals(resumeState)|| "-3".equals(resumeState))
					conditionSQL=sql;
			}
			from+="  and "+dbname+"A01.a0100 is not null ";
			//  20160630 linbz  增加时间区间 条件
			if(!("".equals(applyStartDate.trim())&&"".equals(applyEndDate.trim()))){
				if(Sql_switcher.searchDbServer() == Constant.MSSQL){
					from+="  and zpt.apply_date between '"+applyStartDate+" 00:00:00' and '"+applyEndDate+" 23:59:59' ";
				}else{
					from+="  and zpt.apply_date between to_date('"+applyStartDate+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"+applyEndDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ";
				}
				from+=" ";
			}
			/**外网招聘时，人才库共享，不加限制*/
			/*if(personType.equals("1")&&!(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1")))
			{
				    String codeid="";
				
					if(this.userView.getStatus()==0)
					{
						String code=this.userView.getUnit_id();
						if(code==null||code.equals(""))
						{
							return;
						}
						else if(code.trim().length()==3)
						{
							code="";
						}
						else
						{
							if(code.indexOf("`")==-1)
							{
								code=code.substring(2);
							}else
							{
								String[] temp=code.split("`");
								for(int i=0;i<temp.length;i++)
								{
									if(temp[i]==null||temp[i].equals(""))
										continue;
									code=temp[i].substring(2);
									break;
								}
							}
						}
						codeid=code;
					}
					else if(this.userView.getStatus()==4)
					{
						String codeset=this.userView.getManagePrivCode();
						String codevalue=this.userView.getManagePrivCodeValue();
						if(codeset==null||codeset.equals(""))
						{
							return;
						}else
						{
							if(codevalue==null||codevalue.equals(""))
							{
								codevalue="";
							}
						}
						codeid=codevalue;
					}
			
				from+=" and ("+dbname+"a01.b0110 like '"+codeid+"%' or "+dbname+"a01.b0110 is null)";
			}*/
			/**内部人才交流，不能看见本单位的简历*/
			if(active_field!=null&&!"".equals(active_field.trim()))
			{
				from+=" and ("+dbname+"a01."+active_field+"='1' or "+dbname+"A01."+active_field+" is null)";

				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))
				{
					if("-1".equals(resumeState)|| "-2".equals(resumeState)|| "-3".equals(resumeState))
					{
						if(this.userView.getStatus()==0)
						{
							//String unitid=this.userView.getUnitIdByBusi("7");	  
							String unitid=this.userView.getUnitIdByBusi("7");	//按业务范围/操作单位/人员范围获得 管理范围   
							if(unitid==null|| "".equals(unitid)){
								from+=" and 1=2 ";
							}
							else if("UN`".equalsIgnoreCase(unitid))
							{
								//全部，现在处理为都可以看见
							}
							else
							{
								//String[] unit_arr=this.userView.getUnit_id().split("`");//业务用户获得操作单位
								from+=" and ( ";
								String[] unit_arr=this.userView.getUnitIdByBusi("7").split("`");
								for(int i=0;i<unit_arr.length;i++)
								{
									if(unit_arr[i]==null|| "".equals(unit_arr[i]))
										continue;
									String org=unit_arr[i].substring(2);
									if(i==0){
										from+=" "+dbname+"a01.b0110 not like '"+org+"%'";
									}else{
										from+=" and "+dbname+"a01.b0110 not like '"+org+"%'";
									}
								
								}
								from+=" or "+dbname+"a01.b0110 is null )";
							}
						}
						else
						{
				        	String org=this.userView.getUserOrgId();//自助用户获得所在单位
				        	if(org==null|| "".equals(org))
				    	    	from+=" and 1=2 ";
				            else{
				            	from+=" and ("+dbname+"a01.b0110 not like '"+org+"%'";
				        		from+=" or "+dbname+"a01.b0110 is null )";
				            }

						}
					}
				}
			}
			/**人才库*/
			if("1".equals(personType))
			{
				if(active_field!=null&&!"".equals(active_field.trim()))
					from+=" and ("+dbname+"a01."+active_field+"='1' or "+dbname+"a01."+active_field+" is null)";
				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))
				{
					if(active_field!=null&&!"".equals(active_field.trim()))
					{
						if(this.userView.getStatus()==0)
						{
							String unitid=this.userView.getUnitIdByBusi("7");//this.userView.getUnit_id();
							if(unitid==null|| "".equals(unitid))
								from+=" and 1=2 ";
							else if("UN`".equalsIgnoreCase(unitid))
							{
								//全部，现在处理为都可以看见
							}
							else
							{
								String[] unit_arr=this.userView.getUnit_id().split("`");
								for(int i=0;i<unit_arr.length;i++)
								{
									if(unit_arr[i]==null|| "".equals(unit_arr[i]))
										continue;
									String org=unit_arr[i].substring(2);
									from+=" and "+dbname+"a01.b0110 not like '"+org+"%'";//如果操作单位为部门是否有问题
								}
							}
						}
						else
						{
				        	String org=this.userView.getUserOrgId();
				        	if(org==null|| "".equals(org))
				        		from+=" and 1=2 ";
				        	else
			                	from+="and "+dbname+"a01.b0110 not like '"+org+"%'";
						}
					} 
				}
			}
			//查询应聘岗位（专业）
			if("01".equalsIgnoreCase(hireObjectId)&&professional!=null&&!("".equals(professional.trim()))){
			   
		         if(hireMajor.trim().length()<=0){
		             throw new GeneralException("招聘专业指标未配置,请联系系统管理员!");
                } else {
                    if ("1".equalsIgnoreCase(personType) || "4".equalsIgnoreCase(personType)) {//1代表着来自人才库  4：我的收藏夹
                        from += " and  " + dbname + "A01.a0100 in ( ";
                        from += " select zpt.a0100 from zp_pos_tache zpt,z03 where zpt.zp_pos_id=z03.z0301 and  Z03.Z0338 like '%" + professional + "%' ";
                        from += " ) ";
                    } else
                        from += " and z03." + hireMajor + " like '%" + professional + "%'";
                }
			}
			else if(!"01".equalsIgnoreCase(hireObjectId)&&((posID!=null&&!"".equals(posID.trim()))||(value!=null&&!"".equals(value.trim())))  ){
				
                if ("1".equalsIgnoreCase(personType) || "4".equalsIgnoreCase(personType)) {//1代表着来自人才库  4：我的收藏夹
                    from += " and  " + dbname + "A01.a0100 in ( ";
                    from += " select zpt.a0100 from zp_pos_tache zpt,z03 where zpt.zp_pos_id=z03.z0301 ";
                }
                
                if (posID.length() > 0)
                    from += " and z0311='" + posID + "'";
                else
                    from += " and z0311 like '" + value + "%'";
               
                if ("1".equalsIgnoreCase(personType) || "4".equalsIgnoreCase(personType))   //1代表着来自人才库  4：我的收藏夹
                    from += " ) ";
                
            }
			
			String order=bo.getSql_order(resumeFieldMap,order_item,order_desc,dbname,personType,resumeState);
			String resumeCount=bo.getResumeCount(select,from);
		 
			String dd="";
			if("0".equals(personType)&&!"-1".equals(resumeState))
				dd="resumeState,thenumber,a0101,zp_name,"+fielditem1+"1,"+fielditem2+"2,"+fielditem3+"3,"+fielditem4+"4,"+fielditem5+"5,a0100,zp_pos_id";
			else if("4".equals(personType)|| "-1".equals(resumeState))
				dd="thenumber,a0101,"+fielditem1+"1,"+fielditem2+"2,"+fielditem3+"3,"+fielditem4+"4,"+fielditem5+"5,a0100";
			else if("1".equals(personType))//zp_name为第一意向的岗位名称，通过dd传给前台
				dd="thenumber,a0101,zp_name,"+fielditem1+"1,"+fielditem2+"2,"+fielditem3+"3,"+fielditem4+"4,"+fielditem5+"5,a0100";
			if("-3".equals(resumeState)|| "-2".equals(resumeState))
			{
				dd+=",resume_flag";
			}
			
			String sql_view="select "+dbname+"A01.a0100,"+dbname+"A01.a0101  ";
			String sel = bo.getSelect(resumeFieldMap, order_item, order_desc, dbname, personType, resumeState);
			if("0".equals(personType)&&!"-1".equals(resumeState))
				sql_view=sql_view+",zp_pos_id";
			sql_view=sql_view+","+sel;
			//后台加密，避免前台加密造成页面加载缓慢
			String encryption_sql=select+" "+from+" "+order;
			if("0".equals(personType)){//0应聘库
				if("-1".equals(resumeState)){
					encryption_sql="select "+dbname+"A01.a0100 "+from+" "+order;
				}else{
					encryption_sql="select "+dbname+"A01.a0100 ,zpt.zp_pos_id "+from+" "+order;
					//encryption_sql="select "+dbname+"A01.a0100 "+from+" "+order;
				}
			}else{
				encryption_sql="select "+dbname+"A01.a0100 "+from+" "+order;
			}
			
			if("01".equalsIgnoreCase(hireObjectId)){
				this.getFormHM().put("isShow", "2");
			}else {
				this.getFormHM().put("isShow", "1");
			}
			this.getFormHM().put("posID",posID);
			this.getFormHM().put("value", value);
			this.getFormHM().put("viewvalue", viewvalue);
			this.getFormHM().put("professional", "");
			this.getFormHM().put("hireObjectList",bo.getHireObjectList());
			//this.getFormHM().put("conditionSQL", conditionSQL);
			this.userView.getHm().put("hire_condition_sql", conditionSQL);
			this.getFormHM().put("sql_view",sql_view);
			this.getFormHM().put("resumeCount",resumeCount);
			//System.out.println(select +" "+from +" "+order );
			//System.out.println(dd);
			this.getFormHM().put("colums",dd);
			this.getFormHM().put("str_sql",select);
			this.getFormHM().put("str_whl",from);
			this.getFormHM().put("order_str",order);
			this.userView.getHm().put("hire_order_str", order);
			
			this.getFormHM().put("dbname",dbname);
			this.getFormHM().put("posConditionList",posConditionList);
			this.getFormHM().put("resume_state_item",resume_state_item);
			this.getFormHM().put("order_desc",order_desc);
			this.getFormHM().put("order_item",order_item);
			this.getFormHM().put("orderItemList",orderItemList);
			this.getFormHM().put("orderDescList",getOrderDescList());
			this.getFormHM().put("fielditem1",fielditem1);
			this.getFormHM().put("fielditem2",fielditem2);
			this.getFormHM().put("fielditem3",fielditem3);
			this.getFormHM().put("fielditem4",fielditem4);
			this.getFormHM().put("fielditem5",fielditem5);
			this.getFormHM().put("resumeFieldList",resumeFieldList);
			this.getFormHM().put("resumeFieldMap",resumeFieldMap);
			this.getFormHM().put("resumeStateList",resumeStateList);
			this.getFormHM().put("resumeState",resumeState);
			this.getFormHM().put("z0301",z0301);
			this.getFormHM().put("personType",personType);
			this.getFormHM().put("upValue",upValue);
			this.getFormHM().put("schoolPosition", schoolPosition);
			this.getFormHM().put("queryType", queryType);
			//this.getFormHM().put("encryption_sql", encryption_sql);
			this.userView.getHm().put("hire_encryption_sql", encryption_sql);
			/*ZpCondTemplateXMLBo ZTCXB=new ZpCondTemplateXMLBo(this.getFrameconn());
			ArrayList complexTemplateList = ZTCXB.getComplexTemplateList();
			String templateid="-1";
			if(this.getFormHM().get("templateid")!=null)
				templateid=(String)this.getFormHM().get("templateid");
			this.getFormHM().put("templateid", templateid);
			this.getFormHM().put("complexTemplateList", complexTemplateList);*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	public ArrayList getOrderDescList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("asc","升序"));
		list.add(new CommonData("desc","降序"));
		return list;
	}
	
	
	public String getZpPosID(String z0301)
	{
		String zp_pos_id="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search("select z0311 from z03 where z0301='"+z0301+"'");
			if(this.frowset.next())
				zp_pos_id=this.frowset.getString(1);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return zp_pos_id;
	}
	
	public String getCommonDataValue(ArrayList list,int index)
	{
		CommonData data=(CommonData)list.get(index);
		return data.getDataValue();
	}
	public void updateTable()
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from zp_pos_tache where 1=2");
			ResultSetMetaData meta = this.frowset.getMetaData();
			boolean flag = false;
			for(int i=1;i<=meta.getColumnCount();i++)
			{
				String columnName=meta.getColumnName(i).toLowerCase();
				if("username".equalsIgnoreCase(columnName))
				{
					flag=true;
					break;
				}
				
			}
			if(!flag)
			{
				Table table = new Table("zp_pos_tache");
				Field field = new Field("username","username");
				field.setDatatype(DataType.STRING);
	    		field.setLength(50);
	    		table.addField(field);
	    		DbWizard dbw=new DbWizard(this.getFrameconn());
	    		dbw.addColumns(table);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList getPosIDList(String value)
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("",""));
			if(value!=null&&value.trim().length()>0)
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from organization where codesetid='@K' and codeitemid like '"+value+"%' and codeitemid in (select E01A1 from K01)");
				while(this.frowset.next())
				{
					CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					list.add(data);
				}		
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	

}
