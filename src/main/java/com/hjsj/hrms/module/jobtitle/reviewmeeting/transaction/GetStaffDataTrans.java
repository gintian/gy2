package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ChooseStaffBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:GetStaffDataTrans </p>
 * <p>Description: 选择参会人员</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GetStaffDataTrans extends IBusiness {
	@Override
    @SuppressWarnings({ "unchecked", "unchecked" })
	public void execute() throws GeneralException {
		try {
			ChooseStaffBo bo = new ChooseStaffBo(this.frameconn,this.userView);
			String w0321 = (String)this.getFormHM().get("w0321");//评审会议编号
			int typeCommittee = (Integer)this.getFormHM().get("typeCommittee");//评审会议编号
			ArrayList buttonList = bo.getButtonList();//得到操作按钮
			ArrayList<ColumnsInfo> columnList = bo.getColumnList(w0321);//得到列头
			String w0301 = (String)this.getFormHM().get("w0301");//评审会议编号
			w0301 = PubFunc.decrypt(w0301);
			StringBuffer datasql =  new StringBuffer();//查询数据源sql
			datasql.append("select z.user_id,z.w0301,z.w0101,z.w0501,W01.w0103,W01.w0105,W01.w0107,z.username,z.password,z.state,z.group_id from zc_expert_user z");
			datasql.append(" left join (select W0101,W0103,W0105,W0107 from W01) W01 on z.W0101 = W01.W0101");
			datasql.append(" where z.W0301 = '"+ w0301 +"' and z.type ="+typeCommittee+" and z.W0501='xxxxxx'");

			TableConfigBuilder builder = new TableConfigBuilder("zc_choosestaff_00001", columnList, "choose", userView, this.getFrameconn());
			builder.setDataSql(datasql.toString());//数据查询sql语句
			builder.setAutoRender(false);//是否自动渲染表格到页面
			builder.setSetScheme(false);//是否可以设置栏目设置
			builder.setSelectable(true);//选框
			builder.setEditable(true);//表格编辑
			builder.setPageSize(20);//每页条数
			builder.setConstantName("jobtitle/choosePerson");
			if(w0321.indexOf("01")!=-1||w0321.indexOf("09")!=-1)//暂停或起草状态的评审会议才能维护参会人员
				builder.setTableTools(buttonList);//表格工具栏功能
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
