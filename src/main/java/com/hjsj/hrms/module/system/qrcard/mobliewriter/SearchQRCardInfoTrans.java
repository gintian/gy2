package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchQRCardInfoTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String qrid = (String) this.formHM.get("qrid");//二维码表单id
        ContentDAO dao = null;
        RowSet rs = null;
        
        try {
            
            dao = new ContentDAO(this.frameconn);
            StringBuffer sql = new StringBuffer();
            Map<String,String> qrData = new HashMap<String,String>();//二维码数据集合
            sql.append("select tab_id,qr_name,detail_description,b0110 from t_sys_tipwizard_qrcord where qr_id = ?");
            List<Object> param = new ArrayList<Object>();
            param.add(qrid);
            rs = dao.search(sql.toString(),param);
            if(rs.next()) {
                String tab_id = rs.getString("tab_id");//关联业务表单id
                TemplateTableBo templateBo = new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.getUserView());
                String base = templateBo.getDestBase();
                String qr_name = rs.getString("qr_name");//表单名称
                String detail_description = rs.getString("detail_description");//表单内容描述
                String org = rs.getString("b0110");
                qrData.put("tab_id", tab_id);
                qrData.put("qr_name", qr_name);
                qrData.put("detail_description", detail_description);
                qrData.put("org", org);
                qrData.put("base", base);
                if(StringUtils.isNotEmpty(base)) {
                    this.userView.setDbname(base);
                }
                
            }
            this.formHM.put("qrData",qrData);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                PubFunc.closeResource(rs);
        }
    }

}
