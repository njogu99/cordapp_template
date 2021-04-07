package com.insurance.contracts;

import com.insurance.states.InsuranceToken;
import com.insurance.states.InsuranceTokenState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class InsuranceContract implements Contract {
    public static final String ID = "com.insurance.contracts.InsuranceContract";
    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        List<InsuranceToken> inpStates=tx.inputsOfType(InsuranceToken.class);
        requireThat(require ->{
            require.using("There is must be zero input State" , inpStates.size()<1);
            require.using("There is must be one output State of type Insurance Token State" , tx.getOutputs().get(0).getData() instanceof InsuranceToken);
            return require;
        });


    }
    public interface Commands extends CommandData {
        class Issue implements Commands {
        }
    }
}
