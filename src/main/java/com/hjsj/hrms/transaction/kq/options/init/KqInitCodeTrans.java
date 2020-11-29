package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.ChangeFactoryId;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.utils.FormatValue;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class KqInitCodeTrans extends IBusiness {
	
	private boolean isNull(String str)
	{
		boolean boo=false;
		if(!(str==null|| "".equals(str)))
		{
			boo=true;
		}
		
		return boo;
	}

	public void execute() throws GeneralException {
		String out=(String)this.getFormHM().get("out");//公出
		String otime=(String)this.getFormHM().get("outime");//加班
		String rest=(String)this.getFormHM().get("rest");//休息
		String staffl=(String)this.getFormHM().get("staffl");//员工日明细
		String staffy=(String)this.getFormHM().get("staffy");//员工月汇总
		String deptl=(String)this.getFormHM().get("deptl");//部门日明细
		String depty=(String)this.getFormHM().get("depty");//部门月汇总
		String scope=(String)this.getFormHM().get("scope");//时间范围标记
//		String tstart=(String)this.getFormHM().get("Tstart");//开始时间
//		String tend=(String)this.getFormHM().get("Tend");//结束时间
		String q19=(String)this.getFormHM().get("q19");//调班申请
		String q21=(String)this.getFormHM().get("q21");//替班申请
		String shift=(String)this.getFormHM().get("shift");//员工排班信息表
		String txsq=(String)this.getFormHM().get("txsq");//调休申请表
		String ypsk=(String)this.getFormHM().get("ypsk");//员工刷卡信息表
		String jqgl=(String)this.getFormHM().get("jqgl");//假期信息表
		String bzry=(String)this.getFormHM().get("bzry");//班组人员
		String kqbz=(String)this.getFormHM().get("kqbz");//考勤班组
		String kqorg=(String)this.getFormHM().get("kqorg");//单位部门排班表
		String dxjb = (String)this.getFormHM().get("dxjb");//调休加班明细表
		String tstart=(String)this.getFormHM().get("count_start");
		String tend=(String)this.getFormHM().get("count_end");
		
		String kqCard = (String)this.getFormHM().get("kqCard");//考勤卡号
		String kqType = (String)this.getFormHM().get("kqType");//考勤方式
		
		FormatValue fv=new FormatValue();
		//###当开始时间和结束时间为空时，格式化时间出错wangzhongjun2011-03-31
//		String sta=fv.formatItemType("D",0,10,tstart);
//		String end=fv.formatItemType("D",0,10,tend);
		//###修改如下wangzhongjun2011-03-31
		String sta = "";
		String end = "";
		if (tstart != null && tstart.length() > 0 && !"undefined".equalsIgnoreCase(tstart)) {
			sta=fv.formatItemType("D",0,10,tstart);
		}
		if (tend != null && tend.length() > 0 && !"undefined".equalsIgnoreCase(tend)) {
			end=fv.formatItemType("D",0,10,tend);
		}
		if("2".equals(scope))
		{
			if("".equals(tstart)|| "".equals(tend))
				throw GeneralExceptionHandler.Handle(new GeneralException("","初始化数据时间范围不能为空！","",""));
		}
		this.getFormHM().put("scope","1");
		String all_init=(String)this.getFormHM().get("all_init"); 		 
		
		ChangeFactoryId cfi=new ChangeFactoryId(frameconn);
		ArrayList alist =new ArrayList();		
		
		if(all_init!=null&& "1".equals(all_init))
		{
			//out=otime=rest=staffl=staffy="1";
			//deptl=depty=scope=q19=q21=shift="1";
			//txsq=ypsk=jqgl="1";
			otherKqInti();
		}
		
		DbWizard dbWizard = new DbWizard(frameconn);
		boolean isExist = false;
		
		if(this.isNull(out))//公出表
		{
			isExist = dbWizard.isExistTable("Q13_arc",false);
			if("1".equals(scope))
			{
			    //注释原因：避免使用人事异动申请时单号在模板相应表中导致重复
				//alist.add("Q13.q1301");
				cfi.delAll("Q13");
				if(isExist)
				    cfi.delAll("Q13_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q13","q13",sta,end,"D");
				if(isExist)
				    cfi.delWhere("Q13_arc","q13",sta,end,"D");
			}
		}
		if(this.isNull(otime))//加班
		{
		    isExist = dbWizard.isExistTable("Q11_arc",false);
		    if("1".equals(scope))
			{
		        //注释原因：避免使用人事异动申请时单号在模板相应表中导致重复
		    	//alist.add("Q11.q1101");
				cfi.delAll("Q11");
				if(isExist)
                    cfi.delAll("Q11_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q11","q11",sta,end,"D");
				if(isExist)
                    cfi.delWhere("Q11_arc","q11",sta,end,"D");
			}
		}
		if(this.isNull(rest))//请假
		{
		    isExist = dbWizard.isExistTable("Q15_arc",false);
			if("1".equals(scope))
			{
			    //注释原因：避免使用人事异动申请时单号在模板相应表中导致重复
				//alist.add("Q15.q1501");
				cfi.delAll("Q15");
				if(isExist)
                    cfi.delAll("Q15_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q15","q15",sta,end,"D");
				if(isExist)
                    cfi.delWhere("Q15_arc","q15",sta,end,"D");
			}
		}
		if(this.isNull(staffl))//员工日明细
		{
		    isExist = dbWizard.isExistTable("Q03_arc",false);
			if("1".equals(scope))
			{
				cfi.delAll("Q03");
				if(isExist)
                    cfi.delAll("Q03_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q03","q03z0",sta,end,"A");
				if(isExist)
                    cfi.delWhere("Q03_arc","q03z0",sta,end,"A");
			}
		}
		if(this.isNull(dxjb))//调休加班明细
        {
            if("1".equals(scope))
            {
                cfi.delAll("Q33");
            }
            if("2".equals(scope))
            {
                cfi.delWhere("Q33","q33z0",sta,end,"A");
            }
        }           
		if(this.isNull(staffy))//员工月汇总
		{
		    isExist = dbWizard.isExistTable("Q05_arc",false);
			if("1".equals(scope))
			{
				cfi.delAll("Q05");
				if(isExist)
                    cfi.delAll("Q05_arc");
			}
			if("2".equals(scope))
			{				
				cfi.delWhere("Q05","q03z0",sta,end,"A");
				if(isExist)
                    cfi.delWhere("Q05_arc","q03z0",sta,end,"A");
			}
		}
		if(this.isNull(deptl))//部门日明细
		{
		    isExist = dbWizard.isExistTable("Q07_arc",false);
			if("1".equals(scope))
			{
				cfi.delAll("Q07");
				if(isExist)
                    cfi.delAll("Q07_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q07","q03z0",sta,end,"A");
				if(isExist)
                    cfi.delWhere("Q07_arc","q03z0",sta,end,"A");
			}
		}
		if(this.isNull(depty))//部门月汇总
		{
		    isExist = dbWizard.isExistTable("Q09_arc",false);
			if("1".equals(scope))
			{				
				cfi.delAll("Q09");
				if(isExist)
                    cfi.delAll("Q09_arc");
				
			}
			if("2".equals(scope))
			{
				cfi.delWhere("Q09","q03z0",sta,end,"A");
				if(isExist)
                    cfi.delWhere("Q09_arc","q03z0",sta,end,"A");
			}
		}
	    if(this.isNull(q19))//调班管理
	    {
	    	if("1".equals(scope))
	    	{
	    		alist.add("Q19.Q1901");
	    		cfi.delAll("Q19");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("Q19","q19",sta,end,"D");
	    	}
	    }
	    if(this.isNull(q21))//替班管理
	    {
	    	if("1".equals(scope))
	    	{
	    		alist.add("Q21.Q2101");
	    		cfi.delAll("Q21");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("Q21","q21",sta,end,"D");
	    	}
	    }	
		//ArrayList fielditemlist=DataDictionary.getFieldList("",Constant.USED_FIELD_SET);
		if(this.isNull(txsq))//调休管理
		{
			if("1".equals(scope))
	    	{
	    		alist.add("Q25.Q2501");
	    		cfi.delAll("Q25");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("Q25","q25",sta,end,"D");
	    	}
		}
		if(this.isNull(shift))//员工排班信息
		{
		    isExist = dbWizard.isExistTable("kq_employ_shift_arc",false);
			if("1".equals(scope))
	    	{	    		
	    		cfi.delAll("kq_employ_shift");
	    		if(isExist)
	    		    cfi.delAll("kq_employ_shift_arc");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("kq_employ_shift","q03z0",sta,end,"A");
	    		if(isExist)
	    		    cfi.delWhere("kq_employ_shift_arc", "q03z0", sta, end, "A");
	    	}
		}
		if(this.isNull(ypsk))//员工刷卡信息表
		{
		    isExist = dbWizard.isExistTable("kq_originality_data_arc",false);
			if("1".equals(scope))
	    	{	    		
	    		cfi.delAll("kq_originality_data");
	    		if(isExist)
                    cfi.delAll("kq_originality_data_arc");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("kq_originality_data","work_date",sta,end,"A");
	    		if(isExist)
                    cfi.delWhere("kq_originality_data_arc", "work_date", sta, end, "A");
	    	}
		}
		if(this.isNull(jqgl))//假期信息表
		{
			if("1".equals(scope))
	    	{
	    		cfi.delAll("Q17");
	    	}
	    	if("2".equals(scope))
	    	{
	    		cfi.delWhere("Q17","q17",sta,end,"D");
	    	}
		}
		if(this.isNull(kqbz))
		{
			cfi.delAll("kq_shift_group");			
		}
		if(this.isNull(bzry))
		{
			cfi.delAll("kq_group_emp");			
		}
		//单位部门排班表
		if(this.isNull(kqorg))
		{
		    isExist = dbWizard.isExistTable("kq_org_dept_shift_arc",false);
			if("1".equals(scope))
			{
				cfi.delAll("kq_org_dept_shift");
				if(isExist)
				    cfi.delAll("kq_org_dept_shift_arc");
			}
			if("2".equals(scope))
			{
				cfi.delWhere("kq_org_dept_shift","q03z0",sta,end,"A");
				if(isExist)
				    cfi.delWhere("kq_org_dept_shift_arc","q03z0",sta,end,"A");
			}
		}
		
		if (kqCard != null && "1".equals(kqCard)) { // 考勤卡号
			this.delkqCardorKqType("cardno");
		}

		if (kqType != null && "1".equals(kqType)) { // 考勤方式
			this.delkqCardorKqType("kq_type");
		}
		
		//**修改FactoryID		
		cfi.changeId(alist);		 
		this.getFormHM().put("mess","2");
		this.getFormHM().put("all_init","0");
	}
	/**
	 * 其他初始化表
	 * @throws GeneralException
	 */
    private void otherKqInti()throws GeneralException
    {
    	String table="";		
		table="kq_duration";//考勤期间		
		ChangeFactoryId cfi=new ChangeFactoryId(this.getFrameconn());
		cfi.delAll(table);
		table="kq_restofweek";//公休日
		cfi.delAll(table);
		String sql="insert into kq_restofweek (B0110,rest_weeks) values('UN','6,7')";
		cfi.insertSQLInit(sql);
		table="kq_feast";//节假日
		ArrayList list=new ArrayList();		
		cfi.delAll(table);
		sql="insert into kq_feast (feast_id,feast_name,feast_dates,kq_year) values(1,'元旦','01.01','')";
		cfi.insertSQLInit(sql);
		sql="insert into kq_feast (feast_id,feast_name,feast_dates,kq_year) values(2,'劳动节','05.01,05.02,05.03','')";
		cfi.insertSQLInit(sql);
		sql="insert into kq_feast (feast_id,feast_name,feast_dates,kq_year) values(3,'国庆节','10.01,10.02,10.03','')";
		cfi.insertSQLInit(sql);
		sql="update id_factory set currentid='3' where sequence_name='kq_feast.feast_id'";
		cfi.updateSQLInit(sql);
		table="kq_class";//基本班次
		list.add("kq_class.class_id");
		cfi.delAll(table);
		sql="insert into kq_class(class_id,name,restflag) values(0,'休息',1)";
		cfi.insertSQLInit(sql);
		table="kq_shift";//周期班
		list.add("kq_shift.shift_id");
		cfi.delAll(table);
		table="kq_shift_class";//周基班与基本班次对应表
		cfi.delAll(table);
		table="kq_machine_location";//考勤机安装信息
		list.add("kq_machine_location.location_id");
		cfi.delAll(table);
		table="kq_turn_rest";//公休日倒休信息
		list.add("kq_turn_rest.turn_id");
		cfi.delAll(table);
		table="kq_data_rule";//考勤数据格式
		list.add("kq_turn_rest.turn_id");
		cfi.delAll(table);
		table="kq_parameter";//考勤数据参数
		cfi.delAll(table);
		sql="insert into kq_parameter(B0110,name,description,content,status) values('UN','RADIX_POINT','小数点位数','2',1)";
		cfi.insertSQLInit(sql);
		sql="insert into kq_parameter(B0110,name,description,content,status) values('UN','ON_DUTY','默认出勤','1',1)";
		cfi.insertSQLInit(sql);
		table="kq_shift_group";//班组信息表
		list.add("kq_shift_group.group_id");
		cfi.delAll(table);
		table="kq_group_emp";//人员班组对应表
		cfi.delAll(table);
		table="Q23";//员工存班信息
		list.add("Q23.q2301");
		cfi.delAll(table);
		table="Q27";//员工补休信息
		list.add("Q27.q2701");
		cfi.delAll(table);
		table="Q29";//--30.部门年休假计划表     
		list.add("Q29.q2901");
		cfi.delAll(table);
		table="Q31";//--31.员工个人休假申请表	
		list.add("Q31.q3101");
		cfi.delAll(table);
		table="kq_employ_change";
		cfi.delAll(table);//人员变动信息表
		table="kq_org_dept_shift";//部门排班信息
		cfi.delAll(table);
		table="kq_cards";
		list.add("kq_cards.card_no");
		cfi.delAll(table);
		table="kq_archive_schema";//考勤数据归档方案
		cfi.delAll(table);
		table="kq_org_dept_able_shift";//部门
		cfi.delAll(table);
		cfi.changeId(list);	
    }

    /**
     * 初始化: 考勤卡号 或 考勤方式
     * @param column
     * @throws GeneralException
     */
    private void delkqCardorKqType(String column)throws GeneralException{
		ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
		String userOrgId = managePrivCode.getPrivOrgId();
		
		KqParameter para = new KqParameter(this.userView, userOrgId, this.getFrameconn());
		HashMap hashmap = para.getKqParamterMap();
		String columnName = (String) hashmap.get(column);

		String sql = "select pre from dbname";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list_up = new ArrayList();
		StringBuffer up_sql = null;
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String pre = this.frowset.getString("pre");
				if (columnName != null && columnName.length() > 0) {
					up_sql = new StringBuffer();
					up_sql.append("update " + pre + "A01 set ");   
					up_sql.append(" " + columnName + "=''");
					list_up.add(up_sql);
				}
			}
			for (int i = 0; i < list_up.size(); i++) {
				dao.update(list_up.get(i).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
