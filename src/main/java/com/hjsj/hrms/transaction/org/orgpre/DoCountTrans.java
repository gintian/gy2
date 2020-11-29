package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class DoCountTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String pos_set="";
		String unit_set="";
			RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.frameconn);
			
			if(ps_workout_vo!=null)
			{
			  String  ps_workout=ps_workout_vo.getString("str_value");
			  ps_workout=ps_workout!=null?ps_workout:"";
			  if(ps_workout.length()>0){
				  String strs[]=ps_workout.split("\\|");//K01|K0114,K0111
				  pos_set=strs[0];
			  }
			}
			PosparameXML pos = new PosparameXML(this.frameconn);
			unit_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
		
		String getyear = (String)hm.get("yearnum");
		String getmonth = (String)hm.get("monthnum");
		getmonth=getmonth!=null&&getmonth.length()>0?getmonth:"0";
		
		String fieldstr = (String)reqhm.get("included");
		fieldstr = fieldstr!=null && fieldstr.length()>0?fieldstr:"";
		reqhm.remove("included");
		//String view_scan = (String)hm.get("view_scan");
		//view_scan=view_scan!=null?view_scan:"User,";
		String view_scan = pos.getNodeAttributeValue("/params/view_scan", "orgpre");
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
		
		String changeflag = "1";
		String count =(String)reqhm.get("count");
		count=count!=null?count:"";
		reqhm.remove("count");
		
		String a_code = (String)reqhm.get("a_code");//当前机构树选中节点
		a_code=a_code!=null&&a_code.length()>1?a_code:"UN";
/*		if(a_code.trim().length()>=2){
			a_code=a_code.replace("UN","");
			a_code=a_code.replace("UM","");
		}*/
		
		if(!"".equals(pos_set)){
			FieldSet fs = DataDictionary.getFieldSetVo(pos_set);
			if(fs==null)
				return;
			changeflag = fs.getChangeflag();
			changeflag= "".equals(changeflag)?"0":changeflag;
			if(count.length()>1)
			{
				DataCondBo databo = new DataCondBo(this.userView,this.frameconn,pos_set,
						view_scan,getyear,getmonth,changeflag);
				if(!"".equals(unit_set)){
					FieldSet fset = DataDictionary.getFieldSetVo(unit_set);
					if(fset==null)
						return;
					changeflag = fset.getChangeflag();
					changeflag= "".equals(changeflag)?"0":changeflag;
					databo.resetDate(changeflag, unit_set,"0");
				}
				databo.runCond("0",a_code);
			}
		}
		if(!"".equals(unit_set)){
			FieldSet fs = DataDictionary.getFieldSetVo(unit_set);
			if(fs==null)
				return;
			changeflag = fs.getChangeflag();
			changeflag= "".equals(changeflag)?"0":changeflag;
			
			DataSynchroBo dsbo = new DataSynchroBo(this.userView, unit_set, dao, view_scan.substring(0,view_scan.length()-1), getyear, getmonth, changeflag); 
			
			if("0".equals(changeflag)){
				if(count.length()>1)
				{
					DataCondBo databo = new DataCondBo(this.userView,this.frameconn,unit_set,
							view_scan,getyear,getmonth,changeflag);
					databo.runCond1(pos_set,"0",a_code);
				}
			}else{
				int num=0;
				if(fieldstr.trim().length()>0&&!"0".equals(changeflag)){
					num = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
				}
				if(count.length()>1)
				{
					DataCondBo databo = new DataCondBo(this.userView,this.frameconn,unit_set,
							view_scan,getyear,getmonth,changeflag);
					databo.runCond1(pos_set,"0",a_code);
				}
				int num2=0;
				if(fieldstr.trim().length()>0&&!"0".equals(changeflag)){
					num2 = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
				}
				if(num2!=num){
					if(count.length()>1)
					{
						DataCondBo databo = new DataCondBo(this.userView,this.frameconn,unit_set,
								view_scan,getyear,getmonth,changeflag);
						databo.runCond1(pos_set,"0",a_code);
					}
				}
			}
		}
		//如果设置岗位占编
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		/**兼职参数*/
		String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
		//兼职岗位占编 1：占编	
		String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");	
		if("true".equals(flag)&&"1".equals(takeup_quota)){
			RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",this.getFrameconn());
			if(ps_workparttime_vo!=null){
	    		 String ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
	    		 if(ps_workparttime.length()==5){
	    			 	String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");//兼职子集
	    				/**兼职单位字段*/
	    				//String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");	
	    				//兼职部门
	    				//String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");	
	    				//兼任兼职
	    				String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
	    				/**任免标识字段*/
	    				String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
	    				String[] tmpdbs=view_scan.split(",");
	    				StringBuffer sqlstr=new StringBuffer();
						sqlstr.append("update "+pos_set+" set "+ps_workparttime+"=0 where ");
						FieldSet fs = DataDictionary.getFieldSetVo(pos_set);
						changeflag = fs.getChangeflag();
						changeflag= "".equals(changeflag)?"0":changeflag;
						if(!"0".equals(changeflag)){
							sqlstr.append(" Id='");
							sqlstr.append(getId(getyear,getmonth,changeflag));
							sqlstr.append("'");
							sqlstr.append(" and I9999=(select max(I9999) from ");
							sqlstr.append(pos_set);
							sqlstr.append(" a where a.e01a1=");
							sqlstr.append(pos_set);
							sqlstr.append(".e01a1 and Id='");
							sqlstr.append(getId(getyear,getmonth,changeflag));
							sqlstr.append("')");
						}else{
							sqlstr.append("I9999=(select max(I9999) from ");
	    					sqlstr.append(pos_set);
	    					sqlstr.append(" a where a.e01a1=");
	    					sqlstr.append(pos_set);
	    					sqlstr.append(".e01a1)");
						}
						try {
							dao.update(sqlstr.toString());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    				for(int i=0;i<tmpdbs.length;i++){
	    					String tmpdb=tmpdbs[i];
	    					if(tmpdb.length()==3){
								sqlstr.setLength(0);
	    						sqlstr.append("update "+pos_set+" set "+ps_workparttime+"="+ps_workparttime+"+(select count(distinct "+tmpdb+"A01.A0100) from "+tmpdb+"A01 left join "+tmpdb+setid+" on "+tmpdb+"A01.a0100="+tmpdb+setid+".a0100 where "+pos_field+" like "+pos_set+".e01a1"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and "+appoint_field+"='0') where ");
	    						if(!"0".equals(changeflag)){
	    							sqlstr.append(" Id='");
	    							sqlstr.append(getId(getyear,getmonth,changeflag));
	    							sqlstr.append("'");
	    							sqlstr.append(" and I9999=(select max(I9999) from ");
	    							sqlstr.append(pos_set);
	    							sqlstr.append(" a where a.e01a1=");
	    							sqlstr.append(pos_set);
	    							sqlstr.append(".e01a1 and Id='");
	    							sqlstr.append(getId(getyear,getmonth,changeflag));
	    							sqlstr.append("')");
	    						}else{
	    							sqlstr.append("I9999=(select max(I9999) from ");
	    	    					sqlstr.append(pos_set);
	    	    					sqlstr.append(" a where a.e01a1=");
	    	    					sqlstr.append(pos_set);
	    	    					sqlstr.append(".e01a1)");
	    						}
	    						sqlstr.append(this.getPriv("e01a1"));
	    						try {
									dao.update(sqlstr.toString());
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	    					}
	    				}
	    		 }
			}
		}
		
		//统计每个单位下的兼职人员
		String staticparttimejob = com.hrms.struts.constant.SystemConfig.getPropertyValue("staticparttimejob");
		if("staticparttimejob".equals(staticparttimejob)){
			String sql = "{ call staticparttimejob(?,?) }";
			CallableStatement cs = null;
			try{
				if(!view_scan.endsWith(",")){
					view_scan+=",";
				}
					
				if(view_scan.length()<5){
					view_scan="usr";
				}
			    cs = this.frameconn.prepareCall(sql);//通过它来执行sql
			    cs.setString(1,view_scan);//指出输入参数
			    cs.setString(2,"%");//指出输入参数
			    cs.execute();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(cs!=null)
					try {
						cs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
		}
	
	}

	private String getId(String year,String month,String changeflag){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9)
				return year+"."+month;
			else
				return year+".0"+month;
		}
	}
	
	private String getPriv(String itemid){
		StringBuffer sql=new StringBuffer();
		String orgunit=this.userView.getUnit_id();
		orgunit=orgunit.toUpperCase();
		sql.append(" and (");
		if(!"UN".equals(orgunit)){
			String str[]=orgunit.split("`");
			for(int i=0;i<str.length;i++){
				if(str[i].indexOf("UN")!=-1){
					sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
				}else if(str[i].indexOf("UM")!=-1){
					sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
				}else{
					continue;
				}
			}
		}
		sql.append("1=2)");
		
		return sql.toString();
	}
}
