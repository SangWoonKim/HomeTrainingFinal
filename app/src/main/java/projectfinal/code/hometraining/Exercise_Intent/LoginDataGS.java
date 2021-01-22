package projectfinal.code.hometraining.Exercise_Intent;

/* Login에서 쓰일 로그인 정보를 getter,setter 클래스*/
public class LoginDataGS {
    //singleton
    //수정부
    private static LoginDataGS Instance;

    public static LoginDataGS getInstance(){
        if (Instance == null){
            Instance = new LoginDataGS();
        }
        return Instance;
    }
    private String Login_ID;
    private String Login_PW;


    //getter
    public String getLogin_ID() {
        return Login_ID;
    }
    public String getLogin_PW() {
        return Login_PW;
    }


    //setter
    public void setLogin_ID(String login_ID) {
        this.Login_ID = login_ID;
    }
    public void setLogin_PW(String login_PW) {
        this.Login_PW = login_PW;
    }
}
