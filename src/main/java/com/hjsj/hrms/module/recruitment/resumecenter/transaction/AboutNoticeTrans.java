package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.PrintResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AboutNoticeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			// 需要显示的指标
			ArrayList<String> selectedItems = (ArrayList) this.getFormHM().get("selectedItems");
			// 选中的人
			ArrayList<String> a0100_es = (ArrayList) this.getFormHM().get("a0100s");
			// 分组指标
			String groupValue = (String) this.getFormHM().get("groupValue");
			String flag = (String) this.getFormHM().get("flag");
			RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname = ""; // 应聘人员库
			if (vo != null)
				dbname = vo.getString("str_value");
	
			if ("searchInfo".equals(flag)) {
				HashMap map = searchInfo(selectedItems, a0100_es, groupValue, dbname);
				this.formHM.put("searchInfo",map.get("groupData"));
				this.formHM.put("groupDesc",map.get("groupDesc"));
				this.formHM.put("groupKey",map.get("groupKey"));
			}else if("showNoticeTitle".equals(flag)){
				ArrayList noticeTitle = getNoticeTitle();
				this.formHM.put("info",noticeTitle);
			}else if("showNoticeContent".equals(flag)){
				String id = (String) this.getFormHM().get("id");
				HashMap noticeContent = getNoticeContent(PubFunc.decrypt(id));
				this.formHM.put("noticeContent",noticeContent);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 根据选择指标查询信息，如果有分组指标按分组指标保存
	 * 返回分组指标对应的信息，分组指标对应的值和描述，分组指标值
	 * @param selectedItems
	 * @param a0100_es
	 * @param groupValue
	 * @param dbname
	 * @return
	 * @throws GeneralException 
	 */
	private HashMap searchInfo(ArrayList<String> selectedItems,
			ArrayList<String> a0100_es, String groupValue, String dbname) throws GeneralException {
		HashMap map = new HashMap();
		try {
			PrintResumeBo printResumeBo = new PrintResumeBo(this.frameconn,this.userView);
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer("select Z03.Z0301,a.a0100,");
			for (String itemid : selectedItems)
				sql.append(itemid + ",");

			sql.setLength(sql.length() - 1);
			sql.append(" from " + dbname + "A01 a ");
			sql.append(" left join zp_pos_tache zp on a.A0100 = zp.A0100 ");
			sql.append(" left join Z03 on zp.ZP_POS_ID = Z0301 ");
			FieldItem fieldItem = null;
			for (String itemid : selectedItems){
				fieldItem = DataDictionary.getFieldItem(itemid);
				if(fieldItem!=null&&fieldItem.isPerson()&&!StringUtils.contains(sql.toString().toLowerCase(), (dbname+fieldItem.getFieldsetid()).toLowerCase())){
						String tablename = dbname + fieldItem.getFieldsetid();
						sql.append(" left join (select a1.* from "+tablename+" a1 where  a1.i9999=(select MAX(b1.I9999) from "+tablename+" b1 where b1.A0100=a1.a0100)) "+tablename +" on a.A0100="+tablename+".A0100");
	
				}
			}
			
			sql.append(" where zp.THENUMBER = (select min(THENUMBER) from zp_pos_tache where a.A0100 = zp_pos_tache.A0100)");
			sql.append(" and zp.A0100 in(");
			for (String a0100 : a0100_es)
				sql.append("'" + PubFunc.decrypt(a0100) + "',");

			sql.setLength(sql.length() - 1);
			sql.append(")");
			if (StringUtils.isNotEmpty(groupValue))
				sql.append(" order by " + groupValue);
			
			//保存分组数据
			HashMap<String, ArrayList> groupData = new HashMap<String, ArrayList>();
			//保存个人数据
			HashMap<String, String> data = new HashMap<String, String>();
			//分组指标键值方便生成主题
			HashMap<String, String> groupDesc = new HashMap<String, String>();
			ArrayList groupKey = new ArrayList();
			ArrayList list = new ArrayList();
			this.frowset = dao.search(sql.toString());
			String value = "";
			String tempvalue = "";
			String url = "/hire/hireNetPortal/search_notice_card.do?b_card=infoself&encryptParam=";
			//模板id
			String cardid = "#";
			while (this.frowset.next()) {
				if(StringUtils.isNotEmpty(groupValue)){
					tempvalue = "z0351".equals(groupValue)?this.frowset.getString("Z0301"):this.frowset.getString(groupValue);
					if(groupData.get(tempvalue)==null){
						list = new ArrayList();
						if("z0351".equals(groupValue)){
							groupData.put(this.frowset.getString("Z0301"),list);
							groupDesc.put(this.frowset.getString("Z0301"), this.frowset.getString(groupValue));
							groupKey.add(this.frowset.getString("Z0301"));
						}else{
							fieldItem = DataDictionary.getFieldItem(groupValue);
							groupData.put(this.frowset.getString(groupValue),list);
							groupDesc.put(this.frowset.getString(groupValue),AdminCode.getCodeName(fieldItem.getCodesetid(), this.frowset.getString(groupValue)));
							groupKey.add(this.frowset.getString(groupValue));
						}
					}
				}else{
					groupData.put("noFlag", list);
				}
				data = new HashMap<String, String>();
				for (String itemid : selectedItems){
					fieldItem = DataDictionary.getFieldItem(itemid);
					value = this.frowset.getString(itemid);
					//获取代码值
					if(fieldItem!=null&&"A".equals(fieldItem.getItemtype())&&!"0".equals(fieldItem.getCodesetid()))
						value = AdminCode.getCodeName(fieldItem.getCodesetid(), value);
					value = value==null?"":value;
					data.put(itemid, value);
				}
				cardid = printResumeBo.getResumeTemplateId(this.frowset.getString("a0100"));
				if(StringUtils.isNotEmpty(cardid)&&!"#".equals(cardid))
					data.put("infoUrl", url+PubFunc.encrypt("inforkind=9&zp_noticetemplate_flag=true&tabid="+cardid+"&userbase="+dbname+"&fieldpriv=0&a0100="+PubFunc.encrypt(this.frowset.getString("a0100"))));
				else
					data.put("infoUrl","");
				list.add(data);
			}
			map.put("groupData", groupData);
			map.put("groupDesc", groupDesc);
			map.put("groupKey", groupKey);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 获取公示标题列
	 */
	private ArrayList getNoticeTitle() {
		ArrayList list = new ArrayList();
		String a_tempstr = "("+ Sql_switcher.diffDays(Sql_switcher.sqlNow(), "createtime") + ")<period";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select id,topic,createtime from announce where flag='13' and "+a_tempstr+" order by priority,createtime desc");
			HashMap<String, String> map = new HashMap<String, String>();
			while(this.frowset.next()){
				map = new HashMap<String, String>();
				map.put("id", PubFunc.encrypt(this.frowset.getString("id")));
				map.put("topic", this.frowset.getString("topic"));
				map.put("createtime", PubFunc.FormatDate(this.frowset.getDate("createtime")));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据公示id获取公示内容
	 * @param id 
	 */
	private HashMap getNoticeContent(String id) {
		HashMap<String, String> data = new HashMap<String, String>();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select * from announce where flag='13' and id=?";
			ArrayList list = new ArrayList();
			list.add(id);
			this.frowset = dao.search(sql, list);
			while(this.frowset.next()){
				data.put("topic", this.frowset.getString("topic"));
				data.put("content", this.frowset.getString("content"));
				data.put("createtime", PubFunc.FormatDate(this.frowset.getDate("createtime")));
				data.put("period", this.frowset.getString("period"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
}
