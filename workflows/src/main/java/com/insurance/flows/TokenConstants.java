package com.insurance.flows;

import net.corda.core.identity.CordaX500Name;

public interface TokenConstants {
    CordaX500Name NOTARY = CordaX500Name.parse("O=Notary,L=London,C=GB");
    CordaX500Name TECHDOMAIN = CordaX500Name.parse("O=Tech Domain,L=Nairobi,C=KE");
    CordaX500Name INSURANCE = CordaX500Name.parse("O=Insurance,L=Nairobi,C=KE");
    CordaX500Name GARAGE = CordaX500Name.parse("O=Garage,L=Nairobi,C=KE");
}
