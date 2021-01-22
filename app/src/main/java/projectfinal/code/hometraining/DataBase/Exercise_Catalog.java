package projectfinal.code.hometraining.DataBase;

/*
* DBHelper에서 sql문으로 조회한 값을 CatalogAdapter에 전달하기 위한 getter,setter */
public class Exercise_Catalog {
    private String C_part;
    private String C_name;
    private int C_setcal;
    private String C_image;
    private String C_imageOrg;



    //getter
    public String getC_part() {
            return C_part;
    }
    public String getC_name() {
        return C_name;
    }
    public int getC_setcal() {
        return C_setcal;
    }
    public String getC_image() {
        return C_image;
    }
    public String getC_imageOrg() {
        return this.C_imageOrg;
    }

    //setter
    public void setC_part(String c_part) {
        this.C_part = c_part;
        }
    public void setC_name(String c_name) {
        this.C_name = c_name;
    }
    public void setC_setcal(int c_setcal) {
        this.C_setcal = c_setcal;
    }
    public void setC_image(String c_image) {
        this.C_image = c_image;
    }
    public void setC_imageOrg(String c_imageOrg) {
        C_imageOrg = c_imageOrg;
    }

}
