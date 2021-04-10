package com.insurance.states;

import com.insurance.contracts.InsuranceContract;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(InsuranceContract.class)
public class InsuranceToken implements ContractState {


    @NotNull
    private final String Name;
    @NotNull
    private final String IDNo;
    @NotNull
    private final String dob;
    private final int amount;
    private Party insurance;



    public InsuranceToken(@NotNull String Name, @NotNull String IDNo, @NotNull String dob, int amount, Party insurance) {
        Validate.notNull(insurance, "Insurance cannot be empty.");
        Validate.notBlank(Name, "Registration Number cannot be empty.");
        Validate.notBlank(IDNo, "Make cannot be empty.");
        Validate.notBlank(dob, "Model cannot be empty.");
        Validate.isTrue(amount >=0 , "Mileage cannot be negative.");
        Validate.isTrue(amount > 0, "Price cannot be 0.");
        this.Name = Name;
        this.IDNo = IDNo;
        this.dob = dob;
        this.amount = amount;
        this.insurance = insurance;
    }








    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(insurance);
    }

    @NotNull
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

}
