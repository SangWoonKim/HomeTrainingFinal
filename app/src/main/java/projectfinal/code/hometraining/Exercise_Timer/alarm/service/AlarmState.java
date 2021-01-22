package projectfinal.code.hometraining.Exercise_Timer.alarm.service;

public class AlarmState {

    //수정부
    private static AlarmState Instance;

    //singleton
    public static AlarmState getInstance(){
        if (Instance == null){
            Instance = new AlarmState();
        }
        return Instance;
    }

    private boolean check_alarm;

    //getter
    public boolean isCheck_alarm() {
        return check_alarm;
    }

    //setter
    public void setCheck_alarm(boolean check_alarm) {
        this.check_alarm = check_alarm;
    }

    public void clearAlarm(){
        check_alarm=false;
    }

}
