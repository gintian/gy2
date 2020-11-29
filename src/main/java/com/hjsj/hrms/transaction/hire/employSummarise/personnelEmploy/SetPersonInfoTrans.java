package com.hjsj.hrms.transaction.hire.employSummarise.personnelEmploy;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 3020071029
 * <p>Title:SetPersonInfoTrans.java</p>
 * <p>Description>:SetPersonInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 8, 2009 3:07:05 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SetPersonInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String nbase=(String)this.getFormHM().get("nbase");//源库前缀
			ArrayList list = (ArrayList)this.getFormHM().get("a0100s");
			String type=(String)this.getFormHM().get("type");
   	    	String state=(String)this.getFormHM().get("state");
   	    	String id=(String)this.getFormHM().get("id");//目标库前缀
   	    	String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			if(map!=null&&map.get("resume_state")!=null)
				resume_state_field=(String)map.get("resume_state");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList updateList = new ArrayList();
			ArrayList nbaseA0100List=new ArrayList();
			ArrayList beanList=new ArrayList();
			String mess="";
			ArrayList a0100s = new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				//参数解密
				String str = (String)list.get(i);
				String nbases = str.substring(0, 3);
				String A0100_str = PubFunc.decrypt(str.substring(3));
				String A0100=nbases+A0100_str;
				a0100s.add(A0100);
				nbaseA0100List.add(A0100.substring(0, 3)+"`"+A0100.substring(3)+"`"+id);
				if("43".equals(state))
				{
		    		this.frowset=dao.search("select a0101 from "+nbase+"a01 where a0100='"+A0100.substring(3)+"' and "+resume_state_field+"='43'");
			    	while(this.frowset.next())
			     	{
			    		throw GeneralExceptionHandler.Handle(new Exception(this.frowset.getString("a0101")+" 已被录用，不能再次录用!"));
			    	}
				}
				
				
				
				RowSet rowSet=dao.search("select z03.z0321,z03.Z0325,z03.z0311,zp_pos_tache.nbase from zp_pos_tache,z03 where z03.z0301=zp_pos_tache.zp_pos_id  and zp_pos_tache.resume_flag='12'  and a0100='"+A0100.substring(3)+"'");
				String z0321="";
				String z0325="";
				String z0311="";
				if(rowSet.next())
				{
					z0321=rowSet.getString("z0321")==null?"":rowSet.getString("z0321");//需求单位
					z0325=rowSet.getString("z0325")==null?"":rowSet.getString("z0325");//需求部门
					z0311=rowSet.getString("z0311")==null?"":rowSet.getString("z0311");//需求岗位
				}
				RecordVo vo = new RecordVo(nbase+"a01");
				vo.setString("a0100",A0100.substring(3));
				vo = dao.findByPrimaryKey(vo);
				if(!"".equals(z0321))
					vo.setString("b0110",z0321);
				if(!"".equals(z0325))
					vo.setString("e0122",z0325);
				if(!"".equals(z0311))
					vo.setString("e01a1",z0311);
				updateList.add(vo);	
			}
			dao.updateValueObject(updateList);
			//不走人事异动 需编制控制
			if("dbname".equalsIgnoreCase(type)){
				 ScanFormationBo scanFormationBo=new ScanFormationBo(this.frameconn,this.userView);
				 if(scanFormationBo.doScan()){
				 		if(scanFormationBo.needDoScan(id,"All")){
				 			beanList=scanFormationBo.getMoveAddPersonData(nbaseA0100List);
				 			scanFormationBo.execDate2TmpTable(beanList);
				 		    mess=scanFormationBo.isOverstaffs();
				 		if(!"ok".equals(mess)){
				 				if("warn".equals(scanFormationBo.getMode())){
				 					this.getFormHM().put("mess",mess);
				 				}else{
				 					throw GeneralExceptionHandler.Handle(new Exception(mess));
				  				}
				  			}
				  		}
				  }
			}
			this.getFormHM().put("state", state);
			this.getFormHM().put("type",type);
			this.getFormHM().put("id",id);
			this.getFormHM().put("a0100s", a0100s);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
