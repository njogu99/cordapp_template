package com.insurance.flows;

import co.paralleluniverse.fibers.Suspendable;
import kotlin.Unit;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.SignedTransaction;

public class ReportToInsuranceFlow {
    @InitiatingFlow
    public static class SendToAdminFlow extends FlowLogic<Unit> {

        private final Party insurance;
        private final SignedTransaction finalTx;

        public SendToAdminFlow(Party insurance, SignedTransaction finalTx) {

            this.insurance = insurance;
            this.finalTx = finalTx;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {

            FlowSession adminPartySession = initiateFlow(insurance);
            //val session = initiateFlow(regulator)
            subFlow(new SendTransactionFlow(adminPartySession, finalTx));
            return null;
        }

    }

    @InitiatedBy(SendToAdminFlow.class)
    public static class RecieveAdminFlow extends FlowLogic<Unit> {

        private final FlowSession otherPartySession;

        public RecieveAdminFlow(FlowSession otherPartySession) {
            this.otherPartySession = otherPartySession;
        }

        @Override
        @Suspendable
        public Unit call() throws FlowException {

            subFlow(new ReceiveTransactionFlow(otherPartySession, true, StatesToRecord.ALL_VISIBLE));
            return null;
        }

    }
}
