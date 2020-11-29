package com.hjsj.hrms.module.system.qrcard.setting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.BeanUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
/**
*
* @Titile: SaveQRCardParamTrans
* @Description:二维码表单信息的添加，删除，修改
* @Company:hjsj
* @Create time:2018年8月7日10:18:20
* @author: wangwh
* @version 1.0
*
*/
public class SaveQRCardParamTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		// 最大qrid用于qrid的添加
		int maxQrid = 0;
		int num = 0;
		//设置标志位判断是否成功
		Boolean flag = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		List list = new ArrayList();
		UserView userview = this.userView;
		String createuser = userview.getUserName();
		//接收信息将传过来的数据转换成map
		HashMap qrCardDataMap = null;
    	try {
    	    
            qrCardDataMap = (HashMap) BeanUtils.describe(this.getFormHM().get("qrCardData"));		
            if(!"delete".equals(qrCardDataMap.get("type")) &&(qrCardDataMap.get("description") == null || ((String)qrCardDataMap.get("description")).trim().length()==0)){
            	this.getFormHM().put("result", flag);
            	this.getFormHM().put("return_msg", "1");
            	return;
            }
    		// 删除
    		if ("delete".equals(qrCardDataMap.get("type"))) {
    				StringBuffer sql = new StringBuffer("delete from t_sys_tipwizard_qrcord where qr_id =?");
    				list.add((String) qrCardDataMap.get("qrid"));
    				num = dao.delete(sql.toString(), list);
    				if (num > 0) {
    					flag = true;
    				}
    		}
    		// 修改
    		else if ("update".equals(qrCardDataMap.get("type"))) {
    				StringBuffer sql = new StringBuffer(
    						"update t_sys_tipwizard_qrcord set tab_id=?,qr_description=?,qr_name=?,b0110=? where qr_id=?");
    				list.add(qrCardDataMap.get("tabid"));
    				list.add(qrCardDataMap.get("description"));
    				list.add(qrCardDataMap.get("name"));
    				list.add(qrCardDataMap.get("b0110"));
    				list.add(qrCardDataMap.get("qrid"));
    				num = dao.update(sql.toString(), list);
    				if (num > 0) {
    					flag = true;
    				}
    		}
    		// 添加
    		else if ("add".equals(qrCardDataMap.get("type"))) {
    			     //获取当前时间
    		        Calendar calendar = Calendar.getInstance();
    		        Date date = DateUtils.getSqlDate(calendar);
    				StringBuffer sql = new StringBuffer("select max(qr_id) qr_id from t_sys_tipwizard_qrcord");
    				this.frowset = dao.search(sql.toString(), list);
    				if (this.frowset.next()) {
    					maxQrid = this.frowset.getInt("qr_id") + 1;
    				}
    				sql.delete(0, sql.length());
    				sql.append(
    						"insert into t_sys_tipwizard_qrcord (qr_id,tab_id,qr_name,qr_description,createuser,createtime,b0110) values (?,?,?,?,?,?,?)");
    				list.add(maxQrid);
    				list.add(qrCardDataMap.get("tabid"));
    				list.add(qrCardDataMap.get("name"));
    				list.add(qrCardDataMap.get("description"));
    				list.add(createuser);
    				list.add(date);
    				list.add(qrCardDataMap.get("b0110"));
    				num = dao.insert(sql.toString(), list);
    				if (num > 0) {
    					flag = true;
    				}
    		}
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.getFormHM().put("result", flag);
        } 
	}
}
