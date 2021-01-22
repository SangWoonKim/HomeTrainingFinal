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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable((Parcelable) this.meta,flags);
        dest.writeList(this.documents);
    }

    protected Category_Result(Parcel in) {
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.documents = new ArrayList<Document>();
        in.readList(this.documents,Document.class.getClassLoader());
    }

    public static final Creator<Category_Result> CREATOR = new Parcelable.Creator<Category_Result>() {
        @Override
        public Category_Result createFromParcel(Parcel source) {
            return new Category_Result(source);
        }

        @Override
        public Category_Result[] newArray(int size) {
            return new Category_Result[size];
        }
    };
}
