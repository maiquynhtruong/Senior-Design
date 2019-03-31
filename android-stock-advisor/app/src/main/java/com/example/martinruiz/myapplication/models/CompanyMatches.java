package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CompanyMatches implements Serializable {
    @SerializedName("bestMatches") List<Company> companyList;

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public Company getBestMatchedCompany() {
        if (companyList == null || companyList.isEmpty()) return null;
        int bestIndex = 0;
        float bestMatchScore = 0f;
        for (int i = 0; i < companyList.size(); i++) {
            Company company = companyList.get(i);
            float matchScore = Float.valueOf(company.getMatchScore());
            if (matchScore > bestMatchScore) {
                bestIndex = i;
                bestMatchScore = matchScore;
            }
        }
        return companyList.get(bestIndex);
    }

}
