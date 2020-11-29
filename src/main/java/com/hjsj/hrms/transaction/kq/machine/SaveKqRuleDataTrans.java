package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
/**
 * 保存文件规则
 * <p>Title:SaveKqRuleDataTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 29, 2006 4:51:30 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveKqRuleDataTrans  extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String tran_flag = "", opt = "";
			DynaBean bean = (LazyDynaBean) this.getFormHM().get("codesetvo");
			opt = (String) this.getFormHM().get("opt");
			opt = null == opt || opt.trim().length() <= 0 ? "add" : opt.trim();
			String id = (String) this.getFormHM().get("id");
	
			if ("add".equalsIgnoreCase(opt)) {
	
				if (bean != null) {
					tran_flag = (String) bean.get("tran_flag");
				}
	
				if (tran_flag == null || tran_flag.length() <= 0)
					tran_flag = (String) this.getFormHM().get("tran_flag");
	
				if (tran_flag == null || tran_flag.length() <= 0)
					return;
	
				String rule_id = "";
				if ("1".equals(tran_flag)) {
					/******* 保存规则 ********/
					RecordVo kq_rule_vo = (RecordVo) this.getFormHM().get("kq_rule_vo");
					rule_id = saveRecordVO(kq_rule_vo);
				} else if ("2".equals(tran_flag)) {
					/******* 保存规则名称 ********/
					// String rule_name=(String)this.getFormHM().get("rule_name");
					String rule_name = (String) bean.get("rule_name");
					rule_id = saveRuleName(rule_name);
					this.getFormHM().put("rule_name", rule_name);
				}
				if (rule_id == null || rule_id.length() <= 0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							"请先创建文件规则名称", "", ""));
				String newid = rule_id.replaceFirst("[^0]", "#");
				rule_id = rule_id.substring(newid.indexOf("#"));
				this.getFormHM().put("rule_id", rule_id);
				this.getFormHM().put("tran_flag", "");
	
			} else if ("edit".equalsIgnoreCase(opt)) {
				String name = (String) bean.get("rule_name");
				this.editRuleName(id, name);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 保存新建规则名称
	 * 
	 * @param rule_name
	 * @return
	 * @throws GeneralException
	 */
	public String saveRuleName(String rule_name) throws GeneralException {
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		RecordVo kq_rule_vo = new RecordVo("kq_data_rule");
		String rule_id = "";
		try {
			rule_id = idg.getId("kq_data_rule.rule_id").toUpperCase();
			kq_rule_vo.setInt("rule_id", Integer.parseInt(rule_id));
			kq_rule_vo.setString("rule_name", rule_name);
			kq_rule_vo.setInt("machine_s", 0);
			kq_rule_vo.setInt("machine_e", 0);
			kq_rule_vo.setInt("card_s", 0);
			kq_rule_vo.setInt("card_e", 0);
			kq_rule_vo.setInt("year_s", 0);
			kq_rule_vo.setInt("year_e", 0);
			kq_rule_vo.setInt("md_s", 0);
			kq_rule_vo.setInt("md_e", 0);
			kq_rule_vo.setInt("hm_s", 0);
			kq_rule_vo.setInt("hm_e", 0);
			kq_rule_vo.setInt("status", 0);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.addValueObject(kq_rule_vo);
		} catch (Exception e) {
			throwSaveRuleException();
		}
		return rule_id;
	}

	/**
	 * 保存规则
	 * 
	 * @param kq_rule_vo
	 * @return
	 * @throws GeneralException
	 */
	public String saveRecordVO(RecordVo kq_rule_vo) throws GeneralException {
		String rule_id = kq_rule_vo.getString("rule_id");
		String status_value = (String) this.getFormHM().get("status_value");
		if (status_value == null || status_value.length() <= 0)
			status_value = "0";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			kq_rule_vo.setString("status", status_value);
			dao.updateValueObject(kq_rule_vo);
		} catch (Exception e) {
			throwSaveRuleException();
		}
		return rule_id;
	}

	/**
	 * 修改 名稱
	 * 
	 * @param id
	 * @param name
	 * @throws GeneralException
	 */
	private void editRuleName(String id, String name) throws GeneralException {

		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo kq_rule_vo = new RecordVo("kq_data_rule");
		kq_rule_vo.setString("rule_id", id);
		kq_rule_vo.setString("rule_name", name);

		try {
			int count = dao.updateValueObject(kq_rule_vo);
			if (count > 0) {
				this.getFormHM().put("rule_id", id);
				this.getFormHM().put("rule_name", name);
				this.getFormHM().remove("opt");
			}

		} catch (Exception e) {
			throwSaveRuleException();
		}
	}
	
	private void throwSaveRuleException() throws GeneralException {
		throw new GeneralException("",
					ResourceFactory.getProperty("kq.register.work.error"), "",
					"");
	}
}
