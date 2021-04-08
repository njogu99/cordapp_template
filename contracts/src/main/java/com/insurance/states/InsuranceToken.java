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

    ;
    @NotNull
    private final String RegNo;
    @NotNull
    private final String make;
    @NotNull
    private final String model;
    private final int mileage;
    private final int price;
    private Party ownedBy;
    private Party insurance;



    public InsuranceToken(@NotNull String regNo, @NotNull String make, @NotNull String model, int mileage, int price, Party ownedBy, Party insurance) {
        Validate.notNull(insurance, "Insurance cannot be empty.");
        Validate.notBlank(regNo, "Registration Number cannot be empty.");
        Validate.notBlank(make, "Make cannot be empty.");
        Validate.notBlank(model, "Model cannot be empty.");
        Validate.isTrue(mileage >=0 , "Mileage cannot be negative.");
        Validate.isTrue(price > 0, "Price cannot be 0.");
        this.RegNo = regNo;
        this.make = make;
        this.model = model;
        this.mileage = mileage;
        this.price = price;
        this.ownedBy = ownedBy;
        this.insurance = insurance;
    }








    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(insurance);
    }

    @NotNull
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

    public Party getInsurance() {
        return insurance;
    }

    public Party getOwnedBy() {
        return ownedBy;
    }

    public void setInsurance(Party issuer) {
        this.insurance = insurance;
    }
    public void setOwnedBy(Party ownedBy) {
        this.ownedBy = ownedBy;
    }

}
