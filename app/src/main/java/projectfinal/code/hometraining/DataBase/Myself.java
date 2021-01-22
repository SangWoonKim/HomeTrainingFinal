package projectfinal.code.hometraining.DataBase;

/*나 자신과의 싸움 그래프에서 쓰이는 getter,setter*/
public class Myself {
    String  M_date;
    String M_setcal;

    //getter
    public String getM_date() {
        return this.M_date;
    }
    public String getM_setcal() {
        return this.M_setcal;
    }

    //setter
    public void setM_date(String m_date) {
        M_date = m_date;
    }
    public void setM_setcal(String m_setcal) {
        M_setcal = m_setcal;
    }
}
