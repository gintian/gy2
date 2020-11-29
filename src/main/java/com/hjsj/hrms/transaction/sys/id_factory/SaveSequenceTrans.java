/**
 * 
 */
package com.hjsj.hrms.transaction.sys.id_factory;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SaveSequenceTrans</p>
 * <p>Description:保存序号对象，包括更新保存以及新增对象的保存</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-11-19:11:11:37</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveSequenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		String updateflag=(String)this.getFormHM().get("updateflag");

		if(updateflag==null|| "".equals(updateflag))
			return ;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=(RecordVo)this.getFormHM().get("idvo");
			//系统管理序号维护，新增、修改序列，前缀含有特殊字符需还原处理  jingq add 2014.09.20
			vo.setString("prefix", PubFunc.keyWord_reback(vo.getString("prefix")));
			vo.setInt("auto_increase",1);
			if("add".equals(updateflag))
			{
				String sysorclient=(String)this.getFormHM().get("sysorclient");
				if("2".equals(sysorclient))//用户自定义
					vo.setInt("status",1);
				else
					vo.setInt("status",0);//系统s
				String seqname=vo.getString("sequence_name");
				int startint=seqname.indexOf('.')+1;
				String itemname=seqname.substring(startint,seqname.length());
				String ct_rule=vo.getString("c_rule");
				Integer c_rule=new Integer(ct_rule);
				String byprefix=vo.getString("byprefix");
				if(byprefix==null|| "".equals(byprefix)){
					vo.setString("byprefix", "0");
				}
				String sql="select * from id_factory where sequence_name=?";
				ArrayList voList=new ArrayList();
				voList.add(vo.getString("sequence_name"));
				this.frowset=dao.search(sql, voList);
				//当添加存在序号时，前台提示 已存在  27748   wangb1 20170516 
				if(this.frowset.next()){
					throw GeneralExceptionHandler.Handle(new Exception("已存在！"));
				}
				//序号不存在，新增  27748 wangb1 20170516
				dao.addValueObject(vo);
				FieldItem item=(FieldItem)DataDictionary.getFieldItem(itemname);
				if(item!=null){
					item.setSequenceable(true);
					item.setC_rule(c_rule.intValue());
					item.setSequencename(vo.getString("sequence_name"));
					item.setSeqprefix_field(vo.getString("prefix_field"));
					String prefix_field_len=vo.getString("prefix_field_len");
					if(prefix_field_len!=null&&!"".equals(prefix_field_len))
						item.setPrefix_field_len(Integer.parseInt(prefix_field_len));
					String b=vo.getString("byprefix");
					boolean f=false;
					if("1".equals(b)){
						f=true;
					}
					item.setByprefix(f);
				}
			}
			if("update".equals(updateflag))
			{
				try{
					dao.findByPrimaryKey(vo);
					dao.updateValueObject(vo);
					String seqname=vo.getString("sequence_name");
					int startint=seqname.indexOf('.')+1;
					String itemname=seqname.substring(startint,seqname.length());
					FieldItem item=(FieldItem)DataDictionary.getFieldItem(itemname);
					if(item!=null){
						String ct_rule=vo.getString("c_rule");
						Integer c_rule=new Integer(ct_rule);
						item.setSequenceable(true);
						item.setC_rule(c_rule.intValue());
						item.setSequencename(vo.getString("sequence_name"));
						item.setSeqprefix_field(vo.getString("prefix_field"));
						String prefix_field_len=vo.getString("prefix_field_len");
						if(prefix_field_len!=null&&!"".equals(prefix_field_len))
							item.setPrefix_field_len(Integer.parseInt(prefix_field_len));
						String b=vo.getString("byprefix");
						boolean f=false;
						if("1".equals(b)){
							f=true;
						}
						item.setByprefix(f);
					}
				}catch(Exception e){
					String old_sequence_name=(String)this.getFormHM().get("old_sequence_name");
					RecordVo oldvo= new RecordVo("id_factory");
					oldvo.setString("sequence_name", old_sequence_name);
					if(dao.deleteValueObject(oldvo)>0)
						dao.addValueObject(vo);

					String old_seqname=old_sequence_name;
					int startint=old_seqname.indexOf('.')+1;
					String old_itemname=old_seqname.substring(startint,old_seqname.length());
					FieldItem item=(FieldItem)DataDictionary.getFieldItem(old_itemname);
					if(item!=null){
						item.setSequenceable(false);
						item.setC_rule(0);
						item.setSequencename("");
						item.setPrefix_field_len(0);
						item.setByprefix(false);
					}

					String seqname=vo.getString("sequence_name");
					startint=seqname.indexOf('.')+1;
					String itemname=seqname.substring(startint,seqname.length());
					item=(FieldItem)DataDictionary.getFieldItem(itemname);
					if(item!=null){
						String ct_rule=vo.getString("c_rule");
						Integer c_rule=new Integer(ct_rule);
						item.setSequenceable(true);
						item.setC_rule(c_rule.intValue());
						item.setSequencename(vo.getString("sequence_name"));
						item.setSeqprefix_field(vo.getString("prefix_field"));
						String prefix_field_len=vo.getString("prefix_field_len");
						if(prefix_field_len!=null&&!"".equals(prefix_field_len))
							item.setPrefix_field_len(Integer.parseInt(prefix_field_len));
						String b=vo.getString("byprefix");
						boolean f=false;
						if("1".equals(b)){
							f=true;
						}
						item.setByprefix(f);
					}
				}

			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
