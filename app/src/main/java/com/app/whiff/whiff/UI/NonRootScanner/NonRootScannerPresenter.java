package com.app.whiff.whiff.UI.NonRootScanner;

/**
 * NonRootScannerPresenter
 * Actions to take when buttons are clicked in NonRootScanner activity.
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {

    public NonRootScannerViewInterface view;

    public NonRootScannerPresenter(NonRootScanner nonRootScanner) {
        view = nonRootScanner;
    }

    public void VpnButtonClicked() {
    }

    public void StartClicked() {
        view.hideFabStart();
        view.startVPN();

    }

    public void StopClicked() {
        view.hideFabStop();
        view.stopVPN();
    }

}
