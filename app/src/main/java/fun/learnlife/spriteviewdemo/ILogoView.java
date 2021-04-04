package fun.learnlife.spriteviewdemo;

interface ILogoView {

    void start();
    void speakStart();
    void idle();
    void loading();
    void startContinuousListening();
    void ttsPlaying(int type);
}
