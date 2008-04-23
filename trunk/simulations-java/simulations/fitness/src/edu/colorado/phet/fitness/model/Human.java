package edu.colorado.phet.fitness.model;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.fitness.module.fitness.Exercise;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:05:20 PM
 */
public class Human {

    private Gender gender = DEFAULT_VALUE.getGender();
    private String name = "Larry";
    private ArrayList listeners = new ArrayList();

    private DefaultTemporalVariable height = new DefaultTemporalVariable( DEFAULT_VALUE.getHeightMeters() );//meters
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( DEFAULT_VALUE.getMassKG() );//kg
    private DefaultTemporalVariable age = new DefaultTemporalVariable( DEFAULT_VALUE.getAgeSeconds() );//sec
    private DefaultTemporalVariable fatMassFraction = new DefaultTemporalVariable( ( 100 - DEFAULT_VALUE.getFatFreeMassPercent() ) / 100.0 );

    private DefaultTemporalVariable lipids = new DefaultTemporalVariable();
    private DefaultTemporalVariable carbs = new DefaultTemporalVariable();
    private DefaultTemporalVariable proteins = new DefaultTemporalVariable();

    private DefaultTemporalVariable activity = new DefaultTemporalVariable();//initialized to 0.5*BMR
    private DefaultTemporalVariable exercise = new DefaultTemporalVariable();//initialized to make sure weight is constant at startup
    private DefaultTemporalVariable bmr = new DefaultTemporalVariable();//dependent variable
    private Exercise exerciseObject = null;
    private static final ReferenceHuman REFERENCE_MALE = new ReferenceHuman( true, 22, 5, 8.5, 70, 86 );
    private static final ReferenceHuman REFERENCE_FEMALE = new ReferenceHuman( false, 22, 5, 4.5, 57, 74 );
    private static final ReferenceHuman DEFAULT_VALUE = REFERENCE_FEMALE;

    static class ReferenceHuman {
        boolean male;
        double ageYears;
        double heightFT;
        double massKG;
        double fatFreeMassPercent;

        ReferenceHuman( boolean male, double ageYears, double heightFT, double heightIN, double massKG, double fatFreeMassPercent ) {
            this.male = male;
            this.ageYears = ageYears;
            this.heightFT = heightFT + heightIN / 12;
            this.massKG = massKG;
            this.fatFreeMassPercent = fatFreeMassPercent;
        }

        double getHeightMeters() {
            return FitnessUnits.feetToMeters( heightFT );
        }

        double getAgeSeconds() {
            return FitnessUnits.yearsToSeconds( ageYears );
        }

        double getMassKG() {
            return massKG;
        }

        double getFatFreeMassPercent() {
            return fatFreeMassPercent;
        }

        public Gender getGender() {
            return male ? Gender.MALE : Gender.FEMALE;
        }
    }


    public Human() {
        setDiet( FitnessModel.BALANCED_DIET );
        addListener( new Adapter() {
            public void heightChanged() {
                updateBMR();
            }

            public void weightChanged() {
                updateBMR();
            }

            public void ageChanged() {
                updateBMR();
            }

            public void genderChanged() {
                updateBMR();
            }

            public void fatPercentChanged() {
                updateBMR();
            }
        } );
        updateBMR();
        activity.setValue( bmr.getValue() * 0.5 );
        double dCal = getDeltaCaloriesGained();
        exercise.setValue( exercise.getValue() + dCal );
        lipids.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        carbs.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        proteins.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        exercise.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyExerciseChanged();
            }
        } );
    }

    private void notifyExerciseChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.exerciseChanged();
        }
    }

    public Diet getDiet() {
        return FitnessModel.getDiet( lipids.getValue(), carbs.getValue(), proteins.getValue() );
    }

    private void notifyDietChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).dietChanged();
        }
    }

    public void setDiet( Diet diet ) {
        lipids.setValue( diet.getFat() );
        carbs.setValue( diet.getCarb() );
        proteins.setValue( diet.getProtein() );
    }

    private void updateBMR() {
//        bmr.setValue( BasalMetabolicRate.getBasalMetabolicRateHarrisBenedict( getMass(), getHeight(), getAge(), gender ) );
//        System.out.println( "value = " + value +", FFMP="+getFatFreeMassPercent());
        bmr.setValue( 392 + 21.8 * getFatFreeMassPercent() );
        updateActivity();
    }

    /**
     * http://usmilitary.about.com/od/airforcejoin/a/afmaxweight.htm
     * The formula to compute BMI is
     * weight (in pounds) divided by the square of height (in inches),
     * multiplied by 704.5
     * <p/>
     * (Don't worry about that though, the below chart shows the maximum and minimum weights using the formula).
     *
     * @return
     */
    public double getBMIOrig() {
        return getWeightPounds() / Math.pow( getHeightInches(), 2 ) * 704.5;
    }

    public double getBMI() {
        return getMass() / Math.pow( getHeight(), 2 );
    }

    private double getHeightInches() {
        return getHeight() / 0.0254;
    }

    private double getWeightPounds() {
        return getMass() * 2.20462262;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

//    public double getLeanMuscleMass() {
//        return leanMuscleMass.getValue();
//    }

//    public double getFatPercent() {
//        return 100 - leanMuscleMass.;
//    }

//    public void setLeanMuscleMass( double value ) {
//        this.leanMuscleMass.setValue( value );
//        notifyMusclePercentChanged();
//        notifyFatPercentChanged();
//    }

    private void notifyFatPercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.fatPercentChanged();
        }
    }

    private void notifyMusclePercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.musclePercentChanged();
        }
    }

//    public void setFatPercent( double value ) {
//        this.leanMuscleMass = 100 - value;
//        notifyFatPercentChanged();
//        notifyMusclePercentChanged();

    //    }

    double activityLevel = 0.5;

    public void setActivityLevel( double val ) {
        activityLevel = val;
        updateActivity();
//        this.activity.setValue( val );
    }

    private void updateActivity() {
        this.activity.setValue( activityLevel * bmr.getValue() );
        notifyActivityChanged();
    }

    private void notifyActivityChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).activityChanged();
        }
    }

    public DefaultTemporalVariable getLipids() {
        return lipids;
    }

    public DefaultTemporalVariable getCarbs() {
        return carbs;
    }

    public DefaultTemporalVariable getProteins() {
        return proteins;
    }

    public DefaultTemporalVariable getBmr() {
        return bmr;
    }

    public DefaultTemporalVariable getActivity() {
        return activity;
    }

    public DefaultTemporalVariable getExercise() {
        return exercise;
    }

    public void simulationTimeChanged( double simulationTimeChange ) {
        setAge( getAge() + simulationTimeChange );
        double caloriesGainedPerDay = getDeltaCaloriesGained();
        double kgGainedPerDay = FitnessUnits.caloriesToKG( caloriesGainedPerDay );
        setMass( getMass() + kgGainedPerDay * FitnessUnits.secondsToDays( simulationTimeChange ) );
    }

    private double getDeltaCaloriesGained() {
        return getDailyCaloricIntake() - getDailyCaloricExpense();
    }

    private double getDailyCaloricExpense() {
        return bmr.getValue() + activity.getValue() + exercise.getValue();
    }

    private double getDailyCaloricIntake() {
        return lipids.getValue() + proteins.getValue() + carbs.getValue();
    }

    public void setExercise( Exercise exercise ) {
        this.exerciseObject = exercise;//todo: should be a list
        this.exercise.setValue( exercise.getCalories() );
    }

    public Exercise getExerciseObject() {
        return exerciseObject;
    }

    public double getHeartHealth() {
        return 0.5;
    }

    public double getFatMassPercent() {
        return fatMassFraction.getValue() * 100;
    }

    public double getFatFreeMassPercent() {
        return 100 - getFatMassPercent();
    }

    public void setFatMassPercent( double value ) {
        if ( getGender() == Gender.FEMALE ) {
            value = MathUtil.clamp( 10, value, 40 );
        }
        else if ( getGender() == Gender.MALE ) {
            value = MathUtil.clamp( 4, value, 40 );
        }
        fatMassFraction.setValue( value / 100.0 );
        notifyFatPercentChanged();
    }
    //    public double getDailyCaloricIntake() {
//        double sum = 0;
//        for ( Iterator iterator = foods.iterator(); iterator.hasNext(); ) {
//            FoodItem foodItem = (FoodItem) iterator.next();
//            sum += foodItem.getCalories();
//        }
//        return sum;
//    }

    public static class Gender {
        public static Gender MALE = new Gender( "male" );
        public static Gender FEMALE = new Gender( "female" );
        private String name;

        private Gender( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public double getAge() {
        return age.getValue();
    }

    public void setAge( double age ) {
        this.age.setValue( age );
        notifyAgeChanged();
    }

    private void notifyAgeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.ageChanged();
        }
    }

    public double getHeight() {
        return height.getValue();
    }

    public void setHeight( double height ) {
        this.height.setValue( height );
        notifyHeightChanged();
        notifyBMIChanged();
    }

    private void notifyBMIChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.bmiChanged();
        }
    }

    private void notifyHeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.heightChanged();
        }
    }

    public double getMass() {
        return mass.getValue();
    }

    public void setMass( double weight ) {
        this.mass.setValue( Math.max( weight, 0 ) );
        notifyWeightChanged();
        notifyBMIChanged();
    }

    private void notifyWeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.weightChanged();
        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender( Gender gender ) {
        if ( this.gender != gender ) {
            this.gender = gender;
            notifyGenderChanged();
        }
    }

    private void notifyGenderChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).genderChanged();
        }
    }

    public static interface Listener {
        void bmiChanged();

        void heightChanged();

        void weightChanged();

        void genderChanged();

        void musclePercentChanged();

        void fatPercentChanged();

        void foodItemsChanged();

        void ageChanged();

        void dietChanged();

        void exerciseChanged();

        void activityChanged();
    }

    public static class Adapter implements Listener {

        public void bmiChanged() {
        }

        public void heightChanged() {
        }

        public void weightChanged() {
        }

        public void genderChanged() {
        }

        public void musclePercentChanged() {
        }

        public void fatPercentChanged() {
        }

        public void foodItemsChanged() {
        }

        public void ageChanged() {
        }

        public void dietChanged() {
        }

        public void exerciseChanged() {
        }

        public void activityChanged() {
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

//    public void addFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.add( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "added foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }
//    }
//
//    private void notifyFoodItemsChanged() {
//        for ( int i = 0; i < listeners.size(); i++ ) {
//            Listener listener = (Listener) listeners.get( i );
//            listener.foodItemsChanged();
//        }
//    }
//
//    public void removeFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.remove( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "removed foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }
//    }

}
