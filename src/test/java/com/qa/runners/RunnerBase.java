package com.qa.runners;

import com.qa.utils.DriverManager;
import com.qa.utils.GlobalParams;
import com.qa.utils.ServerManager;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.*;

public class RunnerBase {

    private static final ThreadLocal<TestNGCucumberRunner> testNGCucumberRunner =
            new ThreadLocal<>();

    protected static TestNGCucumberRunner getRunner() {
        return testNGCucumberRunner.get();
    }

    private static void setRunner(TestNGCucumberRunner runner) {
        testNGCucumberRunner.set(runner);
    }

    @Parameters({
            "platformName", "udid", "deviceName", "systemPort", "chromeDriverPort",
            "wdaLocalPort", "webkitDebugProxyPort"
    })
    @BeforeClass(alwaysRun = true)
    public void setUpClass(
            @Optional("Android") String platformName,
            @Optional("") String udid,
            @Optional("Android1") String deviceName,
            @Optional("8200") String systemPort,
            @Optional("9515") String chromeDriverPort,
            @Optional("8100") String wdaLocalPort,            // Android will ignore
            @Optional("27753") String webkitDebugProxyPort    // Android will ignore
    ) throws Exception {

        ThreadContext.put("ROUTINGKEY", platformName + "_" + deviceName);

        GlobalParams params = new GlobalParams();
        params.setPlatformName(platformName);
        params.setUDID(udid);
        params.setDeviceName(deviceName);

        switch (platformName) {
            case "Android":
                params.setSystemPort(systemPort);
                params.setChromeDriverPort(chromeDriverPort);
                break;
            case "iOS":
                params.setWdaLocalPort(wdaLocalPort);
                params.setWebkitDebugProxyPort(webkitDebugProxyPort);
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platformName);
        }

        // Start server if needed
        // new ServerManager().startServer();

        // MUST succeed, or runner is not created
        new DriverManager().initializeDriver();

        // ThreadLocal runner initialization
        setRunner(new TestNGCucumberRunner(this.getClass()));

    }

    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void scenario(PickleWrapper pickle, FeatureWrapper cucumberFeature) throws Throwable {
        getRunner().runScenario(pickle.getPickle());
    }

    @DataProvider
    public Object[][] scenarios() {
        return getRunner().provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {

        DriverManager driverManager = new DriverManager();
        if (driverManager.getDriver() != null) {
            driverManager.getDriver().quit();
            driverManager.setDriver(null);
        }

        ServerManager serverManager = new ServerManager();
        if (serverManager.getServer() != null) {
            serverManager.getServer().stop();
        }

        TestNGCucumberRunner runner = getRunner();
        if (runner != null) {
            runner.finish();
            testNGCucumberRunner.remove();
        }
    }
}

