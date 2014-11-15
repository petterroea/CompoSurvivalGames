package com.petterroea.survival.proper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

public class WebServer {
	private HttpServer server;
	public static SurvivalGamesMain main;
	public WebServer(SurvivalGamesMain main) {
		this.main = main;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/json", new JsonHandler());
			server.createContext("/", new DefaultHandler());
			server.setExecutor(null);
			
			System.out.println("Successfully started http server on port 8000");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void start() {
		server.start();
	}
	public void stop() {
		server.stop(1);
	}
	static class DefaultHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            
            Headers h = t.getResponseHeaders();
            h.add("Access-Control-Allow-Origin", "*");
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	static class JsonHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = main.getScoreArray().toString();
            
            Headers h = t.getResponseHeaders();
            h.add("Access-Control-Allow-Origin", "*");
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
