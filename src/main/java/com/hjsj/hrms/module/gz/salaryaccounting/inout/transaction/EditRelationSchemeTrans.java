package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *Title:UpdateAccordTitleTrans
 *Description:对应方案的编辑，包括修改、删除、上移、下移
 *Company:HJHJ
 *Create time:2015-7-9 
 *@author lis
 */
public class EditRelationSchemeTrans extends IBusiness
{
	@Override
    public void execute() throws GeneralException
	{
		String oper = (String) this.getFormHM().get("oper");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		SalaryInOutBo inOutBo=new SalaryInOutBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		try
		{
			if(!"".equals(oper) && oper != null)
			if ("0".equals(oper))//修改方案名称
			{
				String id = (String) this.getFormHM().get("id");
				String name = (String) this.getFormHM().get("name");
				if(isUniqueName(name, id, "")){
					RecordVo vo = new RecordVo("gz_relation");
					vo.setInt("id", Integer.valueOf(id));
					vo = dao.findByPrimaryKey(vo);
					vo.setString("name", name);
					dao.updateValueObject(vo);
				}
				
			} else if ("1".equals(oper))//删除方案
			{
				ArrayList<String> ids = (ArrayList<String>) this.getFormHM().get("ids");
				ArrayList list = new ArrayList();
				StringBuffer str = new StringBuffer("");
				for (int i = 0; i < ids.size(); i++){
					list.add(ids.get(i));
					str.append(",?");
				}

				dao.delete("delete from gz_relation where id in("+str.substring(1)+")", list);
			}else if("move".equals(oper)){//上下移动方案
				String ori_id = (String) this.getFormHM().get("ori_id");
				String to_id = (String) this.getFormHM().get("to_id");
				String to_seq = (String) this.getFormHM().get("to_seq");
				String ori_seq = (String) this.getFormHM().get("ori_seq");
				inOutBo.moveUpDown(ori_id,ori_seq, to_id, to_seq);

			}else if ("read".equals(oper))//读取方案
			{
				String id = (String) this.getFormHM().get("id");
				String context = "";
				ArrayList list = new ArrayList();
				list.add(id);
				this.frowset = dao.search("select * from gz_relation where id=?",list);
				if (this.frowset.next())
					context = Sql_switcher.readMemo(this.frowset, "rel");

				String[] temps = context.split("\\|");

				ArrayList oppositeItem = new ArrayList();
				if (temps.length > 0)
				{
					String[] oppositeItemArr = temps[0].split("\\,");
					for (int i = 0; i < oppositeItemArr.length; i++)
					{
						if (StringUtils.isNotBlank(oppositeItemArr[i].trim()))
							oppositeItem.add(oppositeItemArr[i].trim());
					}
				}

				ArrayList relationItem = new ArrayList();
				if (temps.length > 1)
				{
					String[] relationItemArr = temps[1].substring(1, temps[1].length() - 1).split("\\,\\,");
					for (int i = 0; i < relationItemArr.length; i++)
					{
						if (StringUtils.isNotBlank(relationItemArr[i].trim()))
						{
							if (relationItemArr.length == 1)
								relationItem.add(relationItemArr[i].trim().substring(0, relationItemArr[i].trim().length()));
							else
								relationItem.add(relationItemArr[i].trim());
						}
					}
				}
				this.getFormHM().put("relationItemList", relationItem);
				this.getFormHM().put("oppositeItemList", oppositeItem);

			}else if("save".equals(oper)){ //保存方案
				String name=(String)this.getFormHM().get("schemeName");
				String onlyName=(String)this.getFormHM().get("onlyName");
				if(isUniqueName(name, "", salaryid)){
					ArrayList<String> oppositeItem = new ArrayList<String>();   //对应指标 
					ArrayList<String> relationItem = new ArrayList<String>();  //关联指标
					if(!"".equals(onlyName.trim()))
						relationItem.add("onlyName"+":"+onlyName);
					ArrayList<MorphDynaBean> recordDatas=(ArrayList<MorphDynaBean>)this.getFormHM().get("recordDatas");
					for(MorphDynaBean bean:recordDatas){
						if(bean == null)
							continue;
						String itemid1 = (String)bean.get("itemid1");
						String itemid2 = (String)bean.get("itemid2");
						if(StringUtils.isNotBlank(itemid1))
							oppositeItem.add((String)bean.get("itemdesc")+":"+itemid1);
						if(StringUtils.isNotBlank(itemid2))
							relationItem.add((String)bean.get("itemdesc")+":"+itemid2);
					}
					inOutBo.saveRelationScheme(name, oppositeItem, relationItem);
				}else{
					this.getFormHM().put("savemsg", "当前名称已经存在！");
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * @author lis
	 * @Description: 名称是否是唯一
	 * @date 2015-11-23
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean isUniqueName(String name,String id, String salaryid){
		boolean flag = true;
		try {
			String sql = "";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(StringUtils.isBlank(id)){//新增
				ArrayList list = new ArrayList();
				sql = "select name from gz_relation where  "+Sql_switcher.isnull("salaryid", "'"+salaryid+"'")+"=? and "+Sql_switcher.isnull("userflag", "'"+this.userView.getUserName()+"'")+"=? and name=?";
				list.add(salaryid);
				list.add(this.userView.getUserName());
				list.add(name);
				this.frowset = dao.search(sql,list);
			}else{//重命名
				sql = "select name from gz_relation where  name=? and id!=?";
				this.frowset = dao.search(sql,Arrays.asList(name,id));
			}
			String msg = "0";
			if(this.frowset.next()){//当前名称已经存在
				msg = "error";
				flag = false;
			}
			this.getFormHM().put("msg",msg);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
		return flag;
	}
}
