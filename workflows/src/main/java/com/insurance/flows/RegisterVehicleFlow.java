package com.insurance.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.insurance.contracts.InsuranceContract;
import com.insurance.states.InsuranceToken;
import com.insurance.states.InsuranceTokenState;
import com.sun.javafx.collections.ImmutableObservableList;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.IDN;
import java.util.Optional;

@InitiatingFlow
@StartableByRPC
public class RegisterVehicleFlow extends FlowLogic<SignedTransaction> {

    private final ProgressTracker progressTracker = new ProgressTracker();

    @NotNull
    private final String Name;
    @NotNull
    private final String IDNo;
    @NotNull
    private final String dob;
    private final int amount;
    private Party insurance;


    public RegisterVehicleFlow(@NotNull String Name, @NotNull String IDNo, @NotNull String dob, int amount, Party insurance) {
        this.Name = Name;
        this.IDNo = IDNo;
        this.dob = dob;
        this.amount = amount;
        this.insurance = insurance;
    }

    @Nullable
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }


    public String getName() {
        return Name;
    }

    @NotNull
    public String getIDNo() {
        return IDNo;
    }

    @NotNull
    public String getDob() {
        return dob;
    }


    public int getAmount() {
        return amount;
    }

    public Party getInsurance() {
        return insurance;
    }

    public void setInsurance(Party issuer) {
        this.insurance = insurance;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {

        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Stage 1.
        //  progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        // Generate an unsigned transaction.

        Optional<Party> admin = getServiceHub().getIdentityService().partiesFromName("Tech Domain", true).stream().findFirst();
        Party me = getOurIdentity();

        InsuranceToken activityState = new InsuranceToken(Name,IDNo,dob,amount, insurance);

        final Command<InsuranceContract.Commands.Issue> txCommand = new Command(
                new InsuranceContract.Commands.Issue(), me.getOwningKey());

        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(activityState, InsuranceContract.ID)
                .addCommand(txCommand);

        // Stage 2.

        // Verify that the transaction is valid.
        txBuilder.verify(getServiceHub());

        // Stage 3.

        // Sign the transaction.
        final SignedTransaction fullySignedTx = getServiceHub().signInitialTransaction(txBuilder);


        final SignedTransaction finalTx= subFlow(new FinalityFlow(fullySignedTx));
        subFlow(new ReportToInsuranceFlow.SendToAdminFlow(admin.get(), finalTx));

        return finalTx;
    }
}
