package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherJounalBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * 类名称:SaveGroupTrans
 * 类描述:保存分录分组指标
 * 创建人: xucs
 * 创建时间:2013-8-23 下午01:32:31 
 * 修改时间:xucs
 * 修改时间:2013-8-23 下午01:32:31
 * 修改备注:
 * @version
 *
 */
public class SaveGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String pn_id =(String)this.getFormHM().get("pn_id");//凭证id
			String fl_id =(String)this.getFormHM().get("fl_id");//分录 id
			String c_group="";//分组分录选定的指标值
			String interface_type="";//凭证类别 
			VoucherBo vb=new VoucherBo(this.getFrameconn(), null, null, pn_id);//处理凭证相关的业务类对象VoucherBo
			VoucherJounalBo vjb = new VoucherJounalBo(this.getFrameconn());//处理凭证分录相关的业务对象VoucherJounalBo
			if(!("".equals(this.getFormHM().get("c_group"))||this.getFormHM().get("c_group")==null)){
				c_group=(String) this.getFormHM().get("c_group");
				c_group=c_group.substring(0, c_group.length()-1);
			}
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String xmlValue=vb.getXmlValue();//存在数据库中的xml值
			ArrayList xmlList = vjb.getList(xmlValue);//将xml值转换成List
			String sql ="select c_group from gz_warrantlist where pn_id="+pn_id+"and fl_id not in ("+fl_id+")";
			String newC_groupvalue="";
			this.frowset=dao.search(sql);
			while(frowset.next()){
			    if(!(frowset.getString(1)==null||frowset.getString(1)=="")){
			        newC_groupvalue=newC_groupvalue+frowset.getString(1)+",";
			    }
			}
			sql="select c_group from gz_warrantlist where pn_id="+pn_id;
			String oldC_groupvalue="";
			this.frowset=dao.search(sql);
			while(frowset.next()){
                if(!(frowset.getString(1)==null||frowset.getString(1)=="")){
                    oldC_groupvalue=oldC_groupvalue+frowset.getString(1)+",";
                }
            }
			ArrayList oldC__groupList=vjb.getList(oldC_groupvalue.toUpperCase());
			ArrayList newC_groupList=vjb.getList(newC_groupvalue.toUpperCase()+c_group.toUpperCase());
			
			//添加新设置的分录分组指标（若已经存在 忽略）
			xmlList.removeAll(oldC__groupList);
			xmlList.addAll(newC_groupList);
//			ArrayList newList = vjb.getList(c_group);
//			for(int i=0;i<newList.size();i++){
//				String tt = (String) newList.get(i);
//				if(xmlList.contains(tt.toUpperCase())){
//					continue;
//				}
//				xmlList.add(newList.get(i));
//			}
			//更改xml中的content属性中的内容
			vb.setAttributeValue("/voucher/items", "fields", vjb.getListValue(xmlList).toUpperCase());
			String xmlSql = "update GZ_Warrant set content='"+vb.saveStrValue()+"' where pn_id="+pn_id+"";
			dao.update(xmlSql);
			//更改对应分录中的c_group字段
			sql="update gz_warrantlist set c_group='"+c_group.toUpperCase()+"'where pn_id="+pn_id+"and fl_id="+fl_id+"";
			dao.update(sql);
			sql="select interface_type from gz_warrant where pn_id="+pn_id+"";
			this.frowset=dao.search(sql);
			while(frowset.next()){
				interface_type=frowset.getString(1);
			}
			this.getFormHM().put("interface_type",interface_type);
			this.getFormHM().put("pn_id", pn_id);
			this.getFormHM().put("fl_id",fl_id);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
