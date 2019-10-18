package sembako.sayunara.android.constant;

public class Constant {

    public static final int DEFAULT_CANCEL_BUTTON_SHOW = 1000 * (60 * 60);
    public static final String DEFAULT_PROGRESS_TEXT = "Memuat permintaan";

    public static final String COLLECTION_USER = "users";
    public static final String COLLECTION_STORE = "stores";
    public static final String DEFAULT_AVATAR="https://upload.wikimedia.org/wikipedia/id/thumb/d/d5/Aang_.jpg/300px-Aang_.jpg";

    public interface USER_KEY {
        String userId = "userId";
        String username = "username";
        String type= "type";
        String avatar = "avatar";
        String email = "email";
        String isMitra = "getIsMitra";
        String hasStore = "hasStore";
        String isAvtive = "isActive";
        String phoneNumber = "phoneNumber";
        String isLogin = "isLogin";
    }
}

