package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateDateExportBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeExportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CertificateDateExportTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        String errorMsg = "";
        try {
            CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
            String fieldSetId = bo.getCertSubset();
            String certNOItemId = bo.getCertNOItemId();
            ArrayList<String> userbaseList = bo.getCertNbase();
            String recordFieldSet = bo.getCertBorrowSubset();
            String certStatus = bo.getCertStatus();
            String certBorrowState = bo.getCertBorrowState();
            String certCategoryCode = bo.getCertCategoryCode();
            // 证书信息集证书类别指标
            String certCategoryItemId = bo.getCertCategoryItemId();
            // 证书信息集证书到期日期指标
            String certEndDateItemId = bo.getCertEndDateItemId();
            // 证书信息集证书名称
            String certName = bo.getCertName();
            if (userbaseList == null || userbaseList.size() == 0) {
                errorMsg = "人员库不能为空！";
                return;
            }

            if (StringUtils.isEmpty(fieldSetId)) {
                errorMsg = "请设置证书子集！";
                return;
            }

            if (StringUtils.isEmpty(certNOItemId)) {
                errorMsg = "请设置证书编号指标！";
                return;
            }

            if (StringUtils.isEmpty(recordFieldSet)) {
                errorMsg = "请设置证书借阅记录子集！";
                return;
            }

            if (StringUtils.isEmpty(certStatus)) {
                errorMsg = "请设置证书状态指标！";
                return;
            }
            
            if (StringUtils.isEmpty(certCategoryCode)) {
                errorMsg = "请设置证书类别代码类！";
                return;
            }

            if (StringUtils.isEmpty(certCategoryItemId)) {
                errorMsg = "请设置证书类别指标！";
                return;
            }

            if (StringUtils.isEmpty(certEndDateItemId)) {
                errorMsg = "请设置证书结束日期指标！";
                return;
            }

            if (StringUtils.isEmpty(certName)) {
                errorMsg = "请设置证书名称指标！";
                return;
            }

            if (StringUtils.isEmpty(certBorrowState)) {
                errorMsg = "请设置证书是否借出指标！";
                return;
            }
            
            TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("certificateManage_001");
            String filterSql = tableCache.getFilterSql();
            ArrayList columns = tableCache.getDisplayColumns();
            String sql = tableCache.getTableSql();
            String querySql1 = tableCache.getQuerySql();
            String sortSql = tableCache.getSortSql();
           
            sql = "select * from (" + sql + ") myGridData where 1=1 ";
            if(StringUtils.isNotEmpty(querySql1))
                sql += querySql1;
            
            if(StringUtils.isNotEmpty(filterSql))
                sql += filterSql;
            
            String certificateIds = (String) this.getFormHM().get("certificateIds");
            String[] certificates = certificateIds.split(",");
            StringBuffer where = new StringBuffer();
            HashMap<String, HashMap<String, String>> certificateMap = new HashMap<String, HashMap<String, String>>();
            for (String certificate : certificates) {
                if(StringUtils.isEmpty(certificate))
                    continue;
                
                String[] certificateinfo = certificate.split(":");
                if(StringUtils.isNotEmpty(where.toString()))
                    where.append(" or ");
                
                where.append("(" + certCategoryItemId + "='" + certificateinfo[0].split("`")[0] + "'");
                where.append(" and " + certNOItemId + "='" + certificateinfo[1] + "')");
            }
            
            if(StringUtils.isNotEmpty(where.toString()))
                sql += " and (" + where + ")";
                
            sql += sortSql;
            
            CertificateDateExportBo exportBo = new CertificateDateExportBo(this.frameconn, this.userView); 
            //表头字段列表
            ArrayList<LazyDynaBean> mergedCellList = exportBo.getExcleMergedList(columns);
            ArrayList<LazyDynaBean> headList = exportBo.getExcleHeadList(columns, mergedCellList, false, 0); 
            String fileName = this.userView.getUserName() + "_证书信息.xls";
            ResumeExportExcelBo excelBo = new ResumeExportExcelBo(this.frameconn);
            int headStartRowNum = 0;
            if(!mergedCellList.isEmpty())
                headStartRowNum = 1;
            excelBo.exportExcelBySql(fileName, null, mergedCellList, headList, sql, null, headStartRowNum);
            this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
        } finally {
            this.getFormHM().put("errorMsg", errorMsg);
        }
        
    }

}
