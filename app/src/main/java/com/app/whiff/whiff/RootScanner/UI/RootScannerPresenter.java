package com.app.whiff.whiff.RootScanner.UI;

/**
 * RootScannerPresenter
 * Actions to take when buttons are clicked in RootScanner activity.
 */

public class RootScannerPresenter implements RootScannerPresenterInterface {

    public RootScannerViewInterface view;

    public RootScannerPresenter(RootScanner homepage) {
        view = homepage;
    }

    public void ActivityStarted() {
        view.installTCPdump();
    }

    public void StartClicked() {
        view.hideFabStart();
    }

    public void StopClicked() {
        view.hideFabStop();
    }

}
