package com.app.whiff.whiff.NonRootScanner.UI;

/**
 * Created by gyych on 1/1/2018.
 */

public class NonRootScannerPresenter implements NonRootScannerPresenterInterface {
    public NonRootScannerViewInterface view;
    public NonRootScannerPresenter(NonRootScanner homepage)
    {
        view = homepage;
    }
    public void StartClicked(){
        view.hideFabStart();
    }
    public void StopClicked(){
        view.hideFabStop();
    }
}
