package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_code = (String) hm.get("a_code");
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		this.getFormHM().put("a_code1", a_code);
		a_code = PubFunc.decrypt(SafeCode.decode(a_code));
		hm.remove("a_code");
		String searchstr = (String) this.getFormHM().get("searchstr");
		searchstr = SafeCode.decode(searchstr);
		searchstr = PubFunc.keyWord_reback(searchstr);
		ArrayList fieldList = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		StringBuffer columns=new StringBuffer();
		StringBuffer strwhere=new StringBuffer();
		StringBuffer strsql = new StringBuffer("select ");
        ArrayList itemList = DataDictionary.getFieldList("r50", Constant.USED_FIELD_SET);
		if(itemList==null)//liweichao
			throw new GeneralException(ResourceFactory.getProperty("conlumn.infopick.educate.nomainset"));
		TrainCourseBo tb = new TrainCourseBo(this.userView,this.frameconn);
		try {
			for (int i = 0; i < itemList.size(); i++) {
				FieldItem item = (FieldItem) itemList.get(i);
				if ("R5000".equalsIgnoreCase(item.getItemid())) {
					FieldItem item4 = new FieldItem();
					item4.setFieldsetid("r50");
					item4.setItemid("detail");
					item4.setItemdesc("学员");
					item4.setItemtype("A");
					item4.setCodesetid("0");
					item4.setAlign("center");
					item4.setReadonly(true);
					fieldList.add(item4);
					
					FieldItem item3 = new FieldItem();
					item3.setFieldsetid("r50");
					item3.setItemid("course");
					item3.setItemdesc("课件");
					item3.setItemtype("A");
					item3.setCodesetid("0");
					item3.setAlign("center");
					item3.setReadonly(true);
					fieldList.add(item3);
					
					FieldItem item2 = new FieldItem();
					item2.setFieldsetid("r50");
					item2.setItemid("commentnum");
					item2.setItemdesc("评论");
					item2.setItemtype("A");
					item2.setCodesetid("0");
					item2.setAlign("center");
					item2.setReadonly(true);
					item2.setVisible(false);
					fieldList.add(item2);
				}
				strsql.append(item.getItemid());
				strsql.append(",");
				columns.append(item.getItemid());
				columns.append(",");
				fieldList.add(item);
			}
			
			FieldItem item5 = new FieldItem();
			item5.setFieldsetid("r50");
			item5.setItemid("imageurl");
			item5.setItemdesc(ResourceFactory.getProperty("conlumn.infopick.educate.img"));
			item5.setItemtype("A");
			item5.setCodesetid("0");
			item5.setAlign("center");
			item5.setReadonly(true);
			fieldList.add(item5);
			
			FieldItem item3 = new FieldItem();
			item3.setFieldsetid("r50");
			item3.setItemid("oper");
			item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
			item3.setItemtype("A");
			item3.setCodesetid("0");
			item3.setAlign("center");
			item3.setReadonly(true);
			fieldList.add(item3);
			// sqlstr.append("*,'' as oper,'' as course from R50");
			
			strsql.append(" imageurl,(select count('1') from  tr_course_comments where r5100 in (select r5100 from r51 where r5000 =R50.r5000) and state=0) as commentnum ");
			columns.append("imageurl,commentnum,'' as oper,'' as course");
			if(!this.userView.isSuper_admin()&&a_code.trim().length()<1){
				strwhere.append(" from R50 where ((1=1");
				sqlstr.append("select r5000,r5003,norder from R50 where ((1=1");
			}else{
				strwhere.append(" from R50 where (1=1");
				sqlstr.append("select r5000,r5003,norder from R50 where (1=1");
			}
			
			if (!this.userView.isSuper_admin()) {
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String unit = bo.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
				if(unit.indexOf("UN`")==-1){
					String sql=" and (";
					String []units = unit.split("`");
						if (units.length > 0 && unit.length() > 0) {
							for (int i = 0; i < units.length; i++) {
								String b0110s = units[i].substring(2);
								sql+="r5020=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("r5020"));
								sql+=" or r5020 like '";
								sql+=b0110s;
								sql+="%'";
								sql+=" or ";
							}
						}
					sql+=Sql_switcher.isnull("r5020", "'-1'");
					sql+="='-1' or r5020='HJSJ'";
					if (Sql_switcher.searchDbServer() == 1) {
						sql+=" or r5020=''";
					}
					sql+=" or r5014=1)";
					sqlstr.append(sql);
					strwhere.append(sql);
				}
			}
				
			String tmp = "";
			if(this.userView.isSuper_admin()){
				if(a_code.trim().length() > 0)
					tmp=" and R5004 like '"+a_code+"%'";
			}else
				tmp = getWhereCode(a_code);
			
			sqlstr.append(tmp);
			strwhere.append(tmp);
			if (a_code != null && !"".equals(a_code) && !"all".equals(a_code))
				strwhere.append(" or codeitemid='" + a_code + "'");

			sqlstr.append(")");
			strwhere.append(")");
			if(!this.userView.isSuper_admin()&&a_code.trim().length()<1){
				sqlstr.append(" or r5014=1)");
				strwhere.append(" or r5014=1)");
			}
			String str = sqlWhere(searchstr);
			if (str != null && !"".equals(str)) {
				sqlstr.append(" and (" + str + ")");
				strwhere.append(" and (" + str + ")");
			}
			
			strwhere.append(" and ((r5022<>'01' and "+Sql_switcher.isnull("r5037", "'0'")+"<>'1')");
			strwhere.append(" or (r5022='01' and "+Sql_switcher.isnull("r5037", "'0'")+"<>'1')");
			strwhere.append(" or (r5022<>'01' and "+Sql_switcher.isnull("r5037", "'0'")+"='1'))");
			sqlstr.append(" and ((r5022<>'01' and "+Sql_switcher.isnull("r5037", "'0'")+"<>'1')");
			sqlstr.append(" or (r5022='01' and "+Sql_switcher.isnull("r5037", "'0'")+"<>'1')");
			sqlstr.append(" or (r5022<>'01' and "+Sql_switcher.isnull("r5037", "'0'")+"='1'))");
			sqlstr.append(" order by norder,r5000");
			
			ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
			String diyType = constant.getNodeAttributeValue("/param/diy_course", "codeitemid");
			
			if(!VfsService.existPath())
	            throw new GeneralException("没有配置多媒体存储路径！");
			
			this.formHM.put("diyType", diyType);
			
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		//判断用户是否购买能力素质模块
		EncryptLockClient lock=(EncryptLockClient) this.getFormHM().get("lock");
		String flag = String.valueOf(lock.isHaveBM(36));
		this.getFormHM().put("sqlstr", sqlstr.toString());
		this.getFormHM().put("itemlist", fieldList);
		this.getFormHM().put("tablename", "r50");
		this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
		this.getFormHM().put("strsql", strsql.toString());
		this.getFormHM().put("columns", columns.toString());
		this.getFormHM().put("strwhere", strwhere.toString());
		this.getFormHM().put("flag", flag);
	}

	public String sqlWhere(String search) {
		StringBuffer sqlwhere = new StringBuffer();

		if (search != null && search.trim().length() > 0) {
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				String sexpr = searcharr[0];
				String sfactor = searcharr[1];
				try {
					boolean blike = false;
					blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true
							: false;
					FactorList factor = new FactorList(sexpr, sfactor, "",
							true, blike, true, 1, "su");
					String wherestr = factor.getSqlExpression();
					if (wherestr.indexOf("WHERE") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("WHERE") + 5, wherestr
										.length());
					else if (wherestr.indexOf("where") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("where") + 5, wherestr
										.length());
					wherestr = wherestr.replaceAll("A01", "r50");
					sqlwhere.append(wherestr);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
		}
		return sqlwhere.toString();
	}
	
	private String getWhereCode(String a_code){
		String tmpCodes="";
		//if(!userView.isSuper_admin()){
			TrainCourseBo tbo = new TrainCourseBo(this.userView);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select codeitemid,b0110 from codeitem where codesetid='55'");
			if(a_code.trim().length()>0){
				sqlstr.append(" and (codeitemid like '"+a_code+"%')");
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sqlstr.toString());
				while(this.frowset.next()){
					String b0110=this.frowset.getString("b0110");
					if(tbo.isUserParent(b0110)!=-1){
						tmpCodes+=this.frowset.getString("codeitemid")+",";
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//}
		if(tmpCodes!=null&&tmpCodes.length()>0){
			tmpCodes=tmpCodes.substring(0, tmpCodes.length()-1);
			if(a_code.trim().length()>0)
				tmpCodes=" and r5004 in ('"+tmpCodes.replaceAll(",", "','")+"')";
			else
				tmpCodes=" and (r5004 in ('"+tmpCodes.replaceAll(",", "','")+"')"+" or "+Sql_switcher.isnull("r5004", "'0'")+"='0')";
		}else if(a_code.trim().length()>0){
			if(a_code.trim().length()>0)
				tmpCodes=" and r5004<>''";
		}else{
			tmpCodes=" and "+Sql_switcher.isnull("r5004", "'0'")+"='0'";
		}
		return tmpCodes;
	}
	
}
