package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/***
 * 
 * <p>Title: GetVirturalRoleTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  Nov 18, 2016 6:20:37 PM</p>
 * @author changxy 
 * @version 1.0
 * 查看虚拟组织成员
 */
public class GetSeachVirturalMemberTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String code=(String)this.getFormHM().get("code");
		code=PubFunc.decrypt(code.split("=")[1]);
		String codeid=code.split("=")[1];
		VirturalRoleTransBo bo=new VirturalRoleTransBo(this.frameconn,this.userView);
		ArrayList list=bo.getColunmList(codeid);
		String sql=bo.getVirturalColumsql(codeid,true);
		ArrayList buttonList=new ArrayList();
		ButtonInfo button=new ButtonInfo();
		button.setText("Excel导出");
		button.setFunctype("export");
		buttonList.add(button);
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name","1");
		String onlyViewName = "";
		if(onlyname!=null && onlyname.length()>0){
			GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
			ArrayList<CommonData> chklist = gf.getUsedFieldBySetNameTrans("A01",this.userView);
			for(CommonData comDa:chklist){
				if(comDa.getDataValue().equals(onlyname)){ 
					onlyViewName = (comDa.getDataName()).split(":")[1];break;
				}
			}
			if(onlyViewName.length()>0) onlyViewName = "、"+onlyViewName;
		}
		
		TableConfigBuilder tablebulider=new TableConfigBuilder("virtual_members_00002",list,"virtualmembers",this.userView,this.frameconn);
		tablebulider.setTitle("虚拟组织成员");
		tablebulider.setDataSql(sql);
		tablebulider.setOrderBy(" order by A0000");
		tablebulider.setScheme(false);//栏目设置
		if(onlyViewName.indexOf("姓名")>-1)//当唯一性指标为姓名时不重复添加姓名
			onlyViewName="";
		tablebulider.setSearchConfig("ORG0000017", "请输入姓名"+onlyViewName,false);
		tablebulider.setAutoRender(true);
		tablebulider.setTableTools(buttonList);
		String config=tablebulider.createExtTableConfig();
		this.getFormHM().clear();
		this.getFormHM().put("config",config);
		
		
		
	}

}
