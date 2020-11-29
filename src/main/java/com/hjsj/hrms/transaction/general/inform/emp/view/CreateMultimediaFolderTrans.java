package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>
 * Title:SaveMultimediaTrans
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2007-9-4:下午02:03:54
 * </p>
 * 
 * @author FengXiBin
 * @version 4.0
 */

public class CreateMultimediaFolderTrans extends IBusiness {

    public void execute() throws GeneralException {
        //新建附件分类前需要检测，附件分类的数量是否大于等于34个，如果大于等于34个则不允许添加新的分类
        String checkFlag = (String) this.getFormHM().get("checkFlag");
     // update by xiegh on 20171205 类型转换错误
        String kind = this.getFormHM().get("kind") + "";
        if(StringUtils.isEmpty(kind) || "null".equalsIgnoreCase(kind))
        	kind = "6";
        
        if("1".equalsIgnoreCase(checkFlag)) {
            this.getFormHM().remove("checkFlag");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            if("useUp".equalsIgnoreCase(this.getFlag(dao, kind)))
                this.getFormHM().put("flag", "useUp");
            else
                this.getFormHM().put("flag", "ok");
            
            return;
        }
        
        String sortname = (String) this.getFormHM().get("foldername");
        this.getFormHM().put("foldername", "");
        saveMutilmedia(sortname, kind);

    }

    public synchronized void saveMutilmedia(String sortname, String kind) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        int id = this.getMaxId(dao);
        String flag = this.getFlag(dao, kind);
        if("useUp".equalsIgnoreCase(flag))
            this.getFormHM().put("flag", "useUp");
        else
            this.getFormHM().put("flag", "ok");
            
        UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
        if (id < 1) {
            this.insert("1", "F", sortname, kind, dao);
            this.getFormHM().put("id", "1");
            this.getFormHM().put("sortname", sortname);
            this.getFormHM().put("multimediaflag", "F");
        } else {
            String getid = id + 1 + "";
            this.insert(getid, flag, sortname, kind, dao);
            this.getFormHM().put("id", getid);
            this.getFormHM().put("sortname", sortname);
            this.getFormHM().put("multimediaflag", flag);
        }

        user_bo.saveMediaResource(flag, this.userView);
    }

    /**
     * 得到i9999
     * 
     * @param a0100
     * @param dbpre
     * @return
     */
    public int getMaxId(ContentDAO dao) {
        RowSet rs;
        StringBuffer sb = new StringBuffer();
        int retint = 0;
        sb.append(" select max(id) as id from mediasort ");
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) {
                retint = rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retint;
    }

    /**
     * 获得文件夹类型
     * 
     * @param dao
     * @return
     */
    public String getFlag(ContentDAO dao, String kind) {
        String ret = "";
        RowSet rs = null;
        try {
            // xuj 2010-4-20 去掉K代号，k代号已成为多媒体岗位说明书固定分类 就象人员照片为P,在mediasort中也没有记录
            String[] flags = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                    "J", "L", "M", "N", "O", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
            StringBuffer sb = new StringBuffer();
            StringBuffer falgtemp = new StringBuffer();
//            sb.append("select flag from mediasort where dbflag=?");
            sb.append("select flag from mediasort");
            ArrayList<String> paramList = new ArrayList<String>();
            if("6".equals(kind))// 人员
            	paramList.add("1");
            else if("0".equals(kind))// 职位
            	paramList.add("3");
            else if("9".equals(kind))// 基准岗位
            	paramList.add("4");
            else  // 单位
            	paramList.add("2");
            
//            rs = dao.search(sb.toString(), paramList);
            rs = dao.search(sb.toString());
            while (rs.next()) {
                falgtemp.append(rs.getString("flag"));
            }
            
            for (int i = 0; i < flags.length; i++) {
                if (falgtemp.indexOf(flags[i]) == -1) {
                    ret = flags[i];
                    break;
                }
            }
            
            if ("".equals(ret))
                ret = "useUp";
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return ret;
    }

    /**
     * 插入照片
     * 
     * @param a0100
     * @param dbpre
     */
    public void insert(String id, String flag, String sortname, String kind, ContentDAO dao) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("insert into mediasort ");
            sb.append("(id,flag,sortname,dbflag)");
            sb.append(" values ");
            if ("6".equals(kind))
                sb.append("(" + id + ",'" + flag + "','" + sortname + "',1)");
            else if ("0".equals(kind))
                sb.append("(" + id + ",'" + flag + "','" + sortname + "',3)");
            else if ("9".equals(kind))
                sb.append("(" + id + ",'" + flag + "','" + sortname + "',4)");
            else
                sb.append("(" + id + ",'" + flag + "','" + sortname + "',2)");
            
            dao.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加新增的多媒体分类的权限
     * 
     * @param dao
     * @param flag
     */
    public void AppendMediaPriv(ContentDAO dao, String flag) {
        boolean priv = false;
        String mediapriv = "";
        int status = 0;
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from t_sys_function_priv where id = '" + this.userView.getUserName().toLowerCase() + "'");
        try {
            this.frowset = dao.search(sb.toString());
            while (this.frowset.next()) {
                mediapriv = this.frowset.getString("mediapriv");
                status = this.frowset.getInt("status");
            }
            
            if (!(mediapriv == null || "".equals(mediapriv) || ",".equals(mediapriv))) {
                String arr[] = mediapriv.split(",");
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].equalsIgnoreCase(flag)) {
                        priv = true;
                        break;
                    }
                }
            }
            
            if (priv == false) {
                RecordVo vo = new RecordVo("t_sys_function_priv");
                vo.setString("id", this.userView.getUserName());
                vo.setInt("status", status);
                RecordVo a_vo = dao.findByPrimaryKey(vo);
                if (flag == null || "".equals(flag))
                    mediapriv = "," + flag + ",";
                else
                    mediapriv = mediapriv + flag + ",";
                
                a_vo.setString("mediapriv", mediapriv);
                dao.updateValueObject(a_vo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给该用户的组添加权限
     * 
     * @param dao
     * @param flag
     */
    public void AppendMediaPrivGroup(ContentDAO dao, String flag) {
        String groupid = this.userView.getGroupId();
        String groupname = "";
        String mediapriv = "";
        int status = 0;
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from usergroup where groupid = " + groupid + " ");
        try {
            this.frowset = dao.search(sb.toString());
            while (this.frowset.next()) {
                groupname = this.frowset.getString("groupname");
            }
            
            sb.delete(0, sb.length());
            sb.append(" select * from t_sys_function_priv where id = '" + groupname + "'");
            this.frowset = dao.search(sb.toString());
            while (this.frowset.next()) {
                mediapriv = this.frowset.getString("mediapriv");
                status = this.frowset.getInt("status");
            }
            
            RecordVo vo = new RecordVo("t_sys_function_priv");
            vo.setString("id", groupname);
            vo.setInt("status", status);
            RecordVo a_vo = dao.findByPrimaryKey(vo);
            if (flag == null || "".equals(flag))
                mediapriv = "," + flag + ",";
            else
                mediapriv = mediapriv + flag + ",";
            
            a_vo.setString("mediapriv", mediapriv);
            dao.updateValueObject(a_vo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
