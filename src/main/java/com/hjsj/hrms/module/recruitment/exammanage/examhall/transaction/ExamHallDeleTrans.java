package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title: ExamHallDeleTrans </p>
 * <p>Description: 删除考场</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-11-6 下午6:00:47</p>
 * @author liuyang
 * @version 1.0
 */
public class ExamHallDeleTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String tip = "1";
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String ids = (String) this.getFormHM().get("id");
            //删除考场表数据
            StringBuffer stb = new StringBuffer("");
            stb.append("delete  from zp_exam_hall where id in ( ");
            stb.append( ids );
            stb.append( "  )" );
            ArrayList values = new ArrayList();
            dao.delete(stb.toString(), values);
            //删除准考证
            StringBuffer deleProspectiveCandidates = new StringBuffer("");
            deleProspectiveCandidates.append(" select z.A0100,z.nbase,z.z0301  from zp_exam_assign z  ");
            deleProspectiveCandidates.append(" where z.exam_hall_id in("+ids+")");  
            RowSet rs = dao.search(deleProspectiveCandidates.toString());
            String a0100s ="";
            String nbases ="";
            String z0301s ="";
            while (rs.next()) {
                a0100s = a0100s+ PubFunc.encrypt(rs.getString("A0100"))+",";
                nbases = nbases+ PubFunc.encrypt(rs.getString("nbase"))+","; 
                z0301s = z0301s+ PubFunc.encrypt(rs.getString("z0301"))+",";
            }
            if(a0100s.length()>0&&a0100s.indexOf(",")>-1){
                a0100s = a0100s.substring(0,a0100s.length()-1);
                nbases = nbases.substring(0,nbases.length()-1);
                z0301s = z0301s.substring(0,z0301s.length()-1);
                
                ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
                bo.clearHallRecord(a0100s.split(","),nbases.split(","),z0301s.split(","));
            }
            //清空考生分配考场信息
            StringBuffer stbf = new StringBuffer("");
            stbf.append(" UPDATE zp_exam_assign ");
            stbf.append(" SET ");
            stbf.append(" exam_hall_id = NULL ").append(" , ");
            stbf.append(" hall_id = NULL ").append(" , ");
            stbf.append(" hall_name = NULL ").append(" , ");
            stbf.append(" seat_id = NULL ");
            stbf.append(" WHERE exam_hall_id in ( "+ids+" ) " );
            dao.update(stbf.toString(), values);
            
            
        } catch (SQLException e) {
            tip = "0";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.getFormHM().put("deleTip", tip);
        this.getFormHM().remove("type");
    }
}
