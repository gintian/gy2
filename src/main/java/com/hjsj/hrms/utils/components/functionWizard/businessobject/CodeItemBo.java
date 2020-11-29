package com.hjsj.hrms.utils.components.functionWizard.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeItemBo {
	private UserView uv;
	private Connection conn;
	private ArrayList hfactorlist = new ArrayList();
	private ArrayList vfactorlist = new ArrayList();
	private ArrayList s_hfactorlist = new ArrayList();
	private ArrayList s_vfactorlist = new ArrayList();
	private ArrayList itemidlist = new ArrayList();
	private String hfcodesetid;
	private String vfcodesetid;
	private String s_hfcodesetid;
	private String s_vfcodesetid;
	private String itemtype;
	
	public CodeItemBo(Connection conn,UserView uv){
		this.conn = conn;
		this.uv = uv;
	}
	/**
	 * 从薪资类别中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public void getMidVariableList(String salaryid){
		try{
			ContentDAO dao  = new ContentDAO(conn);
			RowSet rset=dao.search("select * from salaryset where salaryid='"+salaryid+"'");
			StringBuffer itemstr=new StringBuffer();
			HashMap map = new HashMap();
			while(rset.next()){
				String codeid = rset.getString("CODESETID");
				String type = rset.getString("ITEMTYPE"); 
				String ITEMDESC = rset.getString("ITEMDESC"); 
				ITEMDESC=ITEMDESC!=null?ITEMDESC:"";
				
				if("photo".equalsIgnoreCase(ITEMDESC))
					continue;
				if("ext".equalsIgnoreCase(ITEMDESC))
					continue;
				if(itemstr.indexOf(rset.getString("ITEMID"))!=-1){
					continue;
				}
				itemstr.append(rset.getString("ITEMID")+",");
				if(codeid!=null&&codeid.trim().length()>0){
					map = new HashMap();
					map.put("id", rset.getString("ITEMID")+":"+rset.getString("ITEMDESC"));
					map.put("name", rset.getString("ITEMDESC"));
					if(codeid.equalsIgnoreCase(hfcodesetid)){
						hfactorlist.add(map);
					}
					if(codeid.equalsIgnoreCase(vfcodesetid)){
						vfactorlist.add(map);
					}
					if(codeid.equalsIgnoreCase(s_hfcodesetid)){
						s_hfactorlist.add(map);
					}
					if(codeid.equalsIgnoreCase(s_vfcodesetid)){
						s_vfactorlist.add(map);
					}
					if(type.equalsIgnoreCase(itemtype)){
						itemidlist.add(map);
					}
				}
			}
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,CodeSetID from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("')");
			rset = dao.search(buf.toString());
			while(rset.next()){
				String codeid = rset.getString("CodeSetID");
				String type = rset.getString("ntype");
				String chz = rset.getString("chz");
				chz=chz!=null?chz:"";
				
				if("photo".equalsIgnoreCase(chz))
					continue;
				if("ext".equalsIgnoreCase(chz))
					continue;
				if(itemstr.indexOf(rset.getString("cname"))!=-1){
					continue;
				}
				itemstr.append(rset.getString("cname")+",");
				if(codeid!=null&&codeid.trim().length()>0){
					map = new HashMap();
					map.put("id", rset.getString("cname")+":"+rset.getString("chz"));
					map.put("name", rset.getString("chz"));
					if(codeid.equalsIgnoreCase(hfcodesetid)){
						hfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(vfcodesetid)){
						vfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(s_hfcodesetid)){
						s_hfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(s_vfcodesetid)){
						s_vfactorlist.add(map);
					}
					if(numToType(type).equalsIgnoreCase(itemtype)){
						itemidlist.add(map);
					}
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * 从薪资类别中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public void getSalarySetList(String salaryid){
		try{
			ContentDAO dao  = new ContentDAO(conn);
			RowSet rset=dao.search("select * from salaryset where salaryid='"+salaryid+"'");
			StringBuffer itemstr=new StringBuffer();
			HashMap map = new HashMap();
			while(rset.next()){
				String codeid = rset.getString("CODESETID");
				String type = rset.getString("ITEMTYPE"); 
				String ITEMDESC = rset.getString("ITEMDESC"); 
				ITEMDESC=ITEMDESC!=null?ITEMDESC:"";
				
				if("photo".equalsIgnoreCase(ITEMDESC))
					continue;
				if("ext".equalsIgnoreCase(ITEMDESC))
					continue;
				if(itemstr.indexOf(rset.getString("ITEMID"))!=-1){
					continue;
				}
				itemstr.append(rset.getString("ITEMID")+",");
				if(codeid!=null&&codeid.trim().length()>0){
					map = new HashMap();
					map.put("id", rset.getString("ITEMID")+":"+rset.getString("ITEMDESC"));
					map.put("name", rset.getString("ITEMDESC"));
					if(codeid.equalsIgnoreCase(hfcodesetid)){
						hfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(vfcodesetid)){
						vfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(s_hfcodesetid)){
						s_hfactorlist.add(map);
					}else if(codeid.equalsIgnoreCase(s_vfcodesetid)){
						s_vfactorlist.add(map);
					}
					if(type.equalsIgnoreCase(itemtype)){
						itemidlist.add(map);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * 从模板自定义表中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public void getTableList(String tabid){
		ArrayList itemlist = itemList(tabid);
		try{
			StringBuffer itemstr=new StringBuffer();
			HashMap map = new HashMap();
			for(int i=0;i<itemlist.size();i++){
				FieldItem item = (FieldItem)itemlist.get(i);
				if(item!=null){
					if("photo".equalsIgnoreCase(item.getItemdesc()))
						continue;
					if("ext".equalsIgnoreCase(item.getItemdesc()))
						continue;
					if(itemstr.indexOf(item.getItemid()+"_"+item.getNChgstate())!=-1){
						continue;
					}
					itemstr.append(item.getItemid()+"_"+item.getNChgstate()+",");
					String codeid = item.getCodesetid();
					String type = item.getItemtype();
					if(codeid!=null&&codeid.trim().length()>0){
						map = new HashMap();
						map.put("id", item.getItemid()+"_"+item.getNChgstate()+":"+item.getItemdesc());
						map.put("name", item.getItemdesc());
						if(codeid.equalsIgnoreCase(hfcodesetid)){
							hfactorlist.add(map);
						}else if(codeid.equalsIgnoreCase(vfcodesetid)){
							vfactorlist.add(map);
						}else if(codeid.equalsIgnoreCase(s_hfcodesetid)){
							s_hfactorlist.add(map);
						}else if(codeid.equalsIgnoreCase(s_vfcodesetid)){
							s_vfactorlist.add(map);
						}
						if("N".equals(itemtype) && type.equalsIgnoreCase(itemtype)){
							itemidlist.add(map);
						}else if("A".equals(itemtype) && !"0".equals(codeid) && type.equalsIgnoreCase(itemtype)) {
							itemidlist.add(map);
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
     * 查询子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	public ArrayList functionList(){
		ArrayList fielditemlist=new ArrayList();//原先写了这个返回值，可能是为了取循环的集合，不过用不到
		loopList();
		return fielditemlist;
	}
	
	 /**
     * 查询临时变量+子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	public ArrayList functionList(String mode,String keyid){
		ArrayList fielditemlist=new ArrayList();
		try {
			loopList();
			if("1".equals(mode) || "2".equals(mode)) { //薪资获取自己账套的临时变量
				StringBuffer buf=new StringBuffer();
				ArrayList list = new ArrayList();
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate=? ) order by sorting");
				list.add(keyid);
				getMidVarList(conn,buf,list);
			}else if("3".equals(mode)) {//人事异动获取自己模板的临时变量
				StringBuffer buf=new StringBuffer();
				ArrayList list = new ArrayList();
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetId <> 0 and (templetid=? or cstate = '1')");
				list.add(keyid);
				getMidVarList(conn,buf,list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fielditemlist;
	}
	
	/**
	 * 循环需要的代码指标，放入全局变量横纵等指标中
	 * @param listset
	 */
	private void loopList() {
		StringBuffer itemstr=new StringBuffer();
		HashMap map = new HashMap();
		try {
			ArrayList listset = uv.getPrivFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				 ArrayList listitem= DataDictionary.getFieldList(fieldset.getFieldsetid(),Constant.USED_FIELD_SET);
				 if(listitem==null)
					 continue;
				 for(int j=0;j<listitem.size();j++){
					 FieldItem item = (FieldItem)listitem.get(j);
					 if(item==null)
						 continue;
					 if("photo".equalsIgnoreCase(item.getItemdesc()))
						 continue;
					 if("ext".equalsIgnoreCase(item.getItemdesc()))
						 continue;
					 if(itemstr.indexOf(item.getItemid())!=-1){
						 continue;
					 }
					 itemstr.append(item.getItemid()+",");
					 String codeid = item.getCodesetid();
					 String type = item.getItemtype();
					 map = new HashMap();
					 map.put("id", item.getItemid()+":"+item.getItemdesc());
					 map.put("name", item.getItemdesc());
	
					 if(codeid.equalsIgnoreCase(hfcodesetid)){
						hfactorlist.add(map);
					 }
					 if(codeid.equalsIgnoreCase(vfcodesetid)){
						vfactorlist.add(map);
					 }
					 if(codeid.equalsIgnoreCase(s_hfcodesetid)){
						s_hfactorlist.add(map);
					 }
					 if(codeid.equalsIgnoreCase(s_vfcodesetid)){
						s_vfactorlist.add(map);
					 }
				 	 if(type.equalsIgnoreCase(itemtype)){
						itemidlist.add(map);
					 }
				 }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVarList(Connection conn,StringBuffer buf,ArrayList list){
		ArrayList fieldlist=new ArrayList();
		StringBuffer itemstr=new StringBuffer();
		RowSet rset = null;
		try{
			ContentDAO dao=new ContentDAO(conn);
			rset=dao.search(buf.toString(),list);
			while(rset.next())
			{
				String itemid = rset.getString("cname");
				String name = rset.getString("chz");
				String itemtype = "";
				String codeSetId = "";
				switch(rset.getInt("ntype"))
				{
					case 1://
						itemtype = "N";
						codeSetId = "0";
						break;
					case 2:
						itemtype = "A";
						codeSetId = "0";
						break;
					case 3:
						itemtype = "D";
						codeSetId = "0";
						break;
					case 4:
						itemtype = "A";
						codeSetId = rset.getString("codesetid");
						break;
				}
				
				 if(name==null)
					 continue;
				 if("photo".equalsIgnoreCase(name))
					 continue;
				 if("ext".equalsIgnoreCase(name))
					 continue;
				 if(itemstr.indexOf(itemid)!=-1){
					 continue;
				 }
				 itemstr.append(itemid+",");
				 HashMap map = new HashMap();
				 map.put("id", itemid+":"+name);
				 map.put("name", name);

				 if(codeSetId.equalsIgnoreCase(hfcodesetid)){
					hfactorlist.add(map);
				 }
				 if(codeSetId.equalsIgnoreCase(vfcodesetid)){
					vfactorlist.add(map);
				 }
				 if(codeSetId.equalsIgnoreCase(s_hfcodesetid)){
					s_hfactorlist.add(map);
				 }
				 if(codeSetId.equalsIgnoreCase(s_vfcodesetid)){
					s_vfactorlist.add(map);
				 }
			 	 if(itemtype.equalsIgnoreCase(itemtype)){
					itemidlist.add(map);
				 }
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return fieldlist;
	}
	
	private ArrayList itemList(String tableid){
		ArrayList itemlist = new ArrayList();
		if(tableid.length()>0){
			try {
				TemplateTableBo changebo = new TemplateTableBo(conn,Integer.parseInt(tableid),uv);
				ArrayList list = changebo.getAllFieldItem();
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				if(b0110item!=null){
					itemlist.add(b0110item);
				}else{
					b0110item.setFieldsetid("A01");
					b0110item.setItemid("B0110");
					b0110item.setItemdesc("单位名称");
					b0110item.setItemtype("A");
					b0110item.setCodesetid("UN");
					itemlist.add(b0110item);
				}
				
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					if("photo".equalsIgnoreCase(fielditem.getItemdesc()))
						continue;
					if("ext".equalsIgnoreCase(fielditem.getItemdesc()))
						continue;
					if(fielditem.isChangeAfter()){
						String itemdesc ="拟"+fielditem.getItemdesc();
						fielditem.setItemdesc(itemdesc);
					}else if(fielditem.isChangeBefore()){
						if(!"A01".equalsIgnoreCase(fielditem.getFieldsetid())){
							String itemdesc="现"+fielditem.getItemdesc();
							fielditem.setItemdesc(itemdesc);
						}
					}
					itemlist.add(fielditem);
				}
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(conn,tableid);
		itemlist.addAll(templist);
		return itemlist;
	}
	public String numToType(String numtype){
		String type="A";
		if("1".equals(numtype))
			type="N";
		else if("2".equals(numtype))
			type="A";
		else if("3".equals(numtype))
			type="D";
		else if("4".equals(numtype))
			type="A";
		return type;
	}
	public ArrayList getHfactorlist() {
		return hfactorlist;
	}
	public void setHfactorlist(ArrayList hfactorlist) {
		this.hfactorlist = hfactorlist;
	}
	public ArrayList getItemidlist() {
		return itemidlist;
	}
	public void setItemidlist(ArrayList itemidlist) {
		this.itemidlist = itemidlist;
	}
	public ArrayList getS_hfactorlist() {
		return s_hfactorlist;
	}
	public void setS_hfactorlist(ArrayList s_hfactorlist) {
		this.s_hfactorlist = s_hfactorlist;
	}
	public ArrayList getS_vfactorlist() {
		return s_vfactorlist;
	}
	public void setS_vfactorlist(ArrayList s_vfactorlist) {
		this.s_vfactorlist = s_vfactorlist;
	}
	public ArrayList getVfactorlist() {
		return vfactorlist;
	}
	public void setVfactorlist(ArrayList vfactorlist) {
		this.vfactorlist = vfactorlist;
	}
	public String getHfcodesetid() {
		return hfcodesetid;
	}
	public void setHfcodesetid(String hfcodesetid) {
		this.hfcodesetid = hfcodesetid;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getS_hfcodesetid() {
		return s_hfcodesetid;
	}
	public void setS_hfcodesetid(String s_hfcodesetid) {
		this.s_hfcodesetid = s_hfcodesetid;
	}
	public String getS_vfcodesetid() {
		return s_vfcodesetid;
	}
	public void setS_vfcodesetid(String s_vfcodesetid) {
		this.s_vfcodesetid = s_vfcodesetid;
	}
	public String getVfcodesetid() {
		return vfcodesetid;
	}
	public void setVfcodesetid(String vfcodesetid) {
		this.vfcodesetid = vfcodesetid;
	}
}
