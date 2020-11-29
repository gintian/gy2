/**
 * 
 */
package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
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

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

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
	@Override
    public void execute() throws GeneralException
	{
		String tabid = (String)this.getFormHM().get("tab_id");
		TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabid));
		TemplateParam paramBo = templateBo.getParamBo();
		ArrayList templist = new ArrayList();
		
		// 判断审批方式为"手工指派"并且指定了某审批关系
		String signLogo = "noHand";
		String sp_mode = "";  // 0表示自动流转 1表示手工指派
		String relation_id = "";
		sp_mode = paramBo.getSp_mode()+"";
		relation_id = paramBo.getRelation_id();
		
		if((sp_mode!=null && sp_mode.trim().length()>0 && "1".equalsIgnoreCase(sp_mode)) && (relation_id!=null && relation_id.trim().length()>0))
		{//如果是手动流转，且有审批关系
			String actor_type ="1";
			if ("gwgx".equalsIgnoreCase(relation_id)){
			}
			else {
				RecordVo relationVo = getRelationVo(relation_id);
				actor_type = relationVo.getString("actor_type"); // 审批关系类型 1:自助 4:业务
			}
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
				if ("gwgx".equalsIgnoreCase(relation_id)){
					templist=getGwDescList();	
				}
				else {
					//bug 40316 当业务用户关联自助用户，且设置的审批关系是业务审批关系时候，查询的是业务关联的自助用户的审批关系，导致无法查出设置的直接领导等，按照手工选人选择下一个报批人。
					if("1".equals(actor_type)){
						templist = getMainbodyDescList(relation_id,(this.userView.getDbname()+this.userView.getA0100()));
					}else if("4".equals(actor_type)){
						templist = getMainbodyDescList(relation_id,"");
					}
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
		this.getFormHM().put("rolelist",templist); //包含 人员、角色、用户、组织机构
		this.getFormHM().put("emergencylist",AdminCode.getCodeItemList("37"));
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
	

	private ArrayList getGwDescList()
	{		
		ArrayList tempList = new ArrayList();
		try
		{		
			WorkflowBo wkbo=new WorkflowBo(this.getFrameconn(),this.userView);
			LazyDynaBean _abean=new LazyDynaBean();
			if (this.userView.getUserPosId()==null || this.userView.getUserPosId().length()<1 ){
				return tempList;
			}
			_abean.set("type","@K");
			_abean.set("value",this.userView.getUserPosId());
			_abean.set("from_nodeid","reportNode"); 
			ArrayList list=wkbo.getSuperPos_userList(_abean,"human","9");	
		    for (int i=0;i<list.size();i++){
                LazyDynaBean abean=(LazyDynaBean)list.get(i);
                String actor_type=(String)abean.get("actor_type"); //1 自助用户  4：业务用户
                String userId=(String)abean.get("mainbodyid");
                String a0101=(String)abean.get("a0101");
                String displayName="";
                if ("1".equals(actor_type)){
                	String b0110=(String)abean.get("b0110");
                	String e0122=(String)abean.get("e0122");
                	String e01a1=(String)abean.get("e01a1");
                	displayName=b0110+"/"+e0122+"/"+e01a1+"/"+a0101;   
                	
                }
                else {
                	displayName=(String)abean.get("groupname")+"/"+userId;
                	if (!userId.equals(a0101) && a0101!=null && a0101.length()>0){
                		displayName=displayName+"("+a0101+")";
                	}
                }
                
                CommonData temp = new CommonData(PubFunc.encrypt(userId)+"`"+a0101+"`"+actor_type, displayName);
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
				
				CommonData temp = new CommonData(PubFunc.encrypt(mainbody_id)+"`"+a0101+"`"+actor_type, gradedesc);
				tempList.add(temp);		    				
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}			
		return tempList;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
}
