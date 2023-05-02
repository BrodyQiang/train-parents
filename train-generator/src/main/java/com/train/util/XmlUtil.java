package com.train.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/2 14:03
 */
public class XmlUtil {

    static String pomPath = "train-generator/pom.xml";

    /**
     * 获取mybatis-generator配置文件路径 generator-config-xxx.xml
     * @return train-generator/src/main/resources/generator-config-train-system-management.xml
     * @throws DocumentException
     */
    public static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

    /**
     * 获取模块名
     * @return train-system-management
     * @throws DocumentException
     */
    public static String getModule() throws DocumentException {
        String generatorPath = XmlUtil.getGeneratorPath();
        return generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
    }


}
