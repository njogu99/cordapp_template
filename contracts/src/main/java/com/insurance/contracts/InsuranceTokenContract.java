package com.insurance.contracts;

import com.insurance.states.InsuranceToken;
import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class InsuranceTokenContract extends EvolvableTokenContract implements Contract {

    @Override
    public void additionalCreateChecks(@NotNull final LedgerTransaction tx) {

        final InsuranceToken outputInsuranceTokenType = tx.outputsOfType(InsuranceToken.class).get(0);
        requireThat(require -> {
            require.using("Amount cannot be negative.",
                    outputInsuranceTokenType.getAmount() >= 0);
            require.using("Amount cannot be 0.",
                    outputInsuranceTokenType.getAmount() > 0);
            return null;
        });
    }

    @Override
    public void additionalUpdateChecks(@NotNull final LedgerTransaction tx) {
        final InsuranceToken inputInsuranceTokenType = tx.inputsOfType(InsuranceToken.class).get(0);
        final InsuranceToken outputInsuranceTokenType = tx.outputsOfType(InsuranceToken.class).get(0);

        requireThat(require -> {
            require.using("Name Cannot be updated.",
                    outputInsuranceTokenType.getName().equals(inputInsuranceTokenType.getName()));
            require.using("ID Number Cannot be updated.",
                    outputInsuranceTokenType.getIDNo().equals(inputInsuranceTokenType.getIDNo()));
            require.using("Date of Birth be updated.",
                    outputInsuranceTokenType.getDob().equals(inputInsuranceTokenType.getDob()));
            require.using("Amount Cannot be 0.",
                    outputInsuranceTokenType.getAmount() > 0);

            return null;
        });

    }
}
