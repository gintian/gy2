package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherJounalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 类名称:SearchVoucherTrans
 * 类描述:获得财务凭证下的分录
 * 创建人: xucs
 * 创建时间:2013-8-23 下午01:45:02 
 * 修改时间:xucs
 * 修改时间:2013-8-23 下午01:45:02
 * 修改备注:
 * @version
 *
 */
public class SearchVoucherTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			
			String pn_id=(String) this.getFormHM().get("pn_id");
			HashMap hm=(HashMap) this.getFormHM().get("requestPamaHM");
			String showflag=(String) hm.get("showflag");
			String interface_type=(String) hm.get("interface_type");
			/**
			 * 判断第一次进入页面时要显示的凭证
			 * **/
			if("1".equals(showflag)||showflag==null||pn_id==null||"".equals(pn_id)){
				VoucherJounalBo vjb = new VoucherJounalBo(this.getFrameconn(),this.userView);
				ArrayList piList=vjb.getPIList();
				if(piList.size()==0){
					this.getFormHM().put("pn_id", "");
					return;
				}
				pn_id=(String) piList.get(0);
				interface_type=(String) piList.get(1);
			}
			String[] salaryidArray=null;//为分录分组指标做准备
			String xmlValue="";//取得存在凭证项目中的项目指标
			String[] xmlArray=null;
			String sqlValue="";//查询时使用，在处理Excel显示个个字段时也要使用到
			String groupValue="";
			String[]groupArray=null;
			ArrayList groupList=new ArrayList();
			HashMap tempMap = new HashMap();//用于做借贷方向的判断
			String titleValue="";//为显示Excel的列表头部做准备
			ArrayList titlelist = new ArrayList();//用来存储要显示那些字段
			ArrayList list = new ArrayList();//用于控制在上循环显示的行
			String none_fieldValue="";
			ArrayList none_field=new ArrayList();//用来排除不在页面上显示的列
			//按月汇总的处理
			if("2".equals(interface_type)){
				xmlValue="fl_id,c_mark,c_subject,fl_name,c_itemsql,c_where,seq";
				sqlValue="fl_id,c_mark,c_subject,fl_name,c_itemsql,c_where,seq";
				xmlArray=xmlValue.split(",");
				ArrayList tempList = new ArrayList();
				Connection conn=this.getFrameconn();
				VoucherBo vb=new VoucherBo(conn, null, null, pn_id);
				VoucherJounalBo vjb = new VoucherJounalBo(this.getFrameconn());
				none_field=vjb.getNone_field();
				titlelist=vb.getTitleList(xmlArray, conn, null, interface_type,null);
				tempList=vjb.getList(sqlValue,pn_id, null);
				list=(ArrayList) tempList.get(0);
				titleValue=vjb.getListValue(titlelist);
				none_fieldValue=vjb.getListValue(none_field);
				this.getFormHM().put("titleValue", titleValue);
				this.getFormHM().put("none_fieldValue", none_fieldValue);
				this.getFormHM().put("none_field", none_field);
				this.getFormHM().put("list",list);
				this.getFormHM().put("titlelist",titlelist);
				this.getFormHM().put("xmlArray", xmlArray);
				this.getFormHM().put("xmlValue", xmlValue);
				this.getFormHM().put("sqlValue", sqlValue);
				this.getFormHM().put("pn_id", pn_id);
				
			}//财务凭证的处理
			else if("1".equals(interface_type)){
				VoucherJounalBo vjb = new VoucherJounalBo(this.getFrameconn());
				ArrayList nloanList=vjb.getNloanList();
				ArrayList tempList = new ArrayList();
				this.getFormHM().put("nloanList", nloanList);
				none_field=vjb.getNone_field();
				Connection conn=this.getFrameconn();
				VoucherBo vb=new VoucherBo(conn, null, null, pn_id);
				xmlValue=vb.getXmlValue();
				salaryidArray=vjb.getSalaryIdArray(pn_id);			
				groupList=vjb.getGroupList(pn_id);
				groupValue=vjb.getListValue(groupList);
				if(!("".equals(groupValue)||groupValue==null)){
					groupArray=groupValue.split(",");
				}	
				boolean is_dual_money =false;//是否是双币凭证
				String c_extitemsql ="";
				if (vb.isDualMoney()) 
					c_extitemsql ="c_extitemsql,";
				xmlValue="fl_id,c_group,c_where,c_itemsql,"+c_extitemsql+xmlValue.toLowerCase();
				xmlArray=xmlValue.split(",");
				String tgroup =vb.getTemp(xmlArray,conn);			
				titlelist=vb.getTitleList(xmlArray, conn, groupArray, interface_type,salaryidArray);
				ArrayList tgroupList=vjb.getList(tgroup);
				ArrayList xmlArrayList=vjb.getXmlArrayList(xmlArray, tgroup);
				groupValue=tgroup+groupValue;
				if(!("".equals(groupValue)||groupValue==null)){
					groupArray=groupValue.split(",");
				}
				sqlValue = vjb.getListValue(xmlArrayList);
				tempList =vjb.getList(sqlValue,pn_id, groupArray);
				list=(ArrayList) tempList.get(0);
				tempMap=(HashMap) tempList.get(1);
				titleValue = vjb.getListValue(titlelist);
				none_fieldValue=vjb.getListValue(none_field);
				xmlArrayList.remove("seq");
				for(int i=0;i<groupList.size();i++){
					String  ss=(String) groupList.get(i);
					if(xmlArrayList.contains(ss.toLowerCase())){
						continue;
					}
					xmlArrayList.add(groupList.get(i));
				}
				for(int i=0;i<tgroupList.size();i++){
					String  ss=(String) tgroupList.get(i);
					if(xmlArrayList.contains(ss.toLowerCase())){
						continue;
					}
					xmlArrayList.add(tgroupList.get(i));
				}
				xmlArrayList.remove("");
				xmlArrayList.add("seq");
				String txmlValue=vjb.getListValue(xmlArrayList);
				xmlArray=txmlValue.split(",");
				this.getFormHM().put("tempMap", tempMap);
				this.getFormHM().put("titleValue", titleValue);
				this.getFormHM().put("none_fieldValue", none_fieldValue);
				this.getFormHM().put("xmlValue", txmlValue);
				this.getFormHM().put("sqlValue", sqlValue);
				this.getFormHM().put("none_field", none_field);
				this.getFormHM().put("list",list);
				this.getFormHM().put("titlelist",titlelist);
				this.getFormHM().put("xmlArray", xmlArray);
				this.getFormHM().put("pn_id", pn_id);
				
			}
			this.getFormHM().put("interface_type", interface_type);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
