package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class SaveSetItemDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag="false";
			
			String setName=(String)this.formHM.get("setName");
			String nbase=(String)this.formHM.get("nbase");
			String currentObject=this.formHM.get("currentObject")==null?"":(String)this.formHM.get("currentObject");
			ArrayList fileList=this.formHM.get("fileList")==null?new ArrayList():(ArrayList)this.formHM.get("fileList");
			ArrayList delList=this.formHM.get("delList")==null?new ArrayList():(ArrayList)this.formHM.get("delList");
			MorphDynaBean bean=(MorphDynaBean)this.formHM.get("saveData");
			HashMap saveData=PubFunc.DynaBean2Map(bean);
//			int dataIndex=(Integer)this.formHM.get("dataIndex");
			
			if("".equals(currentObject)||"".equals(setName)){
				this.formHM.put("flag", flag);
				return;
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			currentObject=PubFunc.decrypt(currentObject);
			
			nbase=getNbase(nbase,currentObject);
			if(isNbase(dao,nbase)){
				if(currentObject.indexOf(nbase)>-1)
					currentObject = currentObject.substring(3);
			}else{
				this.formHM.put("flag", flag);
				return;
			}
			/**
			if(!nbase.contains(",")){
				if(nbase.length()>3)
					nbase=PubFunc.decrypt(nbase);
				if(currentObject.indexOf(nbase)>=0){
					if(currentObject.indexOf(nbase)>3){
						String newNbase = PubFunc.decrypt(nbase);
						if(isNbase(dao,newNbase)){
							nbase = newNbase;
						}
					}
					currentObject = currentObject.substring(3);
				}
			}else{
				String newNbase = currentObject.substring(0,3);
				if(isNbase(dao,newNbase)){
					nbase = newNbase;
					currentObject = currentObject.substring(3);
				}else{
					this.formHM.put("flag", flag);
					return;
				}
			}
			**/
			String table="";
			String dbflag="";
			if(setName.toUpperCase().startsWith("A")){
				table=nbase+setName;
				dbflag="A";
			}else if(setName.toUpperCase().startsWith("B")){
				table=setName;
				dbflag="B";
			}else if(setName.toUpperCase().startsWith("K")){
				table=setName;
				dbflag="k";
			}
			
			for(Object obj:saveData.keySet()){
				String itemid=(String)obj;
				ArrayList list=this.userView.getPrivFieldList(setName);
				FieldItem item = getFieldItem(list,itemid);
				if(item==null)
					continue;
				Object objvalue=null;
				String value=(String)saveData.get(itemid);
				if("A".equals(item.getItemtype())){
					value=value.split("`")[0];
					//如果长度超出指标长度，则自动截断
					if(item.getItemlength()<value.length())
						value=value.substring(0, item.getItemlength());
					objvalue=value;
				}else if("D".equals(item.getItemtype())){
					//yyyy-MM-dd H:m:s
					String type="yyyy-MM-dd H:m:s";
					if(value.length()==4){
						type="yyyy";
					}else if(value.length()==7){
						type="yyyy-MM";
					}else if(value.length()==10){
						type="yyyy-MM-dd";
					}else if(value.length()==13){
						type="yyyy-MM-dd H";
					}else if(value.length()==16){
						type="yyyy-MM-dd H:m";
					}else if(value.length()==19){
						type="yyyy-MM-dd H:m:s";
					}
					SimpleDateFormat sdf = new SimpleDateFormat(type);
					if(!"".equals(value))
						objvalue =DateUtils.getSqlDate(sdf.parse(value));
					else
						objvalue =null;
				}else if("M".equals(item.getItemtype())){
					if (value == null || "null".equals(value) || "".equals(value))
					{	
						value=null;
					}
					objvalue=value;
				}else if("N".equals(item.getItemtype())){
					if (value == null || "null".equals(value) || "".equals(value))
					{	
						value=null;
					}
					objvalue=value;
				}
				saveData.put(itemid, objvalue);
			}
			
			String dataIndex="";
			String itemSql="";
			ArrayList itemValueList=new ArrayList();
			String itemchildguid = "";
			if((this.formHM.get("dataIndex")==null||"".equals(this.formHM.get("dataIndex").toString()))
					&&(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName))){
				//新增
				String sql="";
				String columns="";
				String values="";
				int index=1;
				ArrayList valueList=new ArrayList();
				valueList.add(currentObject);
				sql="select MAX(i9999) maxi9999 from "+table+" where A0100 = ?";
				this.frowset=dao.search(sql,valueList);
				if(this.frowset.next()){
					index=this.frowset.getInt("maxi9999")+1;
				}
				dataIndex=String.valueOf(index);
				valueList=new ArrayList();
				if(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName)){
					columns+=" I9999,";
					values+="?,";
					valueList.add(dataIndex);
					String primkey="A0100";
					if(setName.toUpperCase().startsWith("A")){
						primkey="A0100";
					}else if(setName.toUpperCase().startsWith("B")){
						primkey="B0110";
					}else if(setName.toUpperCase().startsWith("H")){
						primkey="H0100";
					}else if(setName.toUpperCase().startsWith("K")){
						primkey="K0100";
					}
					columns+=primkey+",";
					values+="?,";
					valueList.add(currentObject);
				}
				for(Object obj:saveData.keySet()){
					String itemid=(String)obj;
					columns+=itemid+",";
					Object value=saveData.get(itemid);
//					value=value.split("`")[0];
					valueList.add(value);
					values+="?,";
				}
				if(!"".equals(columns)){
					columns=columns.substring(0, columns.length()-1);
				}
				if(!"".equals(columns)){
					values=values.substring(0, values.length()-1);
				}
				sql="insert into "+table+" ("+columns+") values ("+values+")";
				itemSql = sql;
				itemValueList = valueList;
//				dao.insert(sql, valueList);
			}else{
				//修改
				String sql="";
				ArrayList valuelist=new ArrayList();
				String columns=" set ";
				dataIndex=(String)this.formHM.get("dataIndex");
				if(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName)){
					columns+=" I9999 = ? ,";
					valuelist.add(dataIndex);
				}
				
				for(Object obj:saveData.keySet()){
					String itemid=(String)obj;
					Object value=
							saveData.get(itemid);
//					value=value.split("`")[0];
					columns+=itemid+" = ?,";
					valuelist.add(value);
				}
				columns=columns.substring(0, columns.length()-1);
				if(setName.toUpperCase().startsWith("A")){
					sql="update "+table+columns+" where A0100 = ? ";
				}else if(setName.toUpperCase().startsWith("B")){
					sql="update "+table+columns+" where B0110 = ? ";
				}else if(setName.toUpperCase().startsWith("H")){
					sql="update "+table+columns+" where H0100 = ? ";
				}else if(setName.toUpperCase().startsWith("K")){
					sql="update "+table+columns+" where E01A1 = ? ";
				}
				valuelist.add(currentObject);
				//主集没有I9999
				if(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName)){
					sql+="and I9999 = ?";
					valuelist.add(dataIndex);
				}
				itemSql = sql;
				itemValueList = valuelist;
//				dao.update(sql,valuelist);
			}
			//保存子集附件
			if(fileList.size()>0){
				String sql="";
				ArrayList valuelist=new ArrayList();
//					PhotoImgBo imgbo=new PhotoImgBo(this.frameconn);
//			        String RootDir = imgbo.getPhotoRootDir();
				//获取A01中的GUIDKEY
				String mainguid="";
				sql="select GUIDKEY from "+nbase+"A01 where A0100 = ? ";
				valuelist=new ArrayList();
				valuelist.add(currentObject);
				this.frowset=dao.search(sql,valuelist);
				if(this.frowset.next()){
					mainguid=this.frowset.getString("GUIDKEY");
				}
				//获取子集中的GUIDKEY
				String childguid="";
				if(!"A01".equals(setName)){
					//xus 18/9/18 如果表中没有guidkey字段 则加上此字段
					DbWizard db = new DbWizard(this.frameconn);
					//表中没有GUIDKEY字段  增加GUIDKEY标识字段
					if(!db.isExistField(nbase+setName, "GUIDKEY",false)){
						Table t = new Table(nbase+setName);
						Field f = new Field("GUIDKEY",DataType.STRING);
						f.setNullable(true);
						f.setLength(38);
						t.addField(f);
						db.addColumns(t);
					}
					sql="select GUIDKEY from "+nbase+setName+" where A0100 = ? and I9999 = ? ";
					valuelist=new ArrayList();
					valuelist.add(currentObject);
					valuelist.add(dataIndex);
					this.frowset=dao.search(sql,valuelist);
					if(this.frowset.next()){
						childguid=this.frowset.getString("GUIDKEY");
					}
				}
				//如果没有GUIDKEY 则生成
				if(childguid==null||"null".equals(childguid)||"".equals(childguid)){
					UUID uuid =UUID.randomUUID();
					childguid = uuid.toString();
				}
				itemchildguid = childguid;
				for(Object obj:fileList){
					MorphDynaBean filebean=(MorphDynaBean)obj;
					String filename=filebean.get("filename").toString();
					filename=PubFunc.decrypt(filename);
					String fileid = filebean.get("fileid").toString();
					
//						String filepath = filebean.get("filepath").toString();
//						if(filepath.length()>0)
//							filepath=PubFunc.decrypt(filepath);
//						String fileext=filebean.get("fileext").toString();
					
					//xus 文件保存到临时文件夹
//						ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
//						String consRootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
//				          
//						 consRootDir = consRootDir.replace("\\",File.separator);          
//				          if (!consRootDir.endsWith(File.separator))
//				        	  consRootDir =consRootDir+File.separator;   
//				          RootDir = consRootDir+"multimedia"+File.separator;
	                //xus 文件保存到临时文件夹---end
					
					
					if("add".equals(filebean.get("action").toString())){
						// 59383 上传组件在上传时已保存附件了 不知为何在这里又保存一次 导致重复
//						saveFileToSys(filebean, nbase, setName, currentObject, dataIndex, mainguid, childguid, dbflag);
						IDGenerator idg = new IDGenerator(2, this.frameconn);
						String id = idg.getId("hr_multimedia_file.id");
						//-------------新增到hr_multimedia_file表中--------------begin
						String srcfilename=filebean.get("srcfilename").toString();
						String ext=filebean.get("fileext").toString();
						String topic=srcfilename.substring(0, srcfilename.indexOf(ext));
						Date date=new Date();
						Timestamp time=new Timestamp(date.getTime());
						sql = "insert into hr_multimedia_file "
								+ "(id,mainguid,childguid,nbase,a0100,displayorder,topic,class,description,path,filename,srcfilename,ext,dbflag"
								+ ",createusername,createtime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						valuelist=new ArrayList();
						valuelist.add(id);
						valuelist.add(mainguid);
						valuelist.add(childguid);
						valuelist.add(nbase);
						valuelist.add(currentObject);
						valuelist.add(id);
						valuelist.add(topic);
						valuelist.add("F");
						valuelist.add(null);
						valuelist.add(fileid);
						valuelist.add(filename);
						valuelist.add(srcfilename);
						valuelist.add(ext);
						valuelist.add(setName.charAt(0)+"");
						valuelist.add(this.getUserView().getUserName());
						valuelist.add(time);
						dao.insert(sql, valuelist);
						//-------------新增到hr_multimedia_file表中--------------end
					}else{
//							filepath=filepath.substring(filepath.indexOf(RootDir)+RootDir.length());
//							filepath=filepath.substring(0,filepath.indexOf(filename));
//							if(filepath.endsWith("\\")||filepath.endsWith("/"))
//								filepath=filepath.substring(0,filepath.length()-1);
						if(!"A01".equals(setName)){
							sql="delete from hr_multimedia_file where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and childguid=(select GUIDKEY from "+nbase+setName+" where A0100 =? and I9999=?) and path=?";//filename=? and 
						}else{
							sql="delete from hr_multimedia_file where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and (childguid='' or childguid is null) and path=?";//filename=? and 
						}
						valuelist=new ArrayList();
						valuelist.add(currentObject);
						if(!"A01".equals(setName)){
							valuelist.add(currentObject);
							valuelist.add(dataIndex);
						}
//						valuelist.add(filename);
						valuelist.add(fileid);
						dao.delete(sql, valuelist);
						// 删除附件
						VfsService.deleteFile(this.userView.getUserName(), fileid);
					}
				}
			}
			//删除附件
			if(!delList.isEmpty()) {
				for(Object obj:fileList){
					MorphDynaBean filebean=(MorphDynaBean)obj;
					String fileid = filebean.get("fileid").toString();
					// 删除附件
					boolean deleteFileFlag = VfsService.deleteFile(this.userView.getUserName(), fileid);
					if(deleteFileFlag) {
						//删除hr_multimedia_file表中记录
						String sql = "";
						ArrayList valuelist = new ArrayList();
						if(!"A01".equals(setName)){
							sql="delete from hr_multimedia_file where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and childguid=(select GUIDKEY from "+nbase+setName+" where A0100 =? and I9999=?) and path=?";//filename=? and 
						}else{
							sql="delete from hr_multimedia_file where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and (childguid='' or childguid is null) and path=?";//filename=? and 
						}
						valuelist=new ArrayList();
						valuelist.add(currentObject);
						if(!"A01".equals(setName)){
							valuelist.add(currentObject);
							valuelist.add(dataIndex);
						}
//						valuelist.add(filename);
						valuelist.add(fileid);
						dao.delete(sql, valuelist);
					}
				}
			}
			//-------------新增到子集中--------------
			if((this.formHM.get("dataIndex")==null||"".equals(this.formHM.get("dataIndex").toString()))
					&&(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName))){
				dao.insert(itemSql, itemValueList);
			}else{
				dao.update(itemSql,itemValueList);
			}
			if(!"".equals(itemchildguid)){
				String sql="update "+table+" set GUIDKEY  = ? where A0100 = ? and I9999 = ? ";
				ArrayList valuelist=new ArrayList();
				valuelist.add(itemchildguid);
				valuelist.add(currentObject);
				valuelist.add(dataIndex);
				dao.update(sql, valuelist);
			}
			this.formHM.put("flag", "true");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.formHM.put("flag", "false");
			this.formHM.put("message", e.getMessage());
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * xus 判断nbase是否存在
	 * @param dao
	 * @param nbase
	 * @return
	 * @throws SQLException
	 */
	private boolean isNbase(ContentDAO dao,String nbase) throws SQLException{
		boolean flag=false;
		ArrayList values= new ArrayList();
		values.add(nbase);
		String sql = "select pre from DBName where pre = ?";
		this.frowset=dao.search(sql,values);
		if(this.frowset.next()){
			flag=true;
		}
		return flag;
	};
	
	private FieldItem getFieldItem(ArrayList list,String itemid){
		FieldItem item = null;
		for(Object obj:list){
			FieldItem newitem=(FieldItem)obj;
			if(newitem.getItemid().toUpperCase().equals(itemid.toUpperCase())){
				item=newitem;
				break;
			}
		}
		return item;
	}
	
	/**
	 * 文件保存到多媒体路径
	 * @param filebean
	 * @param nbase
	 * @param setid
	 * @param A0100
	 * @param I9999
	 * @param mainguid
	 * @param childguid
	 * @return 多媒体路径
	 * @throws GeneralException
	 */
	public String saveFileToSys(MorphDynaBean filebean,String nbase,String setid,String A0100,String I9999
			,String mainguid,String childguid,String dbflag) throws GeneralException{
		String path="";
		String srcName=filebean.get("srcfilename").toString();
		String filetitle=srcName.substring(0,srcName.lastIndexOf("."));
		String filename=filebean.get("filename").toString();
		filename=PubFunc.decrypt(filename);
		// vfs改造 该filepath 已改为 fileid
		String fileid=filebean.get("fileid").toString();
//		String filepath=filebean.get("filepath").toString();
//		filepath=PubFunc.decrypt(filepath);
		String fileext=filebean.get("fileext").toString();
		
		
//		if (!filepath.endsWith(File.separator))
//			filepath =filepath+File.separator;
//		File oldfile = new File(filepath+filename);
		
		MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
				dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
		multiMediaBo.initParam();
		InputStream input = null;
		try {
			input = VfsService.getFile(fileid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File oldfile = multiMediaBo.inputstreamtofile(input, filename);
		
//		path=multiMediaBo.getRootDir();
          
		HashMap valueMap = new HashMap();
		valueMap.put("mainguid",mainguid);
		valueMap.put("childguid",childguid);
		valueMap.put("nbase",nbase);
		valueMap.put("a0100",A0100);
		valueMap.put("filetype","F");
		valueMap.put("filetitle",filetitle);
		valueMap.put("description","");
          
		multiMediaBo.saveMultimediaFile(valueMap, oldfile,true);
          
		return path;
	}
	
	
	private String getRelativeDir(String dbflag,String mainguid,String childguid,String setid,String RootDir) throws GeneralException
	{
		String relative =dbflag;
		try{
			String str  = mainguid; 
			int iHash = Math.abs(str.hashCode());
			String dir1 = ""+iHash/1000000%500;
			while (dir1.length()<3) dir1 ="0"+dir1;
			String dir2 = ""+iHash/1000%500;
			while (dir2.length()<3) dir2 ="0"+dir2;     
			relative =relative + "\\"+"A"+dir1 + "\\"+"A"+dir2 ;            
			relative =relative+"\\"+mainguid+"\\";
			if (!"".equals(setid)){              
				relative =relative+setid.toUpperCase()+"\\";                
				if ("01".equals(setid.substring(1)))
					relative =relative+mainguid;//主集的
				else
					relative =relative+childguid;
			}
			//创建目录
			String dir = RootDir + relative;   
			dir =dir.replace("\\", File.separator).replace("/", File.separator);
			File tempDir = new File(dir);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
		}
		catch(Exception e){
			e.printStackTrace();            
		}
		return relative;
		
	}
	
	private String getNbase(String nbases,String currentObject){
		String nbase="";
		if(nbases.contains(",")){
			//如果传入多条nbase则取currentObject中的nbase
			nbase = currentObject.substring(0,3);
		}else{
			if(nbases.length()>3){
				//解密
				nbase =PubFunc.decrypt(nbases);
				//选人组件获取的nbase带有前缀，去掉前缀
				nbase=nbase.substring(nbase.indexOf("_")+1,nbase.length());
			}else{
				nbase=nbases;
			}
		}
		return nbase;
	}
}
