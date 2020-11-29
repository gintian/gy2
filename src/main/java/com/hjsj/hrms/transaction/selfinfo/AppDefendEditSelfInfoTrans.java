package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p> Title: AppEditSelfInfoTrans </p>
 * <p> Description: AppEditSelfInfoTrans类处理个人申请修改的业务 </P>
 * 
 * @author wangzhongjun
 * @version 1.0 create time:2009-12-23
 */
public class AppDefendEditSelfInfoTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/**
	 * 查询t_hr_mydata_chg表中的已批的所有数据信息
	 */
	public void execute() throws GeneralException {
		
		try{
			MyselfDataApprove self = new MyselfDataApprove(this.frameconn, this.userView);
			Map map = (Map) this.getFormHM().get("requestPamaHM");
			// String prove = (String) map.get("b_search");
			String setname = (String) map.get("setname");
			String flag = (String)map.get("flag");
			String userbase = (String) this.getFormHM().get("userbase");
			String a0100 = (String) this.getFormHM().get("a0100");
			
			List infofieldlist = (List)this.getFormHM().get("infofieldlist");
			String part_unit=(String)this.getFormHM().get("part_unit");
			String part_setid=(String)this.getFormHM().get("part_setid");
			// List infoList = new ArrayList();
			FieldSet fs = DataDictionary.getFieldSetVo(setname);
			String multimedia_file_flag = fs.getMultimedia_file_flag();
			this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
			if(null != a0100 && !"".equals(a0100)){
				CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
				userbase = cps.checkDb(userbase);
				a0100 = cps.checkA0100("",userbase , a0100, "");
			}
			if("infoself".equals(flag)){
				userbase = this.userView.getDbname();
				a0100 = this.userView.getA0100();
				this.getFormHM().put("selfA0100", a0100);
				this.getFormHM().put("selfBase", userbase);
			}
			if (setname != null && "A01".equals(setname)) {//人员基本信息
				String chg_id = self.getChgid(userbase, a0100, "01");
				if (chg_id == null || chg_id.length() <= 0) {
					chg_id = self.getChgid(userbase, a0100, "07");
				}
				if (chg_id == null || chg_id.length() <= 0) {
					chg_id = self.getChgid(userbase, a0100, "02");
				}
				infofieldlist = self.getOneMyselfDataA01(chg_id, userbase, a0100, setname,this.userView, infofieldlist);
				this.getFormHM().put("infofieldlist", infofieldlist);
			} else if (setname != null && "A00".equals(setname)) {//多媒体信息
				
			} else {//其他子信息
				List list = (ArrayList) this.getFormHM().get("detailinfolist");
				//起草的记录
				String chg_id = self.getChgid(userbase, a0100, "01");
				//【7167】自助服务-我的信息-信息维护-申请修改，选择家庭子集，已批和新建状态的内容有重的现象 jingq upd 2015.02.02
				list = self.getOneMyselfDataOther(chg_id, setname, userbase, list, part_unit, part_setid,"01");
				//被驳回的记录
				chg_id = self.getChgid(userbase, a0100, "07");
				list = self.getOneMyselfDataOther(chg_id, setname, userbase, list, part_unit, part_setid,"07");
				//报批的记录
				chg_id = self.getChgid(userbase, a0100, "02");
				list = self.getOneMyselfDataOther(chg_id, setname, userbase, list, part_unit, part_setid,"02");
				this.getFormHM().put("pageinfolist", list);
			}
			//获得单位、部门、职位、姓名
			this.setOrgInfo(userbase, a0100,this.frameconn);
			OtherParam op=new OtherParam(this.getFrameconn());
			Map cardMap=op.serachAtrr("/param/formual[@name='bycardno']");
			//是否启用身份证关联结算
			String cardflag = "false";
			if(cardMap!=null&&cardMap.size()==6){
				cardflag=(String) cardMap.get("valid");
			}
			this.getFormHM().put("cardflag", cardflag);
			Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.frameconn);
			this.getFormHM().put("idType", othparam.getValue(Sys_Oth_Parameter.CHK_IdTYPE));
			this.getFormHM().put("idTypeValue", othparam.getIdTypeValue());
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 获得“单位/部门/职位/姓名”形式字符窜
	 * 
	 * @param userbase
	 * @param A0100
	 * @param dao
	 */
	private String setOrgInfo(String userbase, String A0100,
			Connection connection) {
		ContentDAO dao = new ContentDAO(connection);
		StringBuffer strsql = new StringBuffer();
		StringBuffer name = new StringBuffer();
		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		String a0101 = "";
		try {
			strsql.append("select b0110,e0122,e01a1,a0101 from ");
			strsql.append(userbase);
			strsql.append("A01 where a0100='");
			strsql.append(A0100);
			strsql.append("'");
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) {
				b0110 = this.getFrowset().getString("B0110");
				e0122 = this.getFrowset().getString("E0122");
				e01a1 = this.getFrowset().getString("E01A1");
				a0101 = this.getFrowset().getString("a0101");
			}
		} catch (Exception e) {

		} finally {
			if (b0110 != null && b0110.trim().length() > 0)
				b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode
						.getCode("UN", b0110).getCodename() : "";
			if (e0122 != null && e0122.trim().length() > 0)
				e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode
						.getCode("UM", e0122).getCodename() : "";
			if (e01a1 != null && e01a1.trim().length() > 0)
				e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode
						.getCode("@K", e01a1).getCodename() : "";
		}
		name.append(b0110);
		name.append("/");
		name.append(e0122);
		name.append("/");
		name.append(e01a1);
		name.append("/");
		name.append(a0101);
		this.getFormHM().put("b0110",b0110);
  	    this.getFormHM().put("e0122",e0122);
  	    this.getFormHM().put("e01a1",e01a1);//压回页面
  	    this.getFormHM().put("a0101",a0101);
		return name.toString();
	}
	
	

}
