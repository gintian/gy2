package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.orgpre.OrgPreBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class PosTableTrans extends IBusiness {

	private Calendar c= Calendar.getInstance();
	private String year =String.valueOf(c.get(Calendar.YEAR));
	private String month =String.valueOf(c.get(Calendar.MONTH)+1);
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.getFrameconn());
		String  ps_workout=ps_workout_vo.getString("str_value");
		StringTokenizer str=new StringTokenizer(ps_workout,"|");//K01|K0114,K0111

		String ps_parttime="0",ps_workparttime="";
		//如果设置岗位占编
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		/**兼职参数*/
		String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
		//兼职岗位占编 1：占编	
		String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");	
		if("true".equals(flag)&&"1".equals(takeup_quota)){
			RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",this.getFrameconn());
			if(ps_workparttime_vo!=null){
	    		 ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
	    		 if(ps_workparttime.length()==5){
	    			 ps_parttime="1";
	    		 }
			}
		}
		
		String setid="";  
		String planitem=""; 
		String realitem="";
		
		if(str.hasMoreTokens()){
			setid = str.nextToken().toUpperCase();
			 if(str.hasMoreTokens()){
		    	 StringTokenizer strfield=new StringTokenizer(str.nextToken(),",");
		    	 planitem=strfield.nextToken().toUpperCase();
		    	 realitem=strfield.nextToken().toUpperCase();
		      }
		}else{
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.job.parameters.notset")+"!"));
		}
		
		String a_code = (String)hm.get("a_code");
		hm.remove("a_code");
		
		String b0110 = (String)hm.get("b0110");
		
		if(!b0110.equals(new CheckPrivSafeBo(frameconn, userView).checkOrg(b0110, "4")))
			throw GeneralExceptionHandler.Handle(new Exception("没有权限!"));
		
		hm.remove("b0110");
		insertMainSet(b0110);
		
		String infor = (String)hm.get("infor");
		hm.remove("infor");
		
		String nextlevel = (String)hm.get("nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		hm.remove("nextlevel");
		
		String unit_type = (String)hm.get("unit_type");
		hm.remove("unit_type");
		OrgPreBo orgprebo = new OrgPreBo(this.frameconn,this.userView);
		String dpname="";
		String codesetid="UN";
		if(a_code.indexOf("UN")!=-1)
			codesetid="UN";
		else if(a_code.indexOf("UM")!=-1)
			codesetid="UM";
		else if(a_code.indexOf("@K")!=-1)
			codesetid="K";
		dpname=AdminCode.getCodeName(b0110,codesetid);
		if(dpname==null||dpname.trim().length()<1)
			dpname=codeName(codesetid,b0110);
		dpname=dpname!=null&&dpname.trim().length()>0?dpname:orgprebo.orgCodeName(b0110);
		
		FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
		
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("(select parentid from organization where codeitemid=K01.E0122 group by parentid) as B0110,");
		sql.append("K01.E0122,");
		sql.append("K01.E01A1,");
		sql.append(planitem+",");
		sql.append(realitem);
		if("1".equals(ps_parttime)){
			sql.append(","+ps_workparttime);
		}

		StringBuffer wherestr = new StringBuffer();
		wherestr.append("from ");
		wherestr.append(setid);
		
		if(!fieldset.isMainset()){
			wherestr.append(" a ");
			wherestr.append("right join K01");
			wherestr.append(" on ");
			wherestr.append("K01.E01A1=");
			wherestr.append("a.E01A1 ");
		}
		wherestr.append(" where ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate =sdf.format(new Date());
		wherestr.append("K01.E01A1 in(select codeitemid from organization where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)");
		wherestr.append(" and ");
		if(!fieldset.isMainset())
			wherestr.append("K01.");
		wherestr.append("E01A1 like '");
		wherestr.append(b0110);
		wherestr.append("%'");
		//添加操作单位过滤
		String unit_id=this.userView.getUnit_id();
		String nunitarr[] = unit_id.split("`"); 
		if(nunitarr.length>0&&nunitarr[0].length()>2){
			wherestr.append(" and (1=2 ");
		}
        for(int i=0;i<nunitarr.length;i++){
        	String codeid = nunitarr[i];
        	if(codeid!=null&&codeid.trim().length()>2){
        		wherestr.append(" or ");
        		if(!fieldset.isMainset())
        			wherestr.append("K01.");
        		wherestr.append("E01A1 like '");
        		wherestr.append(codeid.substring(2));
        		wherestr.append("%'");
        	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
        	}
        }
        if(nunitarr.length>0&&nunitarr[0].length()>2){
			wherestr.append(")");
		}
        if(!fieldset.isMainset()){
	        if("0".equals(fieldset.getChangeflag())){
	        	wherestr.append(" and i9999=(select max(i9999) from "+setid+" k where k.e01a1=a.e01a1)");
	        }else{
	        	wherestr.append(" and Id=");
	        	//wherestr.append("'"+getId(fieldset.getChangeflag())+"'");
	        	wherestr.append("(select max(id) from "+setid+" k1 where k1.e01a1=a.e01a1)");
	        	wherestr.append(" and I9999=(select max(I9999) from ");
	        	wherestr.append(setid);
	        	wherestr.append(" k where k.e01a1=");
	        	wherestr.append("a.e01a1 and Id=");
				//wherestr.append("'"+getId(fieldset.getChangeflag())+"'");
	        	wherestr.append("(select max(id) from "+setid+" k2 where k2.e01a1=a.e01a1)");
				wherestr.append(")");
	        }
        }
        StringBuffer columns = new StringBuffer();
		columns.append("B0110,E0122,E01A1,");
		columns.append(planitem+",");
		columns.append(realitem);
		if("1".equals(ps_parttime)){
			columns.append(","+ps_workparttime);
		}
		
		StringBuffer orderby = new StringBuffer();
		if(!fieldset.isMainset())
			orderby.append("order by k01.e01a1,I9999");
		
		ArrayList fieldlist = new ArrayList();
		
		fieldlist.add(DataDictionary.getFieldItem("b0110"));
		fieldlist.add(DataDictionary.getFieldItem("e0122"));
		fieldlist.add(DataDictionary.getFieldItem("e01a1"));
		
		this.getFormHM().put("setid",setid);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("planitem",planitem.toLowerCase());
		this.getFormHM().put("realitem",realitem.toLowerCase());
		this.getFormHM().put("ps_parttime", ps_parttime);
		this.getFormHM().put("ps_workparttime", ps_workparttime.toLowerCase());
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",columns.toString());
		this.getFormHM().put("orderby",orderby.toString());
		this.getFormHM().put("dpname",dpname);
		this.getFormHM().put("nextlevel",nextlevel);
		this.getFormHM().put("fieldlist", fieldlist);
		String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		display_e0122=display_e0122!=null&&!"".equals(display_e0122)?display_e0122:"0";
		this.getFormHM().put("level", display_e0122);
	}
	private String codeName(String codeid,String codeitem){
		String codename="";
		String sql = "select codeitemdesc from organization where codeitemid='"+codeitem+"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				codename=this.frowset.getString("codeitemdesc");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return codename;
	}
	
	private void insertMainSet(String b0110){
		String sql = "select e01a1,codeitemid,parentid from organization left join k01 on codeitemid=e01a1 where codeitemid like'"+b0110+"%' and codesetid='@K'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(calendar.getTime());
		try {
			this.frowset = dao.search(sql);
			String e01a1=null;
			String codeitemid="";
			String parentid="";
			while(this.frowset.next()){
				e01a1=this.frowset.getString("e01a1");
				if(e01a1==null|| "".equals(e01a1)){
					codeitemid=this.frowset.getString("codeitemid");
					parentid=this.frowset.getString("parentid");
					sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+parentid+"','"+codeitemid+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
					dao.update(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getId(String changeflag){
		
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9)
				return year+"."+month;
			else
				return year+".0"+month;
		}
	}
}
