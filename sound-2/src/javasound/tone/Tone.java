package javasound.tone;

import jass.engine.SinkIsFullException;
import jass.generators.LoopBuffer;
import jass.render.SourcePlayer;


public class Tone {
    private static float srate = 44100.f;
    private SourcePlayer player;
    private LoopBuffer loopBuffer;
    private static float[] COS = new float[]{0.0f, 3.0517578E-5f, 9.1552734E-5f, 1.5258789E-4f, 2.4414062E-4f, 3.6621094E-4f, 4.8828125E-4f, 6.4086914E-4f, 8.239746E-4f, 0.0010070801f, 0.0012207031f, 0.0014648438f, 0.0017089844f, 0.0019836426f, 0.0022888184f, 0.0025939941f, 0.0029296875f, 0.0032653809f, 0.0036621094f, -0.003753662f, -0.0033569336f, -0.0029296875f, -0.0024719238f, -0.0019836426f, -0.0014953613f, -9.765625E-4f, -4.5776367E-4f, 0.007904053f, 0.008483887f, 0.009094238f, 0.00970459f, 0.010314941f, 0.010986328f, 0.011657715f, 0.0045166016f, 0.0052490234f, 0.0059814453f, 0.006713867f, 0.0074768066f, 0.016082764f, 0.016906738f, 0.017730713f, 0.018585205f, 0.019439697f, 0.012512207f, 0.013427734f, 0.014343262f, 0.015289307f, 0.02407837f, 0.025054932f, 0.026062012f, 0.027069092f, 0.02029419f, 0.021362305f, 0.02243042f, 0.031341553f, 0.032440186f, 0.033599854f, 0.034729004f, 0.02810669f, 0.029266357f, 0.03048706f, 0.039520264f, 0.040771484f, 0.042022705f, 0.035491943f, 0.0368042f, 0.038116455f, 0.04724121f, 0.048614502f, 0.049957275f, 0.043548584f, 0.044952393f, 0.04638672f, 0.055633545f, 0.05706787f, 0.058563232f, 0.052246094f, 0.053741455f, 0.063079834f, 0.06463623f, 0.06619263f, 0.059936523f, 0.061553955f, 0.07095337f, 0.07260132f, 0.06640625f, 0.06808472f, 0.06976318f, 0.07925415f, 0.080963135f, 0.07489014f, 0.07662964f, 0.08618164f, 0.08795166f, 0.0897522f, 0.083740234f, 0.08557129f, 0.095214844f, 0.097076416f, 0.09112549f, 0.09298706f, 0.10272217f, 0.10461426f, 0.098724365f, 0.10067749f, 0.110443115f, 0.11242676f, 0.1065979f, 0.10858154f, 0.1184082f, 0.12045288f, 0.11468506f, 0.11672974f, 0.12661743f, 0.12869263f, 0.12298584f, 0.13290405f, 0.13504028f, 0.12936401f, 0.13153076f, 0.14147949f, 0.14367676f, 0.13806152f, 0.14025879f, 0.15029907f, 0.14471436f, 0.14694214f, 0.15701294f, 0.15927124f, 0.15374756f, 0.15603638f, 0.16616821f, 0.16064453f, 0.16299438f, 0.17312622f, 0.17547607f, 0.17004395f, 0.1802063f, 0.18261719f, 0.17718506f, 0.17959595f, 0.18981934f, 0.18444824f, 0.18685913f, 0.19714355f, 0.19177246f, 0.19424438f, 0.20452881f, 0.19921875f, 0.20172119f, 0.21203613f, 0.21453857f, 0.20925903f, 0.21960449f, 0.22216797f, 0.21688843f, 0.2272644f, 0.22982788f, 0.22460938f, 0.23501587f, 0.23760986f, 0.23239136f, 0.24282837f, 0.24545288f, 0.2402649f, 0.2507019f, 0.25335693f, 0.24819946f, 0.258667f, 0.26132202f, 0.25619507f, 0.26669312f, 0.26937866f, 0.2642517f, 0.27478027f, 0.26965332f, 0.27236938f, 0.28289795f, 0.27783203f, 0.2805481f, 0.29110718f, 0.28604126f, 0.28878784f, 0.29934692f, 0.294281f, 0.3048706f, 0.3076172f, 0.3025818f, 0.3131714f, 0.3159485f, 0.3109131f, 0.3215332f, 0.3164978f, 0.31930542f, 0.32992554f, 0.32492065f, 0.32772827f, 0.3383484f, 0.3333435f, 0.34396362f, 0.34680176f, 0.34179688f, 0.3524475f, 0.35525513f, 0.35028076f, 0.3609314f, 0.35595703f, 0.35879517f, 0.3694458f, 0.36447144f, 0.37512207f, 0.3779602f, 0.37298584f, 0.38363647f, 0.3864746f, 0.38150024f, 0.3921814f, 0.38720703f, 0.39004517f, 0.40072632f, 0.39575195f, 0.4064026f, 0.40924072f, 0.40429688f, 0.4149475f, 0.41778564f, 0.41281128f, 0.4234619f, 0.41848755f, 0.42132568f, 0.43197632f, 0.42700195f, 0.4376526f, 0.44049072f, 0.43551636f, 0.44613647f, 0.4489746f, 0.44400024f, 0.45462036f, 0.44961548f, 0.4524536f, 0.46307373f, 0.45806885f, 0.46087646f, 0.47149658f, 0.46646118f, 0.4770813f, 0.4798584f, 0.47485352f, 0.48544312f, 0.4882202f, 0.4831848f, 0.4937439f, 0.4887085f, 0.4914856f, 0.5020447f, 0.49697876f, 0.49972534f, 0.5102844f, 0.505188f, 0.51571655f, 0.51846313f, 0.5133667f, 0.52386475f, 0.5265808f, 0.52145386f, 0.5319824f, 0.53463745f, 0.5295105f, 0.54000854f, 0.5426636f, 0.5375061f, 0.54797363f, 0.55059814f, 0.54541016f, 0.5558777f, 0.5584717f, 0.5532837f, 0.5636902f, 0.5662842f, 0.5610657f, 0.57144165f, 0.5740051f, 0.5687561f, 0.5791321f, 0.58166504f, 0.5763855f, 0.58670044f, 0.5892334f, 0.58392334f, 0.59420776f, 0.5967102f, 0.5913696f, 0.60165405f, 0.60409546f, 0.59872437f, 0.6011658f, 0.61138916f, 0.60598755f, 0.60839844f, 0.6185913f, 0.6209717f, 0.61550903f, 0.6257019f, 0.62802124f, 0.6225586f, 0.6248779f, 0.63500977f, 0.6294861f, 0.6317749f, 0.6418457f, 0.644104f, 0.6385498f, 0.6485901f, 0.6508179f, 0.64523315f, 0.6474304f, 0.65740967f, 0.6595764f, 0.65393066f, 0.6560669f, 0.6660156f, 0.66033936f, 0.66244507f, 0.67233276f, 0.67440796f, 0.66867065f, 0.67071533f, 0.6805725f, 0.68258667f, 0.67678833f, 0.678772f, 0.6885681f, 0.69052124f, 0.68466187f, 0.686615f, 0.6963501f, 0.6982422f, 0.6923218f, 0.69418335f, 0.7038574f, 0.705719f, 0.69970703f, 0.7015381f, 0.7111511f, 0.71292114f, 0.71469116f, 0.70861816f, 0.71035767f, 0.71987915f, 0.72158813f, 0.7154541f, 0.71713257f, 0.726593f, 0.72824097f, 0.7298584f, 0.7236328f, 0.7252197f, 0.7345886f, 0.736145f, 0.7376709f, 0.7313843f, 0.73287964f, 0.7421875f, 0.74365234f, 0.74508667f, 0.7387085f, 0.7401428f, 0.7415161f, 0.7507019f, 0.7520752f, 0.75341797f, 0.7469177f, 0.74823f, 0.7495117f, 0.75860596f, 0.7598572f, 0.7610779f, 0.7544861f, 0.75567627f, 0.75686646f, 0.7658386f, 0.7669983f, 0.7680969f, 0.76919556f, 0.7624817f, 0.7635498f, 0.7645874f, 0.7734375f, 0.7744446f, 0.77545166f, 0.7764282f, 0.76956177f, 0.7704773f, 0.7713928f, 0.77230835f, 0.77316284f, 0.78186035f, 0.7826843f, 0.7835083f, 0.78430176f, 0.7850952f, 0.77804565f, 0.7787781f, 0.77948f, 0.7801819f, 0.7808838f, 0.78933716f, 0.79000854f, 0.7906189f, 0.79122925f, 0.7918091f, 0.7923584f, 0.7929077f, 0.785614f, 0.7861328f, 0.7866211f, 0.78707886f, 0.7875061f, 0.78793335f, 0.7883301f, 0.7887268f, 0.7969055f, 0.7972412f, 0.7975769f, 0.7978821f, 0.79815674f, 0.7984314f, 0.79867554f, 0.79888916f, 0.79907227f, 0.7992554f, 0.7994385f, 0.79956055f, 0.7996826f, 0.7998047f, 0.7998657f, 0.79992676f, 0.7999878f, 0.7999878f, 0.7999878f, 0.7999878f, 0.79992676f, 0.7998657f, 0.7998047f, 0.7996826f, 0.79956055f, 0.7994385f, 0.7992554f, 0.79907227f, 0.79888916f, 0.79867554f, 0.7984314f, 0.79815674f, 0.7978821f, 0.7975769f, 0.7972412f, 0.7969055f, 0.7887268f, 0.7883301f, 0.78793335f, 0.7875061f, 0.78707886f, 0.7866211f, 0.7861328f, 0.785614f, 0.7929077f, 0.7923584f, 0.7918091f, 0.79122925f, 0.7906189f, 0.79000854f, 0.78933716f, 0.7808838f, 0.7801819f, 0.77948f, 0.7787781f, 0.77804565f, 0.7850952f, 0.78430176f, 0.7835083f, 0.7826843f, 0.78186035f, 0.77316284f, 0.77230835f, 0.7713928f, 0.7704773f, 0.76956177f, 0.7764282f, 0.77545166f, 0.7744446f, 0.7734375f, 0.7645874f, 0.7635498f, 0.7624817f, 0.76919556f, 0.7680969f, 0.7669983f, 0.7658386f, 0.75686646f, 0.75567627f, 0.7544861f, 0.7610779f, 0.7598572f, 0.75860596f, 0.7495117f, 0.74823f, 0.7469177f, 0.75341797f, 0.7520752f, 0.7507019f, 0.7415161f, 0.7401428f, 0.7387085f, 0.74508667f, 0.74365234f, 0.7421875f, 0.73287964f, 0.7313843f, 0.7376709f, 0.736145f, 0.7345886f, 0.7252197f, 0.7236328f, 0.7298584f, 0.72824097f, 0.726593f, 0.71713257f, 0.7154541f, 0.72158813f, 0.71987915f, 0.71035767f, 0.70861816f, 0.71469116f, 0.71292114f, 0.7111511f, 0.7015381f, 0.69970703f, 0.705719f, 0.7038574f, 0.69418335f, 0.6923218f, 0.6982422f, 0.6963501f, 0.686615f, 0.68466187f, 0.69052124f, 0.6885681f, 0.678772f, 0.67678833f, 0.68258667f, 0.6805725f, 0.67071533f, 0.66867065f, 0.67440796f, 0.67233276f, 0.66244507f, 0.66033936f, 0.6660156f, 0.6560669f, 0.65393066f, 0.6595764f, 0.65740967f, 0.6474304f, 0.64523315f, 0.6508179f, 0.6485901f, 0.6385498f, 0.644104f, 0.6418457f, 0.6317749f, 0.6294861f, 0.63500977f, 0.6248779f, 0.6225586f, 0.62802124f, 0.6257019f, 0.61550903f, 0.6209717f, 0.6185913f, 0.60839844f, 0.60598755f, 0.61138916f, 0.6011658f, 0.59872437f, 0.60409546f, 0.60165405f, 0.5913696f, 0.5967102f, 0.59420776f, 0.58392334f, 0.5892334f, 0.58670044f, 0.5763855f, 0.58166504f, 0.5791321f, 0.5687561f, 0.5740051f, 0.57144165f, 0.5610657f, 0.5662842f, 0.5636902f, 0.5532837f, 0.5584717f, 0.5558777f, 0.54541016f, 0.55059814f, 0.54797363f, 0.5375061f, 0.5426636f, 0.54000854f, 0.5295105f, 0.53463745f, 0.5319824f, 0.52145386f, 0.5265808f, 0.52386475f, 0.5133667f, 0.51846313f, 0.51571655f, 0.505188f, 0.5102844f, 0.49972534f, 0.49697876f, 0.5020447f, 0.4914856f, 0.4887085f, 0.4937439f, 0.4831848f, 0.4882202f, 0.48544312f, 0.47485352f, 0.4798584f, 0.4770813f, 0.46646118f, 0.47149658f, 0.46087646f, 0.45806885f, 0.46307373f, 0.4524536f, 0.44961548f, 0.45462036f, 0.44400024f, 0.4489746f, 0.44613647f, 0.43551636f, 0.44049072f, 0.4376526f, 0.42700195f, 0.43197632f, 0.42132568f, 0.41848755f, 0.4234619f, 0.41281128f, 0.41778564f, 0.4149475f, 0.40429688f, 0.40924072f, 0.4064026f, 0.39575195f, 0.40072632f, 0.39004517f, 0.38720703f, 0.3921814f, 0.38150024f, 0.3864746f, 0.38363647f, 0.37298584f, 0.3779602f, 0.37512207f, 0.36447144f, 0.3694458f, 0.35879517f, 0.35595703f, 0.3609314f, 0.35028076f, 0.35525513f, 0.3524475f, 0.34179688f, 0.34680176f, 0.34396362f, 0.3333435f, 0.3383484f, 0.32772827f, 0.32492065f, 0.32992554f, 0.31930542f, 0.3164978f, 0.3215332f, 0.3109131f, 0.3159485f, 0.3131714f, 0.3025818f, 0.3076172f, 0.3048706f, 0.294281f, 0.29934692f, 0.28878784f, 0.28604126f, 0.29110718f, 0.2805481f, 0.27783203f, 0.28289795f, 0.27236938f, 0.26965332f, 0.27478027f, 0.2642517f, 0.26937866f, 0.26669312f, 0.25619507f, 0.26132202f, 0.258667f, 0.24819946f, 0.25335693f, 0.2507019f, 0.2402649f, 0.24545288f, 0.24282837f, 0.23239136f, 0.23760986f, 0.23501587f, 0.22460938f, 0.22982788f, 0.2272644f, 0.21688843f, 0.22216797f, 0.21960449f, 0.20925903f, 0.21453857f, 0.21203613f, 0.20172119f, 0.19921875f, 0.20452881f, 0.19424438f, 0.19177246f, 0.19714355f, 0.18685913f, 0.18444824f, 0.18981934f, 0.17959595f, 0.17718506f, 0.18261719f, 0.1802063f, 0.17004395f, 0.17547607f, 0.17312622f, 0.16299438f, 0.16064453f, 0.16616821f, 0.15603638f, 0.15374756f, 0.15927124f, 0.15701294f, 0.14694214f, 0.14471436f, 0.15029907f, 0.14025879f, 0.13806152f, 0.14367676f, 0.14147949f, 0.13153076f, 0.12936401f, 0.13504028f, 0.13290405f, 0.12298584f, 0.12869263f, 0.12661743f, 0.11672974f, 0.11468506f, 0.12045288f, 0.1184082f, 0.10858154f, 0.1065979f, 0.11242676f, 0.110443115f, 0.10067749f, 0.098724365f, 0.10461426f, 0.10272217f, 0.09298706f, 0.09112549f, 0.097076416f, 0.095214844f, 0.08557129f, 0.083740234f, 0.0897522f, 0.08795166f, 0.08618164f, 0.07662964f, 0.07489014f, 0.080963135f, 0.07925415f, 0.06976318f, 0.06808472f, 0.06640625f, 0.07260132f, 0.07095337f, 0.061553955f, 0.059936523f, 0.06619263f, 0.06463623f, 0.063079834f, 0.053741455f, 0.052246094f, 0.058563232f, 0.05706787f, 0.055633545f, 0.04638672f, 0.044952393f, 0.043548584f, 0.049957275f, 0.048614502f, 0.04724121f, 0.038116455f, 0.0368042f, 0.035491943f, 0.042022705f, 0.040771484f, 0.039520264f, 0.03048706f, 0.029266357f, 0.02810669f, 0.034729004f, 0.033599854f, 0.032440186f, 0.031341553f, 0.02243042f, 0.021362305f, 0.02029419f, 0.027069092f, 0.026062012f, 0.025054932f, 0.02407837f, 0.015289307f, 0.014343262f, 0.013427734f, 0.012512207f, 0.019439697f, 0.018585205f, 0.017730713f, 0.016906738f, 0.016082764f, 0.0074768066f, 0.006713867f, 0.0059814453f, 0.0052490234f, 0.0045166016f, 0.011657715f, 0.010986328f, 0.010314941f, 0.00970459f, 0.009094238f, 0.008483887f, 0.007904053f, -4.5776367E-4f, -9.765625E-4f, -0.0014953613f, -0.0019836426f, -0.0024719238f, -0.0029296875f, -0.0033569336f, -0.003753662f, 0.0036621094f, 0.0032653809f, 0.0029296875f, 0.0025939941f, 0.0022888184f, 0.0019836426f, 0.0017089844f, 0.0014648438f, 0.0012207031f, 0.0010070801f, 8.239746E-4f, 6.4086914E-4f, 4.8828125E-4f, 3.6621094E-4f, 2.4414062E-4f, 1.5258789E-4f, 9.1552734E-5f, 3.0517578E-5f, 0.0f};
    private int bufferSize;
    private int bufferSizeJavaSound;

    // Convert db to amp for tone
    public static float db2a( float db ) {
        return (float)( Math.sqrt( 2 ) * Math.exp( db * Math.log( 10 ) / 20 ) );
    }

    /**
     * Create a (non-mute) tone.
     */
    public Tone( float freq, float vol ) {
        this( freq, vol, false );
    }

    public Tone( float freq, float vol, boolean muted ) {
        bufferSize = 1024;
        bufferSizeJavaSound = 8 * bufferSize;
        //        bufferSize = 1024;
        //        bufferSizeJavaSound = 8 * 1024;
        float[] COS_FLOAT = new float[COS.length];
        for( int i = 0; i < COS.length; i++ ) {
            COS_FLOAT[i] = (float)COS[i];
        }
        loopBuffer = new LoopBuffer( srate, bufferSize, COS_FLOAT );//filename);
        player = new SourcePlayer( bufferSize, bufferSizeJavaSound, srate );

        try {
            player.addSource( loopBuffer );
        }
        catch( SinkIsFullException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        setVolume( vol );
        setPitch( freq );
        setMuted( muted );
    }

    void setPitch( float freq ) {
        float f = (float)freq;
        /*Hack to make the freq correspond to reality.  Used MBTuner to verify tuning.*/
        float speed = f / 50;
        loopBuffer.setSpeed( speed );
    }

    void start() {
        player.setPriority( Thread.NORM_PRIORITY );
        //        player.setPriority( Thread.MAX_PRIORITY );
        player.start();
    }

    void stop() {
        player.stopPlaying();
    }

    void setVolume( float amp ) {
        if( amp < 0 ) {
            throw new RuntimeException( "Volume was negative: " + amp );
        }
        else if( amp > 1.0 ) {
            throw new RuntimeException( "Volume out of range: " + amp );
        }
        amp *= 1.413;
        loopBuffer.setVolume( (float)amp );
    }

    void setMuted( boolean t ) {
        player.setMute( t );
    }
}

