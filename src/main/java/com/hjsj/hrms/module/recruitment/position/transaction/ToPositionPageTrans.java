package com.hjsj.hrms.module.recruitment.position.transaction;


import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.mortbay.util.ajax.JSON;

import java.util.HashMap;
/**
 * 
 * <p>Title: ToPositionPageTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-26 下午03:31:31</p>
 * @author xiongyy
 * @version 1.0
 */
public class ToPositionPageTrans extends IBusiness {



    @Override
    public void execute() throws GeneralException {
        try {
            PositionBo pobo = new PositionBo(this.getFrameconn(), new ContentDAO(this.getFrameconn()), this.getUserView());
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String z0319 =SafeCode.decode((String) hm.get("z0319"));
            String from = (String) hm.get("from");
            hm.remove("from");
            String pageNum =(String) hm.get("pageNum");
            String searchStr =(String) hm.get("searchStr");
            String pagesize =(String) hm.get("pagesize");
            String iscontinue = (String) hm.get("iscontinue");
            StringBuffer pageDesc = new StringBuffer();  //做返回用的条件
            pageDesc.append(pageNum+"`");
            pageDesc.append(searchStr+"`");
            pageDesc.append(pagesize);
            
            UserView userview = this.getUserView();
            String responsPosiName = userview.getUserFullName();
            String reponsA0100 = userview.getDbname()+userview.getA0100();
            if(reponsA0100!=null&&reponsA0100.length()>0)
                reponsA0100 = PubFunc.encrypt(reponsA0100);
            String photosrc = pobo.getResponsPosiPhoto(userview.getA0100());
            String z0301 = PubFunc.decrypt((String) hm.get("z0301"));
            hm.remove("z0301");
            String type =pobo.getMemTypeByUserView(z0301);
            String isPublish="";
            if(z0301==null||z0301.length()==0){
                isPublish = pobo.getPriveForPublish();
            }else{            	
            	String havaPerson = pobo.getPersonNum(z0301);//获取当前职位下的人员数量
            	this.getFormHM().put("havaPerson", havaPerson);
            }
            
            String hide_zp_rember = SystemConfig.getPropertyValue("hide_zp_rember");
            String display = "block";
            if("true".equalsIgnoreCase(hide_zp_rember))
            	display = "none";
            
            
            String jsonStr = pobo.getPositionJson(z0301);//生成页面的json格式的字符串
            String zp_pos_apply_start_field = SystemConfig.getPropertyValue("zp_pos_apply_start_field");
    	    String zp_pos_apply_end_field = SystemConfig.getPropertyValue("zp_pos_apply_end_field");
            if(!pobo.getItemInZ03(zp_pos_apply_start_field) || !pobo.getItemInZ03(zp_pos_apply_end_field)){
            	zp_pos_apply_start_field = "";
            	zp_pos_apply_end_field = "";
            }
            
            this.getFormHM().put("zp_pos_apply_start_field", zp_pos_apply_start_field);
            this.getFormHM().put("zp_pos_apply_end_field", zp_pos_apply_end_field);
            this.getFormHM().put("display", display);
            this.getFormHM().put("responsPosiName", responsPosiName);
            this.getFormHM().put("reponsA0100", reponsA0100);
            this.getFormHM().put("photosrc", photosrc);
            this.getFormHM().put("responsPosiName", responsPosiName);
            this.getFormHM().put("jsonStr", jsonStr);
            this.getFormHM().put("z0319", z0319);
            this.getFormHM().put("from", from==null?"":from);//从什么地方进入的职位详情
            this.getFormHM().put("isPublish", isPublish);
            this.getFormHM().put("pageDesc", pageDesc.toString());
            this.getFormHM().put("type", type);
            this.getFormHM().put("iscontinue", iscontinue);
            this.getFormHM().put("privChannel", JSON.toString(pobo.getChannelJson()));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        
        
        
    }

}
