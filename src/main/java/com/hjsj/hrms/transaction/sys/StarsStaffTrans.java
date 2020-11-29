package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

public class StarsStaffTrans extends IBusiness {

	public void execute() throws GeneralException {
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"SYS_OTH_PARAM","param");
		String complex_id = constantXml.getTextValue(Sys_Oth_Parameter.COMPLEX_ID);
		complex_id = complex_id == null?"":complex_id;
		if (complex_id.trim().length() == 0 || "#".equals(complex_id)) {
			complex_id = sysbo.getValue(Sys_Oth_Parameter.COMPLEX_ID_NUM);
			complex_id = complex_id == null?"":complex_id;
			if (complex_id.trim().length() == 0){
				complex_id = "";
			}
		}
		
		String complex_expr = "";
		if (complex_id.trim().length() > 0) {
			String sql = "select id,name from lexpr where id='" + complex_id + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					complex_expr = this.frowset.getString("name");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.getFormHM().put("complex_expr", complex_expr);
		this.getFormHM().put("complex_id", complex_id);
		String sql = "";
		ArrayList fdescList = new ArrayList();// 确定为是指标的描述内容部分
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String staff_info = constantXml.getTextValue(Sys_Oth_Parameter.STAFF_INFO);
		staff_info = staff_info == null?"":staff_info;
		if (staff_info.trim().length() <= 0) {
			staff_info=sysbo.getValue(Sys_Oth_Parameter.STAFF_INFO_NUM);
			staff_info = staff_info == null?"":staff_info;
		}
		
		String fieldsetids = "", itemids = "";// 指标所属信息集和指标编码
		ArrayList peopledesc = new ArrayList();// 描述信息
		ArrayList photourl = new ArrayList();// 照片地址
		ArrayList starfields = new ArrayList();// 明星员工信息浏览页面的指标
		try {
			FieldItem fieldItem_B0110 = DataDictionary.getFieldItem("B0110");
			String b0110desc = fieldItem_B0110.getItemdesc();
			FieldItem fieldItem_E01A1 = DataDictionary.getFieldItem("E01A1");
			String e01a1desc = fieldItem_E01A1.getItemdesc();
			FieldItem fieldItem_E0122 = DataDictionary.getFieldItem("E0122");
			String e0122desc = fieldItem_E0122.getItemdesc();
			FieldItem fieldItem_A0101 = DataDictionary.getFieldItem("A0101");
			String a0101desc = fieldItem_A0101.getItemdesc();
			if ("".equals(staff_info)) {
				staff_info = b0110desc+"：["+b0110desc+"]<br>"+e0122desc+"：["+e0122desc+"]<br>"+a0101desc+"：["+a0101desc+"]<br>";
			}
			
			String sti[] = staff_info.split("\\[");
			for (int j = 0; j < sti.length; j++) {
				String st[] = sti[j].split("\\]");
				if (b0110desc.equalsIgnoreCase(st[0])) {
					fieldsetids += "A01,";
					itemids += "A01.B0110,";
					fdescList.add("[" + st[0] + "]");
				} else if (e01a1desc.equalsIgnoreCase(st[0])) {
					fieldsetids += "A01,";
					itemids += "A01.E01A1,";
					fdescList.add("[" + st[0] + "]");
				} else {
					sql = "select fieldsetid,itemid from fielditem where itemdesc ='" + st[0] + "' and fieldsetid like 'A%' and useflag='1' ";
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						fieldsetids += this.frowset.getString("fieldsetid") + ",";
						itemids += this.frowset.getString("fieldsetid") + "." + this.frowset.getString("itemid") + ",";
						fdescList.add("[" + st[0] + "]");
					}
				}
			}

			String fieldstr = "B0110,E0122,E01A1,A0101";
			String[] f = fieldstr.split(",");
			for (int i = 0; i < f.length; i++) {//在信息浏览页面这四项是必须要获取值的
				FieldItem fieldItem_O = DataDictionary.getFieldItem(f[i]);
				if (null != fieldItem_O) {
					FieldItem fieldItem = (FieldItem) fieldItem_O.clone();
					fieldItem.setDisplaywidth(fieldItem.getDisplaywidth() * 12);
					fieldItem.setVisible(true);
					starfields.add(fieldItem);
				}
			}
			String a0100s = "";
			RowSet rs2 = null;
			String fitem[] = new String[] {};
			ArrayList descList = new ArrayList();
			if (itemids.trim().length() != 0) {
				fitem = itemids.split(",");
			}
			String strsql=getCondWhere(complex_id, itemids);
			if(!"".equalsIgnoreCase(strsql)){
				strsql = strsql+" order by i,A0000";
				rs2 = dao.search(strsql);
				while (rs2.next()) {
					a0100s += rs2.getString("nbase") + ":" + rs2.getString("a0100") + ",";//库类型和人员编号
					String itemdesc = "";
					for (int i = 0; i < fitem.length; i++) {//在信息描述中出现的所有有效指标
						String itemid[] = fitem[i].split("\\.");
						FieldItem item= (FieldItem)DataDictionary.getFieldItem(itemid[1]);
						String itemtype = item.getItemtype();
						if("D".equalsIgnoreCase(itemtype)){
							itemdesc += rs2.getDate(itemid[1]) + ",";
						}else{
							if("".equalsIgnoreCase(rs2.getString(itemid[1]))){//zgd 2014-4-17 修改内容为“”的指标值，使后面的descofitem中有值。
								itemdesc += "null,";
							}else{
								itemdesc += rs2.getString(itemid[1]) + ",";
							}
						}
					}
					
					if(StringUtils.isEmpty(itemdesc))
					    itemdesc = itemdesc.replace("\n", "");
					
					descList.add(itemdesc);
				}
			}
			String dba0100s[] = new String[] {};
			if (a0100s.trim().length() != 0) {
				dba0100s = a0100s.split(",");
			}
			PhotoImgBo pib = new PhotoImgBo(this.frameconn);
			String absPath = "";
            try{
                absPath = pib.getPhotoRootDir();
            }catch(Exception ex){
            }
			
            for (int j = 0; j < dba0100s.length; j++) {
                staff_info = constantXml.getTextValue(Sys_Oth_Parameter.STAFF_INFO);
                staff_info = staff_info == null ? "" : staff_info;
                if (staff_info.trim().length() == 0) {
                    staff_info = sysbo.getValue(Sys_Oth_Parameter.STAFF_INFO_NUM);
                    staff_info = staff_info == null ? "" : staff_info;
                    if ("".equals(staff_info)) {
                        staff_info = b0110desc + "：[" + b0110desc + "]<br>" + e0122desc + "：["
                                + e0122desc + "]<br>" + a0101desc + "：[" + a0101desc + "]<br>";
                    }
                }
                while (staff_info.indexOf("\r\n") != -1) {
                    staff_info = staff_info.replace("\r\n", "<br>");
                }

                String dbprea0100[] = dba0100s[j].split(":");
                String a0100 = dbprea0100[1];
                String dbpre = dbprea0100[0];
                String fielditem[] = itemids.split(",");

                String filename = "";
                if ("".equals(a0100))
                    continue;
                //{} else {}
                boolean genPhotoSuccess = false;
                /*判断多媒体路径是否存在,如果存在，获取多媒体路径下的图片*/
                if (absPath != null && absPath.length() > 0) {
                		String realPath = absPath + pib.getPhotoRelativeDir(dbpre, a0100);
                		String guid = pib.getGuid();
                        //获取 文件名为 “photo.xxx”的文件，格式未知
                    String fileWName = pib.getPersonImageWholeName(realPath, "photo");
                     // 如果不存在文件，创建文件
                    if (fileWName.length() < 1) {
                        fileWName = pib.createPersonPhoto(realPath, this.frameconn, dbpre,
                                    a0100, "photo");
                    }
                    
                  //如果有图片或创建了图片，使用新图片
                    if (fileWName.length() > 0) {
                    		realPath += fileWName;
                        filename = ("/servlet/DisplayOleContent?byTrans=1&perguid="+guid);
                        this.userView.getHm().put(guid, realPath);
                        genPhotoSuccess = true;
                    }
                }
                
                if(!genPhotoSuccess){
                		filename = ServletUtilities.createPhotoFile(dbpre + "A00", a0100, "P", null);
                		if (!"".equals(filename)) { // 首页展示模块，明星员工图片不显示 jingq upd
                            // 2014.10.17
					    filename = "/servlet/DisplayOleContent?filename=" + PubFunc.encrypt(filename);
					} else {
					    filename = "/images/photo.jpg";
					}
                }
                
                photourl.add(filename);
                String descs = (String) descList.get(j);
                String descofitem[] = descs.split(",");
                for (int m = 0; m < fielditem.length; m++) {
                    if (fielditem[m] == null || "".equalsIgnoreCase(fielditem[m]))
                        continue;
                    
                    String nbaseitemid[] = fielditem[m].split("\\.");
                    String fitemid = nbaseitemid[1];

                    if (fitemid.length() < 4)
                        continue;
                    
                    FieldItem fieldItem_O = DataDictionary.getFieldItem(fitemid);
                    if (j == 0) {
                        if (null != fieldItem_O && !("B0110".equalsIgnoreCase(fitemid) || "E0122".equalsIgnoreCase(fitemid)
                                        || "E01A1".equalsIgnoreCase(fitemid) || "A0101".equalsIgnoreCase(fitemid))) {
                            FieldItem fieldItem = (FieldItem) fieldItem_O.clone();
                            fieldItem.setDisplaywidth(fieldItem.getDisplaywidth() * 12);
                            fieldItem.setVisible(true);
                            starfields.add(fieldItem);
                        }
                    }

                    if ("E01A1".equalsIgnoreCase(fitemid) || "B0110".equalsIgnoreCase(fitemid)
                            || "E0122".equalsIgnoreCase(fitemid)) {
                        String codesetid_type = "";
                        if ("E01A1".equalsIgnoreCase(fitemid)) {
                            codesetid_type = "@K";
                        }
                        
                        if ("B0110".equalsIgnoreCase(fitemid)) {
                            codesetid_type = "UN";
                        }
                        
                        if ("E0122".equalsIgnoreCase(fitemid)) {
                            codesetid_type = "UM";
                        }
                        
                        String itemdesc = AdminCode.getCodeName(codesetid_type, descofitem[m]);
                        staff_info = staff_info.replace(staff_info.substring(staff_info.indexOf((String) fdescList.get(m)),
                                staff_info.indexOf((String) fdescList.get(m)) + fdescList.get(m).toString().length()), itemdesc);
                        continue;
                    }

                    String codesetid = fieldItem_O.getCodesetid();
                    if (codesetid == null || "".equals(codesetid) || "0".equals(codesetid)) {
                        String desc = "";
                        if (descofitem[m] == null || "".equals(descofitem[m]) || "null".equalsIgnoreCase(descofitem[m])) {
                            desc = "";
                        } else {
                            desc = descofitem[m];
                        }
                        
                        staff_info = staff_info.replace(staff_info.substring(staff_info.indexOf((String) fdescList.get(m)), 
                                staff_info.indexOf((String) fdescList.get(m)) + fdescList.get(m).toString().length()), desc);
                    } else {
                        String itemdesc = AdminCode.getCodeName(codesetid, descofitem[m]);
                        staff_info = staff_info.replace(staff_info.substring(staff_info.indexOf((String) fdescList.get(m)), 
                                staff_info.indexOf((String) fdescList.get(m)) + fdescList.get(m).toString().length()), itemdesc);
                    }
                }
                
                peopledesc.add(staff_info);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("starfields", starfields);
			this.getFormHM().put("photourl", photourl);
			this.getFormHM().put("peopledesc", peopledesc);
		}
	}

	private String getCondWhere(String stock_cond, String itemids) {
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		int peopleNumber = 0;
		String A01 = "";
		if (login_vo != null) {
			A01 = login_vo.getString("str_value").toLowerCase();
		}
		
		if ("".equals(A01)) {
			return "";
		}
		
		String dbp[] = A01.split(",");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String userbase = "";
		String sql = "";
		String strsql = "";
		String itemlists = "";
		try {
			String fitem[] = new String[] {};
			if (itemids.trim().length() != 0) {
				fitem = itemids.split(",");
			}
			
			for (int i = 0; i < dbp.length; i++) {
				if("".equalsIgnoreCase(stock_cond)){
					break;
				}
				
				sql = "";
				userbase = dbp[i];
				StringBuffer wherestring = new StringBuffer();
				for (int m = 0; m < fitem.length; m++) {
					String nbaseitemid[] = fitem[m].split("\\.");
					if ("B0110".equalsIgnoreCase(nbaseitemid[1]) || "E0122".equalsIgnoreCase(nbaseitemid[1])
							|| "E01A1".equalsIgnoreCase(nbaseitemid[1]) || "A0101".equalsIgnoreCase(nbaseitemid[1])) {
					} else {
						if (i == 0) {
							itemlists += "," + nbaseitemid[1];
						}
						
						if (!"A01".equalsIgnoreCase(nbaseitemid[0])) {
							StringBuffer wherestr = new StringBuffer(" left join (select " + userbase + "A01.A0100," + nbaseitemid[1]
											+ " from " + userbase + "A01 left join " + userbase + nbaseitemid[0] + " on "
											+ userbase + "A01.A0100=" + userbase + nbaseitemid[0] + ".A0100 where 1=1 ");
							wherestr.append("and " + userbase + nbaseitemid[0] + ".i9999=(select max(i9999) from " + userbase + nbaseitemid[0] + " t" + m
									+ " where t" + m + ".a0100=" + userbase + "A01.a0100)) " + nbaseitemid[0] + m + " on " + nbaseitemid[0] + m
									+ ".A0100=" + userbase + "A01.A0100");
							wherestring.append(wherestr);
						}
					}
				}
				
				String strwhere = "";
				RecordVo vo = new RecordVo("lexpr");
				vo.setString("id", stock_cond);
				vo = dao.findByPrimaryKey(vo);
				String expr = vo.getString("lexpr");
				String factor = vo.getString("factor");
				factor = factor.replaceAll("\\$THISMONTH\\[\\]", "当月");
				String history = vo.getString("history");
				if (history == null || "".equals(history))
					history = "0";
				
				boolean bhis = false;
				if ("1".equals(history))
					bhis = true;
				
				FactorList factorlist = new FactorList(expr, factor, userbase, bhis, false, true, 1, userView.getUserId());
				strwhere = factorlist.getSqlExpression();
				int strwhereIndex = strwhere.toUpperCase().indexOf("WHERE");
				String fromStr =strwhere.substring(0,strwhereIndex);
				strwhere =" " +  strwhere.substring(strwhereIndex);
				
			//	strwhere = strwhere.substring(strwhereIndex);
				/*
				 * guodd 2016-08-20 oracle下分页出问题
				 * 在oracle 下 select * from usrA01 where a0100 in (select a0100 from usrA01 where ......)这种sql遇到rownum分页时出不来数据
				 * 换成select * from usrA01 where ......这种没问题
				 * strwhere = " " + userbase + "A01.a0100 in (select " + userbase
						+ "A01.A0100 " + strwhere + ")";
				sql = "select " + userbase + "A01.a0100," + i + " as i,'" + userbase + "' as nbase,a0000,B0110,E0122,E01A1,A0101"
						+ itemlists + " from " + userbase + "A01 " + wherestring + " where " + strwhere;
				*/
				sql = "select " + userbase + "A01.a0100," + i + " as i,'" + userbase + "' as nbase,a0000,B0110,E0122,E01A1,A0101"
						+ itemlists + fromStr + wherestring + strwhere;
				this.frowset = dao.search(sql);
				while (this.frowset.next()) {
					peopleNumber++;
				}
				strsql += sql + " union ";
			}
			if(strsql.trim().length()!=0){
				strsql = strsql.substring(0, strsql.length() - 7);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("strsql", strsql);
			this.getFormHM().put("peopleNumber", String.valueOf(peopleNumber));
			this.getFormHM().put("columns",
					"a0000,nbase,A0100,B0110,E0122,E01A1,A0101" + itemlists);
			this.getFormHM().put("order", " order by i,A0000");
		}
		return strsql;
	}
}
