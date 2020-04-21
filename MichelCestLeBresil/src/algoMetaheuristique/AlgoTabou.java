package algoMetaheuristique;

//import static java.lang.String.format;
import java.util.ArrayList;
import support.City;
import support.Route;
//import java.util.Iterator;

/**
 *
 * @author cr-on
 */
public class AlgoTabou {
    

    
    // Méthode : implémentation de l'algo.

    public static Route optiTS(Route initialRoute, int nbmax, int tailleTabou){
        //Choix d'une solution admissible
        Route currentRoute = new Route(initialRoute);

        //Nombre de ville dans la Route initiale
        int nbCities = currentRoute.getCities().size();
        
        //Initialisation de la meilleure route (Aspiration)
        Route bestRoute = currentRoute;
        
        //Initialisation du compteur
        int nbiter = 0;
        
        //Initialistaion de la liste Tabou
        ArrayList<ArrayList> tabouList = new ArrayList<>();
        
        //Initialisation meilleure itération
        int best_iter = 0;

        //System.out.println("Route inititial : " + currentRoute + " distance : " + format("%.2f", currentRoute.getTotalDistance()) + " km");

        while ((bestRoute.getTotalDistance() >= 1000.0) && (nbiter-best_iter < nbmax)){
            
            //Incrémentation du compteur
            nbiter += 1;
            
            //Génération du voisinage de solution
            ArrayList<Route> voisins = new ArrayList<>();

            for (int i=0; i<nbCities-1; i++){
                for (int j=i+1; j<nbCities; j++){
                    voisins.add(new Route(currentRoute,i,j));
                }
            }

            //Recherche du meilleur voisin et mise à jour de la liste taboue
            Route bestVoisin = voisins.get(0);
            for (Route route : voisins){
                if (route.getTotalDistance() < bestVoisin.getTotalDistance()){
                    //System.out.println(tabouList.contains(route.getCities()));
                    if (tabouList.contains(route.getCities())){
                        //System.out.println("Solution dans la liste taboue");
                        if (route.getTotalDistance()< bestRoute.getTotalDistance()){
                            //System.out.println("Solution meilleure que l'aspiration");
                            bestVoisin =route;
                        }
                    }
                    else{
                        bestVoisin = route;
                    }
                }
            }

            //Affectation du meilleur voisin à la solution actuelle
            
            currentRoute = bestVoisin;
            if (tabouList.size()<tailleTabou){
                tabouList.add(currentRoute.getCities());
            }
            else{
                tabouList.remove(0);
                tabouList.add(currentRoute.getCities());
            }
            

            //Affection conditionnel de la solution actuelle à la meillere solution
            if (currentRoute.getTotalDistance() < bestRoute.getTotalDistance()){
                bestRoute = currentRoute;
                best_iter = nbiter;
            }
        }
        
        //System.out.println("Nombre d'itérations : " +  nbiter);
        //System.out.println("Meilleure itération : " + best_iter);
        //System.out.println("Les " + tabouList.size() +  " routes de a liste taboue");
        //System.out.println(tabouList);
        //System.out.println("Meilleure route : " + bestRoute + " distance : " + format("%.2f", bestRoute.getTotalDistance()) + " km");
        return bestRoute;      
    }
    public static void initTS(Route initRoute){
        
    }
    
    // Test de l'Algo Tabou
    public static void main(String[] args) {
        City bordeaux = new City("Bordeaux",44.833333,-0.566667);
        City lyon = new City("Lyon",45.750000,4.850000);
        City nantes = new City("Nantes",47.216667,-1.550000);
        City paris = new City("Paris",48.866667,2.333333);
        City marseille = new City("Marseille",43.300000,5.400000);
        City dijon = new City("Dijon",47.316667,5.016667);

        ArrayList<City> cities = new ArrayList<>();

        cities.add(bordeaux);
        cities.add(lyon);
        cities.add(nantes);
        cities.add(paris);
        cities.add(marseille);
        cities.add(dijon);
        
        Route initialRoute = new Route(cities);
        //Collections.shuffle(initialRoute.getCities());
        optiTS(initialRoute,100,10);
    }
}
