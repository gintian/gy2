package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
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
 * create time:2015-5-6 上午09:53:33
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class SearchRecruitLinksTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
        	int clientWid = (Integer) this.getFormHM().get("clientWid");
        	clientWid = (int) (clientWid*0.9*0.9);
        	String flowid = (String) this.getFormHM().get("flowid");
        	if(flowid != null && flowid.length() > 0)
        		flowid = PubFunc.decrypt(flowid);
        	
        	RecruitflowBo rfb = new RecruitflowBo(this.frameconn, this.userView);
        	boolean isParent = rfb.isParentFlow(flowid, "");
        	
        	//组合表格列json
        	StringBuffer rzColumn = new StringBuffer("[");
        	//序号
            rzColumn.append("{text:'序号',");
            rzColumn.append("width:52,");
            rzColumn.append("align:'center',");
            rzColumn.append("sortable:false,");
            rzColumn.append("dataIndex:'seq',");
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("editablevalidfunc:null,");
            rzColumn.append("renderer:null},");
            //招聘环节系统名称
            rzColumn.append("{text:'招聘环节系统名称',");
            rzColumn.append("width:"+(clientWid*0.20)+",");
            rzColumn.append("align:'center',");
            rzColumn.append("sortable:false,");
            rzColumn.append("dataIndex:'sysName',");
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("editablevalidfunc:null,");
            rzColumn.append("renderer:null},");
            //招聘环节用户名称
            rzColumn.append("{text:'招聘环节用户名称',");
            rzColumn.append("width:"+(clientWid*0.20)+",");
            rzColumn.append("align:'center',");
            rzColumn.append("sortable:false,");
            rzColumn.append("dataIndex:'custom_name',");
            rzColumn.append("menuDisabled:true,");
            if(!isParent) {
                rzColumn.append("'editor':{maxLength:25,");
                rzColumn.append(          "enableKeyEvents:true,");
                rzColumn.append(          "'allowBlank':false,");
                rzColumn.append(          "'validator': function(value){");
                rzColumn.append(          "   var tn = value.replace(/(^\\s*)|(\\s*$)/g, ''),"); 
                rzColumn.append(          "   errMsg = '招聘环节用户名称的值不能为空'; ");  
                rzColumn.append(          "   return (tn.length>0) ? true : errMsg;");
                rzColumn.append(          "},");
                rzColumn.append(           "'listeners':{");
                rzColumn.append(              "afterrender:{fn:function(){afterEdit()}},");
                rzColumn.append(              "keydown:{fn:function(field,e){pressKey(field,e)}}");
                rzColumn.append(           "}");
                rzColumn.append("},");
            }
            rzColumn.append("renderer:null");
            rzColumn.append("},");
            //备注
            rzColumn.append("{xtype:'bigtextcolumn',");
            rzColumn.append("text:'备注',");
            rzColumn.append("width:"+(clientWid*0.30)+",");
            rzColumn.append("align:'left',");
            rzColumn.append("sortable:false,");
            rzColumn.append("dataIndex:'remark',");
            if(!isParent) {
                rzColumn.append("'editor':{maxLength:200,");
                rzColumn.append(          "xtype:'bigtextfield',");
                rzColumn.append(          "'allowBlank':true,");
                rzColumn.append(           "'validator': null,");
                //zxj 20160321 此处加校验后，输入长度超过限制时，ie报错，其它浏览器没问题，原因不明，先不加校验，汉字长度的校验截取后台保存时处理
//                rzColumn.append(          "'validator':function(value){");
//                rzColumn.append(          "   return (Ext.getStringByteLength(value)<=200);");
//                rzColumn.append(          "},");
                rzColumn.append(          "'listeners':{afterrender:{fn:function(){afterEdit()}}}");
                rzColumn.append("},");
            }
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("renderer:null");
            rzColumn.append("},");
          //用人单位处理环节
            rzColumn.append("{text:'用人单位处理环节',");
            rzColumn.append("width:120,");
            rzColumn.append("align:'center',");
            rzColumn.append("dataIndex:'org_flag',");
            rzColumn.append("sortable:false,");
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("editablevalidfunc:null,");
            rzColumn.append("renderer:Global.rerender,");
            rzColumn.append("},");
            //是否启用
            rzColumn.append("{text:'启用',");
            rzColumn.append("align:'center',");
            rzColumn.append("dataIndex:'valid',");
            rzColumn.append("sortable:false,");
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("editablevalidfunc:null,");
            rzColumn.append("renderer:Global.rerender,");
            rzColumn.append("width:65},");
//            rzColumn.append("style:'width:auto'},");
            //环节系统内置id
            rzColumn.append("{text:'环节系统内置id',");
            rzColumn.append("width:0,");
            rzColumn.append("hidden:true,");
            rzColumn.append("align:'center',");
            rzColumn.append("dataIndex:'nodeid',");
            rzColumn.append("sortable:false,");
            rzColumn.append("menuDisabled:true,");
            rzColumn.append("editablevalidfunc:null,");
            rzColumn.append("renderer:null}");
            rzColumn.append("]");
            
            FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
            StringBuffer jsonInfo = insertLinkBo.getLinkInfos(flowid,",");
            StringBuffer requery = insertLinkBo.getLinkInfos(flowid,"`");
            this.getFormHM().put("records", requery.toString());
            this.getFormHM().put("isParent", String.valueOf(isParent));
            this.getFormHM().put("rzColumn", rzColumn.toString());
            this.getFormHM().put("rzValue", jsonInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
