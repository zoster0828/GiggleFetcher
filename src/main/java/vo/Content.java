package vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Random;

@Data
public class Content {
	private String siteName;
	private String title;
	private String url;
	private String thumbnailUrl;
	private String s3Thumb;
	private String desc;
	private Long time;
	public Content(String siteName, String title, String url, String thumbnailUrl, String s3Thumb, String desc){
		this.siteName = siteName;
		this.title = title;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.s3Thumb = s3Thumb;
		this.desc = desc;
	}

	public String toSql() {
		String sql = "insert into contents(sitename,title,url,count,views,s3thumb,thumbnail,description) values('"
				+ siteName + "'," + "'"
				+ title.replace("'", "") + "'," + "'"
				+ url + "'," + "'" + new Random().nextInt(20) + "'," + "'"
				+ new Random().nextInt(20) + "'," + "'" + s3Thumb + "',"
				+ "'" + thumbnailUrl + "'," + "'"
				+ desc.replace("'", "") + "');";

		return sql;
	}
}
