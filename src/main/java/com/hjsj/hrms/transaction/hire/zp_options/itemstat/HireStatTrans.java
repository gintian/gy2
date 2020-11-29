package com.hjsj.hrms.transaction.hire.zp_options.itemstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.itemstat.InitHireStatBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.*;
/**
 * 
 * @author yxc  2007-4-29
 * 
 */
public class HireStatTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String returnflag="";
		if(reqhm.get("returnflag")!=null)
			returnflag=(String)reqhm.get("returnflag");
		else
			returnflag=(String)this.getFormHM().get("returnflag");
		this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		InitHireStatBo ihsb=new InitHireStatBo();
		
		ArrayList retlist=new ArrayList();
		ArrayList itemlist=new ArrayList();
		String startime="";
		String endtime="";
		String zp_fullname="";
		String zp_pos_id="";
		itemlist=ihsb.getStatItem(dao);
		RecordVo z03vo=new RecordVo("Z03");
		Calendar calendar  = Calendar.getInstance();
		if(reqhm.containsKey("init")){
			if("1".equals(reqhm.get("init"))){//统计分析进入
				zp_fullname=(String) hm.get("zp_fullname");
			}else if("2".equals(reqhm.get("init"))){//招聘岗位 简历质量进入
				zp_fullname=SafeCode.decode((String) reqhm.get("zp_fullname"));
			}
//			初始化页面
			reqhm.remove("init");
			
			reqhm.remove("zp_fullname");
			reqhm.remove("zp_name");
			startime=(String) reqhm.get("startime");
			endtime=(String) reqhm.get("endtime");
			if(startime==null){
				startime=this.getInitstartime();
			}
			if(endtime==null){
				endtime=String.valueOf(calendar.get(Calendar.YEAR))+"-";
				if((calendar.get(Calendar.MONTH)+1)<10)
					endtime=endtime+"0";
				endtime=endtime+String.valueOf(calendar.get(Calendar.MONTH)+1)+"-";
				if(calendar.get(Calendar.DATE)<10)
					endtime = endtime+"0";
				endtime=endtime+String.valueOf(calendar.get(Calendar.DATE));
			}
			if(startime==null|| "".equals(startime))
			{
				calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR)-30);
				startime=String.valueOf(calendar.get(Calendar.YEAR))+"-";
				if((calendar.get(Calendar.MONTH)+1)<10)
					startime=startime+"0";
				startime=startime+String.valueOf(calendar.get(Calendar.MONTH)+1)+"-";
				if(calendar.get(Calendar.DATE)<10)
					startime = startime+"0";
				startime=startime+String.valueOf(calendar.get(Calendar.DATE));
			}
			String pos=(String) reqhm.get("pos");
			reqhm.remove("pos");
			if(pos!=null){
				hm.put("pos",pos);
			}else{
				hm.put("pos","");
			}
			zp_pos_id = (String) reqhm.get("zp_pos_id");
			String mark = (String) reqhm.get("mark");
			if(!"no".equals(mark)){				
				zp_pos_id=PubFunc.decrypt(zp_pos_id);
			}
			/**zp_pos_id ==z0301,已经加密了,解密回来**/
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
				zp_fullname="";
			}
			reqhm.remove("startime");
			reqhm.remove("endtime");
			reqhm.remove("zp_pos_id");
		}else{
//			查询页面
			String pos=(String) hm.get("pos");
			hm.put("pos",pos);
			startime=(String) hm.get("startime");
			endtime=(String) hm.get("endtime");
			zp_pos_id=(String) hm.get("zp_pos_id");
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
		}

		retlist=(ArrayList) ihsb.statItemResult(startime,endtime,zp_pos_id,itemlist,dao);
		List showlist=dealRetlist(retlist);
		hm.put("itemlist",itemlist);
		hm.put("zp_fullname",zp_fullname==null?"":zp_fullname);
		hm.put("startime",startime);
		hm.put("endtime",endtime);
		hm.put("retlist",showlist);
		if(zp_pos_id==null)
			zp_pos_id="";
		ArrayList zp_poslist=ihsb.getZposlist(dao,zp_pos_id,this.getFrameconn());
		hm.put("zp_pos_id",zp_pos_id);
		hm.put("zp_poslist",zp_poslist);
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
		HashMap pmap=xmlBo.getAttributeValues();
		String schoolPosition="";
		if(pmap.get("schoolPosition")!=null&&((String)pmap.get("schoolPosition")).length()>0)
			schoolPosition=(String)pmap.get("schoolPosition");
		hm.put("schoolPsoition", schoolPosition);
	}
	public List dealRetlist(ArrayList retlist){
		List myList=new ArrayList();
		Map[] temp=(Map[]) retlist.get(1);
		List itemlist=(ArrayList) retlist.get(0);
		for(int i=0;i<temp.length;i++){
			List showlist=new ArrayList();
			FieldItem fi=(FieldItem) itemlist.get(i);
			for(Iterator it=temp[i].keySet().iterator();it.hasNext();){			
				String codeid=fi.getCodesetid();
				String codeitemid=(String) it.next();
				String codename=AdminCode.getCodeName(codeid,codeitemid);
				String value=(String) temp[i].get(codeitemid);
				if(codename!=null&&codename.length()>0){
					CommonData dataobj = new CommonData(value,codename);
					showlist.add(dataobj);
				}else{
					CommonData dataobj = new CommonData(value,codeitemid);
					showlist.add(dataobj);
				}
			}
			myList.add(showlist);
		}
		return myList;
	}
	private String getInitstartime() throws GeneralException{
		String startime="";
		String sql="select min(z0329) as z0329 from Z03";
		List retlist=(new ContentDAO(this.getFrameconn()).searchDynaList(sql));
		if(retlist.size()>0){
			DynaBean d=(DynaBean) retlist.get(0);
			startime=(String) d.get("z0329");
		}
		return startime;
	}
}
