package com.ivanchimeno.gympuplan.lib;

/**
 * Encapsulates a typical response from the server.
 */
public class GympuResponse
{
    private String actionSent;

    private String statusCode;

    private String gympuLanUsername;

    private String gympuLanDisplayName;

    private String gympuLanUserGrade;

    private String gympuLanUserGroup;

    private String gympuLanSessionId;

    private String gympuLanVPlanId;

    private String gympuLanVPlanLastUpdate;

    private String gympuLanVPlanNumberOfPages;


    public GympuResponse()
    {

    }

    public String ActionSent() {
        return actionSent;
    }

    public void setActionSent(String actionSent) {
        this.actionSent = actionSent;
    }

    public String StatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String GympuLanUsername() {
        return gympuLanUsername;
    }

    public void setGympuLanUsername(String gympuLanUsername) {
        this.gympuLanUsername = gympuLanUsername;
    }

    public String GympuLanDisplayName() {
        return gympuLanDisplayName;
    }

    public void setGympuLanDisplayName(String gympuLanDisplayName) {
        this.gympuLanDisplayName = gympuLanDisplayName;
    }

    public String GympuLanUserGrade() {
        return gympuLanUserGrade;
    }

    public void setGympuLanUserGrade(String gympuLanUserGrade) {
        this.gympuLanUserGrade = gympuLanUserGrade;
    }

    public String GympuLanUserGroup() {
        return gympuLanUserGroup;
    }

    public void setGympuLanUserGroup(String gympuLanUserGroup) {
        this.gympuLanUserGroup = gympuLanUserGroup;
    }

    public String GympuLanSessionId() {
        return gympuLanSessionId;
    }

    public void setGympuLanSessionId(String gympuLanSessionId) {
        this.gympuLanSessionId = gympuLanSessionId;
    }

    public String GympuLanVPlanId() {
        return gympuLanVPlanId;
    }

    public void setGympuLanVPlanId(String gympuLanVPlanId) {
        this.gympuLanVPlanId = gympuLanVPlanId;
    }

    public String GympuLanVPlanLastUpdate() {
        return gympuLanVPlanLastUpdate;
    }

    public void setGympuLanVPlanLastUpdate(String gympuLanVPlanLastUpdate) {
        this.gympuLanVPlanLastUpdate = gympuLanVPlanLastUpdate;
    }

    public String GympuLanVPlanNumberOfPages() {
        return gympuLanVPlanNumberOfPages;
    }

    public void setGympuLanVPlanNumberOfPages(String gympuLanVPlanNumberOfPages) {
        this.gympuLanVPlanNumberOfPages = gympuLanVPlanNumberOfPages;
    }
}