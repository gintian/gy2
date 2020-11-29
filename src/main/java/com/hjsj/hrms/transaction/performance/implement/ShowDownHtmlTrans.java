package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ShowDownHtmlTrans.java</p>
 * <p>Description>:考核实施目标卡制定</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 17, 2010 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ShowDownHtmlTrans extends IBusiness 
{

	private HashMap descmap = new HashMap();
	private HashMap parentmap = new HashMap();
	
	public void execute() throws GeneralException 
	{
		RowSet rowSet=null;
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String planid=(String)this.getFormHM().get("planid");
			BatchGradeBo.getPlan_perPointMap().remove(planid);
			String objectid=(String)hm.get("objectid");
			this.getFormHM().put("object_id",objectid);
			String template_id=(String)hm.get("template_id");
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			if(planid==null || planid!=null && planid.trim().length()==0)
				return;
		//	if(opt.equals("1"))
//			{
				// 获得需要的计划参数
				LoadXml loadXml=null; //new LoadXml();
		    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
				{							
					loadXml = new LoadXml(this.getFrameconn(),planid);
					BatchGradeBo.getPlanLoadXmlMap().put(planid,loadXml);
				}
				else
				{
					loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
				}
	            Hashtable htxml = loadXml.getDegreeWhole();
			//  String spByBodySeq = (String)htxml.get("SpByBodySeq"); //按考核主体顺序号控制审批流程(True, False默认为False)				    
			    String gradeByBodySeq = (String)htxml.get("GradeByBodySeq"); //按考核主体顺序号控制评分流程(True, False默认为False)
				
				ArrayList perMainBodyList=pb.getPerMainBodyList(objectid,planid);
				this.getFormHM().put("perMainBodyList",perMainBodyList);
				this.getFormHM().put("gradeByBodySeq",gradeByBodySeq);
				this.getFormHM().put("object_type",String.valueOf(getPerPlanVo(planid).getInt("object_type")));
				this.getFormHM().put("method", getMethod(planid));
//			}
			if("2".equals(opt))
			{
				ArrayList pointPowerHeadList=pb.getPointPowerHeadList(template_id);
				ArrayList pointPowerList=pb.getPointPowerList(objectid,planid, pointPowerHeadList);
				this.getFormHM().put("pointPowerList",pointPowerList);
			}
//			if(opt.equals("3"))
//			{
				ArrayList khRelaMainbody=pb.getKhRelaMainbody(objectid,planid);
				this.getFormHM().put("khRelaMainbody",khRelaMainbody);
//			}
			if("4".equals(opt))
			{
				HashMap map=pb.getItemPriv(objectid,planid);
				ArrayList pointItemList = (ArrayList)map.get("pointItemList");
				ArrayList itemprivList = (ArrayList)map.get("itemPrivList");
				this.getFormHM().put("itemprivList",itemprivList);
				this.getFormHM().put("pointItemList",pointItemList);
				this.getFormHM().put("optMap",map.get("optMap"));
			}
			//目标卡制定定位考核对象用
		    ContentDAO dao = new ContentDAO(this.frameconn);
			String objInfo="";
			String sql = "select codeitemid,parentid,codeitemdesc from organization ";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				descmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				parentmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
			}
			String dbStr = "";
			this.frowset = dao.search("select dbname from dbname where lower(pre)='usr'");
			if (this.frowset.next())
				dbStr += "/" + this.frowset.getString("dbname");
			
			String onlyname = "";
			if(getPerPlanVo(planid).getInt("object_type")==2)
			{
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(frameconn);
				onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				
				if(onlyname!=null && onlyname.trim().length()>0 && !"#".equals(onlyname))
				{
					FieldItem fielditem = DataDictionary.getFieldItem(onlyname);
					String useFlag = fielditem.getUseflag(); 
					if("0".equalsIgnoreCase(useFlag))
						throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
				}
			}
		    sql = "select * from per_object where plan_id=" + planid + " and object_id='"+objectid+"'";
			this.frowset = dao.search(sql);
			if (this.frowset.next())
			{
				String b0110 = this.frowset.getString("b0110");
				String e0122 = this.frowset.getString("e0122");
				String a0101 = this.frowset.getString("a0101");
				String person = "";
				if (onlyname != null && onlyname.trim().length()>0)
				{
					String strSql = "select " + onlyname + " from usrA01 where a0100='" + objectid + "' ";
					rowSet = dao.search(strSql);
					String only_id="";
					while(rowSet.next())
					{
						only_id = rowSet.getString(onlyname) == null ? "" : rowSet.getString(onlyname);
					}
					if(only_id != null && only_id.trim().length()>0)
						person = a0101+"("+only_id+")";
					else
						person = a0101;
				}
				if (e0122 != null && e0122.length() > 0)
					objInfo = getSuperOrgLink(e0122, "UM");
				else if (b0110 != null && b0110.length() > 0)
					objInfo = getSuperOrgLink(b0110, "UN");
				
				if(getPerPlanVo(planid).getInt("object_type")==2)
				{
					if (onlyname != null && !"".equals(onlyname))
						objInfo=person+"/"+objInfo;
					else
						objInfo=a0101+"/"+objInfo;
				}
				else
					objInfo=a0101+"/"+objInfo;
				if (objInfo.length() > 0)
					objInfo += dbStr;
			}
			this.getFormHM().put("objInfo", objInfo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public String getSuperOrgLink(String codeitemid, String codesetid)
	{
		StringBuffer org_str = new StringBuffer("");
		try
		{
			String itemid = codeitemid;
			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			while (true)
			{
				String parentid = (String) this.parentmap.get(itemid);
				if (parentid==null || parentid.equals(itemid))
					break;
				else
				{
					org_str.append("/" + this.descmap.get(parentid));
					itemid = parentid;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return org_str.toString();
	}
	public RecordVo getPerPlanVo(String planid)
	{

		RecordVo vo = new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	} 
	/**
	 * 获取考核计划的考核方式
	 * @param planid 考核计划id
	 * @return method 1： 360度  2:目标管理
	 */
    private String getMethod(String planid) {
        String method = "1";
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search("select Method from per_plan where Plan_id =" + planid + "");
            if (this.frowset.next()) {
            	String m = this.frowset.getString("Method");
            	method = m == null || "".equals(m) ? method : m; // 旧数据method有null值存在 lium
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }
}
