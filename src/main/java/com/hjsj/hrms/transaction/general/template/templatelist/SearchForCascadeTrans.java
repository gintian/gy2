package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title: SearchForCascadeTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-5-5 下午01:26:40</p>
 * @author xiongyy
 * @version 1.0
 */
public class SearchForCascadeTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String codeitemid  = (String) this.getFormHM().get("codeitemid");
            String codeid  = (String) this.getFormHM().get("codeid");
            String b0110="";
            String b0110desc="";
            String e0122="";
            String e0122desc="";
            
            if("um".equalsIgnoreCase(codeid)){
                String desc = getB0110(codeitemid,"un");
                if(desc!=null&&desc.length()>0){
                    b0110 = desc.split("`")[0];
                    b0110desc= desc.split("`")[1];
                    
                }
                
            }else if("@k".equalsIgnoreCase(codeid)){
                String desc1 = getB0110(codeitemid,"un");
                String desc2 = getB0110(codeitemid,"um");
                if(desc1!=null&&desc1.length()>0){
                    b0110 = desc1.split("`")[0];
                    b0110desc= desc1.split("`")[1];
                    
                }
                if(desc2!=null&&desc2.length()>0){
                    e0122 = desc2.split("`")[0];
                    e0122desc= desc2.split("`")[1];
                    
                }
                
            }
            
            
            this.getFormHM().put("b0110", b0110);
            this.getFormHM().put("e0122", e0122);
            this.getFormHM().put("b0110desc", b0110desc);
            this.getFormHM().put("e0122desc", e0122desc);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String getB0110(String codeitemid,String codeid) throws  SQLException {
        
            ArrayList list = new ArrayList();
            list.add(codeitemid);
            String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid = (select parentid from organization where codeitemid =?)";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            RowSet rs = dao.search(sql, list);
            String codeitemids="";
            String codesetid="";
            String codeitemdesc="";
            
            while (rs.next()) {
                codeitemids= rs.getString("codeitemid");
                codesetid = rs.getString("codesetid");
                codeitemdesc = rs.getString("codeitemdesc");
            }
            
            if(codeid.equalsIgnoreCase(codesetid)){
                return codeitemids+"`"+codeitemdesc;
                
            }else if(codeitemid.equalsIgnoreCase(codeitemids)){
                return "";
                
            }else{
                return getB0110(codeitemids,codeid);
                
            }
        
        
    }
    
    
    
}
