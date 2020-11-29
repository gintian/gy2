package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hjsj.hrms.businessobject.sys.busimaintence.Compositor;
import com.hjsj.hrms.businessobject.sys.busimaintence.Serialnumber;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class AddBusiFieldTrans extends IBusiness {
// 增加和插入指标
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String box = (String)reqhm.get("bitianxiang");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo fieldVo=new RecordVo("t_hr_busifield");
		String fieldsetid;
		String itemid;
		if(reqhm.containsKey("query")){
//		进入页面	
			BusiSelStr busiselstr=new BusiSelStr();
			reqhm.remove("query");
			fieldsetid=(String) reqhm.get("fieldsetid");
			String userType = (String) hm.get("userType");
			if(reqhm.containsKey("itemid")){
//				进入插入页面
				String displayid=(String)reqhm.get("displayid");
				reqhm.remove("displayid");
				try {
					fieldVo=this.putRecordVo(this.getFrameconn(),fieldsetid,"1",userType);
					Integer dis=new Integer(displayid);
					fieldVo.setInt("displayid",dis.intValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					fieldVo=this.putRecordVo(this.getFrameconn(),fieldsetid,"0",userType);
				} catch (SQLException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			hm.put("busiFieldVo",fieldVo);
			hm.put("codesetsel",busiselstr.getCodeStr(dao,""));
			hm.put("date",busiselstr.getDateSel(""));
			try {
				hm.put("relating",busiselstr.getRelatingCode(dao,""));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
//			增加修改指标
			if(reqhm.containsKey("itemid")){
//				插入指标
				fieldVo =(RecordVo) hm.get("fieldVo");
				this.addFieldVo(dao,fieldVo,box);
				try {
					Compositor.compositfield(dao,fieldVo,"1");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reqhm.remove("itemid");
			}else
			{
//				保存指标
				fieldVo =(RecordVo) hm.get("fieldVo");
				if(fieldVo.getString("itemdesc")==null) {
					return;
				}
				this.addFieldVo(dao,fieldVo,box);
			}
		
	}
	}
	public String getDisplayid(){
		return null;
	}
	public void addFieldVo(ContentDAO dao,RecordVo busiFieldVo,String box) throws GeneralException{
		BusiSelStr busiss=new BusiSelStr();
		busiFieldVo.setString("keyflag","0");
		busiFieldVo.setString("state","1");
		busiFieldVo.setString("useflag","0");
		String itpe=busiFieldVo.getString("itemtype");
		//新增指标，指标类型含有/会丢失，改为.
		String[] itpes=itpe.split("\\.");
		if("A".equals(itpes[0])){
			busiFieldVo.setString("itemtype","A");
			if("S".equals(itpes[1])){
				busiFieldVo.setString("codeflag","0");
				busiFieldVo.setString("codesetid","0");
				busiFieldVo.setInt("displaywidth",busiFieldVo.getString("itemdesc").length()*2);
				busiFieldVo.setString("reserveitem",box);
			}
			if("C".equals(itpes[1])){
				busiFieldVo.setString("codeflag","0");
				busiFieldVo.setInt("displaywidth",busiFieldVo.getString("itemdesc").length()*2);
				busiFieldVo.setString("reserveitem",box);
				String codesetid=busiFieldVo.getString("codesetid");
				busiFieldVo.setString("itemlength",busiss.getChildLen(dao,codesetid));
			}
			if("R".equals(itpes[1])){
				busiFieldVo.setString("codeflag","1");
				busiFieldVo.setInt("displaywidth",busiFieldVo.getString("itemdesc").length()*2);
				String reserveitem=busiFieldVo.getString("reserveitem");
				//【7103】业务字典，插入指标，界面报错。 jingq add 2015.01.28
				reserveitem = PubFunc.keyWord_reback(reserveitem);
				String[] revitem=reserveitem.split("/");
				busiFieldVo.setString("codesetid",revitem[0]);
				busiFieldVo.setString("itemlength",revitem[1]);
				busiFieldVo.setString("reserveitem",box);
			}
		}
		if("N".equals(itpes[0])){
			busiFieldVo.setString("itemtype","N");
			busiFieldVo.setString("codeflag","0");
			busiFieldVo.setString("codesetid","0");
			busiFieldVo.setInt("displaywidth",busiFieldVo.getString("itemdesc").length()*2);
			busiFieldVo.setString("reserveitem",box);
		}
		if("D".equals(itpes[0])){
			busiFieldVo.setString("itemtype","D");
			busiFieldVo.setString("codeflag","0");
			busiFieldVo.setString("codesetid","0");
			busiFieldVo.setInt("displaywidth",10);
			busiFieldVo.setString("reserveitem",box);
		}
		if("M".equals(itpes[0])){
			busiFieldVo.setString("itemtype","M");
			busiFieldVo.setString("codeflag","0");
			busiFieldVo.setString("codesetid","0");
			// 这句报错，不能用getInt方法 注掉  guodd 2017-07-08
			if(busiFieldVo.getString("itemlength")==""/* ||busiFieldVo.getInt("itemlength")==10 */){
				busiFieldVo.setString("itemlength", "10");
			}
			busiFieldVo.setInt("displaywidth",busiFieldVo.getString("itemdesc").length()*2);
			busiFieldVo.setString("reserveitem",box);
		}
		dao.addValueObject(busiFieldVo);
	}
	public void insertFiledVo(ContentDAO dao,RecordVo busiFieldVo){
		
	}
	/*
	 * userType =0/=null 用户模式， =1开发模式
	 * 业务字典，新建指标项，根据userType生成指标代号
	 * jingq add 2015.01.21
	 */
	public RecordVo putRecordVo(Connection conn,String fieldsetid,String flag,String userType) throws SQLException{
		RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
		String itemidisplay=Serialnumber.getSerialnum(conn,fieldsetid,userType);
		String[] itemdip=itemidisplay.split("/");
		busiFieldVo.setString("fieldsetid",fieldsetid);
		busiFieldVo.setString("itemid",itemdip[0]);
		if("0".equals(flag))
			busiFieldVo.setString("displayid",itemdip[1]);
		return busiFieldVo;
	}
	
}
