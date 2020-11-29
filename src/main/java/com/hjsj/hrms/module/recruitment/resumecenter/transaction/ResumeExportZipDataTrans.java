package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.interfaces.ResumeInterface;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 简历中心导出文件交易类 
 * @Title:        ResumeExportZipDataTrans.java
 * @Description:  用于简历中心导出压缩文件，注：当a0100不为空时，导出a0100中包含的人员；当a0100为空时，导出全部的简历。
 * @Company:      hjsj     
 * @Create time:  2016-5-3 下午04:00:33
 * @author        chenxg
 * @version       1.0
 */
public class ResumeExportZipDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String nbase = "";
            RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
            if(vo != null)
                nbase=vo.getString("str_value");
            
            if(StringUtils.isEmpty(nbase))
                throw new GeneralException("", "未设置招聘人员库,请到招聘设置-参数设置-后台参数设置招聘人员库！", "", "");
            
            String a0100 = (String) this.getFormHM().get("a0100");// 选中的要导出的人员的a0100
            ArrayList<String> empList = new ArrayList<String>();
            if(StringUtils.isNotEmpty(a0100)){
                String[] a0100s = a0100.split(",");
                for (int i = 0; i < a0100s.length; i++) {
                    String personId = a0100s[i];
                    if(StringUtils.isNotEmpty(personId))
                        empList.add(nbase + PubFunc.decrypt(personId));
                }
            } else {
                TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_resume_191130_00001");
                String filterSql = tableCache.getFilterSql();
                String sql = (String) this.userView.getHm().get("hire_sql");
                
                if(StringUtils.isNotEmpty(filterSql))
                    sql = "select * from (" + sql + ") jianli where 1=1 " + filterSql;
                    
                ContentDAO dao = new ContentDAO(this.frameconn);
                this.frowset = dao.search(sql + " order by recdate desc");
                while (this.frowset.next())
                    empList.add(nbase + this.frowset.getString("A0100"));
            }
            
            if(empList.size()==0) {
            	this.getFormHM().put("infor", "未选中有效数据！");
            	return;
            }
            
            ResumeInterface bo = new ResumeInterface(this.frameconn, this.userView);
            String outName = bo.ExportResumeZip(empList);
            this.getFormHM().put("zipname", outName);
            this.getFormHM().put("infor", "ok");
        } catch (Exception e) {
            this.getFormHM().put("infor", e.toString());
            e.printStackTrace();
        }

    }

}
