package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:修改招聘流程环节功能。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-9 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class UpdateLinkFuncTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	RowSet search = null;
        try {
        	String linkid = (String) this.getFormHM().get("linkid");
        	ArrayList ids = (ArrayList) this.getFormHM().get("ids");
            ArrayList seqs = (ArrayList) this.getFormHM().get("seqs");
            ArrayList methodNames = (ArrayList) this.getFormHM().get("methodNames");
            ArrayList custom_names = (ArrayList) this.getFormHM().get("custom_names");
            ArrayList valids = (ArrayList) this.getFormHM().get("valids");
            String uncheckedid = (String) this.getFormHM().get("uncheckedid");//需要取消启用状态的id
            //判断当前状态下是否应聘成员，有的话不允许关闭
            if(StringUtils.isNotEmpty(uncheckedid)){
            	ContentDAO dao = new ContentDAO(this.frameconn);
                StringBuffer sql = new StringBuffer();
                sql.append("select * from zp_pos_tache ");
                sql.append(" where link_id=(select link_id from zp_flow_status where id=?) ");
                sql.append(" and resume_flag=(select status from zp_flow_status where id=?)");
	            ArrayList<String> id = new ArrayList<String>();
	            id.add(uncheckedid);
	            id.add(uncheckedid);
	            search = dao.search(sql.toString(),id);
	            if(search.next()){
	            	this.getFormHM().put("falg", true);
	            	return;
	            }
            }
            FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
            insertLinkBo.updateLinkFucs("zp_flow_functions",ids, seqs, custom_names, valids,null,methodNames,linkid);
            
            StringBuffer jsonStr = insertLinkBo.getLinkTableFuns(linkid, ";");
    		
    		this.getFormHM().put("jsonStr", jsonStr.toString());
            this.getFormHM().put("msg", "success");
            
        } catch (Exception e) {
        	this.getFormHM().put("msg", "failure");
            e.printStackTrace();
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    		PubFunc.closeResource(search);
    	}
    }

}
