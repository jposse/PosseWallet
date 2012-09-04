/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jposse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;


/**
 *
 * @author jposse
 */
class JsonReq {
    String[] params;
    String id;
    String method;
    String jsonrpc;
}


class JsonResp {
    String id;
    Object result;
    JsonErr error;
    String jsonrpc;
}

class JsonErr {
    int code; 
    String message;
}

public class RpcServlet extends HttpServlet {

   public static final org.slf4j.Logger log = LoggerFactory.getLogger("com.jposse.RpcServlet");
        
        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("application/json");
            Gson gson = new GsonBuilder().serializeNulls().create();
            StringBuilder sb = new StringBuilder();
            String retval;
            while ((retval = request.getReader().readLine())!=null) {
                sb.append(retval);
            }
            log.debug("HttpServletRequest is {}", sb.toString());
            JsonReq jreq = gson.fromJson(sb.toString(), JsonReq.class);
            JsonResp jresp = new JsonResp();
            if (jreq.method.equalsIgnoreCase("getaddress")) {
                jresp.result = WalletService.getAddress();
                jresp.id = jreq.id;
                jresp.error=null;
                jresp.jsonrpc = jreq.jsonrpc;
            } else if (jreq.method.equalsIgnoreCase("listaddresses")) {
                jresp.result = WalletService.listAddresses();
                jresp.id = jreq.id;
                jresp.jsonrpc = jreq.jsonrpc;
            } else if (jreq.method.equalsIgnoreCase("getbalance")) {
                jresp.result = WalletService.getBalance();
                jresp.id = jreq.id;
                jresp.jsonrpc = jreq.jsonrpc;
            } else {
                log.error("Unknown method called: {}", jreq.method);
                jresp.id = jreq.id;
                jresp.result = "";
                JsonErr jerr = new JsonErr();
                jerr.code=1;
                jerr.message = "Unknown Method: "+jreq.method;
                jresp.error = jerr;
                jresp.jsonrpc = jreq.jsonrpc;     
            }
            log.info("{} returning via HttpServletResponse: {}", jreq.method, gson.toJson(jresp));
            response.getWriter().write(gson.toJson(jresp));
        }
}