package com.insurance.flows;

import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;

@StartableByRPC
@StartableByService
@InitiatingFlow
public class CreateNewAccount extends FlowLogic<String>{

    private String acctName;

    public CreateNewAccount(String acctName) {
        this.acctName = acctName;
    }


    @Override
    public String call() throws FlowException {
        StateAndRef<AccountInfo> newAccount = null;
        try {
            newAccount = getServiceHub().cordaService(KeyManagementBackedAccountService.class).createAccount(acctName).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AccountInfo acct = newAccount.getState().getData();
        return "" + acct.getName() + " vehicle account was created. UUID is : " + acct.getIdentifier();
    }
}
