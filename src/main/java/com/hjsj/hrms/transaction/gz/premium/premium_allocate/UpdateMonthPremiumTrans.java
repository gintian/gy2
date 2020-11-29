package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:UpdateMonthPremiumTrans.java
 * </p>
 * <p>
 * Description:更新部门月奖金
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-11-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class UpdateMonthPremiumTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String currentOrg = (String) hm.get("orgcode");
	currentOrg = currentOrg == null ? "" : currentOrg;

	String theYear = (String) this.getFormHM().get("year");
	String theMonth = (String) this.getFormHM().get("month");

	String oper = (String) hm.get("oper");
	hm.remove("oper");
	oper = oper == null ? "" : oper;

	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
	String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
	String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标
	String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
	String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET","keep_save_field");// 封存字段
	String salaryid = xml.getNodeAttributeValue("/Params/BONUS_SET", "salaryid");// 共享工资类别

	String busiField = setid + "z0";// 业务日期字段
	StringBuffer updateBuf = new StringBuffer();
	updateBuf.append("update " + setid + " set ");
	
	String whlSql = " where " + Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;
	String childOrgSqlStr ="select codeitemid from organization where  codesetid in ('UM','UN') and parentid='" + currentOrg + "'";
	
	ContentDAO dao = new ContentDAO(this.frameconn);

	try
	{
	    if ("appeal".equalsIgnoreCase(oper))// 上报:将自己改为上报状态
	    {
			updateBuf.append(rep_field + "='1'");
			updateBuf.append(whlSql);
//			updateBuf.append(" and (b0110='" + currentOrg + "' or b0110 in (");
//			updateBuf.append(childOrgSqlStr);
//			updateBuf.append("))");
			updateBuf.append(" and b0110='" + currentOrg + "'");
			dao.update(updateBuf.toString());
			
	    } else if ("distribute".equalsIgnoreCase(oper))// 下发:将直接子机构改为下发状态
	    {
			updateBuf.append(dist_field + "='1'");
			updateBuf.append(whlSql);
			updateBuf.append(" and b0110 in (" + childOrgSqlStr + ")");
			dao.update(updateBuf.toString());
			//如果不进行人员奖金的发放，下发到叶子节点时，上报状态默认值改为是
			
			if (salaryid != null && salaryid.trim().length() > 0)
			{
			   
			} else
			{
				updateBuf.setLength(0);
				updateBuf.append("update " + setid + " set ");
				updateBuf.append(rep_field + "='1'");
				updateBuf.append(whlSql);
				
				String str="select codeitemid from organization where parentid='"+currentOrg+"' and  codesetid in ('UM','UN') "
						 +" and codeitemid not in (select parentid from organization where upper(codesetid)='UM' or upper(codesetid)='UN' )";
				
				updateBuf.append(" and b0110 in ( "+str+" )");
				dao.update(updateBuf.toString());
			}			
	    } else if ("del".equalsIgnoreCase(oper))// 删除
	    {
		String delStr = (String)this.getFormHM().get("paramStr");
		String[] b0110s = delStr.split(",");
//		ArrayList list1 = new ArrayList();
		for(int i=0;i<b0110s.length;i++){
		    String[] temp = b0110s[i].split(":");
//		    ArrayList list = new ArrayList();
		    String b0110=temp[0];
//		    String i9999=temp[1];	
//		    list.add(b0110);
//		    list.add(new Integer(i9999));
//		    list1.add(list);
		    String sql = "delete from "+setid+" where b0110 like '"+b0110+"%' and "+ Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;
		    dao.delete(sql, new ArrayList());		    
		}
//		String sql = "delete from "+setid+" where b0110=? and i9999=? and ("+keep_save_field+" is null or "+keep_save_field+"='2')";
//		dao.batchUpdate(sql, list1);		
	    } else if ("keepsave".equalsIgnoreCase(oper))// 封存当前业务日期范围内的顶层机构的直接和非直接子机构
	    {
		updateBuf.append(keep_save_field + "='1'");
		updateBuf.append(whlSql);
		updateBuf.append(" and b0110 like '"+currentOrg+"%'");
		dao.update(updateBuf.toString());
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}

    }

}
