package org.tacsbot.api.report;

public interface ReportApi {

    int getEngagedUsersCount();

    int getUsersCount();

    int getArticlesCount();

    int getSuccessfulArticlesCount();

    int getFailedArticlesCount();


}
