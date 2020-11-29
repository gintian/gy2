package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MobileTemplateBo {
	private Connection conn = null;
    private UserView userView = null;
    
	/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv_Input="0";
	/**审批模式=0自动流转，=1手工指派*/
	private String sp_mode="0";
	/**是否需要审批*/
	private boolean bsp_flag=false;
    private String  no_sp_yj="false"; //true: 审批不填写意见  false:填写 
    private Boolean isWeiX=true;//true 微信，false 二维码
    
    private  int recorsionCount;
    
    public Boolean getIsWeiX() {
		return isWeiX;
	}


	public void setIsWeiX(Boolean isWeiX) {
		this.isWeiX = isWeiX;
	}


	public MobileTemplateBo(Connection _conn,UserView _userView)
    {
    	this.conn=_conn;
    	this.userView=_userView;
    }
    
    
    /**
     * 
     * @param valueStr
     * {  “tabid”:”1”     //模板id 
		  ."ins_id":"101"  //实例ID
		  ,"taskid":"20"   //任务ID 
		   ,"fromMessage":"1"     //1：来自通知单待办  0：不是
		  ,"content":""  //审批意见
		  ,"opt":"1"  //1:报批  2：提交  3：驳回
		  ,"actorid":""  // 手工报批时指定的审批人，暂不支持手工报批,自动审批模板此参数传空即可
		 } 
     * @return success :成功 ，其它信息为保存不成功返回的错误信息
     */
    public String dealTask (String valueStr) throws GeneralException
    {
    	String info="success";
    	RowSet rset=null;
   		ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
    		HashMap valueMap=(HashMap)JSON.parse(valueStr);
   			String tabid=(String)valueMap.get("tabid");
   	    	String ins_id=(String)valueMap.get("ins_id");
   	    	if(ins_id.length()==0)
   	    	{
   	    		ins_id="0";
   	    		valueMap.put("ins_id", "0");
   	    	}
   	    	String taskid=(String)valueMap.get("taskid");
   	    	if(taskid.length()==0)
   	    	{
   	    		taskid="0";
   	    		valueMap.put("taskid", "0");
   	    	}
   	    	String content=(String)valueMap.get("content");
   	    	String opt=(String)valueMap.get("opt");
   	    	String fromMessage=(String)valueMap.get("fromMessage");
    		SearchDataBo  templatebo=new SearchDataBo(conn, this.userView,tabid);
    		templatebo.setIsWx(this.isWeiX);
    		String selfapply="0";
    		if((ins_id==null||ins_id.trim().length()==0|| "0".equals(ins_id))&& "0".equals(fromMessage))
    			selfapply="1";
    		templatebo.setOpt(opt);//驳回不进行自动计算、校验公式、必填校验。
    		String updateSubmitFlagSql="update t_wf_task_objlink set submitflag=1 where ins_id=? and task_id=? and submitflag=0";//bug 40922 微信必填项不校验。因为数据submigflag为0查不到人员数据，这里对0的自动修改为1
			ArrayList list=new ArrayList();
			list.add(Integer.parseInt(ins_id));
			list.add(Integer.parseInt(taskid));
			dao.update(updateSubmitFlagSql,list);
    		 LazyDynaBean infoBean=templatebo.validateInfo(ins_id,taskid,selfapply); //报批、批准前执行  计算、审核、 编制控制校验
    		 if("error".equals((String)infoBean.get("flag")))
    			 throw new  GeneralException((String)infoBean.get("msg"));
    		 
    		 templatebo.getTemplateTableBo().setValidateM_L(true);
    		 if((ins_id!=null&&ins_id.trim().length()!=0&&!"0".equals(ins_id))&& "3".equals(opt)) //驳回
    		 { 
                 templatebo.rejectTask(taskid,ins_id,"1",content); 
    		 }
    		 else  if((ins_id==null||ins_id.trim().length()==0|| "0".equals(ins_id))&& "2".equals(opt)) //提交
    		 {
    			 templatebo.submitTemplet(selfapply,tabid,dao);
    		 }
    		 else if("1".equals(opt)) //报批
    		 { 
    				 templatebo.createNextTask(taskid,ins_id,"1",content,"","","",selfapply); 
    		 }
    		 
    	}
    	catch(Exception ex)
   		{
			 ex.printStackTrace(); 
			 String errorMsg=ex.toString();
	         int index_i=errorMsg.indexOf("description:");
	         info=errorMsg.substring(index_i+12); 
	//		 throw GeneralExceptionHandler.Handle(ex);
   		}
    	return info;
    }
    
    //----------------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * 撤回和撤销单据
     * valueStr:
     *  {  “tabid”:”1”     //模板id 
		   "ins_id":"101"  //实例ID
		   "taskid":"20"   //任务ID 
		   "ischeck":""再次撤单标记 默认1，（撤单后提示是否覆盖时 确认后回传默认数据为0）
		   "opt":"4" 4:撤单  5 撤销
		   "infor_type":1人员 2单位 3岗位
		 } 
     * @param valueStr
     * @return return_Map
     * {
     * success:true/false  (type=1,2,3 为true|false type为空),
     * type:1:无法撤销,2：模板有起草单据，提示是否覆盖,3：成功,
     * msg:处理信息
     * }
     * @throws GeneralException
     */
	public String recallOrDeleteTask(String valueStr) throws GeneralException {
		String info = "";
		HashMap return_Map=new HashMap();
		try {
			HashMap valueMap = (HashMap) JSON.parse(valueStr);
			String tabid = (String) valueMap.get("tabid");
			String ins_id = (String) valueMap.get("ins_id");
			String ischeck=(String)valueMap.get("ischeck");
			String infor_type=(String)valueMap.get("infor_type");
			if (StringUtils.isEmpty(ins_id)) {
				ins_id = "0";
			}
			String taskid = (String) valueMap.get("taskid");
			if (StringUtils.isEmpty(taskid)) {
				taskid = "0";
			}
			
			//正则校验task_id ins_id 判断是否是纯数字
			Pattern pattern = Pattern.compile("[0-9]+");
			if(!pattern.matcher(ins_id).matches()||
			   !pattern.matcher(tabid).matches()||
			   !pattern.matcher(taskid).matches()) {//判断是不是纯数字，防止注入
				
				return_Map.put("success", "false");
				return_Map.put("msg","非法数据！");
				
				return JSON.toString(return_Map);
			}	
			
			String opt = (String) valueMap.get("opt");
			if ("4".equals(opt)) {// 撤回
				return_Map = recallTask(ins_id, taskid, tabid,ischeck);
				
			}else if("5".equals(opt)) {//撤销
				delTask(tabid, taskid,infor_type);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = e.toString();
			int index_i = errorMsg.indexOf("description:");
			info = errorMsg.substring(index_i + 12);
			return_Map.put("success", "false");
			return_Map.put("msg", info);
		}
		return JSON.toString(return_Map);
	}
 
	   /***
	    * 二维码入职保存附件 ins_id = 0 时查看个人附件与公共附件有没有相同的文件名
	    * t_wf_file path存储的文件名是系统随机生成的不会重复
	    * @param objid
	    * @param dbname
	    * @return
	    */
	    private ArrayList<String> getFilenameList(ContentDAO dao, String objid, String dbname) {
	        ArrayList<String> list = new ArrayList<String>();
	        String sql = "select filepath from t_wf_file where objectid=? and upper(basepre)=? ";
	        RowSet rs = null;
	        try {
	            rs = dao.search(sql, Arrays.asList(objid, dbname.toUpperCase()));
	            //vfs 兼容 保存fileid与路径
	            while(rs.next()) {
	                String name = rs.getString("filepath");
	                if(name.indexOf(File.separator)>-1) {
	                	name = name.substring(name.lastIndexOf(File.separator)+1);
	                }
	                list.add(name);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }finally {
	            PubFunc.closeDbObj(rs);
	        }
	        return list;

	    }   
    
    /**
     * 保存模板信息
     * @param valueStr
     * { “tabname”:”加班申请单”     //模板名称
		  , “tabid”:”1”     //模板id 
		  ."ins_id":"101"  //实例ID
	       ,"fromMessage":"1"     //1：来自通知单待办  0：不是
		  ,"taskid":"20"   //任务ID 
		  ,"object_id":"Usr00000001" //模板数据ID，人员：库前缀+A0100
		                                   单位|岗位：B0100|E01A1
		                                   
		  ,”info_type”:”mobile”   // mobile:手机适配页   normal:电脑端页面
          ,”page_no”:”1” //页签		                                   
		  , "fieldList":[ { “gridno”:”1”   // 单元格编号  (必填)
		  ," item_id":"A0703"  //指标ID   
		  ," item_type":"A"    //指标类型 
		  ," item_length":"20"  //指标长度
		  ,"decimal_width":"0" //小数位数
		  ," chgstate ":"1"     // 1：变化前指标  2：变化后指标
		  ," subflag ":"1"      //1：子集  0：不是子集  
		  ,"value":"1"        //值 (必填)
		  ,"priv":"1"          // 0：无读写权限 1：读权限  2：写权限
		  },......] 
		} 
     * @return { info:success,fieldList:[{},{}.....]        }  //success:成功 ，其它信息为保存不成功返回的错误信息
     */
   public  String  saveTemplateInfo (String valueStr) throws GeneralException
   {
	   		HashMap returnMap=new HashMap();
	   		String  info="success"; 
	   		RowSet rset=null;
	   		ContentDAO dao=new ContentDAO(this.conn);
	   		try
	   		{
	   			HashMap valueMap=(HashMap)JSON.parse(valueStr);
	   			String tabid=(String)valueMap.get("tabid");
	   	    	String ins_id=(String)valueMap.get("ins_id");
	   	    	String taskid=(String)valueMap.get("taskid");
	   			String object_id=(String)valueMap.get("object_id");  
	   			String fromMessage=(String)valueMap.get("fromMessage"); //1：来自通知单待办  0：不是
	   			String page_no="0";   //页签 
	   			if(valueMap.get("page_no")!=null)  
	   				page_no=(String)valueMap.get("page_no");
	   			String info_type="mobile"; // mobile:手机适配页   normal:电脑端页面
	   			if(valueMap.get("info_type")!=null)
	   				info_type=(String)valueMap.get("info_type"); 
	   		//	RecordVo tab_vo=new RecordVo("Template_table");
			//	tab_vo.setInt("tabid",Integer.parseInt(tabid));
			//	tab_vo=dao.findByPrimaryKey(tab_vo); 
				RecordVo tab_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
				//	TemplateTableBo templateTableBo = new TemplateTableBo(this.conn, Integer.parseInt(tabid), this.userView);
				TemplateDataBo templateDataBo=new TemplateDataBo(this.conn,this.userView,Integer.parseInt(tabid));
				TemplateBo templatebo=new TemplateBo(conn, this.userView, Integer.parseInt(tabid));
				TemplateParam tableParamBo=templatebo.getParamBo();
				String staticKey = "static";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					staticKey = "static_o";
	   			}
	   			int _static = tab_vo.getInt(staticKey);
	   			Object[] fieldList=(Object[])valueMap.get("fieldList");  
	   			HashMap setMap=getSetMap(tabid,dao,taskid,tab_vo,info_type,page_no);
	   			
	   			String tabname="templet_"+tabid;
	   			if(taskid==null||taskid.trim().length()==0|| "0".equals(taskid))
	   			{
	   				if("0".equals(fromMessage))
	   					tabname="g_templet_"+tabid;
	   				else
	   					tabname=this.userView.getUserName()+"templet_"+tabid;
	   			} 
	   			if("1".equals(this.userView.getHm().get("fillInfo"))){
	   				if(isA0100Exits(tabname,this.userView.getA0100())){
	   					tableParamBo.setOperationType(0);//新增人员
	   					templatebo.autoAddRecord(tabname);
	   				}
	   				
	   			}
	   			RecordVo template_vo=new RecordVo(tabname);
	   			if(_static==10) //单位
				{ 
	   				template_vo.setString("b0110",object_id); 
				}
				else if(_static==11) //职位
				{ 
					template_vo.setString("e01a1",object_id); 
				}
				else
				{
					template_vo.setString("basepre",object_id.substring(0,3)); 
					template_vo.setString("a0100",object_id.substring(3));  
				} 
	   			
	   			if(ins_id!=null&&ins_id.trim().length()!=0&&!"0".equals(ins_id))
	   			{
	   				template_vo.setInt("ins_id",Integer.parseInt(ins_id));
	   			}
	   			
	   			ArrayList files_list = this.getFilenameList(dao, object_id.substring(3), object_id.substring(0,3));
	   			
	   			template_vo=dao.findByPrimaryKey(template_vo);
	   			HashMap _setMap=null;
	   			for(int i=0;i<fieldList.length;i++)
	   			{
	   				_setMap=(HashMap)fieldList[i];
	   				String gridno=(String)_setMap.get("gridno");
	   				String pageid=(String)_setMap.get("pageid");
	   				HashMap setInfoMap=(HashMap)setMap.get(pageid+"_"+gridno);
	   				String itemid=(String)setInfoMap.get("item_id"); //指标ID   
	   				String item_name=itemid;
	   				String item_type=(String)setInfoMap.get("item_type");
	   				int decimal_width=0;
	   				int item_length=0;
	   				String subflag=(String)setInfoMap.get("subflag"); //1:子集  0：指标
	   				String value="";
	   				if(!"1".equals(subflag)&&!"F".equalsIgnoreCase(item_type)&&!"P".equalsIgnoreCase(item_type)) {
	   					value=(String)_setMap.get("value"); 
	   					item_length=Integer.parseInt((String)setInfoMap.get("item_length"));
	   					decimal_width=Integer.parseInt((String)setInfoMap.get("decimal_width"));
	   					
	   				}	   				
	   				String chgstate=(String)setInfoMap.get("chgstate");   // 1：变化前指标  2：变化后指标
	   				if("1".equals(chgstate))
	   					continue;
	   				if(!"V".equalsIgnoreCase((String)setInfoMap.get("flag"))) {
	   					item_name=item_name+"_"+chgstate;
	   				}
	   				String priv=(String)setInfoMap.get("priv");   // 0：无读写权限 1：读权限  2：写权限
	   				if (!"2".equals(priv)&&!"1".equals(this.userView.getHm().get("fillInfo"))){
	   					continue;
	   				}  
	   				if("1".equals(subflag)) //子集
					{
						String set_id=(String)setInfoMap.get("set_id");
						JSONArray arr = JSONArray.fromObject(_setMap.get("value"));
						 
				    	String paramStr="{\"tabid\":\""+tabid+"\",\"ins_id\":\""+ins_id+"\",\"taskid\":\""+taskid+"\",\"fromMessage\":\"0\"   }"; 
						String sql=getObjSql(paramStr, tab_vo,1,object_id,",t_"+set_id+"_"+chgstate);
		    			rset=dao.search(sql);
		    			if(rset.next())
		    			{ 
						   String sub_dataXml=Sql_switcher.readMemo(rset,"t_"+set_id+"_"+chgstate);	 
						   template_vo.setString("t_"+set_id.toLowerCase()+"_"+chgstate,getSubSetValue(arr,setInfoMap,ins_id,tabname,object_id,sub_dataXml,tabid));
		    			} 
					}
					else if("P".equals(item_type)) //照片
					{  
						if(templateDataBo.getParamBo().getInfor_type()!=1)
							continue;
						HashMap _value=(HashMap)JSON.parse(value);
						_value = (HashMap) _setMap.get("value");
						String state="";
						if(_value!=null&&_value.containsKey("state")){
							state=(String) _value.get("state");
						}
						if(_value!=null&&!"D".equalsIgnoreCase(state)){
							String file_name=(String)_value.get("file_name");
							String fileId = (String)_value.get("fileId");
							String basepre=object_id.substring(0,3);
							String a0100=object_id.substring(3);
							if (fileId != null && fileId.length()>0) {
								    if ("0".equals(taskid)){
								    	 
								        RecordVo vo = new RecordVo(tabname);
								        templateDataBo.setFileName(file_name);
								        templateDataBo.updateDAO(vo, fileId, a0100, basepre,"0", tabname);
								    }
								    else {
								        TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userView);
								        String tablename= "templet_"+tabid;
		                                RecordVo vo = new RecordVo(tablename); 
		                                templateDataBo.updateDAO(vo, fileId, a0100, basepre,ins_id, tablename);
								    } 
							}
						}else if("D".equalsIgnoreCase(state)){//bug 49240 微信删除头像保存，头像又显示出来了,库中数据没有清空。
							String basepre=object_id.substring(0,3);
							String a0100=object_id.substring(3);
						    if ("0".equals(taskid)){
						    	 
						        RecordVo vo = new RecordVo(tabname);
						        templateDataBo.updateDAO(vo, null, a0100, basepre,"0", tabname);
						    }
						    else {
						        TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userView);
						        String tablename= "templet_"+tabid;
                                RecordVo vo = new RecordVo(tablename); 
                                templateDataBo.updateDAO(vo, null, a0100, basepre,ins_id, tablename);
						    } 
						}
					}
					else if("F".equals(item_type)) //附件
					{
						String fileValues = "";
						Object[] values=(Object[])_setMap.get("value");
						JSONArray valueArray = new JSONArray();
						for(int k = 0;k<values.length;k++) {
						    HashMap valuemap = (HashMap) values[k];
						    valueArray.add(valuemap);
						}
					
						JSONArray arr = JSONArray.fromObject(JSON.parse(value));
						arr = valueArray;
						StringBuffer delIds=new StringBuffer("");
						for(int j=0;j<arr.size();j++){
							JSONObject obj = (JSONObject)arr.get(j);
							if(obj.isNullObject())
								continue;
							String state=obj.getString("state");//D：表示删除 ，空表示新增的附件
							String file_id = "";
							if(obj.containsKey("file_id")) {
							    file_id=obj.getString("file_id");
							}
							if("D".equals(state))  
							{
								delIds.append(","+PubFunc.decrypt(SafeCode.decode(file_id)));//这里没有转码加密，后台解密转码有问题
								continue;
							}
							
//							fileValues+=","+obj.getString("file_name")+"|"+System.getProperty("java.io.tmpdir")+"|"+obj.getString("name")+"|"+(obj.containsKey("file_type")?obj.getString("file_type"):"");
							fileValues+=","+obj.getString("fileId")+"|"+obj.getString("file_name")+"|"+(obj.containsKey("file_type")?obj.getString("file_type"):"");
						}
						
						if(fileValues.length()>0)
						{
							String infor_type = templateDataBo.getParamBo().getInfor_type()+"" ; 
							String _object_id =object_id; 
							if("1".equals(infor_type))
								_object_id=object_id.substring(0,3)+"`"+object_id.substring(3);
							String attachmenttype = "0"; //0:公共  1：个人
							if("file_private".equals(itemid))
								attachmenttype="1";
							AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn,tabid);
							/**
							 * 54922 
							 * 删除上次保存的错误文件功能应该排除ins_id为0的情况，
							 * 会存在多个附件报批情况，多个附件不加限制导致只能上传一个附件
							 * */
							if(!isWeiX){//如果是二维码，需要先删除上次报错保存的附件
								if(!"0".equals(ins_id)) {
									attachmentBo.deleteAttachment(ins_id, _object_id, infor_type, attachmenttype);
								}else {
									//检查是否有重复文件 有重复文件不保存
							        if(files_list.size()>0) {
                                        String[] fileStr = fileValues.substring(1).split(",");
                                        String fileValues_check = "";
                                        for(String str : fileStr) {
                                            if(StringUtils.isEmpty(str)) {
                                                continue;
                                            }
                                            if(!files_list.contains(str.split("\\|")[0])) {
                                                fileValues_check+=","+str;
                                            }
                                        }
                                        fileValues = fileValues_check;
                                    }
								}
							}
							attachmentBo.saveAttachment(ins_id, fileValues.substring(1), _object_id, infor_type, attachmenttype,true,"");//bug 51010
						}
						//删除附件
						if(delIds.length()>0)
						{ 
							String username=this.userView.getUserName(); 
							StringBuffer sb = new StringBuffer();
							sb.append("select filepath from t_wf_file where create_user='");
							sb.append(username);
							sb.append("' and file_id in(-1,");
							sb.append(delIds.toString().substring(1));
							sb.append(")");
							rset = dao.search(sb.toString());
							while(rset.next()){
								String filePath = rset.getString("filepath");
								if(StringUtils.isNotEmpty(filePath)) {
									VfsService.deleteFile(this.userView.getUserName(), filePath);
								}
								/*if(StringUtils.isNotBlank(filePath)){
									File file = new File(filePath);
									if(file.exists()){//如果文件存在则删除
										file.delete();
									}
								}*/
							}
							sb.setLength(0);
							sb.append("update  t_wf_file set state=1 where create_user='");
							sb.append(username);
							sb.append("' and file_id in(-1,");
							sb.append(delIds.toString().substring(1));
							sb.append(")");
							dao.update(sb.toString()); 
						} 
					}
					else
					{
//		   				item_name=item_name+"_"+chgstate;
	   				    //String fillable=(String)setInfoMap.get("fillable");   //是否必填 bug 32629 保存时校验必填影响计算公式计算，在报批时还会校验必填，此处必填校验去掉。
		   				String hz=(String)setInfoMap.get("hz");
		   				String format=(String)setInfoMap.get("format");
		   				/*if(fillable.equalsIgnoreCase("true")&&(value==null||value.trim().length()==0))
		   					  throw new GeneralException(hz+"为必填项!");*/
		   				
		   				if("A".equalsIgnoreCase(item_type)|| "M".equalsIgnoreCase(item_type))
		   				{
		   					if(value==null)
		   						value="";
		   					template_vo.setString(item_name.toLowerCase(),value);
		   					
		   					//当存在a0101_2 变化后的指标 实时更新到a0101_1中
		   					if("a0101_2".equalsIgnoreCase(item_name)) {
		   						template_vo.setString("a0101_1",value);
		   					}
		   					
		   				}
		   				else if("N".equalsIgnoreCase(item_type))
		   				{
		   					if(value==null||"null".equals(value))
		   						value=null;
		   					if (value==null || "".equals(value)){
		   						template_vo.setObject(item_name.toLowerCase(), null);
		   					}
		   					else if(value.matches("^[+-]?[\\d]*[.]?[\\d]+"))
		   					{
			   					if(decimal_width==0)
			   					{	
			   						if (value.length()>item_length){
			   							value=value.substring(0,item_length);
			   						}
			   						String a_value = PubFunc.round(value, 0);
			   						template_vo.setInt(item_name.toLowerCase(),Integer.parseInt( a_value));
			   					}
			   					else{
			   						//判断是数值是否超过精度
			   						String intValue= value;//整数位
			   						String decValue="";
			   						if (value.contains(".")){
			   							String [] arrTmp= value.split("\\.");
			   							intValue=arrTmp[0];
			   							decValue=arrTmp[1];
			   						}
			   						
			   						if (intValue.length()>item_length){
			   							intValue=intValue.substring(0,item_length);
			   						}
			   						if (decValue.length()>decimal_width){
			   							decValue=decValue.substring(0,decimal_width);
			   						}
			   						value=intValue;
			   						if (decValue.length()>0){
			   							value=value+"."+decValue;
			   						}
			   						template_vo.setDouble(item_name.toLowerCase(),Double.parseDouble(value)); 
			   						
			   					}
		   					}
		   					else
		   					{
		   						throw new GeneralException(hz+"需为数值!");
		   					}
		   				}
		   				else if("D".equalsIgnoreCase(item_type))
		   				{
		   					if(value!=null&&value.trim().length()==0)
		   					{
		   						value=null;
		   						template_vo.setDate(item_name.toLowerCase(),value);
		   					}
		   					else
		   					{
			   					Calendar cd=Calendar.getInstance(); 
			   					value=value.replaceAll("\\.","-");
			   					if("yyyy.MM.dd".equals(format)|| "yyyy.MM.dd hh:mm".equals(format))
			   					{	
			   						if("yyyy.MM.dd hh:mm".equals(format))
			   							value+=":00";
			   						format=format.replace("hh", "HH");
			   						template_vo.setDate(item_name.toLowerCase(),value);
			   					
			   					}
			   					else if("yyyy".equals(format))
			   						template_vo.setDate(item_name.toLowerCase(),value+"-"+(cd.get(Calendar.MONTH)+1)+"-"+(cd.get(Calendar.DATE)));
			   					else if("yyyy.MM".equals(format))
			   						template_vo.setDate(item_name.toLowerCase(),value+"-"+(cd.get(Calendar.DATE)));
		   					}
		   				}
					}
	   				
	   			}
	   			if(template_vo.hasAttribute("photo")){//前面已经将photo单独保存了，vo保存时oracle报错，这里判断是否有照片字段，有就移除。
	   				template_vo.removeValue("photo");
	   			}
	   			dao.updateValueObject(template_vo); 
	   			autoCompute(ins_id,fromMessage,tabid,taskid); //保存时自动计算
	   			
	    		ArrayList _fieldList=setFieldList(valueStr,tab_vo,"","1",templateDataBo.getParamBo()); 
	    		returnMap.put("fieldList",_fieldList); 
	   		}
	   		catch(Exception ex)
	   		{
				 ex.printStackTrace(); 
				 ex.printStackTrace(); 
				 String errorMsg=ex.toString();
		         int index_i=errorMsg.indexOf("description:");
		         info=errorMsg.substring(index_i+12); 
			//	 throw GeneralExceptionHandler.Handle(ex);
	   		}
			finally
			{
					PubFunc.closeDbObj(rset);
			}
	   		returnMap.put("info",info); 
			return JSON.toString(returnMap);
   }
   /**
    * 撤销任务
    * @param tabid
    * @param task_id
    * @return
    */
	private void delTask(String tab_id, String task_id,String infor_type) {
		ContentDAO dao = new ContentDAO(this.conn);
		AttachmentBo attachmentBo=new AttachmentBo(userView, conn, tab_id);
		RowSet rs = null;
		RowSet rs_file=null;
		try {
			//删除通知单里的记录
			String updateMsg="delete from tmessage where   object_type=?  and noticetempid=? ";
			ArrayList updateMsgList=new ArrayList();
			String select_str=""; 
			if("1".equals(infor_type))
			{
				updateMsg+=" and a0100=?  and lower(db_type)=?";
				select_str=" a0100,basepre ";
			
			}
			else if("2".equals(infor_type))
			{
				updateMsg+=" and b0110=? ";
				select_str=" b0110 ";
			
			}
			else if("3".equals(infor_type))
			{
				updateMsg+=" and e01a1=? ";
				select_str=" e01a1 ";
			
			}
			
			 // 根据tab_id和ins_id的取值得到人事异动列表对应的表名
			String setname= "g_templet_"+tab_id; 
			StringBuffer sql=new StringBuffer();
			if(task_id.length()>0 && !"0".equalsIgnoreCase(task_id)){//进入了审批流
				setname="templet_"+tab_id; 
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				sql.append(" and t_wf_task_objlink.submitflag=1 and t_wf_task_objlink.task_id in ("+task_id+")  and t_wf_task_objlink.state<>3   )");  
				
			}
			else//未进入审批流
			{ 
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  submitflag=1 "); 
			}
			rs=dao.search(sql.toString());
			while(rs.next())
			{ 
				ArrayList valueList=new ArrayList();
				valueList.add(new Integer(infor_type));
				valueList.add(new Integer(tab_id));
				if("1".equals(infor_type))
				{
					valueList.add(rs.getString("a0100"));
				    valueList.add(rs.getString("basepre").toLowerCase());
				}
				else if("2".equals(infor_type)) {
					valueList.add(rs.getString("b0110"));
				}
				else if("3".equals(infor_type)) {
					valueList.add(rs.getString("e01a1"));
				}
				updateMsgList.add(valueList);
			}  
			if(updateMsgList.size()>0) {
				dao.batchUpdate(updateMsg.toString(), updateMsgList); //撤销记录时对通知表的操作
			}
			 TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tab_id),this.userView);
			///开始撤销数据
			 TemplateParam paramBo=new TemplateParam(this.conn,this.userView,Integer.parseInt(tab_id));
			 TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
			if("0".equals(task_id)){//未进入审批流
			    tablebo.updateWorkCodeState(setname,"submitflag=1");
			    //删除签章
			    StringBuffer signsql=new StringBuffer("select * from "+"g_templet_"+tab_id); 
			    signsql.append(" where  submitflag=1");
			    rs=dao.search(signsql.toString());
			    ArrayList personList=new ArrayList();
			    while(rs.next()){
			    	if("1".equalsIgnoreCase(infor_type)){
			    		String nbase=rs.getString("BasePre");
			    		String a0100=rs.getString("a0100");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		list.add(a0100);
			    		personList.add(list);
			    	}else if("2".equalsIgnoreCase(infor_type)){
			    		String nbase=rs.getString("b0110");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		personList.add(list);
			    	}else if("3".equalsIgnoreCase(infor_type)){
			    		String nbase=rs.getString("e01a1");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		personList.add(list);
			    	}
			    	String signature = rs.getString("signature");
			    	if(signature!=null&&!"".equalsIgnoreCase(signature))//liuyz bug28641
			    	{
			    		delSignatureXml(dao,signature);
			    	}
			    	//删除附件 liuyz bug 26890 
					if("1".equals(infor_type)&&(rs.getString("basepre")!=null&&rs.getString("a0100")!=null))
					{
						ArrayList childList=new ArrayList();						
						childList.add(rs.getString("basepre"));
						childList.add(rs.getString("a0100"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						String sql_filepath="select filepath from t_wf_file where Lower(basepre)=Lower(?) and objectid=? and tabid=? and create_user=? and ins_id=? ";
						rs_file = dao.search(sql_filepath, childList);
						while(rs_file.next()) {
							attachmentBo.delFileByVfs(rs_file.getString("filepath"));
						}
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where Lower(basepre)=Lower(?) and objectid=? and tabid=? and create_user=? and ins_id=?");
						dao.delete(attarSql.toString(), childList);
					}
					else if("2".equals(infor_type)&&(rs.getString("b0110")!=null)){
						ArrayList childList=new ArrayList();						
						childList.add(rs.getString("b0110"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						String sql_filepath="select filepath from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=? ";
						rs_file = dao.search(sql_filepath, childList);
						while(rs_file.next()) {
							attachmentBo.delFileByVfs(rs_file.getString("filepath"));
						}
						
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						dao.delete(attarSql.toString(), childList);
					}else if("3".equals(infor_type)&&(rs.getString("e01a1")!=null)){
						ArrayList childList=new ArrayList();						
						childList.add(rs.getString("e01a1"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						
						String sql_filepath="select filepath from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=? ";
						rs_file = dao.search(sql_filepath, childList);
						while(rs_file.next()) {
							attachmentBo.delFileByVfs(rs_file.getString("filepath"));
						}
						
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						dao.delete(attarSql.toString(), childList);
					}
			    }
			    dao.update("delete from "+setname+"  where  submitflag=1 ");
			    chgLogBo.deleteChangeInfoNoInProcess(personList, "0", tab_id, "0", Integer.parseInt(infor_type));//删除变动日志信息。
			}
			else
			{ 
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.conn,this.userView);
				TemplateParam tableParamBo = new TemplateParam(this.conn, this.userView,Integer.parseInt(tab_id));
				int sp_mode = tableParamBo.getSp_mode();
				StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id); 
				strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				strsql.append(" and  task_id=xxx  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
				
				//往考勤申请单中写入报批记录
				ins.insertKqApplyTable(strsql.toString().replaceAll("xxx",task_id),tab_id,"0","10","templet_"+tab_id);
				//删除变动日志信息。
				chgLogBo.deleteChangeInfoInProcess(strsql.toString().replaceAll("xxx",task_id),tab_id,Integer.parseInt(infor_type));	
				
				
				 
				{
					StringBuffer t_sql=new StringBuffer("select * from templet_"+tab_id+" where  ");
					t_sql.append("   exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
					t_sql.append(" and t_wf_task_objlink.task_id in ("+task_id+")  and (t_wf_task_objlink.state is null  or t_wf_task_objlink.state=0)  and t_wf_task_objlink.tab_id="+tab_id+"  ");
					t_sql.append(" and t_wf_task_objlink.submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )   )");  
					ArrayList recordList=dao.searchDynaList(t_sql.toString());
					TemplateInterceptorAdapter.deleteRecords(recordList,new Integer(tab_id).intValue(),paramBo,this.userView); 
				} 
				
				dao.update("update t_wf_task_objlink set state=3   where task_id in ("+task_id+")  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
				/**结束正在处理的任务*/
				 
				int taskid=Integer.parseInt(task_id);
				if(ins.isStartNode(taskid+"") && isAllSelectedTaskId(dao, tab_id, taskid)){
					ins.processEnd(Integer.valueOf(taskid), Integer.valueOf(tab_id), userView,0);					
				}else{
					String topic=tablebo.getRecordBusiTopic(taskid,0); 
					if(topic.indexOf(",共0")!=-1)
					{   
						int ins_id = this.isAllPriTask(dao, tab_id, taskid ,sp_mode);
						if(ins_id!=-1){
							//结束流程
							RecordVo ins_vo=new RecordVo("t_wf_instance");
							ins_vo.setInt("ins_id",ins_id);
							ins_vo=dao.findByPrimaryKey(ins_vo);
							if(ins_vo!=null){
								ins_vo.setDate("end_date",new Date());
								ins_vo.setString("finished","6");
								dao.updateValueObject(ins_vo);
							}
						}
						RecordVo task_vo=new RecordVo("t_wf_task");
						task_vo.setInt("task_id",taskid);
						task_vo=dao.findByPrimaryKey(task_vo);
						if(task_vo!=null)
						{
							topic=tablebo.getRecordBusiTopicByState(taskid,3);
							task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
							task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
							task_vo.setString("task_topic", topic);
							
							String fullsender=this.userView.getUserFullName();
							if(fullsender==null|| "".equalsIgnoreCase(fullsender))
								fullsender=this.userView.getUserName(); 
							String sender=null;
							if(this.userView.getStatus()!=0)
								sender=this.userView.getDbname()+this.userView.getA0100();
							else
								sender=this.userView.getUserId();
							String appuser=task_vo.getString("appuser")+this.userView.getUserName()+",";
							task_vo.setString("appuser", appuser);
							task_vo.setString("a0100",sender);
							task_vo.setString("a0101",fullsender);
							task_vo.setString("content","撤销记录");
							if(ins_id!=-1){
								task_vo.setString("state", "06");
								task_vo.setString("task_type", String.valueOf(NodeType.END_NODE));
								task_vo.setString("actorname", "");
								task_vo.setString("sp_yj", "02");
							}
							dao.updateValueObject(task_vo);
						}
						/** 删除其它系统的待办任务 */
						PendingTask imip=new PendingTask();
						String pendingType="业务模板";  
						String pendingCode="HRMS-"+PubFunc.encrypt(taskid+""); 
						imip.updatePending("T",pendingCode,100,pendingType,userView);
					}
					else
						dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+taskid); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs_file);
		}
	}
   
	/**
	 * 确认流程走过的节点是否都是当前节点之前一条线上的
	 * @param dao
	 * @param tabid
	 * @param task_id
	 * @param sp_mode 
	 * @return
	 * @throws GeneralException
	 */
	private int isAllPriTask(ContentDAO dao, String tabid, int task_id, int sp_mode) throws GeneralException {
		int ins_id = -1;
		recorsionCount = 0;
		RowSet rs=null;
		try{
	            String sqlstr = "select * from t_wf_task where ins_id=(select ins_id from t_wf_task where task_id="+task_id+")";
	            rs=dao.search(sqlstr);
	            int insid = 0;
	            int current_node_id = 0;
	            //走过的节点集合
	            HashSet usedNode = new HashSet();
	            while(rs.next())
	            {
	                if(task_id==rs.getInt("task_id")){//当前任务对应的节点
	                	current_node_id = rs.getInt("node_id");
	                	insid = rs.getInt("ins_id");
	                }else
	                	usedNode.add(rs.getInt("node_id"));
	            }
	            if(sp_mode==1) {
	            	ins_id = insid==0?-1:insid;
	            }else {
	            	int current_node_id_ = current_node_id;
		            //其一条线上的节点集合
		            HashSet usedNode_ = new HashSet();
	            	//找出前一个节点 并与走过的节点对比 并把它放入一个set中
		            //递归 超过400次自动结束
	            	this.getpriNode(dao,usedNode_,current_node_id_,usedNode);
	            	if(recorsionCount<=400){
	            		if(usedNode_.size()<usedNode.size()){//证明有不是他一条线的
	    	            }else{
	    	            	//将流程结束
	    	            	ins_id = insid;
	    	            }
	            	}
	            }
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	        }
		 return ins_id;
	}
	
	/**
	 * 递归调用查找上一个节点
	 * @param dao
	 * @param usedNode_
	 * @param current_node_id_
	 * @param usedNode 
	 * @throws SQLException
	 */
	private void getpriNode(ContentDAO dao, HashSet usedNode_, int current_node_id_, HashSet usedNode) throws SQLException{
		RowSet rowSet = null;
		int pre_node = 0;
		if(recorsionCount>400){//超过400次自动退出递归
			return;
		}
		recorsionCount++;
		String sql = "select pre_nodeid from t_wf_transition where next_nodeid="+current_node_id_;
    	rowSet = dao.search(sql);
    	while(rowSet.next()){
    		pre_node = rowSet.getInt("pre_nodeid");//前一个节点
    		if(usedNode_.contains(pre_node))
    			return;
    		if(usedNode.contains(pre_node)){
    			current_node_id_ = pre_node;
    			usedNode_.add(pre_node);
    			getpriNode(dao,usedNode_,current_node_id_,usedNode);
    		}
    	}
	}
	
	/**   
	 * @Title: isSelectedTaskId   
	 * @Description: 判断单据里面的记录是否被选中，如果没有选中的，则后续不处理   
	 * @param @param dao
	 * @param @param tabid
	 * @param @param task_id
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @author:wangrd   
	 * @throws   
	*/
	private boolean isAllSelectedTaskId(ContentDAO dao,String tabid,int task_id)throws GeneralException
    {
	    boolean b=false;
	    RowSet rs=null;
        try
        {
            String sqlstr = "select count(*) from templet_"+tabid 
                +" where  exists (select null from t_wf_task_objlink where templet_"
                +tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "
                +"  and task_id="+task_id+"   and submitflag=0  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            rs=dao.search(sqlstr);
            if(rs.next())
            {
                if(rs.getInt(1)==0)
                   b=true; 
            }
                    
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally {
        	PubFunc.closeDbObj(rs);
        }
        return b;
    }
	
	/**
	 * 删除签章相关数据
	 * @param dao
	 * @param signature
	 */
	private void delSignatureXml(ContentDAO dao,String signature) {
    	Document doc = null;
    	try {
			doc = PubFunc.generateDom(signature);
			Element ele = doc.getRootElement().getChild("record");
			List<Element> list = ele.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				if("item".equals(e.getName())){
					String documentid = "BJCA";
					String SignatureID = e.getAttributeValue("SignatureID");
					String sql = "delete from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'";
					dao.delete(sql, new ArrayList());
					File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
				    if (!tempFile.exists()) {  
				    	 continue;
				    }  
				    tempFile.getAbsoluteFile().delete();
				}
			}
			
    	}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
   //撤回功能
	private HashMap<String, String> recallTask(String ins_id, String task_id, String tabid,String ischeck) {
		HashMap<String,String> msgMap=new HashMap<String, String>();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			String isRecallTask="";
			if("1".equals(ischeck)) {
				isRecallTask=this.isRecallTask(task_id, tabid);
			}
			if(StringUtils.isNotEmpty(isRecallTask)) {//无法撤回
				msgMap.put("success", "false");
				msgMap.put("type", "1");
				msgMap.put("msg",isRecallTask);
				return msgMap;
			}else {
				StringBuffer delsqlfortask=new StringBuffer();
				StringBuffer delsqlforobjlink=new StringBuffer();
				StringBuffer delsqlforins=new StringBuffer();
				//删除任务 将insid对应的t_wf_task以及t_wf_task_objlink 中数据清除
				delsqlfortask.append("delete from t_wf_task where ins_id = ? ");
				delsqlforobjlink.append("delete from t_wf_task_objlink where ins_id = ? ");
				delsqlforins.append("delete from t_wf_instance where ins_id =? ");
				//判断此ins_id对应的人员或者单位在起草临时表中是否存在正在起草的人
				String recallname ="";
				String ishaverecall="0";
				if("1".equals(ischeck)) {
					recallname=this.getStartTask(ins_id,tabid);
					
				}else if("0".equals(ischeck)) {
					ishaverecall="1";
				}
				if(StringUtils.isNotBlank(recallname)) {//撤回覆盖
					msgMap.put("success", "false");
					msgMap.put("type", "2");
					msgMap.put("msg",recallname);
					return msgMap;
				}
				TemplateInterceptorAdapter.afterHandle(0,Integer.parseInt(ins_id),Integer.parseInt(tabid),null,"recall",this.userView);
				//对选中任务对应的临时表的数据先迁移到起草的临时表然后进行删除templet_tabid
				this.deleteTempletData(ins_id,tabid,"9",ishaverecall);
				
				/**对选中的任务t_wf_task进行删除*/
				dao.delete(delsqlfortask.toString(), Arrays.asList(ins_id));
				/**对选中的任务关联表t_wf_task_objlink进行删除*/
				dao.delete(delsqlforobjlink.toString(),Arrays.asList(ins_id));
				/**对选中的任务关联表t_wf_instance进行删除*/
				dao.delete(delsqlforins.toString(), Arrays.asList(ins_id));
				/** 删除其它系统的待办任务 */
				this.deletePendingTask(task_id);
				
				msgMap.put("success", "true");
				msgMap.put("type", "3");
				msgMap.put("msg", "");
				return msgMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return msgMap;
	}
	
	/**
     * 删除其它系统的待办任务
     * @param taskIdList
     */
	private void deletePendingTask(String task_id) {
		try {
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";
			task_id=PubFunc.encrypt(task_id);
			imip.updatePending("T","HRMS-"+task_id,100,pendingType,this.userView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对选中任务对应的临时表的数据先迁移到起草的临时表然后进行删除templet_tabid
	 * @param ins_id
	 * @param tab_id
	 * @param module_id
	 * @param ishaverecall 
	 * @throws GeneralException
	 */
	private void deleteTempletData(String ins_id, String tab_id, String module_id, String ishaverecall) throws GeneralException {
		//对选中任务对应的临时表的数据先迁移到起草的临时表
		try {
			TemplateTableBo tablebo = new TemplateTableBo(this.conn, 
	                Integer.parseInt(tab_id), this.userView);
			boolean bSelfApply = false;
			if("9".equals(module_id))
				bSelfApply = true;
			tablebo.setBEmploy(bSelfApply);
			//调考勤接口
			String sql = "select * from templet_"+tab_id+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+
					ins_id+" and state=3  ) and ins_id="+ins_id;
			String tablename="templet_"+tab_id;
	   	    WF_Instance wf_ins=new WF_Instance(tablebo, this.conn);
	   	    wf_ins.insertKqApplyTable(sql,tab_id,"","10",tablename); //往考勤申请单中写
			tablebo.saveRecallTemplatedata(this.userView.getUserName(),Integer.parseInt(ins_id),"",ishaverecall);
			TempletChgLogBo chgLogBo=new TempletChgLogBo(conn,this.userView);
			chgLogBo.recallTaskUpdateInsidToZero(ins_id);//把流程中的变动日志改回起草状态
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断此ins_id对应的人员或者单位在起草临时表中是否存在正在起草的人
	 * @param ins_id
	 * @param tab_id
	 * @param module_id
	 * @param dao
	 * @throws GeneralException
	 */
    private String getStartTask(String ins_id, String tab_id) throws GeneralException{
    	String recallname = "";
    	try {
			TemplateTableBo tablebo = new TemplateTableBo(this.conn, 
	                Integer.parseInt(tab_id), this.userView);
			boolean bSelfApply = false;
			bSelfApply = true;//移动端默认自助模块
			tablebo.setBEmploy(bSelfApply);
			recallname = tablebo.getRecallStartTask(this.userView.getUserName(),Integer.parseInt(ins_id));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return recallname;
	}
   /***
    * 判断此单据是否可以撤销
    * @param task_id
    * @param tabid
    * @return
    */
	private String isRecallTask(String task_id, String tabid) {
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		String notRecallName = "";
		try {
			// 判断是不是发起人
			boolean isStart = false;
			Boolean hasRevoke = true;
			StringBuffer strsql = new StringBuffer();
			strsql.append("select 1 from t_wf_instance twi where ");
			strsql.append(
					"((twi.actor_type=4 and lower(twi.actorid)='" + this.userView.getUserName().toLowerCase() + "') ");
			if (this.userView.getA0100() != null && !"".equals(this.userView.getA0100())) {
				strsql.append(" or (twi.actor_type=1 and lower(twi.actorid)='" + this.userView.getDbname().toLowerCase()
						+ this.userView.getA0100() + "') ");
			}
			strsql.append(" ) and  twi.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id =?) ");
			rs = dao.search(strsql.toString(), Arrays.asList(task_id));
			if (rs.next()) {
				isStart = true;
			}
			if (isStart) {
				strsql.setLength(0);
				rs = null;
				strsql.append("select count(*) num from t_wf_task twt where twt.task_type=2 ");
				strsql.append(
						"and twt.bread=1 and twt.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id =?) ");
				rs = dao.search(strsql.toString(), Arrays.asList(task_id));
				if (rs.next()) {
					int num = rs.getInt("num");
					if (num > 0) {
						hasRevoke = false;
					}
				}
			} else {
				hasRevoke = false;
			}
			if (!hasRevoke) {
				// 查出模板名字
				RecordVo vo = new RecordVo("template_table");
				vo.setInt("tabid", Integer.parseInt(tabid));
				vo = dao.findByPrimaryKey(vo);
				String tabname = vo.getString("name");
				if (StringUtils.isNotBlank(tabname) && notRecallName.indexOf(tabname) == -1) {
					notRecallName += tabname;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return notRecallName;
	}
	
   private boolean isA0100Exits(String tableName,String A0100){
	   RowSet rs=null;
	   try {
		
		   ContentDAO dao=new ContentDAO(this.conn);
		   rs=dao.search("select A0100 from "+tableName+" where A0100='"+A0100+"'");
		   if(rs.next()){
			   return false;
		   }
		   
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		PubFunc.closeDbObj(rs);
	}
	   return true;
   }

   private  String getAllSubSetValue(JSONArray valueArray,HashMap setInfoMap,String ins_id,String tabname,String objid,String Sub_dataXml,String tabid) throws GeneralException
   {
	   String sub_dataXml="";
	   try
	   {
		    
		   HashMap dataMap=new HashMap();
		   for(int j=0;j<valueArray.size();j++){
				JSONObject obj = (JSONObject)valueArray.get(j);
				String i9999=obj.getString("I9999");;
				String timestamp=""; 
				if(obj.get("timestamp")!=null)
					timestamp=obj.getString("timestamp");
				dataMap.put(i9999+"|"+timestamp,obj);
		   } 
		    if(StringUtils.isEmpty(Sub_dataXml)) {//子集设置变化后 子集内容为空时 新建子集xml
		    	StringBuilder sbl=new StringBuilder();
		    	ArrayList<HashMap<String,String>> subColList=(ArrayList<HashMap<String,String>>)setInfoMap.get("sub_domain");
		    	for(HashMap<String, String> map:subColList) {
		    		sbl.append(map.get("item_id")+"`");
		    	}
		    	Sub_dataXml+="<?xml version=\"1.0\" encoding=\"GBK\"?><records columns=\""+(sbl.length()>0?sbl.substring(0, sbl.length()-1):"")+"\"></records>";
		    }
		    	
			Sub_dataXml=Sub_dataXml.replace("&", "＆");
			Document doc=PubFunc.generateDom(Sub_dataXml);
			Element eleRoot=null;  //xml解析得到/records对象
			Element element=null;  //xml解析得到/record对象
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			eleRoot =(Element) findPath.selectSingleNode(doc);
			if(eleRoot!=null){
				String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
				String[] columnArr=columns.split("`");
				
				boolean isCdata = false;
				for(int j=0;j<columnArr.length;j++){
					FieldItem item=DataDictionary.getFieldItem(columnArr[j]);
					if(item!=null){
						int inputtype = item.getInputtype();
						if("M".equalsIgnoreCase(item.getItemtype())&&inputtype==1&&!isCdata){
							isCdata = true;
							break;
						}
					}
				}
				
				List recordList=eleRoot.getChildren("record"); 
				ArrayList dataList=new ArrayList();  //存储每一条record对应的值和属性
				ArrayList newObjList=new ArrayList();
				HashMap existMap=new HashMap();
				if(recordList!=null&&recordList.size()>0)
				{
					for(int i=0;i<recordList.size();i++)
					{
						element=(Element)recordList.get(i);
						String I9999=element.getAttributeValue("I9999");
						String state=element.getAttributeValue("state");
						String edit = element.getAttributeValue("edit")==null?"1":element.getAttributeValue("edit");
						String timestamp=element.getAttributeValue("timestamp")!=null?element.getAttributeValue("timestamp"):"";
						String[] old_text=element.getValue().split("`");
						JSONObject obj =(JSONObject)dataMap.get(I9999+"|"+timestamp); 
						if(obj!=null)
						{
							existMap.put(I9999+"|"+timestamp,"1");
							
							String _state=(String)obj.getString("state");
							if("D".equalsIgnoreCase(_state))
							{
								recordList.remove(i);
								continue;
							} 
							StringBuffer contentValueBuf = new StringBuffer(""); 
							for(int j=0;j<columnArr.length;j++){ 
							    if(j==0&&isCdata)
										contentValueBuf.append(" <![CDATA["); 
								if(columnArr[j].toLowerCase().indexOf("attach")!=-1) //附件
								{
									String attach_old_value="";//bug 43497 微信支持子集，点击保存报数组越界。old_text的长度小于j直接取报错。
									if(old_text.length>0&&old_text.length>j){
										attach_old_value=old_text[j];
									}
									JSONArray attach_value=obj.getJSONArray((columnArr[j]));
									contentValueBuf.append(saveSubAttachmentToNewDir(attach_old_value,attach_value,tabid));
									contentValueBuf.append("`");
									
								}
								else
								{
									
									contentValueBuf.append(obj.getString(columnArr[j])+"`");
									
								} 
							}
							if(contentValueBuf.length() > 0){
								String contentValueBuf_ = contentValueBuf.substring(0,contentValueBuf.length()-1);
								if(isCdata){
									contentValueBuf_+="]]>";
								}
								element.setText(contentValueBuf_);
							} 
						} 
						 
					}
					 
				}
				
				
			    for(int j=0;j<valueArray.size();j++){
						JSONObject obj = (JSONObject)valueArray.get(j);
						String i9999=obj.getString("I9999");;
						String timestamp=""; 
						if(obj.get("timestamp")!=null)
							timestamp=obj.getString("timestamp");
						if(existMap.get(i9999+"|"+timestamp)==null)
							newObjList.add(obj);
				 } 
					
					
				 //新增记录
				 for(int i=0;i<newObjList.size();i++)
				 {
					 JSONObject obj =(JSONObject)newObjList.get(i); 
					 element=new Element("record");
					 element.setAttribute("I9999","-1");
					 element.setAttribute("state","");
					 element.setAttribute("edit","1");
					 element.setAttribute("timestamp",System.currentTimeMillis()+"");
					
					 StringBuffer contentValueBuf = new StringBuffer(""); 
				 	 for(int j=0;j<columnArr.length;j++){ 
						    if(j==0&&isCdata)
									contentValueBuf.append(" <![CDATA["); 
							if(columnArr[j].toLowerCase().indexOf("attach")!=-1) //附件
							{
								 
								JSONArray attach_value=obj.getJSONArray((columnArr[j]));
								contentValueBuf.append(saveSubAttachmentToNewDir("",attach_value,tabid));
								contentValueBuf.append("`");
								
							}
							else
							{ 
								contentValueBuf.append(obj.getString(columnArr[j])+"`"); 
							} 
					  }
					 if(contentValueBuf.length() > 0){
							String contentValueBuf_ = contentValueBuf.substring(0,contentValueBuf.length()-1);
							if(isCdata){
								contentValueBuf_+="]]>";
							}
							element.setText(contentValueBuf_);
					 } 
					 recordList.add(element);	
				 } 
			 
			}

		 
		  
			sub_dataXml = outputter.outputString(doc);
		     
	   }
	   catch(Exception ex)
 	   {
			 throw GeneralExceptionHandler.Handle(ex);
 	   }
	   
	   return sub_dataXml;
   }
   /**
    * 保存子集记录
    * @param valueArray
    * @param setInfoMap
    * @throws GeneralException
    */
   private  String getSubSetValue(JSONArray valueArray,HashMap setInfoMap,String ins_id,String tabname,String objid,String Sub_dataXml,String tabid) throws GeneralException
   {
	   String sub_dataXml="";
	   try
	   {
		    
		   HashMap dataMap=new HashMap();
		   for(int j=0;j<valueArray.size();j++){
				JSONObject obj = (JSONObject)valueArray.get(j);
				String i9999=obj.getString("I9999");;
				String timestamp=""; 
				if(obj.get("timestamp")!=null&&!"undefined".equalsIgnoreCase(String.valueOf(obj.get("timestamp"))))
					timestamp=obj.getString("timestamp");
				dataMap.put(i9999+"|"+timestamp,obj);
		   } 
		    if(StringUtils.isEmpty(Sub_dataXml)) {//子集设置变化后 子集内容为空时 新建子集xml
		    	StringBuilder sbl=new StringBuilder();
		    	ArrayList<HashMap<String,String>> subColList=(ArrayList<HashMap<String,String>>)setInfoMap.get("sub_domain");
		    	for(HashMap<String, String> map:subColList) {
		    		sbl.append(map.get("item_id")+"`");
		    	}
		    	Sub_dataXml+="<?xml version=\"1.0\" encoding=\"GBK\"?><records columns=\""+(sbl.length()>0?sbl.substring(0, sbl.length()-1):"")+"\"></records>";
		    }
		    	
			Sub_dataXml=Sub_dataXml.replace("&", "＆");
			Document doc=PubFunc.generateDom(Sub_dataXml);
			Element eleRoot=null;  //xml解析得到/records对象
			Element element=null;  //xml解析得到/record对象
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			eleRoot =(Element) findPath.selectSingleNode(doc);
			if(eleRoot!=null){
				String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
				String[] columnArr=columns.split("`");
				
				boolean isCdata = false;
				for(int j=0;j<columnArr.length;j++){
					FieldItem item=DataDictionary.getFieldItem(columnArr[j]);
					if(item!=null){
						int inputtype = item.getInputtype();
						if("M".equalsIgnoreCase(item.getItemtype())&&inputtype==1&&!isCdata){
							isCdata = true;
							break;
						}
					}
				}
				
				List recordList=eleRoot.getChildren("record"); 
				//ins_id 为0则为发起时 且 是微信扫二维码进入时 之前存储的记录应该默认为空 防止因为校验重复修改导致子集数据重复
				if("0".equals(ins_id)&&!this.isWeiX) {
					if(recordList!=null&&recordList.size()>0) {
						recordList.clear();
					}
				}
				ArrayList dataList=new ArrayList();  //存储每一条record对应的值和属性
				ArrayList newObjList=new ArrayList();
				HashMap existMap=new HashMap();
				if(recordList!=null&&recordList.size()>0)
				{
					Iterator iterator = recordList.iterator();
					while(iterator.hasNext())
					{
						element=(Element)iterator.next();
						String I9999=element.getAttributeValue("I9999");
						String state=element.getAttributeValue("state");
						String edit = element.getAttributeValue("edit")==null?"1":element.getAttributeValue("edit");
						String timestamp=element.getAttributeValue("timestamp")!=null&&!"undefined".equalsIgnoreCase(element.getAttributeValue("timestamp"))?element.getAttributeValue("timestamp"):"";
						String[] old_text=element.getValue().split("`",-1);
						JSONObject obj =(JSONObject)dataMap.get(I9999+"|"+timestamp); 
						if(obj!=null)
						{
							existMap.put(I9999+"|"+timestamp,"1");
							
							String _state=(String)obj.getString("state");
							if("D".equalsIgnoreCase(_state))
							{
								if("-1".equalsIgnoreCase(I9999)){
									iterator.remove();
								}else{
									element.setAttribute("state","D");
								}
								continue;
							} 
							StringBuffer contentValueBuf = new StringBuffer(""); 
							for(int j=0;j<columnArr.length;j++){ 
							    if(j==0&&isCdata)
										contentValueBuf.append(" <![CDATA["); 
								if(columnArr[j].toLowerCase().indexOf("attach")!=-1) //附件
								{
									String attach_old_value="";//bug 43497 微信支持子集，点击保存报数组越界。old_text的长度小于j直接取报错。
									if(old_text.length>0&&old_text.length>j){
										attach_old_value=old_text[j];
									}
									if(!JSONNull.getInstance().equals(obj.get((columnArr[j])))) {
										JSONArray attach_value=obj.getJSONArray((columnArr[j]));
										contentValueBuf.append(saveSubAttachmentToNewDir(attach_old_value,attach_value,tabid));
									}
									contentValueBuf.append("`");
									
								}
								else
								{
									
									contentValueBuf.append(obj.getString(columnArr[j])+"`");
									
								} 
							}
							if(contentValueBuf.length() > 0){
								String contentValueBuf_ = contentValueBuf.substring(0,contentValueBuf.length()-1);
								if(isCdata){
									contentValueBuf_+="]]>";
								}
								element.setText(contentValueBuf_);
							} 
						} 
						 
					}
					 
				}
				
				
			    for(int j=0;j<valueArray.size();j++){
						JSONObject obj = (JSONObject)valueArray.get(j);
						String i9999=obj.getString("I9999");;
						String timestamp=""; 
						if(obj.get("timestamp")!=null&&!"undefined".equalsIgnoreCase(String.valueOf(obj.get("timestamp"))))
							timestamp=obj.getString("timestamp");
						if(existMap.get(i9999+"|"+timestamp)==null)
							newObjList.add(obj);
				 } 
					
					
				 //新增记录
				 for(int i=0;i<newObjList.size();i++)
				 {
					 JSONObject obj =(JSONObject)newObjList.get(i); 
					 element=new Element("record");
					 element.setAttribute("I9999","-1");
					 element.setAttribute("state","");
					 element.setAttribute("edit","1");
					 String timestamp=""; 
					 if(obj.get("timestamp")!=null&&!"undefined".equalsIgnoreCase(String.valueOf(obj.get("timestamp"))))
							timestamp=obj.getString("timestamp");
					 if(StringUtils.isBlank(timestamp)){
						 timestamp=System.currentTimeMillis()+"";
					 }
					 element.setAttribute("timestamp",timestamp);
					
					 StringBuffer contentValueBuf = new StringBuffer(""); 
				 	 for(int j=0;j<columnArr.length;j++){ 
						    if(j==0&&isCdata)
									contentValueBuf.append(" <![CDATA["); 
							if(columnArr[j].toLowerCase().indexOf("attach")!=-1) //附件
							{
								if(!JSONNull.getInstance().equals(obj.get((columnArr[j])))) {
									JSONArray attach_value=obj.getJSONArray((columnArr[j]));
									contentValueBuf.append(saveSubAttachmentToNewDir("",attach_value,tabid));
								} 
								contentValueBuf.append("`");
							}
							else
							{ 
								contentValueBuf.append(obj.getString(columnArr[j])+"`"); 
							} 
					  }
					 if(contentValueBuf.length() > 0){
							String contentValueBuf_ = contentValueBuf.substring(0,contentValueBuf.length()-1);
							if(isCdata){
								contentValueBuf_+="]]>";
							}
							element.setText(contentValueBuf_);
					 } 
					 recordList.add(element);	
				 } 
			 
			}

		 
		  
			sub_dataXml = outputter.outputString(doc);
		     
	   }
	   catch(Exception ex)
 	   {
		   ex.printStackTrace();
		   throw GeneralExceptionHandler.Handle(ex);
 	   }
	   
	   return sub_dataXml;
   }
   
   
	   
	
	/**
	 * @author lis
	 * @Description: 保存子集附件到新目录
	 * @date Jul 18, 2016
	 * @param attachmentValues
	 * @return
	 * @throws GeneralException
	 */
	private String saveSubAttachmentToNewDir(String attachmentValues,JSONArray attach_value,String tabid) throws GeneralException{
		ArrayList attachmentlist = new ArrayList();
		StringBuffer attachment = new StringBuffer();
		RowSet rset=null;
    	ContentDAO dao=new ContentDAO(this.conn);
		try {
			String defaultFile_type="";
		    rset=dao.search("select id,sortname,flag from mediasort where dbflag=1 order by id");
		    while(rset.next())
		    {
		    	if(StringUtils.isBlank(defaultFile_type)) {
		    	    defaultFile_type=rset.getString("flag");
		    	}
		    } 
			 HashMap dataMap=new HashMap();
			 ArrayList new_attach=new ArrayList(); 
			 for(int j=0;j<attach_value.size();j++){
					JSONObject obj = (JSONObject)attach_value.get(j);
					String file_name=obj.getString("file_name");
					dataMap.put(file_name,obj); 
			 }
			   
			
			 int seq=0;  
			 HashMap existFileMap=new HashMap();
			 AttachmentBo attachmentBo = new AttachmentBo(this.userView, conn,tabid);
			 if(StringUtils.isNotBlank(attachmentValues)){
				String[] attachmentValueArry = attachmentValues.split(",");
				for(String attachmentValue : attachmentValueArry){
					if(StringUtils.isBlank(attachmentValue))
						continue;
					String[] subDataArry = attachmentValue.split("\\|");
					String filename = subDataArry[0];
					String filepath = subDataArry[1];  
					
					if(dataMap.get(filename)==null)
					{
						attachment.append(","+attachmentValue);
						continue;
					} 
					existFileMap.put(filename,"1"); 
					if(!filepath.endsWith(File.separator)) filepath += File.separator;
					String filePath = filepath + filename;
					filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
					File file = new File(filePath);
					
					
					
					JSONObject obj=(JSONObject)dataMap.get(filename);
					String state=obj.getString("state");
					if("D".equalsIgnoreCase(state))
					{
						if(file.exists())
							file.delete();
						continue;
					}
					seq++;
					attachment.append(","+attachmentValue);
				} 
				
	         }
			  
			 for(int j=0;j<attach_value.size();j++){
					JSONObject obj = (JSONObject)attach_value.get(j);
					String file_name=obj.getString("file_name");
					if(existFileMap.get(file_name)==null)
						new_attach.add(obj);
			 } 
			 
			 if(seq>0)
				 seq++;
			 
			 for(int i=0;i<new_attach.size();i++)
			 {
				    JSONObject obj = (JSONObject)new_attach.get(i); 
				    String file_type=obj.getString("file_type");
				    String filename=obj.getString("file_name");
				    String filepath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator");
				    String filePath = filepath + filename;
				    String fileId = "";
				    if(!JSONNull.getInstance().equals(obj.get(("fileId")))) {
				    	fileId = obj.getString("fileId");
				    }
				    String name=obj.getString("name");
					filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
					File file = new File(filePath); 
					String middlepath = "";
					if("\\".equals(File.separator)){//证明是windows
						middlepath = "subdomain\\template_";
					}else if("/".equals(File.separator)){//证明是linux
						middlepath = "subdomain/template_";
					}
					StringBuffer tempValue = new StringBuffer(); 
					if(StringUtils.isNotEmpty(fileId)) {
						VfsFileEntity enty = VfsService.getFileEntity(fileId);
						tempValue.append("|" + name); 
						tempValue.append("|" + fileId); 
						tempValue.append("|" + name); 
						tempValue.append("|" +PubFunc.round(String.valueOf(enty.getFilesize()*1.0/1024/1024),2)+"MB" ); 
						tempValue.append("|" +name.split("\\.")[0]); 
						tempValue.append("|" +(seq+i));
						tempValue.append("|type:" +defaultFile_type); 
					}else if(file.exists()){
						HashMap valueMap = new HashMap();
						//保存文件到指定目录
						attachmentBo.setRealFileName(name);
						valueMap = attachmentBo.SaveFileToDisk(file, middlepath);
						
						tempValue.append("|" + filename); 
						tempValue.append("|" + attachmentBo.getAbsoluteDir()); 
						tempValue.append("|" + name); 
						long size=attachmentBo.getFileSizes(file);
						
						if(size/1024/1024>1)
							tempValue.append("|" +PubFunc.round(String.valueOf(size*1.0/1024/1024),2)+"MB" ); 
						else
							tempValue.append("|" +PubFunc.round(String.valueOf(size*1.0/1024),2)+"KB" ); 
						
						tempValue.append("|" +filename.split("\\.")[0]); 
						tempValue.append("|" +(seq+i));
						tempValue.append("|type:" +defaultFile_type); 
						file.delete();
					}
					if(tempValue.length() > 0){
						attachment.append("," + tempValue.toString().substring(1));
					}
					 
			 } 
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		if(attachment.length()>0)
			return attachment.substring(1);
		
		return "";
	}     
   
   /**
    * 自动计算
    * @param ins_id 实例号
    * @param fromMessage    //1：来自通知单待办  0：不是
    * @param tabid  模板ID
    * @param taskid 
    * @throws GeneralException
    */
   private void  autoCompute(String ins_id,String fromMessage,String tabid, String taskid)  throws GeneralException
   {
	   try
	   {
				SearchDataBo  templatebo=new SearchDataBo(conn, this.userView,tabid);
				String selfapply="0";
				if((ins_id==null||ins_id.trim().length()==0|| "0".equals(ins_id))&& "0".equals(fromMessage))
				{
					selfapply="1";
					templatebo.getTemplateTableBo().setBEmploy(true);
				}
				//TemplateTableBo tableBo=templatebo.getTemplateTableBo();
				TemplateBo templateBo=new TemplateBo(conn, this.userView, Integer.parseInt(tabid));
				templateBo.setModuleId("9");
				templateBo.setTaskId(taskid);
				//如果为考勤业务申请模板，保存时增加自动计算功能
		 		if("1".equals(templateBo.getParamBo().getAutoCaculate())||(templateBo.getParamBo().getAutoCaculate().length()==0&&SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))))
				{  
		 			ArrayList formulalist=templateBo.readFormula();
	    			formulalist.addAll(templateBo.readSubsetFormula());
		 			if(formulalist.size()>0)
		 			{
			 			 if("0".equals(ins_id)||ins_id.trim().length()==0)
			 			{
			 				templateBo.batchCompute("0");  
			 			}
			 			else  
			 			{
			 				templateBo.batchCompute(String.valueOf(ins_id));
			 			}
		 			}
				} 
	   }
	   catch(Exception ex)
  		{
			 throw GeneralExceptionHandler.Handle(ex);
  		}
   }
   
   
   
   /**
    * 获得模板指标信息
    * @param tabid 模板ID
    * @param dao
    * @param taskid 任务号
    * @return
    */
   private HashMap getSetMap(String tabid,ContentDAO dao,String taskid,RecordVo tab_vo,String info_type,String page_no)
   {
		HashMap setMap=new HashMap();
	   try
	   {
		   
				getTabParam(tab_vo); //获得模板定义的参数
			String sxml=tab_vo.getString("sp_flag");
			if(sxml==null|| "".equals(sxml)|| "0".equals(sxml))
				this.bsp_flag=false;
			else
				this.bsp_flag=true;
			HashMap filedPriv  =new HashMap();
			if(taskid!=null&& "0".equals(sp_mode)) //自动流程中的节点需判断节点设置的指标权限
			{
				if("".equalsIgnoreCase(taskid)){//发起节点或者通知单task_id为空赋值0
					taskid="0";
				}
				filedPriv=getFieldPrivFillable(taskid,this.conn,tabid); 
			} 
			ArrayList setList=getTemplateSetList(Integer.parseInt(tabid),filedPriv,info_type,page_no,"",taskid);//传递task_id过去，判断是起草还是审批中
			
			HashMap tmpMap=null;
			for(Iterator t=setList.iterator();t.hasNext();)
			{
				tmpMap=(HashMap)t.next();
				String gridno=(String)tmpMap.get("gridno");
				String pageid=(String)tmpMap.get("pageid");
				setMap.put(pageid+"_"+gridno,tmpMap);
			}
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return setMap;
   }
   
    //-----------------------------------------------------------------------------------------------------------------------------
    
    
    /**
     * 
     * @param paramStr
     * { "tabid":"1"             //模板ID
		 ,"isEdit":"1"            // 1：数据可编辑  0：只读（浏览已批的任务）
		 ,"taskid":"20"           // 待办任务号（发起任务|通知单 为空值）
		 ."ins_id":"102"          //实例ID（发起任务|通知单 为空值）
		 ,"fromMessage":"1"     //1：来自通知单待办  0：不是
		 ,"object_id":"Usr00000001" //模板数据ID，人员：库前缀+A0100
		                                   单位|岗位：B0100|E01A1
		 ,”info_type”:”mobile”   // mobile:手机适配页   normal:电脑端页面
         ,”page_no”:”1” //页签		                                    
		} 
		说明：发起单据|来自通知单时taskid,ins_id,object_id值为空
		      审批单据时，显示待办任务object_id 数据的内容，如值为空默认显示第一个对象的数据。
     * @return
     * { “tabname”:”加班申请单”     //模板名称
		  , "tabid”:”1”     //模板id 
		  ."ins_id":"101"  //实例ID
		  ,"taskid":"20"   //任务ID
		  ,"deal_flag":"1"  //任务办理标识，0：无办理标识 1：批准+驳回 
		                                2：报批  3：提交
		   ,"no_sp_yj":"true"  //true: 审批不填写意见  false:填写
		  ,auto_appeal:"1"  // 1:自动流程  2：手工审批（暂不支持）
		  ,"object_id":"Usr00000001" //模板数据ID，人员：库前缀+A0100
		                                   单位|岗位：B0100|E01A1
		  ,"message":""   //如单据已处理返回‘该单据已处理’，否则为空
		  ,"objs":"Usr00000001, Usr00000002, Usr00000003......" //审批对象，数量最多100个
		  , "fieldList":[ { “gridno”:”1”   // 单元格编号
		  , “hz”:”请假类型”    // 指标标题--〉指标名称
		  ." item_id":"A0703"  //指标ID   
		  ," item_type":"A"    //指标类型 
		  ," item_length":"20"  //指标长度
		  ,"decimal_width":"0" //小数位数
		  ," chgstate ":"1"     // 1：变化前指标  2：变化后指标
		  ," subflag ":"1"      //1：子集  0：不是子集  
		  ,"value":"1"        //值
		  ,"value_view":"产假" //代码值描述
		  ,"priv":"1"          // 0：无读写权限 1：读权限  2：写权限
		  ,"fillable":"true"  //是否必填
		  ," code_id ":"IS"    //指标关联
		  ,"format":""  //  yyyy.MM.dd | yyyy.MM.dd hh.mm| yyyyy | yyyy.MM
		  },{.....} ]
		,codes:[
		{"codesetid":"IS"
		 ,"codelist":[{"itemid":"xxx","itemdesc":"xxxxxx"},{"itemid":"xxx","itemdesc":"xxxxxx"}....]
		}
		, {"codesetid":"AB"
		 ,"codelist":[{"itemid":"xxx","itemdesc":"xxxxxx"},{"itemid":"xxx","itemdesc":"xxxxxx"}....]
		}]
		} 
     * @throws Exception 
     * @throws NumberFormatException 
     */
    public  String  getTemplateInfo (String paramStr) throws NumberFormatException, Exception
    {
    	String templateInfo="";
    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
    	String tabid=(String)paramMap.get("tabid");
    	String ins_id=(String)paramMap.get("ins_id");
    	       ins_id=StringUtils.isEmpty(ins_id)?"0":ins_id;//
    	String taskid=(String)paramMap.get("taskid");
    		   taskid=StringUtils.isEmpty(taskid)?"0":taskid;//
		String isEdit=(String)paramMap.get("isEdit");    // 1：数据可编辑  0：只读（浏览已批的任务）
		String info_type="mobile"; // mobile:手机适配页   normal:电脑端页面
		if(paramMap.get("info_type")!=null)
			info_type=(String)paramMap.get("info_type");
		String page_no="0";   //页签
		String page_num="1";   //页数
		if(paramMap.get("page_no")!=null)  
			page_no=(String)paramMap.get("page_no");
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rset=null;
    	try
    	{  
       		TemplateTableBo	templateTableBo = new TemplateTableBo(conn, Integer.parseInt(tabid), this.userView);//bug 31637 修正微信审批意见指标可以编辑
    		initTemplate(paramMap,templateTableBo); //创建临时表、表结构同步、引入人员数据 （通知单数据）、同步数据
    		RecordVo tab_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
    		ArrayList codeList=null;
    		ArrayList pageIndexList=new ArrayList();
    		codeList=getCodeList(tabid,taskid,info_type);
    		if(!"mobile".equals(info_type))
    		{ 
    			ArrayList list=getPageList(Integer.parseInt(tabid), false, "",taskid);
    			page_num=list.size()+""; //获得模板页签数
    			for(int i=0;i<list.size();i++) {
    				TemplatePage pagebo = (TemplatePage)list.get(i);
    				pageIndexList.add(pagebo.getPageId()+"");
    			}
    			if(!pageIndexList.contains(page_no)) {
    			    if(pageIndexList.size()>0) {
    			        
    			        page_no=(String)pageIndexList.get(0);
    			    }
    				HashMap map=(HashMap)JSON.parse(paramStr);
    				if(map.get("page_no")!=null) {
    					map.put("page_no", page_no);
    					paramStr = JSONObject.fromObject(map).toString();
    				}
    			}
    		}
    		
    		String objs=getObjs(paramStr,tab_vo); //获得模板下对象信息
    		TemplateParam templateParam= new TemplateParam(conn, this.userView, Integer.parseInt(tabid));
    		if("normal".equals(info_type)) {
                if(templateParam.getOperationType()!=0) {
                    throw new Exception(ResourceFactory.getProperty("template.mobile.cardTypeErrer"));
                }
                if(templateParam.getSp_mode()!=0) {//自动流转
                    throw new Exception(ResourceFactory.getProperty("template.mobile.spModeErrer"));
                }
                if(templateParam.isAllow_defFlowSelf()) {//是否自定义审批流程
                    throw new Exception(ResourceFactory.getProperty("template.mobile.defFlowErrer"));
                }
                if(StringUtils.isEmpty(templateParam.getDest_base())) {//提交时目标库
                    throw new Exception(ResourceFactory.getProperty("template.mobile.descBaseErrer"));
                }
                if (!templateParam.isBsp_flag()) {//是否需要审批
                    throw new Exception(ResourceFactory.getProperty("template.mobile.spFlagErrer"));
                }
                if(pageIndexList.size()==0){//bug 48567 二维码模版所有页面都不显示，提示用户错误信息
                    throw new Exception(ResourceFactory.getProperty("template.mobile.noShowPageErrer"));
                }

            }

    		ArrayList fieldList=setFieldList(paramStr,tab_vo,objs,isEdit,templateParam);
    		HashMap infoMap=new HashMap();
    		infoMap.put("message","");
    		if("1".equals(isEdit)&&Integer.parseInt(ins_id)>0){
    		    String errorinfo= getInformation(ins_id,taskid);
    		    if(errorinfo.trim().length()>0)
    		        infoMap.put("message",errorinfo);
    		}
    		
    		infoMap.put("pageIndexList",pageIndexList);    
    		infoMap.put("tabname", tab_vo.getString("name"));
    		infoMap.put("tabid", tab_vo.getString("tabid"));
    		infoMap.put("ins_id", ins_id);
    		infoMap.put("taskid", taskid);
    		if(bsp_flag&& "0".equals(this.sp_mode))
    			infoMap.put("auto_appeal", "1"); //自动审批
    		else if(bsp_flag)
    			infoMap.put("auto_appeal", "2"); //手工审批 
    		
    		if("0".equals(isEdit))
    			infoMap.put("deal_flag", "0");  //任务办理标识，0：无办理标识 1：批准+驳回      2：报批  3：提交
    		else if(ins_id.length()>0&&!"0".equals(ins_id)&&taskid.length()>0&&!"0".equals(taskid)) //填充起始节点标识，此申请是否为当前用户
    		{
    		    infoMap.put("deal_flag", "1"); 
    			String isStartNode=isStartNode(ins_id,taskid,tabid,this.sp_mode); //分析此实例，是否为当前用户发起的申请
    			if("1".equals(isStartNode))
    				infoMap.put("deal_flag", "2"); 
    		}
    		else if(ins_id.trim().length()==0|| "0".equals(ins_id))
    		{
    			if(!bsp_flag)  //是否需要审批
    				infoMap.put("deal_flag", "3");
    			else
    				infoMap.put("deal_flag", "2");
    		}
    		else {
    			infoMap.put("deal_flag", "1"); 
    		}

    		//添加已读标识
    		if(!"0".equalsIgnoreCase(taskid)&&(ins_id.length()>0&&!"0".equals(ins_id))&&"1".equals(isEdit)) {
    			this.setReadFlag(taskid);//设置是否阅读
    		}
    		
    		if("1".equals(infoMap.get("deal_flag"))||"0".equals(ins_id)||"1".equals(isEdit)) {//审批状态和起草显示撤销按钮
    			infoMap.put("deleteBtn", "true");//撤销按钮标记
    		}
    		if(taskid.length()>0&&!"0".equals(taskid)) {
    			boolean flag=getRecallTask(taskid);
    			if(flag) {
    				infoMap.put("cencleBtn", "true");//撤回按钮标记
    			}
    		}
    		
    		infoMap.put("no_sp_yj",this.no_sp_yj);
    		String object_id=(String)paramMap.get("object_id");  //模板数据ID，人员：库前缀+A0100 ,  单位|岗位：B0100|E01A1
    		if(object_id==null||object_id.trim().length()==0)
    		{
    			infoMap.put("object_id", objs.split(",")[0]);
    		}
    		else
    			infoMap.put("object_id", object_id);
    		infoMap.put("objs", objs);
    		infoMap.put("page_num",page_num);
    		infoMap.put("page_no",page_no);
    		infoMap.put("fieldList",fieldList);
    		
    		if(codeList!=null)
    			infoMap.put("codes", codeList); 
    		
    		if(infoMap.size()>0)
    			templateInfo=JSON.toString(infoMap);
    	}
    	finally
    	{
    		PubFunc.closeDbObj(rset);
    	}
    	return templateInfo;
    }
    
    /**
     * 设置已读标识
     * @param taskid
     */
    private void setReadFlag(String taskid)
 	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int j=1;
			StringBuffer updateSql = new StringBuffer("update t_wf_task set bread=1 where task_id in (-1");
			StringBuffer middleSql = new StringBuffer("");
			String[] taskidStr = taskid.split(",");
			for(int i=0;i<taskidStr.length;i++){
				String taskId = taskidStr[i];
				middleSql.append(",");
				middleSql.append(taskId);
				if(i==500*j){//每500条执行一次
					j++;
					middleSql.append(")");
					dao.update(updateSql.toString()+middleSql.toString());
					middleSql.setLength(0);
				}
			}
			if(middleSql.length()>0)
				dao.update(updateSql.toString()+middleSql.toString()+" )");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    
    /**
     * 判断流程节点是否是当前操作人发起是否可撤回
     * @param ins_id
     * @param task_id
     * @return
     */
    private boolean getRecallTask(String task_id) {
    	boolean flag=true;
    	ContentDAO dao=new ContentDAO(conn);
    	RowSet rs=null;
    	try {
    		//判断是不是发起人
			boolean isStart = false;
			StringBuffer strsql=new StringBuffer();
			strsql.append("select 1 from t_wf_instance twi where twi.finished=2 and ");// 撤回的单据应该排除掉已结束的。
			strsql.append("((twi.actor_type=4 and lower(twi.actorid)='"+this.userView.getUserName().toLowerCase()+"') ");
			if(this.userView.getA0100()!=null&&!"".equals(this.userView.getA0100()))
				strsql.append(" or (twi.actor_type=1 and lower(twi.actorid)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"') ");
			strsql.append(" ) and  twi.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = '"+task_id+"') ");
			rs=dao.search(strsql.toString());
			if(rs.next())
				isStart = true;
			if(isStart){
				strsql.setLength(0);
				strsql.append("select count(*) num from t_wf_task twt where twt.task_type=2 ");
				strsql.append("and twt.bread=1 and twt.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = "+task_id+") ");
				rs = dao.search(strsql.toString());
				if(rs.next())
				{
					int num = rs.getInt("num");
					if(num>0)
					{
						flag=false;
					}
				}
			}else {
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return flag;
    }
    
    public String getInformation(String ins_id,String task_id){
		String sql = "";
		ArrayList paramList=new ArrayList();
		sql = "select  ins_id,task_id,task_topic,t_wf_task.node_id,actorid,actor_type,actorname,start_date,t_wf_node.tabid from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id and task_state='3'  and t_wf_task.task_id=? and ins_id=? ";
		paramList.add(task_id);
		paramList.add(ins_id);
		String errorinfo = "";
		if (task_id != null && task_id.length() > 0) {
			ContentDAO dao = new ContentDAO(this.conn);

			try {
				PendingTask imip=new PendingTask();
				String timeoutUration = SystemConfig.getPropertyValue("timeoutUration");
				if(StringUtils.isBlank(timeoutUration))
					timeoutUration = "30";
				if(StringUtils.isNotBlank(timeoutUration)&&Integer.parseInt(timeoutUration)<10)
					timeoutUration = "10";
				RowSet  frowset=dao.search(sql,paramList);
				RowSet  frowset2=null;
				String actor_type ="";
				String node_id="";
				String tabid ="";
				task_id="";
				int j=0;
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userView);
				while(frowset.next()){
					j++;
					actor_type = frowset.getString("actor_type");
					node_id = frowset.getString("node_id");
					tabid= frowset.getString("tabid");
					task_id=frowset.getString("task_id");
					String actor_id =frowset.getString("actorid");

					LazyDynaBean abean=null;
					HashMap dataMap=new HashMap();
					if("5".equals(actor_type))//本人
					{
						
						String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
						sql0+=" and twt.task_id=? and "+Sql_switcher.isnull("twt.state","0")+"=0 and tt.a0100=? and lower(tt.basepre)=? ";
						paramList=new ArrayList();
						paramList.add(task_id);
						paramList.add(userView.getA0100());
						paramList.add(userView.getDbname().toLowerCase());
						frowset2=dao.search(sql0,paramList);
						
						while(frowset2.next())
						{
							
							if(dataMap.get(node_id+task_id)==null)
							{
								paramList=new ArrayList();
								paramList.add(task_id);
								paramList.add(node_id);
								dao.update("update t_wf_task_objlink set special_node=1 where task_id=? and node_id=? ",paramList);
								dataMap.put(node_id+task_id,"1");
							}
							
							String username=frowset2.getString("username");
							if(username==null||username.trim().length()==0)
								dao.update("update t_wf_task_objlink set username='"+userView.getDbname().toUpperCase()+userView.getA0100()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
							
						}
						 
					}
					else if("2".equals(actor_type)){//角色

						String scope_field="";
						String containUnderOrg="0";
						paramList=new ArrayList();
						paramList.add(tabid);
						paramList.add(node_id);
						frowset2=dao.search("select * from t_wf_node where tabid=? and node_id=? ",paramList);
						String ext_param="";
						Document doc=null;
						Element element=null;
						if(frowset2.next())
							ext_param=Sql_switcher.readMemo(frowset2,"ext_param"); 
						if(ext_param!=null&&ext_param.trim().length()>0)
						{
							doc=PubFunc.generateDom(ext_param);
							String xpath="/params/scope_field";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);
							if(childlist.size()==0){
								xpath="/param/scope_field";
								 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								 childlist=findPath.selectNodes(doc);
							}
							if(childlist!=null&&childlist.size()>0)
							{
								for(int i=0;i<childlist.size();i++)
								{
									element=(Element)childlist.get(i);
									if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
									{
										scope_field=element.getText().trim();
										if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim()))
											containUnderOrg="1";
									}
								}
							}
						}
						//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
						
						if(scope_field.length()>0)
						{
							ArrayList sqlParamList=new ArrayList();
							String sql0 = "select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
							sql0 += " and twt.task_id=?  and "+Sql_switcher.isnull("twt.state","0")+"=0  and (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username=? ";
							sqlParamList.add(task_id);
							sqlParamList.add(userView.getUserName());
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql0+=" or username='"+usernameList.get(i)+"' ";
								}
							}	
							sql0 += " ) ";
							sql0 += " or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,"twt.locked_time")+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and ";
							sql0 += " ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>? ))) ";
							sqlParamList.add(userView.getUserName());

							//sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ) ";
							{
								String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
								//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
								if (actor_id!=null && actor_id.length()>0){
									String role_property="";//角色特征
									paramList=new ArrayList();
									paramList.add(actor_id);
									frowset2= dao.search("select role_property from t_sys_role where role_id=? ",paramList);
									if (frowset2.next()){    	
										role_property= frowset2.getString("role_property");
									}
									
									String filterField="";
									if ("1".equals(role_property)){//部门领导
										String e0122=this.userView.getUserDeptId();
										if (e0122!=null &&e0122.length()>0){
											operOrg="UM"+e0122;
										}
										else {
											operOrg="";
										}
									}
									else if ("6".equals(role_property)){//单位领导
										String b0110=this.userView.getUserOrgId();
										if (b0110!=null &&b0110.length()>0){
											operOrg="UN"+b0110;
										}
										else {
											operOrg="";
										}
									}
									
								}
								
								
								
								String codesetid="";
								boolean noSql=true;
								if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
								{ 
									if(scope_field.toUpperCase().indexOf("E0122")!=-1)
									{
										codesetid="UM";
										String value=getSubmitTaskInfo(task_id,"UM");
										if(value.length()>0)
										{ 
											scope_field="'"+value+"'"; 
										}
										else
										{
											noSql=false;
											 
										}
									}
									else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
									{
										codesetid="UN";  
										String value=getSubmitTaskInfo(task_id,"UN");
										if(value.length()>0)
										{
											scope_field="'"+value+"'"; 
										}
										else
										{
											noSql=false;
											 
										}
									}
								}
								else
								{
									String[] temps=scope_field.split("_");
									String itemid=temps[0].toLowerCase(); 
									
									FieldItem _item=DataDictionary.getFieldItem(itemid);
									if(_item!=null)
										codesetid=_item.getCodesetid();
								}
								if("UN`".equalsIgnoreCase(operOrg))
								{
									
								}
								else if(noSql)
								{
									if(operOrg!=null && operOrg.length() > 3)
									{
										StringBuffer tempSql = new StringBuffer(""); 
										String[] temp = operOrg.split("`");
										for (int i = 0; i < temp.length; i++) {
											if("1".equals(containUnderOrg))
											{
												tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
											}
											else
											{
												if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2)))
													tempSql.append(" or "+scope_field+"='" + temp[i].substring(2)+ "'");
												else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2)))
													tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
											}
										}
										
										if(tempSql.length()==0)
										{
											if("UN".equalsIgnoreCase(codesetid))
											{
												if("1".equals(containUnderOrg))
													tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
												else
													tempSql.append(" or "+scope_field+"='"+userView.getUserOrgId()+"'");
											}
											else if ("UM".equalsIgnoreCase(codesetid))
											{
												tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
											}
										}
										
										if(tempSql.toString().trim().length()==0)
											tempSql.append(" or 1=2 ");
										
										sql0+=" and ( " + tempSql.substring(3) + " ) ";
									}
									else
									{
										if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
										{
											if("UN".equalsIgnoreCase(codesetid))
											{
												if("1".equals(containUnderOrg))
													sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
												else
													sql0+=" and "+scope_field+"='"+userView.getUserOrgId()+"'";
											}
											else if ("UM".equalsIgnoreCase(codesetid))
											{
												sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
											}
										}
										else
											sql0+=" and 1=2 ";
									}
								}
								else
								{
									sql0+=" and 1=2 ";
								}
							}
						
							frowset2=dao.search(sql0,sqlParamList);
							ArrayList updList=new ArrayList();
							while(frowset2.next())
							{
								String username=frowset2.getString("username");
								if(dataMap.get(node_id+task_id)==null)
								{
									paramList=new ArrayList();
									paramList.add(task_id);
									paramList.add(node_id);
									dao.update("update t_wf_task_objlink set special_node=1 where task_id=? and node_id=? ",paramList);
									dataMap.put(node_id+task_id,"1");
								}
								
								//if(username==null||username.trim().length()==0)
								//{
									ArrayList tempList=new ArrayList();
									Timestamp dateTime = new Timestamp((new Date()).getTime());
									tempList.add(dateTime);
									tempList.add(userView.getUserName());
									tempList.add(frowset2.getString("seqnum"));
									tempList.add(new Integer(frowset2.getString("task_id")));
									updList.add(tempList);
								//	dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
								//}
							}
							
							if(updList.size()>0)
							{ 
								dao.batchUpdate("update t_wf_task_objlink set locked_time=?,username=? where seqnum=? and task_id=?",updList );
							}
							
							 
							sql0="select * from  t_wf_task_objlink  where task_id=?   and username=?  ";	
							paramList=new ArrayList();
							paramList.add(task_id);
							paramList.add(userView.getUserName());
							frowset2=dao.search(sql0,paramList);
							if(frowset2.next())
							{
								
							}else 
							{
								String encrypt_task_id=PubFunc.encrypt(task_id);
								imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
								errorinfo="该单据已被他人锁定处理了！";
							}
						}
						else
						{
							//普通角色也抢单 2013-7-19 dengc
							StringBuffer sqlForRole = new StringBuffer("");
							paramList=new ArrayList();
							sqlForRole.append("select username from t_wf_task_objlink twt where ");
							sqlForRole.append(" twt.task_id=?  and  (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username=?  ");
							paramList.add(task_id);
							paramList.add(userView.getUserName());
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。单
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sqlForRole.append(" or username='"+usernameList.get(i)+"' ");
								}
							}
							sqlForRole.append(" ) ");
							sqlForRole.append(" or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,"twt.locked_time")+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and ");
							sqlForRole.append(" ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>? ))) ");
							paramList.add(userView.getUserName());
							//String	sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' )   ";	
							frowset2=dao.search(sqlForRole.toString(),paramList);
							if(frowset2.next())
							{
								//String username=frowset2.getString("username");
								//if(username==null||username.trim().length()==0)
								paramList=new ArrayList();
								paramList.add(userView.getUserName());
								paramList.add(task_id);
								paramList.add(node_id);
								dao.update("update t_wf_task_objlink set locked_time="+Sql_switcher.sqlNow()+",username=?  where task_id=? and node_id=? ",paramList);
								 
							}else{
									String encrypt_task_id=PubFunc.encrypt(task_id);
									imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
									errorinfo="该单据已被他人锁定处理了！";
							}
						}
					
					
					}
				}
				if(frowset2!=null)
					frowset2.close();
				if(frowset!=null)
					frowset.close();
				
				if(j==0){
					return "该单据已处理！";
				}
			} catch (Exception arg6) {
				arg6.printStackTrace();
			}
		}

		return errorinfo;
	}
    
    
    /** 
	 * @Title: getPageList 
	 * @Description:  获取模板显示的页签
	 * @param @param isMobile 是否显示异动标签
	 * @param noShowPageNo  不显示那些页签
	 * @param @return
	 * @param @throws Exception
	 * @return ArrayList
	 */
	private ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo,String taskId) throws Exception {
		ArrayList outlist = new ArrayList();
		try {
			com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo = new com.hjsj.hrms.module.template.utils.TemplateUtilBo(this.conn,
					this.userView);
			TemplateCardBo cardBo = new TemplateCardBo(this.conn,this.userView,tabId);
						   cardBo.setTask_id(taskId);
			ArrayList list = utilBo.getAllTemplatePage(tabId);
			for (int i = 0; i < list.size(); i++) {
				TemplatePage pagebo = (TemplatePage) list.get(i);
				if(!"".equals(noShowPageNo)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(pagebo.getPageId());
					String pagearr [] = noShowPageNo.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint)
						continue;
				}else if (!pagebo.isShow()) {
					continue;
				}

				if (isMobile != pagebo.isMobile()) {
					continue;
				}
				
				if(!pagebo.isPrint())//设置此页不打印 不显示此页
					continue;
				
				//判断此页的指标无读写权限。无读写权限指标的不显示
				
				if (!cardBo.isHaveReadFieldPriv(pagebo.getPageId() + "")) {//
					continue;
				}
				
				
				outlist.add(pagebo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return outlist;
	}    
    
    /**
	 * 分析此实例，是否为当前用户发起的申请
	 * @param ins_id
	 * @param task_id
	 * @param tabid   模板ID
	 * @param sp_mode  =0自动流转，=1手工指派
	 */
	private String isStartNode(String ins_id,String task_id,String tabid,String sp_mode)
	{
		String startflag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select actorid from t_wf_instance where ins_id=?");
			ArrayList list=new ArrayList();
			list.add(ins_id);
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next())
			{
				/**
				 * 申请人编码
				 * 自助平台用户:应用库前缀+人员编码
				 * 业务平台用户：operuser中的账号
				 */
				String applyobj=rset.getString("actorid");//   ins_vo.getString("actorid");
				String a0100=this.userView.getDbname()+this.userView.getA0100();
				String usrname=this.userView.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname)))
					startflag="1";
				if("1".equals(startflag)&&task_id!=null&&!"0".equals(task_id)&&task_id.trim().length()>0&& "0".equals(sp_mode))
				{
					rset=dao.search("select nodetype from t_wf_node where tabid="+tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						 
						if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype")))
							startflag="0";
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return startflag;
	}
    
    
    
    
    
    /**
     * 初始化
     * 创建临时表、表结构同步、引入人员数据 （通知单数据）、同步数据
     * @param paramMap
     */
    private void initTemplate(HashMap paramMap,TemplateTableBo templateTableBo)
    {
    	String tabid=(String)paramMap.get("tabid");
    	String ins_id=(String)paramMap.get("ins_id");
    	String isEdit=(String)paramMap.get("isEdit");    // 1：数据可编辑  0：只读（浏览已批的任务）
    	String fromMessage=(String)paramMap.get("fromMessage");    //1：来自通知单待办  0：不是
    	if("0".equals(isEdit))
    			return;
    	try
    	{
	    	
	    	 if ("0".equalsIgnoreCase(ins_id)||ins_id.trim().length()==0) {
	             if("0".equals(fromMessage))  //业务申请
	             {
		            	 templateTableBo.setBEmploy(true);
		                 templateTableBo.createTempTemplateTable();  //创建临时表、表结构同步
		                 ArrayList a0100list=new ArrayList();
		                 a0100list.add(this.userView.getA0100());
		                 
		                 
		                 TemplateBo templateBo=new TemplateBo(this.conn,this.userView,Integer.parseInt(tabid));
		                 templateBo.setModuleId("9");
		            	 templateBo.impDataFromArchive(a0100list, this.userView.getDbname());//按人员库前缀导入数据
		                 
		               //  templateTableBo.impDataFromArchive(a0100list,this.userView.getDbname()); //引入人员数据
	              
	             }
	             else
	             {   
	               	 templateTableBo.createTempTemplateTable(this.userView.getUserName());  //创建临时表、表结构同步、引入通知单数据
	             }
	             /** 档案中与模板中的数据进行数据同步 */
	        //    templateTableBo.syncDataFromArchive(); 
	         }
	    	 else
	    	 {
	    		templateTableBo.changeSpTableStrut();
	    	 }
	    	 
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
		}
    }
    
    
    /**
     * 获得模板指标信息和对象指标对应值 
     * @param paramStr
     * @param tab_vo 表单对象
     * @param objs     模板数据
     * @param isEdit 1：数据可编辑  0：只读（浏览已批的任务）
     * @return
     */
    private ArrayList  setFieldList(String  paramStr,RecordVo tab_vo,String objs,String isEdit,TemplateParam templateParam)
    {
    	ArrayList setList=new ArrayList();
    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
    	String tabid=(String)paramMap.get("tabid");
    	String ins_id=(String)paramMap.get("ins_id");   //实例ID
    	String taskid=(String)paramMap.get("taskid");  // 待办任务号（发起任务|通知单 为空值）
    	String fromMessage=(String)paramMap.get("fromMessage");   //1：来自通知单待办  0：不是
    	String object_id=(String)paramMap.get("object_id");  //模板数据ID，人员：库前缀+A0100 ,  单位|岗位：B0100|E01A1
    	String page_no="0";   //页签 
    	if(StringUtils.isBlank(ins_id)){
    		ins_id="0";
    	}
		if(paramMap.get("page_no")!=null)  
			page_no=(String)paramMap.get("page_no");
		String info_type="mobile"; // mobile:手机适配页   normal:电脑端页面
		if(paramMap.get("info_type")!=null)
			info_type=(String)paramMap.get("info_type"); 
    	RowSet rset=null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
	    		AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn, tabid);//获取附件设置的大小，传给前台控制。
	    		attachmentBo.initParam(false);
	    		String multimedia_maxsize = attachmentBo.getMaxFileSize()/1024/1024 + "";
	    		if(StringUtils.isBlank(multimedia_maxsize)){
	    			multimedia_maxsize="20";
	    		}
    			String defaultFile_type="";
    		    HashMap fileTypeMap=new HashMap(); //多媒体类型
    		    rset=dao.search("select id,sortname,flag from mediasort where dbflag=1 order by id ");
    		    while(rset.next())
    		    {
    		    	if(StringUtils.isBlank(defaultFile_type))
    		    		defaultFile_type=rset.getString("id");
    		    	fileTypeMap.put(rset.getString("flag"), rset.getString("id"));
    		    } 
    		    String opinion_field =templateParam.getOpinion_field(); //审批意见指标
    			getTabParam(tab_vo); //获得模板定义的参数
    			String sxml=tab_vo.getString("sp_flag");
    			if(sxml==null|| "".equals(sxml)|| "0".equals(sxml))
    				this.bsp_flag=false;
    			else
    				this.bsp_flag=true;
    			
    			HashMap filedPriv  =new HashMap();
    			if(taskid!=null&& "0".equals(sp_mode)&&"1".equalsIgnoreCase(isEdit)) //自动流程中的节点需判断节点设置的指标权限
    			{
    				if("".equalsIgnoreCase(taskid)){//发起节点或者通知单task_id为空赋值0
    					taskid="0";
    				}
    				filedPriv=getFieldPrivFillable(taskid,this.conn,tabid); 
    			} 
    			setList=getTemplateSetList(Integer.parseInt(tabid),filedPriv,info_type,page_no,opinion_field,taskid);//传递taskid过去判断是起草还是流程中
    			//获得某对象指标下的值
    			if(object_id==null||object_id.trim().length()==0)
    				object_id=objs.split(",")[0];
    			String sql=getObjSql(paramStr, tab_vo,2,object_id,"");
    			rset=dao.search(sql);
    			if("mobile".equals(info_type)){
    				if(rset.next())
	    			{
	    				HashMap setMap=null;
	    				for(Iterator t=setList.iterator();t.hasNext();)
	    				{
	    					String value="";
	    					String value_view="";
	    					setMap=(HashMap)t.next();
	    					String item_id=(String)setMap.get("item_id");
	    					String item_type=(String)setMap.get("item_type");
	    					String chgstate=(String)setMap.get("chgstate");
	    					String code_id=(String)setMap.get("code_id");
	    					String hisMode=(String)setMap.get("hisMode");
	    					String subflag=(String)setMap.get("subflag");
	    					
	    					
	    			        /*
	    					if(item_type.equalsIgnoreCase("M")&&item_id.equalsIgnoreCase("photo"))
	                        {//如果指标是备注型
	                        	String filephoto="";
	                    		filephoto=ServletUtilities.createOleFile("photo","ext",rset); 
	                        	if(!(filephoto==null||filephoto.equals("")))//安全平台改造，将filename加密
	                        		value="/servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(filephoto));
	                        	else
	                        		value="blank";//没有照片时
	                        }else  
	                        */	
	    					
	    					if("1".equals(subflag)) //子集
	    					{
	    						String set_id=(String)setMap.get("set_id");
	    						String columname="t_"+set_id+"_"+chgstate;
	    						String sub_dataXml=Sql_switcher.readMemo(rset,columname);	
	    						
	    						setMap.put("value",transSubData(sub_dataXml,set_id,fileTypeMap));
		    					setMap.put("value_view",""); 
		    					setMap.put("file_type",defaultFile_type); 
		    					setMap.put("multimedia_maxsize",multimedia_maxsize); 
		    					setMap.put("fileTypeMap",fileTypeMap); 
		    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
		    					if("0".equals(isEdit)&&"2".equals(priv))
		    						setMap.put("priv", "1"); 
	    					}
	    					else if("P".equals(item_type)) //照片
	    					{  
	    						String filephoto="";
	    						String fileid = rset.getString("fileid");
	    						if(StringUtils.isEmpty(fileid)) {
	    							filephoto=ServletUtilities.createOleFile("photo","ext",rset); 
	    						}
	                        	if(!(filephoto==null|| "".equals(filephoto)))//安全平台改造，将filename加密
	                        		value="servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(filephoto));
	                        	else if(StringUtils.isNotEmpty(fileid)){
	                        		value = "servlet/vfsservlet?fileid="+fileid+"";
	                        	}else {
	                        		value="blank";//没有照片时
	                        	}
	                        	HashMap valueMap=new HashMap();
	                        	String rwPriv="2";
								/*if("0".equals(ins_id))//bug 43902  被驳回后  就不可以修改照片了
	    							rwPriv="2";*/
	                        	valueMap.put("file_name", "photo"+rset.getString("ext"));
	                        	valueMap.put("path",value);
	                        	setMap.put("value",valueMap);
	                        	setMap.put("priv",rwPriv);
		    					setMap.put("value_view",""); 
		    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
		    					if("0".equals(isEdit)&&"2".equals(priv))
		    						setMap.put("priv", "1"); 
	    					}
	    					else if("F".equals(item_type)) //附件
	    					{
	    						attachmentBo.initParam(false);
	    						
	    						String basepre="";
	    						String _object_id="";
	    						if(templateParam.getInfor_type()==1)
	    						{
	    							basepre=object_id.substring(0,3);
	    							_object_id=object_id.substring(3);
	    						} 
	    						String attachmenttype="0"; //公共附件
	    						if("file_private".equals(item_id))
	    							attachmenttype="1"; 
	    						String rwPriv="2";
	    						if("0".equals(isEdit))
	    							rwPriv="1";
	    						String gridno=(String)setMap.get("gridno");
	    						String pageid=(String)setMap.get("pageid");
	    						String pageid_gridno=pageid+"_"+gridno;
	    						ArrayList list=attachmentBo.getAttachmentList(ins_id,_object_id,basepre,this.userView.getUserName(),attachmenttype,rwPriv,pageid_gridno);
	    						ArrayList fileList=new ArrayList();
	    						LazyDynaBean bean=null;
	    						for(Iterator tt=list.iterator();tt.hasNext();)
	    						{
	    							bean=(LazyDynaBean)tt.next();
	    							String ext=(String)bean.get("ext");
	    							if(ext.charAt(0)!='.')
	    								ext="."+ext;
	    							String filepath=(String)bean.get("filepath");
	    							VfsFileEntity enty = VfsService.getFileEntity(filepath);
	    							/*if(!filepath.startsWith(attachmentBo.getRootDir())) {
	    								filepath = attachmentBo.getRootDir()+filepath;
	    							}*/
	    							//String[] temp=filepath.split("\\\\");
	    							HashMap valueMap=new HashMap(); 
	    							valueMap.put("file_id",(String)bean.get("file_id")); //主键ID
	    							valueMap.put("fileId", filepath);
	    							valueMap.put("name",enty.getName()); //上传的原文件名
	    							valueMap.put("file_name",enty.getName()); //保存到临时文件夹下的临时文件名
	    							valueMap.put("file_type",(String)bean.get("filetype"));
	    							String sortName=(String)bean.get("sortname");
	    							valueMap.put("file_type_name",sortName);
	    							valueMap.put("candelete","1".equalsIgnoreCase((String)bean.get("candelete"))?true:false);//文件是否可以被删除、个人附件不是自己上传的无法删除
	    							valueMap.put("path","/servlet/vfsservlet?fileid="+filepath+"&fromjavafolder=true");
	    							fileList.add(valueMap);
	    						}
	    						String file_type=(String) setMap.get("file_type");
	    						if(StringUtils.isNotBlank(file_type)){
	    							defaultFile_type=(String)fileTypeMap.get(file_type);
	    						}
	    						setMap.put("value",fileList);
		    					setMap.put("value_view",""); 
		    					setMap.put("file_type",defaultFile_type); 
		    					setMap.put("multimedia_maxsize",multimedia_maxsize); 
		    					setMap.put("fileTypeMap",fileTypeMap); 
		    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
		    					if("0".equals(isEdit)&&"2".equals(priv))
		    						setMap.put("priv", "1"); 
	    					}
	    					else
	    					{
		                        { 
		                        	if(("2".equals(hisMode)|| "3".equals(hisMode)|| "4".equals(hisMode))&& "1".equals(chgstate))
		                    		{//如果是多条记录或者是条件记录或者是条件序号&&指标是变化前的&&不是子集 
		                    			setMap.put("item_type", "M");
		                    		}
		                        	value=getTemplateFieldValue(rset,setMap); 
		                        	
		                        	
		                        	
		                        }
		    			        item_type=(String)setMap.get("item_type");
		    					if("A".equals(item_type)&&code_id!=null&&!"0".equals(code_id)&&value!=null&&value.trim().length()>0&&value.split("`").length==2)
		    					{ 
		    						value_view=value.split("`")[1];
		    						value=value.split("`")[0]; 
		    					}
		    					
		    					if("1".equals(chgstate)&&("2".equals(hisMode)|| "3".equals(hisMode)|| "4".equals(hisMode))&& "1".equals(chgstate))  //确保移动端此字段显示行高按字符型显示
		                		{
		    						if(value.indexOf("`")==-1){
		    							setMap.put("item_type", "A");
		    						}else{
		    							value=value.replace("`", "\r\n");//bug 49829  变化前指标取多条记录时，当结果有多个，需要换行显示。
		    						}
		                			setMap.put("code_id", "0");//bug 合同续期模板，年度考核等级无法显示 代码型指标变化第几条由大文本显示改为字符型显示，需要将code_id设置为0
		                		}
		    					
		    					
		    					setMap.put("value",value);
		    					setMap.put("value_view",value_view); 
		    					setMap.put("isOpinionfield","0");//记录此字段是否是审批意见指标：0不是，1是
		    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
		    					if(opinion_field.equalsIgnoreCase(item_id))//bug 31637 修正微信审批意见指标可以编辑
		    					{
		    						//如果是审批意见指标，重新赋值格式化后的数据。
		    						ArrayList optionFiledList = TemplateUtilBo.formatOptionFiledValue(value);
		    						if(optionFiledList.size()>0)
		    							setMap.put("value",optionFiledList);
		    						setMap.put("priv", "1");
		    						setMap.put("isOpinionfield","1");
		    					}
		    					else if("0".equals(isEdit)&&"2".equals(priv))
		    						setMap.put("priv", "1"); 
	    					}
	    				}
	    			}
    			}else{
    				HashMap setMap=null;
    				for(Iterator t=setList.iterator();t.hasNext();)
    				{
    					String value="";
    					String value_view="";
    					setMap=(HashMap)t.next();
    					String item_id=(String)setMap.get("item_id");
    					String item_type=(String)setMap.get("item_type");
    					String chgstate=(String)setMap.get("chgstate");
    					String code_id=(String)setMap.get("code_id");
    					String hisMode=(String)setMap.get("hisMode");
    					String subflag=(String)setMap.get("subflag");
    					String default_value="";
    					if(setMap.containsKey("default_value")){
    						default_value=(String) setMap.get("default_value");
    					}
    			        /*
    					if(item_type.equalsIgnoreCase("M")&&item_id.equalsIgnoreCase("photo"))
                        {//如果指标是备注型
                        	String filephoto="";
                    		filephoto=ServletUtilities.createOleFile("photo","ext",rset); 
                        	if(!(filephoto==null||filephoto.equals("")))//安全平台改造，将filename加密
                        		value="/servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(filephoto));
                        	else
                        		value="blank";//没有照片时
                        }else  
                        */	
    					
    					if("1".equals(subflag)) //子集
    					{
    						String set_id=(String)setMap.get("set_id");
    						String columname="t_"+set_id+"_"+chgstate;
    						String sub_dataXml="";	
    						
    						setMap.put("value",transSubData(sub_dataXml,set_id,fileTypeMap));
	    					setMap.put("value_view",""); 
	    					setMap.put("file_type",defaultFile_type); 
	    					setMap.put("multimedia_maxsize",multimedia_maxsize); 
	    					setMap.put("fileTypeMap",fileTypeMap); 
	    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
	    					if("0".equals(isEdit)&&"2".equals(priv))
	    						setMap.put("priv", "1"); 
    					}
    					else if("P".equals(item_type)) //照片
    					{  
    						String filephoto="";
                        	if(!(filephoto==null|| "".equals(filephoto)))//安全平台改造，将filename加密
                        		value="servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(filephoto));
                        	else
                        		value="blank";//没有照片时
                        	HashMap valueMap=new HashMap();
                        	String rwPriv="2";
							/*if("0".equals(ins_id))//bug 43902  被驳回后  就不可以修改照片了
    							rwPriv="2";*/
                        	valueMap.put("file_name", "photo.png");
                        	valueMap.put("path",value);
                        	setMap.put("value",valueMap);
                        	setMap.put("priv",rwPriv);
	    					setMap.put("value_view",""); 
	    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
	    					if("0".equals(isEdit)&&"2".equals(priv))
	    						setMap.put("priv", "1"); 
    					}
    					else if("F".equals(item_type)) //附件
    					{
    						attachmentBo.initParam(false);
    						
    						String basepre="";
    						String _object_id="";
    						if(templateParam.getInfor_type()==1)
    						{
    							basepre=object_id.substring(0,3);
    							_object_id=object_id.substring(3);
    						} 
    						String attachmenttype="0"; //公共附件
    						if("file_private".equals(item_id))
    							attachmenttype="1"; 
    						String rwPriv="1";
    						if("0".equals(ins_id))
    							rwPriv="2";
    						String gridno=(String)setMap.get("gridno");
    						String pageid=(String)setMap.get("pageid");
    						String pageid_gridno=pageid+"_"+gridno;
    						ArrayList list=attachmentBo.getAttachmentList(ins_id,_object_id,basepre,this.userView.getUserName(),attachmenttype,rwPriv,pageid_gridno);
    						ArrayList fileList=new ArrayList();
    						LazyDynaBean bean=null;
    						for(Iterator tt=list.iterator();tt.hasNext();)
    						{
    							bean=(LazyDynaBean)tt.next();
    							String ext=(String)bean.get("ext");
    							if(ext.charAt(0)!='.')
    								ext="."+ext;
    							String filepath=(String)bean.get("filepath");
    							if(!filepath.startsWith(attachmentBo.getRootDir())) {
    								filepath = attachmentBo.getRootDir()+filepath;
    							}
    							String[] temp=filepath.split("\\\\");
    							HashMap valueMap=new HashMap(); 
    							valueMap.put("file_id",(String)bean.get("file_id")); //主键ID
    							valueMap.put("name",(String)bean.get("attachmentname")+ext); //上传的原文件名
    							valueMap.put("file_name",temp[temp.length-1]); //保存到临时文件夹下的临时文件名
    							valueMap.put("file_type",(String)bean.get("filetype"));
    							String sortName=(String)bean.get("sortname");
    							valueMap.put("file_type_name",sortName);
    							valueMap.put("candelete","1".equalsIgnoreCase((String) bean.get("candelete"))?true:false);//标识单个附件是否有删除权限
    							valueMap.put("path","/servlet/vfsservlet?fileid="+PubFunc.encrypt(filepath)+"&fromjavafolder=true");
    							fileList.add(valueMap);
    						}
    						String file_type=(String) setMap.get("file_type");
    						if(StringUtils.isNotBlank(file_type)){
    							defaultFile_type=(String)fileTypeMap.get(file_type);
    						}
    						setMap.put("value",fileList);
	    					setMap.put("value_view",""); 
	    					setMap.put("file_type",defaultFile_type); 
	    					setMap.put("multimedia_maxsize",multimedia_maxsize); 
	    					setMap.put("fileTypeMap",fileTypeMap); 
	    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
	    					if("0".equals(isEdit)&&"2".equals(priv))
	    						setMap.put("priv", "1"); 
    					}
    					else
    					{
	                        { 
	                        	if(("2".equals(hisMode)|| "3".equals(hisMode)|| "4".equals(hisMode))&& "1".equals(chgstate))
	                    		{//如果是多条记录或者是条件记录或者是条件序号&&指标是变化前的&&不是子集 
	                    			setMap.put("item_type", "M");
	                    		}
	                        	value=getTemplateFieldValue(default_value,setMap); 
	                        }
	    			        item_type=(String)setMap.get("item_type");
	    					if("A".equals(item_type)&&code_id!=null&&!"0".equals(code_id)&&value!=null&&value.trim().length()>0&&value.split("`").length==2)
	    					{ 
	    						value_view=value.split("`")[1];
	    						value=value.split("`")[0]; 
	    					}
	    					if("1".equals(chgstate)&&("2".equals(hisMode)|| "3".equals(hisMode)|| "4".equals(hisMode))&& "1".equals(chgstate))  //确保移动端此字段显示行高按字符型显示
	                		{
	                			setMap.put("item_type", "A");
	                			setMap.put("code_id", "0");//bug 合同续期模板，年度考核等级无法显示 代码型指标变化第几条由大文本显示改为字符型显示，需要将code_id设置为0
	                		}
	    					
	    					
	    					setMap.put("value",value);
	    					setMap.put("value_view",value_view); 
	    					setMap.put("isOpinionfield","0");//记录此字段是否是审批意见指标：0不是，1是
	    					String priv=(String)setMap.get("priv"); // 0：无读写权限 1：读权限  2：写权限 
	    					if(opinion_field.equalsIgnoreCase(item_id))//bug 31637 修正微信审批意见指标可以编辑
	    					{
	    						//如果是审批意见指标，重新赋值格式化后的数据。
	    						ArrayList optionFiledList = TemplateUtilBo.formatOptionFiledValue(value);
	    						if(optionFiledList.size()>0)
	    							setMap.put("value",optionFiledList);
	    						setMap.put("priv", "1");
	    						setMap.put("isOpinionfield","1");
	    					}
	    					else if("0".equals(isEdit)&&"2".equals(priv))
	    						setMap.put("priv", "1"); 
    					}
    				}
    			}
    			
    			
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	finally
    	{
    		PubFunc.closeDbObj(rset);
    	}
    	return setList;
    }
    
    private ArrayList transSubData(String Sub_dataXml,String setid,HashMap fileTypeMap)
    {
    	ArrayList dataList=new ArrayList();
    	try
    	{
    		String multimedia_file_flag = "0";
    		if (!"".equals(Sub_dataXml) && Sub_dataXml!=null){ 
    			ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
                String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
                rootDir=rootDir.replace("\\",File.separator);
                if (!rootDir.endsWith(File.separator)) rootDir =rootDir+File.separator;
                rootDir += "multimedia"+File.separator;
				Sub_dataXml = PubFunc.keyWord_reback(Sub_dataXml);
				Sub_dataXml=Sub_dataXml.replace("&", "＆");
				Document doc=PubFunc.generateDom(Sub_dataXml);
				Element eleRoot=null;  //xml解析得到/records对象
				Element element=null;  //xml解析得到/record对象
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
				eleRoot =(Element) findPath.selectSingleNode(doc);
				if(eleRoot!=null){
					String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
					if(columns.toLowerCase().indexOf("attach")!=-1){
						if(setid.indexOf("_")!=-1){
							setid=setid.substring(0,setid.indexOf("_"));
						}
						multimedia_file_flag = this.checkAttach(setid);
					}
					List recordList=eleRoot.getChildren("record");
					if(recordList!=null&&recordList.size()>0)
					{
						for(int i=0;i<recordList.size();i++)
						{
							//存储record中的属性和值，先将contentValue的值存储为i，得到最终的值后再进行替换
							HashMap recordMap=new HashMap(); 
							element=(Element)recordList.get(i);
							String I9999=element.getAttributeValue("I9999");
							recordMap.put("I9999", I9999);
							String state=element.getAttributeValue("state");
							if("D".equalsIgnoreCase(state)){
								continue;
							}
							recordMap.put("state", state);
							String edit = element.getAttributeValue("edit")==null?"1":element.getAttributeValue("edit"); //历史记录是否可编辑
							recordMap.put("edit", edit);
							
							String timestamp=element.getAttributeValue("timestamp")!=null?element.getAttributeValue("timestamp"):""; //时间戳
							if(timestamp.length()>0)
								recordMap.put("timestamp", timestamp);
							
							String contentValue=element.getValue();
							//存储column和其对应的值，lineNum为当前record的条数
							 
							if(contentValue!=null&&contentValue.length()>0)
							{
								String[] valueArr=contentValue.split("`",-1);
								String[] columnArr=columns.split("`");
								for(int j=0;j<columnArr.length;j++){
									String value = "";
									if("0".equals(multimedia_file_flag)&&columnArr[j].toLowerCase().indexOf("attach")!=-1)//不支持附件
										continue;
									else if("1".equals(multimedia_file_flag)&&columnArr[j].toLowerCase().indexOf("attach")!=-1){
										if(j<valueArr.length) {
											value= valueArr[j];
											if("".equals(value.trim())){
	                                        	value = "";
	                                        }
										}
										if(StringUtils.isNotBlank(value)){
											String [] valuearr = value.split(",");
											ArrayList attachList=new ArrayList();
											
											
											/*
											<?xml version="1.0" encoding="GBK"?>
											<records columns="A1905`A1910`A1915`A1920`attach">
											  <record I9999="-1" state="">2017.02.01`2017.02.24`aaaaa`sdd`
											  
											  
											  98c7661e-e9dc-4512-8c7d-2bd0c472274d.txt|E:\test\multimedia\subdomain\template_1\T172\T447|长链接.txt|0.22KB|98c7661e-e9dc-4512-8c7d-2bd0c472274d|0|type:F,
											  6d162b93-5747-4d47-9dcc-f7fac4b1a7e6.doc|E:\test\multimedia\subdomain\template_1\T348\T424|数据库设计说明—绩效管理v1.doc|1.36MB|6d162b93-5747-4d47-9dcc-f7fac4b1a7e6|1|type:F
											  
											  </record>
											</records>
											 ------------------------------------------------
											
											,"value":[{
												      “i9999”:”-1”,
												      “state”:””,
												       “A19X1”:”XXXXX”,
												       “A19X2”:”XXXXX”,
												       ……
												       “attach”:[{  //附件
												“name”:”xxx.doc” //上传的原文件名
												,”file_name”:”XXXXX.doc” //保存在服务器端的文件名
												,“path”:”/servlet/DisplayOleContent?openflag=true&&filePath=xxx”  
												,”file_type”:”” //文件类型
												,”file_type_name”:””//文件类型描述
												},{…}]
												},{…}]   
											
                                           */
											
											
											for(int m=0;m<valuearr.length;m++){
											    HashMap valueMap=new HashMap();
											    String file_name ="";
												String []attacharr = valuearr[m].split("\\|"); 
												for(int k=0;k<attacharr.length;k++){
													if(k==0){
														file_name = attacharr[0]; 
														if(file_name.indexOf(".")==-1){
															file_name=PubFunc.decrypt(SafeCode.decrypt(file_name));
														}
														valueMap.put("file_name", "");
														
													}else if(k==1){
														String filepath = attacharr[1]; 
														String path="/servlet/vfsservlet?fileid="+filepath+"&fromjavafolder=true";
														/*if(StringUtils.isNotBlank(filepath)){
															if(!filepath.startsWith(rootDir)) {
																filepath = rootDir+filepath;
															}
															path=ServletUtilities.createSubAttachFile(filepath+File.separator+file_name,file_name.substring(file_name.lastIndexOf(".")), conn);
															if(StringUtils.isNotBlank(path)){
																path="servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(path))+"&bencrypt=true";
															}
														}*/
														valueMap.put("path", path); 
													}
													else if(k==2){
														String filepath = attacharr[1]; 
														valueMap.put("name",attacharr[k]); 
													}
													else if(k==6)
													{
														String type=attacharr[k].split(":")[1];
														valueMap.put("file_type",type);
														valueMap.put("file_type_name",fileTypeMap.get(type)!=null?(String)fileTypeMap.get(type):"");
													}
												} 
												attachList.add(valueMap);
											}
											
											if(attachList.size()>0) 
												recordMap.put(columnArr[j], attachList); 
											else
												recordMap.put(columnArr[j], "");
											 
										}else
											recordMap.put(columnArr[j], "");
										 
										continue;
									}
									if(valueArr.length>j){
										value= valueArr[j];
                                        if("".equals(value.trim())){
                                        	value = "";
                                        }
                                        FieldItem fieldItem = DataDictionary.getFieldItem(columnArr[j]);
                                        if(fieldItem!=null&&"A".equalsIgnoreCase(fieldItem.getItemtype())&&!"0".equalsIgnoreCase(fieldItem.getCodesetid())&&!"".equalsIgnoreCase(fieldItem.getCodesetid())){
                                        	value+="||"+AdminCode.getCodeName(fieldItem.getCodesetid(),value);
                                        }
                                        recordMap.put(columnArr[j], value);
									}else{
										recordMap.put(columnArr[j], value);
									}
								}
							} 
							dataList.add(recordMap);
						}
						 
					} 
				}

			}
			 
    		
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	
    	
    	
    	return dataList;
    }
    
    
    /**
	 * 检查子集是否支持附件
	 * @param temp
	 */
	private String checkAttach(String setid) {
		ContentDAO dao=new ContentDAO(this.conn);
		String multimedia_file_flag = "0";
		try {
			RecordVo recordVo = new RecordVo("fieldset");
			recordVo.setString("fieldsetid", setid);
			recordVo = dao.findByPrimaryKey(recordVo);
			multimedia_file_flag = recordVo.getString("multimedia_file_flag");
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return multimedia_file_flag;
	}
    
	/**
	 * 从模板表中取数
	 * @param rset
	 * @return
	 */
	private String getTemplateFieldValue(RowSet rset,HashMap setMap)
	{
		String item_id=(String)setMap.get("item_id");
		String field_type=(String)setMap.get("item_type");
		String chgState=(String)setMap.get("chgstate");
		String formula=(String)setMap.get("formula");
		String code_id=(String)setMap.get("code_id");
		String format=(String)setMap.get("format");
		String value="";
		String field_name=null;	 
		boolean flag = false;
		try
		{	 
			
			if(formula!=null&&formula.indexOf("<EXPR>")!=-1)
			{ 
				int f=formula.indexOf("<EXPR>");
				int t=formula.indexOf("</FACTOR>"); 
				String _temp=formula.substring(0,f);
				String _temp2=formula.substring(t+9);
				formula=_temp+_temp2; 
			}  
			if("0".equals(chgState))
					field_name=item_id;
			else//变化前或变化后指标
			{
					field_name=item_id+"_"+chgState; 
			}
			
			if("M".equalsIgnoreCase(field_type))
			{
					//判断数据字典里的指标类型
					FieldItem item=DataDictionary.getFieldItem(item_id);
					if(item!=null&&item.getItemtype()!=null){
						if("M".equalsIgnoreCase(item.getItemtype())){
							value=Sql_switcher.readMemo(rset,field_name);
							value =value.replaceAll("\n", "\\\n");//前端json串不支持\n，否则无法解析  //xuj update 2015-12-28
							value =value.replaceAll("\r", " ");
						}	
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{ 
							String str = Sql_switcher.readMemo(rset,field_name); 
							value=str;
						}
						else if("N".equalsIgnoreCase(item.getItemtype()))
						{
							int ndec=Integer.parseInt((String)setMap.get("decimal_width"));//小数点位数 
							String str = Sql_switcher.readMemo(rset,field_name);
							String values ="";
							if(str.indexOf("`")!=-1){
								String strs[] =str.split("`");
								for(int i=0;i<strs.length;i++){
									if(strs[i].trim().length()>0){
										values += PubFunc.DoFormatDecimal(strs[i],ndec);
										if(i<strs.length-1){
											values+="`";
										}
									}
								}
							}else{
								values =PubFunc.DoFormatDecimal(str,ndec);
							}
							value=values;
							
						}else{ 
								String str = Sql_switcher.readMemo(rset,field_name);
								String values ="";
								if(str.indexOf("`")!=-1){
									String strs[] =str.split("`");
									for(int i=0;i<strs.length;i++){
										if(strs[i].trim().length()>0){
											if(code_id!=null&&!"0".equals(code_id)){
												values += AdminCode.getCodeName(code_id,strs[i]);
											}else
												values += strs[i];
											if(i<strs.length-1){
												values+="`";
											}
										}
									}
								}else{
									if(code_id!=null&&!"0".equals(code_id)){
										values = AdminCode.getCodeName(code_id,str);
									}else
										values = str;	
								}
								value=values;
 
						}
					}
					
				}
				else if("D".equalsIgnoreCase(field_type))
				{ 
					format=format.replace("hh", "HH");
				//	Date date=rset.getTimestamp(field_name);
					String datevalue=PubFunc.FormatDate(rset.getTimestamp(field_name),format);
					value=datevalue;
				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=Integer.parseInt((String)setMap.get("decimal_width"));//小数点位数  
					value=PubFunc.DoFormatDecimal(rset.getString(field_name),ndec);
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					codevalue=((codevalue==null)?"":codevalue.trim());
					if(codevalue.length()>0)
					{
						if(code_id==null|| "0".equals(code_id.trim())||code_id.trim().length()==0){
							if(field_name!=null&&field_name.startsWith("codesetid_")){
								if("UM".equalsIgnoreCase(codevalue))
									codevalue="部门";
								else if("UN".equalsIgnoreCase(codevalue))
									codevalue="单位";
							}
								
							value=codevalue;
						}
						else
						{
							if("UM".equalsIgnoreCase(code_id)&&AdminCode.getCodeName(code_id,codevalue).trim().length()==0)
							{
								value=codevalue+"`"+AdminCode.getCodeName("UN",codevalue);
							}
							else
							{
								if("UM".equalsIgnoreCase(code_id))
								{
									CodeItem item=AdminCode.getCode("UM",codevalue);
									if(item!=null)
					    	    	{
										value=codevalue+"`"+item.getCodename();
					        		}
					    	    	else
					    	    	{
					    	    		value=codevalue+"`"+AdminCode.getCodeName(code_id,codevalue);
					    	    	}
								}
								else
									value=codevalue+"`"+AdminCode.getCodeName(code_id,codevalue);
							}
						}
					}
				} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		} 
		return value;
	}
	//格式化默认值。根据每个字段设置的格式，把默认值进行格式化。
	private String getTemplateFieldValue(String defaultValue,HashMap setMap)
	{
		String item_id=(String)setMap.get("item_id");
		String field_type=(String)setMap.get("item_type");
		String chgState=(String)setMap.get("chgstate");
		String formula=(String)setMap.get("formula");
		String code_id=(String)setMap.get("code_id");
		String format=(String)setMap.get("format");
		String value="";
		String field_name=null;	 
		boolean flag = false;
		try
		{	 
			
			if(formula!=null&&formula.indexOf("<EXPR>")!=-1)
			{ 
				int f=formula.indexOf("<EXPR>");
				int t=formula.indexOf("</FACTOR>"); 
				String _temp=formula.substring(0,f);
				String _temp2=formula.substring(t+9);
				formula=_temp+_temp2; 
			}  
			if("0".equals(chgState))
					field_name=item_id;
			else//变化前或变化后指标
			{
					field_name=item_id+"_"+chgState; 
			}
			
			if("M".equalsIgnoreCase(field_type))
			{
					//判断数据字典里的指标类型
					FieldItem item=DataDictionary.getFieldItem(item_id);
					if(item!=null&&item.getItemtype()!=null){
						if("M".equalsIgnoreCase(item.getItemtype())){
							value=defaultValue;
							value =value.replaceAll("\n", "\\\n");//前端json串不支持\n，否则无法解析  //xuj update 2015-12-28
							value =value.replaceAll("\r", " ");
						}	
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{ 
							String str =defaultValue; 
							value=str;
						}
						else if("N".equalsIgnoreCase(item.getItemtype()))
						{
							int ndec=Integer.parseInt((String)setMap.get("decimal_width"));//小数点位数 
							String str = defaultValue;
							String values ="";
							if(str.indexOf("`")!=-1){
								String strs[] =str.split("`");
								for(int i=0;i<strs.length;i++){
									if(strs[i].trim().length()>0){
										values += PubFunc.DoFormatDecimal(strs[i],ndec);
										if(i<strs.length-1){
											values+="`";
										}
									}
								}
							}else{
								values =PubFunc.DoFormatDecimal(str,ndec);
							}
							value=values;
							
						}else{ 
								String str = defaultValue;
								String values ="";
								if(str.indexOf("`")!=-1){
									String strs[] =str.split("`");
									for(int i=0;i<strs.length;i++){
										if(strs[i].trim().length()>0){
											if(code_id!=null&&!"0".equals(code_id)){
												values += AdminCode.getCodeName(code_id,strs[i]);
											}else
												values += strs[i];
											if(i<strs.length-1){
												values+="`";
											}
										}
									}
								}else{
									if(code_id!=null&&!"0".equals(code_id)){
										values = AdminCode.getCodeName(code_id,str);
									}else
										values = str;	
								}
								value=values;
 
						}
					}
					
				}
				else if("D".equalsIgnoreCase(field_type))
				{ 
					format=format.replace("hh", "HH");
				//	Date date=rset.getTimestamp(field_name);
					 DateFormat df = DateFormat.getDateInstance();
					 String datevalue=defaultValue;
					 if(StringUtils.isNotBlank(datevalue)){//当默认值是空值时不需要格式化日期
						 try{
							 Date date=null;
							 if("SYSTIME".equalsIgnoreCase(defaultValue)){
								 date=new Date();
							 }else{
								 date=df.parse(defaultValue);
							 }
							datevalue=PubFunc.FormatDate(date,format);
						 }catch(Exception ex){
							 ex.printStackTrace();
						 }
					 }
					value=datevalue;
				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=Integer.parseInt((String)setMap.get("decimal_width"));//小数点位数  
					value=PubFunc.DoFormatDecimal(defaultValue,ndec);
				}
				else //'A'
				{
					String codevalue=defaultValue;
					codevalue=((codevalue==null)?"":codevalue.trim());
					if(codevalue.length()>0)
					{
						if(code_id==null|| "0".equals(code_id.trim())||code_id.trim().length()==0){
							if(field_name!=null&&field_name.startsWith("codesetid_")){
								if("UM".equalsIgnoreCase(codevalue))
									codevalue="部门";
								else if("UN".equalsIgnoreCase(codevalue))
									codevalue="单位";
							}
								
							value=codevalue;
						}
						else
						{
							if("UM".equalsIgnoreCase(code_id)&&AdminCode.getCodeName(code_id,codevalue).trim().length()==0)
							{
								value=codevalue+"`"+AdminCode.getCodeName("UN",codevalue);
							}
							else
							{
								if("UM".equalsIgnoreCase(code_id))
								{
									CodeItem item=AdminCode.getCode("UM",codevalue);
									if(item!=null)
					    	    	{
										value=codevalue+"`"+item.getCodename();
					        		}
					    	    	else
					    	    	{
					    	    		value=codevalue+"`"+AdminCode.getCodeName(code_id,codevalue);
					    	    	}
								}
								else
									value=codevalue+"`"+AdminCode.getCodeName(code_id,codevalue);
							}
						}
					}
				} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		} 
		return value;
	}
 
	
    
	/**
	 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
	 * @param task_id 任务号
	 * @return
	 */
	private HashMap getFieldPrivFillable(String task_id,Connection conn,String tabid)
	{
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="";
			if(!"0".equalsIgnoreCase(task_id)){
				sql="select ext_param from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
			}else{//如果是起始节点，通过tabid和nodeType查找节点
				sql="select ext_param from t_wf_node where tabid='"+tabid+"' and nodeType=1";
			}
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param);
					String xpath="/params/field_priv/field";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist=findPath.selectNodes(doc);	
					if(childlist.size()==0){
						xpath="/params/field_priv/field";
						 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						 childlist=findPath.selectNodes(doc);
					}
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							String editable="";
							//0|1|2(无|读|写)
							if(element!=null&&element.getAttributeValue("editable")!=null)
								editable=element.getAttributeValue("editable");
							if(editable!=null&&editable.trim().length()>0)
							{
								String columnname=element.getAttributeValue("name").toLowerCase();
								if(columnname.endsWith("_2")){ 
									String fillable = element.getAttributeValue("fillable");
									if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable))
										editable="3"; 
								}
								_map.put(columnname, editable);
							}
							
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
    
    /**
     * 获得模板定义的参数
     * @param tab_vo
     */
    private String  getTabParam(RecordVo tab_vo)
    { 
	    try
	    {
	    	String sxml=tab_vo.getString("ctrl_para");
	    	Document doc=PubFunc.generateDom(sxml); 
	    	String xpath="/params/updates";
	    	XPath	findPath = XPath.newInstance(xpath);// 取得符合条件的节点
	    	List	childlist=findPath.selectNodes(doc);	
			String DefaultTransIn="0";
			Element element=null;
			if(childlist!=null&&childlist.size()>0)
			{ 
				element=(Element)childlist.get(0);
				if(element.getAttribute("UnrestrictedMenuPriv_Input")!=null)
					this.UnrestrictedMenuPriv_Input=(String)element.getAttributeValue("UnrestrictedMenuPriv_Input");  ////数据录入不判断子集和指标权限, 0判断(默认值),1不判断
			}
			
			/**审批方法*/
			xpath="/params/sp_flag";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.sp_mode=(String)element.getAttributeValue("mode");
				if(element.getAttributeValue("no_sp_yj")!=null)
				{
					String _no_sp_yj=(String)element.getAttributeValue("no_sp_yj"); //no_sp_yj:审批不填写意见  1:选中   0:空表示没选中（默认）
					if("1".equals(_no_sp_yj))
						this.no_sp_yj="true";
				}
			}
	    }
	    catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return UnrestrictedMenuPriv_Input;
    }
    
    
    /**
     * 获得模板下对象信息
     * @param paramStr  
     * @return  "Usr00000001" //模板数据ID，人员：库前缀+A0100     单位|岗位：B0100|E01A1
     */
    private String getObjs(String  paramStr,RecordVo tab_vo)
    {
    	StringBuffer obj_str=new StringBuffer("");
    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
    	String tabid=(String)paramMap.get("tabid");
    	String ins_id=(String)paramMap.get("ins_id");   //实例ID
    	String taskid=(String)paramMap.get("taskid");  // 待办任务号（发起任务|通知单 为空值）
    	String fromMessage=(String)paramMap.get("fromMessage");   //1：来自通知单待办  0：不是
    	String object_id=(String)paramMap.get("object_id");  //模板数据ID，人员：库前缀+A0100 ,  单位|岗位：B0100|E01A1
    	
    	if((ins_id.trim().length()==0|| "0".equals(ins_id))&&(taskid.trim().length()==0|| "0".equals(taskid))) //发起单据
    	{
    		if("0".equals(fromMessage))
    		{ 
    			return this.userView.getDbname()+this.userView.getA0100();
    		}
    	} 
    	String sql=getObjSql(paramStr,tab_vo,1,"","");
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rset=null;
    	try
    	{ 
    			String staticKey = "static";
    			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
    				staticKey = "static_o";
    			}
    			int _static=tab_vo.getInt(staticKey);
    			rset=dao.search(sql);
    			while(rset.next())
    			{
    				if(_static==10) //单位
    				{ 
    						obj_str.append(","+rset.getString("b0110")); 
    				}
    				else if(_static==11) //职位
    				{ 
    						obj_str.append(","+rset.getString("e01a1"));
    				}
    				else
    				{
    					obj_str.append(","+rset.getString("basepre")+rset.getString("a0100"));
    				} 
    			} 
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	finally
    	{
    		PubFunc.closeDbObj(rset);
    	}
    	return obj_str.length()>0?obj_str.substring(1):obj_str.toString();//如果查不到人obj_str为空串直接截取会报错。
    }
    
    
    /**
     * 获得模板下数据SQL
     * @param paramStr
     * @param tab_vo 模板对象
     * @flag  1:nbase+a0100 | b0110 | e01a1 , 2: *
     * @return
     */
    private String getObjSql(String paramStr,RecordVo tab_vo,int flag,String objectid,String ext_select)
    {
    	StringBuffer str=new StringBuffer("");
    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
    	String tabid=(String)paramMap.get("tabid");
    	String ins_id=(String)paramMap.get("ins_id");   //实例ID
    	String taskid=(String)paramMap.get("taskid");  // 待办任务号（发起任务|通知单 为空值）
    	String fromMessage=(String)paramMap.get("fromMessage");   //1：来自通知单待办  0：不是 
    	String tab_flag=(String)paramMap.get("tab_flag");//=1 来自于我的申请
    	StringBuffer sql=new StringBuffer("");
    	String select_str="*";
    	String where="";
		String statickey = "static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			statickey="static_o";
		}
		int _static=tab_vo.getInt(statickey);
    	if(flag==1)
    	{ 
    		if(_static==10) //单位
    			select_str="b0110";
    		else if(_static==11) //职位
    			select_str="e01a1";	
    		else
    			select_str="basepre,a0100";
    	}
    	if(ext_select!=null&&ext_select.trim().length()>0)
    		select_str+=ext_select;
    	if(objectid!=null&&objectid.length()>0)
    	{
    		
    		where=" and lower(basepre)='"+objectid.substring(0,3).toLowerCase()+"'  and a0100='"+objectid.substring(3)+"' ";
    		if(_static==10) //单位
    			where =" and  b0110='"+objectid+"'";
    		else if(_static==11) //职位
    			where =" and e01a1='"+objectid+"'";
    	}
    	
     	if((ins_id.trim().length()==0|| "0".equals(ins_id))&&(taskid.trim().length()==0|| "0".equals(taskid))) //发起单据
    	{
    		if("0".equals(fromMessage))
    		{
    			if(objectid.length()>0)
    			{
    				sql.append("select  "+select_str+"  from g_templet_"+tabid+" where 1=1 ");
    			}
    		}
    		else
    		{
    			sql.append("select  "+select_str+"  from  "+this.userView.getUserName()+"templet_"+tabid+" where 1=1");
    		} 
    	}
    	else
    	{ 
			sql.append("select   "+select_str+"   from  templet_"+tabid); 
			sql.append(" where exists (select null from t_wf_task_objlink where  templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "); 
			if(!"1".equals(tab_flag)) {
				sql.append(" and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");	
			}
			sql.append(" and t_wf_task_objlink.tab_id="+tabid+" and t_wf_task_objlink.task_id="+taskid+"    and ( "+Sql_switcher.isnull("t_wf_task_objlink.state","0")+"<>3 )  )");
			
    	} 
     	sql.append(where);
     	return  sql.toString();
     	
    }
    
    
    
    
    /**
     * 获得模板下单层代码列表
     * @param tabid 模板ID
     * @return
     */
    private ArrayList  getCodeList(String tabid,String task_id,String info_type)
    {
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rset=null;
    	ArrayList dataList=new ArrayList();
    	try
    	{ 
    		String layersCodeSet=""; //多层级代码
    		StringBuffer strsql=new StringBuffer();
    		if("mobile".equals(info_type)) {
    			String str=getSubSetCodeField(tabid, conn,info_type);
    			strsql.append("select distinct  codesetid  from codeitem where (codesetid in ( ");
    			strsql.append("  select distinct codeId  from Template_set where tabid=?  ");
    			strsql.append("  and pageid in (select  pageid from Template_Page where isMobile=1 and tabid=?   ) ");
    			strsql.append(" and field_name is not null "
    					+" and nullif(field_name,'') is not null "
    					+"  and field_type is not null and subflag=0 and codeid<>'0' ) ");
    			if(StringUtils.isNotEmpty(str)) {
    				strsql.append(" or codesetid in ("+str+") ) ");
    			}else{
    				strsql.append(" ) ");
    			}
    			strsql.append("  and codeitemid<>parentid  group by codesetid ");
    		}else {
    			String str=getSubSetCodeField(tabid, conn,info_type);
    			strsql.append("select codesetid from codeitem where codeitemid<>parentid and "+(StringUtils.isNotEmpty(str)?"(":"")+" codesetid in (" + 
    					" (select distinct codeId  from Template_set where tabid=?   "
    					+ "and field_name is not null  and nullif(field_name,'') is not null   "
    					+ "and field_type is not null and subflag=0 and codeid<>'0' )) ");
    			
    			if(StringUtils.isNotEmpty(str)) {
    				strsql.append(" or codesetid in ("+str+") ) ");
    			}
    			strsql.append("  group by codesetid ");
    		}
    		
    		if("mobile".equals(info_type))
    			rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid) ,Integer.valueOf(tabid)}));
    		else
    			rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
    		while(rset.next())
    		{
    			if("mobile".equals(info_type)) {
    				layersCodeSet+=",'"+rset.getString("codesetid")+"'";
    				HashMap _map=new HashMap();
    				_map.put("codesetid", rset.getString("codesetid"));
    				_map.put("isSingleLevel", false);//是否有多级
    				dataList.add(_map);
    			}else {

					layersCodeSet+=",'"+rset.getString("codesetid")+"'";
    				HashMap _map=new HashMap();
    				_map.put("codesetid", rset.getString("codesetid"));
    				_map.put("isSingleLevel", false);//是否有多级
    				dataList.add(_map);
				
    			}
    		}
    		strsql.setLength(0);
    		strsql.append("select  pageid from Template_Page where isMobile=1 and tabid=?");
    		rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
    		while(rset.next())
    		{
    			String pageid=rset.getString("pageid");
    			 MoblieTemplatePageBo pageBo = new MoblieTemplatePageBo(this.conn, Integer.valueOf(tabid),Integer.valueOf(pageid),task_id,this.userView);
                 ArrayList celllist = (ArrayList)pageBo.getAllCell(Integer.valueOf(tabid),Integer.valueOf(pageid),task_id).get(0);
                 for(int i=0;i<celllist.size();i++)
                 {
                	 MobileTemplateSetBo setbo =(MobileTemplateSetBo)celllist.get(i);
                	 if(setbo!=null){
	                	 if("UM".equalsIgnoreCase(setbo.getCodeid())||"UN".equalsIgnoreCase(setbo.getCodeid())||"@K".equalsIgnoreCase(setbo.getCodeid()))//bug 35207 setbo.getCodeid()为空，导致报错。
	                	 {
	                		 if(!setbo.isbLimitManagePriv())//不按管理范围控制
	                		 {
	                			 layersCodeSet+=",'"+setbo.getCodeid()+"'";
	                   			 HashMap _map=new HashMap();
	                 			 _map.put("codesetid", setbo.getCodeid());
	                   			 _map.put("isSingleLevel", false);//是否有多级
	                   			 _map.put("ctrltype", "0");
	                   			 _map.put("gridno", setbo.getGridno()+"");
	                   			 _map.put("pageid", setbo.getPageid()+"");
	                   			 dataList.add(_map);
	                		 }
	                		 else if(setbo.isbLimitManagePriv())//按管理范围控制
	                		 {
	                			 layersCodeSet+=",'"+setbo.getCodeid()+"'";
		                   		 HashMap _map=new HashMap();
		             			 _map.put("codesetid", setbo.getCodeid());
		                   		 _map.put("isSingleLevel", false);//是否有多级
		                   		 _map.put("ctrltype", "1");
		                   		 _map.put("gridno", setbo.getGridno()+"");
		                   		 _map.put("pageid", setbo.getPageid()+"");
		                   		 dataList.add(_map);
	                		 }
	                	 }
                	 }
                 }
    		}
    		strsql.setLength(0);
    		if("mobile".equals(info_type)){
    			String str=getSubSetCodeField(tabid, conn,info_type);
    			strsql.append(" select   *  from codeitem where (codesetid in ( ");
        		strsql.append(" select distinct codeId  from Template_set where tabid=?  and pageid in (select  pageid from Template_Page where isMobile=1 and tabid=?  )  ");
        		strsql.append(" and field_name is not null"
        				+" and nullif(field_name,'') is not null "
        				+"  and field_type is not null and subflag=0 and codeid<>'0' ) ");
        		if(str.length()>0){
        			strsql.append(" or codesetid in(").append(str).append(")) ");
        		}else{
        			strsql.append(" ) ");
        		}
        		strsql.append(" and "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))+" between start_date and end_date ");
        		if(layersCodeSet.length()>0)
        			strsql.append(" and codesetid not in ("+layersCodeSet.substring(1)+")");
        		strsql.append(" order by codesetid,a0000 ");//liuyz bug25736 排序顺序不对
    			rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid) ,Integer.valueOf(tabid)}));
    			ArrayList tmpList=new ArrayList();
    			HashMap tmpMap=new HashMap();
    			String codeset="";
    			while(rset.next())
    			{
    				String codesetid=rset.getString("codesetid");
    				String codeitemid=rset.getString("codeitemid");
    				String codeitemdesc=rset.getString("codeitemdesc");
    				if(codeset.length()==0)
    					codeset=codesetid;
    				tmpMap=new HashMap();
    				tmpMap.put("itemid", codeitemid);
    				tmpMap.put("itemdesc", codeitemdesc);
    				if(!codeset.equals(codesetid))
    				{
    					HashMap _map=new HashMap();
    					_map.put("codesetid", codeset);
    					_map.put("codelist", tmpList);
    					dataList.add(_map);
    					tmpList=new ArrayList();
    					codeset=codesetid;
    				}
    				tmpList.add(tmpMap);  
    			}
    			HashMap _map=new HashMap();
    			_map.put("codesetid", codeset);
    			_map.put("codelist", tmpList);
    			dataList.add(_map);
    		}
    		
    		
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	finally
    	{
    		PubFunc.closeDbObj(rset);
    	}
    	return dataList;
    }
    /***
     * 子集指标代码类
     * */
    private String getSubSetCodeField(String tabid,Connection conn,String info_type) {
    	ContentDAO dao=new ContentDAO(conn);
    	RowSet rs=null;
    	String fieldStr="";
    	try {
    		Document doc = null;
    		Element element = null;
    		String setname="";
    		ArrayList param=new ArrayList();
    		String sql="select sub_domain from Template_set where TabId=? and subflag=1 ";
    		param.add(tabid);
    		if("mobile".equals(info_type)) {
    			sql+=" and pageid in (select  pageid from Template_Page where isMobile=1 and tabid=?   ) ";
    			param.add(tabid);
    		}
			rs=dao.search(sql,param);
			while(rs.next()) {
				String fields="";
				String sub_doMain=Sql_switcher.readMemo(rs, "sub_domain");
				if(StringUtils.isNotEmpty(sub_doMain)) {
					 doc = PubFunc.generateDom(sub_doMain);
                     String xpath = "/sub_para/para";
                     XPath findPath = XPath.newInstance(xpath);
                     List childlist = findPath.selectNodes(doc);
                     if (childlist != null && childlist.size() > 0) {
                    	 element = (Element) childlist.get(0);
                    	 fields = element.getAttributeValue("fields")==null?"":element.getAttributeValue("fields");
                    	 setname=element.getAttributeValue("setname");
                    	 if(StringUtils.isNotEmpty(fields)&&fields.indexOf("`")>-1&&StringUtils.isNotEmpty(setname)) {
                    		 String[] fieldArry=fields.split("`");
                    		 for (int i = 0; i < fieldArry.length; i++) {
                    			if("UM".equalsIgnoreCase(fieldArry[i])||"UN".equalsIgnoreCase(fieldArry[i])
                    					 ||"@K".equalsIgnoreCase(fieldArry[i])||"attach".equalsIgnoreCase(fieldArry[i]))
                    				 continue;
								FieldItem item=DataDictionary.getFieldItem(fieldArry[i],setname);
								if(item!=null&&item.isCode()) {//代码类指标
									fieldStr+=",'"+item.getCodesetid()+"'";
								}
							}
                    	 }
                     }
                     
				}
			}
			if(StringUtils.isNotEmpty(fieldStr)) {
           	 fieldStr=fieldStr.substring(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return fieldStr;
    }
    
    /**
     * 获得临时变量的FieldItem
     * @param midVarList 临时变量指标列表
     * @param varId 临时变量Id
     * @return
     */
    private FieldItem getMIdVarFieldItem(ArrayList midVarList,String varId)
    {
    	FieldItem returnItem=null;
    	try
    	{ 
    		for(int i=0;i<midVarList.size();i++)
			{
				FieldItem fieldItem=(FieldItem)midVarList.get(i);
				if (varId.equalsIgnoreCase(fieldItem.getItemid())){
					returnItem=fieldItem;
					break;
				}
			}
    		
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    	}
    	return returnItem;
    }
    
    /**
     * 获得移动页面指标列表
     * @param tabid 模板ID
     * @param filedPriv  流程定义了某节点的读写权限和必填项
     * @param info   mobile:手机适配页   normal:电脑端页面
     * @param page_no 页签号
     * @return
     *   , "fieldList":[ { “gridno”:”1”   // 单元格编号
		  , “hz”:”请假类型”    // 指标标题--〉指标名称
		  ." item_id":"A0703"  //指标ID   
		  ," item_type":"A"    //指标类型 
		  ," item_length":"20"  //指标长度
		  ,"decimal_width":"0" //小数位数
		  ," chgstate ":"1"     // 1：变化前指标  2：变化后指标
		  ," subflag ":"1"      //1：子集  0：不是子集  
		  ,"value":"1"        //值
		  ,"value_view":"产假" //代码值描述
		  ,"priv":"1"          // 0：无读写权限 1：读权限  2：写权限
		  ,"fillable":"true"  //是否必填
		  ," code_id ":"IS"    //指标关联
		  ,"format":""  //  yyyy.MM.dd | yyyy.MM.dd hh:mm| yyyyy | yyyy.MM
		  },{.....} ]
     */
    private ArrayList getTemplateSetList(int tabid,HashMap filedPriv,String info_type,String page_no,String opinion_field,String taskid)
    {
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rset=null;
    	RowSet rowSet=null;
    	RowSet rowSetOnPcPage=null;
    	
    	ArrayList dataList=new ArrayList();
    	try
    	{ 
    		
    		ArrayList midVarList=null;//临时变量列表
    		StringBuffer strsql=new StringBuffer("select pageid,flag,field_name,chgstate,gridno,hz,field_type,setname,disformat,yneed,codeid,hisMode,subflag,Sub_domain,subflag from Template_Set where tabid=? ");
    		
    		if("mobile".equals(info_type)) //微信端指标
    		{
		    	strsql.append(" and pageid in (select  pageid from Template_Page where isMobile=1 and tabid=?  )   and ( ( nullif(field_name,'') is not null  and field_type is not null ) or subflag=1 or Flag in ('P','F'))  "); //定义了手机适用页的模板
		    	strsql.append("  order by pageid,rtop,rleft");
				rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid) ,Integer.valueOf(tabid)}));
    		}
    		else
    		{
    			strsql.append(" and pageid=? and ( ( nullif(field_name,'') is not null  and field_type is not null ) or subflag=1 or Flag in ('P','F')) ");
    			strsql.append("  order by rtop,rleft");
    			rset=dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid),Integer.parseInt(page_no)}));
    		}
    		HashMap columnMap=new HashMap();
    		String sql="select * from g_templet_"+tabid+" where 1=2";
    		if(!"0".equalsIgnoreCase(taskid)){//如果task不是0，需要查流程中的表
    			sql="select * from templet_"+tabid+" where 1=2";
    		}
			rowSet=dao.search(sql);
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				columnMap.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}
			TemplateParam templateParam= new TemplateParam(conn, this.userView, tabid);
			String end_str="_1";
			if(templateParam.getOperationType()==0||templateParam.getOperationType()==5)
				end_str="_2";
			HashMap infoMap=new HashMap();
			ArrayList pcPageSubsetList=new ArrayList();
			if("mobile".equals(info_type)) //微信端指标
    		{
				strsql.setLength(0);
				strsql.append("select pageid,flag,field_name,chgstate,gridno,hz,field_type,setname,disformat,yneed,codeid,hisMode,subflag,Sub_domain,subflag from Template_Set where tabid=? ");
		    	strsql.append(" and pageid in (select  pageid from Template_Page where isMobile!=1 and tabid=?  ) and subflag=1 "); //定义了手机适用页的模板
		    	strsql.append("  order by rtop,rleft");
		    	rowSetOnPcPage =dao.search(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(tabid) ,Integer.valueOf(tabid)}));
		    	while(rowSetOnPcPage.next()){
		    		HashMap map=new HashMap();
		    		String _sub_domain = Sql_switcher.readMemo(rowSetOnPcPage, "sub_domain");
		    		String itemid=rowSetOnPcPage.getString("field_name")!=null?rowSetOnPcPage.getString("field_name"):"";
					String set_id=rowSetOnPcPage.getString("setname")!=null?rowSetOnPcPage.getString("setname"):"";
					String chgstate=rowSetOnPcPage.getString("chgstate"); // 1：变化前指标  2：变化后指标
					String setname=rowSetOnPcPage.getString("setname");
					SubSetDomain subdomain=new SubSetDomain(_sub_domain);
					map.put("subdomain", subdomain);
					map.put("setname", setname);
					map.put("chgstate", chgstate);
					map.put("itemid", itemid);
					map.put("set_id", set_id);
					map.put("gridno", rowSetOnPcPage.getString("gridno"));
					map.put("pageid", rowSetOnPcPage.getString("pageid"));
					map.put("hz", rowSetOnPcPage.getString("hz").replaceAll("`",""));
					map.put("pageid", rowSetOnPcPage.getString("pageid"));
					map.put("subflag", rowSetOnPcPage.getString("subflag"));
					pcPageSubsetList.add(map);
		    	}
    		}
			while(rset.next())
			{
					String flag=rset.getString("flag");
					String itemid=rset.getString("field_name")!=null?rset.getString("field_name"):"";
					String chgstate=rset.getString("chgstate"); // 1：变化前指标  2：变化后指标
					
					infoMap=new HashMap();
					infoMap.put("flag", flag);
					infoMap.put("gridno", rset.getString("gridno"));
					infoMap.put("pageid", rset.getString("pageid"));
					infoMap.put("hz", rset.getString("hz").replaceAll("`",""));
					infoMap.put("item_id", itemid);
					infoMap.put("pageid", rset.getString("pageid"));
					String set_id=rset.getString("setname")!=null?rset.getString("setname"):"";
					String sub_domain="";
					String sub_domain_=Sql_switcher.readMemo(rset, "sub_domain");
					String subflag=rset.getString("subflag");// 1:子集
					FieldItem item=null;
					
					if("2".equals(chgstate)&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(itemid)) {//审批标识
						infoMap.put("opinion_field", "true");
					}
					
					
					if("1".equals(subflag)) //子集
					{
						infoMap.put("default_value", "");//子集默认值为空
						infoMap.put("item_type","");
						String _sub_domain = Sql_switcher.readMemo(rset, "sub_domain");
						String setname=rset.getString("setname");
						SubSetDomain subdomain=new SubSetDomain(_sub_domain);
						for(int i=0;i<pcPageSubsetList.size();i++){
							HashMap map=(HashMap) pcPageSubsetList.get(i);
							SubSetDomain _subdomain=(SubSetDomain)map.get("subdomain");
							String _setname=(String)map.get("setname");
							String _chgstate=(String)map.get("chgstate");
							String _itemid=(String)map.get("itemid");
							String _set_id=(String)map.get("set_id");
							if(setname.equalsIgnoreCase(_setname)&&chgstate.equalsIgnoreCase(_chgstate)&&itemid.equalsIgnoreCase(_itemid)){
								String subFields = subdomain.getSubFields();
								String _subFields = _subdomain.getSubFields();
								if(subFields.equalsIgnoreCase(_subFields)){
									subdomain=_subdomain;
									break;
								}
							}
						}
						if(subdomain.getSubDomainId().trim().length()>0)
						{
							set_id=setname+"_"+subdomain.getSubDomainId();
						}
						
						ArrayList subFieldList=subdomain.getSubFieldList();
						SubField subField=null;
						ArrayList sub_domain_list=new ArrayList();
						for(Iterator t=subFieldList.iterator();t.hasNext();)
						{
							subField=(SubField)t.next();
							String name=subField.getFieldname();
							String need=subField.getNeed();
							if(name!=null)
							{
								if("attach".equals(name)) //子集附件
								{
									HashMap subMap=new HashMap();
									subMap.put("item_id","attach"); 
									if("0".equals(need)||"false".equalsIgnoreCase(need))
										subMap.put("fillable", "false");  
									else
										subMap.put("fillable", "true");
									sub_domain_list.add(subMap);
								}
								else
								{
									item=DataDictionary.getFieldItem(name, setname);
									if(item!=null)
									{
										HashMap subMap=new HashMap();
										subMap.put("item_id", item.getItemid());
										subMap.put("item_name", item.getItemdesc());
										subMap.put("item_type", item.getItemtype());
										subMap.put("item_length", item.getItemlength());
										subMap.put("decimal_width", item.getDecimalwidth());
										subMap.put("code_id", item.getCodesetid());
										subMap.put("fillable", need);  
										subMap.put("disFormat", subField.getSlop());  
										sub_domain_list.add(subMap);
									}
								}
								
							}
							
						}
						infoMap.put("chgstate",chgstate);   
						if("1".equals(chgstate)) {
							infoMap.put("priv", "1"); 
						}else {
							//设置了流程节点权限时 子集或者指标默认不设置读写权限 则认为是无权限
							if(!filedPriv.isEmpty()) {
								String set_id_ = set_id.toLowerCase();
								if(!set_id_.endsWith("_2")&&!set_id_.endsWith("_1")) {
									set_id_ = setname+"_2";
								}
								if(filedPriv.containsKey(set_id_.toLowerCase())) {//流程节点 子集权限
									infoMap.put("priv", filedPriv.get(set_id_.toLowerCase())); 
								}else {
									infoMap.put("priv", "0"); 
								}
							}else {
								infoMap.put("priv", "2"); 
							}
						}
						infoMap.put("sub_domain",sub_domain_list); 
						infoMap.put("mustfillrecord", subdomain.getMustfillrecord());
						infoMap.put("subflag", "1");  //1：子集  0：不是子集 
						infoMap.put("code_id", "0"); 
					}
					else if("P".equals(flag)) //照片
					{
						infoMap.put("default_value", "");//照片默认值为空
						infoMap.put("item_type","P");
						infoMap.put("subflag", "0");  //1：子集  0：不是子集 
						String priv="2";
						infoMap.put("priv", priv);  
                        if (rset.getInt("yneed") == 0) {
                            infoMap.put("fillable","false");
                        }
                        else 
                           {
                            infoMap.put("fillable","true"); 
                           }

					}
					else if("F".equals(flag)) //附件
					{
						infoMap.put("default_value", "");//附件默认值为空
						infoMap.put("item_type","F");
						String _sub_domain = Sql_switcher.readMemo(rset, "sub_domain");
						SubSetDomain subdomain=new SubSetDomain(_sub_domain);
						if("1".equals(subdomain.getAttachmentType())) //附件类型 ，个人：1 ，    公共：0
							infoMap.put("item_id","file_private");
						else
							infoMap.put("item_id","file_public");
							infoMap.put("priv", "2"); 
						if(rset.getInt("yneed")==0)
							infoMap.put("fillable","false");
						else 
							infoMap.put("fillable","true"); 
						infoMap.put("subflag", "0");  //1：子集  0：不是子集 
						infoMap.put("code_id", "0"); 
						infoMap.put("file_type", subdomain.getFile_type()); 
					}
					else
					{
						String default_value ="";
						if(StringUtils.isNotBlank(sub_domain_)){
							SubSetDomain subdomain=new SubSetDomain(sub_domain_);
							default_value = subdomain.getDefault_value();//获取设置的默认值
						}
						infoMap.put("default_value", default_value);
						infoMap.put("item_type", rset.getString("field_type")!=null?rset.getString("field_type"):""); 
						if ("V".equals(flag)){//临时变量
							if (midVarList==null){
								SearchDataBo  dataBo=new SearchDataBo(this.conn, this.userView,String.valueOf(tabid));   	
								midVarList=dataBo.getTemplateTableBo().getMidVariableList();
							}
							item=getMIdVarFieldItem(midVarList, itemid);
						}
						else {
							item=DataDictionary.getFieldItem(itemid, rset.getString("setname"));
						}
						
						if(!"1".equals(templateParam.getId_gen_manual())&&"2".equals(chgstate)&&item.isSequenceable()) {
							
							IDGenerator idg=new IDGenerator(2,this.conn);
							TemplateBo bo=new TemplateBo(conn, this.userView, tabid);
							RecordVo factoryVo=bo.getFactoryIdVo(item.getSequencename());
							String seq_no="";
							String prefix_field_value="";
							if(item.getC_rule()==1) {
								String prefix_field=factoryVo.getString("prefix_field");
								if(prefix_field!=null&&prefix_field.trim().length()>0&&columnMap.get(prefix_field.toLowerCase()+end_str)!=null) {
									seq_no=bo.getSequenceNoInEqual(item.getItemid()+"_2","g_templet_"+tabid,prefix_field+end_str,item.getPrefix_field_len());
									if(seq_no==null|| "".equalsIgnoreCase(seq_no))
										seq_no=idg.getId(item.getSequencename());
									if(seq_no.length()>item.getItemlength())
										throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
									infoMap.put("value", prefix_field_value+seq_no);
								}else {
									seq_no=bo.getSequenceNoInEqual(item.getItemid()+"_2","g_templet_"+tabid);
									if(seq_no==null|| "".equalsIgnoreCase(seq_no))
										seq_no=idg.getId(item.getSequencename());
									if(seq_no.length()>item.getItemlength())
										throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
									infoMap.put("value", prefix_field_value+seq_no);
								}
							}else {
								String prefix_field=factoryVo.getString("prefix_field");
								String backfix="";
					 	        if(prefix_field_value!=null&&prefix_field_value.length()>0)
					 	            backfix="_"+prefix_field_value;
					 	        RecordVo idFactory=new RecordVo("id_factory");
					 	        idFactory.setString("sequence_name", item.getFieldsetid().toUpperCase()+"."+item.getItemid().toUpperCase()+backfix);
					 	        
								if(dao.isExistRecordVo(idFactory)&&prefix_field!=null&&prefix_field.trim().length()>0&&prefix_field_value.length()>0)//prefix_field_value e0122
									seq_no=idg.getId(item.getSequencename()+"`"+prefix_field_value);
								else
									seq_no=idg.getId(item.getSequencename());
								
								if(seq_no.length()>item.getItemlength())
									throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
								infoMap.put("value", prefix_field_value+seq_no);
								
							}
							infoMap.put("editFlag", "false");//添加标识 变化后序号 界面不可编辑
						} 
						
						if (item==null)
							continue;
						infoMap.put("item_length",String.valueOf( item.getItemlength()));
						if("N".equals(rset.getString("field_type"))){
							infoMap.put("decimal_width", rset.getString("disformat"));
							if("V".equalsIgnoreCase(flag))
								infoMap.put("decimal_width", item.getDecimalwidth()+"");
						}else
							infoMap.put("decimal_width", "0");
						infoMap.put("chgstate",chgstate);   
						infoMap.put("subflag", "0");  //1：子集  0：不是子集  
						if(rset.getInt("yneed")==0)
							infoMap.put("fillable","false");
						else 
							infoMap.put("fillable","true"); 
						
					//	infoMap.put("fillable","false");
						int disFormat=rset.getInt("disformat");
						String format="";
						if("D".equals(rset.getString("field_type")))
							format="yyyy.MM.dd";
						if(disFormat==6)
								format="yyyy.MM.dd";
						else if(disFormat==7)
							format="yyyy.MM.dd";//format="yy.MM.dd";不支持
						else if(disFormat==8||disFormat==9)
							format="yyyy.MM";
						else if(disFormat==10||disFormat==11)
							format="yyyy.MM";//format="yy.MM";暂不支持
						else if(disFormat==19)
							format="yyyy";
						else if(disFormat==25)
							format="yyyy.MM.dd hh:mm";
						infoMap.put("format",format);  //  yyyy.MM.dd | yyyy.MM.dd hh:mm| yyyyy | yyyy.MM
						infoMap.put("code_id",rset.getString("codeid")); 
						 
						String state=this.userView.analyseFieldPriv(itemid);
						/*if(state!=null&&state.equals("0")) 去除员工自助权限 与pc端保持一致
	            			state=this.userView.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限  
*/						if(!"2".equals(state)&& "1".equals(this.UnrestrictedMenuPriv_Input)&& "2".equals(chgstate))//xgq this.UnrestrictedMenuPriv_Input.equals("0")
							state="2";
						
						String columnName=itemid.toLowerCase()+"_"+chgstate;
						if(filedPriv!=null&&filedPriv.get(columnName)!=null){
	                		state = ""+filedPriv.get(columnName);
	                		if("3".equals(state)&&"2".equals(chgstate))
	                		{
	                			state ="2";
	                			infoMap.put("fillable","true");
	                		}
	                	} 
						 
						if("1".equals(chgstate)&& "2".equals(state)) //变化前指标只读
						{
							state="1";
							infoMap.put("fillable","false");
						}
						infoMap.put("hisMode",rset.getString("hisMode")); //如果是多条记录或者是条件记录或者是条件序号
						if ("V".equals(flag)){//临时变量只读权限
							state="1";
						}
						infoMap.put("priv", state); 
					}
					infoMap.put("set_id", set_id);
					if(StringUtils.isNotEmpty(sub_domain))
						infoMap.put("sub_domain", sub_domain);
					dataList.add(infoMap);
			} 
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	finally
    	{
    		PubFunc.closeDbObj(rset);
    	}
    	return dataList;
    }
    
    
    //获得自动审批 或 不走审批流程的模板
    private void setUsedTemplate(HashMap tabMap,String tabid, ContentDAO dao)
    {
    	try
    	{
	    //	RecordVo tab_vo=new RecordVo("Template_table");
		//	tab_vo.setInt("tabid",Integer.parseInt(tabid));
		//	tab_vo=dao.findByPrimaryKey(tab_vo); 
			RecordVo tab_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
			String sp_flag=tab_vo.getString("sp_flag");
			if(!(sp_flag==null|| "".equals(sp_flag)|| "0".equals(sp_flag)))
			{
				String sxml=tab_vo.getString("ctrl_para");
		    	Document doc=PubFunc.generateDom(sxml);
		    	/**审批方法*/
		    	String xpath="/params/sp_flag";
		    	XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);			
				if(childlist!=null&&childlist.size()>0)
				{
					Element element=(Element)childlist.get(0);
					String _sp_mode=(String)element.getAttributeValue("mode");  //审批模式=0自动流转，=1手工指派
					if("0".equals(_sp_mode))
						tabMap.put(tabid,"true");
					else
						tabMap.put(tabid,"false");
				}
				else 
					tabMap.put(tabid,"false");
			}
			else
				tabMap.put(tabid,"true");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    //------------------------------------------------------------------------------------------------------
    
    
    
    /**
     * 获取本人可发起申请的业务模板（定义了手机适用页的模板）
     * @param flag 1：全部模板  2：考勤模板
     * @return
     *  [{ “tabid”:”1”     //模板id
			  ,"tabname":"公出申请"  //模板名称
			  ,"infor_type":"1"  //模板数据类型 1：人员  2：单位  3：职位
			  ,"busitype":""}   //业务类型：1：加班 、2：公出 、3:请假
			, { “tabid”:”2”
			  ,"tabname":"加班申请"
			  ,"infor_type":"1"
			  ,"busitype":""}]
			说明：如果返回空字符串表示没有可申请的模板
     */
    public  String  getApplyTemplate(int flag)
    {
	    	String template_str="";
	        RowSet rs =null;
	        ContentDAO dao = new ContentDAO(this.conn); 
	        ArrayList list = new ArrayList(); 
	        String static_="static";
	        if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
	        	static_="static_o";
	        }
	        try { 
		            StringBuffer strsql = new StringBuffer();
		            strsql.append("select a.tabid,a.name,b.operationname,b."+static_+" "+static_+" from ");
		            strsql.append(" template_table a ,operation b where a.operationcode=b.operationcode ");
		            strsql.append(" and b.operationtype <> 0   "); //不包含人员调入模板
		            strsql.append(" and a.tabid in (select  tabid from Template_Page where isMobile=1 ) "); //定义了手机适用页的模板
		           
		            TemplateTableParamBo tp = new TemplateTableParamBo(this.conn);
		            String jb_tabids=tp.getAllDefineKqTabs(1);  //定义了考勤加班 参数的模板
		            String qj_tabids=tp.getAllDefineKqTabs(2);  //定义了考勤请假 参数的模板
		            String gc_tabids=tp.getAllDefineKqTabs(3);  //定义了考勤公出 参数的模板
		          	if(flag==2)  //2：考勤模板
		        	{ 
			            strsql.append(" and a.tabid in (-1000 "+jb_tabids+qj_tabids+gc_tabids+" ) ");
		        	}
		          	strsql.append(" order by a.tabid ");
		            rs = dao.search(strsql.toString());
		            HashMap map=null;
		            HashMap tabMap=new HashMap(); //仅支持自动审批 或 不走审批流程的模板
		            while (rs.next()){// 权限控制
			               if(rs.getString(static_)!=null&& "1".equals(rs.getString(static_)))
			                  if (!this.userView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
			                    continue;
			               if(rs.getString(static_)!=null&& "2".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&& "8".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&& "3".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&& "4".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&& "5".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&& "6".equals(rs.getString(static_)))
			                      if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
			                        continue;
			               if(rs.getString(static_)!=null&&("10".equals(rs.getString(static_))|| "11".equals(rs.getString(static_))))
			                   continue; 
			               String tabid=rs.getString("tabid");
		                
			               map = new HashMap(); 
			               map.put("tabid",tabid );
			               
			             
			              //仅支持自动审批 或 不走审批流程的模板
			               setUsedTemplate(tabMap, tabid, dao);
			               String _flag=(String)tabMap.get(tabid); 
			               if("false".equals(_flag))
			            	   continue;
			               
			               map.put("tabname", rs.getString("name"));
			               map.put("infor_type", "1");   //模板数据类型 1：人员  2：单位  3：职位
			               String busitype="";
			               if((","+jb_tabids+",").indexOf(","+tabid+",")!=-1)
			            	   busitype="1";
			               else  if((","+qj_tabids+",").indexOf(","+tabid+",")!=-1)
			            	   busitype="3";
			               else  if((","+gc_tabids+",").indexOf(","+tabid+",")!=-1)
			            	   busitype="2";
			               map.put("busitype", busitype); //业务类型：1：加班 、2：公出 、3:请假
			               list.add(map);
		            } 
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        finally{
	            PubFunc.closeDbObj(rs);
	        }
	        if(list.size()>0)
	        	template_str=JSON.toString(list);
	        
	        return template_str;
    }
    
    
   //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    

    /**
     * 获取本人的已申请任务（定义了手机适用页的模板），按任务处理时间倒序排列，最新的在最前面
     * @param paramStr  { "flag":"1"             //1:全部模板  2：考勤模板     ,"pageIndex":"1"      //起始行    ,"pageSize":"20"      //每页行数  ,"busi_type":"" // 10:部门或单位待办   11:岗位待办   其它为人员 } 
     * @return [{"tabname”:”加班申请单”     //模板名称
						  ,"tabid”:”1”     //模板id 
						  ,"taskid":"12"   //待办任务号 
						  ."ins_id":"101"  //实例ID
						  ," applicant":"李四"   //申请人姓名 
						 , "task_state":""   //=1,初始化 =2运行中 =3等待 =4终止 =5结束 =6暂停
						  ," applyTime ":"YYYY-MM-DD hh:mm"} //任务申请时间
						, { "tabname”:”加班申请单”   
							 ,"tabid”:”1”   
							  ,"taskid":"12"    
							  ."ins_id":"102"   
							  ," applicant":"李四" 
   						     ,"task_state":""   //=1,初始化 =2运行中 =3等待 =4终止 =5结束 =6暂停
   						     ,'current_user':"" //当前审批人
							  ," applyTime ":"YYYY-MM-DD hh:mm"},......] 
							说明：如果返回空字符串表示没有待办任务 
     * @throws GeneralException
     */
    public String  getMyApplied (String paramStr) throws GeneralException
    {
    	String task_str="";
    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
    	int pageIndex=Integer.parseInt((String)paramMap.get("pageIndex"));  //起始行
    	int pageSize=Integer.parseInt((String)paramMap.get("pageSize"));      //每页行数
    	int flag=Integer.parseInt((String)paramMap.get("flag"));     //1:全部模板  2：考勤模板 
    	TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
    	String kq_tabids=tp.getAllDefineKqTabs(0);  
    	String busi_type="";  //
    	if(paramMap.get("busi_type")!=null)
    		busi_type=(String)paramMap.get("busi_type");   //10:部门或单位待办   11:岗位待办   其它为人员
    	ArrayList dataList=new ArrayList();
    	RowSet rowSet=null;
    	try
		{
				
				ContentDAO dao=new ContentDAO(this.conn); 
				String sql=getMyAppliedSql(flag,busi_type,kq_tabids);
		    	if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
		    		rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(busi_type) }));
		    	else
		    		rowSet=dao.search(sql); 
		    	HashMap infoMap=null;
		    	while(rowSet.next())
				{
		    		 infoMap=new HashMap();
		    		 String tabid=rowSet.getString("tabid");
					 String tabName=rowSet.getString("name");
					 String task_id=rowSet.getString("task_id");
					 String ins_id=rowSet.getString("ins_id");
					 String actorname=rowSet.getString("actorname")!=null?rowSet.getString("actorname"):"";
					 String recallflag=rowSet.getString("recallflag");
					 /**
					  * xus 16/12/02
					  * 添加流程状态字段
					  */
					 String task_state=rowSet.getString("task_state");
					 infoMap.put("task_state",task_state);
					 infoMap.put("tabname",tabName);
					 infoMap.put("ins_id",rowSet.getString("ins_id"));
					 infoMap.put("tabid",rowSet.getString("tabid"));
					 infoMap.put("applicant",this.userView.getUserFullName());
					 infoMap.put("current_user",actorname);
					 infoMap.put("applyTime", rowSet.getString("ins_start_date"));
					 infoMap.put("recallflag", rowSet.getString("recallflag"));
						/**安全改造，将参数加密**/
					infoMap.put("taskid",rowSet.getString("task_id")); 
					dataList.add(infoMap);
				}
		    	
		    	ArrayList subList=new ArrayList();
	    		for(int i=pageIndex-1;i<pageIndex+pageSize-1&&i<dataList.size();i++)
	    		{
	    			infoMap=(HashMap)dataList.get(i);
	    			subList.add(infoMap);
	    		} 
	    		dataList=subList;
		    	
		}
    	catch (Exception e) {
            e.printStackTrace();
	    } 
	    finally{
	            PubFunc.closeDbObj(rowSet);
	    } 
    	if(dataList.size()>0)
    		task_str=JSON.toString(dataList);
    	return task_str;
    }
    
    
    /**
     * 获得本人申请单据的SQL
     * @param flag 1:全部模板  2：考勤模板 
     * @param busi_type 10:部门或单位待办   11:岗位待办   其它为人员
     * @param kq_tabids  考勤模板
     * @return
     */
    private String getMyAppliedSql(int flag,String busi_type,String kq_tabids)
    {
    	String static_="static";
    	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
    		static_="static_o";
    	}
    	StringBuffer strsql=new StringBuffer(""); 
		String format_str="yyyy-MM-dd HH:mm";
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			format_str="yyyy-MM-dd hh24:mi"; 
		//结束状态  兼容旧程序  2014-04-01 dengcan   case when T.task_topic like '%共0%' then U.name  else T.task_topic end name
		strsql.append("select U.ins_id,t.task_state,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,"+Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,T.task_id,T.actorname,case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag  from t_wf_task T,t_wf_instance U,template_table tt ");
		strsql.append(" where T.ins_id=U.ins_id "); 
		if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
		{
			strsql.append(" and   U.tabid=tt.tabid and tt."+static_+"=?  "); 
		}
		else
		{
			strsql.append("  and  U.tabid=tt.tabid and tt."+static_+"!=10 and tt."+static_+"!=11  "); 
		}
		if(flag==2){ //考勤业务办理 
			strsql.append(" and tt.tabid in ("+kq_tabids.substring(1)+")" );
		}
		strsql.append(" and tt.tabid in (select  tabid from Template_Page where isMobile=1 ) "); //定义了手机适用页的模板
		strsql.append(" and  ( ( task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )  and task_topic not like '%共0人%' and  task_topic not like '%共0条%'  )  or   ( T.task_type='9' and  T.task_state='5' ) )   ");//=3等待状态 =6暂停
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
				userid="-1"; 
		/**人员列表*/
		strsql.append( " and  upper(U.actorid) in ('");
		strsql.append(userid.toUpperCase());
		strsql.append("','");
		strsql.append(this.userView.getUserName().toUpperCase());
		strsql.append("')");  
		//1：审批任务 2：加签任务 3：报备任务  4：空任务
		strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
		strsql.append(" order by U.start_date DESC"); 
		return strsql.toString();
    }
    
    
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    
    /**
     * 获取本人的已办任务（定义了手机适用页的模板），任务时间倒序排列
     * @param paramStr  { "flag":"1"             //1:全部模板  2：考勤模板   ,"pageIndex":"1"      //起始行  ,"pageSize":"20"      //每页行数  ,"type":"1"           //1:待办审批任务  2：待办报备任务  3：全部    ,"busi_type":"" // 10:部门或单位待办   11:岗位待办   其它为人员 } 
     * @return  [{    "taskname”:”加班申请单”     //代办任务名称
							  ,"tabid”:”1”     //模板id 
							  ," applicant":"李四"   //申请人姓名  
							  ,"taskid":"12"   //待办任务号（通知单此内容为空）
							  ,"ins_id":"3"     //实例号（通知单此内容为空）
							  ,"current_user":"" //当前审批人
							 ," applyTime ":"YYYY-MM-DD hh:mm"} //任务申请时间
							, {  "taskname”:”加班申请单”   
							 ,"tabid”:”1”    
							  ," applicant":"李四"   
							  ,"taskid":"15"   
							  ,"ins_id":"3"   
							  ,"current_user":"" //当前审批人  
							  ,"applyTime":"YYYY-MM-DD hh:mm"},......] 
							说明：如果返回空字符串表示没有待办任务 
     */
    public String getYpTask (String paramStr) throws GeneralException
    {
	    	String task_str="";
	    	ArrayList dataList=new ArrayList();
	    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
	    	int pageIndex=Integer.parseInt((String)paramMap.get("pageIndex"));  //起始行
	    	int pageSize=Integer.parseInt((String)paramMap.get("pageSize"));      //每页行数
	    	int flag=Integer.parseInt((String)paramMap.get("flag"));     //1:全部模板  2：考勤模板 
	    	String busi_type="";  //
	    	if(paramMap.get("busi_type")!=null)
	    		busi_type=(String)paramMap.get("busi_type");   //10:部门或单位待办   11:岗位待办   其它为人员
	    	int type=Integer.parseInt((String)paramMap.get("type"));
	    	TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
	    	String kq_tabids=tp.getAllDefineKqTabs(0);  //考勤模板 
	    	RowSet rowSet=null;
	    	try
			{
					
					ContentDAO dao=new ContentDAO(this.conn);
			    	String sql=getYbTaskSql(flag,type,kq_tabids,busi_type);
			    	if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
			    		rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(busi_type) }));
			    	else
			    		rowSet=dao.search(sql); 
			    	HashMap operationTypeMap=new HashMap(); //获得模板的业务操作类型
			    	HashMap infoMap=new HashMap();
			    	int i=0;//bug 36308 计算条数不准确，导致移动端只能显示41条数据
			    	StringBuffer ins_ids=new StringBuffer("");
			    	while(rowSet.next())
					{
			    		if(pageIndex-1>i){
			    			i++;
			    			continue;
			    		}
			    		infoMap=new HashMap();
						String tabid=rowSet.getString("tabid");
						String tabName=rowSet.getString("name");
						String task_id=rowSet.getString("task_id");
						String ins_id=rowSet.getString("ins_id");
						ins_ids.append(","+ins_id);
						String _flag=rowSet.getString("flag")!=null?rowSet.getString("flag"):"";
						String task_topic=rowSet.getString("task_topic");
						String _static="";
						String statickey = "static";
						if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
							statickey = "static_o";
						}
						_static=rowSet.getString(statickey);
						if("".equals(_flag))
						{
							String operationType="";
							if(operationTypeMap.get(tabid)==null)
							{
								operationType=findOperationType(tabid);
								operationTypeMap.put(tabid, operationType);
							}
							else
								operationType=(String)operationTypeMap.get(tabid); 
							String topic =getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,busi_type); 
							if(topic.indexOf(",共0")!=-1) //撤销任务主题
							{
								topic=getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+tabid,dao,Integer.parseInt(operationType), busi_type);
							}
							
							task_topic=tabName+topic;
						}
						infoMap.put("taskname",task_topic);
						infoMap.put("ins_id",rowSet.getString("ins_id"));
						infoMap.put("tabid",rowSet.getString("tabid"));
						infoMap.put("applicant",rowSet.getString("fullname"));
						infoMap.put("applyTime", rowSet.getString("start_date"));
						/**安全改造，将参数加密**/
						infoMap.put("taskid",rowSet.getString("task_id")); 
						dataList.add(infoMap);
						if(i==(pageIndex+pageSize-2))
							break;
						i++;
					}
			    	/*ArrayList subList=new ArrayList();
		    		for(int i=pageIndex-1;i<pageIndex+pageSize-1&&i<dataList.size();i++)
		    		{
		    			infoMap=(HashMap)dataList.get(i);
		    			subList.add(infoMap);
		    		} 
		    		dataList=subList;*/
			    	
			    	if(ins_ids.length()>0) //赋值各任务的当前审批人
			    	{
			    		
			    		String temp_sql=" select ins_id,actorname,actor_type from t_wf_task where ins_id in ("+ins_ids.substring(1)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
			    		//获得各实例下当前审批人
						HashMap ins_CurrentSpInfo=new HashMap(); // 
						if(sql.length()>0)
						{ 
							rowSet=dao.search(temp_sql);
							int ins_id=0;
							String actorname="";
							String actor_type = "";
							while(rowSet.next())
							{
								if(ins_id==0)
									ins_id=rowSet.getInt("ins_id");
								if(ins_id==rowSet.getInt("ins_id")){
									actorname+=","+rowSet.getString("actorname");
									actor_type +=","+ rowSet.getString("actor_type");
								}else
								{
									if(actorname.toString().length()>1&&actor_type.length()>1)//liuyz 排除actor_type和actorname为空值的情况
									{
										ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
										ins_id=rowSet.getInt("ins_id");
										actorname=","+rowSet.getString("actorname");
										actor_type =","+ rowSet.getString("actor_type");
									}
								} 
							}
							PubFunc.closeDbObj(rowSet);
							
							if(ins_id!=0&&actorname.length()>1)//liuyz 排除actor_type和actorname为空值的情况
								ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
						}
						
						
						for(Iterator t=dataList.iterator();t.hasNext();)
						{
							HashMap _Map=(HashMap)t.next();
							String ins_id=(String)_Map.get("ins_id");
							if(ins_CurrentSpInfo.get(ins_id)!=null)
							{
								
								_Map.put("current_user", (String)ins_CurrentSpInfo.get(ins_id));
							}
							else
								_Map.put("current_user", "");
							
						}
						
						
			    		
			    	}
			    	
			    	
			    	
			}
	    	catch (Exception e) {
	            e.printStackTrace();
		    } 
		    finally{
		            PubFunc.closeDbObj(rowSet);
		    } 
	    	if(dataList.size()>0)
	    		task_str=JSON.toString(dataList);
		    	
	    	return task_str;
    }
    
    
    
	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String busi_type)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{ 
			StringBuffer strsql=new StringBuffer(); 
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
			{
				a0101="codeitemdesc_1";
				if(operationtype==5)
					a0101="codeitemdesc_2";
			}
			String strWhere=" where "; 
			String strWhere2="";
			if(state==0)
				strWhere2=" and (state is null or  state=0) ";
			else
				strWhere2=" and state="+state+" "; 
			strWhere+=" exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" "+strWhere2+"  )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());			
			int i=0;
			while(rset.next())
			{
				if(i>4)
					break;
				if(i!=0)
					stopic.append(",");
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());
		
			rset=dao.search(strsql.toString());
			if(rset.next())
				nmax=rset.getInt("nmax"); 
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total")); 	
			stopic.append(nmax);
			 
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
				stopic.append("条记录 ");
			else
				stopic.append("人"); 
			if(state==3)
				stopic.append(" 被撤销");
			stopic.append(")");
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}
	
	
    
    /**
     * 获得已批任务的描述信息
     * @param task_id   任务号
     * @param tabname  临时表名
     * @param operationtype  模板业务类型
     * @param tab_id 模板ID
     * @param busi_type:10:部门或单位待办   11:岗位待办   其它为人员
     * @return
     */
	private String getTopic(String task_id,String tabname,int operationtype,String tab_id,String busi_type)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String a0101="a0101_1"; 
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			} 
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
			{
				a0101="codeitemdesc_1";
				if(operationtype==5)
					a0101="codeitemdesc_2";
			}
			String sql="";
			String seqnum="1"; 
			rset=dao.search("select seqnum from "+tabname+" where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )");
			if(rset.next())
				seqnum=rset.getString(1)!=null?rset.getString(1):"";
			
			if(seqnum.length()>0)
			{
				sql=" select "+a0101+" from "+tabname+",t_wf_task_objlink two where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
						  +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
			}
			else
			{
				sql=" select "+a0101+" from "+tabname+"  where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";
			}  
			rset=dao.search(sql);			
			int i=0;
			while(rset.next())
			{
				if(i>4)
					break;
				if(i!=0)
					stopic.append(",");
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			if(i>4)
			{
				if(seqnum.length()>0)
					sql="select count(*)  from t_wf_task_objlink   where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
		 		else
					sql=" select count(*) from "+tabname+"  where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";  
				rset=dao.search(sql);
					if(rset.next())
						nmax=rset.getInt(1);
			}
			else
				nmax=i;
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));		
			stopic.append(nmax);
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type)))
				stopic.append("条记录)");
			else
				stopic.append("人)");
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return stopic.toString();
		
	}
    
    
    
    /**
     * 获得模板的业务操作类型
     * @param tabid 模板ID
     * @return
     */
    private  String findOperationType(String tabid)
	{
		String operationType="";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid=? )",Arrays.asList(new Object[] {Integer.valueOf(tabid) }));
			if(rowSet.next())
				operationType=rowSet.getString("operationtype"); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return operationType;
	}
    
    
    /**
     * 生成已办任务SQL
     * @param flag 1:全部模板  2：考勤模板
     * @param type 1:已办审批任务  2：已办报备任务  3：全部
     * @param busi_type:10:部门或单位待办   11:岗位待办   其它为人员
     * @return
     */
    private  String getYbTaskSql(int flag,int type,String kq_tabids,String busi_type) throws GeneralException
    {
    	 
	    	StringBuffer strsql=new StringBuffer(""); 
	    	String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi"; 
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
	        strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
			strsql.append("T.actorname,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished,tt.name   from t_wf_task T,t_wf_instance U,template_table tt ");
			strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' ) and  (task_state='5'  or task_state='6' ) ) ");
			strsql.append(" and U.tabid in (select  tabid from Template_Page where isMobile=1 ) "); //定义了手机适用页的模板
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type))){
					strsql.append(" and tt."+static_+"=? " ); 
			}
			else 
					strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");  
			
	        
	        if(flag==2)
	        { 
				if(kq_tabids.length()==0)
					kq_tabids+=",-1000"; 
				strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  "); 
			}
	        if(type==1)
	        	strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  "); 
	        else if(type==2)
	        	strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='3'  ");   
	        
	        
			strsql.append(" and  T.task_type!=1 "); 
			strsql.append(" and (( (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) and ");
			strsql.append(" ( T.flag=1 and U.ins_id in (select ins_id from t_wf_task where "+getInsFilterWhere("")+" and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派' )) )");
			strsql.append(" or ( ");
			
			strsql.append(" ("+getInsFilterWhere("T.")+" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'    )"); 
			strsql.append(" and   U.ins_id not in ( ");
			strsql.append("  select ins_id from t_wf_task where  ( task_topic not like '%共0人%' and  task_topic not like '%共0条%'  ) and (task_type='2' )  and  (task_state='5'  or task_state='6' )  and flag=1 ");
			strsql.append("   and   ins_id in (select ins_id from t_wf_task where  "+getInsFilterWhere("")+"   and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派'  ) ) ");
			strsql.append(" )");
			strsql.append(")");
	         
		
			boolean isSource=false; //判断当前用户是否有模板权限，如没有就无需查找记录了，提高程序执行性能 
			if(this.userView.isHavetemplateid(IResourceConstant.RSBD)||this.userView.isHavetemplateid(IResourceConstant.ORG_BD)||this.userView.isHavetemplateid(IResourceConstant.POS_BD)||this.userView.isHavetemplateid(IResourceConstant.GZBD)||this.userView.isHavetemplateid(IResourceConstant.INS_BD)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG))
				isSource=true;
			if(this.userView.isSuper_admin())
				isSource=true; 
			if(!isSource){
				strsql.append(" and 1=2 ");  
			}  
			strsql.append(" order by T.end_date DESC");
	    	return strsql.toString();
    }
    
    
    /**
     * 根据当前用户身份生成已办任务的查询条件
     * @param othername
     * @return
     */
    private String getInsFilterWhere(String othername)
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
		userid="-1";
		// 业务帐号关联自助用户，钉钉查不到指派给业务用户的单据
		ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userView);
		strwhere.append(" upper("+othername+"actorid) in ('");
		strwhere.append(userid.toUpperCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toUpperCase()).append("'");
		for(int i=0;i<usernameList.size();i++){
			strwhere.append(",'").append(usernameList.get(i)).append("'");
		}
		strwhere.append(" ) ");
			
		if(this.userView.getRolelist().size()>0)
		{
			strwhere.append(" or  ( "+othername+"actor_type=2 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and  upper("+othername+"actorid) in ( ");
			String str="";
			for(int i=0;i<this.userView.getRolelist().size();i++)
			{
					str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
			}
			strwhere.append(str.substring(1));
			strwhere.append(" ) )");
			//本人和有范围的角色
			strwhere.append("  or(( T.actor_type=2 or  T.actor_type=5 )and exists (select null from t_wf_task_objlink where ins_id=U.ins_id and task_id=T.task_id and node_id= T.node_id and tab_id=U.tabid and (state=1 or state=2) and ( upper(username)='"+this.userView.getUserName().toUpperCase()+"' or upper(username)='"+this.userView.getDbname().toUpperCase()+this.userView.getA0100()+"' ) ))");
		}
		  
		String a0100=this.userView.getDbname()+this.userView.getA0100();
		if(a0100==null||a0100.length()==0)
				a0100=this.userView.getUserName();
			/**组织元*/
		strwhere.append(" or (T.actor_type='3' and T.a0100='"+a0100+"')"); 
		return " ( "+strwhere.toString()+" ) ";
	}
   
    
    
     
   // ------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * 获取本人的代办任务+通知单（定义了手机适用页的模板）个数
     * @param paramStr  { "flag":"1"             //1:全部模板  2：考勤模板     ,"type":"1"           //1:待办审批任务  2：待办报备任务  3：全部 } 
     * @return  {"task_number":"7"}  //如没有待办任务，返回 {"task_number":"0"}
     */
    public String  getPendingTaskNumber(String paramStr) throws GeneralException
    { 
    	HashMap numberMap=new HashMap();
    	try
    	{
	    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
	    	paramMap.put("pageIndex", "1");
	    	paramMap.put("pageSize", "1000");
	    	ArrayList list= getPendingTaskList (JSON.toString(paramMap)); 
	    	numberMap.put("task_number", list.size());
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	return  JSON.toString(numberMap);
    }
    
    
    
    
    /**
     * 获取本人的代办任务+通知单（定义了手机适用页的模板），通知单+按代办任务时间倒序排列
     * @param paramStr  { "flag":"1"             //1:全部模板  2：考勤模板   ,"pageIndex":"1"      //起始行  ,"pageSize":"20"      //每页行数  ,"type":"1"           //1:待办审批任务  2：待办报备任务  3：全部  } 
     * @return  [{ “taskname”:”加班申请单”     //代办任务名称
							  ,"tabid”:”1”     //模板id
							  ,"sender":"张三"  //发送人姓名（报批人）
							  ," applicant":"李四"   //申请人姓名
							  ," taskpri ":"1"  //任务优先级=1,紧急=2,一般=3,不急
							  ,"tasktype":"1"  //1:待办任务  2：通知单
							  ,"taskid":"12"   //待办任务号（通知单此内容为空）
							  ,"ins_id":"3"     //实例号（通知单此内容为空）
							  ,"recieveTime":"YYYY-MM-DD hh:mm"} //任务接收时间
							, { “taskname”:”加班申请单”   
							  , “tabid”:”1”   
							  ,"sender":"张三" 
							  ," applicant":"李四" 
							  ," taskpri ":"1"  
							  ,"tasktype":"1"  
							  ,"taskid":"15"   
							  ,"ins_id":"3"     
							  ,"recieveTime":"YYYY-MM-DD hh:mm"},......] 
							说明：如果返回空字符串表示没有待办任务

     */
    public String getPendingTask(String paramStr) throws GeneralException
    {
    	String task_str="";
    	ArrayList dataList=getPendingTaskList (paramStr);
    	if(dataList.size()>0)
    		task_str=JSON.toString(dataList);
    	return task_str;
    }
    
    /**
     * 获取本人的代办任务+通知单（定义了手机适用页的模板），通知单+按代办任务时间倒序排列
     * @param paramStr  { "flag":"1"             //1:全部模板  2：考勤模板   ,"pageIndex":"1"      //起始行  ,"pageSize":"20"      //每页行数  ,"type":"1"           //1:待办审批任务  2：待办报备任务  3：全部    ,"busi_type":"" // 10:部门或单位待办   11:岗位待办   其它为人员} 
     * @return  ArrayList

     */
    public ArrayList  getPendingTaskList (String paramStr) throws GeneralException
    { 
	    	HashMap paramMap=(HashMap)JSON.parse(paramStr);
	    	int pageIndex=Integer.parseInt((String)paramMap.get("pageIndex"));  //起始行
	    	int pageSize=Integer.parseInt((String)paramMap.get("pageSize"));      //每页行数
	    	int flag=Integer.parseInt((String)paramMap.get("flag"));     //1:全部模板  2：考勤模板 
	    	String busi_type="";  //
	    	if(paramMap.get("busi_type")!=null)
	    		busi_type=(String)paramMap.get("busi_type");   //10:部门或单位待办   11:岗位待办   其它为人员
	    	TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
        	String kq_tabids=tp.getAllDefineKqTabs(0);  
	    	
	    	String sql=getPendingTaskSql(Integer.parseInt((String)paramMap.get("flag")),Integer.parseInt((String)paramMap.get("type")),kq_tabids,busi_type);  //获得查询代办SQL
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	RowSet rowSet=null;
	    	ArrayList dataList=new ArrayList();
	    	ArrayList messList=new ArrayList();
	    	try
	    	{ 
	    		messList = getTmessageList(flag,kq_tabids);//获得通知单消息
	    		dataList.addAll(messList); 
	    		
	    		WorkflowBo wb=new WorkflowBo(this.conn,this.userView);
	    		HashMap obj=new HashMap();
	    		rowSet=dao.search(sql);
	    		LazyDynaBean abean=null;
	    		LazyDynaBean paramBean=null;
	    		ArrayList tempList=new ArrayList();
	    		HashMap tabMap=new HashMap();
	    		int k=1;
	    		while(rowSet.next())
	    		{
	    			if(pageIndex-messList.size()-1>k){
	    				k++;
	    				continue;
	    			}
	    			abean=new LazyDynaBean();
	    			
	    			String tabid=rowSet.getString("tabid");
					String task_id=rowSet.getString("task_id");
					String ins_id=rowSet.getString("ins_id");
					String actor_type=rowSet.getString("actor_type");
					String node_id=rowSet.getString("node_id");
	    			String actorname=rowSet.getString("actorname"); // 发起人
	    			String a0101_1=rowSet.getString("a0101_1"); //报批人
	    			String start_date=rowSet.getString("start_date"); //任务接收时间
	    			String taskpri=rowSet.getString("task_pri"); //=1,紧急 =2,一般 	=3,不急
	    			if ("0".equals(taskpri))
	    				taskpri="1";
	    			else if ("1".equals(taskpri))
	    				taskpri="2";
	    			else if ("2".equals(taskpri))
	    				taskpri="3"; 
	    			//仅支持自动审批 或 不走审批流程的模板
	    			setUsedTemplate(tabMap, tabid, dao);
		            String _flag=(String)tabMap.get(tabid); 
		            if("false".equals(_flag))
		            	   continue;
	    			
	    			
	    			paramBean=new LazyDynaBean();
					paramBean.set("tabid",tabid);
					paramBean.set("task_id",task_id);
					paramBean.set("ins_id",ins_id);
					paramBean.set("actor_type",actor_type);
					paramBean.set("node_id",node_id);
					paramBean.set("actorid", rowSet.getString("actorid"));
					if("3".equals(rowSet.getString("bs_flag"))) {
					    abean.set("task_topic",rowSet.getString("task_topic")+"_报备");
					}else {
					    abean.set("task_topic",rowSet.getString("task_topic"));
					}
					if("5".equals(rowSet.getString("actor_type")))//本人
					{
						ArrayList listrecord = wb.getRecordList(paramBean,this.userView);
						if(listrecord.size()==0)
							continue;
						else{
							String stopic_temp="";
							for(int j =0 ;j<listrecord.size();j++){
								LazyDynaBean abean2= (LazyDynaBean)listrecord.get(j);
								if(abean2.get("seqnum")!=null)
									stopic_temp+= ",'"+abean2.get("seqnum")+"'";
							}
							if(stopic_temp.length()>0)
								stopic_temp = stopic_temp.substring(1);
							abean.set("stopic_temp",stopic_temp);
							
						}
					}
					else if("2".equals(rowSet.getString("actor_type")))//角色
					{ 
						ArrayList listrecord = wb.getRecordList(paramBean,this.userView);
						if(listrecord.size()==0)
							continue;
						else{
							String stopic_temp="";
							for(int j =0 ;j<listrecord.size();j++){
								LazyDynaBean abean2= (LazyDynaBean)listrecord.get(j);
								if(abean2.get("seqnum")!=null)
									stopic_temp+= ",'"+abean2.get("seqnum")+"'";
							}
							if(stopic_temp.length()>0)
								stopic_temp = stopic_temp.substring(1);
							abean.set("stopic_temp",stopic_temp);
						}
					} 
					abean.set("tabid",rowSet.getString("tabid")); 
					abean.set("task_id",rowSet.getString("task_id"));
					/**安全平台改造，将涉及到的信息加密end**/
					abean.set("ins_id",rowSet.getString("ins_id"));
					abean.set("node_id",node_id);
					abean.set("template_type", rowSet.getString("template_type"));
					abean.set("isMessage","0");
					abean.set("a0101_1", a0101_1);
					abean.set("actorname",actorname);
					abean.set("taskpri",taskpri);
					abean.set("start_date",start_date);
					
					tempList.add(abean);
					if(k==(pageIndex+pageSize-messList.size()-1))
						break;
					k++;
	    		} 
	    		for(int i=0;i<tempList.size();i++){
	    			abean= (LazyDynaBean)tempList.get(i);
					if(abean!=null&&abean.get("stopic_temp")!=null&&abean.get("stopic_temp").toString().length()>0){
						//模板标题
						String sql2 = "";
						String tabid = (String)abean.get("tabid");
						String stopic_temp = (String)abean.get("stopic_temp");
						String topArr[] = stopic_temp.split(",");
	                    int countTop = topArr.length;
	                    int endIndex = 5; 
	                    if(countTop<5){
	                        endIndex = countTop;
	                    } 
	                    String splitSql="";
	                    for(int j=0; j<endIndex; j++){
	                        splitSql = splitSql+topArr[j]+",";
	                    }
	                    splitSql  = splitSql.substring(0, splitSql.length()-1);
	                    String stopic = "";
	                    int size = 0;
	                    
	                    if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type))){
	                        sql2 = "select ta.name,t.codeitemdesc_1 from template_table ta, t_wf_instance ins,templet_"+tabid+" t where ta.tabid=ins.tabid and t.ins_id=ins.ins_id and t.seqnum in("+splitSql+")";
	                    }else{
	                        sql2 = "select ta.name,t.a0101_1 from template_table ta,t_wf_instance ins,templet_"+tabid+" t where ta.tabid=ins.tabid and t.ins_id=ins.ins_id and t.seqnum in("+splitSql+")";
	                    }
	                    rowSet=dao.search(sql2);
	                    while(rowSet.next())
	                    {
	                        if(size<4){
	                            if(stopic.length()==0)
	                                stopic= rowSet.getString("name")+"(";
	                            if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type))){
	                            	stopic+= rowSet.getString("codeitemdesc_1")+",";
	                            }else{
	                                stopic+= rowSet.getString("a0101_1")+",";
	                            }
	                            size++;
	                        }else{
	                            break;
	                        }
	                    }
						if(stopic.length()>0){
							if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type))){
								stopic= stopic+ResourceFactory.getProperty("hmuster.label.total")+countTop+"条)";
							}else{
								stopic= stopic+ResourceFactory.getProperty("hmuster.label.total")+countTop+"人)";
							}
							abean.set("task_topic", stopic);
						}
					}
					
					obj=new HashMap();
	    			obj.put("taskname",(String)abean.get("task_topic") );
	    			obj.put("tabid", (String)abean.get("tabid"));
	    			obj.put("sender",(String)abean.get("a0101_1") );
	    			obj.put("applicant", (String)abean.get("actorname"));
	    			obj.put("taskpri", (String)abean.get("taskpri") );
	    			obj.put("tasktype", "1");
	    			obj.put("taskid",(String)abean.get("task_id") );
	    			obj.put("ins_id", (String)abean.get("ins_id"));
	    			obj.put("recieveTime",(String)abean.get("start_date"));
	    			dataList.add(obj);
					
				} 
	    		
	    		/*ArrayList subList=new ArrayList();
	    		for(int i=pageIndex-1;i<pageIndex+pageSize-1&&i<dataList.size();i++)
	    		{
	    			obj=(HashMap)dataList.get(i);
	    			subList.add(obj);
	    		} 
	    		dataList=subList;*/
	    	} catch (Exception e) {
		            e.printStackTrace();
		    } 
		    finally{
		            PubFunc.closeDbObj(rowSet);
		    } 
	    
	    	return dataList;
    }
    
    
    /**
     * 获得当前用户的通知单
     * @param flag   1:全部模板  2：考勤模板 
     * @param kq_tabids  考勤模板
     * @return
     */
	private ArrayList getTmessageList(int flag,String kq_tabids)
	{
		ArrayList msglis=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		String static_="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			static_="static_o";
		}
		try {
			StringBuffer sql=new StringBuffer();
			sql.append("select DISTINCT Noticetempid,Template_table.name as name"); //  ,State");
			sql.append(" from tmessage left join Template_table on tmessage.Noticetempid=Template_table.tabid ");
			sql.append(" where (State='0' or State='1')  and Template_table."+static_+"!=10 and Template_table."+static_+"!=11 "); 
			sql.append(" and tmessage.Noticetempid in (select  tabid from Template_Page where isMobile=1 ) "); //定义了手机适用页的模板
			if(flag==2)
			{ 
				if(kq_tabids.length()==0)
					kq_tabids+=",-1000";
				sql.append(" and tmessage.Noticetempid in ("+kq_tabids.substring(1)+ " ) "); 
			}   
			DbWizard dbw=new DbWizard(this.conn);
			if(!dbw.isExistField("tmessage","b0110_self",false))
			{
				Table table = new Table("tmessage");
				Field field=new Field("B0110_Self","B0110_Self");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				table.addField(field);	
				dbw.addColumns(table);
			}
			
			ArrayList list=new ArrayList();
			 
			rs = dao.search(sql.toString());	
			String tabid="";
			boolean isCorrect=false;
			HashMap map=new HashMap();
			HashMap objMap=new HashMap();
			HashMap tabMap=new HashMap();
			while(rs.next())
			{
				tabid=rs.getString("Noticetempid"); 
				if(tabid==null||tabid.length()<=0)
                   continue;
				isCorrect=false;
				if(this.userView.isHaveResource(IResourceConstant.RSBD,tabid))//人事移动
					isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.ORG_BD,tabid))//组织变动
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.POS_BD,tabid))//岗位变动
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.GZBD,tabid))//工资变动
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.INS_BD,tabid))//保险变动
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.PSORGANS,tabid))
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.PSORGANS_FG,tabid))
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.PSORGANS_GX,tabid))
						isCorrect=true;
				if(!isCorrect)
					if(this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid))
						isCorrect=true;
				
				//仅支持自动审批 或 不走审批流程的模板
				setUsedTemplate(tabMap, tabid, dao);
	            String _flag=(String)tabMap.get(tabid); 
	            if("false".equals(_flag))
	            	   continue;
		
				if(isCorrect&&map.get(tabid)==null)
				{ 
					HashMap msgMap=getMessageInfo(rs.getString("Noticetempid"),"");
					String str=(String)msgMap.get("topic");
					if(!"0".equals(str))
					{ 
						 	objMap=new HashMap();
						 	objMap.put("taskname",subText(rs.getString("name"))+str+" _通知" );
						 	objMap.put("tabid", tabid);
						 	objMap.put("sender",(String)msgMap.get("send_users") );
						 	objMap.put("applicant", (String)msgMap.get("send_users"));//与发送人一样 前台使用这个。
						 	objMap.put("taskpri","2" );//=1,紧急 =2,一般 	=3,不急
						 	objMap.put("tasktype", "2"); //1:待办任务  2：通知单
						 	objMap.put("taskid","" );
						 	objMap.put("ins_id", "");
						 	objMap.put("recieveTime",(String)msgMap.get("receive_times"));
			    			msglis.add(objMap);
			    			
			    			map.put(tabid,"1");
					}
				}
			} 
		 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		   
		return msglis;
		
	}
    
	/**
	 * 截取9个汉字
	 * @param text
	 * @return
	 */
	private String subText(String text)
	{
		if(text==null||text.length()<=0)
			return "";
		if(text.length()<18)
			return text;
		text=text.substring(0,18)+"...";
		return text;
	}
    
    
    /**
     * 根据当前用户身份生成代办任务的查询条件
     * @return
     */
    private String getTaskFilterWhere()
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		String orgid="UN"+this.userView.getUserOrgId();//单位编码
		String deptid="UM"+this.userView.getUserDeptId();//部门编码
		String posid="@K"+this.userView.getUserPosId();//  getUserOrgId();//职位编码
		/**组织元*/
		strwhere.append("(T.actor_type='3' and T.actorid in ('");//=3:组织单元
		strwhere.append(orgid.toUpperCase());
		strwhere.append("','");
		strwhere.append(deptid.toUpperCase());
		strwhere.append("','");
		strwhere.append(posid.toUpperCase());
		strwhere.append("'))");
		
		strwhere.append(" or ( T.actor_type='5'  )");
		
		/**人员列表*/
		strwhere.append( " or ((T.actor_type='1' or T.actor_type='4') and lower(T.actorid) in ('");//=1:人员  =4:业务用户 
		strwhere.append(userid.toLowerCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toLowerCase()).append("'");
		strwhere.append("))");
		
		/**角色ID列表*/
	 	ArrayList rolelist= this.userView.getRolelist();//角色列表
	 	StringBuffer strrole=new StringBuffer();
	 	for(int i=0;i<rolelist.size();i++)
	 	{

	 		strrole.append("'");
	 		strrole.append((String)rolelist.get(i));
	 		strrole.append("'");
 			strrole.append(",");	 		
	 	}
	 	if(rolelist.size()>0)
	 	{
	 		strrole.setLength(strrole.length()-1);
	 		strwhere.append(" or (T.actor_type='2' and T.actorid in (");
	 		strwhere.append(strrole.toString());
	 		strwhere.append("))");
	 	}
		return strwhere.toString();
	}
    
    
    /**
     * 生成代办任务SQL
     * @param flag 1:全部模板  2：考勤模板
     * @param type 1:待办审批任务  2：待办报备任务  3：全部
     * @param busi_type  //10:部门或单位待办   11:岗位待办   其它为人员
     * @return
     */
    private  String getPendingTaskSql(int flag,int type,String kq_tabids,String busi_type) throws GeneralException
    {
    	
	    	StringBuffer strsql=new StringBuffer(""); 
	    	String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi"; 
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			strsql.append("select U.tabid,U.actorname,a0101_1,task_topic ,state states,"+Sql_switcher.dateToChar("T.start_date",format_str)+" start_date,task_pri,bread,bfile,task_id ,T.ins_id,U.template_type,T.actor_type,T.actorid,T.node_id,T.bs_flag from t_wf_task T,t_wf_instance U");
			strsql.append(",template_table tt "); 
			strsql.append(" where T.ins_id=U.ins_id and ( task_topic not like '%,共0人)' and task_topic not like '%,共0条记录)' )  "); //20080825解决审批时，把当前审批表的中人员删除掉，这种任务暂不列不出,处理方式有点问题。
		    strsql.append(" and U.tabid=tt.tabid   ");
		    strsql.append(" and U.tabid in (select  tabid from Template_Page where isMobile=1 ) "); //定义了手机适用页的模板
			if(busi_type!=null&&("10".equals(busi_type)|| "11".equals(busi_type))){//如果是单位管理机构调整 或 岗位管理机构调整
				strsql.append("  and tt."+static_+"="+busi_type+"   "); 
			}
			else
				strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11   ");  
	        if(flag==2)
	        { 
				if(kq_tabids.length()==0)
					kq_tabids+=",-1000"; 
				strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  "); 
			}
	        if(type==1) {
	            //待办+报备
	            strsql.append(" and ( ( "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1' and U.finished='2' ) or ("+Sql_switcher.isnull("T.bs_flag", "'1'")+"='3' and bread=0  )  ) "); 
	        }else if(type==2) {
	            strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='3' and U.finished='2'   ");   
	        }
	        strsql.append(" and task_type='2' and task_state='3'    "); 
			strsql.append(" and (");
			strsql.append(getTaskFilterWhere());
			strsql.append(")");
		 
		
		boolean isSource=false; //判断当前用户是否有模板权限，如没有就无需查找记录了，提高程序执行性能 
		if(this.userView.isHavetemplateid(IResourceConstant.RSBD)||this.userView.isHavetemplateid(IResourceConstant.ORG_BD)||this.userView.isHavetemplateid(IResourceConstant.POS_BD)||this.userView.isHavetemplateid(IResourceConstant.GZBD)||this.userView.isHavetemplateid(IResourceConstant.INS_BD)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG))
			isSource=true;
		if(this.userView.isSuper_admin())
			isSource=true; 
		if(!isSource){
			strsql.append(" and 1=2 ");  
		} 
		strsql.append(" order by T.start_date desc");
    	return strsql.toString();
    }
    
    
    
    
    /**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @param Noticetempid: 通知单模板ID
	 * @param type 10: 单位管理机构调整   11: 岗位管理机构调整  其它：人员
	 * @return infoMap  topic:通知单详细描述  send_users：发送者   receive_times：接收时间
	 */
	public HashMap  getMessageInfo(String noticetempid,String type)
	{
			int nmax=0;
			StringBuffer stopic=new StringBuffer();
			stopic.append("(");
			String filter_by_manage_priv="0"; //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
			String include_suborg="1"; //0不包括下属单位, 1包括(默认值)
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);			
			DbWizard dbw=new DbWizard(this.conn);
			String sql="";
			HashMap infoMap=new HashMap(); //通知单信息 
			try
			{ 
				
					//获得模板设置的与通知单有关的参数
					HashMap paramMap=getTemplateParam(noticetempid);
					filter_by_manage_priv=(String)paramMap.get("filter_by_manage_priv");
					include_suborg=(String)paramMap.get("include_suborg");
			
					String where_str="";  //通知单获取条件
					if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())&& "1".equals(filter_by_manage_priv))//不是超级用户，并且要按照权限范围接收数据
					{
						String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
						if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
						{
							where_str+=" and ( "; 
							if(operOrg!=null && operOrg.length() >3)
							{
								StringBuffer tempSql = new StringBuffer(""); 
								String[] temp = operOrg.split("`");
								for (int j = 0; j < temp.length; j++) { 
									 if (temp[j]!=null&&temp[j].length()>0)
									 {
										 if("0".equalsIgnoreCase(include_suborg))//不包含下属单位
										 {
											 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
											 {
												 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
											 }
											 else
												 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
										 }
										 else
											 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");				
									 }
								}
								if(tempSql.length()>0)
								{
									where_str+=tempSql.substring(3);
								}
								else
									where_str+=" tmessage.b0110='##'";
							}
							else
								where_str+=" tmessage.b0110='##'";
							
							where_str+=" or nullif(tmessage.b0110,'') is null )";
						} 
					}
					if(dbw.isExistField("tmessage", "receivetype", false)){
						where_str+=" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ";
						if(this.getRoleArr(userView).length()>0)
							where_str+=" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))";
						else
							where_str+=" )";
					}else
						where_str+=" and (nullif(username,'') is null or lower(username)='"+userView.getUserName().toLowerCase()+"')";
					//判断通知单类型 单位 或 岗位  或人员
					if(type!=null&& "10".equals(type))
						where_str+=" and object_type=2 ";
					else if(type!=null&& "11".equals(type))
						where_str+=" and object_type=3 ";
					else
						where_str+=" and ( object_type is null or object_type=1 ) "; 
					 
					
					String sqlstr  = "";//查找消息库中的发送人、接收日期、审阅状态
			 		String sqlstr2  = "";//查找临时表关联消息库中的发送人、接收日期、审阅状态
					String receive_time="receive_time";
					if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
						}else{
							receive_time="receive_time";
						}
					sql="select count(*) a from ( " ;
					if(type!=null&&("10".equals(type)|| "11".equals(type))){
						sql+="select distinct b0110 from tmessage   where  Noticetempid=? and state='0'  " ;
						sqlstr+="select  b0110,bread,send_user,"+Sql_switcher.dateToChar(receive_time,"yyyy-MM-dd HH:mm")+" receive_time, start_date,state from tmessage   where Noticetempid=?   ";//去掉state=0
					}
					else{	
					 	sql+="select distinct a0100,lower(db_type) db_type  from tmessage   where  Noticetempid=? and state='0'  " ; 
						sqlstr+="select  a0100,bread,send_user,"+Sql_switcher.dateToChar(receive_time,"yyyy-MM-dd HH:mm")+" receive_time,state from tmessage   where  Noticetempid=?   " ;//去掉state=0
					}
					
					sql+=where_str;
					sqlstr+=where_str;
					
					if(dbw.isExistTable(this.userView.getUserName()+"templet_"+noticetempid, false)){
						if(type!=null&&("10".equals(type)|| "11".equals(type))){
						    if("10".equals(type)){//如果是部门或者单位的话 查询的应该是b0110
						        sql+=" union select  distinct b0110  from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  ";
				 		        sqlstr2="  select b0110 from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  ";
						    }else{//如果操作的是岗位的话,查询的就应该是e01a1
						        sql+=" union select  distinct e01a1   from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  ";
			 		        sqlstr2="  select e01a1 from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  ";
						    }
						}else{ 
							sql+=" union select distinct a0100,lower(basepre) db_type from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  "; 
			 				sqlstr2="  select a0100 from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1  ";
						}
					} 
					sql+=" ) aa";
					rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(noticetempid) }));
					while(rowSet.next())
						nmax+=rowSet.getInt(1);
					
					StringBuffer  topic_num=new StringBuffer("");
					topic_num.append(",");
					topic_num.append(ResourceFactory.getProperty("hmuster.label.total"));			
					topic_num.append(nmax);
					if(type!=null&&("10".equals(type)|| "11".equals(type))){
						topic_num.append("条)");	
					}else
						topic_num.append("人)");
					
					if(nmax==0)
					{
						stopic.setLength(0);
						stopic.append("0");
						infoMap.put("topic", "0");
						
					}else{
						
						
						//---------------------获取通知单里的数据信息------------------------------------
						sql="select distinct a0101 from tmessage   where  Noticetempid=?  and state='0' ";
						if(type!=null&&("10".equals(type)|| "11".equals(type))){//对单位活着岗位的操作
							sql="select distinct organization.codeitemdesc from tmessage,organization   where  tmessage.b0110=organization.codeitemid  and Noticetempid=?  and tmessage.state='0' ";
						}  
						String where_str2="";  //临时表中来自通知单的数据
						if(dbw.isExistTable(this.userView.getUserName()+"templet_"+noticetempid, false)){
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								where_str2+=" union select codeitemdesc_1 codeitemdesc  from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1 "; 
							}else
								where_str2+=" union select a0101_1 a0101 from "+this.userView.getUserName()+"templet_"+noticetempid+"  where state=1 ";
						}
						
						RowSet rset=dao.search(sql+where_str+where_str2,Arrays.asList(new Object[] {Integer.valueOf(noticetempid) }));
						int i=0;
						while(rset.next())
						{
							if(i>2)
								break;
							if(i!=0)
								stopic.append(",");
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								stopic.append(rset.getString(1));
							}
							else
								stopic.append(rset.getString("a0101")==null?"":rset.getString("a0101"));
							i++;
						}
						//-----------------------             end       --------------------------------------
						 
						rset=dao.search(sqlstr+" order by receive_time desc ",Arrays.asList(new Object[] {Integer.valueOf(noticetempid) })); 
						String send_users =",";
						String receive_times="";
						int n=0;
						while(rset.next()){
							n++;
							String a0100 = rset.getString(1);
							String state = rset.getString("state"); 
							if(rset.getString("send_user")!=null&&send_users.indexOf(rset.getString("send_user"))==-1&&n<4){
									send_users+=rset.getString("send_user")+",";
									
							}
							if(rset.getString("receive_time")!=null&&n==1){
									String time  = rset.getString("receive_time"); 
									receive_times=time; 
							}
						} 
						send_users = send_users.replace(",,", ",");
						while(send_users.startsWith(","))
							send_users = send_users.substring(1,send_users.length());
						while(send_users.endsWith(","))
							send_users = send_users.substring(0,send_users.length()-1); 
						
						infoMap.put("topic", stopic.toString()+topic_num);
						infoMap.put("send_users", send_users);
						infoMap.put("receive_times", receive_times);
					} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			infoMap.put("topic", "0");
			return infoMap;
		}
		finally
		{
				PubFunc.closeDbObj(rowSet);
		}
		return infoMap;
	}
    
	/**
     * 获得模板设置的与通知单有关的参数
     * @param noticetempid 模板ID
     * @return
     * @throws GeneralException
     */
    private HashMap getTemplateParam(String noticetempid) throws GeneralException
    {
	    	HashMap map=new HashMap();
	    	RowSet rowSet=null;
	    	ContentDAO dao=new ContentDAO(this.conn);			
	    	String filter_by_manage_priv="0"; //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
			String include_suborg="1"; //0不包括下属单位, 1包括(默认值)
	    	try
			{  
				rowSet=dao.search("select ctrl_para from template_table where tabid=?",Arrays.asList(new Object[] {Integer.valueOf(noticetempid) })); 
				if(rowSet.next())
				{
					String sxml=Sql_switcher.readMemo(rowSet,"ctrl_para"); 
					Document doc=null;
					Element element=null;
					if(sxml!=null&&sxml.trim().length()>0)
					{
						doc=PubFunc.generateDom(sxml);
						String xpath="/params/receive_notice";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);			
						if(childlist!=null&&childlist.size()>0)
						{
							element=(Element)childlist.get(0);
							 filter_by_manage_priv=(String)element.getAttributeValue("filter_by_manage_priv");
							 if(element.getAttributeValue("include_suborg")!=null)
								 include_suborg=(String)element.getAttributeValue("include_suborg");
						}
						
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				 throw GeneralExceptionHandler.Handle(e);
			}
			finally
			{
				PubFunc.closeDbObj(rowSet);
			} 
	    	map.put("filter_by_manage_priv", filter_by_manage_priv);
	    	map.put("include_suborg", include_suborg);
	    	return map;
    }
    
    
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
    	HashMap map=new HashMap();
    	map.put("asss","1");
    	map.put("daf","2");
    	ArrayList list=new ArrayList();
    	HashMap _map=new HashMap();
    	_map.put("asss","1");
    	_map.put("daf","2");
    	list.add(_map);
    	map.put("zzz",list);
		String s="{\"tabname\":\"加班申请11\",\"tabid\":\"58\",\"ins_id\":\"\",\"fieldList\":[{\"gridno\":\"3\",\"value\":\"null\"},{\"gridno\":\"5\",\"value\":\"null\"},{\"gridno\":\"4\",\"value\":\"null\"}]}"; 
    	HashMap obj=(HashMap)JSON.parse(s); 
    	
    	Object[] sss=(Object[])obj.get("fieldList"); 
    	HashMap mm=(HashMap)sss[0];
    }
    private String getRoleArr(UserView userView) {
		ArrayList rolelist= userView.getRolelist();//角色列表
	 	StringBuffer strrole=new StringBuffer();
	 	for(int i=0;i<rolelist.size();i++)
	 	{
	 		strrole.append("'");
	 		strrole.append((String)rolelist.get(i));
	 		strrole.append("'");
 			strrole.append(",");	 		
	 	}
	 	if(rolelist.size()>0)
	 	{
	 		strrole.setLength(strrole.length()-1);
	 	}
		return strrole.toString();

	}
    private String getSubmitTaskInfo(String task_id,String orgFlag)
	{
		String info="";
		ContentDAO dao=new ContentDAO(this.conn);
		String fielditem="e0122";
		if("UN".equalsIgnoreCase(orgFlag))
			fielditem="b0110";
		RowSet rset=null;
		try
		{
			String a0100="";
			ArrayList paramList=new ArrayList();
			paramList.add(task_id);
			rset=dao.search("select a0100_1 from t_wf_task where task_id=? ",paramList);
			if(rset.next())
				a0100=rset.getString(1);
			if(a0100!=null&&a0100.trim().length()>0)
			{
				if(a0100.length()>3)
				{
					String dbpre=a0100.substring(0,3);
					boolean flag=false;
					ArrayList dblist=DataDictionary.getDbpreList();
					for(int i=0;i<dblist.size();i++)
					{
						if(((String)dblist.get(i)).equalsIgnoreCase(dbpre))
							flag=true;
					}
					if(flag)
					{
						rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
						if(rset.next())
						{
							info=rset.getString(1);
						}
					} 
				}
				
				if(info.length()==0)
				{
					rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
					if(rset.next())
					{
						String _a0100=rset.getString("a0100");
						String _nbase=rset.getString("nbase");
						if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
						{
							a0100 = _nbase+_a0100;
							rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
							if(rset.next())
							{
								info=rset.getString(1);
							}
						}
						
					}
					
				}
				if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
					String dbpre=a0100.substring(0,3);
					fielditem="b0110";
					rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
					if(rset.next())
					{
						info=rset.getString(1);
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception e)
			{
				
			}
		}
		return info;
	}
}
