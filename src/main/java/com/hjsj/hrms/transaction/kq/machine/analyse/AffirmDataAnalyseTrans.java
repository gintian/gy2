package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataAnalyseUtils;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.CountMoInfo;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 提交考勤数据处理
 *<p>Title:SubmitDataAnalyseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 29, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class AffirmDataAnalyseTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	String temp_Table=(String)this.getFormHM().get("analyseTempTab");    	
    	
		String dataUpdateType="0";		
		String analyseType="1";
		/**取得权限**/
		/**数据处理模式 mark=1为集中处理，0为分用户**/
		String mark= KqParam.getInstance().getData_processing();
		mark=mark!=null&&mark.length()>0?mark:"0";
		if("1".equalsIgnoreCase(mark))
		{
			analyseType="101";
		}else
		{
			analyseType="1";
		}
		String a_code=(String)this.getFormHM().get("a_code");
		String nbase=(String)this.getFormHM().get("nbase");
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		if(a_code==null||a_code.length()<=0)
	    {
	    	   a_code="UN";
	    }
		String kind="2";
		if(a_code.indexOf("UN")!=-1)
		{
			kind="2";
		}else if(a_code.indexOf("UM")!=-1)
		{
			kind="1";
		}else if(a_code.indexOf("@K")!=-1)
		{
			kind="0";
		}else if(a_code.indexOf("EP")!=-1)
		{
			kind="-1";
		}
		String code="";
		if(a_code.length()>2)
		{
			code=a_code.substring(2);
		}
		if("-1".equals(kind))
		{
			code=nbase+code;
		}
		
		KqParameter para=new KqParameter(this.userView,code,this.getFrameconn());
	    HashMap hashmap =para.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String kq_cardno=(String)hashmap.get("cardno");
		String kq_Gno=(String)hashmap.get("g_no");
		
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
    	
		DataAnalyseUtils dataAnalyseUtils=new DataAnalyseUtils(this.getFrameconn(),this.userView);;
		HashMap kqItem_hash=dataAnalyseUtils.count_Leave();
		
		DataProcedureAnalyse dataProcedureAnalyse=new DataProcedureAnalyse(this.getFrameconn(),this.userView,analyseType,kq_type,kq_cardno,kq_Gno,dataUpdateType,kq_dbase_list);
		dataProcedureAnalyse.setPick_flag("1");
		if(analyseType!=null&& "101".equals(analyseType))
		{
			ReconstructionKqField reconstructionKqField=new ReconstructionKqField(this.getFrameconn());   
			String tablename=dataProcedureAnalyse.getFAnalyseTempTab();
				
			if(!reconstructionKqField.checkFieldSave(tablename,"cur_user"))
			{
				DbWizard dbw=new DbWizard(this.getFrameconn());
		    	DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());    	
				Table table=new Table(tablename);	
				Field field=new Field("cur_user","当前操作人员");//当前操作人员
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				field.setKeyable(false);			
				field.setVisible(false);
				table.addField(field);				
				dbw.addColumns(table);					
				dbmodel.reloadTableModel(tablename);	
			}
		}
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		UpdateQ33 updateQ33 = new UpdateQ33(this.userView,this.getFrameconn());
		String overtime_for_leavetime = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
		boolean countOverforleave = false;
		if (dbWizard.isExistTable("Q33", false) && !"".equals(overtime_for_leavetime)) {
			countOverforleave = true;
		}
		
		boolean hasTheCount = (userView.hasTheFunction("0C3121") || userView.hasTheFunction("2702021")) ? true : false;
		boolean hasTheCollect = (userView.hasTheFunction("0C3122") || userView.hasTheFunction("2702022")) ? true : false;
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET); 
		ArrayList kqItemList = new ArrayList();
		ArrayList columnlist= new ArrayList();
		if (hasTheCount) {
			for(int i=0;i<fielditemlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				if(!("i9999").equalsIgnoreCase(fielditem.getItemid()))
					columnlist.add(fielditem);
			}
			kqItemList = dataProcedureAnalyse.getTargetList();
		}
		String statcolumnstr="";
		String insertcolumnstr="";	  
    	if (hasTheCollect) {
    		StringBuffer statcolumn=new StringBuffer();
    		StringBuffer insertcolumn=new StringBuffer();
    		StringBuffer un_statcolumn=new StringBuffer();
    		StringBuffer un_insertcolumn=new StringBuffer();
    		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
    		//首钢 上岛标识 不在考勤规则里，但是月统计还需要计算进来；这里过滤一下
    		if("".equals(sdao_count_field)||sdao_count_field.length()<0)
    		{
    			for(int i=0;i<fielditemlist.size();i++){
    				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
    				if("N".equals(fielditem.getItemtype()))
    				{
    					if(!"i9999".equals(fielditem.getItemid()))
    					{
    						int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
    						
    						if(want_sum==1)
    						{
    							statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    							insertcolumn.append(""+fielditem.getItemid()+",");
    						}
    						un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    						un_insertcolumn.append(""+fielditem.getItemid()+",");
    					}
    				}	
    			}
    			
    		}else
    		{
    			if(dbWizard.isExistField("Q03",sdao_count_field.toLowerCase(),false))
    			{
    				for(int i=0;i<fielditemlist.size();i++){
    					FieldItem fielditem=(FieldItem)fielditemlist.get(i);
    					if("N".equals(fielditem.getItemtype()))
    					{
    						
    						if(!"i9999".equals(fielditem.getItemid()))
    						{
    							int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
    							
    							if(want_sum==1||sdao_count_field.equalsIgnoreCase(fielditem.getItemid()))
    							{
    								statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    								insertcolumn.append(""+fielditem.getItemid()+",");
    								
    							}
    							un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    							un_insertcolumn.append(""+fielditem.getItemid()+",");
    						}
    					}
    				}
    			}else
    			{
    				for(int i=0;i<fielditemlist.size();i++){
    					FieldItem fielditem=(FieldItem)fielditemlist.get(i);
    					if("N".equals(fielditem.getItemtype()))
    					{
    						if(!"i9999".equals(fielditem.getItemid()))
    						{
    							int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
    							
    							if(want_sum==1)
    							{
    								statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    								insertcolumn.append(""+fielditem.getItemid()+",");
    							}
    							un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
    							un_insertcolumn.append(""+fielditem.getItemid()+",");
    						}
    					}	
    				}
    			}
    		}
    		if(statcolumn.toString()!=null&statcolumn.toString().length()>0)
    		{
    			int l=statcolumn.toString().length()-1;
    			statcolumnstr=statcolumn.toString().substring(0,l);
    			l=insertcolumn.toString().length()-1;	  
    			insertcolumnstr=insertcolumn.toString().substring(0,l);	
    		}else
    		{
    			int l=un_statcolumn.toString().length()-1;
    			statcolumnstr=un_statcolumn.toString().substring(0,l);
    			l=un_insertcolumn.toString().length()-1;		  
    			insertcolumnstr=un_insertcolumn.toString().substring(0,l);
    		}
		}
        String kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
    	String kq_period = CollectRegister.getMonthRegisterDate(start_date,end_date);
 	    String mainindex = dataProcedureAnalyse.getmainsql();
	    String mainindex1 = dataProcedureAnalyse.getmainsql2();
	    ArrayList dateList = RegisterDate.getKqDayList(frameconn);
	    String startDate = (String) dateList.get(0);
	    String endDate = (String) dateList.get(1);
	   
		if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
		{
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				nbase = (String)kq_dbase_list.get(i);
				String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);		
				dataProcedureAnalyse.updateDataToQ03(temp_Table,kqItem_hash,start_date,end_date,nbase,whereIN,code,kind);
				
				//对当前确认人员进行月汇总
				if (hasTheCollect) {
					dataProcedureAnalyse.collectRegisterDataByAnalyse(nbase, startDate, endDate, 
							kq_type, kq_duration, kq_period, mainindex, mainindex1, fielditemlist, statcolumnstr, insertcolumnstr, whereIN, code, kind);
				}
			    
				if (countOverforleave) 
				{
					updateQ33.updateQ33(start_date + "`" + end_date, nbase,temp_Table);
				}
			}
		}
		
		if(hasTheCollect){
		    KqUtilsClass.setIncludeA01ForLeadingInItem(false);
			kqUtilsClass.leadingInItemToQ05(kq_dbase_list,startDate,endDate,"","",kq_duration);//加入导入项
			
			//对月汇总进行计算
			CountMoInfo countMoInfo = new CountMoInfo(this.userView,this.getFrameconn());
			if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
			{
				for(int i=0;i<kq_dbase_list.size();i++)
				{
					nbase = (String)kq_dbase_list.get(i);
					String whereIN = RegisterInitInfoData.getWhereINSql(userView,nbase);	
					countMoInfo.oneCountKQ(kq_duration,whereIN,nbase,code,kind,kqItemList,columnlist);	
				}
			}
		}
		
		// 更新日期类型
		// dataProcedureAnalyse.updateDateType();
	}
}
