package edu.cmu.tartan.xml;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlResponseClient extends XmlResponse {

	private XmlResultString resultStr;	//OK or NG
	private XmlNgReason ngReason;
	private static final String NG_REASON = "ng_reason";

	
	
	public XmlResponseClient(XmlMessageType msgType) {
		this.msgType = msgType;
	}
	
	public XmlResultString getResultStr() {
		return resultStr;
	}
	
	public XmlNgReason getNgReason() {
		return ngReason;
	}
	
	
	@Override
	public XmlParseResult doYourJob(Document doc) {
		
		if(msgType.equals(XmlMessageType.UPLOAD_MAP_DESIGN) ) {
			return parsingResultForMap(doc);
		}
		else if(msgType.equals(XmlMessageType.REQ_LOGIN)) {
			return parsingResultForLogin(doc);		
		}
		else if(msgType.equals(XmlMessageType.ADD_USER)) {
			return parsingResultForAddUser(doc);		
		}
		
		return XmlParseResult.UNKNOWN_MESSAGE; 
		
	}

	private XmlParseResult parsingResultForAddUser(Document doc) {
		NodeList nList;
		
		//<user_info add_result="OK" ng_reason="-"  />
		nList = getNodeListOfGivenTag("user_info", doc);
		parsingResultAndNgReason("add_result", NG_REASON, nList);
		
		if(resultStr == null || ngReason == null)
			return XmlParseResult.INVALID_DATA;
		else 
			return XmlParseResult.SUCCESS;
	}

	
	private XmlParseResult parsingResultForLogin(Document doc) {
		NodeList nList;
		
		//<login_info ng_reason="OK" result="OK" role="player"/>
		nList = getNodeListOfGivenTag("login_info", doc);
		parsingResultAndNgReason("result", NG_REASON, nList);

		if(resultStr == null || ngReason == null)
			return XmlParseResult.INVALID_DATA;
		else 
			return XmlParseResult.SUCCESS;		
	}

	private XmlParseResult parsingResultForMap(Document doc) {
		NodeList nList;
		
		//<game_info result="OK" ng_reason="-" />
		nList = getNodeListOfGivenTag("game_info", doc);
		parsingResultAndNgReason("result", NG_REASON, nList);
		
		if(resultStr == null || ngReason == null )
			return XmlParseResult.INVALID_DATA;
		else 
			return XmlParseResult.SUCCESS;		
	}

	
	private void parsingResultAndNgReason(String resultAttrName, String ngReasonAttrName, NodeList nList) { 
		resultStr = XmlResultString.valueOf(getAttributeValueAtNthTag(resultAttrName, nList, 0));
		ngReason = XmlNgReason.valueOf(getAttributeValueAtNthTag(ngReasonAttrName, nList, 0));
	}
}
