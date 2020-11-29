package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 职称评审_上会材料_差额投票
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 */
@SuppressWarnings("serial")
public class ReviewDiffTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			String type = (String) this.getFormHM().get("type");
			String w0301 = PubFunc.decrypt((String) this.getFormHM().get("w0301_e"));
			String review_links = (String) this.getFormHM().get("review_links");
			/** 
			 * 1：选择申报人页面快速查询
			 * 2：添加分组
			 * 3：修改分组信息
			 * 4：删除分组
			 * 5：应选人数设置保存
			 * 6：更新会议状态
			 * 7：分组页面的表格控件整理
			 * 8：暂停投票
			 * 9：
			 * 10：添加申报人
			 * 11：删除申报人
			 * 12：分组表结构同步
			 * 13：选择申报人页面表格控件整理
			 * 14：
			 * 15：获取会议进度和状态
			 *  
			 *  */
			if("1".equals(type)) {// fast search
				String subModuleId = (String) this.getFormHM().get("subModuleId");
				if("jobtitle_reviewfile_diff_selperson".equals(subModuleId)){
					String _type = (String)this.getFormHM().get("type");
					if("1".equals(_type)) {// 1:输入查询
						ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
						ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
						bo.diffSelPersonFastSearch(valuesList);
					}
				}
				
				
			} else if("2".equals(type)) {// add categories
				String categories_name = (String) this.getFormHM().get("categories_name");
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.addCategorie(w0301, review_links, categories_name);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("3".equals(type)) {// update categories
				ArrayList<DynaBean> savedata = (ArrayList<DynaBean>) this.getFormHM().get("savedata");
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				bo.updateCategorie(savedata);
				this.getFormHM().put("errorcode", 0);
				
			} else if("4".equals(type)) {// delete categories
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.deleteCategorie(categories_id);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("5".equals(type)) {// save config
				String ids = (String) this.getFormHM().get("ids");
				
				HashMap<String, String> configMap = new HashMap<String, String>();
				configMap.put(review_links, ids);
				
				ReviewMeetingBo bo = new ReviewMeetingBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.saveW03Ctrl_param(w0301, review_links, configMap);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("6".equals(type)) {// update approval_state
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				String approval_state = (String) this.getFormHM().get("approval_state");
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.updateApproval_state(categories_id, approval_state);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("7".equals(type)) {// get tableconfig
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				String config = bo.getTableConfigForDiff(w0301, review_links);
				this.getFormHM().put("tableConfig", config.toString());
				
				HashMap<String, String> categoriesmap = bo.getCategoriesMap(w0301, review_links);
				this.getFormHM().put("categoriesmap", categoriesmap);

				HashMap<String, ArrayList<HashMap<String, String>>> personmap = bo.getCategories_relations(w0301, review_links);
				this.getFormHM().put("personmap", personmap);
				
				this.getFormHM().put("w0575codesetid", DataDictionary.getFieldItem("W0575").getCodesetid());

				ReviewMeetingBo mbo = new ReviewMeetingBo(this.getFrameconn(), this.getUserView());
				String value = mbo.getW03Ctrl_param(w0301).get(review_links);
				this.getFormHM().put("ctrl_param", ","+value+",");
				
			} else if("8".equals(type)) {// stop review
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				bo.stopCategories(categories_id);
				bo.updateApproval_state(categories_id, "3");
				this.getFormHM().put("errorcode", 0);
				
			} else if("10".equals(type)) {// add person
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				ArrayList<String> w0501_eList = (ArrayList<String>) this.getFormHM().get("w0501_eList");
				String c_level = (String) this.getFormHM().get("c_level");
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.addCategories_relations(categories_id, w0501_eList, c_level);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("11".equals(type)) {// delete person
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				String w0501 = PubFunc.decrypt((String) this.getFormHM().get("w0501"));
				String c_level = (String) this.getFormHM().get("c_level");
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.deleteCategories_relations(categories_id, w0501, c_level);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("12".equals(type)) {// async table zc_personnel_categories
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				int errorcode = bo.asyncTableCategories();
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("13".equals(type)) {// selectperson tableconfig
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				String config = bo.getTableConfigForDiffSelPerson(w0301, review_links);
				this.getFormHM().put("tableConfig", config.toString());
				
			} else if("14".equals(type)) {
				
			} else if("15".equals(type)) {// get approval_state Progress
				
				ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
				HashMap<String, String> map = bo.getProgressAndApprovalState(w0301, review_links);
				
				this.getFormHM().put("progressstatemap", map);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
