package com.hjsj.hrms.transaction.general.statics.crossstatic;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 将二维交叉表页面使用到的条件保存到form中
 * <p>Title: SaveTowCrossTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Sep 11, 2014 9:12:04 AM</p>
 * @author liuy
 * @version 1.0
 */
public class SaveTowCrossTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("id");
		id=id!=null?id:"";
		String crosswise = (String)hm.get("crosswise");
		String lengthways = (String)hm.get("lengthways");
		this.getFormHM().put("crosswise" , crosswise);
		this.getFormHM().put("lengthways" , lengthways);
		
		ArrayList typeList = new ArrayList();
		typeList = this.getTypeList(dao);
		this.getFormHM().put("typeList" , typeList);
		
		String dbname = (String)hm.get("userbases");
		this.getFormHM().put("dbname" , dbname);
		
		ArrayList dbnamelist = new ArrayList();
		dbnamelist = this.getDbNameList(dao);		
		this.getFormHM().put("dbnamelist" , dbnamelist);
		
		//清空之前选中的人员范围
		ArrayList tempCondList = new ArrayList();
		this.getFormHM().put("tempCondList" , tempCondList);
		
	}
	
	/**
	 * 得到统计项分类列表
	 * @param dao
	 * @return
	 */
	private ArrayList getTypeList(ContentDAO dao){
		String type="";
		StringBuffer hideType = new StringBuffer();
		ArrayList typeList = new ArrayList();
		try {
			this.frowset=dao.search("select categories from  sname where infokind=1 group by categories ");
			while(this.frowset.next())
			{
				type = this.frowset.getString("categories");
				if(type==null||type.length()==0)
					continue;
				if(!userView.isSuper_admin()){
					this.frecset = dao.search("select id from  sname where infokind=1 and categories='"+type+"'");
					boolean flag = false;
					while(this.frecset.next()){
						String id =String.valueOf(this.frecset.getInt("id"));
						if(userView.isHaveResource(IResourceConstant.STATICS,id)){
							flag = true;
							break;
						}
					}
					if(!flag)
						continue;
				}
				CommonData cd = new CommonData(type,type);
				typeList.add(cd);
				hideType.append(","+type);
			}
			this.formHM.put("hideType", hideType.length()>0?hideType.substring(1):"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return typeList;
	}
	
	/**
	 * 得到人员库列表
	 * @param dao
	 * @return
	 */
	private ArrayList getDbNameList(ContentDAO dao){
		ArrayList list = new ArrayList();
		//授权当前用户的应用库列表
		try {
			String dbListSql = "select pre,dbname from dbname order by dbid";
			String dbpriv = this.userView.getDbpriv().toString();
			this.frecset = dao.search(dbListSql);
			while (this.frecset.next()) {
				String pre = this.frecset.getString("pre");
				if (dbpriv.indexOf(pre) != -1||this.userView.isSuper_admin()) {
					CommonData data = new CommonData();
					data.setDataName(this.frecset.getString("dbname"));
					data.setDataValue(this.frecset.getString("pre"));
					list.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
