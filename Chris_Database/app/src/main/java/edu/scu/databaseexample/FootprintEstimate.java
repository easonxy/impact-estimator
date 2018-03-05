package edu.scu.databaseexample;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Class to contain an impact estimate (initially just CO2)
 *
 * Created by teubert on 2/26/18.
 */
public class FootprintEstimate {
    private static final String DEBUG_TAG = "FootprintEstimate";

    // It's own class for extendability
    public double CO2 = 0;
    // Future versions could include other metrics

    public long nDays;

    public static FootprintEstimate generateEstimate(DayTripsSummary dayTrips, UserProfile user) {
        double co2 = 0;

        // TODO(CT): FIX UNITS- KG

        // http://shrinkthatfootprint.com/what-is-your-carbon-footprint

        // Add trips portion
        for (Trip trip : dayTrips.trips) {
            co2 += trip.getEstimate().CO2;
        }

        // Add breathing
        // From http://www.slate.com/articles/news_and_politics/explainer/2009/08/7_billion_carbon_sinks.html
        co2 += 2.3;
        // TODO(CT): Add pets

        // Add food
        // 	= Σ[CO2 from farm] + Σ[CO2 from transport]
        //  - Transport Decreased through farmers market
        //  - Farm decreased by going vegetarian/vegan

        // CO2 Emissions: http://www.greeneatz.com/foods-carbon-footprint.html - does not include freight
        // Only production
        final double mealCO2            = 9.04; // kg/day
        final double averageFoodCO2     = 6.85; // kg/day
        final double noBeefCO2          = 5.25; // kg/day
        final double vegitarianFoodCO2  = 4.66; // kg/day
        final double veganFoodCO2       = 4.11; // kg/day

        // https://www.npr.org/sections/thesalt/2011/12/31/144478009/the-average-american-ate-literally-a-ton-this-year
        final double food_consumption   = 2.4787; // kg food/day

        // Freight
        // https://carbonfund.org/how-we-calculate/
        final double air_cargo          = 8.196e-7;     // kg CO2/kg-km
        final double truck              = 9.1e-8;       // kg CO2/kg-km
        final double train              = 1.5e-8;       // kg CO2/kg-km
        final double sea                = 3.741e-8;     // kg CO2/kg-km

        // https://cuesa.org/learn/how-far-does-your-food-travel-get-your-plate
        final double far_distance       = 3500;      // km - guess
        final double average_distance   = 2414;      // km - how far food travels
        final double local_distance     = 125;       // km

        final double freight_far        = far_distance * (air_cargo + train + sea)/3; // average
        final double freight_average    = average_distance * (air_cargo + train + sea)/3; // average
        final double freight_local      = local_distance * truck;  // local typically truck

        co2 += averageFoodCO2 + freight_average;

        // Add electricity
        //= Σ[Source efficiency]*[Percentage from source]*[Total Amount]
       // - Decreased through more efficient sources (get solar panel, etc)
        // - Decreased by degrading amount
        final double lbsPerKWh = 1.222;
        final double averageKWh = 29.4757; // from: https://www.eia.gov/tools/faqs/faq.php?id=97&t=3
        co2 += averageKWh*lbsPerKWh;

        // TODO(CT): Add locality for power source/average house use
        // TODO(CT): Add actual energy use
        // TODO(CT): Add house size

        // Add other sources
        double other = 0;
        co2 += other;

        // Reductions
        double reductions  = 0;
        co2 -= reductions;

        return new FootprintEstimate(co2, 1);
    }

    /**
     * Create an estimate
     *
     * @param CO2   CO2 Use
     * @param nDays Number of days impact estimate is over
     */
    public FootprintEstimate(double CO2, long nDays) {
        Log.v(DEBUG_TAG, "Creating FootprintEstimate Object");
        this.CO2 = CO2;
        this.nDays = nDays;
    }

    /**
     * Create an empty estimate
     *
     * @param nDays Number of days impact estimate is over
     */
    public FootprintEstimate(long nDays) {
        this.nDays = nDays;
    }

    /**
     * Add CO2 use to estimate
     *
     * @param CO2Addition   CO2 impact to add to estimate
     */
    public void addToEstimate(double CO2Addition) {
        this.CO2 += CO2Addition;
    }
}
