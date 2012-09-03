PosseWallet
===========

PosseWallet is a java-based lightweight bitcoin wallet designed to replicate and extend the functionality of the bitcoind daemon. 


Features
========

    * Only downloads headers, not full blockchain
    * Integrated Jetty webserver, so extending is simple
    * Reports via json (like bitcoind), but multi-threaded & non-blocking
    * Can send / receive coins
    * Can define custom triggers when events happen (ie. update database row when funds arrive). 

How to build / use?
====================

    #1. Build

         $ant jar

    #2. Use

        $cd dist
        $java -jar PosseWallet.jar & 
        $tail -f PosseWallet.log (note: first time will download blockchain headers - about 15MB)

        Open web-browser and go to: http://localhost:8333, try the following commands:
            * http://localhost:8333/getAddress  - (will generate new address, return it to you, and save in wallet)
            * http://localhost:8333/listAddresses - (will return a json list of all addresses in wallet)


Contributions / Code:
=====================

    Yes, please get involved to help code. If you want to donate please send to: 1KxMVQXDRCzRh9EdecdyjrpNgZZbaMRkYq 
    (even a contribution of just .10 BTC helps)

