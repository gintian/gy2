/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:选择报批对象</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 18, 200610:21:20 AM
 * @author chenmengqing
 * @version 4.0
 */

public class SelectApplyObjectTrans extends IBusiness 
{
	public void execute() throws GeneralException 
	{
		/**如果代码太多的话，AdminCode.getCodeItemList效率不高
		 * 有待优化
		 */
		String tabid = (String)this.getFormHM().get("tabid");
		ArrayList templist = new ArrayList();
		
		// 判断审批方式为"手工指派"并且指定了某审批关系 JinChunhai 2013.04.02
		String signLogo = "noHand";
		LazyDynaBean abean = this.parse_xml_param(tabid);
		String sp_mode = "";  // 0表示自动流转 1表示手工指派
		String relation_id = "";
		if(abean!=null)
		{
			sp_mode = (String)abean.get("sp_mode");
			relation_id = (String)abean.get("relation_id");
		}
		if((sp_mode!=null && sp_mode.trim().length()>0 && "1".equalsIgnoreCase(sp_mode)) && (relation_id!=null && relation_id.trim().length()>0))
		{//如果是手动流转，且有审批关系
			RecordVo relationVo = getRelationVo(relation_id);
			String actor_type = relationVo.getString("actor_type"); // 审批关系类型 1:自助 4:业务
			if((this.userView.getStatus()==0 && (this.userView.getA0100()==null || this.userView.getA0100().trim().length()<=0) && "1".equalsIgnoreCase(actor_type)) || (this.userView.getStatus()==4 && "4".equalsIgnoreCase(actor_type)))
			{//业务用户，并且没有关联自助用户，并且自助审批关系     或者      自助用户，业务审批关系
				ArrayList rolelist = AdminCode.getCodeItemList("41");		
				for(int i=0;i<rolelist.size();i++)
				{
					CodeItem item = (CodeItem)rolelist.get(i);
					if("0".equals(item.getCodeitem()))
						continue;
					templist.add(item);
				}
			}
			else//业务用户，业务审批关系   或者    自助用户，自助审批关系
			{
				signLogo = "hand";
				//bug 40316 当业务用户关联自助用户，且设置的审批关系是业务审批关系时候，查询的是业务关联的自助用户的审批关系，导致无法查出设置的直接领导等，按照手工选人选择下一个报批人。
				if("1".equals(actor_type)){
					templist = getMainbodyDescList(relation_id,(this.userView.getDbname()+this.userView.getA0100()));
					this.getFormHM().put("actor_type",actor_type);//bug 43677 无法发送短信通知
				}else if("4".equals(actor_type)){
					templist = getMainbodyDescList(relation_id,"");
					this.getFormHM().put("actor_type",actor_type);
				}
				if(templist.size()==0) //如果审批关系没有定义，仍按手工选人方式
				{
					signLogo = "noHand";
					ArrayList rolelist = AdminCode.getCodeItemList("41");		
					for(int i=0;i<rolelist.size();i++)
					{
						CodeItem item = (CodeItem)rolelist.get(i);
						if("0".equals(item.getCodeitem()))
							continue;
						templist.add(item);
					}	
				}
			}
		}
		else//如果是手动流转，且没有审批关系，    或者是自动流转
		{
			ArrayList rolelist = AdminCode.getCodeItemList("41");		
			for(int i=0;i<rolelist.size();i++)
			{
				CodeItem item = (CodeItem)rolelist.get(i);
				if("0".equals(item.getCodeitem()))
					continue;
				templist.add(item);
			}			
		}
		this.getFormHM().put("signLogo",signLogo);
		this.getFormHM().put("rolelist",templist);
		this.getFormHM().put("emergencylist",AdminCode.getCodeItemList("37"));
		ArrayList list=AdminCode.getCodeItemList("30");
		if (list.size()<1){//有删除此代码项的单位 导致提交有问题
			throw new GeneralException("系统代码项30已在代码体系中删除，请联系管理员！");
		}
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String type=hm.get("type")!=null?(String)hm.get("type"):"";
		hm.remove("type");
		if("2".equals(type)) //驳回
		{
			ArrayList tempList=new ArrayList();
			for(int j=0;j<list.size();j++)
			{
				CodeItem vo=(CodeItem)list.get(j);
				if("02".equals(vo.getCodeitem()))
					tempList.add(vo);
			}
			list=tempList;
		}
		this.getFormHM().put("sp_yjlist",list);
		
		TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
		String isSendMessage="0";
		if(tablebo.isBemail()&&tablebo.isBsms())
			isSendMessage="3";
		else if(tablebo.isBemail())
			isSendMessage="1";
		else if(tablebo.isBsms())
			isSendMessage="2";
		
		if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715"))
			isSendMessage="0";
		
		String enduser_fullname=getenduser_fullname(tablebo.getEnduser(),tablebo.getEndusertype());
		 
		this.getFormHM().put("enduser_fullname",enduser_fullname);	
		this.getFormHM().put("enduser",tablebo.getEnduser());
		this.getFormHM().put("endusertype",tablebo.getEndusertype());
		this.getFormHM().put("topic", "");
		this.getFormHM().put("sp_yj", "01");
		this.getFormHM().put("emergency", "1");
		this.getFormHM().put("infor_type",String.valueOf(tablebo.getInfor_type()));
		this.getFormHM().put("email_staff", String.valueOf(tablebo.isEmail_staff()));
		this.getFormHM().put("isSendMessage", isSendMessage);
		
		if(hm.get("modeType")!=null&&((String)hm.get("modeType")).trim().length()>0)
		{
			this.getFormHM().put("type",(String)hm.get("modeType"));
			hm.remove("modeType");
		}		
	}
		
	private String getenduser_fullname(String enduser,String endusertype)
	{
		String fullname="";
		if(enduser.trim().length()>0&&endusertype.trim().length()>0)
		{
			try
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if("0".equals(endusertype)) //用户
				{
					this.frowset=dao.search("select fullname from operuser where username='"+enduser+"'");
				}
				else
				{
					this.frowset=dao.search("select a0101 fullname from "+enduser.trim().substring(0,3)+"a01 where a0100='"+enduser.trim().substring(3)+"'");
				}
				if(this.frowset.next())
				{
					if(this.frowset.getString(1)!=null&&this.frowset.getString(1).trim().length()>0)
						fullname=this.frowset.getString(1);
					else
						fullname=enduser;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fullname;
	}

	/**
	 * 解释业务模板定义的审批方法的参数
	 * @param sxml
	 * @return
	 */
	private LazyDynaBean parse_xml_param(String tabid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		String sp_mode = "";
		String relation_id = "";
		RowSet rowSet = null;
		try
		{			
			ContentDAO dao = new ContentDAO(this.getFrameconn());	
			rowSet = dao.search("select ctrl_para from template_table where tabid = '"+tabid+"' ");
		    if(rowSet.next())
		    {
		    	String ctrl_para = rowSet.getString("ctrl_para");
		    	if (ctrl_para==null || ctrl_para.trim().length()<=0)
		    	{
		    		bean.set("sp_mode", sp_mode);
					bean.set("relation_id", relation_id);
		    	} 
		    	else
		    	{
					Document doc = PubFunc.generateDom(ctrl_para);
					
					/**审批方法*/
					String xpath = "/params/sp_flag";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist = findPath.selectNodes(doc);			
					if(childlist!=null && childlist.size()>0)
					{
						Element element = (Element)childlist.get(0);
						sp_mode = (String)element.getAttributeValue("mode");//审批模式: 0自动流转模式, 1手工指派模式
						relation_id = (String)element.getAttributeValue("relation_id");//如果定义了审批关系 relation_id指的是审批关系的id
						if(relation_id==null)
							relation_id="";
						if(sp_mode==null)
							sp_mode="";
						bean.set("sp_mode", sp_mode);
						bean.set("relation_id", relation_id);
					}		
		    	}			
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}			
		return bean;
	}
	
	/**
	 * 解释业务模板定义的审批方法的参数
	 * @param a0100
	 * @return
	 */
	private ArrayList getMainbodyDescList(String relation_id,String a0100)
	{
		ArrayList tempList = new ArrayList();
		RowSet rowSet = null;
		RowSet rs = null;
		try
		{		
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null || "00".equals(display_e0122) || "".equals(display_e0122))
				display_e0122 = "0";
			String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			seprartor = seprartor!=null&&seprartor.length()>0?seprartor:"/";
			if(" ".equals(seprartor))
				seprartor="&nbsp;";
			String isOperUser = "0";//判断是否是业务用户
			if(a0100==null || a0100.trim().length()<=0){//对业务用户而言
				isOperUser = "1";
				a0100 = this.getUserView().getUserName();
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String strSql = "select b0110,e0122,e01a1,a0101,mainbody_id,sp_grade,actor_type,groupid from t_wf_mainbody "+
							"where relation_id = '"+relation_id+"' and object_id = '"+a0100+"' order by sp_grade ";
			rowSet = dao.search(strSql);
			//只有自助审批关系才有定义在部门岗位上的审批关系。priority:position,then department and then unit
			if("0".equals(isOperUser)){
				int count = 0;
				if(rowSet.next()){
					count = 1;
				}
				if(count>0){
					rowSet.beforeFirst();
				}else{
					HashMap prioritymap = new HashMap();//key:order value:relation of position.if null then relation of deartment and the rest can be done in the same manner
					String pre = a0100.substring(0,3);
					String employeeNo = a0100.substring(3);
					String unstr = "";
					String umstr= "";
					String ukstr = "";
					StringBuffer sbsql = new StringBuffer("");
					//找出发起人所在的单位部门岗位
					sbsql.append("select b0110,e0122,e01a1 from "+pre+"a01 where a0100='"+employeeNo+"'");
					RowSet myrs = null;
					myrs = dao.search(sbsql.toString());
					if(myrs.next()){
						String strun = myrs.getString(1)==null?"###":myrs.getString(1);
						String strum = myrs.getString(2)==null?"###":myrs.getString(2);
						String struk = myrs.getString(3)==null?"###":myrs.getString(3);
						unstr="UN"+strun;
						umstr="UM"+strum;
						ukstr="@K"+struk;
					}
					sbsql.setLength(0);
					sbsql.append("select object_id from t_wf_mainbody where relation_id = '"+relation_id+"' and  ");
					sbsql.append(Sql_switcher.left("object_id", 2)+" in ('UN','UM','@K')");
					myrs = dao.search(sbsql.toString());
					while(myrs.next()){
						String inner_object_id = myrs.getString(1)==null?"***":myrs.getString(1);
						if((unstr.indexOf(inner_object_id)!=-1) || (umstr.indexOf(inner_object_id)!=-1) || (ukstr.indexOf(inner_object_id)!=-1)){//eg:inner_object_id=UN0101,unstr=un010101
							String prefix = inner_object_id.substring(0,2);
							if("UN".equalsIgnoreCase(prefix)){
								if(prioritymap.get("3")==null){
									prioritymap.put("3", inner_object_id);
								}else{
									String tempobjectid = (String)prioritymap.get("3");
									if(tempobjectid.length()>inner_object_id.length()){//只有现在的长度比上一个小，才替换
										prioritymap.put("3", inner_object_id);
									}
								}
							}else if("UM".equalsIgnoreCase(prefix)){
								if(prioritymap.get("2")==null){
									prioritymap.put("2", inner_object_id);
								}else{
									String tempobjectid = (String)prioritymap.get("2");
									if(tempobjectid.length()>inner_object_id.length()){//只有现在的长度比上一个小，才替换
										prioritymap.put("2", inner_object_id);
									}
								}
							}else if("@K".equalsIgnoreCase(prefix)){
								if(prioritymap.get("1")==null){
									prioritymap.put("1", inner_object_id);
								}else{
									String tempobjectid = (String)prioritymap.get("1");
									if(tempobjectid.length()>inner_object_id.length()){//只有现在的长度比上一个小，才替换
										prioritymap.put("1", inner_object_id);
									}
								}
							}
						}
					}
					if(myrs!=null){
						myrs.close();
					}
					String myobject = "########";
					for(int k=1;k<=3;k++){
						String tempstr = String.valueOf(k);
						if(prioritymap.get(tempstr)!=null){
							myobject = (String)prioritymap.get(tempstr);
							break;
						}
					}
					StringBuffer sb = new StringBuffer("");
					sb.append("select b0110,e0122,e01a1,a0101,mainbody_id,sp_grade,actor_type,groupid from t_wf_mainbody ");
					sb.append("where relation_id = '"+relation_id+"' and ");
					sb.append("object_id = '"+myobject+"'");
					sb.append(" order by sp_grade");
					rowSet = dao.search(sb.toString());
				}
			}
			while(rowSet.next())
		    {
				String actor_type = isNull(rowSet.getString("actor_type")); // 1:自助用户 4:业务用户
				String groupid = isNull(rowSet.getString("groupid"));
				String mainbody_id = isNull(rowSet.getString("mainbody_id"));
				String a0101 = isNull(rowSet.getString("a0101"));
		    	String sp_grade = isNull(rowSet.getString("sp_grade"));
		    	if("9".equals(sp_grade))
					sp_grade = " （直接领导）";
		    	else if("10".equals(sp_grade))
					sp_grade = " （主管领导）";
		    	else if("11".equals(sp_grade))
					sp_grade = " （第三级领导）";
		    	else if("12".equals(sp_grade))
					sp_grade = " （第四级领导）";
				
		    	String gradedesc = "";
				if(actor_type!=null && actor_type.trim().length()>0 && "4".equalsIgnoreCase(actor_type))
				{
					String str = "select groupname,username,fullname from UserGroup ug,operuser ou "+
					 			 "where ug.groupid = ou.groupid and ug.groupid = '"+groupid+"' and username = '"+mainbody_id+"' ";
					rs = dao.search(str);
					if (rs.next())
				    {
						String fullname = isNull(rs.getString("fullname")); 
						String username = isNull(rs.getString("username"));
						if(fullname==null || fullname.trim().length()<=0)
							fullname = username;
						
						a0101 = fullname;
						gradedesc = isNull(rs.getString("groupname"))+"/"+fullname+" "+sp_grade;
				    }							
				}
				else
				{					
					String e0122 = "";
			    	if(Integer.parseInt(display_e0122)==0)				
			    		e0122 = AdminCode.getCodeName("UM",(String)isNull(rowSet.getString("e0122")));				
					else
					{
						CodeItem item = AdminCode.getCode("UM",(String)isNull(rowSet.getString("e0122")),Integer.parseInt(display_e0122));
		    	    	if(item!=null)	    	    	
		    	    		e0122 = item.getCodename();	        		
		    	    	else	    	    	
		    	    		e0122 = AdminCode.getCodeName("UM",(String)isNull(rowSet.getString("e0122")));	    	    		    	    	
					}
					
			    	gradedesc = AdminCode.getCodeName("UN",isNull(rowSet.getString("b0110")))+seprartor+e0122
					            +seprartor+AdminCode.getCodeName("@K",isNull(rowSet.getString("e01a1")))+seprartor+a0101+""+sp_grade;					
				}
				
				CommonData temp = new CommonData(mainbody_id+"`"+a0101, gradedesc);
				tempList.add(temp);		    				
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}			
		return tempList;
	}
	
	/**
     * 取得审批关系信息 
     * @param planid
     * @return
     */
	public RecordVo getRelationVo(String relation_id)
	{
	
		RecordVo vo = new RecordVo("t_wf_relation");
		try
		{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    vo.setInt("relation_id", Integer.parseInt(relation_id));
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}

	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
}
