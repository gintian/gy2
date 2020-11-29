package com.hjsj.hrms.module.card.transaction;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.RowSet;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.aspose.words.Document;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.module.card.businessobject.CardConstantSet;
import com.hjsj.hrms.module.card.businessobject.YkcardOutWord;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

public class CreateYkcardWordTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		System.gc();
		//System.out.println("CreateYkcardExcelTrans-----------------------------------开始----------------------------");
		String flag=(String)this.getFormHM().get("flag");
		String cyear=(String)this.getFormHM().get("cyear");	
		String querytype=(String)this.getFormHM().get("querytype");	
		String cmonth=(String)this.getFormHM().get("cmonth");	
		String userpriv=(String)this.getFormHM().get("userpriv");	
		String istype=(String)this.getFormHM().get("istype");              /*0代表薪酬1登记表*/	
		String season=(String)this.getFormHM().get("season");	
		String ctimes=(String)this.getFormHM().get("ctimes");	
		String cdatestart=(String)this.getFormHM().get("cdatestart");	
		String cdateend=(String)this.getFormHM().get("cdateend");			
		String cardid=(String)this.getFormHM().get("cardid");
		String officeOrWps = (String)this.getFormHM().get("officeOrWps");
		String infokind=(String)this.getFormHM().get("infokind");
		String userbase=(String)this.getFormHM().get("userbase");
		String tabid=(String)this.getFormHM().get("tabid");
		String b0110=(String)this.getFormHM().get("b0110");
		String pre=(String)this.getFormHM().get("pre");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		String fileFlag=(String)this.getFormHM().get("fileFlag");
		String autoSize=(String)this.getFormHM().get("autoSize");
		
		boolean isMobile = "1".equals(this.getFormHM().get("isMobile"));//手机端标识
		String flagType=(String)this.getFormHM().get("flagType");;
		ArrayList nid=new ArrayList();
		if("all".equals(flag)||"1".equals(flag)){//查询多个
			if(this.getFormHM().get("nid")!=null)
				nid=(ArrayList)this.getFormHM().get("nid");
		}else{
			nid.add((String)this.getFormHM().get("nid"));
		}
		
		
		/**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
		CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
		if(infokind!=null && "1".equalsIgnoreCase(infokind)&&StringUtils.isNotEmpty(userbase)){//liuy 2014-10-23 只有人员才需要判断人员库
			if(!this.userView.isSuper_admin()){
				String paramBasePre=userbase;
				String returnBasePre =safeBo.checkDb(paramBasePre);//这个方法当不越权时返回传进去的人员库，越权时返回当前人员的第一个人员库
				/**当返回的人员库值的长度大于0并且不等于传进去的人员库时说明越权**/
				if(returnBasePre.trim().length()>0&&!paramBasePre.equals(returnBasePre)){//如果当前用户的人员库没有这个选中人员的人员库，终止导入
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
				}
			}
			if(!this.userView.isSuper_admin()){
				/**验证管理范围，如果越权则返回实有的管理范围**/
				String paramManapriv=this.userView.getManagePrivCodeValue();
				String realManapriv=safeBo.checkOrg(paramManapriv, "");
				String paramPre=userbase;//这里所有的人员库都进行了验证，如果越权的人员库，在上面就结束了
				String paramA0100=(String)nid.get(0);//这里的A0100尚未进行验证
				if(StringUtils.isNotEmpty(paramA0100)){//liuy 2015-3-24 8205：首页/登记表/员工工作证，切换到劳务人员库，没有人员时，批量生成PDF，提示：人员权限越权，操作被终止				
					if(paramA0100.indexOf("`")>-1) {
						paramA0100=paramA0100.split("`")[1];
					}
					String realA0100=safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
					if(realA0100.trim().length()>0&&!realA0100.equals(paramA0100)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
					}
				}
			}
		}
        /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员end**/
		if("0".equals(istype))
		{
		  XmlParameter xml=new XmlParameter("UN",userView.getUserOrgId(),"00");
		  xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn());	
		  cardid=xml.getCard_id();
		  
		  if(tabid==null||tabid.length()<=0)
		    {
		    	String flags=xml.getFlag();
				CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
				ArrayList cardidlist=cardConstantSet.setCardidSelect(this.getFrameconn(),this.userView,flags,pre,nid.get(0).toString(),b0110);
				if(cardidlist!=null&&cardidlist.size()>0)
				{
					CommonData dataobj=(CommonData)cardidlist.get(0);
					tabid=dataobj.getDataValue();
					cardid=tabid;
				}
		    }else
		    {
		    	cardid=tabid;
		    }
		    String type=xml.getType();              //0条件1时间
		    //System.out.println("CreateYkcardExcelTrans------type------------------" + type);
		    if("0".equals(type))
			 querytype="0";
		 
		}else if("1".equals(istype)){
		   querytype="0";
		}
		
		//System.out.println("CreateYkcardExcelTrans------istype------------------" + istype);
		try{
			
			if("all".equals(flagType)) {//选择导出全部时 应从查询结果表中取对应数据
				nid=this.getObjidList(infokind, this.userView);
			}else if("1".equals(flagType)) {
			    nid=getPargObjIdList(infokind, nid);
			}
			
			YkcardOutWord outWord=new YkcardOutWord(this.userView,this.frameconn);
			outWord.setOfficeOrWps(officeOrWps);
			if(StringUtils.isNotEmpty(autoSize)) {
				if("true".equals(autoSize)) {
				    outWord.setAutoSize(true);
				}else {
				    outWord.setAutoSize(false);
				}
			}
			if("5".equalsIgnoreCase(infokind))
			{
				String plan_id=(String)this.getFormHM().get("plan_id");
				outWord.setPlan_id(plan_id);
			}
			outWord.setQueryTypeTime(cyear, cmonth, cyear, cmonth, season, cyear, ctimes, cdatestart, cdateend);
			String url="";
			String filePath="";
			if("1".equals(flag)){//批量生成单个文件 已压缩文件夹形式导出
				ArrayList filnames=new ArrayList();
				for (int i = 0; i < nid.size(); i++) {
					String id=(String)nid.get(i);
					if("1".equals(infokind)&&id.indexOf("`")>-1) {
						userbase=id.split("`")[0];
						id=id.split("`")[1];
					}
					String filename="";
					filename=outWord.outWordYkcard(Integer.parseInt(cardid), id, querytype, infokind, userbase, userpriv, userpriv, fieldpurv);
					if(!"word".equals(fileFlag)){
						filePath = System.getProperty("java.io.tmpdir")+File.separator+filename;
						filename=wordToPdf(filePath,filename);
						/*Document doc = new Document(filePath);
						int lastindex = filePath.lastIndexOf(".");
						filePath = filePath.substring(0,lastindex)+".pdf";
						doc.save(filePath);
						//清除生成的word(tomcat临时文件中)
						File docfile = new File(filePath);
						if(docfile.exists())
							docfile.delete();*/
					}
					filnames.add(filename);	
				}
				url=createZipFile(filnames,outWord.getExportName());
			}else{
				url=outWord.outWordYkcard(Integer.parseInt(cardid), nid, querytype, infokind, userbase, userpriv, userpriv, fieldpurv);
				if(!"word".equals(fileFlag)){
					filePath = System.getProperty("java.io.tmpdir")+File.separator+url;
					url=wordToPdf(filePath,url);
					/*Document doc = new Document(filePath);
					int lastindex = url.lastIndexOf(".");
					url = url.substring(0,lastindex)+".pdf";
					doc.save(System.getProperty("java.io.tmpdir")+File.separator+url);
					//清除生成的word(tomcat临时文件中)
					File docfile = new File(filePath);
					if(docfile.exists())
						docfile.delete();*/
				}
			}
			
			if(!isMobile){
				url = PubFunc.encrypt(url);
			}
			
			this.getFormHM().clear();//清空参数，减少网络数据传输量
			this.getFormHM().put("url",url);
			if("1".equals(flag)){
				this.getFormHM().put("fileFlag", "zip");
			}else{
				this.getFormHM().put("fileFlag", fileFlag);
			}
			
			//System.out.println("CreateYkcardExcelTrans-----------------------------------结束----------------------------");
		}catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			System.gc();
		}
	}
	
	
	public String  wordToPdf(String filePath,String url){
		try {
			Document doc = new Document(filePath);
			int lastindex = url.lastIndexOf(".");
			url = url.substring(0,lastindex)+".pdf";
			doc.save(System.getProperty("java.io.tmpdir")+File.separator+url);
			//清除生成的word(tomcat临时文件中)
			File docfile = new File(filePath);
			if(docfile.exists())
				docfile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * 生成导出文件 压缩文件
	 * **/
	public String createZipFile(ArrayList filenames,String tabName){
		String tmpFileName=this.userView.getUserName()+"_"+tabName+".zip";
		byte[] buffer=new byte[2048];
		String filePath=System.getProperty("java.io.tmpdir")+File.separator;
		String strZipPath=filePath+tmpFileName;
		BufferedInputStream origin=null;
		FileOutputStream fileOut = null;
		ZipOutputStream out = null;
		try {
			fileOut = new FileOutputStream(filePath+tmpFileName);
			out = new ZipOutputStream(fileOut);
			out.setEncoding("GBK");
			for (int i = 0; i < filenames.size(); i++) {
				FileInputStream fis=null;
				File file=null;
				try {
					file=new File(filePath+filenames.get(i));
					fis=new FileInputStream(file);
					origin=new BufferedInputStream(fis,2048);
					out.putNextEntry(new ZipEntry(file.getName()));
					int count;
					while((count = origin.read(buffer, 0, 2048)) != -1){
						out.write(buffer, 0, count);
					}
				}finally{
					PubFunc.closeResource(fis);
					PubFunc.closeResource(origin);
				}
				if(file.exists())
					file.delete();
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeIoResource(out);
			PubFunc.closeIoResource(origin);
		}
		return tmpFileName;
	}
	/***
	 * 取部分人员 按人员顺序排序
	 * @param inforkind
	 * @param objlist
	 * @return
	 */
	private ArrayList<String> getPargObjIdList(String inforkind,ArrayList<String> objlist){
	    ArrayList<String> list=new ArrayList<String>();
	    StringBuffer wheresbf=new StringBuffer();
	    boolean flag=false;
	    for(String key:objlist) {
	        if(key.indexOf("`")>-1) {
	            flag=true;
	            wheresbf.append(" or ( AA.dbname='"+key.split("`")[0]+"' and ");
	            wheresbf.append(" AA.objid='"+key.split("`")[1]+"') ");
	        }else {
	            wheresbf.append(" or AA.objid='"+key+"' ");
	        }
	    }
	    ContentDAO dao=new ContentDAO(this.frameconn);
        TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get("ykcard");
        RowSet rs=null;
        try {
            String sql=" select * from ("+tableCacheList.getTableSql()+") AA "+" where "+wheresbf.substring(3).toString()+tableCacheList.getSortSql();
            rs=dao.search(sql);
            while(rs.next()) {
                if(flag) {
                    list.add(rs.getString("dbname")+"`"+rs.getString("objid"));
                }else {
                    list.add(rs.getString("objid"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
	}
	
	private ArrayList<String> getObjidList(String inforKind,UserView userview) throws Exception{
		ArrayList<String> list=new ArrayList<String>();
		RowSet rs=null;
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get("ykcard");
			StringBuffer sql=new StringBuffer();
			if("1".equals(inforKind)&&tableCacheList!=null&&StringUtils.isNotEmpty(tableCacheList.getTableSql())) {
				sql.append("select dbname as nbase,objid from(");
				sql.append(tableCacheList.getTableSql());
				sql.append(" where A.username=? and A.flag=? ");
				sql.append(") c");
			}else if("5".equals(inforKind)){
				sql.append(tableCacheList.getTableSql());
			}else {
				sql.append("select objid,nbase from t_card_result where username=? and flag=?");
			}
			if("5".equals(inforKind)) {
				rs=dao.search(sql.toString());
			}else {
				rs=dao.search(sql.toString() ,Arrays.asList(userview.getUserName(),inforKind));
			}
			while(rs.next()) {
				if("1".equals(inforKind))
					list.add(rs.getString("nbase")+"`"+rs.getString("objid"));
				else
					list.add(rs.getString("objid"));
			}
			
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
}



