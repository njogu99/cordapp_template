package com.insurance.states;

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InsuranceTokenState extends EvolvableTokenType {

    public  final int fractionDigits;
    @NotNull
    private final List<Party> maintainer;
    @NotNull
    private final UniqueIdentifier uniqueIdentifier;

    @Override
    public UniqueIdentifier getLinearId() {

        return uniqueIdentifier;
    }

    public InsuranceTokenState(List<Party> maintainer, UniqueIdentifier uniqueIdentifier, int fractionDigits) {

        this.maintainer = maintainer;
        this.uniqueIdentifier = uniqueIdentifier;
        this.fractionDigits = fractionDigits;
    }

    public List<Party> getMaintainers() {
        return maintainer;
    }


    @Override
    public int getFractionDigits() {
        return 0;
    }

//    @Override
//    public List<Party> getMaintainers() {
//        // TODO Auto-generated method stub
//        return maintainer;
//    }

}
