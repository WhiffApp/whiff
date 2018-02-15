package com.app.whiff.whiff.ARPSpoofer.UI;

/**
 * ARPSpooferPresenter
 * Actions to take when buttons are clicked in ARPSpoofer activity.
 */

public class ARPSpooferPresenter implements ARPSpooferPresenterInterface {

    public ARPSpooferViewInterface view;

    public ARPSpooferPresenter(ARPSpoofer homepage) {
        view = homepage;
    }

    public void ActivityStarted() {
    }

    public void StartClicked() {
        view.hideFabStart();
    }

    public void StopClicked() {
        view.hideFabStop();
    }

}
