package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 人员信息排序
 * <p>Title:SortPartySubTrans.java</p>
 * <p>Description>:SortPartySubTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 30, 2010 10:32:12 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SortPartySubTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String setname=(String)this.getFormHM().get("setname");
		if(setname==null||setname.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到子集信息"));
		String a0100=(String)this.getFormHM().get("a0100");
		if(a0100==null||a0100.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到人员信息"));
		String nbase=(String)this.getFormHM().get("nbase");
		if(nbase==null||nbase.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到人员信息"));
		String i9999=(String)this.getFormHM().get("i9999");
		
		doScan(type,nbase,setname,a0100,i9999);
		
		String tablename=nbase+setname;
		this.doSort(type, tablename, a0100, i9999);
	}
	private synchronized void doSort(String type,String tablename,String a0100,String i9999)throws GeneralException {
		String key="a0100";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			if("up".equals(type)){
				int upi9999=9999;
				String sql = "select * from "+tablename+" where "+key+"='"+a0100+"' and i9999<"+i9999+" order by i9999 desc";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					upi9999=this.frecset.getInt("i9999");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.up")));
				}
				
				/*zxj 20150522  下边4行注释原因：  
				 * 1、表上有触发器时，会影响update()的返回值，虽然执行成功了，也可能触发器里会返回0，导致误认为update失败
				 * 2、使用-999998作为交换顺序中间值，防止用正数时与已有顺序号冲突
				 */
				sql = "update "+tablename+" set i9999=-999998 where "+key+"='"+a0100+"' and i9999="+upi9999;
				//if(dao.update(sql)>0){
				dao.update(sql);
				sql = "update "+tablename+" set i9999="+upi9999+" where "+key+"='"+a0100+"' and i9999="+i9999;
					//if(dao.update(sql)>0){
				dao.update(sql);
				sql = "update "+tablename+" set i9999="+i9999+" where "+key+"='"+a0100+"' and i9999=-999998";
				dao.update(sql);
				//	}
				//}
				
			}else if("down".equals(type)){
				int downi9999=9999;
				String sql = "select * from "+tablename+" where "+key+"='"+a0100+"' and i9999>"+i9999+" order by i9999";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					downi9999=this.frecset.getInt("i9999");
					
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.down")));
				}
				
				/*zxj 20150522 下边4行注释行原因：  
				 * 1、表上有触发器时，会影响update()的返回值，虽然执行成功了，也可能触发器里会返回0，导致误认为update失败
				 * 2、使用-999997作为交换顺序中间值，防止用正数时与已有顺序号冲突
				 */
				sql = "update "+tablename+" set i9999=-999997 where "+key+"='"+a0100+"' and i9999="+downi9999;
				//if(dao.update(sql)>0){
				dao.update(sql);
				sql = "update "+tablename+" set i9999="+downi9999+" where "+key+"='"+a0100+"' and i9999="+i9999;
				//if(dao.update(sql)>0){
				dao.update(sql);
				sql = "update "+tablename+" set i9999="+i9999+" where "+key+"='"+a0100+"' and i9999=-999997";
				dao.update(sql);
					//}
				//}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @Title: 检查编制   

	 */
	private  void doScan(String type,String userbase,String setname,String A0100,String i9999)throws GeneralException {
		String tablename =userbase+setname;
		String checkI9999="";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			ArrayList msglist = new ArrayList();
			ScanFormationBo scanFormationBo = new ScanFormationBo(this.getFrameconn(),this.userView);
			if (scanFormationBo.doScan()){
				if ("true".equals(scanFormationBo.getPart_flag()) 
						&&    setname.equalsIgnoreCase(scanFormationBo.getPart_setid())){//兼职子集
					;
				}
				else {					
					try{
						if("up".equals(type)){
							checkI9999=i9999;
							
						}else if("down".equals(type)){
							int downi9999=9999;
							String sql = "select * from "+tablename+" where a0100='"+A0100+"' and i9999>"+i9999+" order by i9999";
							this.frecset =dao.search(sql);
							if(this.frecset.next()){
								downi9999=this.frecset.getInt("i9999");
								checkI9999 = String.valueOf(downi9999);
							}else{
								throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.down")));
							}

						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
			
		 		 	String maxI999 = this.getMaxI9999(userbase,setname, A0100, dao);
		 			if (checkI9999.equals(maxI999)) {
						StringBuffer itemids = new StringBuffer();
						LazyDynaBean scanBean = new LazyDynaBean();
						setScanBeanList(itemids,scanBean, userbase,setname, A0100,maxI999);
					
						if (scanFormationBo.needDoScan(userbase+',', itemids.toString())){								
							scanBean.set("objecttype", "1");	
							scanBean.set("nbase", userbase);
							scanBean.set("a0100", A0100);	
							scanBean.set("ispart", "0");									
							scanBean.set("addflag", "0");									
					
							ArrayList beanList = new ArrayList();
							beanList.add(scanBean);
							scanFormationBo.execDate2TmpTable(beanList);
							String mess=  scanFormationBo.isOverstaffs();
							if (!"ok".equals(mess)){
								if("warn".equals(scanFormationBo.getMode())){
									msglist.add(mess);
								}else{
									throw GeneralExceptionHandler.Handle(new GeneralException("",mess,"",""));
								}
							}			
						}
		 			}
			
				}
			}
            if(msglist.size()>0){
                StringBuffer msg = new StringBuffer();
                for(int i=0;i<msglist.size();i++){
                    if(msglist.size()>1){
                        msg.append((i+1)+":"+msglist.get(i)+"\\n");
                    }else{
                        msg.append(msglist.get(i));
                    }
                }
                this.getFormHM().put("infomsg", msg.toString());
            }else
                this.getFormHM().put("infomsg", "");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	private String getMaxI9999(String usrbase,String setname,String a0100,ContentDAO dao) throws SQLException{
		String maxi9999 = "0";
		String sql = "select max(i9999) i9999 from "+usrbase+setname+" where a0100='"+a0100+"'";
		this.frecset = dao.search(sql);
		if(this.frecset.next())
			maxi9999 = String.valueOf(this.frecset.getInt("i9999"));
		
		return maxi9999;
	}

	private void setScanBeanList(StringBuffer scanItemIds,
			LazyDynaBean scanBean, String userbase, String setname,
			String A0100, String i9999) {

		scanItemIds.setLength(0);
		StringBuffer buf = new StringBuffer();
		buf.append("select * from " + userbase + setname);
		buf.append(" where a0100='" + A0100 + "' and i9999 =" + i9999);
		ArrayList fieldlist = DataDictionary.getFieldList(setname,
				Constant.USED_FIELD_SET);

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			if (this.frowset.next()) {
				for (int j = 0; j < fieldlist.size(); j++) {
					FieldItem fielditem = (FieldItem) fieldlist.get(j);
					String itemid = fielditem.getItemid().toLowerCase();
                    Object obj =this.frowset.getObject(fielditem.getItemid());
                    String itemvalue = "";
                    if (obj!=null )
                        itemvalue =obj.toString();  
					itemvalue = itemvalue != null
							&& itemvalue.trim().length() > 0 ? itemvalue : "";
					if (!"".equals(scanItemIds.toString())) {
						scanItemIds.append(",");
					}
					scanItemIds.append(itemid);
					scanBean.set(itemid, itemvalue);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
}
