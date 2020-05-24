import org.junit.Assert;
import org.junit.Test;

import static com.tuiasi.http.utils.LinkHandlingUtils.processLinkToLocalPath;
import static com.tuiasi.http.utils.LinkHandlingUtils.processLinkToStandardForm;

public class LinkHandlingUtilsTest {

    @Test
    public void testLinkToStandardWithoutProtocolWithoutWWW(){
        Assert.assertEquals("www.domain.com", processLinkToStandardForm("domain.com"));
    }

    @Test
    public void testLinkToStandardWithoutWWW(){
        Assert.assertEquals("www.domain.com", processLinkToStandardForm("http://domain.com"));
    }
    @Test
    public void testLinkToStandardWithoutProtocol(){
        Assert.assertEquals("www.domain.com", processLinkToStandardForm("www.domain.com"));
    }
    @Test
    public void testLinkToStandard(){
        Assert.assertEquals("www.domain.com", processLinkToStandardForm("http://www.domain.com"));
    }

    @Test
    public void testLinkToStandardWithPathQueryAndFragment(){
        Assert.assertEquals("www.domain.com/path?query=1#fragment", processLinkToStandardForm("http://www.domain.com/path?query=1#fragment"));
    }


    @Test
    public void testLinkToFilePath(){
        Assert.assertEquals("www.domain.com/index.html", processLinkToLocalPath("www.domain.com"));
    }

    @Test
    public void testLinkToFileWithoutProtocolWithoutWWW(){
        Assert.assertEquals("www.domain.com/index.html", processLinkToLocalPath("domain.com"));
    }

    @Test
    public void testRobotsLinkToFile(){
        Assert.assertEquals("www.domain.com/robots.txt", processLinkToLocalPath("www.domain.com/robots.txt"));
    }

    @Test
    public void testLinkToFileWithPathQueryAndFragment(){
        Assert.assertEquals("www.domain.com/path/query=1/fragment/index.html", processLinkToLocalPath("http://www.domain.com/path?query=1#fragment"));
    }

}
