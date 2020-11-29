package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowTaskDescribeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operator=(String)hm.get("operator");   //  new ; edit
			String p0400=(String)hm.get("p0400");
			String planid=(String)this.getFormHM().get("planid");
			String object_id=(String)this.getFormHM().get("object_id");
			String opt=(String)this.getFormHM().get("opt");
			String model=(String)this.getFormHM().get("model");
			String editCardSp=(String)hm.get("editCardSp"); // 目标卡制订参数
			hm.remove("editCardSp");
			if(editCardSp==null || editCardSp.trim().length()<=0)
				editCardSp = "noEdit";
			
			ArrayList list=(ArrayList) DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET).clone();
			
			ArrayList taskDescribeList=new ArrayList();
		//	ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),Integer.parseInt(opt),model);
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,"5",opt);
			if(bo.isOpenGrade_Members()){
				FieldItem field=new FieldItem();
				field.setItemid("rater");
				field.setItemdesc("评价人");
				field.setItemtype("A");
				list.add(field);
			}
			
			HashMap numberMap=(HashMap)this.getFormHM().get("numberMap");
			bo.setNumberMap(numberMap);
			bo.setEditCardSp(editCardSp);
			ArrayList _list=new ArrayList();
			ArrayList leafItemList=(ArrayList)this.getFormHM().get("leafItemList");
			for(int i=0;i<leafItemList.size();i++)
			{
				LazyDynaBean _bean=(LazyDynaBean)leafItemList.get(i);
				String item_id=(String)_bean.get("item_id");
				if(bo.getItemPrivMap()!=null&&bo.getItemPrivMap().get(item_id)!=null&& "0".equals((String)bo.getItemPrivMap().get(item_id))) //&&(bo.getPlan_vo().getInt("status")==4||bo.getPlan_vo().getInt("status")==6)) //20141119 dengcan
				{
					
				}
				else
					_list.add(_bean);
			}
			bo.setLeafItemList((ArrayList)this.getFormHM().get("leafItemList"));
			this.getFormHM().put("leafItemList", _list);
			String itemtype="0";
			if(hm.get("itemtype")!=null)
			{
				itemtype=(String)hm.get("itemtype");
				hm.remove("itemtype");
			}
			ArrayList attachList=new ArrayList();  //任务附件
			String isTraceOrMust="0";
			if("new".equals(operator))
			{
				if("True".equalsIgnoreCase((String)bo.getPlanParam().get("TaskSupportAttach"))) //任务支持附件上传
	            {
	            	isTraceOrMust = bo.isTraceOrMust();
	            }
				String a_p0400="";
				if(hm.get("a_p0400")!=null&&((String)hm.get("a_p0400")).length()>0)
				{
					a_p0400=(String)hm.get("a_p0400");
				}
		/*		else if(this.getFormHM().get("a_p0400")!=null&&((String)this.getFormHM().get("a_p0400")).length()>0)
				{
					a_p0400=(String)this.getFormHM().get("a_p0400");
				}*/
				String itemid=(String)hm.get("itemid");
				taskDescribeList=bo.getTaskDescribeList(list,planid,object_id,"Usr","",String.valueOf(bo.getPlan_vo().getInt("object_type")),itemid);
				this.getFormHM().put("a_p0400", a_p0400);
				hm.remove("a_p0400");
				hm.remove("itemid");
				this.getFormHM().put("itemKind", "2");
				this.getFormHM().put("fromflag", "1");
				this.getFormHM().put("objectCardGradeMembersRater", "");
			}
			else if("edit".equals(operator))
			{
				String index="0";
				if(hm.get("index")!=null)
					index=(String)hm.get("index");
				int ind=Integer.parseInt(index);
				
				ArrayList editableTaskList=bo.getEditTableTask2((ArrayList)this.getFormHM().get("editableTaskList"));
				if(ind==editableTaskList.size()-1)
					ind=0;
				else
					ind=ind+1;
				
				String _p0400=hm.get("index")!=null?((CommonData)editableTaskList.get(ind)).getDataValue():p0400;
				
				taskDescribeList=bo.getTaskDescribeList(list,planid,object_id,"Usr",(hm.get("index")!=null?((CommonData)editableTaskList.get(ind)).getDataValue():p0400),String.valueOf(bo.getPlan_vo().getInt("object_type")),"");
				
				
				if(bo.isOpenGrade_Members()){//新增修改页面开始多人评分 zhanghua
					ArrayList<String> tlist =new ArrayList<String>();
					tlist.add(_p0400);
					HashMap map=bo.getCardGradeMembersA0101Map(tlist);
					if(map.containsKey(PubFunc.encrypt(_p0400))){
						ArrayList<String> strNamelist=(ArrayList<String>)map.get(PubFunc.encrypt(_p0400));
						StringBuffer strName=new StringBuffer();
						for(String str :strNamelist){
							strName.append(str+",");
						}
						strName.deleteCharAt(strName.length()-1);
						this.getFormHM().put("objectCardGradeMembersRater", strName.toString());
					}else{
						this.getFormHM().put("objectCardGradeMembersRater","");
					}
				}
				
				this.getFormHM().put("itemKind",bo.getItemKind((hm.get("index")!=null?((CommonData)editableTaskList.get(ind)).getDataValue():p0400)));
	            this.getFormHM().put("editableTaskList",editableTaskList);
	            this.getFormHM().put("p0400", hm.get("index")!=null?((CommonData)editableTaskList.get(ind)).getDataValue():p0400);
	            int p400=Integer.parseInt((hm.get("index")!=null?((CommonData)editableTaskList.get(ind)).getDataValue():p0400));
	            RecordVo vo = new RecordVo("p04");
	            ContentDAO dao=new ContentDAO(this.getFrameconn());
	            vo.setInt("p0400", p400);
	            String fromflag="";
	            if(dao.isExistRecordVo(vo))
	            {
	            	 vo=dao.findByPrimaryKey(vo);
	            	 itemtype=vo.getString("itemtype");
	            	 if(itemtype==null|| "".equals(itemtype))
	            		 itemtype="0";
	            	 fromflag=vo.getString("fromflag");
	            }
	            
	            hm.remove("index");
	            if("True".equalsIgnoreCase((String)bo.getPlanParam().get("TaskSupportAttach"))) //任务支持附件上传
	            {
	            	isTraceOrMust = bo.isTraceOrMust();
	            	String a_objectID=object_id;
	    			if(bo.getPlan_vo().getInt("object_type")==1||bo.getPlan_vo().getInt("object_type")==3||bo.getPlan_vo().getInt("object_type")==4)
	    			{
	    				a_objectID=bo.getUn_functionary();  
	    			} 
	            	StringBuffer strsql=new StringBuffer("select * from per_article  where plan_id="+planid+" and a0100='"+a_objectID+"' " );
	    			strsql.append(" and lower(nbase)='usr' and task_id="+_p0400+"  and article_type=3 order by Article_id");  
	    			this.frowset=dao.search(strsql.toString());
	    			while(this.frowset.next())
	    			{
	    				if(this.frowset.getInt("fileflag")==2)  //附件
	    				{
	    					LazyDynaBean abean=new LazyDynaBean();
	    					abean.set("id", this.frowset.getString("Article_id"));
	    					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
	    					attachList.add(abean);
	    				}
	    			}
	            }
	            
	            
	            this.getFormHM().put("fromflag", fromflag);
	            
			}
			this.getFormHM().put("attachList",attachList);
			this.getFormHM().put("itemtype",itemtype);
			this.getFormHM().put("taskDescribeList",taskDescribeList);
			this.getFormHM().put("isTraceOrMust", isTraceOrMust);
			this.getFormHM().put("planStatus",String.valueOf(bo.getPlan_vo().getInt("status")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
