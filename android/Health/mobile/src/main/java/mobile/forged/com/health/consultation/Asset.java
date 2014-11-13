package mobile.forged.com.health.consultation;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Asset {
    public enum AssetType {
        Image, Video
    }

    // [region] properties

    public AssetType type;
    public String uri;
    public String url;
    public String thumbnailUrl;
    public String brightcoveID;

    // [endregion]

    // [region] factories

    public static Asset videoAsset(String videoUri, String brightcoveID, String thumbnailUrl) {
        Asset asset = new Asset();
        asset.type = AssetType.Video;
        asset.uri = videoUri;
        asset.url = thumbnailUrl;
        asset.thumbnailUrl = thumbnailUrl;
        asset.brightcoveID = brightcoveID;
        return asset;
    }

    public static Asset imageAsset(String imageUri, String imageUrl, String thumbnailUrl) {
        Asset asset = new Asset();
        asset.type = AssetType.Image;
        asset.uri = imageUri;
        asset.url = imageUrl;
        asset.thumbnailUrl = thumbnailUrl;
        return asset;
    }

    // [endregion]
}