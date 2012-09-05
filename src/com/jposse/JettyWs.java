/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jposse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.LoggerFactory;
/**
 *
 * @author jposse
 */
public class JettyWs extends Thread {
    public static final org.slf4j.Logger log = LoggerFactory.getLogger("com.jposse.JettyWs");
    private Server server;
    
    public JettyWs(int port) {
       
        server = new Server(port);
        ServletContextHandler context;
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        log.info("Registering Servlets");
        context.addServlet(new ServletHolder(new GetAddressServlet()),"/getAddress");
        context.addServlet(new ServletHolder(new ListAddressServlet()), "/listAddresses");
        context.addServlet(new ServletHolder(new GetBalanceServlet()), "/getBalance");
        context.addServlet(new ServletHolder(new ListTransServlet()), "/listTransactions");
        context.addServlet(new ServletHolder(new RpcServlet()), "/rpc");
    }
    
    @Override
    public void run() {
        try {
            this.server.start();
            log.info("JettyWs Running...");
            this.server.join();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
    public void stopJetty() {
        try {
            log.info("Stopping JettyWs....");
            this.server.stop();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
