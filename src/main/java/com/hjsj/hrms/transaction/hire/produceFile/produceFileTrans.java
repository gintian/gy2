package com.hjsj.hrms.transaction.hire.produceFile;

import com.hjsj.hrms.businessobject.hire.*;
import com.hjsj.hrms.businessobject.train.TrParamXmlBo;
import com.hjsj.hrms.businessobject.train.b_plan.PlanTransBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class produceFileTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			String fieldWidths=(String)this.getFormHM().get("fieldWidths");
			String tablename=(String)this.getFormHM().get("tablename");
			String flag=(String)this.getFormHM().get("flag");
			String outName="";
			if("z03".equalsIgnoreCase(tablename))
			{
				//hashvo.setValue("whl_sql","${positionDemandForm.sql}");原来通过ajax请求将sql语句传到后台来的现在不能这样传递了（招聘管理--需求报批||需求审核--文件--生成PDF||生成Excel）
				String whl_sql=(String) this.userView.getHm().get("hire_sql");//安全平台改造，从userView中取得sql语句(String)this.getFormHM().get("whl_sql");
				if(whl_sql!=null)
					whl_sql=PubFunc.keyWord_reback(whl_sql);
				Connection con = this.getFrameconn();
				
				ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				ArrayList a_list=new ArrayList();
				for(Iterator t=list.iterator();t.hasNext();)
				{
					FieldItem item=(FieldItem)t.next();
					if(item.getState()!=null&& "0".equals(item.getState()))
						continue;
					else
						a_list.add(item);
					
				}
				if("1".equals(flag))		//PDF
				{
					ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());
					
					outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,a_list,whl_sql,"2");
				}
				else						//EXCEL
				{
					ExecuteExcel executeExcel=new ExecuteExcel(con);
					outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,a_list,whl_sql,"2",this.userView);
				}
			}
			else if("zp_pos_tache".equalsIgnoreCase(tablename))  //人员筛选
			{
				outName=getPersonFilterFileName(flag,tablename,fieldWidths);	
			}
			else if("zp_test_template".equalsIgnoreCase(tablename))  //面试考核 导出PDF、Excel
			{
				outName=getInterviewExamineFileName(flag,tablename,fieldWidths);
			}
			else if("zp_test_template2".equalsIgnoreCase(tablename))  //输出计划下已录人员信息
			{
				outName=getInterviewExamineFileName2(flag,tablename,fieldWidths);
			}
			else if("z05".equalsIgnoreCase(tablename))    //面试安排信息表  面试安排、面试通知里的导出pdf以及EXCEL都是走的这里
			{
				outName=getInterviewArrangeFileName(flag,tablename,fieldWidths);
			}
			else if("personnelEmploy".equalsIgnoreCase(tablename))    //人员录用
			{
				outName=getPersonnelEmployFileName(flag,tablename,fieldWidths);
			}
			else if("engagePlan".equalsIgnoreCase(tablename))    //招聘计划
			{
				outName=getEngagePlanFileName(flag,tablename,fieldWidths);
			}
			else if("posList".equalsIgnoreCase(tablename))
			{
				outName=getEngagePlanFileName2(flag,tablename,fieldWidths);  //招聘总结-职位需求输出
				
			}
			else if("resume".equalsIgnoreCase(tablename))    //输出人员简历信息
			{
				String nbase=(String)this.getFormHM().get("nbase");
				String a0100 = (String)this.getFormHM().get("a0100");
				String[] a0100s = a0100.split("#");
				a0100 = "";
				for (int i = 0; i < a0100s.length; i++) {
				    a0100 += PubFunc.decrypt(a0100s[i]) + "#";
				}
				
				String number = (String)this.getFormHM().get("number");
				String isSelectedAll=(String)this.getFormHM().get("isSelectedAll");
				String resumeState=(String)this.getFormHM().get("resumeState");
				String employType=(String)this.getFormHM().get("employType");
				String personType=(String)this.getFormHM().get("personType");
				String order_str=(String)this.userView.getHm().get("hire_order_str");//(String)this.getFormHM().get("order_str");
				String queryType= (String)this.getFormHM().get("queryType");
//				if(order_str!=null)
//					order_str=PubFunc.keyWord_reback(order_str);
				String z0301=(String)this.getFormHM().get("z0301");
				String conditionSQL= (String)this.userView.getHm().get("hire_condition_sql");//(String)this.getFormHM().get("conditionSQL");
				//if(conditionSQL!=null)
				//	conditionSQL=PubFunc.keyWord_reback(conditionSQL);
				String codesetid="";	
				String codeid="";
				if(!this.userView.isSuper_admin())
				{
					
					codeid=this.getUserView().getUnitIdByBusi("7");
					if(codeid==null||codeid.trim().length()==0)
					{
							codeid="-0";
					}else if(codeid.trim().length()==3)
					{
							codesetid=codeid.substring(0,2);
							codeid="";
							
					}
					else//dml 2011-03-27 业务用户的操作单位设置有时候会不带`这个符号{
					{
						if(codeid.trim().length()>3){
								if(codeid.indexOf("UN")!=-1&&codeid.indexOf("`")==-1){
									codeid=codeid.substring(2);
								}
						}
			     	}		    	
				
				}
				outName=getResumeExcel(nbase,a0100,tablename,codesetid,codeid,resumeState,isSelectedAll,personType,order_str,z0301,conditionSQL,queryType,number);
			}
	/////////////////////////////-----培训班-------///////////////////////////////////////		
			else if("r31".equalsIgnoreCase(tablename))    //培训班
			{
				outName=getTrainPlanFileName(flag,tablename,fieldWidths);
			}else if("r25".equalsIgnoreCase(tablename))    //培训计划
			{
				outName=getTrainPlan(flag,tablename,fieldWidths);
			}
			else if("position".equalsIgnoreCase(tablename))
			{
				//招聘岗位中导出简历
				String records = (String)this.getFormHM().get("records");//records存放是z0301,以，分割
				String recordArr[]= records.split(",");
				records="";
				for(int i =0;i<recordArr.length;i++){
					String record=recordArr[i];
					record = PubFunc.decrypt(record);
					records = records+","+record;
				}
				records = records.substring(1);
				ExecuteExcel executeExcel=new ExecuteExcel(this.getFrameconn());
				outName=executeExcel.exportResumeFormPosition(this.userView, records);
			}
			this.getFormHM().put("flag",flag);
			if(!"-1".equals(outName)){
				if (outName.endsWith("#"))
					   outName = outName.substring(0, outName.length()-1) + ".xls";
					outName = PubFunc.encrypt(outName);
			}
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 输出人员简历信息
	 * @param nbase  招聘库前缀
	 * @param a0100  人员ids
	 * @return
	 */
	public String getResumeExcel(String nbase,String a0100,String tablename,String codesetid,String codeid,String resumeState,String isSelectedAll,String personType,String order_str,String z0301,String conditionSQL,String queryType,String number)
	{
		String outName="";
		Connection con = this.getFrameconn();
		ExecuteExcel executeExcel=new ExecuteExcel(con);
		EmployResumeBo bo=new EmployResumeBo(this.getFrameconn(),this.getUserView());
		ArrayList fieldList=new ArrayList();
		if("0".equals(personType)&&!"-1".equals(resumeState)){//personType 0:应聘库 1：人才库 4:我的收藏夹  resumeState 简历状态 -1代表着未选岗位
			fieldList=bo.getFieldList();
		}else{//未选岗位的应聘简历  收藏夹 应聘人才库不导出z03信息
			fieldList=bo.getFieldListExceptZ03();
		}
		
		String encryption_sql = (String)this.userView.getHm().get("hire_encryption_sql");//(String) this.getFormHM().get("encryption_sql");
		//encryption_sql=SafeCode.keyWord_reback(encryption_sql);
		encryption_sql=encryption_sql.substring(0, encryption_sql.indexOf("order"));
		if(fieldList.size()>0)
		{
			String whl_sql="";
			String suSetSql="";
			/**不是全选*/
			if("0".equals(isSelectedAll))
			{
				whl_sql=bo.getResumeSqlModify(fieldList,nbase,a0100,1,number,personType,encryption_sql,resumeState);
				suSetSql=bo.getResumeSql(fieldList,nbase,a0100,0,number,personType);
			}
			/**全选了*/
			else{
				
				try
				{
					int i=0;
					if("4".equals(personType)|| "1".equals(personType))
					{
						if("4".equals(personType))
						{
		    				whl_sql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,1,1);
		    				suSetSql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,1,0);
						}else
						{
							whl_sql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,2,1);
							suSetSql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,2,0);
						}
						if("1".equals(personType))
				    	{
	        	     		String personTypeField="";
	        		    	ParameterXMLBo bo0=new ParameterXMLBo(this.getFrameconn(),"1");
	        		    	HashMap map=bo0.getAttributeValues();
	        		    	if(map!=null&&map.get("person_type")!=null)
		        	    		personTypeField=(String)map.get("person_type");
	        		     	if(personTypeField!=null&&!"".equals(personTypeField))
	        		     	{
	        		     		if(i!=0){
	        		    	    	whl_sql+=" and ";
	        		    	    	suSetSql+=" and ";
	        		     		}else{
	        		     			whl_sql+=" where ";
	        		     			suSetSql += " where ";
	        		     		}
	        		     		whl_sql+=" "+nbase+"a01."+personTypeField+"='"+personType+"'";
	        		     		suSetSql+=" "+nbase+"a01."+personTypeField+"='"+personType+"'";
	        	    		}
	        		     	whl_sql+=" and "+nbase+"A01.a0100 is not null ";
	        		     	suSetSql+=" and "+nbase+"A01.a0100 is not null ";
			    		}
					}
					else
					{
						
						if(resumeState.endsWith("-1"))
						{
							whl_sql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,2,1);
							suSetSql=bo.getResumeSql3(fieldList, nbase, resumeState, this.userView,2,0);
							whl_sql+=" where "+nbase+"a01.a0100 not in (select a0100 from zp_pos_tache)  and "+nbase+"A01.a0100 is not null ";
							suSetSql+=" where "+nbase+"a01.a0100 not in (select a0100 from zp_pos_tache)  and "+nbase+"A01.a0100 is not null ";
						}
						else
						{
							whl_sql=bo.getResumeSql2Modify(fieldList, nbase, codesetid, codeid, resumeState,z0301,queryType,1,encryption_sql);
							suSetSql=bo.getResumeSql2(fieldList, nbase, codesetid, codeid, resumeState,z0301,queryType,0);
						}
				    	
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			if(!"-1".equals(z0301)&&!"-1".equals(resumeState)&&!"-3".equals(resumeState)&&!"-2".equals(resumeState))
			{
				whl_sql+=" and "+nbase+"a01.a0100 in (select distinct a0100 from zp_pos_tache where zp_pos_id='"+z0301+"' and resume_flag='"+resumeState+"')";
				suSetSql+=" and "+nbase+"a01.a0100 in (select distinct a0100 from zp_pos_tache where zp_pos_id='"+z0301+"' and resume_flag='"+resumeState+"')";
			}
			if(!"-1".equals(z0301)&& "-3".equals(resumeState))
			{
				whl_sql+=" and "+nbase+"a01.a0100 in (select distinct a0100 from zp_pos_tache where zp_pos_id='"+z0301+"')";
				suSetSql+=" and "+nbase+"a01.a0100 in (select distinct a0100 from zp_pos_tache where zp_pos_id='"+z0301+"')";
			}
			if(!"0".equals(isSelectedAll)&&!"0".equals(personType)){//收藏夹、应聘简历的全选
				whl_sql+=" and "+nbase+"a01.a0100 in ("+encryption_sql+")";
			}
			if(order_str!=null&&!"".equals(order_str))
			{
				if(order_str.toLowerCase().indexOf("zpt")!=-1)
				{
					//whl_sql+=" left join zp_pos_tache zpt on zpt.a0100="+nbase+"a01.a0100 "+order_str;
					if("-3".equals(resumeState)|| "-2".equals(resumeState))
					{
						String xx=whl_sql.toString();
						String yy=suSetSql.toString();
						if("-3".equals(resumeState))
						{
							whl_sql="select "+nbase+"A01.* from ("+xx+") "+nbase+"a01 left join (select  a.* "+
					    	" from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301) a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 ) ) zpt on zpt.a0100="+nbase+"a01.a0100 ";
							suSetSql="select "+nbase+"A01.* from ("+yy+") "+nbase+"a01 left join (select  a.* "+
					    	" from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301) a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 ) ) zpt on zpt.a0100="+nbase+"a01.a0100 ";

							StringBuffer condSql=new StringBuffer(" where (("+nbase+"a01.a0100 not in (select a0100 from zp_pos_tache)) or "+nbase+"a01.a0100 in (select a0100 ");
							condSql.append(" from zp_pos_tache where zp_pos_id in (select z0301 from z03 z where ");
							if("-0".equals(codeid))
							{
								condSql.append("  z.z0325 like '#' ");
							}
							else if(codeid.indexOf("`")==-1&&!"".equals(codeid))
							{
								condSql.append("   z.z0325 like '"+codeid+"%'  ");
							}
							else if("".equals(codeid))
							{
								condSql.append("  (z.z0325 like '%' or z.z0311 is null or z.z0325 is null)");
							}
							else
							{
								StringBuffer tempsql=new StringBuffer("");
								String[] temp=codeid.split("`");
								for(int i=0;i<temp.length;i++)
								{
									tempsql.append(" or z.Z0325 like '"+temp[i].substring(2)+"%'");
								}
								condSql.append(" ( "+tempsql.substring(3)+" ) ");
							}
							condSql.append(")))");
							whl_sql+=condSql.toString();
							suSetSql+=condSql.toString();
							if(conditionSQL!=null&&conditionSQL.trim().length()>0)
							{
								whl_sql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
								suSetSql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
							}
							whl_sql+=" "+order_str;
							//suSetSql+=" "+order_str;
	
						}else
						{
				        	whl_sql="select "+nbase+"A01.* from ("+xx+") "+nbase+"a01 left join (select  a.*"+
						    	"  from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301) a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 ) ) zpt on zpt.a0100="+nbase+"a01.a0100 ";
				        	
				        	suSetSql="select "+nbase+"A01.* from ("+yy+") "+nbase+"a01 left join (select  a.* "+
					    	"   from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301)  a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 ) ) zpt on zpt.a0100="+nbase+"a01.a0100 ";
				        	StringBuffer condSql=new StringBuffer(" where (("+nbase+"a01.a0100 in (select a0100 ");
							condSql.append(" from zp_pos_tache where zp_pos_id in (select z0301 from z03 z where ");
							if("-0".equals(codeid))
							{
								condSql.append("  z.z0325 like '#' ");
							}
							else if(codeid.indexOf("`")==-1&&!"".equals(codeid))
							{
								condSql.append("   z.z0325 not like '"+codeid+"%'  ");
							}
							else if("".equals(codeid))
							{
								condSql.append("  1=1 ");
							}
							else
							{
								StringBuffer tempsql=new StringBuffer("");
								String[] temp=codeid.split("`");
								for(int i=0;i<temp.length;i++)
								{
									tempsql.append(" and z.Z0325 not like '"+temp[i].substring(2)+"%'");
								}
								condSql.append(" ( "+tempsql.substring(4)+" ) ");
							}
							condSql.append("))))");
							whl_sql+=condSql.toString();
							suSetSql+=condSql.toString();
							if(conditionSQL!=null&&conditionSQL.trim().length()>0)
							{
								whl_sql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
								suSetSql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
							}
							whl_sql+=" "+order_str;
							//suSetSql+=" "+order_str;
						}
					}
					else
					{
				    	String xx=whl_sql.toString();
				    	String yy=suSetSql.toString();
				    	whl_sql="select "+nbase+"A01.* from ("+xx+") "+nbase+"a01 ,(select  a.*"+
							" from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301) a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 and b.resume_flag='"+resumeState+"') ) zpt where zpt.a0100="+nbase+"a01.a0100 ";
						
				    	suSetSql="select "+nbase+"A01.* from ("+yy+") "+nbase+"a01 ,(select  a.* "+
						" from (select a.*,b.* from zp_pos_tache a left join z03 b on a.zp_pos_id=b.z0301) a where a.apply_date=(select max(b.apply_date) from zp_pos_tache b where a.a0100=b.a0100 and b.resume_flag='"+resumeState+"') ) zpt where zpt.a0100="+nbase+"a01.a0100 ";
				    	
				    	if(conditionSQL!=null&&conditionSQL.trim().length()>0)
						{
							whl_sql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
							suSetSql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
						}
						whl_sql+=" "+order_str;
						//suSetSql+=" "+order_str;
					}
				}
				else
				{
					if(conditionSQL!=null&&conditionSQL.trim().length()>0)
					{
						whl_sql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
						suSetSql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
					}
					whl_sql+=" "+order_str;
					//suSetSql+=" "+order_str;
				}
			}
			else
			{
				if(conditionSQL!=null&&conditionSQL.trim().length()>0)
				{
					whl_sql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
					suSetSql+=" and "+nbase+"a01.a0100 in ("+conditionSQL+")";
				}
				
			}
			HashMap map = bo.getSubSetMap(suSetSql, fieldList, nbase);
			executeExcel.setSubSetMap(map);
			executeExcel.getNumberMap();
			outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,fieldList,whl_sql,"2",this.userView);
			outName=outName.replaceAll(".xls","#");
		}
		else
		{
			outName="-1";
		}
		return outName;
	}
	
	
	/**
	 * 人员筛选
	 * @param flag
	 * @param tablename
	 * @param fieldWidths
	 * @return
	 */
	public String getPersonFilterFileName(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		ArrayList fieldList=employActualize.getFieldList();
		ArrayList afieldList=new ArrayList();
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			if("D".equals(item.getItemtype()))
				item.setItemtype("A");
			afieldList.add(item);
		}
		
		
		ArrayList list=employActualize.getTableColumn_headNameList();
		ArrayList tableColumnsList=(ArrayList)list.get(1);
		String codeid=(String)this.getFormHM().get("codeid");
		String extendSql=(String)this.getFormHM().get("extendSql")+" "+(String)this.getFormHM().get("orderSql");
		extendSql=PubFunc.keyWord_reback(extendSql);
		String dbname=employActualize.getZP_DB_NAME();
		String sql=employActualize.getQuerySQL(dbname,tableColumnsList,codeid,extendSql);
		
		String whl_sql=sql;
		Connection con = this.getFrameconn();
		if("1".equals(flag))		//PDF
		{
			ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());			
			outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,afieldList,whl_sql,"2");
		}
		else						//EXCEL
		{
			ExecuteExcel executeExcel=new ExecuteExcel(con);
			outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,afieldList,whl_sql,"2",this.userView);
			outName=outName.replaceAll(".xls","#");
		}
		return outName;
	}
	
//	//招聘总结-职位需求
	public String getEngagePlanFileName2(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		
		try
		{
		String extendWhereSql=(String)this.getFormHM().get("extendSql");
		extendWhereSql = PubFunc.decrypt(extendWhereSql);
		extendWhereSql=PubFunc.keyWord_reback(extendWhereSql);
		String orderSql=(String)this.getFormHM().get("orderSql");
		orderSql = PubFunc.decrypt(orderSql);
		String whl_sql="select * from z03 ";
		int num=0;
		if(extendWhereSql!=null&&extendWhereSql.trim().length()>0)
		{
			whl_sql+=" where "+extendWhereSql;
			num++;
		}
		PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
		{
			
		}
		else
		{
	    	ArrayList unitcodeList=positionDemand.getUnitIDList2(this.userView);
	    	StringBuffer tempSql=new StringBuffer("");
	        for(int i=0;i<unitcodeList.size();i++)
	        	tempSql.append(" or z03.z0311  like '"+(String)unitcodeList.get(i)+"%'");
	        if(tempSql!=null&&tempSql.toString().trim().length()>0)
	    	if(num!=0)
	    		whl_sql+=" and ("+tempSql.substring(3)+")";
	    	else
		     	whl_sql+=" where ("+tempSql.substring(3)+") ";	
		}
		if(orderSql!=null&&orderSql.trim().length()>0)
			whl_sql+=" "+orderSql;
		
		ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
		ArrayList fieldList=new ArrayList();
	
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);				
			if("0".equals(item.getState())|| "M".equals(item.getItemtype())|| "z0303".equalsIgnoreCase(item.getItemid())|| "z0305".equalsIgnoreCase(item.getItemid()))
				continue;
			fieldList.add(item);
			if("z0311".equalsIgnoreCase(item.getItemid()))
			{
				FieldItem a_item=new FieldItem();
				a_item.setItemid("employedcount");
				a_item.setItemdesc("实招人数");
				a_item.setItemtype("N");
				fieldList.add(a_item);
			}
		}
		
		Connection con = this.getFrameconn();
		
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();
		EmploySummarise employSummarise=new EmploySummarise(this.getFrameconn());
		HashMap map=employSummarise.getPosCountMap(dbname);
		
		if("1".equals(flag))		//PDF
		{
			ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());
			executePdf.setPlanCountMap(map);
			outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,fieldList,whl_sql,"2");
		}
		else						//EXCEL
		{
			ExecuteExcel executeExcel=new ExecuteExcel(con);
			executeExcel.setPlanCountMap(map);
			outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,fieldList,whl_sql,"2",this.userView);
			outName=outName.replaceAll(".xls","#");
		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return outName;
	}
	
	
	//招聘计划
	public String getEngagePlanFileName(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		try
		{
			String extendWhereSql=(String)this.getFormHM().get("extendSql");
			extendWhereSql=PubFunc.keyWord_reback(extendWhereSql);
			String orderSql=(String)this.getFormHM().get("orderSql");
			String whl_sql="select * from z01 ";
			boolean isWhere=false;
			if(extendWhereSql!=null&&extendWhereSql.trim().length()>0)
			{
				whl_sql+=" where "+extendWhereSql;
				isWhere=true;
			}
			EmploySummarise employSummarise=new EmploySummarise(this.getFrameconn());
			if(!(userView.isSuper_admin()))
			{
				if(!isWhere)
					whl_sql+=" where ";
				else
					whl_sql+=" and ";
				
				PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
				ArrayList unitcodeList=positionDemand.getUnitIDList2(userView);
				StringBuffer tempSql=new StringBuffer("");
			    for(int i=0;i<unitcodeList.size();i++)
			    	tempSql.append(" or z01.z0105 like '"+(String)unitcodeList.get(i)+"%'");
				
			    whl_sql+="  ("+tempSql.substring(3)+")";
				
				
			}
			
			
			
			if(orderSql!=null&&orderSql.trim().length()>0)
				whl_sql+=" "+orderSql;
			
			ArrayList list=DataDictionary.getFieldList("Z01",Constant.USED_FIELD_SET);
			ArrayList fieldList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				if("z0101".equals(item.getItemid()))
					continue;
				fieldList.add(item);
			}
			Connection con = this.getFrameconn();
			
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();
			
			HashMap map=employSummarise.getPlanCountMap(dbname);
			
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());
				executePdf.setPlanCountMap(map);
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,fieldList,whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con);
				executeExcel.setPlanCountMap(map);
				outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,fieldList,whl_sql,"2",this.userView);
				outName=outName.replaceAll(".xls","#");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return outName;
	}
	
	
	
	/**
	 * 输出计划下已录用的人员信息
	 */
	public String getInterviewExamineFileName2(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		try
		{
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			InterviewExamine interviewExamine=new InterviewExamine(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();  //应用库前缀
			
			String viewType=(String)this.getFormHM().get("viewType");
			String extendWhereSql=(String)this.getFormHM().get("extendSql");
			extendWhereSql=PubFunc.keyWord_reback(extendWhereSql);
			String orderSql=(String)this.getFormHM().get("orderSql");
			String codeid=(String)this.getFormHM().get("codeid");
			String z0101=(String)this.getFormHM().get("z0101");
			String str=(String)this.getFormHM().get("str");
		//	String sql=interviewExamine.getSql(codeid,dbname,extendWhereSql,orderSql,this.getUserView(),"");
			interviewExamine.setStr(str);
			String sql=interviewExamine.getSql(codeid,dbname,extendWhereSql,orderSql,this.getUserView(),z0101,viewType);
				
			ArrayList tableColumnsList=new ArrayList();
			String columns=interviewExamine.getTableColumns(dbname,tableColumnsList,"0");
			
			
			String whl_sql=sql;
			Connection con = this.getFrameconn();
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());	
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,interviewExamine.getFieldList(tableColumnsList),whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con);		
				outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,interviewExamine.getFieldList(tableColumnsList),whl_sql,"2",this.userView);
				outName=outName.replaceAll(".xls","#");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outName;
	}
	
	
	
	
	
	/**
	 * 面试考核
	 */
	public String getInterviewExamineFileName(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		try
		{
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			InterviewExamine interviewExamine=new InterviewExamine(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();  //应用库前缀
			String extendWhereSql = (String)this.userView.getHm().get("hire_sql_extend");//(String)this.getFormHM().get("extendSql");
			//extendWhereSql=PubFunc.keyWord_reback(extendWhereSql);
			String orderSql = (String)this.userView.getHm().get("hire_sql_order");//(String)this.getFormHM().get("orderSql");
			String codeid=(String)this.getFormHM().get("codeid");			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			int advanceFlag=testTemplatAdvance.size();
			interviewExamine.setAdvanceFlag(advanceFlag);
			String columns="";//interviewExamine.getTableColumns(dbname,tableColumnsList,"0");
			ArrayList tableColumnsList=new ArrayList();
			if(testTemplatAdvance.size()>0){
			    ArrayList tempList=interviewExamine.getTableColumnsForAdvance(dbname,tableColumnsList,"0",testTemplatAdvance);//得到要显示的列
			    columns=(String) tempList.get(0);
			    ArrayList itemIdList=(ArrayList) tempList.get(1);
			    interviewExamine.setAdvanceList(itemIdList);
			}else{
			    columns=interviewExamine.getTableColumns(dbname,tableColumnsList,"0");//得到要显示的列
			}
			String sql=interviewExamine.getSql(codeid,dbname,extendWhereSql,orderSql,this.getUserView(),"","");
			
			String whl_sql=sql;
			Connection con = this.getFrameconn();
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());	
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,interviewExamine.getFieldList(tableColumnsList),whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con);		
				outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,interviewExamine.getFieldList(tableColumnsList),whl_sql,"2",this.userView);
				outName=outName.replaceAll(".xls","#");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outName;
	}
	
	
	/**
	 * 人员录用
	 */
	public String getPersonnelEmployFileName(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		try
		{
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();  //应用库前缀
			String extendWhereSql=(String)this.getFormHM().get("extendSql");
			extendWhereSql = PubFunc.decrypt(extendWhereSql);
			extendWhereSql=PubFunc.keyWord_reback(extendWhereSql);
			String orderSql=(String)this.getFormHM().get("orderSql");
			/**orderSql 被加密**/
			orderSql = PubFunc.decrypt(orderSql);
			String codeid=(String)this.getFormHM().get("codeid");
			String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
			String isPhoneField=email_phone.split("/")[1];
			String isMailField=email_phone.split("/")[0];
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			PersonnelEmploy personnelEmploy=new PersonnelEmploy(this.getFrameconn());
			personnelEmploy.setTestTemplatAdvance(testTemplatAdvance);
			ArrayList columnsList=personnelEmploy.getColumnList(isMailField,isPhoneField,dbname,"1");
			String sql=personnelEmploy.getSql2(codeid,columnsList,dbname,extendWhereSql,orderSql);
	
			String whl_sql=sql;
			Connection con = this.getFrameconn();
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());	
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,personnelEmploy.getFieldList(columnsList),whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con);		
				outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,personnelEmploy.getFieldList(columnsList),whl_sql,"2",this.userView);
				outName=outName.replaceAll(".xls","#");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outName;
	}
	
	
	/**
	 * 面试安排信息表
	 * @return
	 */
	public String getInterviewArrangeFileName(String flag,String tablename,String fieldWidths)
	{
		String outName="";
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();  //应用库前缀
		
		InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		HashMap employerNameMap=interviewEvaluatingBo.getEmployerNameMap("Usr");
		String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
		String isPhoneField=email_phone.split("/")[1];
		String isMailField=email_phone.split("/")[0];
		interviewEvaluatingBo.setAddProfessionalColumnName(true);
		ArrayList fieldList=interviewEvaluatingBo.getColumnList(DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET),isMailField,isPhoneField,dbname);;
		fieldList.remove(fieldList.size()-1);
		String extendWhereSql=(String)this.getFormHM().get("extendSql");
		extendWhereSql=PubFunc.decrypt(extendWhereSql);
		String orderSql=(String)this.getFormHM().get("orderSql");
		orderSql = PubFunc.decrypt(orderSql);
		String codeid=(String)this.getFormHM().get("codeid");
		String sql=interviewEvaluatingBo.getInterviewArrangeInfoSQL(codeid,dbname,isMailField,isPhoneField,DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET),extendWhereSql,orderSql,2,this.userView);

		String whl_sql=sql;
		Connection con = this.getFrameconn();
		if("1".equals(flag))		//PDF
		{
			ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView().getUserName());
			executePdf.setEmployerNameMap(employerNameMap);
			
			outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,interviewEvaluatingBo.getFieldList(fieldList),whl_sql,"2");
		}
		else						//EXCEL
		{
			ExecuteExcel executeExcel=new ExecuteExcel(con);
			executeExcel.setEmployerNameMap(employerNameMap);
			outName=executeExcel.createExcel(this.getUserView().getUserName(),tablename,interviewEvaluatingBo.getFieldList(fieldList),whl_sql,"2",this.userView);
			outName=outName.replaceAll(".xls","#");
		}
		return outName;
	}
	
	
	//////////////////////////////////////////////     培训班   ////////////////////////////////////////////////////////
	
	
	

	// 培训班
	public String getTrainPlanFileName(String flag,String tablename,String fieldWidths) throws GeneralException
	{
		String outName="";
		try
		{
			String whl_sql=(String) this.userView.getHm().get("train_sql");
			whl_sql = SafeCode.decode(whl_sql);
			whl_sql=PubFunc.keyWord_reback(whl_sql);
			TrParamXmlBo trParamXmlBo=new TrParamXmlBo(this.getFrameconn());
			HashMap para_map=trParamXmlBo.getAttributeValues();
			String  fieldStr=(String)para_map.get("plan_mx");       //常量表  参数TR_PARAM (  R3121,R3124,R3125)
			
			ArrayList list=DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
			ArrayList fieldList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				
                if (fieldStr != null && fieldStr.length() > 0)
				{
					if(fieldStr.toLowerCase().indexOf(item.getItemid().toLowerCase())==-1)
						continue;
				}
				if(whl_sql.length()>0)
				{
					if(whl_sql.toLowerCase().indexOf(item.getItemid().toLowerCase())==-1)
						continue;
				}
				if("r3125".equalsIgnoreCase(item.getItemid())&&whl_sql.indexOf("trainplan")!=-1){
					FieldItem field1= new FieldItem();
					field1.setItemid("trainplan");
					field1.setItemdesc(item.getItemdesc());
					field1.setItemtype(item.getItemtype());
					field1.setItemlength(item.getItemlength());
					field1.setCodesetid("0");
					field1.setVisible(true);
					field1.setDisplaywidth(item.getDisplaywidth());
					fieldList.add(field1);
				}else
					fieldList.add(item);
			}
			Connection con = this.getFrameconn();
			
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView());			
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,fieldList,whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con,this.getUserView(),tablename);
				outName=executeExcel.createExcel(fieldList,whl_sql,"2");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return outName;
	}
	

	//////////////////////////////////////////////     培训计划   ////////////////////////////////////////////////////////
	
	
	

	// 培训计划
	public String getTrainPlan(String flag,String tablename,String fieldWidths) throws GeneralException
	{
		String outName="";
		try
		{
			String whl_sql=(String)this.userView.getHm().get("train_sql");
			whl_sql = SafeCode.decode(whl_sql);
			whl_sql=PubFunc.keyWord_reback(whl_sql);
			String model=(String)this.getFormHM().get("model");
			model=model!=null&&model.length()>0?model:"0";
			PlanTransBo planbo = new PlanTransBo(this.getFrameconn(),model);
			ArrayList fieldList=planbo.itemPDFList();
			Connection con = this.getFrameconn();
			if("1".equals(flag))		//PDF
			{
				ExecutePdf executePdf=new ExecutePdf(con,tablename,this.getUserView());			
				outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,tablename,fieldList,whl_sql,"2");
			}
			else						//EXCEL
			{
				ExecuteExcel executeExcel=new ExecuteExcel(con,this.getUserView(),tablename);
				outName=executeExcel.createExcel(fieldList,whl_sql,"2");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return outName;
	}
	
}
