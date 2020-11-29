package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * 类名称:SaveVoucherJournalTrans
 * 类描述:保存凭证分录交易类
 * 创建人: xucs
 * 创建时间:2013-8-23 上午11:10:22 
 * 修改时间:xucs
 * 修改时间:2013-8-23 上午11:10:22
 * 修改备注:
 * @version
 *
 */
public class SaveVoucherJournalTrans extends IBusiness {


	public void execute() throws GeneralException {
		try{
			String flag =(String) this.getFormHM().get("flag");//flag 1:新增凭证分录 2：修改凭证分录
			String itemid=(String) this.getFormHM().get("itemid");//从新增、修改界面传过来的要新增或者修改的字段
			String itemValue=(String) this.getFormHM().get("itemvalue");//从新增、修改界面传过来的要新增或者修改的字段的值
			String pn_id=(String) this.getFormHM().get("pn_id");//凭证Id
			String fl_id=(String) this.getFormHM().get("fl_id");//分录 id
			String interface_type = (String) this.getFormHM().get("interface_type");
			ArrayList list=new ArrayList();
		    String[] itemValueArray = itemValue.split(",");
		    String[] itemidArray = itemid.split(",");
		    for(int i=0;i<itemValueArray.length;i++){
		        if(itemValueArray[i]==null){
		            itemValueArray[i]="";
		        }else{
		            itemValueArray[i]=itemValueArray[i].trim();
		        }
		        
		    }
			String seq="";//凭证分录顺序号
			String  sql="";
			Connection conn =this.getFrameconn();
			ContentDAO dao=new ContentDAO(conn);
			/********************start*******************************/
			if("2".equals(interface_type)){
				sql=" select c_subject from gz_warrantlist where pn_id= '"+pn_id+"' and fl_id <> '"+("1".equals(flag)?fl_id+1:fl_id)+"'";
				this.frowset = dao.search(sql);
				List<String> subList = new ArrayList<String>();
				while(this.frowset.next()){
					String subStr = frowset.getString("c_subject");
					subList.add(subStr);
				}
				if(subList.contains(itemValueArray[1]))
					throw GeneralExceptionHandler.Handle(new Exception("该科目已经被当前凭证下的其他分录引用，请检查！"));
			}
			/************************end***************************/
			if("1".equals(flag)){
				sql="select MAX(fl_id) as sid,MAX(seq) as seq from gz_warrantlist where pn_id='"+pn_id+"'";
				
				this.frowset=dao.search(sql);
				while(frowset.next()){
					String sid=frowset.getString("sid");
					seq=frowset.getString("seq");
					if(sid==null ||"".equals(sid)){
						fl_id="1";
					}
					else{
						fl_id=String.valueOf(Integer.parseInt(sid)+1);
					}
					if(seq==null||"".equals(seq)){
						seq="1";
					}else{
						seq=String.valueOf(Integer.parseInt(seq)+1);
					}
				}
				list.add(pn_id);
				list.add(fl_id);
				list.add(seq);
				String ss="?,?,?,";//xiegh 20170614 兼容新加的N类型字段  避免""转numeric报错
				String fieldId = "";
				for(int i=0;i<itemValueArray.length;i++){
					if(!"".equals(itemValueArray[i])){
						list.add(itemValueArray[i]);
						ss=ss+"?"+",";
						fieldId=fieldId+itemidArray[i]+",";
					}
				}
				
				fieldId=fieldId.substring(0, fieldId.length()-1);
				ss=ss.substring(0, ss.length()-1);
				sql="insert into gz_warrantlist (pn_id,fl_id,seq,"+fieldId+") values("+ss+")";
				dao.insert(sql, list);
				this.getFormHM().put("pn_id", pn_id);
				this.getFormHM().put("interface_type", interface_type);
				this.getFormHM().put("flag", flag);
			}else if("2".equals(flag)){
			    RecordVo vo = new RecordVo("GZ_WARRANTLIST");
			    vo.setInt("pn_id", new Integer(pn_id).intValue());
			    vo.setInt("fl_id",new Integer(fl_id).intValue());
			    vo = dao.findByPrimaryKey(vo);
			    for(int i=0;i<itemidArray.length;i++){
			    //不对屏蔽 已经维护过值的字段怎么去掉值
			    	//if(!"".equals(itemValueArray[i]))////xiegh 20170614 兼容新加的N类型字段  避免""转numeric报错
			        vo.setString(itemidArray[i], itemValueArray[i]);
			    }
				dao.updateValueObject(vo);
				this.getFormHM().put("pn_id", pn_id);
                this.getFormHM().put("interface_type", interface_type);
                this.getFormHM().put("flag", flag);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
