package com.insurance.flows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.insurance.flows.IssueTokenFlow;
import com.insurance.states.InsuranceToken;

import net.corda.core.identity.Party;
import net.corda.core.messaging.DataFeed;
import net.corda.core.node.AppServiceHub;

import net.corda.core.node.services.CordaService;
import net.corda.core.node.services.Vault.Page;
import net.corda.core.node.services.Vault.Update;
import net.corda.core.serialization.SingletonSerializeAsToken;
import rx.Observer;

@CordaService
public class AutoIssueToken extends SingletonSerializeAsToken {
    private final AppServiceHub serviceHub;

    public AutoIssueToken(AppServiceHub serviceHub) {
        this.serviceHub = serviceHub;
        // code ran at service creation / node startup
        init();
    }

    private void init() {

        issueTokens();

    }

    private void issueTokens() {
        Party ourIdentity = serviceHub.getMyInfo().getLegalIdentities().get(0);
        Optional<Party> adminParty = serviceHub.getIdentityService().partiesFromName("Tech Domain", true).stream()
                .findFirst();

        if (ourIdentity.equals(adminParty.get())) {
            DataFeed<Page<InsuranceToken>, Update<InsuranceToken>> dataFeed = serviceHub
                    .getVaultService().trackBy(InsuranceToken.class);

            Observer<Update<InsuranceToken>> observer = new Observer<Update<InsuranceToken>>() {

                @Override
                public void onCompleted() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(Throwable e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNext(Update<InsuranceToken> t) {

                    processState(t);
                }

            };
            dataFeed.getUpdates().subscribe(observer);
        }

        // dataFeed.getUpdates().subscribe(s -> processState(s));

    }

    private void processState(Update<InsuranceToken> updates) {
        // LoyalityActivityState state;
        updates.getProduced().forEach(message -> {
            InsuranceToken state = message.getState().getData();
            ExecutorService executor = Executors.newSingleThreadExecutor();


            if (state.getAmount() == 1000) {

                int cover = (state.getAmount() * 10);
                executor.submit(() -> {

                    serviceHub
                            .startFlow(new IssueTokenFlow("HTKN",cover, state.getInsurance()));
                });
            }

        });
        // return state;
    }
}

