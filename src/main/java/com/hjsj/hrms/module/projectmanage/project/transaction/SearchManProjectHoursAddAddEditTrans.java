package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ManProjectHoursBo;
import com.hjsj.hrms.module.projectmanage.project.businessobject.ProjectManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title: SearchManProjectHoursAddAddEditTrans </p>
 * <p>Description: 获取页面列表</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-28 下午1:02:23</p>
 * @author liuyang
 * @version 1.0
 */
public class SearchManProjectHoursAddAddEditTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String type = (String) this.getFormHM().get("type");
            String modifyPermissions = "YES";
            if("edit".equals(type)&&!this.userView.hasTheFunction("3900103"))
                modifyPermissions = "NO";
            if("landMarkEdit".equals(type)&&!this.userView.hasTheFunction("3900106"))
                modifyPermissions = "NO";
            this.getFormHM().put("modifyPermissions",modifyPermissions);
            ManProjectHoursBo bo = new ManProjectHoursBo(this.frameconn, this.userView);
            if("add".equals(type)||"edit".equals(type)){
                ArrayList fieldList = DataDictionary.getFieldList("P11",1);
                ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
                ArrayList listValues = new ArrayList();
                ColumnsInfo columnsInfo = new ColumnsInfo();
                //排除指标
                String exceptFields = ",p1123,p1125,";
                for(int i=0; i<fieldList.size(); i++){
                
                    FieldItem fi = (FieldItem)fieldList.get(i);
                    // 去除未构库的指标
                    if(!"1".equals(fi.getUseflag())){
                    continue;
                    }
                // 去除隐藏的指标
                    if(!"1".equals(fi.getState())){
                    continue;
                    }
                // 去除不需要的指标
                    if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
                    continue;
                    }
                    listValues.add(bo.getColumnList(fi));
                }
                String organizationsName = "";
                if(!this.userView.isSuper_admin()){
                    ProjectManageBo proBo = new ProjectManageBo(this.userView,this.frameconn);
                    String  b0110= proBo.getUnitIdByBusi();
                    String[] b0110s = b0110.split("`");
                    if(b0110s.length>=1 && b0110s[0].length()>=2){
                        String codesetId = b0110s[0].substring(0,2);
                        if("UN".equalsIgnoreCase(codesetId)|| "UM".equalsIgnoreCase(codesetId))
                            organizationsName = b0110s[0] +"`"+AdminCode.getCodeName(b0110s[0].substring(0,2), b0110s[0].substring(2));
                    }
                }
                
                this.getFormHM().put("organizationsName", organizationsName); 
                this.getFormHM().put("listValues", listValues);
                
                if("edit".equals(type)){
                    String projectId = (String) this.getFormHM().get("projectId");
                    projectId = PubFunc.decryption(projectId);
                    
                    ArrayList manList = bo.getBeforeEditList(projectId);
                    HashMap projectList = bo.getBeforeProjectList(projectId,listValues);
                    
                    this.getFormHM().put("manList",manList);
                    this.getFormHM().put("projectList",projectList);
                }
                
                if("add".equals(type)){
                    HashMap map = new HashMap();
                    map.put("p1305", AdminCode.getCodeName("UN",userView.getUserOrgId()));
                    map.put("p1303", AdminCode.getCodeName("UM",userView.getUserDeptId()));
                    map.put("a0101", userView.getUserFullName());
                    map.put("a0100", PubFunc.encrypt(userView.getDbname()+userView.getA0100()));
                    map.put("p1311", "01");
                    map.put("p1309", userView.getUserEmail());
                    map.put("p1307", userView.getUserTelephone());
                    map.put("imageUrl", bo.getPhotoPath(userView.getDbname(),userView.getA0100()));
                    if(StringUtils.isEmpty(userView.getA0100()))
                        this.getFormHM().put("manList","");
                    else 
                        this.getFormHM().put("manList",map);
                }
            }
            if("landMarkAdd".equals(type)||"landMarkEdit".equals(type)){
                String projectId = (String) this.getFormHM().get("projectId");
                projectId = PubFunc.decryption(projectId);
                ArrayList fieldList = DataDictionary.getFieldList("P12",1);
                ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
                ArrayList listValues = new ArrayList();
                ColumnsInfo columnsInfo = new ColumnsInfo();
                //排除指标
                String exceptFields = ",p1219,p1221,";
                for(int i=0; i<fieldList.size(); i++){
                
                    FieldItem fi = (FieldItem)fieldList.get(i);
                    // 去除未构库的指标
                    if(!"1".equals(fi.getUseflag())){
                    continue;
                    }
                // 去除隐藏的指标
                    if(!"1".equals(fi.getState())){
                    continue;
                    }
                // 去除不需要的指标
                    if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
                    continue;
                    }
                    listValues.add(bo.getColumnList(fi));
                }
                String date = (String) this.getFormHM().get("date");
                this.getFormHM().put("listValues", listValues);
                if("landMarkEdit".equals(type)){
                    String landMarkId = (String) this.getFormHM().get("landMarkId");
                    landMarkId = PubFunc.decryption(landMarkId);
                    HashMap landMarkList = bo.getBeforeLandMarkList(projectId,listValues,landMarkId);
                    this.getFormHM().put("landMarkList",landMarkList);
                }
                HashMap projectDateMap = bo.getProjectDate(projectId);
                this.getFormHM().put("beginDate",projectDateMap.get("p1107").toString().substring(0,10).replaceAll("-", "."));
                this.getFormHM().put("endDate",projectDateMap.get("p1109").toString().substring(0,10).replaceAll("-", "."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
