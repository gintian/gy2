package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>
 * Title:SearchRecruitHj.java
 * </p>
 * <p>
 * Description:浏览招聘流程的具体环节
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-13 下午03:47:33
 * </p>
 * 
 * @author zx
 * @version 1.0
 *
 */
public class SearchFlowLinkFunctionsTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        StringBuffer rzColumn = new StringBuffer("[");
    	try {
    		int clientWid = (Integer) this.getFormHM().get("clientWid");
        	clientWid = (int) (clientWid*0.98*0.93*0.7);
        	String isParent = (String) this.getFormHM().get("isParent");
        	
        	
    		rzColumn.append("{text:'序号',width:"+(clientWid*0.05)+",locked:false,align:'center',dataIndex:'seq',menuDisabled:true,editablevalidfunc:null,renderer:null},");
            rzColumn.append("{text:'操作按钮系统名称',width:"+(clientWid*0.20)+",locked:false,align:'center',dataIndex:'sysName',menuDisabled:true,editablevalidfunc:null,renderer:null},");
            
            if("false".equalsIgnoreCase(isParent)){
            	rzColumn.append("{text:'操作按钮用户名称',width:"+(clientWid*0.20)+",align:'center',dataIndex:'custom_name','editor':{maxLength:100,'allowBlank':false,'validator':null,'listeners':{afterrender:{fn:function(){afterEdit2()}}}},menuDisabled:true,renderer:null," +
            	"editablevalidfunc:null},");
            	rzColumn.append("{text:'方法名',width:"+(clientWid*0.35)+",locked:false,align:'center',dataIndex:'methodName',menuDisabled:true," +
            	"'editor':{maxLength:200,allowBlank:false,'validator':Global.regName,'listeners':{afterrender:{fn:function(){afterEdit2()}}}},editablevalidfunc:'Global.isEdit',renderer:null},");
            }else{
            	rzColumn.append("{text:'操作按钮用户名称',width:"+(clientWid*0.20)+",align:'center',dataIndex:'custom_name',menuDisabled:true,renderer:null,editablevalidfunc:null},");
            	rzColumn.append("{text:'方法名',width:"+(clientWid*0.35)+",locked:false,align:'center',dataIndex:'methodName',menuDisabled:true,editablevalidfunc:'Global.isEdit',renderer:null},");
            }
            rzColumn.append("{text:'是否系统定义',width:0,align:'center',dataIndex:'ownflag',hidden:true,menuDisabled:true,editablevalidfunc:null,renderer:null},");
            rzColumn.append("{text:'启用',align:'center',dataIndex:'valid', flex: 1,editablevalidfunc:null,renderer:Global.rerender}");
            rzColumn.append("]");
            
			String linkid = (String) this.getFormHM().get("linkid");
			
			FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
			StringBuffer jsonInfo = insertLinkBo.getLinkTableFuns(linkid,",");
			
            this.getFormHM().put("rzColumn", rzColumn.toString());
            this.getFormHM().put("rzValue", jsonInfo.toString());	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
