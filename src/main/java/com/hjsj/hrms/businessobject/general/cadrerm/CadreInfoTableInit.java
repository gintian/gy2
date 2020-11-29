/**
 * 
 */
package com.hjsj.hrms.businessobject.general.cadrerm;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 干部任免信息初始化
 * @author Owner
 *
 */
public class CadreInfoTableInit {

	private Connection conn;
	private String dbPre;
	private String url;
	
	/**
	 * 构造器
	 * @param conn		DB连接
	 * @param dbPre     人员库前缀
	 */
	public CadreInfoTableInit(Connection conn ,String dbPre) {
		this.conn = conn;
		this.dbPre = dbPre;
	}
	
	/**
	 * 构造器
	 * @param conn		DB连接
	 * @param dbPre     人员库前缀
	 */
	public CadreInfoTableInit(Connection conn ,String dbPre,String url) {
		this.conn = conn;
		this.dbPre = dbPre;
		this.url = url;
	}
	
	/**
	 * 获得干部图片信息流
	 * @param cadreId
	 * @return
	 */
	public InputStream getCadreImageStream(String cadreId){
		InputStream image=null;
		String sql="select ole from " + dbPre+"A00 where a0100='"+cadreId+"'and " +
				"i9999=(select max(i9999) from "+dbPre+"a00 where a0100='"+cadreId+"')" ;
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				 image = rs.getBinaryStream("ole");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}	
		return image;
	}
	
	/**
	 * 获得干部图片信息
	 * @param cadreId
	 * @return
	 */
	public String getCadreImageStr(String cadreId) {
		StringBuffer imageInfo = new StringBuffer();		
		String sql = "select ole from " + dbPre + "A00 where a0100='" + cadreId
				+ "'and " + "i9999=(select max(i9999) from " + dbPre
				+ "a00 where a0100='" + cadreId + "')";
		ResultSet rs = null;
		InputStream image = null;
		Statement stmt = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if (rs.next()) {
				image = rs.getBinaryStream("ole");
			}
			byte[] b = new byte[1024];
			while ((image.read(b)) != -1) {
				String temp = new String(b);
				imageInfo.append(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(image);
			PubFunc.closeResource(rs);
			PubFunc.closeResource(stmt);
		}
		return imageInfo.toString();
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public String cadreImagePath(String url,String cadreId){
		
		String filename="";
		try {
			filename = ServletUtilities.createPhotoFile(dbPre+"A00",cadreId,"P",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        StringBuffer photourl=new StringBuffer();
        if(!"".equals(filename)){
        	photourl.append(url);
        	photourl.append("/servlet/DisplayOleContent?filename=");
        	photourl.append(filename);
        }else{
        	photourl.append("/images/photo.jpg");
        }
        
        StringBuffer str_html=new StringBuffer();
        str_html.append("<img src=\"");
        str_html.append(photourl.toString());
        str_html.append("\" ");
        str_html.append(" height=\"");
        str_html.append("170");
        str_html.append("\" width=\"");
        str_html.append("115");
        str_html.append("\" border=1></img>");
        
		return str_html.toString();
	}

	/**
	 * 初始化干部信息
	 * @param cadreId
	 * @return
	 */
	public CadreInfoTable initCadreTableInfo(String cadreId){
		CadreInfoTable cadreInfoTable = new CadreInfoTable();
		//基本信息
		cadreInfoTable.setName(this.getCadreName(cadreId));
		cadreInfoTable.setSex(this.getCadreSex(cadreId));
		cadreInfoTable.setBirthDate(this.getBirthDate(cadreId));
		cadreInfoTable.setAge(this.getCadreAge(cadreId));
		cadreInfoTable.setNation(this.getNation(cadreId));
		if(this.url != null){
			cadreInfoTable.setImagePath(this.cadreImagePath(this.url,cadreId));
		}
		cadreInfoTable.setBirthAddress(this.getBirthAddress(cadreId));
		cadreInfoTable.setBodyStatus(this.getBodyStatus(cadreId));
		cadreInfoTable.setAddress(this.getAddress(cadreId));
		cadreInfoTable.setEnterWorkDate(this.getEnterWorkDate(cadreId));
		cadreInfoTable.setEnterPartyDate(this.getEnterPartyDate(cadreId));
		cadreInfoTable.setDuty(this.getDuty(cadreId));
		cadreInfoTable.setTeChang(this.getTeChang(cadreId));
		
		//学历
		cadreInfoTable.setQrzSchoolAge(this.getSchoolAge(cadreId,"1"));
		cadreInfoTable.setQrzDegree(this.getDegree(cadreId,"1"));
		cadreInfoTable.setQrzSchool(this.getShcool(cadreId,"1"));
		cadreInfoTable.setQrzSpecialty(this.getSpecialty(cadreId,"1"));
		cadreInfoTable.setZSchoolAge(this.getSchoolAge(cadreId,"2"));
		cadreInfoTable.setZDegree(this.getDegree(cadreId,"2"));
		cadreInfoTable.setZSchool(this.getShcool(cadreId,"2"));
		cadreInfoTable.setZSpecialty(this.getSpecialty(cadreId,"2"));
		
		//当前单位,职务
		cadreInfoTable.setCurrentDuty(this.getCurrentDuty(cadreId));
		cadreInfoTable.setCurrentOrg(this.getCurrentOrg(cadreId));
		
		//System.out.println("简历数据初始化。。。。。。。。");
		//简历
		cadreInfoTable.setResumeStartDate(this.getResumeStartDate(cadreId));
		cadreInfoTable.setResumeEndDate(this.getResumeEndDate(cadreId));
		cadreInfoTable.setResumeOrg(this.getResumeOrg(cadreId));
		cadreInfoTable.setResumeDuty(this.getResumeDuty(cadreId));
		//System.out.println("简历数据初始化结束。。。。。");
		
		//System.out.println("奖励数据初始化开始。。。。。。。");
		//奖励
		cadreInfoTable.setEncouragementName(this.getEncouragementName(cadreId));
		cadreInfoTable.setEncouragementCausation(this.getEncouragementCausation(cadreId));
		cadreInfoTable.setEncouragementDate(this.getEncouragementDate(cadreId));
		cadreInfoTable.setEncouragementOrg(this.getEncouragementOrg(cadreId));
		//System.out.println("奖励数据初始化结束。。。。。");
		
		//System.out.println("惩罚数据初始化开始。。。。。。。。。。");
		//惩罚
		cadreInfoTable.setPunishName(this.getPunishName(cadreId));
		cadreInfoTable.setPunishCausation(this.getPunishCausation(cadreId));
		cadreInfoTable.setPunishDate(this.getPunishDate(cadreId));
		cadreInfoTable.setPunishOrg(this.getPunishOrg(cadreId));
		//System.out.println("惩罚数据初始化结束。。。。。。。。。。。。。");
		
		//年度考核
		cadreInfoTable.setExamDate(this.getExamDate(cadreId));
		cadreInfoTable.setExamResult(this.getExamResult(cadreId));
		
		//培训情况
		cadreInfoTable.setFosterDate(this.getFosterDate(cadreId));
		
		
		//System.out.println("家庭成员数据初始化开始。。。。。。。。。。");
		//家庭成员
		cadreInfoTable.setFamilyName(this.getFamilyName(cadreId));
		cadreInfoTable.setFamilyRelation(this.getFamilyRelation(cadreId));
		cadreInfoTable.setFamilyBirthDate(this.getFamilyBirthDate(cadreId));
		cadreInfoTable.setFamilyZzmm(this.getFamilyZzmm(cadreId));
		cadreInfoTable.setFamilyOrg(this.getFamilyOrg(cadreId));
		cadreInfoTable.setFamilyBirthDateTxt(this.getFamilyBirthDateTxt(cadreId));
		//System.out.println("家庭成员数据初始化结束。。。。。。。。。。。。。");
		
		//System.out.println("社会关系数据初始化开始。。。。。。。。。。");
		//社会关系
		cadreInfoTable.setCommunityName(this.getCommunityName(cadreId));
		cadreInfoTable.setCommunityRelation(this.getCommunityRelation(cadreId));
		cadreInfoTable.setCommunityBirthDate(this.getCommunityBirthDate(cadreId));
		cadreInfoTable.setCommunityZzmm(this.getCommunityZzmm(cadreId));
		cadreInfoTable.setCommunityOrg(this.getCommunityOrg(cadreId));
		cadreInfoTable.setCommunityBirthDateTxt(this.getCommunityBirthDateTxt(cadreId));
		//System.out.println("社会关系数据初始化结束。。。。。。。。。。。。。");
		
		//其他
		cadreInfoTable.setFillInTableDate(this.getCurrentFormatDate());
		cadreInfoTable.setAgeDate(this.getCurrentFormatDate());		
		
		//System.out.println("000000000000000000000000000000000000000000000000000");
		return cadreInfoTable;
	}
	
	
	/**
	 * 获得干部姓名
	 * @param cadreId
	 * @return
	 */
	public String getCadreName(String cadreId){
		String name = "";
		String setid ="A01";
		String itemid="A0101";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			//System.out.println(sql);
			ResultSet rs =null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					name = rs.getString(itemid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return name;
	}
	
	/**
	 * 获得干部的性别
	 * @param cadreId
	 * @return
	 */
	public String getCadreSex(String cadreId){
		String sex = "";
		String setid ="A01";
		String itemid="A0107";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select codeitemdesc from codeitem where " +
					"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"') " +
					"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			//System.out.println(sql);
			ResultSet rs =null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					sex = rs.getString("codeitemdesc");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return sex;
	}
	
	/**
	 * 获得干部出生年月
	 * 格式(196204)
	 * @param cadreId
	 * @return
	 */
	public String getBirthDate(String cadreId){
		String birthDate = "";
		String setid ="A01";
		String itemid="A0111";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					birthDate = rs.getString(itemid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if(!"".equals(birthDate)){
			birthDate = birthDate.substring(0,4)+"."+birthDate.substring(5,7);
		}
		return birthDate;
	}
	
	/**
	 * 获得干部的年龄
	 * @param cadreId ID
	 * @return
	 */
	public String getCadreAge(String cadreId){
		String age="";
		String setid ="A01";
		String itemid="A0111";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+"("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),itemid )+")/365.00000000 as value"+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					age = rs.getString("value");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if(age == null || "".equals(age) || age.indexOf('.')==-1){
		}else{
			age = age.substring(0,age.indexOf('.'));
		}
		return age;
	}
	
	/**
	 * 获得干部的民族
	 * @param cadreId
	 * @return
	 */
	public String getNation(String cadreId){
		String nation = "";
		String setid ="A01";
		String itemid="a0121";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select codeitemdesc from codeitem where " +
					"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"') " +
					"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					nation = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return nation;
	}
	
	/**
	 * 获得干部的出生地
	 * 优先读取人员主集（A01）“出生地(描述)”指标中的内容，
	 * 如果“出生地(描述)”未构库或内容未填，则以“出生地”指标的内容为准。
	 * @param cadreId
	 * @return
	 */
	public String getBirthAddress(String cadreId){
		String birthAddress = "";
		String setid ="A01";
		String itemid="e0126";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					birthAddress = rs.getString(itemid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if(birthAddress == null || "".equals(birthAddress)){
			String setid2 ="A01";
			String itemid2="a0117";
			if(this.isFieldItemExist(itemid2) && this.isFieldSetExist(setid2) ){
				String sql="select codeitemdesc , codeitemid from codeitem where codeitemid in( select "+ itemid2+
				" from " + dbPre+setid2 + " where a0100='"
				+cadreId+"') and codesetid = (select codesetid from fielditem where itemid='"+itemid2+"')";
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						birthAddress = rs.getString("codeitemdesc");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
		}

		return birthAddress;
	}
	
	/**
	 * 获得干部健康状况
	 * @param cadreId
	 * @return
	 */
	public String getBodyStatus(String cadreId){
		String bodyStatus = "";
		String setid ="A01";
		String itemid="A0124";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select codeitemdesc from codeitem where " +
					"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"') " +
					"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					bodyStatus = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return bodyStatus;
	}
	
	/**
	 * 获得干部籍贯
	 * @param cadreId
	 * @return
	 */
	public String getAddress(String cadreId){
		String address ="";
		String setid ="A01";
		String itemid="e0124";
		ResultSet rs=null;
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					address = rs.getString(itemid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if(address == null || "".equals(address)){
			String setid2 ="A01";
			String itemid2="a0114";
			if(this.isFieldItemExist(itemid2) && this.isFieldSetExist(setid2) ){
				String sql="select codeitemdesc , codeitemid from codeitem where codeitemid in( select "+ itemid2+
				" from " + dbPre+setid2 + " where a0100='"
				+cadreId+"') and codesetid = (select codesetid from fielditem where itemid='"+itemid2+"')";
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						address = rs.getString("codeitemdesc");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
		}
		
		return address;
	}
		
	/**
	 * 参加工作时间
	 * @param cadreId
	 * @return
	 */
	public String getEnterWorkDate(String cadreId){
		String enterWorkDate="";
		String setid ="A01";
		String itemid="A0141";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					enterWorkDate = rs.getString(itemid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if(!"".equals(enterWorkDate)){
			enterWorkDate = enterWorkDate.substring(0,4)+"."+enterWorkDate.substring(5,7);
		}
		return enterWorkDate;
	}
	
	/**
	 * 获得干部入党时间
	 * 	中共党员 入党时间
	 * 	其他党派 名称
	 * @param cadreId
	 * @return
	 */
	public String getEnterPartyDate(String cadreId){
		String enterPartyDate ="";
		
		boolean b = false; //是否是中共党员
		
		StringBuffer temp = new StringBuffer();
		String setid0 ="A22";
		String itemid0="A2205";
		if(this.isFieldItemExist(itemid0) && this.isFieldSetExist(setid0) ){
			String sql0="select codeitemdesc , codeitemid from codeitem where codeitemid in( select "+ itemid0+
			" from " + dbPre+setid0 + " where a0100='"+cadreId+"') and codesetid = (select codesetid from fielditem where itemid='"+itemid0+"')";
			//System.out.println(sql0);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql0);
				while(rs.next()){
					String codeitemid = rs.getString("codeitemid");
					if("01".equals(codeitemid)){//中共党员
						b = true;
					}else if("04".equals(codeitemid)){
						temp.append("民革");
					}else if("05".equals(codeitemid)){
						temp.append("民盟");
					}else if("06".equals(codeitemid)){
						temp.append("民建");
					}else if("07".equals(codeitemid)){
						temp.append("民进");
					}else if("08".equals(codeitemid)){
						temp.append("农工党");
					}else if("09".equals(codeitemid)){
						temp.append("致公党");
					}else if("10".equals(codeitemid)){
						temp.append("九三学社");
					}else if("11".equals(codeitemid)){
						temp.append("台盟");
					}else if("12".equals(codeitemid)){
						temp.append("无党派");
					}else if("13".equals(codeitemid)){
						temp.append("无党派");
					}
					/*word长度控制
					if(temp.toString().length()>3){
						temp.delete(3,temp.toString().length());
					}
					*/
					/*
					 else if(codeitemid.equals("04")||codeitemid.equals("05")||codeitemid.equals("06")
							||codeitemid.equals("07")||codeitemid.equals("08")||codeitemid.equals("09")
							||codeitemid.equals("10")||codeitemid.equals("11")||codeitemid.equals("12")){
						//其他党派或无党派民主人士
						temp.append(rs.getString("codeitemdesc"));									
					} 
					 
					01	中共党员 02	中共预备党员 03	共青团员 04	民革会员
					05	民盟盟员 06	民建会员 07	民进会员 08	农工党党员
					09	致公党党员 10	九三学社社员 11	台盟盟员 12	无党派民主人士
					13	群众 99	其他
					C/S中
					    if strValue='04' then  str:=str+'民革；';
				        if strValue='05' then  str:=str+'民盟；';
				        if strValue='06' then  str:=str+'民建；';
				        if strValue='07' then  str:=str+'民进；';
				        if strValue='08' then  str:=str+'农工党；';
				        if strValue='09' then  str:=str+'致公党；';
				        if strValue='10' then  str:=str+'九三学社；';
				        if strValue='11' then  str:=str+'台盟；';
				        if strValue='12' then  str:=str+'无党派；';
				        if strValue='13' then  str:=str+'无党派；';
					
					
					*/
					
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		if(b){
			//中共党员(返回日期)
			String setid ="A22";
			String itemid="A2210";
			String itemid1="A2205";
			if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemid1)  ){
				String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'"
					+" and " + itemid1+ " ='01'";
				//System.out.println(sql);
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						enterPartyDate = rs.getString(itemid);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
			if(!"".equals(enterPartyDate)){
				enterPartyDate = enterPartyDate.substring(0,4)+"."+enterPartyDate.substring(5,7);
			}
		}
		
		if("".equals(enterPartyDate)){
			enterPartyDate = temp.toString();
		}else{
			enterPartyDate += ";"+ temp.toString();
		}
		//System.out.println("_____________"+enterPartyDate);
		return enterPartyDate;
	}
	
	/**
	 * 获得干部专业技术职务
	 * @param cadreId
	 * @return
	 */
	public String getDuty(String cadreId){
		String duty="";
		String setid ="A10";
		String itemid="A1005";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select codeitemdesc from codeitem where " +
					"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +
						" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"')) "+
					"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
	        //System.out.println(sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					duty = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return duty;
	}

	/**
	 * 获得干部的特长
	 * @param cadreId
	 * @return
	 */
	public String getTeChang(String cadreId){
		String teChang="";
		String setid ="A10";
		String itemid="e1003";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			//System.out.println(sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					teChang = rs.getString(itemid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return teChang;
	}
	
	/**
	 * 
	 * 获得学历指标名称 C0401（1 全日制 2 在职）
	 * 
	 * @return
	 */
	private String getXuLiFielditem(){
		String fieldItem = "";
		String sql = "select itemid from fielditem where itemdesc='学历性质'";		
		String sql1="select itemid from fielditem where itemdesc='学历标识'";
		String sql2="select itemid from fielditem where itemdesc='学习性质'";
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				fieldItem = rs.getString("itemid");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		if("".equals(fieldItem)){
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql1);
				if(rs.next()){
					fieldItem = rs.getString("itemid");	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		if("".equals(fieldItem)){
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql2);
				if(rs.next()){
					fieldItem = rs.getString("itemid");	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return fieldItem;
	}
	
	/**
	 * 获得干部的学历
	 * @param cadreId
	 * @param flag    学历标识 1 全日制 2 在职
	 * @return
	 */
	public String getSchoolAge(String cadreId,String flag){
		String schoolAge="";
		String setid ="A04";
		String itemid="A0405";
		String xlitemid = this.getXuLiFielditem();
		if(xlitemid == null || "".equals(xlitemid)){
		}else{
			if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
				String sql="select codeitemdesc from codeitem where codeitemid =(select " +itemid
						+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +xlitemid+"='"+flag+"' and" +
								" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"'and " +xlitemid+"='"+flag+"')) " +
						"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
				//System.out.println("*********************"+sql);
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						schoolAge = rs.getString("codeitemdesc");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
			
		}
			
		return schoolAge;
	}
	
	/**
	 * 获得干部的学位
	 * @param cadreId
	 * @param flag    学历标识 1 全日制 2 在职
	 * @return
	 */
	public String getDegree(String cadreId,String flag){
		String degree="";
		String setid ="A04";
		String itemid="A0440";
		String xlitemid = this.getXuLiFielditem();
		if(xlitemid == null || "".equals(xlitemid)){
		}else{
			if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
				String sql="select codeitemdesc from codeitem where codeitemid =(select " +itemid
						+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +xlitemid+"='"+flag+"' and" +
								" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"'and " +xlitemid+"='"+flag+"')) " +
						"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			//	System.out.println("____________________________________________" + sql);
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						degree = rs.getString("codeitemdesc");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
			
		}
			
		return degree;
	}
	
	/**
	 * 获得干部毕业学校
	 * @param cadreId
	 * @param flag 学历标识 1 全日制 2 在职
	 * @return
	 */
	public String getShcool(String cadreId,String flag){
		String shool="";
		String setid ="A04";
		String itemid="A0435";
		String xlitemid = this.getXuLiFielditem();
		if(xlitemid == null || "".equals(xlitemid)){
		}else{
			if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
				String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " 
					+xlitemid+"='"+flag+"' and" +
					" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"' and " +xlitemid+"='"+flag+"') ";
				//System.out.println(sql);
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						shool = rs.getString(itemid);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
			
		}

		return shool;
	}
	
	
	/**
	 * 获得干部所学专业
	 * @param cadreId
	 * @param flag 学历标识 1 全日制 2 在职
	 * @return
	 */
	public String getSpecialty(String cadreId ,String flag){
		String specialty = "";		
		String setid ="A04";
		String itemid=this.getZY();
		String xlitemid = this.getXuLiFielditem();
		if((xlitemid == null || "".equals(xlitemid))||(itemid == null || "".equals(itemid))){
		}else{
			if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
				String sql="select codeitemdesc from codeitem where codeitemid =(select " +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +xlitemid+"='"+flag+"' and" +
						" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"' and " +xlitemid+"='"+flag+"')) " +
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
				ResultSet rs=null;
				try {
					ContentDAO dao = new ContentDAO(conn);
					rs=dao.search(sql);
					if(rs.next()){
						specialty = rs.getString("codeitemdesc");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally
				{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
		}
		return specialty;
	}
	
	/**
	 * 专业依据
	 * @return
	 */
	private String getZY(){
		String zy = "";
		String sql=	"select itemid from fielditem where itemid='A0410'";
		String sql1="select itemid from fielditem where itemdesc='所学专业（描述）'";
		String sql2="select itemid from fielditem where itemdesc='专业描述'";
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				zy = rs.getString("itemid");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		if("".equals(zy)){
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql1);
				if(rs.next()){
					zy = rs.getString("itemid");	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		if("".equals(zy)){
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql2);
				if(rs.next()){
					zy = rs.getString("itemid");	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return zy;
	}
	
	/**
	 * 获得干部任职状态
	 * @param cadreId
	 * @return
	 */
	public String getDutyFlag(String cadreId){
		String flag = "2";
		String setid="A07";
		String itemid="A0737";//在职状态 1未在职 2在职
		if(this.isFieldItemExist(itemid)){
			String sql="select codeitemid from codeitem where " +
			"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'"
			+" and" +
			" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"')"+" ) " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					flag = rs.getString("codeitemid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return flag;
	}
	/**
	 * 获得干部现任职务
	 * @param cadreId
	 * @return
	 */
	public String getCurrentDuty(String cadreId){
		String currentDuty = "";
		String setid ="A07";
		String itemid="A0704";
		String flag = this.getDutyFlag(cadreId);		
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="";
			if(this.isFieldItemExist("A0737")){//任职状态是否构库
				sql="select codeitemdesc from codeitem where " +
				"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'"
				+" and" +
				" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"')"+" and A0737='"+flag+"' ) " +
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			}else{
				sql="select codeitemdesc from codeitem where " +
				"codeitemid=(select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'"
				+" and" +
				" i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"')"+" ) " +
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			}
			
			
			//System.out.println("职务" + sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					currentDuty = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return currentDuty;
	}
	
	/**
	 * 获得干部任职机构名称
	 * @param cadreId
	 * @return
	 */
	public String getCurrentOrg(String cadreId){
		String currentOrg="";
		String setid ="A07";
		String itemid="A0711";
		String flag = this.getDutyFlag(cadreId);	
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="";
			if(this.isFieldItemExist("A0737")){//任职状态是否构库
				sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +
				"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and A0737 ='" + flag +"'";
			}else{
				sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and " +
				"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') ";
			}
			//System.out.println("任职单位" +sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					currentOrg = rs.getString(itemid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return currentOrg;
	}
	
	
	/**
	 * 获得干部简历起始时间集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getResumeStartDate(String cadreId){
		ArrayList resumeStartDate = new ArrayList();
		String setid ="A19";
		String itemid="A1905";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'" ;
		//	System.out.println("简历起始时间："+ sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString(itemid);
					if(date==null|| "".equals(date)){
						resumeStartDate.add("");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);	
						//System.out.println(date);
						resumeStartDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		//System.out.println("aa=" + resumeStartDate);
		return resumeStartDate;
	}
	
	
	/**
	 * 获取干部简历结束时间集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getResumeEndDate(String cadreId){
		ArrayList resumeEndDate = new ArrayList();
		String setid ="A19";
		String itemid="A1910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'" ;
			//System.out.println("简历结束时间："+ sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString(itemid);
					if(date==null|| "".equals(date)){
						resumeEndDate.add("");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);		
						resumeEndDate.add(date);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return resumeEndDate;
	}
	
	/**
	 * 获取干部简历单位集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getResumeOrg(String cadreId){
		ArrayList resumeOrg = new ArrayList();
		String setid ="A19";
		String itemid="A1915";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'" ;
			//System.out.println("简历单位："+ sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String org = rs.getString(itemid);
					if(org==null|| "".equals(org)){
						resumeOrg.add("");
					}else{
						resumeOrg.add(org);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return resumeOrg;
	}
	
	/**
	 * 获得干部简历职务集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getResumeDuty(String cadreId){
		ArrayList resumeDuty = new ArrayList();
		String setid ="A19";
		String itemid="A1920";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select " +itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"'" ;
			//System.out.println("简历职务："+ sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String duty = rs.getString(itemid);
					if(duty==null|| "".equals(duty)){
						resumeDuty.add("");
					}else{
						resumeDuty.add(duty);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return resumeDuty;
	}
	
	/*
	
	相应子集有奖励子集（A28）和惩罚子集（A29）；
	相应指标有：
	奖励名称（E2803）、奖励原因（E2805）、奖励时间（E2807）、奖励批准单位（E2809），
	处分名称（E2903）、处分原因（E2905	）、受处理时间（E2907）和受处分给予单位（E2909）；

	*/
	/**
	 * 获得奖励名称集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getEncouragementName(String cadreId){
		ArrayList encouragementName = new ArrayList();
		
		String setid ="A28";
		String itemid="E2803";
		String dateFlag = "E2807";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){		
			String sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					/*"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and "+*/
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+ ")" +//" order by i9999 ) " +
			" and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			
			//System.out.println("奖励名称集合="+sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String name = rs.getString("codeitemdesc");
					if(name == null || "".equals(name)){
						encouragementName.add("");
					}else{
						encouragementName.add(name);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return encouragementName;
	}
	
	/**
	 * 获得奖励原因集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getEncouragementCausation(String cadreId){
		ArrayList encouragementCausation = new ArrayList();
		String setid ="A28";
		String itemid="E2805";
		String dateFlag = "E2807";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){		
			String sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					/*"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and "+*/
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+" ) " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			//System.out.println("奖励原因集合="+sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String causation = rs.getString("codeitemdesc");
					if(causation == null || "".equals(causation)){
						encouragementCausation.add("");
					}else{
						encouragementCausation.add(causation);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return encouragementCausation;
	}
	
	/**
	 * 获得奖励时间集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getEncouragementDate(String cadreId){
		ArrayList encouragementDate = new ArrayList();

		/*select  e2807 from usra28 where a0100='00000007'
		and year(e2807) between year(getdate())-3 and year(getdate())*/
		String setid ="A28";
		String itemid="E2807";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
			Sql_switcher.year(itemid) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
			+Sql_switcher.year(Sql_switcher.sqlNow()) ;
			//System.out.println("奖励时间集合="+sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date  = rs.getString(itemid);
					if(date == null || "".equals(date)){
						encouragementDate.add("");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);	
						encouragementDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return encouragementDate;
	}
	
	/**
	 * 获得奖励单位集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getEncouragementOrg(String cadreId){
		ArrayList encouragementOrg = new ArrayList();
		String setid ="A28";
		String itemid="E2809";
		String dateFlag = "E2807";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){

			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
			Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
			+Sql_switcher.year(Sql_switcher.sqlNow()) ;
			/*
			String sql="select codeitemdesc from codeitem where codeitemid =(select" +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and "+
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+") " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";*/
		//	System.out.println("奖励单位集合=" +sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String org = rs.getString(itemid);
					if(org == null || "".equals(org)){
						encouragementOrg.add("");
					}else{
						encouragementOrg.add(org);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return encouragementOrg;
	}
		
	/**
	 * 获得处罚名称集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getPunishName(String cadreId){
		ArrayList punishName = new ArrayList();
		String setid ="A29";
		String itemid="E2903";
		String dateFlag = "E2907";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){		
			String sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+"  ) " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String name = rs.getString("codeitemdesc");
					if(name == null || "".equals(name)){
						punishName.add("");
					}else{
						punishName.add(name);
					}
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}		 
		return punishName;
	}
	
	/**
	 * 获得处罚原因集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getPunishCausation(String cadreId){
		ArrayList punishCausation = new ArrayList();
		String setid ="A29";
		String itemid="E2905";
		String dateFlag = "E2907";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){		
			String sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+"  ) " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String causation = rs.getString("codeitemdesc");
					if(causation == null || "".equals(causation)){
						punishCausation.add("");
					}else{
						punishCausation.add(causation);
					}
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return punishCausation;
	}
	
	/**
	 * 获得处罚时间集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getPunishDate(String cadreId){
		ArrayList punishDate = new ArrayList();
		String setid ="A29";
		String itemid="E2907";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
			Sql_switcher.year(itemid) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
			+Sql_switcher.year(Sql_switcher.sqlNow()) ;
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date  = rs.getString(itemid);
					if(date == null || "".equals(date)){
						punishDate.add("");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);	
						punishDate.add(date);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return punishDate;
	}
	
	/**
	 * 获得处罚单位集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getPunishOrg(String cadreId){
		ArrayList punishOrg = new ArrayList();
		String setid ="A29";
		String itemid="E2909";
		String dateFlag = "E2907";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(dateFlag)){

			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
			Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
			+Sql_switcher.year(Sql_switcher.sqlNow()) ;
			/*
			String sql="select codeitemdesc from codeitem where codeitemid =(select" +itemid
			+" from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and "+
					Sql_switcher.year(dateFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-3 and "
					+Sql_switcher.year(Sql_switcher.sqlNow())+") " +
			"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";*/
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String org = rs.getString(itemid);
					if(org == null || "".equals(org)){
						punishOrg.add("");
					}else{
						punishOrg.add(org);
					}
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}	
		return punishOrg;
	}
	
	//考核情况
	/**
	 * 获得考核时间
	 */
	public ArrayList getExamDate(String cadreId){
		ArrayList examDate = new ArrayList();
		//取最近3条记录
		String setid ="A25";
		String itemid="A2510";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			ArrayList temp = new ArrayList();
			String sql="select "+Sql_switcher.year(itemid)+ " as value  from "+dbPre+setid+"  where a0100='"+cadreId+"' order by i9999";
			//System.out.println("考核时间=" + sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String d = rs.getString("value");
					temp.add(d);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
			int n = temp.size();
			//System.out.println(n);
			if(n!= 0){
				for(int i = n-1; n>= 0; i--){
					examDate.add((String)temp.get(i));
					if(i <= (n-3)){
						break;
					}
				}
			}
		}
		//System.out.println(examDate.size());
		return examDate;
	}
	
	
	/**
	 * 获得考核结果
	 * @param cadreId
	 * @return
	 */
	public ArrayList getExamResult (String cadreId){
		ArrayList examResult = new ArrayList();
		//取最近3条记录
		String setid ="A25";
		String itemid="A2520";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			ArrayList temp = new ArrayList();
			String sql="select "+itemid+ " as value  from "
			+dbPre+setid+"  where a0100='"+cadreId+"' order by i9999";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String d = rs.getString("value");
					temp.add(d);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
			
			int n = temp.size();
			if(n!=0){
				for(int i = n-1; n>= 0; i--){
					String flag = (String)temp.get(i);
					String sql1="select codeitemdesc from codeitem where codeitemid ='"+flag+"' "+
					" and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"') ";
					Statement stmt1;
					ResultSet rs1=null;
					try {
						ContentDAO dao = new ContentDAO(conn);
						rs=dao.search(sql);
						if(rs1.next()){
							String d = rs1.getString("codeitemdesc");
							examResult.add(d);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally
					{
						if(rs1!=null) {
                            try {
                                rs1.close();
                            } catch (SQLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
					}
					
					if(i <= (n-3)){
						break;
					}
				}
			}
		}
		return examResult;
	}
	
	/**
	 * 获得干部近五年的培训时间
	 * @param cadreId
	 * @return
	 */
	public String getFosterDate(String cadreId){
		String fosterDate = "";
		String setid ="A37";
		String itemid="A3725";
		String itemFlag="A3715";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="select sum(" +itemid
			+") as value  from "+dbPre+setid+" where a0100='"+cadreId+"' and "+
					/*"i9999=(select max(i9999) from "+dbPre+setid+" where a0100='"+cadreId+"') and "+*/
					Sql_switcher.year(itemFlag) + " between " + Sql_switcher.year(Sql_switcher.sqlNow())+"-5 and "
					+Sql_switcher.year(Sql_switcher.sqlNow());
			//System.out.println(sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				if(rs.next()){
					String d = rs.getString("value");
					if(d == null || "".equals(d)){
					}else{
						fosterDate = d;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return fosterDate;
	}
	

	/**
	 * 获得干部家庭部分姓名集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyName(String cadreId){
		ArrayList familyName = new ArrayList();
		String setid ="A79";
		String itemid="A7905";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2'  or "+itemFlag+" like '%3' "+" ) order by i9999 ";
			}		
			//System.out.println(sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String name = rs.getString(itemid);
					if(name == null || "".equals(name)){
						familyName.add("");
					}else{
						familyName.add(name);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return familyName;
	}
	
	/**
	 * 获得干部家庭部分关系集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyRelation(String cadreId){
		ArrayList familyRelation = new ArrayList();
		String setid ="A79";
		String itemid="A7910";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"'  "+
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";
				
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"'and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2' or "+itemFlag+" like '%3' "+" ) "+
				" and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"'))";				
			}	
			//System.out.println(sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String relation = rs.getString("codeitemdesc");
					if(relation == null || "".equals(relation)){
						familyRelation.add("");
					}else{
						familyRelation.add(relation);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return familyRelation;
	}
	
	
	/**
	 * 获取干部家庭出生日期(年龄)集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyBirthDateTxt(String cadreId){
		ArrayList familyBirthDate = new ArrayList();
		String setid ="A79";
		String itemid="A7915";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				//sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";
				sql="select "+itemid+" as value"+
				" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select "+itemid +" as value"+
				" from "+dbPre+setid+" where a0100='"+cadreId+"'"+
				" and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2'  or "+itemFlag+" like '%3' "+" ) order by i9999 ";
			}	
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString("value");
					if(date == null || "".equals(date)){
						familyBirthDate.add("");
					}else{
						date = date.substring(0,4)+date.substring(5,7);	
						familyBirthDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return familyBirthDate;
	}
	
	
	
	/**
	 * 获取干部家庭出生日期(年龄)集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyBirthDate(String cadreId){
		ArrayList familyBirthDate = new ArrayList();
		String setid ="A79";
		String itemid="A7915";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				//sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";
				sql="select "+"("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),itemid )+")/365.00000000 as value"+
				" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select "+"("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),itemid )+")/365.00000000 as value"+
				" from "+dbPre+setid+" where a0100='"+cadreId+"'"+
				" and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2'  or "+itemFlag+" like '%3' "+" ) order by i9999 ";
			}	
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString("value");
					if(date == null || "".equals(date)){
						familyBirthDate.add("");
					}else{
						date = date.substring(0,date.indexOf('.'));	
						familyBirthDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return familyBirthDate;
	}
	
	/**
	 * 获得干部家庭中单位集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyOrg(String cadreId){
		ArrayList familyOrg = new ArrayList();
		String setid ="A79";
		String itemid="A7920";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2'  or "+itemFlag+" like '%3' "+" ) order by i9999 ";
			}			
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String org = rs.getString(itemid);
					if(org == null || "".equals(org)){
						familyOrg.add(" ");
					}else{
						familyOrg.add(org);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return familyOrg;
	}
	
	/**
	 * 获得干部家庭政治面貌集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getFamilyZzmm(String cadreId){
		ArrayList familyZzmm =new ArrayList();
		String setid ="A79";
		String itemid="A7925";
		String itemFlag="A7910";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) && this.isFieldItemExist(itemFlag)){
			String sql="";
			if(this.checkUserMarriageStatus(cadreId)){//未婚者，所有记录
				sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"' order by i9999 "+
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"'))";
				
			}else{//已婚者, 只显示配偶(1)及子(2)女(3)
				sql="select codeitemdesc from codeitem where codeitemid in(select " +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"'and ( "+
				itemFlag +" like '%1' or "+itemFlag+" like '%2' or  "+itemFlag+" like '%3' "+" ) "+
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"'))";				
			}	
			//System.out.println("干部家庭政治面貌集合=" + sql);
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String zzmm = rs.getString("codeitemdesc");
					if(zzmm == null || "".equals(zzmm)){
						familyZzmm.add(" ");
					}else{
						familyZzmm.add(zzmm);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return familyZzmm;
	}
	
	
	/**
	 * 获得干部社会部分姓名集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityName(String cadreId){
		ArrayList communityName = new ArrayList();
		String setid ="A74";
		String itemid="A7441";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";				
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String name = rs.getString(itemid);
					if(name == null || "".equals(name)){
						communityName.add(" ");
					}else{
						communityName.add(name);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		
		return communityName;
	}
	
	/**
	 * 获得干部社会部分关系集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityRelation(String cadreId){
		ArrayList communityRelation = new ArrayList();
		String setid ="A74";
		String itemid="A7401";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			String sql="select codeitemdesc from codeitem where codeitemid =(select" +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"' order by i9999 "+
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";				
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String relation = rs.getString(itemid);
					if(relation == null || "".equals(relation)){
						communityRelation.add(" ");
					}else{
						communityRelation.add(relation);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return communityRelation;
	}
	
	/**
	 * 获取干部社会出生日期(年龄)集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityBirthDateTxt(String cadreId){
		ArrayList communityBirthDate = new ArrayList();
		String setid ="A74";
		String itemid="A7404";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			//String 	sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";	
			String sql="select "+itemid +" as value"+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString("value");
					if(date == null || "".equals(date)){
						communityBirthDate.add(" ");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);	
						communityBirthDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return communityBirthDate;
	}
	
	/**
	 * 获取干部社会出生日期(年龄)集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityBirthDate(String cadreId){
		ArrayList communityBirthDate = new ArrayList();
		String setid ="A74";
		String itemid="A7404";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			//String 	sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";	
			String sql="select "+"("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),itemid )+")/365.00000000 as value"+" from "+dbPre+setid+" where a0100='"+cadreId+"'";
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String date = rs.getString("value");
					if(date == null || "".equals(date)){
						communityBirthDate.add(" ");
					}else{
						date = date.substring(0,4)+"."+date.substring(5,7);	
						communityBirthDate.add(date);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return communityBirthDate;
	}
	
	/**
	 * 获得干部社会中单位集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityOrg(String cadreId){
		ArrayList communityOrg = new ArrayList();
		String setid ="A74";
		String itemid="A7417";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid)){
			String sql="select "+itemid+" from "+dbPre+setid+" where a0100='"+cadreId+"' ORDER BY I9999";					
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String org = rs.getString(itemid);
					if(org == null || "".equals(org)){
						communityOrg.add(" ");
					}else{
						communityOrg.add(org);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return communityOrg;
	}
	
	/**
	 * 获得干部社会政治面貌集合
	 * @param cadreId
	 * @return
	 */
	public ArrayList getCommunityZzmm(String cadreId){
		ArrayList  communityZzmm =new ArrayList();
		String setid ="A74";
		String itemid="A7431";
		if(this.isFieldItemExist(itemid) && this.isFieldSetExist(setid) ){
			String sql="select codeitemdesc from codeitem where codeitemid =(select" +itemid
				+" from "+dbPre+setid+" where a0100='"+cadreId+"' order by i9999 "+
				"and codesetid=(select codesetid  from fielditem where itemid='"+itemid+"')";	
			ResultSet rs=null;
			try {
				ContentDAO dao = new ContentDAO(conn);
				rs=dao.search(sql);
				while(rs.next()){
					String zzmm = rs.getString(itemid);
					if(zzmm == null || "".equals(zzmm)){
						communityZzmm.add(" ");
					}else{
						communityZzmm.add(zzmm);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return communityZzmm;
	}
	
	
	/**
	 * 验证干部是否未结婚
	 * @param cadreId 
	 * @return
	 */
	public boolean checkUserMarriageStatus(String cadreId){
		boolean b = false;
		String sql="select a0127 from  usra01 where a0100='"+cadreId+"'";
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				String flag = rs.getString("a0127");
				if("1".equals(flag)){ //未婚
					b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return b;
	}
	
	/**
	 * 判断指标是否存在
	 * @param itemid 指标ID
	 * @return
	 */
	public boolean isFieldItemExist(String itemid){
		boolean b = false;
		String sql="select useflag from fielditem where itemid='"+itemid+"'";
		//System.out.println(sql);
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				String flag = rs.getString("useflag");
				if("1".equals(flag)){
					b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return b;
	}
	
	/**
	 * 判断指标集是否存在
	 * @param fieldsetid 指标集ID
	 * @return
	 */
	public boolean isFieldSetExist(String fieldsetid){
		boolean b = false;
		String sql="select * from fieldset where fieldsetid='"+ fieldsetid + "'";
		//System.out.println(sql);
		ResultSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			if(rs.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return b;		
	}
	
	/**
	 * 获得系统式化当前时间
	 * @return
	 */
	public String getCurrentFormatDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		String date =  sdf.format(currentTime); 
		date = date.substring(0,4)+date.substring(5,7);	
		return date;
	}
	
	/**
	 *填表人
	 * @param userName
	 * @return
	 */
	public static String getFillInTableName(String userName){
		return userName;
	}
	
	
	public static void main(String [] args){		
		String temp="1962-04-23 00:00:00.000";
		System.out.println(temp.substring(0,4)+temp.substring(5,7));
	}

}
