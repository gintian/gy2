package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InputCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/*
		 * upflag =0 不做导入，导入失败 =1 覆盖原有代码，同时添加新代码
		 */
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap formhm = this.getFormHM();
		String upflag = (String) formhm.get("upflag");
		ArrayList templist = new ArrayList();
		ArrayList uporaddlist = new ArrayList();
		ArrayList updatelist = new ArrayList();
		ArrayList addlist = new ArrayList();
		ArrayList codesetlist = new ArrayList();
		try {
			if ("".equals(upflag)) {
				FormFile ufile = (FormFile) this.getFormHM().get("ufile");
				InputStream inputStream=null;
				try{
    				inputStream=ufile.getInputStream();
    				templist = this.getInputCodeItem(inputStream);
    				if (templist.size() < 2) {
    					Exception ex = new Exception("很抱歉！导入失败,导入的数据有问题！");
    					throw GeneralExceptionHandler.Handle(ex);
    				}
    				uporaddlist = uporaddCodeitem(templist);
    				//判断导入的代码项ID是否有重复的，并把重复的代码项提示给用户   jingq add 2014.12.12
    				HashMap map = new HashMap();
    				String repeatmes = "";
    				for (int i = 0; i < uporaddlist.size(); i++) {
    					ArrayList list = (ArrayList) uporaddlist.get(i);
    					for (int j = 0; j < list.size(); j++) {
    						RecordVo vo = (RecordVo) list.get(j);
    						String codeitemid = vo.getString("codeitemid");
    						String codeitemdesc = vo.getString("codeitemdesc");
    						if(!map.containsKey(codeitemid)){
    							map.put(codeitemid, codeitemdesc);
    						} else {
    							repeatmes += "&nbsp;&nbsp;&nbsp;&nbsp;"+codeitemid+"`"+codeitemdesc+"<br/>";
    						}
    					}
    				}
    				if(!"".equals(repeatmes)){
    					Throwable e = new Throwable("导入失败，以下代码项重复:<br/>"+repeatmes);
    					throw GeneralExceptionHandler.Handle(e);
    				}
    				if (uporaddlist.size() > 1) {
    					updatelist = (ArrayList) uporaddlist.get(0);
    					addlist = (ArrayList) uporaddlist.get(1);
    				}
    				codesetlist = (ArrayList) templist.get(0);
				} catch (Exception e) {
		            e.printStackTrace();
		            throw GeneralExceptionHandler.Handle(e);
		        }
				finally{
				    PubFunc.closeResource(inputStream);   
				}
			} else {
				updatelist = (ArrayList) this.getUserView().getHm().get(
						"uplaodlist");
				addlist = (ArrayList) this.getUserView().getHm().get("addlist");
				codesetlist = (ArrayList) this.getUserView().getHm().get(
						"codesetlist");
			}
			if (updatelist.size() > 0) {
				if (upflag != null && upflag.length() > 0) {
					if ("0".equals(upflag)) {
						this.getFormHM().put("upflag", "");
						this.getFormHM().put("filevalue", "ss");
						this.userView.getHm().remove("addlist");
						this.userView.getHm().remove("uplaodlist");
						this.userView.getHm().remove("codesetlist");
						return;
					}
					if ("1".equals(upflag)) {

						RecordVo codvo = new RecordVo("codeset");
						codvo.setString("codesetid", (String) codesetlist
								.get(0));
						codvo.setString("codesetdesc", (String) codesetlist
								.get(1));
						codvo.setString("maxlength", (String) codesetlist
								.get(2));
						codvo.setString("status", (String) codesetlist.get(3));
						codvo.setString("validateflag", (String) codesetlist.get(4));
						dao.updateValueObject(codvo);
						ArrayList list = new ArrayList();
						list.add(codesetlist.get(0));
						dao.delete("delete from codeitem where codesetid=?",
								list);
						dao.addValueObject(addlist);
						dao.addValueObject(updatelist);

						this.getFormHM().put("upflag", "");
						this.getFormHM().put("filevalue", "ss");
						this.userView.getHm().remove("addlist");
						this.userView.getHm().remove("uplaodlist");
						this.userView.getHm().remove("codesetlist");
						return;
					}
				} else {
					// 问是否需要覆盖
					this.getFormHM().put("filevalue", "exist");
					UserView userview = this.getUserView();
					userview.getHm().put("uplaodlist", updatelist);
					userview.getHm().put("addlist", addlist);
					userview.getHm().put("codesetlist", codesetlist);
				}
			} else {
				// 增加新的codeitemid
				RecordVo codvo = new RecordVo("codeset");
				codvo.setString("codesetid", (String) codesetlist.get(0));
				try {
					RecordVo vo = dao.findByPrimaryKey(codvo);
					codvo.setString("codesetdesc", (String) codesetlist.get(1));
					codvo.setString("maxlength", (String) codesetlist.get(2));
					codvo.setString("status", (String) codesetlist.get(3));
					codvo.setString("validateflag", (String) codesetlist.get(4));
					dao.updateValueObject(codvo);
				} catch (Exception e) {
					codvo.setString("codesetdesc", (String) codesetlist.get(1));
					codvo.setString("maxlength", (String) codesetlist.get(2));
					codvo.setString("status", (String) codesetlist.get(3));
					codvo.setString("validateflag", (String) codesetlist.get(4));
					dao.addValueObject(codvo);
				}
				dao.addValueObject(addlist);
				this.getFormHM().put("filevalue", "success");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			// throw new GeneralException("导入的指标格式不对请重新导入");

		}

	}

	public ArrayList getInputCodeItem(InputStream f) throws IOException,
			SQLException, GeneralException {
		ArrayList templist = new ArrayList();
		ArrayList codesetlist = new ArrayList();
		InputStreamReader fr = new InputStreamReader(f);
		BufferedReader br = new BufferedReader(fr);
		String temp = "";
		String codesetid = "";
		if (br.markSupported()) {
			br.mark(1024 * 1024);
		}
		ArrayList firstwordlist = new ArrayList();
		while ((temp = br.readLine()) != null) {// 验证读取的文件数据格式是否正确，必须满足能组成树形结构的关系
			ArrayList mylist = getStr(temp);
			if (mylist.size() >= 1)
				firstwordlist.add(mylist.get(0));
		}
		if (firstwordlist.size() < 4) {
			return templist;
		}

		br.reset();
		boolean flag = false;
		while ((temp = br.readLine()) != null) {
			ArrayList mylist = getStr(temp);
			if (mylist.size() >= 3 && !flag) {// 获取代码类数据
				flag = true;
				String codesetdesc = "";
				codesetid = (String) mylist.get(0);
				String maxlength = (String) mylist.get(1);
				codesetdesc = (String) mylist.get(2);
				String status = "";
				String validateflag ="";
				if (mylist.size() >= 4) {
					status = (String) mylist.get(3);
				}
				if (mylist.size() >= 5) {
					validateflag = (String) mylist.get(4);
				}
				codesetlist.add(codesetid);
				codesetlist.add(codesetdesc);
				codesetlist.add(maxlength);
				codesetlist.add(status);
				codesetlist.add(validateflag);
				templist.add(codesetlist);
			} else if (flag && mylist.size() > 1) {// 获取代码项数据
				String id = (String) mylist.get(0);
				String desc = (String) mylist.get(1);
				
				//修改bs导入代码类，与cs格式保持一致   jingq add 2014.12.12
				String corcode = "";//转换代码
				String invalid = "";//是否有效
				String start_date = "";//开始时间
				String end_date = "";//结束时间
				//【7914】代码导入，需兼容以前bs导出的文件  jingq upd 2015.03.10
				if(mylist.size()>=3){
					corcode = (String) mylist.get(2);
				}
				if(mylist.size()>=4){
					invalid = (String) mylist.get(3);
				}
				if(mylist.size()>=5){
					start_date = (String) mylist.get(4);
				}
				if(mylist.size()>=6){
					end_date = (String) mylist.get(5);
				}
				RecordVo codeitemvo = getempVo2(codesetid,desc,id,corcode,invalid,start_date,end_date);
				templist.add(codeitemvo);
			}
		}
		br.close();
		fr.close();
		return templist;
	}

	public RecordVo getempVo2(String codesetid,String desc,String id,String corcode,String invalid,String start_date,String end_date){
		RecordVo vo = new RecordVo("codeitem");
		vo.setString("codesetid", codesetid);
		vo.setString("codeitemid", id);
		vo.setString("codeitemdesc", desc);
		vo.setString("corcode", corcode);
		vo.setString("invalid", invalid);
		//【8054】系统管理-库机构-代码体系，导入功能无法使用，报错  jingq upd 2015.03.16
		vo.setDate("start_date", start_date);
		vo.setDate("end_date", end_date);
		vo.setString("parentid", id);
		vo.setString("childid", id);
		return vo;
	}
	
	public void updatevo(ArrayList inputvo) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.updateValueObject(inputvo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public void addvo(RecordVo vo) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		dao.addValueObject(vo);
	}

	public ArrayList getStr(String str) {
		ArrayList strlist = new ArrayList();
		//可能有些字段为空，所以此处改为` ，方便截取出全部字段。  jingq add  2014.12.15
		str = str.replace("`", "` ");
		String[] args = str.split("`");
		try {
			if (str.length() > 0) {
				//用args.length来判断是否是代码类    jingq add 2014.12.12
				if (str.indexOf("`") != -1&&args.length<=3) {//读代码类
					String strs[] = str.split("`");
					String s[] = strs[0].split(" ");
					for(int i=0;i<2;i++){
						strlist.add(s[i]);
					}
					String temp= "";
					for(int i=2;i<s.length;i++){
						temp+=s[i]+" ";
					}
					strlist.add(temp.substring(0, temp.length()-1));
					if(strs.length>1&&strs[1].trim().length()>0){//status字段
						strlist.add(strs[1].trim());
					}
					if(strs.length>2&&strs[2].trim().length()>0){//validateflag字段
						strlist.add(strs[2].trim());
					}
				} else {//读代码项
					
					//读出所有数据  jingq upd 2014.12.12
					String strs[] = str.split("`");
					String s[] = strs[0].replaceAll("\t", " ").split(" ");//使用制表位\t 需转成空格
					strlist.add(s[0]);
					
					/**
					 * update start
					 * strlist.add(s[s.length-1]);
					 * 上面的方式如果代码描述中间有空格，则只能导入最后的文字。
					 * 改为按空格分割后循环拼，这样描述才完整
					 * guodd 2016-10-26
					 */
					StringBuffer desc = new StringBuffer("");
					for(int k=1;k<s.length;k++){
						if(s[k].length()<1)
							continue;
						desc.append(s[k]).append(" ");
					}
					strlist.add(desc.toString().trim());
					/** update end */
					
					for (int i = 1; i < strs.length; i++) {
						strlist.add(strs[i].trim());
					}
				}
			}
		} catch (Exception e) {

		}
		return strlist;
	}

	public RecordVo getempVo(String codesetid, String desc, String id) {
		RecordVo codeitemvo = new RecordVo("codeitem");
		codeitemvo.setString("codesetid", codesetid);
		codeitemvo.setString("codeitemid", id);
		codeitemvo.setString("codeitemdesc", desc);
		codeitemvo.setString("parentid", id);
		codeitemvo.setString("childid", id);
		return codeitemvo;
	}

	public ArrayList uporaddCodeitem(ArrayList templist)
			throws GeneralException {
		HashMap rehm = new HashMap();// 存放数据库中存在的代码项数据
		LinkedHashMap hm = new LinkedHashMap();//必须用链式，否则计算parentid 出错 wangb 2019-11-27 bug 55780
		ArrayList codesist = (ArrayList) templist.get(0);
		String codesetid = (String) codesist.get(0);
		//【51902】当导入的代码为UN、UM、@K时，提示信息：单位/部门/岗位代码不允许导入！ guodd 2019-09-02
		if("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("label.codeitemid.un") + "/"+
					ResourceFactory.getProperty("label.codeitemid.um") + "/"+
					ResourceFactory.getProperty("hmuster.label.post") + 
					ResourceFactory.getProperty("kq.item.code") + 
					ResourceFactory.getProperty("batchimport.info.error.notimport"), "",
					""));
		}
		String status = (String)codesist.get(3);
		String validateflag = (String)codesist.get(4);
		String codeflag;
		// try {
		codeflag = SystemConfig.getPropertyValue("dev_flag");
		/*
		 * } catch (GeneralException e1) { throw new GeneralException("",
		 * "后台未设定dev_flag参数，请设定后再进行上传操作！","", ""); }
		 */
		ArrayList inputlist = new ArrayList();
		ArrayList updatelist = new ArrayList();// 需要更新的代码项
		ArrayList addlist = new ArrayList();// 新添加的代码项
		/*Pattern pattern = Pattern.compile("\\d*");
		Matcher matcher = pattern.matcher(codesetid);
		boolean b = matcher.matches();*/
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		boolean b = false;//判断是用户代码还是系统代码 系统true 用户false
		if("1".equals(status)|| "2".equals(status)){
			b = true;
		}
		if (b) {// 如果是用户模式则无权限导入系统代码
			if (codeflag == null || "0".equals(codeflag)|| "".equals(codeflag.trim())) {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory
								.getProperty("codemaintence.code.provide"), "",
						""));
			}
		}else{//在用户模式下导入的是用户代码，如果库中存在相同的代码是系统代码则无权导入
			if (codeflag == null || "0".equals(codeflag)|| "".equals(codeflag.trim())) {
				try {
					rs = dao.search("select * from codeset where codesetid='"
							+ codesetid + "'");
					while(rs.next()){
						String s = rs.getString("status");
						if("1".equals(s)||"2".equals(s)){
							throw GeneralExceptionHandler.Handle(new GeneralException("",
									"系统中存在与此代码类相同的系统代码，您无权用用户代码替换！", "",
									""));
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			
			rs = dao.search("select * from codeitem where codesetid='"
					+ codesetid + "'");
			while (rs.next()) {
				String id = rs.getString("codeitemid");
				String desc = rs.getString("codeitemdesc");
				RecordVo codeitemvo = getempVo(codesetid, desc, id);
				rehm.put(id, codeitemvo);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String date = sdf.format(new Date());
			RecordVo tempa = (RecordVo) templist.get(1);// 正要导入的第一条代码项数据
			tempa.setInt("layer",1);
			if (templist.size() == 2) {// 当只有一条代码项数据
				//使用导入的转换代码和开始、结束时间   jingq upd 2014.12.12
				/*if(validateflag.equals("1")){//当导入的是时间标示代码类 xuj2009-10-12
					tempa.setDate("start_date", date);
					tempa.setDate("end_date", "9999-12-31");
				}else
					tempa.setInt("invalid", 1);*/
				if (rehm.containsKey(tempa.getString("codeitemid"))) {
					updatelist.add(tempa);
				} else {
					addlist.add(tempa);
				}

			}
			Integer length = new Integer(tempa.getString("codeitemid").length());
			hm.put(length.toString(), length);
			//导入代码项 必须添加层级layer 字段值 wangb 2019-11-27 bug 55780
			HashMap layerHM = new HashMap(); 
			layerHM.put(tempa.getString("codeitemid"),tempa.getInt("layer"));
			for (int i = 2; i < templist.size(); i++) {
				try {
					RecordVo tempb = (RecordVo) templist.get(i);
					if (i == templist.size() - 1) {// 正要导入的最后一条代码项数据
						/*if(validateflag.equals("1")){//当导入的是时间标示代码类 xuj2009-10-12
							tempb.setDate("start_date", date);
							tempb.setDate("end_date", "9999-12-31");
						}else
							tempb.setInt("invalid", 1);*/
						int codeitemlen = tempb.getString("codeitemid")
								.length();
						String tmpparentid = tempb.getString("parentid");
						for(Iterator it=hm.values().iterator();it.hasNext();){
							Integer tpinte = (Integer) it.next();
							if (codeitemlen - tpinte.intValue() > 0) {
								tmpparentid = tempb
								.getString("parentid").substring(0,
										tpinte.intValue());
							}
						}
						tempb.setString("parentid", tmpparentid);
						if(layerHM.get(tmpparentid) == null) {
							tempb.setInt("layer", (Integer)tempa.getInt("layer")+1);
						}else {
							tempb.setInt("layer", (Integer)layerHM.get(tmpparentid)+1);
						}
//						tempb.setInt("layer", (Integer)(layerHM.get(tmpparentid)==null?0:layerHM.get(tmpparentid))+1);
						if (rehm.containsKey(tempb.getString("codeitemid"))) {
							updatelist.add(tempb);
						} else {
							addlist.add(tempb);
						}
					}
					if (tempb.getString("codeitemid").length() != tempa
							.getString("codeitemid").length()) {// 判断代码项数据间的父子关系
						if (tempb.getString("codeitemid").length() > tempa
								.getString("codeitemid").length()) {
							Integer len = new Integer(tempa.getString(
									"codeitemid").length());
							if (!hm.containsKey(len.toString()))
								hm.put(len.toString(), len);
							tempa.setString("childid", tempb
									.getString("codeitemid"));
							tempb.setString("parentid", tempa
									.getString("codeitemid"));
							tempb.setInt("layer", (Integer)layerHM.get(tempa.getString("codeitemid"))+1);
						}
						if (tempb.getString("codeitemid").length() < tempa
								.getString("codeitemid").length()) {
							int codeitemlen = tempb.getString("codeitemid")
									.length();
							String tmpparentid = tempb.getString("parentid");
							for(Iterator it=hm.values().iterator();it.hasNext();){
								Integer tpinte = (Integer) it.next();
								if (codeitemlen - tpinte.intValue() > 0) {
									tmpparentid = tempb
									.getString("parentid").substring(0,
											tpinte.intValue());
								}
							}
							tempb.setString("parentid", tmpparentid);
							if(layerHM.containsKey(tmpparentid)){
								tempb.setInt("layer", (Integer)layerHM.get(tmpparentid)+1);
							}else{
								tempb.setInt("layer", 1);
							}
						}

					} else {
						if (((Integer) hm.values().iterator().next())
								.intValue() != tempb.getString("codeitemid")
								.length())
							tempb.setString("parentid", tempa
									.getString("parentid"));
						tempb.setInt("layer", tempa.getInt("layer")+1);
					}

					RecordVo input = tempa;
					/*if(validateflag.equals("1")){//当导入的是时间标示代码类 xuj2009-10-12
						input.setDate("start_date", date);
						input.setDate("end_date", "9999-12-31");
					}else
						input.setInt("invalid", 1);*/
					if (rehm.containsKey(input.getString("codeitemid"))) {
						updatelist.add(input);
					} else {
						addlist.add(input);
					}
					layerHM.put(tempb.getString("codeitemid"),tempb.getInt("layer"));
					tempa = tempb;

				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			inputlist.add(updatelist);
			inputlist.add(addlist);

		} catch (Exception e) {
			e.printStackTrace();
			// throw GeneralExceptionHandler.Handle(e);
		}
		return inputlist;
	}
}
