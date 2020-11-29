package com.hjsj.hrms.module.kq.kqdata.transaction;


import com.hjsj.hrms.module.kq.kqdata.businessobject.ImportKqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.ImportKqDataMxServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataMxServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * 考勤明细 导入月汇总交易类
 * 
 * @date 2020.03.05
 * @author xuanz
 *
 */
public class ImportKqDataMxTrans extends IBusiness {
	private static final long serialVersionUID = 1L;
    private String kq_year = "";
    private String kq_duration = "";
    private String org_name = "";
    private String type = "";
    private String flag = "";
	@Override
    public void execute() throws GeneralException {
		String jsonStr = (String)this.formHM.get("jsonStr");
		//获取前台json数据
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.frameconn);
		ImportKqDataMxService ImportService = new ImportKqDataMxServiceImpl(this.userView, this.frameconn);
		JSONObject returnJson = new JSONObject();
		String return_code="success";
		String return_msg = "success";
		try {
			type = jsonObj.getString("type");
			String scheme_id = jsonObj.getString("scheme_id");
			kq_duration = jsonObj.getString("kq_duration");
			kq_year = jsonObj.getString("kq_year");
			flag=jsonObj.getString("flag");
			String showMx = jsonObj.getString("showMx");
			String org_id = jsonObj.getString("org_id")==null?"":(String)jsonObj.getString("org_id");//多个以逗号分隔
			if(StringUtils.isNotBlank(org_id) && !org_id.contains(",")){
				String unName = AdminCode.getCodeName("UN",PubFunc.decrypt(org_id));
				String umName = AdminCode.getCodeName("UM",PubFunc.decrypt(org_id));
				org_name = StringUtils.isBlank(unName)?umName:unName;
			}
			if("down".equals(flag)&&("daily".equals(type) || "collect".equals(type))) {
				
				String fileName = ImportService.doTypeExportExl(scheme_id, kq_duration, kq_year, org_id, showMx, type, service);
				/**
				 * 返回标识
				 * =1未设置月汇总统计指标
				 */
				if("1".equals(fileName)) {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.date.collect.itemnull.error");
				}else {
					JSONObject obj = new JSONObject();
					obj.put("filename", SafeCode.encode(PubFunc.encrypt(fileName)));
					returnJson.put("return_data", obj);
				}
			}else if("import".equals(flag)&&"collect".equals(type))  {
				String fileid = jsonObj.getString("fileid");
				ImportService.importKqData(fileid, scheme_id, kq_duration, kq_year, org_id, type);
				String errorMsg = ImportService.getErrorMsg();
				return_msg = (String)this.getUserView().getHm().get("errorMsg");
				if (StringUtils.isNotEmpty(return_msg)) {
				    return_code="fail";
                }
				JSONObject obj = new JSONObject();
				obj.put("list", errorMsg);
				returnJson.put("return_data", obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.date.error.export");
		}finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
		}
		this.formHM.put("returnStr", returnJson.toString());
		
	}
}
