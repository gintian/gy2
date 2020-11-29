package com.hjsj.hrms.module.template.templatesubset.transaction;

import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;


public class TemplateSaveSubsetTrans extends IBusiness {

	
	@Override
    public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			String tablename=PubFunc.decryption((String)this.getFormHM().get("table_name")); //列表表名
			//String a0100=(String)this.getFormHM().get("a0100");
			//String basepre=(String)this.getFormHM().get("basepre");
			String columnName=(String)this.getFormHM().get("columnName"); //列表中子集对应的列
			String xmldata=(String)this.getFormHM().get("xmldata"); //列表中子集对应的列
	        String viewtype=(String)this.getFormHM().get("viewtype");
	        //String approveflag=(String)this.getFormHM().get("approveflag");
			//xmldata=subBo.decodeData(xmldata);  //处理数据
			//将变化后子集的xml中rwPriv、fieldsPriv、fieldsWidth、fieldsTitle这四个属性去掉
			//xmldata=subBo.removeAttributesFromXml(xmldata);
			String uniqueId=(String)this.getFormHM().get("uniqueId"); 
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("task_id");
			if(!"0".equalsIgnoreCase(task_id)){
				task_id= PubFunc.decrypt(SafeCode.decode(task_id));
			}
			String realtask_id_e=(String)this.getFormHM().get("realtask_id_e");
			if(!"0".equalsIgnoreCase(task_id)){
				realtask_id_e= PubFunc.decrypt(SafeCode.decode(realtask_id_e));
			}
			if(StringUtils.isBlank(realtask_id_e)){
				realtask_id_e="0";
			}
			if(task_id.indexOf(",")==-1&&StringUtils.isNotBlank(task_id)){
				realtask_id_e=task_id;
			}
			String objectid=(String)this.getFormHM().get("objectid");
			objectid = PubFunc.decrypt(SafeCode.decode(objectid));
			TemplateParam paramBo=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tabid));
			ContentDAO dao=new ContentDAO(this.frameconn);
			TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
			String oldXml="";
			TemplateUtilBo utilBo= new TemplateUtilBo(this.frameconn,this.userView);
			Boolean isAutoLog=paramBo.getIsAotuLog();
			Boolean isRejectAutoLog=paramBo.getIsRejectAotuLog();
			if(isRejectAutoLog&&!"0".equalsIgnoreCase(ins_id)){
				Boolean isHaveRejecttask=utilBo.isHaveRejectTaskByInsId(ins_id);
				if(isHaveRejecttask){
					isAutoLog=true;
				}
			}
			TemplateSubsetBo subBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,columnName);
			String sql="select "+columnName+" from "+tablename+" where ";
			if(paramBo.getInfor_type() == 1){// =1 人员模板
				String[] arr = objectid.split("`");
				String basepre = arr[0];
				String a0100 = arr[1];
				sql+=" a0100='"+a0100+"' and BasePre='"+basepre+"' ";
			}else if(paramBo.getInfor_type() == 2){//2 单位模板
				sql+=" B0110='"+objectid+"' ";
			}else if(paramBo.getInfor_type() == 3){//3 岗位模板
				sql+="E01A1='"+objectid+"' ";
			}
			if(ins_id!=null&&!"0".equalsIgnoreCase(ins_id))
			{
				sql+=" and ins_id='"+ins_id+"'";
			}
			RowSet rowset = dao.search(sql);
			if(rowset.next()){
				oldXml=Sql_switcher.readMemo(rowset,columnName);
			}
			//保存前清理无效的附件
			subBo.clearTempFile(oldXml, xmldata);
			if(isAutoLog&&!("0".equalsIgnoreCase(ins_id)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
				 ArrayList cellList= utilBo.getAllCell(Integer.parseInt(tabid));
				 TemplateSet subsetBo =null;
				 for(int i=0;i<cellList.size();i++){
					 TemplateSet setBo = (TemplateSet) cellList.get(i);
					 if(setBo.isSubflag()&&columnName.equalsIgnoreCase(setBo.getTableFieldName())){
						 subsetBo=setBo;
					 }
				 }
				chgLogBo.insertOrUpdateOneSubsetLogger(columnName, oldXml, xmldata, ins_id, realtask_id_e, objectid, tablename, paramBo.getInfor_type(),subsetBo);//保存某个子集的变动信息到变动日志中
				String changeInfoStr=chgLogBo.getSubsetChgLogInfo(objectid,ins_id,tabid,columnName);//获取这个子集的变动信息用于前台显示。
				this.getFormHM().put("chgInfoList", changeInfoStr);
			}
			if("list".equalsIgnoreCase(viewtype)){//是列表模式下保存
			    xmldata = subBo.saveSubAttachment(xmldata);
				boolean state=subBo.saveSub_dataXml(tablename, columnName, objectid, tabid,xmldata,ins_id);
				HashMap subDataMap=subBo.getSubDataMap(xmldata);
                JSONObject subDatajson = JSONObject.fromObject(subDataMap);  
                this.getFormHM().put("subData", SafeCode.encode(subDatajson.toString()));
                this.getFormHM().put("xmldata", SafeCode.encode(xmldata));
				if(state){
					this.getFormHM().put("succeed", true);
				}
			}
			else {
			    if(uniqueId!=null&&uniqueId.indexOf("fld")!=-1){ //执行保存操作，卡片页面的子集数据需同步
                    String[] arg=uniqueId.split("_"); 
                    TemplateSet templateSet=utilBo.getCell(Integer.valueOf(tabid),Integer.valueOf(arg[1]),Integer.valueOf(arg[2]));
                    //liuyz卡片子集直接保存到数据库
                    TemplateSubsetBo  subsetBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,templateSet.getTableFieldName(),templateSet.getXml_param(),"1"); 
                    //解析xml 得到用于存储的子集xml信息
                    ArrayList xmldata_ = subsetBo.saveSubAttachments(xmldata);
                    boolean state=subsetBo.saveSub_dataXml(tablename, columnName, objectid, tabid,String.valueOf(xmldata_.get(0)),ins_id);
                    HashMap subDataMap=subsetBo.getSubDataMap(String.valueOf(xmldata_.get(1)));
                    JSONObject subDatajson = JSONObject.fromObject(subDataMap);  
                    this.getFormHM().put("subData", SafeCode.encode(subDatajson.toString()));
                    this.getFormHM().put("xmldata", SafeCode.encode(String.valueOf(xmldata_.get(1))));
                    if(state){
    					this.getFormHM().put("succeed", true);
    				}
	            }
			}
			 
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}