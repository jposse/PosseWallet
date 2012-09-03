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
    
    
    public static void main(String[] args) {
        log.info("Starting PosseWallet");
        
        log.info("Starting Jetty");
        JettyWs jws = null;
        try {
            //Start Jetty
            jws = new JettyWs(8333);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), Main.class.getName());
        }
        jws.start();
        log.info("Jetty Started");
        
        //WalletService(prodNetwork, DiscoveryType, BlockChainFile, WalletFile)
        log.info("Starting WalletService");
        WalletService ws = new WalletService(1, "dns", "posse.blockchain", "posse.wallet");
        ws.startDiscovery();
        ws.startDownload();
        log.info("Finished downloading blockchain.");
    }
}
