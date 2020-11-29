package com.hjsj.hrms.transaction.hire.zp_options.statesate;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.itemstat.InitHireStatBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class StateStatTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String returnflag="";
		if(reqhm.get("returnflag")!=null)
		{
			returnflag=(String)reqhm.get("returnflag");
		}
		else
		{
			returnflag=(String)this.getFormHM().get("returnflag");
		}
		this.getFormHM().put("returnflag",returnflag==null?"":returnflag);
		InitHireStatBo ihsb=new InitHireStatBo();
		String zp_fullname="";
		String startime="";
		String endtime="";
		String zp_pos_id="";
		RecordVo z03vo=new RecordVo("Z03");
		ArrayList retlist=new ArrayList();
		String codeitemid="";
		ArrayList poslist=new ArrayList();
		String zpstatefield=ihsb.getStateField(this.frameconn);
		//ResourceFactory b = null;
		if(zpstatefield==null|| "".equals(zpstatefield))
		{
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.zp_total.state")));
		}
		try{
		if(reqhm.containsKey("init")){
			reqhm.remove("init");
			zp_fullname=(String) hm.get("zp_fullname");
			reqhm.remove("zp_fullname");
			reqhm.remove("zp_name");
			startime=(String) reqhm.get("startime");
			endtime=(String) reqhm.get("endtime");
			zp_pos_id=(String) reqhm.get("zp_pos_id");
			//2014.11.24 xxd 特殊字符还原
			zp_pos_id = PubFunc.keyWord_reback(zp_pos_id);
			if(zp_pos_id!=null&&zp_pos_id.length()>0){
				z03vo.setString("z0301",zp_pos_id);
				try {
					z03vo=dao.findByPrimaryKey(z03vo);
				} catch (Exception e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			
			if(endtime==null){
				endtime=z03vo.getString("z0331").toString();
			}
			if(startime==null){
				startime=z03vo.getString("z0329").toString();
			}
			
			}else{
				zp_pos_id="";
				zp_fullname="";
			}
			reqhm.remove("startime");
			reqhm.remove("endtime");
			reqhm.remove("zp_pos_id");
			String dbpre=ihsb.getZpDbname(dao);
//			System.out.println("hhh");
		
			String updatesql="update "+dbpre+"A01 set "+zpstatefield+"='10' where "+zpstatefield+" is null";
			dao.update(updatesql);
			retlist=(ArrayList) ihsb.statStateResult("","",zp_pos_id,codeitemid,dao,this.getFrameconn());
			poslist=ihsb.getZposlist(dao,zp_pos_id,this.getFrameconn());
		}else{
			codeitemid=(String) hm.get("codeitemid");
			startime=(String) hm.get("startime");
			endtime=(String) hm.get("endtime");
			zp_pos_id=(String) hm.get("zp_pos_id");
			//2014.11.24 xxd 特殊字符还原
			zp_pos_id = PubFunc.keyWord_reback(zp_pos_id);
			zp_fullname=(String) hm.get("zp_fullname");
			if(zp_pos_id.length()>0){
				zp_pos_id=zp_pos_id.split("/")[0];
			z03vo.setString("z0301",zp_pos_id);
			try {
				z03vo=dao.findByPrimaryKey(z03vo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			if(endtime==null){
				endtime=z03vo.getString("z0331").toString();
			}
			if(startime==null){
				startime=z03vo.getString("z0329").toString();
			}
			}else{
				zp_fullname="";
			}
			 retlist=(ArrayList) ihsb.statStateResult("","",zp_pos_id,codeitemid,dao,this.getFrameconn());
			 poslist=ihsb.getZposlist(dao,zp_pos_id,this.getFrameconn());
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		int j=0;
		for(int i=0;i<retlist.size();i++)
		{
			CommonData cd=(CommonData)retlist.get(i);
			if(cd.getDataValue()!=null&&("0".equals(cd.getDataValue())|| "0.0".equals(cd.getDataValue())|| "0.00".equals(cd.getDataValue())))
			{
				j++;
				continue;
			}
		}
		ArrayList alist = new ArrayList();
		if(j==retlist.size())
		{
		}
		else
		{
			alist=retlist;
		}
		ArrayList codelist=ihsb.getState(dao,codeitemid);
		hm.put("zp_pos_id",zp_pos_id);
		hm.put("codelist",codelist);
		hm.put("zp_poslist",poslist);
		hm.put("zp_fullname",zp_fullname);
		hm.put("codeitemid",codeitemid);
		hm.put("retlist",retlist);
		hm.put("alist",alist);
		hm.put("startime",startime);
		hm.put("endtime",endtime);
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
		hm.put("schoolPosition", schoolPosition);
	}

}
