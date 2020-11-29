package com.hjsj.hrms.module.template.templatesubset.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public class TemplateSubsetTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			if("0".equals(type)){//下载
				ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM");
                String rootdir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
                rootdir=rootdir.replace("\\",File.separator);
                if (!rootdir.endsWith(File.separator)) rootdir =rootdir+File.separator;
                rootdir += "multimedia"+File.separator;
				String path=(String)this.getFormHM().get("path");
				String srcfilename=(String)this.getFormHM().get("srcfilename");
				String filename=(String)this.getFormHM().get("filename");
				srcfilename = SafeCode.decode(srcfilename);
				filename = PubFunc.decrypt(filename);
				String isIE =(String)this.getFormHM().get("isIE");//是否ie
				String ext=(String)this.getFormHM().get("ext");
				if(!StringUtils.isNumeric(filename)){
					path = PubFunc.decrypt(path);
					if(!path.startsWith(rootdir)&&path.startsWith("subdomain"+File.separator+"template_")) {
						path = rootdir+path;
					}
					if(!path.endsWith(File.separator)) {
						path = path+File.separator;
					}
					path = path.replace("\\", File.separator).replace("/", File.separator);
	            	String filepath = "";
	            	//srcfilename = PubFunc.encrypt(filename);
	            	if(filename.startsWith(path)) {
	            		filepath = PubFunc.encrypt(filename);
	            	}else {
	            		filepath = PubFunc.encrypt(path+filename);
	            	}
            		
	            	
	            	this.getFormHM().put("displayfilename", srcfilename);
	            	this.getFormHM().put("path", filepath);
				}else {
					InputStream in=null;
					try {
						//59945 VFS+UTF-8：人事异动，子集上传附件，点击下载，后台报未传入文件id，见附件。
						//报错是因为filename 为空
						if(StringUtils.isEmpty(filename)){
							return;
						}
						filename=PubFunc.encrypt(filename);
						in=VfsService.getFile(filename);
						if(in!=null) {
							srcfilename = SafeCode.encode(srcfilename);
							this.getFormHM().put("displayfilename", srcfilename);
							this.getFormHM().put("path", filename);
						}else {
							throw new GeneralException("未找到文件！");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						PubFunc.closeIoResource(in);
					}
				}
			}else if("1".equals(type)){//显示子集
				String tabid=(String)this.getFormHM().get("tabid");
				String view_type=(String)this.getFormHM().get("view_type");
				String tablename=PubFunc.decryption((String)this.getFormHM().get("table_name")); //列表表名
				//String a0100=(String)this.getFormHM().get("a0100");
				String objectid=(String)this.getFormHM().get("objectid");//唯一性标示
				objectid = PubFunc.decrypt(SafeCode.decode(objectid));
				String basepre=(String)this.getFormHM().get("basepre");
				if("card".equalsIgnoreCase(view_type))
					basepre = PubFunc.decrypt(SafeCode.decode(basepre));
				String columnName=(String)this.getFormHM().get("columnName"); //列表中子集对应的列
				String Sub_dataXml=(String)this.getFormHM().get("data_xml"); //获取子集数据
				String nodePriv=(String)this.getFormHM().get("nodePriv"); //rwPriv
				String rwPriv=(String)this.getFormHM().get("rwPriv");
				Boolean isAutoLog=(Boolean)this.getFormHM().get("isAutoLog");
				TemplateSubsetBo subBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,columnName);
				//判断是否需要显示子集序号
				String showSubsetsOrder= SystemConfig.getPropertyValue("showSubsetsOrder");
				JSONArray classifyMap = new JSONArray(); 
				Boolean isNeedSubsetNo=false;
				if(StringUtils.isNotBlank(showSubsetsOrder)){
					String[] tabids=showSubsetsOrder.split(",");
					for(int i=0;i<tabids.length;i++){
						if(tabid.equals(tabids[i])){
							isNeedSubsetNo=true;
							break;
						}
					}
				}
				//根据tabid和columnName得到子集表头的信息
				String Xml_param =subBo.getSubFieldsPropertys(nodePriv,rwPriv);  //将xml对象转换为前台界面需要解析的xml
				//得到列表中子集对应的数据
				if (StringUtils.isBlank(Sub_dataXml) && StringUtils.isNotBlank(objectid)){
					String ins_id=(String)this.getFormHM().get("ins_id");
					Sub_dataXml=subBo.getSub_dataXml(tablename, objectid, tabid,ins_id);
					Sub_dataXml=Sub_dataXml.replace("~", "～");
					Sub_dataXml=Sub_dataXml.replace("^", "＾");//子集中有^子集不显示。
				}
				else {
					if(Sub_dataXml.startsWith("<?xml")){//没有转码,需要替换内容中的~号。
						Sub_dataXml=Sub_dataXml.replace("~", "～");//天津工业大学子集中有~子集不显示。
						Sub_dataXml=Sub_dataXml.replace("^", "＾");//子集中有^子集不显示。
					}
					Sub_dataXml= SafeCode.decode(Sub_dataXml);
				}
				HashMap subDataMap=subBo.getSubDataMap(Sub_dataXml);
				JSONObject subDatajson = JSONObject.fromObject(subDataMap); 
				FieldSet fieldSet=DataDictionary.getFieldSetVo(subBo.getSubDomain().getSetName().toUpperCase());
				String remarks=fieldSet.getExplain();
				if(StringUtils.isBlank(remarks)){
					remarks="";
				}
				this.getFormHM().put("remarks", SafeCode.encode(remarks));
				this.getFormHM().put("Xml_param", SafeCode.encode(Xml_param));
				this.getFormHM().put("subDatajson", subDatajson.toString()); 
				this.getFormHM().put("succeed", true);
				this.getFormHM().put("isNeedSubsetNo", isNeedSubsetNo);
				this.getFormHM().put("allow_del_his", subBo.getSubDomain().getAllow_del_his());
				this.getFormHM().put("record_key_id_pre", this.userView.getUserName());
				this.getFormHM().put("data_xml", "");//47012 客户网络限制，回传data_xml没有转码，导致链接被阻止。data_xml前台无用。
				if(isAutoLog!=null&&isAutoLog){
					String ins_id=(String)this.getFormHM().get("ins_id");
					TemplateParam paramBo=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tabid));
					TempletChgLogBo chglogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
					String userObject=StringUtils.isNotBlank(objectid)?objectid:StringUtils.isNotBlank(basepre)?basepre:"";
					String changeInfoStr=chglogBo.getSubsetChgLogInfo(userObject,ins_id,tabid,columnName);//获取对应子集的变动信息
					this.getFormHM().put("chgInfoList", changeInfoStr);
				}
			}else if("2".equals(type)){
				String tabid=(String)this.getFormHM().get("tabid");
				String taskid=(String)this.getFormHM().get("task_id");
				String fillInfo=(String) this.userView.getHm().get("fillInfo");
				Pattern pattern = Pattern.compile("[0-9]+");   
				if(pattern.matcher(taskid).matches()){
				}else
					taskid = PubFunc.decryption(taskid);
				TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tabid));
				//查询多媒体文件分类
				TemplateParam param = new TemplateParam(this.getFrameconn(),this.userView,Integer.valueOf(tabid));
				String infor_type = param.getInfor_type() + "";//1是人员，2是单位，3是岗位
				ArrayList mediasortList = new ArrayList();//多媒体目录
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				//判断是否是（起草或者驳回到起草）或者是审批状态
				Boolean tasktype=false;		
				if("0".equals(taskid)|| "1".equals(templateBo.isStartNode(taskid))){
					tasktype=true;
				}else {
					tasktype=false;
				}
				StringBuffer sb = new StringBuffer("");
				sb.append("select * from mediasort where dbflag="+infor_type+"order by id");
				this.frowset = dao.search(sb.toString());
				while(this.frowset.next()){
				    String flag = this.frowset.getString("flag");
				    String sortname = this.frowset.getString("sortname");
				    CommonData data=new CommonData(flag,sortname);
				    if(tasktype==true){
					    if (!this.userView.isSuper_admin()){//判断多媒体权限
					    	if(!"1".equals(fillInfo)){
					    		if (!this.userView.hasTheMediaSet(flag)) 
					    			continue;
					    	}
					    }
				    }
					mediasortList.add(data);
				}
				this.getFormHM().put("mediasortList", mediasortList);
				String value=(String)this.getFormHM().get("value");
				if(StringUtils.isNotEmpty(value)&&!"undefined".equalsIgnoreCase(value)) {
					String[] value_arry=value.split(",");
					for(String str:value_arry) {
						if(StringUtils.isNotEmpty(str)) {
							String[] attach_arry=str.split("\\|", -1);
							String filename=attach_arry[0];
							//存储为路径时 子集附件补全filename /multimedia/+path+filename 
							if(PubFunc.decrypt(attach_arry[0]).indexOf(".")>-1) {
								if(StringUtils.isNotEmpty(PubFunc.decrypt(attach_arry[1]))) {
									String path=PubFunc.decrypt(attach_arry[1]);
									if(!path.startsWith(File.separator)) {
										path=File.separator+"multimedia"+File.separator+path;
									}
									if(!path.endsWith(File.separator)) {
										path+=File.separator;
									}
									attach_arry[0]=PubFunc.encrypt(path+PubFunc.decrypt(attach_arry[0]));
									value=value.replace(filename, attach_arry[0]);
								}
							}
						}
						
					}
					this.getFormHM().put("value", value);
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