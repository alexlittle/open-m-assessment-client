package org.digitalcampus.mquiz.listeners;

public interface SubmitResultsListener {
    void submitResultsComplete();
    void progressUpdate(String msg);
}
