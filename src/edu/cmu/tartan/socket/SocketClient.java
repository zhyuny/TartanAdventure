package edu.cmu.tartan.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import edu.cmu.tartan.config.Config;
import edu.cmu.tartan.manager.IQueueHandler;
import edu.cmu.tartan.manager.ResponseMessage;
import edu.cmu.tartan.manager.SocketMessage;
import edu.cmu.tartan.xml.XmlParser;
import edu.cmu.tartan.xml.XmlParserType;
import edu.cmu.tartan.xml.XmlResponseClient;
import edu.cmu.tartan.xml.XmlResultString;

public class SocketClient implements Runnable {

	/**
	 * Game logger for game log
	 */
	protected static final Logger gameLogger = Logger.getGlobal();

	private String serverIp = "127.0.0.1";
	private int serverPort = 10015;
	private boolean isDesigner = false;

	private Socket socket = null;
	private ResponseMessage responseMessage;
	private IQueueHandler queue;

	private boolean isLoop;
	private boolean quitFromCli = false;

	public SocketClient(ResponseMessage responseMessage, IQueueHandler queue, boolean isDesigner) {
		isLoop = true;
		this.responseMessage = responseMessage;
		this.queue = queue;
		this.isDesigner = isDesigner;
	}

	@Override
	public void run() {
		connectToServer();
	}

	public boolean connectToServer() {
		serverIp = Config.getServerIp();
		if (isDesigner) {
			serverPort = Config.getDesignerPort();
		} else {
			serverPort = Config.getUserPort();
		}

		try {
			socket = new Socket(serverIp, serverPort);
			gameLogger.info("Connected to server");

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String message = "";

            while(isLoop) {

            	if ((message = reader.readLine()) == null) break;
            	//TODO Check a null state
				if (message.equals("null")) break;

				receiveMessage(message);
            }

            if (socket != null) stopSocket();

        } catch (UnknownHostException e) {

        	gameLogger.warning("Server not found : " + e.getMessage());
        	return false;

        } catch (IOException e) {

        	gameLogger.warning("IOException : " + e.getMessage());
        	return false;
        }

		return true;
	}

	public boolean waitToConnection(int timeout) {
		while (timeout > 0 && socket == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException exception) {
				gameLogger.info("Exception :" + exception.getMessage());
				Thread.currentThread().interrupt();
				break;
			}

			timeout -= 10;
		}

		return (socket != null && socket.isConnected());
	}

	public boolean receiveMessage(String message) {
		
		gameLogger.info("Received message : " + message);
		XmlParser xmlParser;
		String messageType = null;
		XmlResponseClient xr = null;

		try {
			xmlParser = new XmlParser(XmlParserType.CLIENT);
			xmlParser.parseXmlFromString(message);
			messageType = xmlParser.getMessageType();
			xr = (XmlResponseClient) xmlParser.getXmlResponse();

		} catch (ParserConfigurationException e) {
			gameLogger.warning("ParserConfigurationException : " + e.getMessage());
			return false;
		}

		gameLogger.info("Received message type : " + messageType);

    switch(messageType) {
			case("REQ_LOGIN"):
				sendByResponseMessage(xr.getResultStr(), null);
				break;
			case("ADD_USER"):
				sendByResponseMessage(xr.getResultStr(), null);
				break;
			case("REQ_GAME_START"):
				sendByResponseMessage(xr.getResultStr(), null);
				break;
			case("GAME_END"):
				if (quitFromCli) {
					sendByResponseMessage(XmlResultString.OK, xr.getGameText());
					quitFromCli = false;
				} else {
					sendByQueue(xr.getGameText());
					sendByQueue("quit");
				}
				break;
			case("UPLOAD_MAP_DESIGN"):
				sendByResponseMessage(xr.getResultStr(), null);
				break;
			case("EVENT_MESSAGE"):
				sendByQueue(xr.getEventMsg());
				break;
			default:
				break;
		}
		return true;
	}

	public boolean sendByQueue(String message) {
		boolean returnValue = false;
		returnValue = queue.produce(new SocketMessage(Thread.currentThread().getName(), message));
		return returnValue;
	}

	public boolean sendByResponseMessage(XmlResultString result, String message) {

		String returnValue = "FAIL";

		if(XmlResultString.OK == result) {
			returnValue = "SUCCESS";
		}

		try {
			synchronized (responseMessage) {
				if (message == null ) {
					responseMessage.setMessage(returnValue);
				} else {
					responseMessage.setMessage(message);
				}
				responseMessage.notify();
			}
		} catch (IllegalMonitorStateException e) {
			gameLogger.warning("IllegalMonitorStateException : " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendMessage(String message) {
		
		gameLogger.info("Send to Server : " + message);
		
		if (socket == null || !socket.isConnected()) {
			gameLogger.info("Socket is not connected to the server yet.");
			return false;
		}
		try {
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);

			writer.println(message);
			return true;
		} catch (IOException e) {
			gameLogger.warning("IOException : " + e.getMessage());
		}
		return false;
	}

	public boolean stopSocket() {

		gameLogger.info("Close a client socket");
		
		boolean returnValue = false;
		isLoop = false;
		quitFromCli = false;

		try {
			Thread.sleep(1000);
			if (socket != null) socket.close();
			socket = null;
		} catch (IOException e) {
			gameLogger.warning("IOException : " + e.getMessage());
		} catch (InterruptedException e) {
			gameLogger.warning("InterruptedException");
			Thread.currentThread().interrupt();
		}
		return returnValue;
	}
	
	public void setQuitFromCli(boolean value) {
		this.quitFromCli = value;
	}

}
