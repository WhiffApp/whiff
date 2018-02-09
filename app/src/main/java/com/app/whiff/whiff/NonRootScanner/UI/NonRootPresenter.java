package com.app.whiff.whiff.NonRootScanner.UI;

/**
 * Created by gyych on 1/1/2018.
 */

public class NonRootPresenter implements NonRootPresenterInterface {
    public NonRootViewInterface view;
    public NonRootPresenter(NonRootScanner homepage)
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
