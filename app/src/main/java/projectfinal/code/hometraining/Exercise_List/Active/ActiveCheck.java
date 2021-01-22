package projectfinal.code.hometraining.Exercise_List.Active;

/*운동활동에 대한 완료여부에 대한 정보를 갖는 클래스*/
public class ActiveCheck {

    //수정부
    private static ActiveCheck Instance;

    //singleton
    public static ActiveCheck getInstance(){
        if (Instance == null){
            Instance = new ActiveCheck();
        }
        return Instance;
    }
    private boolean check_active;

    //getter
    public boolean isCheck_active() {
        return check_active;
    }
    //setter
    public void setCheck_active(boolean check_active) {
        this.check_active = check_active;
    }
    public void clear(){
        check_active=false;
    }


}


