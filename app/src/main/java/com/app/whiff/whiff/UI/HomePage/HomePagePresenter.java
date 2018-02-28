package com.app.whiff.whiff.UI.HomePage;

import android.content.Context;

/**
 * RootScannerPresenter
 * Actions to take when buttons are clicked in RootScanner activity.
 */

public class HomePagePresenter implements HomePagePresenterInterface {

    public Context context;
    public HomePageViewInterface view;


    public HomePagePresenter(HomePage homepage) {
        view = homepage;
    }

    public void RootScannerButtonClicked() {
        // Start RootScanner Activity
    }

    public void NonRootScannerButtonClicked() {
        // Start NonRootScanner Activity
    }

    public void ARPSpooferButtonClicked() {
        // Start ARPSpoofer Activity
    }

    public void StartClicked() {
    }

    public void StopClicked() {
    }

}
