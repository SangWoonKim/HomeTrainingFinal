package projectfinal.code.hometraining.DataBase;

/*
 * DBHelper에서 sql문으로 조회한 값을 FoodAdapter에 전달하기 위한 getter,setter */
public class Food {

    public String F_part;
    public String F_name;
    public int F_setcal;
    public String F_image;

    //getter
    public String getF_part() {
        return this.F_part;
    }
    public String getF_name() {
        return this.F_name;
    }
    public int getF_setcal() {
        return this.F_setcal;
    }
    public String getF_image() {
        return this.F_image;
    }

    //setter
    public void setF_part(String f_part) {
        F_part = f_part;
    }

    public void setF_name(String f_name) {
        F_name = f_name;
    }

    public void setF_setcal(int f_setcal) {
        F_setcal = f_setcal;
    }
    public void setF_image(String f_image) {
        F_image = f_image;
    }
}
