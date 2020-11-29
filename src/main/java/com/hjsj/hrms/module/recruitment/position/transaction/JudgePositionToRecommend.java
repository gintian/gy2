package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 * <p>Title: JudgePositionToRecommend </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-4-7 上午11:36:34</p>
 * @author xiongyy
 * @version 1.0
 */
public class JudgePositionToRecommend extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String z0301s = (String) this.getFormHM().get("z0301s");
            String a0100s = (String) this.getFormHM().get("a0100s");
            String msg = "";
            ArrayList z0301List = new ArrayList();
            ArrayList a0100sList = new ArrayList();
            toDecryptSplit(z0301s, z0301List);
            String a0100str = a0100s.split("`")[0];
            toDecryptSplit(a0100str,a0100sList);
            msg = toReturnMsg(z0301List, a0100sList);
            this.getFormHM().put("msg", msg);
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }



    private String toReturnMsg(ArrayList z0301List, ArrayList a0100sList)
            throws GeneralException {
        String msg = "";
        for (int i = 0; i < a0100sList.size(); i++) {
            String a0100 =(String )a0100sList.get(i);
            for (int j = 0; j < z0301List.size(); j++) {
                String z0301 = (String) z0301List.get(j);
                msg = getMsg(a0100,z0301);
                if(msg!=null&&msg.length()>0)
                    return msg;
            }
            
        }
        return msg;
    }

    
    
    /**
     * 
     * @param a0100
     * @param z0301
     * @return
     * @throws GeneralException 
     */
    private String getMsg(String a0100, String z0301) throws GeneralException {
        try {
        	RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");  
            String msg = "";
            ArrayList valueList = new ArrayList();
            String sql = " select a0101,z0351,z0333 from zp_pos_tache zp ,z03 z,"+dbname+"A01 o " +
            "where zp.ZP_POS_ID = z.Z0301 and o.A0100=zp.A0100  and zp.ZP_POS_ID = ? and zp.A0100=?";
            valueList.add(z0301);
            valueList.add(a0100);
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            RowSet rs = dao.search(sql, valueList);
            while(rs.next()){
                String position = rs.getString("z0351");
                if(position==null)
                {
                	position="未知职位";
                }
                if(rs.getString("z0333")!=null&&rs.getString("z0333").length()!=0)
                    position+="-"+rs.getString("z0333");
                msg = rs.getString("a0101")+"已经申请过“"+position+"”职位！";
            }
            
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


//给加密的以逗号分隔的字符串 解析成 集合
    private void toDecryptSplit(String str, ArrayList list) {
        String[] split = str.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(PubFunc.decrypt(split[i]));
        }
    }
}
