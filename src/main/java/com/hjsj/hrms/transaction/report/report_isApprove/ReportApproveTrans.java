package com.hjsj.hrms.transaction.report.report_isApprove;

import com.hjsj.hrms.businessobject.report.TpageBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class ReportApproveTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String unitcode = (String) this.getFormHM().get("unitcode1");
			String mainbody_id = (String) this.getFormHM().get("mainbody_id");
			String tabid = (String) this.getFormHM().get("tabid");
			String appuser = "";
			String username = "";
			String description = "";
			String[] str_tabid = tabid.split("/");
			/*校验参数（补表）必填项start*/
			TpageBo tpageBo = new TpageBo(this.frameconn);
			for(int i = 0; i < str_tabid.length; i++) {
				if(StringUtils.isBlank(str_tabid[i]))
					continue;
				RecordVo treport_vo=new RecordVo("tname");
				treport_vo.setInt("tabid", Integer.parseInt(str_tabid[i]));
				treport_vo=dao.findByPrimaryKey(treport_vo);
				int tsortid = treport_vo.getInt("tsortid");
				ArrayList pageAndParamList = tpageBo.getPageListAndTparam2(str_tabid[i], tsortid, unitcode);
				HashMap paramMap = (HashMap) pageAndParamList.get(1);
				Collection<HashMap> values = paramMap.values();
				for (HashMap obj : values) {
					if("1".equals(obj.get("paramnull"))) {
						this.frowset = dao.search("select "+obj.get("paramename")+" from tp_s"+tsortid+" where unitcode='"+getUserView().getUserId()+"'");
						if(this.frowset.next()&&StringUtils.isBlank(this.frowset.getString(1)))
							throw new Exception(str_tabid[i]+"号表，补表中有必填项没填！");
					}
				}
			}
			/*校验参数（补表）必填项end*/
			for (int i = 0; i < str_tabid.length; i++) {
				if(StringUtils.isBlank(str_tabid[i]))
					continue;

				String sql = "select appuser,username,description from treport_ctrl where unitcode = '" + unitcode
						+ "' and tabid = '" + str_tabid[i] + "'";
				RowSet rs = dao.search(sql);
				if (rs.next()) {
					if (rs.getString("appuser") == null) {
						appuser = "";

					} else {
						appuser = rs.getString("appuser");
					}
					if (rs.getString("description") == null || "".equals(rs.getString("description"))) {
						description = "";
					} else {
						description = rs.getString("description");
					}
					username = rs.getString("username");
				}
				if (username == null) {
					username = this.getUserView().getUserName();
				}
				appuser = appuser + ";" + getappuser(mainbody_id, username);
				description = description + ";" + getTime() + " " + this.getUserView().getUserFullName() + " "
						+ "审批意见：数据准确，同意上报！上报给：" + getappuser(mainbody_id, username);
				RecordVo vo = new RecordVo("treport_ctrl");
				vo.setString("unitcode", unitcode);
				vo.setInt("tabid", Integer.parseInt(str_tabid[i]));
				vo.setInt("status", 4);
				vo.setString("currappuser", mainbody_id);
				vo.setString("username", username);
				vo.setString("appuser", appuser);
				vo.setString("description", description);
				dao.updateValueObject(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public String getappuser(String mainbody_id ,String username){
		String appuser = "";
		try{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql = "select a0101,mainbody_id from t_wf_mainbody where mainbody_id = '"+mainbody_id+"' and object_id = '"+username+"'";
		RowSet rowset = dao.search(sql);
		if(rowset.next()){
			appuser = rowset.getString("a0101");
			if(appuser==null|| "".equals(appuser)){
				appuser = rowset.getString("mainbody_id");
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return appuser;
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public String getTime(){
		String time = "";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式
			Date nowDate = new Date();//得到当前时间
			time = sdf.format(nowDate );
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return time;
	}

}
