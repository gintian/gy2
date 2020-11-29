package com.hjsj.hrms.module.kq.kqdata.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.ArrayList;

/**
 * 考勤明细 导入月汇总
 * 
 * @date 2020.03.05
 * @author xuanz
 *
 */
public interface ImportKqDataMxService {
	/**
	 *  导入汇总数据
	 * @param fileid
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 * @param type
	 */
	void importKqData(String fileid, String scheme_id, String kq_duration, String kq_year, String org_id, String type);
	/**
	 * 获取模板数据的异常信息
	 */
	public String getErrorMsg();
	/**
	 * 
	 * 保存导入数据
	 * @param kqDataList
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 */
	void saveCardData(ArrayList<Object> kqDataList,String scheme_id,String kq_duration,String kq_year,String org_id);
	/**
	 * 		下载导入数据模板
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 * @param showMx
	 * @param type
	 * @param service
	 * @return
	 * @throws GeneralException
	 */
	String doTypeExportExl(String scheme_id, String kq_duration, String kq_year, String org_id, String showMx, String type, KqDataMxService service)
			throws GeneralException;
}
