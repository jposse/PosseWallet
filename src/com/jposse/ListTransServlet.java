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
public class ListTransServlet extends HttpServlet {
    
    public static final org.slf4j.Logger log = LoggerFactory.getLogger("com.jposse.GetAddressServlet");
        
        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
            log.info("Jetty called getAddress");
            response.setContentType("application/json");
            String json = new Gson().toJson(WalletService.listTransactions());
            response.getWriter().write(json);
        }
}