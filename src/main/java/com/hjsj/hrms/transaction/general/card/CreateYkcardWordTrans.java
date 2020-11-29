package com.hjsj.hrms.transaction.general.card;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.YkcardOutWord;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.ArrayList;

public class CreateYkcardWordTrans extends IBusiness {

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
		String infokind=(String)this.getFormHM().get("infokind");
		String userbase=(String)this.getFormHM().get("userbase");
		String tabid=(String)this.getFormHM().get("tabid");
		String b0110=(String)this.getFormHM().get("b0110");
		String pre=(String)this.getFormHM().get("pre");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		String fileFlag=(String)this.getFormHM().get("fileFlag");
		boolean isMobile = "1".equals(this.getFormHM().get("isMobile"));//手机端标识
		ArrayList nid=new ArrayList();
		if("all".equals(flag)||"1".equals(flag)){//查询多个
			if(this.getFormHM().get("nid")!=null)
				nid=(ArrayList)this.getFormHM().get("nid");
		}else{
			nid.add((String)this.getFormHM().get("nid"));
		}
		
		
		
		/**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
		CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
		if(infokind!=null && "1".equalsIgnoreCase(infokind)){//liuy 2014-10-23 只有人员才需要判断人员库
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
			YkcardOutWord outWord=new YkcardOutWord(this.userView,this.frameconn);
			if("5".equalsIgnoreCase(infokind))
			{
				String plan_id=(String)this.getFormHM().get("plan_id");
				outWord.setPlan_id(plan_id);
			}
			outWord.setQueryTypeTime(cyear, cmonth, cyear, cmonth, season, cyear, ctimes, cdatestart, cdateend);
			String url="";
			DocumentBuilder builder = new AsposeLicenseUtil();
			String filePath="";
			if("1".equals(flag)){//批量生成单个文件 已压缩文件夹形式导出
				ArrayList filnames=new ArrayList();
				for (int i = 0; i < nid.size(); i++) {
					String id=(String)nid.get(i);
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
				url = SafeCode.encode(PubFunc.encrypt(url));
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
			//throw GeneralExceptionHandler.Handle(e);
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
	public String createZipFile(ArrayList filenames,String tabName) {
		String tmpFileName = this.userView.getUserName() + "_" + tabName + ".zip";
		byte[] buffer = new byte[2048];
		String filePath = System.getProperty("java.io.tmpdir") + File.separator;
		BufferedInputStream origin = null;
		try(FileOutputStream fileOutputStream=new FileOutputStream(filePath + tmpFileName);
			ZipOutputStream out = new ZipOutputStream(fileOutputStream)) {
			out.setEncoding("GBK");
			for (int i = 0; i < filenames.size(); i++) {
				FileInputStream fis = null;
				File file = null;
				try {
					file = new File(filePath + filenames.get(i));
					fis = new FileInputStream(file);
					origin = new BufferedInputStream(fis, 2048);
					out.putNextEntry(new ZipEntry(file.getName()));
					int count;
					while ((count = origin.read(buffer, 0, 2048)) != -1) {
						out.write(buffer, 0, count);
					}
				} finally {
					PubFunc.closeResource(origin);
					PubFunc.closeResource(fis);
				}
				if (file.exists())
					file.delete();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return tmpFileName;
	}
}



