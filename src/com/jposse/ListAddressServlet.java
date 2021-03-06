/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jposse;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jposse
 */
public class ListAddressServlet extends HttpServlet {
    
    public static final org.slf4j.Logger log = LoggerFactory.getLogger("com.jposse.ListAddressServlet");
        
        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
            log.info("Jetty called listAddresses");
            response.setContentType("application/json");
            String json = new Gson().toJson(WalletService.listAddresses());
            response.getWriter().write(json);
        }
}