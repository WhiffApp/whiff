package edu.sim.whiff.UI.HomePage;

/**
 * Created by gyych on 1/1/2018.
 */

public class HomePagePresenter implements HomePagePresenterInterface {
    public HomePageViewInterface view;
    public HomePagePresenter(HomePage homepage)
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
