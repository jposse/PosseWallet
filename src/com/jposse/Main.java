/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jposse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jposse
 */
public class Main {
    
    public static final Logger log = LoggerFactory.getLogger("com.jposse.Main");
    public static WalletService ws = null;
    public static JettyWs jws = null;
    public static boolean dloading = false;
    
    
    public static void main(String[] args) {
        log.info("Starting PosseWallet");
        
        log.info("Starting Jetty");
        try {
            //Start Jetty
            jws = new JettyWs(8333);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), Main.class.getName());
        }
        jws.start();
        log.info("Jetty Started");
        
        
        log.info("Starting WalletService");
        //WalletService(prodNetwork, DiscoveryType, BlockChainFile, WalletFile)
        ws = new WalletService(1, "dns", "posse.blockchain", "posse.wallet");
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            //Can't graceful shutdown during download...so either kill quick or soft depending on state
            @Override
            public void run() {
                    jws.stopJetty();
                    ws.gracefulShutdown();
            }
        });
        ws.startDiscovery();
        log.info("Discovery Started");
        dloading = true;
        ws.startDownload();
        dloading = false;
        log.info("Finished downloading blockchain.");
    }
}
