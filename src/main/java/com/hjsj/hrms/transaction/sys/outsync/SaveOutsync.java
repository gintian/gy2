package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveOutsync extends IBusiness 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException 
	{
		RecordVo vo = (RecordVo) this.getFormHM().get("record");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String jobId = (String) this.getFormHM().get("jobId");
		
		vo.setString("targetnamespace", PubFunc.keyWord_reback(vo.getString("targetnamespace")));
		vo.setString("url", PubFunc.keyWord_reback(vo.getString("url")));
		
		ArrayList list = new ArrayList();
		list.add(vo.getString("sys_id"));
		// ----------------------保存开始------------------------------
		RowSet re = null;
		try 
		{
			re = dao.search("select * from t_sys_outsync where sys_id='"
							+ vo.getString("sys_id") + "'");
			String state = vo.getString("state");
			vo.setInt("state", Integer.parseInt(state));
			String other_param = SafeCode.decode(vo.getString("other_param"));
			
			if (other_param == null || other_param.trim().length() <= 0) {
				other_param = "<?xml version='1.0' encoding='UTF-8'?><params><jobId>" + jobId+ "</jobId></params>";
			} else {
				PareXmlUtils utils = new PareXmlUtils(other_param);
				utils.setTextValue("/params/jobId", jobId);
				other_param = utils.getDocumentString();
			}
			
			vo.setString("other_param", other_param);
			if (re.next()) {
				// ----------------------修改开始------------------------------
				if(vo.getInt("state") == 0){
					vo.setInt("fail_time", 0);
				}
				dao.updateValueObject(vo);
				// ----------------------修改结束------------------------------
			} else {
				// ----------------------保存开始------------------------------
				vo.setInt("state", Integer.parseInt(state));
				vo.setInt("fail_time", 0);
				dao.addValueObject(vo);
				// ----------------------保存结束------------------------------
			}
			//-------------------
			if ("1".equals(vo.getString("state"))) {
				new HrSyncBo(this.frameconn).addSysOutsyncFlag("t_org_view",
						list);
				new HrSyncBo(this.frameconn).addSysOutsyncFlag("t_hr_view",
						list);
				new HrSyncBo(this.frameconn).addSysOutsyncFlag("t_post_view",
						list);
			} else {
				new HrSyncBo(this.frameconn).delSysOutsyncFlag("t_org_view",
						list);
				new HrSyncBo(this.frameconn).delSysOutsyncFlag("t_hr_view",
						list);
				new HrSyncBo(this.frameconn).delSysOutsyncFlag("t_post_view",
						list);
			}
			//-------------------xus 19/9/9勾选了同步照片,在t_hr_view表中加上  接口代号+P 字段
			//  如果同步参数设置中勾选了同步照片，则加上hrcloudp字段
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			String photo = hsb.getAttributeValue(HrSyncBo.photo);
			photo=photo!=null&&photo.trim().length()>0?photo:"0";
			DbWizard dbw = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
			Table hr_view_table = new Table("t_hr_view");
			//19/9/9 【53107】v7.6.1封版：数据视图，勾选了同步照片，外部系统配置，新增的时候自动在t_hr_view表中加上  接口代号+P 字段。
			if(!"0".equals(photo) && vo.getInt("state") == 1){
				if(!dbw.isExistField("t_hr_view", vo.getString("sys_id")+"p",false)) {
					Field item = new Field(vo.getString("sys_id")+"p", vo.getString("sys_id")+"p");
					item.setDatatype(DataType.INT);
					item.setLength(2);
					hr_view_table.addField(item);
					dbw.addColumns(hr_view_table);
					dbmodel.reloadTableModel("t_hr_view");
					String updsql = "update t_hr_view set "+vo.getString("sys_id")+"p = 1 ";
					dao.update(updsql);
				}
			}else{
				if(dbw.isExistField("t_hr_view", vo.getString("sys_id")+ "p",false)) {
					Field item = new Field(vo.getString("sys_id")+"p", vo.getString("sys_id")+"p");
					hr_view_table.addField(item);
					dbw.dropColumns(hr_view_table);
					dbmodel.reloadTableModel("t_hr_view");
				}
			}
		} catch (SQLException e) {
			throw new GeneralException(e.getMessage());
		}finally
		{
			if(re != null)
			{
				try {
					re.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
