package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
/**
 * <p>Title:培训计划</p>
 * <p>Description:新增培训计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class AddPlanTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		String r2501=idg.getId("r25.r2501");
		
		String model = (String)this.getFormHM().get("model");
		model=model!=null&&model.trim().length()>0?model:"1";
		
		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		String codesetid="";
		String codevalue="";
		String b0110="";
		String e0122="";
		if(a_code!=null&&a_code.trim().length()>1)
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
		}
		WeekUtils wu = new WeekUtils();
		String r2508 = wu.strDate();
		String r2503 = Calendar.getInstance().get(Calendar.YEAR)+"";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int i9999 = getMaxI9999(dao);
		
		ArrayList list = new ArrayList();
		list.add(r2501);
		list.add(b0110);
		list.add(e0122);
		list.add(spflag);
		list.add(ResourceFactory.getProperty("train.b_plan.newplan"));
		list.add(r2503);
		list.add(r2508);
		list.add(i9999+"");
		try {
			dao.update("insert into r25(r2501,b0110,e0122,r2509,r2502,r2503,r2508,i9999) values(?,?,?,?,?,?,?,?)",list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getFormHM().put("r2501",r2501);
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("e0122",e0122);
		this.getFormHM().put("r2509",spflag);
		this.getFormHM().put("r2502",ResourceFactory.getProperty("train.b_plan.newtrain"));
		this.getFormHM().put("r2508",r2508);
		this.getFormHM().put("r2503",r2503);
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
		try {
			this.frowset = dao.search("select max(I9999) as i9999 from r25");
			if(this.frowset.next()){
				i9999 = this.frowset.getInt("i9999");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i9999;
	}
}
