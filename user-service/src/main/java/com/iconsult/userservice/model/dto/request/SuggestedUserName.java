package com.iconsult.userservice.model.dto.request;

import java.util.List;

public class SuggestedUserName {
    List<String> suggestedUserNames;
    Boolean isValidName;

    public SuggestedUserName(List<String> suggestedUserNames, Boolean isValidName) {
        this.suggestedUserNames = suggestedUserNames;
        this.isValidName = isValidName;
    }

    public List<String> getSuggestedUserNames() {
        return suggestedUserNames;
    }

    public void setSuggestedUserNames(List<String> suggestedUserNames) {
        this.suggestedUserNames = suggestedUserNames;
    }

    public Boolean getIsValidName() {
        return isValidName;
    }

    public void setIsValidName(Boolean isValidName) {
        this.isValidName = isValidName;
    }
}
