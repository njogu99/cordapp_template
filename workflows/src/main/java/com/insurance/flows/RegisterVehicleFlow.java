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
import java.util.Optional;

@InitiatingFlow
@StartableByRPC
public class RegisterVehicleFlow extends FlowLogic<SignedTransaction> {

    private final ProgressTracker progressTracker = new ProgressTracker();

    private final Party ownedBy;
    private final Party insurance;
    private final String RegNo;
    @NotNull
    private final String make;
    @NotNull
    private final String model;
    private final int mileage;
    private final int price;


    public RegisterVehicleFlow(Party ownedBy, Party insurance, String regNo, @NotNull String make, @NotNull String model, int mileage, int price) {
        this.ownedBy = ownedBy;
        this.insurance = insurance;
        this.RegNo = regNo;
        this.make = make;
        this.model = model;
        this.mileage = mileage;
        this.price = price;
    }

    @Nullable
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public Party getOwnedBy() {
        return ownedBy;
    }

    public Party getInsurance() {
        return insurance;
    }

    public String getRegNo() {
        return RegNo;
    }

    @NotNull
    public String getMake() {
        return make;
    }

    @NotNull
    public String getModel() {
        return model;
    }

    public int getMileage() {
        return mileage;
    }

    public int getPrice() {
        return price;
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

        InsuranceToken activityState = new InsuranceToken(RegNo,make,model,mileage,price,me, insurance);

        final Command<InsuranceContract.Commands.Issue> txCommand = new Command(
                new InsuranceContract.Commands.Issue(), activityState.getOwnedBy().getOwningKey());

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
