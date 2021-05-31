package fr.antoninhuaut.projet.controller.global;

import fr.antoninhuaut.projet.view.global.HomeView;

public class HomeController extends FXController {

    private HomeView homeView;

    public void setView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void postLoad() {

    }
}