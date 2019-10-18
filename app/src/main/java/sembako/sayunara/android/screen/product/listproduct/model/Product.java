
package sembako.sayunara.android.screen.product.listproduct.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    @SerializedName("createdAt")
    @Expose
    private createdAt createdAt = new createdAt();
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discount")
    @Expose
    private long discount;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private ArrayList<String> images = new ArrayList<>();
    @SerializedName("isActive")
    @Expose
    private boolean isActive;
    @SerializedName("isPromo")
    @Expose
    private boolean isPromo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private long price;
    @SerializedName("rating")
    @Expose
    private long rating;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("weight")
    @Expose
    private String weight;


    @SerializedName("unit")
    @Expose
    private String unit;


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    @SerializedName("stock")
    @Expose
    private long stock;


    public TotalCount getCount() {
        return count;
    }

    public void setCount(TotalCount count) {
        this.count = count;
    }

    @SerializedName("count")
    @Expose
    private TotalCount count = new TotalCount();


    public createdAt getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(createdAt createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPromo() {
        return isPromo;
    }

    public void setPromo(boolean promo) {
        isPromo = promo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


}
