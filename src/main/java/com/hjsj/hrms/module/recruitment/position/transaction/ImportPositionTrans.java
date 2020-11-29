package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ImportPositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ImportPositionTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {          
            ContentDAO dao = new ContentDAO(this.getFrameconn());           
            String fileId = (String) getFormHM().get("fileId");
            String flags = (String) getFormHM().get("flag"); // 导入的文件    
            ImportPositionBo bo = new ImportPositionBo(this.frameconn, dao, this.userView);
            if("1".equalsIgnoreCase(flags)){
                //导入excel，如果报错返回报错信息
            	ArrayList<Object> msglist = bo.importPosition(fileId); 
                ArrayList<Object> msg =(ArrayList<Object>) msglist.get(0);
                ArrayList list = (ArrayList) this.userView.getHm().get("valueLists");
                if(msg.size()==0){
                    this.getFormHM().put("msg", "false");
                    if(msglist.size()>1){
                        String succNumber = (String) msglist.get(1);
                        this.getFormHM().put("succNumber", succNumber.toString());
                    }
                } else {
                StringBuffer msgs = new StringBuffer("[");
                for(int i=0;i<msg.size();i++){
                    msgs.append("{data:'" + (String) msg.get(i) + "'},");
                } 
                
                if(msgs.toString().endsWith(","))
                    msgs.setLength(msgs.length() - 1);
                
                msgs.append("]");
                this.getFormHM().put("msg", msgs.toString());
                if(list != null){
                    this.getFormHM().put("dataNumber", list.size());
                } else
                    this.getFormHM().put("dataNumber", "0");
                
                }
                
            }else if("2".equalsIgnoreCase(flags)){              
                ArrayList msglist = (ArrayList) this.userView.getHm().get("valueLists");
                String sql = (String) this.userView.getHm().get("ImportPositionSql");
                ArrayList z0301Lists = (ArrayList) this.userView.getHm().get("z0301Lists");
                
                //导入excel数据进入数据库
                String msgs = bo.importExcel(sql,msglist,z0301Lists); 
                this.getFormHM().put("succNumber", msgs.toString()); 
            }
        } catch(Exception e) {           
            e.printStackTrace();
        }
    }    
}
