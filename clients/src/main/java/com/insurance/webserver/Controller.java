package com.insurance.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.flows.IssueTokenFlow;
import com.insurance.flows.RedeemTokenFlow;
import com.insurance.flows.RegisterVehicleFlow;
import com.insurance.flows.SpendTokenFlow;
import com.insurance.states.InsuranceToken;
import com.insurance.states.InsuranceTokenState;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);
    private final CordaX500Name me;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }


    public String toDisplayString(X500Name name){
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isMe(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }


    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/nodes", produces = TEXT_PLAIN_VALUE)
    public HashMap<String, List<String>> nodes() {
        HashMap<String, List<String>> myMap = new HashMap<>();
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el)  && !isNetworkMap(el));
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());
        myMap.put("nodes", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }
    @GetMapping(value = "/token", produces = TEXT_PLAIN_VALUE)
    private String token() {
        return proxy.vaultQuery(FungibleToken.class).getStates().toString();
    }

    @GetMapping(value = "/me",produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami(){
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }

    @PostMapping(path = "create-token", produces = "text/plain")
    private ResponseEntity<String> createToken(@RequestParam(value = "name") String Name,
                                               @RequestParam(value = "idno") String IDNo,
                                               @RequestParam(value = "dob") String DOB,
                                               @RequestParam(value = "amount") int Amount,
                                               @RequestParam(value = "insurance") String Insurance
    ) throws ExecutionException, InterruptedException {

//        Party observer = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(observerName));
        Party insurance = proxy.partiesFromName(Insurance, false).iterator().next();



        SignedTransaction result = proxy.startFlowDynamic(RegisterVehicleFlow.class, Name,IDNo,DOB,Amount,insurance).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.CREATED).body("User "+ Name +" registered successfully.\n Transaction id " + result.getId() + "added to ledger.");
    }
//    @PostMapping(path = "issue-token", produces = "text/plain")
//    private ResponseEntity<String> issueToken(@RequestParam(value = "tokenname") String tokenName,
//                                              @RequestParam(value = "quantity") Long quantity,
//                                              @RequestParam(value = "recipient") String recipientName,
//                                              @RequestParam(value = "observers") String observerName
//                                              ) throws ExecutionException, InterruptedException {
//
////        Party recipient = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(recipientName));
//        Party recipient = proxy.partiesFromName(recipientName, false).iterator().next();
////        Party observer = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(observerName));
//        Party observer = proxy.partiesFromName(observerName, false).iterator().next();
//
//        ArrayList<Party> observers = new ArrayList<Party>();
//        observers.add (observer);
//
//        SignedTransaction result = proxy.startFlowDynamic(IssueTokenFlow.class, tokenName,quantity,recipient,observers).getReturnValue().get();
//        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
//    }
    @PostMapping(path = "spend-token", produces = "text/plain")
    private ResponseEntity<String> spendToken(@RequestParam(value = "tokenname") String tokenName,
                                              @RequestParam(value = "holdername") String holderName,
                                              @RequestParam(value = "quantity") Long quantity,
                                              @RequestParam(value = "recipient") String recipientName
    ) throws ExecutionException, InterruptedException {

//        Party recipient = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(recipientName));
        Party recipient = proxy.partiesFromName(recipientName, false).iterator().next();
//        Party observer = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(observerName));
        Party holder = proxy.partiesFromName(holderName, false).iterator().next();

        SignedTransaction result = proxy.startFlowDynamic(SpendTokenFlow.class, tokenName,holder,quantity,recipient).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
    }


    @PostMapping(path = "redeem-token", produces = "text/plain")
    private ResponseEntity<String> redeemToken(@RequestParam(value = "tokenname") String tokenName,
                                              @RequestParam(value = "issuername") String issuerName,
                                              @RequestParam(value = "quantity") Long quantity
    ) throws ExecutionException, InterruptedException {

        Party issuer = proxy.partiesFromName(issuerName, false).iterator().next();

        SignedTransaction result = proxy.startFlowDynamic(RedeemTokenFlow.class, tokenName,issuer,quantity).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
    }

//    @GetMapping(value = "tokens", produces = "application/json")
//    private List<HashMap<String, String>> tokens() {
//        List<StateAndRef<InsuranceToken>> states = proxy.vaultQuery(InsuranceToken.class).getStates();
//
//        return states.stream().map(stateAndRef -> {
//            InsuranceToken token = stateAndRef.getState().getData();
//
//            HashMap<String, String> map = new HashMap<>();
//            map.put("issuer", token.getInsurance().getName().getOrganisation());
//            map.put("owner", token.getOwnedBy().getName().getOrganisation());
//            map.put("amount", String.valueOf(token.getPrice()));
//            map.put("registration",token.getRegNo());
//
//            return map;
//        }).collect(Collectors.toList());
//    }
}