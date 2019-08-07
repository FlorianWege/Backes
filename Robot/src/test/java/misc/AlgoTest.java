package misc;

import core.RobotAlgo;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AlgoTest {
    @Test()
    public void Test() throws IOException {
        String pathS = "RobotAlgos/ExampleAlgo.txt";

        URL url = getClass().getClassLoader().getResource(pathS);

        assert url != null : pathS + " not found";

        RobotAlgo algo = new RobotAlgo(new File(url.getFile()));

        Assert.assertEquals(algo.getAssociatedLevelName(), "Level 2");
    }
}
