package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.train.TrParamXmlBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:新增培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class AddTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		String r3101=idg.getId("R31.R3101");
		
		String model = (String)this.getFormHM().get("model");
		model=model!=null&&model.trim().length()>0?model:"1";
		
		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		
		String codeid=this.getUserView().getManagePrivCodeValue();
		String codeset=this.getUserView().getManagePrivCode();
		TrainPlanBo trainPlanBo=new TrainPlanBo(this.getFrameconn());
		HashMap unit_depart_map=trainPlanBo.getUnit_Depart(codeid,codeset,"1",this.getUserView());
		
		
		String codesetid="";
		String codevalue="";
		String b0110="";
		String e0122="";
		
		
		if(unit_depart_map.get("e0122")!=null&&((String)unit_depart_map.get("e0122")).length()>0)					
			e0122=(String)unit_depart_map.get("e0122");
		if(unit_depart_map.get("b0110")!=null&&((String)unit_depart_map.get("b0110")).length()>0)
			b0110=(String)unit_depart_map.get("b0110");
		
		if(a_code==null||a_code.length()<1){
			//多个操作单位单位和部门显示为第一个
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi().split("`")[0];
		}
		
		if(a_code!=null&&a_code.trim().length()>2)
			codesetid=a_code.substring(0,2);
		if(a_code!=null&&a_code.trim().length()>2)
			codevalue=a_code.substring(2);
		
		if("UN".equalsIgnoreCase(codesetid)&&(!"".equalsIgnoreCase(codevalue))){
			b0110=codevalue;
		}else if("UM".equalsIgnoreCase(codesetid)&&(!"".equalsIgnoreCase(codevalue))){
			e0122=codevalue;
			CodeItem codeitem=AdminCode.getCode("UM", codevalue);
			String parentid=codeitem.getPcodeitem();
			b0110=getParentCodeValue(parentid);
		}
		
		String spflag = "01";
		if("2".equals(model)){
			spflag = "03";
		}else if("3".equals(model)){
			spflag = "02";
		}
		WeekUtils wu = new WeekUtils();
		String r3118 = wu.strDate();
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int i9999 = getMaxI9999(dao);
		
		TrParamXmlBo trParamXmlBo=new TrParamXmlBo(this.getFrameconn());
		HashMap para_map=trParamXmlBo.getAttributeValues();
		String  fieldStr=(String)para_map.get("plan_mx");       //常量表  参数TR_PARAM (  R3121,R3124,R3125)
		

		RecordVo old_vo = new RecordVo("R31");
		old_vo.setString("r3101", r3101);
		old_vo.setString("b0110", b0110);
		old_vo.setString("e0122", e0122);
		old_vo.setString("r3127", spflag);
		old_vo.setString("r3130", "<"+ResourceFactory.getProperty("train.b_plan.newtrain")+">");

		if(fieldStr.toLowerCase().indexOf("r3118")!=-1){
			old_vo.setDate("r3118", r3118);
			old_vo.setInt("i9999", i9999);
		}else{
			old_vo.setInt("i9999", i9999);
			r3118="";
		}
		dao.addValueObject(old_vo);
		
		
		this.getFormHM().put("r3101",r3101);
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("e0122",e0122);
		this.getFormHM().put("r3127",spflag);
		this.getFormHM().put("r3130","<"+ResourceFactory.getProperty("train.b_plan.newtrain")+">");
		this.getFormHM().put("r3118",r3118);
		this.getFormHM().put("model",model);
		
	}
	/**
	 * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位
	 * 节点。
	 * @param codevalue
	 * @return
	 */
	private String getParentCodeValue(String codevalue){
		String value="";
		StringBuffer buf=new StringBuffer();
		buf.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
		ArrayList paralist=new ArrayList();
		paralist.add(codevalue);
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
			{
				String codeid=rset.getString("codesetid");
				String parentid=rset.getString("parentid");
				if(!"UN".equalsIgnoreCase(codeid))
					value=getParentCodeValue(parentid);
				else
					value=rset.getString("codeitemid");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return value;
	}
	private int getMaxI9999(ContentDAO dao){
		int i9999=1;
		DbWizard dbw = new DbWizard(this.getFrameconn());
        // 如果存在i9999字段,首先将所有该字段非空记录加1,再将要保存的记录该字段设为1
        if (dbw.isExistField("r31", "i9999", false))
        {
            String sql = "update r31 set i9999=i9999+1 where i9999 is not null";
            try
            {
                dao.update(sql);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
		return i9999;
	}
}
