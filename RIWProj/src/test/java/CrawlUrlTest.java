import com.tuiasi.http.CrawlURL;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

public class CrawlUrlTest {

    @SneakyThrows
    @Test
    public void testLinkToStandardWithoutProtocolWithoutWWW() {
        Assert.assertEquals("http://www.domain.com", new CrawlURL("domain.com").toString());
    }


    @SneakyThrows
    @Test
    public void testLinkToStandardWithoutProtocol() {
        Assert.assertEquals("http://www.domain.com", new CrawlURL("www.domain.com").toString());
    }


    @SneakyThrows
    @Test
    public void testLinkToStandardWithoutWWW() {
        Assert.assertEquals("http://www.domain.com", new CrawlURL("http://domain.com").toString());
    }


    @SneakyThrows
    @Test
    public void testLinkToStandard() {
        Assert.assertEquals("http://www.domain.com", new CrawlURL("http://www.domain.com").toString());
    }


    @SneakyThrows
    @Test
    public void testHTTPSLinkToStandard() {
        Assert.assertEquals("https://www.domain.com", new CrawlURL("https://www.domain.com").toString());
    }

    @SneakyThrows
    @Test
    public void testLinkToStandardWithSubdomain() {
        Assert.assertEquals("https://www.domain.com", new CrawlURL("https://www.help.domain.com").toString());
    }


    @SneakyThrows
    @Test
    public void testLinkToStandardWithPathFragmentAndQuery() {
        Assert.assertEquals("http://www.domain.com/path?query=1#fragment", new CrawlURL("http://www.domain.com/path?query=1#fragment").toString());
    }
}
