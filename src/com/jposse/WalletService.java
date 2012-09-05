/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jposse;

import com.google.bitcoin.core.AbstractWalletEventListener;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.discovery.IrcDiscovery;
import com.google.bitcoin.discovery.PeerDiscovery;
import com.google.bitcoin.store.DiskBlockStore;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jposse
 */
public class WalletService {
    public static File walletFile = null;
    public static Wallet wallet;
    public static ECKey eckey;
    public static NetworkParameters params = null;
    public static File blockFile;
    public static DiskBlockStore blockStore;
    public static BlockChain blockChain;
    public static PeerDiscovery discovery;
    public static PeerGroup peerGroup;
    public static final org.slf4j.Logger log = LoggerFactory.getLogger("com.jposse.WalletService");
    
    public WalletService(int productionNet, String discoveryType, String blockFileName, String walletFileName) {
        
        String ircName;
        if (productionNet==1) {
            log.info("Starting Production Network");
            params = NetworkParameters.prodNet();
            ircName = "#bitcoin";
        } else {
            log.info("Starting Test Network");
            params = NetworkParameters.testNet();
            ircName = "#bitcoinTest";
        }
        
        if (discoveryType.equalsIgnoreCase("irc")) {
            discovery = new IrcDiscovery(ircName);
        } else if (discoveryType.equalsIgnoreCase("dns")) {
            discovery = new DnsDiscovery(params);
        }
        
        //Load wallet or create new. If wallet is new, create eckey and save
        try {
            walletFile = new File(walletFileName);
            wallet = Wallet.loadFromFile(walletFile);
            eckey = wallet.keychain.get(0);
        } catch (Exception e) {
            wallet = new Wallet(params);
            eckey = new ECKey();
            wallet.keychain.add(eckey);
            try {
                wallet.saveToFile(walletFile);
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
        }
        
        //Setup blockchain to disk
        try {
            blockFile = new File(blockFileName);
            blockStore = new DiskBlockStore(params, blockFile);
            blockChain = new BlockChain(params, wallet, blockStore);
        } catch (Exception e) {
            log.info("Clearing transactions from wallet becuase no blockchain found?");
            wallet.clearTransactions(0);
            log.error(e.getMessage());
        }
        
        //Setup peer group
        log.info("Starting peers");
        peerGroup = new PeerGroup(params, blockChain);
        peerGroup.setUserAgent("PosseWallet", "0.1");
        if (productionNet==0) {
            log.info("Adding hardcoded testnet sites");
            try {
                peerGroup.addAddress(new PeerAddress(InetAddress.getByName("blockexplorer.com"), 18333));
                //peerGroup.addAddress(new PeerAddress(InetAddress.getByName("localhost"), 18333));
            } catch (UnknownHostException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.info("On production net, so adding auto-discovery");
            peerGroup.addPeerDiscovery(discovery);
        }
        
        peerGroup.addWallet(wallet);
        peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
          
        wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                super.onCoinsReceived(wallet, tx, prevBalance, newBalance);
                System.out.println("TX RECVD: Confidence: "+tx.getConfidence().toString());
                        
            }
            
            @Override
            public void onChange() {
                try {
                    //TODO: Run custom code here. 
                    wallet.saveToFile(walletFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }    
        });
    }
   
    static ArrayList<String> listTransactions() {
        ArrayList<String> retlst = new ArrayList<String>();
        for (Transaction t: wallet.getTransactionsByTime()) {
            retlst.add("Transaction: "+t.getHashAsString()+" Confidence: "+t.getConfidence());
        }
        return retlst;
    }
    
    public void startDiscovery() {
        log.info("Starting Discovery");
        peerGroup.start();
    }
    
    public void startDownload() {
        log.info("Downloading chain");
        peerGroup.downloadBlockChain();
    }
    
    static void saveWallet() {
        log.info("Saving Wallet");
        try {
            wallet.saveToFile(walletFile);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    
    static BigInteger getBalance() {
        return wallet.getBalance();
    }
    
    static ArrayList<String> listAddresses() {
        log.debug("Listing all Addresses");
        ArrayList<String> retval = new ArrayList<String>();
        for (ECKey key: wallet.getKeys()) {
            retval.add(key.toAddress(params).toString());
        }
        return retval;
    }
    
    static String getAddress() { 
        log.debug("Generating new Address");
        if (params==null) {
            return "";
        }
        ECKey newkey = new ECKey();
        wallet.keychain.add(newkey);
        saveWallet();
        return newkey.toAddress(params).toString();
    }
    void gracefulShutdown() {
        try {
            System.out.println("Graceful Shutdown Called");
            if (Main.dloading) {                
                log.info("Graceful Shutdown Called - skipped stopping peerGroup");
                wallet.saveToFile(walletFile);
            } else {
                log.info("Graceful Shutdown Called - stopping peerGroup");
                peerGroup.stop();
                System.out.println("wha1");
                wallet.saveToFile(walletFile);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
