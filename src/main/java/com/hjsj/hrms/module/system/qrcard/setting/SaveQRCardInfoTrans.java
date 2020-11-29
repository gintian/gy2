package com.hjsj.hrms.module.system.qrcard.setting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("serial")
/**
*
* @Titile: SaveQRCardInfoTrans
* @Description:本类用于更新二维码表单内容信息
* @Company:hjsj
* @Create time:2018年8月7日10:17:26
* @author: wangwh
* @version 1.0
*
*/
public class SaveQRCardInfoTrans extends IBusiness {
        @Override
        @SuppressWarnings("unchecked")
        public void execute() throws GeneralException {
            // 本类用于更新二维码表单内容信息
            int num = 0;
            //标志位用于判断执行是否成功
            boolean flag = false;
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            List<String> List = new ArrayList<String>();
            //获取二维码表单id号
            String qrid = (String) this.getFormHM().get("qrid");
            //获取二维码表单内容信息
            String description = (String) this.getFormHM().get("description");
            try {
                RecordVo recordVo = new RecordVo("t_sys_tipwizard_qrcord");
                recordVo.setInt("qr_id",Integer.parseInt(qrid));
                recordVo.setString("detail_description",description);
                num = dao.updateValueObject(recordVo);
                if (num > 0) {
                    flag = true;
                }              
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                this.getFormHM().put("result", flag);
            }
        } 
    }

