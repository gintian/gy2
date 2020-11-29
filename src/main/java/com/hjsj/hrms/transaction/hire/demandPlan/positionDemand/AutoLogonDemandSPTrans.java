package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 3000000235
 * <p>Title:AutoLogonDemandSPTrans.java</p>
 * <p>Description>:AutoLogonDemandSPTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 19, 2009 6:54:13 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class AutoLogonDemandSPTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String z0301 = (String)hm.get("id");
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
			ArrayList positionDemandDescList=positionDemand.getPositionDemandDescList(list,z0301);	
			this.getFormHM().put("positionDemandDescList",positionDemandDescList);
			this.getFormHM().put("z0301",z0301);
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301",z0301);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo = dao.findByPrimaryKey(vo);
			String sp_flag=vo.getString("z0319");
			String positionState="1";
		/*	01	起草
			02	已报批
			03	已批
			04	已发布
			05	执行中
			06	结束
			07	驳回
			08	报审
			09	暂停*/
			if("03".equals(sp_flag)|| "04".equals(sp_flag)|| "06".equals(sp_flag))
			{
				positionState="0";
			}
			String isReport="0";
			if("01".equals(sp_flag)|| "02".equals(sp_flag)|| "07".equals(sp_flag))
			{
				isReport="1";
			}
			String isReject="0";
			if("02".equals(sp_flag))
			{
				isReject="1";
			}
			String positionStateDesc = AdminCode.getCodeName("23",sp_flag);
			this.getFormHM().put("positionState",positionState);
			this.getFormHM().put("positionStateDesc", positionStateDesc);
			this.getFormHM().put("isReport", isReport);
			this.getFormHM().put("isReject", isReject);
			
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			String hirechannel=(String)map.get("hire_object");
			String zpchanel="";
			for(int k=0;k<positionDemandDescList.size();k++){
				LazyDynaBean bean=(LazyDynaBean)positionDemandDescList.get(k);
				if(((String)bean.get("itemid")).equalsIgnoreCase(hirechannel)){
					zpchanel=(String)bean.get("value");
					break;
				}
			}
			String isOrgWillTableIdDefine="0";
			/**是否定义单位部门预算表*/
			if(map!=null&&map.get("orgWillTableId")!=null&&!"#".equals((String)map.get("orgWillTableId")))
			{
				isOrgWillTableIdDefine="1";
			}
			PosparameXML pos = new PosparameXML(this.getFrameconn());  
			/**=1控制到部门，=0控制到单位*/
			String bzctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
			GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap gzhm = XMLbo.getValuesMap();
			/**=0控制到部门*/
			String gzctrl_type = (String) gzhm.get("ctrl_type");
			if((bzctrl_type!=null&& "1".equals(bzctrl_type))||gzctrl_type!=null&& "0".equals(gzctrl_type))
				this.getFormHM().put("showUMCard","1");
			else
				this.getFormHM().put("showUMCard","0");
			
			String spRelation = "";
			if(map!=null&&map.get("spRelation")!=null)
				spRelation=(String)map.get("spRelation");
			
			String[] zpspfs = getSpf(spRelation).split(":");
			String spcount = zpspfs[0];
			String actortype = "";
			if(zpspfs.length>1)
				actortype = zpspfs[1];
			
			this.getFormHM().put("isOrgWillTableIdDefine", isOrgWillTableIdDefine);
			this.getFormHM().put("orgUM",vo.getString("z0325"));
			this.getFormHM().put("orgUN",vo.getString("z0321"));
			this.getFormHM().put("e01a1",vo.getString("z0311"));
			this.getFormHM().put("zpchanel", zpchanel);
			this.getFormHM().put("spRelation", spRelation);
			this.getFormHM().put("spcount", spcount);
			this.getFormHM().put("zpappfalg", getzpappflag(actortype));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	/**
	 * 获取当前用户在审批关系中的直接领导的个数和审批关系的类型
	 * @param spRelation
	 * @return
	 */
	public String getSpf(String spRelation) {
		String count = "0";
		String Actortype = "";
		String Objectid = "";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search("select Actor_type from t_wf_relation  where relation_id='" + spRelation + "'");
			if (this.frowset.next())
				Actortype = this.frowset.getString("Actor_type");

			if ("1".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getDbname() + this.userView.getA0100();
			else if ("4".equalsIgnoreCase(Actortype))
				Objectid = this.userView.getUserName();

			this.frowset = dao.search("select count(1) count from t_wf_mainbody  where Actor_type='" + Actortype + "' and Object_id='"
					+ Objectid + "' and relation_id='" + spRelation + "' and sp_grade='9'");
			if (this.frowset.next())
				count = this.frowset.getInt("count") + "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count + ":" + Actortype;
	} 
	/**
	 * 判断当前用户类型和审批关系的参与者类型是否一致
	 * @param type 审批关系的参与者类型
	 * @return flag true：一致|false：不一致
	 */
	private String getzpappflag(String type) {
		
		String flag = "true";
		String a0100 = this.userView.getA0100();
		String dbname = this.userView.getDbname();
		int status = this.userView.getStatus();
		//审批关系的参与者类型为自助用户时，当前用户为业务用户但是关联了自助用户时默认为true
		if ("1".equalsIgnoreCase(type) && status != 4 && (a0100 == null
				|| a0100.length() < 4 || dbname == null || dbname.length() < 1))
			flag = "false";
		
		if ("4".equalsIgnoreCase(type) && status == 4)
			flag = "false";
		
		return flag;

	}
	
}
