package com.hjsj.hrms.transaction.general.relation;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class AddRelationTrans extends IBusiness {

	public void execute() throws GeneralException {

		String info = (String) this.getFormHM().get("info");
		RecordVo vo = new RecordVo("t_wf_relation");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap) this.getFormHM();
		HashMap ham=(HashMap)this.getFormHM().get("requestPamaHM");
		String id="";
		if(ham!=null){
		 id=(String) ham.get("relation_id");
		 id = id == null ?"":id;
		}
		
		try
		{
		    if ("editvalidflag".equals(info))
		    {
		    	String validflag = (String)hm.get("validflag");
		    	String relation_id = (String)hm.get("relation_id");
		    	vo.setString("relation_id", relation_id);
		    	vo = dao.findByPrimaryKey(vo);
		    	vo.setString("validflag",validflag );
		    	dao.updateValueObject(vo);
		    	
		    }else if ("editorder".equals(info))
		    {
		    	String relation_id = (String)hm.get("relation_id");
		    	String move = (String)hm.get("move");
		    	vo.setString("relation_id", relation_id);
		    	vo = dao.findByPrimaryKey(vo);
		    	String sql = " select * from  t_wf_relation order by seq ";
		    	this.frowset =dao.search(sql);
		    	HashMap mapid = new HashMap();
		    	HashMap mapnum = new HashMap();
		    	int i = 0;
		    	while(this.frowset.next()){
		    		mapid.put(this.frowset.getString("relation_id"), ""+i);
		    		mapnum.put(""+i, this.frowset.getString("relation_id"));
		    		i++;
		    	}
		    	if(i>0)
		    		i--;
		    	if(mapnum!=null&&mapnum.get("0")!=null){
		    		if("up".equals(move)){
		    			if("0".equals(mapid.get(relation_id))){
		    				throw new GeneralException("已经是第一条记录,不允许上移！");
		    			}
		    			RecordVo vo2 = new RecordVo("t_wf_relation");
		    			vo2.setString("relation_id",""+ mapnum.get((Integer.parseInt(""+mapid.get(relation_id))-1)+""));
		    			vo2 = dao.findByPrimaryKey(vo2);
		    			String voseq = vo.getString("seq");
		    			vo.setString("seq", vo2.getString("seq"));
		    			vo2.setString("seq", voseq);
		    			dao.updateValueObject(vo);
		    			dao.updateValueObject(vo2);
		    			
		    		}else if("down".equals(move)){
		    			if((""+i).equals(mapid.get(relation_id))){
		    				throw new GeneralException("已经是最后一条记录,不允许下移！");
		    			}
		    			RecordVo vo2 = new RecordVo("t_wf_relation");
		    			vo2.setString("relation_id",""+ mapnum.get((Integer.parseInt(""+mapid.get(relation_id))+1)+""));
		    			vo2 = dao.findByPrimaryKey(vo2);
		    			String voseq = vo.getString("seq");
		    			vo.setString("seq", vo2.getString("seq"));
		    			vo2.setString("seq", voseq);
		    			dao.updateValueObject(vo);
		    			dao.updateValueObject(vo2);
		    		}
		    	}
		    	
		    	
		    }
		    else if ("save".equals(info))
		    {
		    	String relationid=(String) hm.get("relation_id");
		    	String errorinfo="";
		    	String str="";
		    	String cname = (String)hm.get("cname");
		    	String actor_type = (String)hm.get("actor_type");
		    	String validflag = (String)hm.get("validflag");
		    	String relying=(String)hm.get("relying");
		    	String default_line=(String)hm.get("default_line");
		    	 cname = SafeCode.decode(cname);
		    	 relying= SafeCode.decode(relying);
		    	 relationid=SafeCode.decode(relationid);
		    	 if("1".equals(default_line)){
		    		 String hql="select * from t_wf_relation where actor_type='"+actor_type+"' and default_line='1'";
		    		 RowSet ros=dao.search(hql);
		    		 while(ros.next()){
		    			 if(relationid.equals(ros.getString("relation_id"))){
		    				 continue;
		    			 }
		    			 if("1".equals(actor_type)){
		    				 str="自助用户";
		    			 }
		    			 if("4".equals(actor_type)){
		    				 str="业务用户";
		    			 }
		    			 errorinfo=str+"只能有一个主汇报关系！";
		    		 }
		    		 
		    	 }
	    		 this.getFormHM().put("errorinfo", errorinfo);
		    	 if(errorinfo.length()>0){
		    		 return;
		    	 }
		    	 if("".equals(relationid)){
		    		 IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			    	 String relation_id="";
			    	 try{
			    	  relation_id = idg.getId("t_wf_relation.relation_id");
			    	 }catch(Exception e){
			    		 String sql = "insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue,auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)" +
			    		 		"values ('t_wf_relation.relation_id', '审批关系号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1) ";
			    		dao.update(sql);
			    		relation_id = idg.getId("t_wf_relation.relation_id");
			    	 }
			    	 int relation= Integer.parseInt(relation_id);
			    	 vo.setString("relation_id", ""+relation);
			    	 vo.setString("relying",relying);
			    	 vo.setString("default_line",default_line);
			    	 vo.setString("create_user", this.getUserView().getUserName());
			    	 vo.setDate("create_time", DateStyle.getSystemTime());
			    	vo.setString("cname", cname);
					vo.setString("actor_type", actor_type);
					vo.setString("validflag", validflag);
					vo.setString("seq", ""+relation);
			    	dao.addValueObject(vo);
		    	}else{
			    	 vo.setString("relation_id", ""+relationid);
			    	 vo.setString("relying",relying);
			    	 vo.setString("default_line",default_line);
			    	 vo.setString("create_user", this.getUserView().getUserName());
			    	 vo.setDate("create_time", DateStyle.getSystemTime());
			    	vo.setString("cname", cname);
					vo.setString("actor_type", actor_type);
					vo.setString("validflag", validflag);
					vo.setString("seq", ""+relationid);
			    	dao.updateValueObject(vo);
		    	}
		    	 
		    	
		    } else if ("saverename".equals(info))
		    {
		    	String cname = (String)hm.get("cname");
		    	 cname = SafeCode.decode(cname);
		    	 String relation_id=(String)hm.get("relation_id");
		    	 vo.setString("relation_id", ""+relation_id);
		    	 vo = dao.findByPrimaryKey(vo);
		    	vo.setString("cname", cname);
		    	dao.updateValueObject(vo);
		    	
		    }  else if ("saveas".equals(info))
		    {
//		    	String default_line=(String)hm.get("default_line");
//		    	String errorinfo="";
//		    	if(default_line.equals("1")){
//		    		errorinfo="不能另存主汇报关系！";
//		    		this.getFormHM().put("errorinfo", errorinfo);
//		    		return;
//		    	}
		    	String cname = (String)hm.get("cname");
		    	 cname = SafeCode.decode(cname);
		    	 String relation_id=(String)hm.get("relation_id");
		    	 vo.setString("relation_id", ""+relation_id);
		    	 vo = dao.findByPrimaryKey(vo);
		    	 RecordVo vo2 = new RecordVo("t_wf_relation");
		    	 IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		    	 String relation_id0 = idg.getId("t_wf_relation.relation_id");
		    	 vo2.setString("relation_id", ""+relation_id0);
		    	 
		    	 vo2.setString("create_user", this.getUserView().getUserName());
		    	 vo2.setDate("create_time", DateStyle.getSystemTime());
		    	vo2.setString("cname", cname);
				vo2.setString("actor_type", vo.getString("actor_type"));
				vo2.setString("validflag", vo.getString("validflag"));
				vo2.setString("seq", ""+relation_id0);
				vo2.setString("relying", vo.getString("relying"));
				vo2.setString("default_line", "1".equals(vo.getString("default_line"))?"":"0");
		    	dao.addValueObject(vo2);
		    	RecordVo vo3 = new RecordVo("t_wf_mainbody");//另存 把审批关系也带过去
		    	String Object_id="";
		    	String Mainbody_id="";
//		    	String sql="select * from t_wf_mainbody where relation_id='"+relation_id+"' and actor_type='"+vo.getString("actor_type")+"'";
//		    	ResultSet rs=dao.search(sql);
//		    	while(rs.next()){
//		    		Object_id=rs.getString("Object_id");
//		    		Mainbody_id=rs.getString("Mainbody_id");
//		    		vo3.setString("object_id", Object_id);
//		    		vo3.setString("mainbody_id", Mainbody_id);
//		    		vo3.setString("relation_id", relation_id);
//		    		vo3=dao.findByPrimaryKey(vo3);
//		    		vo3.setString("relation_id", relation_id0);
//		    		dao.addValueObject(vo3);
//		    	}
		    	String sql="insert into t_wf_mainbody (Relation_id,Object_id,Mainbody_id,Actor_type,B0110,E0122,E01A1,A0101,SP_GRADE,GROUPID,CREATE_TIME,CREATE_user,MOD_TIME,MOD_user) select "+Integer.parseInt(relation_id0)+", Object_id,Mainbody_id,Actor_type,B0110,E0122,E01A1,A0101,SP_GRADE,GROUPID,CREATE_TIME,CREATE_user,MOD_TIME,MOD_user from t_wf_mainbody where  relation_id='"+relation_id+"' and actor_type='"+vo.getString("actor_type")+"'";
		    	dao.insert(sql, new ArrayList());
		    	
		    }else  if ("delete".equals(info))
		    {
		    	String deletestr = (String)hm.get("deletestr");
		    	if(deletestr!=null&&deletestr.indexOf("/")!=-1){
		    		String deletestrtemp[] = deletestr.split("/");
		    		for(int i =0; i<deletestrtemp.length;i++){
		    			if(deletestrtemp[i].trim().length()>0){
		    				dao.delete(" delete from t_wf_relation where relation_id="+deletestrtemp[i].trim()+"", new ArrayList());
		    				dao.delete(" delete from t_wf_mainbody where relation_id="+deletestrtemp[i].trim()+"", new ArrayList());
		    			}
		    		}
		    		
		    	}
		    	
		    }
		    else
		    {
		    if(!"".equals(id)){
		    	vo.setString("relation_id", id);
		    	vo=dao.findByPrimaryKey(vo);
		    	this.getFormHM().put("checkrelationvo", vo);
		    	ArrayList relyingList=new ArrayList();
		    	String actor_type=vo.getString("actor_type");
				this.frowset=dao.search("select * from t_wf_relation where validflag=1 and relation_id<>'"+id+"' and Actor_type='"+actor_type+"'   order by seq");
				CommonData temp = new CommonData("0", "");
				relyingList.add(temp); 
				while(this.frowset.next())
				{
					temp = new CommonData(this.frowset.getString("relation_id"),this.frowset.getString("cname"));
					relyingList.add(temp);
				} 
				
				/*
				String sql="select * from t_wf_relation where validflag=1 and Actor_type='"+actor_type+"' and default_line=1 ";
				ResultSet rs=null;
				rs=dao.search(sql);
				while(rs.next()){
					temp = new CommonData("-1", "default");
					relyingList.add(temp);
				}*/
				this.getFormHM().put("relyingList",relyingList);
		    }else{
				vo.setString("cname", "");
				vo.setString("actor_type", "");
				vo.setString("validflag", "");
				
				ArrayList relyingList=new ArrayList();
				this.frowset=dao.search("select * from t_wf_relation where validflag=1 and Actor_type=1  order by seq");
				CommonData temp = new CommonData("0", "");
				relyingList.add(temp); 
				while(this.frowset.next())
				{
					temp = new CommonData(this.frowset.getString("relation_id"),this.frowset.getString("cname"));
					relyingList.add(temp);
				} 
				
				/*
				String sql="select * from t_wf_relation where validflag=1 and Actor_type=1  and default_line=1 ";
				ResultSet rs=null;
				rs=dao.search(sql);
				while(rs.next()){
					temp = new CommonData("-1", "default");
					relyingList.add(temp);
				}
				*/
				this.getFormHM().put("relyingList",relyingList);
				this.getFormHM().put("isDefault","0");
				this.getFormHM().put("relyingId","");
				this.getFormHM().put("checkrelationvo", vo);
		    }

		    }

		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		} finally
		{
		   // this.getFormHM().put("checkrelationvo", vo);
		}
	    
	}

	
	
}
