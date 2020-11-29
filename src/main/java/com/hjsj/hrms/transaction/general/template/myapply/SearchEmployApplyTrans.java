/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.myapply;

import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 25, 2008:1:18:42 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchEmployApplyTrans extends IBusiness {

	/**
	 * 加上主键以及其它控制字段
	 * @param flag
	 * @param fieldlist
	 */
	private ArrayList  addOtherField(int flag,ArrayList fieldlist)
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
		Field temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
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
	
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
		{
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("taskid");			
			String tabid=(String)this.getFormHM().get("tabid");
			String pageid=(String)this.getFormHM().get("pageno");
			if(tabid==null|| "".equals(tabid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
			if(pageid==null|| "".equals(pageid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.pageid"));
			
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			/**很重要，仅查个人的数据*/
			tablebo.setBEmploy(true);
			
			if(!tablebo.isCorrect(tabid))
				throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
			
			
			String htmlview=tablebo.createTemplatePageView(Integer.parseInt(pageid),1);

			htmlview=htmlview.replaceAll("templet_"+tabid,"g_templet_"+tabid);
			/**数据集一些变量存放在session中
			 * 数据集控制，需在session中存入数据集名（也可以是表）
			 * 以及对应的字段列表,Field对象列表
			 * */
			String signxml = tablebo.getSignxml();
			
			String attachmentcount = tablebo.getAttachmentcount();
			String attachmentareatotype = tablebo.getAttachmentAreaToType();
			this.getFormHM().put("attachmentcount",attachmentcount);//处理附件
			this.getFormHM().put("attachmentareatotype", attachmentareatotype);//处理附件
			this.getFormHM().put("infor_type",String.valueOf(tablebo.getInfor_type()));
			/**个人业务的申请ins_id=0,在后面会有创建者的判断不用处理附件的相关加密问题**/
			
			/* zxj 20160613 人事异动不再区分标准版专业版，电子签章是人事异动功能之一		
             if(this.userView.getVersion_flag()==0)// 1:专业版 0:标准版
				signxml ="";*/
			this.getFormHM().put("signxml",signxml);
			HashMap hm=new HashMap();
		//	ArrayList fieldlist=tablebo.getAllFieldItemByPage(Integer.parseInt(pageid));

			hm.put("fieldlist",tablebo.getCurrentFieldlist()); //addOtherField(0,fieldlist));
			hm.put("g_templet_"+tabid,"templet");			

			/**返回对象类型
			 * =0,返回对象为RecordVo
			 * =1,返回对象为LazyDynaBean对象类型
			 * */
			hm.put("objecttype","0");//RecordVo
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			session.setAttribute("g_templet_"+tabid,hm);
			this.getFormHM().put("setname","g_templet_"+tabid);			
			
			if(tablebo.isBsp_flag())
				this.getFormHM().put("sp_ctrl","1"); //需要审批,又简单模式和通用模式两种
			else
				this.getFormHM().put("sp_ctrl","0");//不需要审批
			
  		    this.getFormHM().put("sp_mode",String.valueOf(tablebo.getSp_mode()));
  			if(tablebo.getSp_mode()==0&&tablebo.isBsp_flag())
  			{
  				this.getFormHM().put("applyobj",ResourceFactory.getProperty("button.appeal")); // getApplyObjectName(task_id,ins_id,tablebo));
  				WorkflowBo bo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView); 
  				String nextNodeStr="";
  				try{
  				 nextNodeStr=bo.getNextNodeStr(0,0,"1");
  				}catch(Exception ee){
  					
  				}
  				this.getFormHM().put("nextNodeStr",nextNodeStr);
  				 
  			}
  			else
  			{
  				this.getFormHM().put("isApplySpecialRole","0");
  				this.getFormHM().put("applyobj",ResourceFactory.getProperty("button.appeal"));  		
  				this.getFormHM().put("nextNodeStr","");
  			}

  			htmlview="<div id='fixedtabdiv' class='fixedtab common_border_color' style='left:5;top:30px;position:absolute'  >"+htmlview+"</div>";
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
  			this.getFormHM().put("limit_manage_priv", limit_manage_priv);
			this.getFormHM().put("htmlview",htmlview); 
			this.getFormHM().put("outformlist",tablebo.getMusterOrTemplate());
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
			
			String type="";
			if(hm.get("type")!=null)
			{
				type=(String)hm.get("type");
				this.getFormHM().put("type",type);
				hm.remove("type");
			}
			this.getFormHM().put("type",type);
			if("1".equals(tablebo.getAutoCaculate())||(tablebo.getAutoCaculate().length()==0&&SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))))
		    {  
			 			ArrayList formulalist=tablebo.readFormula();
			 			if(formulalist.size()>0)
			 			{
			 				this.getFormHM().put("refresh", "true");
			 			}
			 			else
			 				this.getFormHM().put("refresh", "false");
			}
			else
				this.getFormHM().put("refresh", "false");
	        if (tablebo.isHaveCalcItem()){
	            this.getFormHM().put("refresh", "true");
	        }
	        
			this.getFormHM().put("selfplatform", "1");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
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
	private String getApplyObjectName(String task_id,String ins_id,TemplateTableBo tablebo)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		ArrayList actorlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		WF_Node wf_node=null;
		boolean b_end=false;
		try
		{
			if(ins_id==null|| "0".equals(ins_id))
			{
				wf_node=tablebo.getWF_StartNode();
				ArrayList list=wf_node.getNextHumanNodeList();
				if(list.size()>0)
				{
					wf_node=(WF_Node)list.get(0);
					actorlist=wf_node.getActorList();
				}
			}
			else
			{
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",Integer.parseInt(task_id));
				task_vo=dao.findByPrimaryKey(task_vo);
				if(task_vo==null)
					throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
				int node_id=task_vo.getInt("node_id");
				wf_node=new WF_Node(node_id,this.getFrameconn(),tablebo);
				/**分析下一个节点是否为END*/
				ArrayList nextlist=wf_node.getNextNodeList(null);
				if(nextlist.size()==1)
				{
					WF_Node wf_endnode=(WF_Node)nextlist.get(0);
					if(wf_endnode.getNodetype()==NodeType.END_NODE)
					{
						b_end=true;
					}
				}
				
				nextlist=wf_node.getNextHumanNodeList();//取下一个人工节点
				for(int i=0;i<nextlist.size();i++)
				{
					wf_node=(WF_Node)nextlist.get(i);
					actorlist.addAll(wf_node.getActorList());
				}
				//actorlist=wf_node.getActorList();
			}	
			/**参与者对象列表*/
			for(int i=0;i<actorlist.size();i++)
			{
				WF_Actor wf_actor=(WF_Actor)actorlist.get(i);
				if(i>0)
					buf.append(",");
				else
				{
					buf.append("报送[");   
				}
				if(SystemConfig.getPropertyValue("clientName")!=null&& "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
				{
					String node_id=String.valueOf(wf_actor.getNode_id());
					RowSet rowSet=dao.search("select nodename from t_wf_node where node_id="+node_id+" and tabid="+tablebo.getTabid());
					if(rowSet.next())
						buf.append(rowSet.getString(1));
					rowSet.close();
				}
				else
					buf.append(wf_actor.getActorname());
 
				int role_property=wf_actor.decideIsKhRelation(wf_actor.getActorid(),wf_actor.getActortype(),this.getFrameconn());
				if(role_property!=0)
				{
					this.getFormHM().put("isApplySpecialRole", "1");
				}
				else
					this.getFormHM().put("isApplySpecialRole","0");
				
				
				
			}//for i loop end.
			if(buf.length()==0)
			{
				if(b_end)
					buf.append(ResourceFactory.getProperty("button.submit"));					
				else
					buf.append(ResourceFactory.getProperty("button.appeal"));
			
				this.getFormHM().put("isApplySpecialRole","0");
			}
			else
			{
				buf.append("]审批");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
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
		if(sub_domain!=null&&sub_domain.length()>0)
		{
			try {
				doc=PubFunc.generateDom(sub_domain);;
				String xpath="/sub_para/para";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					String priv =(String)element.getAttributeValue("limit_manage_priv");
					if("1".equals(priv)){
						if(!this.userView.isSuper_admin()){
							if(this.userView.getManagePrivCodeValue()!=null&&this.userView.getManagePrivCodeValue().length()>2)
							sb.append(" and  codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
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
}
