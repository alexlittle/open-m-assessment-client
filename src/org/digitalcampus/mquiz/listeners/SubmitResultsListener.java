package org.digitalcampus.mquiz.listeners;

public interface SubmitResultsListener {
    void submitResultsComplete(String msg);
    void progressUpdate(String msg);
}
