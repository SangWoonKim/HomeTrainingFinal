package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Category_Result implements Parcelable {

    @SerializedName("meta")
    @Expose
    private Meta meta;

    @SerializedName("documents")
    @Expose
    private List<Document> documents = null;


    //meta의 데이터 받기
    public Meta getMeta() {
        return meta;
    }
    //meta의 데이터 쓰기
    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    //document의 데이터 받기
    public List<Document> getDocuments() {
        return documents;
    }
    //document의 데이터 쓰기
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //소포에 값을 넣는 메소드 즉 객체나 배열등을 넣는다
    //객체가 직렬화되어 보내지기 이전에 데이터를 직렬화시켜주는 메소드로, dest에 순차적으로 Class 내부에 있는 데이터들을 저장
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //meta클래스에 document를 병합
        //즉 소포안에 소포 저장
        dest.writeParcelable((Parcelable) this.meta,flags);
        //배열 저장
        dest.writeList(this.documents);
    }

    //생성자
    protected Category_Result(Parcel in) {
        //meta클래스 읽기
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        //배열 생성
        this.documents = new ArrayList<Document>();
        in.readList(this.documents,Document.class.getClassLoader());
    }

    //객체 수신자
    public static final Creator<Category_Result> CREATOR = new Parcelable.Creator<Category_Result>() {
        //수신받은 데이터 읽는 메소드드
        @Override
        public Category_Result createFromParcel(Parcel source) {
            //자신의 객체를 생성하여 소보에 담긴 정보를 반환
            return new Category_Result(source);
        }

        @Override
        public Category_Result[] newArray(int size) {
            return new Category_Result[size];
        }
    };
}
