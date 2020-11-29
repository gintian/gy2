锘縡unction loadXMLDoc(dname) 
{
	try //Internet Explorer
	{
		xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
	}
	catch(e)
	{
		try //Firefox, Mozilla, Opera, etc.
		{
			xmlDoc=document.implementation.createDocument("","",null);
		}
		catch(e) {alert(e.message)}
	}
	try 
	{
		xmlDoc.async=false;
		xmlDoc.load(dname);
		return(xmlDoc);
	}
	catch(e) {alert(e.message)}
	return(null);
}
function loadXMLString(dname){
	try //Internet Explorer
  {
  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
  xmlDoc.async="false";
  xmlDoc.loadXML(dname);
  }
catch(e)
  {
  try //Firefox, Mozilla, Opera, etc.
    {
    parser=new DOMParser();
    xmlDoc=parser.parseFromString(dname,"text/xml");
    }
  catch(e) {alert(e.message)}
  }
  return xmlDoc;
} 