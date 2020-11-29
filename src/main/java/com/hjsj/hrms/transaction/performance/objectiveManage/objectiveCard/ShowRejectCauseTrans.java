package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Hashtable;

public class ShowRejectCauseTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			String object_id=(String)this.getFormHM().get("object_id"); 
			String body_id=(String)this.getFormHM().get("body_id");
			ArrayList list=new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());  
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
			{
	         	parameter_content = new LoadXml(this.getFrameconn(),planid);
				BatchGradeBo.getPlanLoadXmlMap().put(planid,parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String SpByBodySeq="False";
			if(params.get("SpByBodySeq")!=null)
				SpByBodySeq=(String)params.get("SpByBodySeq");
			String sql="";
            ObjectCardBo cardBo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView());
            //判断本人是否参与评分
            boolean isSelfScore=cardBo.isSelfScore(planid);

            if("true".equalsIgnoreCase(SpByBodySeq)){
                boolean isNotHaveSelf=false;
                //允许上级制定目标卡时并且本人不参与评分的情况下 上上级驳回给上级
                if((!isSelfScore&&("02".equals(cardBo.getObjectSpFlag())&& "True".equalsIgnoreCase((String)cardBo.getPlanParam().get("allowLeadAdjustCard"))))){
                    isNotHaveSelf=true;
                }
				StringBuffer buf = new StringBuffer("");
				String column="";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					column=" level_o";
				else
					column=" level ";
				buf.append(" select a0101,per_mainbody.sp_seq,mainbody_id,pmbs.name,pmbs.body_id,pmbs."+column+" from per_mainbody left join per_mainbodyset pmbs on per_mainbody.body_id=pmbs.body_id "); 
				buf.append(" where plan_id="+planid);
				buf.append(" and object_id='"+object_id+"' ");
				buf.append(" and per_mainbody.sp_seq<(select sp_seq from per_mainbody ");
				buf.append(" where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"') and nullif(sp_flag,'') is not null");
				buf.append(" and mainbody_id<>'"+object_id+"' ");
				buf.append(" order by per_mainbody.sp_seq ");
                this.frowset=dao.search(buf.toString());
                while(this.frowset.next())
                {
                    CommonData temp=new CommonData(this.frowset.getString("mainbody_id"),this.frowset.getString("a0101")+"("+this.frowset.getString("name")+")");
                    list.add(temp);
                }
				if(isNotHaveSelf){
					//若主体没有本人，那么添加第一级主体到可驳回到的人员列表 zhanghua 2018-09-04
                    buf.setLength(0);
                    buf.append(" select a0101,per_mainbody.sp_seq,mainbody_id,pmbs.name,pmbs.body_id,pmbs."+column+" from per_mainbody left join per_mainbodyset pmbs on per_mainbody.body_id=pmbs.body_id ");
                    buf.append(" where plan_id="+planid);
                    buf.append(" and object_id='"+object_id+"' ");
                    buf.append(" and per_mainbody.sp_seq<(select sp_seq from per_mainbody ");
                    buf.append(" where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"') and nullif(sp_flag,'') is null");
                    buf.append(" and mainbody_id<>'"+object_id+"' ");
                    buf.append(" order by per_mainbody.sp_seq ");

                    this.frowset=dao.search(buf.toString());
                    if(this.frowset.next())
                    {
                        CommonData temp=new CommonData(this.frowset.getString("mainbody_id"),this.frowset.getString("a0101")+"("+this.frowset.getString("name")+")");
                        list.add(temp);
                    }
                }
			}else{
				String _str="level";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str="level_o";
				int level=-100; 
				if(body_id!=null&&body_id.trim().length()>0)
					level=Integer.parseInt(body_id);
				String level_str="";  //5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级 
				if(level==-2)
						level_str="-1,0,1";
				else if(level==-1)
						level_str="0,1";
				else if(level==0)
						level_str="1";
				if(level_str.length()>0)
				{
				    sql="select pm.object_id,pm.mainbody_id,pm.a0101,pms."+_str+",pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id and pm.plan_id="+planid
						+" and pm.object_id='"+object_id+"' and pms."+_str+" in ("+level_str+") and nullif(pm.sp_flag,'') is not null order by pms."+_str+" asc ";  
					this.frowset=dao.search(sql);
					while(this.frowset.next())
					{	
							CommonData temp=new CommonData(this.frowset.getString("mainbody_id"),this.frowset.getString("a0101")+"("+this.frowset.getString("name")+")");
							list.add(temp);	
					}
				}
			}
			if(isSelfScore) {
                sql = "select object_id,a0101 from per_object where plan_id=" + planid + " and object_id='" + object_id + "'";
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    CommonData temp = new CommonData(this.frowset.getString("object_id"), this.frowset.getString("a0101") + "(考核对象)");
                    list.add(temp);
                }
            }
			 
			this.getFormHM().put("rejectObjList",list);
			this.getFormHM().put("rejectObj", object_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
