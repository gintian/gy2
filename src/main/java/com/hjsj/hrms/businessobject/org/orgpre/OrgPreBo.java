package com.hjsj.hrms.businessobject.org.orgpre;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class OrgPreBo {
	private Connection conn;
	private UserView userView;
	public OrgPreBo(Connection conn,UserView userView){
		this.conn=conn;
		this.userView=userView;
	}
	public OrgPreBo(Connection conn){
		this.conn=conn;
	}
	/**
	 * 当前单位或部门的计划人数跟上级计划人数对比
	 * @param a_code //当前单位
	 * @param planitem
	 * @param b0110
	 * @param updateValue
	 * @return
	 */
	public boolean planUpPerson(String parentid,String planitem,String b0110,int updateValue){
		boolean check=true;
		parentid=parentid!=null?parentid:"";
		b0110=b0110!=null?b0110:"";
		float planperson =planpersonValue(parentid,planitem,b0110,updateValue);
		float upperson = uppersonValue(parentid,planitem);
		if(!parentid.equalsIgnoreCase(b0110)){
			if(planperson>upperson){
				check=false;
			}
		}
		return check;
	}
	
	/**
	 * 当前单位或部门的计划人数跟上级计划人数对比
	 * @param a_code //当前单位
	 * @param planitem
	 * @param b0110
	 * @param updateValue
	 * @return
	 */
	public boolean planUpPerson(String parentid,String planitem,String b0110,int updateValue,String B0110str){
		boolean check=true;
		parentid=parentid!=null?parentid:"";
		b0110=b0110!=null?b0110:"";
		float planperson =planpersonValue(parentid,planitem,b0110,updateValue,B0110str);
		float upperson = uppersonValue(parentid,planitem);
		if(!parentid.equalsIgnoreCase(b0110)){
			if(planperson>upperson){
				check=false;
			}
		}
		return check;
	}
	public float uppersonValue(String parentid,String planitem){
		float upperson=0;
		FieldItem fielditem = DataDictionary.getFieldItem(planitem);

		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buf = new StringBuffer();
		if(fielditem.isMainSet()){
			buf.append("select "+planitem+" as upperson from "+fielditem.getFieldsetid()+" where B0110='"+parentid+"'");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					upperson = rs.getFloat("upperson");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			buf.append("select "+planitem+" as upperson from "+fielditem.getFieldsetid()+" where B0110='");
			buf.append(parentid+"'");
			buf.append(" and I9999=(select max(I9999) from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110='"+parentid+"')");
			
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					upperson = rs.getFloat("upperson");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return upperson;
	}
	public float planpersonValue(String parentid,String planitem,String b0110,int updateValue){
		float planperson=0;
		b0110=b0110.replace(",","','");
		FieldItem fielditem = DataDictionary.getFieldItem(planitem);
		
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buf = new StringBuffer();
		if(fielditem.isMainSet()){
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110 in(select codeitemid from organization where parentid='");
			buf.append(parentid);
			buf.append("') and B0110 not in('");
			buf.append(b0110);
			buf.append("') and B0110<>'");
			buf.append(parentid);
			buf.append("'");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					planperson = rs.getFloat("planperson")+updateValue;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" a where B0110 in(select codeitemid from organization where parentid='");
			buf.append(parentid);
			buf.append("') and B0110 not in('");
			buf.append(b0110);
			buf.append("') and B0110<>'");
			buf.append(parentid);
			buf.append("' and I9999=(select max(I9999) from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110=a.B0110) and B0110 in(select codeitemid from organization)");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					planperson = rs.getFloat("planperson")+updateValue;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return planperson;
	}
	public float planpersonValue(String parentid,String planitem,String b0110,int updateValue,String b0110str){
		float planperson=0;
		b0110=b0110.replace(",","','");
		FieldItem fielditem = DataDictionary.getFieldItem(planitem);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buf = new StringBuffer();
		if(fielditem.isMainSet()){
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110 in(select codeitemid from organization where parentid='");
			buf.append(parentid);
			buf.append("') and B0110 not in('");
			buf.append(b0110);
			buf.append("') and B0110<>'");
			buf.append(parentid);
			buf.append("' and B0110 not in("+b0110str+")");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					planperson = rs.getFloat("planperson")+updateValue;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" a where B0110 in(select codeitemid from organization where parentid='");
			buf.append(parentid);
			buf.append("') and B0110 not in('");
			buf.append(b0110);
			buf.append("') and B0110<>'");
			buf.append(parentid);
			buf.append("' and I9999=(select max(I9999) from ");
			buf.append(fielditem.getFieldsetid());
			FieldSet fieldset = DataDictionary.getFieldSetVo(fielditem.getFieldsetid());
			buf.append(" where B0110=a.B0110");
			if(!"0".equals(fieldset.getChangeflag())){
				buf.append(" and "+fieldset.getFieldsetid()+"z0=(select max("+fieldset.getFieldsetid()+"z0) from "+fieldset.getFieldsetid()+" where B0110=a.B0110))");
			}else {
				buf.append(") and B0110 in(select codeitemid from organization)");
			}
			buf.append(" and B0110 not in("+b0110str+")");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					planperson = rs.getFloat("planperson")+updateValue;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return planperson;
	}
	public boolean planDownPerson(String planitem,String b0110,int updateValue){
		boolean check=true;
		FieldItem fielditem = DataDictionary.getFieldItem(planitem);
		
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buf = new StringBuffer();
		if(fielditem.isMainSet()){
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110 in(select codeitemid from organization where parentid='");
			buf.append(b0110);
			buf.append("'");
			buf.append(" and "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))+" between start_date and end_date ");
			buf.append(") and B0110<>'");
			buf.append(b0110);
			buf.append("'");
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					float planperson = rs.getFloat("planperson");
					if(planperson>updateValue){
						check=false;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			buf.append("select sum("+planitem+") as planperson");
			buf.append(" from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" a where B0110 in(select codeitemid from organization where parentid='");
			buf.append(b0110);
			buf.append("'");
			buf.append(" and "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))+" between start_date and end_date ");
			buf.append(") and B0110<>'");
			buf.append(b0110);
			buf.append("' and I9999=(select max(I9999) from ");
			buf.append(fielditem.getFieldsetid());
			buf.append(" where B0110=a.B0110");
			FieldSet fieldset = DataDictionary.getFieldSetVo(fielditem.getFieldsetid());
			if(!"0".equals(fieldset.getChangeflag())){
				buf.append(" and "+fieldset.getFieldsetid()+"z0=(select max("+fieldset.getFieldsetid()+"z0) from "+fieldset.getFieldsetid()+" where B0110=a.B0110))");
			}else{
				buf.append(")");
			}
			try {
				RowSet rs = dao.search(buf.toString());
				while(rs.next()){
					float planperson = rs.getFloat("planperson");
					if(planperson>updateValue){
						check=false;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return  check;
	}
	public void condRealPerson(){
		PosparameXML pos = new PosparameXML(this.conn);  
		String setid=pos.getValue(PosparameXML.AMOUNTS,"setid"); 
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
		String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); //是否控制到部门
		ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
		String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
		dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
		String[] dbarr = dbs.split(",");
		ContentDAO dao = new ContentDAO(this.conn);
		if(fieldset!=null){ 
			ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
			for(int i=0;i<planitemlist.size();i++){
				String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
				String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
				String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
				if("1".equals(flag)){
					try {
						StringBuffer sqlstr = new StringBuffer();
						sqlstr.append("update ");
						sqlstr.append(setid); 
						sqlstr.append(" set "); 
						sqlstr.append(realitem);
						sqlstr.append("=0"); 
						if(!fieldset.isMainset()){
							sqlstr.append(" where I9999=(select max(I9999) from ");
							sqlstr.append(setid+" bo where bo.B0110="+setid+".B0110)");
						}
						dao.update(sqlstr.toString());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int j=0;j<dbarr.length;j++){
						String dbpre=dbarr[j];
						if(dbpre!=null&&dbpre.trim().length()>0) {
                            saveQueryResult(fieldset,realitem,cond,dbpre);
                        }
					}
				}
			}
			
		}
		Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(conn);
		String pos_ctrl=othparam.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
		RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.conn);
		String  ps_workout=ps_workout_vo.getString("str_value");
		StringTokenizer str=new StringTokenizer(ps_workout,"|");//K01|K0114,K0111
		if(pos_ctrl!=null&& "true".equalsIgnoreCase(pos_ctrl)&&str.hasMoreTokens()){
			String possetid = str.nextToken().toUpperCase();
			FieldSet fieldsetpos = DataDictionary.getFieldSetVo(possetid);
			if(str.hasMoreTokens()){
		    	StringTokenizer strfield=new StringTokenizer(str.nextToken(),",");
		    	strfield.nextToken();
		    	String realitem=strfield.nextToken().toUpperCase();
		    	try {
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("update ");
					sqlstr.append(possetid); 
					sqlstr.append(" set "); 
					sqlstr.append(realitem);
					sqlstr.append("=0"); 
					if(!fieldsetpos.isMainset()){
						sqlstr.append(" where I9999=(select max(I9999) from ");
						sqlstr.append(possetid+" bo where bo.E01A1="+possetid+".E01A1)");
					}
					dao.update(sqlstr.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int j=0;j<dbarr.length;j++){
					String dbpre=dbarr[j];
					if(dbpre!=null&&dbpre.trim().length()>0) {
                        saveQueryPosResult(fieldsetpos,realitem,dbpre);
                    }
				}
		    }
		}
	}
	public void saveQueryResult(FieldSet fieldset,String realitem,String id,String dbpre){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("LExpr");
			String wherestr = "from "+dbpre+"A01 where 1=1";
			if(id!=null&&id.length()>0){
				vo.setInt("id",Integer.parseInt(id));
			
				vo = dao.findByPrimaryKey(vo);
	
			    FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
			    		dbpre, false, false, true, 1, "su");
				wherestr = factor.getSqlExpression();
			}
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			sqlstr.append(fieldset.getFieldsetid());
			sqlstr.append(" set ");
			sqlstr.append(realitem);
			sqlstr.append("=(select count("+dbpre+"A01.A0100)+"+fieldset.getFieldsetid()+"."+realitem+" ");
			sqlstr.append(wherestr);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				sqlstr.append(" and (rpad("+dbpre+"A01.E0122,length("+fieldset.getFieldsetid()+".B0110))="+fieldset.getFieldsetid()+".B0110 or ");
				sqlstr.append(" (rpad("+dbpre+"A01.B0110,length("+fieldset.getFieldsetid()+".B0110))="+fieldset.getFieldsetid()+".B0110 )))");
			}else{
				sqlstr.append(" and (left("+dbpre+"A01.E0122,len("+fieldset.getFieldsetid()+".B0110))="+fieldset.getFieldsetid()+".B0110 or ");
				sqlstr.append(" (left("+dbpre+"A01.B0110,len("+fieldset.getFieldsetid()+".B0110))="+fieldset.getFieldsetid()+".B0110 )))");
			}
//			sqlstr.append(dbpre+"A01.B0110="+fieldset.getFieldsetid()+".B0110))");
			if(!fieldset.isMainset()){
				sqlstr.append(" where I9999=(select max(I9999) from "+fieldset.getFieldsetid());
				sqlstr.append(" a where a.B0110="+fieldset.getFieldsetid()+".B0110)");
			}
			dao.update(sqlstr.toString());
			
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveQueryPosResult(FieldSet fieldset,String realitem,String dbpre){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			sqlstr.append(fieldset.getFieldsetid());
			sqlstr.append(" set ");
			sqlstr.append(realitem);
			sqlstr.append("=(select count("+dbpre+"A01.A0100)+"+fieldset.getFieldsetid()+"."+realitem+" from "+dbpre+"A01");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sqlstr.append(" where rpad("+dbpre+"A01.E01A1,length("+fieldset.getFieldsetid()+".E01A1))="+fieldset.getFieldsetid()+".E01A1)");
            } else {
                sqlstr.append(" where left("+dbpre+"A01.E01A1,len("+fieldset.getFieldsetid()+".E01A1))="+fieldset.getFieldsetid()+".E01A1)");
            }
			if(!fieldset.isMainset()){
				sqlstr.append(" where I9999=(select max(I9999) from "+fieldset.getFieldsetid());
				sqlstr.append(" a where a.E01A1="+fieldset.getFieldsetid()+".E01A1)");
			}
			dao.update(sqlstr.toString());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String orgCodeName(String codeitemid){
		String orgname="";
		StringBuffer buf = new StringBuffer();
		buf.append("select codeitemdesc from organization where codeitemid='");
		buf.append(codeitemid);
		buf.append("'");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(buf.toString());
			while(rs.next()){
				orgname=rs.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orgname;
	}
	
	/**
	 * 编制管理的统计汇总
	 * @param conn
	 */
	public void doCount(Connection conn,UserView userView){
		
		ContentDAO dao = new ContentDAO(conn);
		
		String pos_set="";
		String unit_set="";
			RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);
			
			if(ps_workout_vo!=null)
			{
			  String  ps_workout=ps_workout_vo.getString("str_value");
			  ps_workout=ps_workout!=null?ps_workout:"";
			  if(ps_workout.length()>0){
				  String strs[]=ps_workout.split("\\|");//K01|K0114,K0111
				  pos_set=strs[0];
			  }
			}
			PosparameXML pos = new PosparameXML(conn);
			unit_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
		
		String getyear = Calendar.getInstance().get(Calendar.YEAR)+"";
		String getmonth = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
		getmonth=getmonth!=null&&getmonth.length()>0?getmonth:"0";
		
		String fieldstr = null;
		fieldstr = fieldstr!=null && fieldstr.length()>0?fieldstr:"";
		//String view_scan = (String)hm.get("view_scan");
		//view_scan=view_scan!=null?view_scan:"User,";
		String view_scan = pos.getNodeAttributeValue("/params/view_scan", "orgpre");
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
		
		String changeflag = "1";
		String count ="ok";
		count=count!=null?count:"";
		if(!"".equals(pos_set)){
			FieldSet fs = DataDictionary.getFieldSetVo(pos_set);
			if(fs==null) {
                return;
            }
			changeflag = fs.getChangeflag();
			changeflag= "".equals(changeflag)?"0":changeflag;
			if(count.length()>1)
			{
				DataCondBo databo = new DataCondBo(userView,conn,pos_set,
						view_scan,getyear,getmonth,changeflag);
				if(!"".equals(unit_set) && !"0".equals(unit_set)){
					FieldSet fset = DataDictionary.getFieldSetVo(unit_set);
					if(fset==null) {
                        return;
                    }
					changeflag = fset.getChangeflag();
					changeflag= "".equals(changeflag)?"0":changeflag;
					databo.resetDate(changeflag, unit_set,"0");
				}
				databo.runCond("0","UN");
			}
		}
		if(!"".equals(unit_set) && !"0".equals(unit_set)){
			FieldSet fs = DataDictionary.getFieldSetVo(unit_set);
			if(fs==null) {
                return;
            }
			changeflag = fs.getChangeflag();
			changeflag= "".equals(changeflag)?"0":changeflag;
			
			DataSynchroBo dsbo = new DataSynchroBo(userView, unit_set, dao, view_scan.substring(0,view_scan.length()-1), getyear, getmonth, changeflag); 
			
			if("0".equals(changeflag)){
				if(count.length()>1)
				{
					DataCondBo databo = new DataCondBo(userView,conn,unit_set,
							view_scan,getyear,getmonth,changeflag);
					databo.runCond1(pos_set,"0","UN");
				}
			}else{
				int num=0;
				if(fieldstr.trim().length()>0&&!"0".equals(changeflag)){
					num = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
				}
				if(count.length()>1)
				{
					DataCondBo databo = new DataCondBo(userView,conn,unit_set,
							view_scan,getyear,getmonth,changeflag);
					databo.runCond1(pos_set,"0","UN");
				}
				int num2=0;
				if(fieldstr.trim().length()>0&&!"0".equals(changeflag)){
					num2 = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
				}
				if(num2!=num){
					if(count.length()>1)
					{
						DataCondBo databo = new DataCondBo(userView,conn,unit_set,
								view_scan,getyear,getmonth,changeflag);
						databo.runCond1(pos_set,"0","UN");
					}
				}
			}
		}
		
		
		//如果设置岗位占编
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		/**兼职参数*/
		String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
		//兼职岗位占编 1：占编	
		String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");	
		if("true".equals(flag)&&"1".equals(takeup_quota)){
			RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
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
	    						FieldSet fs = DataDictionary.getFieldSetVo(pos_set);
	    						changeflag = fs.getChangeflag();
	    						changeflag= "".equals(changeflag)?"0":changeflag;
	    						
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
		
	}
	
	private String getId(String year,String month,String changeflag){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9) {
                return year+"."+month;
            } else {
                return year+".0"+month;
            }
		}
	}
}
