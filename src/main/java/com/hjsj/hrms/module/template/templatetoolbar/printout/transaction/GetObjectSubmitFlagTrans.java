package com.hjsj.hrms.module.template.templatetoolbar.printout.transaction;


import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class GetObjectSubmitFlagTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException
	{
		TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
        String moduleId = frontProperty.getModuleId();
        String tabId = frontProperty.getTabId();
        String task_id = frontProperty.getTaskId();
        String infor_type=frontProperty.getInforType();	
        String returnFlag = frontProperty.getReturnFlag();
        String task_sp_flag=(String)this.getFormHM().get("task_sp_flag");
        String businessModel_yp=(String)this.getFormHM().get("businessModel_yp");
        String isDelete = frontProperty.getOtherParam("isDelete");
        ArrayList personlist=new ArrayList();
        TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
        TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tabId));
        String setname = utilBo.getTableName(moduleId, Integer.parseInt(tabId), task_id);
        /** 批量审批 */
        ArrayList tasklist = null;
        if (frontProperty.isBatchApprove()) {
            tasklist = getTaskList(task_id);
        } else {
            tasklist = new ArrayList();
            tasklist.add(task_id);
        }
        String ins_id = "";
        for (int i = 0; i < tasklist.size(); i++) {
            if (i != 0)
                ins_id = ins_id + ",";
            ins_id = ins_id + utilBo.getInsId((String) tasklist.get(i));
        }
		String sql="select a0100,basepre,a0101_1 from "+setname+" ";
		if(infor_type!=null&& "2".equals(infor_type))
			sql = "select b0110 a0100,codeitemdesc_1 a0101_1 from "+setname+" ";
		if(infor_type!=null&& "3".equals(infor_type))
			sql = "select e01a1 a0100,codeitemdesc_1 a0101_1 from "+setname+" ";
		if("0".equals(ins_id)){
			sql+=" where submitflag='1' ";
		}else{
			sql+=" where  exists (select null from t_wf_task_objlink where "+setname+".seqnum=t_wf_task_objlink.seqnum and "+setname+".ins_id=t_wf_task_objlink.ins_id and submitflag =1  ";
			if(!"2".equals(task_sp_flag)&&!"3".equals(businessModel_yp)){
				sql+=" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ";
			}
		}
		if(tasklist.size()>1)
		{ 
			if(ins_id.length()>0){
				sql+=" and   task_id in ("+ins_id.substring(1)+" ) ";
			}
			if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
				sql+=" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ";
			}else {
				sql+=" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ";
			}
		}
		else
		{
			if(ins_id!=null&&!"0".equals(ins_id)){
				if(task_id!=null&&!"0".equalsIgnoreCase(task_id))
				{
					sql+=" and task_id="+task_id;
				}
				if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
					sql+=" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ";
				}else {
					sql+=" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ";
				}
			}
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String tag="";
		Des des= new Des();//调用cs控件打印时采用这个对象对nbase和A0100进行加密
		try
		{
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				if(infor_type!=null&& "1".equals(infor_type)){
					tag="<NBASE>"+des.EncryPwdStr(this.frowset.getString("basepre"))+"</NBASE><ID>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
					//tag="<NBASE>"+this.frowset.getString("basepre")+"</NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
				}else{
					tag="<NBASE>"+des.EncryPwdStr("")+"</NBASE><ID>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
					//tag="<NBASE></NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
				}
				
				CommonData dataobj = new CommonData(tag,this.frowset.getString("a0100"));
				personlist.add(dataobj);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("personlist",personlist);
		this.getFormHM().put("hosturl", this.userView.getServerurl());
        this.getFormHM().put("dbtype", Sql_switcher.searchDbServer() + "");
        this.getFormHM().put("username", this.userView.getUserName());
        this.getFormHM().put("userfullname", this.userView.getUserFullName());
        this.getFormHM().put("superUser", this.userView.isSuper_admin() ? "1" : "0");
        this.getFormHM().put("tablepriv", this.userView.getTablepriv()+"");
        this.getFormHM().put("nodepriv", getFieldPriv(frontProperty.getTaskId(),paramBo));
 }
	
    private ArrayList getTaskList(String batch_task) throws GeneralException {
        String[] lists = StringUtils.split(batch_task, ",");
        ArrayList list = new ArrayList();
        for (int i = 0; i < lists.length; i++)
            list.add(lists[i]);
        return list;
    }
    
    private String getFieldPriv(String taskIds, TemplateParam paramBo) throws GeneralException {
        String fields = this.userView.getFieldpriv().toString();
        fields = fields.toUpperCase();
        TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
        ArrayList cellList = utilBo.getAllCell(paramBo.getTabId());
        TemplateDataBo dataBo = new TemplateDataBo(this.frameconn, this.userView, paramBo);
        HashMap filedPrivMap = dataBo.getFieldPrivMap(cellList, taskIds);
        if (filedPrivMap != null) {
            Iterator iterator = filedPrivMap.entrySet().iterator();
            String key = "";
            String value = "";
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                key = (String) entry.getKey();
                key = key.toUpperCase();
                value = (String) entry.getValue();
                if (key.indexOf("_") != -1) {
                    key = key.substring(0, key.indexOf("_"));
                    if (fields.indexOf(key) != -1) {
                        if ("0".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", "");
                                fields = fields.replace(key + "1", "");
                                fields = fields.replace(key + "2", "");
                            } else {
                                fields = fields.replace("," + key + "0", "");
                                fields = fields.replace("," + key + "1", "");
                                fields = fields.replace("," + key + "2", "");
                            }
                        } else if ("1".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "1");
                                fields = fields.replace(key + "1", key + "1");
                                fields = fields.replace(key + "2", key + "1");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "1");
                                fields = fields.replace("," + key + "1", "," + key + "1");
                                fields = fields.replace("," + key + "2", "," + key + "1");
                            }
                        } else if ("2".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "2");
                                fields = fields.replace(key + "1", key + "2");
                                fields = fields.replace(key + "2", key + "2");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "2");
                                fields = fields.replace("," + key + "1", "," + key + "2");
                                fields = fields.replace("," + key + "2", "," + key + "2");
                            }
                        }
                    } else {
                        fields = fields + "," + key + value;
                    }
                } else {
                    if (fields.indexOf(key) != -1) {
                        if ("0".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", "");
                                fields = fields.replace(key + "1", "");
                                fields = fields.replace(key + "2", "");
                            } else {
                                fields = fields.replace("," + key + "0", "");
                                fields = fields.replace("," + key + "1", "");
                                fields = fields.replace("," + key + "2", "");
                            }
                        } else if ("1".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "1");
                                fields = fields.replace(key + "1", key + "1");
                                fields = fields.replace(key + "2", key + "1");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "1");
                                fields = fields.replace("," + key + "1", "," + key + "1");
                                fields = fields.replace("," + key + "2", "," + key + "1");
                            }
                        } else if ("2".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "2");
                                fields = fields.replace(key + "1", key + "2");
                                fields = fields.replace(key + "2", key + "2");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "2");
                                fields = fields.replace("," + key + "1", "," + key + "2");
                                fields = fields.replace("," + key + "2", "," + key + "2");
                            }
                        }
                    } else {
                        fields = fields + "," + key + value;
                    }

                }
            }
        }
        return fields;
    }	
}
