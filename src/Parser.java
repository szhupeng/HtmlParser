import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Document doc = Jsoup.connect("http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201703/t20170310_1471429.html")
					.get();
			Elements elements = doc.getElementsByClass("MsoNormal");
			System.out.println("总共："+elements.size());
			org.jdom2.Document document=new org.jdom2.Document();
			org.jdom2.Element root=new org.jdom2.Element("root");
			org.jdom2.Element province = null;
			org.jdom2.Element city = null;
			Element last = null;
			for (Element e : elements) {
				Elements child = e.children();
				if("b".equals(child.get(1).tagName())){
					province =new org.jdom2.Element("province");
					String name = child.get(1).text().replace("　", "");
					String code = child.get(0).text().substring(0,6);
					province.setAttribute("name", name);
					province.setAttribute("code", code);
					root.addContent(province);
				} else {
					String name = child.get(2).text().replace("　", "");
					String code = child.get(1).text().substring(0,6);
					if(null==last){
						last = e;
						city = new org.jdom2.Element("city");
						city.setAttribute("name", name);
						city.setAttribute("code", code);
						province.addContent(city);
					}else if(last.children().get(1).text().substring(0,4).equals(code.substring(0, 4))){
						org.jdom2.Element district =new org.jdom2.Element("district");
						district.setAttribute("name", name);
						district.setAttribute("code", code);
						city.addContent(district);
						province.addContent(city.detach());
					} else {
						city = new org.jdom2.Element("city");
						city.setAttribute("name", name);
						city.setAttribute("code", code);
						province.addContent(city);
						
						last = e;
					}
				}
			}
			
			document.setRootElement(root);
	        XMLOutputter outputter=new XMLOutputter();
	        outputter.setFormat(Format.getPrettyFormat());//设置文本格式
	        outputter.output(document, new FileOutputStream(new File("city_data.xml")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
