package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ManProjectHoursBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>Title: ManProjectHoursAddAndEditTrans </p>
 * <p>Description: 添加修改项目数据</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-28 下午1:03:17</p>
 * @author liuyang
 * @version 1.0
 */
public class ManProjectHoursAddAndEditTrans  extends IBusiness{
    @Override
    public void execute() throws GeneralException {
       try{
            ManProjectHoursBo bo = new ManProjectHoursBo(this.getFrameconn(),this.getUserView());
         
            MorphDynaBean dataList = (MorphDynaBean)this.getFormHM().get("mapList");
            
            String type = (String)this.getFormHM().get("type");
            
            if("edit".equals(type)){
                ArrayList managerId = (ArrayList)this.getFormHM().get("managerId");
                ArrayList memebersId = (ArrayList)this.getFormHM().get("memebersId");
                memebersId.add(managerId.get(0));
                
                String projectId = (String)this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                
                String newData = bo.editProject(dataList,memebersId,projectId);
                this.getFormHM().put("newData", newData);
            }
            else if("add".equals(type)){
                ArrayList managerId = (ArrayList)this.getFormHM().get("managerId");
                ArrayList memebersId = (ArrayList)this.getFormHM().get("memebersId");
                memebersId.add(managerId.get(0));
                String newData = bo.addProject(dataList,memebersId);
                this.getFormHM().put("newData", newData);
            }
            else if("landMarkAdd".equals(type)){
                String projectId = (String)this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                
                bo.addLandMark(dataList,projectId);
                ArrayList newData = bo.getChildren(projectId, "projectmanage_0001");
                this.getFormHM().put("newData", newData);
            }
            else if("landMarkEdit".equals(type)){
                String projectId = (String)this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                
                String landMarkId = (String)this.getFormHM().get("landMarkId");
                landMarkId = this.decryptParam(landMarkId);
                
                String newData = bo.editLandMark(dataList,projectId,landMarkId);
                this.getFormHM().put("newData", newData);
            }
            else if("editProjectManager".equals(type)){
                String tip = "";
                String projectId = (String) this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                String beforeManId = (String) this.getFormHM().get("beforeManId");
                beforeManId = this.decryptParam(beforeManId);
                MorphDynaBean afterManMap = (MorphDynaBean) this.getFormHM().get("afterManMap");
                String memberToManager = (String) this.getFormHM().get("memberToManager");
                String endDate = (String) this.getFormHM().get("endDate");
                tip = bo.editProjectManager(projectId,beforeManId,afterManMap,memberToManager,endDate);
                this.getFormHM().put("result", tip);
            }
            else if("beforeDeleMember".equals(type)){
                String projectId = (String) this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                String manId = (String) this.getFormHM().get("manId");
                manId = this.decryptParam(manId);
                String result = bo.beforeDeleMember(projectId,manId);
                this.getFormHM().put("result", result);
            }
            else if("deleMember".equals(type)){
                String projectId = (String) this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                String manId = (String) this.getFormHM().get("manId");
                manId = this.decryptParam(manId);
                String result = bo.updateIsNotExisMembers(manId,projectId);
                this.getFormHM().put("result", result);
            }
            else if("addMembers".equals(type)){
                String projectId = (String) this.getFormHM().get("projectId");
                projectId = this.decryptParam(projectId);
                String beginDate = (String) this.getFormHM().get("beginDate");
                String endDate = (String) this.getFormHM().get("endDate");
                ArrayList menData = (ArrayList) this.getFormHM().get("menData");
                bo.addMembers(projectId,menData,beginDate,endDate);
            }
            this.getFormHM().put("tip", "1");
         }catch (Exception e) {
            this.getFormHM().put("tip", "2");
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
         }
     }
    
    private String decryptParam(String param) {
        if (param != null && !"".equals(param.trim()))
            return PubFunc.decryption(param);
        
        return "";
    }
}
