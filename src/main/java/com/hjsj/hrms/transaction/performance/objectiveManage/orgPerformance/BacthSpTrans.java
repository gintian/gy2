package com.hjsj.hrms.transaction.performance.objectiveManage.orgPerformance;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 批量审批目标卡
 * <p>Title:BacthSpTrans.java</p>
 * <p>Description>:BacthSpTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 3, 2011  4:54:29 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class BacthSpTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String ids=(String)this.getFormHM().get("ids");
			String url_p=(String)this.getFormHM().get("url_p");
			String isEmail=(String)this.getFormHM().get("isEmail");
			String[] arr=ids.replaceAll("／", "/").split("/");
			ArrayList dataList = new ArrayList();
			String info = "";
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				String[] temp = arr[i].split("`");
				
				
			    temp[0]=PubFunc.decrypt(temp[0].trim());
			    temp[1]=PubFunc.decrypt(temp[1].trim());
                
				String plan_id=temp[0];
				String object_id=temp[1];
				String sp_flag=temp[2];//批量审批，只能操作已报批的考核对象
				String curruser=temp[3];
				String level=temp[4];
				String statu=temp[5];
				if("02".equalsIgnoreCase(sp_flag)&&this.userView.getA0100().equals(curruser)&&!"5".equals(statu))//已报批状态，并且当前操作用户是自己,并且不是暂停的计划才可以进行批量审批
				{
					ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView());
					RecordVo perObject_vo=bo.getPerObject_vo();
					
					HashMap dynaMap=null;
					if(perObject_vo!=null&&perObject_vo.getString("body_id")!=null&&!"".equals(perObject_vo.getString("body_id")))
					{
						LoadXml loadXml = new LoadXml();
						dynaMap = loadXml.getDynaItem(bo.getPlan_vo().getInt("plan_id")+"", this.getFrameconn(),perObject_vo.getString("body_id"));
					}
					if(dynaMap!=null&&dynaMap.size()>0)
						bo.initData();  //初始化一些数据，为加扣分项目作判断
					bo.setOptCardObject(level);
					String flag = bo.getIsApproveFlag(dataList);
					ArrayList pointList = bo.getCommonTask(3);
					ArrayList itemPointInfo=bo.getItemPointInfo();
					/**规则校验*/
					if(info.length()==0)
					{
						info=bo.validateRule(null);
						if(info.length()==0)
						{
							if("true".equalsIgnoreCase(bo.getIsLimitPointValue()))
							{
								info=bo.validateIsLimit(null);
							}
						}
					}
					/**必填校验*/
					if(info.length()==0)
		    		{
		    			info=bo.validateFollowPointMustFill(2);
		    		}
					/**报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)**/
					if(bo.getPlanParam().get("ProcessNoVerifyAllScore")!=null && "true".equalsIgnoreCase((String)bo.getPlanParam().get("ProcessNoVerifyAllScore")))
					{
						if(info.length()==0)
			    		{
			    	    	//if((pointList==null||pointList.size()==0)||(((String)bo.getPlanParam().get("taskAdjustNeedNew")).equalsIgnoreCase("False")&&bo.getIsAdjustPoint()&&bo.getObjectSpFlag().equalsIgnoreCase("07")))
			    		    	info=bo.validateTaskScore();
				    	   // else
				    	    	//info=bo.validateTask(pointList,itemPointInfo,1);
			    		}
					}
					if(info.length()>0)
		      		{
						String tp = "【"+bo.getPlan_vo().getString("name")+"】计划下的考核对象【";
						if(bo.getPlan_vo().getInt("object_type")==2)
						{
							ContentDAO dao  = new ContentDAO(this.getFrameconn());
							this.frowset=dao.search("select a0101 from usra01 where a0100='"+object_id+"'");
							while(this.frowset.next())
							{
						    	tp+=this.frowset.getString("a0101")+"】";
							}
						}
						else{
							if(AdminCode.getCodeName("UN",object_id)!=null&&AdminCode.getCodeName("UN",object_id).length()>0)
								tp+=AdminCode.getCodeName("UN",object_id)+"】";
							else 
								tp+=AdminCode.getCodeName("UM",object_id)+"】";
						}
						tp+="\r\n"+info;
						this.getFormHM().put("info", SafeCode.encode(tp));
						return;
		    		}
					
				}
				else{
					continue;
				}
			}
			
			if(info.length()==0&&dataList.size()>0)
			{
				/*bean.set("plan_id",this.plan_vo.getInt("plan_id")+"");
				bean.set("object_id", object_id);
				bean.set("str",str);
				bean.set("flag",flag);
				//b0110+"^"+e0122+"^"+e01a1+"^"+a0101+"^"+amainbody_id+"/"+body_id*/
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
					String plan_id = (String)bean.get("plan_id");
					String object_id=(String)bean.get("object_id");
					String flag = (String)bean.get("flag");
					String str=(String)bean.get("str");
					String level = (String)bean.get("level");
					ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView());
					RecordVo perObject_vo=bo.getPerObject_vo();
					
					HashMap dynaMap=null;
					if(perObject_vo!=null&&perObject_vo.getString("body_id")!=null&&!"".equals(perObject_vo.getString("body_id")))
					{
						LoadXml loadXml = new LoadXml();
						dynaMap = loadXml.getDynaItem(bo.getPlan_vo().getInt("plan_id")+"", this.getFrameconn(),perObject_vo.getString("body_id"));
					}
					if(dynaMap!=null&&dynaMap.size()>0)
						bo.initData();  //初始化一些数据，为加扣分项目作判断
					bo.setIsEmail(isEmail);
					if("0".equals(flag))//报批，自动找第一个主体
					{
						String temp = str.substring(str.lastIndexOf("^")+1);
						bo.setOptCardObject(level);
						bo.appealSpObject(object_id,temp,this.userView.getA0100(),plan_id,"Usr",url_p);
					}
					else{//批准
						bo.setOptCardObject(level);
						 bo.approveSpObject(object_id,this.userView.getA0100(),plan_id,"Usr");
				    	 bo.optPersonalComment("2");
					}
				}
				this.getFormHM().put("info", "0");
			}else if(info.length()==0&&dataList.size()==0)
			{
				this.getFormHM().put("info", "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
