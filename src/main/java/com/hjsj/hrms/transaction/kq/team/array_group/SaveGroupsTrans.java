package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.team.GroupsArray;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveGroupsTrans extends IBusiness implements KqClassArrayConstant {

    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("save_flag");
            if (flag == null || flag.length() <= 0) {
                return;
            }

            String name = (String) this.getFormHM().get("name");
            if ("add".equalsIgnoreCase(flag)) {
                if (name == null || name.length() <= 0)
                    return;
            }

            String org_id = (String) this.getFormHM().get("org_id");
            String codeitem = "";
            if (org_id == null || org_id.length() <= 0) {
                GroupsArray groupsArray = new GroupsArray(this.getFrameconn(), this.userView);
                codeitem = groupsArray.orgCodeID();
            } else {
                codeitem = org_id;
            }
            
            if ("update".equalsIgnoreCase(flag)) {
                update(name, codeitem);
            } else if ("add".equalsIgnoreCase(flag)) {
                addRecord(name, codeitem);
            }
            
            this.getFormHM().remove("org_id");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void addRecord(String name, String codeitem) throws GeneralException {
        //检查是否已经存在同名班组
        validateGroupName("", name);
        
        IDGenerator idg = new IDGenerator(2, this.getFrameconn());
        RecordVo vo = new RecordVo(kq_shift_group_table);
        String group_id = idg.getId((kq_shift_group_table + "." + kq_shift_group_Id).toLowerCase());

        vo.setString("group_id", group_id);
        vo.setString("name", name);
        vo.setString("org_id", codeitem);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.addValueObject(vo);
            UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
            user_bo.saveResource(group_id, this.userView, IResourceConstant.KQ_CLASS_GROUP);//加权限
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * 检查是否已经存在同名班组
     */
    private void validateGroupName(String groupId, String groupName) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append(kq_shift_group_name);
        sql.append("='");
        sql.append(groupName);
        sql.append("'");
        if (null != groupId && !"".equals(groupId.trim())) {
            sql.append(" AND ");
            sql.append(kq_shift_group_Id);
            sql.append("<>'");
            sql.append(groupId);
            sql.append("'");
        }
        
        KqDBHelper kqDBHelper = new KqDBHelper(this.frameconn); 
        if (kqDBHelper.isRecordExist(kq_shift_group_table, sql.toString()))
            throw new GeneralException(ResourceFactory.getProperty("kq.group.add.name.exist"));    
    }

    private void update(String name, String codeitem) throws GeneralException {
        String group_id = (String) this.getFormHM().get("group_id");
        
        //检查是否已经存在同名班组
        validateGroupName(group_id, name);
        
        StringBuffer updateSQL = new StringBuffer();
        updateSQL.append("update " + kq_shift_group_table + " set ");
        updateSQL.append("name='" + name + "',org_id='" + codeitem + "'");
        updateSQL.append(" where group_id='" + group_id + "'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.update(updateSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
