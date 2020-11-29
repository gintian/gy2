<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.hjsj.hrms.interfaces.general.DBstep.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig"%>
<%@ page import="oracle.jdbc.*" %>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher" %>
<%	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    iDBManager2000 ObjConnBean = new iDBManager2000();
    Base64 base64;
	String mCommand;
	String mDocumentID = "";
	String mSignatureID = "";
	String mSignature = "";	
	String mSignatures;

	String strSql;
  String mUserName;
  String mExtParam;

	boolean mResult;
	java.lang.String KeyName;                 //文件名
	java.io.File ObjFile;                     //文件对象
	java.io.FileReader ObjFileReader;         //读文件对象
	char[] ChrBuffer;                        //缓冲
	int intLength;                            //实际读出的字符数


  byte[] SignatureBody = null;
  int mSignatureSize;


	String mSignatureName;			  //印章名称
	String mSignatureUnit;			  //签章单位
	String mSignatureUser;			  //持章人
	String mSignatureSN;			  //签章SN
	String mSignatureGUID;			  //全球唯一标识符

	String mMACHIP;			  //机器IP
	String OPType;			  //操作标志
	String mKeySn;       //KEY序列号
	mCommand=request.getParameter("COMMAND");
	mUserName=new String(request.getParameter("USERNAME").getBytes("8859_1"));
	mExtParam=new String(request.getParameter("EXTPARAM").getBytes("8859_1"));

	File tempFile=null;
	String GIFIMAGE="";
 
  if(mCommand.equalsIgnoreCase("SAVESIGNATURE")){
     mDocumentID=new String(request.getParameter("DOCUMENTID").getBytes("8859_1"));
     mSignatureID=new String(request.getParameter("SIGNATUREID").getBytes("8859_1"));
     mSignature=new String(request.getParameter("SIGNATURE").getBytes("8859_1"));
     SignatureBody = mSignature.getBytes();
     mSignatureSize = SignatureBody.length;
    //  base64 = new Base64();
   // mSignatureID = base64.encode(mSignatureID).toString();
   //  mDocumentID = base64.encode(mDocumentID).toString();
     strSql="SELECT * from HTMLSignature Where SignatureID='"+mSignatureID+"'";
     String flag ="0";
     try{
       if (ObjConnBean.OpenConnection()) {
         try {
           ResultSet result = ObjConnBean.ExecuteQuery(strSql);
           if (result.next()) {
           if(Sql_switcher.searchDbServer()==2){
    	        strSql = "update HTMLSignature set SIGNATUREID=?,SignatureSize=?,Signature=EMPTY_BLOB() Where SignatureID='"+mSignatureID+"'";
    			}
    			if(Sql_switcher.searchDbServer()==1){
    			  strSql = "update HTMLSignature set SIGNATUREID=?,SignatureSize=?,Signature=? Where SignatureID='"+mSignatureID+"'";
    			  }
    			  flag="1";
           }
           else {
           if(Sql_switcher.searchDbServer()==2){
            strSql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,Signature,username) values (?,?,?,EMPTY_BLOB(),'"+userView.getUserName()+"') ";
            }
            if(Sql_switcher.searchDbServer()==1){
        	 strSql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,Signature,username) values (?,?,?,?,'"+userView.getUserName()+"') ";
         }
             java.util.Date dt=new java.util.Date();
             long lg=dt.getTime();
             Long ld=new Long(lg);
             mSignatureID=ld.toString();
             flag="0";
             //写入signxml里
           }
           result.close();
         }
         catch (SQLException e) {
           System.out.println(e.toString());
         }
         java.sql.PreparedStatement prestmt=null;
          Statement stmt=null;
         try {
         //批量签章
         
         ResultSet rs2=null;
         if(mDocumentID.endsWith(","))
         mDocumentID = mDocumentID.substring(0,mDocumentID.length()-1);
         String mDocumentIDs [] = mDocumentID.split(",");
	     for(int i =0;i<mDocumentIDs.length;i++){
	           stmt = ObjConnBean.Conn.createStatement();
	           String a_documentid="";
	           if(flag.equals("0")) 
	           			 a_documentid=mDocumentIDs[i];
	           else
	          			 a_documentid=mSignatureID;
	           rs2=stmt.executeQuery("select * from HTMLSignature where DocumentID='"+a_documentid+"' and lower(username)='"+userView.getUserName().toLowerCase()+"'");
	           if(userView.getVersion()<70){//版本号小于70才启用这个功能
		           if(rs2.next())
		           		continue;
	           }
	           ObjConnBean.Conn.setAutoCommit(true) ; 
	           prestmt =ObjConnBean.Conn.prepareStatement(strSql); 
	           if(flag.equals("0")){
		           prestmt.setString(1, mDocumentIDs[i]);
		           prestmt.setString(2, mSignatureID);
		           prestmt.setInt(3,mSignatureSize);
		         //  InputStream in =null;
		      //   	in =  new ByteArrayInputStream(SignatureBody);
		      //     byte[] mTmp = new byte[mSignatureSize];
		      //     in.read(mTmp, 0, mSignatureSize);
			       if(Sql_switcher.searchDbServer()==1){
			          prestmt.setBinaryStream(4,new ByteArrayInputStream(SignatureBody),mSignatureSize);
			       }
	           }else{
	               prestmt.setString(1, mSignatureID);
		           prestmt.setInt(2,mSignatureSize);
		           if(Sql_switcher.searchDbServer()==1){
			          prestmt.setBinaryStream(3,new ByteArrayInputStream(SignatureBody),mSignatureSize);
			       }
	          }
	         ObjConnBean.Conn.setAutoCommit(false) ;
	           prestmt.execute();
	           ObjConnBean.Conn.commit();
	           if(Sql_switcher.searchDbServer()==2){
		           ObjConnBean.Conn.setAutoCommit(false) ; 
		           ResultSet rs = stmt.executeQuery("select Signature from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentIDs[i]+"'" + " for update");
		           //oracle.jdbc.OracleResultSet update = (OracleResultSet) rs;
		           
		          if (rs.next()){
		             try{
		              java.sql.Blob Signature = (java.sql.Blob)rs.getBlob("Signature");
		               //System.out.println(Signature);
		               ObjConnBean.PutAtBlob(Signature,SignatureBody);
		             }
		             catch (IOException e) {
		                System.out.println(e.toString());
		             }
		           }
		           ObjConnBean.Conn.commit();
	          }
	          
	           
	          // stmt = ObjConnBean.Conn.createStatement();
	           
	        ///oracle   ResultSet rs = stmt.executeQuery("select Signature from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'" + " for update");
	        ///oracle   oracle.jdbc.OracleResultSet update = (OracleResultSet) rs;
	      //      ResultSet rs = stmt.executeQuery("select Signature from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'" + " ");
	      //     if (rs.next()){
	     //        try{
	          ///     oracle.sql.BLOB Signature = (oracle.sql.BLOB)update.getBLOB("Signature");
	               //System.out.println(Signature);
	          ///     ObjConnBean.PutAtBlob(Signature,SignatureBody);
	               //sqlServer 的处理
	      //         String sql = "update HTMLSignature set SignatureID=?  Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'";
	      //          PreparedStatement pstmt = null;	
	      //          pstmt = ObjConnBean.Conn.prepareStatement(sql);
	         ///oracle       pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(SignatureBody
				//				          )), SignatureBody.length);
			//	 pstmt.setBinaryStream(1,new ByteArrayInputStream(SignatureBody
			//					          ), SignatureBody.length);
	        //       pstmt.executeUpdate();
	        //       System.out.println( sql);	
	        //     }
	        //     catch (Exception e) {
	        //        System.out.println(e.toString());
	        //     }
	        //   }
	       //    rs.close();
	        //   stmt.close();
	        //   ObjConnBean.Conn.commit();
	          //保存HTMLDocument
	             ObjConnBean.Conn.setAutoCommit(true) ;
	           stmt = ObjConnBean.Conn.createStatement();
	         ResultSet  rs = stmt.executeQuery("select * from HTMLDocument Where  DocumentID='"+mDocumentIDs[i]+"'" + " ");
	           
	           if (rs.next()){
	             
	           }else{
	           String strsql = "insert into HTMLDocument (DocumentID,XYBH,BMJH,JF,YF,HZNR,QLZR,CPMC,DGSL,DGRQ) values (?,?,?,?,?,?,?,?,?,?)";
	            prestmt =ObjConnBean.Conn.prepareStatement(strsql);
	           prestmt.setString(1, mDocumentIDs[i]);
	           prestmt.setString(2, "500");
	           prestmt.setString(3, "5");
	           prestmt.setString(4, "5");
	           prestmt.setString(5, "5");
	           prestmt.setString(6, "5");
	           prestmt.setString(7, "5");
	           prestmt.setString(8, "5");
	           prestmt.setString(9, "5");
	            prestmt.setString(10,"5");
	           ObjConnBean.Conn.setAutoCommit(false) ;
	           prestmt.execute();
	           ObjConnBean.Conn.commit();
	          
	           }
	          // stmt.close();
	          // ObjConnBean.Conn.commit();
	          // SignatureBody=null;
	        }
         	if(rs2!=null)
         		rs2.close();
            SignatureBody=null;
          //  prestmt2.close();
            if(prestmt!=null)
	            prestmt.close();
            if(stmt!=null)
	            stmt.close();
              ObjConnBean.CloseConnection();
         }
         catch (SQLException e) {
           System.out.println(e.toString());
         }
       }
     }
     finally {
       out.clear();
     out.print("SIGNATUREID="+mSignatureID+"\r\n");
     out.print("RESULT=OK");
     
     }
     
   
  }
		if(mCommand.equalsIgnoreCase("SAVESIGNATUREASGIF")){   //获取当前签章图片
		mDocumentID=request.getParameter("DOCUMENTID");
		mSignatureID=request.getParameter("SIGNATUREID");
		   //测试保存图片
		   String GIFPATH=new    String(request.getParameter("GIFPATH").getBytes("8859_1"));//图片路径，可传空
      tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+GIFPATH+".jpg");
      if (tempFile.exists()) {                 
                tempFile.delete();  
            }     
            tempFile.createNewFile(); 
      // File.createTempFile(GIFPATH, ".jpg",
       //                 new File(System.getProperty("java.io.tmpdir")));
	
	
   //  String UserName=new    String(request.getParameter("USERNAME").getBytes("8859_1"));//用户名称
	//String GIFPATH=new    String(request.getParameter("GIFPATH").getBytes("8859_1"));//图片路径，可传空
	//String ClipType=new    String(request.getParameter("CLIPTYPE").getBytes("8859_1"));//切分方式
	//String ClipCount=new String(request.getParameter("CLIPCOUNT").getBytes("8859_1"));//切分数量
	// GIFIMAGE=request.getParameter("GIFIMAGE");//图片数据，标准BASE64编码
	 if(request.getParameter("GIFIMAGE")!=null){
		    base64 = new Base64();
			String gifBuffer=new String(request.getParameter("GIFIMAGE").getBytes("8859_1"));
			try {				
					 java.io.FileOutputStream fout = new java.io.FileOutputStream(tempFile);  
					char[] buffer = (gifBuffer.toCharArray());
					fout.write(base64.decode(buffer));  //保存签章图片
					fout.close();	
			}
			catch (Exception e) {
					e.printStackTrace();
				}			
	 }else if(request.getParameter("GIFIMAGE")==null&&request.getParameter("GIFIMAGE0")!=null){//按照金格文档切割n份后的图片会是GIFIMAGE0到GIFIMAGEn
		 base64 = new Base64();
			String gifBuffer=new String(request.getParameter("GIFIMAGE0").getBytes("8859_1"));
			try {				
					 java.io.FileOutputStream fout = new java.io.FileOutputStream(tempFile);  
					char[] buffer = (gifBuffer.toCharArray());
					fout.write(base64.decode(buffer));  //保存签章图片
					fout.close();	
			}
			catch (Exception e) {
					e.printStackTrace();
				}	
		}
	 //System.out.println("GIFIMAGE:"+GIFIMAGE);
	//  System.out.println("GIFIMAGE0:"+request.getParameter("GIFIMAGE0"));
	//String GIFIMAGE0=request.getParameter("GIFIMAGE0");//图片数据，标准BASE64编码
	//String GIFIMAGE1=request.getParameter("GIFIMAGE1");//图片数据，标准BASE64编码
	//String GIFIMAGEn=request.getParameter("GIFIMAGEn");//图片数据，标准BASE64编码
	//String EXTPARAM=new    String(request.getParameter("EXTPARAM").getBytes("8859_1"));//扩展参数
	//String EXTPARAM1=new    String(request.getParameter("EXTPARAM1").getBytes("8859_1"));//扩展参数1
	}
	if(mCommand.equalsIgnoreCase("GETNOWTIME")){         //获取服务器时间
		java.sql.Date mDate;
		Calendar cal  = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime=formatter.format(cal.getTime());
		out.clear();
		out.print("NOWTIME="+mDateTime+"\r\n");
		out.print("RESULT=OK");
	}
	if(mCommand.equalsIgnoreCase("DELESIGNATURE")){   //删除签章数据信息
		mDocumentID=request.getParameter("DOCUMENTID");
		mSignatureID=request.getParameter("SIGNATUREID");
   		if (ObjConnBean.OpenConnection()){
  			strSql="SELECT * from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'";
			ResultSet rs=null;
			rs = ObjConnBean.ExecuteQuery(strSql);
			if(rs.next()){
				try{
					strSql="DELETE from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'";
					ObjConnBean.ExecuteUpdate(strSql);
				}
				catch(Exception ex){
					out.println(ex.toString());
				}
			}
			ObjConnBean.CloseConnection();
  		}
		out.clear();
		out.print("RESULT=OK");
	}

	if(mCommand.equalsIgnoreCase("LOADSIGNATURE")){    //调入签章数据信息
		mDocumentID=request.getParameter("DOCUMENTID");
		mSignatureID=request.getParameter("SIGNATUREID");
     if (ObjConnBean.OpenConnection()){
        strSql="SELECT Signature,SignatureSize from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'";
        ResultSet rs=null;
        rs = ObjConnBean.ExecuteQuery(strSql);
        if(rs.next()){
        if(Sql_switcher.searchDbServer()==2){
           mSignatureSize = rs.getInt("SignatureSize");
          // mSignature = ObjConnBean.GetAtBlob(rs.getBlob("Signature"),mSignatureSize); 
           InputStream instream = rs.getBlob("Signature").getBinaryStream();
           BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(instream));  
		StringBuffer tStringBuffer = new StringBuffer();  
			String sTempOneLine = new String("");  
			 while ((sTempOneLine = tBufferedReader.readLine()) != null){  
			tStringBuffer.append(sTempOneLine);  
			}  
			 mSignature = tStringBuffer.toString();  
        }
           //sqlServer 读取
           if(Sql_switcher.searchDbServer()==1){
           byte[] mTmp = new byte[rs.getInt("SignatureSize")];
		InputStream instream = rs.getBinaryStream("Signature");
		//instream.read(mTmp, 0, rs.getInt("SignatureSize"));
		BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(instream));  
		StringBuffer tStringBuffer = new StringBuffer();  
			String sTempOneLine = new String("");  
			 while ((sTempOneLine = tBufferedReader.readLine()) != null){  
			tStringBuffer.append(sTempOneLine);  
			}  
			 mSignature = tStringBuffer.toString();  
		}
		
        }
        ObjConnBean.CloseConnection();
     }
     out.clear();
     out.print(mSignature+"\r\n");
     out.print("RESULT=OK");
  }

	if(mCommand.equalsIgnoreCase("SHOWSIGNATURE")){   //获取当前签章SignatureID，调出SignatureID，再自动调LOADSIGNATURE数据
		  mDocumentID=request.getParameter("DOCUMENTID");
		  //if(!"BJCA".equals(mDocumentID)){
			  mSignatures="";
	   		if (ObjConnBean.OpenConnection()){
	  			strSql="SELECT * from HTMLSignature Where DocumentID='"+mDocumentID + "'";
				ResultSet rs=null;
				rs = ObjConnBean.ExecuteQuery(strSql);
				while(rs.next()){
					mSignatures=mSignatures+rs.getString("SignatureID")+";";
				}
				ObjConnBean.CloseConnection();
	  		}
			out.clear();
			out.print("SIGNATURES="+mSignatures+"\r\n");
			out.print("RESULT=OK");
		 // }
    	
	}


	//---------------------------------------------------------------------------------------
	if(mCommand.equalsIgnoreCase("GETSIGNATUREDATA")){           //批量签章时，获取所要保护的数据
	    	String mSignatureData="";
		mDocumentID=request.getParameter("DOCUMENTID");
   		if (ObjConnBean.OpenConnection()){
  			strSql="SELECT XYBH,BMJH,JF,YF,HZNR,QLZR,CPMC,DGSL,DGRQ  from HTMLDocument Where DocumentID='"+mDocumentID + "'";
			ResultSet rs=null;
			rs = ObjConnBean.ExecuteQuery(strSql);
			if (rs.next()){
				mSignatureData=mSignatureData+"XYBH="+(rs.getString("XYBH"))+"\r\n";
				mSignatureData=mSignatureData+"BMJH="+(rs.getString("BMJH"))+"\r\n";
				mSignatureData=mSignatureData+"JF="+(rs.getString("JF"))+"\r\n";
				mSignatureData=mSignatureData+"YF="+(rs.getString("YF"))+"\r\n";
				mSignatureData=mSignatureData+"HZNR="+(rs.getString("HZNR"))+"\r\n";
				mSignatureData=mSignatureData+"QLZR="+(rs.getString("QLZR"))+"\r\n";
				mSignatureData=mSignatureData+"CPMC="+(rs.getString("CPMC"))+"\r\n";
				mSignatureData=mSignatureData+"DGSL="+(rs.getString("DGSL"))+"\r\n";
				mSignatureData=mSignatureData+"DGRQ="+(rs.getString("DGRQ"))+"\r\n";
			}
			mSignatureData=java.net.URLEncoder.encode(mSignatureData);
			ObjConnBean.CloseConnection();
  		}
		out.clear();
		out.print("SIGNATUREDATA="+mSignatureData+"\r\n");
		out.print("RESULT=OK");
	}

	if(mCommand.equalsIgnoreCase("PUTSIGNATUREDATA")){            //批量签章时，写入签章数据
		mDocumentID=new String(request.getParameter("DOCUMENTID").getBytes("8859_1"));
		mSignature=new String(request.getParameter("SIGNATURE").getBytes("8859_1"));
		SignatureBody = mSignature.getBytes();
        mSignatureSize = SignatureBody.length;
   		if (ObjConnBean.OpenConnection()){
      			java.sql.PreparedStatement prestmt=null;
      			try{
				//取得唯一值(mSignature)
    				java.util.Date dt=new java.util.Date();
    				long lg=dt.getTime();
    				Long ld=new Long(lg);
    				mSignatureID=ld.toString();
        			String Sql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,Signature,username) values (?,?,?,EMPTY_BLOB(),'"+userView.getUserName()+"') ";
		    	    prestmt =ObjConnBean.Conn.prepareStatement(Sql);
        			prestmt.setString(1, mDocumentID);
			        prestmt.setString(2, mSignatureID);
    	    		prestmt.setInt(3,mSignatureSize);

			        ObjConnBean.Conn.setAutoCommit(true);
        			prestmt.execute();
		    	    ObjConnBean.Conn.commit();
        			prestmt.close();
					Statement stmt=null;
					   ObjConnBean.Conn.setAutoCommit(false) ;
					   stmt = ObjConnBean.Conn.createStatement();
					   ResultSet update=(ResultSet)stmt.executeQuery("select Signature from HTMLSignature Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'" + " for update");
					   if (update.next()){
						 try
						 {
						///   ObjConnBean.PutAtBlob(((oracle.jdbc.OracleResultSet)update).getBLOB("Signature"),SignatureBody);
						 //sqlServer 的处理
			               String sql = "update HTMLSignature set SignatureID=?  Where SignatureID='"+mSignatureID+"' and DocumentID='"+mDocumentID+"'";
			              PreparedStatement pstmt = null;	
			                pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(SignatureBody
										          )), SignatureBody.length);
			              pstmt.executeUpdate();	
						 }
						 catch (Exception e) {
							System.out.println(e.toString());
						 }
					   }
					   update.close();
					   stmt.close();
					   ObjConnBean.Conn.commit();
					   SignatureBody=null;
			        mResult=true;
    	  		}
      			catch(SQLException e){
       		 		System.out.println(e.toString());
        			mResult=false;
      			}
  		ObjConnBean.CloseConnection();
  		}
		out.clear();
		out.print("SIGNATUREID="+mSignatureID+"\r\n");
		out.print("RESULT=OK");
	}

	//---------------------------------------------------------------------------------------


	if(mCommand.equalsIgnoreCase("SIGNATUREKEY")){
		mUserName=new String(request.getParameter("USERNAME").getBytes("8859_1")); 
		String RealPath ="iSignatureHTML\\"+mUserName+"\\"+mUserName+".key";
		KeyName=application.getRealPath(RealPath);

		ObjFile=new java.io.File(KeyName);         //创建文件对象 
		ChrBuffer=new char[10];
		try{
			if(ObjFile.exists()){//文件存在 
				InputStreamReader isr=new InputStreamReader(new FileInputStream(KeyName));
				//ObjFileReader = new java.io.FileReader(ObjFile); 		//创建读文件对象 
				//ObjFileReader.skip(1);
				//ObjFileReader.read(ChrBuffer, 0, 1);
				//System.out.println(ChrBuffer);
				while((intLength=isr.read(ChrBuffer))!=-1){    //读文件内容 
					out.write(ChrBuffer,0,intLength);         
				} 
				out.write("\r\n");
				out.write("RESULT=OK");
				isr.close(); //关闭读文件对象 
			} 
			else{
				out.println("File Not Found"+KeyName); //文件不存在 
			} 
		}
		catch(Exception e){
                        
			System.out.println(e.toString());
		}		
	}



	if(mCommand.equalsIgnoreCase("SAVEHISTORY")){    //保存签章历史信息
		mSignatureName=new String(request.getParameter("SIGNATURENAME").getBytes("8859_1"));//印章名称
		mSignatureUnit=new String(request.getParameter("SIGNATUREUNIT").getBytes("8859_1"));//印章单位
		mSignatureUser=new String(request.getParameter("SIGNATUREUSER").getBytes("8859_1"));//印章用户名
		mSignatureSN=new String(request.getParameter("SIGNATURESN").getBytes("8859_1"));//印章序列号
		mSignatureGUID=new String(request.getParameter("SIGNATUREGUID").getBytes("8859_1"));//全球唯一标识
		mDocumentID=new String(request.getParameter("DOCUMENTID").getBytes("8859_1"));//页面ID
		mSignatureID=new String(request.getParameter("SIGNATUREID").getBytes("8859_1"));//签章序列号
		mMACHIP=new String(request.getParameter("MACHIP").getBytes("8859_1"));//签章机器IP
		OPType=new String(request.getParameter("LOGTYPE").getBytes("8859_1"));//日志标志
    mKeySn=new String(request.getParameter("KEYSN").getBytes("8859_1"));//KEY序列号
    if (ObjConnBean.OpenConnection()){
      java.sql.PreparedStatement prestmt=null;
      try{
				java.sql.Date mDate;
				Calendar cal  = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String mDateTime=formatter.format(cal.getTime());

        strSql="insert into HTMLHistory(SignatureName,SignatureUnit,SignatureUser,SignatureSN,";
		strSql=strSql+"SignatureGUID,DocumentID,SignatureID,IP,LogType,KeySN)";
        strSql=strSql+" values(?,?,?,?,?,?,?,?,?,?)";
        prestmt =ObjConnBean.Conn.prepareStatement(strSql);

        prestmt.setString(1, mSignatureName);
        prestmt.setString(2, mSignatureUnit);
        prestmt.setString(3, mSignatureUser);
        prestmt.setString(4, mSignatureSN);
        prestmt.setString(5, mSignatureGUID);
        prestmt.setString(6, mDocumentID);
        prestmt.setString(7, mSignatureID);
        prestmt.setString(8, mMACHIP);
        //prestmt.setString(9,mDateTime);
        prestmt.setString(9,OPType);
        prestmt.setString(10,mKeySn);
        ObjConnBean.Conn.setAutoCommit(true);
        prestmt.execute();
        ObjConnBean.Conn.commit();
        prestmt.close();
        mResult=true;
      }
      catch(SQLException e){
        System.out.println(e.toString());
        mResult=false;
      }
  		ObjConnBean.CloseConnection();
    }
		out.clear();
		out.print("SIGNATUREID="+mSignatureID+"\r\n");
		out.print("RESULT=OK");
	}
%>