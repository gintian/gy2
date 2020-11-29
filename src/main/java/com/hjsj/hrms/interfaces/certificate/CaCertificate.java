package com.hjsj.hrms.interfaces.certificate;

import com.jit.attr.jitCertVerify;

import java.security.cert.X509Certificate;
import java.util.StringTokenizer;

/**
 * @author chenmengqing
 *
 */
public class CaCertificate {
	  /**认证方式
	   * ＝１北京公安吉大正元
	   * */
	  private String ca_type="1";

	  public CaCertificate(String ca_type)
	  {
	    this.ca_type=ca_type;
	  }

	  /**beijing公安吉大正元ｃａ认证*/
	  public String jitBjga_CaCertificate(Object certificate)throws Exception
	  {
	    X509Certificate[]  certs=(X509Certificate[])certificate;//request.getAttribute("javax.servlet.request.X509Certificate");
	    X509Certificate gaX509Cert=null;
	    gaX509Cert=certs[0];
	    jitCertVerify certVerify = new jitCertVerify();
	    certVerify.setBaseDN("c=cn");
	    String sub=null;
	    String identitystr=null;
	    try
	    {
	      /**PKI的LDAP服务器IP及端口号*/
	      certVerify.setParameter("10.8.1.160,10.1.1.103","390,389");
	      /**证书crL及证书链验证*/
	      
	      certVerify.verify(gaX509Cert,true,true);
	      sub = gaX509Cert.getSubjectDN().toString();
	      StringTokenizer stok=new StringTokenizer(sub,",");
	      if(stok.hasMoreTokens())
	       identitystr=stok.nextToken();
	      StringTokenizer stokn=new StringTokenizer(identitystr," ");
	      if(stokn.hasMoreTokens())
	      {
	          stokn.nextToken();
	          identitystr=stokn.nextToken();
	      }
	    }
	    catch(Exception e)
	    {
	       throw e;
	    }
	    return identitystr;
	  }

}
