package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*클릭시 세부정보 얻는 클래스*/
public class Document implements Parcelable {

    @SerializedName("place_name")
    @Expose
    private String placeName;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("place_url")
    @Expose
    private String placeUrl;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("address_name")
    @Expose
    private String addressName;
    @SerializedName("road_address_name")
    @Expose
    private String roadAddressName;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("category_group_code")
    @Expose
    private String categoryGroupCode;
    @SerializedName("category_group_name")
    @Expose
    private String categoryGroupName;
    @SerializedName("x")
    @Expose
    private double x;
    @SerializedName("y")
    @Expose
    private double y;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRoadAddressName() {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName) {
        this.roadAddressName = roadAddressName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    public void setCategoryGroupCode(String categoryGroupCode) {
        this.categoryGroupCode = categoryGroupCode;
    }

    public String getCategoryGroupName() {
        return categoryGroupName;
    }

    public void setCategoryGroupName(String categoryGroupName) {
        this.categoryGroupName = categoryGroupName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeName);
        dest.writeString(this.distance);
        dest.writeString(this.placeUrl);
        dest.writeString(this.categoryName);
        dest.writeString(this.addressName);
        dest.writeString(this.roadAddressName);
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.categoryGroupCode);
        dest.writeString(this.categoryGroupName);
        dest.writeDouble(this.x);
        dest.writeDouble(this.y);
    }

    public Document() {
    }

    protected Document(Parcel in) {
        this.placeName = in.readString();
        this.distance = in.readString();
        this.placeUrl = in.readString();
        this.categoryName = in.readString();
        this.addressName = in.readString();
        this.roadAddressName = in.readString();
        this.id = in.readString();
        this.phone = in.readString();
        this.categoryGroupCode = in.readString();
        this.categoryGroupName = in.readString();
        this.x = in.readDouble();
        this.y = in.readDouble();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}