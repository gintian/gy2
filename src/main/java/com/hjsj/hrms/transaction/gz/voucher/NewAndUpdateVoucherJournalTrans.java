package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherJounalBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * 
 * 类名称:NewAndUpdateVoucherJournalTrans
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-9-13 下午01:49:29 
 * 修改人:xucs
 * 修改时间:2013-9-13 下午01:49:29
 * 修改备注:
 * @version
 *
 */
public class NewAndUpdateVoucherJournalTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("flag");// flag 1:新增2：修改
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String fl_id = (String) hm.get("a_id");//要修改的分录id
            String pn_id = (String) this.getFormHM().get("pn_id");//分录所属的凭证id
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            VoucherJounalBo vjb = new VoucherJounalBo(this.getFrameconn());
            VoucherBo vb=new VoucherBo(this.getFrameconn(), null, null,pn_id);
            String groupValue ="";
            String collectValue="";
            String sql="";
            sql="select c_group from gz_warrantlist where pn_id="+pn_id;
            this.frowset=dao.search(sql);
            while(this.frowset.next()){
                groupValue=groupValue+this.frowset.getString(1)+","; 
            }
            if(!("".equals(groupValue))){
                groupValue=groupValue.substring(0, groupValue.length()-1);
            }
            ArrayList groupList = vjb.getList(groupValue);
            sql="select COLLECT_FIELDS from gz_warrant where pn_id="+pn_id;
            this.frowset=dao.search(sql);
            while(this.frowset.next()){
                collectValue=collectValue+this.frowset.getString(1)+","; 
            }
            if(!("".equals(collectValue))){
                collectValue=collectValue.substring(0, collectValue.length()-1);
            }
            ArrayList collectList = vjb.getList(collectValue);
            String xmlValue =vb.getXmlValue();
            ArrayList xmlList=vjb.getList(xmlValue);//要在界面上维护的指标
            
            ArrayList none_filed=vjb.getNone_field();//排除界面上不显示的指标
            String none_filedvalue=vjb.getListValue(none_filed);
            none_filed=vjb.getList(none_filedvalue);
            
            xmlList.removeAll(none_filed);//移除不能在界面显示的指标  不在界面上显示的指标自然不能维护
            xmlList.removeAll(groupList);//分录分组指标也是不能维护他的值的
            xmlList.removeAll(collectList);//分录汇总指标也是不能维护值的
            xmlList.remove("c_itemsql");//限制条件 和计算公式也是不能维护值
            xmlList.remove("c_where");
            
            ArrayList containList =vjb.getContianList();//获取业务字典中的字段
            ArrayList templist = (ArrayList) xmlList.clone();
            templist.removeAll(containList);
            xmlList.removeAll(templist);
            xmlValue=vjb.getListValue(xmlList);//不在业务字典中的值是不能维护的
            sql="select interface_type from GZ_WARRANT where pn_id="+pn_id;
            this.frowset=dao.search(sql);
            if(this.frowset.next()){
                String interface_tye=this.frowset.getString(1);
                if("2".equalsIgnoreCase(interface_tye)){
                    xmlValue="C_MARK,C_SUBJECT,FL_NAME"; 
                    xmlValue=xmlValue.toLowerCase();
                }
            }
            if("1".equals(flag)){
                ArrayList maintainList = vjb.maintainJounal(xmlValue,null);
                this.getFormHM().put("maintainList", maintainList);
                this.getFormHM().put("flag", flag);
            }
            if("2".equalsIgnoreCase(flag)){
                sql ="select "+xmlValue+" from GZ_WARRANTLIST where pn_id="+pn_id+" and fl_id="+ fl_id+"";
                this.frowset=dao.search(sql);
                String [] xmlTempArray =xmlValue.split(",");
                String []ValueArray=new String[xmlTempArray.length];
                String []tempArray = null;
                if(xmlValue!=null){
                    tempArray=xmlValue.split(",");
                }
                if(this.frowset.next()){
                    for(int i=0;i<tempArray.length;i++){
                        ValueArray[i]=this.frowset.getString(i+1);
                    }  
                } 
                ArrayList maintainList = vjb.maintainJounal(xmlValue,ValueArray);
                this.getFormHM().put("maintainList", maintainList);
                this.getFormHM().put("flag", flag);
                this.getFormHM().put("pn_id", pn_id);
                this.getFormHM().put("fl_id", fl_id);
            }
           
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
