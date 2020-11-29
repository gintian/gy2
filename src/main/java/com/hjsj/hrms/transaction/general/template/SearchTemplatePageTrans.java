/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.jobtitle.configfile.transaction.DomXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * <p>Title:SearchTemplatePageTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 20065:21:38 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTemplatePageTrans extends IBusiness {

	private void setReadFlag(String taskid)
	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo taskvo=new RecordVo("t_wf_task");
			taskvo.setInt("task_id",Integer.parseInt(taskid));
			taskvo.setInt("bread",1);
			dao.updateValueObject(taskvo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 得到下一环节审批对象的名称，为了在审批或提交按钮上显示
	 * 审批对象的名称
	 * @param task_id
	 * @param ins_id
	 * @return
	 * @throws GeneralException
	 */
	private String getApplyObjectNameByxgq(String task_id,String ins_id,String tabid,boolean isBatch)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		ArrayList actorlist=new ArrayList();
		ArrayList nextlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		WF_Node wf_node=null;
		boolean b_end=false;
		try
		{
			
			int node_id=-1;
			if(!"0".equals(task_id))
			{
				try {
					this.frowset=dao.search("select node_id from t_wf_task where task_id="+task_id);
				
				if(this.frowset.next())
					node_id=this.frowset.getInt(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(node_id==-1|| "0".equals(ins_id))
			{
				wf_node=new  WF_Node(this.getFrameconn());
				RecordVo vo=wf_node.getBeginNode(String.valueOf(tabid));
				node_id=vo.getInt("node_id");
			}
			wf_node=new  WF_Node(node_id,this.getFrameconn());  
			String sp_flag=wf_node.getSpFlag(wf_node.getExt_param());
			if(sp_flag.length()>0)
			{
				buf.append(sp_flag);
			}
			else
			{
				if(isBatch&&!"0".equals(task_id))
					return "报送&确认";
				ArrayList nextNodeList=wf_node.getNextNodeList(null); //获得下一节点
				if(nextNodeList==null||nextNodeList.size()==0){
					buf.append(ResourceFactory.getProperty("button.appeal"));
					return buf.toString();
				}
				WF_Node nextnode=(WF_Node)nextNodeList.get(0);
				if(nextnode.getNodetype()==9){
					buf.append(ResourceFactory.getProperty("button.submit"));
                }else if  (nextnode.getNodetype()==7){//或汇聚 判断下一节点是否是最后节点，如果是最后节点也显示提交
                    nextNodeList=nextnode.getNextNodeList(null); 
                    if(nextNodeList==null||nextNodeList.size()==0){
                        buf.append(ResourceFactory.getProperty("button.appeal"));
                        return buf.toString();
                    }
                    nextnode=(WF_Node)nextNodeList.get(0);
                    if(nextnode.getNodetype()==9){
                        buf.append(ResourceFactory.getProperty("button.submit"));
                    }
                    else 
                    {
                        buf.append(ResourceFactory.getProperty("button.appeal"));
                    } 
                    
                }
                else 
                {
					buf.append(ResourceFactory.getProperty("button.appeal"));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	public void execute() throws GeneralException {
	    int num=Integer.parseInt((String)this.getFormHM().get("num"));
	    this.getFormHM().put("num",String.valueOf(++num));
		this.getFormHM().put("username", this.userView.getUserName());
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		String isFileViewer = "0";
		isFileViewer = getFileViewer();
		this.getFormHM().put("isFileViewer", isFileViewer);
		this.getFormHM().put("isDisSubMeetingButton", isDisSubMeetingButton());
		if("0".equalsIgnoreCase(sp_batch))
			singletaskApprove();
		else
		{
			batchtaskApprove();//批量审批
		}
		
	}
	/*
	 * 判断是否应该加载fileViewer控件  1：加载 0：不加载
	 * */
	public String getFileViewer(){
		String str = "0";
		String tabid=(String)this.getFormHM().get("tabid");
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from template_set where (upper(flag)='P' or upper(flag)='F') and tabid="+tabid);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				str = "1";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 同一个业务模板多个任务进行审批处理
	 * @throws GeneralException
	 */
	private void batchtaskApprove()throws GeneralException {
		
		this.getFormHM().put("taskState","-1");//活动状态  t_wf_task表中task_state字段
		
		ArrayList tasklist=(ArrayList)this.getFormHM().get("tasklist");
		String page_num=(String)this.getFormHM().get("page_num"); 
		String tabid=(String)this.getFormHM().get("tabid");
		String pageid=(String)this.getFormHM().get("pageno");
		/**审阅标志*/
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		if(tabid==null|| "".equals(tabid))
			throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
		if(pageid==null|| "".equals(pageid))
			throw new GeneralException(ResourceFactory.getProperty("error.notdefine.pageid"));
		
		TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
		
		HashMap ahm=(HashMap)this.getFormHM().get("requestPamaHM");
		String businessModel=(String)this.getFormHM().get("businessModel");
		if(businessModel==null)
			businessModel="0";
		if(ahm.get("businessModel")!=null)
		{
			businessModel=(String)ahm.get("businessModel");
		}
		else
		{
			businessModel="0";
		}
		ahm.remove("businessModel");
		this.getFormHM().put("businessModel",businessModel);
		tablebo.setBusiness_model(businessModel);
		if(("0".equals(businessModel)|| "62".equals(businessModel))&& "2".equals(sp_flag)&&ahm.get("model")!=null&& "yp".equals((String)ahm.get("model")))
		{
			tablebo.setBusiness_model("3");
			ahm.remove("model");
			this.getFormHM().put("businessModel_yp","3");
		}
		else if("0".equals(businessModel)&& "2".equals(sp_flag)&&ahm.get("model")!=null&& "myApply".equals((String)ahm.get("model")))
		{
			tablebo.setBusiness_model("5");
			ahm.remove("model"); 
			this.getFormHM().put("businessModel_yp","");
		}
		else
			this.getFormHM().put("businessModel_yp","");
		//列表，卡片切换不用检索条件
		String index_template = (String)ahm.get("index_template");
		this.getFormHM().put("index_template",index_template);
		this.getFormHM().put("no_sp_yj", tablebo.getNo_sp_yj());
		ahm.remove("index_template");
		/**
		 * 设置流程实例号
		 * ,根据流程实例号是否为0，取得对应的原始单据中的数据，还是
		 * 审批表的数据
		 * */
		String task_id="";
		String ins_id="";
		String ins_ids="";
		StringBuffer task_ids=new StringBuffer();
		LazyDynaBean dyna=null;
		int i=0;
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		if("1".equals(sp_flag))
		{
		  for(i=0;i<tasklist.size();i++)
		  {
			  dyna=(LazyDynaBean)tasklist.get(i);
			  task_id=(String)dyna.get("task_id");
			  /*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			  if(!templateMap.containsKey(task_id)){//不包含当前taskid,说明程序被串改
				  throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			  }
			  */
			  setReadFlag(task_id);//更新t_wf_task表的bread字段。 是否阅读
			  task_ids.append(task_id);
			  task_ids.append(",");
		  }
		  String self=tablebo.getDef_flow_self();   //=1:自定义审批流程
		  if ("1".equals(self)){
				String[] tasklist_=StringUtils.split(task_ids.toString(),",");
				for(int k=0;k<tasklist_.length;k++){
					if (tablebo.isDef_flow_self(Integer.parseInt(tasklist_[k]))){
                      self="2";
                      break;
                  }
				}
			}
		  String errorinfo= getInformation(task_ids.toString(),self);
		  if(errorinfo.trim().length()>0)
				throw new GeneralException(ResourceFactory.getProperty(errorinfo));
		}
		ArrayList inslist=new ArrayList();
		
		String startflag="1"; //判断当前任务节点是不是初始节点 =1为初始节点，=0不是初始节点，对于批量审批而已，有一个不是初始节点的单据，那么startflag=1. liuzy 20151130
		String _ins_id="";//流程号 用于传到form中
		String _task_id="";//任务号 用于传到form中  郭峰添加
		for(i=0;i<tasklist.size();i++)
		{
			dyna=(LazyDynaBean)tasklist.get(i);			
			ins_id=(String)dyna.get("ins_id");
			task_id=(String)dyna.get("task_id");
			if(i==0){
				_ins_id=ins_id;
				_task_id=task_id;
			}
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(!templateMap.containsKey(task_id)){
				  throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			  }
			*/  
			if(!"1".equals(tablebo.isStartNode(ins_id,task_id,tabid,tablebo.getSp_mode())))
				startflag="0";
			
			inslist.add(ins_id);
			ins_ids+=","+ins_id;
			/**多个任务列表*/
	        tablebo.getTasklist().add(task_id); 
		}
		
		WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
		String info=ins.getKqMappingInfo(tabid);
		if(info.length()>1)
		{
			this.getFormHM().put("type","23");
		}
		
		tablebo.setInslist(inslist);
		/**仅为了提示,按实例处理*/
		tablebo.setIns_id(Integer.parseInt(ins_id));
		this.getFormHM().put("ins_ids",ins_ids);

		
		if(tablebo.getSp_mode()==1&&tablebo.isBsp_flag())
		{
			this.getFormHM().put("isFinishTask","0");
			
		}
		if(tablebo.getSp_mode()==0&&tablebo.isBsp_flag())
		{
	//		this.getFormHM().put("isFinishTask",isFinishTask(task_id,ins_id));
			this.getFormHM().put("isFinishTask","0");
		}
		this.getFormHM().put("startflag", startflag);
		
		//judgeIsLlexpr(tabid,tablebo.getLlexpr(),Integer.parseInt(ins_id),inslist);
		
		String filterStr ="";
		if(this.userView.getHm().get("filterStr")!=null) //切换页签需要考虑人员筛选条件 2013-11-29
	    {
	    	   filterStr=(String)this.userView.getHm().get("filterStr");
	    }
	    else
	    {
		       HashMap hm2 = (HashMap) this.getFormHM().get("requestPamaHM");
		       filterStr = (String)hm2.get("filterStr");
		       hm2.remove("filterStr");
		       filterStr=  filterStr==null?"":SafeCode.decode(filterStr);
		       filterStr = PubFunc.keyWord_reback(filterStr);
	    }
	
	    tablebo.setFilterStr(filterStr);
	       
	    if(filterStr!=null&&filterStr.length()>0){
				filterStr = filterStr.replaceAll(this.userView.getUserName()+"templet_"+tabid, "templet_"+tabid);
		}
	    if("0".equals(ins_id)){
	        	filterStr=filterStr.replaceAll("templet_"+tabid,this.userView.getUserName()+"templet_"+tabid);
	    }
	    this.getFormHM().put("filterStr", filterStr);
	    tablebo.setTask_sp_flag((String)this.getFormHM().get("sp_flag"));
	     
	    if(page_num==null)
        	page_num="1";
		int pageNum=Integer.parseInt(page_num);
	    if(pageNum==0)
	        pageNum=1;
		String htmlview=tablebo.createTemplatePageView(Integer.parseInt(pageid),Integer.parseInt(page_num));
		if(pageNum>tablebo.getPageCount())
			pageNum=tablebo.getPageCount();
		this.getFormHM().put("page_num", String.valueOf(pageNum)); 
		this.getFormHM().put("pageCount",String.valueOf(tablebo.getPageCount()));
		
		String attachmentcount = tablebo.getAttachmentcount();
		String attachmentareatotype = tablebo.getAttachmentAreaToType();
		this.getFormHM().put("attachmentcount",attachmentcount);
		this.getFormHM().put("attachmentareatotype", attachmentareatotype);
		String signxml = tablebo.getSignxml();
		/* zxj 20160613 人事异动不再区分标准版专业版，电子签章是人事异动功能之一      
        if(this.userView.getVersion_flag()==0)// 1:专业版 0:标准版
           signxml ="";*/
		this.getFormHM().put("signxml",signxml);
		/**数据集一些变量存放在session中
		 * 数据集控制，需在session中存入数据集名（也可以是表）
		 * 以及对应的字段列表,Field对象列表
		 * */
		HashMap hm=new HashMap();
	//	ArrayList fieldlist=tablebo.getAllFieldItemByPage(Integer.parseInt(pageid));
		hm.put("fieldlist",tablebo.getCurrentFieldlist());//addOtherField(1,fieldlist,tablebo.getInfor_type()));
		hm.put("templet_"+tabid,"templet");
		/**返回对象类型
		 * =0,返回对象为RecordVo
		 * =1,返回对象为LazyDynaBean对象类型
		 * */
		hm.put("objecttype","0");//RecordVo
		HttpSession session=(HttpSession)this.getFormHM().get("session");
		session.setAttribute("templet_"+tabid,hm);
		this.getFormHM().put("setname","templet_"+tabid);	
		if(tablebo.isBsp_flag())
			this.getFormHM().put("sp_ctrl","1"); //需要审批,又简单模式和通用模式两种
		else
			this.getFormHM().put("sp_ctrl","0");//不需要审批
		this.getFormHM().put("sp_mode",String.valueOf(tablebo.getSp_mode()));
		
		if(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0)  //自动流转（批量审批）
		{
			
		 //	this.getFormHM().put("applyobj",getApplyObjectName(task_id,ins_id,tablebo));	
		 	this.getFormHM().put("applyobj",getApplyObjectNameByxgq(task_id,ins_id,tabid,true));
		}
		else
		{
			this.getFormHM().put("applyobj",ResourceFactory.getProperty("button.appeal"));
		}
		String divtop="30px";
        if ("hcm".equals(this.userView.getBosflag())) divtop="38px";
        htmlview="<div id='fixedtabdiv'class='fixedtab common_border_color' style='left:5;top:"+divtop+";position:absolute'  >"+htmlview+"</div>";
		//htmlview="<div id='fixedtabdiv' class='fixedtab common_border_color' style='left:5;top:30px;position:absolute'  >"+htmlview+"</div>";
		//产生虚拟的div电子签章多页（打印每页都生成图片）
		if(signxml!=null&&signxml.length()>0){
			ArrayList pagelist =(ArrayList)this.getFormHM().get("pagelist");
			if(pagelist!=null&&pagelist.size()>1){
				String htmldiv ="";
				for( i=0;i<pagelist.size();i++){
					TemplatePageBo pagebo = (TemplatePageBo)pagelist.get(i);
					if(pageid.equalsIgnoreCase(""+pagebo.getPageid())){
						
					}else{
						TemplatePageBo pagebo2=new TemplatePageBo(this.getFrameconn(),pagebo.getTabid(),pagebo.getPageid());
						ArrayList celllist=pagebo2.getAllCell();
						for(int j=0;j<celllist.size();j++)
						{
							TemplateSetBo cell=(TemplateSetBo)celllist.get(j);
							if("S".equalsIgnoreCase(cell.getFlag())){
								htmldiv+="<div id=signature"+pagebo.getPageid()+"S"+j+" />";
							}
						}		
						
					}
				}
				htmlview+=htmldiv;
			}
		}
		 HashMap cell_param_map = tablebo.getModeCell4();
		 String limit_manage_priv="";
		 String un  = isPriv_ctrl(cell_param_map, "b0110_2");
		 if(un!=null&&un.trim().length()>0)
			 limit_manage_priv+="UN";
		  un  = isPriv_ctrl(cell_param_map, "e0122_2");
		 if(un!=null&&un.trim().length()>0)
			 limit_manage_priv+=",UM";
		 un  = isPriv_ctrl(cell_param_map, "e01a1_2");
		 if(un!=null&&un.trim().length()>0)
			 limit_manage_priv+=",@K";
		 un  = isPriv_ctrl(cell_param_map, "parentid_2");
		 if(un!=null&&un.trim().length()>0)
             limit_manage_priv+=",UM";
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		 FieldItem item = DataDictionary.getFieldItem(onlyname);
		 int infor_type = tablebo.getInfor_type();
		 String generalmessage="";
		 if(infor_type==1){
	      generalmessage ="可以输入\"姓名\"";
            if (item != null) {
                generalmessage+=",\""+item.getItemdesc()+"\"";
            }
            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
            if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag())))
                generalmessage+=",\""+item.getItemdesc()+"\"";
		 }else if(infor_type==2){
		     generalmessage ="可以输入\"组织名称\"";
		 }else{
		     generalmessage ="可以输入\"岗位名称\"";
		 }
			generalmessage+="进行查询";
			generalmessage = SafeCode.encode(generalmessage);
			//节点设置权限批量处理解决不了
			if(ins_id!=null&&!"0".equals(ins_id)&&task_id!=null&&!"0".equals(task_id)){
				HashMap FieldPriv  = tablebo.getFieldPriv(task_id, this.getFrameconn());
				String fields= this.userView.getFieldpriv().toString();
				fields = fields.toUpperCase();
				if(FieldPriv!=null){
						Iterator iterator=FieldPriv.entrySet().iterator();
						String key="";
						String value="";
						while(iterator.hasNext())
						{
							Entry entry=(Entry)iterator.next();
							key=(String)entry.getKey();
							key = key.toUpperCase();
							value=(String)entry.getValue();
							if(key.indexOf("_")!=-1){
								key = key.substring(0,key.indexOf("_"));
								if(fields.indexOf(key)!=-1){
								if("0".equals(value))
								{
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", "");
										fields = fields.replace(key+"1", "");
										fields = fields.replace(key+"2", "");
									}else{
										fields = fields.replace(","+key+"0", "");
										fields = fields.replace(","+key+"1", "");
										fields = fields.replace(","+key+"2", "");
									}
								}else if("1".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"1");
										fields = fields.replace(key+"1", key+"1");
										fields = fields.replace(key+"2", key+"1");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"1");
										fields = fields.replace(","+key+"1", ","+key+"1");
										fields = fields.replace(","+key+"2", ","+key+"1");
									}
								}else if("2".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"2");
										fields = fields.replace(key+"1", key+"2");
										fields = fields.replace(key+"2", key+"2");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"2");
										fields = fields.replace(","+key+"1", ","+key+"2");
										fields = fields.replace(","+key+"2", ","+key+"2");
									}
								}
								}else{
									fields=fields+","+key+value;
								}
							}else{
								if(fields.indexOf(key)!=-1){
								if("0".equals(value))
								{
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", "");
										fields = fields.replace(key+"1", "");
										fields = fields.replace(key+"2", "");
									}else{
										fields = fields.replace(","+key+"0", "");
										fields = fields.replace(","+key+"1", "");
										fields = fields.replace(","+key+"2", "");
									}
								}else if("1".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"1");
										fields = fields.replace(key+"1", key+"1");
										fields = fields.replace(key+"2", key+"1");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"1");
										fields = fields.replace(","+key+"1", ","+key+"1");
										fields = fields.replace(","+key+"2", ","+key+"1");
									}
								}else if("2".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"2");
										fields = fields.replace(key+"1", key+"2");
										fields = fields.replace(key+"2", key+"2");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"2");
										fields = fields.replace(","+key+"1", ","+key+"2");
										fields = fields.replace(","+key+"2", ","+key+"2");
									}
								}
								}else{
									fields=fields+","+key+value;
								}
							
							}
						}
						this.getFormHM().put("nodeprive", fields);
	        	}else{
	        		this.getFormHM().put("nodeprive", "-1");
	        	}
			}else{
				this.getFormHM().put("nodeprive", "-1");
			}
			
	
		this.getFormHM().put("def_flow_self","0");
		this.getFormHM().put("generalmessage", generalmessage);
		this.getFormHM().put("limit_manage_priv", limit_manage_priv);
		this.getFormHM().put("filter_by_factor", tablebo.getFilter_by_factor());
		this.getFormHM().put("htmlview",htmlview);
		/**安全平台改造，前台不允许存放sql语句，所以将sql存放到userview中**/
		this.userView.getHm().put("template_sql", tablebo.getHmuster_sql());
		//this.getFormHM().put("hmuster_sql", tablebo.getHmuster_sql());
		this.userView.getHm().put("sql_filter", tablebo.getHmuster_sql());
		String temp_priv_html = tablebo.getPriv_html();
		this.getFormHM().put("priv_html", tablebo.getPriv_html());
		this.getFormHM().put("outformlist",tablebo.getMusterOrTemplate());
		this.getFormHM().put("batch_task",task_ids.toString());
		this.getFormHM().put("llexpr",tablebo.getLlexpr());
		//this.getFormHM().put("factor",tablebo.getFactor());
		//System.out.println(tablebo.getFactor()); 
//		session.setAttribute("SYS_FILTER_FACTOR",tablebo.getFactor()); 2014-02-22  dengcan  当模板定义了检索条件，条件选人时会出错，选不到人 
		session.setAttribute("SYS_FILTER_FACTOR","");
		this.getFormHM().put("no_priv_ctrl",tablebo.getNo_priv_ctrl());
		this.getFormHM().put("sys_filter_factor",SafeCode.encode(tablebo.getFactor().replaceAll("\"","@")));
		//传设置算法分析器设置临时变量语句
		if("1".equals(tablebo.getFilter_by_factor()))
			session.setAttribute("SUPPORT_VARIABLE_SQL","select  *  from   midvariable where nflag=0 and templetid= "+tabid);
		session.setAttribute("MODEL_STRING","RSYD");
		if(checkFlagHmuster(tabid)){
			this.getFormHM().put("checkhmuster","1");
		}else{
			this.getFormHM().put("checkhmuster","0");
		} 
		this.getFormHM().put("user_","");
		this.getFormHM().put("user_h","");
		String isSendMessage="0";
		if(tablebo.isBemail()&&tablebo.isBsms())
			isSendMessage="3";
		else if(tablebo.isBemail())
			isSendMessage="1";
		else if(tablebo.isBsms())
			isSendMessage="2";
		if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715"))
			isSendMessage="0";
		this.getFormHM().put("isSendMessage", isSendMessage);
		//出现生成序号
		if("1".equals(tablebo.getId_gen_manual())){
			tablebo.existFilloutSequence();
			if("1".equals(tablebo.getExistid_gen_manual()))
				this.getFormHM().put("sequence","1");
			else
				this.getFormHM().put("sequence","0");
		}else{
			this.getFormHM().put("sequence", "0");
		}
		//bug 35282 批量审批后页面没有刷新，导致计算公式计算的值没有重新显示在页面。
		setFormRefresh(ins_id,tablebo,"false");
        if (tablebo.isHaveCalcItem()){
            this.getFormHM().put("refresh", "true");
        }/*else {            
            this.getFormHM().put("refresh", "false");
        }*/
		this.getFormHM().put("ins_id", _ins_id);
		this.getFormHM().put("taskid", _task_id);
		
	}

	
	public String getTaskState(String task_id)
	{
		String task_state="-1";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select task_state from t_wf_task where task_id="+task_id+" and flag=1");
			if(rowSet.next())
				task_state=rowSet.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return task_state;
	}
	
	/**
	 * 单个任务进行审批
	 * @throws GeneralException
	 */
	private void singletaskApprove() throws GeneralException {
		try
		{ 
			String tabid=(String)this.getFormHM().get("tabid");
			String pageid=(String)this.getFormHM().get("pageno");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("taskid");
			String objectId=(String)this.getFormHM().get("objectId");
			
			
			/**开始进行安全判断，是不是由后台传到前台的task_id**/
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			HashMap templateMap=(HashMap) this.userView.getHm().get("templateMap");
			if(templateMap!=null&&!templateMap.containsKey(task_id)){
				throw  new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			String page_num=(String)this.getFormHM().get("page_num"); 
	
			if(ins_id==null|| "0".equals(ins_id))
				this.getFormHM().put("taskState","-1");
			else
				this.getFormHM().put("taskState",getTaskState(task_id));
			
			/**审阅标志*/
			String sp_flag=(String)this.getFormHM().get("sp_flag");
			//sp_flag=1,ins_id!=0时，taskid应该为0
			if((ins_id!=null&& "0".equals(ins_id))&&"1".equals(sp_flag))
				this.getFormHM().put("taskid", "0");
			//处理代办任务
			HashMap ahm=(HashMap)this.getFormHM().get("requestPamaHM");
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if (objectId!=null &&objectId.length()>0){
				objectId=PubFunc.decrypt(objectId);
				tablebo.setObjectId(objectId);
			}
			String self=tablebo.getDef_flow_self();   //=1:自定义审批流程
			String etoken = (String)ahm.get("etoken");
			String businessModel=(String)this.getFormHM().get("businessModel");
			if(businessModel==null)
				businessModel="0";
			if(ahm.get("businessModel")!=null)
			{
				businessModel=(String)ahm.get("businessModel");
			}
		/*	else
			{
				businessModel="0";
			} */
			ahm.remove("businessModel");
			this.getFormHM().put("businessModel",businessModel);
			
			if(ins_id!=null&&!"0".equals(ins_id)&& "1".equals((String)ahm.get("sp_flag"))&&!"61".equals(businessModel)&&!"71".equals(businessModel)){  //20140909 待办时检测是否已处理，当待办变为已办，告知外部系统时需将 sp_flag=2
				//判断当前任务是否当前人审批
				if ("1".equals(self)){
					if (tablebo.isDef_flow_self(Integer.parseInt(task_id)))
	                      self="2";
				}
				String errorinfo= getInformation(task_id,self);
				if(errorinfo.trim().length()>0)
					throw new GeneralException(ResourceFactory.getProperty(errorinfo));
			}
			if(tabid==null|| "".equals(tabid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
			if(pageid==null|| "".equals(pageid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.pageid"));
			 
			if(ins_id==null|| "0".equals(ins_id))
			{
				if(!tablebo.isCorrect(tabid))
					throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
			}
			
			tablebo.setBusiness_model(businessModel);
			
			if(("0".equals(businessModel)|| "62".equals(businessModel))&& "2".equals(sp_flag)&&ahm.get("model")!=null&& "yp".equals((String)ahm.get("model")))
			{
				tablebo.setBusiness_model("3");
				ahm.remove("model");
				this.getFormHM().put("businessModel_yp","3");
			}
			else if("0".equals(businessModel)&& "2".equals(sp_flag)&&ahm.get("model")!=null&& "myApply".equals((String)ahm.get("model")))
			{
				tablebo.setBusiness_model("5");
				ahm.remove("model"); 
				this.getFormHM().put("businessModel_yp","");
			}
			else
				this.getFormHM().put("businessModel_yp","");
			
			//列表，卡片切换不用检索条件
			String index_template = (String)ahm.get("index_template");
			this.getFormHM().put("index_template",index_template);
			ahm.remove("index_template");
			/**
			 * 设置流程实例号
			 * ,根据流程实例号是否为0，取得对应的原始单据中的数据，还是
			 * 审批表的数据
			 * */
			if("1".equals(sp_flag))
				setReadFlag(task_id);
			ArrayList inslist=new ArrayList();
			inslist.add(ins_id);
			tablebo.setInslist(inslist);
			tablebo.setIns_id(Integer.parseInt(ins_id));
			/**新增任务列表,20080418*/
	        tablebo.getTasklist().add(task_id); 
			//judgeIsLlexpr(tabid,tablebo.getLlexpr(),Integer.parseInt(ins_id),inslist);
	       String filterStr ="";
	       if(this.userView.getHm().get("filterStr")!=null) //切换页签需要考虑人员筛选条件 2013-11-29
	       {
	    	   filterStr=(String)this.userView.getHm().get("filterStr");
	       }
	       else
	       {
		       HashMap hm2 = (HashMap) this.getFormHM().get("requestPamaHM");
		       filterStr = (String)hm2.get("filterStr");
		       hm2.remove("filterStr");
		       /**这里用到时记得将加密后的参数给解密回来,暂时还没有用到，不知道怎么解密**/
		       filterStr=  filterStr==null?"":SafeCode.decode(filterStr);
		       filterStr = PubFunc.keyWord_reback(filterStr);
	       }
	       tablebo.setFilterStr(filterStr);
	       
	        if(filterStr!=null&&filterStr.length()>0){
				filterStr = filterStr.replaceAll(this.userView.getUserName()+"templet_"+tabid, "templet_"+tabid);
			}
	        if("0".equals(ins_id)){
	        	filterStr=filterStr.replaceAll("templet_"+tabid,this.userView.getUserName()+"templet_"+tabid);
	        }
	        this.getFormHM().put("filterStr", filterStr);
	        tablebo.setTask_sp_flag((String)this.getFormHM().get("sp_flag"));
	        if(page_num==null)
	        	page_num="1";
	        int pageNum=Integer.parseInt(page_num); 
	        if(pageNum==0)
	        	pageNum=1;
			String htmlview=tablebo.createTemplatePageView(Integer.parseInt(pageid),pageNum);
			if(pageNum>tablebo.getPageCount())
				pageNum=tablebo.getPageCount();
			this.getFormHM().put("page_num", String.valueOf(pageNum)); 
			this.getFormHM().put("pageCount",String.valueOf(tablebo.getPageCount()));
			
			String attachmentcount = tablebo.getAttachmentcount();
			String attachmentareatotype = tablebo.getAttachmentAreaToType();
			this.getFormHM().put("attachmentcount",attachmentcount);
			this.getFormHM().put("attachmentareatotype", attachmentareatotype);
			String signxml = tablebo.getSignxml();
			/* zxj 20160613 人事异动不再区分标准版专业版，电子签章是人事异动功能之一     
            if(this.userView.getVersion_flag()==0)// 1:专业版 0:标准版
               signxml ="";*/
			this.getFormHM().put("signxml",signxml);	
			/**根据前台传过来的参数控制对应的实体表*/
			if(ins_id==null|| "0".equals(ins_id))
				htmlview=htmlview.replaceAll("templet_"+tabid,this.userView.getUserName()+"templet_"+tabid);
			/**数据集一些变量存放在session中
			 * 数据集控制，需在session中存入数据集名（也可以是表）
			 * 以及对应的字段列表,Field对象列表
			 * */
			
			 
			//System.out.println(htmlview);
			
			HashMap hm=new HashMap();
		//	ArrayList fieldlist=tablebo.getAllFieldItemByPage(Integer.parseInt(pageid));
			hm.put("fieldlist",tablebo.getCurrentFieldlist());//addOtherField(1,fieldlist,tablebo.getInfor_type()));
			if(ins_id==null|| "0".equals(ins_id))
			{
			//	hm.put("fieldlist",addOtherField(0,fieldlist,tablebo.getInfor_type()));
				hm.put(this.userView.getUserName()+"templet_"+tabid,"templet");
			}
			else
			{
			//	hm.put("fieldlist",addOtherField(1,fieldlist,tablebo.getInfor_type()));
				hm.put("templet_"+tabid,"templet");
			}
			/**填充起始节点标识，此申请是否为当前用户*/
			String isStartNode=tablebo.isStartNode(ins_id,task_id,tabid,tablebo.getSp_mode());
			this.getFormHM().put("startflag",isStartNode);
			/**返回对象类型
			 * =0,返回对象为RecordVo
			 * =1,返回对象为LazyDynaBean对象类型
			 * */
			hm.put("objecttype","0");//RecordVo
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			if(ins_id==null|| "0".equals(ins_id))
			{
				session.setAttribute(this.userView.getUserName()+"templet_"+tabid,hm);
				this.getFormHM().put("setname",this.userView.getUserName()+"templet_"+tabid);			
			}
			else
			{
				session.setAttribute("templet_"+tabid,hm);
				this.getFormHM().put("setname","templet_"+tabid);			
			}
			this.getFormHM().put("def_flow_self",tablebo.getDef_flow_self());//自定义审批流程
			if(tablebo.isBsp_flag())
				this.getFormHM().put("sp_ctrl","1"); //需要审批,又简单模式和通用模式两种
			else
				this.getFormHM().put("sp_ctrl","0");//不需要审批
			this.getFormHM().put("sp_mode",String.valueOf(tablebo.getSp_mode()));
			if(tablebo.getSp_mode()==0&&tablebo.isBsp_flag())
			{
			 
				this.getFormHM().put("applyobj",getApplyObjectNameByxgq(task_id,ins_id,tabid,false)); //getApplyObjectName(task_id,ins_id,tablebo));
			}
			else
			{
				this.getFormHM().put("applyobj","");
				this.getFormHM().put("isApplySpecialRole","0");
			}
			this.getFormHM().put("nextNodeStr","");
			
			
			if("61".equals(businessModel)|| "62".equals(businessModel)|| "71".equals(businessModel)|| "72".equals(businessModel)) //如果是报备 或加签
			{
				if("62".equals(businessModel)|| "72".equals(businessModel))
				{
					this.getFormHM().put("isFinishTask","1");
				}
				else
				{
					this.getFormHM().put("isFinishTask",isFinishTask(task_id,ins_id)); 
				}
				
			}
			else
			{
				 
					this.getFormHM().put("isFinishTask","0");
			 
			}
			
			 
			if(task_id!=null&&task_id.trim().length()>0)
				this.getFormHM().put("isFinishedRecord", tablebo.isFinishedRecord(task_id));
			else
				this.getFormHM().put("isFinishedRecord","0");
			
			this.getFormHM().put("ins_ids",","+ins_id);
			this.getFormHM().put("no_sp_yj", tablebo.getNo_sp_yj());
			String divtop="30px";
			if ("hcm".equals(this.userView.getBosflag())) divtop="38px";
			htmlview="<div id='fixedtabdiv'class='fixedtab common_border_color' style='left:5;top:"+divtop+";position:absolute'  >"+htmlview+"</div>";
			//产生虚拟的div电子签章多页（打印每页都生成图片）
			if(signxml!=null&&signxml.length()>0){
				ArrayList pagelist =(ArrayList)this.getFormHM().get("pagelist");
				if(pagelist!=null&&pagelist.size()>1){
					String htmldiv ="";
					for(int i=0;i<pagelist.size();i++){
						TemplatePageBo pagebo = (TemplatePageBo)pagelist.get(i);
						if(pageid.equalsIgnoreCase(""+pagebo.getPageid())){
							
						}else{
							TemplatePageBo pagebo2=new TemplatePageBo(this.getFrameconn(),pagebo.getTabid(),pagebo.getPageid());
							ArrayList celllist=pagebo2.getAllCell();
							for(int j=0;j<celllist.size();j++)
							{
								TemplateSetBo cell=(TemplateSetBo)celllist.get(j);
								if("S".equalsIgnoreCase(cell.getFlag())){
									htmldiv+="<div id=signature"+pagebo.getPageid()+"S"+j+" />";
								}
							}		
							
						}
					}
					htmlview+=htmldiv;
				}
			}
			 HashMap cell_param_map = tablebo.getModeCell4();
			 String limit_manage_priv="";
			 String un  = isPriv_ctrl(cell_param_map, "b0110_2");
			 if(un!=null&&un.trim().length()>0)
				 limit_manage_priv+="UN";
			  un  = isPriv_ctrl(cell_param_map, "e0122_2");
			 if(un!=null&&un.trim().length()>0)
				 limit_manage_priv+=",UM";
			 un  = isPriv_ctrl(cell_param_map, "e01a1_2");
			 if(un!=null&&un.trim().length()>0)
				 limit_manage_priv+=",@K";
			 un  = isPriv_ctrl(cell_param_map, "parentid_2");
	         if(un!=null&&un.trim().length()>0)
	             limit_manage_priv+=",UM";
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			 FieldItem item = DataDictionary.getFieldItem(onlyname);
			 int infor_type = tablebo.getInfor_type();
	         String generalmessage="";
	         if(infor_type==1){
	          generalmessage ="可以输入\"姓名\"";
	            if (item != null) {
	                generalmessage+=",\""+item.getItemdesc()+"\"";
	            }
	            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
	            item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
	            if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag())))
	                generalmessage+=",\""+item.getItemdesc()+"\"";
	         }else if(infor_type==2){
	             generalmessage ="可以输入\"组织名称\"";
	         }else{
	             generalmessage ="可以输入\"岗位名称\"";
	         }
	            generalmessage+="进行查询";
	            generalmessage = SafeCode.encode(generalmessage);
			this.getFormHM().put("generalmessage", generalmessage);
			 this.getFormHM().put("limit_manage_priv", limit_manage_priv);
			this.getFormHM().put("filter_by_factor", tablebo.getFilter_by_factor());
			this.getFormHM().put("no_priv_ctrl",tablebo.getNo_priv_ctrl());
			String temp_priv_html = tablebo.getPriv_html();
			this.getFormHM().put("priv_html", tablebo.getPriv_html());
			this.getFormHM().put("htmlview",htmlview);
			this.userView.getHm().put("sql_filter", tablebo.getHmuster_sql());
			/**安全平台改造，尽量不将sql传向前台**/
			//this.getFormHM().put("hmuster_sql",tablebo.getHmuster_sql());
			this.userView.getHm().put("template_sql",tablebo.getHmuster_sql());
			this.getFormHM().put("outformlist",tablebo.getMusterOrTemplate());
			this.getFormHM().put("llexpr",tablebo.getLlexpr());
			//this.getFormHM().put("factor",tablebo.getFactor());
			//System.out.println("ss" + tablebo.getFactor());
	//		session.setAttribute("SYS_FILTER_FACTOR",tablebo.getFactor()); 2014-02-22  dengcan  当模板定义了检索条件，条件选人时会出错，选不到人 
			session.setAttribute("SYS_FILTER_FACTOR","");
			//传设置算法分析器设置临时变量语句
			if("1".equals(tablebo.getFilter_by_factor()))
				session.setAttribute("SUPPORT_VARIABLE_SQL","select  *  from   midvariable where nflag=0 and templetid= "+tabid);
			session.setAttribute("MODEL_STRING","RSYD");
			this.getFormHM().put("sys_filter_factor",SafeCode.encode(tablebo.getFactor().replaceAll("\"","@")));
			if(checkFlagHmuster(tabid)){
				this.getFormHM().put("checkhmuster","1");
			}else{
				this.getFormHM().put("checkhmuster","0");
			}
			
			
			String isSendMessage="0";
			if(tablebo.isBemail()&&tablebo.isBsms())
				isSendMessage="3";
			else if(tablebo.isBemail())
				isSendMessage="1";
			else if(tablebo.isBsms())
				isSendMessage="2";
			
			if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("3800715"))
				isSendMessage="0";
			this.getFormHM().put("isSendMessage", isSendMessage);
			//出现生成序号
			if("1".equals(tablebo.getId_gen_manual())){
				tablebo.existFilloutSequence();
				if("1".equals(tablebo.getExistid_gen_manual()))
					this.getFormHM().put("sequence","1");
				else
					this.getFormHM().put("sequence","0");
			}else{
				this.getFormHM().put("sequence", "0");
			}
			
			WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
			String info=ins.getKqMappingInfo(tabid);
			if(info.length()>1)
			{
				this.getFormHM().put("type","23");
			}
			
			//节点设置权限批量处理解决不了
			if(ins_id!=null&&!"0".equals(ins_id)&&task_id!=null&&!"0".equals(task_id)){
				HashMap FieldPriv  = tablebo.getFieldPriv(task_id, this.getFrameconn());
				String fields= this.userView.getFieldpriv().toString();
				fields = fields.toUpperCase();
				if(FieldPriv!=null){
						Iterator iterator=FieldPriv.entrySet().iterator();
						String key="";
						String value="";
						while(iterator.hasNext())
						{
							Entry entry=(Entry)iterator.next();
							key=(String)entry.getKey();
							key = key.toUpperCase();
							value=(String)entry.getValue();
							if(key.indexOf("_")!=-1){
								key = key.substring(0,key.indexOf("_"));
								if(fields.indexOf(key)!=-1){
								if("0".equals(value))
								{
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", "");
										fields = fields.replace(key+"1", "");
										fields = fields.replace(key+"2", "");
									}else{
										fields = fields.replace(","+key+"0", "");
										fields = fields.replace(","+key+"1", "");
										fields = fields.replace(","+key+"2", "");
									}
								}else if("1".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"1");
										fields = fields.replace(key+"1", key+"1");
										fields = fields.replace(key+"2", key+"1");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"1");
										fields = fields.replace(","+key+"1", ","+key+"1");
										fields = fields.replace(","+key+"2", ","+key+"1");
									}
								}else if("2".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"2");
										fields = fields.replace(key+"1", key+"2");
										fields = fields.replace(key+"2", key+"2");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"2");
										fields = fields.replace(","+key+"1", ","+key+"2");
										fields = fields.replace(","+key+"2", ","+key+"2");
									}
								}
								}else{
									fields=fields+","+key+value;
								}
							}else{
								if(fields.indexOf(key)!=-1){
								if("0".equals(value))
								{
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", "");
										fields = fields.replace(key+"1", "");
										fields = fields.replace(key+"2", "");
									}else{
										fields = fields.replace(","+key+"0", "");
										fields = fields.replace(","+key+"1", "");
										fields = fields.replace(","+key+"2", "");
									}
								}else if("1".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"1");
										fields = fields.replace(key+"1", key+"1");
										fields = fields.replace(key+"2", key+"1");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"1");
										fields = fields.replace(","+key+"1", ","+key+"1");
										fields = fields.replace(","+key+"2", ","+key+"1");
									}
								}else if("2".equals(value)){
									if(fields.startsWith(key)){
										fields = fields.replace(key+"0", key+"2");
										fields = fields.replace(key+"1", key+"2");
										fields = fields.replace(key+"2", key+"2");
									}else{
										fields = fields.replace(","+key+"0", ","+key+"2");
										fields = fields.replace(","+key+"1", ","+key+"2");
										fields = fields.replace(","+key+"2", ","+key+"2");
									}
								}
								}else{
									fields=fields+","+key+value;
								}
							
							}
						}
						this.getFormHM().put("nodeprive", fields);
	        	}else{
	        		this.getFormHM().put("nodeprive", "-1");
	        	}
			}else{
				this.getFormHM().put("nodeprive", "-1");
			}
			if(task_id!=null&&!"0".equals(task_id))
				this.getFormHM().put("isEndTask_flow",String.valueOf(ins.isEndNode(Integer.parseInt(task_id),tablebo)));
			else
				this.getFormHM().put("isEndTask_flow","false");
			this.getFormHM().put("allow_def_flow_self",String.valueOf(tablebo.isDef_flow_self(Integer.parseInt(task_id))));
			setFormRefresh(ins_id,tablebo,isStartNode);
			if (tablebo.isHaveCalcItem()){
			    this.getFormHM().put("refresh", "true");
			}
		
		}
		catch(Exception e)
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 判断是否要保存是否要刷新界面
	 * @param ins_id
	 * @param tablebo
	 */
	private void setFormRefresh(String ins_id,TemplateTableBo tablebo,String isStartNode)
	{
		try
		{ 
			ArrayList formulalist=tablebo.readFormula();
 			if(formulalist.size()>0)
 			{ 
			//	if(ins_id==null||ins_id.equals("0"))			
				{
					if("1".equals(tablebo.getAutoCaculate())||(tablebo.getAutoCaculate().length()==0&&SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))))
				    {  
					    this.getFormHM().put("refresh", "true"); 
					}
					else
						this.getFormHM().put("refresh", "false");
				}
		   /*	else
				{
					if(isStartNode.equals("1"))
						 this.getFormHM().put("refresh", "true");  
					else
						this.getFormHM().put("refresh", "false");
				}   */
 			}
 			else
 			{
 				this.getFormHM().put("refresh", "false");
 			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断其他审批人是否已处理完。
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public String isFinishTask(String task_id,String ins_id)
	{
		String flag="0";
		try
		{ 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select  count(*)  from t_wf_task tw,t_wf_instance ti where tw.ins_id=ti.ins_id  and tw.ins_id="+ins_id+" and tw.task_id="+task_id;
			       sql+=" and  tw.task_state='5'  ";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
					flag="1";
			}
		 }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	/**
	 * 加上主键以及其它控制字段
	 * @param flag
	 * @param fieldlist
	 */
	private ArrayList  addOtherField(int flag,ArrayList fieldlist,int infor_type)
	{
		ArrayList list=new ArrayList();
        Field item = null;
        String field_name=null;
		for(int i=0;i<fieldlist.size();i++)
		{
            Object obj = fieldlist.get(i);
            if(obj instanceof FieldItem)
            {
                FieldItem fielditem = (FieldItem)obj;
                item = fielditem.cloneField();
            } else
            {
                item = (Field)obj;
            }	
			if(item.isChangeAfter())
				field_name=item.getName()+"_2";
			else if(item.isChangeBefore())
				field_name=item.getName()+"_1";
			else 
				field_name=item.getName();            
           	item.setName(field_name);
            list.add(item);
		}
		Field temp=null;
		if(infor_type==1)
		{
			temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setKeyable(true);
			temp.setSortable(false);	
			temp.setLength(10);
			//temp.setSequenceable(true);
			//temp.setSequencename("rsbd.a0100");
			list.add(temp);
	
			temp=new Field("A0101_1",ResourceFactory.getProperty("label.title.name"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setKeyable(false);
			temp.setSortable(false);	
			temp.setReadonly(true);	
			//temp.setNChgstate(1);
			temp.setLength(30);
			list.add(temp);
			
			temp=new Field("basepre",ResourceFactory.getProperty("label.dbase"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);	
			temp.setKeyable(true);
			temp.setLength(3);
			list.add(temp);
		}
		else if(infor_type==2)
		{
			temp=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("UN");
			temp.setKeyable(true);
			list.add(temp);
			 
			temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("general.template.orgname"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			list.add(temp);
		}
		else if(infor_type==3)
		{
			temp=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("@K");
			temp.setKeyable(true);
			list.add(temp);
			
			temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			list.add(temp);
		}
		/**提交选择标识*/
		temp=new Field("submitflag","submitflag");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(false);
		temp.setSortable(false);
		temp.setValue("0");
		list.add(temp);
		
		if(flag==0)
		{
			/**状态标志=0,=1来源消息(其它模板发过来的通知)*/
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setValue("0");
			list.add(temp);
		}
		else //审批表结构
		{
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field

			list.add(temp);
			temp=new Field("ins_id","ins_id");
			temp.setLength(12);
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field
			list.add(temp);
			temp=new Field("task_id","task_id");
			temp.setLength(12);
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field
			list.add(temp);
		}
		return list;
	}
	private boolean checkFlagHmuster(String relatTableid){
		boolean checkflag = false;
		String temp=this.userView.getResourceString(5);
		if(temp.trim().length()==0) 
			temp="-1";
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT tabid FROM muster_name where ");
		strsql.append("nmodule='5'");
		strsql.append(" and nPrint="+relatTableid);
//		if(!this.userView.isAdmin()&&!this.userView.getGroupId().equals("1")){
//			strsql.append(" and tabid in (");   
//			strsql.append(temp); 
//			strsql.append(") ");
//		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(strsql.toString());
			String tabid ="-1";
			while(this.frowset.next()){
				tabid = this.frowset.getString("tabid");
				checkflag=true;
				break;
//				if(!this.userView.isSuper_admin()){
//					if(temp.indexOf(tabid)!=-1){
//						checkflag=true;
//						break;
//					}
//				}else{
//					checkflag=true;
//					break;
//				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkflag;
	}
	public String  isPriv_ctrl(HashMap cell_param_map,String field){
		String sub_domain="";
		Document doc = null;
		Element element=null;
		StringBuffer sb = new StringBuffer();
		LazyDynaBean bean = (LazyDynaBean)cell_param_map.get(field);
		if(bean!=null&&bean.get("sub_domain")!=null)
			sub_domain=(String)bean.get("sub_domain");
		sub_domain = SafeCode.decode(sub_domain);
		if(sub_domain!=null&&sub_domain.trim().length()>5)
		{
			try {
				doc=PubFunc.generateDom(sub_domain);
				String xpath="/sub_para/para";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					String priv =(String)element.getAttributeValue("limit_manage_priv");
					if("1".equals(priv)){
						if(!this.userView.isSuper_admin()){
							String privCode= this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
							if (!((privCode!=null)&& ("UN".equalsIgnoreCase(privCode)))){//不是全权才判断 20140604wangrd							    
							    if(this.userView.getManagePrivCodeValue()!=null&&this.userView.getManagePrivCodeValue().length()>=2)
							        sb.append(" and  codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
							    else 
							        sb.append(" and 1=2 ");
							}
						}
					}
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
		}
	
	public String getInformation(String task_ids,String self){
		String errorinfo="";
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			PendingTask imip=new PendingTask();
			String taskids="";
			String[] lists=StringUtils.split(task_ids,",");
			for(int i=0;i<lists.length;i++)
			{
				taskids+=","+lists[i];
			}
			String sql ="";
			if("2".equals(self)){   // 当自定义审批流程时，应查询t_wf_node_manual表中的数据，liuzy 20150813
				sql="select t.ins_id,t.task_id,t.task_topic,t.node_id,t.actorid,t.actor_type,t.actorname,t.start_date,m.tabid from t_wf_task t,t_wf_node_manual m where t.ins_id=m.ins_id and t.node_id = m.id and t.task_id in ("+taskids.substring(1)+" ) ";
			}else{
				sql ="select ins_id,task_id,task_topic,t_wf_task.node_id,actorid,actor_type,actorname,start_date,t_wf_node.tabid from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id and task_state='3'  and t_wf_task.task_id in ("+taskids.substring(1)+")";
			}
			RowSet  frowset=dao.search(sql);
			RowSet  frowset2=null;
			String actor_type ="";
			String node_id="";
			String tabid ="";
			String task_id="";
			int j=0;
			//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能	
			ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(userView);
			while(frowset.next()){
				j++;
				actor_type = frowset.getString("actor_type");
				node_id = frowset.getString("node_id");
				tabid= frowset.getString("tabid");
				task_id=frowset.getString("task_id");
				String actor_id =frowset.getString("actorid");
				/*
				if(j==0){
					sql ="select t_wf_task.actor_type,t_wf_instance.tabid from t_wf_task,t_wf_instance where t_wf_task.ins_id = t_wf_instance.ins_id and  t_wf_task.task_state='3'  and t_wf_task.task_id="+task_id;
					frowset=dao.search(sql);
					if(frowset.next()){//流程修改过
						actor_type = frowset.getString("actor_type");
						node_id = "0";
						tabid= frowset.getString("tabid");
					}
					else{
						return "该单据已处理！";
					}
				}
				else */
				{
					LazyDynaBean abean=null;
					HashMap dataMap=new HashMap();
					if("5".equals(actor_type))//本人
					{
						
						String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
						sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0 and tt.a0100='"+userView.getA0100()+"' and lower(tt.basepre)='"+userView.getDbname().toLowerCase()+"'";
						frowset2=dao.search(sql0);
						
						while(frowset2.next())
						{
							
							if(dataMap.get(node_id+task_id)==null)
							{
								dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
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
						frowset2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
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
						
						if(scope_field.length()>0)
						{
							String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
							sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ";
							//如果有业务用户，增加查询业务用户用户名
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql0+=" or username='"+usernameList.get(i)+"' ";
								}
							}							
							sql0+=") ";
							{
								String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
								//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
								if (actor_id!=null && actor_id.length()>0){
									String role_property="";//角色特征
									frowset2= dao.search("select role_property from t_sys_role where role_id= '"+actor_id+"'");
									if (frowset2.next()){    	
										role_property= frowset2.getString("role_property");
									}
									
									String filterField="";
									if ("1".equals(role_property)){//部门领导
										String e0122=this.userView.getUserDeptId();
										if (e0122!=null &&e0122.length()>0){
											operOrg="UN"+e0122;
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
						    
							frowset2=dao.search(sql0);
							ArrayList updList=new ArrayList();
							while(frowset2.next())
							{
								String username=frowset2.getString("username");
								if(dataMap.get(node_id+task_id)==null)
								{
									dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
									dataMap.put(node_id+task_id,"1");
								}
								
								
								if(username==null||username.trim().length()==0)
								{
									ArrayList tempList=new ArrayList();
									tempList.add(userView.getUserName());
									tempList.add(frowset2.getString("seqnum"));
									tempList.add(new Integer(frowset2.getString("task_id")));
									updList.add(tempList);
								//	dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
								}
								
								
							}
							
							if(updList.size()>0)
							{ 
								dao.batchUpdate("update t_wf_task_objlink set username=? where seqnum=? and task_id=?",updList );
							}
							
							 //bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
							sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and (username='"+userView.getUserName()+"'   ";
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql0+=" or username='"+usernameList.get(i)+"' ";
								}
							}							
							sql0+=") ";	 
							frowset2=dao.search(sql0);
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
							////普通角色也抢单 2013-7-19 dengc
							String	sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ";
							//如果有业务用户，增加查询业务用户用户名
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql0+=" or username='"+usernameList.get(i)+"' ";
								}
							}							
							sql0+=") ";
							frowset2=dao.search(sql0);
							if(frowset2.next())
							{
								String username=frowset2.getString("username");
								if(username==null||username.trim().length()==0)
									dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"'  where task_id="+task_id+" and node_id="+node_id);
								 
							}else{
									String encrypt_task_id=PubFunc.encrypt(task_id);
									imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
									errorinfo="该单据已被他人锁定处理了！";
							}
						}
					
					
					}
				}
				
				
				
			}
			if(frowset2!=null)
				frowset2.close();
			if(frowset!=null)
				frowset.close();
			
			if(j==0){
				//调用第三方接口将单子置为已办
				for(int i=0;i<lists.length;i++)
				{
					String taskid =lists[i];
					String encrypt_task_id=PubFunc.encrypt(taskid);
					imip.updatePending("T","HRMS-"+encrypt_task_id,1,"业务模板",this.userView);
				}
				//查询人是不是都被撤销。如果没有则提示。
				String sqlstr="select count(1) num from t_wf_task_objlink where task_id in ("+taskids.substring(1)+") and state='3'";
				RowSet rowSet = dao.search(sqlstr);
				int coutNum=0;
				if(rowSet.next())
				{
					coutNum = rowSet.getInt("num");
				}
				PubFunc.closeDbObj(rowSet);
				if(coutNum==0)
					return "该单据已处理！";
				else 
					return "";
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorinfo;
	
		
	}
	/**
	 * 获得报批人所在的单位  或 部门
	 * @param task_id
	 * @param orgFlag UN:单位  UM：部门
	 * @return
	 */
	private String getSubmitTaskInfo(String task_id,String orgFlag)
	{
		String info="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String fielditem="e0122";
		if("UN".equalsIgnoreCase(orgFlag))
			fielditem="b0110";
		RowSet rset=null;
		try
		{
			String a0100="";
			//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
			int ins_id=0;
			int node_id=0;
			String state="";
		//	rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
			rset=dao.search("select ins_id,state,node_id from t_wf_task where task_id="+task_id);
			if(rset.next())
			{
				node_id=rset.getInt("node_id");
				ins_id=rset.getInt("ins_id");
				state=rset.getString("state");
			}
			if("07".equals(state))  //驳回
			{ 
				rset=dao.search("select a0100_1 from t_wf_task where node_id="+node_id+" and ins_id="+ins_id+" and state='08' and "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='5' order by task_id desc");
			}
			else
				rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id); 
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
	/*
	 * 是否显示上会 按钮
	 * */
	public String isDisSubMeetingButton(){
		String str = "0";
		String tabid=(String)this.getFormHM().get("tabid");
		try{
			DomXml	domXml = new DomXml();
			String templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "5")+",";
			if (templateId.contains(tabid)){
				str="1";
				return str;
			}
			templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "6")+",";
			if (templateId.contains(tabid)){
				str="1";
				return str;
			}
	
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
}
