package com.insurance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
@StartableByService
public class IssueTokenFlow extends FlowLogic<SignedTransaction> {

    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String tokenName;
    private final Long quantity;
    private final Party recipient;
//    private final List<Party> observers;

    public IssueTokenFlow(String tokenName, Long quantity, Party recipient /*, List<Party> observers */) {
        this.tokenName = tokenName;
        this.quantity = quantity;
        this.recipient = recipient;
//        this.observers = observers;
    }


    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        //get the fixed token type
        TokenType token = new TokenType(tokenName,0);


        //assign the issuer who will be issuing the tokens
        IssuedTokenType issuedTokenType = new IssuedTokenType(getOurIdentity(), token);

        //specify how much amount to issue to holder
        Amount<IssuedTokenType> amount = new Amount(quantity, issuedTokenType);

        //create fungible amount specifying the new owner
        FungibleToken fungibleToken  = new FungibleToken(amount, recipient, TransactionUtilitiesKt.getAttachmentIdForGenericParam(token));

        //use built in flow for issuing tokens on ledger
        return subFlow(new IssueTokens(Arrays.asList(fungibleToken)/*, observers */));
    }



}


